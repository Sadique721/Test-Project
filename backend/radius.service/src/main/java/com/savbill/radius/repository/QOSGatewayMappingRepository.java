package com.savbill.radius.repository;

import com.savbill.radius.entity.QOSPolicyGatewayMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QOSGatewayMappingRepository extends JpaRepository<QOSPolicyGatewayMapping, Long> {
    List<QOSPolicyGatewayMapping> findAllByQosPolicyIdOrderById(Long qosPolicyId);


}
