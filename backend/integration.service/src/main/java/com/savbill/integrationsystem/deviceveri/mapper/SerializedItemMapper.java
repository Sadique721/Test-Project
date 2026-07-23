package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.SerializedItemData;
import com.savbill.integrationsystem.deviceveri.model.SerializedItemDTO;

@Mapper
public abstract class SerializedItemMapper implements IBaseMapper<SerializedItemDTO, SerializedItemData> {

    @Override
    public abstract SerializedItemData dtoToDomain(SerializedItemDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract SerializedItemDTO domainToDTO(SerializedItemData domain, @Context CycleAvoidingMappingContext context);


}
