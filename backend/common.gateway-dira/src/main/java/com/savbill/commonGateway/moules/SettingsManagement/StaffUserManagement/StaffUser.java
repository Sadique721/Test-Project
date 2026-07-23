package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole.StaffAccessibleRoleMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping.StaffUserLocationMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserPlanServiceMapping.StaffUserPlanServiceMapping;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.Teams;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "tblmstaffuser")
@EntityListeners(AuditableListener.class)
public class StaffUser extends Auditable implements IBaseData<Integer> {

    @OneToMany(targetEntity = StaffUserPlanServiceMapping.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "staffmapping_id")
    List<StaffUserPlanServiceMapping> staffUserServiceMappings;
    @Transient
    Long businessUnitId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffid", nullable = false, length = 40)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String username;
    @Column(length = 40)
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

//We are Having those property already in Auditable
//    @CreationTimestamp
//    @DiffIgnore
//    @Column(name = "created_on", nullable = false, updatable = false)
//    private LocalDateTime createdate;
//
//    @UpdateTimestamp
//    @DiffIgnore
//    @Column(name = "lastmodified_on")
//    private LocalDateTime updatedate;
    @Column(name = "sstatus", nullable = false, length = 40)
    private String status;
    @Column(nullable = false)
    private LocalDateTime last_login_time;
    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;
    @Transient
    private String newpassword;
    @DiffIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltstaffrolerel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private Set<Role> roles = new HashSet<>();
    @OneToMany
    @JoinColumn(name = "staffid")
    private List<StaffAccessibleRoleMapping> accessibleRoles = new ArrayList<>();
    private String otp;
    private LocalDateTime otpvalidate;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltteamusermapping", joinColumns = @JoinColumn(name = "staffid", referencedColumnName = "staffid"), inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "team_id"))
    @ToString.Exclude
    private Set<Teams> team = new HashSet<>();
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean sysstaff = false;
    @Transient
    private String fullName;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "service_area_id")
    private ServiceArea servicearea;
    //@JsonBackReference
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "businessunitid")
    private BusinessUnit businessUnit;
    @Column(name = "access_level_group_name")
    private String tacacsAccessLevelGroup;
    @JsonIgnore
//    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "parent_staff_id")
    private StaffUser staffUserparent;
    @JsonIgnore
//    @DiffIgnore
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "staffUserparent")
    private List<StaffUser> staffUserChildList = new ArrayList<>();
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "branchid", nullable = true, length = 40)
    private Integer branchId;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstaffservicearearel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstaffbusinessunitrel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "businessunitid")})
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
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
    @Lob
    @Column(name = "profile_image", columnDefinition = "BLOB", length = 30000)
    private byte[] profileImage;
    @Column(name = "department")
    private Integer department;
    @Column(name = "uuid")
    private String uuid;
    @Transient
    @Column(name = "event_name")
    private String eventName;
    @Transient
    @Column(name = "event_id")
    private Long eventId;
    @Transient
    @Column(name = "is_notification_required")
    private Boolean isNotificationRequired = false;
    @Column(name = "is_password_expired")
    private Boolean isPasswordExpired = false;
    @Column(name = "password_date")
    private LocalDateTime passwordDate;
    @Column(name = "mvno_deactivation_flag")
    private Boolean mvnoDeactivationFlag;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstafflocationmapping", joinColumns = {@JoinColumn(name = "staff_id")}, inverseJoinColumns = {@JoinColumn(name = "id")})
    private List<StaffUserLocationMapping> staffUserLocationMappings = new ArrayList<>();

    public StaffUser(Integer id) {
        this.id = id;
    }

    public StaffUser(StaffUser staffUser) {
        this.id = staffUser.getId();
//        this.updatedate = staffUser.getUpdatedate();
        this.username = staffUser.getUsername();
        this.firstname = staffUser.getFirstname();
        this.lastname = staffUser.getLastname();
        this.status = staffUser.getStatus();
        this.branchId = staffUser.getBranchId();
        this.password = staffUser.getPassword();
        this.email = staffUser.getEmail();
        this.phone = staffUser.getPhone();
        this.countryCode = staffUser.getCountryCode();
//        this.createdate = staffUser.getCreatedate();
        this.failcount = staffUser.getFailcount();
        this.last_login_time = staffUser.getLast_login_time();
        this.partnerid = staffUser.getPartnerid();
        this.newpassword = staffUser.getNewpassword();
        this.otp = staffUser.getOtp();
        this.otpvalidate = staffUser.getOtpvalidate();
        this.uuid = staffUser.getUuid();
        this.eventName = staffUser.getEventName();
        this.isPasswordExpired = staffUser.getIsPasswordExpired();
        this.passwordDate = staffUser.getPasswordDate();
        this.isNotificationRequired = staffUser.getIsNotificationRequired();
        this.staffUserLocationMappings = staffUser.getStaffUserLocationMappings();
        this.staffUserparent = new StaffUser(staffUser.getStaffUserparent() != null ? staffUser.getStaffUserparent().getId() : null);
        this.staffUserServiceMappings = staffUser.getStaffUserServiceMappings();
        this.hrmsId = staffUser.getHrmsId();
    }

    public StaffUser(Integer id, String username, String firstname, String lastname, String email, String phone, String countryCode, String status, Integer partnerid, Boolean isDelete, Integer mvnoId, Integer branchId, String hrmsId, Integer department, Boolean mvnoDeactivationFlag) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.countryCode = countryCode;
        this.status = status;
        this.partnerid = partnerid;
        this.isDelete = isDelete;
        this.mvnoId = mvnoId;
        this.branchId = branchId;
        this.hrmsId = hrmsId;
        this.department = department;
        this.mvnoDeactivationFlag = mvnoDeactivationFlag;
    }

    public StaffUser(Integer id, String firstname, String lastname, String phone) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.fullName = firstname + " " + lastname;
    }

    public StaffUser(Integer id, String firstname, String lastname, String phone, String username) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.username = username;
        this.fullName = firstname + " " + lastname;
    }

    public StaffUser(Integer id, String firstname, String lastname, String phone, Integer partnerid, String username) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.fullName = firstname + " " + lastname;
        this.partnerid = partnerid;
        this.username = username;
    }

    public StaffUser(Integer id, String password, String email, String phone, String countryCode, Integer mvnoId, Long businessUnitId) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.countryCode = countryCode;
        this.mvnoId = mvnoId;
        this.businessUnitId = businessUnitId;
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaffUser other = (StaffUser) obj;
        return Objects.equals(this.id, other.id);
    }

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != this.getFirstname() && !this.getFirstname().isEmpty() && this.getFirstname().trim().length() > 0) {
                fullName = this.getFirstname();
            }
            if (null != this.getLastname() && !this.getLastname().isEmpty() && this.getLastname().trim().length() > 0) {
                fullName += " " + this.getLastname();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "StaffUser [id=" + id + "]";
    }

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
