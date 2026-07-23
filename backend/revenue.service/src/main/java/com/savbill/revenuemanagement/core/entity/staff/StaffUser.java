package com.savbill.revenuemanagement.core.entity.staff;



import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "tblstaffuser")
@EntityListeners(AuditableListener.class)
public class StaffUser extends Auditable {

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

    /*@CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on")
    private LocalDateTime updatedate;*/

    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;

    private String otp;

    private LocalDateTime otpvalidate;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(columnDefinition = "Boolean default false",nullable = false)
    private Boolean sysstaff = false;


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
    @Transient
    private String fullName;
    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;
    @Column(name = "hrms_id", length = 50)
    private String hrmsId;
    @Lob
    @Column(name = "profile_image", columnDefinition = "BLOB", length = 30000)
    private byte[] profileImage;

    @Column(name = "department")
    private String department;


    //@ManyToMany(fetch = FetchType.EAGER)
    //@JoinTable(name = "tblstaffrolerel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    //private Set<Role> roles = new HashSet<>();

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != this.getFirstname() && !this.getFirstname().isEmpty() && this.getFirstname().trim().length() > 0) {
                fullName = this.getFirstname();
            }
            if (null != this.getLastname() && !this.getLastname().isEmpty() && this.getLastname().trim().length() > 0) {
                fullName += " " + this.getLastname() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
