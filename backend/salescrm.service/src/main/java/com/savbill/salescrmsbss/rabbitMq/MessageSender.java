//package com.savbill.salescrmsbss.rabbitMq;
//
//import com.savbill.salescrmsbss.rabbitMq.message.*;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.savbill.salescrmsbss.entity.pojo.SendLeadDocConvertPojo;
//import com.savbill.salescrmsbss.rabbitMq.message.CustomerPojoMessage;
//import com.savbill.salescrmsbss.rabbitMq.message.EmailMessage;
//import com.savbill.salescrmsbss.rabbitMq.message.FollowUpMessage;
//import com.savbill.salescrmsbss.rabbitMq.message.LeadMasterPojoMessage;
//import com.savbill.salescrmsbss.rabbitMq.message.SendLeadStatusReq;
//import com.savbill.salescrmsbss.rabbitMq.message.SendSaveLeadData;
//import com.savbill.salescrmsbss.rabbitMq.message.SendUpdateLeadData;
//
//@Component
//public class MessageSender {
//	private static Log log = LogFactory.getLog(MessageSender.class);
//
//	@Autowired
//	private RabbitTemplate rabbitTemplate;
//
////	public String send(CustomerMessage message,String queueName)
////	{
////		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
////		log.info("Send msg  "+ message);
////		return "Message Published";
////	}
//
//	public String send(CustomerPojoMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(FollowUpMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	// @Transactional
//	public String send(SendSaveLeadData message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(SendUpdateLeadData message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(SendLeadStatusReq message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(SendLeadDocConvertPojo message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(LeadMasterPojoMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(EmailMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(SendLeadQuotationMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(QuickInvoicePojoMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//}
