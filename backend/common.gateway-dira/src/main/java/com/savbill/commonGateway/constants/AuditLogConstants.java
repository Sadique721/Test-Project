package com.savbill.commonGateway.constants;

import lombok.Data;

@Data
public class AuditLogConstants {

    public static final String AUDIT_FOR_EMPLOYEE = "employee";
    public static final String AUDIT_FOR_PARTNER = "partner";
    public static final String AUDIT_FOR_CUSTOMER = "customer";
    public static final String AUDIT_FOR_PAYMENT_GATEWAY = "paymentgateway";
    public static final String OPERATION_INSERT = "insert";
    public static final String OPERATION_DELETE  = "delete";
    public static final String OPERATION_VIEW  = "view";
    public static final String OPERATION_UPDATE  = "update";

}
