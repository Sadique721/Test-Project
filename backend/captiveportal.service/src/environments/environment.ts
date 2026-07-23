// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  // savbill_COMMON_IP_PORT: window["env"]["savbill_COMMON_IP_PORT"] || "165.22.210.161:8090",
  APIGATEWAY_IP_PORT:
    window["env"]["APIGATEWAY_IP_PORT"] || "143.198.140.196:30080",
  savbill_OTP_PROFILE: window["env"]["savbill_OTP_PROFILE"] || "OTP",
  savbill_PLAN_NAME: window["env"]["savbill_PLAN_NAME"] || "p11",
  MVNO_ID: window["env"]["MVNO_ID"] || "2",
  FREE_PLAN: window["env"]["FREE_PLAN"] || "52",
  PARTNER_ID: window["env"]["PARTNER_ID"] || "1",
  SERVICE_ID: window["env"]["SERVICE_ID"] || "7",
  SA_ID: window["env"]["SA_ID"] || "5",
  S_NAME: window["env"]["S_NAME"] || "Broadband_K",
  USERNAME: window["env"]["USERNAME"] || "admin",
  PASSWORD: window["env"]["PASSWORD"] || "admin@123",
  FREEFLOW: window["env"]["FREEFLOW"] || true,
  ONLINEFLOW: window["env"]["ONLINEFLOW"] || true,
  VOUCHERFLOW: window["env"]["VOUCHERFLOW"] || true,

  COUNTRY_ID: window["env"]["COUNTRY_ID"] || "2",
  CITY_ID: window["env"]["CITY_ID"] || "2",
  STATE_ID: window["env"]["STATE_ID"] || "2",
  PINCODE_ID: window["env"]["PINCODE_ID"] || "2",
  AREA_ID: window["env"]["AREA_ID"] || "2",
  BRANCH_ID: window["env"]["BRANCH_ID"] || "2",
  VAST_URL:
    window["env"]["VAST_URL"] ||
    "http://143.110.241.176/revive/www/delivery/fc.php?script=apVideo:vast2&zoneid=2",
  TITLE: window["env"]["TITLE"] || "savbill Captive",
  COUNTRY_CODE: window["env"]["COUNTRY_CODE"] || "91",
  SOCIAL_LOGIN_URL:
    window["env"]["SOCIAL_LOGIN_URL"] || "http://localhost:3000/auth/twitter",
};
