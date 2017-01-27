package io.github.tslamic.prem;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;
import io.github.tslamic.prem.stub.BillingServiceStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static io.github.tslamic.prem.AssertUtil.assertInvokedNever;
import static io.github.tslamic.prem.AssertUtil.assertInvokedOnce;
import static io.github.tslamic.prem.Constant.BILLING_RESPONSE_RESULT_OK;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class SimplePremiumerBindTest {
  @Test public void bindNoBillingCapabilitiesNoBind() {
    final Binder binder = new SimpleBinderMock(false, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindNoBillingCapabilitiesBind() {
    final Binder binder = new SimpleBinderMock(false, true);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesNoBind() {
    final Binder binder = new SimpleBinderMock(true, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesBindServiceBillingNotSupported() {
    final Binder binder = new SimpleBinderMock(true, true, false);
    assertSimplePremiumer(binder, false);
  }

  @Test public void bindBillingCapabilitiesBindServiceBillingSupported() {
    final Binder binder = new SimpleBinderMock(true, true, true);
    assertSimplePremiumer(binder, true);
  }

  private static void assertSimplePremiumer(@NonNull Binder binder, boolean isBound) {
    final PremiumerListener listener = mock(PremiumerListener.class);
    premiumer(listener, binder).bind();
    if (isBound) {
      assertInvokedOnce(listener).onBillingAvailable();
      assertInvokedNever(listener).onBillingUnavailable();
    } else {
      assertInvokedNever(listener).onBillingAvailable();
      assertInvokedOnce(listener).onBillingUnavailable();
    }
  }

  private static Premiumer premiumer(@NonNull PremiumerListener listener, @NonNull Binder binder) {
    final Builder builder = PremiumerBuilder.with(RuntimeEnvironment.application)
        .sku("dummy.sku")
        .listener(listener)
        .autoNotifyAds(false);
    return TestFactory.premiumer(builder, binder);
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
