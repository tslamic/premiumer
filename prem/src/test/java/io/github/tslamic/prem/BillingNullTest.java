package io.github.tslamic.prem;

import com.android.vending.billing.IInAppBillingService;
import io.github.tslamic.prem.stub.BillingServiceStub;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class) public class BillingNullTest extends BadBillingTest {
  @Override public void isBillingSupported() {
    // Do nothing as response is non-null.
  }

  @Override public void consumeSku() {
    // Do nothing as response is non-null.
  }

  @Override IInAppBillingService service() {
    return new BillingServiceStub();
  }
}
