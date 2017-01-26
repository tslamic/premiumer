package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

final class Util {
  private Util() {
    throw new AssertionError();
  }

  static <T> T checkNotNull(@Nullable T obj, @NonNull String message) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
    return obj;
  }

  static boolean isBlank(@Nullable CharSequence string) {
    return string == null || string.toString().trim().length() == 0;
  }

  static boolean safeEquals(@Nullable Object fst, @Nullable Object snd) {
    return fst == snd || fst != null && fst.equals(snd);
  }

  @SafeVarargs static <T> ArrayList<T> arrayList(@NonNull T... items) {
    checkNotNull(items, "items == null");
    final ArrayList<T> list = new ArrayList<>(items.length);
    Collections.addAll(list, items);
    return list;
  }
}
