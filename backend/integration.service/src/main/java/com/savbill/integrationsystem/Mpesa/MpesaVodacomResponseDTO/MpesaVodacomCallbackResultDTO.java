package com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import static com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant.*;

@Setter
@XmlRootElement(name = MPESA_BROKER)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"version", "result"})
public class MpesaVodacomCallbackResultDTO {

    private static final String NAMESPACE = "http://infowise.co.tz/broker/";

    private String version = VERSION_CODE;
    private Result result;

    @XmlAttribute(name = VERSION)
    public String getVersion() {
        return version;
    }

    @XmlElement(name = RESULT, namespace = NAMESPACE)
    public Result getResult() {
        return result;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Result {

        @XmlElement(name = SERVICE_PROVIDER, namespace = NAMESPACE)
        private ServiceProvider serviceProvider;

        @XmlElement(name = TRANSACTION, namespace = NAMESPACE)
        private CallbackTransaction transaction;
    }

    @Setter
    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ServiceProvider {

        @XmlElement(name = SP_ID, namespace = NAMESPACE)
        private long spId;

        @XmlElement(name = SP_PASSWORD, namespace = NAMESPACE)
        private String spPassword;

        @XmlElement(name = TIMESTAMP, namespace = NAMESPACE)
        private long timestamp;
    }

    @Setter
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class CallbackTransaction {

        private String resultType;
        private String resultCode;
        private String resultDesc;
        private String serviceReceipt;
        private String serviceDate;
        private String originatorConversationID;
        private String conversationID;
        private String transactionID;
        private String initiator;
        private String initiatorPassword;

        @XmlElement(name = RESULT_TYPE, namespace = NAMESPACE)
        public String getResultType() {
            return resultType;
        }

        @XmlElement(name = RESULT_CODE, namespace = NAMESPACE)
        public String getResultCode() {
            return resultCode;
        }

        @XmlElement(name = RESULT_DESC, namespace = NAMESPACE)
        public String getResultDesc() {
            return resultDesc;
        }

        @XmlElement(name = SERVICE_RECEIPT, namespace = NAMESPACE)
        public String getServiceReceipt() {
            return serviceReceipt;
        }

        @XmlElement(name = SERVICE_DATE, namespace = NAMESPACE)
        public String getServiceDate() {
            return serviceDate;
        }

        @XmlElement(name = ORIGINATOR_CONVERSATION_ID, namespace = NAMESPACE)
        public String getOriginatorConversationID() {
            return originatorConversationID;
        }

        @XmlElement(name = CONVERSATION_ID, namespace = NAMESPACE)
        public String getConversationID() {
            return conversationID;
        }

        @XmlElement(name = TRANSACTION_ID, namespace = NAMESPACE)
        public String getTransactionID() {
            return transactionID;
        }

        @XmlElement(name = INITIATOR, namespace = NAMESPACE)
        public String getInitiator() {
            return initiator;
        }

        @XmlElement(name = INITIATOR_PASSWORD, namespace = NAMESPACE)
        public String getInitiatorPassword() {
            return initiatorPassword;
        }
    }
}