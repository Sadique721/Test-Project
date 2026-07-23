package com.savbill.cpm.modules.servicePlan.mapper;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.servicePlan.domain.Services;
import com.savbill.cpm.modules.servicePlan.model.ServicesDTO;
@JaversSpringDataAuditable
@Mapper
public interface ServicesMapper  extends IBaseMapper<ServicesDTO, Services> {
}
