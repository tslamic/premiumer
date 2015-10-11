package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class Purchase implements Parcelable {

    String orderId;
    String packageName;
    String sku;
    long purchaseTime;
    int purchaseState;
    String developerPayload;
    String token;
    String signature;
    String json;

    public Purchase(String json, String signature) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject obj = new JSONObject(json);
                orderId = obj.optString("orderId");
                packageName = obj.optString("packageName");
                sku = obj.optString("productId");
                purchaseTime = obj.optLong("purchaseTime");
                purchaseState = obj.optInt("purchaseState");
                developerPayload = obj.optString("developerPayload");
                token = obj.optString("token", obj.optString("purchaseToken"));
            } catch (JSONException ignore) {
            }
            this.signature = signature;
            this.json = json;
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSku() {
        return sku;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public String getToken() {
        return token;
    }

    public String getSignature() {
        return signature;
    }

    public String asJson() {
        return json;
    }

    @Override
    public String toString() {
        return "Purchase: " + json;
    }

    // Parcelable stuff below.

    public static Creator<Purchase> CREATOR = new Creator<Purchase>() {
        @Override
        public Purchase createFromParcel(Parcel source) {
            return new Purchase(source.readString(), source.readString());
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // No special content.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(json);
        dest.writeString(signature);
    }

}
