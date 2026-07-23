package com.savbill.radius.repository;

import com.savbill.radius.entity.CoaDMProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoaDMProfileRepository extends JpaRepository<CoaDMProfile, Long>,QuerydslPredicateExecutor<CoaDMProfile> {
    Optional<CoaDMProfile> findByName(String name);
    List<CoaDMProfile> findByType(String type);


}
