package com.savbill.commonGateway.kafka.service;

import com.savbill.commonGateway.kafka.KafkaConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicCreation {

    @Value("${topicPartition}")
    private String topicPartition;

    @Value("${replicationFactor}")
    private String replicationFactor;

    @Autowired
    private KafkaAdmin adminClient;



    public void createTopic(String topic){
        NewTopic newTopic = TopicBuilder.name(topic).partitions(Integer.parseInt(topicPartition)).replicas(Integer.parseInt(replicationFactor)).config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT).build();
        adminClient.createOrModifyTopics(newTopic);
        System.out.println("*************Kafka Topic Created : "+topic+" ****************");
    }

    public void createKafkaTopics(){
        createTopic(KafkaConstant.KAFKA_CMS_TOPIC); //CMS
        createTopic(KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA); // CMS CUSTOMER TOPIC
        createTopic(KafkaConstant.KAFKA_RADIUS_TOPIC); //RADIUS
        createTopic(KafkaConstant.KAFKA_NOTIFICATION_TOPIC); //NOTIFICATION
        createTopic(KafkaConstant.KAFKA_PMS_TOPIC); //PARTNER
        createTopic(KafkaConstant.KAFKA_TICKET_TOPIC); //TICKET
        createTopic(KafkaConstant.KAFKA_INVENTORY_TOPIC); //INVENTORY
        createTopic(KafkaConstant.KAFKA_REVENUE_TOPIC); //REVENUE
        createTopic(KafkaConstant.KAFKA_TASK_TOPIC); //TASK
        createTopic(KafkaConstant.KAFKA_INTEGRATION_TOPIC); //INTEGRATION
        createTopic(KafkaConstant.KAFKA_SALES_CRM_TOPIC); // SALSECRM
        createTopic(KafkaConstant.CMS_CHANGE_PLAN); // SALSECRM
        createTopic(KafkaConstant.SALES_CRM); // SALSECRM
        createTopic(KafkaConstant.NETCONFIG); // SALSECRM
        createTopic(KafkaConstant.NOTIFICATIONCOMMON); // SALSECRM
        createTopic(KafkaConstant.KAFKA_COMMON_TOPIC); //COMMON
    }
}
