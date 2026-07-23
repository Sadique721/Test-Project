package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.PostpaidPlanData;
import com.savbill.integrationsystem.deviceveri.model.PostpaidPlanDTO;

@Mapper
public abstract class PostpaidPlanMapper implements IBaseMapper<PostpaidPlanDTO, PostpaidPlanData> {

    @Override
    public abstract PostpaidPlanData dtoToDomain(PostpaidPlanDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PostpaidPlanDTO domainToDTO(PostpaidPlanData domain, @Context CycleAvoidingMappingContext context);


}
