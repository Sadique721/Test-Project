package com.savbill.notification.kafka;

import com.savbill.notification.savbilliwfnotification.dto.NotificationResponseDTO;
import com.savbill.notification.savbilliwfnotification.util.KafkaUrlConfiguration;
import com.savbill.notification.utils.NotificationConstants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.BatchLoggingErrorHandler;
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

    @Autowired
    KafkaUrlConfiguration kafkaURLConfiguration;


    @Bean
    public KafkaAdmin adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaUrl);
        return new KafkaAdmin(configs);
    }
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
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 6000);
        //configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880"); // Increase fetch size for large messages
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 2621440); // Increase partition fetch size
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Increase partition fetch size
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,maxPollRecords);
        configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,fetchMaxBytes);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,fetchMinBytes);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        //factory.setConcurrency(100);
        factory.setConsumerFactory(kafkaConsumerFactory());
        //factory.setBatchListener(true);
        return factory;
    }


    //DARSHAN-CODE
    @Bean
    public ConsumerFactory<String, NotificationResponseDTO> emailDataFactory() {
        JsonDeserializer<NotificationResponseDTO> deserializer = new JsonDeserializer<>(NotificationResponseDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURLConfiguration.getKafkaURL());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, NotificationConstants.IWF_NOTIFICATION_GROUP);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 6000);
        //configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880"); // Increase fetch size for large messages
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 2621440); // Increase partition fetch size
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Increase partition fetch size
        return new DefaultKafkaConsumerFactory<String, NotificationResponseDTO>(config, new StringDeserializer(), deserializer);
    }


    //DARSHAN-CODE
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> consumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> containerFactory = new ConcurrentKafkaListenerContainerFactory();
        containerFactory.setConsumerFactory(emailDataFactory());
        containerFactory.setBatchListener(true);
        containerFactory.setBatchErrorHandler(new BatchLoggingErrorHandler());
        return containerFactory;
    }

    //DARSHAN-CODE
    @Bean("Email-Config")
    public NewTopic emailConfigTopic() {
        return TopicBuilder.name("NOTIFICATIONCOMMON")
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                .build();
    }

    @Bean
    public ConsumerFactory<String, Object> kafkaCommonConsumer() {
        Map<String, Object> configProps = new HashMap<>();

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class, false);
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
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 6000);
        //configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880"); // Increase fetch size for large messages
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 2621440); // Increase partition fetch size
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Increase partition fetch size
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> commonKafkaListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConcurrency(100);
        factory.setConsumerFactory(kafkaCommonConsumer());
        //factory.setBatchListener(true);
        return factory;
    }

    public Map<String, Object> packetDataPropsPrimary() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        config.put("spring.deserializer.value.delegate.class", JsonDeserializer.class.getName());
//        config.put("spring.json.trusted.packages", "*");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.KAFKA_NOTIFICATION_GROUP);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Reliability and performance-related configs
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // safer manual commit
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "600000");
        return config;
    }

}
