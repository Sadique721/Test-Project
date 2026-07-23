package com.savbill.ticketmanagement.kafka;

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
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(3);
        factory.setConsumerFactory(kafkaConsumerFactory());
        //factory.setBatchListener(true);
        return factory;
    }

    /**
     * Builds and returns a configuration map for a Kafka consumer, specifically tailored
     * to handle `KafkaMessageData` with custom deserialization settings.
     *
     * The configuration includes the Kafka bootstrap servers, deserializers, consumer group,
     * offset reset strategy, and various poll and fetch settings. This configuration is
     * intended for consumers that will deserialize `KafkaMessageData` from Kafka messages.
     *
     * @return a map of configuration properties for the Kafka consumer
     */
    public Map<String, Object> packetDataPropsPrimary() {
        Map<String, Object> config = new HashMap<>();

        // Create and configure the deserializer explicitly
        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer.getClass());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.COMBINED_GROUP);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer).getConfigurationProperties();
    }
}
