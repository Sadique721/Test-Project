package com.savbill.radius.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String KAFKA_COMMON_TOPIC = "common";

    public static final String KAFKA_CMS_TOPIC = "cms";

    public static final String KAFKA_PMS_TOPIC = "pms";

    public static final String KAFKA_INVENTORY_TOPIC = "inventory";

    public static final String KAFKA_REVENUE_TOPIC = "revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC = "notification";

    public static final String KAFKA_RADIUS_TOPIC = "radius";

    public static final String KAFKA_TICKET_TOPIC = "ticket";

    public static final String KAFKA_NETCONFIG_TOPIC = "netconfig";


    //KAFKA-GROUP-ID'S
    public static final String KAFKA_COMMON_GROUP_ID = "radius-common-group";

    public static final String KAFKA_PMS_GROUP_ID = "radius-pms-group";

    public static final String KAFKA_INVENTORY_GROUP_ID = "radius-inventory-group";

    public static final String KAFKA_REVENUE_GROUP_ID = "radius-revenue-group";

    public static final String KAFKA_NOTIFICATION_GROUP_ID = "radius-notification-group";

    public static final String KAFKA_CMS_GROUP_ID = "cms-cms-group";

    public static final String KAFKA_TICKET_GROUP_ID = "radius-ticket-group";

    public static final String KAFKA_NETCONFIG_GROUP_ID = "radius-netconfig-group";


    //For Event types

    public static final String UPDATE_CONCURRENCY = "UPDATE_CONCURRENCY";

    public static final String CUSTOMER_ENDDATE = "CUSTOMER_ENDDATE";

    public static final String SEND_QUOTA = "SEND_QUOTA";
    public static final String SEND_QUOTA_RESET = "SEND_QUOTA_RESET";

    public static final String QUOTA_INTRIM = "QUOTA_INTRIM";

    public static final String CUSTOMERS_UPDATE_RESERVED_QUOTA = "CUSTOMERS_UPDATE_RESERVED_QUOTA";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA = "SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";


}
