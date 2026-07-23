package com.savbill.partnermanagement.modules.Tax.mapper;


import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.Tax.domain.Tax;
import com.savbill.partnermanagement.modules.Tax.dto.TaxPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface TaxMapper extends IBaseMapper<TaxPojo, Tax> {
    @Override
    public abstract TaxPojo domainToDTO(Tax data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract Tax dtoToDomain(TaxPojo dtoData, @Context CycleAvoidingMappingContext context);

}
