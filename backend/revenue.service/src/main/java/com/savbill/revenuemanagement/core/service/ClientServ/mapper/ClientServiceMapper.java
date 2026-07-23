package com.savbill.revenuemanagement.core.service.ClientServ.mapper;


import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.dto.ClientServicePojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
//    @Mappings({
//            //@Mapping(target = "displayId", source = "id"),
//            @Mapping(target = "displayName", source = "name")
//    })


    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);
}
