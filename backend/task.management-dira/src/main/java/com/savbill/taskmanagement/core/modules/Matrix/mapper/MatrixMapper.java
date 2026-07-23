package com.savbill.taskmanagement.core.modules.Matrix.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Matrix.domain.Matrix;
import com.savbill.taskmanagement.core.modules.Matrix.model.MatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MatrixMapper extends IBaseMapper<MatrixDTO, Matrix> {

}
