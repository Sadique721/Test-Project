package com.savbill.integrationsystem.AirtelIntigration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirtelApiCheckStatusResponseDTO {
    private Data data;
    private Status status;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("status_code")
    private String statusCode;

    // Getters and Setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Nested Classes
    public static class Data {
        private Transaction transaction;

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    public static class Transaction {
        @JsonProperty("airtel_money_id")
        private String airtelMoneyId;
        private String id;
        private String message;
        private String status;

        public String getAirtelMoneyId() {
            return airtelMoneyId;
        }

        public void setAirtelMoneyId(String airtelMoneyId) {
            this.airtelMoneyId = airtelMoneyId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Status {
        @JsonProperty("response_code")
        private String responseCode;
        private String code;
        private boolean success;

        @JsonProperty("result_code")
        private String resultCode;
        private String message;

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
