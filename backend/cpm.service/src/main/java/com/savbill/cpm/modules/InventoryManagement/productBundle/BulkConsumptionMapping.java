package com.savbill.cpm.modules.InventoryManagement.productBundle;
import com.savbill.cpm.core.data.IBaseData;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@Table(name="tbltbulkconsumptionmapping")
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
