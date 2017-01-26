package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * Provides means to verify a purchase.
 */
public interface PurchaseVerifier {
  /**
   * Verifies a purchase.
   *
   * @param signatureBase64 the base64-encoded public key to use for verifying.
   * @param purchaseData the signed JSON string (signed, not encrypted)
   * @param purchaseSignature the signature for the data, signed with the private key
   * @return {@code true} if purchase is successfully verified, {@code false} otherwise.
   */
  @WorkerThread boolean verify(@Nullable String signatureBase64, @NonNull String purchaseData,
      @NonNull String purchaseSignature);
}
