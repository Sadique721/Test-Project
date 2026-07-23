package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.DiscountPlanMapping;
import com.savbill.cpm.pojo.api.DiscountPlanMappingPojo;

@Mapper
public interface DiscountMappingMapper extends IBaseMapper<DiscountPlanMappingPojo, DiscountPlanMapping> {
}
