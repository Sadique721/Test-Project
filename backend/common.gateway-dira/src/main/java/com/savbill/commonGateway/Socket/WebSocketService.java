package com.savbill.commonGateway.Socket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    private WebSocketService(final SimpMessagingTemplate simpMessagingTemplate){
        this.messagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(String destination , Object object){
        System.out.println("enter in common reciever : with message destination "+destination+" and payload "+object);
        log.info("message with destination :"+destination+" and payload : "+object);
        messagingTemplate.convertAndSend(destination , object);

    }
}
