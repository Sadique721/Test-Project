package com.savbill.salescrmsbss.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.LeadMasterSequence;
import com.savbill.salescrmsbss.repository.LeadMasterSequenceRepository;
import com.savbill.salescrmsbss.service.LeadMasterSequenceService;

@Service
public class LeadMasterSequenceServiceImpl implements LeadMasterSequenceService{

	@Autowired
	private LeadMasterSequenceRepository leadMasterSequenceRepository;

	@Override
	public List<LeadMasterSequence> findAll() {
		return this.leadMasterSequenceRepository.findAll();
	}

	@Override
	public LeadMasterSequence save(LeadMasterSequence leadMasterSequence) {
		return this.leadMasterSequenceRepository.save(leadMasterSequence);
	}

	@Override
	public void updateSeq(String id, String newId) {
		this.leadMasterSequenceRepository.updateSeq(newId,id);
	}
	
}
