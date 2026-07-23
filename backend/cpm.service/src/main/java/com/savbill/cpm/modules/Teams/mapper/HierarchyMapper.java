package com.savbill.cpm.modules.Teams.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Teams.domain.Hierarchy;
import com.savbill.cpm.modules.Teams.model.HierarchyDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class HierarchyMapper implements IBaseMapper<HierarchyDTO, Hierarchy> {


//    @Override
//    @Mappings({
//            @Mapping(target = "mvnoId", ignore = true)
//    })
//    public Hierarchy updateDTOToDomain(HierarchyDTO hierarchyDTO, Hierarchy hierarchy, CycleAvoidingMappingContext context) {
//        return hierarchy;
//    }
}
