package com.savbill.cpm.modules.ippool.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ippool.domain.IPAllocation;
import com.savbill.cpm.modules.ippool.model.IPAllocationDTO;

@Mapper
public interface IPAllocationMapper extends IBaseMapper<IPAllocationDTO, IPAllocation> {
}
