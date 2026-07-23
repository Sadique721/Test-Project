/*
package com.savbill.partnermanagement.rabbitmq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender 
{
	private static Log log = LogFactory.getLog(MessageSender.class);

//	@Autowired
//	private RabbitTemplate rabbitTemplate;

	public String send(CustomMessage message,String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}

}
*/
