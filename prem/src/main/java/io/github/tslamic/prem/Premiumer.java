package io.github.tslamic.prem;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public interface Premiumer {
  /**
   * Binds to the in-app billing service.
   * Invokes {@link PremiumerListener#onBillingAvailable()} if successful, {@link
   * PremiumerListener#onBillingUnavailable()} otherwise.
   */
  @MainThread void bind();

  /**
   * Unbinds from the in-app billing service and invokes
   * {@link PremiumerListener#onBillingUnavailable()}.
   */
  @MainThread void unbind();

  /**
   * Starts the in-app billing purchase flow.
   * You must override {@code onActivityResult(int, int, Intent)} in the {@link Activity}
   * you specified to receive the result of this op.
   *
   * @return {@code true} if the flow is initiated, {@code false} otherwise.
   */
  @MainThread boolean purchase(@Nullable Activity activity);

  /**
   * Handles the in-app purchase flow result. This should be invoked in {@code
   * onActivityResult(int, int, Intent)} of the same {@link Activity} that initiated
   * {@link #purchase(Activity)}:
   *
   * <pre><code>
   * {@literal @}Override
   *  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   *    if (!premiumer.handleActivityResult(requestCode, resultCode, data)) {
   *      super.onActivityResult(requestCode, resultCode, data);
   *    }
   *  }
   * </code></pre>
   *
   * Will invoke:
   * <ul>
   * <li>{@link PremiumerListener#onPurchaseBadResult(int, Intent)}, if {@code resultCode !=
   * Activity.RESULT_OK}.</li>
   * <li>{@link PremiumerListener#onPurchaseBadResponse(Intent)}, if {@code data} is {@code null},
   * billing response is not OK or in-app billing information is missing.</li>
   * <li>{@link PremiumerListener#onPurchaseFailedVerification()} if provided {@link
   * PurchaseVerifier} determines a purchase is invalid.</li>
   * <li>{@link PremiumerListener#onPurchaseSuccessful(Purchase)} if a purchase has been
   * successful.</li>
   * </ul>
   *
   * @return {@code true} if handled, {@code false} otherwise.
   */
  @MainThread boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data);

  /**
   * Retrieves sku details.
   * If enqueued, {@link PremiumerListener#onSkuDetails(SkuDetails)} will be invoked.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean skuDetails();

  /**
   * Retrieves the purchase details.
   * If enqueued, {@link PremiumerListener#onPurchaseDetails(Purchase)} will be invoked.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean purchaseDetails();

  /**
   * Consumes sku.
   * Invokes {@link PremiumerListener#onSkuConsumed()} if successfully consumed, {@link
   * PremiumerListener#onFailedToConsumeSku()} otherwise.
   *
   * @return {@code true} if request has been enqueued, {@code false} otherwise.
   */
  boolean consumeSku();
}
