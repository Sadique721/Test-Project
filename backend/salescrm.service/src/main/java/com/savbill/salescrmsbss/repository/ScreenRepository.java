package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Screens;

@Repository
public interface ScreenRepository extends JpaRepository<Screens,Long> ,QuerydslPredicateExecutor<Screens>{

    List<Screens> findIdByScreenname(String name);
}
