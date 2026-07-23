package com.savbill.taskmanagement.core.modules.Teams.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Teams.domain.Hierarchy;
import com.savbill.taskmanagement.core.modules.Teams.model.HierarchyDTO;
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
