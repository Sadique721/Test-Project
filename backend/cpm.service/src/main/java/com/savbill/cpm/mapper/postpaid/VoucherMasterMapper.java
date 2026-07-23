package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.radius.VoucherMaster;
import com.savbill.cpm.pojo.api.VoucherMasterPojo;

@Mapper
public interface VoucherMasterMapper extends IBaseMapper<VoucherMasterPojo, VoucherMaster> {
}
