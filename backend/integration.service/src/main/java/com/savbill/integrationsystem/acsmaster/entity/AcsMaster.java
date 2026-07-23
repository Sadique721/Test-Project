package com.savbill.integrationsystem.acsmaster.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.Auditable;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "tblmacsmaster")
public class AcsMaster extends Auditable<AcsMaster> implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acs_id", length = 40)
    private Long id;

    @Column(name = "acs_name", length = 40)
    private String name;

    @Column(name = "acs_url", length = 40)
    private String url;

    @Column(name = "acs_username", length = 40)
    private String username;

    @Column(name = "acs_password", length = 40)
    private String password;

    @Column(name = "MVNOID", length = 40, updatable = false)
    private Long mvnoId;


    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = AcsMasterUrlParamMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "acs_master_id", referencedColumnName = "acs_id")
    List<AcsMasterUrlParamMapping> acsMasterUrlParamMappingList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = AcsMasterAPIMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "acs_master_id", referencedColumnName = "acs_id")
    List<AcsMasterAPIMapping> acsMasterAPIMappings;

    @Column(name = "vendor_id", length = 40, updatable = false)
    private Long vendorId;

    @Column(name = "isdelete")
    private Boolean isdelete;


    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isdelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isdelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AcsMaster acsMaster = (AcsMaster) o;
        return getId() != null && Objects.equals(getId(), acsMaster.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
