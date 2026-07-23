package com.savbill.partnermanagement.nepaliCalendarUtils.repository;


import com.savbill.partnermanagement.nepaliCalendarUtils.domain.NepaliDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NepaliDateRepository extends JpaRepository<NepaliDate, Integer>, QuerydslPredicateExecutor<NepaliDate> {
		Optional<NepaliDate> findByYear(String year);
}
