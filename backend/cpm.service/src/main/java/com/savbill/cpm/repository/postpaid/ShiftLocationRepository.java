package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.ShiftLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface ShiftLocationRepository extends JpaRepository<ShiftLocation, Long>, QuerydslPredicateExecutor<ShiftLocation> {
    @Query(value = "SELECT * FROM tbltshiftlocation where customer_id =:custId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    ShiftLocation findLatestByCustomerId(Integer custId);
}
