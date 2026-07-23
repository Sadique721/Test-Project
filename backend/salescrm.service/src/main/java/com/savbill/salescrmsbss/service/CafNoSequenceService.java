package com.savbill.salescrmsbss.service;

import java.util.List;

import com.savbill.salescrmsbss.entity.CafNoSequence;

public interface CafNoSequenceService {

	List<CafNoSequence> findAll();

	CafNoSequence save(CafNoSequence cafNoSequence);

	void updateSeq(String id, String newId);
}
