package com.savbill.notification.repository;

import com.savbill.notification.entity.NotificationAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



//@JaversSpringDataAuditable
@Repository
public interface NotificationAuditRepository extends JpaRepository<NotificationAudit , Long>   , QuerydslPredicateExecutor<NotificationAudit> {

    @Query("SELECT n FROM NotificationAudit n WHERE n.username = :username ORDER BY n.id DESC")
    Page<NotificationAudit> findByUsername(@Param("username") String username, Pageable pageable);
}
