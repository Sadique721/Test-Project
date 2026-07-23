package com.savbill.cpm.modules.Cas.Model;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.modules.Cas.Domain.CasPackageMapping;
import com.savbill.cpm.modules.Cas.Domain.CasParameterMapping;
import lombok.Data;

import java.util.List;

@Data
public class CasMasterDTO extends Auditable implements IBaseDto2 {
    Long id;
    String casname;
    String status;
    Boolean isDeleted = false;
    Integer mvnoId;
    String endpoint;
    Long buId;
    List<CasPackageMapping> casPackageMappings;

    List<CasParameterMapping> casParameterMappings;

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
