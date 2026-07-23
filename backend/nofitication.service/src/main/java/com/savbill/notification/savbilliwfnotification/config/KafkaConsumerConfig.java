//package com.savbill.notification.savbilliwfnotification.config;
//import com.savbill.notification.savbilliwfnotification.dto.NotificationResponseDTO;
//import com.savbill.notification.savbilliwfnotification.util.KafkaUrlConfiguration;
//import com.savbill.notification.utils.NotificationConstants;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.config.TopicConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.BatchLoggingErrorHandler;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
////@Profile("kafka")
//public class KafkaConsumerConfig {
//
//    @Autowired
//    KafkaUrlConfiguration kafkaURLConfiguration;
//
//
////    @Bean
////    public ConsumerFactory<String, NotificationResponseDTO> emailDataFactory() {
////        JsonDeserializer<NotificationResponseDTO> deserializer = new JsonDeserializer<>(NotificationResponseDTO.class);
////        deserializer.setRemoveTypeHeaders(false);
////        deserializer.addTrustedPackages("*");
////        deserializer.setUseTypeMapperForKey(true);
////
////        Map<String, Object> config = new HashMap<>();
////        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURLConfiguration.getKafkaURL());
////        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
////        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
////        config.put(ConsumerConfig.GROUP_ID_CONFIG, NotificationConstants.IWF_NOTIFICATION_GROUP);
////        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
////        return new DefaultKafkaConsumerFactory<String, NotificationResponseDTO>(config, new StringDeserializer(), deserializer);
////    }
////
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> consumerFactory() {
////        ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> containerFactory = new ConcurrentKafkaListenerContainerFactory();
////        containerFactory.setConsumerFactory(emailDataFactory());
////        containerFactory.setBatchListener(true);
////        containerFactory.setBatchErrorHandler(new BatchLoggingErrorHandler());
////        return containerFactory;
////    }
////
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> kafkaListenerContainerFactory() {
////        ConcurrentKafkaListenerContainerFactory<String, NotificationResponseDTO> factory =
////                new ConcurrentKafkaListenerContainerFactory<>();
////        factory.setConsumerFactory(consumerFactory().getConsumerFactory());
////
////        return factory;
////    }
////
////    @Bean("Email-Config")
////    public NewTopic emailConfigTopic() {
////        return TopicBuilder.name("NOTIFICATIONCOMMON")
////                .partitions(1)
////                .replicas(1)
////                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
////                .build();
////    }
////
////    @Bean
////    public ConsumerFactory<String, String> emailALtDataFactory() {
////        Map<String, Object> config = new HashMap<>();
////        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURLConfiguration.getKafkaURL());
////        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
////        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
////        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, NotificationConstants.BOOL_TRUE_AS_STR);
////        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.MAX_VALUE);
////
////        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer());
////    }
//
//     // COMMON CONSUMER CONFIG
//     @Bean
//     public ConsumerFactory<String, Object> kafkaConsumerFactory() {
//         Map<String, Object> configProps = new HashMap<>();
//         configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaURLConfiguration.getKafkaURL());
//         configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(Object.class));
//     }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(kafkaConsumerFactory());
//        return factory;
//    }
//
//
//}
