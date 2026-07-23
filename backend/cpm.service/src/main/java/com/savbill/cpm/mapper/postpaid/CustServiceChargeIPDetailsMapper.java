package com.savbill.cpm.mapper.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.CustServiceChargeIPDetails;
import com.savbill.cpm.pojo.api.CustServiceChargeIPDetailsPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class CustServiceChargeIPDetailsMapper implements IBaseMapper<CustServiceChargeIPDetailsPojo, CustServiceChargeIPDetails> {

    @Override
    public abstract CustServiceChargeIPDetails dtoToDomain(CustServiceChargeIPDetailsPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustServiceChargeIPDetailsPojo domainToDTO(CustServiceChargeIPDetails domain, @Context CycleAvoidingMappingContext context);
}
