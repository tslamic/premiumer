package io.github.tslamic.prem;

import com.android.vending.billing.IInAppBillingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertEq;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.PACKAGE_NAME;
import static io.github.tslamic.prem.TestUtil.SKU;

@RunWith(RobolectricTestRunner.class) public class SimpleBillingSuccessTest {
  @Test public void billingSupportedOk() throws Exception {
    final boolean supported = billing().isBillingSupported();
    assertThat(supported).isTrue();
  }

  //@Test public void purchaseOk() throws Exception {
  //  final Activity activity = mock(Activity.class);
  //
  //  final boolean purchased = billing().purchase(activity, SKU, 0, null);
  //  assertThat(purchased).isTrue();
  //
  //  final IntentSender sender = any();
  //  final Intent intent = any();
  //  verify(activity, times(1)).startIntentSenderForResult(sender, anyInt(), intent, anyInt(),
  //      anyInt(), anyInt());
  //}

  @Test public void skuDetailsOk() throws Exception {
    final SkuDetails details = billing().skuDetails(SKU);
    assertThat(details).isNotNull();
    final SkuDetails actual = new SkuDetails(JSON_SKU);
    assertEq(details, actual);
  }

  private static Billing billing() {
    final IInAppBillingService service = new SuccessBillingService();
    return new SimpleBilling(PACKAGE_NAME, service);
  }
}
