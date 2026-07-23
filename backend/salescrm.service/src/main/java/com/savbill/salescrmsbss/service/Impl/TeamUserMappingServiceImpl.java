package com.savbill.salescrmsbss.service.Impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.TeamUserMapping;
import com.savbill.salescrmsbss.repository.TeamUserMappingRepository;
import com.savbill.salescrmsbss.service.TeamUserMappingService;

@Service
public class TeamUserMappingServiceImpl implements TeamUserMappingService{

	@Autowired
	private TeamUserMappingRepository teamUserMappingRepository;
	
	@Override
	public Set<Long> findByStaffIds(Integer staffId){
		Set<Long> staffIds = new HashSet<>();
		List<TeamUserMapping> teamUserMappingList = this.teamUserMappingRepository.findByStaffId(staffId);
	  for (TeamUserMapping teamUserMapping : teamUserMappingList) {
		  List<TeamUserMapping> teamUserMappings = this.teamUserMappingRepository.findByTeamId(teamUserMapping.getTeamId());
		  teamUserMappings.forEach(data->staffIds.add(data.getStaffId().longValue()));
	  }
	  return staffIds;
	}
}
