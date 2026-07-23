package com.savbill.cpm.modules.tickets.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.tickets.domain.TatQueryFieldMapping;
import com.savbill.cpm.modules.tickets.model.TatQueryFieldMappingDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TatQueryFieldMapper extends IBaseMapper<TatQueryFieldMappingDTO, TatQueryFieldMapping> {
}
