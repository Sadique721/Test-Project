package com.savbill.integrationsystem.nms.entity;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public class ConnfigurationMapper implements IBaseMapper<ConnfigurationDTO, Connfiguration> {
    @Override
    public ConnfigurationDTO domainToDTO(Connfiguration connfiguration, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public Connfiguration dtoToDomain(ConnfigurationDTO dtoData, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public List<ConnfigurationDTO> domainToDTO(List<Connfiguration> connfigurations, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public Connfiguration updateDTOToDomain(ConnfigurationDTO connfigurationDTO, Connfiguration connfiguration, CycleAvoidingMappingContext context) {
        return null;
    }
}
