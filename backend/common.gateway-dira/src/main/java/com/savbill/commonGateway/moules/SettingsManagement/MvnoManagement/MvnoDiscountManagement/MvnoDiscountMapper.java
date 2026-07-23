package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDiscountManagement;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoMapperHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = MvnoMapperHelper.class)
public abstract class MvnoDiscountMapper implements IBaseMapper<MvnoDiscountMappingDTO, MvnoDiscountMapping> {

    @Override
    @Mappings({
            @Mapping(source = "dtoData.mvnoId", target = "mvno"),
    })
    public abstract MvnoDiscountMapping dtoToDomain(MvnoDiscountMappingDTO dtoData, CycleAvoidingMappingContext context);

}
