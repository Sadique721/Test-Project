package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.SubscriberaddressrelData;
import com.savbill.integrationsystem.deviceveri.model.SubscriberaddressrelDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class SubscriberaddressrelMapper implements IBaseMapper<SubscriberaddressrelDTO, SubscriberaddressrelData> {

    @Override
    public abstract SubscriberaddressrelData dtoToDomain(SubscriberaddressrelDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract SubscriberaddressrelDTO domainToDTO(SubscriberaddressrelData domain, @Context CycleAvoidingMappingContext context);

}
