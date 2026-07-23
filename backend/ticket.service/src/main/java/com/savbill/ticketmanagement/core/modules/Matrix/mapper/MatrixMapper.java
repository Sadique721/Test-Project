package com.savbill.ticketmanagement.core.modules.Matrix.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.Matrix;
import com.savbill.ticketmanagement.core.modules.Matrix.model.MatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MatrixMapper extends IBaseMapper<MatrixDTO, Matrix> {

}
