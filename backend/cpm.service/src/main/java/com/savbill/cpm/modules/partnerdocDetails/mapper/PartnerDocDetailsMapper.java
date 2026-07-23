package com.savbill.cpm.modules.partnerdocDetails.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.partnerdocDetails.domain.PartnerdocDetails;
import com.savbill.cpm.modules.partnerdocDetails.model.PartnerdocDTO;
import com.savbill.cpm.service.postpaid.PartnerService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PartnerDocDetailsMapper implements IBaseMapper<PartnerdocDTO, PartnerdocDetails> {

    @Autowired
    private PartnerService partnerService;

    @Override
    @Mapping(target = "partner", source = "partnerId")
    public abstract PartnerdocDetails dtoToDomain(PartnerdocDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "partner", target = "partnerId")
    public abstract PartnerdocDTO domainToDTO(PartnerdocDetails domain, @Context CycleAvoidingMappingContext context);

    Integer fromPartnerTopartnerId(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner partnerIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity;
        try {
            entity = partnerService.get(entityId);
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
