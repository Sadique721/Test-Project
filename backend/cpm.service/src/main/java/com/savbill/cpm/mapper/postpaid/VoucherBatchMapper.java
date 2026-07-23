package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.radius.VoucherBatch;
import com.savbill.cpm.pojo.api.VoucherBatchPojo;

@Mapper
public interface VoucherBatchMapper extends IBaseMapper<VoucherBatchPojo, VoucherBatch> {
}
