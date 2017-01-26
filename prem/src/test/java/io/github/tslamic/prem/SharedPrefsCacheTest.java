package io.github.tslamic.prem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.AssertUtil.assertEq;
import static io.github.tslamic.prem.TestUtil.JSON_PURCHASE;

@RunWith(RobolectricTestRunner.class) public class SharedPrefsCacheTest {
  private PurchaseCache.SharedPrefsCache cache;

  @Before public void setUp() throws Exception {
    cache = new PurchaseCache.SharedPrefsCache(RuntimeEnvironment.application);
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
