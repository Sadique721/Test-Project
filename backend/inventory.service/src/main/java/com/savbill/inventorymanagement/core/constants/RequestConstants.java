package com.savbill.inventorymanagement.core.constants;

public class RequestConstants {

    public interface STATUS {
        public static final String APPROVE = "Approve";
        public static final String REJECTED = "Rejected";
    }

    public interface REQUEST_STATUS {
        public static final String WAITING_FOR_APPROVAL = "Waiting for Approval";
        public static final String REJECTED = "Rejected";
        public static final String IN_PROGRESS = "In-Progress";
        public static final String OPEN = "Open";
        public static final String CLOSE = "Close";
        public static final String INPROGRESS = "In Progress";
        public static final String COMPLETED = "Completed";
        public static final String PARTIALLY_COMPLETED = "Partially Completed";
    }

    public interface ON_BE_HALF {
        public static final String WAREHOUSE = "WareHouse";
        public static final String POP = "Pop";
        public static final String SERVICEAREA = "ServiceArea";
        public static final String STAFFUSER = "StaffUser";
    }

    public interface FAIL_MESSAGE {
        public static final String THIRED_PARTY_TO_THIRED_PARTY_REQUEST = "3rd party to 3rd party warehouse inventory request not allowed.";
        public static final String DUPLICATE_PRODUCT_SELECTION = "Duplicate product is not allow";
        public static final String THIRED_PARTY_TO_THIRED_PARTY_FORWARD = "3rd party to 3rd party warehouseforward is not allowed.";
        public static final String WAREHOUSE_DIFFERENT_SELECTION = "WareHouses should be different";
        public static final String DELETE_REQUEST = "This request is deleted please refresh the page";
    }
}
