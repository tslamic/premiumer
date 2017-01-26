package io.github.tslamic.prem;

import android.support.annotation.Nullable;

import static com.google.common.truth.Truth.assertThat;

final class AssertUtil {
  private AssertUtil() {
    throw new AssertionError();
  }

  static void assertEq(@Nullable Object fst, @Nullable Object snd) {
    assertThat(fst).isEqualTo(snd);
    assertThat(fst.hashCode()).isEqualTo(snd.hashCode());
  }
}
