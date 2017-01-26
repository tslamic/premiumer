package io.github.tslamic.prem;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;

@RunWith(RobolectricTestRunner.class) public class SimpleBillingBadResponseTest
    extends BadBillingTest {
  @Override IInAppBillingService service() {
    return new BadResponseBillingService();
  }

  static final class BadResponseBillingService implements IInAppBillingService {
    static final int BILLING_RESPONSE_RESULT_FAILURE = BILLING_RESPONSE_RESULT_OK - 1;

    @Override public int isBillingSupported(int apiVersion, String packageName, String type)
        throws RemoteException {
      return BILLING_RESPONSE_RESULT_FAILURE;
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle)
        throws RemoteException {
      return badResponseBundle();
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
        String developerPayload) throws RemoteException {
      return badResponseBundle();
    }

    @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
        String continuationToken) throws RemoteException {
      return badResponseBundle();
    }

    @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
        throws RemoteException {
      return BILLING_RESPONSE_RESULT_FAILURE;
    }

    @Override public IBinder asBinder() {
      throw new AssertionError();
    }

    static Bundle badResponseBundle() {
      final Bundle bundle = new Bundle(1);
      bundle.putInt(Constant.RESPONSE_CODE, BILLING_RESPONSE_RESULT_FAILURE);
      return bundle;
    }
  }
}
