package io.github.tslamic.prem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import edu.emory.mathcs.backport.java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.Util.arrayList;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class SimpleBinderTest {
  private static Context queryServices(@Nullable final List<ResolveInfo> services) {
    return new ContextStub() {
      @Override public PackageManager getPackageManager() {
        return new SimpleBinderPackageManager(services);
      }
    };
  }

  private void assertBillingCapabilities(@NonNull Context context, boolean expected) {
    final Binder binder = new SimpleBinder(context);
    final Intent intent = new Intent();
    assertThat(binder.hasBillingCapabilities(intent)).isEqualTo(expected);
  }

  @Test public void hasBillingCapabilitiesNull() throws Exception {
    final Context context = queryServices(null);
    assertBillingCapabilities(context, false);
  }

  @Test public void hasBillingCapabilitiesEmpty() throws Exception {
    final Context context = queryServices(Collections.emptyList());
    assertBillingCapabilities(context, false);
  }

  @Test public void hasBillingCapabilitiesOk() throws Exception {
    final Context context = new SimpleBinderContext();
    assertBillingCapabilities(context, true);
  }

  @Test public void bind() throws Exception {
    final SimpleBinderContext context = new SimpleBinderContext();
    final Intent intent = new Intent();
    final ServiceConnection connection = new Connection();
    final Binder binder = new SimpleBinder(context);

    binder.bind(intent, connection, 0);

    assertThat(context.service).isEqualTo(intent);
    assertThat(context.connection).isEqualTo(connection);
    assertThat(context.flags).isEqualTo(0);
    assertThat(binder.isBound()).isTrue();
  }

  @Test public void bindFailure() throws Exception {
    final Context context = new ContextStub() {
      @Override public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return false;
      }
    };
    final Intent intent = new Intent();
    final ServiceConnection connection = new Connection();
    final Binder binder = new SimpleBinder(context);
    binder.bind(intent, connection, 0);
    assertThat(binder.isBound()).isFalse();
  }

  @Test public void unbind() throws Exception {
    final SimpleBinderContext context = new SimpleBinderContext();
    final Intent intent = new Intent();
    final ServiceConnection connection = new Connection();
    final Binder binder = new SimpleBinder(context);

    binder.bind(intent, connection, 0);
    binder.unbind();

    assertThat(context.service).isNull();
    assertThat(context.connection).isNull();
    assertThat(context.flags).isEqualTo(0);
    assertThat(binder.isBound()).isFalse();
  }

  static class SimpleBinderContext extends ContextStub {
    Intent service;
    ServiceConnection connection;
    int flags;

    @Override public boolean bindService(Intent service, ServiceConnection conn, int flags) {
      this.service = service;
      this.connection = conn;
      this.flags = flags;
      return true;
    }

    @Override public void unbindService(ServiceConnection conn) {
      if (conn.equals(connection)) {
        service = null;
        connection = null;
        flags = 0;
      }
    }

    @Override public PackageManager getPackageManager() {
      final ResolveInfo info = mock(ResolveInfo.class);
      return new SimpleBinderPackageManager(arrayList(info));
    }
  }

  static class SimpleBinderPackageManager extends PackageManagerStub {
    private final List<ResolveInfo> services;

    SimpleBinderPackageManager(@Nullable List<ResolveInfo> services) {
      this.services = services;
    }

    @Override public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
      return services;
    }
  }

  static class Connection implements ServiceConnection {
    boolean onServiceConnected;
    boolean onServiceDisconnected;

    @Override public void onServiceConnected(ComponentName name, IBinder service) {
      onServiceConnected = true;
    }

    @Override public void onServiceDisconnected(ComponentName name) {
      onServiceDisconnected = true;
    }
  }
}
