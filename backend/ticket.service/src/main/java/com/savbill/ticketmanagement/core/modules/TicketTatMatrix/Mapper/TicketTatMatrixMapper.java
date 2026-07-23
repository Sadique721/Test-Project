package com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketTatMatrixMapper extends IBaseMapper<TicketTatMatrixDTO, TicketTatMatrix> {
}
