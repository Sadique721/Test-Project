package com.savbill.notification.utils;

import com.savbill.notification.kafka.KafkaMessageData;
import com.savbill.notification.kafka.KafkaMessageReceiver;
import com.savbill.notification.rabbitmq.message.CustomMessage;
import com.savbill.notification.services.impl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public  class ThreadPoolUtilityClass {

    static Executor customerSaveExecutor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("CUSTOMERSAVE-%d").build());

    @Async
    public void sacveCustomerProcess(KafkaMessageData messageData) {
        customerSaveExecutor.execute(new saveCustomerAsync(messageData));
    }

}

class saveCustomerAsync implements Runnable{

    KafkaMessageData kafkaMessageData;

    KafkaMessageReceiver kafkaMessageReceiver;



    public saveCustomerAsync(KafkaMessageData kafkaMessageData) {
        this.kafkaMessageData = kafkaMessageData;
    }

    @Override
    public void run() {
        try {
            CustomerServiceImpl customerService = ApplicationContextProvider.getApplicationContext().getBean(CustomerServiceImpl.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), CustomMessage.class);
            System.out.println(dataMessage);
            customerService.saveSubscriber(dataMessage);
        }catch (Exception e){
            e.printStackTrace();
        }    }
}
