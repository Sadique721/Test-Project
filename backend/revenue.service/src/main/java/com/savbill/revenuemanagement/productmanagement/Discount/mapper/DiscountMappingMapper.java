package com.savbill.revenuemanagement.productmanagement.Discount.mapper;


import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountPlanMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountPlanMappingPojo;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountMappingMapper extends IBaseMapper<DiscountPlanMappingPojo, DiscountPlanMapping> {
    @Override
    public abstract DiscountPlanMappingPojo domainToDTO(DiscountPlanMapping data, CycleAvoidingMappingContext context);
 @Override
    public abstract DiscountPlanMapping dtoToDomain(DiscountPlanMappingPojo dtoData, CycleAvoidingMappingContext context);


}
