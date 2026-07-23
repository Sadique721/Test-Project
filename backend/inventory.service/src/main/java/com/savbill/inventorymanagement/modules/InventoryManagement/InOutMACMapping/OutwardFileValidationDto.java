package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OutwardFileValidationDto {

    private Long outwardId;
    private Long itemId;
    private String macAddress;
    private String serialNumber;
    private String msisdn;
    private String imsi;
    private String iccid;
    private String pin1;
    private String puk1;
    private String pin2;
    private String puk2;
    private String kiEncrypted;
    private String acc;
    private String adm;
    private String kic;
    private String kid;
    private String kik;
    private Integer mvnoId;
    private String port;
    private String trackable;
    private String cas;
    private boolean valid = true;
    private List<String> remarkData = new ArrayList<>();

    private Integer rowNumber;

    public void addRemark(String remark) {
        this.valid = false;
        this.remarkData.add(remark);
    }
}
