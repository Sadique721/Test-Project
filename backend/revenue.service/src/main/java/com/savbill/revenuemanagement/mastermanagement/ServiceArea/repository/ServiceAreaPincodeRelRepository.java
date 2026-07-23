package com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository;



import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceAreaPincodeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAreaPincodeRelRepository  extends JpaRepository<ServiceAreaPincodeRel, Long> {
}
