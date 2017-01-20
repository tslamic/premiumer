package io.github.tslamic.prem;

import android.content.Intent;

/**
 * Stub implementations of the {@link PremiumerListener}.
 * Extend this if you do not intend to override every method of {@link PremiumerListener}.
 */
public class SimplePremiumerListener implements PremiumerListener {

  /**
   * {@inheritDoc}
   */
  @Override public void onShowAds() {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onHideAds() {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onBillingUnavailable() {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onSkuDetails(SkuDetails details) {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onSkuConsumed() {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onFailedToConsumeSku() {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseSuccessful(Purchase purchase) {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseBadResult(int result, Intent data) {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseBadResponse(Intent data) {
  }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseInvalidPayload(Purchase purchase, String expected,
      String actual) {
  }
}
