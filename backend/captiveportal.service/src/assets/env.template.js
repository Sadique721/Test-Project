(function (window) {
  window.env = window.env || {};

  // Environment variables
  //   window["env"]["savbill_COMMON_IP_PORT"] = "${savbill_COMMON_IP_PORT}";
  window["env"]["APIGATEWAY_IP_PORT"] = "${APIGATEWAY_IP_PORT}";
  window["env"]["savbill_OTP_PROFILE"] = "${savbill_OTP_PROFILE}";
  window["env"]["savbill_PLAN_NAME"] = "${savbill_PLAN_NAME}";
  window["env"]["FREE_PLAN"] = "${FREE_PLAN}";
  window["env"]["MVNO_ID"] = "${MVNO_ID}";
  window["env"]["PARTNER_ID"] = "${PARTNER_ID}";
  window["env"]["SERVICE_ID"] = "${SERVICE_ID}";
  window["env"]["SA_ID"] = "${SA_ID}";
  window["env"]["S_NAME"] = "${S_NAME}";
  window["env"]["USERNAME"] = "${USERNAME}";
  window["env"]["PASSWORD"] = "${PASSWORD}";
  window["env"]["FREEFLOW"] = "${FREEFLOW}";
  window["env"]["ONLINEFLOW"] = "${ONLINEFLOW}";
  window["env"]["VOUCHERFLOW"] = "${VOUCHERFLOW}";

  window["env"]["COUNTRY_ID"] = "${COUNTRY_ID}";
  window["env"]["CITY_ID"] = "${CITY_ID}";
  window["env"]["STATE_ID"] = "${STATE_ID}";
  window["env"]["PINCODE_ID"] = "${PINCODE_ID}";
  window["env"]["AREA_ID"] = "${AREA_ID}";
  window["env"]["BRANCH_ID"] = "${BRANCH_ID}";
  window["env"]["TITLE"] = "${TITLE}";
  window["env"]["VAST_URL"] = "${VAST_URL}";
  window["env"]["COUNTRY_CODE"] = "${COUNTRY_CODE}";
  window["env"]["SOCIAL_LOGIN_URL"] = "${SOCIAL_LOGIN_URL}";
})(this);
