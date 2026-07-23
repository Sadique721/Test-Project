package com.savbill.salescrmsbss.repository;

import com.savbill.salescrmsbss.entity.pojo.CustomerBasicDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Customers;

import java.util.List;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer>{

    @Query("SELECT new com.savbill.salescrmsbss.entity.pojo.CustomerBasicDto(c.id, c.firstname) " +
            "FROM Customers c " +
            "WHERE c.isDeleted = false " +
            "AND (:mvnoId = 1 OR c.mvnoId = :mvnoId) " +
            "AND (:hasBu = false OR c.buId IN :buIds) ")
    List<CustomerBasicDto> findCustomersByMvnoAndBu(
            @Param("mvnoId") Integer mvnoId,
            @Param("buIds") List<Integer> buIds,
            @Param("hasBu") boolean hasBu
    );

}
