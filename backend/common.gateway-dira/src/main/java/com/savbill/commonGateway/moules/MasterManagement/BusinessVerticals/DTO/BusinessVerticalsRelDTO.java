package com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.DTO;


import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessVerticalsRelDTO implements IBaseDto
{
    private Long id;
    private Long businessVerticalsId;
    private Long region_id;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {return id;}

    @Override
    public Integer getMvnoId() { return mvnoId; }

    @Override
    public void setMvnoId(Integer mvnoId) { this.mvnoId = mvnoId;  }
}
