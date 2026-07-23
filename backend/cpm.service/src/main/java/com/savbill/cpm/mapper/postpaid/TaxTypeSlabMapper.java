package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.TaxTypeSlab;
import com.savbill.cpm.pojo.api.TaxTypeSlabPojo;

@Mapper
public interface TaxTypeSlabMapper extends IBaseMapper<TaxTypeSlabPojo, TaxTypeSlab> {
}
