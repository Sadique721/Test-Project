package com.savbill.ticketmanagement.core.modules.Mvno.mapper;


import com.savbill.ticketmanagement.core.modules.Mvno.domain.Mvno;
import com.savbill.ticketmanagement.core.modules.Mvno.model.MvnoDTO;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class MvnoMapper implements IBaseMapper<MvnoDTO, Mvno> {

}
