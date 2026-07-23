package com.savbill.cpm.modules.payments.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.payments.domain.Payment;
import com.savbill.cpm.modules.payments.model.PaymentDTO;

@Mapper
public interface PaymentMapper extends IBaseMapper<PaymentDTO, Payment> {
}
