package io.github.tslamic.prem;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.github.tslamic.prem.Util.checkNotNull;

/**
 * Builds a new {@link Premiumer} instance.
 */
public final class PremiumerBuilder
    implements Builder, Builder.SkuProvider, Builder.ListenerProvider {
  final Context context;
  String sku;
  PremiumerListener listener;
  Executor executor;
  boolean autoNotifyAds = true;
  int requestCode = 17;
  PayloadGenerator payloadGenerator;
  PurchaseVerifier purchaseVerifier;
  PurchaseCache purchaseCache;
  String signatureBase64;

  @NonNull public static SkuProvider with(@NonNull Context context) {
    return new PremiumerBuilder(context);
  }

  private PremiumerBuilder(@NonNull Context context) {
    this.context = checkNotNull(context, "context == null").getApplicationContext();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull @Override public ListenerProvider sku(@NonNull String sku) {
    this.sku = checkNotNull(sku, "sku == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder listener(@NonNull PremiumerListener listener) {
    this.listener = checkNotNull(listener, "listener == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder executor(@NonNull Executor executor) {
    this.executor = checkNotNull(executor, "executor == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder autoNotifyAds(boolean notify) {
    this.autoNotifyAds = notify;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder requestCode(int requestCode) {
    this.requestCode = requestCode;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder payloadGenerator(@NonNull PayloadGenerator generator) {
    this.payloadGenerator = checkNotNull(generator, "generator == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder purchaseVerifier(@NonNull PurchaseVerifier verifier) {
    this.purchaseVerifier = checkNotNull(verifier, "verifier == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder purchaseCache(@NonNull PurchaseCache cache) {
    this.purchaseCache = checkNotNull(cache, "cache == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Builder signatureBase64(@NonNull String signatureBase64) {
    this.signatureBase64 = checkNotNull(signatureBase64, "signatureBase64 == null");
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull public Premiumer build() {
    if (payloadGenerator == null) {
      payloadGenerator = new PayloadGenerator.UuidPayloadGenerator();
    }
    if (purchaseCache == null) {
      purchaseCache = new PurchaseCache.SharedPrefsCache(context);
    }
    if (executor == null) {
      executor = Executors.newSingleThreadExecutor();
    }
    return new SimplePremiumer(this);
  }
}
