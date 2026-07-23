package com.savbill.taskmanagement.core.modules.Customers.repository;

import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
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
    Optional<Customers> findById(@Param("customerId") Integer CustomerId);

    List<Customers>  findAllByEmailAndMvnoId(@Param("email") String email, @Param("mvnoId") Integer mvnoId);



    @Query(value = "select * from tblcustomers t where t.email like '%' :domain and t.MVNOID =:mvnoId",nativeQuery = true)
    List<Customers> findAllByDomainAndMvnoId(@Param("domain") String domain , @Param("mvnoId") Integer mvnoId);


    List<Customers> findAllByEmailAndBuId(@Param("email") String email, @Param("buId") Long buId);

    List<Customers> findAllByEmailAndBuIdAndMvnoId(@Param("email") String email, @Param("buId") Long buId , @Param("mvnoId") Integer mvnoId);


    @Query(value = "select * from tblcustomers t where t.email like '%' :domain and t.BUID =:buId",nativeQuery = true)
    List<Customers> findAllByDomainandBuId(@Param("domain") String domain , @Param("buId") Long buId);

    @Query(value = "select * from tblcustomers t where t.email like '%' :domain and t.BUID =:buId and t.MVNOID =:mvnoId",nativeQuery = true)
    List<Customers> findAllByDomainandBuIdAndMvnoId(@Param("domain") String domain , @Param("buId") Long buId,@Param("mvnoId") Integer mvnoId);

}
