package com.savbill.integrationsystem.SOAPService.GetAccocuntDetails;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class GetAccountDetailsSoapResponseDto {

    private Integer id;
    private Integer maxconcurrentsession;
    private String status;
    private String password;
    private Integer billday;
    private String planname;
    private String framedIp;
    private String vlan_id;
    private String framedIPNetmask;
    private String framedroute;
    private String nasPortId;
    private String gatewayIP;
    private String framedIpv6Address;
    private String delegatedprefix;
    private String callingStationId;
    private boolean macValidation;
    private String acctno;
    private String mobile;
    private String email;

    public GetAccountDetailsSoapResponseDto(Integer id, Integer maxconcurrentsession, String status, String password, Integer billday, String planName) {
        this.id = id;
        this.maxconcurrentsession = maxconcurrentsession;
        this.status = status;
        this.password = password;
        this.billday = billday;
        this.planname = planName;
    }



}
