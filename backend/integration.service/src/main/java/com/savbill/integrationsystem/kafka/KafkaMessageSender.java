package com.savbill.integrationsystem.kafka;

import com.savbill.integrationsystem.utility.ApplicationContextProvider;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSender {

    KafkaProducer kafkaProducer;
    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    private static Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

//    public String send(KafkaMessageData message) {
//        try {
//            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_INTEGRATION_TOPIC, message);
//            kafkaProducerConfig.kafkaProducer().send(record);
//            System.out.println("Data send successfully on topic with message : " + message);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("error:{}");
//        }
//        log.info("Send msg  " + message);
//        return "Message Published";
//    }


    public String send(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_INTEGRATION_TOPIC, KafkaConstant.KAFKA_INTEGRATION_TOPIC + 1, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.info("Send msg  " + message);
        return "Message Published";
    }

}
