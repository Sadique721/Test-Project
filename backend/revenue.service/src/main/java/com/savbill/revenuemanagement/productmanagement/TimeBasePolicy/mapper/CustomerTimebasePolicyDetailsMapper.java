package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.mapper;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerTimebasePolicyDetailsMapper extends IBaseMapper<TimeBasePolicyDetailsDTO, TimeBasePolicyDetails> {
}
