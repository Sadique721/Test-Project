package com.savbill.partnermanagement.modules.ClientServ.mapper;


import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.ClientServ.domain.ClientService;
import com.savbill.partnermanagement.modules.ClientServ.dto.ClientServicePojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
//    @Mappings({
//            //@Mapping(target = "displayId", source = "id"),
//            @Mapping(target = "displayName", source = "name")
//    })


    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);

//    @Mapping(target = "displayId", source = "id")
//    @Mapping(target = "displayName", source = "name")
//    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);
}
