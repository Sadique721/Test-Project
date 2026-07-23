package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.WareHouse;
import com.savbill.integrationsystem.rms.model.WareHouseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class WareHouseMapper implements IBaseMapper<WareHouseDto, WareHouse> {
    @Override
    public abstract WareHouseDto domainToDTO(WareHouse wareHouse, CycleAvoidingMappingContext context);

    @Override
    public abstract WareHouse dtoToDomain(WareHouseDto dtoData, CycleAvoidingMappingContext context) ;

    @Override
    public List<WareHouseDto> domainToDTO(List<WareHouse> wareHouses, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public WareHouse updateDTOToDomain(WareHouseDto wareHouseDto, WareHouse wareHouse, CycleAvoidingMappingContext context) {
        return null;
    }
}
