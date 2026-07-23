/*
package com.savbill.ticketmanagement.rabbitmq;

import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Messages.ActivePlanListReqMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender 
{
	private static Log log = LogFactory.getLog(MessageSender.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public String send(CustomMessage message,String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}


	public String send(CustTicketCloseMsg message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketCreationMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(SendProblemDomainChangeMsg message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(CustTicketStatusMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketAssignMessege message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}
	public String send(TicketETRMsg message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketRescheduleMsg message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketMessageIntegration message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(WorkflowTicketMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketPickMessageToTeam message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(SendFollowUpRemarkMsg message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

	public String send(TicketTatReminderNotification message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}


	public String send(TicketFollowUpMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}


	public String send(CafFollowUpMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}


	public String send(CloseTicketCheckMessage message, String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}


	*/
/** Message Sender for Ticket Followup remark to customer started**//*

	public String send(TicketFollowupRemarkCustomerMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender for Ticket Followup remark to customer ended**//*



	*/
/** Message Sender for Ticket External remark to customer started**//*

	public String send(TicketExternalRemarkCustomerMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender for Ticket External remark to customer ended**//*


	*/
/** Message Sender for Ticket Alert to Staff started**//*

	public String send(TicketAlertStaffMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender for Ticket Alert to staff ended**//*


	*/
/** Message Sender Immediate Attention to staff  for unregistered started**//*

	public String send(ImmediateAttentionForUnRegisterCustomerMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender Immediate Attention to staff  for unregistered ended**//*


	*/
/** Message Sender Immediate Attention to customer  for registered started**//*

	public String send(ImmediateAttentionForRegisterCustomerMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender Immediate Attention to customer  for registered ended**//*


	*/
/** Message Sender Immediate Attention to staff  for Unregistered started**//*

	public String send(ImmediateAttentionForUnRegisterCustomerToStaffMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender Immediate Attention to staff  for Unregistered ended**//*


	*/
/** Message Sender for Unpick Ticket Alert to Staff started**//*

	public String send(UnPickTicketAlertStaffMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	*/
/**Message Sender for Unpick Ticket Alert to staff ended**//*


}
*/
