package com.savbill.integrationsystem.CRDB.Constants;

public interface CRDBConstant {

    String CRDB_PAYMENT_GATEWAY_NAME = "CRDB Bank";
    String CRDB_FLOW                 = "CRDB_FLOW";
    int STATUS_SUCCESS                    = 200;
    int STATUS_INVALID_TOKEN              = 201;
    int STATUS_INVALID_CHECKSUM           = 202;
    int STATUS_ALREADY_PAID               = 203;
    int STATUS_INVALID_REFERENCE         = 204;
    int STATUS_REFERENCE_EXPIRED          = 205;
    int STATUS_DUPLICATE                  = 206;
    int STATUS_TRANSACTION_ALREADY_PAID   = 207;
    int STATUS_INTERNAL_ERROR             = 500;
    String STATUS_SUCCESS_DESC                  = "success";
    String STATUS_INVALID_TOKEN_DESC            = "Invalid Token";
    String STATUS_INVALID_CHECKSUM_DESC         = "Invalid checksum";
    String STATUS_ALREADY_PAID_DESC             = "Payment reference number already paid";
    String STATUS_INVALID_REFERENCE_DESC        = "Invalid payment reference number";
    String STATUS_REFERENCE_EXPIRED_DESC        = "Payment reference number has expired";
    String STATUS_DUPLICATE_DESC                = "Duplicate entry";
    String STATUS_TRANSACTION_ALREADY_PAID_DESC = "Transaction reference number already paid";
    String STATUS_INTERNAL_ERROR_DESC           = "Internal server error";
    String AMOUNT_TYPE_FIXED    = "FIXED";
    String AMOUNT_TYPE_FLEXIBLE = "FLEXIBLE";
    String AMOUNT_TYPE_FULL     = "FULL";
    String CRDB_DEFAULT_CURRENCY     = "TZS";
    String DEFAULT_PAYMENT_DESC =  "FTTH Service";
    String DEFAULT_PAYMENT_TYPE = "90";
    String DEFAULT_ZERO_AMOUNT = "0";
    String INITIATE    = "Initiate";
    String SUCCESSFUL  = "Successful";
//    String INSTITUTION_ID = "INSTITUTION_ID";
//    String AMOUNT_TYPE = "AMOUNT_TYPE";
//    String PAYMENT_TYPE = "PAYMENT_TYPE";
//    String PAYMENT_DESC = "PAYMENT_DESC";
//    String TRANSACTION_CHANNEL = "TRANSACTION_CHANNEL";
    String CRDB_INSTITUTION_ID = "CRDB_INSTITUTION_ID";
    String CRDB_AMOUNT_TYPE = "CRDB_AMOUNT_TYPE";
    String CRDB_PAYMENT_TYPE = "CRDB_PAYMENT_TYPE";
    String CRDB_PAYMENT_DESC = "CRDB_PAYMENT_DESC";
    String CRDB_TRANSACTION_CHANNEL = "CRDB_TRANSACTION_CHANNEL";
    /**
     * Prefix used as a temporary pgTransactionId placeholder during the
     * Verification step (before the bank's transactionRef is known).
     * Pattern:  CRDB_PENDING_<accountNumber>
     */
    String PENDING_TX_PREFIX = "CRDB_PENDING_";

}
