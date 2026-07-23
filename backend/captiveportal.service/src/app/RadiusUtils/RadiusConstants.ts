import { HttpHeaders } from "@angular/common/http";
import { environment } from "src/environments/environment";
// const IP_PORT = environment.savbill_COMMON_IP_PORT;
export const APIGATEWAY_IP_PORT = environment.APIGATEWAY_IP_PORT;
export const savbill_OTP_PROFILE = environment.savbill_OTP_PROFILE;
export const savbill_PLAN_NAME = environment.savbill_PLAN_NAME;
export const FREE_PLAN = environment.FREE_PLAN;
export const PARTNER_ID = environment.PARTNER_ID;
export const SERVICE_ID = environment.SERVICE_ID;
export const SA_ID = environment.SA_ID;
export const S_NAME = environment.S_NAME;
export const USERNAME = environment.USERNAME;
export const PASSWORD = environment.PASSWORD;
export const FREEFLOW = environment.FREEFLOW;
export const ONLINEFLOW = environment.ONLINEFLOW;
export const VOUCHERFLOW = environment.VOUCHERFLOW;

export const COUNTRY_ID = environment.COUNTRY_ID;
export const CITY_ID = environment.CITY_ID;
export const STATE_ID = environment.STATE_ID;
export const PINCODE_ID = environment.PINCODE_ID;
export const AREA_ID = environment.AREA_ID;
export const BRANCH_ID = environment.BRANCH_ID;
export const TITLE = environment.TITLE;
export const VAST_URL = environment.VAST_URL;
export const COUNTRY_CODE = environment.COUNTRY_CODE;
export const SOCIAL_LOGIN_URL = environment.SOCIAL_LOGIN_URL;

//Constants for savbill Radius.
export const DELETE_GROUP_CONFIRM_MESSAGE =
  "Are you sure you want to delete this group?";
export const DELETE_CLIENT_CONFIRM_MESSAGE =
  "Are you sure you want to delete this client?";
export const DELETE_CUSTOMER_CONFIRM_MESSAGE =
  "Are you sure you want to delete this customer?";
export const DELETE_CONFIRM_MESSAGE = (str: String) =>
  `Are you sure you want to delete this ${str}?`;

export const savbill_COMMON_BASE_URL = `${
  APIGATEWAY_IP_PORT.startsWith("https://") ||
  APIGATEWAY_IP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${APIGATEWAY_IP_PORT}/api/v1/SavbillCommonGateway`;
export const savbill_RADIUS_BASE_URL = `${
  APIGATEWAY_IP_PORT.startsWith("https://") ||
  APIGATEWAY_IP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${APIGATEWAY_IP_PORT}/SavbillRadius`;
export const savbill_CMS_BASE_URL = `${
  APIGATEWAY_IP_PORT.startsWith("https://") ||
  APIGATEWAY_IP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${APIGATEWAY_IP_PORT}/api/v1/cpm`;
export const BSS_PORTAL_URL = `${
  APIGATEWAY_IP_PORT.startsWith("https://") ||
  APIGATEWAY_IP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${APIGATEWAY_IP_PORT}/api/v1/cpm/portal/subscriber`;

// export const FREE_PLAN = environment.FREE_PLAN;
// export const PARTNER_ID = environment.PARTNER_ID;
// export const SERVICE_ID = environment.SERVICE_ID;
// export const SERVICEAREA_ID = environment.SERVICEAREA_ID;
// export const savbill_COMMON_BASE_URL = `http://192.168.24.31:30080/api/v1`;
// export const savbill_WIFI_BASE_URL = `http://192.168.24.31:30080/savbillWifi`;
// export const savbill_RADIUS_BASE_URL = `http://192.168.24.31:30080/savbillRadius`;

//Constants for common use in both savbill Radius and savbill WIFI.
export const HEADER = new HttpHeaders()
  .set("content-type", "application/json")
  .set("authorization", "Basic YWRtaW46YWRtaW4xMjM=")
  .set("Access-Control-Allow-Origin", "*")
  .set("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE,OPTIONS");

export const CONFIRM_DIALOG_TITLE = "Record Delete Confirmation";
export const ACTIVE = "Active";
export const IN_ACTIVE = "Inactive";
//Used in pagination
export const ITEMS_PER_PAGE = 5;
export const MVNO_ID = environment.MVNO_ID;
export const getHeaders = { headers: HEADER };
export const currency = "";
export const currencyView = "";
