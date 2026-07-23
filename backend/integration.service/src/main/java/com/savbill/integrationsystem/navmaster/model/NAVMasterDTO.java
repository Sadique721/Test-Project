package com.savbill.integrationsystem.navmaster.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.savbill.integrationsystem.navmaster.entity.NAVMasterAggregationParamMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NAVMasterDTO extends Auditable<Long> implements IBaseDto {

    private Long id;
    private String userName;
    private String serviceName;
    private String pwd;
    private String url;
    private String status;
    private String aggregationFrequency;
    private String batchName;
    private List<NAVMasterAggregationParamMapping> navMasterAggregationParamMappingList;
    private Long mvnoId;
    private Boolean isdelete = false;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Long getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }
}
