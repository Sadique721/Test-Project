package com.savbill.cpm.modules.purchaseDetails.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.purchaseDetails.domain.PaymentGatewayResponse;
import com.savbill.cpm.modules.purchaseDetails.model.PaymentGatewayResponseDTO;

@Mapper
public abstract class PaymentGatewayResponseMapper implements IBaseMapper<PaymentGatewayResponseDTO, PaymentGatewayResponse> {
}
