package io.github.tslamic.prem;

import android.content.Context;
import android.os.Handler;
import com.android.vending.billing.IInAppBillingService;

import static io.github.tslamic.prem.TestUtil.PACKAGE_NAME;

final class TestFactory {
  private TestFactory() {
    throw new AssertionError();
  }

  static Billing billing(IInAppBillingService service) {
    return new SimpleBilling(PACKAGE_NAME, service);
  }

  static Binder binder(Context context) {
    return new SimpleBinder(context);
  }

  static Premiumer premiumer(Builder builder, Binder binder) {
    if (!(builder instanceof PremiumerBuilder)) {
      throw new AssertionError();
    }
    return new SimplePremiumer((PremiumerBuilder) builder, binder);
  }

  static PurchaseCache cache(Context context) {
    return new PurchaseCache.SharedPrefsCache(context);
  }

  static Handler premiumerHandler(PremiumerListener listener) {
    return new PremiumerHandler(listener);
  }
}
