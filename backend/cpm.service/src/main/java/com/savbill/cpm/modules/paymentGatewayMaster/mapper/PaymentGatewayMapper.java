package com.savbill.cpm.modules.paymentGatewayMaster.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.savbill.cpm.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;

@Mapper
public interface PaymentGatewayMapper extends IBaseMapper<PaymentGatewayDTO, PaymentGateWay> {
}
