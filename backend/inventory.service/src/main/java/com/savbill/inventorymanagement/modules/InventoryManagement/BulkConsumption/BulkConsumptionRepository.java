package com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface BulkConsumptionRepository extends JpaRepository<BulkConsumption,Long>, QuerydslPredicateExecutor<BulkConsumption> {
    @Query(value = "select count(*) from tblmbulkconsumption m where m.name =:name and m.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmbulkconsumption m where m.name =:name and m.is_deleted =false and mvno_id in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbulkconsumption t where t.id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblmbulkconsumption t where t.id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

    @Query(value = "select count(*) from tblmbulkconsumption t where t.id=:id and is_deleted=false " ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
}
