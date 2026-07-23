package com.savbill.revenuemanagement.productmanagement.Tax.mapper;

import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxTypeSlabPojo;
import org.mapstruct.Mapper;

@Mapper
public interface TaxTypeSlabMapper extends IBaseMapper<TaxTypeSlabPojo, TaxTypeSlab> {
}
