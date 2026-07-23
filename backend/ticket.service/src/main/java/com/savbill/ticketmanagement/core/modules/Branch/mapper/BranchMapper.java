package com.savbill.ticketmanagement.core.modules.Branch.mapper;


import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Branch.domain.Branch;
import com.savbill.ticketmanagement.core.modules.Branch.model.BranchDTO;
import com.savbill.ticketmanagement.core.modules.Branch.service.BranchService;
import com.savbill.ticketmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.ticketmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
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
