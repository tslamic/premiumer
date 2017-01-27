package io.github.tslamic.premiumer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import io.github.tslamic.prem.PremiumerListener;
import io.github.tslamic.prem.Purchase;
import io.github.tslamic.prem.SkuDetails;

class LogPremiumerListener implements PremiumerListener {
  private static final String TAG = LogPremiumerListener.class.getSimpleName();

  @Override public void onShowAds() {
    log("onShowAds()");
  }

  @Override public void onHideAds() {
    log("onHideAds()");
  }

  @Override public void onBillingAvailable() {
    log("onBillingAvailable()");
  }

  @Override public void onBillingUnavailable() {
    log("onBillingUnavailable()");
  }

  @Override public void onSkuDetails(@Nullable SkuDetails details) {
    log("onSkuDetails(), details=%s", details);
  }

  @Override public void onSkuConsumed() {
    log("onSkuConsumed()");
  }

  @Override public void onFailedToConsumeSku() {
    log("onFailedToConsumeSku()");
  }

  @Override public void onPurchaseRequested(@Nullable String payload) {
    log("onPurchaseRequested(), payload=%s", payload);
  }

  @Override public void onPurchaseDetails(@Nullable Purchase purchase) {
    log("onPurchaseDetails(), purchase=%s", purchase);
  }

  @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) {
    log("onPurchaseSuccessful(), purchase=%s", purchase);
  }

  @Override public void onPurchaseBadResult(int resultCode, @Nullable Intent data) {
    log("onPurchaseBadResult(), resultCode=%d, data=%s", resultCode, data);
  }

  @Override public void onPurchaseBadResponse(@Nullable Intent data) {
    log("onPurchaseBadResponse(), data=%s", data);
  }

  @Override public void onPurchaseFailedVerification() {
    log("onPurchaseFailedVerification()");
  }

  private void log(String format, Object... args) {
    Log.d(TAG, String.format(format, args));
  }
}
