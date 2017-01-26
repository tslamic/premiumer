package io.github.tslamic.prem;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.vending.billing.IInAppBillingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_PURCHASE_DATA;
import static io.github.tslamic.prem.Constant.RESPONSE_SIGNATURE;
import static io.github.tslamic.prem.TestUtil.EAGER_EXECUTOR;
import static io.github.tslamic.prem.TestUtil.JSON_PURCHASE;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.SKU;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) public class SimplePremiumerTest {
  private static final String SIGNATURE = "signature";
  private static final int REQUEST_CODE = 123;

  private PurchaseVerifier verifier;
  private PurchaseCache cache;
  private PayloadGenerator generator;
  private PremiumerListener listener;
  private Binder binder;
  private Premiumer premiumer;

  @Before public void setUp() throws Exception {
    verifier = mock(PurchaseVerifier.class);
    cache = mock(PurchaseCache.class);
    generator = mock(PayloadGenerator.class);
    listener = mock(PremiumerListener.class);
    binder = new BinderMock();

    final Builder builder = PremiumerBuilder.with(RuntimeEnvironment.application)
        .sku(SKU)
        .listener(listener)
        .executor(EAGER_EXECUTOR)
        .payloadGenerator(generator)
        .purchaseVerifier(verifier)
        .purchaseCache(cache)
        .autoNotifyAds(false)
        .requestCode(REQUEST_CODE)
        .signatureBase64(SIGNATURE);

    premiumer = new SimplePremiumer((PremiumerBuilder) builder, binder);
  }

  @Test public void bindUnbind() throws Exception {
    premiumer.bind();
    assertBound(true);

    premiumer.unbind();
    assertBound(false);
  }

  @Test public void purchaseNotBound() throws Exception {
    final Activity activity = mock(Activity.class);
    final boolean purchased = premiumer.purchase(activity);
    assertThat(purchased).isFalse();
  }

  @Test public void purchaseActivityNull() throws Exception {
    premiumer.bind();
    assertBound(true);

    final boolean purchased = premiumer.purchase(null);
    assertThat(purchased).isFalse();
  }

  @Test public void purchaseOk() throws Exception {
    premiumer.bind();
    final Activity activity = mock(Activity.class);
    final boolean purchased = premiumer.purchase(activity);
    assertThat(purchased).isTrue();
    verify(generator, times(1)).generate();
  }

  @Test public void handleActivityResultBadRequestCode() throws Exception {
    final int badRequestCode = REQUEST_CODE - 1;
    final boolean handled = premiumer.handleActivityResult(badRequestCode, RESULT_OK, new Intent());
    assertThat(handled).isFalse();
  }

  @Test public void handleActivityResultBadResult() throws Exception {
    final int result = RESULT_CANCELED;
    final Intent intent = new Intent();
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, result, intent);
    assertThat(handled).isTrue();
    verify(listener, times(1)).onPurchaseBadResult(result, intent);
  }

  @Test public void handleActivityResultBadResponseNull() throws Exception {
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, null);
    assertThat(handled).isTrue();
    verify(listener, times(1)).onPurchaseBadResponse(null);
  }

  @Test public void handleActivityResultBadResponse() throws Exception {
    final int badResponseCode = BILLING_RESPONSE_RESULT_OK - 1;
    final Intent intent = newBillingIntent(badResponseCode, null, null);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    verify(listener, times(1)).onPurchaseBadResponse(intent);
  }

  @Test public void handleActivityResultBadData() throws Exception {
    final Intent intent = newBillingIntent(BILLING_RESPONSE_RESULT_OK, "", null);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    verify(listener, times(1)).onPurchaseBadResponse(intent);
  }

  @Test public void handleActivityVerificationFailed() throws Exception {
    final String json = JSON_PURCHASE;
    final String billingSignature = "billing";

    final Intent intent = newBillingIntent(BILLING_RESPONSE_RESULT_OK, json, billingSignature);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    verify(verifier, times(1)).verify(SIGNATURE, json, billingSignature);
    verify(listener, times(1)).onPurchaseFailedVerification();
  }

  @Test public void handleActivityResultVerificationOk() throws Exception {
    final String json = JSON_PURCHASE;
    final String billingSignature = "billing";

    when(verifier.verify(SIGNATURE, json, billingSignature)).thenReturn(true);

    final Intent intent = newBillingIntent(BILLING_RESPONSE_RESULT_OK, json, billingSignature);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    verify(verifier, times(1)).verify(SIGNATURE, json, billingSignature);

    final Purchase purchase = new Purchase(json, billingSignature);
    verify(cache, times(1)).cache(purchase);
    verify(listener, times(1)).onPurchaseSuccessful(purchase);
  }

  @Test public void skuDetails() throws Exception {
    premiumer.bind();
    final boolean enqueued = premiumer.skuDetails();
    assertThat(enqueued).isTrue();

    final SkuDetails details = new SkuDetails(JSON_SKU);
    verify(listener, times(1)).onSkuDetails(details);
  }

  @Test public void purchaseDetails() throws Exception {
    final Purchase purchase = new Purchase(JSON_PURCHASE, null);
    when(cache.load()).thenReturn(purchase);

    premiumer.bind();
    final boolean enqueued = premiumer.purchaseDetails();
    assertThat(enqueued).isTrue();
    verify(listener, times(1)).onPurchaseDetails(purchase);
  }

  @Test public void consumeSkuFailed() throws Exception {
    premiumer.bind();
    final boolean consumed = premiumer.consumeSku();
    assertThat(consumed).isTrue();
    verify(listener, times(1)).onFailedToConsumeSku();
  }

  @Test public void consumeSkuOk() throws Exception {
    final Purchase purchase = new Purchase(JSON_PURCHASE, null);
    when(cache.load()).thenReturn(purchase);

    premiumer.bind();
    final boolean consumed = premiumer.consumeSku();
    assertThat(consumed).isTrue();
    verify(cache, times(1)).clear();
    verify(listener, times(1)).onSkuConsumed();
  }

  private void assertBound(boolean expected) {
    if (expected) {
      assertThat(binder.isBound()).isTrue();
      verify(listener, times(1)).onBillingAvailable();
    } else {
      assertThat(binder.isBound()).isFalse();
      verify(listener, times(1)).onBillingUnavailable();
    }
  }

  private static Intent newBillingIntent(int responseCode, @Nullable String purchaseData,
      @Nullable String signature) {
    final Intent intent = new Intent();
    intent.putExtra(RESPONSE_CODE, responseCode);
    intent.putExtra(RESPONSE_PURCHASE_DATA, purchaseData);
    intent.putExtra(RESPONSE_SIGNATURE, signature);
    return intent;
  }

  static final class BinderMock implements Binder {
    boolean isBound;

    @NonNull @Override public IInAppBillingService service(IBinder binder) {
      return new SuccessBillingService();
    }

    @NonNull @Override
    public Billing billing(@NonNull String packageName, @NonNull IInAppBillingService service) {
      return new SimpleBilling(packageName, service);
    }

    @Override public boolean hasBillingCapabilities(@NonNull Intent intent) {
      return true;
    }

    @Override
    public boolean bind(@NonNull Intent intent, @NonNull ServiceConnection conn, int flags) {
      isBound = true;
      conn.onServiceConnected(null, null);
      return true;
    }

    @Override public void unbind() {
      isBound = false;
    }

    @Override public boolean isBound() {
      return isBound;
    }
  }
}
