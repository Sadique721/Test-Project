package com.savbill.cpm.modules.SubBusinessVertical.Mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.savbill.cpm.modules.BusinessVerticals.Mapper.BusinessVerticalsMpper;
import com.savbill.cpm.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.savbill.cpm.modules.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.cpm.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.savbill.cpm.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class SubBusinessVerticalMapper implements IBaseMapper<SubBusinessVerticalDTO, SubBusinessVertical> {

    @Autowired
    BusinessVerticalsService businessVerticalsService;

    @Autowired
    BusinessVerticalsMpper businessVerticalsMpper;

   // @Mapping(source = "buVerticalsId", target = "businessVerticals")
    public abstract SubBusinessVertical dtoToDomain(SubBusinessVerticalDTO dtoData,@Context CycleAvoidingMappingContext context);

    //@Mapping(source = "businessVerticals", target = "buVerticalsId")
    public abstract SubBusinessVerticalDTO domainToDTO(SubBusinessVertical data,@Context CycleAvoidingMappingContext context);

    Integer fromBusinessVerticalToId(BusinessVerticals entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    BusinessVerticals fromIdToBusinessVertical(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        BusinessVerticals entity;
        try {
            BusinessVerticalsDTO entityDTO = businessVerticalsService.getEntityById(entityId.longValue());
            entity = businessVerticalsMpper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
