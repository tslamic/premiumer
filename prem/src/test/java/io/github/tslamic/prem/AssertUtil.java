package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.mockito.Mockito;

import static com.google.common.truth.Truth.assertThat;

final class AssertUtil {
  private AssertUtil() {
    throw new AssertionError();
  }

  static void assertEq(@Nullable Object fst, @Nullable Object snd) {
    assertThat(fst).isEqualTo(snd);
    if (fst != null) {
      assertThat(fst.hashCode()).isEqualTo(snd.hashCode());
    }
  }

  static <T> T assertInvokedOnce(@NonNull T mock) {
    return Mockito.verify(mock, Mockito.times(1));
  }

  static <T> T assertInvokedNever(@NonNull T mock) {
    return Mockito.verify(mock, Mockito.never());
  }

  static <T> void assertNoInteraction(@NonNull T mock) {
    Mockito.verifyZeroInteractions(mock);
  }
}
