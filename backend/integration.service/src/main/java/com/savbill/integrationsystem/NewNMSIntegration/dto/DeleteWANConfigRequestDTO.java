package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteWANConfigRequestDTO {
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

    @JsonProperty("MODE")
    private Integer mode;

    @JsonProperty("CONNTYPE")
    private Integer connType;

    @JsonProperty("UPORT")
    private Integer uPort;

    @JsonProperty("SSID")
    private Integer ssid;


    public DeleteWANConfigRequestDTO(String serialno, String oltid, String ponid, String onuidtype, String onuid, int mode, int conntype,int uport, int ssid) {
        this.serialNo = serialno;
        this.oltId = oltid;
        this.ponId = ponid;
        this.onuIdType = onuidtype;
        this.onuId = onuid;
        this.mode = mode;
        this.connType = conntype;
        this.uPort = uport;
        this.ssid = ssid;
    }
}
