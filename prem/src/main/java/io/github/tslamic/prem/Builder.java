package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public interface Builder {
  /**
   * Set the {@link Executor}.
   *
   * @throws NullPointerException if executor is {@code null}.
   */
  @NonNull Builder executor(@NonNull Executor executor);

  /**
   * If {@code true}, {@link PremiumerListener#onShowAds()} or {@link
   * PremiumerListener#onHideAds()} will be automatically invoked after a successful bind,
   * unbind, purchase or consumption. {@code true} by default.
   */
  @NonNull Builder autoNotifyAds(boolean autoNotifyAds);

  /**
   * The integer code returned by the in-app billing after a {@link
   * Premiumer#purchase(Activity)} request.
   */
  @NonNull Builder requestCode(int requestCode);

  /**
   * Generator for a developer-specified {@link String} containing
   * supplemental information about a purchase. {@link PayloadGenerator.UuidPayloadGenerator} by
   * default.
   *
   * @throws NullPointerException if generator is {@code null}.
   */
  @NonNull Builder payloadGenerator(@NonNull PayloadGenerator generator);

  /**
   * Verifies a {@link Purchase}. By default, no verification is performed.
   */
  @NonNull Builder purchaseVerifier(@NonNull PurchaseVerifier verifier);

  /**
   * Stores and retrieves a {@link Purchase}.
   * If none provided, {@link PurchaseCache.SharedPrefsCache} is used.
   */
  @NonNull Builder purchaseCache(@NonNull PurchaseCache cache);

  /**
   * Specify application's public key, encoded in base64.
   * This is used for verification of purchase signatures. You can find your app's base64-encoded
   * public key in your application's page on Google Play Developer Console. Note that this
   * is NOT your "developer public key".
   */
  @NonNull Builder signatureBase64(@NonNull String signatureBase64);

  /**
   * Builds a new {@link Premiumer} instance.
   */
  @NonNull Premiumer build();

  interface SkuProvider {
    /**
     * Set the sku name, e.g. {@code android.test.purchased}.
     *
     * @throws NullPointerException if sku is {@code null}.
     */
    @NonNull ListenerProvider sku(@NonNull String sku);
  }

  interface ListenerProvider {
    /**
     * Set a {@link PremiumerListener} receiving {@link Premiumer} events.
     *
     * @throws NullPointerException if listener is {@code null}.
     */
    @NonNull Builder listener(@NonNull PremiumerListener listener);
  }
}
