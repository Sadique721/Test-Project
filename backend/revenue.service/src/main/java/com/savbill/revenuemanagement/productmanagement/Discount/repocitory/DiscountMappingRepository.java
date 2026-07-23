package com.savbill.revenuemanagement.productmanagement.Discount.repocitory;



import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DiscountMappingRepository extends JpaRepository<DiscountMapping, Integer>{
    List<DiscountMapping> findByDiscountId(Integer discountId);
}
