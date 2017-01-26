package io.github.tslamic.prem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import org.json.JSONException;

import static io.github.tslamic.prem.Util.checkNotNull;
import static io.github.tslamic.prem.Util.isBlank;

/**
 * Used to store and retrieve a {@link Purchase}.
 */
public interface PurchaseCache {
  /**
   * Stores purchase information - in plain text - to {@link SharedPreferences}.
   */
  final class SharedPrefsCache implements PurchaseCache {
    private static final String PREFS_NAME = "__premiumer";
    private static final String PURCHASE_JSON = "__premiumer_purchase";
    private static final String PURCHASE_SIGNATURE = "__premiumer_signature";

    private final SharedPreferences prefs;

    SharedPrefsCache(@NonNull Context context) {
      checkNotNull(context, "context == null");
      prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits") // Already on worker thread.
    @WorkerThread @Override public void cache(@NonNull Purchase purchase) {
      final SharedPreferences.Editor editor = prefs.edit();
      editor.putString(PURCHASE_JSON, purchase.asJson());
      editor.putString(PURCHASE_SIGNATURE, purchase.signature);
      editor.commit();
    }

    @WorkerThread @Nullable @Override public Purchase load() {
      final String json = prefs.getString(PURCHASE_JSON, null);
      if (!isBlank(json)) {
        final String signature = prefs.getString(PURCHASE_SIGNATURE, null);
        try {
          return new Purchase(json, signature);
        } catch (JSONException ignore) {
        }
      }
      return null;
    }

    @Override public void clear() {
      prefs.edit().clear().apply();
    }
  }

  /**
   * Caches a {@link Purchase}. This will be invoked on a worker thread.
   */
  @WorkerThread void cache(@NonNull Purchase purchase);

  /**
   * Loads a {@link Purchase}. Returns {@code null} if no purchase is found.
   * This will be invoked on a worker thread.
   */
  @WorkerThread @Nullable Purchase load();

  /**
   * Clears this cache.
   */
  void clear();
}
