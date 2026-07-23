package com.savbill.commonGateway.moules.userUiPreferences.repository;

import com.savbill.commonGateway.moules.userUiPreferences.domain.UserUiPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface UserUiPreferencesRepository extends JpaRepository<UserUiPreferences, Long>, QuerydslPredicateExecutor<UserUiPreferences> {

    List<UserUiPreferences> findAllByMvnoId(Long mvnoId);
    List<UserUiPreferences> findAllByMvnoIdIn(List<Long> mvnoId);
    Optional<UserUiPreferences> findByMvnoIdAndPageNameAndIsDeleteFalse(Integer mvnoId, String pageName);

    List<UserUiPreferences> findAllByIsDeleteFalseAndMvnoIdIn(List<Long> mvnoIds);

}

