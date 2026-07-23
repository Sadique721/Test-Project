package com.savbill.commonGateway.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String KAFKA_COMMON_TOPIC="common";

    public static final String KAFKA_CMS_TOPIC="cms";

    public static final String KAFKA_PMS_TOPIC="pms";

    public static final String KAFKA_INVENTORY_TOPIC="inventory";

    public static final String KAFKA_REVENUE_TOPIC="revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC="notification";

    public static final String KAFKA_RADIUS_TOPIC="radius";

    public static final String KAFKA_TICKET_TOPIC="ticket";
    public static final String KAFKA_TASK_TOPIC="task";
    public static final String KAFKA_INTEGRATION_TOPIC="integration";
    public static final String KAFKA_SALES_CRM_TOPIC="sales";
    public static final String CMS_CHANGE_PLAN="cms-change-plan";
    public static final String SALES_CRM="salescrm";
    public static final String NETCONFIG="netconfig";
    public static final String NOTIFICATIONCOMMON="NOTIFICATIONCOMMON";






    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_CMS_GROUP_ID="common-cms-group";

    public static  final String KAFKA_PMS_GROUP_ID="common-pms-group";

    public static  final String KAFKA_INVENTORY_GROUP_ID="common-inventory-group";

    public static  final String KAFKA_REVENUE_GROUP_ID="common-revenue-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="common-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="common-radius-group";

    public static  final String KAFKA_TICKET_GROUP_ID="common-ticket-group";

    public static  final String KAFKA_SALESCRM_GROUP_ID="common-salescrm-group";

    public static final String UPDATE_STAFF_STATUS ="STAFF_STATUS_DUNNING";
    public static final String CUST_DEACTIVATION_WHEN_MVNO_IS_INACTIVE = "cust-deactivatation-when-mvno-is-inactive";
    public static final String UPDATE_MVNO_STATUS = "MVNO_STATUS_DUNNING";

    public static final String IPS_TO_ISP = "IPS_TO_ISP";

    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";
    public static final String OPT_FOR_LOGIN_2FA="OPT_FOR_LOGIN_2FA";

    public static final String CREATE_DATA_ROLE = "CREATE_DATA_ROLE";

    public static final String DELETE_DATA_ROLE = "DELETE_DATA_ROLE";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";

    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";

    public static final String OPT_PROFILE_SAVE="OPT_PROFILE_SAVE";

    public static final String OPT_PROFILE_UPDATE="OPT_PROFILE_UPDATE";

    public static final String BUILDING_MGMT_SAVE = "BUILDING_MGMT_SAVE";

    public static final String BUILDING_MGMT_UPDATE = "BUILDING_MGMT_UPDATE";

}
