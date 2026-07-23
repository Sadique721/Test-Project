package com.savbill.cpm.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.pojo.ClientServicePojo;
import org.mapstruct.Mapping;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);

    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);
}
