package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPPoolDtls;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPPoolDtlsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface IPPoolDtlsMapper extends IBaseMapper<IPPoolDtlsDTO, IPPoolDtls> {
}
