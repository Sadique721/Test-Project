package com.savbill.partnermanagement.modules.PlanGroup.domain;
import com.savbill.partnermanagement.core.data.IBaseData;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "tblmserviceareaplangroupmapping")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceAreaPlanGroupMapping implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = PlanGroup.class)
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid", updatable = true, insertable = true)
    private PlanGroup planGroup;

    @ManyToOne(targetEntity = ServiceArea.class)
    @JoinColumn(name = "service_area_id", referencedColumnName = "service_area_id", updatable = true, insertable = true)
    private ServiceArea serviceArea;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Transient
    private Integer plangroupId;

    @Transient
    private Long serviceAreaId;


    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }


    public ServiceAreaPlanGroupMapping(ServiceAreaPlanGroupMapping serviceAreaPlanGroupMapping,PlanGroup planGroup, ServiceArea serviceArea) {
        this.id = serviceAreaPlanGroupMapping.getId();
        this.isDeleted = serviceAreaPlanGroupMapping.getIsDeleted();
        this.planGroup = planGroup;
        this.serviceArea = serviceArea;
    }


}
