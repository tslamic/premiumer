package io.github.tslamic.prem;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) public class SkuDetailsTest {
  private static final String JSON = "{"
      + "  \"title\":\"TestTitle\","
      + "  \"price\":\"€7.99\","
      + "  \"type\":\"inapp\","
      + "  \"description\":\"TestDescription\","
      + "  \"price_amount_micros\":\"7990000\","
      + "  \"price_currency_code\":\"EUR\","
      + "  \"productId\":\"TestProductId\""
      + "}";

  @Test(expected = NullPointerException.class) public void withNull() throws Exception {
    new SkuDetails(null);
  }

  @Test(expected = JSONException.class) public void withEmpty() throws Exception {
    new SkuDetails("");
  }

  @Test(expected = JSONException.class) public void withEmptyJson() throws Exception {
    new SkuDetails("{}");
  }

  @Test(expected = JSONException.class) public void withIncompleteJson() throws Exception {
    new SkuDetails("{\"productId\":\"TestProductId\"\"}");
  }

  @Test public void withProperJson() throws Exception {
    final SkuDetails details = new SkuDetails(JSON);

    assertThat(details.getTitle()).isEqualTo("TestTitle");
    assertThat(details.getPrice()).isEqualTo("€7.99");
    assertThat(details.getType()).isEqualTo("inapp");
    assertThat(details.getDescription()).isEqualTo("TestDescription");
    assertThat(details.getPriceAmount()).isEqualTo(7990000);
    assertThat(details.getCurrencyCode()).isEqualTo("EUR");
    assertThat(details.getSku()).isEqualTo("TestProductId");

    final SkuDetails s = new SkuDetails(JSON);
    assertThat(details).isEqualTo(s);
  }

  @Test public void parcelable() throws Exception {
    final SkuDetails details = new SkuDetails(JSON);
    final SkuDetails fromParcel = TestUtil.fromParcel(details, SkuDetails.CREATOR);
    assertThat(details).isEqualTo(fromParcel);
  }
}
