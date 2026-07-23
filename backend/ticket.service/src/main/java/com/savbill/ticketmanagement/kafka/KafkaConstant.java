package com.savbill.ticketmanagement.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String COMBINED_GROUP="combined_group_ticket_management";
    public static final String KAFKA_COMMON_TOPIC="common";

    public static final String KAFKA_CMS_TOPIC="cms";

    public static final String KAFKA_PMS_TOPIC="pms";

    public static final String KAFKA_INVENTORY_TOPIC="inventory";

    public static final String KAFKA_REVENUE_TOPIC="revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC="notification";

    public static final String KAFKA_RADIUS_TOPIC="radius";

    public static final String KAFKA_TICKET_TOPIC="ticket";
    public static final String KAFKA_TASK_TOPIC="task";



    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_COMMON_GROUP_ID="ticket-common-group";

    public static  final String KAFKA_CMS_GROUP_ID="ticket-cms-group";

    public static  final String KAFKA_TASK_GROUP_ID="ticket-task-group";

    public static  final String KAFKA_PMS_GROUP_ID="ticket-pms-group";

    public static  final String KAFKA_INVENTORY_GROUP_ID="ticket-inventory-group";

    public static  final String KAFKA_REVENUE_GROUP_ID="ticket-revenue-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="ticket-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="ticket-radius-group";

    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";

    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";
    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";

    public static final String SEND_FOLLOWUP_REMARK_MSG="SEND_FOLLOWUP_REMARK_MSG";

    public static final String SEND_PROBLEM_DOMAIN_CHANGE_MSG="SEND_PROBLEM_DOMAIN_CHANGE_MSG";

    public static final String TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF="TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF";

    public  static final String TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER="TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER";

    public static final String TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF="TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF";

    public static final String TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF";

    public static final String TICKET_TAT_SUCCESS_MESSAGE="TICKET_TAT_SUCCESS_MESSAGE";

    public static final String TICKET_TAT_AUDIT_SUCCESS="TICKET_TAT_AUDIT_SUCCESS";

    public static final String TICKET_ETR_AUDIT_SUCCESS = "TICKET_ETR_AUDIT_SUCCESS";
    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

    public static  final String TICKET_ASSIGN_TEAM_SUCCESS = "TICKET_ASSIGN_TEAM_SUCCESS";

    public static  final String SEND_EXTERNAL_REMARK_FOR_TICKET = "SEND_EXTERNAL_REMARK_FOR_TICKET";

    public static  final String SEND_EXTERNAL_REMARK_FOR_TASK = "SEND_EXTERNAL_REMARK_FOR_TASK";


    public static final String SEND_TICKET_ALERT_REMARK= "SEND_TICKET_ALERT_REMARK";

    public static final String TASK_CLOSE_MESSAGE = "TASK_CLOSE_MESSAGE";
    public static String EXTERNAL_TICKET_CLOSE = "EXTERNAL_TICKET_CLOSE";

}
