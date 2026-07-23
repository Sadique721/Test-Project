package com.savbill.integrationsystem.core.utillity;


public class URLConstants {
    public static final String BASE_PORTAL_API_URL = "/api/v1/SavbillIntegrationSystem";
    public static final String NAV_MASTER = "/navMaster";
    public static final String AGGREGATION_REPORT = "/aggregationReport";
    public static final String ACS_MASTER = "/acsMaster";
    public static final String REST_API = "/restApi";

    public static final String GOVERNMENT_INTEGRATION_MASTER = "/governmentintegrationmaster";

    public static final String NMS_MASTER = "/nmsMaster";
    public static final String NMS_INTEGRATION = "/nmsIntegration";
    public static final String CONFIG_CONTROLLER = "/config";
    public static final String PHONPE_MASTER = "/phonpe";

    public static final String INVOICE_INTIGRATION = "/invoiceIntigration";
    public static final String THIRD_PARTY_INTEGRATION_MENU = "/thirdPartyMenu" ;


    public interface MoMoPeUrlConstants {
          String REQUEST_TO_PAY  = "/collection/v1_0/requesttopay";
          String GENERATE_ACCESS_TOKEN = "/collection/token/";
          String GENERATE_API_KEY = "/v1_0/apiuser/";
          String API_KEY ="/apikey";
          String CREATE_USER = "/v1_0/apiuser";
          String REQUEST_TO_PAY_TRANSACTION_STATUS = "/collection/v2_0/payment/";
    }

    public interface SelcomPay {
        String SELCOM = "SELCOM ";
        String CREATE_ORDER  = "/create-order";
        String ORDER_STATUS = "/order-status?order_id=";
        String WEBHOOK_URL_ENDPOINT = "/selcomWebHook";
    }

    public static final String ONLINE_PAY_AUDIT = "/onlinePayAudit";
    public static final String CUST_CALL_LOGS = "/callLogs";

    public interface WaveMoney{
        String PAYMENT  = "/payment";
        String AUTHENTICATE = "/authenticate?transaction_id=";
        String CALLBACK_URL_ENDPOINT = "/waveMoneyCallBack";
        Integer timeToLiveInSeconds = 600;
        String REMARKS ="Order From Savbill";
    }

    public interface OnePay{
        String PAYMENT  = "/payment";
        String AUTHENTICATE = "/authenticate?transaction_id=";
        String CALLBACK_URL_ENDPOINT = "/onePayCallBack";
        Integer timeToLiveInSeconds = 600;
        String REMARKS ="Order From Savbill";
        String ONEPAY = "/OnePay";
        String WALLET_VERIFY_PHONE_NUMBER = "/Ver01/Wallet/Wallet_VerifyPhoneNumber";
        String HTTP_UAT="http://uat.";
        String RESPONSEDIRECTAP  = ".com/AGD_ResponseDirectAP";
        String CHECK_STATUS = "/Ver02/Wallet/Wallet_CheckTransactionStatus";
        String DIRECT_API = "/Ver02/Wallet/Wallet_DirectAPIV2";
//        String REQUEST_TO_PAY  = "com/AGD_ResponseDirectAP";
        String VERSION = "02";
        String REMARK = "Testing";
        String EXPIRED_SECONDS = "060";
    }

    public interface KbzPay{
        String CREATE_ORDER  = "/precreate";
        String CALLBACK_URL_ENDPOINT = "/kbzPayCallBack";
        String QUERY_ORDER = "/queryorder";
        Integer timeToLiveInSeconds = 600;
        String CREATE_ORDER_METHOD ="kbz.payment.precreate";
        String CREATE_ORDER_VER = "1.0";
        String QUERY_ORDER_VER = "3.0";
        String QUERY_ORDER_METHOD = "kbz.payment.queryorder";
        String TRADE_TYPE ="PWAAPP";
        String TRADE_TYPE_QR ="PAY_BY_QRCODE";
        String WAIT_TO_GENERATE = "wait_to_generate";
        String SHA_256 = "SHA256";

    }

    public interface PayStackPay {
        String PAYSTACK = "PAYSTACK ";
        String PAYSTACK_PAY = "/paystack";
        String INITIATE_PAYMENT = "/initiatePayment";
        String CALLBACK = "/callback";
        String VERIFY_TRANSACTION = "/verifytxn";
        String TRANSACTION_INITIALIZE = "/transaction/initialize";
        String TRANSACTION_VERIFY = "/transaction/verify/";
    }
    public interface Transactease{
        String TRANSACTEASE = "TRANSACTEASE";
        String TRANSACTEASE_PAY = "/transactease";
        String INITIATE_PAYMENT = "/initiatePayment";
        String CALLBACK = "/transacteaseCallback";
        String VERSION_NO = "1.0.0";
        String GET_TRANSACTION_STATUS= "GET_TRANSACTION_STATUS";
        String CLIENT_CREDENTIALS= "client_credentials";
        String LOGIN= "LOGIN";
        String LOGIN_API= "/api/login";
        String CHECK_STATUS="/api/transaction/status";
        String POSTAL_CODE = "11411";
    }
    public interface MpesaUrlConstants {

        String TRANSACTION_STATUS = "/mpesa/transactionstatus/v1/query";
        String MPESA = "MPESA";
        String MPESA_EXPRESS_SIMULATE = "MPESA_EXPRESS_SIMULATE";
        String GENERATE_ACCESS_TOKEN = "/oauth/v1/generate?grant_type=client_credentials";
        String B2C_PAYMENT_REQUEST="/mpesa/b2c/v3/paymentrequest";
        String RESULT_CALLBACK_URL="/b2c/result";
        String QUEUE_CALLBACK_URL="/b2c/queue";
        String EXPRESS_SIMULATE_PROCESS_REQUEST = "/mpesa/stkpush/v1/processrequest";
        String C2B_INITIATE_PAYMENT = "/c2b/mpesa/express/initiatePayment";
        String QR_PAYMENT = "/initiateDynamicQrPayment";
        String EXPRESS_SIMULATE_CALLBACK = "/mpesa/expressSimulate/callback";
        String EXPRESS_SIMULATE_CHECK_QUERY = "/mpesa/stkpushquery/v1/query";
        String DYNAMIC_QR = "/mpesa/qrcode/v1/generate";
    }
}
