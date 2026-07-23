package com.savbill.cpm.mapper.postpaid;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.PlanServiceInventoryMapping;
import com.savbill.cpm.pojo.api.PlanServiceInventoryMappingPojo;
import org.mapstruct.Mapper;

@Mapper
public interface PlanServiceInventoryMapper  extends IBaseMapper<PlanServiceInventoryMappingPojo, PlanServiceInventoryMapping> {
}
