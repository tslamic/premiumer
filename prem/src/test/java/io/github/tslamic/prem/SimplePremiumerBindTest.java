package io.github.tslamic.prem;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;

@RunWith(RobolectricTestRunner.class) public class SimplePremiumerBindTest {
  @Test public void bindNoBillingCapabilitiesNoBind() throws Exception {
    final Binder binder = new SimpleBinderMock(false, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindNoBillingCapabilitiesBind() throws Exception {
    final Binder binder = new SimpleBinderMock(false, true);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesNoBind() throws Exception {
    final Binder binder = new SimpleBinderMock(true, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesBindServiceBillingNotSupported() throws Exception {
    final Binder binder = new SimpleBinderMock(true, true, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesBindServiceBillingSupported() throws Exception {
    final Binder binder = new SimpleBinderMock(true, true, true);
    assertSimplePremiumer(binder, true);
  }

  private static void assertSimplePremiumer(@NonNull Binder binder, boolean isBound) {
    final BillingAvailableListener listener = new BillingAvailableListener();
    premiumer(listener, binder).bind();
    assertThat(listener.onBillingAvailable).isEqualTo(isBound);
    assertThat(listener.onBillingUnavailable).isEqualTo(!isBound);
  }

  private static Premiumer premiumer(@NonNull PremiumerListener listener, @NonNull Binder binder) {
    final Builder builder = PremiumerBuilder.with(RuntimeEnvironment.application)
        .sku("dummy.sku")
        .listener(listener)
        .autoNotifyAds(false);
    return new SimplePremiumer((PremiumerBuilder) builder, binder);
  }

  static class BillingAvailableListener extends PremiumerListenerStub {
    boolean onBillingAvailable;
    boolean onBillingUnavailable;

    @Override public void onBillingAvailable() {
      onBillingAvailable = true;
    }

    @Override public void onBillingUnavailable() {
      onBillingUnavailable = true;
    }
  }

  static class SimpleBinderMock extends SimpleBinder {
    final boolean hasBillingCapabilities;
    final boolean canBind;
    final boolean isBillingSupported;

    SimpleBinderMock(boolean hasBillingCapabilities, boolean canBind) {
      this(hasBillingCapabilities, canBind, false);
    }

    SimpleBinderMock(boolean hasBillingCapabilities, boolean canBind, boolean isBillingSupported) {
      super(RuntimeEnvironment.application);
      this.hasBillingCapabilities = hasBillingCapabilities;
      this.canBind = canBind;
      this.isBillingSupported = isBillingSupported;
    }

    @Override public boolean hasBillingCapabilities(@NonNull Intent intent) {
      return hasBillingCapabilities;
    }

    @Override
    public boolean bind(@NonNull Intent intent, @NonNull ServiceConnection conn, int flags) {
      return canBind && super.bind(intent, conn, flags);
    }

    @NonNull @Override public IInAppBillingService service(IBinder binder) {
      return new BillingServiceStub() {
        @Override public int isBillingSupported(int apiVersion, String packageName, String type)
            throws RemoteException {
          return isBillingSupported ? BILLING_RESPONSE_RESULT_OK : (BILLING_RESPONSE_RESULT_OK - 1);
        }
      };
    }
  }
}
