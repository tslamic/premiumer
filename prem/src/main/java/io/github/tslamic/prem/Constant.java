package io.github.tslamic.prem;

final class Constant {
  private Constant() {
    throw new AssertionError();
  }

  static final String RESPONSE_CODE = "RESPONSE_CODE";
  static final String RESPONSE_DETAILS_LIST = "DETAILS_LIST";
  static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
  static final String RESPONSE_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  static final String RESPONSE_SIGNATURE = "INAPP_DATA_SIGNATURE";
  static final String RESPONSE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  static final String REQUEST_ITEM_ID_LIST = "ITEM_ID_LIST";
  static final String BILLING_TYPE = "inapp";
  static final int BILLING_RESPONSE_RESULT_OK = 0;
}
