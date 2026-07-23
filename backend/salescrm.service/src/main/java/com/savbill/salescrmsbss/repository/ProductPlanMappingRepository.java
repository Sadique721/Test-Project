package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.ProductPlanMapping;

import java.util.List;

@Repository
public interface ProductPlanMappingRepository extends JpaRepository<ProductPlanMapping, Long> {

    ProductPlanMapping findByApigwProductPlanMappingId(Long id);

    List<ProductPlanMapping> findByPostPaidPlan_id(Integer id);
}
