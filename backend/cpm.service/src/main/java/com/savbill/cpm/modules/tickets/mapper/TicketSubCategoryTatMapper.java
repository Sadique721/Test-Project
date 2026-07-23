package com.savbill.cpm.modules.tickets.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.tickets.domain.TicketSubCategoryTatMapping;
import com.savbill.cpm.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketSubCategoryTatMapper  extends IBaseMapper<TicketReasonCategoryDTO, TicketSubCategoryTatMapping> {
}
