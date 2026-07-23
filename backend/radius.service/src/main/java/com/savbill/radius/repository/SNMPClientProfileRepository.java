package com.savbill.radius.repository;

import com.savbill.radius.entity.SNMPClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SNMPClientProfileRepository extends JpaRepository<SNMPClientProfile,Long>, QuerydslPredicateExecutor {
}
