package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class PremiumerListenerProxy implements PremiumerListener {
  private final PremiumerListener listener;

  PremiumerListenerProxy(@NonNull PremiumerListener listener) {
    this.listener = listener;
  }

  @Override public void onShowAds() {
    listener.onShowAds();
  }

  @Override public void onHideAds() {
    listener.onHideAds();
  }

  @Override public void onBillingAvailable() {
    listener.onBillingAvailable();
  }

  @Override public void onBillingUnavailable() {
    listener.onBillingUnavailable();
  }

  @Override public void onSkuDetails(@Nullable SkuDetails details) {
    listener.onSkuDetails(details);
  }

  @Override public void onSkuConsumed() {
    listener.onSkuConsumed();
  }

  @Override public void onFailedToConsumeSku() {
    listener.onFailedToConsumeSku();
  }

  @Override public void onPurchaseRequested(@Nullable String payload) {
    listener.onPurchaseRequested(payload);
  }

  @Override public void onPurchaseDetails(@Nullable Purchase purchase) {
    listener.onPurchaseDetails(purchase);
  }

  @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) {
    listener.onPurchaseSuccessful(purchase);
  }

  @Override public void onPurchaseBadResult(int resultCode, @Nullable Intent data) {
    listener.onPurchaseBadResult(resultCode, data);
  }

  @Override public void onPurchaseBadResponse(@Nullable Intent data) {
    listener.onPurchaseBadResponse(data);
  }

  @Override public void onPurchaseFailedVerification() {
    listener.onPurchaseFailedVerification();
  }

  @Override public void onException(@NonNull Exception exception) {
    listener.onException(exception);
  }
}
