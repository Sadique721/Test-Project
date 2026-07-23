package com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserMappingsRepocitory extends JpaRepository<TeamUserMapping, Long>, QuerydslPredicateExecutor<TeamUserMapping> {
    @Query(value="Select team_id from tbltteamusermapping where staffid=:staffId", nativeQuery = true)
    List<Long> teamIds(@Param("staffId") Long staffId);


    List<TeamUserMapping> findAllByTeamId(Long teamId);
    List<TeamUserMapping> findAllByStaffId(Long staffId);

    @Query(value="select t.staffid from tbltteamusermapping t where t.team_id in (:teamId)", nativeQuery = true)
    List<Long> findStaffIds(@Param("teamId") List<Long> teamId);

}
