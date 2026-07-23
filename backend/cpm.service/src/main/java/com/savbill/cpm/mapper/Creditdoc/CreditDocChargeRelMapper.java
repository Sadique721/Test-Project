package com.savbill.cpm.mapper.Creditdoc;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.creditdoc.CreditDocChargeRel;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.model.postpaid.Charge;
import com.savbill.cpm.pojo.CreditDoc.CreditDocChargeRelDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class CreditDocChargeRelMapper implements IBaseMapper<CreditDocChargeRelDTO, CreditDocChargeRel> {

//    @Override
//    @Mapping(target = "charge", source = "chargeName")
//    public abstract CreditDocChargeRel dtoToDomain(CreditDocChargeRelDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "chargeName", source = "charge")
    public abstract CreditDocChargeRelDTO domainToDTO(CreditDocChargeRel domain, @Context CycleAvoidingMappingContext context);

    String fromChargeToChargeName(Charge charge) {
        return charge == null ? null : charge.getName();
    }
}
