package com.savbill.cpm.modules.InventoryManagement.PopManagement.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.savbill.cpm.modules.InventoryManagement.PopManagement.model.PopManagementDTO;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.cpm.modules.ServiceArea.service.ServiceAreaService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PopManagementMapper implements IBaseMapper<PopManagementDTO, PopManagement> {

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private ServiceAreaService serviceAreaService;


    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "popName")
    //@Mapping(source = "dto.serviceAreaId", target = "servicearea")
    public abstract PopManagement dtoToDomain(PopManagementDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "popName", target = "name")
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "popName")
    //@Mapping(source = "data.servicearea", target = "serviceAreaId")
    public abstract PopManagementDTO domainToDTO(PopManagement data, @Context CycleAvoidingMappingContext context);

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
