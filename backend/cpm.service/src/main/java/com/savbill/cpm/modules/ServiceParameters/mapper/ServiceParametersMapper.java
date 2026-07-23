package com.savbill.cpm.modules.ServiceParameters.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.cpm.modules.ServiceParameters.model.ServiceParametersDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
