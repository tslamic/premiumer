package io.github.tslamic.premiumer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import io.github.tslamic.prem.Purchase;
import io.github.tslamic.prem.SkuDetails;


public class MainActivity extends BaseActivity {

    private View mAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAds = findViewById(R.id.ads);
    }

    public void onPurchase(View v) {
        final boolean enqueued = mPremiumer.purchase(this);
        if (!enqueued) {
            showToast("Purchase request not enqueued.");
        }
    }

    public void onConsume(View v) {
        final boolean enqueued = mPremiumer.consumeSku();
        if (!enqueued) {
            showToast("Consume request not enqueued.");
        }
    }

    public void onSkuDetails(View v) {
        final boolean enqueued = mPremiumer.requestSkuDetails();
        if (!enqueued) {
            showToast("Sku details request not enqueued.");
        }
    }

    public void onPurchaseInfo(View v) {
        final Purchase info = mPremiumer.getPurchaseInfo();
        final String content = null == info ? "null" : info.asJson();
        InfoDialogFragment.newInstance("Purchase Info", content)
                .show(getSupportFragmentManager(), "purchase");
    }

    @Override
    public void onShowAds() {
        ensureInvocationIsOnMainThread();
        mAds.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideAds() {
        ensureInvocationIsOnMainThread();
        mAds.setVisibility(View.GONE);
    }

    @Override
    public void onBillingUnavailable() {
        ensureInvocationIsOnMainThread();
        showToast("Billing Unavailable.");
    }

    @Override
    public void onSkuDetails(SkuDetails details) {
        ensureInvocationIsOnMainThread();
        InfoDialogFragment.newInstance("Sku Details", details.asJson())
                .show(getSupportFragmentManager(), "sku");
    }

    @Override
    public void onPurchaseSuccessful(Purchase purchase) {
        ensureInvocationIsOnMainThread();
        showToast("Purchase Successful.");
    }

    @Override
    public void onPurchaseBadResult(int result, Intent data) {
        ensureInvocationIsOnMainThread();
        final String s = Activity.RESULT_CANCELED == result ? "Cancelled." : "First User.";
        showToast("Purchase " + s);
    }

    @Override
    public void onPurchaseBadResponse(Intent data) {
        ensureInvocationIsOnMainThread();
        showToast("Bad Purchase Response.");
    }

    @Override
    public void onPurchaseInvalidPayload(Purchase purchase, String expected, String actual) {
        ensureInvocationIsOnMainThread();
        showToast("Purchase tokens do not match.");
    }

    @Override
    public void onSkuConsumed() {
        ensureInvocationIsOnMainThread();
        showToast("Sku Consumed.");
    }

    @Override
    public void onFailedToConsumeSku() {
        ensureInvocationIsOnMainThread();
        showToast("Failed to consume sku.");
    }

    private static void ensureInvocationIsOnMainThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("callback not on the main thread");
        }
    }

    public static class InfoDialogFragment extends DialogFragment {

        public static InfoDialogFragment newInstance(String title, String content) {
            final Bundle args = new Bundle(2);
            args.putString("title", title);
            args.putString("content", TextUtils.isEmpty(content) ? "null" : content);

            final InfoDialogFragment f = new InfoDialogFragment();
            f.setArguments(args);
            return f;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            final String title = args.getString("title");
            final String content = args.getString("content");
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(content)
                    .create();
        }

    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

}
