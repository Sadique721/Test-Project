package com.savbill.revenuemanagement.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String COMBINED_GROUP="combined_group_revenue_management";

    public static final String KAFKA_COMMON_TOPIC="common";

    public static final String KAFKA_CMS_TOPIC="cms";

    public static final String KAFKA_CMS_CHANGE_PLAN_TOPIC="cms-change-plan";

    public static final String KAFKA_PMS_TOPIC="pms";

    public static final String KAFKA_INVENTORY_TOPIC="inventory";

    public static final String KAFKA_REVENUE_TOPIC="revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC="notification";

    public static final String KAFKA_RADIUS_TOPIC="radius";

    public static final String KAFKA_TICKET_TOPIC="ticket";
    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";




    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_CMS_GROUP_ID="revenue-cms-group";

    public static  final String KAFKA_CMS_CHANGE_PLAN_GROUP_ID="revenue-cms-change-plan-group";

    public static  final String KAFKA_PMS_GROUP_ID="revenue-pms-group";

    public static  final String KAFKA_INVENTORY_GROUP_ID="revenue-inventory-group";

    public static  final String KAFKA_COMMON_GROUP_ID="revenue-common-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="revenue-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="revenue-radius-group";

    public static  final String KAFKA_TICKET_GROUP_ID="revenue-ticket-group";


    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";
    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";
    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";
    public static final String SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER="UPDATE_PARTNER_BALANCE";

    public static final String KAFKA_INTEGRATION_TOPIC="integration";

    public static  final String KAFKA_INTEGRATION_GROUP_ID="revenue-integration-group";

    public static final String INVOICE_PAYMENT_PURCHASE = "INVOICE_PAYMENT_PURCHASE";

    public static final String SEND_QR = "SEND_QR";

    public static final String CREATE = "CREATE";

    public static final String UPDATE = "UPDATE";

    public static final String DELETE = "DELETE";

    public static final String ADD_WALLET = "ADD_WALLET";



}
