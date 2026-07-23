package com.savbill.notification.repository;

import com.savbill.notification.entity.EmailConfig;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long>, QuerydslPredicateExecutor<EmailConfig>, JpaSpecificationExecutor<EmailConfig>, QueryByExampleExecutor<EmailConfig> {
    Optional<EmailConfig> findByUserName(String name);

    Optional<EmailConfig> findByUserNameAndMvnoId(String userName, Long mvnoId);

    Optional<EmailConfig> findByMvnoId(Long mvnoId);
    List<EmailConfig> findAllByMvnoId(Long mvnoId);

    Optional<EmailConfig> findByMvnoIdIn(List<Long> mvnoIds);

    Optional<EmailConfig> findByEmailConfigIdAndMvnoId(Long emailConfigId, Long mvnoId);

    Optional<EmailConfig> findByEmailConfigId(Long emailConfigId);

    Optional<EmailConfig> findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long mvnoId, Long buId);
    List<EmailConfig> findAllByMvnoIdAndBuIdAndIsDeleteIsFalse(Long mvnoId, Long buId);

    @Query("select t.buId from EmailConfig t where t.emailConfigId =:configid")
    List<Long> getBuIdFromConfigId(@Param("configid") Long configid);

    List<EmailConfig> findByUserNameLike(String userName);

    List<EmailConfig> findAllByIsDeleteIsFalseAndIsActiveIsTrue();

    List<EmailConfig> findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdEquals(Long mvnoid, Long buid);

    List<EmailConfig> findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdIsNull(Long mvnoid);

    Page<EmailConfig> findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCase(String serviceType, Pageable pageable);

    Page<EmailConfig> findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCaseAndMvnoIdIn(String serviceType, Collection<Long> mvnoId, Pageable pageable);

    Page<EmailConfig> findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCaseAndMvnoIdInAndBuIdIn(String serviceType, Collection<Long> mvnoId, Collection<Long> buId, Pageable pageable);

    Page<EmailConfig> findAll(Specification specification, Pageable pageable);
}
