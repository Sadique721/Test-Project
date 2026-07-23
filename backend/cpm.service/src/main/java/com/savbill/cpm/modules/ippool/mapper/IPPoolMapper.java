package com.savbill.cpm.modules.ippool.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ippool.domain.IPPool;
import com.savbill.cpm.modules.ippool.model.IPPoolDTO;
import org.mapstruct.Mapping;

@Mapper
public interface IPPoolMapper extends IBaseMapper<IPPoolDTO, IPPool> {
    @Override
    @Mapping(target = "displayId", source = "poolId")
    @Mapping(target = "displayPoolName", source = "poolName")
    public abstract IPPoolDTO domainToDTO(IPPool domain, @Context CycleAvoidingMappingContext context);
}
