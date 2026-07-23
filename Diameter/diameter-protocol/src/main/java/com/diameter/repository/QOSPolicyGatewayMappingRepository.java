package com.diameter.repository;

import com.diameter.model.PostpaidPlan;
import com.diameter.model.QOSPolicyGatewayMapping;
import com.diameter.model.QOSPolicyGatewayMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QOSPolicyGatewayMappingRepository extends JpaRepository<QOSPolicyGatewayMappingEntity, Long> {


}

