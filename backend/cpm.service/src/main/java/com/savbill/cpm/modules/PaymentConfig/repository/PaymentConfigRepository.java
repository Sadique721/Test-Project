package com.savbill.cpm.modules.PaymentConfig.repository;

import com.savbill.cpm.modules.PaymentConfig.entity.PaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {

    List<PaymentConfig> findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(String paymentConfigName , Long mvnoId);

    List<PaymentConfig> findAllByMvnoIdAndIsDeleteIsFalse(Long mvnoId);
}
