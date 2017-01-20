package io.github.tslamic.prem;

import android.os.Parcel;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app product's listing details.
 */
public final class SkuDetails extends BillingItem {
  String sku;
  String type;
  String price;
  long priceAmount;
  String currencyCode;
  String title;
  String description;

  SkuDetails(@NonNull String json) throws JSONException {
    super(json);
  }

  @Override void init(@NonNull JSONObject object) throws JSONException {
    sku = object.getString("productId");
    type = object.getString("type");
    price = object.getString("price");
    priceAmount = object.getLong("price_amount_micros");
    currencyCode = object.getString("price_currency_code");
    title = object.getString("title");
    description = object.getString("description");
  }

  @NonNull public String getSku() {
    return sku;
  }

  @NonNull public String getType() {
    return type;
  }

  @NonNull public String getPrice() {
    return price;
  }

  public long getPriceAmount() {
    return priceAmount;
  }

  @NonNull public String getCurrencyCode() {
    return currencyCode;
  }

  @NonNull public String getTitle() {
    return title;
  }

  @NonNull public String getDescription() {
    return description;
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
    return "SkuDetails{"
        + "sku='"
        + sku
        + "', type='"
        + type
        + "', price='"
        + price
        + "', priceAmount='"
        + priceAmount
        + "', currencyCode='"
        + currencyCode
        + "', title='"
        + title
        + "', description='"
        + description
        + '}';
  }

  // Parcelable stuff below.

  private SkuDetails(@NonNull Parcel parcel) {
    super(parcel);
    sku = parcel.readString();
    type = parcel.readString();
    price = parcel.readString();
    priceAmount = parcel.readLong();
    currencyCode = parcel.readString();
    title = parcel.readString();
    description = parcel.readString();
  }

  public static final Creator<SkuDetails> CREATOR = new Creator<SkuDetails>() {
    @Override public SkuDetails createFromParcel(Parcel source) {
      return new SkuDetails(source);
    }

    @Override public SkuDetails[] newArray(int size) {
      return new SkuDetails[size];
    }
  };

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(sku);
    dest.writeString(type);
    dest.writeString(price);
    dest.writeLong(priceAmount);
    dest.writeString(currencyCode);
    dest.writeString(title);
    dest.writeString(description);
  }
}
