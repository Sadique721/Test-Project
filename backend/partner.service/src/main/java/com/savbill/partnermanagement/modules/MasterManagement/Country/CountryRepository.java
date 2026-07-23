package com.savbill.partnermanagement.modules.MasterManagement.Country;



import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

//@JaversSpringDataAuditable
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> , QuerydslPredicateExecutor<Country> {

    @Query("SELECT new map(c.id as id, c.name as name) FROM Country c WHERE c.id IN :ids")
    List<Map<String, Object>> findIdNamePairs(@Param("ids") List<Integer> ids);

}
