package com.savbill.radius.kafka;

public class MessageConstants {
	public static final String MOBILE_NUMBER = "mobileNumber";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String PASSWORD = "password";
	public static final String USER_NAME = "userName";
	public static final String EMAIL_ID = "emailId";
	public static final String SOURCE_NAME_SAVBILL_RADIUS = "Savbill Radius";

	public static final String SLICE_CHUNK = "sliceChunk";

	public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
	public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
	public static final String DEAD_LETTER_KEY = "deadLetterKey";
	public static final String SAVBILL_EXCHANGE = "savbill.exchange";

	public static final String QUEUE_LOGIN_SUCCESS = "login.success.queue";
	public static final String QUEUE_LOGIN_FAILURE = "login.failure.queue";
	public static final String QUEUE_CREATE_CUSTOMER = "create.customer.queue";
	public static final String QUEUE_UPDATE_CUSTOMER = "update.customer.queue";
	public static final String QUEUE_DELETE_CUSTOMER = "delete.customer.queue";
	public static final String QUEUE_CHANGE_STATUS = "change.status.queue";
	public static final String QUEUE_CHANGE_PASSWORD = "change.password.queue";
	public static final String QUEUE_RECHARGE_QUOTA = "recharge.quota.queue";
	public static final String QUEUE_REGISTRATION_SUCCESS = "registration.success.queue";
	public static final String QUEUE_REGISTRATION_FAILURE = "registration.failure.queue";

	public static final String QUEUE_ROLE_SUCCESS_RADIUS =  "role.success.queue.radius";
	public static final String QUEUE_LOGIN_SUCCESS_RADIUS = "login.success.queue.radius";
	public static final String QUEUE_LOGIN_FAILURE_RADIUS = "login.failure.queue.radius";
	public static final String QUEUE_STAFF_SUCCESS_RADIUS = "staff.success.queue.radius";
	public static final String QUEUE_UPDATE_QUOTA = "update.bssquota.queue";
	public static final String QUEUE_LOCATION_MASTER = "location.master.wifi";

	public static final String QUEUE_APIGW_CUSTOMER = "apigw.customer.queue";
	public static final String QUEUE_CUSTOMER_QUOTA_DETAILS = "apigw.customer.quota.details";
	public static final String QUEUE_CUSTOMER_PACKAGE_REL = "apigw.customer.package.rel";
	public static final String QUEUE_APIGW_POSTPAIDPLAN = "apigw.plan";
	public static final String QUEUE_APIGW_QOS_POLICY = "apigw.qospolicy";
	public static final String QUEUE_APIGW_CUST_REPLY = "apigw.custreply";
	public static final String QUEUE_RADIUS_CUST_MAC_ADD = "radius.add.mac";
	public static final String QUEUE_RADIUS_CUST_MAC_REMOVE = "radius.remove.mac";
	public static final String QUEUE_APIGW_CUSTOMER_MAC_MAPPING = "apigw.customer.mac.mapping";

	public static final String QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS="staff_create_from_bss";

	public static final String QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS="service_area_created_from_bss";

	public static final String QUEUE_UPDATE_CUSTOMER_QUOTA = "update.customer.quota";

	public static final String QUEUE_UPDATE_CUSTOMER_PASSWORD = "update.customer.password";

	public static final String QUEUE_APIGW_TIME_BASE_POLICY = "apigw.timebasepolicy";

	public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY = "apigw.create.timebasepolicy";

	public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS = "apigw.create.timebasepolicydetails";

	public static final String QUEUE_RADIUS_COA_DM = "apiw.send.radius.coadm";

	public static final String QUEUE_RADIUS_CUSTOMER_UPDATE_STATUS = "apiw.send.radius.customer.update.status";

	public static final String QUEUE_APIGW_SERVICE_START_STOP = "apigw.service.status.change";

	public static final String QUEUE_SEND_NASUPDATE = "bss.radius.send.nasupdate";

	public static final String QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.create.custservicechargeipdtls";

	public static final String QUEUE_APIGW_UPDATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.update.custservicechargeipdtls";

	public static final String QUEUE_SEND_QUOTA_FROM_RADIUS = "send.quota.detail.radius.queue";

	public static final String QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS = "send.quota.intrim.radius.queue";


	public static final String QUEUE_SEND_CREATE_DATA_ROLE_RADIUS = "queue.send.create.data.role.to.radius";
	public static final String QUEUE_SEND_DELETE_DATA_ROLE_RADIUS = "queue.send.delete.data.role.to.radius";
    public static final String QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_RADIUS = "queue.businessunit.create.data.share.radius";
    public static final String QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_RADIUS = "queue.businessunit.update.data.share.radius";

	public static final String QUEUE_DELETE_MAC_FROM_RADIUS = "delete.mac.from.radius";
	public static final String QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS = "update.concurrency.from.radius";

	public  static  final String QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS = "radius.send.enddate.cms.queue";
	public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS = "queue.customers.update.data.share.radius";
	public static final String QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS = "queue.customers.update.reserved.quota.radius";

	public static final String QUEUE_CUSTOMERS_CREATE_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.create.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_CREATE_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.create.data.share.NetConf.microservice";
	public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.update.data.share.NetConf.microservice";
	public static final String QUEUE_CUSTOMERS_DELETE_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.delete.data.share.netconf.microservice";
	public static final String QUEUE_CUSTOMERS_UPDATECUSTOMERSTATUS_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.updateCustomerStatus.data.share.netconf.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTUPDATE_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.defoultupdate.data.share.netconf.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTDEPROVISION_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.defoultdeprovision.data.share.netconf.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTPROVISION_DATA_SHARE_NETCONF_MICROSERVICE = "queue.customers.defoultprovision.data.share.netconf.microservice";
    public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.update.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_UPDATECUSTOMERSTATUS_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.updateCustomerStatus.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_DELETE_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.delete.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTPROVISION_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.defoultprovision.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTUPDATE_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.defoultupdate.data.share.radius.microservice";
	public static final String QUEUE_CUSTOMERS_DEFOULTDEPROVISION_DATA_SHARE_RADIUS_MICROSERVICE = "queue.customers.defoultdeprovision.data.share.radius.microservice";

	public static final String QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE = "queue.send.customer.status.message";


	public static final String QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE = "queue.send.customer.ip.to.update.radius.message";
	public static final String QUEUE_SEND_CUSTOMER_IP_TO_SAVE_RADIUS_MESSAGE = "queue.send.customer.ip.to.save.radius.message";

	public static final String QUEUE_SEND_CUSTOMER_IP_TO_DELETE_RADIUS_MESSAGE = "queue.send.customer.ip.to.delete.radius.message";

	public  static  final String QUEUE_SEND_SYSTEM_CONFIGURATION_COMMON = "radius.send.system.configuration.queue";
	public static final String QUEUE_SEND_CUSTOMER_STATUS_INACTIVE_DUNNING_MESSAGE = "queue.send.customer.status.inactive.message";
	public static final String QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS_ISP = "queue.create.mvno.common.apigw.to.radius.isp";


	public static final String QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_RADIUS = "queue.create.mvno.common.apigw.to.radius";

	public static final String QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS = "queue.update.mvno.common.apigw.to.radius";

	public static final String QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS = "send.cust.plan.detail.radius.queue";

	public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_RADIUS = "apigw.customer.status.radius.queue";

	public static final String QUEUE_SEND_ZERO_CUSTQUOTA_DATA_TO_CMS = "queue.send.zero.custquota.data.to.cms";

	public static final String QUEUE_CPR_UPDATE_FROM_REVENUE_RADIUS = "revenue.cpr.enddate.update.radius";

}
