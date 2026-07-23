package com.savbill.taskmanagement.core.modules.PlanService.dto;

import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.BusinessUnit.domain.BusinessUnit;

import javax.validation.constraints.NotNull;

public class PlanServiceDto extends Auditable implements IBaseDto {

    private Integer id;

    @NotNull
    private String name;
    private String planTYpe;
    private Integer validity;


    private String quota;
    private String stml;
    private String icname;
    private String iccode;
    private Long businessunitid;
    private BusinessUnit businessUnit;
    private Boolean isQoSV = true;
    private String expiry;
    private String ledgerId;
//    private List<Long> pcategoryId;
    //    private List<ServiceParamMappingDTO> serviceParamMappingList;
    private boolean is_dtv;
    //    private ProductCategory productCategory;
    private Long investmentid;
//    private List<ProductCategory> productCategory;

    public boolean getis_dtv() {
        return is_dtv;
    }

    public void setis_dtv(boolean is_dtv) {
        this.is_dtv = is_dtv;
    }

    private Integer displayId;
    private String displayName;

    private Boolean feasibility;
    private Boolean poc;
    private Boolean installation;
    private Boolean provisioning;
    private Boolean isPriceEditable;
    private Long feasibilityTeamId;
    private Long pocTeamId;
    private Long installationTeamId;
    private Long provisioningTeamId;

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id)
                ;
    }

    @Override
    public Integer getMvnoId() {
        return getMvnoId();
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.setMvnoId(mvnoId);
    }

    @Override
    public Long getBuId() {
        return null;
    }
}
