package io.github.tslamic.prem;

import android.os.Looper;
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

  static boolean safeEquals(@Nullable Object fst, @Nullable Object snd) {
    return fst == snd || fst != null && fst.equals(snd);
  }

  static boolean checkMainThread() {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
}
