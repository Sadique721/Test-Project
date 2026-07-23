import { HttpHeaders } from "@angular/common/http";
import { environment } from "src/environments/environment";
const apigatewayIP_PORT = environment.APIGATEWAY_IP_PORT;
const apigatewayCommonIp_PORT = environment.APIGATEWAY_IP_PORT;

const userName = environment.USERNAME;
const passWord = environment.PASSWORD;
export const FREE_PLAN = environment.FREE_PLAN;
export const TIMER_COUNT = environment.TIMER_COUNT;
export const QR_COUNT = environment.QR_COUNT;
export const SESSIONKEY = environment.SESSIONKEY;
export const SESSIONTIMEOUT = environment.SESSIONTIMEOUT;
export const LOGIN_CAPTCHA = environment.LOGIN_CAPTCHA;
export const SECRET_KEY = environment.SECRET_KEY;

export const FACEBOOK_LINK = "https://www.facebook.com/people/Savbill-NetTech/100068155428264/";
export const TWITTER_LINK = "https://twitter.com/SavbillNettech";
export const LINKEDIN_LINK = "https://in.linkedin.com/company/savbill-net-tech";

export const StaffUsername = `${userName}`;
export const StaffPassword = `${passWord}`;
export const DEMOGRAPHICDATA = JSON.parse(localStorage.getItem("demographic"));
export var COUNTRY = "Country";
export var STATE = "State";
export var CITY = "City";
export var PINCODE = "Pincode";
export var AREA = "Area";
export var REGEX = "Number";
export const TITLE = environment.TITLE;
if (DEMOGRAPHICDATA) {
  COUNTRY = DEMOGRAPHICDATA[0].newName || "Country";
  STATE = DEMOGRAPHICDATA[1].newName || "State";
  CITY = DEMOGRAPHICDATA[2].newName || "City";
  PINCODE = DEMOGRAPHICDATA[3].newName || "Pincode";
  AREA = DEMOGRAPHICDATA[4].newName || "Area";
  REGEX = DEMOGRAPHICDATA[3].validationRegex || "Number";
} else {
  COUNTRY = "Country";
  STATE = "State";
  CITY = "City";
  PINCODE = "Pincode";
  AREA = "Area";
  REGEX = "Number";
}
export function masterdata(data) {
  COUNTRY = data[0].newName || "Country";
  STATE = data[1].newName || "State";
  CITY = data[2].newName || "City";
  PINCODE = data[3].newName || "Pincode";
  AREA = data[4].newName || "Area";
  REGEX = data[3].validationRegex || "Number";
}
//Constants for Savbill Radius.
export const DELETE_GROUP_CONFIRM_MESSAGE = "Are you sure you want to delete this group?";
export const DELETE_CLIENT_CONFIRM_MESSAGE = "Are you sure you want to delete this client?";
export const DELETE_CUSTOMER_CONFIRM_MESSAGE = "Are you sure you want to delete this customer?";
export const DELETE_CONFIRM_MESSAGE = (str: String) =>
  `Are you sure you want to delete this ${str}?`;

export const SAVBILL_COMMON_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/SavbillCommonGateway`;
export const SAVBILL_PRODUCT_MANAGEMENT_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/cpm`;
export const SAVBILL_SUBSCRIBER_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/cpm/portal/subscriber`;
export const SAVBILL_RADIUS_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/SavbillRadius`;
export const SAVBILL_NOTIFICATION_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/SavbillNotification`;
export const SAVBILL_REVENUE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/Revenue`;

export const SAVBILL_TICKET_MANAGEMENT = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/TicketManagement`;

export const SAVBILL_INVENTORY_MANAGEMENT = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/SavbillInventoryManagement`;
export const SAVBILL_INTEGRATION_SYSTEM_BASE_URL = `${
  apigatewayIP_PORT.startsWith("https://") || apigatewayIP_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayIP_PORT}/api/v1/SavbillIntegrationSystem`;

export const SAVBILL_API_GATEWAY_COMMON_MANAGEMENT = `${
  apigatewayCommonIp_PORT.startsWith("https://") || apigatewayCommonIp_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayCommonIp_PORT}/api/v1/SavbillCommonGateway`;

export const SAVBILL_REVENUE_MANAGEMENT_BASE_URL = `${
  apigatewayCommonIp_PORT.startsWith("https://") || apigatewayCommonIp_PORT.startsWith("http://")
    ? ""
    : "http://"
}${apigatewayCommonIp_PORT}/api/v1/Revenue`;
//Constants for common use in both Savbill Radius and Savbill WIFI.
export const HEADER = new HttpHeaders()
  .set("content-type", "application/json")
  .set("authorization", "Basic YWRtaW46YWRtaW4xMjM=")
  .set("Access-Control-Allow-Origin", "*")
  .set("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE,OPTIONS");

export const CONFIRM_DIALOG_TITLE = "Record Delete Confirmation";
export const ACTIVE = "Active";
export const IN_ACTIVE = "Inactive";

export const CUSTOMER_TYPE = {
  PREPAID: "Prepaid",
  POSTPAID: "Postpaid"
};
//Used in pagination
export const ITEMS_PER_PAGE = 5;
export const getHeaders = { headers: HEADER };
export const pageLimitOptions = [
  { value: 5 },
  { value: 10 },
  { value: 20 },
  { value: 50 },
  { value: 100 }
];
