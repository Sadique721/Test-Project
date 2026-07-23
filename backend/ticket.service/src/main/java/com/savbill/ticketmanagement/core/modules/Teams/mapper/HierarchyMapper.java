package com.savbill.ticketmanagement.core.modules.Teams.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Teams.domain.Hierarchy;
import com.savbill.ticketmanagement.core.modules.Teams.model.HierarchyDTO;
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
