package com.savbill.commonGateway.moules.PaymentConfigMapping.repository;

import com.savbill.commonGateway.moules.PaymentConfigMapping.entity.PaymentConfigMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentConfigMappingRepository extends JpaRepository<PaymentConfigMapping, Long> {
    List<PaymentConfigMapping> findAllByPaymentConfigId(Long paymentConfigId);

    List<PaymentConfigMapping> findAllByPaymentParameterName(String parameterName);
}
