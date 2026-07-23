package com.savbill.revenuemanagement.core.mapper.invoice;

import com.savbill.revenuemanagement.core.dto.invoice.CreditDocChargeRelDTO;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocChargeRel;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper
public abstract class CreditDocChargeRelMapper implements IBaseMapper<CreditDocChargeRelDTO, CreditDocChargeRel> {

    @Autowired
    private ChargeRepository chargeRepository;
//    @Override
//    @Mapping(target = "charge", source = "chargeName")
//    public abstract CreditDocChargeRel dtoToDomain(CreditDocChargeRelDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "chargeName", source = "chargeid")
    public abstract CreditDocChargeRelDTO domainToDTO(CreditDocChargeRel domain, @Context CycleAvoidingMappingContext context);

    String fromChargeToChargeName(Charge charge) {
        return charge == null ? null : charge.getName();
    }

    String fromChargeIdToChargeName(Integer chargeId) {
        Optional<Charge> charge = chargeRepository.findById(chargeId);

        return charge.map(Charge::getName).orElse(null);
    }
}
