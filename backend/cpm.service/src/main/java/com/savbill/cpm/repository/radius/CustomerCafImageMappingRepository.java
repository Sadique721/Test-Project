package com.savbill.cpm.repository.radius;

import com.savbill.cpm.model.common.CustomerCafImageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerCafImageMappingRepository extends JpaRepository<CustomerCafImageMapping, Long> {

    Optional<CustomerCafImageMapping> findByCustomerIdAndFilename(Long customerId, String filename);

    List<CustomerCafImageMapping> findByCustomerId(Long customerId);

}
