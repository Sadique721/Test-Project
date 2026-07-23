package com.savbill.revenuemanagement.productmanagement.servicePlan.domain;


import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "tblmservicechargemapping")
@Data
@NoArgsConstructor
public class ServiceChargeMapping implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = Charge.class)
    @JoinColumn(name = "chargeid", referencedColumnName = "CHARGEID", updatable = true, insertable = true)
    private Charge Charge;

    @ManyToOne(targetEntity = Services.class)
    @JoinColumn(name = "servicesid", referencedColumnName = "serviceid", updatable = true, insertable = true)
    private Services services;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

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
}
