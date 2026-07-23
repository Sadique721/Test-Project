package com.savbill.revenuemanagement.core.service.postpaid;

import lombok.Data;

@Data
public class DebitDocDetailList {

    private  Integer debitDocId;
    private  Long serviceId;

    private Double totalAmount;

    private String chargeType;

    private String planId;

    private Integer chargeId;

    private Double chargeActualAmount;

    private String qosName;

    private Double chargeActualOfferPrice;

    public DebitDocDetailList(Integer debitDocId, Long serviceId, Double totalAmount, String chargeType,String planId,Integer chargeId,Double chargeActualAmount,String qosName,Double chargeActualOfferPrice) {
        this.debitDocId = debitDocId;
        this.serviceId = serviceId;
        this.totalAmount = totalAmount;
        this.chargeType = chargeType;
        this.planId = planId;
        this.chargeId=chargeId;
        this.chargeActualAmount=chargeActualAmount;
        this.qosName=qosName;
        this.chargeActualOfferPrice=chargeActualOfferPrice;
    }
}
