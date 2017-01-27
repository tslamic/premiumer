package io.github.tslamic.prem;

import android.app.Activity;
import com.android.vending.billing.IInAppBillingService;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.TestUtil.SKU;

public abstract class BadBillingTest {
  @Test public void isBillingSupported() {
    final boolean supported = billing().isBillingSupported();
    assertThat(supported).isFalse();
  }

  @Test public void purchase() {
    final boolean purchased = billing().purchase(new Activity(), SKU, 0, null);
    assertThat(purchased).isFalse();
  }

  @Test public void skuDetails() {
    final SkuDetails details = billing().skuDetails(SKU);
    assertThat(details).isNull();
  }

  @Test public void consumeSku() {
    final boolean consumed = billing().consumeSku(SKU);
    assertThat(consumed).isFalse();
  }

  @Test public void ownsSku() {
    final boolean owned = billing().ownsSku(SKU);
    assertThat(owned).isFalse();
  }

  Billing billing() {
    return TestFactory.billing(service());
  }

  abstract IInAppBillingService service();
}
