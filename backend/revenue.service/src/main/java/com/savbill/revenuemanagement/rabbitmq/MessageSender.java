//package com.savbill.revenuemanagement.rabbitmq;
//
//import com.savbill.revenuemanagement.core.dto.invoice.UpdateInvoiceNumberMessage;
//import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceCharges;
//import com.savbill.revenuemanagement.core.service.prepaid.UpdateCprMessage;
//import com.savbill.revenuemanagement.rabbitmq.messages.*;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.CustomerPackageRelMessage;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.OrganizationInvoiceRejectMesssage;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.PartnerAmountMessage;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDebitDocMessage;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDocIdsMessages;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ListOfCreditDocForBatch;
//import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateCustplanMappingMessage;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageSender
//{
//	private static Log log = LogFactory.getLog(MessageSender.class);
//
//	@Autowired
//	private RabbitTemplate rabbitTemplate;
//
//	public String send(	CustomMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//	public String send(PrepaidInvoiceCharges message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		//log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//	public String send(CustomerPackageRelMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CreditDocMessageList message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(VoidInvoiceMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(PartnerAmountMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(OrganizationInvoiceRejectMesssage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CustomerInvoiceMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		//log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//
//	public String send(Integer message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE,queueName,message);
//		log.info("Send msg" + message);
//		return "Message Published";
//	}
//	public String send(PostPaidTrailInvoiceMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(UpdateCustplanMappingMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(CreditDocIdsMessages message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(ListOfCreditDocForBatch message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//	public String send(CreditDebitDocMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(BudPayPaymentMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(CustPlanMappingStatusMessage message, String queueName) {
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		log.info("Send msg  " + message);
//		return "Message Published";
//	}
//
//	public String send(UpdateCprMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		//log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//
//	public String send(UpdateInvoiceNumberMessage message, String queueName)
//	{
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		//log.info("Send msg  "+ message);
//		return "Message Published";
//	}
//
//}
