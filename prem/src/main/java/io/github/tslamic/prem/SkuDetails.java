package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.tslamic.prem.Util.checkNotNull;

/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails implements Parcelable {
  private final String sku;
  private final String type;
  private final String price;
  private final long priceAmount;
  private final String currencyCode;
  private final String title;
  private final String description;
  private final String json;

  public SkuDetails(@NonNull String json) throws JSONException {
    checkNotNull(json, "json == null");
    final JSONObject obj = new JSONObject(json);
    sku = obj.getString("productId");
    type = obj.getString("type");
    price = obj.getString("price");
    priceAmount = obj.getLong("price_amount_micros");
    currencyCode = obj.getString("price_currency_code");
    title = obj.getString("title");
    description = obj.getString("description");
    this.json = json;
  }

  public String getSku() {
    return sku;
  }

  public String getType() {
    return type;
  }

  public String getPrice() {
    return price;
  }

  public long getPriceAmount() {
    return priceAmount;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String asJson() {
    return json;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final SkuDetails details = (SkuDetails) o;
    return priceAmount == details.priceAmount
        && sku.equals(details.sku)
        && type.equals(details.type)
        && price.equals(details.price)
        && currencyCode.equals(details.currencyCode)
        && title.equals(details.title)
        && description.equals(details.description);
  }

  @Override public int hashCode() {
    int result = sku.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + price.hashCode();
    result = 31 * result + (int) (priceAmount ^ (priceAmount >>> 32));
    result = 31 * result + currencyCode.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + description.hashCode();
    return result;
  }

  @Override public String toString() {
    return "SkuDetails: " + json;
  }

  // Parcelable stuff below.

  private SkuDetails(@NonNull Parcel parcel) {
    sku = parcel.readString();
    type = parcel.readString();
    price = parcel.readString();
    priceAmount = parcel.readLong();
    currencyCode = parcel.readString();
    title = parcel.readString();
    description = parcel.readString();
    json = parcel.readString();
  }

  public static final Creator<SkuDetails> CREATOR = new Creator<SkuDetails>() {
    @Override public SkuDetails createFromParcel(Parcel source) {
      return new SkuDetails(source);
    }

    @Override public SkuDetails[] newArray(int size) {
      return new SkuDetails[size];
    }
  };

  @Override public int describeContents() {
    return 0; // No special content.
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(sku);
    dest.writeString(type);
    dest.writeString(price);
    dest.writeLong(priceAmount);
    dest.writeString(currencyCode);
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(json);
  }
}
