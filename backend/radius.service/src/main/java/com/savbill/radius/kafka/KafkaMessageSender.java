package com.savbill.radius.kafka;

import com.savbill.radius.utils.ApplicationContextProvider;
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
    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    KafkaProducer kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger("savbillradiusqueue");

//    public String send(KafkaMessageData message) {
//        try {
//            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_RADIUS_TOPIC, message);
//            kafkaProducerConfig.kafkaProducer().send(record);
//            log.debug("Data send successfully on topic with message : " + message);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("error:{}");
//        }
//        log.debug("Send msg  " + message);
//        return "Message Published";
//    }


    public String send(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(KafkaConstant.KAFKA_RADIUS_TOPIC, KafkaConstant.KAFKA_RADIUS_TOPIC + 2, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        log.debug("::::::::::Data send successfully on topic :::::::::: " + metadata.topic() + " With partition : " + metadata.partition() + "Number of offset : " + metadata.offset());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }
}
