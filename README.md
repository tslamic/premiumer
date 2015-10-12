![premiumer](http://i.imgur.com/lg5cEE3.png)

If your Android app is showing ads, but you would like to offer an option to remove them with a single in-app purchase, `Premiumer` is here to help!

First, add a dependency to your `build.gradle`:

```gradle
compile 'com.github.tslamic.premiumer:library:1.0'
```

There is no need to add any `aidl` files. Just ensure you call the appropriate lifecycle methods, for example like so:

```java
abstract class BaseActivity extends AppCompatActivity 
								implements PremiumerListener {

    private static final String SKU = "android.test.purchased";

    protected Premiumer mPremiumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a new Premiumer instance.
        // There are a few other builder options available.
        mPremiumer = new Premiumer.Builder(this)
				        .sku(SKU)
				        .listener(this)
				        .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Binds the billing service, checks if 
        // ads should be shown and invokes either 
        // onShowAds() or onHideAds() on the registered listener.
        // If billing is unavailable, onBillingUnavailable() is 
        // invoked instead.
        mPremiumer.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbinds from the billing service.
        mPremiumer.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stops listening for events and 
        // dismisses any pending requests.
        mPremiumer.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If you call Premiumer.purchase, then you must call
        // Premiumer.handleActivityResult.
        if (!mPremiumer.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
```

With the above base class, extending it is equally easy. Check [the sample](https://github.com/tslamic/premiumer/blob/master/app/src/main/java/io/github/tslamic/premiumer/MainActivity.java). You will most likely want to use any of the remaining `Premiumer` methods:

- `requestSkuDetails()` which, unsurprisingly, requests the sku details
- `purchase(Activity)` which initiates the In-app Billing purchase
- `getPurchaseInfo()` which returns purchase related information
- `consumeSku()` which can consume the purchased sku

As you might have guessed, all `Premiumer` interaction results in a `PremiumerListener` callback. Here's the complete list:

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

If you feel that's too much, you can cherry pick the methods by extending `SimplePremiumerListener`.

Contribution
---

Improvement suggestions, bug reports, pull requests, etc. are very welcome and greatly appreciated!

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
