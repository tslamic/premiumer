package io.github.tslamic.prem;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.vending.billing.IInAppBillingService;
import java.util.ArrayList;
import org.json.JSONException;

import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.BILLING_TYPE;
import static io.github.tslamic.prem.Constant.REQUEST_ITEM_ID_LIST;
import static io.github.tslamic.prem.Constant.RESPONSE_BUY_INTENT;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_DETAILS_LIST;
import static io.github.tslamic.prem.Constant.RESPONSE_ITEM_LIST;
import static io.github.tslamic.prem.Util.arrayList;
import static io.github.tslamic.prem.Util.checkNotNull;

class SimpleBilling implements Billing {
  private final String packageName;
  private final IInAppBillingService service;

  SimpleBilling(@NonNull String packageName, @NonNull IInAppBillingService service) {
    this.packageName = checkNotNull(packageName, "packageName == null");
    this.service = checkNotNull(service, "service == null");
  }

  @Override public boolean isBillingSupported() {
    try {
      final int response = service.isBillingSupported(3, packageName, BILLING_TYPE);
      return response == BILLING_RESPONSE_RESULT_OK;
    } catch (RemoteException ignore) {
    }
    return false;
  }

  @Override
  public boolean purchase(@NonNull Activity activity, @NonNull String sku, int requestCode,
      @Nullable String payload) {
    try {
      final Bundle bundle = service.getBuyIntent(3, packageName, sku, BILLING_TYPE, payload);
      if (responseOk(bundle)) {
        final PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
        if (pendingIntent != null) {
          final IntentSender sender = pendingIntent.getIntentSender();
          activity.startIntentSenderForResult(sender, requestCode, null, 0, 0, 0);
          return true;
        }
      }
    } catch (RemoteException | IntentSender.SendIntentException ignore) {
    }
    return false;
  }

  @Override public SkuDetails skuDetails(@NonNull String sku) {
    try {
      final Bundle skus = new Bundle(1);
      skus.putStringArrayList(REQUEST_ITEM_ID_LIST, arrayList(sku));
      final Bundle bundle = service.getSkuDetails(3, packageName, BILLING_TYPE, skus);
      if (responseOk(bundle)) {
        final ArrayList<String> list = bundle.getStringArrayList(RESPONSE_DETAILS_LIST);
        if (list != null && !list.isEmpty()) {
          final String json = list.get(0);
          return new SkuDetails(json);
        }
      }
    } catch (RemoteException | JSONException ignore) {
    }
    return null;
  }

  @Override public boolean consumeSku(@NonNull String purchaseToken) {
    try {
      final int response = service.consumePurchase(3, packageName, purchaseToken);
      return response == BILLING_RESPONSE_RESULT_OK;
    } catch (RemoteException ignore) {
    }
    return false;
  }

  @Override public boolean ownsSku(@NonNull String sku) {
    try {
      final Bundle bundle = service.getPurchases(3, packageName, BILLING_TYPE, null);
      if (responseOk(bundle)) {
        final ArrayList<String> list = bundle.getStringArrayList(RESPONSE_ITEM_LIST);
        return null != list && list.contains(sku);
      }
    } catch (RemoteException ignore) {
    }
    return false;
  }

  static boolean responseOk(@Nullable Bundle bundle) {
    return bundle != null && bundle.getInt(RESPONSE_CODE) == BILLING_RESPONSE_RESULT_OK;
  }
}
