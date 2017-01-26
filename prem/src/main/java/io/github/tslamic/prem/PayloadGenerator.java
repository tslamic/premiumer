package io.github.tslamic.prem;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import java.util.UUID;

/**
 * Generates {@link String}s denoting a developer payload.
 */
public interface PayloadGenerator {
  /**
   * Generates random {@link UUID} strings denoting a developer payload.
   */
  final class UuidPayloadGenerator implements PayloadGenerator {
    /**
     * Generates a random {@link UUID} string.
     */
    @Override @MainThread @Nullable public String generate() {
      return UUID.randomUUID().toString();
    }
  }

  /**
   * Returns a developer-specified {@link String} containing supplemental information about a
   * purchase.
   */
  @MainThread @Nullable String generate();
}
