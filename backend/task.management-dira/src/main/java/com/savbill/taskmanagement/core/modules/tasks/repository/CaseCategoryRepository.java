package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseCategoryRepository extends JpaRepository<CaseCategory, Long>, QuerydslPredicateExecutor<CaseCategory> {


    List<CaseCategory> findAllByCategoryNameContainingIgnoreCase(String categoryName);


    List<CaseCategory> findAllByCategoryNameEqualsIgnoreCase(String categoryName);



    @Query(value = "select * from tblmcasecategory c  where c.case_category_id IN :categoryIds and c.is_deleted = false and c.is_default_case_category = true and c.buid =:buid" , nativeQuery = true)
    List<CaseCategory> findAllDefaultCaseCategoryUsingCategoryId(@Param("categoryIds") List<Integer> categoryIds , @Param("buid") Long buid);

    @Query(value = "select * from tblmcasecategory c  where c.case_category_id IN :categoryIds and c.is_deleted = false and c.is_default_case_category = true" , nativeQuery = true)
    List<CaseCategory> findAllDefaultCaseCategoryUsingCategoryId(@Param("categoryIds") List<Integer> categoryIds);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.is_deleted=false and mvnoid in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.is_deleted=false and (mvnoid = 1 or (mvnoid = :mvnoId and buid in :buids))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buids") List buids);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.category_id =:id and c.is_deleted=false and (mvnoid = 1 or (mvnoid = :mvnoId and buid in :buids))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buids") List buids);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.category_id =:id and c.is_deleted=false and mvnoid in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmcasecategory c where c.category_name=:name and c.category_id =:id and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    //@Query(value = "select * from tblmcasecategory c  where c.is_default_case_category = true" , nativeQuery = true)
    CaseCategory findByIsDefaultCaseCategoryTrueAndMvnoId(Integer mvnoId);
}
