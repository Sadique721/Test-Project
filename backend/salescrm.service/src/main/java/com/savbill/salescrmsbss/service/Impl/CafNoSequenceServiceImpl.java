package com.savbill.salescrmsbss.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.CafNoSequence;
import com.savbill.salescrmsbss.repository.CafNoSequenceRepository;
import com.savbill.salescrmsbss.service.CafNoSequenceService;

@Service
public class CafNoSequenceServiceImpl implements CafNoSequenceService{

	@Autowired
	private CafNoSequenceRepository cafNoSequenceRepository;
	
	@Override
	public List<CafNoSequence> findAll() {
		return this.cafNoSequenceRepository.findAll();
	}

	@Override
	public CafNoSequence save(CafNoSequence cafNoSequence) {
		return this.cafNoSequenceRepository.save(cafNoSequence);
	}

	@Override
	public void updateSeq(String id, String newId) {
		this.cafNoSequenceRepository.updateSeq(newId,id);
	}

}
