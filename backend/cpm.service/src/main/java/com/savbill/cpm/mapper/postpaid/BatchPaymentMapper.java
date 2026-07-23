package com.savbill.cpm.mapper.postpaid;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.BatchPayment;
import com.savbill.cpm.pojo.api.BatchPaymentPojo;
import org.mapstruct.Mapper;

@Mapper
public interface BatchPaymentMapper  extends IBaseMapper<BatchPaymentPojo  , BatchPayment> {
}
