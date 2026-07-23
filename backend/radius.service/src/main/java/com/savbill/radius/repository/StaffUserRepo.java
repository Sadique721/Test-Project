package com.savbill.radius.repository;

import com.savbill.radius.entity.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffUserRepo extends JpaRepository<StaffUser,Long>, QuerydslPredicateExecutor<StaffUser> {
}
