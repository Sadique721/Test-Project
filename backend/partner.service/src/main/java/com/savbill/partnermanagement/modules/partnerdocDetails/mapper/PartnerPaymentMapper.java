package com.savbill.partnermanagement.modules.partnerdocDetails.mapper;


import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partner.entity.PartnerPayment;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerPaymentDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

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
