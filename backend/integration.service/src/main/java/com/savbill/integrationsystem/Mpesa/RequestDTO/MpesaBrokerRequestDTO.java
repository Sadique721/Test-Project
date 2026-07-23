package com.savbill.integrationsystem.Mpesa.RequestDTO;

import com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "mpesaBroker")
@XmlAccessorType(XmlAccessType.FIELD)
public class MpesaBrokerRequestDTO {

    @XmlElement(name = "request")
    private Request request;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Request {

        @XmlElement(name = "serviceProvider")
        private ServiceProvider serviceProvider;

        @XmlElement(name = "transaction")
        private Transaction transaction;

        public ServiceProvider getServiceProvider() {
            return serviceProvider;
        }

        public void setServiceProvider(ServiceProvider serviceProvider) {
            this.serviceProvider = serviceProvider;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ServiceProvider {

        @XmlElement(name = ValidateMpesaConstant.SP_ID)
        private long spId;

        @XmlElement(name = ValidateMpesaConstant.SP_PASSWORD)
        private String spPassword;

        @XmlElement(name = ValidateMpesaConstant.TIMESTAMP)
        private long timestamp;

        public long getSpId() {
            return spId;
        }

        public void setSpId(long spId) {
            this.spId = spId;
        }

        public String getSpPassword() {
            return spPassword;
        }

        public void setSpPassword(String spPassword) {
            this.spPassword = spPassword;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        @XmlElement(name = ValidateMpesaConstant.ACCOUNT_REFERENCE)
        private String accountReference;

        @XmlElement(name = ValidateMpesaConstant.AMOUNT)
        private Double amount;

        @XmlElement(name = ValidateMpesaConstant.COMMAND_ID)
        private String commandID;

        @XmlElement(name = ValidateMpesaConstant.INITIATOR)
        private String initiator;

        @XmlElement(name = ValidateMpesaConstant.INITIATOR_PASSWORD)
        private String initiatorPassword;

        @XmlElement(name = ValidateMpesaConstant.ORIGINATOR_CONVERSATION_ID)
        private String originatorConversationID;

        @XmlElement(name = ValidateMpesaConstant.CONVERSATION_ID)
        private String conversationID;

        @XmlElement(name = ValidateMpesaConstant.RECIPIENT)
        private long recipient;

        @XmlElement(name = ValidateMpesaConstant.MPESA_RECEIPT)
        private String mpesaReceipt;

        @XmlElement(name = ValidateMpesaConstant.TRANSACTION_DATE)
        private String transactionDate;

        @XmlElement(name = ValidateMpesaConstant.TRANSACTION_ID)
        private String  transactionID;

        public String getAccountReference() {
            return accountReference;
        }

        public void setAccountReference(String accountReference) {
            this.accountReference = accountReference;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCommandID() {
            return commandID;
        }

        public void setCommandID(String commandID) {
            this.commandID = commandID;
        }

        public String getInitiator() {
            return initiator;
        }

        public void setInitiator(String initiator) {
            this.initiator = initiator;
        }

        public String getInitiatorPassword() {
            return initiatorPassword;
        }

        public void setInitiatorPassword(String initiatorPassword) {
            this.initiatorPassword = initiatorPassword;
        }

        public String getOriginatorConversationID() {
            return originatorConversationID;
        }

        public void setOriginatorConversationID(String originatorConversationID) {
            this.originatorConversationID = originatorConversationID;
        }

        public String getConversationID() {
            return conversationID;
        }

        public void setConversationID(String conversationID) {
            this.conversationID = conversationID;
        }

        public long getRecipient() {
            return recipient;
        }

        public void setRecipient(long recipient) {
            this.recipient = recipient;
        }

        public String getMpesaReceipt() {
            return mpesaReceipt;
        }

        public void setMpesaReceipt(String mpesaReceipt) {
            this.mpesaReceipt = mpesaReceipt;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getTransactionID() {
            return transactionID;
        }

        public void setTransactionID(String transactionID) {
            this.transactionID = transactionID;
        }
    }
}
