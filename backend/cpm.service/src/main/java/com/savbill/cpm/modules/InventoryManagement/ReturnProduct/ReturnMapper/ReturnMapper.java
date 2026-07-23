package com.savbill.cpm.modules.InventoryManagement.ReturnProduct.ReturnMapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.InventoryManagement.ReturnProduct.ReturnDomain.ReturnDto;
import com.savbill.cpm.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import org.mapstruct.Mapper;

@Mapper
public interface ReturnMapper extends IBaseMapper<ReturnDto, Return> {
}
