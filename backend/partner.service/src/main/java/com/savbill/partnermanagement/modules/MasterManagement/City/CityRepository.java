package com.savbill.partnermanagement.modules.MasterManagement.City;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface CityRepository extends JpaRepository<City, Integer>, QuerydslPredicateExecutor<City> {

    @Query("SELECT new map(c.id as id, c.name as name) FROM City c WHERE c.id IN :ids")
    List<Map<String, Object>> findIdNamePairs(@Param("ids") List<Integer> ids);

}
