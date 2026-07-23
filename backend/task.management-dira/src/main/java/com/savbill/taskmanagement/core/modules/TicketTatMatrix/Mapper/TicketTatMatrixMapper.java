package com.savbill.taskmanagement.core.modules.TicketTatMatrix.Mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketTatMatrixMapper extends IBaseMapper<TicketTatMatrixDTO, TicketTatMatrix> {
}
