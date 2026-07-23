package com.savbill.revenuemanagement.productmanagement.qosPolicy.repository;

import com.savbill.revenuemanagement.productmanagement.qosPolicy.domain.QOSPolicyGatewayMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QOSGatewayMappingRepository extends JpaRepository<QOSPolicyGatewayMapping, Long> {
    List<QOSPolicyGatewayMapping> findAllByQosPolicyId(Long qosPolicyId);


}
