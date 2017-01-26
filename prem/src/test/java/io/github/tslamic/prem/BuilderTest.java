package io.github.tslamic.prem;

import android.content.Context;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.TestUtil.SKU;

@RunWith(RobolectricTestRunner.class) public class BuilderTest {
  private final Context context = RuntimeEnvironment.application;
  private final PremiumerListener listener = new MockPremiumerListener();

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void contextNull() throws Exception {
    PremiumerBuilder.with(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void skuNull() throws Exception {
    PremiumerBuilder.with(context).sku(null).listener(null).build();
  }

  @Test(expected = NullPointerException.class) public void listenerNullInBuild() throws Exception {
    PremiumerBuilder.with(context).sku(SKU).listener(null).build();
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void generatorNull() throws Exception {
    partialBuilder().payloadGenerator(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void verifierNull() throws Exception {
    partialBuilder().purchaseVerifier(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void cacheNull() throws Exception {
    partialBuilder().purchaseCache(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void signatureNull() throws Exception {
    partialBuilder().signatureBase64(null);
  }

  @Test public void instance() throws Exception {
    final String signature = "signature";
    final boolean ads = false;
    final int requestCode = 666;

    final PremiumerListener listener = new SimplePremiumerListener();
    final Builder b = PremiumerBuilder.with(context)
        .sku(SKU)
        .listener(listener)
        .autoNotifyAds(ads)
        .requestCode(requestCode)
        .signatureBase64(signature);
    b.build();

    assertThat(b).isInstanceOf(PremiumerBuilder.class);
    final PremiumerBuilder builder = (PremiumerBuilder) b;

    assertThat(builder.context).isEqualTo(context);
    assertThat(builder.sku).isEqualTo(SKU);
    assertThat(builder.listener).isInstanceOf(SimplePremiumerListener.class);
    assertThat(builder.executor).isInstanceOf(Executor.class);
    assertThat(builder.autoNotifyAds).isEqualTo(ads);
    assertThat(builder.requestCode).isEqualTo(requestCode);
    assertThat(builder.payloadGenerator).isInstanceOf(PayloadGenerator.UuidPayloadGenerator.class);
    assertThat(builder.purchaseVerifier).isNull();
    assertThat(builder.purchaseCache).isInstanceOf(PurchaseCache.SharedPrefsCache.class);
    assertThat(builder.signatureBase64).isEqualTo(signature);
  }

  private Builder partialBuilder() {
    return PremiumerBuilder.with(context).sku(SKU).listener(listener);
  }
}
