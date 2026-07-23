package com.savbill.inventorymanagement.rabbitmq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender 
{
	private static Log log = LogFactory.getLog(MessageSender.class);

//	@Autowired
//	private RabbitTemplate rabbitTemplate;
//
//	public String send(CustomMessage message,String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(InwardDto message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CustMacMappingMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(ItemMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CustomerInventoryMappingMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(WareHouseDto message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(ProductCategoryDto message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(ProductMessage productMessage, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, productMessage);
//		log.info("Send msg  " + productMessage);
//		return "Message Published";
//	}
//	public String send(PopManagementMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//	}
//
//	public String send(CustomerInventoryRevenueMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//	}
//	public String send(ChargeMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//	}
//
//    public String send(RecordPaymentMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//    }
//
//	public String send(SaveWarehouseTeamMappingSharedMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//	}
//
//	public String send(UpdateWarehouseTeamMappingSharedMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg " + message);
//		return "Message Published";
//	}
//	public String send(InventorySerialNumberMessage message,String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(CustInvParamsMessage message,String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(InventoryApprovalSuccessMsg message,String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//
//	public String send(WarrantyNotificationMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(DevicePortNotificationMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//
//	public String send(SaveUpdateVendorMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}

}
