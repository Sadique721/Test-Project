package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.CustomerMessage;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblcustomers")
public class CustomerData {

    @Id
    @Column(name = "custid", nullable = false, length = 40)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String username;
    @Column(nullable = false, length = 40)
    private String firstname;
    @Column(nullable = false, length = 40)
    private String lastname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, length = 10)
    private String mobile;
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;
    @Column(name = "servicearea_id")
    private Long servicearea;
    @Column(name = "branchid")
    private Long branch;
    @Column(name = "status", nullable = false, length = 100)
    private String status;
    private String countryCode;
    @Column(name = "CREATEDDATE", length = 100)
    private LocalDateTime createdDate;

    @Column(name = "LASTMODIFIEDDATE", length = 100)
    private LocalDateTime lastmodifiedDate;
    @Column(name = "CREATEBYNAME", nullable = false, length = 10)
    private String createbyname;
    @Column(name = "UPDATEBYNAME", nullable = false, length = 10)
    private String updatebyname;
    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 10)
    private Integer createdByStaffId;
    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 10)
    private Integer lastModifiedByStaffId;
    @Column(name = "account_number", nullable = false, length = 10)
    private String accountNumber;
    @Column(name = "customer_type", nullable = false, length = 10)
    private String customerType;
    @Column(name = "isorgcust")
    private Boolean isorgcust = false;
    @Column(length = 25)
    private String pan;

    @Column(name = "parentcustid")
    private Integer parentcustid;

    @Column(name = "olt")
    private String olt;
    @Column(name = "pop")
    private String pop;
    @Column(name="blockno")
    private String blockNo;

    public CustomerData() {
    }

    public CustomerData(CustomerMessage customer) {
        this.id = customer.getId().intValue();
        this.username = customer.getUsername();
        this.firstname = customer.getFirstname();
        this.lastname = customer.getLastname();
        this.email = customer.getEmail();
        this.mobile = customer.getMobile();
        this.mvnoId = customer.getMvnoId();
        this.buId = customer.getBuId();
        this.servicearea = customer.getServicearea();
        this.branch = customer.getBranch();
        this.status = customer.getStatus();
        this.countryCode = customer.getCountryCode();
        this.createdDate = customer.getCreatedDate();
        this.lastmodifiedDate = customer.getLastmodifiedDate();
        this.createbyname = customer.getCreatebyname();
        this.updatebyname = customer.getUpdatebyname();
        this.createdByStaffId = customer.getCreatedByStaffId();
        this.lastModifiedByStaffId = customer.getLastModifiedByStaffId();
        this.accountNumber = customer.getAccountNumber();
        this.customerType = customer.getCustomerType();
        this.isorgcust = customer.getIsorgcust();
        this.pan = customer.getPan();
        this.parentcustid = customer.getParentcustid();
        this.olt = customer.getOlt();
        this.pop = customer.getPop();
        this.blockNo = customer.getBlockNo();
    }
}
