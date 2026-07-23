package com.savbill.revenuemanagement.core.schedulers;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulerManagementRepository extends JpaRepository<SchedulerManagement,Long>, QuerydslPredicateExecutor<SchedulerManagement> {

    @Query(value = "select * from tblmschedulers s where s.status = 'Active'",nativeQuery = true)
    List<SchedulerManagement> findAllByActiveStatus();


    Boolean existsBySchedulerName(SchedulerName schedulerName);
}
