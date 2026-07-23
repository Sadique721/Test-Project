package com.savbill.notification.Schedular;

public class CronjobConst {


    public static final String TIME_FOR_SMS_NOTIFICATION = "cronjobtimeforsmsnotification";



    public interface SCHEDULER_AUDIT {
        public static final String SCHEDULER_STATUS_SUCCESS = "success";

        public static final String SCHEDULER_STATUS_LOCKED = "locked";

        public static final String SCHEDULER_STATUS_FAILURE = "failure";

        public static final String TIME_FOR_SMS_NOTIFICATION_SCHEDULER = "SMS Notification Scheduler";
    }
}
