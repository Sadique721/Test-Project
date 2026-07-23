package com.savbill.salescrmsbss.service;

import java.io.IOException;
import java.util.List;

import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.entity.pojo.CreateLeadQuotationDTO;
import com.savbill.salescrmsbss.entity.pojo.EmailAuditingDTO;
import com.savbill.salescrmsbss.entity.pojo.QuotationDetailsDTO;
import com.savbill.salescrmsbss.helper.LeadQuotationWfDTO;

public interface LeadQuotationService {

	QuotationDetails createLeadQuotationByCircuit(CreateLeadQuotationDTO createLeadQuotationDTO);

	List<QuotationDetailsDTO> findListOfQuotationDetailsByLeadId(Long leadId);

	public void sendEmailWithQuotationDetails(EmailAuditingDTO emailDTO) throws IOException;

    QuotationDetailsDTO assignWorkFlow(Long quotationId, Long staffId, Long buid, Long mvnoId);

    void updateLeadQuotationApprover(LeadQuotationWfDTO leadQuotationWfDTO);

    void updateLeadQuotationAssignApproverInfo(LeadQuotationWfDTO leadQuotationWfDTO);
}
