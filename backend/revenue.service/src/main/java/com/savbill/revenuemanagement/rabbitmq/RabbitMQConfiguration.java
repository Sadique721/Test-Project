/*
package com.savbill.revenuemanagement.rabbitmq;

import com.savbill.revenuemanagement.core.constants.SharedDataConstants;
import liquibase.pro.packaged.S;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration
{
	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	public Queue queue() {
		return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
	}

	@Bean
	public Binding binding() {
		return  BindingBuilder.bind(queue()).to(exchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
	}

	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(converter());
		return rabbitTemplate;
	}

	@Bean
	public DirectExchange savbillExchange() {
		return new DirectExchange(RabbitMqConstants.SAVBILL_EXCHANGE);
	}

	@Bean
	public Queue prepaidCustomerInvoiceCreation() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding prepaidCustomerInvoiceCreationBinding() {
		return BindingBuilder.bind(prepaidCustomerInvoiceCreation()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue postpaidCustomerInvoiceCreation() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding postpaidCustomerInvoiceCreationBinding() {
		return BindingBuilder.bind(postpaidCustomerInvoiceCreation()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue postpaidCustomerInvoiceDirectCharge() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding postpaidCustomerInvoiceDirectChargeBinding() {
		return BindingBuilder.bind(postpaidCustomerInvoiceDirectCharge()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue prepaidCustomerInvoiceDirectCharge() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding prepaidCustomerInvoiceDirectChargeBinding() {
		return BindingBuilder.bind(prepaidCustomerInvoiceDirectCharge()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue customerInvoiceInventoryCharge() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding customerInvoiceInventoryChargeBinding() {
		return BindingBuilder.bind(customerInvoiceInventoryCharge()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue partnerInvoiceCreation() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_INVOICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding partnerInvoiceCreationBinding() {
		return BindingBuilder.bind(partnerInvoiceCreation()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendProductManagementQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendProductManagementBindings() {
		return BindingBuilder.bind(sendProductManagementQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCustomerInventoryManagementQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendCustomerInventoryManagementBindings() {
		return BindingBuilder.bind(sendCustomerInventoryManagementQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTaxUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	public Binding sendTaxUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendTaxUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendChargeSaveDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendsendChargeSaveDataShareQueueBinding() {
		return BindingBuilder.bind(sendChargeSaveDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendChargeUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendsendChargeUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendChargeUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPlanSaveDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanSaveDataShareQueueBinding() {
		return BindingBuilder.bind(sendPlanSaveDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPlanUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendPlanUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendDiscountSaveDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendDiscountSaveDataShareQueueBinding() {
		return BindingBuilder.bind(sendDiscountSaveDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendDiscountUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendDiscountUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendDiscountUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPlanGroupSaveDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupSaveQueueSaveDataShareQueueBinding() {
		return BindingBuilder.bind(sendPlanGroupSaveDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPlanGroupUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendDiscountUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCustomersSaveDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersSaveQueueSaveDataShareQueueBinding() {
		return BindingBuilder.bind(sendCustomersSaveDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendCustomersUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendCustomersUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendCreditDocument() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCreditDocumentQueueBinding() {
		return BindingBuilder.bind(sendCreditDocument()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendCustomersDocument() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendsendCustomersDocumentQueueBinding() {
		return BindingBuilder.bind(sendCustomersDocument()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCustomersUpdateDocument() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendsendCustomersUpdatetQueueBinding() {
		return BindingBuilder.bind(sendCustomersDocument()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTaxCreateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTaxCreateDataShareQueueBinding() {
		return BindingBuilder.bind(sendTaxCreateDataShareQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Binding sendServiceCreateDataShareQueueBinding() {
		return BindingBuilder.bind(sendServiceCreateDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendServiceCreateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceUpdateDataShareQueueBinding() {
		return BindingBuilder.bind(sendServiceUpdateDataShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendServiceUpdateDataShareQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}


	@Bean
	public Queue sendChangePlanDataShareQueuerevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChangePlanDataShareQueuerevenueBinding() {
		return BindingBuilder.bind(sendChangePlanDataShareQueuerevenue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendDirectChargeDataToRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendDirectChargeDataToRevenueQueueBind() {
		return BindingBuilder.bind(sendDirectChargeDataToRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendServiceStatusQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SERVICE_START_STOP)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendServiceStatusBindings() {
		return BindingBuilder.bind(sendServiceStatusQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendDStaffUserSaveToRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendDStaffUserSaveToRevenueQueueBind() {
		return BindingBuilder.bind(sendDStaffUserSaveToRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendDCreditDoctoCMS() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendDCreditDoctoCMSBind() {
		return BindingBuilder.bind(sendDCreditDoctoCMS()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCafToCustomerQueuerevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCafToCustomerQueuerevenueBindinf() {
		return BindingBuilder.bind(sendCafToCustomerQueuerevenue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPriceBookDetailsQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Queue sendUpdatedVoidInvoice() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendsendUpdatedVoidInvoiceBindinf() {
		return BindingBuilder.bind(sendUpdatedVoidInvoice()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendApproveOrgInvoiceQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendApproveOrgInvoiceQueueBindings() {
		return BindingBuilder.bind(sendApproveOrgInvoiceQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Binding sendPriceBookQueueBindings() {
		return BindingBuilder.bind(sendPriceBookDetailsQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendApproveCPRDate() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendApproveCPRDateBindings() {
		return BindingBuilder.bind(sendApproveCPRDate()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPlanCreate() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPlanCreateBindings() {
		return BindingBuilder.bind(sendPlanCreate()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCreateRoleToRevenueQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreateRoleToRevenueBinding() {
		return BindingBuilder.bind(sendCreateRoleToRevenueQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendUpdateRoleToRevenueQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendUpdateRoleToRevenueBinding() {
		return BindingBuilder.bind(sendUpdateRoleToRevenueQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPartnerPlanBundleCPRDate() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerPlanBundleCPRDateBindings() {
		return BindingBuilder.bind(sendPartnerPlanBundleCPRDate()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendAreaSaveSharedDataQueueRevenue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendAreaSaveSharedDataBindingRevenue(){
		return BindingBuilder.bind(sendAreaSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerSaveSharedDataQueueRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerSaveSharedDataBindingRevenue() {
		return BindingBuilder.bind(sendPartnerSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPartnerUpdateSharedDataQueueRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerUpdateSharedDataBindingRevenue() {
		return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPartnerDeleteSharedDataQueueRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DELETE_PARTNER_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerDeleteSharedDataBindingRevenue() {
		return BindingBuilder.bind(sendPartnerDeleteSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerPlanBundlerevenuerUpDate() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerPlanBundleUpDateRevenueBindings() {
		return BindingBuilder.bind(sendPartnerPlanBundlerevenuerUpDate()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCustomerChangeStatus() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendCustomerChangeStatusBindings() {
		return BindingBuilder.bind(sendCustomerChangeStatus()).to(savbillExchange()).withQueueName();
	}

	//Partner Balance
	@Bean
	public Queue sendPartnerBalanceApi() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerBalanceApiBindings() {
		return BindingBuilder.bind(sendPartnerBalanceApi()).to(savbillExchange()).withQueueName();
	}

	//Shift Location
	@Bean
	public Queue sendPartnerShiftLocationPartner() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerShiftLocationPartnerBindings() {
		return BindingBuilder.bind(sendPartnerShiftLocationPartner()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendMVNOSharedDataQueueRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendMVNOSharedDataBindingRevenue() {
		return BindingBuilder.bind(sendMVNOSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendRoleUpdatedRevenueDataQueueSample() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDAT_MVNO_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleUpdatedRevenueDataBindingSample() {
		return BindingBuilder.bind(sendRoleUpdatedRevenueDataQueueSample()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendBranchSaveSharedDataPartnerQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchSaveSharedDataPartnerBinding() {
		return BindingBuilder.bind(sendBranchSaveSharedDataPartnerQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Branch APIGW to Inventory Microservice
	@Bean
	public Queue sendBranchUpdatedSharedDataPartnerQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchUpdatedSharedDataPartnerQueueBinding() {
		return BindingBuilder.bind(sendBranchUpdatedSharedDataPartnerQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendRegionSaveSharedDataQueuePartner(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRegionSaveSharedDataBindingRevenue(){
		return BindingBuilder.bind(sendRegionSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendRegionSharedDataQueuePatrtner(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRegionUpdateSharedDataBindingPartner(){
		return BindingBuilder.bind(sendRegionSharedDataQueuePatrtner()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerApprovePaymentRevenue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnersendPartnerApprovePaymentRevenueBindings() {
		return BindingBuilder.bind(sendPartnerApprovePaymentRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerBalancePartner() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerBalancePartnerBindings() {
		return BindingBuilder.bind(sendPartnerBalancePartner()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendServiceTerminationQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_TERMINATION_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceTerminationQueueBinding() {
		return BindingBuilder.bind(sendServiceTerminationQueue()).to(savbillExchange()).withQueueName();
	}

	//System Configuration to Revenue
	@Bean
	public Queue sendCreateSystemConfigurationRevenueQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreateSystemConfigurationRevenueBinding(){
		return BindingBuilder.bind(sendCreateSystemConfigurationRevenueQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendUpdateSystemConfigurationRevenueQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendUpdateSystemConfigurationRevenueBinding(){
		return BindingBuilder.bind(sendUpdateSystemConfigurationRevenueQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendBillToOrgRejectCustPackrel(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_REJECT_ORG_INVOICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendBillToOrgRejectCustPackrelBinding(){
		return BindingBuilder.bind(sendBillToOrgRejectCustPackrel()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendBranchCreate(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendBranchCreateBinding(){
		return BindingBuilder.bind(sendBranchCreate()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendRoleCreationDetailsToRevenue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleCreationDetailsToRevenueBind(){
		return BindingBuilder.bind(sendRoleCreationDetailsToRevenue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendRoleDeletionDetailsToRevenue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleDeletionDetailsToRevenueBind(){
		return BindingBuilder.bind(sendRoleCreationDetailsToRevenue()).to(savbillExchange()).withQueueName();
	}

	*/
/**customer online payment recieve to reveneue queue binding started**//*

	@Bean
	public Queue SendCustomerOnlinePaymentQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding  SendCustomerOnlinePaymentQueueBinding() {
		return BindingBuilder.bind(SendCustomerOnlinePaymentQueue()).to(savbillExchange()).withQueueName();
	}

	*/
/**customer online payment recieve to reveneue queue binding ended**//*


	@Bean
	public Queue sendRecordPaymentQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Binding sendRecordPaymentBindings() {
		return BindingBuilder.bind(sendRecordPaymentQueue()).to(savbillExchange()).withQueueName();
	}

	*/
/**customer invoice send to queue binding started**//*

	@Bean
	public Queue SendCustomerInvoiceNotificationQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding  SendCustomerInvoiceNotificationQueueBinding() {
		return BindingBuilder.bind(SendCustomerInvoiceNotificationQueue()).to(savbillExchange()).withQueueName();
	}

	*/
/**customer invoice send to queue binding ended**//*



	@Bean
	public Queue dbrHoldResume() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding dbrHoldResumeBinding() {
		return BindingBuilder.bind(dbrHoldResume()).to(savbillExchange()).withQueueName();
	}

	*/
/**
	 * Postpaid Trail Invoice Revenue to CMS
	 *//*

	@Bean
	public Queue postpiadTrailInvoiceRevenueToCMSQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_POSTPAID_TRIAL_INVOICE_REVENUE_TO_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding postpiadTrailInvoiceRevenueToCMSBinding() {
		return BindingBuilder.bind(postpiadTrailInvoiceRevenueToCMSQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue billingInvoiceQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BILLING_INVOICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding billingInvoiceQueueBinding() {
		return BindingBuilder.bind(billingInvoiceQueue()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue branchUpdateRevenueQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding branchUpdateRevenueBinding() {
		return BindingBuilder.bind(branchUpdateRevenueQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue inventoryProductFromRevenueQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding inventoryProductFromRevenueBinding() {
		return BindingBuilder.bind(inventoryProductFromRevenueQueue()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue inventoryCustomerQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding inventoryCustomerBinding() {
		return BindingBuilder.bind(inventoryCustomerQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue pincodeUpdateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding pincodeUpdateBinding() {
		return BindingBuilder.bind(pincodeUpdateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue serviceAreaCreateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICEAREA_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding serviceAreaCreateBinding() {
		return BindingBuilder.bind(serviceAreaCreateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue serviceAreaUpdateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICEAREA_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding serviceAreaUpdateBinding() {
		return BindingBuilder.bind(serviceAreaUpdateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue BUCreateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding BUCreateBinding() {
		return BindingBuilder.bind(BUCreateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue BUUpdateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding BUUpdateBinding() {
		return BindingBuilder.bind(BUUpdateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue cityUpdateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding cityUpdateBinding() {
		return BindingBuilder.bind(cityUpdateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue stateUpdateQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding stateUpdateBinding() {
		return BindingBuilder.bind(stateUpdateQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue saveCustomerDiscountQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_CMS_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding saveCustomerDiscountBinding() {
		return BindingBuilder.bind(saveCustomerDiscountQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendSaveVoucherBatchQueue() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SAVE_VOUCHER_BATCH_DATA_SHARE_TO_REVENUEMANAGEMENT).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendSaveVoucherBatchQueueBinding() {
		return BindingBuilder.bind(sendSaveVoucherBatchQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendupdatedCustPlanMappingQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendupdatedCustPlanMappingQueueBinding() {
		return BindingBuilder.bind(sendupdatedCustPlanMappingQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendCreateTeamsCommonApiGwRevenueQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreateTeamsCommonApiGwRevenueBinding(){
		return BindingBuilder.bind(sendCreateTeamsCommonApiGwRevenueQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendUpdateTeamsCommonApiGwRevenueQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendUpdateTeamsCommonApiGwRevenueBinding(){
		return BindingBuilder.bind(sendUpdateTeamsCommonApiGwRevenueQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCreditDocIdsToCMSQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreditDocIdsToCMSQueueBinding(){
		return BindingBuilder.bind(sendCreditDocIdsToCMSQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCreditDocToCMSQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreditDocToCMSQueueBinding(){
		return BindingBuilder.bind(sendCreditDocToCMSQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCreditDebitDocToCMSQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreditDebitDocToCMSQueueBinding(){
		return BindingBuilder.bind(sendCreditDebitDocToCMSQueue()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue sendBudPayPaymentStatusQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendBudPayPaymentStatusQueueBindings() {
		return BindingBuilder.bind(sendBudPayPaymentStatusQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendBudPayPaymentcreditQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendBudPayPaymentcreditQueueBindings() {
		return BindingBuilder.bind(sendBudPayPaymentcreditQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendMVNOSharedDataQueueRevenueForISP() {
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE_ISP)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNOSharedDataBindingRevenueISP() {
		return BindingBuilder.bind(sendMVNOSharedDataQueueRevenueForISP()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendMVNODiscountQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNODiscountQueueBinding() {
		return BindingBuilder.bind(sendMVNODiscountQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendBudPayChangePlanMessageToRevenue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBudPayChangePlanMessageToRevenueBinding() {
		return BindingBuilder.bind(sendBudPayChangePlanMessageToRevenue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendDStaffUserUpdateToRevenue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_REVENUE
				)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendDStaffUserUpdateToRevenueQueueBind() {
		return BindingBuilder.bind(sendDStaffUserUpdateToRevenue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendCustMappingStatusUpdateToCMS() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustMappingStatusUpdateToCMSQueueBind() {
		return BindingBuilder.bind(sendCustMappingStatusUpdateToCMS()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendDirectChargeListQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_List_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendDirectChargeListQueueBinding(){
		return BindingBuilder.bind(sendDirectChargeListQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendCprUpdateToCmsListQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CPR_UPDATE_FROM_REVENUE_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCprUpdateToCmsListQueueQueueBinding(){
		return BindingBuilder.bind(sendCprUpdateToCmsListQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendCprUpdateToRadiusListQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CPR_UPDATE_FROM_REVENUE_RADIUS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCprUpdateToRadiusListQueueQueueBinding(){
		return BindingBuilder.bind(sendCprUpdateToRadiusListQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendInvoiceNumberUpdateToCMSListQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVOICE_NUMBER_UPDATE_FROM_REVENUE_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendInvoiceNumberUpdateToCMSListQueueBinding(){
		return BindingBuilder.bind(sendInvoiceNumberUpdateToCMSListQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendCustomerStatusToRevenue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCustomerStatusToRevenueBinding() {
		return BindingBuilder.bind(sendCustomerStatusToRevenue()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue sendDirectChargeListQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_List_SHARE_REVENUE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendDirectChargeListQueueBinding(){
		return BindingBuilder.bind(sendDirectChargeListQueue()).to(savbillExchange()).withQueueName();
	}
}
*/
