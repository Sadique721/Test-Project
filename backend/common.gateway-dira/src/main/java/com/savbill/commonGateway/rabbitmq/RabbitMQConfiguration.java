//package com.savbill.commonGateway.rabbitmq;
//
//import com.savbill.commonGateway.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
//import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveInvestmentCodeSharedDataMessage;
//import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdateInvestmentCodeSharedDataMessage;
////import org.springframework.amqp.core.*;
////import org.springframework.amqp.rabbit.annotation.RabbitListener;
////import org.springframework.amqp.rabbit.connection.ConnectionFactory;
////import org.springframework.amqp.rabbit.core.RabbitTemplate;
////import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
////import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import javax.transaction.Transactional;
//
//@Configuration
//@Profile("rabbitmq")
//public class RabbitMQConfiguration
//{
//	@Bean
//	Queue deadLetterQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.DEAD_LETTER_QUEUE).build();
//	}
//
//	@Bean
//	DirectExchange deadLetterExchange() {
//		return new DirectExchange(RabbitMqConstants.DEAD_LETTER_EXCHANGE);
//	}
//
//	@Bean
//	Binding DLQbinding() {
//		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(RabbitMqConstants.DEAD_LETTER_KEY);
//	}
//
//	@Bean
//	public DirectExchange  savbillExchange() {
//		return new DirectExchange (RabbitMqConstants.SAVBILL_EXCHANGE);
//	}
//
//	@Bean
//	public MessageConverter jsonMessageConverter() {
//		return new Jackson2JsonMessageConverter();
//	}
//
//	@Bean
//	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		rabbitTemplate.setMessageConverter(jsonMessageConverter());
//		return rabbitTemplate;
//	}
//
//	/**
//	 * For testing purpose only
//	 * @return
//	 */
//	@Bean
//	public Queue createTestReceiveQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.TEST_RECEIVE).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createTestReceiveBinding() {
//		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue createTestSendQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.TEST_SEND).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createTestSendBinding() {
//		return BindingBuilder.bind(createTestReceiveQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue createCountryQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//	@Bean
//	public Binding createCountryBinding() {
//		return BindingBuilder.bind(createCountryQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountrySharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountrySharedDataBinding() {
//		return BindingBuilder.bind(sendCountrySharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountryUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountrySharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountrySharedDataBindingInventory() {
//		return BindingBuilder.bind(sendCountrySharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountryUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountrySharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountrySharedDataBindingSample() {
//		return BindingBuilder.bind(sendCountrySharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountryUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendCountryUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	//City Queues
//	@Bean
//	public Queue createCityQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CITY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createCityBinding() {
//		return BindingBuilder.bind(createCityQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCitySharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCitySharedDataBinding() {
//		return BindingBuilder.bind(sendCitySharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCityUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCityUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendCityUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//    Create City APIGW to Inventory Microservice
//	@Bean
//	public Queue sendCitySharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCitySharedDataBindingInventory() {
//		return BindingBuilder.bind(sendCitySharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	//    Update City APIGW to Inventory Microservice
//	@Bean
//	public Queue sendCityUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCityUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendCityUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCitySharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCitySharedDataBindingSample() {
//		return BindingBuilder.bind(sendCitySharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCityUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCityUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendCityUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue createStateQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_STATE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createStateBinding() {
//		return BindingBuilder.bind(createStateQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateSharedDataBinding() {
//		return BindingBuilder.bind(sendStateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	//customer for common apigateway
//
//	@Bean
//	public Queue sendCustomersSaveSharedDataForApiCommonQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCustomersSaveSharedDataForApiCommonBinding() {
//		return BindingBuilder.bind(sendCustomersSaveSharedDataForApiCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendCustomersUpdatedSharedDataForApiCommonQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCustomersUpdatedSharedDataForApiCommonBinding() {
//		return BindingBuilder.bind(sendCustomersUpdatedSharedDataForApiCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Binding sendStateUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendStateUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendStateSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendStateSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendStateUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendStateSharedDataBindingSample() {
//		return BindingBuilder.bind(sendStateSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendStateUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue createPincodeQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PINCODE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createPincodeBinding() {
//		return BindingBuilder.bind(createPincodeQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendPincodeSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//    Create Pincode APIGW to Inventory Microservice
//	@Bean
//	public Queue sendPincodeSaveSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeSaveSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendPincodeSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	//    Update Pincode APIGW to Inventory Microservice
//	@Bean
//	public Queue sendPincodeUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeSaveSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeSaveSharedDataBindingSample() {
//		return BindingBuilder.bind(sendPincodeSaveSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	//Queue configuration for Area
//	@Bean
//	public Queue createAreaQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_AREA)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding createAreaBinding() {
//		return BindingBuilder.bind(createAreaQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//    Create Area APIGW to Inventory Microservice
//	@Bean
//	public Queue sendAreaSaveSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaSaveSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendAreaSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Update Area APIGW to Inventory Microservice
//	@Bean
//	public Queue sendAreaUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaSaveSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaSaveSharedDataBindingSample() {
//		return BindingBuilder.bind(sendAreaSaveSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	//Service Area
//	@Bean
//	public Queue serviceArea() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding serviceareabinding() {
//		return BindingBuilder.bind(serviceArea()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaBindings() {
//		return BindingBuilder.bind(sendServiceAreaQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue serviceAreaFromAPIGW() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding serviceAreaFromAPIGWSuccessBinding() {
//		return BindingBuilder.bind(serviceAreaFromAPIGW()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaKpi() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS_KPI)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//	@Bean
//	public Binding sendServiceAreaKpibinding() {
//		return BindingBuilder.bind(sendServiceAreaKpi()).to(savbillExchange()).withQueueName();
//	}
//	//ServiceArea
//
//	@Bean
//	public Queue sendServiceAreaSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaSaveSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaSaveSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaSaveSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaSaveSharedDataBindingSample() {
//		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	//Business Unit Queues
//	@Bean
//	public Queue businessUnitQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding businessUnitBindings() {
//		return BindingBuilder.bind(businessUnitQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue businessunitCreatedFrombss() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding bssbusinessunitCreateSuccessBinding() {
//		return BindingBuilder.bind(businessunitCreatedFrombss()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue businessUnitFromAPIGW() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_SUCCESS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding businessUnitFromAPIGWSuccessBinding() {
//		return BindingBuilder.bind(businessUnitFromAPIGW()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessunitKpiQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessunitQueueBindings() {
//		return BindingBuilder.bind(sendBusinessunitKpiQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingSample() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//	//Business Verticals Queue
//	@Bean
//	public Queue sendBusinessVerticalsSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessVerticalsUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsUpdatedSharedDaataBinding() {
//		return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//// Branch Queue
//@Bean
//public Queue sendBranchQueue() {
//	return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH)
//			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//}
//
//	@Bean
//	public Binding sendBranchBindings() {
//		return BindingBuilder.bind(sendBranchQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue branchMessageFromAPIGW() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_BRANCH_SUCCESS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding branchMessageFromAPIGWSuccessBinding() {
//		return BindingBuilder.bind(branchMessageFromAPIGW()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBranchKpiQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//	@Bean
//	public Binding sendBranchKpiBindings() {
//		return BindingBuilder.bind(sendBranchKpiQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBranchSaveSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchSaveSharedDataBinding() {
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBranchUpdatedSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchUpdatedSharedDataBinding() {
//		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//    Create Branch APIGW to Inventory Microservice
//	@Bean
//	public Queue sendBranchSaveSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchSaveSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Update Branch APIGW to Inventory Microservice
//	@Bean
//	public Queue sendBranchUpdatedSharedDataQueueInventory() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchUpdatedSharedDataBindingInventory() {
//		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBranchSaveSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchSaveSharedDataBindingSample() {
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBranchUpdatedSharedDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchUpdatedSharedDataBindingSample() {
//		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//
//	// Queues for CPM
//	@Bean
//	public Queue sendCountrySaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountrySaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendCountrySaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryUpdateSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountryUpdateSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendCountryUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCitySaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCitySharedDataBindingCPM() {
//		return BindingBuilder.bind(sendCitySaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	//    Update City APIGW to CPM Microservice
//	@Bean
//	public Queue sendCityUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCityUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendCityUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendPincodeSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	//    Update Pincode APIGW to CPM Microservice
//	@Bean
//	public Queue sendPincodeUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendPincodeUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendStateSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendStateSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendStateUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendAreaSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Update Area APIGW to CPM Microservice
//	@Bean
//	public Queue sendAreaUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendAreaUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendServiceAreaUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBusinessVerticalsSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessVerticalsUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBranchSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Update Branch APIGW to CPM Microservice
//	@Bean
//	public Queue sendBranchUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendBranchUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendInvestmentCodeSaveSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//   @Bean
//	public Binding sendInvestmentCodeSaveSharedDataBindingCPM(){
//	   return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//   }
//	@Bean
//	public Queue sendInvestmentCodeUpdateSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendInvestmentCodeUpdateSharedDataBindingCPM(){
//		return BindingBuilder.bind(sendInvestmentCodeUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendSubBusinessUnitSaveSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendSubBusinessUnitSaveSharedDataBindingCPM(){
//		return BindingBuilder.bind(sendSubBusinessUnitSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendSubBusinessUnitUpdateSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendSubBusinessUnitUpdateSharedDataBindingCPM(){
//		return BindingBuilder.bind(sendSubBusinessUnitUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendSubBusinessVerticalsSaveSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendSubBusinessVerticalsSaveSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendSubBusinessVerticalsSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendSubBusinessVerticalsUpdatedSharedDataQueueCPM() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendSubBusinessVerticalsUpdatedSharedDataBindingCPM() {
//		return BindingBuilder.bind(sendSubBusinessVerticalsUpdatedSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendDepartmentSaveSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendDepartmentSaveShareDataBindingCPM(){
//		return BindingBuilder.bind(sendDepartmentSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendDepartmentUpdateSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendDepartmentUpdateShareDataBindingCPM(){
//		return BindingBuilder.bind(sendDepartmentUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBankManagementSaveSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBankManagementSaveShareDataBindingCPM(){
//		return BindingBuilder.bind(sendBankManagementSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBankManagementUpdateSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBankManagementUpdateShareDataBindingCPM(){
//		return BindingBuilder.bind(sendBankManagementUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRegionSaveSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionSaveSharedDataBindingCPM(){
//		return BindingBuilder.bind(sendRegionSaveSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRegionUpdateSharedDataQueueCPM(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionUpdateSharedDataBindingCPM(){
//		return BindingBuilder.bind(sendRegionUpdateSharedDataQueueCPM()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendCountrySaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountrySaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendCountrySaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCountryUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendCountryUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendStateSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendStateSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendStateSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendStateUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendStateUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendCitySaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCitySaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendCitySaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCityUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCityUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendCityUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//	@Bean
//	public Queue sendPincodeSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendPincodeSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendPincodeSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendPincodeUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendPincodeUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendAreaSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendAreaSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendAreaSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendAreaUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendAreaUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendServiceAreaSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICEAREA_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendServiceAreaSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendServiceAreaSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdateSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICEAREA_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendServiceAreaUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendAreaUpdateSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendInvestmentCodeSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendInvestmentCodeSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendInvestmentCodeSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendInvestmentCodeUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendInvestmentCodeSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBusinessUnitSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//
//	@Bean
//	public Queue sendSubBusinessUnitSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendSubBusinessUnitSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendSubBusinessUnitSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendSubBusinessUnitSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendSubBusinessUnitUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendSubBusinessUnitSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//	@Bean
//	public Queue sendBankSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBankSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBankSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBankSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBankUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBankSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//
//	@Bean
//	public Queue sendBranchSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBranchSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBranchSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBranchUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendBranchSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//	@Bean
//	public Queue sendRegionSaveSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionSaveSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendRegionSaveSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRegionSharedDataQueueRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionUpdateSharedDataBindingRevenue(){
//		return BindingBuilder.bind(sendRegionSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//	// Setting Data Transfer to Common APIGW TO CMS
//	//MVNO
//	@Bean
//	public Queue sendCreateMvnoCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateMvnoCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendCreateMvnoCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateMvnoCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateMvnoCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendUpdateMvnoCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Role
//	@Bean
//	public Queue sendCreateRoleCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateRoleCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendCreateRoleCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateRoleCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateRoleCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendUpdateRoleCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Staff User
//	@Bean
//	public Queue sendCreateStafUserCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateStaffUserCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendCreateStafUserCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateStaffUserCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateStaffUserCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendUpdateStaffUserCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Teams
//	@Bean
//	public Queue sendCreateTeamsCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateTeamsCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendCreateTeamsCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateTeamsCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateTeamsCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendUpdateTeamsCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration
//	@Bean
//	public Queue sendCreateSystemConfigurationCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationCommonGatewayCMSQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationCommonGatewayCMSBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationCommonGatewayCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Partner for Common APIGateway
//
//	@Bean
//	public Queue sendPartnerSaveSharedDataForApiCommonQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_API_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPartnerSaveSharedDataForApiCommonBinding() {
//		return BindingBuilder.bind(sendPartnerSaveSharedDataForApiCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendPartnerUpdatedSharedDataForApiCommonQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPartnerUpdatedSharedDataForApiCommonBinding() {
//		return BindingBuilder.bind(sendPartnerUpdatedSharedDataForApiCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPlanServiceAreBindCheckQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPlanServiceAreBindCheckBinding() {
//		return BindingBuilder.bind(sendPlanServiceAreBindCheckQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendPlanServiceAreBindCheckAtDeleteQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK_AT_DELETE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPlanServiceAreBindCheckAtDeleteBinding() {
//		return BindingBuilder.bind(sendPlanServiceAreBindCheckAtDeleteQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendDStaffUserSaveToRevenue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendDStaffUserSaveToRevenueQueueBind() {
//		return BindingBuilder.bind(sendDStaffUserSaveToRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendDStaffUserUpdateToRevenue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendDStaffUserUpdateToRevenueBind() {
//		return BindingBuilder.bind(sendDStaffUserUpdateToRevenue()).to(savbillExchange()).withQueueName();
//	}
////	Send Role to Revenue
//	@Bean
//	public Queue sendCreateRoleToRevenueQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateRoleToRevenueBinding() {
//		return BindingBuilder.bind(sendCreateRoleToRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendUpdateRoleToRevenueQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateRoleToRevenueBinding() {
//		return BindingBuilder.bind(sendUpdateRoleToRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryPartnerSharedDataQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountryPartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendCountryPartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCountryPartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCountryUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendCountryPartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//===
//	@Bean
//	public Queue sendSateCreatePartnerSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateCreatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendSateCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendStateUpdatePartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendStateUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendStateUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//city
//	@Bean
//	public Queue sendCityCreatePartnerSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCityCreatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendCityCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCityUpdatePartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCityUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendCityUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//// pincode
//@Bean
//public Queue sendPincodeCreatePartnerSharedDataQueue() {
//	return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_PARTNER)
//			.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//			.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//			.build();
//}
//
//	@Bean
//	public Binding sendPincodeCreatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendPincodeCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendPincodeUpdatePartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendPincodeUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendPincodeUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Area
//	@Bean
//	public Queue sendAreaCreatePartnerSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaCreatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendAreaCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendAreaUpdatePartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendAreaUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendAreaUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	//----service Area-----
//
//	@Bean
//	public Queue sendServiceAreaCreatePartnerSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaCreatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendServiceAreaCreatePartnerSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceAreaUpdatePartnerUpdateSharedDataQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendServiceAreaUpdatePartnerSharedDataBinding() {
//		return BindingBuilder.bind(sendServiceAreaUpdatePartnerUpdateSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMVNOSharedDataQueueRevenue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingRevenue() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendRoleUpdatedRevenueDataQueueSample() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDAT_MVNO_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleUpdatedRevenueDataBindingSample() {
//		return BindingBuilder.bind(sendRoleUpdatedRevenueDataQueueSample()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBusinessVerticalsDataSaveSharedDataQueuePartner() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_DATA_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsDataSaveSharedDataQueuePartnerBindings() {
//		return BindingBuilder.bind(sendBusinessVerticalsDataSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessVerticalsUpdatedSharedDataQueuePartner() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsUpdatedSharedDataQueuePartnerBinding() {
//		return BindingBuilder.bind(sendBusinessVerticalsUpdatedSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBranchSaveSharedDataPartnerQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchSaveSharedDataPartnerBinding() {
//		return BindingBuilder.bind(sendBranchSaveSharedDataQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Update Branch APIGW to Inventory Microservice
//	@Bean
//	public Queue sendBranchUpdatedSharedDataPartnerQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBranchUpdatedSharedDataPartnerQueueBinding() {
//		return BindingBuilder.bind(sendBranchUpdatedSharedDataPartnerQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendRegionSaveSharedDataQueuePartner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionSaveSharedDataBindingPartner(){
//		return BindingBuilder.bind(sendRegionSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRegionSharedDataQueuePatrtner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRegionUpdateSharedDataBindingPartner(){
//		return BindingBuilder.bind(sendRegionSharedDataQueuePatrtner()).to(savbillExchange()).withQueueName();
//	}
//
//	//    Create Business Unit APIGW to Inventory Microservice
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueRadius() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingRadius() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueRadius()).to(savbillExchange()).withQueueName();
//	}
//	//    Update Business Unit APIGW to Inventory Microservice
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueRadius() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingRadius() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueRadius()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendCafToCustomerCommonAPIGWQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_COMMONGATEWAY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendCafToCustomerCommonAPIGWBinding() {
//		return BindingBuilder.bind(sendCafToCustomerCommonAPIGWQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendInvestmentCodeSaveSharedDataQueuePartner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendInvestmentCodeSaveSharedDataBindingPartner(){
//		return BindingBuilder.bind(sendInvestmentCodeSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendInvestmentCodeSharedDataQueuePartner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendInvestmentCodeUpdateSharedDataBindingPartner(){
//		return BindingBuilder.bind(sendInvestmentCodeSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBusinessVerticalsSaveSharedDataQueuePartner() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_VERTICALS_DATA_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendBusinessVerticalsSaveSharedDataBindingPartner() {
//		return BindingBuilder.bind(sendBusinessVerticalsSaveSharedDataQueuePartner()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendServiceCreateForCommonQueue() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendServiceCreateForCommonQueueBindings() {
//		return BindingBuilder.bind(sendServiceCreateForCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendServiceUpdateForCommon() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendserviceUpdateForCommonBindinf() {
//		return BindingBuilder.bind(sendServiceUpdateForCommon()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration to Inventory
//	@Bean
//	public Queue sendCreateSystemConfigurationInventoryQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationInventoryBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationInventoryQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationInventoryQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationInventoryBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationInventoryQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration to Revenue
//	@Bean
//	public Queue sendCreateSystemConfigurationRevenueQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationRevenueBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationRevenueQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationRevenueBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration to Ticket
//	@Bean
//	public Queue sendCreateSystemConfigurationTicketQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationTicketBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationTicketQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationTicketQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationTicketBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationTicketQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration to Partner
//	@Bean
//	public Queue sendCreateSystemConfigurationPartnerQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationPartnerBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationPartnerQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationPartnerQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationPartnerBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationPartnerQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//System Configuration to SalesCRM
//	@Bean
//	public Queue sendCreateSystemConfigurationSalesCrmQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateSystemConfigurationSalesCrmBinding(){
//		return BindingBuilder.bind(sendCreateSystemConfigurationSalesCrmQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendUpdateSystemConfigurationSalesCrmQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateSystemConfigurationSalesCrmBinding(){
//		return BindingBuilder.bind(sendUpdateSystemConfigurationSalesCrmQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendRoleCreationDetailsToRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToRevenueBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToRevenue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToRevenue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToRevenueBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToRevenue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//
//	@Bean
//	public Queue sendRoleCreationDetailsTocms(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsTocmsBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsTocms()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsTocms(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsTocmsBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsTocms()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendRoleCreationDetailsToInventory(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToInventoryBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToInventory()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToInventory(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_INVENTORY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToInventoryBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToInventory()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//	@Bean
//	public Queue sendRoleCreationDetailsToPartner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToPartnerBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToPartner()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToPartner(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToPartnerBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToPartner()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendRoleCreationDetailsToCrm(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_CRM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToCrmBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToCrm()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToCrm(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_CRM)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToCrmBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToCrm()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//	@Bean
//	public Queue sendRoleCreationDetailsToTicket(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToTicketBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToTicket()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToTicket(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_TICKET)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToTicketBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToTicket()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendRoleCreationDetailsToRadius(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleCreationDetailsToRadiusBind(){
//		return BindingBuilder.bind(sendRoleCreationDetailsToRadius()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendRoleDeletionDetailsToRadius(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendRoleDeletionDetailsToRadiusBind(){
//		return BindingBuilder.bind(sendRoleDeletionDetailsToRadius()).to(savbillExchange()).withQueueName();
//	}
//
//	//Send Mvno and BU for Notification Microservice
//
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueNotification() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_NOTIFICATION)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingNotification() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueNotification() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingNotification() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//	}
//
//	// mvno
//
//	@Bean
//	public Queue sendMvnoSaveSharedDataQueueNotification() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendMvnoSaveSharedDataBindingNotification() {
//		return BindingBuilder.bind(sendMvnoSaveSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMvnoUpdatedSharedDataQueueNotification() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendMvnoUpdatedSharedDataBindingNotification() {
//		return BindingBuilder.bind(sendMvnoUpdatedSharedDataQueueNotification()).to(savbillExchange()).withQueueName();
//	}
//
//	// Team Hierarchy
//	@Bean
//	public Queue sendHierarchySaveSharedDataCommonAPIQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAM_HIERARCHY_CREATE_DATA_SHARE_COMMONGATEWAY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendHierarchySaveSharedDataCommonAPIBinding() {
//		return BindingBuilder.bind(sendHierarchySaveSharedDataCommonAPIQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendHierarchyUpdatedSharedDataCommonAPIQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_TEAM_HIERARCHY_UPDATE_DATA_SHARE_COMMONGATEWAY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendHierarchyUpdatedSharedDataCommonAPIBinding() {
//		return BindingBuilder.bind(sendHierarchyUpdatedSharedDataCommonAPIQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	//Warehouse Team Mapping Inventory to CommonAPI
//	@Bean
//	public Queue sendCreateWarehouseTeamMappingCommonAPIQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CREATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONGATEWAY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateWarehouseTeamMappingCommonAPIBinding(){
//		return BindingBuilder.bind(sendCreateWarehouseTeamMappingCommonAPIQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendUpadteWarehouseTeamMappingCommonAPIQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_UPDATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONGATEWAY)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpadteWarehouseTeamMappingCommonAPIBinding(){
//		return BindingBuilder.bind(sendUpadteWarehouseTeamMappingCommonAPIQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendCreateTeamsCommonGatewayRevenueQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateTeamsCommonGatewayRevenueBinding(){
//		return BindingBuilder.bind(sendCreateTeamsCommonGatewayRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendUpdateTeamsCommonGatewayRevenueQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_REVENUE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateTeamsCommonGatewayRevenueBinding(){
//		return BindingBuilder.bind(sendUpdateTeamsCommonGatewayRevenueQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendPaymentConfigurationToCMSQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendPaymentConfigurationToCMSQueueBinding(){
//		return BindingBuilder.bind(sendPaymentConfigurationToCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	/**recieve socket message for cms queue binding started**/
//	@Bean
//	public Queue SendSocketMessageToCommonQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON).withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding  SendSocketMessageToCommonQueueBinding() {
//		return BindingBuilder.bind(SendSocketMessageToCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	/**recieve socket message for cms queue binding ended**/
//
//
//
//	@Bean
//	public Queue sendMvnoDocSaveMessageQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendMvnoDocSaveMessageQueueBindings() {
//		return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMvnoDocUpdateMessageQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_MVNO_DOC_UPDATE_FROM_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendMvnoDocUpdateMessageQueueBindings() {
//		return BindingBuilder.bind(sendMvnoDocSaveMessageQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendMvnoStatusDunningNotificationQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendMvnoStatusDunningNotificationQueueBindings() {
//		return BindingBuilder.bind(sendMvnoStatusDunningNotificationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendStaffStatusDunningNotificationQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendStaffStatusDunningNotificationQueueBindings() {
//		return BindingBuilder.bind(sendStaffStatusDunningNotificationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendCustomerStatusDunningNotificationQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendCustomerStatusDunningNotificationQueueBindings() {
//		return BindingBuilder.bind(sendCustomerStatusDunningNotificationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue radiusSystemConfigurationQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_SYSTEM_CONFIGURATION_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding radiusSystemConfigurationQueueBinding() {
//		return BindingBuilder.bind(radiusSystemConfigurationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendCreateMvnoRadiusQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendCreateMvnoRadiusBinding(){
//		return BindingBuilder.bind(sendCreateMvnoRadiusQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendUpdateMvnoRadiusQueue(){
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendUpdateMvnoRadiusBinding(){
//		return BindingBuilder.bind(sendUpdateMvnoRadiusQueue()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendMVNOSharedDataQueueRevenueForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingRevenueISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueRevenueForISP()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMVNOSharedDataQueueCMSForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingCMSISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueCMSForISP()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendMVNOSharedDataQueuePartnerForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_Partner_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingPartnerISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueuePartnerForISP()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendMVNOSharedDataQueueSalesCRMForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_SALES_CRM_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingSalesCRMISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueSalesCRMForISP()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMVNOSharedDataQueueNotificationForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_NOTIFICATION_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingNotificationISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueNotificationForISP()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMVNOSharedDataQueueInventoryForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_INVENTORY_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingInventoryISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueInventoryForISP()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendMVNOSharedDataQueueTicketForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_TICKET_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingTicketISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueTicketForISP()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendMVNOSharedDataQueueRadiusForISP() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_RADIUS_ISP)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendMVNOSharedDataBindingRadiusISP() {
//		return BindingBuilder.bind(sendMVNOSharedDataQueueRadiusForISP()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendBusinessUnitSaveSharedDataQueueCMS() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitSaveSharedDataBindingCMS() {
//		return BindingBuilder.bind(sendBusinessUnitSaveSharedDataQueueCMS()).to(savbillExchange()).withQueueName();
//	}
//	@Bean
//	public Queue sendBusinessUnitUpdatedSharedDataQueueCMS() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendBusinessUnitUpdatedSharedDataBindingCMS() {
//		return BindingBuilder.bind(sendBusinessUnitUpdatedSharedDataQueueCMS()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendSaveMVNO() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendSaveMVNOBinding() {
//		return BindingBuilder.bind(sendSaveMVNO()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendUpdateMVNO() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendUpdateMVNOBinding() {
//		return BindingBuilder.bind(sendUpdateMVNO()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendSaveStaffPartner() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendSaveStaffPartnerBinding() {
//		return BindingBuilder.bind(sendSaveStaffPartner()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendUpdateStaffPartner() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_PARTNER)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendUpdateStaffPartnerBinding() {
//		return BindingBuilder.bind(sendUpdateStaffPartner()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue commonOtpGenerationQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding commonOtpGenerationBinding() {
//		return BindingBuilder.bind(commonOtpGenerationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendOTPProfileToCommonQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendOTPProfileToCommonBinding(){
//		return BindingBuilder.bind(sendOTPProfileToCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//	@Bean
//	public Queue sendOTPProfileUpdateToCommonQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON_UPDATE)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendOTPProfileUpdateToCommonBinding(){
//		return BindingBuilder.bind(sendOTPProfileUpdateToCommonQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendOTPProfileToCMSQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendOTPProfileToCMSBinding(){
//		return BindingBuilder.bind(sendOTPProfileToCMSQueue()).to(savbillExchange()).withQueueName();
//	}
//
//
//
//
//	@Bean
//	public Queue sendPaymentConfigurationToIntegrationQueue(){
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_INTEGRATION)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//	@Bean
//	public Binding sendPaymentConfigurationToIntegrationQueueBinding(){
//		return BindingBuilder.bind(sendPaymentConfigurationToIntegrationQueue()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendLocationToCMS() {
//		return QueueBuilder.durable(SharedDataConstants.QUEUE_SEND_LOCATION_TO_COMMON)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY)
//				.build();
//	}
//
//	@Bean
//	public Binding sendLocationToCMSToCMSQueueBind() {
//		return BindingBuilder.bind(sendLocationToCMS()).to(savbillExchange()).withQueueName();
//	}
//
//	@Bean
//	public Queue sendLocationServiceareamappingQueue() {
//		return QueueBuilder.durable(RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING)
//				.withArgument("x-dead-letter-exchange", RabbitMqConstants.DEAD_LETTER_EXCHANGE)
//				.withArgument("x-dead-letter-routing-key", RabbitMqConstants.DEAD_LETTER_KEY).build();
//	}
//
//	@Bean
//	public Binding sendLocationServiceareamappingQueueQueueBindings() {
//		return BindingBuilder.bind(sendLocationServiceareamappingQueue()).to(savbillExchange()).withQueueName();
//	}
//}
//
//
