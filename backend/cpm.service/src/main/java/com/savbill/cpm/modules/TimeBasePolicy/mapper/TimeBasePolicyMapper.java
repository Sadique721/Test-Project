package com.savbill.cpm.modules.TimeBasePolicy.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.TimeBasePolicy.domain.TimeBasePolicy;
import com.savbill.cpm.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TimeBasePolicyMapper extends IBaseMapper<TimeBasePolicyDTO, TimeBasePolicy> {
}
