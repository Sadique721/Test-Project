package com.savbill.taskmanagement.core.modules.ServiceParameters.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.taskmanagement.core.modules.ServiceParameters.model.ServiceParametersDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
