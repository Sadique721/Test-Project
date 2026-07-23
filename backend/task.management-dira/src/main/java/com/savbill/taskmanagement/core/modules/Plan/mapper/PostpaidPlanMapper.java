package com.savbill.taskmanagement.core.modules.Plan.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Plan.domain.PostpaidPlan;
import com.savbill.taskmanagement.core.modules.Plan.dto.PlanPojo;
import org.mapstruct.Mapper;

@Mapper
public abstract class PostpaidPlanMapper implements IBaseMapper<PlanPojo, PostpaidPlan> {


}
