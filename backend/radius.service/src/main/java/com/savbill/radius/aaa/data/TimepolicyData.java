package com.savbill.radius.aaa.data;

import java.util.ArrayList;
import java.util.List;

import com.savbill.radius.entity.QOSPolicyGatewayMapping;

public class TimepolicyData {

    private int policyid;
    private String fromDay;
    private String toDay;
    private String fromTime;
    private String toTime;
    private String access;
    private int fromNumber;
    private int toNumber;
    private int quotadtlid;
    private int details_id;

    private boolean isFreeQuota;
    List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping = new ArrayList<QOSPolicyGatewayMapping>();


    public int getPolicyid() {
        return policyid;
    }

    public void setPolicyid(int policyid) {
        this.policyid = policyid;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public int getQuotadtlid() {
        return quotadtlid;
    }

    public void setQuotadtlid(int quotadtlid) {
        this.quotadtlid = quotadtlid;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public int getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(int fromNumber) {
        this.fromNumber = fromNumber;
    }

    public int getToNumber() {
        return toNumber;
    }

    public void setToNumber(int toNumber) {
        this.toNumber = toNumber;
    }

    public List<QOSPolicyGatewayMapping> getQosPolicyGatewayMapping() {
        return qosPolicyGatewayMapping;
    }

    public void setQosPolicyGatewayMapping(List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping) {
        this.qosPolicyGatewayMapping = qosPolicyGatewayMapping;
    }

    public int getDetails_id() {
        return details_id;
    }

    public void setDetails_id(int details_id) {
        this.details_id = details_id;
    }

	public boolean isFreeQuota() {
		return isFreeQuota;
	}

	public void setFreeQuota(boolean freeQuota) {
		isFreeQuota = freeQuota;
	}
}
