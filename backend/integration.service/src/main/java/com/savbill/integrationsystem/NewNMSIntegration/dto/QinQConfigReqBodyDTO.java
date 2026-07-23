package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QinQConfigReqBodyDTO {

    @JsonProperty("SERIALNO")
    private String serialNo;

    @JsonProperty("OLTID")
    private String oltId;

    @JsonProperty("PONID")
    private String ponId;

    @JsonProperty("ONUIDTYPE")
    private String onuIdType;

    @JsonProperty("ONUID")
    private String onuId;

    @JsonProperty("SVLAN")
    private String sVlan;

    @JsonProperty("CVLAN")
    private String cVlan;

    @JsonProperty("UV")
    private String uv;

    public QinQConfigReqBodyDTO(String serialNo, String oltId, String ponId, String onuIdType,
                                String onuId, String sVlan, String cVlan, String uv) {
        this.serialNo = serialNo;
        this.oltId = oltId;
        this.ponId = ponId;
        this.onuIdType = onuIdType;
        this.onuId = onuId;
        this.sVlan = sVlan;
        this.cVlan = cVlan;
        this.uv = uv;
    }
}
