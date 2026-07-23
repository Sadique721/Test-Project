package com.savbill.commonGateway.moules.PaymentConfig.repository;

import com.savbill.commonGateway.moules.PaymentConfig.entity.PaymentConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {

    List<PaymentConfig> findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(String paymentConfigName , Long mvnoId);

    List<PaymentConfig> findAllByMvnoIdAndIsDeleteIsFalse(Long mvnoId);

    Page<PaymentConfig> findAllByMvnoIdAndIsDeleteIsFalse(Long mvnoId, Pageable pageable);

    List<PaymentConfig> findAllByMvnoIdAndIsActiveIsTrueAndIsDeleteIsFalse(Long mvnoId);
}
