//package com.savbill.inventorymanagement.rabbitmq;
//
//import org.apache.xmlbeans.impl.xb.xsdschema.Public;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfiguration
//{
////	@Bean
////	Queue deadLetterQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
////	}
////
////	@Bean
////	DirectExchange deadLetterExchange() {
////		return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
////	}
////
////	@Bean
////	Binding DLQbinding() {
////		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
////	}
////
////	@Bean
////	public DirectExchange  savbillExchange() {
////		return new DirectExchange (RabbitMqConstants.SAVBILL_EXCHANGE);
////	}
////
////	@Bean
////	public MessageConverter jsonMessageConverter() {
////		return new Jackson2JsonMessageConverter();
////	}
////
////	@Bean
////	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
////		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
////		rabbitTemplate.setMessageConverter(jsonMessageConverter());
////		return rabbitTemplate;
////	}
////
////	/**
////	 * For testing purpose only
////	 * @return
////	 */
////	@Bean
////	public Queue createTestReceiveQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.TEST_RECEIVE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding createTestReceiveBinding() {
////		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue createTestSendQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.TEST_SEND).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding createTestSendBinding() {
////		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	Country Create
////	@Bean
////	public Queue sendCountrySharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCountrySharedDataBinding() {
////		return BindingBuilder.bind(sendCountrySharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
////// State Create
////	@Bean
////	public Queue sendStateSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendStateSharedDataBinding() {
////		return BindingBuilder.bind(sendStateSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	City Create
////	@Bean
////	public Queue sendCitySharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCitySharedDataBinding() {
////		return BindingBuilder.bind(sendCitySharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	MVNO Create
////	@Bean
////	public Queue sendMVNOSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendMVNOSharedDataBinding() {
////		return BindingBuilder.bind(sendMVNOSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	Role Create
////	@Bean
////	public Queue sendRoleSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendRoleSharedDataBinding() {
////		return BindingBuilder.bind(sendRoleSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	Staff User Create
////	@Bean
////	public Queue sendStaffUserSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendStaffUserSharedDataBinding() {
////		return BindingBuilder.bind(sendStaffUserSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	MVNO Update and Delete
////	@Bean
////	public Queue sendMVNOUpdatedSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendMVNOUpdatedSharedDataBinding() {
////		return BindingBuilder.bind(sendMVNOUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	Role Update and Delete
////	@Bean
////	public Queue sendRoleUpdatedSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendRoleUpdatedSharedDataBinding() {
////		return BindingBuilder.bind(sendRoleUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
////// Staff User Update and Delete
////	@Bean
////	public Queue sendStaffUserUpdatedSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendStaffUserUpdatedSharedDataBinding() {
////		return BindingBuilder.bind(sendStaffUserUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	Country Update and Delete
////	@Bean
////	public Queue sendCountryUpdatedSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCountryUpdatedSharedDataBinding() {
////		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	City Update and Delete
////	@Bean
////	public Queue sendCityUpdatedSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCityUpdatedSharedDataBinding() {
////		return BindingBuilder.bind(sendCityUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
////// State Update and Delete
////	@Bean
////	public Queue sendStateUpdatingSharedDataQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendStateUpdatingSharedDataBinding() {
////		return BindingBuilder.bind(sendStateUpdatingSharedDataQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Pincode APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPincodeSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPincodeSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPincodeSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Pincode APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPincodeUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPincodeUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////
////	//    Create Area APIGW to Inventory Microservice
////	@Bean
////	public Queue sendAreaSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendAreaSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendAreaSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Area APIGW to Inventory Microservice
////	@Bean
////	public Queue sendAreaUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendAreaUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////
////	//    Create Service Area APIGW to Inventory Microservice
////
////	@Bean
////	public Queue sendServiceAreaSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendServiceAreaSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Service Area APIGW to Inventory Microservice
////	@Bean
////	public Queue sendServiceAreaUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendServiceAreaUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Business Unit APIGW to Inventory Microservice
////
////	@Bean
////	public Queue sendBusinessUnitSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendBusinessUnitSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Business Unit APIGW to Inventory Microservice
////	@Bean
////	public Queue sendBusinessUnitUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendBusinessUnitUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Branch APIGW to Inventory Microservice
////	@Bean
////	public Queue sendBranchSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendBranchSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendBranchSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Branch APIGW to Inventory Microservice
////	@Bean
////	public Queue sendBranchUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendBranchUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////
////	//    Create Services APIGW to Inventory Microservice
////	@Bean
////	public Queue sendServiceSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendServiceSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendServiceSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Services APIGW to Inventory Microservice
////	@Bean
////	public Queue sendServiceUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendServiceUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendServiceUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Partner APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPartnerSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPartnerSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPartnerSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Partner APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPartnerUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPartnerUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Tax APIGW to Inventory Microservice
////	@Bean
////	public Queue sendTaxSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendTaxSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendTaxSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Tax APIGW to Inventory Microservice
////	@Bean
////	public Queue sendTaxUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendTaxUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendTaxUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Plan APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPlanSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPlanSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPlanSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Plan APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPlanUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPlanUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPlanUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Plan Group APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPlanGroupSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPlanGroupSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Plan Group APIGW to Inventory Microservice
////	@Bean
////	public Queue sendPlanGroupUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendPlanGroupUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Charge APIGW to Inventory Microservice
////	@Bean
////	public Queue sendChargeSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendChargeSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendChargeSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Charge APIGW to Inventory Microservice
////	@Bean
////	public Queue sendChargeUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendChargeUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendChargeUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Team APIGW to Inventory Microservice
////	@Bean
////	public Queue sendTeamsSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendTeamsSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendTeamsSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Team APIGW to Inventory Microservice
////	@Bean
////	public Queue sendTeamsUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendTeamsUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Hierarchy APIGW to Inventory Microservice
////	@Bean
////	public Queue sendHierarchySaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendHierarchySaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendHierarchySaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Hierarchy APIGW to Inventory Microservice
////	@Bean
////	public Queue sendHierarchyUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendHierarchyUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Create Customers APIGW to Inventory Microservice
////	@Bean
////	public Queue sendCustomersSaveSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCustomersSaveSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendCustomersSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//    Update Customers APIGW to Inventory Microservice
////	@Bean
////	public Queue sendCustomersUpdatedSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCustomersUpdatedSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendPopManagementQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendPopManagementBindings() {
////		return BindingBuilder.bind(sendPopManagementQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendProductManagementQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendProductManagementBindings() {
////		return BindingBuilder.bind(sendProductManagementQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue productFromRms(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding productFromRmsBinding() {
////		return BindingBuilder.bind(productFromRms()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue productCategoryToIntegration(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding productCategoryToIntegrationBinding() {
////		return BindingBuilder.bind(productCategoryToIntegration()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue wareHouseToIntegration(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding wareHouseToIntegrationBinding() {
////		return BindingBuilder.bind(wareHouseToIntegration()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue inwardToIntegration(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding inwardToIntegrationBinding() {
////		return BindingBuilder.bind(inwardToIntegration()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue inwardSendToIntegration(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding inwardSendToIntegrationBinding() {
////		return BindingBuilder.bind(inwardSendToIntegration()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue CreateCustomerInventoryQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding CreateCustomerInventoryQueueBinding() {
////		return BindingBuilder.bind(CreateCustomerInventoryQueue()).to(savbillExchange()).withQueueName();
////	}
////
//////	@Bean
//////	public Queue CreateCustomerInventoryQueue() {
//////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//////	}
//////
//////	@Bean
//////	public Binding CreateCustomerInventoryQueueBinding() {
//////		return BindingBuilder.bind(CreateDunningDocumentExpiredQueueForCustomer()).to(savbillExchange()).withQueueName();
//////	}
////	@Bean
////	public Queue sendCustomerInventoryManagementQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendCustomerInventoryManagementBindings() {
////		return BindingBuilder.bind(sendCustomerInventoryManagementQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendInventoryCreateNewChargeQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryCreateNewChargeBindings() {
////		return BindingBuilder.bind(sendInventoryCreateNewChargeQueue()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendInventoryUpdateNewChargeQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryUpdateNewChargeBindings() {
////		return BindingBuilder.bind(sendInventoryUpdateNewChargeQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendInventoryCreateRefChargeQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryCreateRefChargeBindings() {
////		return BindingBuilder.bind(sendInventoryCreateRefChargeQueue()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendInventoryUpdateRefChargeQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryUpdateRefChargeBindings() {
////		return BindingBuilder.bind(sendInventoryUpdateRefChargeQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	// CAF to Customer To Inventory
////	@Bean
////	public Queue sendCafToCustomerInventoryQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendCafToCustomerInventoryBinding() {
////		return BindingBuilder.bind(sendCafToCustomerInventoryQueue()).to(savbillExchange()).withQueueName();
////	}
////	// CAS Master
////	@Bean
////	public Queue sendCasMasterCreateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CASMASTER_CREATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendCasMasterCreateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendCasMasterCreateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendCasMasterUpdateSharedDataQueueInventory() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendCasMasterUpdateSharedDataBindingInventory() {
////		return BindingBuilder.bind(sendCasMasterUpdateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
////	}
////
////	//System Configuration to Inventory
////	@Bean
////	public Queue sendCreateSystemConfigurationInventoryQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendCreateSystemConfigurationInventoryBinding(){
////		return BindingBuilder.bind(sendCreateSystemConfigurationInventoryQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendUpdateSystemConfigurationInventoryQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendUpdateSystemConfigurationInventoryBinding(){
////		return BindingBuilder.bind(sendUpdateSystemConfigurationInventoryQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendRecordPaymentQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////	@Bean
////	public Binding sendRecordPaymentBindings() {
////		return BindingBuilder.bind(sendRecordPaymentQueue()).to(savbillExchange()).withQueueName();
////	}
////
////
////
////	@Bean
////	public Queue sendRoleCreationDetailsToInventory(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendRoleCreationDetailsToInventoryBind(){
////		return BindingBuilder.bind(sendRoleCreationDetailsToInventory()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendRoleDeletionDetailsToInventory(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendRoleDeletionDetailsToInventoryBind(){
////		return BindingBuilder.bind(sendRoleDeletionDetailsToInventory()).to(savbillExchange()).withQueueName();
////	}
////	//Warehouse Team Mapping Inventory to CommonAPI
////	@Bean
////	public Queue sendCreateWarehouseTeamMappingCommonAPIQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONAPIGW)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendCreateWarehouseTeamMappingCommonAPIBinding(){
////		return BindingBuilder.bind(sendCreateWarehouseTeamMappingCommonAPIQueue()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendUpadteWarehouseTeamMappingCommonAPIQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONAPIGW)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Binding sendUpadteWarehouseTeamMappingCommonAPIBinding(){
////		return BindingBuilder.bind(sendUpadteWarehouseTeamMappingCommonAPIQueue()).to(savbillExchange()).withQueueName();
////	}
//////   Plan or Plan Group Inventory Item Serial Number Send Inventory To CMS
////	@Bean
////	public Queue sendItemSerialNumberInventoryToCMSQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendItemSerialNumberInventoryToCMSBindings() {
////		return BindingBuilder.bind(sendItemSerialNumberInventoryToCMSQueue()).to(savbillExchange()).withQueueName();
////	}
////
////
////	@Bean
////	public Queue sendInventoryInwardApprovalQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryInwardApprovalQueueBindings() {
////		return BindingBuilder.bind(sendInventoryInwardApprovalQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendCustInvParamsToCMSQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendCustInvParamsToCMSBindings() {
////		return BindingBuilder.bind(sendCustInvParamsToCMSQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Binding sendStatusUpdateToInventoryQueueBinding(){
////		return BindingBuilder.bind(sendStatusUpdateToInventoryQueue()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue sendStatusUpdateToInventoryQueue(){
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////	@Bean
////	public Queue sendMVNOSharedDataQueueInventoryForISP() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_INVENTORY_ISP)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendMVNOSharedDataBindingInventoryISP() {
////		return BindingBuilder.bind(sendMVNOSharedDataQueueInventoryForISP()).to(savbillExchange()).withQueueName();
////	}
////
////
////	@Bean
////	public Queue sendInventoryWarrantyRemainderToStaffQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_WARRANTY_REMAINDER_MESSAGE_TO_STAFF_TO_NOTIFICATION)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding sendInventoryWarrantyRemainderToStaffQueueBindings() {
////		return BindingBuilder.bind(sendInventoryWarrantyRemainderToStaffQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue AddMacFromQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding AddMacFromBinding() {
////		return BindingBuilder.bind(AddMacFromQueue()).to(savbillExchange()).withQueueName();
////	}
////
////
////	@Bean
////	public Queue AddMacFromAPIGTQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////
////	@Bean
////	public Binding AddMacFromAPIGTBinding() {
////		return BindingBuilder.bind(AddMacFromAPIGTQueue()).to(savbillExchange()).withQueueName();
////	}
////	@Bean
////	public Queue AddUsedPortNotificationQueue() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////	}
////	@Bean
////	public Binding AddUsedPortNotificationBinding() {
////		return BindingBuilder.bind(AddUsedPortNotificationQueue()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendVendorSavedDataToCMS() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendVendorSavedDataToCMSBinding() {
////		return BindingBuilder.bind(sendVendorSavedDataToCMS()).to(savbillExchange()).withQueueName();
////	}
////
////	@Bean
////	public Queue sendVendorUpdatedDataToCMS() {
////		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE)
////				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////				.build();
////	}
////
////	@Bean
////	public Binding sendVendorUpdatedDataToCMSBinding() {
////		return BindingBuilder.bind(sendVendorUpdatedDataToCMS()).to(savbillExchange()).withQueueName();
////	}
//}
