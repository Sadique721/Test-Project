package com.savbill.commonGateway.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka-url}")
    private String kafkaUrl;

    @Value(value = "${max.poll.records}")
    private String maxPollRecords;

    @Value(value = "${fetch.max.bytes}")
    private String fetchMaxBytes;
    @Value(value = "${fetch.min.bytes}")
    private String fetchMinBytes;




    @Bean
    public ConsumerFactory<String, KafkaMessageData> kafkaConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer.getClass());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,maxPollRecords);
        configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,fetchMaxBytes);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,fetchMinBytes);
        configProps.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 6000);
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 2621440); // Increase partition fetch size
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Increase partition fetch size
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConcurrency(3);
        factory.setConsumerFactory(kafkaConsumerFactory());
        //factory.setBatchListener(true);
        return factory;
    }

}
