package io.github.tslamic.premiumer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import io.github.tslamic.prem.Premiumer;
import io.github.tslamic.prem.PremiumerBuilder;
import io.github.tslamic.prem.Purchase;
import io.github.tslamic.prem.SkuDetails;

public class MainActivity extends AppCompatActivity {
  class AdsListener extends LogPremiumerListener {
    @Override public void onBillingAvailable() {
      super.onBillingAvailable();
      status.setTextColor(Color.GREEN);
      status.setText("Billing Available");
    }

    @Override public void onBillingUnavailable() {
      super.onBillingUnavailable();
      status.setTextColor(Color.RED);
      status.setText("Billing Unavailable");
    }

    @Override public void onShowAds() {
      super.onShowAds();
      ads.setVisibility(View.VISIBLE);
    }

    @Override public void onHideAds() {
      super.onHideAds();
      ads.setVisibility(View.GONE);
    }

    @Override public void onSkuDetails(@Nullable SkuDetails details) {
      super.onSkuDetails(details);
      show("Sku Details", details);
    }

    @Override public void onPurchaseDetails(@Nullable Purchase purchase) {
      super.onPurchaseDetails(purchase);
      show("Purchase Details", purchase);
    }

    private void show(@NonNull String title, @Nullable Object content) {
      final String c = content == null ? "Not Available" : content.toString();
      InfoDialogFragment.newInstance(title, c).show(getSupportFragmentManager(), "tag");
    }
  }

  private Premiumer premiumer;
  private TextView status;
  private View ads;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    status = (TextView) findViewById(R.id.status);
    ads = findViewById(R.id.ads);
    premiumer = PremiumerBuilder.with(this)
        .sku("android.test.purchased")
        .listener(new AdsListener())
        .build();
  }

  //@Override protected void onStart() {
  //  super.onStart();
  //  premiumer.bind();
  //}
  //
  //@Override protected void onStop() {
  //  super.onStop();
  //  premiumer.unbind();
  //}

  @Override protected void onResume() {
    super.onResume();
    premiumer.bind();
  }

  @Override protected void onPause() {
    super.onPause();
    premiumer.unbind();
  }

  public void onPurchase(View v) {
    premiumer.purchase(this);
  }

  public void onConsume(View v) {
    premiumer.consumeSku();
  }

  public void onSkuDetails(View v) {
    premiumer.skuDetails();
  }

  public void onPurchaseDetails(View v) {
    premiumer.purchaseDetails();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!premiumer.handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public static class InfoDialogFragment extends DialogFragment {
    static InfoDialogFragment newInstance(String title, String content) {
      final Bundle args = new Bundle(2);
      args.putString("title", title);
      args.putString("content", TextUtils.isEmpty(content) ? "null" : content);

      final InfoDialogFragment f = new InfoDialogFragment();
      f.setArguments(args);
      return f;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Bundle args = getArguments();
      final String title = args.getString("title");
      final String content = args.getString("content");
      return new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(content).create();
    }
  }
}
