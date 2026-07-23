package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.CustChargeDetails;
import com.savbill.cpm.pojo.api.CustChargeDetailsPojo;

@Mapper
public interface CustChargeMapper extends IBaseMapper<CustChargeDetailsPojo, CustChargeDetails> {
}
