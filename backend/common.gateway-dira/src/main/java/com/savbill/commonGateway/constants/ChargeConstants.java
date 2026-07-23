package com.savbill.commonGateway.constants;

import lombok.Data;

@Data
public class ChargeConstants {

    public static String CHARGE_TYPE_RECURRING = "RECURRING";
    public static String CHARGE_TYPE_NON_RECURRING = "NON_RECURRING";
    public static String CHARGE_TYPE_ADVANCE = "ADVANCE";
    public static String CHARGE_TYPE_ADVANCE_RECURRING = "ADVANCE_RECURRING";
    public static String CHARGE_TYPE_REFUNDABLE = "REFUNDABLE";
    public static String CHARGE_TYPE_CUSTOMER_DIRECT = "CUSTOMER_DIRECT";
    public static String CHARGE_CATEGORY_INSTALLATION = "INSTALLATION";
    public static String CHARGE_CATEGORY_IP = "IP";
    public static String CHARGE_CATEGORY_TERMINATION = "TERMINATION";
    public static String CHARGE_CATEGORY_TRANSFER = "TRANSFER";
    public static String CHARGE_CATEGORY_PLAN = "PLAN";
    public static String CLIENT_SERVICE_LOCATION_ID = "locationid";


}
