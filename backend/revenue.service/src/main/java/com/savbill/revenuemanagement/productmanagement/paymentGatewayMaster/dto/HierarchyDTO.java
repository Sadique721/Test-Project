package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.dto;

import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyDTO implements IBaseDto {


    private Long id;

    //private String  flowName;

    //private String status;

    private String hierarchyName;

    private String eventName;

//    private List<TeamHierarchyMapping> teamHierarchyMappingList ;

    private Integer mvnoId;

    private Boolean isDeleted = false;

    private Long buId;

    private Integer lcoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
