package io.github.tslamic.prem;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.android.vending.billing.IInAppBillingService;
import java.util.concurrent.Executor;
import org.json.JSONException;

import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_PURCHASE_DATA;
import static io.github.tslamic.prem.Constant.RESPONSE_SIGNATURE;
import static io.github.tslamic.prem.Util.checkNotNull;
import static io.github.tslamic.prem.Util.isBlank;

class SimplePremiumer implements Premiumer {
  private static final Intent BILLING_INTENT =
      new Intent("com.android.vending.billing.InAppBillingService.BIND").setPackage(
          "com.android.vending");

  private final ServiceConnection connection = new Connection();
  private final Binder binder;
  private Billing billing;

  // Builder values.
  private final Context context;
  private final String sku;
  private final PremiumerListener listener;
  private final Executor executor;
  private final int requestCode;
  private final PayloadGenerator payloadGenerator;
  private final PurchaseVerifier verifier;
  private final PurchaseCache cache;
  private final String signatureBase64;
  private final PremiumerHandler handler;

  SimplePremiumer(@NonNull PremiumerBuilder builder) {
    this(builder, new SimpleBinder(builder.context));
  }

  SimplePremiumer(@NonNull PremiumerBuilder builder, @NonNull Binder binder) {
    this.binder = checkNotNull(binder, "binder == null");
    this.context = builder.context;
    this.sku = builder.sku;
    this.executor = builder.executor;
    this.requestCode = builder.requestCode;
    this.payloadGenerator = builder.payloadGenerator;
    this.verifier = builder.purchaseVerifier;
    this.cache = builder.purchaseCache;
    this.signatureBase64 = builder.signatureBase64;

    // Wrap the listener in a proxy, if show/hide ads should be invoked automatically.
    this.listener = builder.autoNotifyAds ? new NotifyAdsProxy(builder.listener) : builder.listener;
    this.handler = new PremiumerHandler(this.listener);
  }

  @Override public void bind() {
    final boolean bound =
        binder.hasBillingCapabilities(BILLING_INTENT) && binder.bind(BILLING_INTENT, connection,
            Context.BIND_AUTO_CREATE); // listener.onBillingAvailable() will be invoked later.
    if (!bound) {
      listener.onBillingUnavailable();
    }
  }

  @Override public void unbind() {
    binder.unbind();
    listener.onBillingUnavailable();
  }

  @Override public boolean purchase(@Nullable Activity activity) {
    if (billing == null || activity == null) {
      return false;
    }
    final String payload = payloadGenerator.generate();
    return billing.purchase(activity, sku, requestCode, payload);
  }

  @Override
  public boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    // Ignore handling if not related to Premiumer.
    if (this.requestCode != requestCode) {
      return false;
    }

    // If the result is not RESULT_OK, notify and return.
    if (resultCode != Activity.RESULT_OK) {
      listener.onPurchaseBadResult(resultCode, data);
      return true;
    }

    // If the intent data is missing, notify and return.
    if (data == null) {
      listener.onPurchaseBadResponse(null);
      return true;
    }

    // Assume intents with no response code are OK (known issue).
    // If the response is not OK, notify and return.
    final int response = data.getIntExtra(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
    if (response != BILLING_RESPONSE_RESULT_OK) {
      listener.onPurchaseBadResponse(data);
      return true;
    }

    // Ensure the important stuff is present, otherwise this is a bad response.
    final String purchaseData = data.getStringExtra(RESPONSE_PURCHASE_DATA);
    final String signature = data.getStringExtra(RESPONSE_SIGNATURE);
    if (isBlank(purchaseData)) {
      listener.onPurchaseBadResponse(data);
      return true;
    }

    // Success!
    executor.execute(new Runnable() {
      @Override public void run() {
        onSuccessfulPurchase(purchaseData, signature);
      }
    });

    return true;
  }

  @Override public boolean skuDetails() {
    return exec(new Runnable() {
      @Override public void run() {
        final SkuDetails details = billing.skuDetails(sku);
        handler.obtainMessage(PremiumerHandler.ON_SKU_DETAILS, details).sendToTarget();
      }
    });
  }

  @Override public boolean purchaseDetails() {
    return exec(new Runnable() {
      @Override public void run() {
        final Purchase purchase = cache.load();
        handler.obtainMessage(PremiumerHandler.ON_PURCHASE_DETAILS, purchase).sendToTarget();
      }
    });
  }

  @Override public boolean consumeSku() {
    return exec(new Runnable() {
      @Override public void run() {
        boolean consumed = false;
        final Purchase purchase = cache.load();
        if (purchase != null && purchase.purchaseToken != null) {
          consumed = billing.consumeSku(purchase.purchaseToken);
        }
        final int message;
        if (consumed) {
          message = PremiumerHandler.ON_SKU_CONSUMED;
          cache.clear();
        } else {
          message = PremiumerHandler.ON_FAILED_TO_CONSUME_SKU;
        }
        handler.obtainMessage(message, purchase).sendToTarget();
      }
    });
  }

  @WorkerThread void onSuccessfulPurchase(@NonNull String purchaseData, @NonNull String signature) {
    final boolean verified =
        verifier == null || verifier.verify(signatureBase64, purchaseData, signature);
    if (verified) {
      final Purchase purchase = fromJson(purchaseData, signature);
      cache.cache(purchase);
      handler.obtainMessage(PremiumerHandler.ON_PURCHASE_SUCCESSFUL, purchase).sendToTarget();
    } else {
      handler.obtainMessage(PremiumerHandler.ON_PURCHASE_FAILED_VERIFICATION).sendToTarget();
    }
  }

  boolean checkAds() {
    return exec(new Runnable() {
      @Override public void run() {
        final boolean owns = billing.ownsSku(sku);
        final int id = owns ? PremiumerHandler.ON_HIDE_ADS : PremiumerHandler.ON_SHOW_ADS;
        handler.obtainMessage(id).sendToTarget();
      }
    });
  }

  boolean exec(@NonNull Runnable runnable) {
    if (billing == null) {
      return false;
    }
    executor.execute(runnable);
    return true;
  }

  static Purchase fromJson(@NonNull String json, @Nullable String signature) {
    try {
      return new Purchase(json, signature);
    } catch (JSONException e) {
      // This only happens if in-app billing returns a corrupted JSON,
      // in which case it's impossible to recover properly.
      throw new RuntimeException(e);
    }
  }

  private final class NotifyAdsProxy extends PremiumerListenerProxy {
    NotifyAdsProxy(@NonNull PremiumerListener listener) {
      super(listener);
    }

    @Override public void onBillingAvailable() {
      super.onBillingAvailable();
      checkAds();
    }

    @Override public void onBillingUnavailable() {
      super.onBillingUnavailable();
      onShowAds();
    }

    @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) {
      super.onPurchaseSuccessful(purchase);
      onHideAds();
    }

    @Override public void onSkuConsumed() {
      super.onSkuConsumed();
      onShowAds();
    }
  }

  private final class Connection implements ServiceConnection {
    @Override public void onServiceConnected(ComponentName name, IBinder iBinder) {
      final IInAppBillingService s = binder.service(iBinder);
      final Billing b = binder.billing(context.getPackageName(), s);
      if (b.isBillingSupported()) {
        billing = b;
        listener.onBillingAvailable();
      } else {
        binder.unbind();
        listener.onBillingUnavailable();
      }
    }

    @Override public void onServiceDisconnected(ComponentName name) {
      billing = null;
      binder.unbind();
      listener.onBillingUnavailable();
    }
  }
}
