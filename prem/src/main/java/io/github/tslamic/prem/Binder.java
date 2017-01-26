package io.github.tslamic.prem;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;

interface Binder {
  @NonNull IInAppBillingService service(IBinder binder);
  @NonNull Billing billing(@NonNull String packageName, @NonNull IInAppBillingService service);
  boolean hasBillingCapabilities(@NonNull Intent intent);
  boolean bind(@NonNull Intent intent, @NonNull ServiceConnection conn, int flags);
  void unbind();
  boolean isBound();
}
