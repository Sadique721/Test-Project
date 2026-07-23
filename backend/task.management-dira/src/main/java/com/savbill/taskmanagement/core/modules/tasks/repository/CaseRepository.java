package com.savbill.taskmanagement.core.modules.tasks.repository;


import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.model.TaskApprovalDTO;
import com.savbill.taskmanagement.core.modules.tasks.model.TaskApprovalProjection;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@JaversSpringDataAuditable
@Repository
public interface CaseRepository extends JpaRepository<Case, Long>, QuerydslPredicateExecutor<Case>, JpaSpecificationExecutor<Case>,CaseRepositoryCustom  {
    List<Case> findCaseByCaseTypeAndIsDeleteIsFalseOrderByCaseIdDesc(String caseType);

    Case findTopByOrderByCaseIdDesc();



    List<Case> findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(Integer assigneeId);

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0"
            , countQuery = "select count(*) from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0")
    Page<Case> findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(@Param("s1") Integer assigneeId, Pageable pageable);

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0 and t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0 and t.MVNOID in :mvnoIds")
    Page<Case> findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(@Param("s1") Integer assigneeId, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0 and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND lcoid IS NULL"
            , countQuery = "select count(*) from tblcases t where t.current_assignee_id = :s1 and t.is_delete = 0 and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND lcoid IS NULL")
    Page<Case> findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(@Param("s1") Integer assigneeId, Pageable pageable, @Param("mvnoId") Integer mvnoId , @Param("buIds") List buIds);


    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.current_assignee_id = :s1 and t.case_status = :s2 and t.is_delete = 0 AND lcoid IS NULL"
            , countQuery = "select count(*) from tblcases t where t.current_assignee_id = :s1 and t.case_status = :s2 and t.is_delete = 0 AND lcoid IS NULL")
    Page<Case> findAllByCurrentAssignee_IdAndCaseStatusAndIsDeleteIsFalse(@Param("s1") Integer assigneeId, @Param("s2") String status, Pageable pageable);

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.current_assignee_id = :s1 and t.case_status = :s2 and t.is_delete = 0 and t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblcases t where t.current_assignee_id = :s1 and t.case_status = :s2 and t.is_delete = 0 and t.MVNOID in :mvnoIds")
    Page<Case> findAllByCurrentAssignee_IdAndCaseStatusAndIsDeleteIsFalse(@Param("s1") Integer assigneeId, @Param("s2") String status, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    List<Case> findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(String status);

    @Query(value = "tselect * from   savbillconvergebss.tblcases where case_status !='Resolved' and case_status!='Closed' and case_status!='rejected' and final_resolved_by_id IS null and final_closed_by_id  is null",nativeQuery = true)
    List<Case>getAllActiveCases();

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.case_status = :s1 and t.is_delete = 0"
            , countQuery = "select count(*) from tblcases t where t.case_status = :s1 and t.is_delete = 0")
    Page<Case> findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(@Param("s1") String status, Pageable pageable);

    @Query(nativeQuery = true
            , value = "select * from tblcases t where t.case_status = :s1 and t.is_delete = 0 and t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblcases t where t.case_status = :s1 and t.is_delete = 0 and t.MVNOID in :mvnoIds")
    Page<Case> findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(@Param("s1") String status, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    List<Case> findAllByTeamId(Integer teamId);

    @Query(value = "SELECT * from tblcases t WHERE t.is_delete = false", nativeQuery = true
            , countQuery = "SELECT count(*) from tblcases t WHERE t.is_delete = false")
    Page<Case> findAll(Pageable pageable);

//    @Query(value = "SELECT * from tblcases t where t.is_delete = 0 and t.partnerid = :partnerId"
//            , countQuery = "SELECT count(*) from tblcases t where t.is_delete = 0 and t.partnerid = :partnerId", nativeQuery = true)
//    Page<Case> findAllByPartner_IdAndIsDeleteIsFalse(@Param("partnerId") Integer partnerId, Pageable pageable);

//    @Query(nativeQuery = true, value = "select * from tblcases t\n" +
//            "left join tblcustomers t2\n" +
//            "on t2.custid = t.case_for_id \n" +
//            "left join tblstaffuser t3 \n" +
//            "on t3.staffid = t.current_assignee_id \n" +
//            "where ( t.case_number like '%' :s1  '%' or  t2.username like '%' :s2  '%' or \n" +
//            "t2.firstname like '%' :s3  '%' or t2.mobile like '%' :s4  '%' or \n" +
//            "t.case_status like '%' :s5  '%' or t3.firstname like '%' :s6  '%' or\n" +
//            "t3.lastname like '%' :s7  '%' or t.case_origin like '%' :s8  '%' or\n" +
//            "t.priority like '%' :s9  '%' or t.case_type like '%' :s10 '%' or t.case_title like '%' :s11 '%') and t.is_delete = 0"
//            , countQuery = "select count(*) from tblcases t\n" +
//            "left join tblcustomers t2\n" +
//            "on t2.custid = t.case_for_id \n" +
//            "left join tblstaffuser t3 \n" +
//            "on t3.staffid = t.current_assignee_id \n" +
//            "where ( t.case_number like '%' :s1  '%' or  t2.username like '%' :s2  '%' or \n" +
//            "t2.firstname like '%' :s3  '%' or t2.mobile like '%' :s4  '%' or \n" +
//            "t.case_status like '%' :s5  '%' or t3.firstname like '%' :s6  '%' or\n" +
//            "t3.lastname like '%' :s7  '%' or t.case_origin like '%' :s8  '%' or\n" +
//            "t.priority like '%' :s9  '%' or t.case_type like '%' :s10 '%' or t.case_title like '%' :s11 '%') and t.is_delete = 0")
//    Page<Case> findAllByCaseNumberOrCaseStatusOrCurrentAssignee_Firstname(Pageable pageable
//            , @Param("s1") String s1
//            , @Param("s2") String s2
//            , @Param("s3") String s3
//            , @Param("s4") String s4
//            , @Param("s5") String s5
//            , @Param("s6") String s6
//            , @Param("s7") String s7
//            , @Param("s8") String s8
//            , @Param("s9") String s9
//            , @Param("s10") String s10
//            , @Param("s11") String s11);

//    @Query(nativeQuery = true, value = "select * from tblcases t\n" +
//            "left join tblcustomers t2\n" +
//            "on t2.custid = t.case_for_id \n" +
//            "left join tblstaffuser t3 \n" +
//            "on t3.staffid = t.current_assignee_id \n" +
//            "where ( t.case_number like '%' :s1  '%' or  t2.username like '%' :s2  '%' or \n" +
//            "t2.firstname like '%' :s3  '%' or t2.mobile like '%' :s4  '%' or \n" +
//            "t.case_status like '%' :s5  '%' or t3.firstname like '%' :s6  '%' or\n" +
//            "t3.lastname like '%' :s7  '%' or t.case_origin like '%' :s8  '%' or\n" +
//            "t.priority like '%' :s9  '%' or t.case_type like '%' :s10 '%' or t.case_title like '%' :s12 '%') and t.is_delete = 0 and t2.partnerid = :s11"
//            , countQuery = "select count(*) from tblcases t\n" +
//            "left join tblcustomers t2\n" +
//            "on t2.custid = t.case_for_id \n" +
//            "left join tblstaffuser t3 \n" +
//            "on t3.staffid = t.current_assignee_id \n" +
//            "where ( t.case_number like '%' :s1  '%' or  t2.username like '%' :s2  '%' or \n" +
//            "t2.firstname like '%' :s3  '%' or t2.mobile like '%' :s4  '%' or \n" +
//            "t.case_status like '%' :s5  '%' or t3.firstname like '%' :s6  '%' or\n" +
//            "t3.lastname like '%' :s7  '%' or t.case_origin like '%' :s8  '%' or\n" +
//            "t.priority like '%' :s9  '%' or t.case_type like '%' :s10 '%' or t.case_title like '%' :s12 '%') and t.is_delete = 0 and t2.partnerid = :s11")
//    Page<Case> findAllByCaseNumberOrCaseStatusOrCurrentAssignee_FirstnameByPartner(Pageable pageable
//            , @Param("s1") String s1
//            , @Param("s2") String s2
//            , @Param("s3") String s3
//            , @Param("s4") String s4
//            , @Param("s5") String s5
//            , @Param("s6") String s6
//            , @Param("s7") String s7
//            , @Param("s8") String s8
//            , @Param("s9") String s9
//            , @Param("s10") String s10
//            , @Param("s11") Integer s11
//            ,@Param("s12") String s12);

    Case findByCaseNumber(String caseNumber);


    @Query(nativeQuery = true, value = "select * from tblcases t where t.case_id IN (:s1) and t.is_delete = 0"
            , countQuery = "select count(*) from tblcases t where t.case_id IN (:s1) and t.is_delete = 0")
    Page<Case> findAllBy(@Param("s1") List<String> idList, Pageable pageable);

    @Query(value = "select count(*) from tblcases t where t.is_delete = 0 and t.current_assignee_id = :s1", nativeQuery = true)
    Long findMinimumAssignReuqestByStaff(@Param("s1") Integer id);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.case_id =:id and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.case_id =:id and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblcases c where c.case_title=:name and c.case_id =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    List<Case> findAllByCaseNumber(String caseNumber);

    Case findByCaseNumberAndBuIdAndMvnoId(String caseNumber , Long buId, Integer mvnoId);

//    @Query(value = "select * from tblcases c where  c.is_delete=false and c.reason_sub_category_id=:reason_sub_category_id", nativeQuery = true)
//    List<Case> findAllByReasonSubCategoryId(@Param("reason_sub_category_id") Integer reason_sub_category_id);

    List<Case> findAllByCaseStatusNotInAndIsDeleteIsFalseAndCurrentAssignee_IdIsNotNull(List<String> caseStatus);

    List<Case> findAllByCaseStatusInAndIsDeleteIsFalseAndCurrentAssignee_IdIsNull(List<String> caseStatus);

    List<Case> findAllByRootCauseReasonId(Long rootCauseReasonId);


    List<Case> findAllByIsFromCalenderTrueAndIsDeleteIsFalse();

    //@Query(value = "select * from tblcases c where c.current_assignee_id=:currentAssigneeId and c.case_status =:status and c.is_delete=false and c.is_from_calender=true",nativeQuery = true)
    List<Case>findAllByIsDeleteIsFalseAndIsFromCalenderIsTrueAndCurrentAssigneeAndCaseStatus(StaffUser currentAssigneeId, String status);

    //@Query(value = "select * from tblcases c where c.current_assignee_id=:currentAssigneeId and c.is_delete=false and c.is_from_calender=true",nativeQuery = true)
    List<Case>findAllByCurrentAssignee_IdAndIsDeleteIsFalseAndIsFromCalenderTrue(Integer currentAssigneeId);
    Optional<Case>findByCaseIdAndIsFromCalenderFalse(Long entityId);
    @Query(value = "SELECT * FROM tblcases t WHERE t.is_from_calender = true AND t.is_delete=false AND t.is_processed=false " +
            "AND (t.case_status != 'Done' AND t.case_status != 'Discarded') " +
            "AND (t.ending_date IS NOT NULL AND t.ending_date BETWEEN  :formattedDateTimeStr and :nowStr  )", nativeQuery = true)
    List<Case> findCasesByStatusAndDateRange(@Param("formattedDateTimeStr") String formattedDateTimeStr,@Param("nowStr") String nowStr);

    @Query(value = "SELECT * FROM tblcases t WHERE t.is_from_calender = true AND t.is_delete=false AND t.is_processed=false " +
            "AND (t.case_status != 'Done' AND t.case_status != 'Discarded') " +
            "AND (t.ending_date IS NULL AND t.starting_date + INTERVAL 1 DAY BETWEEN t.starting_date AND :midnightStr)", nativeQuery = true)
    List<Case> findCasesByStatusAndDateRangeForMidnight(@Param("midnightStr") String midnightStr);

    List<Case> findAllByCustomers_Id(Integer customersId);

    @Query(
            value = "SELECT c.case_id AS caseId, " +
                    "CONCAT(s.firstname, ' ', s.lastname) AS currentAssigneeName, " +
                    "sc.sub_category_name AS caseSubCategoryName, " +
                    "c.team_id AS teamId, " +
                    "t.team_name AS teamName, " +
                    "c.case_sub_category_id AS caseSubCategoryId, " +
                    "c.current_assignee_id AS currentAssigneeId " +
                    "FROM tblcases c " +
                    "LEFT JOIN tblstaffuser s ON s.staffid = c.current_assignee_id " +
                    "LEFT JOIN tblteams t ON t.team_id = c.team_id " +
                    "LEFT JOIN tblmcasesubcategory sc ON sc.sub_category_id = c.case_sub_category_id " +
                    "WHERE c.is_delete = 0 " +
                    "AND c.case_status NOT IN ('DISCARDED','DONE') " +
                    "AND (c.team_id IN :teamIds OR c.current_assignee_id = :userId) " +
                    "ORDER BY c.case_id DESC",
            countQuery = "SELECT COUNT(*) FROM tblcases c " +
                    "WHERE c.is_delete = 0 " +
                    "AND c.case_status NOT IN ('DISCARDED','DONE') " +
                    "AND (c.team_id IN :teamIds OR c.current_assignee_id = :userId)",
            nativeQuery = true
    )
    Page<TaskApprovalProjection> getTaskApprovalsNative(@Param("teamIds") Set<Integer> teamIds, @Param("userId") Integer userId,Pageable pageable);
}
