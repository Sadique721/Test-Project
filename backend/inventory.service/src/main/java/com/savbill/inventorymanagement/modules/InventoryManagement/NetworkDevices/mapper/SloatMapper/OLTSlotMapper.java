package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.Oltslots;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.NetworkDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.SlotService.NetworkService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {NetworkMapper.class})
public abstract class OLTSlotMapper implements IBaseMapper<OLTSlotDetailDTO, Oltslots> {

    @Mapping(source = "networkDevices", target = "networkId")
    public abstract OLTSlotDetailDTO domainToDTO(Oltslots data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "networkId", target = "networkDevices")
    public abstract Oltslots dtoToDomain(OLTSlotDetailDTO dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    NetworkService networkDeviceService;

    NetworkMapper netMapper = Mappers.getMapper(NetworkMapper.class);

    Oltslots fromId(final Long id) {
        if (id == null) {
            return null;
        }
        final Oltslots sloat = new Oltslots();
        sloat.setId(id);
        return sloat;
    }

    Long fromNetwork(NetworkDevices entity) {
        return entity == null ? null : entity.getId();
    }

    NetworkDevices fromNetworkId(Long entityId) {
        if (entityId == null) {
            return null;
        }
        NetworkDevices entity = null;
        try {
            NetworkDTO netDTo = networkDeviceService.getEntityById(entityId, false);
            entity = netMapper.dtoToDomain(netDTo, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
