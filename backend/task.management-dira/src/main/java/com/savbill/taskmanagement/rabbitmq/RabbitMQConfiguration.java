/*
package com.savbill.ticketmanagement.rabbitmq;


import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Constants.RabbitCallConstants;
import com.savbill.ticketmanagement.rabbitmq.rqconstants.RMQConstants;
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


	@Bean
	public Queue sendCountrySharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCountrySharedDataBinding() {
		return BindingBuilder.bind(sendCountrySharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendStateSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateSharedDataBinding() {
		return BindingBuilder.bind(sendStateSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCitySharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCitySharedDataBinding() {
		return BindingBuilder.bind(sendCitySharedDataQueue()).to(savbillExchange()).withQueueName();
	}



	@Bean
	public Queue sendCountryUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCountryUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCityUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCityUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCityUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendStateUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendStateUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendStateUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPincodeSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeSaveSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendPincodeUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPincodeUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendAreaSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaSaveSharedDataBinding() {
		return BindingBuilder.bind(sendAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendAreaUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendAreaUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}




	//ServiceArea

	@Bean
	public Queue sendServiceAreaSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaSaveSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendServiceAreaUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceAreaUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	*/
/* Business Unit *//*


	@Bean
	public Queue sendBusinessUnitSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessUnitSaveSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendBusinessUnitUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessUnitUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendBranchSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchSaveSharedDataBinding() {
		return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendBranchUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBranchUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//Teams

	@Bean
	public Queue sendTeamsSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTeamsSaveSharedDataBinding() {
		return BindingBuilder.bind(sendTeamsSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTeamsUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTeamsUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendTeamsUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//hierarchy

	@Bean
	public Queue sendHierarchySaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendHierarchySaveSharedDataBinding() {
		return BindingBuilder.bind(sendHierarchySaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendHierarchyUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendHierarchyUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendHierarchyUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//Ticket configuration for message

	@Bean
	public Queue bssAssignTicketToteam() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding bssAssignTicketSuccessBinding() {
		return BindingBuilder.bind(bssAssignTicketToteam()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTicketETRQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTicketETRBindings() {
		return BindingBuilder.bind(sendTicketETRQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTicketETRAuditQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTicketETRAuditBindings() {
		return BindingBuilder.bind(sendTicketETRAuditQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue troubleTicketFollowUpReminderForStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding troubleTicketFollowUpReminderForStaffBindings() {
		return BindingBuilder.bind(troubleTicketFollowUpReminderForStaffQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue troubleTicketFollowUpReminderForCustomerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding troubleTicketFollowUpReminderForCustomerBindings() {
		return BindingBuilder.bind(troubleTicketFollowUpReminderForCustomerQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue troubleTicketFollowUpOverDueForStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding troubleTicketFollowUpOverDueForStaffBindings() {
		return BindingBuilder.bind(troubleTicketFollowUpOverDueForStaffQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue troubleTicketFollowUpOverDueForParentStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding troubleTicktFollowUpOverDueForParentStaffBindings() {
		return BindingBuilder.bind(troubleTicketFollowUpOverDueForParentStaffQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue custTicketClose() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding custTicketCloseBinding() {
		return BindingBuilder.bind(custTicketClose()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTicketTATAuditQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTicketTATAuditBindings() {
		return BindingBuilder.bind(sendTicketTATAuditQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTicketTATMessageQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTicketTATMessageBindings() {
		return BindingBuilder.bind(sendTicketTATMessageQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue selfCareCreateTicketIntegrationInQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding selfCareCreateTicketIntegrationInQueueBinding() {
		return BindingBuilder.bind(selfCareCreateTicketIntegrationInQueue()).to(savbillExchange()).withQueueName();
	}


	@Bean
	public Queue sendChangeCustomerStatus() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChangeCustomerStatusBinding() {
		return BindingBuilder.bind(sendChangeCustomerStatus()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendFollowupRemarkMsgQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendFollowupRemarkMsgBindings() {
		return BindingBuilder.bind(sendFollowupRemarkMsgQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendProblemDomainChangeMsgQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendProblemDomainChangeMsgBindings() {
		return BindingBuilder.bind(sendProblemDomainChangeMsgQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue ticketCreationSuccess() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding ticketCreationSuccessBinding() {
		return BindingBuilder.bind(ticketCreationSuccess()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue ticketmessageIntegrationSendInQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding ticketmessageIntegrationSendInQueueBinding() {
		return BindingBuilder.bind(ticketmessageIntegrationSendInQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendTatParentToTeamQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding sendTatParentToTeamBindings() {
		return BindingBuilder.bind(sendTatParentToTeamQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendWorkflowActionAssignMessage() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendWorkflowActionAssignMessageBinding() {
		return BindingBuilder.bind(sendWorkflowActionAssignMessage()).to(savbillExchange()).withQueueName();
	}


	//Region

	@Bean
	public Queue sendRegionSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_REGION_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendRegionSaveSharedDataBinding() {
		return BindingBuilder.bind(sendRegionSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendRegionUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendRegionUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendRegionUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//Business Vertical

	@Bean
	public Queue sendBusinessVerticalsSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessVerticalsSaveSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendBusinessVerticalsUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendBusinessVerticalsUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	//Customers
	@Bean
	public Queue sendCustomersSaveSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersSaveSharedDataBinding() {
		return BindingBuilder.bind(sendCustomersSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendCustomersUpdatedSharedDataQueue() {
		return QueueBuilder.durable(RMQConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCustomersUpdatedSharedDataBinding() {
		return BindingBuilder.bind(sendCustomersUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
	}


	//save staff in ticket
	@Bean
	public Queue sendStaffUserSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_STAFF_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendStaffUserSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendStaffUserSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}


	//update staff in ticket
	@Bean
	public Queue sendStaffUserUpdateSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendStaffUserUpdateSharedDataBindingTicket() {
		return BindingBuilder.bind(sendStaffUserUpdateSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}





	//save role in ticket
	@Bean
	public Queue sendRoleSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_ROLE_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendRoleSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}


	//update role in ticket
	@Bean
	public Queue sendRoleUpdateSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleUpdateSharedDataBindingTicket() {
		return BindingBuilder.bind(sendRoleUpdateSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}




	//save mvno in ticket
	@Bean
	public Queue sendMvnoSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_MVNO_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendMvnoSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendMvnoSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}


	//update mvno in ticket
	@Bean
	public Queue sendMvnoUpdateSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendMvnoUpdateSharedDataBindingTicket() {
		return BindingBuilder.bind(sendMvnoUpdateSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}



	//    Create Services APIGW to Ticket Microservice
	@Bean
	public Queue sendServiceSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendServiceSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}

	//    Update Services APIGW to Ticket Microservice
	@Bean
	public Queue sendServiceUpdatedSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendServiceUpdatedSharedDataBindingTicket() {
		return BindingBuilder.bind(sendServiceUpdatedSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}


	//    Create Plan APIGW to Ticket Microservice
	@Bean
	public Queue sendPlanSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PLAN_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendPlanSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}

	//    Update Plan APIGW to Ticket Microservice
	@Bean
	public Queue sendPlanUpdateSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPlanUpdateSharedDataBindingTicket() {
		return BindingBuilder.bind(sendPlanUpdateSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}




	//    Create Partner APIGW to Ticket Microservice
	@Bean
	public Queue sendPartnerSaveSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerSaveSharedDataBindingTicket() {
		return BindingBuilder.bind(sendPartnerSaveSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}

	//    Update Partner APIGW to Ticket Microservice
	@Bean
	public Queue sendPartnerUpdateSharedDataQueueTicket() {
		return QueueBuilder.durable(RMQConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendPartnerUpdateSharedDataBindingTicket() {
		return BindingBuilder.bind(sendPartnerUpdateSharedDataQueueTicket()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendClientServDataSaveShareQueue() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendClientServDataSaveShareQueueBinding() {
		return BindingBuilder.bind(sendClientServDataSaveShareQueue()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendClientServDataUpdateShareQueue() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendClientServDataUpdateShareQueueBinding() {
		return BindingBuilder.bind(sendClientServDataUpdateShareQueue()).to(savbillExchange()).withQueueName();
	}





	//    Update Close Ticket Call from APIGW to Ticket Microservice
	@Bean
	public Queue sendTicketDataToAPIGw() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_SEND_TICKET_DATA_TO_APIGW)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendTicketDataToAPIGwBinding() {
		return BindingBuilder.bind(sendTicketDataToAPIGw()).to(savbillExchange()).withQueueName();
	}


	//    Update Close Ticket Call from APIGW to Ticket Microservice
	@Bean
	public Queue sendUpdatedTicketDataToAPIGw() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendUpdatedTicketDataToAPIGwBinding() {
		return BindingBuilder.bind(sendUpdatedTicketDataToAPIGw()).to(savbillExchange()).withQueueName();
	}


//	@Bean
//	public Queue sendClientServDataSaveShareQueue() {
//		return QueueBuilder.durable(RabbitCallConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendClientServDataSaveShareQueueBinding() {
//		return BindingBuilder.bind(sendClientServDataSaveShareQueue()).to(savbillExchange()).withQueueName();
//	}

//	@Bean
//	public Queue sendClientServDataUpdateShareQueue() {
//		return QueueBuilder.durable(RabbitCallConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendClientServDataUpdateShareQueueBinding() {
//		return BindingBuilder.bind(sendClientServDataUpdateShareQueue()).to(savbillExchange()).withQueueName();
//	}

	*/
/** Rabbitmq Binding for ticket followup remark to customer queue**//*

	@Bean
	public Queue CreateTicketFollowupRemarkCustomerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Binding CreateTicketFollowupRemarkCustomerQueueBinding() {
		return BindingBuilder.bind(CreateTicketFollowupRemarkCustomerQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq binding for ticket followup remark to customer ended**//*


	@Bean
	public Queue sendCafToCustomerTicketQueue() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendCafToCustomerTikcetBinding() {
		return BindingBuilder.bind(sendCafToCustomerTicketQueue()).to(savbillExchange()).withQueueName();
	}

	//System Configuration to Ticket
	@Bean
	public Queue sendCreateSystemConfigurationTicketQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendCreateSystemConfigurationTicketBinding(){
		return BindingBuilder.bind(sendCreateSystemConfigurationTicketQueue()).to(savbillExchange()).withQueueName();
	}

	@Bean
	public Queue sendUpdateSystemConfigurationTicketQueue(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendUpdateSystemConfigurationTicketBinding(){
		return BindingBuilder.bind(sendUpdateSystemConfigurationTicketQueue()).to(savbillExchange()).withQueueName();
	}

	*/
/**Email config recieve to apigw queue binding started**//*


	@Bean
	public Queue CreateEmailConfigSendQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_EMAIL_CONFIG_TO_APIGW).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Binding CreateEmailConfigSendQueueBinding() {
		return BindingBuilder.bind(CreateEmailConfigSendQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Email config recieve to apigw queue binding ended**//*




	@Bean
	public Queue sendRoleCreationDetailsToTicket(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleCreationDetailsToTicketBind(){
		return BindingBuilder.bind(sendRoleCreationDetailsToTicket()).to(savbillExchange()).withQueueName();
	}
	@Bean
	public Queue sendRoleDeletionDetailsToTicket(){
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}
	@Bean
	public Binding sendRoleDeletionDetailsToTicketBind(){
		return BindingBuilder.bind(sendRoleDeletionDetailsToTicket()).to(savbillExchange()).withQueueName();
	}

	*/
/** Rabbitmq Binding for ticket external remark to customer queue**//*

	@Bean
	public Queue CreateTicketExternalRemarkCustomerQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding CreateTicketExternalRemarkCustomerQueueBinding() {
		return BindingBuilder.bind(CreateTicketExternalRemarkCustomerQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq binding for ticket extrenal remark to customer ended**//*



	@Bean
	public Queue sendChangePlanDataShareQueueTicket() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_TICKET)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendChangePlanDataShareQueueTicketBinding() {
		return BindingBuilder.bind(sendChangePlanDataShareQueueTicket()).to(savbillExchange()).withQueueName();
	}

	*/
/** Rabbitmq Binding for ticket alert to staff queue**//*

	@Bean
	public Queue CreateTicketAlertStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding CreateTicketAlertStaffQueueBinding() {
		return BindingBuilder.bind(CreateTicketAlertStaffQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq binding for ticket alert to staff ended**//*


	*/
/** Rabbitmq Binding for Immediate Attention to Staff  for Unregistered queue started**//*

	@Bean
	public Queue CreateImmediateAttentionForUnRegisteredToStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding CreateImmediateAttentionForUnRegisteredToStaffQueueBinding() {
		return BindingBuilder.bind(CreateImmediateAttentionForUnRegisteredToStaffQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq Binding for Immediate Attention to  for registered queue ended**//*


	*/
/** Rabbitmq Binding for Unpick ticket alert to staff queue**//*

	@Bean
	public Queue CreateUnpickTicketAlertStaffQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}

	@Bean
	public Binding CreateUnpickTicketAlertStaffQueueBinding() {
		return BindingBuilder.bind(CreateUnpickTicketAlertStaffQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq binding for Unpick ticket alert to staff ended**//*


	*/
/** Rabbitmq Binding for Immediate Attention to Customer  for unregistered queue started**//*


	@Bean
	public Queue CreateImmediateAttentionForUnRegisteredQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Binding CreateImmediateAttentionForUnRegisteredQueueBinding() {
		return BindingBuilder.bind(CreateImmediateAttentionForUnRegisteredQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq Binding for Immediate Attention to Customer for unregistered queue ended**//*



	*/
/** Rabbitmq Binding for Immediate Attention to Customer  for registered queue started**//*

	@Bean
	public Queue CreateImmediateAttentionForRegisteredQueue() {
		return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
	}
	@Bean
	public Binding CreateImmediateAttentionForRegisteredQueueBinding() {
		return BindingBuilder.bind(CreateImmediateAttentionForRegisteredQueue()).to(savbillExchange()).withQueueName();
	}
	*/
/**Rabbitmq Binding for Immediate Attention to Customer for registered queue ended**//*


	@Bean
	public Queue sendMVNOSharedDataQueueTicketForISP() {
		return QueueBuilder.durable(RabbitCallConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_TICKET_ISP)
				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
				.build();
	}

	@Bean
	public Binding sendMVNOSharedDataBindingTicketISP() {
		return BindingBuilder.bind(sendMVNOSharedDataQueueTicketForISP()).to(savbillExchange()).withQueueName();
	}

}
*/
