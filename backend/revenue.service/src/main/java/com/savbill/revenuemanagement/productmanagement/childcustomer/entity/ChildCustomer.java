package com.savbill.revenuemanagement.productmanagement.childcustomer.entity;

import com.savbill.revenuemanagement.productmanagement.childcustomer.UpdateChildCustometMessesge;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblchildcustomer")
public class ChildCustomer {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "last_modify_by_staff_id")
    private Long lastModifyByStaffId;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @Column(name = "modify_date_time")
    private LocalDateTime modifyDateTime;

    @Column(name = "create_by_staff_id")

    private Long createByStaffId;

    @Column(name = "mvno_id")

    private Long mvnoId;

    @Column(name = "parent_cust_id")

    private Long parentCustId;

    @Column(name = "wallet")
    private Double wallet;

    @Column(name = "status")
    private String status;

    @Column(name = "bu_Id")
    private Integer buId;

    @Column(name = "isdeleted")
    private Boolean isdeleted;

    @Column(name = "isparent")
    private Boolean isParent;

    @Column(name = "mobilenumber")
    private String mobileNumber;

    @Column(name = "parent_accountnumber")
    private String parentAccountNumber;
    public ChildCustomer(UpdateChildCustometMessesge data) {
        this.mobileNumber = data.getMobileNumber();
        this.status = data.getStatus();
        this.email= data.getEmail();
        this.isParent= data.getIsParent();
        this.firstName= data.getFirstName();
        this.lastName= data.getLastName();
        this.userName=data.getUserName();
        this.password=data.getPassword();
        this.parentCustId=data.getParentCustId();

    }

    public ChildCustomer() {

    }
}
