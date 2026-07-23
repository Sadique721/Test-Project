package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMappingDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingMapping;
import org.mapstruct.Mapper;

@Mapper
public  abstract  class BuildingMappingMapper implements IBaseMapper<BuildingMappingDTO, BuildingMapping> {
}
