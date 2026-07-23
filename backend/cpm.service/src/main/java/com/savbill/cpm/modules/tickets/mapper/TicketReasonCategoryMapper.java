package com.savbill.cpm.modules.tickets.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.tickets.domain.TicketReasonCategory;
import com.savbill.cpm.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class TicketReasonCategoryMapper implements IBaseMapper<TicketReasonCategoryDTO, TicketReasonCategory> {
}
