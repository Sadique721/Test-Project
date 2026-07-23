package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class OutwardMapper implements IBaseMapper<OutwardDto, Outward> {
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Mappings({
        @Mapping(source = "serviceArea", target = "serviceAreaId"),
        @Mapping(source = "createdByName", target = "createdBy")
    })
    @Override
    public abstract OutwardDto domainToDTO(Outward data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "serviceAreaId", target = "serviceArea")
    @Override
    public abstract Outward dtoToDomain(OutwardDto dtoData, @Context CycleAvoidingMappingContext context);

    Long fromServiceAreaToId(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceArea fromServiceAreaIdToServiceArea(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity;
        try {
            ServiceAreaDTO entityDTO = serviceAreaService.getEntityById(entityId, false);
            entity = serviceAreaMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
