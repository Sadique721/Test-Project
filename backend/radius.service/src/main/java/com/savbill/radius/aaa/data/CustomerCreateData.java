package com.savbill.radius.aaa.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerCreateData {
    public Integer custId;
    public  String username;
    public  String password;
    public  String firstname;
    public  String lastname;
    public  String email;
    public  String title;
    public  String partner;
    public  int failcount;
    public  String acct_no;
    public  String thparam1;
    public  String thparam2;
    public  String thparam3;
    public  String thparam4;
    public  String vsiid;
    public  String vsiname;
    public  String vrfname;
    public  String rdvalue;
    public  String rdexport;
    public  String ipprefixes;
    public  String gatewayIP;
    public  String skipnetconf;
    public  String rdimport;
    public  String bngrouterinterface;
    public  String qos;
    public  String vlanid;
    public  String wanip;
    public  String lanip;
    public  String asnnumber;
    public  String llaccountid;
    public  String peerip;
    public  String remarks;
    public  String custtype;
    public  String mobile;
    public  Date birthDate;
    public  String countryCode;
    public  String partnerid;
    public  String status;
    public  String parentCustomerId;
    //    public String eDate;
    public Integer mvnoId;
    public String phone;
    public  String acctno;
    public  String addparam1;
    public  String addparam2;
    public  String addparam3;
    public  String addparam4;
    public String edate;
    private String ipv4;
    private String ipv6;
    private String vlan;
    private Boolean mac_auth_enable;
    private Boolean mac_provision;
    public String planName;
    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String  primaryDNS;
    private String  primaryIPv6DNS;
    private String  secondaryIPv6DNS;
    private String  secondaryDNS;
    private Integer  macRetentionPeriod;
    private String  macRetentionUnit;

}
