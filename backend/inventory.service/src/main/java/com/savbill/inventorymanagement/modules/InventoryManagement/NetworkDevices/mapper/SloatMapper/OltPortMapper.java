package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.OLTPortDetails;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.OLTPortDTO;
import org.mapstruct.Mapper;

@Mapper
public interface OltPortMapper extends IBaseMapper<OLTPortDTO, OLTPortDetails> {
}
