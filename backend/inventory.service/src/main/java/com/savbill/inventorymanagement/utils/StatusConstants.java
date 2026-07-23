package com.savbill.inventorymanagement.utils;

public class StatusConstants {

    public interface CUSTOMER_STATUS {
        String INAVCTIVE = "InActive";
        String TERMINATE = "Terminate";
        String SUSPEND = "Suspend";
        String ACTIVE = "Active";
        String HOLD = "Hold";
        String INGRACE = "InGrace";
    }

    public interface CUSTOMER_SERVICE_STATUS {
        String ACTIVE = "Active";
        String NEWACTIVATION = "NewActivation";
        String INAVCTIVE = "InActive";
        String TERMINATE = "Terminate";
        String SUSPEND = "Suspend";
        String HOLD = "Hold";
        String INGRACE = "InGrace";
        String STOP = "STOP";
        String DISABLE = "Disable";
        String RESUME = "Resume";
        String EXPIRED = "Expired";
        String FUTURE = "Future";
    }

    public interface INVOICE_TYPE {
        String GROUP = "Group";
        String INDEPENDENT = "Independent";
    }

    public interface INVOICE_STATUS{
        String UNPAID = "UnPaid";
        String CANCELLED = "Cancelled";
        String CLEAR = "Clear";
        String FULLYPAID = "Fully Paid";
        String VOID = "VOID";
    }
}
