package com.savbill.radius.repository;

import com.savbill.radius.entity.FaultyMAC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface FaultyMACKRepocitory extends JpaRepository<FaultyMAC, Long>, QuerydslPredicateExecutor<FaultyMAC> {

    Optional<FaultyMAC> findFaultyMACSByMackIdEqualsAndIsDeletedFalseAndMvnoIdEquals(String mackId, Integer mvnoId);

    List<FaultyMAC> findAllByMvnoId(Integer mvnoId);

    List<FaultyMAC> findAllByMvnoIdAndIsDeletedFalse(Integer mvnoId);

    List<FaultyMAC> findAllByIsDeletedFalse();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbltfaultymac fm WHERE fm.mack_id in :macList", nativeQuery = true)
    void deleteByMacIn(List<String> macList);
}
