package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.SaveStaffUserSharedDataMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tblstaffuser")
public class StaffUser{
    @Id
    @Column(name = "staffid", nullable = false, length = 40)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String username;
    @Column(nullable = false, length = 40)
    private String password;
    @Column(nullable = false, length = 40)
    private String firstname;
    @Column(nullable = false, length = 40)
    private String lastname;
    @Column(nullable = false, length = 40)
    private String email;
    @Column(nullable = false, length = 40)
    private String phone;
    private String countryCode;
    @Column(nullable = false, length = 40)
    private Integer failcount = 0;
    @Column(name = "sstatus", nullable = false, length = 40)
    private String status;
    @Column(nullable = false)
    private LocalDateTime last_login_time;
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;
    @UpdateTimestamp
    @Column(name = "lastmodified_on")
    private LocalDateTime updatedate;
    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;
    @Transient
    private String newpassword;
    private String otp;
    private LocalDateTime otpvalidate;
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(columnDefinition = "Boolean default false",nullable = false)
    private Boolean sysstaff = false;
    @Transient
    private String fullName;
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    @Column(name = "branchid", nullable = true, length = 40)
    private Integer branchId;

    @Column(name = "total_collected", nullable = false, length = 40)
    private Double totalCollected;

    @Column(name = "total_transferred", nullable = false, length = 40)
    private Double totalTransferred;

    @Column(name = "available_amount", nullable = false, length = 40)
    private Double availableAmount;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;
    @Column(name = "hrms_id", length = 50)
    private String hrmsId;

    public StaffUser(SaveStaffUserSharedDataMessage message){
        this.id=message.getId();
        this.username=message.getUsername();
        this.password=message.getPassword();
        this.firstname=message.getFirstname();
        this.lastname=message.getLastname();
        this.email=message.getEmail();
        this.phone=message.getPhone();
        this.countryCode=message.getCountryCode();
        this.status=message.getStatus();
        if (message.getLast_login_time() != null && !message.getLast_login_time().equals("null")) {
            this.last_login_time = LocalDateTime.parse(message.getLast_login_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        this.partnerid=message.getPartnerid();
        this.isDelete=message.getIsDelete();
        this.mvnoId=message.getMvnoId();
        this.branchId=message.getBranchId();
    }
}
