package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.radius.Plan;
import com.savbill.cpm.pojo.api.PlanPojo;

@Mapper
public interface PlanMapper  extends IBaseMapper<PlanPojo, Plan> {
}
