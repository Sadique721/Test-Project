package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetRemarksDTO {

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
    private String reason;
    private String mac;
    private String serial;
    private String port;
    private String trackable;
    private String msisdn;

    public GetRemarksDTO(String imsi, String iccid, String pin1, String puk1, String pin2, String puk2, String kiEncrypted, String acc, String adm, String kic, String kid, String kik, String reason,String mac,String serial,String port,String trackable,String msisdn){
        this.imsi = imsi;
        this.iccid = iccid;
        this.pin1 = pin1;
        this.puk1 = puk1;
        this.pin2 = pin2;
        this.puk2 = puk2;
        this.kiEncrypted = kiEncrypted;
        this.acc = acc;
        this.adm = adm;
        this.kic = kic;
        this.kid = kid;
        this.kik = kik;
        this.reason = reason;
        this.mac = mac;
        this.serial = serial;
        this.port = port;
        this.trackable = trackable;
        this.msisdn = msisdn;


    }
}

