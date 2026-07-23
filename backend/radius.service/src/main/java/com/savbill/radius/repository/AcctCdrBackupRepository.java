package com.savbill.radius.repository;


import com.savbill.radius.entity.AcctCdrBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import org.springframework.stereotype.Repository;


@Repository
public interface AcctCdrBackupRepository extends JpaRepository<AcctCdrBackup, Long>, QuerydslPredicateExecutor<AcctCdrBackup> {


}
