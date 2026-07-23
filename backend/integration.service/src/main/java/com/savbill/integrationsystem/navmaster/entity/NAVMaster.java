package com.savbill.integrationsystem.navmaster.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.Auditable;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "tblmnavmaster")
@AllArgsConstructor
//@NoArgsConstructor
public class NAVMaster extends Auditable<Long> implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private Long id;

    @Column(name = "user_name")
    private String userName;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "pwd")
    private String pwd;
    @Column(name = "url")
    private String url;
    @Column(name = "status")
    private String status;
    @Column(name = "aggregation_frequency")
    private String aggregationFrequency;
    @Column(name = "batch_name")
    private String batchName;

    @OneToMany(targetEntity = NAVMasterAggregationParamMapping.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nav_master_id")
    @ToString.Exclude
    List<NAVMasterAggregationParamMapping> navMasterAggregationParamMappingList;

    @Column(name = "MVNOID")
    private Long mvnoId;

    @Column(name = "isdelete")
    private Boolean isdelete;

    @Transient
    Long identityKey;

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
        NAVMaster navMaster = (NAVMaster) o;
        return getId() != null && Objects.equals(getId(), navMaster.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
