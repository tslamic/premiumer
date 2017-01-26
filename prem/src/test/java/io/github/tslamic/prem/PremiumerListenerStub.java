package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PremiumerListenerStub implements PremiumerListener {
  @Override public void onShowAds() { }

  @Override public void onHideAds() { }

  @Override public void onBillingAvailable() { }

  @Override public void onBillingUnavailable() { }

  @Override public void onSkuDetails(@Nullable SkuDetails details) { }

  @Override public void onSkuConsumed() { }

  @Override public void onFailedToConsumeSku() { }

  @Override public void onPurchaseRequested(@Nullable String payload) { }

  @Override public void onPurchaseDetails(@Nullable Purchase purchase) { }

  @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) { }

  @Override public void onPurchaseBadResult(int resultCode, @Nullable Intent data) { }

  @Override public void onPurchaseBadResponse(@Nullable Intent data) { }

  @Override public void onPurchaseFailedVerification() { }

  @Override public void onException(@NonNull Exception exception) { }
}
