package com.savbill.partnermanagement.customers;


import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblcustomers")
@EntityListeners(AuditableListener.class)
public class Customers extends Auditable {

    @Id
    @Column(name = "custid", nullable = false, length = 40)
    private Integer id;

    //@Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(length = 40)
    private String password;

    @Column(nullable = false, length = 40)
    private String firstname;

    @Column(nullable = false, length = 40)
    private String lastname;

    @Column(name = "custname", nullable = false, length = 40)
    private String custname;

    @Column(name = "email" , nullable = false, unique = true)
    private String email;

    @Column(name = "mobile" ,nullable = false, length = 10)
    private String mobile;

    @Column(name = "country_code", nullable = false, length = 40)
    private String countryCode;

    @Column(name = "servicearea_id", nullable = false, length = 40)
    private Integer serviceAreaId;

    @Column(name = "network_device_id", nullable = false, length = 40)
    private Integer networkdevicesId;

    @Column(name = "cstatus", nullable = false, length = 100)
    private String status;

    @Column(name = "customertype", nullable = false, length = 100)
    private String custtype; //Postpaid,Prepaid

    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "lco_id")
    private Integer lcoId;

    @Column(name = "is_from_pwc")
    private Boolean is_from_pwc;

    @Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    private Long oltslotid;

    private Long oltportid;

    @Column(name = "partnerid")
    private Integer partnerId;

    @Column(name = "plan_purchase_type")
    private String planPurchaseType;

    @Column(name = "calendartype", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @Column(name = "dunning_category", nullable = false, length = 40)
    private String dunningCategory;

    @Column(name = "feasibility_required")
    private String feasibilityRequired;

    @Column(name = "VALLEY_TYPE")
    private String valleyType;

    @Column(name = "CUSTOMER_AREA")
    private String customerArea;

    @Column(nullable = false, length = 75)
    private String custcategory;

    @Column(name="blockno")
    private String blockNo;

    private String selfcarepwd;

    private String contactperson;
}
