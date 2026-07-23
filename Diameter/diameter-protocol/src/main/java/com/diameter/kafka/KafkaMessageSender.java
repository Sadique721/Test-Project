package com.diameter.kafka;

import com.diameter.util.ApplicationContextProvider;
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
    private static Logger log = LoggerFactory.getLogger("savbillcustomerqueue");


    public String send(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(KafkaConstant.KAFKA_DIAMETER_TOPIC, KafkaConstant.KAFKA_DIAMETER_TOPIC + 1, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        log.error("Kafka send failed for topic {}", KafkaConstant.KAFKA_DIAMETER_TOPIC, exception);
                    }
                }
            });

        } catch (Exception e) {
            log.error("Error publishing message to Kafka", e);
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }
}
