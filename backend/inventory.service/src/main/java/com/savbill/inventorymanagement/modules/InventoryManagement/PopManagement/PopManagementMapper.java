package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper
public abstract class PopManagementMapper implements IBaseMapper<PopManagementDTO, PopManagement> {

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Mappings({
//        @Mapping(source = "id", target = "id"),
            //@Mapping(source = "dto.serviceAreaId", target = "servicearea"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "dto.serviceAreaIdsList", target = "serviceAreaNameList")
    })
    @Override
    public abstract PopManagement dtoToDomain(PopManagementDTO dto, @Context CycleAvoidingMappingContext context);

    @Mappings({
//        @Mapping(source = "id", target = "id"),
            //@Mapping(source = "data.servicearea", target = "serviceAreaId"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "data.serviceAreaNameList", target = "serviceAreaIdsList"),
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")
    })
    @Override
    public abstract PopManagementDTO domainToDTO(PopManagement data, @Context CycleAvoidingMappingContext context);

    Long fromServiceAreaToId(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceArea fromIdToServiceArea(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity;
        try {
            entity = serviceAreaService.getByID(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget PopManagementDTO popManagementDTO, PopManagement popManagement) {
        try {
            if (null != popManagement.getServiceAreaNameList() && 0 < popManagement.getServiceAreaNameList().size()) {
                popManagementDTO.setServiceAreaNameList(popManagement.getServiceAreaNameList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
            } else {
                popManagementDTO.setServiceAreaNameList(Collections.singletonList("-"));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Pop Management Mapper" + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

}
