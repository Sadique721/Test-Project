package com.savbill.cpm.Socket;

import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.SendSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSocketService {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    public void SendMessageToCommonForSocket(Object object , String url){
        SendSocketMessage sendSocketMessage = new SendSocketMessage();
        sendSocketMessage.setUrl(url);
        sendSocketMessage.setObject(object);
        //messageSender.send(sendSocketMessage , RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON);
        kafkaMessageSender.send(new KafkaMessageData(sendSocketMessage,sendSocketMessage.getClass().getSimpleName()));
    }

}
