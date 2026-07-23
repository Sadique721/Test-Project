package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Mapper;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMappingDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class BuildingMgmtMapper implements IBaseMapper<BuildingManagementDTO, BuildingManagement> {
    // Convert Entity to DTO (Fix for buildingMgmtId and mvnoId)
    @Override
    @Mappings({
            @Mapping(target = "buildingMappings", source = "buildingMappings"),  // Fix: Ensuring correct list mapping
            //@Mapping(target = "displayName", source = "name")  // Example for another field
    })
    public abstract BuildingManagementDTO domainToDTO(BuildingManagement domain, @Context CycleAvoidingMappingContext context);

    // Explicitly map buildingMgmtId and mvnoId in BuildingMappingDTO
    @Mappings({
            @Mapping(target = "buildingMgmtId", source = "buildingManagement.buildingMgmtId"), // Fix: Ensuring ID mapping
            @Mapping(target = "mvnoId", source = "buildingManagement.mvnoId") // Fix: Ensuring mvnoId mapping
    })
    public abstract BuildingMappingDTO buildingMappingToDTO(BuildingMapping mapping, @Context CycleAvoidingMappingContext context);

    //  DTO to Domain (Fix for buildingMgmtId and mvnoId)
    @Mappings({
            @Mapping(target = "buildingManagement", expression = "java(mapBuildingManagement(dto))") // Custom method to set the reference properly
    })
    public abstract BuildingMapping dtoToBuildingMapping(BuildingMappingDTO dto, @Context CycleAvoidingMappingContext context);

    //  Custom method to create BuildingManagement reference properly
    protected BuildingManagement mapBuildingManagement(BuildingMappingDTO dto) {
        if (dto.getBuildingMgmtId() == null) {
            return null;
        }
        BuildingManagement buildingManagement = new BuildingManagement();
        buildingManagement.setBuildingMgmtId(dto.getBuildingMgmtId());
        buildingManagement.setMvnoId(dto.getMvnoId());
        return buildingManagement;
    }

    @Mappings({
            @Mapping(target = "buildingMappings", source = "buildingMappings")
    })
    public abstract BuildingManagement dtoToDomain(BuildingManagementDTO dto, @Context CycleAvoidingMappingContext context);
}
