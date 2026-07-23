package com.savbill.radius.SoapApi.Dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class GetAccountDetailsDto {

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

    public GetAccountDetailsDto(Integer id, Integer maxconcurrentsession, String status, String password, Integer billday, String planName) {
        this.id = id;
        this.maxconcurrentsession = maxconcurrentsession;
        this.status = status;
        this.password = password;
        this.billday = billday;
        this.planname = planName;
    }

    public GetAccountDetailsDto(Integer id, Integer maxconcurrentsession, String status, String password,
                                Integer billday, String planName, String framedIp, String vlan_id,
                                String framedIPNetmask, String framedroute, String nasPortId, String gatewayIP,
                                String framedIpv6Address, String delegatedprefix, boolean macValidation, String acctno,
                                String mobile) {
        this.id = id;
        this.maxconcurrentsession = maxconcurrentsession;
        this.status = status;
        this.password = password;
        this.billday = billday;
        this.planname = planName;
        this.framedIp = framedIp;

        this.vlan_id = vlan_id;
        this.framedIPNetmask = framedIPNetmask;
        this.framedroute = framedroute;
        this.nasPortId = nasPortId;
        this.gatewayIP = gatewayIP;
        this.framedIpv6Address = framedIpv6Address;
        this.delegatedprefix = delegatedprefix;
        this.macValidation = macValidation;
        this.acctno = acctno;
        this.mobile = mobile;
    }

    public GetAccountDetailsDto(Integer id, Integer maxconcurrentsession, String status, String password,
                                Integer billday, String planName, String framedIp, String vlan_id,
                                String framedIPNetmask, String framedroute, String nasPortId, String gatewayIP,
                                String framedIpv6Address, String delegatedprefix, boolean macValidation, String acctno,
                                String mobile, String email) {
        this.id = id;
        this.maxconcurrentsession = maxconcurrentsession;
        this.status = status;
        this.password = password;
        this.billday = billday;
        this.planname = planName;
        this.framedIp = framedIp;

        this.vlan_id = vlan_id;
        this.framedIPNetmask = framedIPNetmask;
        this.framedroute = framedroute;
        this.nasPortId = nasPortId;
        this.gatewayIP = gatewayIP;
        this.framedIpv6Address = framedIpv6Address;
        this.delegatedprefix = delegatedprefix;
        this.macValidation = macValidation;
        this.acctno = acctno;
        this.mobile = mobile;
        this.email = email;
    }



}
