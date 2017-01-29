![premiumer](http://i.imgur.com/lg5cEE3.png)

Showing ads in your app? Wanna offer a single in-app purchase to remove them? Premiumer does just that!

[![Build Status](https://travis-ci.org/tslamic/premiumer.svg?branch=master)](https://travis-ci.org/tslamic/premiumer)
[![codecov](https://codecov.io/gh/tslamic/premiumer/branch/master/graph/badge.svg)](https://codecov.io/gh/tslamic/premiumer)

# How?
Using Premiumer is incredibly easy. First, add the following dependency:

```groovy
compile 'com.github.tslamic:premiumer2:1.0'
```

Then, create an instance:

```java
Premiumer premiumer = PremiumerBuilder.with(context)
    .sku(billingSku)
    .listener(premiumerListnener)
    .build();
```

Next, bind to the underlying in-app billing service. This should usually follow the `Activity` or `Fragment` lifecycle. Also, ensure `premiumer` can handle purchase results by overriding `onActivityResult`. For example:

```java
@Override protected void onStart() {
  super.onStart();
  premiumer.bind();
}

@Override protected void onStop() {
  super.onStop();
  premiumer.unbind();
}

@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (!premiumer.handleActivityResult(requestCode, resultCode, data)) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
```

If the in-app billing service is availabe and `premiumer` was successfully bound, `onBillingAvailable()` will be invoked on the listener you provided in the builder, `onBillingUnavailable()` otherwise.

You're now all set! :tada:

To perform a purchase, invoke `premiumer.purchase(Activity)`. This should be the same `Activity` overriding `onActivityResult`. Just before a purchase is shipped over to in-app billing service for processing, you'll receive a `onPurchaseRequested(String)` callback.

The `String` argument is a developer-specified payload, uniquely identifying a purchase. By default, Premiumer will use a randomly generated UUID, but you can easily provide your own `PayloadGenerator` when building a Premiumer instance:

```java
PremiumerBuilder.with(context)
    .sku(billingSku)
    .listener(premiumerListnener)
    .payloadGenerator(customGenerator); // custom generator
    .build();
```

Once a purchase is processed, the in-app billing service will return a result to the `Activity` triggering the purchase. If you've properly overriden `onActivityResult`, Premiumer will notify you what happened:

| Callback | Meaning |
| ------------: |:-------------|
| `onPurchaseBadResult (int, Intent)` | purchase result was not OK, e.g. the user cancelled the purchase flow |
| `onPurchaseBadResponse(Intent)` | in-app billing response was incomplete or missing information |
| `onPurchaseFailedVerification() ` | purchase verification failed |
| `onPurchaseSuccessful(Purchase)` | purchase succeeded |

By default, no purchase verification is done. You can change that, however, by providing a `PurchaseVerifier` instance when building Premiumer:

```java
PremiumerBuilder.with(context)
    .sku(billingSku)
    .listener(premiumerListnener)
    .purchaseVerifier(verifier); // custom verifier
    .build();
```

When a purchase is successful, Premiumer will, by default, store the purchase information in plain text to `SharedPreferences`. You can provide a different caching mechanism by specifying `PurchaseCache` when building Premiumer:

```java
PremiumerBuilder.with(context)
    .sku(billingSku)
    .listener(premiumerListnener)
    .purchaseCache(cache); // custom cache
    .build();
```

You can always obtain the information about the item you're about to purchase by invoking `premiumer.skuDetails()`. This will return `onSkuDetails(SkuDetails)` callback.

Similarly, once a purchase has been made, you can retrieve it by invoking `premiumer.purchaseDetails()`. This will invoke `onPurchaseDetails(Purchase` callback.

If, for any reason, you wish to consume a purchase, invoke `premiumer.consumeSku()`. If successful, `onSkuConsumed()` will be invoked, `onFailedToConsumeSku()` otherwise.

# Callbacks

As you might have guessed, all `Premiumer` interaction results in a `PremiumerListener` callback. Here's the complete list:

| Method name   | Meaning      |
| ------------: |:-------------|
| `onShowAds()` | Invoked if ads should be visible. |
| `onHideAds()` | Invoked if ads should be hidden. |
| `onBillingAvailable()` | Invoked if in-app Billing is available. |
| `onBillingUnavailable()` | Invoked if in-app Billing is unavailable. |
| `onSkuDetails(SkuDetails)` | Invoked when `SkuDetails` information is retireved. |
| `onSkuConsumed()` | Invoked if sku has been successfully consumed. |
| `onFailedToConsumeSku()` | Invoked if sku has not been successfully consumed. |
| `onPurchaseRequested(String)` | Invoked on a purchase request. |
| `onPurchaseDetails(Purchase)` | Invoked when purchase details are retrieved. |
| `onPurchaseSuccessful(Purchase)` | Invoked on a successful purchase. |
| `onPurchaseBadResult(int, Intent)` | Invoked when the sku purchase is unsuccessful. |
| `onPurchaseBadResponse(Intent)` | Invoked when the sku purchase returns bad data. |
| `onPurchaseFailedVerification()`| Invoked if a purchase has failed verification. |

If you feel that's too much, you can cherry pick the methods by extending `SimplePremiumerListener`.

Contribution
---

Improvement suggestions, bug reports, pull requests, etc. are very welcome and greatly appreciated!

License
---

	Copyright 2017 Tadej Slamic

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
