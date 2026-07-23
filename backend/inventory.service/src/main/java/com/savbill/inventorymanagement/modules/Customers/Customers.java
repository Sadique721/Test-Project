package com.savbill.inventorymanagement.modules.Customers;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblmcustomers")
@EntityListeners(AuditableListener.class)
public class Customers extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custid", length = 40)
    private Integer id;
    //@Column(name = "title", length = 40)
    private String title;
    @Column(length = 40)
    private String username;
    @Column(length = 40)
    private String firstname;
    @Column(length = 40)
    private String lastname;
    @Column(length = 40)
    private String password;
    @ManyToOne
    @JoinColumn(name = "servicearea_id")
    private ServiceArea servicearea;
    @Column(name = "status", length = 100)
    private String status;
    @Column(name = "MVNOID", length = 40)
    private Integer mvnoId;
    @Transient
    private String fullName;
    @Column(name = "BUID", length = 40)
    private Long buId;
    @Column(name = "popid")
    private Long popid;
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    @Column(name = "oltid")
    private Long oltid;
    @Column(name = "ezybill_customers_id")
    private String ezyBillCustomersId;
    private String latitude;
    private String longitude;
    @Column(name = "custname", length = 40)
    private String custname;
    @Column(name = "ezybill_account_number", length = 100)
    private String ezyBillAccountNumber;
    @Column(name = "parent_experience")
    private String parentExperience;
    @Column(name = "customertype", length = 100)
    private String custtype;

    @Column(name = "network_device_id")
    private Integer networkDeviceId;

    @Transient
    private Integer parentCustomersId;

    @Column(name = "partnerid")
    private Integer partnerId;

    @Column(name = "parentcustid")
    private Integer parentCustId;

    @Column(name = "nas_port")
    private String nasPort;

    @Column(name = "ip_pool_name_bind")
    private String ipPoolNameBind;

    @Column(name = "framed_ip")
    private String framedIp;

    @Column(name = "framed_ip_bind")
    private String framedIpBind;

    @Column(name = "masterdbid")
    private Long masterdbid;

    @Column(name = "splitterid")
    private Long splitterid;

    @Column(name="blockno")
    private String blockNo;

    @Column(name = "caf_approve_status")
    private String cafApproveStatus;

    @Column(name = "firstactivationdate")
    private LocalDateTime firstActivationDate;

    private Long oltslotid;

    private Long oltportid;
    public Integer getId() {
        return id;
    }
    public String getCusttype() {
        return custtype;
    }
    public void setCusttype(String custtype) {
        this.custtype = custtype;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public LocalDateTime getCreatedate() {
        return super.getCreatedate();
    }

    public void setCreatedate(LocalDateTime createdate) {
        super.setCreatedate(createdate);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedate() {
        return super.getUpdatedate();
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        super.setUpdatedate(updatedate);
    }
    public Customers() {
    }

    public Customers(Customers customers) {
        this.status = customers.getStatus();
        this.firstname = customers.getFirstname();
        this.servicearea = customers.getServicearea();
        this.popid = customers.getPopid();
    }

    public Customers(Customers customers, Integer id) {
        this.id = customers.id;
        this.status = customers.getStatus();
        this.firstname = customers.getFirstname();
        this.servicearea = customers.getServicearea();
        this.mvnoId = customers.getMvnoId();
        this.popid = customers.getPopid();
    }


    @Override
    public String toString() {
        return "Customer toString Override :" + username;
    }

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != getFirstname() && !getFirstname().isEmpty() && getFirstname().trim().length() > 0) {
                fullName += " " + getFirstname();
            }
            if (null != getLastname() && !getLastname().isEmpty() && getLastname().trim().length() > 0) {
                fullName += " " + getLastname();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Customers(String firstname, String username, String mobile, String email, String acctno, String customerType, String cafno, String partnerName, String serviceArea) {
        this.firstname = firstname;
        this.username = username;
    }

    public Customers(Integer id) {
        this.id = id;
    }

    public Customers(Integer id, String username, Integer mvnoId , String status, String custtype, Long buId, String password) {
        this.id = id;
        this.username = username;
        this.mvnoId = mvnoId;
        this.status= status;
        this.custtype = custtype;
        this.buId = buId;
        this.password = password;
    }
    public Integer getParentCustomersId() {
        return parentCustomersId;
    }

    public void setParentCustomersId(Integer parentCustomersId) {
        this.parentCustomersId = parentCustomersId;
    }

//    public Customers getParentCustomers() {
//        if (parentCustomers == null) {
//            return null;
//        } else {
//            return parentCustomers;
//        }
//    }

//    public void setParentCustomers(Customers parentCustomers) {
//        this.parentCustomers = parentCustomers;
//    }

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
