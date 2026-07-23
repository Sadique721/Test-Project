package com.savbill.partnermanagement.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String COMBINED_GROUP="combined_group_partner_management";
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
    public static  final String KAFKA_CMS_GROUP_ID="partner-cms-group";

    public static  final String KAFKA_COMMON_GROUP_ID="partner-common-group";

    public static  final String KAFKA_INVENTORY_GROUP_ID="partner-inventory-group";

    public static  final String KAFKA_REVENUE_GROUP_ID="partner-revenue-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="partner-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="partner-radius-group";

    public static  final String KAFKA_TICKET_GROUP_ID="partner-ticket-group";

    public static  final String KAFKA_NETCONFIG_GROUP_ID="partner-netconfig-group";

    public static  final String KAFKA_INTEGRATION_GROUP_ID="partner-integration-group";

    public static  final String KAFKA_SALES_CRM_GROUP_ID="partner-sales-group";
    public static  final String KAFKA_SALSE_CRM_GROUP_ID="cms-sales-crm-group";

    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";


    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";

    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";

    public static final String CREATE_DATA_ROLE = "CREATE_DATA_ROLE";

    public static final String DELETE_DATA_ROLE = "DELETE_DATA_ROLE";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

    public static final String SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER="UPDATE_PARTNER_BALANCE";

}
