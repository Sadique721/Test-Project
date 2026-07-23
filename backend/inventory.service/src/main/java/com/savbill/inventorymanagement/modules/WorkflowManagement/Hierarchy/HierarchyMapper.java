package com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
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
