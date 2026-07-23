package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PopManagementDTO extends Auditable implements IBaseDto {

    private Long id;
    private String name;
    private String latitude;
    private String longitude;
    private List<Long> serviceAreaIdsList;
    private List<String> serviceAreaNameList = new ArrayList<>();
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

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
