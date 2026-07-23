package com.savbill.cpm.modules.PurchaseOrder.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.PurchaseOrder.DTO.PurchaseOrderDTO;
import com.savbill.cpm.modules.PurchaseOrder.Domain.PurchaseOrder;
import org.mapstruct.Mapper;

@Mapper
public interface PurchaseOrderMapper extends IBaseMapper<PurchaseOrderDTO, PurchaseOrder> {
}
