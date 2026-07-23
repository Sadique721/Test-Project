package com.savbill.revenuemanagement.core.MvnoDiscountManagement;

import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = MvnoMapperHelper.class)
public abstract class MvnoDiscountMapper implements IBaseMapper<MvnoDiscountMappingDTO, MvnoDiscountMapping> {

    @Override
    @Mappings({
            @Mapping(source = "dtoData.mvnoId", target = "mvno"),
    })
    public abstract MvnoDiscountMapping dtoToDomain(MvnoDiscountMappingDTO dtoData, CycleAvoidingMappingContext context);


}
