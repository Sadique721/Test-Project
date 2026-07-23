(function (window) {
  window["env"] = window["env"] || {};

  // Environment variables
  // window["env"]["savbill_COMMON_IP_PORT"] = "192.168.24.9:8090";
  window["env"]["APIGATEWAY_IP_PORT"] = "216.48.180.92:38124";
  window["env"]["savbill_OTP_PROFILE"] = "OTP";
  window["env"]["savbill_PLAN_NAME"] = "wifiplan";
  window["env"]["FREE_PLAN"] = "1";
  window["env"]["MVNO_ID"] = "2";
  window["env"]["PARTNER_ID"] = "1";
  window["env"]["SERVICE_ID"] = "2";
  window["env"]["SA_ID"] = "1";
  window["env"]["S_NAME"] = "Broadband_K";
  window["env"]["USERNAME"] = "admin";
  window["env"]["PASSWORD"] = "admin@123";
  window["env"]["FREEFLOW"] = true;
  window["env"]["ONLINEFLOW"] = true;
  window["env"]["VOUCHERFLOW"] = true;

  window["env"]["COUNTRY_ID"] = "2";
  window["env"]["CITY_ID"] = "2";
  window["env"]["STATE_ID"] = "2";
  window["env"]["PINCODE_ID"] = "2";
  window["env"]["AREA_ID"] = "2";
  window["env"]["BRANCH_ID"] = "1";
  window["env"]["TITLE"] = "savbill Captive";
  window["env"]["COUNTRY_CODE"] = "91";
  window["env"]["VAST_URL"] =
    "http://143.110.241.176/revive/www/delivery/fc.php?script=apVideo:vast2&zoneid=2";
  window["env"]["SOCIAL_LOGIN_URL"] = "http://localhost:3000/auth/twitter";
})(this);
