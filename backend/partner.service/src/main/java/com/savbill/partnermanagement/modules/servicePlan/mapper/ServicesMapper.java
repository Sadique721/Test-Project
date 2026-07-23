package com.savbill.partnermanagement.modules.servicePlan.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.Services.Services;
import com.savbill.partnermanagement.modules.Services.ServicesDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServicesMapper  extends IBaseMapper<ServicesDTO, Services> {
}
