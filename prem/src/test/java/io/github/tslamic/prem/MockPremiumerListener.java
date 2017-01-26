package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MockPremiumerListener implements PremiumerListener {
  boolean onShowAds;
  boolean onHideAds;
  boolean onBillingAvailable;
  boolean onBillingUnavailable;
  boolean onSkuDetails;
  SkuDetails skuDetails;
  boolean onSkuConsumed;
  boolean onFailedToConsumeSku;
  boolean onPurchaseRequested;
  String payload;
  boolean onPurchaseDetails;
  Purchase purchase;
  boolean onPurchaseSuccessful;
  boolean onPurchaseBadResult;
  int resultCode;
  Intent intent;
  boolean onPurchaseBadResponse;
  boolean onPurchaseFailedVerification;
  boolean onException;
  Exception exception;

  @Override public void onShowAds() {
    onShowAds = true;
  }

  @Override public void onHideAds() {
    onHideAds = true;
  }

  @Override public void onBillingAvailable() {
    onBillingAvailable = true;
  }

  @Override public void onBillingUnavailable() {
    onBillingUnavailable = true;
  }

  @Override public void onSkuDetails(@Nullable SkuDetails details) {
    onSkuDetails = true;
    skuDetails = details;
  }

  @Override public void onSkuConsumed() {
    onSkuConsumed = true;
  }

  @Override public void onFailedToConsumeSku() {
    onFailedToConsumeSku = true;
  }

  @Override public void onPurchaseRequested(@Nullable String payload) {
    onPurchaseRequested = true;
    this.payload = payload;
  }

  @Override public void onPurchaseDetails(@Nullable Purchase purchase) {
    onPurchaseDetails = true;
    this.purchase = purchase;
  }

  @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) {
    onPurchaseSuccessful = true;
    this.purchase = purchase;
  }

  @Override public void onPurchaseBadResult(int resultCode, @Nullable Intent data) {
    onPurchaseBadResult = true;
    this.resultCode = resultCode;
    this.intent = data;
  }

  @Override public void onPurchaseBadResponse(@Nullable Intent data) {
    onPurchaseBadResponse = true;
    this.intent = data;
  }

  @Override public void onPurchaseFailedVerification() {
    onPurchaseFailedVerification = true;
  }

  @Override public void onException(@NonNull Exception exception) {
    onException = true;
    this.exception = exception;
  }
}
