package com.savbill.revenuemanagement.core.repository.partner;



import com.savbill.revenuemanagement.core.entity.partner.PartnerCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerCommissionRepository extends JpaRepository<PartnerCommission, Integer> {
}
