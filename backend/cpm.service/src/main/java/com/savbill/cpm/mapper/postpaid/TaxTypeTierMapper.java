package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.TaxTypeTier;
import com.savbill.cpm.pojo.api.TaxTypeTierPojo;

@Mapper
public interface TaxTypeTierMapper extends IBaseMapper<TaxTypeTierPojo, TaxTypeTier> {
}
