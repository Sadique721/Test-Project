package com.savbill.integrationsystem.AirtelIntigration;

import lombok.Data;

public class AirtelPaymentRequestDTO {

        private String reference;
        private Subscriber subscriber;
        private Transaction transaction;

        // Getters and Setters
        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public Subscriber getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        // Nested class for Subscriber
        public static class Subscriber {
            private String country;
            private String currency;
            private String msisdn;

            // Getters and Setters
            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getMsisdn() {
                return msisdn;
            }

            public void setMsisdn(String msisdn) {
                this.msisdn = msisdn;
            }
        }

        // Nested class for Transaction
        public static class Transaction {
            private int amount;
            private String country;
            private String currency;
            private String id;

            // Getters and Setters
            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }



}
