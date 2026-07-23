package com.savbill.revenuemanagement.productmanagement.Discount.repocitory;



import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountPlanMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DiscountPlanMappingRepo extends JpaRepository<DiscountPlanMapping, Integer> {

    List<DiscountPlanMapping> findByDiscountId(Integer discountId);


}
