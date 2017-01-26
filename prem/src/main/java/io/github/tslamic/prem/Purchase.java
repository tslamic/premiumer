package io.github.tslamic.prem;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.tslamic.prem.Util.safeEquals;

/**
 * Represents an in-app billing purchase.
 */
public class Purchase extends BillingItem {
  private static final int STATE_PURCHASED = 0;
  private static final int STATE_CANCELLED = 1;
  private static final int STATE_REFUNDED = 2;

  boolean autoRenewing; // Even though this will never be used, it's here for completeness.
  String orderId;
  String packageName;
  String sku;
  long purchaseTime;
  int purchaseState;
  String developerPayload;
  String purchaseToken;
  String signature;

  Purchase(@NonNull String json, @Nullable String signature) throws JSONException {
    super(json);
    this.signature = signature;
  }

  @Override void init(@NonNull JSONObject object) throws JSONException {
    autoRenewing = object.optBoolean("autoRenewing", false);
    orderId = object.getString("orderId");
    packageName = object.getString("packageName");
    sku = object.getString("productId");
    purchaseTime = object.getLong("purchaseTime");
    purchaseState = object.getInt("purchaseState");
    developerPayload = object.getString("developerPayload");
    purchaseToken = object.getString("purchaseToken");
  }

  public boolean isAutoRenewing() {
    return autoRenewing;
  }

  @NonNull public String getOrderId() {
    return orderId;
  }

  @NonNull public String getPackageName() {
    return packageName;
  }

  @NonNull public String getSku() {
    return sku;
  }

  public long getPurchaseTime() {
    return purchaseTime;
  }

  public int getPurchaseState() {
    return purchaseState;
  }

  public boolean isPurchased() {
    return purchaseState == STATE_PURCHASED;
  }

  public boolean isCancelled() {
    return purchaseState == STATE_CANCELLED;
  }

  public boolean isRefunded() {
    return purchaseState == STATE_REFUNDED;
  }

  @NonNull public String getDeveloperPayload() {
    return developerPayload;
  }

  @NonNull public String getToken() {
    return purchaseToken;
  }

  @Nullable public String getSignature() {
    return signature;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Purchase purchase = (Purchase) o;
    return autoRenewing == purchase.autoRenewing
        && purchaseTime == purchase.purchaseTime
        && purchaseState == purchase.purchaseState
        && orderId.equals(purchase.orderId)
        && packageName.equals(purchase.packageName)
        && sku.equals(purchase.sku)
        && developerPayload.equals(purchase.developerPayload)
        && purchaseToken.equals(purchase.purchaseToken)
        && safeEquals(signature, purchase.signature);
  }

  @Override public int hashCode() {
    int result = (autoRenewing ? 1 : 0);
    result = 31 * result + orderId.hashCode();
    result = 31 * result + packageName.hashCode();
    result = 31 * result + sku.hashCode();
    result = 31 * result + (int) (purchaseTime ^ (purchaseTime >>> 32));
    result = 31 * result + purchaseState;
    result = 31 * result + developerPayload.hashCode();
    result = 31 * result + purchaseToken.hashCode();
    result = 31 * result + (signature == null ? 0 : signature.hashCode());
    return result;
  }

  // Parcelable stuff below.

  private Purchase(@NonNull Parcel parcel) {
    super(parcel);
    autoRenewing = parcel.readInt() == 1;
    orderId = parcel.readString();
    packageName = parcel.readString();
    sku = parcel.readString();
    purchaseTime = parcel.readLong();
    purchaseState = parcel.readInt();
    developerPayload = parcel.readString();
    purchaseToken = parcel.readString();
    signature = parcel.readString();
  }

  public static final Creator<Purchase> CREATOR = new Creator<Purchase>() {
    @Override public Purchase createFromParcel(Parcel source) {
      return new Purchase(source);
    }

    @Override public Purchase[] newArray(int size) {
      return new Purchase[size];
    }
  };

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(autoRenewing ? 1 : 0);
    dest.writeString(orderId);
    dest.writeString(packageName);
    dest.writeString(sku);
    dest.writeLong(purchaseTime);
    dest.writeInt(purchaseState);
    dest.writeString(developerPayload);
    dest.writeString(purchaseToken);
    dest.writeString(signature);
  }
}
