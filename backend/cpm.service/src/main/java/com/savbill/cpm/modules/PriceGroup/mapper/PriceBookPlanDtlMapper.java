package com.savbill.cpm.modules.PriceGroup.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.cpm.modules.PriceGroup.model.PriceBookPlanDetailDTO;

@Mapper
public interface PriceBookPlanDtlMapper extends IBaseMapper<PriceBookPlanDetailDTO,PriceBookPlanDetail> {
}
