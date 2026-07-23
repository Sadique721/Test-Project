package com.savbill.ticketmanagement.core.modules.Plan.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Plan.domain.PostpaidPlan;
import com.savbill.ticketmanagement.core.modules.Plan.dto.PlanPojo;
import org.mapstruct.Mapper;

@Mapper
public abstract class PostpaidPlanMapper implements IBaseMapper<PlanPojo, PostpaidPlan> {


}
