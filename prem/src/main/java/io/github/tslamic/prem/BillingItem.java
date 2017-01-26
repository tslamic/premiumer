package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.tslamic.prem.Util.checkNotNull;

abstract class BillingItem implements Parcelable {
  private final String json;

  BillingItem(@NonNull String json) throws JSONException {
    checkNotNull(json, "json == null");
    final JSONObject object = new JSONObject(json);
    this.json = json;
    init(object);
  }

  @SuppressWarnings("UnusedParameters") BillingItem(@NonNull Parcel parcel) {
    json = parcel.readString();
  }

  @Override public int describeContents() {
    return 0; // No special content.
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(json);
  }

  @NonNull public final String asJson() {
    return json;
  }

  @Override public String toString() {
    return asJson();
  }

  abstract void init(@NonNull JSONObject object) throws JSONException;
}
