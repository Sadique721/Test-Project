package com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificatioParametersRepo extends JpaRepository<SpecificationParameters,Long>, QuerydslPredicateExecutor<SpecificationParameters> {
    List<SpecificationParameters> findAllByProductCategory_Id(Long productCategory_id);

    @Query("select s from SpecificationParameters s where s.productCategory.id in :productCategories")
    List<SpecificationParameters> findAllByProductCategoryIn(@Param("productCategories") List<Long> productCategories);

    List<SpecificationParameters> findAllByIdIn(List<Long> ids);


    SpecificationParameters findByParamName(String paramName);
}
