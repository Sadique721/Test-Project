package com.savbill.cpm.modules.InventoryManagement.warehouse;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Teams.domain.Teams;
import com.savbill.cpm.modules.Teams.repository.TeamsRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class WarhouseMapper implements IBaseMapper<WareHouseDto, WareHouse>{

    @Autowired
    private TeamsRepository teamsRepository;

    @Override
    @Mapping(source = "wareHouse.teamsIdsList", target = "teamsIdsList")
    @Mapping(source = "wareHouse.teamsList", target = "teamsList")
    public abstract WareHouseDto domainToDTO(WareHouse wareHouse, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.teamsIdsList", target = "teamsIdsList")
    @Mapping(source = "dtoData.teamsList", target = "teamsList")
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
