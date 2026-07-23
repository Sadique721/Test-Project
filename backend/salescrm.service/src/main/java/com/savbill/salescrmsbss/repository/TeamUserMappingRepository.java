package com.savbill.salescrmsbss.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.TeamUserMapping;

@Repository
public interface TeamUserMappingRepository extends JpaRepository<TeamUserMapping, Long>{

	@Query(name = "select * from tblteamusermapping where staffid=:staffId")
	List<TeamUserMapping> findByStaffId(@Param("staffId") Integer staffId);
	
	@Query(name = "select * from tblteamusermapping where team_id=:teamId")
	List<TeamUserMapping> findByTeamId(@Param("teamId") Long teamId);
}
