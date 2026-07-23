package com.savbill.ticketmanagement.core.modules.ResolutionReasons.model;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.dto.IBaseDto;
import com.savbill.ticketmanagement.core.modules.tickets.domain.ResoSubCategoryMapping;
import com.savbill.ticketmanagement.core.modules.tickets.domain.ResoultionFileMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResolutionReasonsDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;

    private Integer mvnoId;
    private Long buId;
    
    private Integer lcoId;
    private List<ResoSubCategoryMapping> resoSubCategoryMappingList;
    private List<RootCauseResolutionMapping> rootCauseResolutionMappingList;
    private List<ResoultionFileMapping> resoultionFileMappings = new ArrayList<>();
    private String latitude;
    private String longitude;

//    private Integer lcoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }
}
