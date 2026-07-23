package com.savbill.taskmanagement.core.modules.ClientServ.mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.taskmanagement.core.modules.ClientServ.dto.ClientServicePojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
    @Mappings({
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")
    })
    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);

    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);
}
