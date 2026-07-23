package com.savbill.taskmanagement.core.modules.Teams.repository;


import com.savbill.taskmanagement.core.modules.Teams.domain.TeamUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserMappingsRepository extends JpaRepository<TeamUserMapping, Long>, QuerydslPredicateExecutor<TeamUserMapping> {
    @Query(value="Select team_id from tblteamusermapping where staffid=:staffId", nativeQuery = true)
    List<Long> teamIds(@Param("staffId") Long staffId);


    List<TeamUserMapping> findAllByTeamIdIsIn(List<Long> teamIdList);

    List<TeamUserMapping> findAllByStaffId(Long staffId);

    List<TeamUserMapping> findAllByStaffIdIn(List<Long> staffId);

    List<TeamUserMapping> findAllByTeamId(Long teamId);


    @Query(value = "select staffid from tblteamusermapping where team_id=:teamId",nativeQuery = true)
    List<Integer> findAllStaffIdsByTeamId(Integer teamId);
}
