package com.savbill.commonGateway.moules.TeamsManagement.Teams;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.PartnerManagement.Partner;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tblmteams")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class Teams extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name")
    private String name;

    @Column(name = "team_status")
    private String status;

    @Column(name = "teamtype")
    private String teamType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbltteamusermapping", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "staffid"))
    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<StaffUser> staffUser = new HashSet<>();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentteamid")
    private Teams parentTeams;

    @Transient
    private String cafStatus;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name = "product")
    private String product;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    @Override
    public void setBuId(Long buId) {

    }

    public Teams getParentTeams() {
        if (parentTeams == null) {
            return null;
        } else {
            return parentTeams;
        }
    }
    public Teams(Long id){
        this.id=id;
    }

    @Override
    public String toString() {
        return "Teams [id=" + id + "]";
    }
    public Teams(Long id,String name,Long parentteamId){
        this.id=id;
        this.name=name;
        this.parentTeams=new Teams(parentteamId);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teams)) return false;
        Teams team = (Teams) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
