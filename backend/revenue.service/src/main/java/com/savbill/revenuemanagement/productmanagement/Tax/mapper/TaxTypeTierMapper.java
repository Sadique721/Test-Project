package com.savbill.revenuemanagement.productmanagement.Tax.mapper;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxTypeTierPojo;
import org.mapstruct.Mapper;

@Mapper
public interface TaxTypeTierMapper extends IBaseMapper<TaxTypeTierPojo, TaxTypeTier> {
}
