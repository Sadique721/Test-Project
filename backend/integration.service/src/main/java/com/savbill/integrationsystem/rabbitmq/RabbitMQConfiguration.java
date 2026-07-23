//package com.savbill.integrationsystem.rabbitmq;
//
//import org.springframework.amqp.core.*;
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
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//    /**
//     * For testing purpose only
//     *
//     * @return
//     */
//    @Bean
//    public Queue createTestReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.TEST_RECEIVE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createTestReceiveBinding() {
//        return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue createTestSendQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.TEST_SEND).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding createTestSendBinding() {
//        return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//    @Bean
//    public Queue billGenReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BILL_GEN_SEND_INTEGRATION_SYSTEM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding billGenReceiveBinding() {
//        return BindingBuilder.bind(billGenReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue chargeReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding chargeReceiveQueueBinding() {
//        return BindingBuilder.bind(chargeReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue planServiceReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding planServiceReceiveQueueBinding() {
//        return BindingBuilder.bind(planServiceReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customersServiceReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding pcustomersServiceReceiveQueueBinding() {
//        return BindingBuilder.bind(customersServiceReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue creditDoccumentReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding creditDocumentServiceReceiveQueueBinding() {
//        return BindingBuilder.bind(creditDoccumentReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue taxReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_TAX_MGMTN_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding taxReceiveQueueBinding() {
//        return BindingBuilder.bind(taxReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serviceAreaReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding serviceAreaReceiveQueueBinding() {
//        return BindingBuilder.bind(serviceAreaReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue businessUnitReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding businessUnitReceiveQueueBinding() {
//        return BindingBuilder.bind(businessUnitReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue debitDocumentReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_DEBIT_DOCUMENT_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding debitDocumentReceiveQueueBinding() {
//        return BindingBuilder.bind(debitDocumentReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue staffManagementReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding staffManagementReceiveQueueBinding() {
//        return BindingBuilder.bind(staffManagementReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue branchMessageReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding branchMessageReceiveQueueBinding() {
//        return BindingBuilder.bind(branchMessageReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerMessageReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SUCCESS).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerMessageReceiveQueueBinding() {
//        return BindingBuilder.bind(customerMessageReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue creditNoteMessageReceiveQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding creditNoteMessageReceiveQueueBinding() {
//        return BindingBuilder.bind(creditNoteMessageReceiveQueue()).to(savbillExchange()).withQueueName();
//    }
//
////    @Bean
////    public Queue createSendSerialNumberQueue() {
////        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SERIAL_NUMBER).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
////                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
////    }
////
////    @Bean
////    public Binding createSendSerialNumberQueueBinding() {
////        return BindingBuilder.bind(createSendSerialNumberQueue()).to(savbillExchange()).withQueueName();
////
//@Bean
//public Queue customerPlanMappingSendInIntegration() {
//    return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//            .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//}
//
//    @Bean
//    public Binding customerPlanMappingSendInIntegrationBinding() {
//        return BindingBuilder.bind(customerPlanMappingSendInIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue customerServiceMappingSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding customerServiceMappingSendInIntegrationBinding() {
//        return BindingBuilder.bind(customerServiceMappingSendInIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ServiceSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding ServiceSendInIntegrationBinding() {
//        return BindingBuilder.bind(ServiceSendInIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CreatePostpaidPlanQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CreatePostpaidPlanQueueBinding() {
//        return BindingBuilder.bind(CreatePostpaidPlanQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CustomerInventorySendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerInventorySendInIntegrationBinding() {
//        return BindingBuilder.bind(CustomerInventorySendInIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue CustomerInventoryItemSendInIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding CustomerInventoryItemSendInIntegrationBinding() {
//        return BindingBuilder.bind(CustomerInventoryItemSendInIntegration()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue ticketmessageIntegrationSendInQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding ticketmessageIntegrationSendInQueueBinding() {
//        return BindingBuilder.bind(selfCareCreateTicketSendInQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue selfCareCreateTicketSendInQueue() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding selfCareCreateTicketSendInQueueBinding() {
//        return BindingBuilder.bind(ticketmessageIntegrationSendInQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ProductFromRms() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding ProductFromRmsBinding() {
//        return BindingBuilder.bind(ProductFromRms()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue ProductCategoryFromIntegration() {
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//    }
//
//    @Bean
//    public Binding ProductCategoryFromIntegrationBinding() {
//        return BindingBuilder.bind(ProductCategoryFromIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue wareHouseToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding wareHouseToIntegrationBinding() {
//        return BindingBuilder.bind(wareHouseToIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue inwardToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding inwardToIntegrationBinding() {
//        return BindingBuilder.bind(inwardToIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serializedItemFromRms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding serializedItemFromRmsBinding() {
//        return BindingBuilder.bind(serializedItemFromRms()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue serializedItemHistoryFromRms(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding serializedItemHistoryFromRmsBinding() {
//        return BindingBuilder.bind(serializedItemHistoryFromRms()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue inwardSendToIntegration(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding inwardSendToIntegrationBinding() {
//        return BindingBuilder.bind(inwardSendToIntegration()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Binding sendConfigurationToIntigrationQueueBinding(){
//        return BindingBuilder.bind(sendConfigurationToIntigrationQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendConfigurationToIntigrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCUuidToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidToCMSQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendCNmsServiceDeleteRequestQueueBinding(){
//        return BindingBuilder.bind(sendServiceDeleteRequestQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendServiceDeleteRequestQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendUuidDataToCMSQueueBinding(){
//        return BindingBuilder.bind(sendUuidDataToCMSQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendUuidDataToCMSQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Binding sendApproveItemFromAPIGWBinding(){
//        return BindingBuilder.bind(sendApproveItemFromAPIGWQueue()).to(savbillExchange()).withQueueName();
//    }
//    @Bean
//    public Queue sendApproveItemFromAPIGWQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//
//    @Bean
//    public Queue sendPaymentConfigurationToIntegrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_INTEGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentConfigurationToIntegrationQueueBinding(){
//        return BindingBuilder.bind(sendPaymentConfigurationToIntegrationQueue()).to(savbillExchange()).withQueueName();
//    }
//
//
//
//    @Bean
//    public Queue sendPaymentAuditToCmsQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToCmsQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToCmsQueue()).to(savbillExchange()).withQueueName();
//    }
//
//    @Bean
//    public Queue sendPaymentAuditToIntegrationQueue(){
//        return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION)
//                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//                .build();
//    }
//    @Bean
//    public Binding sendPaymentAuditToIntegrationQueueBinding(){
//        return BindingBuilder.bind(sendPaymentAuditToIntegrationQueue()).to(savbillExchange()).withQueueName();
//    }
//}
