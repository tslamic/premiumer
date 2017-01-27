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
import static io.github.tslamic.prem.AssertUtil.assertInvokedOnce;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_PURCHASE_DATA;
import static io.github.tslamic.prem.Constant.RESPONSE_SIGNATURE;
import static io.github.tslamic.prem.TestUtil.EAGER_EXECUTOR;
import static io.github.tslamic.prem.TestUtil.JSON_PURCHASE;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.SKU;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class) public class PremiumerTest {
  private static final String SIGNATURE = "signature";
  private static final int REQUEST_CODE = 123;

  private PurchaseVerifier verifier;
  private PurchaseCache cache;
  private PayloadGenerator generator;
  private PremiumerListener listener;
  private Binder binder;
  private Premiumer premiumer;

  @Before public void setUp() {
    verifier = mock(PurchaseVerifier.class);
    cache = mock(PurchaseCache.class);
    generator = mock(PayloadGenerator.class);
    listener = mock(PremiumerListener.class);

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

    binder = new BinderMock();
    premiumer = TestFactory.premiumer(builder, binder);
  }

  @Test public void bindUnbind() {
    premiumer.bind();
    assertBound(true);

    premiumer.unbind();
    assertBound(false);
  }

  @Test public void purchaseNotBound() {
    assertThat(binder.isBound()).isFalse();

    final Activity activity = mock(Activity.class);
    final boolean purchased = premiumer.purchase(activity);
    assertThat(purchased).isFalse();
  }

  @Test public void purchaseActivityNull() {
    premiumer.bind();
    assertBound(true);

    final boolean purchased = premiumer.purchase(null);
    assertThat(purchased).isFalse();
  }

  @Test public void purchaseOk() {
    premiumer.bind();
    assertBound(true);

    final String payload = "payload";
    when(generator.generate()).thenReturn(payload);

    final Activity activity = mock(Activity.class);
    final boolean purchased = premiumer.purchase(activity);
    assertThat(purchased).isTrue();
    assertInvokedOnce(generator).generate();
    assertInvokedOnce(listener).onPurchaseRequested(payload);
  }

  @Test public void handleActivityResultBadRequestCode() {
    final int badRequestCode = BILLING_RESPONSE_RESULT_OK - 1;
    final boolean handled = premiumer.handleActivityResult(badRequestCode, RESULT_OK, new Intent());
    assertThat(handled).isFalse();
  }

  @Test public void handleActivityResultBadResult() {
    final int result = RESULT_CANCELED;
    final Intent intent = new Intent();
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, result, intent);
    assertThat(handled).isTrue();
    assertInvokedOnce(listener).onPurchaseBadResult(result, intent);
  }

  @Test public void handleActivityResultBadResponseNull() {
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, null);
    assertThat(handled).isTrue();
    assertInvokedOnce(listener).onPurchaseBadResponse(null);
  }

  @Test public void handleActivityResultBadResponse() {
    final int badResponseCode = BILLING_RESPONSE_RESULT_OK - 1;
    final Intent intent = newBillingIntent(badResponseCode, null, null);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    assertInvokedOnce(listener).onPurchaseBadResponse(intent);
  }

  private void handleActivityResultBadData(@Nullable String data) {
    final Intent intent = newBillingIntent(BILLING_RESPONSE_RESULT_OK, data, null);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    assertInvokedOnce(listener).onPurchaseBadResponse(intent);
  }

  @Test public void handleActivityResultBadDataNull() {
    handleActivityResultBadData(null);
  }

  @Test public void handleActivityResultBadDataEmpty() {
    handleActivityResultBadData("");
  }

  private void handleActivityVerification(boolean verified) throws Exception {
    final String responseData = JSON_PURCHASE;
    final String responseSignature = "responseSignature";

    when(verifier.verify(SIGNATURE, responseData, responseSignature)).thenReturn(verified);

    final Intent intent =
        newBillingIntent(BILLING_RESPONSE_RESULT_OK, responseData, responseSignature);
    final boolean handled = premiumer.handleActivityResult(REQUEST_CODE, RESULT_OK, intent);
    assertThat(handled).isTrue();
    assertInvokedOnce(verifier).verify(SIGNATURE, responseData, responseSignature);

    if (verified) {
      final Purchase purchase = new Purchase(responseData, responseSignature);
      assertInvokedOnce(cache).cache(purchase);
      assertInvokedOnce(listener).onPurchaseSuccessful(purchase);
    } else {
      assertInvokedOnce(listener).onPurchaseFailedVerification();
    }
  }

  @Test public void handleActivityVerificationFailed() throws Exception {
    handleActivityVerification(false);
  }

  @Test public void handleActivityResultVerificationOk() throws Exception {
    handleActivityVerification(true);
  }

  @Test public void skuDetails() throws Exception {
    premiumer.bind();
    assertBound(true);

    final boolean enqueued = premiumer.skuDetails();
    assertThat(enqueued).isTrue();

    final SkuDetails details = new SkuDetails(JSON_SKU);
    assertInvokedOnce(listener).onSkuDetails(details);
  }

  @Test public void purchaseDetails() throws Exception {
    final Purchase purchase = new Purchase(JSON_PURCHASE, null);
    when(cache.load()).thenReturn(purchase);

    premiumer.bind();
    assertBound(true);

    final boolean enqueued = premiumer.purchaseDetails();
    assertThat(enqueued).isTrue();
    assertInvokedOnce(listener).onPurchaseDetails(purchase);
  }

  private void consumeSku(@Nullable Purchase cached) {
    premiumer.bind();
    assertBound(true);

    when(cache.load()).thenReturn(cached);

    final boolean consumed = premiumer.consumeSku();
    assertThat(consumed).isTrue();
    assertInvokedOnce(cache).load();

    if (cached == null) {
      assertInvokedOnce(listener).onFailedToConsumeSku();
    } else {
      assertInvokedOnce(listener).onSkuConsumed();
    }
  }

  @Test public void consumeSkuFailed() throws Exception {
    consumeSku(null);
  }

  @Test public void consumeSkuOk() throws Exception {
    final Purchase purchase = new Purchase(JSON_PURCHASE, null);
    consumeSku(purchase);
  }

  @Test public void checkShowAds() {
    premiumer.bind();
    assertBound(true);

    assertThat(premiumer).isInstanceOf(SimplePremiumer.class);
    final SimplePremiumer simplePremiumer = (SimplePremiumer) premiumer;

    final boolean check = simplePremiumer.checkAds();
    assertThat(check).isTrue();
    assertInvokedOnce(listener).onHideAds();
  }

  private void assertBound(boolean bound) {
    assertThat(binder.isBound()).isEqualTo(bound);
    if (bound) {
      assertInvokedOnce(listener).onBillingAvailable();
    } else {
      assertInvokedOnce(listener).onBillingUnavailable();
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
      return new SuccessfulBillingService();
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
