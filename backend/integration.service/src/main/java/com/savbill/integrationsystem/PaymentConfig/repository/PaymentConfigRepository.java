package com.savbill.integrationsystem.PaymentConfig.repository;

import com.savbill.integrationsystem.PaymentConfig.entity.PaymentConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {

    PaymentConfig findByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(String paymentConfigName , Long mvnoId);

    List<PaymentConfig> findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(String paymentConfigName , Long mvnoId);
    List<PaymentConfig> findAllByMvnoIdAndIsDeleteIsFalse(Long mvnoId);
}
