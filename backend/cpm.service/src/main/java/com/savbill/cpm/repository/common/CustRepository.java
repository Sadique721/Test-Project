package com.savbill.cpm.repository.common;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.common.Customers;

//@JaversSpringDataAuditable
@Repository
public interface CustRepository extends PagingAndSortingRepository<Customers, Long> {

}
