package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblcustipmapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustIpMapping extends Auditable {

    @Id
    @Column(name="id")
    private int id;

    @Column(name="custid",nullable = false)
    private Integer custid;

    @Column(name="ip_address", nullable = false)
    private String ipAddress;

    @Column(name="ip_type",nullable = false)
    private String ipType;

    @Column(name="custsermappingid",nullable = false)
    private Integer custsermappingid;


    public CustIpMapping(CustIpMapping custIpMaping) {
        this.id = custIpMaping.getId();
        this.custid = custIpMaping.getCustid();
        this.ipAddress = custIpMaping.getIpAddress();
        this.ipType = custIpMaping.getIpType();
        this.custsermappingid = custIpMaping.getCustsermappingid();
    }
}
