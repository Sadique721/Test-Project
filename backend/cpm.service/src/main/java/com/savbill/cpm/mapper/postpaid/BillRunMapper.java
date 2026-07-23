package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.BillRun;
import com.savbill.cpm.pojo.api.BillRunPojo;

@Mapper
public interface BillRunMapper extends IBaseMapper<BillRunPojo, BillRun> {
}
