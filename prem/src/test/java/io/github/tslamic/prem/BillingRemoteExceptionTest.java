package io.github.tslamic.prem;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class) public class BillingRemoteExceptionTest
    extends BadBillingTest {
  @Override IInAppBillingService service() {
    return new RemoteExceptionBillingService();
  }

  private static final class RemoteExceptionBillingService implements IInAppBillingService {
    @Override public int isBillingSupported(int apiVersion, String packageName, String type)
        throws RemoteException {
      throw new RemoteException();
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle)
        throws RemoteException {
      throw new RemoteException();
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
        String developerPayload) throws RemoteException {
      throw new RemoteException();
    }

    @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
        String continuationToken) throws RemoteException {
      throw new RemoteException();
    }

    @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
        throws RemoteException {
      throw new RemoteException();
    }

    @Override public IBinder asBinder() {
      throw new AssertionError();
    }
  }
}
