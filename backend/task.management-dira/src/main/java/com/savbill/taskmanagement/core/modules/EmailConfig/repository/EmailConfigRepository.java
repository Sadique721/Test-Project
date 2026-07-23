package com.savbill.taskmanagement.core.modules.EmailConfig.repository;


import com.savbill.taskmanagement.core.modules.EmailConfig.domain.EmailConfigBSS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailConfigRepository extends JpaRepository<EmailConfigBSS, Long>,QuerydslPredicateExecutor<EmailConfigBSS>
{
	Optional<EmailConfigBSS> findByUserName(String name);
	Optional<EmailConfigBSS> findByUserNameAndMvnoId(String userName, Long mvnoId);
	Optional<EmailConfigBSS> findByMvnoId(Long mvnoId);
	Optional<EmailConfigBSS> findByMvnoIdIn(List<Long> mvnoIds);
	Optional<EmailConfigBSS> findByEmailConfigIdAndMvnoId(Long emailConfigId, Long mvnoId);

	List<EmailConfigBSS> findAllByMvnoIdAndBuId(Long mvnoId , Long buId);
}
