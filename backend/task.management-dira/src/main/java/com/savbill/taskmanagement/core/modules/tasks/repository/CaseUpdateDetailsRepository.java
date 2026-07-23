package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdateDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseUpdateDetailsRepository extends JpaRepository<CaseUpdateDetails, Long> {

    @Query(value = "SELECT * FROM tblcaseupdatedetails cu " +
            "JOIN tblcaseupdates cd ON cd.updateid = cu.updateid " +
            "JOIN tblcases c ON cd.caseid = c.case_id " +
            "WHERE c.case_id = :caseId " +
            "AND cu.operation = 'Add Remarks' " +
            "ORDER BY cu.updatedtlsid DESC " +
            "LIMIT 1",
            nativeQuery = true)
    CaseUpdateDetails findAllByCaseUpdateId(@Param("caseId") Long caseId);

}
