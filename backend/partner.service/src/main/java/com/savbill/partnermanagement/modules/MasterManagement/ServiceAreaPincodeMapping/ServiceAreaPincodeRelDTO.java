package com.savbill.partnermanagement.modules.MasterManagement.ServiceAreaPincodeMapping;

import com.savbill.partnermanagement.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceAreaPincodeRelDTO implements IBaseDto {

    private Long id;
    private Long serviceAreaId;
    private Long pincodeId;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {return id;}

    @Override
    public Integer getMvnoId() { return mvnoId; }

    @Override
    public void setMvnoId(Integer mvnoId) { this.mvnoId = mvnoId;  }

    @Override
    public Long getBuId() {
        return null;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
