package com.savbill.inventorymanagement.modules.Customers;

import lombok.Data;

import java.util.List;

@Data
public class CustomerNetworkDeviceDTO {
    private Integer customerid;
    private Long popid;
    private String popName;
    private Long oltid;
    private String oltDeviceName;
    private String nasPort;
    private String ipPoolNameBind;
    private String framedIp;
    private String framedIpBind;
    private Long masterdbid;
    private String masterdbDeviceName;
    private Long dnsplitterid;
    private String dnsplitterDerviceName;
    private Long snsplitterid;
    private String snsplitterDerviceName;
    private Long oltslotid;
    private Long oltportid;
    private String staticOrPooledIP;
    private List<String> macAddress;
    private List<String> onuSerialNumber;
    private List<String> externalOnuSerialNumber;
}
