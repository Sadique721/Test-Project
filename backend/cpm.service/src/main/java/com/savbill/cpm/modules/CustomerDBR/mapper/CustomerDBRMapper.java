package com.savbill.cpm.modules.CustomerDBR.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.CustomerDBR.domain.CustomerDBR;
import com.savbill.cpm.modules.CustomerDBR.model.CustomerDBRDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerDBRMapper extends IBaseMapper<CustomerDBRDTO, CustomerDBR> {

    public abstract CustomerDBR dtoToDomain(CustomerDBRDTO dtoData, @Context CycleAvoidingMappingContext context);

}
