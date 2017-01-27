package io.github.tslamic.prem;

import android.os.Handler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static io.github.tslamic.prem.AssertUtil.assertInvokedOnce;
import static io.github.tslamic.prem.PremiumerHandler.ON_BILLING_AVAILABLE;
import static io.github.tslamic.prem.PremiumerHandler.ON_BILLING_UNAVAILABLE;
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
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class PremiumerHandlerTest {
  private static final int[] MESSAGES = {
      ON_SHOW_ADS, ON_HIDE_ADS, ON_BILLING_AVAILABLE, ON_BILLING_UNAVAILABLE, ON_SKU_DETAILS,
      ON_SKU_CONSUMED, ON_FAILED_TO_CONSUME_SKU, ON_PURCHASE_REQUESTED, ON_PURCHASE_DETAILS,
      ON_PURCHASE_SUCCESSFUL, ON_PURCHASE_BAD_RESULT, ON_PURCHASE_BAD_RESPONSE,
      ON_PURCHASE_FAILED_VERIFICATION,
  };

  @Test public void messages() {
    for (int message : MESSAGES) {
      final PremiumerListener listener = mock(PremiumerListener.class);
      final Handler handler = TestFactory.premiumerHandler(listener);
      handler.obtainMessage(message).sendToTarget();
      assertInvoked(listener, message);
    }
  }

  private static void assertInvoked(PremiumerListener listener, int message) {
    switch (message) {
      case ON_SHOW_ADS:
        assertInvokedOnce(listener).onShowAds();
        break;
      case ON_HIDE_ADS:
        assertInvokedOnce(listener).onHideAds();
        break;
      case ON_BILLING_AVAILABLE:
        assertInvokedOnce(listener).onBillingAvailable();
        break;
      case ON_BILLING_UNAVAILABLE:
        assertInvokedOnce(listener).onBillingUnavailable();
        break;
      case ON_SKU_DETAILS:
        assertInvokedOnce(listener).onSkuDetails(null);
        break;
      case ON_SKU_CONSUMED:
        assertInvokedOnce(listener).onSkuConsumed();
        break;
      case ON_FAILED_TO_CONSUME_SKU:
        assertInvokedOnce(listener).onFailedToConsumeSku();
        break;
      case ON_PURCHASE_REQUESTED:
        assertInvokedOnce(listener).onPurchaseRequested(null);
        break;
      case ON_PURCHASE_DETAILS:
        assertInvokedOnce(listener).onPurchaseDetails(null);
        break;
      case ON_PURCHASE_SUCCESSFUL:
        assertInvokedOnce(listener).onPurchaseSuccessful(null);
        break;
      case ON_PURCHASE_BAD_RESULT:
        assertInvokedOnce(listener).onPurchaseBadResult(0, null);
        break;
      case ON_PURCHASE_BAD_RESPONSE:
        assertInvokedOnce(listener).onPurchaseBadResponse(null);
        break;
      case ON_PURCHASE_FAILED_VERIFICATION:
        assertInvokedOnce(listener).onPurchaseFailedVerification();
        break;
      default:
        Assert.fail();
    }
  }
}
