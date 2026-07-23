package com.savbill.inventorymanagement.modules.CasMaster;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class CasMasterDTO extends Auditable implements IBaseDto {
    Long id;
    String casname;
    String status;
    Boolean isDeleted = false;
    Integer mvnoId;
    String endpoint;
    Long buId;
//    List<CasPackageMapping> casPackageMappings;

//    List<CasParameterMapping> casParameterMappings;

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

}
