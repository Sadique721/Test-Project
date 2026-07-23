package com.savbill.radius.repository;

import com.savbill.radius.entity.QOSPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QosPolicyRepository extends JpaRepository<QOSPolicy, Integer> {

    boolean existsById(Long id);

}
