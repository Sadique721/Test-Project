package com.savbill.salescrmsbss.kafka;

import com.savbill.salescrmsbss.utils.ApplicationContextProvider;
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
    private static Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    public String send(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_SALES_CRM_TOPIC,KafkaConstant.KAFKA_SALES_CRM_TOPIC+1 ,message);

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
