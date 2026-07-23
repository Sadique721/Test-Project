package com.savbill.radius.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmprocesscdr")
public class ProcessCDR {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name="USERNAME")
    private String USERNAME;

    @Column(name="SESSIONID")
    private String SESSIONID;

    @Column(name="FRAMEDIPADDRESS")
    private String FRAMEDIPADDRESS;

    @Column(name="SESSIONAUTHRULE")
    private String SESSIONAUTHRULE;

    @Column(name="NASIPADDRESS")
    private String NASIPADDRESS;

    @Column(name="REQUESTTYPE")
    private String REQUESTTYPE;

    @Column(name="MACADDRESS")
    private String MACADDRESS;

    @Column(name="NASPORTID")
    private String NASPORTID;

    @Column(name="FRAMED_IPV6_ADDRESS")
    private String FRAMED_IPV6_ADDRESS;

    @Column(name="FRAMED_INTERFACE_ID")
    private String FRAMED_INTERFACE_ID;

    @Column(name = "DELEGATED_IPV6_PREFIX")
    private String DELEGATED_IPV6_PREFIX;

    @Column(name="AGGREGATEKEY")
    private String AGGREGATEKEY;

    @Column(name = "UPLOAD")
    private String UPLOAD;

    @Column(name = "DOWNLOAD")
    private String DOWNLOAD;

    @Column(name="TOTAL")
    private String TOTAL;

    @Column(name="CDRTIME")
    private String CDRTIME;

    @Column(name="ENDTIME")
    private String ENDTIME;

    @Column(name="STARTTIME")
    private String STARTTIME;

    @Column(name="lastupdatedquota")
    private double lastUpdatedQuota;

    @Column(name="currentsessionquota")
    private double currentSessionQuota;
}
