package com.savbill.ticketmanagement.core.modules.ServiceParameters.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.ticketmanagement.core.modules.ServiceParameters.model.ServiceParametersDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
