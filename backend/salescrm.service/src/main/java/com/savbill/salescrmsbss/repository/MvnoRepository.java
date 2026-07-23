package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Mvno;

@Repository
public interface MvnoRepository extends JpaRepository<Mvno, Long> , QuerydslPredicateExecutor <Mvno>{
    @Query("select t.username from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);

    @Query(value = "CALL update_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void UpdateMvnoidISP(@Param("oldMvnoid") int oldMvnoid, @Param("newMvnoid") int newMvnoid);

}
