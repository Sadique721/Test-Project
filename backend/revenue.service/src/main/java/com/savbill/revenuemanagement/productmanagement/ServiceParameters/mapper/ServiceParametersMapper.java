package com.savbill.revenuemanagement.productmanagement.ServiceParameters.mapper;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.ServiceParameters.domain.ServiceParameter;
import com.savbill.revenuemanagement.productmanagement.ServiceParameters.model.ServiceParametersDTO;

import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
