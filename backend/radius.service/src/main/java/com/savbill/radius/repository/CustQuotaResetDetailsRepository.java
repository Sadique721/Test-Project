package com.savbill.radius.repository;

import com.savbill.radius.entity.CustQuotaResetDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustQuotaResetDetailsRepository extends JpaRepository<CustQuotaResetDetails, Integer> {
}
