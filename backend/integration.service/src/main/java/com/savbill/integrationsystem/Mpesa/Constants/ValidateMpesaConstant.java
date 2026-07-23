package com.savbill.integrationsystem.Mpesa.Constants;

public interface ValidateMpesaConstant {
    String MPESA_PAYMENT_GATEWAY_NAME = "MPESA Vodacom";

    String ACK_RESPONSE_CODE = "0";
    String RECEIVED = "Received";
    String SUCCESS = "Success";
    String COMPLETED = "Completed";
    String TRANSACTION_FAILED = "Failed";
    String TRANSACTION_SUCCESS = "Successful";
    String TRANSACTION_SUCCESS_CODE = "0";
    String TRANSACTION_FAILED_CODE = "999";

    String BASE_PORTAL_API_URL = "/api/v1/mpesa/c2b";
    String XML_NAMESPACE = "http://inforwise.co.tz/broker/";
    String MPESA_BROKER = "mpesaBroker";
    String VERSION_CODE = "2.0";
    String VERSION = "version";
    String REQUEST = "request";
    String RESPONSE = "response";
    String RESULT = "result";
    String SERVICE_PROVIDER = "serviceProvider";
    String TRANSACTION = "transaction";

    String SP_ID = "spId";
    String SP_PASSWORD = "spPassword";
    String TIMESTAMP = "timestamp";
    String COMMAND_ID = "commandID";
    String AMOUNT = "amount";
    String RECIPIENT = "recipient";
    String TRANSACTION_DATE = "transactionDate";
    String TRANSACTION_ID = "transactionID";
    String ACCOUNT_REFERENCE = "accountReference";
    String INITIATOR = "initiator";
    String INITIATOR_PASSWORD = "initiatorPassword";
    String ORIGINATOR_CONVERSATION_ID = "originatorConversationID";
    String CONVERSATION_ID = "conversationID";
    String SERVICE_STATUS = "serviceStatus";
    String SERVICE_DESC = "serviceDesc";
    String RESPONSE_CODE = "responseCode";
    String SERVICE_ID = "serviceID";
    String MPESA_RECEIPT = "mpesaReceipt";
    String RESPONSE_DESC = "responseDesc";
    String RESULT_TYPE = "resultType";
    String RESULT_CODE = "resultCode";
    String RESULT_DESC = "resultDesc";
    String SERVICE_RECEIPT = "serviceReceipt";
    String SERVICE_DATE = "serviceDate";

    String INITIATE = "Initiate";
    String MPESA_FLOW = "MPESA_FLOW";
    String SUCCESSFUL = "successful";
    String REMARK = "MPESA transaction initiated";
    String STATUS_COMMAND_ID="TransactionStatusQuery";
    String TRANSACTION_DESC = "PAYMENT";

}
