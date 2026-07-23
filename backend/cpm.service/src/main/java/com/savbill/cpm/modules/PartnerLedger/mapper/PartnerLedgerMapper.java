package com.savbill.cpm.modules.PartnerLedger.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.PartnerLedger.domain.PartnerLedger;
import com.savbill.cpm.modules.PartnerLedger.model.PartnerLedgerDTO;
import com.savbill.cpm.repository.postpaid.PartnerRepository;

@Mapper
public abstract class PartnerLedgerMapper implements IBaseMapper<PartnerLedgerDTO, PartnerLedger> {

    @Mapping(source = "partner", target = "partnerId")
    public abstract PartnerLedgerDTO domainToDTO(PartnerLedger data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "partnerId", target = "partner")
    public abstract PartnerLedger dtoToDomain(PartnerLedgerDTO dto, @Context CycleAvoidingMappingContext context);

    @Autowired
    private PartnerRepository partnerRepository;

    PartnerLedger fromId(final Long id) {
        if (id == null) {
            return null;
        }
        final PartnerLedger partnerLedger = new PartnerLedger();
        partnerLedger.setId(id);
        return partnerLedger;
    }

    Integer fromPartner(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner fromPartnerId(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity = null;
        try {
            entity = partnerRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
