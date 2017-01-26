package io.github.tslamic.prem;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import static io.github.tslamic.prem.Util.checkNotNull;

class PremiumerHandler extends Handler {
  static final int ON_SHOW_ADS = 0;
  static final int ON_HIDE_ADS = 1;
  static final int ON_BILLING_AVAILABLE = 2;
  static final int ON_BILLING_UNAVAILABLE = 3;
  static final int ON_SKU_DETAILS = 4;
  static final int ON_SKU_CONSUMED = 5;
  static final int ON_FAILED_TO_CONSUME_SKU = 6;
  static final int ON_PURCHASE_REQUESTED = 7;
  static final int ON_PURCHASE_DETAILS = 8;
  static final int ON_PURCHASE_SUCCESSFUL = 9;
  static final int ON_PURCHASE_BAD_RESULT = 10;
  static final int ON_PURCHASE_BAD_RESPONSE = 11;
  static final int ON_PURCHASE_FAILED_VERIFICATION = 12;
  static final int ON_EXCEPTION = 13;

  private final PremiumerListener listener;

  PremiumerHandler(@NonNull PremiumerListener listener) {
    super(Looper.getMainLooper());
    this.listener = checkNotNull(listener, "listener == null");
  }

  @Override public void handleMessage(Message msg) {
    switch (msg.what) {
      case ON_SHOW_ADS:
        listener.onShowAds();
        break;
      case ON_HIDE_ADS:
        listener.onHideAds();
        break;
      case ON_BILLING_AVAILABLE:
        listener.onBillingAvailable();
        break;
      case ON_BILLING_UNAVAILABLE:
        listener.onBillingUnavailable();
        break;
      case ON_SKU_DETAILS:
        listener.onSkuDetails((SkuDetails) msg.obj);
        break;
      case ON_SKU_CONSUMED:
        listener.onSkuConsumed();
        break;
      case ON_FAILED_TO_CONSUME_SKU:
        listener.onFailedToConsumeSku();
        break;
      case ON_PURCHASE_REQUESTED:
        listener.onPurchaseRequested((String) msg.obj);
        break;
      case ON_PURCHASE_DETAILS:
        listener.onPurchaseDetails((Purchase) msg.obj);
        break;
      case ON_PURCHASE_SUCCESSFUL:
        listener.onPurchaseSuccessful((Purchase) msg.obj);
        break;
      case ON_PURCHASE_BAD_RESULT:
        listener.onPurchaseBadResult(msg.arg1, (Intent) msg.obj);
        break;
      case ON_PURCHASE_BAD_RESPONSE:
        listener.onPurchaseBadResponse((Intent) msg.obj);
        break;
      case ON_PURCHASE_FAILED_VERIFICATION:
        listener.onPurchaseFailedVerification();
        break;
      case ON_EXCEPTION:
        listener.onException((Exception) msg.obj);
        break;
      default:
        throw new AssertionError();
    }
  }
}
