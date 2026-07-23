package com.savbill.salescrmsbss.service;

import java.util.List;

import com.savbill.salescrmsbss.entity.LeadMasterSequence;

public interface LeadMasterSequenceService {

	List<LeadMasterSequence> findAll();
	
	LeadMasterSequence save(LeadMasterSequence leadMasterSequence);
	
	void updateSeq(String id,String newId);
}
