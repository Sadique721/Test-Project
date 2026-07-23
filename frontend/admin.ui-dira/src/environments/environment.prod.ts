export const environment = {
    production: true,
    APIGATEWAY_IP_PORT: window["env"]["APIGATEWAY_IP_PORT"] || "192.168.24.6:30085",
    BILLINGENGINE_COMMON_IP_PORT:
        window["env"]["BILLINGENGINE_COMMON_IP_PORT"] || "143.198.140.196:40080",
    OTP_GENERATE_USERNAME: window["env"]["OTP_GENERATE_USERNAME"] || "OTP",
    SAVBILLSALESCRMS_IP_PORT: window["env"]["SAVBILLSALESCRMS_IP_PORT"] || "59.144.18.212:60080",
    TACACS_IP_PORT: window["env"]["TACACS_IP_PORT"] || "localhost:30081",
    SAVBILLINVENTORYMANAGEMENT_IP_PORT:
        window["env"]["SAVBILLINVENTORYMANAGEMENT_IP_PORT"] || "192.168.24.6:30083",
    SAVBILL_TICKET_IP_PORT: ["env"]["SAVBILL_TICKET_IP_PORT"] || "192.168.24.6:30084",
    SAVBILL_API_GATEWAY_COMMON_PORT:
        window["env"]["SAVBILL_API_GATEWAY_COMMON_PORT"] || "192.168.24.6:30080",

    DaIMETER_IP_PORT:
        window["env"]["DAIMETER_PORT"] || "localhost:8080",

    TIMER_COUNT: window["env"]["TIMER_COUNT"] || "10",
    SERVICEAREA_ID: window["env"]["SERVICEAREA_ID"] || "5",
    COUNTRY_ID: window["env"]["COUNTRY_ID"] || "2",
    CITY_ID: window["env"]["CITY_ID"] || "2",
    STATE_ID: window["env"]["STATE_ID"] || "2",
    PINCODE_ID: window["env"]["PINCODE_ID"] || "2",
    AREA_ID: window["env"]["AREA_ID"] || "2",
    FOOTER: window["env"]["FOOTER"] || "Savbill",
    TITLE: window["env"]["TITLE"] || "Savbill BSS",
    LOGIN_CAPTCHA: window["env"]["LOGIN_CAPTCHA"] || "true",
    INDEPENDENT_AAA: window["env"]["INDEPENDENT_AAA"] || "false",
    GOOGLE_MAPS_API_KEY:
        window["env"]["GOOGLE_MAPS_API_KEY"] || "AIzaSyBJMMItJT9bMQlbJK8RnXhHi5rLrICje0s",
};
