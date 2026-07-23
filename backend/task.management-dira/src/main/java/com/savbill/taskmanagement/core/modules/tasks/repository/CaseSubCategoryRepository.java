package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseSubCategoryRepository extends JpaRepository<CaseSubCategory,Long>, QuerydslPredicateExecutor<CaseSubCategory> {



    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.is_deleted=false and mvnoid in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.is_deleted=false and (mvnoid = 1 or (mvnoid = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.sub_category_id =:id and c.is_deleted=false and (mvnoid = 1 or (mvnoid = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.sub_category_id =:id and c.is_deleted=false and mvnoid in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmcasesubcategory c where c.sub_category_name=:name and c.sub_category_id =:id and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    //@Query(value = "select count(*) from tblmcasesubcategory c where c.is_default_case_sub_category=true", nativeQuery = true)
    CaseSubCategory findByIsDefaultCaseSubCategoryTrueAndMvnoId(Integer mvnoId);

    List<CaseSubCategory> findBySubCategoryIdInAndIsDeletedFalse(List<Long> subCategoryIds);

}
