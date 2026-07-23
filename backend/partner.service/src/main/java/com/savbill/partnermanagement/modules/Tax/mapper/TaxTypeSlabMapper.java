package com.savbill.partnermanagement.modules.Tax.mapper;


import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.Tax.domain.TaxTypeSlab;
import com.savbill.partnermanagement.modules.Tax.dto.TaxTypeSlabPojo;
import org.mapstruct.Mapper;

@Mapper
public interface TaxTypeSlabMapper extends IBaseMapper<TaxTypeSlabPojo, TaxTypeSlab> {
}
