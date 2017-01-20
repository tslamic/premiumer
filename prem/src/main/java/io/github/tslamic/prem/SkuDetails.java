package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails implements Parcelable {

  String sku;
  String type;
  String price;
  String title;
  String description;
  String json;

  public SkuDetails(String json) {
    if (!TextUtils.isEmpty(json)) {
      try {
        final JSONObject obj = new JSONObject(json);
        sku = obj.optString("productId");
        type = obj.optString("type");
        price = obj.optString("price");
        title = obj.optString("title");
        description = obj.optString("description");
      } catch (JSONException ignore) {
      }
      this.json = json;
    }
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

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String asJson() {
    return json;
  }

  @Override public String toString() {
    return "SkuDetails: " + json;
  }

  // Parcelable stuff below.

  public static Creator<SkuDetails> CREATOR = new Creator<SkuDetails>() {
    @Override public SkuDetails createFromParcel(Parcel source) {
      return new SkuDetails(source.readString());
    }

    @Override public SkuDetails[] newArray(int size) {
      return new SkuDetails[size];
    }
  };

  @Override public int describeContents() {
    return 0; // No special content.
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(json);
  }
}
