package com.savbill.cpm.modules.InventoryManagement.PopManagement.repository;

import com.savbill.cpm.modules.InventoryManagement.PopManagement.domain.PopManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopManagementRepository extends JpaRepository<PopManagement, Long>, QuerydslPredicateExecutor<PopManagement> {
    // find duplicate pop name at save
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_name =:popname and t.is_deleted =false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("popname")String popname);

    // find duplicate pop name at save with mvnoId
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_name =:popname and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("popname")String popname, @Param("mvnoIds") List mvnoids);

    //Find duplicate pop name at edit
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_id =:popid and t.pop_name =:popname and t.is_deleted =false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("popname")String popname, @Param("popid") Integer popid);

    // Find duplicate pop name at edit with mvnoId
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_id =:popid and  t.pop_name =:popname and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("popname")String popname, @Param("popid") Integer popid, @Param("mvnoIds") List mvnoids);

    //Pop ID verify at Delete
    @Query(value = "select count(*) as tab from tblmpopmanagement t  where t.pop_id =:popid" ,nativeQuery = true)
    Integer deleteVerify(@Param("popid")Integer popid);

    @Query(value = "select t.pop_name as tab from tblmpopmanagement t  where t.pop_id =:popid" ,nativeQuery = true)
    String findPopNameById(@Param("popid")long popid);
}
