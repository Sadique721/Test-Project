package com.savbill.ticketmanagement.core.modules.tickets.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryTatMapping;
import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketSubCategoryTatMapper  extends IBaseMapper<TicketReasonCategoryDTO, TicketSubCategoryTatMapping> {
}
