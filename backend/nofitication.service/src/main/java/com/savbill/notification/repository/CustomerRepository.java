package com.savbill.notification.repository;

import com.savbill.notification.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customers , Long> {

    @Query(value ="select * from tblmcustomer where custid =:custid ", nativeQuery = true)
    Customers findCustomerByCustId(@Param("custid") Integer custid);

    @Query(value ="select * from tblmcustomer where username =:username ", nativeQuery = true)
    List<Customers> findCustomerByUsername(@Param("username") String username);

    Optional<Customers> findAllByUsernameEqualsIgnoreCaseAndMvnoIdEquals(String username, Long mvnoId);

}
