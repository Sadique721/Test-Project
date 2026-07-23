package com.savbill.taskmanagement.core.modules.tasks.repository;


import com.savbill.taskmanagement.core.modules.tasks.domain.TicketTatAudits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TatAuditRepository extends JpaRepository<TicketTatAudits,Integer>, QuerydslPredicateExecutor<TicketTatAudits> {

    List<TicketTatAudits> findAllByCaseId(Integer caseId);

    @Query("SELECT t, CONCAT(COALESCE(s.firstname, ''), ' ', COALESCE(s.lastname, '')) " +
            "FROM TicketTatAudits t " +
            "LEFT JOIN StaffUser s ON s.id = t.assignStaffId " +
            "WHERE t.caseId = :caseId")
    List<Object[]> findTatAuditWithStaffName(@Param("caseId") Integer caseId);
}
