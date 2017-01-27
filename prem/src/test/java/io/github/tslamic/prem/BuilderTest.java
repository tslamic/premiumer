package io.github.tslamic.prem;

import android.content.Context;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.TestUtil.SKU;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class BuilderTest {
  private final Context context = RuntimeEnvironment.application;

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void contextNull() {
    PremiumerBuilder.with(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void skuNull() {
    PremiumerBuilder.with(context).sku(null).listener(null).build();
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void listenerNullInBuild() {
    PremiumerBuilder.with(context).sku(SKU).listener(null).build();
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void generatorNull() {
    partialBuilder().payloadGenerator(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void verifierNull() {
    partialBuilder().purchaseVerifier(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void cacheNull() {
    partialBuilder().purchaseCache(null);
  }

  @SuppressWarnings("ConstantConditions") @Test(expected = NullPointerException.class)
  public void signatureNull() {
    partialBuilder().signatureBase64(null);
  }

  @Test public void instance() {
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

    final PremiumerBuilder builder = (PremiumerBuilder) b;
    assertThat(builder.payloadGenerator).isNull();
    assertThat(builder.purchaseCache).isNull();
    assertThat(builder.executor).isNull();

    b.build();
    assertThat(builder.payloadGenerator).isNotNull();
    assertThat(builder.purchaseCache).isNotNull();
    assertThat(builder.executor).isNotNull();

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
    final PremiumerListener listener = mock(PremiumerListener.class);
    return PremiumerBuilder.with(context).sku(SKU).listener(listener);
  }
}
