package com.savbill.partnermanagement.modules.Region.Mapper;

import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.Branch;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.BranchDTO;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.BranchMapper;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.BranchService;
import com.savbill.partnermanagement.modules.Region.domain.Region;
import com.savbill.partnermanagement.modules.Region.model.RegionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class RegionMapper implements IBaseMapper<RegionDTO, Region> {

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchMapper branchMapper;

    @Override
    @Mapping(source = "dtoData.branchid", target = "branchidList")
    public abstract Region dtoToDomain(RegionDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.branchidList", target = "branchid")
    public abstract RegionDTO domainToDTO(Region data, CycleAvoidingMappingContext context);

    Integer fromBranchToId(Branch entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Branch fromIdToBranch(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Branch entity;
        try {
            BranchDTO entityDTO = branchService.getEntityById(entityId.longValue());
            entity = branchMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
