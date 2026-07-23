package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository;

import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPayment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface BatchPaymentRepository extends JpaRepository<BatchPayment, Long> {
    List<BatchPayment> findAllByIdIn(List<Long> batchIds);

    Long countByBatchNameAndIsDeleted(String name, Boolean isDelete);
}
