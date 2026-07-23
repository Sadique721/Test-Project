package com.savbill.revenuemanagement.core.mapper.partner;

import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PartnerLedgerDetails;
import com.savbill.revenuemanagement.core.entity.partner.PartnerLedgerDetailsDTO;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public abstract class PartnerLedgerDetailMapper implements IBaseMapper<PartnerLedgerDetailsDTO, PartnerLedgerDetails> {

    @Mapping(source = "partner",target = "partnerId")
    @Mapping(source = "data.createDate", target = "createDate")
    public abstract PartnerLedgerDetailsDTO domainToDTO(PartnerLedgerDetails data, @Context CycleAvoidingMappingContext context);
    @Mapping(source = "partnerId",target = "partner")
    public abstract PartnerLedgerDetails dtoToDomain(PartnerLedgerDetailsDTO dto,@Context CycleAvoidingMappingContext context);

    @Autowired
    private PartnerRepository partnerRepository;

    PartnerLedgerDetails fromId(final Long id){
        if(id==null){
            return null;
        }
        final PartnerLedgerDetails partnerLedgerDetails=new PartnerLedgerDetails();
        partnerLedgerDetails.setId(id);
        return  partnerLedgerDetails;
    }

    Integer fromPartner(Partner entity){return entity==null?null:entity.getId();}

    Partner fromPartnerId(Integer entityId){
        if(entityId==null){
            return null;
        }
        Partner entity=null;
        try{
            entity=partnerRepository.findById(entityId).orElse(null);
        }catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return  entity;
    }

    LocalDate fromCreateDateTimeToCreateDate(LocalDateTime entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.toLocalDate();
        }
    }

    LocalDateTime fromCreateDateToCreateDateTime(LocalDate entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.atStartOfDay();
        }
    }

}
