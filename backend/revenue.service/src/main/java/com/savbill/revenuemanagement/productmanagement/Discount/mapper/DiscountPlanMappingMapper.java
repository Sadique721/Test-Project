package com.savbill.revenuemanagement.productmanagement.Discount.mapper;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountMappingPojo;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountPlanMappingMapper extends IBaseMapper<DiscountMappingPojo, DiscountMapping> {
}
