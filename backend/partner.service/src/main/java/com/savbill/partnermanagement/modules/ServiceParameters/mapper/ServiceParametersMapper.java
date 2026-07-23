package com.savbill.partnermanagement.modules.ServiceParameters.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.partnermanagement.modules.ServiceParameters.model.ServiceParametersDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
