package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.State;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer>{

    @Query(name = "select * from TBLMSTATE where is_delete = false and name =:name",nativeQuery = true)
    List<State> findByname(String name);
}
