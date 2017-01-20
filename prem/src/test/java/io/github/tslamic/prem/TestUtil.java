package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;

final class TestUtil {
  private TestUtil() { throw new AssertionError(); }

  static <T extends Parcelable> T fromParcel(T src, Parcelable.Creator<T> creator) {
    final Parcel parcel = Parcel.obtain();
    src.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    return creator.createFromParcel(parcel);
  }
}
