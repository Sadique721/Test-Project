package com.savbill.revenuemanagement.productmanagement.Tax.mapper;


import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface TaxMapper extends IBaseMapper<TaxPojo, Tax> {
    @Override
    public abstract TaxPojo domainToDTO(Tax data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract Tax dtoToDomain(TaxPojo dtoData, @Context CycleAvoidingMappingContext context);

}
