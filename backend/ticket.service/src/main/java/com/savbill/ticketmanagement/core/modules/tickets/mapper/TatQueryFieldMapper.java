package com.savbill.ticketmanagement.core.modules.tickets.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TatQueryFieldMapping;
import com.savbill.ticketmanagement.core.modules.tickets.model.TatQueryFieldMappingDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TatQueryFieldMapper extends IBaseMapper<TatQueryFieldMappingDTO, TatQueryFieldMapping> {
}
