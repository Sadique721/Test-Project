package com.savbill.taskmanagement.core.modules.Mvno.mapper;


import com.savbill.taskmanagement.core.modules.Mvno.domain.Mvno;
import com.savbill.taskmanagement.core.modules.Mvno.model.MvnoDTO;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class MvnoMapper implements IBaseMapper<MvnoDTO, Mvno> {

}
