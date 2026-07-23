package com.savbill.notification.savbilliwfnotification.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class KafkaUrlConfiguration {

    @Value("${kafka-url}")
    private String kafkaURL;
}