package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Stub implementations of the {@link PremiumerListener}.
 * Extend this if you do not intend to override every method of {@link PremiumerListener}.
 */
@MainThread public class SimplePremiumerListener implements PremiumerListener {
  /**
   * {@inheritDoc}
   */
  @Override public void onShowAds() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onHideAds() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onBillingAvailable() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onBillingUnavailable() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onSkuDetails(@Nullable SkuDetails details) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onSkuConsumed() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onFailedToConsumeSku() { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseRequested(@Nullable String payload) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseDetails(@Nullable Purchase purchase) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseSuccessful(@NonNull Purchase purchase) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseBadResult(int resultCode, @Nullable Intent data) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseBadResponse(@Nullable Intent data) { }

  /**
   * {@inheritDoc}
   */
  @Override public void onPurchaseFailedVerification() { }
}
