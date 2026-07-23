//package com.savbill.notification.rabbitmq;
//
//import com.savbill.notification.rabbitmq.message.EmailConfigSendToAPIGWMsg;
//import com.savbill.notification.rabbitmq.message.TicketAuditMessage;
//import com.savbill.notification.rabbitmq.message.TicketETRAuditMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageSender {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    private static Logger log = LoggerFactory.getLogger(MessageSender.class);
//
//    @Profile("rabbitmq")
//    public String send(TicketETRAuditMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//       // log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    @Profile("rabbitmq")
//    public String send(TicketAuditMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//      //  log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//    @Profile("rabbitmq")
//    public String send(EmailConfigSendToAPIGWMsg message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//      //  log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//
//
//}
