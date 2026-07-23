package com.savbill.revenuemanagement.rabbitmq;

public class RabbitMqConstants
{
	public static final String BU_ID = "buId";
	public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
	public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
	public static final String DEAD_LETTER_KEY = "deadLetterKey";
	public static final String SAVBILL_EXCHANGE = "savbill.exchange";
	public static final String QUEUE_BILLING_INVOICE = "billing.invoice";
	public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION="prepaid.invoice";
	public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION="postpaid.invoice";
	public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="postpaid.charge";
	public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="prepaid.charge";
	public static final String QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE="inventory.charge";
	public static final String QUEUE_PARTNER_INVOICE="partner.invoice";
	public static final String QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE = "apigw.inventory.send.product.to.revenue";
	public static final String QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE = "apigw.inventory.send.customer.inventory.to.revenue";
	public static final String QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE="credit_document_revenue";

	public static final String QUEUE_APIGW_SERVICE_START_STOP = "apigw.service.status.change";

	public static final String QUEUE_APPROVE_ORG_INVOICE_REVENUE = "queue.org.invoice.approve.revenue";
	public static final String QUEUE_UPDATE_VOID_INVOICE_STATUS = "queue.update.void.invoice.status";

	public static final String QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT = "queue.cpr.update.date.share.revenuemanagement";
	public static final String QUEUE_PRICEBOOK_CREATE_DATA_REVENUE = "queue.pricebook.create.data.share.revenue";

	public static final String QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE = "queue.create.systemconfiguration.common.apigw.to.revenue";
	public static final String QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE = "queue.update.systemconfiguration.common.apigw.to.revenue";
//	public static final String QUEUE_CREDIT_DOCUMENT_KPI="credit.document.kpi";

	public static final String QUEUE_SEND_CREATE_DATA_ROLE_REVENUE = "queue.send.create.data.role.to.revenue";
	public static final String QUEUE_SEND_DELETE_DATA_ROLE_REVENUE = "queue.send.delete.data.role.to.revenue";

	/**Recieve online payment from cms for adjustment started**/


	public static final String QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT = "queue.send.customer.online.payment.to.revenue";


	/**Recieve online payment from cms for adjustment ended**/

	public static final String QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE = "apigw.inventory.send.recordpayment.to.revenue";

	/**send email for invoice started**/
	public static final String QUEUE_SEND_INVOICE_TO_NOTIFICATION = "queue.revenue.send.invoice.to.notification";

	/**send email for invoice ended**/

	public static final String QUEUE_DBR_SERVICE_HOLD_RESUME="revenue.dbr.service.hold.resume";

	public static final String QUEUE_CHANGE_PLAN_STATUS_CMS = "queue_change_plan_status_cms";
	public static final String QUEUE_CREDIT_DEBIT_DOC_TO_CMS = "queue_credit_debit_doc_to_cms";

	public static final String QUEUE_SEND_POSTPAID_TRIAL_INVOICE_REVENUE_TO_CMS = "queue.send.postpaid.trail.invoice.revenue.to.cms";
	public static final String QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P = "queue.send.custplammap.revenue.to.cms";

	public static final String QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_REVENUE = "queue.create.teams.common.apigw.to.revenue";

	public static final String QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS = "queue_send_budpay_payment_success";
	public static final String QUEUE_SEND_BUD_PAYMENT_DETAILS_TO_REVENUE = "queue.bud.payment.detail.to.revenue";
	public static final String QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE = "queue.bud.payment.credit.to.revenue";
	public static final String QUEUE_SEND_MVNO_DISCOUNT_REVENUE = "queue.mvno.discount.revenue";

	/**receive Customer Budpay change plan message from apigw started**/

	public  static  final String QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE = "apigw.send.customer.budpay.cwsc.change.plan.revenue.queue";

	/**receive Customer Budpay change plan message from apigw ended**/
	public static final String QUEUE_STAFF_UPDATE_DATA_SHARE_REVENUE = "queue.staff.update.data.share.revenue";

	public static final String QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS = "revenue.plan.mapping.status.change";

	public static final String QUEUE_CPR_UPDATE_FROM_REVENUE_RADIUS = "revenue.cpr.enddate.update.radius";

	public static final String QUEUE_CPR_UPDATE_FROM_REVENUE_CMS = "revenue.cpr.enddate.update.cms";

	public static final String QUEUE_INVOICE_NUMBER_UPDATE_FROM_REVENUE_CMS = "revenue.invoice.number.update.cms";

	public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_REVENUE = "apigw.customer.status.revenue.queue";

}
