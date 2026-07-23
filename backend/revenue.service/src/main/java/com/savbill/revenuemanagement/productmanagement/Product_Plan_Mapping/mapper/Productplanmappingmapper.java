package com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.mapper;

import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import org.mapstruct.Mapper;

@Mapper
public interface Productplanmappingmapper extends IBaseMapper<Productplanmappingdto, Productplanmapping> {
}
