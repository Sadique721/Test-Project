package com.savbill.revenuemanagement.core.repository.partner;


import com.savbill.revenuemanagement.core.entity.partner.PartnerCreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartnerCreditDocRepository extends JpaRepository<PartnerCreditDocument, Integer> {
    List<PartnerCreditDocument> getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type);
}
