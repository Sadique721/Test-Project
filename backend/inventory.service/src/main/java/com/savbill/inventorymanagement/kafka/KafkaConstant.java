package com.savbill.inventorymanagement.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String COMBINED_GROUP="combined_group_inventory_management";

    public static final String KAFKA_COMMON_TOPIC="common";

    public static final String KAFKA_CMS_TOPIC="cms";

    public static final String KAFKA_PMS_TOPIC="pms";

    public static final String KAFKA_INVENTORY_TOPIC="inventory";

    public static final String KAFKA_REVENUE_TOPIC="revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC="notification";

    public static final String KAFKA_RADIUS_TOPIC="radius";

    public static final String KAFKA_TICKET_TOPIC="ticket";

    public static final String KAFKA_SALES_CRM_TOPIC="sales";

    public static final String KAFKA_NETCONFIG_TOPIC="netconfig";

    public static final String KAFKA_INTEGRATION_TOPIC="integration";




    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_CMS_GROUP_ID="inventory-cms-group";

    public static  final String KAFKA_PMS_GROUP_ID="inventory-pms-group";

    public static  final String KAFKA_COMMON_GROUP_ID="inventory-common-group";

    public static  final String KAFKA_REVENUE_GROUP_ID="inventory-revenue-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="inventory-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="inventory-radius-group";

    public static  final String KAFKA_TICKET_GROUP_ID="inventory-ticket-group";

    public static  final String KAFKA_NETCONFIG_GROUP_ID="inventory-netconfig-group";

    public static  final String KAFKA_INTEGRATION_GROUP_ID="inventory-integration-group";

    public static  final String KAFKA_SALES_CRM_GROUP_ID="inventory-salescrm-group";

    //Kafka Event-type

    public static final String SAVE_VENDOR="SAVE_VENDOR";

    public static final String UPDATE_VENDOR="UPDATE_VENDOR";

    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";

    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";

    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";

    public static final String CREATE_NEW_CHARGE = "CREATE_NEW_CHARGE";

    public static final String UPDATE_NEW_CHARGE = "UPDATE_NEW_CHARGE";

    public static final String CREATE_REF_CHARGE = "CREATE_REF_CHARGE";

    public static final String UPDATE_REF_CHARGE = "UPDATE_REF_CHARGE";

    public static final String CREATE_DATA_ROLE = "CREATE_DATA_ROLE";

    public static final String DELETE_DATA_ROLE = "DELETE_DATA_ROLE";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

}
