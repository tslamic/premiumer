package io.github.tslamic.prem;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.github.tslamic.prem.stub.ContextStub;
import io.github.tslamic.prem.stub.PackageManagerStub;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.tslamic.prem.TestFactory.binder;
import static io.github.tslamic.prem.Util.arrayList;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class) public class SimpleBinderTest {
  private void assertBillingCapabilities(@NonNull Context context, boolean expected) {
    final Binder binder = binder(context);
    final Intent intent = new Intent();
    assertThat(binder.hasBillingCapabilities(intent)).isEqualTo(expected);
  }

  @Test public void hasBillingCapabilitiesNull() {
    final Context context = new SimpleBinderContext(null);
    assertBillingCapabilities(context, false);
  }

  @Test public void hasBillingCapabilitiesEmpty() {
    final Context context = new SimpleBinderContext();
    assertBillingCapabilities(context, false);
  }

  @Test public void hasBillingCapabilitiesOk() {
    final List<ResolveInfo> list = arrayList(mock(ResolveInfo.class));
    final Context context = new SimpleBinderContext(list);
    assertBillingCapabilities(context, true);
  }

  @Test public void bind() {
    final SimpleBinderContext context = new SimpleBinderContext();
    final Intent intent = new Intent();
    final ServiceConnection connection = mock(ServiceConnection.class);
    final Binder binder = binder(context);

    binder.bind(intent, connection, 0);

    assertThat(context.service).isEqualTo(intent);
    assertThat(context.connection).isEqualTo(connection);
    assertThat(context.flags).isEqualTo(0);
    assertThat(binder.isBound()).isTrue();
  }

  @Test public void bindFailure() {
    final Context context = new ContextStub() {
      @Override public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return false;
      }
    };
    final Intent intent = new Intent();
    final ServiceConnection connection = mock(ServiceConnection.class);
    final Binder binder = binder(context);
    binder.bind(intent, connection, 0);
    assertThat(binder.isBound()).isFalse();
  }

  @Test public void unbind() {
    final SimpleBinderContext context = new SimpleBinderContext();
    final Intent intent = new Intent();
    final ServiceConnection connection = mock(ServiceConnection.class);
    final Binder binder = binder(context);

    binder.bind(intent, connection, 0);
    binder.unbind();

    assertThat(context.service).isNull();
    assertThat(context.connection).isNull();
    assertThat(context.flags).isEqualTo(0);
    assertThat(binder.isBound()).isFalse();
  }

  static class SimpleBinderContext extends ContextStub {
    final PackageManager manager;
    Intent service;
    ServiceConnection connection;
    int flags;

    SimpleBinderContext() {
      this(java.util.Collections.<ResolveInfo>emptyList());
    }

    SimpleBinderContext(List<ResolveInfo> services) {
      this.manager = new SimpleBinderPackageManager(services);
    }

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
      return manager;
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
}
