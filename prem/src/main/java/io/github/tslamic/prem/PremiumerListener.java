package io.github.tslamic.prem;

import android.content.Intent;

/**
 * Callback interface responding to {@link Premiumer} events. All methods are invoked
 * on the main thread.
 *
 * @see SimplePremiumerListener
 */
public interface PremiumerListener {

    /**
     * Invoked if the sku has not yet been purchased and ads should be visible.
     */
    void onShowAds();

    /**
     * Invoked if the sku has been purchased and ads should not be visible.
     */
    void onHideAds();

    /**
     * Invoked if In-app Billing is unavailable.
     */
    void onBillingUnavailable();

    /**
     * Invoked when the SkuDetails information is ready.
     *
     * @param details the SkuDetails class holding the information,
     *                or {@code null}, if an error occurred.
     */
    void onSkuDetails(SkuDetails details);

    /**
     * Invoked when the sku has been successfully consumed.
     */
    void onSkuConsumed();

    /**
     * Invoked when the sku has not been successfully consumed.
     */
    void onFailedToConsumeSku();

    /**
     * Invoked on a successful sku purchase.
     *
     * @param purchase the purchase data.
     */
    void onPurchaseSuccessful(Purchase purchase);

    /**
     * Invoked when the sku purchase is unsuccessful.
     *
     * This happens if the Activity.onActivityResult resultCode is not equal to
     * Activity.RESULT_OK.
     *
     * @param result the onActivityResult resultCode value.
     * @param data   the onActivityResult data.
     */
    void onPurchaseBadResult(int result, Intent data);

    /**
     * Invoked when the sku purchase is unsuccessful.
     *
     * This happens if either onActivityResult data is null, or the billing response is
     * not BILLING_RESPONSE_RESULT_OK.
     *
     * @param data the onActivityResult data, which can be {@code null}.
     */
    void onPurchaseBadResponse(Intent data);

    /**
     * Invoked when the sku purchase is successful, but the request payload differs from the
     * purchase payload.
     *
     * Note that even if {@link io.github.tslamic.prem.Premiumer.Builder#autoNotifyAds(boolean)} is
     * {@code true}, {@link #onHideAds()} will NOT be invoked.
     *
     * @param purchase the Purchase data.
     * @param expected the expected token.
     * @param actual   the actual token.
     */
    void onPurchaseInvalidPayload(Purchase purchase, String expected, String actual);

}
