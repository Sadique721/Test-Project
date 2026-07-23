package com.savbill.integrationsystem.dialShreeIntegration;

import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;

@Service
public class CustCallLogsService {

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    private static final Logger logger = LoggerFactory.getLogger(CustCallLogsService.class);


    public void sendCallLogDataToCMS(CustCallLogsDTO dialShreeDTO) {
        try {
            logger.info("::::::::::::::::::::Inside Send Call Log Data to CMS:::::::::::::::::");
            kafkaMessageSender.send(new KafkaMessageData(dialShreeDTO, dialShreeDTO.getClass().getSimpleName()));
            logger.info("::::::::::::::::::::Send Call Log Data to CMS through Kafka:::::::::::::::::");
        } catch (KafkaException e) {
            logger.error("Kafka-related error while sending Call Log Data to CMS:{} ",  e.getMessage());
            logger.error(e.getStackTrace().toString());
        } catch (Exception e) {
            logger.error("Exception While Sending data to CMS: {}",  e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
