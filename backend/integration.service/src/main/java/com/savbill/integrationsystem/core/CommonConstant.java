package com.savbill.integrationsystem.core;

public class CommonConstant {

    public static final String IRD_SYNCH_NO = "No";
    public static final String IRD_SYNCH_YES = "Yes";
    public static final String CREDITDOC_TYPE_CREDIT_NOTE = "creditnote";
    public static final String TAX_NAME_VAT = "VAT";
    public static final String FIELD_NOT_AVAILABLE = "N/A";
    public static final long EXPIRATION_TIME = 864_000_000;

    public interface AGGREGATION_PARAMS {
        String BUSINESS_UNIT = "BUSINESS_UNIT";
        String BUSINESS_UNIT_COLUMN_NAME = "business_code";
        String IC = "IC";
        String IC_COLUMN_NAME = "ic_code";
        String BRANCH_CODE = "BRANCH_CODE";
        String BRANCH_CODE_COLUMN_NAME = "branch_code";
        String NAV_LEDGER_ID = "NAV_LEDGER_ID";
        String NAV_LEDGER_ID_COLUMN_NAME = "NAV_LEDGER_ID";
        String DATE_WISE = "DATE_WISE";
        String DATE_WISE_COLUMN_NAME = "date";
        String PAYMENT_SOURCE = "PAYMENT_SOURCE";
        String PAYMENT_SOURCE_COLUMN_NAME = "payment_source";
        String PAYMENT_MODE = "PAYMENT_MODE";
        String PAYMENT_MODE_COLUMN_NAME = "payment_mode";
        String BULK_RECEIPT_NUMBER = "BULK_RECEIPT_NUMBER";
        String BULK_RECEIPT_NUMBER_COLUMN_NAME = "bulk_receipt_number";
        String CHEQUE_NUMBER = "CHEQUE_NUMBER";
        String CHEQUE_NUMBER_COLUMN_NAME = "cheque_number";
        String BANK = "BANK";
        String BANK_COLUMN_NAME = "bank_name";

        String SERVICE_AREA = "SERVICE_AREA";
        String SERVICE_AREA_COLUMN_NAME = "service_area_id";
        String POP = "POP";
        String POP_COLUMN_NAME = "pop_name";
        String OLT = "OLT";
        String OLT_COLUMN_NAME = "olt_name";
        String MONTHLY = "MONTHLY";
        String MONTHLY_COLUMN_NAME = "MONTHLY";
    }

    public interface NAV_BATCH_NAME {
        String BILLGEN = "BILLGEN";
        String RCPT = "RCPT";
        String CREDITNOTE = "CREDITNOTE";
        String BUSINESSPR = "BUSINESSPR";
        String REBUSINESSPR = "REVBUSINES";
        String PAYOUT = "PAYOUT";
        String SPREPAID = "SPREPAID";

    }

    public interface COMMON_DATA {
        String BRANCH = "BF183";
        String BU = " BU105";
        String ICCODE = " IC116";

        String NAV_LEDGER_FOR_BUSINESS_PROMOTION = "9322100";
        String NAV_LEDGER_FOR_TDS = "2265300";
        String NAV_LEDGER_SUNDRY_DEBTORS = "2241100";
        String NAV_LEDGER_FOR_ABBS = "9382100";
        String OLT = "Common";
        String POP = "Common";

    }

    public interface ACS_API_NAME {
        String GET_DEVICE_INFORMATION = "Get Device Information";
        String REQUEST_OPTICAL_POWER = "Request Optical Power";
        String REQUEST_NEIGHBOURING_WIFI_DETAILS = "Request Neighbouring WiFi Details";
        String REQUEST_DHCP_DETAILS = "Request DHCP Details";
        String REQUEST_SIGNAL_STRENGTH_DETAILS = "Request Signal Strength Details";
        String POST_DETAILS_TO_CHANGE_DEVICE_DETAILS = "Post Details To change device details";
        String REBOOT_DEVICE = "Reboot Device";
        String CUSTOMER_PROVISIONING = "Customer Provisioning";
        String CUSTOMER_DE_PROVISIONING = "Customer De-Provisioning";
    }

    public interface PAYMENT_MODE {
        String CREDIT_NOTE = "Credit Note";
        String CREDIT_NOTE1 = "CreditNote";
        String CASH = "Cash";
        String CHEQUE="Cheque";
        String ONLINE="Online";

        String OTHER_ADJUSTMENT="OtherAdjustment";
        String ABBS="Abbs";
        String OPG_ADJUSTMENT="OPG Adjustment";
        String NEFT_RTGS="Neft/Rtgs";
        String VAT_RECEIVEABLE="VatReceiveable";
        String DIRECT_DEPOSIT="Direct Deposit";
        String BARTER="Barter";
        String TDS="Tds";
        String BUSINESS_PROMOTION = "Buiness Promotion";
        String DEBIT_CARD="Debit Card";

        String CREDIT_CARD="CreditCard";


    }

    public interface SEND_OPTIONS_GOVERNMENT {
        String SEND_CREDIT_NOTE = "SEND_CREDIT_NOTE";
        String SEND_PAYMENT = "SEND_PAYMENT";
        String SEND_INVOICE = "SEND_INVOICE";


    }

    public interface PAYMENT_TYPE{
        String CASH = "Cash";
        String PAYMENT = "Payment";
    }

    public static final String MVNO_DELETE_UPDATE_ERROR_MSG = "Permission Denied. Unable to update/delete this record";
    public static final String APPROVED = "approved";


    /**This is a flag that determined which Rabbitmq operation done **/
    public interface PAYMENT_CONFIG_RABBITMQ_FLAG{
        String CREATE = "CREATE";

        String UPDATE = "UPDATE";

        String DELETE = "DELETE";
    }


    public static final String DEFAULT_PASSWORD="Savbill@1234";
    public static final String INITIATE  = "Initiate";
    public static final String SUCCESSFUL = "successful";
    public static final String TRANSACTION_SUCCESS = "SUCCESS";
    public static final String TRANSACTION_FAILURE = "FAILED";
    public static final String SELCOM_REVERSE_FLOW  = "SELCOM_REVERSE_FLOW";
    public static final String TIGOPESA_REVERSE_FLOW = "TIGOPESA_REVERSE_FLOW";
    public interface SELCOM_STATUS_CODES{
        String SUCCESS = "000";
        String FAILURE = "417";
        String INVALID_ACCOUNT = "010";
        String INVALID_AMOUNT = "012";
        String AMOUNT_TOO_HIGH = "014";
        String AMOUNT_TOO_LOW = "015";
        String BAD_REQUEST = "400";
    }

    public interface TIGOPESA_STATUS_CODES{
        public static final String SUCCESSFUL_TRANSACTION = "error000";
        public static final String SERVICE_NOT_AVAILABLE = "error001";
        public static final String INVALID_CUSTOMER_REFERENCE = "error010";
        public static final String CUSTOMER_ACCOUNT_LOCKED = "error011";
        public static final String INVALID_AMOUNT = "error012";
        public static final String INSUFFICIENT_AMOUNT = "error013";
        public static final String AMOUNT_TOO_HIGH = "error014";
        public static final String AMOUNT_TOO_LOW = "error015";
        public static final String INVALID_PAYMENT = "error016";
        public static final String GENERAL_ERROR = "error100";
        public static final String RETRY_NO_RESPONSE = "error111";
    }

    public interface TIGOPESA_CONSTANTS{
         String TYPE = "TYPE";
         String TXNID = "TXNID";
         String MSISDN = "MSISDN";
         String AMOUNT = "AMOUNT";
         String COMPANY_NAME = "COMPANYNAME";
         String CUSTOMER_REFERENCE = "CUSTOMERREFERENCEID";
         String SENDER_NAME = "SENDERNAME";
         String REF_ID = "REFID";
         String RESULT = "RESULT";
         String ERROR_CODE = "ERRORCODE";
         String ERROR_DESC = "ERRORDESC";
         String FLAG = "FLAG";
         String CONTENT = "CONTENT";
         String SYNC_BILLPAY_RESPONSE = "SYNC_BILLPAY_RESPONSE";
         String SYNC_BILLPAY_REQUEST = "SYNC_BILLPAY_REQUEST";
         String TRANSACTION_SUCCESS = "TS";
         String TRANSACTION_FAILURE = "TF";

    }


}


