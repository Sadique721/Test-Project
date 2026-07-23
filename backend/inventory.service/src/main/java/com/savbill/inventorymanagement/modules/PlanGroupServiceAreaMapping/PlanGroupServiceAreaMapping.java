package com.savbill.inventorymanagement.modules.PlanGroupServiceAreaMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "tbltplangroupserviceareamapping")
@Data
@NoArgsConstructor
public class PlanGroupServiceAreaMapping extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
