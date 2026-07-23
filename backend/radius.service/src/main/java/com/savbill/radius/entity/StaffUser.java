package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.StaffUserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblstaffuser")
public class StaffUser {

    @Id
    @Column(name = "staffid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = true, length = 40)
    private String password;

    @Column(nullable = true, length = 40)
    private String firstname;

    @Column(nullable = true, length = 40)
    private String lastname;

    @Column(nullable = true, length = 40)
    private String email;

    @Column(nullable = true, length = 40)
    private String phone;

    @Column(nullable = true, length = 40)
    private Integer failcount = 0;

    @Column(name = "sstatus", nullable = false, length = 40)
    private String status;

    @Column(nullable = true)
    private LocalDateTime last_login_time;

    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = true)
    private Boolean isDelete = false;

    private String otp;

    private LocalDateTime otpvalidate;

    @Column(columnDefinition = "Boolean default false", nullable = true)
    private Boolean sysstaff = false;

    @Column(name = "createbyname", nullable = true, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = true, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = true, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = true, length = 40)
    private Integer lastModifiedById;


    @Column(name = "CREATEDATE", nullable = true, updatable = true)
    private LocalDateTime createdate;

    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;


    @Column(name = "service_area_id", nullable = true)
    private Integer serviceareaId;


    @Column(name = "parent_staff_id", nullable = true)
    private Integer staffUserparent;

    @Column(name = "MVNOID", nullable = true, length = 40)
    private Integer mvnoId;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblstaffservicearearel", joinColumns = {@JoinColumn(name = "staffid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstaffbusinessunitrel", joinColumns = {@JoinColumn(name = "staffid")}
    , inverseJoinColumns = {@JoinColumn(name = "businessunitid")})
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();


    public StaffUser(StaffUserMessage staffUserMessage) {
        Map<String, Object> stringObjectMap = staffUserMessage.getCustomerData();
        if (stringObjectMap.get("id") != null) {
            this.id = Integer.parseInt(stringObjectMap.get("id").toString());
        }
        if (stringObjectMap.get("username") != null) {
            this.username = stringObjectMap.get("username").toString();
        }
        if (stringObjectMap.get("password") != null) {
            this.password = stringObjectMap.get("password").toString();
        }
        if (stringObjectMap.get("firstname") != null) {
            this.firstname = stringObjectMap.get("firstname").toString();
        }
        if (stringObjectMap.get("lastname") != null) {
            this.lastname = stringObjectMap.get("lastname").toString();
        }
        if (stringObjectMap.get("email") != null) {
            this.email = stringObjectMap.get("email").toString();
        }
        if (stringObjectMap.get("phone") != null) {
            this.phone = stringObjectMap.get("phone").toString();
        }
        if (stringObjectMap.get("failcount") != null) {
            this.failcount = (Integer) stringObjectMap.get("failcount");
        }
        if (stringObjectMap.get("status") != null) {
            this.status = stringObjectMap.get("status").toString();
        }
        if (stringObjectMap.get("last_login_time") != null) {
            this.last_login_time = (LocalDateTime) stringObjectMap.get("last_login_time");
        }
        if (stringObjectMap.get("partnerid") != null) {
            this.partnerid = (Integer) stringObjectMap.get("partnerid");
        }
        if (stringObjectMap.get("isDelete") != null) {
            this.isDelete = (Boolean) stringObjectMap.get("isDelete");
        }
        if (stringObjectMap.get("otp") != null) {
            this.otp = (String) stringObjectMap.get("otp");
        }
        if (stringObjectMap.get("otpvalidate") != null) {
            this.otpvalidate = (LocalDateTime) stringObjectMap.get("otpvalidate");
        }
        if (stringObjectMap.get("sysstaff") != null) {
            this.sysstaff = (Boolean) stringObjectMap.get("sysstaff");
        }
        if (stringObjectMap.get("sysstaff") != null) {
            this.sysstaff = (Boolean) stringObjectMap.get("sysstaff");
        }
        if (stringObjectMap.get("createdate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.createdate = LocalDateTime.parse(stringObjectMap.get("createdate").toString(), formatter);

        }
        if (stringObjectMap.get("updatedate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.updatedate = LocalDateTime.parse(stringObjectMap.get("updatedate").toString(), formatter);
        }
        if (stringObjectMap.get("createdbyid") != null) {
            this.createdById = Integer.parseInt(stringObjectMap.get("createdbyid").toString());
        }
        if (stringObjectMap.get("lastmodifiedbyid") != null) {
            this.lastModifiedById = Integer.parseInt(stringObjectMap.get("lastmodifiedbyid").toString());
        }
        if (stringObjectMap.get("createdByName") != null) {
            this.createdByName = (stringObjectMap.get("createdByName").toString());
        }
        if (stringObjectMap.get("lastmodifiedbyname") != null) {
            this.lastModifiedByName = (stringObjectMap.get("lastmodifiedbyname").toString());
        }
        if (stringObjectMap.get("mvnoId") != null) {
            this.mvnoId = Integer.parseInt(stringObjectMap.get("mvnoId").toString());
        }
        if (stringObjectMap.get("parent_staff_id") != null) {
            this.staffUserparent = (Integer) stringObjectMap.get("parent_staff_id");
        }
        if (stringObjectMap.get("serviceAreaNameList") != null) {
            List serviceAreaNameList = (List) stringObjectMap.get("serviceAreaNameList");
            for (int i = 0; i < serviceAreaNameList.size(); i++) {
                ServiceArea serviceArea = new ServiceArea((Map) serviceAreaNameList.get(i));
                this.serviceAreaNameList.add(serviceArea);
            }
        }
        if (stringObjectMap.get("businessUnitNameList") != null) {
            List businessUnitNameList = (List) stringObjectMap.get("businessUnitNameList");
            for (int i = 0; i < businessUnitNameList.size(); i++) {
                BusinessUnit businessUnit = new BusinessUnit((Map) businessUnitNameList.get(i));
                this.businessUnitNameList.add(businessUnit);
            }
        }
    }
}

