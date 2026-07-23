package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonCategory;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface TicketReasonCategoryRepo  extends JpaRepository<TicketReasonCategory, Long>, QuerydslPredicateExecutor<TicketReasonCategory> {

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false and (mvno_id = 1 or (mvno_id = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false and (mvno_id = 1 or (mvno_id = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    List<TicketReasonCategory> findAllByCategoryNameContainingIgnoreCase( String categoryName);

    @Query(value = "select * from tblmticketreasoncategory c where c.service_id =:serviceId and is_deleted = 0 and c.is_default_problem_domain = 1" , nativeQuery = true)
    List<TicketReasonCategory> findAllDefualtReasonCategoryUsingServiceId(@Param("serviceId") Integer serviceId);

    @Query(value = "select * from tblmticketreasoncategory c where c.service_id in :serviceIds and is_deleted = 0 and c.is_default_problem_domain = 1 and c.BUID =:buId" , nativeQuery = true)
    List<TicketReasonCategory> findAllDefualtReasonCategoryUsingServiceIdIn(@Param("serviceIds") List<Integer> serviceIds , @Param("buId") Long buId);

    @Query(value = "select * from tblmticketreasoncategory c where c.service_id in :serviceIds and is_deleted = 0 and c.is_default_problem_domain = 1" , nativeQuery = true)
    List<TicketReasonCategory> findAllDefualtReasonCategoryUsingServiceIdIn(@Param("serviceIds") List<Integer> serviceIds);

}
