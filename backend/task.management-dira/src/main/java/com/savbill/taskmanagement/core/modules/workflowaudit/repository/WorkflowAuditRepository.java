package com.savbill.taskmanagement.core.modules.workflowaudit.repository;


import com.savbill.taskmanagement.core.modules.workflowaudit.domain.WorkflowAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long>, QuerydslPredicateExecutor<WorkflowAudit> {

    //Getall workflowauditbycustomerid with pagination
    @Query(nativeQuery = true,
    value = "select * from savbillconvergebss.tbltworkflowaudit t where (t.event_id =:eventid and t.custcaf_id =:id)")
    Page<WorkflowAudit> findByevent_idandcustcaf_id(@Param("eventid") Integer eventid, @Param("id") Integer id, Pageable pageable);

    //Getall workflowauditbyplanid with pagination
    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.plan_id =:id")
    Page<WorkflowAudit> findByplan_id(@Param("id") Integer id, Pageable pageable);

    //Getall workflowauditbycreditDocid with pagination
    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.creditdoc_id =:id")
    Page<WorkflowAudit> findBycreditdoc_id(@Param("id") Integer id, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.custpackage_id =:id")
    Page<WorkflowAudit> findBycustPackage_id(@Param("id") Integer id, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.custcaf_id =:custcaf_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCustCAFANDStaffUser(@Param("custcaf_id") Integer custcaf_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.creditdoc_id =:creditdoc_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCreditDocIDANDStaffUser(@Param("creditdoc_id") Integer creditdoc_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.plan_id =:plan_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByPlanIdANDStaffUser(@Param("plan_id") Integer plan_id, @Param("staff_id") Integer staff_id);

    @Query(nativeQuery = true,
            value = "select * from savbillconvergebss.tbltworkflowaudit t where t.custpackage_id =:custpackage_id and t.staff_id =:staff_id and t.status ='Pending'")
    WorkflowAudit findByCustPackIdANDStaffUser(@Param("custpackage_id") Integer custpackage_id, @Param("staff_id") Integer staff_id);
    
    //find next staff approver for caf customer
    @Query(nativeQuery = true,
            value = "select t.staff_id  from savbillconvergebss.tblcustomercafassignment t where t.custcaf_id =:id")
    Integer findByNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from savbillconvergebss.tblcustomercafassignment t where t.creddoc_id =:id")
    Integer findByPaymentNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from savbillconvergebss.tblcustomercafassignment t where t.custpackage_id =:id")
    Integer findByCustPackIdNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
            value = "select t.staff_id  from savbillconvergebss.tblcustomercafassignment t where t.plan_id =:id")
    Integer findByPlanNextApprover (@Param("id") Integer id);

    @Query(nativeQuery = true,
    value = "select t2.username from savbillconvergebss.tbltcreditdoc t \n" +
            "left join\n" +
            "savbillconvergebss.tblcustomers t2 \n" +
            "on t2.custid = t.CUSTID \n" +
            "where t.CREDITDOCID =:id")
    String findByCustomerName(@Param("id") Integer id);

    @Query(nativeQuery = true,
    value = "select max(t.workflow_audit_id), t.* from savbillconvergebss.tbltworkflowaudit t where t.custpackage_id =:id and t.next_staff_status ='Approved'")
    WorkflowAudit findById(@Param("id") Integer id);
}
