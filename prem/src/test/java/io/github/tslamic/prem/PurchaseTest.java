package io.github.tslamic.prem;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertEq;
import static io.github.tslamic.prem.TestUtil.JSON_PURCHASE;
import static io.github.tslamic.prem.TestUtil.fromParcel;

@RunWith(RobolectricTestRunner.class) public class PurchaseTest {
  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void withNull() throws Exception {
    new Purchase(null, null);
  }

  @Test(expected = JSONException.class) public void withEmpty() throws Exception {
    new Purchase("", null);
  }

  @Test(expected = JSONException.class) public void withEmptyJson() throws Exception {
    new Purchase("{}", null);
  }

  @Test(expected = JSONException.class) public void withIncompleteJson() throws Exception {
    new Purchase("{\"productId\":\"TestProductId\"\"}", null);
  }

  @Test public void withProperJson() throws Exception {
    final String signature = null;
    final Purchase purchase = new Purchase(JSON_PURCHASE, signature);

    assertThat(purchase.isAutoRenewing()).isFalse();
    assertThat(purchase.getOrderId()).isEqualTo("TestOrder");
    assertThat(purchase.getPackageName()).isEqualTo("com.example.app");
    assertThat(purchase.getSku()).isEqualTo("TestProductId");
    assertThat(purchase.getPurchaseTime()).isEqualTo(1345678900000L);
    assertThat(purchase.getPurchaseState()).isEqualTo(0);
    assertThat(purchase.isPurchased()).isTrue();
    assertThat(purchase.isCancelled()).isFalse();
    assertThat(purchase.isRefunded()).isFalse();
    assertThat(purchase.getDeveloperPayload()).isEqualTo("TestDeveloperPayload");
    assertThat(purchase.getToken()).isEqualTo("TestPurchaseToken");
    assertThat(purchase.getSignature()).isEqualTo(signature);
    assertThat(purchase.asJson()).isEqualTo(JSON_PURCHASE);
  }

  @Test public void eq() throws Exception {
    final Purchase p = new Purchase(JSON_PURCHASE, "signature");
    final Purchase q = new Purchase(JSON_PURCHASE, "signature");
    assertEq(p, q);
  }

  @Test public void eqFromJson() throws Exception {
    final Purchase p = new Purchase(JSON_PURCHASE, "signature");
    final Purchase q = new Purchase(p.asJson(), "signature");
    assertEq(p, q);
  }

  @Test public void parcelable() throws Exception {
    final Purchase p = new Purchase(JSON_PURCHASE, null);
    final Purchase q = fromParcel(p, Purchase.CREATOR);
    assertThat(p.asJson()).isEqualTo(q.asJson());
    assertEq(p, q);
  }
}
