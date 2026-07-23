package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddONURequestBodyDTO {
    @JsonProperty("SERIALNO")
    private String SERIALNO;

    @JsonProperty("OLTID")
    private String OLTID;

    @JsonProperty("PONID")
    private String PONID;

    @JsonProperty("PONTYPE")
    private String PONTYPE;

    @JsonProperty("AUTHTYPE")
    private String AUTHTYPE;

    @JsonProperty("ONUID")
    private String ONUID;

    @JsonProperty("PWD")
    private String PWD;

    @JsonProperty("ONUNO")
    private Integer ONUNO;

    @JsonProperty("NAME")
    private String NAME;

    @JsonProperty("DESC")
    private String DESC;

    @JsonProperty("ONUTYPE")
    private String ONUTYPE;

    @JsonProperty("UPBW")
    private String UPBW;

    @JsonProperty("DOWNBW")
    private String DOWNBW;

    public AddONURequestBodyDTO() {}
    public AddONURequestBodyDTO(String SERIALNO, String OLTID, String PONID, String PONTYPE, String AUTHTYPE, String ONUID, String PWD, String NAME, String ONUTYPE, String DESC, String UPBW, String DOWNBW) {
        this.SERIALNO = SERIALNO;
        this.OLTID = OLTID;
        this.PONID = PONID;
        this.PONTYPE = PONTYPE;
        this.AUTHTYPE = AUTHTYPE;
        this.ONUID = ONUID;
        this.PWD = PWD;
        this.NAME = NAME;
        this.ONUTYPE = ONUTYPE;
        this.DESC = DESC;
        this.UPBW = UPBW;
        this.DOWNBW = DOWNBW;
    }
}
