package io.github.tslamic.prem;

import android.support.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.PremiumerHandler.ON_BILLING_AVAILABLE;
import static io.github.tslamic.prem.PremiumerHandler.ON_BILLING_UNAVAILABLE;
import static io.github.tslamic.prem.PremiumerHandler.ON_EXCEPTION;
import static io.github.tslamic.prem.PremiumerHandler.ON_FAILED_TO_CONSUME_SKU;
import static io.github.tslamic.prem.PremiumerHandler.ON_HIDE_ADS;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_BAD_RESPONSE;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_BAD_RESULT;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_DETAILS;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_FAILED_VERIFICATION;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_REQUESTED;
import static io.github.tslamic.prem.PremiumerHandler.ON_PURCHASE_SUCCESSFUL;
import static io.github.tslamic.prem.PremiumerHandler.ON_SHOW_ADS;
import static io.github.tslamic.prem.PremiumerHandler.ON_SKU_CONSUMED;
import static io.github.tslamic.prem.PremiumerHandler.ON_SKU_DETAILS;

@RunWith(RobolectricTestRunner.class) public class PremiumerHandlerTest {
  private static final int[] MESSAGES = {
      ON_SHOW_ADS, ON_HIDE_ADS, ON_BILLING_AVAILABLE, ON_BILLING_UNAVAILABLE, ON_SKU_DETAILS,
      ON_SKU_CONSUMED, ON_FAILED_TO_CONSUME_SKU, ON_PURCHASE_REQUESTED, ON_PURCHASE_DETAILS,
      ON_PURCHASE_SUCCESSFUL, ON_PURCHASE_BAD_RESULT, ON_PURCHASE_BAD_RESPONSE,
      ON_PURCHASE_FAILED_VERIFICATION, ON_EXCEPTION,
  };

  @Test public void messages() {
    for (int message : MESSAGES) {
      final MockPremiumerListener listener = new MockPremiumerListener();
      assertHandler(listener, message);
    }
  }

  private static void assertHandler(@NonNull MockPremiumerListener listener, int message) {
    final PremiumerHandler handler = new PremiumerHandler(listener);
    handler.obtainMessage(message).sendToTarget();
    assertInvoked(listener, message);
  }

  private static void assertInvoked(@NonNull MockPremiumerListener listener, int message) {
    switch (message) {
      case ON_SHOW_ADS:
        assertThat(listener.onShowAds).isTrue();
        break;
      case ON_HIDE_ADS:
        assertThat(listener.onHideAds).isTrue();
        break;
      case ON_BILLING_AVAILABLE:
        assertThat(listener.onBillingAvailable).isTrue();
        break;
      case ON_BILLING_UNAVAILABLE:
        assertThat(listener.onBillingUnavailable).isTrue();
        break;
      case ON_SKU_DETAILS:
        assertThat(listener.onSkuDetails).isTrue();
        break;
      case ON_SKU_CONSUMED:
        assertThat(listener.onSkuConsumed).isTrue();
        break;
      case ON_FAILED_TO_CONSUME_SKU:
        assertThat(listener.onFailedToConsumeSku).isTrue();
        break;
      case ON_PURCHASE_REQUESTED:
        assertThat(listener.onPurchaseRequested).isTrue();
        break;
      case ON_PURCHASE_DETAILS:
        assertThat(listener.onPurchaseDetails).isTrue();
        break;
      case ON_PURCHASE_SUCCESSFUL:
        assertThat(listener.onPurchaseSuccessful).isTrue();
        break;
      case ON_PURCHASE_BAD_RESULT:
        assertThat(listener.onPurchaseBadResult).isTrue();
        break;
      case ON_PURCHASE_BAD_RESPONSE:
        assertThat(listener.onPurchaseBadResponse).isTrue();
        break;
      case ON_PURCHASE_FAILED_VERIFICATION:
        assertThat(listener.onPurchaseFailedVerification).isTrue();
        break;
      case ON_EXCEPTION:
        assertThat(listener.onException).isTrue();
        break;
      default:
        Assert.fail();
    }
  }
}
