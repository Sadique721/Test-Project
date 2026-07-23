package com.savbill.revenuemanagement.productmanagement.servicePlan.mapper;

import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.servicePlan.model.ServicesDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServicesMapper  extends IBaseMapper<ServicesDTO, Services> {
}
