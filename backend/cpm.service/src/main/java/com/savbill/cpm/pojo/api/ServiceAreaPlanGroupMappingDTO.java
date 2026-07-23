package com.savbill.cpm.pojo.api;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ServiceAreaPlanGroupMappingDTO implements IBaseDto {


    private Long id;
    private Integer plangroupid;
    private Long service_area_id;

    private List<ServiceArea> Serviceareaid=new ArrayList<>();

    private List<String> ServiceareaName = new ArrayList<>();

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
