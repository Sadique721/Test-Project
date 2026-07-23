package com.savbill.inventorymanagement.modules.MasterManagement.Country;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> , QuerydslPredicateExecutor<Country> {
    @Query("SELECT c.name FROM Country c WHERE c.id = :id")
    String findNameById(@Param("id") Integer id);
}
