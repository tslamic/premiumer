package io.github.tslamic.prem;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;
import java.util.List;

import static io.github.tslamic.prem.Util.checkNotNull;

class SimpleBinder implements Binder {
  private final Context context;
  private ServiceConnection connection;

  SimpleBinder(@NonNull Context context) {
    this.context = checkNotNull(context, "context == null");
  }

  @NonNull @Override public IInAppBillingService service(IBinder binder) {
    return IInAppBillingService.Stub.asInterface(binder);
  }

  @NonNull @Override
  public Billing billing(@NonNull String packageName, @NonNull IInAppBillingService service) {
    return new SimpleBilling(packageName, service);
  }

  @Override public boolean hasBillingCapabilities(@NonNull Intent intent) {
    final PackageManager manager = context.getPackageManager();
    final List<ResolveInfo> list = manager.queryIntentServices(intent, 0);
    return list != null && !list.isEmpty();
  }

  @Override
  public boolean bind(@NonNull Intent intent, @NonNull ServiceConnection conn, int flags) {
    if (isBound()) {
      throw new IllegalStateException();
    }
    if (context.bindService(intent, conn, flags)) {
      connection = conn;
      return true;
    }
    return false;
  }

  @Override public void unbind() {
    if (connection != null) {
      context.unbindService(connection);
      connection = null;
    }
  }

  @Override public boolean isBound() {
    return connection != null;
  }
}
