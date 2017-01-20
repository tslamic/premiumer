package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

final class Util {
  private Util() { throw new AssertionError(); }

  static <T> T checkNotNull(@Nullable T obj, @NonNull String message) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
    return obj;
  }
}
