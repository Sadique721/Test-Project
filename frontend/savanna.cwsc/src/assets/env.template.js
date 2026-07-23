(function (window) {
  window.env = window.env || {};

  // Environment variables

  window["env"]["APIGATEWAY_IP_PORT"] = "${APIGATEWAY_IP_PORT}";

  window["env"]["USERNAME"] = "${USERNAME}";
  window["env"]["PASSWORD"] = "${PASSWORD}";

  window["env"]["FACEBOOK_LINK"] = "${FACEBOOK_LINK}";
  window["env"]["TWITTER_LINK"] = "${TWITTER_LINK}";
  window["env"]["LINKEDIN_LINK"] = "${LINKEDIN_LINK}";
  window["env"]["TIMER_COUNT"] = "${TIMER_COUNT}";
  window["env"]["QR_COUNT"] = "${QR_COUNT}";
  window["env"]["FREE_PLAN"] = "${FREE_PLAN}";
  window["env"]["TITLE"] = "${TITLE}";
  window["env"]["SESSIONKEY"] = "${SESSIONKEY}";
  window["env"]["SESSIONTIMEOUT"] = "${SESSIONTIMEOUT}";
  window["env"]["LOGIN_CAPTCHA"] = "${LOGIN_CAPTCHA}";
  window["env"]["SECRET_KEY"] = "${SECRET_KEY}";

})(this);
