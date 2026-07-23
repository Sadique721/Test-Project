//package com.savbill.integrationsystem.rabbitmq;
//
//import com.savbill.integrationsystem.deviceveri.domain.SerializedItemData;
//import com.savbill.integrationsystem.deviceveri.model.SerializedItemDTO;
//import com.savbill.integrationsystem.nms.entity.UuidDataDTO;
//import com.savbill.integrationsystem.rms.model.InOutWardMACMapingDTO;
//import com.savbill.integrationsystem.rms.model.InwardDto;
//import com.savbill.integrationsystem.rms.model.ProductDto;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
////import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageSender
//{
//	private static Log log = LogFactory.getLog(MessageSender.class);
//
////	@Autowired
////	private RabbitTemplate rabbitTemplate;
//
//	public String send(CustomMessage message,String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(ProductDto message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(InwardDto message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(SerializedItemDTO message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(InOutWardMACMapingDTO message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(TicketMessageIntegration message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(UuidDataDTO message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CustPayDTOMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//
//}
