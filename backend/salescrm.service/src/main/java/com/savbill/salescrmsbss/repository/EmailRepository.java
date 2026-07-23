package com.savbill.salescrmsbss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

	@Query("SELECT e FROM Email e ORDER BY createdOn DESC")
	List<Email> findAll();

	Optional<Email> findByEmailIdAndMvnoId(Long emailId, Long mvnoId);

}
