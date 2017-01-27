package io.github.tslamic.prem;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertEq;
import static io.github.tslamic.prem.TestUtil.JSON_PURCHASE;

@RunWith(RobolectricTestRunner.class) public class CacheTest {
  private PurchaseCache cache;

  @Before public void setUp() {
    final Context context = RuntimeEnvironment.application;
    cache = TestFactory.cache(context);
  }

  @Test public void checkAll() throws Exception {
    final String signature = "signature";
    final Purchase p = new Purchase(JSON_PURCHASE, signature);
    cache.cache(p);

    final Purchase q = cache.load();
    assertEq(p, q);

    cache.clear();
    final Purchase r = cache.load();
    assertThat(r).isNull();
  }
}
