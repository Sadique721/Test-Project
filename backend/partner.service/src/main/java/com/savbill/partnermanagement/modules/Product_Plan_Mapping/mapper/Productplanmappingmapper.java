package com.savbill.partnermanagement.modules.Product_Plan_Mapping.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.Product_Plan_Mapping.domain.Productplanmapping;
import com.savbill.partnermanagement.modules.Product_Plan_Mapping.dto.Productplanmappingdto;
import org.mapstruct.Mapper;

@Mapper
public interface Productplanmappingmapper extends IBaseMapper<Productplanmappingdto, Productplanmapping> {
}
