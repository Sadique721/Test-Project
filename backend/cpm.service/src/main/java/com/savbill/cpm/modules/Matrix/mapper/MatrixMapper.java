package com.savbill.cpm.modules.Matrix.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Matrix.domain.Matrix;
import com.savbill.cpm.modules.Matrix.model.MatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MatrixMapper extends IBaseMapper<MatrixDTO, Matrix> {

}
