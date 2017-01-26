package io.github.tslamic.prem;

import com.android.vending.billing.IInAppBillingService;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.TestUtil.PACKAGE_NAME;
import static io.github.tslamic.prem.TestUtil.SKU;

public abstract class BadBillingTest {
  @Test public void isBillingSupported() throws Exception {
    final boolean supported = billing().isBillingSupported();
    assertThat(supported).isFalse();
  }

  @Test public void purchase() throws Exception {
    final boolean purchased = billing().purchase(new ActivityMock(), SKU, 0, null);
    assertThat(purchased).isFalse();
  }

  @Test public void skuDetails() throws Exception {
    final SkuDetails details = billing().skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void consumeSku() throws Exception {
    final boolean consumed = billing().consumeSku(SKU);
    assertThat(consumed).isFalse();
  }

  @Test public void ownsSku() throws Exception {
    final boolean owned = billing().ownsSku(SKU);
    assertThat(owned).isFalse();
  }

  Billing billing() {
    return new SimpleBilling(PACKAGE_NAME, service());
  }

  abstract IInAppBillingService service();
}
