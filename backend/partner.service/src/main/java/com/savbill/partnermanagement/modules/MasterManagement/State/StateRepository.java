package com.savbill.partnermanagement.modules.MasterManagement.State;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface StateRepository extends JpaRepository<State, Integer>, QuerydslPredicateExecutor<State> {

    @Query("SELECT new map(s.id as id, s.name as name) FROM State s WHERE s.id IN :ids")
    List<Map<String, Object>> findIdNamePairs(@Param("ids") List<Integer> ids);

}
