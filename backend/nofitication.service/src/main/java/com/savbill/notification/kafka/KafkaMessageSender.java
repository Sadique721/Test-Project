package com.savbill.notification.kafka;

import com.savbill.notification.utils.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageSender {
    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    KafkaProducer kafkaProducer;
    //private static Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

//    public String send(KafkaMessageData message) {
//        try {
//            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_NOTIFICATION_TOPIC, message);
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
            ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_NOTIFICATION_TOPIC,KafkaConstant.KAFKA_NOTIFICATION_TOPIC+1 ,message);

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
