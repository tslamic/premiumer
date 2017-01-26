package io.github.tslamic.prem;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertEq;
import static io.github.tslamic.prem.TestUtil.JSON_SKU;
import static io.github.tslamic.prem.TestUtil.fromParcel;

@RunWith(RobolectricTestRunner.class) public class SkuDetailsTest {
  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void withNull() throws Exception {
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
    final SkuDetails details = new SkuDetails(JSON_SKU);
    assertThat(details.getTitle()).isEqualTo("TestTitle");
    assertThat(details.getPrice()).isEqualTo("€7.99");
    assertThat(details.getType()).isEqualTo("inapp");
    assertThat(details.getDescription()).isEqualTo("TestDescription");
    assertThat(details.getPriceAmount()).isEqualTo(7990000);
    assertThat(details.getCurrencyCode()).isEqualTo("EUR");
    assertThat(details.getSku()).isEqualTo("TestProductId");
    assertThat(details.asJson()).isEqualTo(JSON_SKU);
  }

  @Test public void eq() throws Exception {
    final SkuDetails s = new SkuDetails(JSON_SKU);
    final SkuDetails t = new SkuDetails(JSON_SKU);
    assertEq(s, t);
  }

  @Test public void fromJson() throws Exception {
    final SkuDetails s = new SkuDetails(JSON_SKU);
    final SkuDetails t = new SkuDetails(s.asJson());
    assertEq(s, t);
  }

  @Test public void parcelable() throws Exception {
    final SkuDetails s = new SkuDetails(JSON_SKU);
    final SkuDetails t = fromParcel(s, SkuDetails.CREATOR);
    assertThat(s.asJson()).isEqualTo(t.asJson());
    assertEq(s, t);
  }
}
