package io.github.tslamic.prem;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Premiumer {

    public static class Builder {

        private ExecutorService mAsyncExecutor;
        private boolean mIsInternalExecutor;
        private boolean mAutoNotifyAds = true;
        private PremiumerListener mListener;
        private Context mContext;
        private String mSku;
        private String mLicenseKey;
        private int mRequestCode = 148;

        public Builder(Context context) {
            if (null == context) {
                throw new NullPointerException("context is null");
            }
            mContext = context.getApplicationContext();
        }

        /**
         * Specify the sku name, e.g. "android.test.purchased".
         */
        public Builder sku(String sku) {
            mSku = sku; // Validated when build() is invoked.
            return this;
        }

        /**
         * Specify the license key
         */
        public Builder licenseKey(String licenseKey) {
            mLicenseKey = licenseKey;
            return this;
        }

        /**
         * Specify the listener receiving {@link Premiumer} events.
         *
         * @param listener listener or {@code null}, if you wish to receive no events.
         */
        public Builder listener(PremiumerListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * Specify asynchronous executor.
         *
         * @throws NullPointerException if the executor is {@code null}.
         */
        public Builder asyncExecutor(ExecutorService executor) {
            if (null == executor) {
                throw new NullPointerException("executor is null");
            }
            mAsyncExecutor = executor;
            return this;
        }

        /**
         * Specify if {@link PremiumerListener#onShowAds()} or {@link PremiumerListener#onHideAds()}
         * should be automatically invoked after a successful purchase or consumption.
         *
         * Set to {@code true} by default.
         */
        public Builder autoNotifyAds(boolean notify) {
            mAutoNotifyAds = notify;
            return this;
        }

        /**
         * Specify the request code.
         **/
        public Builder requestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        /**
         * Returns a new {@link Premiumer} instance.
         *
         * @throws IllegalStateException if sku has not been set.
         */
        public Premiumer build() {
            if (TextUtils.isEmpty(mSku)) {
                throw new IllegalStateException("sku is empty");
            }
            if (null == mAsyncExecutor) {
                mAsyncExecutor = Executors.newSingleThreadExecutor();
                mIsInternalExecutor = true;
            }
            return new Premiumer(this);
        }

    }

    private static final String PREMIUMER_PREFS = "__premiumer_prefs";
    private static final String PREMIUMER_PURCHASE_PAYLOAD = "premiumer_payload";
    private static final String PREMIUMER_PURCHASE_DATA = "premiumer_purchase_data";
    private static final String PREMIUMER_PURCHASE_SIGNATURE = "premiumer_purchase_signature";

    private static final String RESPONSE_CODE = "RESPONSE_CODE";
    private static final String RESPONSE_DETAILS_LIST = "DETAILS_LIST";
    private static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    private static final String RESPONSE_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    private static final String RESPONSE_SIGNATURE = "INAPP_DATA_SIGNATURE";
    private static final String RESPONSE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    private static final String RESPONSE_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    private static final String RESPONSE_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    private static final String REQUEST_ITEM_ID_LIST = "ITEM_ID_LIST";
    private static final String BILLING_TYPE = "inapp";
    private static final int BILLING_RESPONSE_RESULT_OK = 0;

    private final ExecutorService mAsyncExecutor;
    private final SharedPreferences mPreferences;
    private final boolean mIsInternalExecutor;
    private final PremiumerHandler mHandler;
    private final boolean mAutoNotifyAds;
    private final String mPackageName;
    private final Context mContext;
    private final String mSku;
    private String mLicenseKey;
    private int mRequestCode;

    private ServiceConnection mServiceConnection;
    private boolean mIsBillingAvailable;
    private IInAppBillingService mService;
    private PremiumerListener mListener;

    private Premiumer(Builder builder) {
        mContext = builder.mContext;
        mPackageName = builder.mContext.getPackageName();
        mPreferences = mContext.getSharedPreferences(PREMIUMER_PREFS, Context.MODE_PRIVATE);
        mListener = builder.mListener;
        mAsyncExecutor = builder.mAsyncExecutor;
        mIsInternalExecutor = builder.mIsInternalExecutor;
        mHandler = new PremiumerHandler(builder.mListener);
        mSku = builder.mSku;
        mAutoNotifyAds = builder.mAutoNotifyAds;
        mRequestCode = builder.mRequestCode;
    }

    /**
     * Binds the In-app Billing service and checks if the ads should be displayed.
     */
    public void onStart() {
        mServiceConnection = new PremiumerServiceConnection();

        final Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");

        // Bind the above Billing service, if available.
        final List<ResolveInfo> list = mContext.getPackageManager().queryIntentServices(intent, 0);
        if (null == list || list.isEmpty()) {
            if (null != mListener) {
                mListener.onBillingUnavailable();
            }
        } else {
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Unbinds the In-app Billing service.
     */
    public void onStop() {
        if (null != mService) {
            if (null != mContext) {
                mContext.unbindService(mServiceConnection);
            }
            mServiceConnection = null;
            mService = null;
            mIsBillingAvailable = false;
        }
    }

    /**
     * Terminates event transmission and all pending requests.
     */
    public void onDestroy() {
        mListener = null;
        if (mIsInternalExecutor) {
            mAsyncExecutor.shutdown();
        }
    }

    /**
     * Initiates the In-app Billing purchase. Must be called from the main thread.
     *
     * @return true if the purchase flow was successfully executed.
     * @throws IllegalStateException if not called from the main (UI) thread.
     */
    public boolean purchase(Activity activity) {
        if (notOnMainThread()) {
            throw new IllegalStateException("must be invoked from the main thread");
        }
        if (!mIsBillingAvailable || null == activity) {
            return false;
        }
        try {
            // Generate a developer payload that will uniquely identify this purchase request.
            final String payload = UUID.randomUUID().toString();

            final Bundle bundle = mService.getBuyIntent(3, mPackageName, mSku, BILLING_TYPE, payload);
            final int response = bundle.getInt(RESPONSE_CODE);
            if (BILLING_RESPONSE_RESULT_OK == response) {
                mPreferences.edit().putString(PREMIUMER_PURCHASE_PAYLOAD, payload).commit();
                final PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
                activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                        mRequestCode, new Intent(), 0, 0, 0);
                return true;
            }
        } catch (RemoteException | IntentSender.SendIntentException ignore) {
        }
        return false;
    }

    /**
     * Handles an activity result.
     *
     * If you are calling {@link #purchase(Activity)}, then you must call this method from your
     * Activity's {@link android.app.Activity#onActivityResult} method.
     *
     * On a successful purchase, {@link PremiumerListener#onPurchaseSuccessful(Purchase)}
     * will be invoked. If {@link io.github.tslamic.prem.Premiumer.Builder#autoNotifyAds(boolean)}
     * is set to {@code true} {@link PremiumerListener#onHideAds()} invocation will follow.
     *
     * Must be called from the main thread.
     *
     * @param requestCode requestCode as you received it.
     * @param resultCode  resultCode as you received it.
     * @param data        Intent data as you received it.
     * @return {@code true} if the result was related to a purchase request and was handled;
     * {@code false} if the result was not related to a purchase and you should
     * handle it.
     * @throws IllegalStateException if not called from the main (UI) thread.
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (notOnMainThread()) {
            throw new IllegalStateException("must be invoked from the main thread");
        }

        // Ignore handling if not related to Premiumer.
        if (mRequestCode != requestCode) {
            return false;
        }

        // If the result is not RESULT_OK, notify and return.
        if (Activity.RESULT_OK != resultCode) {
            if (null != mListener) {
                mListener.onPurchaseBadResult(resultCode, data);
            }
            return true;
        }

        // If the intent data is missing, notify and return.
        if (null == data) {
            if (null != mListener) {
                mListener.onPurchaseBadResponse(null);
            }
            return true;
        }

        // Assume intents with no response code are OK (known issue).
        // If the response is not OK, notify and return.
        final int response = data.getIntExtra(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
        if (BILLING_RESPONSE_RESULT_OK != response) {
            if (null != mListener) {
                mListener.onPurchaseBadResponse(data);
            }
            return true;
        }

        // Handle a valid purchase.
        final String purchaseData = data.getStringExtra(RESPONSE_PURCHASE_DATA);
        final String signature = data.getStringExtra(RESPONSE_SIGNATURE);
        mPreferences.edit()
                .putString(PREMIUMER_PURCHASE_DATA, purchaseData)
                .putString(PREMIUMER_PURCHASE_SIGNATURE, signature)
                .commit();
        if (null != mListener) {
            final Purchase purchase = new Purchase(purchaseData, signature);
            if (payloadMatches(purchase) && verifyPurchaseSignature(purchase)) {
                mListener.onPurchaseSuccessful(purchase);
                if (mAutoNotifyAds) {
                    mListener.onHideAds();
                }
            } else {
                // Note that onHideAds() is purposely not called,
                // even if mAutoNotifyAds yields true.
                mListener.onPurchaseInvalidPayload(purchase,
                        getPurchasePayload(), purchase.developerPayload);
            }
        }
        return true;
    }

    /**
     * Requests detailed sku information. Once processed,
     * {@link PremiumerListener#onSkuDetails(SkuDetails)} will be called.
     *
     * @return true if the request was successfully enqueued.
     */
    public boolean requestSkuDetails() {
        if (!mIsBillingAvailable || mAsyncExecutor.isShutdown()) {
            return false;
        }
        mAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final SkuDetails details = requestSkuDetailsOnWorkerThread();
                mHandler.obtainMessage(PremiumerHandler.SKU_DETAILS, details).sendToTarget();
            }
        });
        return true;
    }

    private SkuDetails requestSkuDetailsOnWorkerThread() {
        final ArrayList<String> skuList = new ArrayList<>();
        skuList.add(mSku);

        final Bundle skuBundle = new Bundle();
        skuBundle.putStringArrayList(REQUEST_ITEM_ID_LIST, skuList);

        try {
            final Bundle details = mService.getSkuDetails(3, mPackageName, BILLING_TYPE, skuBundle);
            final int response = details.getInt(RESPONSE_CODE);
            if (BILLING_RESPONSE_RESULT_OK == response) {
                final ArrayList<String> list = details.getStringArrayList(RESPONSE_DETAILS_LIST);
                if (!(null == list || list.isEmpty())) {
                    final String json = list.get(0);
                    return new SkuDetails(json);
                }
            }
        } catch (RemoteException ignore) {
        }
        return null;
    }

    /**
     * Consumes the sku.
     *
     * If successfully consumed, {@link PremiumerListener#onSkuConsumed()}
     * will be invoked. If {@link io.github.tslamic.prem.Premiumer.Builder#autoNotifyAds(boolean)}
     * is set to {@code true} {@link PremiumerListener#onShowAds()} invocation will follow.
     *
     * If sku consumption fails, {@link PremiumerListener#onFailedToConsumeSku()} will be invoked.
     *
     * @return true if the request was successfully enqueued.
     */
    public boolean consumeSku() {
        if (!mIsBillingAvailable || mAsyncExecutor.isShutdown()) {
            return false;
        }
        mAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                consumeSkuOnWorkerThread();
            }
        });
        return true;
    }

    private void consumeSkuOnWorkerThread() {
        final Purchase purchase = getPurchaseInfo();
        if (null != purchase) {
            final String token = purchase.token;
            if (!TextUtils.isEmpty(token)) {
                try {
                    final int response = mService.consumePurchase(3, mPackageName, token);
                    if (BILLING_RESPONSE_RESULT_OK == response) {
                        mHandler.obtainMessage(PremiumerHandler.SKU_CONSUMED).sendToTarget();
                        if (mAutoNotifyAds) {
                            mHandler.obtainMessage(PremiumerHandler.SHOW_ADS).sendToTarget();
                        }
                        mPreferences.edit()
                                .remove(PREMIUMER_PURCHASE_DATA)
                                .remove(PREMIUMER_PURCHASE_PAYLOAD)
                                .remove(PREMIUMER_PURCHASE_SIGNATURE)
                                .commit();
                        return;
                    }
                } catch (RemoteException ignore) {
                }
            }
        }
        mHandler.obtainMessage(PremiumerHandler.SKU_NOT_CONSUMED).sendToTarget();
    }

    /**
     * Returns the sku purchase info if available, {@code null} otherwise.
     */
    public Purchase getPurchaseInfo() {
        final String json = mPreferences.getString(PREMIUMER_PURCHASE_DATA, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        final String signature = mPreferences.getString(PREMIUMER_PURCHASE_SIGNATURE, null);
        return new Purchase(json, signature);
    }

    private String getPurchasePayload() {
        return mPreferences.getString(PREMIUMER_PURCHASE_PAYLOAD, null);
    }

    private boolean ownsSku() {
        if (!mIsBillingAvailable || null == mService) {
            return false;
        }
        try {
            final Bundle items = mService.getPurchases(3, mPackageName, BILLING_TYPE, null);
            final int response = items.getInt(RESPONSE_CODE);
            if (BILLING_RESPONSE_RESULT_OK == response) {
                final ArrayList<String> purchases = items.getStringArrayList(RESPONSE_PURCHASE_DATA_LIST);
                final ArrayList<String> signatures = items.getStringArrayList(RESPONSE_SIGNATURE_LIST);
                if (purchases != null) {
                    for (int i = 0; i < purchases.size(); i++) {
                        final String json = purchases.get(i);
                        final String signature = signatures != null ? signatures.get(i) : null;
                        final Purchase purchase = new Purchase(json, signature);
                        final String sku = purchase.getSku();
                        if (sku != null && sku.equals(mSku)) {
                            return verifyPurchaseSignature(purchase);
                        }
                    }
                }
            }
        } catch (RemoteException ignore) {
        }
        return false;
    }

    private boolean payloadMatches(Purchase purchase) {
        final String payload = getPurchasePayload();
        return !TextUtils.isEmpty(payload) && payload.equals(purchase.developerPayload);
    }

    private boolean verifyPurchaseSignature(Purchase purchase) {
        if (TextUtils.isEmpty(mLicenseKey))
            return true;

        try {
            final String sku = purchase.getSku();
            final String data = purchase.asJson();
            final String signature = purchase.getSignature();

            return Security.verifyPurchase(sku, mLicenseKey, data, signature);
        } catch (Exception e) {
            return false;
        }
    }

    private void checkAds() {
        if (mAsyncExecutor.isShutdown()) {
            return;
        }
        mAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final int id = ownsSku() ? PremiumerHandler.HIDE_ADS : PremiumerHandler.SHOW_ADS;
                mHandler.obtainMessage(id).sendToTarget();
            }
        });
    }

    private class PremiumerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

            try {
                // Ensure the In-app Billing Version 3 API is supported.
                final int response = mService.isBillingSupported(3, mPackageName, BILLING_TYPE);
                if (BILLING_RESPONSE_RESULT_OK == response) {
                    mIsBillingAvailable = true;
                    checkAds();
                }
            } catch (RemoteException ignore) {
            }

            // In case it is not supported, or a RemoteException was thrown,
            // notify the listener that Billing is unavailable.
            if (!mIsBillingAvailable && null != mListener) {
                mListener.onBillingUnavailable();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mIsBillingAvailable = false;
        }

    }

    private static class PremiumerHandler extends Handler {

        static final int SHOW_ADS = 1;
        static final int HIDE_ADS = 2;
        static final int SKU_DETAILS = 3;
        static final int SKU_CONSUMED = 4;
        static final int SKU_NOT_CONSUMED = 5;

        private final WeakReference<PremiumerListener> mListener;

        public PremiumerHandler(PremiumerListener listener) {
            super(Looper.getMainLooper());
            mListener = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            final PremiumerListener listener = mListener.get();
            if (null == listener) {
                return;
            }
            switch (msg.what) {
                case SHOW_ADS:
                    listener.onShowAds();
                    break;
                case HIDE_ADS:
                    listener.onHideAds();
                    break;
                case SKU_DETAILS:
                    listener.onSkuDetails((SkuDetails) msg.obj);
                    break;
                case SKU_CONSUMED:
                    listener.onSkuConsumed();
                    break;
                case SKU_NOT_CONSUMED:
                    listener.onFailedToConsumeSku();
                    break;
                default:
                    break;
            }
        }
    }

    private static boolean notOnMainThread() {
        return Looper.getMainLooper().getThread() != Thread.currentThread();
    }

}
