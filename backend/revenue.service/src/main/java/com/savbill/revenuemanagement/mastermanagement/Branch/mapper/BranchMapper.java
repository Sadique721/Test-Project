package com.savbill.revenuemanagement.mastermanagement.Branch.mapper;


import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.mastermanagement.Branch.domain.Branch;
import com.savbill.revenuemanagement.mastermanagement.Branch.model.BranchDTO;
import com.savbill.revenuemanagement.mastermanagement.Branch.service.BranchService;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.service.ServiceAreaService;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper
public abstract class BranchMapper  implements IBaseMapper<BranchDTO, Branch> {
	
	@Autowired
    private BranchService branchService;

	@Autowired
    private ServiceAreaService serviceAreaService;

	@Override
    @Mappings({
            @Mapping(source = "branch.serviceAreaNameList", target = "serviceAreaIdsList"),
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")
    })

    public abstract BranchDTO domainToDTO(Branch branch, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.serviceAreaIdsList", target = "serviceAreaNameList")
    public abstract Branch dtoToDomain(BranchDTO dtoData, @Context CycleAvoidingMappingContext context);

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
    void afterMapping(@MappingTarget BranchDTO branchDTO, Branch branch) {
        try {
            if (null != branch.getServiceAreaNameList() && 0 < branch.getServiceAreaNameList().size()) {
            	branchDTO.setServiceAreaNameList(branch.getServiceAreaNameList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
            } else {
            	branchDTO.setServiceAreaNameList(Collections.singletonList("-"));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Branch Mapper" + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

}
