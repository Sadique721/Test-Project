package com.savbill.cpm.modules.ippool.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ippool.domain.IPPoolDtls;
import com.savbill.cpm.modules.ippool.model.IPPoolDtlsDTO;

@Mapper
public interface IPPoolDtlsMapper extends IBaseMapper<IPPoolDtlsDTO, IPPoolDtls> {
}
