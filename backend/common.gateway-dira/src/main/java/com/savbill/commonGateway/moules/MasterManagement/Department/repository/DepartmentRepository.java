package com.savbill.commonGateway.moules.MasterManagement.Department.repository;


import com.savbill.commonGateway.moules.MasterManagement.Department.domain.Department;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> , QuerydslPredicateExecutor<Department> {

    @Query(value = "select * from tblmdepartment where lower(name) like '%' :search  '%' order by id AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from tblmdepartment where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Department> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    List<Department> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);
    @Query("SELECT d FROM Department d WHERE d.status = :status AND d.isDelete = false AND " +
            "(d.mvnoId = :mvnoId OR d.mvnoId IS NULL OR d.mvnoId = 1 OR :mvnoId = 1) " +
            "ORDER BY d.id DESC")
    List<Department> findActiveDepartments(@Param("status") String status, @Param("mvnoId") Integer mvnoId);


    List<Department> findAll();

    Page<Department> findAllByIsDeleteIsFalse(Pageable pageable);
    Page<Department> findAllByIsDeleteIsFalseAndMvnoIdIn(List<Integer> mvnoIds, Pageable pageable);
    @Query("select t from Department t where t.isDelete=false and t.mvnoId in :mvnoIds")
    Page<Department> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query("update Department b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    Page<Department> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(String name, Pageable pageable);

    Department findByNameAndIsDeleteIsFalse(String departmentName);

    Page<Department> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(String name, Pageable pageable, List<Integer> mvnoId);

    @Query(value = "select count(*) from tblmdepartment t where t.NAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmdepartment t where t.NAME=:name and t.id =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select count(*) from tblmdepartment t where t.NAME=:name and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmdepartment t where t.NAME=:name and t.id =:id and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

}
