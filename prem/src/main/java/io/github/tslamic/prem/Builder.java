package io.github.tslamic.prem;

import android.content.Intent;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public interface Builder {
  /**
   * Specify an {@link Executor}.
   *
   * @throws NullPointerException if executor is {@code null}.
   */
  @NonNull Builder executor(@NonNull Executor executor);

  /**
   * Specify if {@link PremiumerListener#onShowAds()} or {@link PremiumerListener#onHideAds()}
   * should be automatically invoked after a successful bind, unbind, purchase or consumption.
   *
   * Set to {@code true} by default.
   */
  @NonNull Builder autoNotifyAds(boolean autoNotifyAds);

  /**
   * The integer result code returned by the in-app billing through its setResult(). Used to
   * determine if {@link Premiumer#handleActivityResult(int, int, Intent)} should be processed.
   */
  @NonNull Builder requestCode(int requestCode);

  /**
   * Used to generate a developer-specified {@link String} containing
   * supplemental information about a purchase. If none provided,
   * {@link PayloadGenerator.UuidPayloadGenerator} is used.
   *
   * @throws NullPointerException if executor is {@code null}.
   */
  @NonNull Builder payloadGenerator(@NonNull PayloadGenerator generator);

  /**
   * Used to verify a {@link Purchase}.
   */
  @NonNull Builder purchaseVerifier(@NonNull PurchaseVerifier verifier);

  /**
   * Used to store and retrieve a {@link Purchase}.
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
     * Specify the sku name, e.g. {@code android.test.purchased}.
     */
    @NonNull ListenerProvider sku(@NonNull String sku);
  }

  interface ListenerProvider {
    /**
     * Specify a {@link PremiumerListener} receiving {@link Premiumer} events.
     */
    @NonNull Builder listener(@NonNull PremiumerListener listener);
  }
}
