package com.savbill.commonGateway.moules.Customers.repository;

import com.savbill.commonGateway.moules.Customers.domain.Customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customers,Integer>, QuerydslPredicateExecutor<Customers> {
    @Query(value = "select * from tblcustomers t where t.username=:username AND t.is_deleted=false AND t.cstatus!='Terminate'", nativeQuery = true)
    Customers findByUserName(@Param("username") String username);
//    Optional<Customers> findById(@Param("customerId") Integer CustomerId);
    List<Customers> findByUsernameAndIsDeletedIsFalse(String username);

    @Query(value = "select custid from tblcustomers t where t.mvno_deactivation_flag =true and t.MVNOID=:MVNOID", nativeQuery = true)
    List<Integer> findCustomerIdsbyMvnoDeactivationFlag(@Param("MVNOID") Integer MVNOID);


    Customers findCustomersByUsername(String userName);

    Customers findCustomersByUsernameAndMvnoId(String userName, Integer mvnoId);
}
