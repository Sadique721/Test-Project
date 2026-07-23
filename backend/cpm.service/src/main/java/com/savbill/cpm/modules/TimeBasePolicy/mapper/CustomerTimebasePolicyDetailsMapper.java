package com.savbill.cpm.modules.TimeBasePolicy.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.savbill.cpm.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerTimebasePolicyDetailsMapper extends IBaseMapper<TimeBasePolicyDetailsDTO, TimeBasePolicyDetails> {
}
