package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DelRequestDTO {
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

    public DelRequestDTO(String serialno, String oltid, String ponid, String onuidtype, String onuid) {
        this.serialNo = serialno;
        this.oltId = oltid;
        this.ponId = ponid;
        this.onuIdType = onuidtype;
        this.onuId = onuid;
    }
}
