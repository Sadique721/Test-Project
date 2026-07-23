/*
package com.savbill.partnermanagement.rabbitmq;

import com.savbill.partnermanagement.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration 
{
	@Bean
	Queue deadLetterQueue() {
		return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
	}

	@Bean
	DirectExchange deadLetterExchange() {
		return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
	}

	@Bean
	Binding DLQbinding() {
		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
	}

	@Bean
	public DirectExchange  savbillExchange() {
		return new DirectExchange (RabbitMqConstants.SAVBILL_EXCHANGE);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	*/
/**
	 * For testing purpose only
	 * @return
	 *//*

	@Bean
	public Queue createTestReceiveQueue() {
		return QueueBuilder.durable(RabbitMqConstants.TEST_RECEIVE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding createTestReceiveBinding() {
		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue createTestSendQueue() {
		return QueueBuilder.durable(RabbitMqConstants.TEST_SEND).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding createTestSendBinding() {
		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
	}

	//	Country Create
	@Bean
	public Queue sendCountrySharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCountrySharedDataBinding() {
		return BindingBuilder.bind(sendCountrySharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	// State Create
	@Bean
	public Queue sendStateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateSharedDataBinding() {
		return BindingBuilder.bind(sendStateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	City Create
	@Bean
	public Queue sendCitySharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCitySharedDataBinding() {
		return BindingBuilder.bind(sendCitySharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	MVNO Create
	@Bean
	public Queue sendMVNOSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNOSharedDataBinding() {
		return BindingBuilder.bind(sendMVNOSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	Role Create
	@Bean
	public Queue sendRoleSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendRoleSharedDataBinding() {
		return BindingBuilder.bind(sendRoleSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	Staff User Create
	@Bean
	public Queue sendStaffUserSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStaffUserSharedDataBinding() {
		return BindingBuilder.bind(sendStaffUserSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	MVNO Update and Delete
	@Bean
	public Queue sendMVNOUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNOUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendMVNOUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	Role Update and Delete
	@Bean
	public Queue sendRoleUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendRoleUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendRoleUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	// Staff User Update and Delete
	@Bean
	public Queue sendStaffUserUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStaffUserUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendStaffUserUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	Country Update and Delete
	@Bean
	public Queue sendCountryUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCountryUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//	City Update and Delete
	@Bean
	public Queue sendCityUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCityUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCityUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	// State Update and Delete
	@Bean
	public Queue sendStateUpdatingSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateUpdatingSharedDataBinding() {
		return BindingBuilder.bind(sendStateUpdatingSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Pincode APIGW to Inventory Microservice
	@Bean
	public Queue sendPincodeSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeSaveSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Pincode APIGW to Inventory Microservice
	@Bean
	public Queue sendPincodeUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//    Create Area APIGW to Inventory Microservice
	@Bean
	public Queue sendAreaSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaSaveSharedDataBinding() {
		return BindingBuilder.bind(sendAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Area APIGW to Inventory Microservice
	@Bean
	public Queue sendAreaUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//    Create Service Area APIGW to Inventory Microservice

	@Bean
	public Queue sendServiceAreaSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaSaveSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Service Area APIGW to Inventory Microservice
	@Bean
	public Queue sendServiceAreaUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Business Unit APIGW to Inventory Microservice

	@Bean
	public Queue sendBusinessUnitSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessUnitSaveSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Business Unit APIGW to Inventory Microservice
	@Bean
	public Queue sendBusinessUnitUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessUnitUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Branch APIGW to Inventory Microservice
	@Bean
	public Queue sendBranchSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchSaveSharedDataBinding() {
		return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Branch APIGW to Inventory Microservice
	@Bean
	public Queue sendBranchUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//    Create Services APIGW to Inventory Microservice
	@Bean
	public Queue sendServiceSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceSaveSharedDataBinding() {
		return BindingBuilder.bind(sendServiceSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Services APIGW to Inventory Microservice
	@Bean
	public Queue sendServiceUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendServiceUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Partner APIGW to Inventory Microservice
	@Bean
	public Queue sendPartnerSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerSaveSharedDataBinding() {
		return BindingBuilder.bind(sendPartnerSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Partner APIGW to Inventory Microservice
	@Bean
	public Queue sendPartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerUpdateSharedDataBinding() {
		return BindingBuilder.bind(sendPartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Tax APIGW to Inventory Microservice
	@Bean
	public Queue sendTaxSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTaxSaveSharedDataBinding() {
		return BindingBuilder.bind(sendTaxSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Tax APIGW to Inventory Microservice
	@Bean
	public Queue sendTaxUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTaxUpdateSharedDataBinding() {
		return BindingBuilder.bind(sendTaxUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Plan APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanSaveSharedDataBinding() {
		return BindingBuilder.bind(sendPlanSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Plan APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanUpdateSharedDataBinding() {
		return BindingBuilder.bind(sendPlanUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Plan Group APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanGroupSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupSaveSharedDataBinding() {
		return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Plan Group APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanGroupUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupUpdateSharedDataBinding() {
		return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Charge APIGW to Inventory Microservice
	@Bean
	public Queue sendChargeSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChargeSaveSharedDataBinding() {
		return BindingBuilder.bind(sendChargeSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Charge APIGW to Inventory Microservice
	@Bean
	public Queue sendChargeUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChargeUpdateSharedDataBinding() {
		return BindingBuilder.bind(sendChargeUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Team APIGW to Inventory Microservice
	@Bean
	public Queue sendTeamsSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTeamsSaveSharedDataBinding() {
		return BindingBuilder.bind(sendTeamsSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Team APIGW to Inventory Microservice
	@Bean
	public Queue sendTeamsUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTeamsUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Hierarchy APIGW to Inventory Microservice
	@Bean
	public Queue sendHierarchySaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendHierarchySaveSharedDataBinding() {
		return BindingBuilder.bind(sendHierarchySaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Hierarchy APIGW to Inventory Microservice
	@Bean
	public Queue sendHierarchyUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendHierarchyUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Create Customers APIGW to Inventory Microservice
	@Bean
	public Queue sendCustomersSaveSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersSaveSharedDataBinding() {
		return BindingBuilder.bind(sendCustomersSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Customers APIGW to Inventory Microservice
	@Bean
	public Queue sendCustomersUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCountryPartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCountryUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendCountryPartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendSateCreatePartnerSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateCreatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendSateCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendStateUpdatePartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendStateUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCityCreatePartnerSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCityCreatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendCityCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendCityUpdatePartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCityUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendCityUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPincodeCreatePartnerSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeCreatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendPincodeUpdatePartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendAreaCreatePartnerSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaCreatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendAreaCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendAreaUpdatePartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendAreaUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendServiceAreaCreatePartnerSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaCreatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendServiceAreaUpdatePartnerUpdateSharedDataQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaUpdatePartnerSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendServiceCreateForPartnerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendServiceCreateForPartnerQueueBindings() {
		return BindingBuilder.bind(sendServiceCreateForPartnerQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendServiceUpdateForPartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendserviceUpdateForPartnerBindinf() {
		return BindingBuilder.bind(sendServiceUpdateForPartner()).to(savbillExchange()).withQueueName();
	}
	//Tax Create To partner

	@Bean
	public Queue sendTAXCreateForPartnerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTAXCreateForPartnerQueueQueueBindings() {
		return BindingBuilder.bind(sendTAXCreateForPartnerQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendTAXUpdateForPartnerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTAXUpdateForPartnerQueueBindinf() {
		return BindingBuilder.bind(sendTAXUpdateForPartnerQueue()).to(savbillExchange()).withQueueName();
	}

	//charge
	@Bean
	public Queue sendChargeSaveSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChargeSaveSharedDataBindingPartner() {
		return BindingBuilder.bind(sendChargeSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//    Update Charge APIGW to Inventory Microservice
	@Bean
	public Queue sendChargeUpdateSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChargeUpdateSharedDataBindingPartner() {
		return BindingBuilder.bind(sendChargeUpdateSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//postpaid plan
	@Bean
	public Queue sendPlanSaveSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanSaveSharedDataBindingPartner() {
		return BindingBuilder.bind(sendPlanSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//    Update Plan APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanUpdateSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanUpdateSharedDataBindingPartner() {
		return BindingBuilder.bind(sendPlanUpdateSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//plan group
	//    Create Plan Group APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanGroupSaveSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupSaveSharedDataBindingPartner() {
		return BindingBuilder.bind(sendPlanGroupSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//    Update Plan Group APIGW to Inventory Microservice
	@Bean
	public Queue sendPlanGroupUpdateSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanGroupUpdateSharedDataBindingPartner() {
		return BindingBuilder.bind(sendPlanGroupUpdateSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//pricebook
	@Bean
	public Queue sendPartnerPlanBundleCreatePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRICEBOOK_CREATE_DATA_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerPlanBundleCreatePartnerBindings() {
		return BindingBuilder.bind(sendPartnerPlanBundleCreatePartner()).to(savbillExchange()).withQueueName();
	}

	//pricebook update
	@Bean
	public Queue sendPartnerPlanBundlePartnerUpDate() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnerPlanBundleUpDatePartnerBindings() {
		return BindingBuilder.bind(sendPartnerPlanBundlePartnerUpDate()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendBranchSaveSharedDataPartnerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchSaveSharedDataPartnerBinding() {
		return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//    Update Branch APIGW to Inventory Microservice
	@Bean
	public Queue sendBranchUpdatedSharedDataPartnerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
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
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_REGION_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRegionSaveSharedDataBindingPartner(){
		return BindingBuilder.bind(sendRegionSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendRegionSharedDataQueuePatrtner(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRegionUpdateSharedDataBindingPartner(){
		return BindingBuilder.bind(sendRegionSharedDataQueuePatrtner()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendBusinessVerticalsSaveSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessVerticalsSaveSharedDataQueuePartnerBindings() {
		return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendBusinessVerticalsUpdatedSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessVerticalsUpdatedSharedDataQueuePartnerBinding() {
		return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerApprovePaymentPartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnersendPartnerApprovePaymentPartnerBindings() {
		return BindingBuilder.bind(sendPartnerApprovePaymentPartner()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendPartnerUpadtePaymentPartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendPartnersendPartnerUpdatePaymentPartnerBindings() {
		return BindingBuilder.bind(sendPartnerUpadtePaymentPartner()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPartnerSaveSharedDataCmsQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerSaveSharedDataCmsBinding() {
		return BindingBuilder.bind(sendPartnerSaveSharedDataCmsQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPartnerUpdateSharedDataCmsQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerUpdateSharedDataCmsBinding() {
		return BindingBuilder.bind(sendPartnerUpdateSharedDataCmsQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendInvestmentCodeSaveSharedDataQueuePartner(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendInvestmentCodeSaveSharedDataBindingPartner(){
		return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendInvestmentCodeSharedDataQueuePartner(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendInvestmentCodeUpdateSharedDataBindingPartner(){
		return BindingBuilder.bind(sendInvestmentCodeSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendBusinessVerticalsDataSaveSharedDataQueuePartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_DATA_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessVerticalsDataSaveSharedDataQueuePartnerBindings() {
		return BindingBuilder.bind(sendBusinessVerticalsDataSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
	}

	//System Configuration to Partner
	@Bean
	public Queue sendCreateSystemConfigurationPartnerQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreateSystemConfigurationPartnerBinding(){
		return BindingBuilder.bind(sendCreateSystemConfigurationPartnerQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendUpdateSystemConfigurationPartnerQueue(){
		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendUpdateSystemConfigurationPartnerBinding(){
		return BindingBuilder.bind(sendUpdateSystemConfigurationPartnerQueue()).to(savbillExchange()).withQueueName();
	}
    @Bean
    public Queue sendPartnerSaveSharedDataSalesCrmQueue() {
        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SALESCRM)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    public Binding sendPartnerSaveSharedDataSalesCrmBinding() {
        return BindingBuilder.bind(sendPartnerSaveSharedDataSalesCrmQueue()).to(savbillExchange()).withQueueName();
    }

    //    Update Partner APIGW to Inventory Microservice
    @Bean
    public Queue sendPartnerUpdateSharedDataSalesCrmQueue() {
        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    public Binding sendPartnerUpdateSharedDataSalesCrmBinding() {
        return BindingBuilder.bind(sendPartnerUpdateSharedDataSalesCrmQueue()).to(savbillExchange()).withQueueName();
    }


	@Bean
	public Queue sendRoleCreationDetailsToPartner(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleCreationDetailsToPartnerBind(){
		return BindingBuilder.bind(sendRoleCreationDetailsToPartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendRoleDeletionDetailsToPartner(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleDeletionDetailsToPartnerBind(){
		return BindingBuilder.bind(sendRoleDeletionDetailsToPartner()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendMVNOSharedDataQueuePartnerForISP() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_Partner_ISP)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNOSharedDataBindingPartnerISP() {
		return BindingBuilder.bind(sendMVNOSharedDataQueuePartnerForISP()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue sendCreateCustomer() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCreateCustomerBinding() {
		return BindingBuilder.bind(sendCreateCustomer()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendUpdateCustomer() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendUpdateCustomerBinding() {
		return BindingBuilder.bind(sendUpdateCustomer()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendSaveMVNO() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendSaveMVNOBinding() {
		return BindingBuilder.bind(sendSaveMVNO()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendUpdateMVNO() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendUpdateMVNOBinding() {
		return BindingBuilder.bind(sendUpdateMVNO()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendSaveStaffPartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_CREATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendSaveStaffPartnerBinding() {
		return BindingBuilder.bind(sendSaveStaffPartner()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue sendUpdateStaffPartner() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_PARTNER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendUpdateStaffPartnerBinding() {
		return BindingBuilder.bind(sendUpdateStaffPartner()).to(savbillExchange()).withQueueName();
	}
}
*/
