package com.savbill.inventorymanagement.modules.StaffUser;


import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.Role.Role;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblmstaffuser")
public class StaffUser extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "sstatus", nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private LocalDateTime last_login_time;

    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltstaffrolerel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tbltteamusermapping",
            joinColumns = @JoinColumn(name = "staffid", referencedColumnName = "staffid"),
            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "team_id"))
    @ToString.Exclude
    @JsonBackReference
    private Set<Teams> team = new HashSet<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Transient
    private String fullName;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "branchid", nullable = true, length = 40)
    private Long branchId;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstaffservicearearel", joinColumns = {@JoinColumn(name = "staffid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltstaffbusinessunitrel", joinColumns = {@JoinColumn(name = "staffid")}
            , inverseJoinColumns = {@JoinColumn(name = "businessunitid")})
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();


    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 40)
    private String phone;



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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

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

    public StaffUser(Integer id, String username, String password, String firstname, String lastname, String status,Integer partnerid) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
        this.partnerid = partnerid;
        this.fullName = firstname + lastname;
    }


    @Override
    public String toString() {
        return "StaffUser [id=" + id + "]";
    }

    public StaffUser(Integer id)
    {
        this.id=id;
    }

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }
}
