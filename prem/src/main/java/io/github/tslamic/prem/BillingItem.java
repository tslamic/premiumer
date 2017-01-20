package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import static io.github.tslamic.prem.Util.checkNotNull;

abstract class BillingItem implements Parcelable {
  BillingItem(@NonNull String json) throws JSONException {
    checkNotNull(json, "json == null");
    final JSONObject object = new JSONObject(json);

    init(object);
  }

  BillingItem(@NonNull Parcel parcel) {
    // To be implemented by subclasses.
  }

  @Override public int describeContents() {
    return 0; // No special content.
  }

  abstract void init(@NonNull JSONObject object) throws JSONException;
}
