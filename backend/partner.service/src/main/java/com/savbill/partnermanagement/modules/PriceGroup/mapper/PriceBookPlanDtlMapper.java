package com.savbill.partnermanagement.modules.PriceGroup.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookPlanDetail;
import org.mapstruct.Mapper;

@Mapper
public interface PriceBookPlanDtlMapper extends IBaseMapper<PriceBookPlanDetailDTO, PriceBookPlanDetail> {
}
