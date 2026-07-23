package com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import org.mapstruct.Mapper;

@Mapper
public interface Productplanmappingmapper extends IBaseMapper<Productplanmappingdto,Productplanmapping> {
}
