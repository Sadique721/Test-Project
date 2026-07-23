package com.savbill.cpm.modules.PartnerLedger.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.PartnerLedger.domain.PartnerPayment;
import com.savbill.cpm.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.savbill.cpm.service.postpaid.PartnerService;

@Mapper
public abstract class PartnerPaymentMapper implements IBaseMapper<PartnerPaymentDTO, PartnerPayment> {

    @Mapping(source = "partner",target = "partnerId")
    public abstract PartnerPaymentDTO domainToDTO(PartnerPayment data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "partnerId",target = "partner")
    public abstract PartnerPayment dtoToDomain(PartnerPaymentDTO dto,@Context CycleAvoidingMappingContext context);

    @Autowired
    private PartnerService partnerService;

    PartnerPayment fromId(final Long id){
        if(id==null){
            return null;
        }
        final PartnerPayment partnerPayment=new PartnerPayment();
        partnerPayment.setId(id);
        return  partnerPayment;
    }

    Integer fromPartner(Partner entity){return entity==null?null:entity.getId();}

    Partner fromPartnerId(Integer entityId){
        if(entityId==null){
            return null;
        }
        Partner entity=null;
        try{
            entity=partnerService.get(entityId);
        }catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return  entity;
    }
}
