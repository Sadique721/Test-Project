package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.DiscountMapping;
import com.savbill.cpm.pojo.api.DiscountMappingPojo;

@Mapper
public interface DiscountPlanMappingMapper extends IBaseMapper<DiscountMappingPojo, DiscountMapping> {
}
