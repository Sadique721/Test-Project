package com.savbill.revenuemanagement.core.entity.staff;

import com.savbill.revenuemanagement.core.entity.TeamUserMapping.TeamsRepository;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveTeamsSharedSharedData;
import com.savbill.revenuemanagement.rabbitmq.messages.UpdateTeamsSharedData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamsService {

    @Autowired
    TeamsRepository teamsRepository;

    private static final Logger logger = Logger.getLogger(TeamsService.class);

    public void saveTeams(SaveTeamsSharedSharedData message) throws Exception{
        try {
            Teams teams = new Teams();
            teams.setId(message.getId());
            teams.setParentTeams(message.getParentTeams());
            teams.setName(message.getName());
            teams.setStatus(message.getStatus());
            teams.setCafStatus(message.getStatus());
            teams.setIsDeleted(message.getIsDeleted());
            teams.setLcoId(message.getLcoId());
            teams.setMvnoId(message.getMvnoId());
            teams.setCreatedById(message.getCreatedById());
            teams.setLastModifiedById(message.getLastModifiedById());
            teams.setPartner(message.getPartner());
            if (message.getTeamType()!=null) {
                teams.setTeamType(message.getTeamType());
            }
            teamsRepository.save(teams);
            logger.info("Teams created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create teams with name " + message.getName());
        }
    }

    public void updateTeams(UpdateTeamsSharedData message) throws Exception{
        try {
            Teams teams = teamsRepository.findById(message.getId()).orElse(null);
            if (teams != null) {
                teams.setId(message.getId());
                teams.setParentTeams(message.getParentTeams());
                teams.setName(message.getName());
                teams.setStatus(message.getStatus());
                teams.setCafStatus(message.getStatus());
                teams.setIsDeleted(message.getIsDeleted());
                teams.setLcoId(message.getLcoId());
                teams.setMvnoId(message.getMvnoId());
                teams.setCreatedById(message.getCreatedById());
                teams.setLastModifiedById(message.getLastModifiedById());
                if (message.getTeamType()!=null) {
                    teams.setTeamType(message.getTeamType());
                }
                teamsRepository.save(teams);
                logger.info("Teams updated successfully with name " + message.getName());
            } else {
                Teams teams1 = new Teams();
                teams1.setId(message.getId());
                teams1.setParentTeams(message.getParentTeams());
                teams1.setName(message.getName());
                teams1.setStatus(message.getStatus());
                teams1.setCafStatus(message.getStatus());
                teams1.setIsDeleted(message.getIsDeleted());
                teams1.setLcoId(message.getLcoId());
                teams1.setMvnoId(message.getMvnoId());
                teams1.setCreatedById(message.getCreatedById());
                teams1.setLastModifiedById(message.getLastModifiedById());
                if (message.getTeamType()!=null) {
                    teams.setTeamType(message.getTeamType());
                }
                teamsRepository.save(teams1);
                logger.info("Teams updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update teams with name " + message.getName());
        }
    }
}
