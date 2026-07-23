export const environment = {
  production: true,
  APIGATEWAY_IP_PORT: window["env"]["APIGATEWAY_IP_PORT"] || "localhost:30080",

  USERNAME: window["env"]["USERNAME"] || "Inderjeet@admin@Riya@MVNO2",
  PASSWORD: window["env"]["PASSWORD"] || "Inderjeet",

  FACEBOOK_LINK:
    window["env"]["FACEBOOK_LINK"] ||
    "https://www.facebook.com/people/Savbill-NetTech/100068155428264/",
  TWITTER_LINK:
    window["env"]["TWITTER_LINK"] || "https://twitter.com/SavbillNettech",
  LINKEDIN_LINK:
    window["env"]["LINKEDIN_LINK"] ||
    "https://in.linkedin.com/company/savbill-net-tech",
  TIMER_COUNT: window["env"]["TIMER_COUNT"] || "10",
  QR_COUNT: window["env"]["TIMER_COUNT"] || "180",
  FREE_PLAN: window["env"]["FREE_PLAN"] || "20",
  TITLE: window["env"]["TITLE"] || "CWSC Portal",
  SESSIONKEY: window["env"]["SESSIONKEY"] || "mySessionKey",
  SESSIONTIMEOUT: window["env"]["SESSIONTIMEOUT"] || 15 * 60 * 1000,
    LOGIN_CAPTCHA: window["env"]["LOGIN_CAPTCHA"] || "true",
  SECRET_KEY: window["env"]["SECRET_KEY"] || "howtotrainyourdragon",
};
