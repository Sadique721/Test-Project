package com.savbill.taskmanagement.core.modules.staffuser.repository;

import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUserServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffUserServiceRepository extends JpaRepository<StaffUserServiceMapping,Long>, QuerydslPredicateExecutor<StaffUserServiceMapping> {


}
