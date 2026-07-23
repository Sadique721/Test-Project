package com.savbill.cpm.model.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.pojo.api.TaxPojo;

@Mapper
public interface TaxMapper  extends IBaseMapper<TaxPojo, Tax> {
}
