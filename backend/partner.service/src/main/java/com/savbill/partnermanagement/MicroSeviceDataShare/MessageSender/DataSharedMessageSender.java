//package com.savbill.partnermanagement.MicroSeviceDataShare.MessageSender;
//
//import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.SavePartnerSharedDataMessage;
//import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.UpdatePartnerSharedDataMessage;
//import com.savbill.partnermanagement.rabbitmq.RabbitMqConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataSharedMessageSender {
//
//    private static Logger log = LoggerFactory.getLogger(DataSharedMessageSender.class);
//
////    @Autowired
////    private RabbitTemplate rabbitTemplate;
//
//
//    //Send Saved Entity Data
//    public String send(SavePartnerSharedDataMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//    public String send(UpdatePartnerSharedDataMessage message, String queueName) {
//        rabbitTemplate.convertAndSend(RabbitMqConstants.SAVBILL_EXCHANGE, queueName, message);
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }
//
//
//}
