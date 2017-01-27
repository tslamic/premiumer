package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Callback interface responding to {@link Premiumer} events. All listener methods will be invoked
 * on the main thread.
 * If you need to override only a handful of methods, use {@link SimplePremiumerListener}.
 *
 * @see SimplePremiumerListener
 */
@MainThread public interface PremiumerListener {
  /**
   * Invoked if ads should be visible.
   */
  void onShowAds();

  /**
   * Invoked if ads should be hidden.
   */
  void onHideAds();

  /**
   * Invoked if in-app Billing is available.
   */
  void onBillingAvailable();

  /**
   * Invoked if in-app Billing is unavailable.
   */
  void onBillingUnavailable();

  /**
   * Invoked when {@link SkuDetails} information is retrieved.
   *
   * @param details {@link SkuDetails} instance or {@code null}, if an error occurred.
   */
  void onSkuDetails(@Nullable SkuDetails details);

  /**
   * Invoked if sku has been successfully consumed.
   */
  void onSkuConsumed();

  /**
   * Invoked if sku has not been successfully consumed.
   */
  void onFailedToConsumeSku();

  /**
   * Invoked on a purchase request.
   *
   * @param payload a developer-specified {@link String} containing supplemental information about
   * a purchase.
   */
  void onPurchaseRequested(@Nullable String payload);

  /**
   * Invoked when purchase details are retrieved.
   *
   * @param purchase {@link Purchase} instance or {@code null}, if unavailable.
   */
  void onPurchaseDetails(@Nullable Purchase purchase);

  /**
   * Invoked on a successful purchase.
   *
   * @param purchase the purchase data.
   */
  void onPurchaseSuccessful(@NonNull Purchase purchase);

  /**
   * Invoked when the sku purchase is unsuccessful.
   * This happens if the Activity.onActivityResult resultCode is not equal to
   * Activity.RESULT_OK.
   *
   * @param resultCode the onActivityResult resultCode value.
   * @param data the onActivityResult data.
   */
  void onPurchaseBadResult(int resultCode, @Nullable Intent data);

  /**
   * Invoked when the sku purchase is unsuccessful.
   * This happens if either onActivityResult data is null, or the billing response is invalid.
   *
   * @param data the onActivityResult data, which can be {@code null}.
   */
  void onPurchaseBadResponse(@Nullable Intent data);

  /**
   * Invoked if a purchase has failed verification.
   */
  void onPurchaseFailedVerification();
}
