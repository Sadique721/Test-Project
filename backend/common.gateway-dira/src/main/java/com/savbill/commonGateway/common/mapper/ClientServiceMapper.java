package com.savbill.commonGateway.common.mapper;


import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.model.ClientServicePojo;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
    @Mappings({@Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")})
    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);

    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);
}
