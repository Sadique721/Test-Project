package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPAllocation;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPAllocationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface IPAllocationMapper extends IBaseMapper<IPAllocationDTO, IPAllocation> {
}
