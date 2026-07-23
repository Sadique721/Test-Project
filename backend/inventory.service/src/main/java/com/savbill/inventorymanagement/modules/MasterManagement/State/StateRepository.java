package com.savbill.inventorymanagement.modules.MasterManagement.State;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface StateRepository extends JpaRepository<State, Integer>, QuerydslPredicateExecutor<State> {
    @Query("SELECT s.name FROM State s WHERE s.id = :id")
    String findNameById(@Param("id") Integer id);
}
