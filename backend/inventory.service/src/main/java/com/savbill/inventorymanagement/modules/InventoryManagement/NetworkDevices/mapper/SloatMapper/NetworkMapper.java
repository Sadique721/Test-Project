package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.NetworkDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NetworkMapper extends IBaseMapper<NetworkDTO, NetworkDevices> {
}
