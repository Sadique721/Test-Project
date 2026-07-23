package com.savbill.revenuemanagement.core.dto.common;

public class ResponseObject {

    String responseCode;
    String responseMessage;
    Object responseObject;

    public String getResponseCode() {
	return responseCode;
    }

    public void setResponseCode(String responseCode) {
	this.responseCode = responseCode;
    }

    public String getResponseMessage() {
	return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
	this.responseMessage = responseMessage;
    }

    public Object getResponseObject() {
	return responseObject;
    }

    public void setResponseObject(Object responseObject) {
	this.responseObject = responseObject;
    }

    @Override
    public String toString() {
	return "ResponseObject [responseCode=" + responseCode + ", responseMessage=" + responseMessage
		+ ", responseObject=" + responseObject + "]";
    }

}
