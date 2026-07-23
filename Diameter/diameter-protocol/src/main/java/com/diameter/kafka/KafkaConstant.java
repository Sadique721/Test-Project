package com.diameter.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static  final String COMBINED_GROUP="combined_diamater_group";
    public static final String KAFKA_DIAMETER_TOPIC = "diameter";
    public static final String KAFKA_CMS_TOPIC="cms";
    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";
    public static final String UPDATE_SERVICE_STATUS_DIAMETER= "update_service_status_diameter";

    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_COMMON_GROUP_ID="cms-common-group";

    //Kafka Event-Types
    public static final String OPT_FOR_PORTAL="OPT_FOR_PORTAL";

    public static final String KAFKA_RADIUSA_TOPIC = "radiusA";

    //ocs
    public static final String ZONE_SAVE = "ZONE_SAVE";
    public static final String ZONE_UPDATE = "ZONE_UPDATE";
    public static final String ZONE_DELETE = "ZONE_DELETE";

    public static final String PULSE_SAVE = "PULSE_SAVE";
    public static final String PULSE_UPDATE = "PULSE_UPDATE";
    public static final String PULSE_DELETE = "PULSE_DELETE";

    public static final String RATE_PACKAGE_SAVE = "RATE_PACKAGE_SAVE";
    public static final String RATE_PACKAGE_UPDATE = "RATE_PACKAGE_UPDATE";
    public static final String RATE_PACKAGE_DELETE = "RATE_PACKAGE_DELETE";

    public static final String RATE_PACKAGE_GROUP_SAVE = "RATE_PACKAGE_GROUP_SAVE";
    public static final String RATE_PACKAGE_GROUP_UPDATE = "RATE_PACKAGE_GROUP_UPDATE";
    public static final String RATE_PACKAGE_GROUP_DELETE = "RATE_PACKAGE_GROUP_DELETE";

    public static final String RATE_PACKAGE_GROUP_MAPPING_SAVE = "RATE_PACKAGE_GROUP_MAPPING_SAVE";
    public static final String RATE_PACKAGE_GROUP_MAPPING_UPDATE = "RATE_PACKAGE_GROUP_MAPPING_UPDATE";
    public static final String RATE_PACKAGE_GROUP_MAPPING_DELETE = "RATE_PACKAGE_GROUP_MAPPING_DELETE";
    public static final String KAFKA_CMS_CHANGE_PLAN_TOPIC="cms-change-plan";
    public static final String KAFKA_INVENTORY_TOPIC="inventory";
}
