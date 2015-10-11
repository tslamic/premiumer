![premiumer](http://i.imgur.com/lg5cEE3.png)

If your Android app is showing ads, but you would like to offer an option to remove them with a single in-app purchase, `Premiumer` is here to help!

Using it is as easy as pie. First, add a dependency to your `build.gradle`:

```gradle
compile 'com.github.tslamic.premiumer:library:1.0'
```

Then, set it up:

```java
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
```

Besides the lifecycle methods seen above, `Premiumer` also has

- `requestSkuDetails()` which, unsurprisingly, requests the sku details
- `purchase(Activity)` which initiates the In-app Billing purchase
- `getPurchaseInfo()` which returns purchase related information
- `consumeSku()` which can consume the purchased sku

All the above queries are then propagated to a `PremiumerListener` with the following callbacks:

| Method name   | Meaning      |
| ------------: |:-------------|
| `onShowAds()` | Invoked if the sku has not yet been purchased and ads should be visible. |
| `onHideAds()` | Invoked if the sku has been purchased and ads should not be visible. |
| `onBillingUnavailable()` | Invoked if In-app Billing is unavailable. |
| `onSkuDetails(SkuDetails)` | Invoked when `SkuDetails` information is ready. |
| `onSkuConsumed()` | Invoked when the sku has been successfully consumed. |
| `onFailedToConsumeSku()` | Invoked when the sku has not been successfully consumed. |
| `onPurchaseSuccessful(Purchase)` | Invoked on a successful sku purchase. |
| `onPurchaseBadResult(int, Intent)` | Invoked when the sku purchase is unsuccessful. |
| `onPurchaseBadResponse(Intent)` | Invoked when the sku purchase is unsuccessful. |
| `onPurchaseInvalidPayload(Purchase, String, String)`| Invoked when the sku purchase is successful, but the request payload differs from the purchase payload. |

Don't forget to check out the sample app.

License
---

	Copyright 2015 Tadej Slamic

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
