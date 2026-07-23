package com.savbill.revenuemanagement.core.entity.TeamUserMapping;

import com.savbill.revenuemanagement.core.entity.staff.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Long>, QuerydslPredicateExecutor<Teams> {

    Set<Teams>findAllByIdIn(Set<Long> ids);



}
