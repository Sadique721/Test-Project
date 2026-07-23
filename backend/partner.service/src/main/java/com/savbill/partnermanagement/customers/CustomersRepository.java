package com.savbill.partnermanagement.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customers,Integer>, QuerydslPredicateExecutor<Customers> {
    Long countByPartnerId(Integer partnerId);
}
