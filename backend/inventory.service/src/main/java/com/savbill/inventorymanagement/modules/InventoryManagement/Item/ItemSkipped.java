package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tblmserializeditemskipped")
@Data
public class ItemSkipped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "inward_id")
    private Long inwardId;

    @Column(name = "outward_id")
    private Long outwardId;

    @Column(name = "type")
    private String type;

    @Column(name = "mvno_id", updatable = false, nullable = false)
    private Long mvnoId;

    @Column(name = "imsi", length = 100)
    private String imsi;

    @Column(name = "iccid", length = 100)
    private String iccid;

    @Column(name = "pin1", length = 100)
    private String pin1;

    @Column(name = "puk1", length = 100)
    private String puk1;

    @Column(name = "pin2", length = 100)
    private String pin2;

    @Column(name = "puk2", length = 100)
    private String puk2;

    @Column(name = "ki_encrypted", length = 100)
    private String kiEncrypted;

    @Column(name = "acc", length = 100)
    private String acc;

    @Column(name = "adm", length = 100)
    private String adm;

    @Column(name = "kic", length = 100)
    private String kic;

    @Column(name = "kid", length = 100)
    private String kid;

    @Column(name = "kik", length = 100)
    private String kik;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "mac")
    private String mac;

    @Column(name = "serial")
    private String serial;

    @Column(name = "port")
    private String port;

    @Column(name = "trackable")
    private String trackable;

    @Column(name = "msisdn")
    private String msisdn;


    @CreationTimestamp
    @Column(name = "createdate", updatable = false)
    private Timestamp createdate;
}
