package com.savbill.integrationsystem.nms.repository;

import com.savbill.integrationsystem.nms.entity.NMSCustDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NMSCustDetailsRepository extends JpaRepository<NMSCustDetails, Long> {

    NMSCustDetails findByCustServMapId(Long custSerMapId);
}
