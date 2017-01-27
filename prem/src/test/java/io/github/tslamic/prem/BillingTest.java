package io.github.tslamic.prem;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.vending.billing.IInAppBillingService;
import io.github.tslamic.prem.stub.BillingServiceStub;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertInvokedOnce;
import static io.github.tslamic.prem.AssertUtil.assertNoInteraction;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_BUY_INTENT;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_DETAILS_LIST;
import static io.github.tslamic.prem.Constant.RESPONSE_ITEM_LIST;
import static io.github.tslamic.prem.TestFactory.billing;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.SKU;
import static io.github.tslamic.prem.Util.arrayList;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class BillingTest {
  @Test public void billingSupportedOk() {
    final IInAppBillingService service = new SuccessfulBillingService();
    final Billing billing = billing(service);
    final boolean result = billing.isBillingSupported();
    assertThat(result).isTrue();
  }

  @Test public void purchaseNullPendingIntent() {
    final IInAppBillingService service = new BillingServiceStub() {
      @Override
      public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
          String developerPayload) throws RemoteException {
        final Bundle bundle = new Bundle(2);
        bundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
        bundle.putParcelable(RESPONSE_BUY_INTENT, null);
        return bundle;
      }
    };
    final Billing billing = billing(service);
    final Activity activity = mock(Activity.class);
    final boolean response = billing.purchase(activity, SKU, 0, null);
    assertThat(response).isFalse();
    assertNoInteraction(activity);
  }

  @Test public void purchaseOk() throws Exception {
    final IInAppBillingService service = new SuccessfulBillingService();
    final Activity activity = mock(Activity.class);
    final Billing billing = billing(service);
    final boolean response = billing.purchase(activity, SKU, 0, null);
    assertInvokedOnce(activity).startIntentSenderForResult(null, 0, null, 0, 0, 0);
    assertThat(response).isTrue();
  }

  @Test public void skuDetailsNull() {
    final IInAppBillingService service = withJsonSkus(null);
    final Billing billing = billing(service);
    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void skuDetailsEmpty() {
    final IInAppBillingService service = withJsonSkus();
    final Billing billing = billing(service);
    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void skuDetailsOk() throws Exception {
    final String json = JSON_SKU;
    final IInAppBillingService service = withJsonSkus(json);
    final Billing billing = billing(service);

    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNotNull();

    final SkuDetails actual = new SkuDetails(json);
    assertThat(details).isEqualTo(actual);
  }

  @Test public void consumeSkuOk() {
    final IInAppBillingService service = new SuccessfulBillingService();
    final Billing billing = billing(service);
    final boolean result = billing.consumeSku(SKU);
    assertThat(result).isTrue();
  }

  @Test public void ownsSkuNullResponseList() {
    final IInAppBillingService service = withOwnedSkus(null);
    final Billing billing = billing(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isFalse();
  }

  @Test public void ownsSkuEmptyResponseList() {
    final IInAppBillingService service = withOwnedSkus();
    final Billing billing = billing(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isFalse();
  }

  @Test public void ownsSkuOk() {
    final IInAppBillingService service = withOwnedSkus(SKU);
    final Billing billing = billing(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isTrue();
  }

  private static IInAppBillingService withJsonSkus(String... jsonSkus) {
    final Bundle bundle = populateLSuccessfulList(RESPONSE_DETAILS_LIST, jsonSkus);
    return new BillingServiceStub() {
      @Override public Bundle getSkuDetails(int apiVersion, String packageName, String type,
          Bundle skusBundle) throws RemoteException {
        return bundle;
      }
    };
  }

  private static IInAppBillingService withOwnedSkus(String... ownedSkus) {
    final Bundle bundle = populateLSuccessfulList(RESPONSE_ITEM_LIST, ownedSkus);
    return new BillingServiceStub() {
      @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
          String continuationToken) throws RemoteException {
        return bundle;
      }
    };
  }

  private static Bundle populateLSuccessfulList(@NonNull String key, @Nullable String... values) {
    final ArrayList<String> list;
    if (values == null) {
      list = null;
    } else {
      list = arrayList(values);
    }
    final Bundle bundle = new Bundle(2);
    bundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
    bundle.putStringArrayList(key, list);
    return bundle;
  }
}
