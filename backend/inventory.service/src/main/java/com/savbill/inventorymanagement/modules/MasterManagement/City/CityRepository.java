package com.savbill.inventorymanagement.modules.MasterManagement.City;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CityRepository extends JpaRepository<City, Integer>, QuerydslPredicateExecutor<City> {
    @Query("SELECT c.name FROM City c WHERE c.id = :id")
    String findNameById(@Param("id") Integer id);
}
