package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.DunningRuleAction;
import com.savbill.cpm.pojo.api.DunningRuleActionPojo;

@Mapper
public interface DunningRuleActionMapper extends IBaseMapper<DunningRuleActionPojo, DunningRuleAction> {
}
