package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.CustomerLedger;
import com.savbill.cpm.model.postpaid.CustomerLedgerPojo;

@Mapper(componentModel = "spring")
public interface CustomerLedgerMapper extends IBaseMapper<CustomerLedgerPojo, CustomerLedger> {
}
