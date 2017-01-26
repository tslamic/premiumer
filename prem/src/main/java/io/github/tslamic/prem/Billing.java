package io.github.tslamic.prem;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

interface Billing {
  boolean isBillingSupported();
  boolean purchase(@NonNull Activity activity, @NonNull String sku, int requestCode,
      @Nullable String payload);
  @Nullable SkuDetails skuDetails(@NonNull String sku);
  boolean consumeSku(@NonNull String purchaseToken);
  boolean ownsSku(@NonNull String sku);
}
