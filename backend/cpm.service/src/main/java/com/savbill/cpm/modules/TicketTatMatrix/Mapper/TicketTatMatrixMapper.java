package com.savbill.cpm.modules.TicketTatMatrix.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.cpm.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketTatMatrixMapper extends IBaseMapper<TicketTatMatrixDTO, TicketTatMatrix> {
}
