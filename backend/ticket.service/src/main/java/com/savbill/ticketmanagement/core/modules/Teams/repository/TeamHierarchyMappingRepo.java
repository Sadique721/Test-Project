package com.savbill.ticketmanagement.core.modules.Teams.repository;


import com.savbill.ticketmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamHierarchyMappingRepo extends JpaRepository<TeamHierarchyMapping,Integer>, QuerydslPredicateExecutor<TeamHierarchyMapping> {
//    Long findById(List<Long> ids);

    TeamHierarchyMapping findByOrderNumberAndHierarchyId(Integer orderNumber, Integer hierarchyId);

    List<TeamHierarchyMapping> findByTeamId(Integer teamidlist);
    List<TeamHierarchyMapping> findAllByHierarchyId(Integer hierarchy_id);

}
