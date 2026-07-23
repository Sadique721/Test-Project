package com.savbill.revenuemanagement.core.entity.TeamUserMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeamUserMappingsRepocitory  extends JpaRepository<TeamUserMapping, Long>, QuerydslPredicateExecutor<TeamUserMapping> {
    @Query(value="Select team_id from tblteamusermapping where staffid=:staffId", nativeQuery = true)
    Set<Long> teamIds(@Param("staffId") Long staffId);


    List<TeamUserMapping> findAllByTeamId(Long teamId);

    Integer countByTeamId(Long teamId);

    List<TeamUserMapping> findAllByStaffId(Long valueOf);
}
