package com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class ExternalItemManagementMapper implements IBaseMapper<ExternalItemManagementDTO, ExternalItemManagement> {
}
