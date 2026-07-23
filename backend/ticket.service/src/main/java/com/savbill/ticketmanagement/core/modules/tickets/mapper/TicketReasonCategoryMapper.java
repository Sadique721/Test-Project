package com.savbill.ticketmanagement.core.modules.tickets.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonCategory;
import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class TicketReasonCategoryMapper implements IBaseMapper<TicketReasonCategoryDTO, TicketReasonCategory> {
}
