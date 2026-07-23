// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  APIGATEWAY_IP_PORT: window["env"]["APIGATEWAY_IP_PORT"] || "localhost:30080",

  USERNAME: window["env"]["USERNAME"] || "Inderjeet@admin@Riya@MVNO2",
  PASSWORD: window["env"]["PASSWORD"] || "Inderjeet",
  TIMER_COUNT: window["env"]["TIMER_COUNT"] || "10",
  QR_COUNT: window["env"]["QR_COUNT"] || "180",
    LOGIN_CAPTCHA: window["env"]["LOGIN_CAPTCHA"] || "false",

  FACEBOOK_LINK:
    window["env"]["FACEBOOK_LINK"] ||
    "https://www.facebook.com/people/Savbill-NetTech/100068155428264/",
  TWITTER_LINK:
    window["env"]["TWITTER_LINK"] || "https://twitter.com/SavbillNettech",
  LINKEDIN_LINK:
    window["env"]["LINKEDIN_LINK"] ||
    "https://in.linkedin.com/company/savbill-net-tech",
  FREE_PLAN: window["env"]["FREE_PLAN"] || "20",
  TITLE: window["env"]["TITLE"] || "CWSC Portal",
  SESSIONKEY: window["env"]["SESSIONKEY"] || "mySessionKey",
  SESSIONTIMEOUT: window["env"]["SESSIONTIMEOUT"] || 15 * 60 * 1000,
  SECRET_KEY: window["env"]["SECRET_KEY"] || "howtotrainyourdragon",
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
