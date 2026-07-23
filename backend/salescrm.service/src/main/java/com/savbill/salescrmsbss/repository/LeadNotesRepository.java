package com.savbill.salescrmsbss.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.LeadNotes;

@Repository
public interface LeadNotesRepository extends JpaRepository<LeadNotes, Long>{

	Page<LeadNotes> findByLeadMaster(LeadMaster leadMaster, Pageable pageable);

}
