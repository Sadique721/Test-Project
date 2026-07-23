package com.savbill.revenuemanagement.productmanagement.parentchildmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "tblparentchildmappingrel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentChildMappingRel {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "parent_username", length = 255)
    private String parentUsername;

    @Column(name = "child_username", length = 255)
    private String childUsername;

    @Column(name = "create_by_staff_id")
    private Long createdByStaff;

    @Column(name = "mvno_id")
    private Long mvno;


    @Column(name = "parent_cust_id")
    private Long parentCustomer;
    
    @Column(name = "child_cust_id")
    private Long childCustomer;

    @Column(name = "isparent")
    private Boolean isparent;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "parent_firstname", length = 255)
    private String parentFirstName;

    @Column(name = "parent_lastname", length = 255)
    private String parentLastName;

    @Column(name = "child_firstname", length = 255)
    private String childFirstName;

    @Column(name = "child_lastname", length = 255)
    private String childLastName;

    @Column(name = "child_email", length = 255)
    private String childEmail;

    @Column(name = "child_mobile", length = 255)
    private String childMobile;

    @Column(name = "status")
    private String status;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "child_password", length = 255)
    private String childPassword;
    @Column(name = "parent_accountnumber")
    private String parentAccountNumber;

    @Column(name = "is_parent_wallet_usable")
    private Boolean isParentWalletUsable;

}