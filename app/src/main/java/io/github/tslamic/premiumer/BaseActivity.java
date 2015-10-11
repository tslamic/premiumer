package io.github.tslamic.premiumer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.tslamic.prem.Premiumer;
import io.github.tslamic.prem.PremiumerListener;

abstract class BaseActivity extends AppCompatActivity implements PremiumerListener {

    private static final String SKU = "android.test.purchased";

    protected Premiumer mPremiumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPremiumer = new Premiumer.Builder(this).sku(SKU).listener(this).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPremiumer.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPremiumer.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPremiumer.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPremiumer.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
