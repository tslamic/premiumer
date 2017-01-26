package io.github.tslamic.prem;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

class ActivityMock extends Activity {
  boolean startIntentSenderForResult;

  @Override
  public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent,
      int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
    startIntentSenderForResult = true;
  }
}
