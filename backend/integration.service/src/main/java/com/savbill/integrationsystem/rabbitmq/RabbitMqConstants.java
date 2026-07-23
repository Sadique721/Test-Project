package com.savbill.integrationsystem.rabbitmq;

public class RabbitMqConstants
{
	public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
	public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
	public static final String DEAD_LETTER_KEY = "deadLetterKey";
	public static final String SAVBILL_EXCHANGE = "savbill.exchange";

	//For test
	public static final String TEST_RECEIVE = "savbill.test.receive";
	// for test
	public static final String TEST_SEND = "savbill.test.send";

	public static final String QUEUE_BILL_GEN_SEND_INTEGRATION_SYSTEM = "bss.apigw.integrationsytem.billgen";
	public static final String QUEUE_CHARGE_MGMTN_SUCCESS="charge_management";
	public static final String QUEUE_PLAN_SERVICE_SUCCESS="plan_service_management";
	public static final String QUEUE_TAX_MGMTN_SUCCESS="tax_management";
	public static final String QUEUE_CUSTOMERS_SUCCESS="customers_management";
	public static final String QUEUE_SERVICE_AREA_SUCCESS="service_area";
	public static final String QUEUE_BUSINESS_UNIT_SUCCESS="business_unit";

	public static final String QUEUE_CREDIT_DOCUMENT_SUCCESS="credit_document";
	public static final String QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS="credit_document";
	public static final String QUEUE_DEBIT_DOCUMENT_SUCCESS="debit_document";

	public static final String QUEUE_CUST_PLAN_MAPPING_UPDATE="cpr.debit.update";
	public static final String QUEUE_STAFF_MANAGEMENT_SUCCESS="Staff Management";
	public static final String QUEUE_BRANCH_SUCCESS = "branch_success";
	public static final String QUEUE_CUSTOMER_SUCCESS = "customer_success";

	public static final String QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN = "bss.apigw.integrationsytem.creditnotegen";
//	public static final String QUEUE_SEND_SERIAL_NUMBER = "bss.apigateway.send.serialnumber";
	public static final String QUEUE_CANCEL_REGENERATE_SUCCESS="cancel_regenerate";

	public static final String QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION = "apigw.customer.planmapping.integration" ;

	public static final String QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION = "apigw.customer.servicemapping.integration" ;

	public static final String QUEUE_SERVICE_FOR_INTEGRATION = "apigw.service.integration" ;

	public static final String QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION = "apigw.plan.integration";

	public static final String QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY = "apigw.customer.inventory" ;

	public static final String QUEUE_SERVICE_FOR_INVENTORY_ITEM = "apigw.inventory.item" ;

	public static final String QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION="apigw.approve.item.integration";
	public static final String QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION="apigw.approve.remove.item.request.integration";
	public static final String QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM = "apigw.ticketmessage.integrationsytem";

	public static final String QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET = "integration.create.selfcareticket";
	public static final String QUEUE_PRODUCT_FROM_RMS="apigw.product.from.rms.integration";
	public static final String QUEUE_PRODUCTCATEGORY_INTEGRATOIN = "apigw.productcategory.integration";
	public static final String QUEUE_WAREHOUSE_INTEGRATOIN = "apigw.warehouse.integration";
	public static final String QUEUE_INWARD_RMS_INTEGRATOIN = "apigw.inward.rms.integration";
	public static final String QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN = "apigw.serialized.item.from.rms.integration";
	public static final String QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN = "apigw.serialized.item.history.rms.integration";

	public static final String QUEUE_SEND_INWARD_TO_INTEGRATOIN = "apigw.send.inward.to.integration";

	public static final String QUEUE_CMS_CONFIGURATION_INTIGRATION = "queue.customer.configuration.intigration";

	public static final String QUEUE_SEND_UUID_DATA_TO_CMS = "queue.send.uuid.data.to.cms";

	public static final String QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST = "queue.send.nms.service.delete.request";

	public static final String QUEUE_SEND_PAYMENT_CONFIGURTION_TO_INTEGRATION = "queue.send.payment.configuration.to.integration";

	public static final String QUEUE_SEND_PAYMENT_AUDIT_TO_CMS = "queue.send.payment.audit.to.cms";

	public static final String QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION = "queue.send.payment.audit.to.integration";
}
