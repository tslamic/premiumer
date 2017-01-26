package io.github.tslamic.prem;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

final class TestUtil {
  private TestUtil() {
    throw new AssertionError();
  }

  static final String PACKAGE_NAME = "dummy.package";
  static final String SKU = "dummy.sku";

  static final Executor EAGER_EXECUTOR = new Executor() {
    @Override public void execute(@NonNull Runnable command) {
      command.run();
    }
  };

  static final String JSON_SKU = "{"
      + "  \"title\":\"TestTitle\","
      + "  \"price\":\"€7.99\","
      + "  \"type\":\"inapp\","
      + "  \"description\":\"TestDescription\","
      + "  \"price_amount_micros\":\"7990000\","
      + "  \"price_currency_code\":\"EUR\","
      + "  \"productId\":\"TestProductId\""
      + "}";

  static final String JSON_PURCHASE = "{"
      + "  \"orderId\":\"TestOrder\","
      + "  \"packageName\":\"com.example.app\","
      + "  \"productId\":\"TestProductId\","
      + "  \"purchaseTime\":1345678900000,"
      + "  \"purchaseState\":0,"
      + "  \"developerPayload\":\"TestDeveloperPayload\","
      + "  \"purchaseToken\":\"TestPurchaseToken\""
      + "}";

  static <T extends Parcelable> T fromParcel(T src, Parcelable.Creator<T> creator) {
    final Parcel parcel = Parcel.obtain();
    src.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    return creator.createFromParcel(parcel);
  }
}
