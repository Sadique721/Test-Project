package com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO;

import lombok.Setter;

import javax.xml.bind.annotation.*;

import static com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant.*;

@Setter
@XmlRootElement(name = MPESA_BROKER)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"version", "response"})
public class MpesaVodacomAckResponseDTO {

    private String version = VERSION_CODE;
    private Response response;

    @XmlAttribute(name = VERSION)
    public String getVersion() {
        return version;
    }

    @XmlElement(name = RESPONSE)
    public Response getResponse() {
        return response;
    }

    @Setter
    @XmlAccessorType(XmlAccessType.PROPERTY)
//    @XmlType(propOrder = {
//            "conversationID",
//            "originatorConversationID",
//            "transactionID",
//            "responseCode",
//            "responseDesc",
//            "serviceStatus"
//    }) TODO confirm whether M-PESA enforce ordering of fields in xml?
    public static class Response {
        private String conversationID;
        private String originatorConversationID;
        private String transactionID;
        private String responseCode;
        private String responseDesc;
        private String serviceStatus;

        @XmlElement(name = CONVERSATION_ID)
        public String getConversationID() {
            return conversationID;
        }

        @XmlElement(name = ORIGINATOR_CONVERSATION_ID)
        public String getOriginatorConversationID() {
            return originatorConversationID;
        }

        @XmlElement(name = TRANSACTION_ID)
        public String getTransactionID() {
            return transactionID;
        }

        @XmlElement(name = RESPONSE_CODE)
        public String getResponseCode() {
            return responseCode;
        }

        @XmlElement(name = RESPONSE_DESC)
        public String getResponseDesc() {
            return responseDesc;
        }

        @XmlElement(name = SERVICE_STATUS)
        public String getServiceStatus() {
            return serviceStatus;
        }
    }
}
