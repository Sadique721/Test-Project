package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class WarhouseMapper implements IBaseMapper<WareHouseDto, WareHouse> {

    @Autowired
    private TeamsRepository teamsRepository;
    @Mappings({
        @Mapping(source = "wareHouse.teamsIdsList", target = "teamsIdsList"),
        @Mapping(source = "wareHouse.teamsList", target = "teamsList")
    })
    @Override
    public abstract WareHouseDto domainToDTO(WareHouse wareHouse, CycleAvoidingMappingContext context);

    @Mappings({
        @Mapping(source = "dtoData.teamsIdsList", target = "teamsIdsList"),
        @Mapping(source = "dtoData.teamsList", target = "teamsList")
    })
    @Override
    public abstract WareHouse dtoToDomain(WareHouseDto dtoData, CycleAvoidingMappingContext context);

    Long fromEntityToId(Teams entity) {
        return entity == null ? null : entity.getId();
    }

    Teams fromIdToEntity(Integer id) {
        if (id == null) {
            return null;
        }
        Teams entity;
        try {
            entity = teamsRepository.findById(id.longValue()).get();
            entity.setId(id.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
