//package com.savbill.salescrmsbss.rabbitMq;
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
//
//@Configuration
//public class RabbitMQConfiguration {
//    @Bean
//    Queue deadLetterQueue() {
//	return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
//    }
//
//    @Bean
//    DirectExchange deadLetterExchange() {
//	return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
//    }
//
//    @Bean
//    Binding DLQbinding() {
//	return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
//    }
//
//    @Bean
//    public DirectExchange savbillExchange() {
//	return new DirectExchange(RabbitMqConstants.SAVBILL_EXCHANGE);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//	return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//	final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//	rabbitTemplate.setMessageConverter(jsonMessageConverter());
//	return rabbitTemplate;
//    }
//
//    @Bean
//    public Queue createCountryQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//    @Bean
//    public Binding createCountryBinding() {
//	return BindingBuilder.bind(createCountryQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createStateQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createStateBinding() {
//    	return BindingBuilder.bind(createStateQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createCityQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createCityBinding() {
//    	return BindingBuilder.bind(createCityQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createPincodeQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createPincodeBinding() {
//    	return BindingBuilder.bind(createPincodeQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createAreaQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createAreaBinding() {
//    	return BindingBuilder.bind(createAreaQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createServiceAreaQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createServiceAreaBinding() {
//    	return BindingBuilder.bind(createServiceAreaQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createPartnerQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createPartnerBinding() {
//    	return BindingBuilder.bind(createPartnerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createClientServiceQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_CLIENT_SERVICE)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createClientServiceBinding() {
//    	return BindingBuilder.bind(createClientServiceQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createPlanGroupQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_GROUP)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createPlanGroupBinding() {
//    	return BindingBuilder.bind(createPlanGroupQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createNetworkDevicesQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_NETWORK_DEVICES)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createNetworkDevicesBinding() {
//    	return BindingBuilder.bind(createNetworkDevicesQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue roleQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.ROLE)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding roleBindings() {
//    	return BindingBuilder.bind(roleQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue userQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_USER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding userBindings() {
//    	return BindingBuilder.bind(userQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue businessUnitQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding businessUnitBindings() {
//    	return BindingBuilder.bind(businessUnitQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpReminderForStaffQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpReminderForStaffBindings() {
//    	return BindingBuilder.bind(leadFollowUpReminderForStaffQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadFollowUpReminderForCustomerQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadFollowUpReminderForCustomerBindings() {
//    	return BindingBuilder.bind(leadFollowUpReminderForCustomerQueue()).to(savbillExchange()).withQueueName();
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
//
//    @Bean
//    public Queue leadInitDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadInitDataBindings() {
//        return BindingBuilder.bind(leadInitDataQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue leadApproverDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadApproverDataBindings() {
//        return BindingBuilder.bind(leadApproverDataQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadApproverUpdateDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_UPDATE_DETAIL)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadApproverUpdateDataBindings() {
//        return BindingBuilder.bind(leadApproverUpdateDataQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadUpdateLeadInfoDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_LEAD_INFO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadSendUpdateLeadInfoBindings() {
//        return BindingBuilder.bind(leadUpdateLeadInfoDataQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadStatusReqDataQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_INFO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadStatusReqDataBindings() {
//        return BindingBuilder.bind(leadStatusReqDataQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue leadStatusDtoQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_DTO)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding leadStatusDtoBindings() {
//        return BindingBuilder.bind(leadStatusDtoQueue()).to(savbillExchange()).withQueueName();
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
//    public Queue sendCustomerCafQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CONVERT_CUSTOMER_CAF_POJO)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerCafBindings() {
//    	return BindingBuilder.bind(sendCustomerCafQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendBranchQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendBranchBindings() {
//    	return BindingBuilder.bind(sendBranchQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPartnerQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendParnerBindings() {
//    	return BindingBuilder.bind(sendPartnerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendServiceAreaQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendServiceAreaBindings() {
//    	return BindingBuilder.bind(sendServiceAreaQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomerQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendCustomerBindings() {
//    	return BindingBuilder.bind(sendCustomerQueue()).to(savbillExchange()).withQueueName();
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
//    @Bean
//    public Queue clientServiceQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//    			.build();
//    }
//
//    @Bean
//    public Binding clientServiceBinding() {
//    	return BindingBuilder.bind(clientServiceQueue()).to(savbillExchange()).withQueueName();
//    }
//    public Queue sendLeadDocConvertQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLeadDocConvertBindings() {
//    	return BindingBuilder.bind(sendCustomerQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMvnoQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_MVNO)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendMvnoBindings() {
//    	return BindingBuilder.bind(sendMvnoQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLeadMasterQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendLeadMasterBindings() {
//    	return BindingBuilder.bind(sendLeadMasterQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPopManagementQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendApiGWCustStatusUpdateBindings() {
//        return BindingBuilder.bind(sendPopManagementQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendApiGWCustStatusUpdate() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//
//    @Bean
//    public Binding sendPopManagementBindings() {
//    	return BindingBuilder.bind(sendPopManagementQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendTeamsQueue() {
//    	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS)
//    			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//    			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendTeamsBindings() {
//    	return BindingBuilder.bind(sendTeamsQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue LeadAssignMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding LeadAssignMessageBinding() {
//        return BindingBuilder.bind(LeadAssignMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createUpdatePlanPricesQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.UPDATE_PLAN_PRICES_IN_CRM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createUpdatePlanPricesBinding() {
//        return BindingBuilder.bind(createUpdatePlanPricesQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue LeadCafConvertionMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_CAF_CONVERTION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding LeadCafConvertionBinding() {
//        return BindingBuilder.bind(LeadCafConvertionMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendEmailWithQuotationReport() {
//        return QueueBuilder.durable(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Queue leadQuotationDetailsForWorkflowMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_QUOTATION_WF)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding leadQuotationDetailsForWorkflowBinding() {
//        return BindingBuilder.bind(leadQuotationDetailsForWorkflowMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendApproverDetailsForQuotationMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendApproverDetailsForQuotationBinding() {
//        return BindingBuilder.bind(sendApproverDetailsForQuotationMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendLeadQuotationAssigneMessage() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendLeadQuotationAssigneBinding() {
//        return BindingBuilder.bind(sendLeadQuotationAssigneMessage()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue mapLeadWithMilestones(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_LEAD_MILESTONES_MAPPING)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding mapLeadWithMilestonesQueueBinding() {
//        return BindingBuilder.bind(mapLeadWithMilestones()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue receivePlanGroupApigtway(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding receivePlanGroupApigtwayBinding() {
//        return BindingBuilder.bind(receivePlanGroupApigtway()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue sendPartnerSaveSharedDataSalesCrmQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerSaveSharedDataSalesCrmBinding() {
//        return BindingBuilder.bind(sendPartnerSaveSharedDataSalesCrmQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    //    Update Partner APIGW to Inventory Microservice
//    @Bean
//    public Queue sendPartnerUpdateSharedDataSalesCrmQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendPartnerUpdateSharedDataSalesCrmBinding() {
//        return BindingBuilder.bind(sendPartnerUpdateSharedDataSalesCrmQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    //System Configuration to SalesCRM
//    @Bean
//    public Queue sendCreateSystemConfigurationSalesCrmQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendCreateSystemConfigurationSalesCrmBinding(){
//        return BindingBuilder.bind(sendCreateSystemConfigurationSalesCrmQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendUpdateSystemConfigurationSalesCrmQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendUpdateSystemConfigurationSalesCrmBinding(){
//        return BindingBuilder.bind(sendUpdateSystemConfigurationSalesCrmQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendRoleCreationDetailsToCrm(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleCreationDetailsToCrmBind(){
//        return BindingBuilder.bind(sendRoleCreationDetailsToCrm()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendRoleDeletionDetailsToCrm(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendRoleDeletionDetailsToCrmBind(){
//        return BindingBuilder.bind(sendRoleDeletionDetailsToCrm()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendCustomersUpdatedSharedDataSalescrmQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCustomersUpdatedSharedDatasalescrmBinding() {
//        return BindingBuilder.bind(sendCustomersUpdatedSharedDataSalescrmQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPlanGroupUpdateQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM_UPDATE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding sendPlanGroupUpdateQueueBinding() {
//        return BindingBuilder.bind(sendPlanGroupUpdateQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendMVNOSharedDataQueueSalesCRMForISP() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_SALES_CRM_ISP)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendMVNOSharedDataBindingSalesCRMISP() {
//        return BindingBuilder.bind(sendMVNOSharedDataQueueSalesCRMForISP()).to(savbillExchange()).withQueueName();
//    }
//}
//
