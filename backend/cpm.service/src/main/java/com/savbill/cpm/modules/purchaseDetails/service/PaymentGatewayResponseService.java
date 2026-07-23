package com.savbill.cpm.modules.purchaseDetails.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.purchaseDetails.domain.PaymentGatewayResponse;
import com.savbill.cpm.modules.purchaseDetails.mapper.PaymentGatewayResponseMapper;
import com.savbill.cpm.modules.purchaseDetails.model.PaymentGatewayResponseDTO;
import com.savbill.cpm.modules.purchaseDetails.repository.PaymentGatewayResponseRepository;

@Service
public class PaymentGatewayResponseService extends ExBaseAbstractService<PaymentGatewayResponseDTO, PaymentGatewayResponse, Long> {
    @Autowired
    PaymentGatewayResponseRepository repository;

    public PaymentGatewayResponseService(PaymentGatewayResponseRepository repository, PaymentGatewayResponseMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
