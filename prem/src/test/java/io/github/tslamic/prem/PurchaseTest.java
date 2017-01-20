package io.github.tslamic.prem;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) public class PurchaseTest {
  private static final String JSON = "{"
      + "  \"orderId\":\"testOrder\","
      + "  \"packageName\":\"com.example.app\","
      + "  \"productId\":\"exampleSku\","
      + "  \"purchaseTime\":1345678900000,"
      + "  \"purchaseState\":0,"
      + "  \"developerPayload\":\"testPayload\","
      + "  \"purchaseToken\":\"testToken\""
      + "}";

  @Test(expected = NullPointerException.class) public void withNull() throws Exception {
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
    final Purchase purchase = new Purchase(JSON, null);

    assertThat(purchase.orderId).isEqualTo("testOrder");
    assertThat(purchase.packageName).isEqualTo("com.example.app");
    assertThat(purchase.sku).isEqualTo("exampleSku");
    assertThat(purchase.purchaseTime).isEqualTo(1345678900000L);
    assertThat(purchase.purchaseState).isEqualTo(0);
    assertThat(purchase.isPurchased()).isTrue();
    assertThat(purchase.isCancelled()).isFalse();
    assertThat(purchase.isRefunded()).isFalse();
    assertThat(purchase.developerPayload).isEqualTo("testPayload");
    assertThat(purchase.purchaseToken).isEqualTo("testToken");

    final Purchase p = new Purchase(JSON, null);
    assertThat(purchase).isEqualTo(p);
  }

  @Test public void parcelable() throws Exception {
    final Purchase purchase = new Purchase(JSON, null);
    final Purchase fromParcel = TestUtil.fromParcel(purchase, Purchase.CREATOR);
    assertThat(purchase).isEqualTo(fromParcel);
  }
}
