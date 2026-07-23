package com.savbill.cpm.modules.InventoryManagement.PopManagement.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PopManagementDTO extends Auditable implements IBaseDto {

    private Long id;
    private String name;
    private String latitude;
    private String longitude;
    List<Long> serviceAreaIdsList;
    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private String popCode;

    private Integer displayId;
    private String displayName;

    @Override
    public Long getIdentityKey() {
        return id;
    }
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
