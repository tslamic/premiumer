package io.github.tslamic.prem;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.vending.billing.IInAppBillingService;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_BUY_INTENT;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_DETAILS_LIST;
import static io.github.tslamic.prem.Constant.RESPONSE_ITEM_LIST;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.PACKAGE_NAME;
import static io.github.tslamic.prem.TestUtil.SKU;
import static io.github.tslamic.prem.Util.arrayList;

@RunWith(RobolectricTestRunner.class) public class SimpleBillingTest {
  @Test public void billingSupportedOk() throws Exception {
    final IInAppBillingService service = new BillingServiceStub() {
      @Override public int isBillingSupported(int apiVersion, String packageName, String type)
          throws RemoteException {
        return BILLING_RESPONSE_RESULT_OK;
      }
    };
    final Billing billing = simpleBilling(service);
    final boolean result = billing.isBillingSupported();
    assertThat(result).isTrue();
  }

  @Test public void purchaseNullPendingIntent() throws Exception {
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
    final Billing billing = simpleBilling(service);
    final boolean response = billing.purchase(new Activity(), SKU, 0, null);
    assertThat(response).isFalse();
  }

  @Test public void purchaseOk() throws Exception {
    final ActivityMock activity = new ActivityMock();
    final IInAppBillingService service = new BillingServiceStub() {
      @Override
      public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
          String developerPayload) throws RemoteException {
        final PendingIntent intent = PendingIntent.getActivity(activity, 0, new Intent(), 0);
        final Bundle bundle = new Bundle(2);
        bundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
        bundle.putParcelable(RESPONSE_BUY_INTENT, intent);
        return bundle;
      }
    };
    final Billing billing = simpleBilling(service);
    final boolean response = billing.purchase(activity, SKU, 0, null);
    assertThat(response).isTrue();
    assertThat(activity.startIntentSenderForResult).isTrue();
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

  @Test public void skuDetailsNull() throws Exception {
    final IInAppBillingService service = withJsonSkus(null);
    final Billing billing = simpleBilling(service);
    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void skuDetailsEmpty() throws Exception {
    final IInAppBillingService service = withJsonSkus();
    final Billing billing = simpleBilling(service);
    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void skuDetailsOk() throws Exception {
    final String json = JSON_SKU;
    final IInAppBillingService service = withJsonSkus(json);
    final Billing billing = simpleBilling(service);
    final SkuDetails details = billing.skuDetails(SKU);
    assertThat(details).isNotNull();
    final SkuDetails actual = new SkuDetails(json);
    assertThat(details).isEqualTo(actual);
  }

  @Test public void consumeSkuOk() throws Exception {
    final IInAppBillingService service = new BillingServiceStub() {
      @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
          throws RemoteException {
        return BILLING_RESPONSE_RESULT_OK;
      }
    };
    final Billing billing = simpleBilling(service);
    final boolean result = billing.consumeSku("dummy.token");
    assertThat(result).isTrue();
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

  @Test public void ownsSkuNullResponseList() throws Exception {
    final IInAppBillingService service = withOwnedSkus(null);
    final Billing billing = simpleBilling(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isFalse();
  }

  @Test public void ownsSkuEmptyResponseList() throws Exception {
    final IInAppBillingService service = withOwnedSkus();
    final Billing billing = simpleBilling(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isFalse();
  }

  @Test public void ownsSkuOk() throws Exception {
    final IInAppBillingService service = withOwnedSkus(SKU);
    final Billing billing = simpleBilling(service);
    final boolean result = billing.ownsSku(SKU);
    assertThat(result).isTrue();
  }

  private static Billing simpleBilling(@NonNull IInAppBillingService service) {
    return new SimpleBilling(PACKAGE_NAME, service);
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
