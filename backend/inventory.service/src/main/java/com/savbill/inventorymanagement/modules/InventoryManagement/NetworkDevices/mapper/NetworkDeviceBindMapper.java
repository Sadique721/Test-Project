package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.NetworkDeviceBindDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class NetworkDeviceBindMapper implements IBaseMapper<NetworkDeviceBindDTO, NetworkDeviceBind> {
}
