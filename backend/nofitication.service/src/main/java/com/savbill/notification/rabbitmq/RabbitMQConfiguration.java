//package com.savbill.notification.rabbitmq;
//
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.QueueBuilder;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
//@Profile("rabbitmq")
//public class RabbitMQConfiguration {
//
//    @Bean
//    Queue deadLetterQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
//    }
//
//    @Bean
//    DirectExchange deadLetterExchange() {
//        return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
//    }
//
//    @Bean
//    Binding DLQbinding() {
//        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
//    }
//
//    @Bean
//    public DirectExchange savbillExchange() {
//        return new DirectExchange(RabbitMqConstants.SAVBILL_EXCHANGE);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Profile("rabbitmq")
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public Queue roleSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_ROLE_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding roleSuccessBinding() {
//        return BindingBuilder.bind(roleSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue loginSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LOGIN_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding loginSuccessBinding() {
//        return BindingBuilder.bind(loginSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffSuccessBinding() {
//        return BindingBuilder.bind(staffSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue loginFailureQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LOGIN_FAILURE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding loginFailureBinding() {
//        return BindingBuilder.bind(loginFailureQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerRegistrationSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerRegistrationSuccessBinding() {
//        return BindingBuilder.bind(customerRegistrationSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerRegistrationFailureQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_REGISTRATION_FAILURE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerRegistrationFailureBinding() {
//        return BindingBuilder.bind(customerRegistrationFailureQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue optGenerationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_GENERATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding optGenerationBinding() {
//        return BindingBuilder.bind(optGenerationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue voucherCodeQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_VOUCHERCODE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding voucherCodeBinding() {
//        return BindingBuilder.bind(voucherCodeQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue UsedQuotaQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_USED_QUOTA).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding UsedQuotaBinding() {
//        return BindingBuilder.bind(UsedQuotaQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue systemConfigCreateCommonQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREATE_SYSTEM_CONFIG_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding systemConfigCreateCommonBinding() {
//        return BindingBuilder.bind(systemConfigCreateCommonQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue systemConfigUpdateCommonQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UPDATE_SYSTEM_CONFIG_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding systemConfigUpdateCommonBinding() {
//        return BindingBuilder.bind(systemConfigUpdateCommonQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue bssAssignTicketToteam() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Queue bssCustomerApprovalSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue bssCustomerApprovalFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRegistrationSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRegistrationFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRenewalSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRenewalFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//
//    @Bean
//    public Queue bssCustomerRechargeSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_RECHARGE_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerRechargeFailQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_RECHARGE_FAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPlanExpireQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PLAN_EXPIRE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPaymentLinkQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue bssCustomerPaymentSuccessQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Queue bssCustomerDunning() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue bssCustomerDeactivation() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Queue bssCustomerOtpRegistration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Queue staffExpiredDocumentQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding bssCustomerApprovalSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerApprovalSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerApprovalFailBinding() {
//        return BindingBuilder.bind(bssCustomerApprovalFailQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRegistrationSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRegistrationSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRegistrationFaiBinding() {
//        return BindingBuilder.bind(bssCustomerRegistrationFailQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRenewalSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRenewalSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRenewalFailBinding() {
//        return BindingBuilder.bind(bssCustomerRenewalFailQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Binding bssCustomerRechargeSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerRechargeSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerRechargeFailBinding() {
//        return BindingBuilder.bind(bssCustomerRechargeFailQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPlanExpireBinding() {
//        return BindingBuilder.bind(bssCustomerPlanExpireQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPaymentLinkBinding() {
//        return BindingBuilder.bind(bssCustomerPaymentLinkQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerPaymentSuccessBinding() {
//        return BindingBuilder.bind(bssCustomerPaymentSuccessQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssAssignTicketSuccessBinding() {
//        return BindingBuilder.bind(bssAssignTicketToteam()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding bssCustomerDunningBinding() {
//        return BindingBuilder.bind(bssCustomerDunning()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Binding bssCustomerOtpRegistrationBinding() {
//        return BindingBuilder.bind(bssCustomerOtpRegistration()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Binding bssCustomerDeactivations() {
//        return BindingBuilder.bind(bssCustomerDeactivation()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding staffExpireddocumentQueue() {
//        return BindingBuilder.bind(staffExpiredDocumentQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpReminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpReminderForStaffBindings() {
//        return BindingBuilder.bind(leadFollowUpReminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpReminderForCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpReminderForCustomerBindings() {
//        return BindingBuilder.bind(leadFollowUpReminderForCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpOverDueForStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpOverDueForStaffBindings() {
//    	return BindingBuilder.bind(leadFollowUpOverDueForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpOverDueForParentStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpOverDueForParentStaffBindings() {
//    	return BindingBuilder.bind(leadFollowUpOverDueForParentStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffStatusQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_SEND_STATUS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffQueue() {
//        return BindingBuilder.bind(staffStatusQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue noFollowUpreminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding noFollowUpreminderForStaffBindings() {
//        return BindingBuilder.bind(noFollowUpreminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue noFollowUpreminderForParentStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding noFollowUpreminderForParentStaffBindings() {
//    	return BindingBuilder.bind(noFollowUpreminderForParentStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue noFollowUpActionReminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding noFollowUpActionpReminderForStaffBindings() {
//        return BindingBuilder.bind(noFollowUpActionReminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue noFollowUpActionReminderForParentStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding noFollowUpActionpReminderForParentStaffBindings() {
//    	return BindingBuilder.bind(noFollowUpActionReminderForParentStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendWorkflowActionAssignMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendWorkflowActionAssignMessageBinding() {
//        return BindingBuilder.bind(sendWorkflowActionAssignMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendChangeCustomerStatus() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangeCustomerStatusBinding() {
//        return BindingBuilder.bind(sendChangeCustomerStatus()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTatParentToTeamQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTatParentToTeamBindings() {
//        return BindingBuilder.bind(sendTatParentToTeamQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendFollowupRemarkMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendFollowupRemarkMsgBindings() {
//        return BindingBuilder.bind(sendFollowupRemarkMsgQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendProblemDomainChangeMsgQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendProblemDomainChangeMsgBindings() {
//        return BindingBuilder.bind(sendProblemDomainChangeMsgQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketETRQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketETRBindings() {
//        return BindingBuilder.bind(sendTicketETRQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTicketETRAuditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketETRAuditBindings() {
//        return BindingBuilder.bind(sendTicketETRAuditQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpReminderForStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpReminderForStaffBindings() {
//    	return BindingBuilder.bind(customerFollowUpReminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpReminderForCustomerQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpReminderForCustomerBindings() {
//    	return BindingBuilder.bind(customerFollowUpReminderForCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpOverDueForStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpOverDueForStaffBindings() {
//    	return BindingBuilder.bind(customerFollowUpOverDueForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerFollowUpOverDueForParentStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerFollowUpOverDueForParentStaffBindings() {
//    	return BindingBuilder.bind(customerFollowUpOverDueForParentStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//
//
//    @Bean
//    public Queue troubleTicketFollowUpReminderForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpReminderForStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpReminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpReminderForCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpReminderForCustomerBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpReminderForCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpOverDueForStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicketFollowUpOverDueForStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpOverDueForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue troubleTicketFollowUpOverDueForParentStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding troubleTicktFollowUpOverDueForParentStaffBindings() {
//        return BindingBuilder.bind(troubleTicketFollowUpOverDueForParentStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue ticketCreationSuccess() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketCreationSuccessBinding() {
//        return BindingBuilder.bind(ticketCreationSuccess()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue  ticketRescheduleforStaff() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding ticketRescheduleforStaffBinding() {
//        return BindingBuilder.bind(ticketRescheduleforStaff()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue  ticketTatBreachedReminder() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketTatBreachedReminderBinding() {
//        return BindingBuilder.bind(ticketTatBreachedReminder()).to(savbillExchange()).withQueueName();
//    }
//
//    /** new Queue binding for partner document dunning**/
//    @Bean
//    public Queue PartnerExpiredDocumentQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding PartnerExpiredDocumentQueueBinding() {
//        return BindingBuilder.bind(PartnerExpiredDocumentQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**new Queue binding for partner document dunning**/
//
//    /** new Queue binding for partner document deactivation dunning**/
//    @Bean
//    public Queue PartnerExpiredDocumentDeactivationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding PartnerExpiredDocumentDeactivationQueueBinding() {
//        return BindingBuilder.bind(PartnerExpiredDocumentDeactivationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**new Queue binding for partner document deactivation dunning**/
//
//    /** new Queue binding for partner document deactivation for staff dunning**/
//    @Bean
//    public Queue PartnerExpiredDocumentDeactivationStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding PartnerExpiredDocumentDeactivationStaffQueueBinding() {
//        return BindingBuilder.bind(PartnerExpiredDocumentDeactivationStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**new Queue binding for partner document deactivation  for staff dunning**/
//
//
//
//
//    @Bean
//    public Queue customerStatusInActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding customerStatusInActiveBinding() {
//        return BindingBuilder.bind(customerStatusInActive()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custDocumentVerification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custDocumentVerificationBinding() {
//        return BindingBuilder.bind(custDocumentVerification()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendEmailWithQuotationReport() {
//        return QueueBuilder.durable(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding emailWithLeadQuotationBinding() {
//        return BindingBuilder.bind(sendEmailWithQuotationReport()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendDunningAdvanceNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//
//    @Bean
//    public Queue custServiceActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custServiceActiveBinding() {
//        return BindingBuilder.bind(custServiceActive()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custServiceInActive() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custServiceInActiveBinding() {
//        return BindingBuilder.bind(custServiceInActive()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custChangePassword() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custChangePasswordBinding() {
//        return BindingBuilder.bind(custChangePassword()).to(savbillExchange()).withQueueName();
//    }
//
////    @Bean
////    public Queue  ticketTatBreachedOverDueReminder() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER)
////                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
////                .build();
////    }
////    @Bean
////    public Binding ticketTatBreachedOverDueReminderBinding() {
////        return BindingBuilder.bind(ticketTatBreachedOverDueReminder()).to(savbillExchange()).withQueueName();
////    }
//
//    @Bean
//    public Queue CustomerExpiredDocumentCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerExpiredDocumentCustomer() {
//        return BindingBuilder.bind(CustomerExpiredDocumentCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custOpenAddressShifting() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding custOpenAddressShiftingBinding() {
//        return BindingBuilder.bind(custOpenAddressShifting()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custCloseAddressShifting() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding custCloseAddressShiftingBinding() {
//        return BindingBuilder.bind(custCloseAddressShifting()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custPaymentVerification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding custPaymentVerificationBinding() {
//        return BindingBuilder.bind(custPaymentVerification()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue custTicketClose() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding custTicketCloseBinding() {
//        return BindingBuilder.bind(custTicketClose()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue custsentToNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding custsentToNotificationQueueBinding() {
//        return BindingBuilder.bind(custsentToNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadCreationNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding leadCreationNotificationQueueBinding() {
//        return BindingBuilder.bind(leadCreationNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketTATAuditQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketTATAuditBindings() {
//        return BindingBuilder.bind(sendTicketTATAuditQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendTicketTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTicketTATMessageBindings() {
//        return BindingBuilder.bind(sendTicketTATMessageQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**Ticket Remark To customer queue binding started**/
//    @Bean
//    public Queue CreateTicketFollowupRemarkCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateTicketFollowupRemarkCustomerQueueBinding() {
//        return BindingBuilder.bind(CreateTicketFollowupRemarkCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**Ticket Remark To customer queue binding ended**/
//
//    /**Email config send to apigw queue binding started**/
//    @Bean
//    public Queue CreateEmailConfigSendQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_EMAIL_CONFIG_TO_APIGW).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateEmailConfigSendQueueBinding() {
//        return BindingBuilder.bind(CreateEmailConfigSendQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /** Customer quota queue binding started**/
//    @Bean
//    public Queue SendQuotaNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    /** Customer quota queue binding ended **/
//
//
//
//
//    //Send Mvno and BU for Notification Microservice
//
//    @Bean
//    public Queue sendBusinessUnitSaveSharedDataQueueNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitSaveSharedDataBindingNotification() {
//        return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBusinessUnitUpdatedSharedDataQueueNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendBusinessUnitUpdatedSharedDataBindingNotification() {
//        return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//    }
//
//    // mvno
//    @Bean
//    public Queue sendMvnoSaveSharedDataQueueNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMvnoSaveSharedDataBindingNotification() {
//        return BindingBuilder.bind(sendMvnoSaveSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoUpdatedSharedDataQueueNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendMvnoUpdatedSharedDataBindingNotification() {
//        return BindingBuilder.bind(sendMvnoUpdatedSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//    }
//
//
//    /**Ticket External  Remark To customer queue binding started**/
//
//    @Bean
//    public Queue CreateTicketExternalRemarkCustomerQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateTicketExternalRemarkCustomerQueueBinding() {
//        return BindingBuilder.bind(CreateTicketExternalRemarkCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**Ticket External Remark To customer queue binding ended**/
//
//    /**customer invoice send to queue binding started**/
//    @Bean
//    public Queue SendCustomerInvoiceNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendCustomerInvoiceNotificationQueueBinding() {
//        return BindingBuilder.bind(SendCustomerInvoiceNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**customer invoice send to queue binding ended**/
//
//    /** Customer quota queue binding started**/
//    @Bean
//    public Queue SendQuotaExhuastNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding  SendQuotaExhuastNotificationQueueBinding() {
//        return BindingBuilder.bind(SendQuotaExhuastNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    /** Customer quota queue binding ended **/
//
//
//    @Bean
//    public Queue sendInventoryInwardApprovalQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendInventoryInwardApprovalQueueBindings() {
//        return BindingBuilder.bind(sendInventoryInwardApprovalQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /** Rabbitmq Binding for ticket alert to staff queue**/
//    @Bean
//    public Queue CreateTicketAlertStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateTicketAlertStaffQueueBinding() {
//        return BindingBuilder.bind(CreateTicketAlertStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**Rabbitmq binding for ticket alert to staff ended**/
//
//    /** Rabbitmq Binding for Immediate Attention to Customer  for unregistered queue started**/
//    @Bean
//    public Queue CreateImmediateAttentionForUnRegisteredQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateImmediateAttentionForUnRegisteredQueueBinding() {
//        return BindingBuilder.bind(CreateImmediateAttentionForUnRegisteredQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**Rabbitmq Binding for Immediate Attention to Customer for unregistered queue ended**/
//
//    /** Rabbitmq Binding for Immediate Attention to Customer  for unregistered queue started**/
//    @Bean
//    public Queue CreateImmediateAttentionForRegisteredQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateImmediateAttentionForRegisteredQueueBinding() {
//        return BindingBuilder.bind(CreateImmediateAttentionForRegisteredQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**Rabbitmq Binding for Immediate Attention to Customer for unregistered queue ended**/
//
//    /** Rabbitmq Binding for Immediate Attention to Staff  for Unregistered queue started**/
//    @Bean
//    public Queue CreateImmediateAttentionForUnRegisteredToStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateImmediateAttentionForUnRegisteredToStaffQueueBinding() {
//        return BindingBuilder.bind(CreateImmediateAttentionForUnRegisteredToStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**Rabbitmq Binding for Immediate Attention to Staff for Unregistered queue ended**/
//
//    /** Rabbitmq Binding for Unpick ticket alert to staff queue**/
//    @Bean
//    public Queue CreateUnpickTicketAlertStaffQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreateUnpickTicketAlertStaffQueueBinding() {
//        return BindingBuilder.bind(CreateUnpickTicketAlertStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**Rabbitmq binding for Unpick ticket alert to staff ended**/
//    @Bean
//    public Queue sendCAFTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCAFTATMessageBindings() {
//        return BindingBuilder.bind(sendCAFTATMessageQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendTerminationTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendsendTerminationTATMessageQueueTATMessageBindings() {
//        return BindingBuilder.bind(sendTerminationTATMessageQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendLEADTATMessageQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLEADTATMessageBindings() {
//        return BindingBuilder.bind(sendLEADTATMessageQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**recieve mvno document dunning  queue binding started**/
//    @Bean
//    public Queue sendMvnoDocumentDunningNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDocumentDunningNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoDocumentDunningNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**recieve mvno document dunning  queue binding ended**/
//
//    /**send mvno Deactivation **/
//    @Bean
//    public Queue sendMvnoExpireNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoDeactivationNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoExpireNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    /**send mvno Deactivation End **/
//
//    /**send mvno Payment **/
//    @Bean
//    public Queue sendMvnoPaymentExpireNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentExpireNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    /**send mvno Payment End **/
//
//    @Bean
//    public Queue sendMvnoPaymentDueNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding sendMvnoPaymentDueNotificationQueueBindings() {
//        return BindingBuilder.bind(sendMvnoPaymentDueNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendMVNOSharedDataQueueNotificationForISP() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_NOTIFICATION_ISP)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNOSharedDataBindingNotificationISP() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueNotificationForISP()).to(savbillExchange()).withQueueName();
//    }
//    /**send mvno Payment End **/
//
//
//    @Bean
//    public Queue sendPlanExpiryNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPlanExpiryNotificationBinding() {
//        return BindingBuilder.bind(sendPlanExpiryNotification()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue AddUsedPortNotificationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding AddUsedPortNotificationBinding() {
//        return BindingBuilder.bind(AddUsedPortNotificationQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendChangePlanNotification() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendChangePlanNotificationQueueBind() {
//        return BindingBuilder.bind(sendChangePlanNotification()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue commonOtpGenerationQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding commonOtpGenerationBinding() {
//        return BindingBuilder.bind(commonOtpGenerationQueue()).to(savbillExchange()).withQueueName();
//    }
//}
