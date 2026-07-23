package com.savbill.commonGateway.rabbitmq;

import com.savbill.commonGateway.rabbitmq.messages.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageSender 
{
	private static Log log = LogFactory.getLog(MessageSender.class);

	/*@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public String send(CustomMessage message,String queueName)
	{
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  "+ message);
		return "Message Published";
	}
	public String send(TeamsMessage message, String queueName, String otherQueueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, otherQueueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(UserMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	public String send(StaffStatusChangeMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg " + message);
		return "Message Published";
	}
	public String send(List<UserMessage> message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}


	public String send(CountryMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(StateMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}
	public String send(StaffUserMessage message, String queueName) {

		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	};
	public String send(CityMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(MvnoMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg " + message);
		return "Message Published";
	}

	public String send(RoleMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(PincodeMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(AreaMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(BranchMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(BranchMessage message, String queueName,String otherQueueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, otherQueueName, message);
		log.info("Send msg " + message);
		return "Message Published";

	}

	public String send(BranchMessageIn message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg " + message);
		return "Message Published";

	}

	public String send(BusinessUnitMessage message, String queueName, String otherQueueName,String thirdQueueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, otherQueueName, message);
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, thirdQueueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(BusinessUnitMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(ServiceareaMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg " + message);
		return "Message Published";

	}

	public String send(ServiceAreaMesseage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, otherQueueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(ServiceAreaIn message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(CommonRoleMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(PaymentConfigMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(MvnoDocDetailsDTO message, String queueName) {
		System.out.println("Send Message");
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(SystemConfigurationMessage message, String queueName) {
		System.out.println("Send Message");
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String sendMapping(List<ServiceareaLocationMappingMessage> message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(OtpMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	public String send(OTPProfileMessage message, String queueName) {
		rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
		log.info("Send msg  " + message);
		return "Message Published";
	}

	}*/

}
