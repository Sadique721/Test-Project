package com.savbill.salescrmsbss.service.Impl;

import com.savbill.salescrmsbss.service.LeadAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.LeadAudit;
import com.savbill.salescrmsbss.repository.LeadAuditRepository;
import com.savbill.salescrmsbss.service.*;

@Service
public class LeadAuditServiceImpl implements LeadAuditService {

	@Autowired
	private LeadAuditRepository leadAuditRepository;
	
	@Override
	public LeadAudit save(LeadAudit leadAudit) {
		try {
			return this.leadAuditRepository.save(leadAudit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
