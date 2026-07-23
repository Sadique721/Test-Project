package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.mapper;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.domain.TimeBasePolicy;
import com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.module.TimeBasePolicyDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TimeBasePolicyMapper extends IBaseMapper<TimeBasePolicyDTO, TimeBasePolicy> {
}
