package io.github.tslamic.prem;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;

import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static io.github.tslamic.prem.Constant.RESPONSE_BUY_INTENT;
import static io.github.tslamic.prem.Constant.RESPONSE_CODE;
import static io.github.tslamic.prem.Constant.RESPONSE_DETAILS_LIST;
import static io.github.tslamic.prem.Constant.RESPONSE_ITEM_LIST;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.SKU;
import static io.github.tslamic.prem.Util.arrayList;
import static org.mockito.Mockito.mock;

class SuccessBillingService implements IInAppBillingService {
  private final boolean developerPayloadRequired;

  SuccessBillingService() {
    this(false);
  }

  SuccessBillingService(boolean developerPayloadRequired) {
    this.developerPayloadRequired = developerPayloadRequired;
  }

  @Override public int isBillingSupported(int apiVersion, String packageName, String type)
      throws RemoteException {
    return BILLING_RESPONSE_RESULT_OK;
  }

  @Override
  public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle)
      throws RemoteException {
    final Bundle bundle = responseOkBundle();
    bundle.putStringArrayList(RESPONSE_DETAILS_LIST, arrayList(JSON_SKU));
    return bundle;
  }

  @Override public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
      String developerPayload) throws RemoteException {
    if (developerPayloadRequired && developerPayload == null) {
      throw new AssertionError();
    }
    final Bundle bundle = responseOkBundle();
    bundle.putParcelable(RESPONSE_BUY_INTENT, mock(PendingIntent.class));
    return bundle;
  }

  @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
      String continuationToken) throws RemoteException {
    final Bundle bundle = responseOkBundle();
    bundle.putStringArrayList(RESPONSE_ITEM_LIST, arrayList(SKU));
    return bundle;
  }

  @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
      throws RemoteException {
    return BILLING_RESPONSE_RESULT_OK;
  }

  @Override public IBinder asBinder() {
    throw new AssertionError();
  }

  private static Bundle responseOkBundle() {
    final Bundle bundle = new Bundle();
    bundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
    return bundle;
  }
}
