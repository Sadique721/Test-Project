package com.savbill.ticketmanagement.core.modules.tickets.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseDocDetails;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseDocDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseDocDetailsMapper implements IBaseMapper<CaseDocDetailsDTO, CaseDocDetails> {
}
