package com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@Table(name="tbltbulkconsumptionmacmapping")
@EntityListeners(AuditableListener.class)
public class BulkConsumptionMapping extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bulkconsumptionid", nullable = false)
    private Long bulkConsumptionId;

    @Column(name = "mac_mapping_id")
    private  Long macMappingId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
