package io.github.tslamic.prem;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public interface Premiumer {
  /**
   * Binds to the in-app billing service.
   * If successful, {@link PremiumerListener#onBillingAvailable()} will be invoked.
   */
  @MainThread void bind();

  /**
   * Unbinds from the in-app billing service.
   * If successful, {@link PremiumerListener#onBillingUnavailable()} ()} will be invoked.
   */
  @MainThread void unbind();

  /**
   * Starts in-app billing purchase flow.
   *
   * @return {@code true} if the flow is initiated, {@code false} otherwise.
   */
  @MainThread boolean purchase(@Nullable Activity activity);

  /**
   * Handles the in-app purchase flow result.
   *
   * @return {@code true} if handled, {@code false} otherwise.
   */
  @MainThread boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data);

  /**
   * Requests sku details.
   * If successful, {@link PremiumerListener#onSkuDetails(SkuDetails)} will be invoked.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean skuDetails();

  /**
   * Retrieves the purchase details.
   * If successful, {@link PremiumerListener#onPurchaseDetails(Purchase)} will be invoked.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean purchaseDetails();

  /**
   * Consumes sku.
   * If successful, {@link PremiumerListener#onSkuConsumed()} will be invoked.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean consumeSku();
}
