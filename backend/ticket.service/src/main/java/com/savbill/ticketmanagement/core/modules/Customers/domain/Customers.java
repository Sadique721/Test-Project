package com.savbill.ticketmanagement.core.modules.Customers.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.modules.CustomerAddress.domain.CustomerAddress;
import com.savbill.ticketmanagement.core.modules.Plan.domain.CustPlanMappping;
import com.savbill.ticketmanagement.core.modules.PlanService.domain.CustomerServiceMapping;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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


    @Column(name = "service_area_id", nullable = false, length = 40)
    private Integer serviceAreaId;

//    @ManyToOne
//    @JoinColumn(name = "network_device_id")
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


//    private transient String partnerName;
//
//    private transient String serviceAreName;

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

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "partner_id", nullable = false, length = 40, updatable = false)
    private Integer parnterId;

    @Column(name = "plan_purchase_type")
    private String planPurchaseType;

    @Column(name = "service_area_name")
    private String serviceAreaName;

    @Column(name = "partner_name")
    private String partnerName;

    @Column(name = "calendartype", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @Column(name = "dunning_category", length = 40)
    private String dunningCategory;


    @Column(name = "parent_cust_username", length = 40)
    private String parentCustUsername;

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

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "custid", orphanRemoval = true)
    private List<CustPlanMappping> planMappingList = new ArrayList<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "custId", orphanRemoval = true)
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
    @OrderBy("id")
    private List<CustomerAddress> addressList = new ArrayList<>();






}
