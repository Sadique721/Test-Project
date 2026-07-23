package com.savbill.radius.aaa.data;

public class PlanQosPolicyMapping {

    private Long id;

    private Long planId;

    private Integer qosPolicy;

    private Double frompercentage;

    private Double topercentage;

    private boolean isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Integer getQosPolicy() {
        return qosPolicy;
    }

    public void setQosPolicy(Integer qosPolicy) {
        this.qosPolicy = qosPolicy;
    }

    public Double getFrompercentage() {
        return frompercentage;
    }

    public void setFrompercentage(Double frompercentage) {
        this.frompercentage = frompercentage;
    }

    public Double getTopercentage() {
        return topercentage;
    }

    public void setTopercentage(Double topercentage) {
        this.topercentage = topercentage;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
