package com.savbill.partnermanagement.modules.Teams;

import com.savbill.partnermanagement.core.constants.CommonConstants;
import com.savbill.partnermanagement.core.constants.SearchConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.partnermanagement.modules.TeamUserMapping.TeamUserMappingsRepocitory;
//import com.savbill.partnermanagement.rabbitmq.MessageSender;
import com.savbill.partnermanagement.rabbitmq.setting.SaveTeamsSharedSharedData;
import com.savbill.partnermanagement.rabbitmq.setting.UpdateTeamsSharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamsService extends ExBaseAbstractService<TeamsDTO, Teams, Long> {

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

//    @Autowired
//    private MessageSender messageSender;

//    @Autowired
//    NotificationTemplateRepository templateRepository;

//    @Autowired
//    private StaffUserService staffUserService;

//    @Autowired
//    CommonListRepository commonListRepository;

//    @Autowired
//    CustomerPackageRepository customerPackageRepository;

//    @Autowired
//    CreditDocRepository creditDocRepository;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    @Autowired
    TeamsMapper teamsMapper;

    public TeamsService(@Lazy TeamsRepository repository, @Lazy TeamsMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "team_id");
        sortColMap.put("name", "team_name");
        sortColMap.put("status", "team_status");
    }

    @Override
    public String getModuleNameForLog() {
        return "[TeamsService]";
    }

    private static final Logger logger = LoggerFactory.getLogger(TeamsService.class);

    public Teams getById(Long id) {
        return teamsRepository.findById(id).get();
    }

    public boolean checkTeamIsAlreadyParentTeam(Long parentTeamId) {
        logger.info("Checking if team with ID {} is already a parent team", parentTeamId);
        Long result = teamsRepository.checkTeamIsAlreadyParentTeam(parentTeamId);
        if (result != null && result != 0) {
            return true;
        } else {
            return false;
        }
    }

    public GenericDataDTO getTeamByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            ApplicationLogger.logger.info(SUBMODULE + "Fetching teams with name: {}", name);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Teams> teamList = null;
            if(getLoggedInUser().getLco()) {
                ApplicationLogger.logger.info(SUBMODULE + "User is LCO");

                if (getMvnoIdFromCurrentStaff() == 1) {
                    ApplicationLogger.logger.info(SUBMODULE + "Fetching for MVNO ID 1 with partner ID {}", getLoggedInUser().getPartnerId());
                    teamList = teamsRepository.findAllBy(name, name, pageRequest, getLoggedInUser().getPartnerId());
                }else {
                    ApplicationLogger.logger.info(SUBMODULE + "Fetching for MVNO ID list {}, with partner ID {}", Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
                }
            }
            else {
                ApplicationLogger.logger.info(SUBMODULE + "User is not LCO");

                if (getMvnoIdFromCurrentStaff() == 1) {
                    ApplicationLogger.logger.info(SUBMODULE + "Fetching for MVNO ID 1");
                    teamList = teamsRepository.findAllBy(name, name, pageRequest);
                }else {
                    ApplicationLogger.logger.info(SUBMODULE + "Fetching for MVNO ID list {}", Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }

            if (null != teamList && 0 < teamList.getSize()) {
                ApplicationLogger.logger.info(SUBMODULE + "Found {} teams", teamList.getSize());
                makeGenericResponse(genericDataDTO, teamList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + "Error occurred while fetching teams: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            ApplicationLogger.logger.info(SUBMODULE + "Search called with filters: {}, page: {}, pageSize: {}, sortBy: {}, sortOrder: {}",
                    filterList, page, pageSize, sortBy, sortOrder);
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);

            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        ApplicationLogger.logger.info(SUBMODULE + "Searching by ANY column with value: {}", searchModel.getFilterValue());
                        return getTeamByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + "Exception occurred during search: " + ex.getMessage(), ex);
        }
        return null;
    }

//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Teams");
//        createExcel(workbook, sheet, TeamsDTO.class, null);
//    }

//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, TeamsDTO.class, null);
//    }

    public List<TeamsDTO> getAllByIdIn(List<Long> idList) throws Exception {
        return teamsRepository.findAllByIdInAndIsDeletedIsFalse(idList).stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }


    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Teams> paginationList = null;
        try {
            ApplicationLogger.logger.info(SUBMODULE + "Request received with pageNumber: {}, pageSize: {}, sortBy: {}, sortOrder: {}, filters: {}",
                    pageNumber, customPageSize, sortBy, sortOrder, filterList);

            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);

            if(getLoggedInUser().getLco()){
                ApplicationLogger.logger.debug(SUBMODULE + "User is LCO. MvnoId: {}, PartnerId: {}", getMvnoIdFromCurrentStaff(), getLoggedInUser().getPartnerId());

                if (getMvnoIdFromCurrentStaff() == 1) {
                    ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAll for mvnoId 1");
                    paginationList = teamsRepository.findAll(pageRequest, getLoggedInUser().getPartnerId());
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAll for DEFAULT_PARTNER_ID");
                        paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
                    } else {
                        ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn for DEFAULT_PARTNER_ID");
                        paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(
                                getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),
                                getLoggedInUser().getPartnerId());
                    }
                }
            }else{
                if (getMvnoIdFromCurrentStaff() == 1) {
                    ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAll for mvnoId 1");
                    paginationList = teamsRepository.findAll(pageRequest);
                }else if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAll for DEFAULT_PARTNER_ID");
                    paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }else{
                    ApplicationLogger.logger.info(SUBMODULE + "Calling teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn (non-LCO)");
                    paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));

                    }
            }
            if (null != paginationList && 0 < paginationList.getSize()) {
                ApplicationLogger.logger.info(SUBMODULE + "Pagination list retrieved with {} records", paginationList.getSize());
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }


    @Override
    public TeamsDTO saveEntity(TeamsDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        entity.setPartnerid((long) getLoggedInUserPartnerId());
        ApplicationLogger.logger.debug(SUBMODULE + "Set mvnoId: {}, partnerId: {}", entity.getMvnoId(), entity.getPartnerid());

        TeamsDTO savedEntity =  super.saveEntity(entity);
        ApplicationLogger.logger.info(SUBMODULE + "Entity saved successfully with ID: {}", savedEntity.getId());
        return savedEntity;

    }

    @Override
    public TeamsDTO updateEntity(TeamsDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";

        try {
           ApplicationLogger.logger.info(SUBMODULE + "Updating entity with ID: {}", entity.getId());
           entity.setMvnoId(getMvnoIdFromCurrentStaff());
           entity.setPartnerid((long) getLoggedInUserPartnerId());
           ApplicationLogger.logger.debug(SUBMODULE + "Set mvnoId: {}, partnerId: {}", entity.getMvnoId(), entity.getPartnerid());

            TeamsDTO updatedEntity =super.updateEntity(entity);
            ApplicationLogger.logger.info(SUBMODULE + "Entity updated successfully with ID: {}", updatedEntity.getId());
            return updatedEntity;

       } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + "Exception occurred while updating entity: " + ex.getMessage(), ex);
            throw ex;
       }
    }

    public List<Teams> findChildTeams(Teams team, List<Teams> teamList) {
        String SUBMODULE = getModuleNameForLog() + " [findChildTeams()] ";
        try {
            if (team != null && team.getParentTeams() != null) {
                ApplicationLogger.logger.debug(SUBMODULE + "Team ID: {} has parent team ID: {}", team.getId(), team.getParentTeams().getId());
                teamList.add(team.getParentTeams());
                findChildTeams(team.getParentTeams(), teamList);
            }
            return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + "Exception occurred: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Teams> findParentTeams(Teams team, List<Teams> teamList) {
        String SUBMODULE = getModuleNameForLog() + " [findParentTeams()] ";
        Teams secondlastTeams = teamsRepository.findByParentTeams(team);
        if (secondlastTeams != null) {
            ApplicationLogger.logger.debug(SUBMODULE + "Parent team found for team ID {}: {}", team.getId(), secondlastTeams.getId());
            secondlastTeams.setCafStatus("Approved");
            teamList.add(secondlastTeams);
            findParentTeams(secondlastTeams, teamList);
        }
        return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [duplicateVerifyAtSave()] ";
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = teamsRepository.duplicateVerifyAtSave(name);
            else count = teamsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            ApplicationLogger.logger.info(SUBMODULE + "Checking duplicate for team name '{}'. Found count: {}", name, count);

            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = teamsRepository.duplicateVerifyAtSave(name);
            else count = teamsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = teamsRepository.duplicateVerifyAtEdit(name, id);
                else
                    countEdit = teamsRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                ApplicationLogger.logger.debug( "Edit count for name '{}' and ID {}: {}", name, id, countEdit);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

//    public GenericDataDTO getStaffUsersFromTeamId(Long teamId) {
//        try {
//
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
//            genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
//            QStaffUser qStaffUser = QStaffUser.staffUser;
//            Teams teams = teamsRepository.findById(teamId).orElse(null);
//            BooleanExpression booleanExpression = qStaffUser.isNotNull().and(qStaffUser.team.contains(teams));
//            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoPojo((List<StaffUser>) staffUserRepository.findAll(booleanExpression)));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
////                    AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, teams.getId().longValue(), teams.getName());
//            return genericDataDTO;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

//    public GenericDataDTO getAllTeamBasedOnAttchedStaff() {
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
//            genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
//            QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
//            BooleanExpression booleanExpression = qTeamUserMapping.isNotNull();
//            booleanExpression = booleanExpression.and(qTeamUserMapping.teamId.isNotNull().and(qTeamUserMapping.staffId.isNotNull()));
//            List<TeamUserMapping> teamUserMappingList = (List<TeamUserMapping>) teamUserMappingsRepocitory.findAll(booleanExpression);
//            List<Long> teamIdList = teamUserMappingList.stream().map(TeamUserMapping::getTeamId).collect(Collectors.toList());
//            List<Teams> teamsList = teamsRepository.findAllByIdIn(teamIdList);
//            List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(teamsList, new CycleAvoidingMappingContext());
//            genericDataDTO.setDataList(teamsDTOS);
//            return genericDataDTO;
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//    public List<TeamsDTO> getAllTeams(){
//        QTeams qTeams = QTeams.teams;
//        BooleanExpression booleanExpression1 = qTeams.isNotNull().and(qTeams.isDeleted.eq(false));
//        if(getLoggedInUser().getLco()){
//            booleanExpression1 =booleanExpression1.and(qTeams.lcoId.eq(getLoggedInUser().getPartnerId()));
//        }
//        List<Teams> teamsList = (List<Teams>) teamsRepository.findAll(booleanExpression1);
//        List<Teams> newTeamsList =  new ArrayList<>();
//        for(Teams teams:teamsList){
//            Set<StaffUser> staffUsers =  teams.getStaffUser().stream().filter(staffUser -> !staffUser.getStatus().equalsIgnoreCase("TERMINATED")).collect(Collectors.toSet());
//            teams.setStaffUser(staffUsers);
//            newTeamsList.add(teams);
//        }
//
//        List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(newTeamsList, new CycleAvoidingMappingContext());
//        return teamsDTOS;
//    }

    public void saveTeams(SaveTeamsSharedSharedData message){
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
//            teams.setStaffUser(message.getStaffUser());
            teams.setPartner(message.getPartner());
            teamsRepository.save(teams);
            logger.info("Teams created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create teams with name " + message.getName(), e.getMessage());
        }
    }


    public void updateTeams(UpdateTeamsSharedData message){
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
//                teams.setStaffUser(message.getStaffUser());
                teams.setPartner(message.getPartner());
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
//                teams1.setStaffUser(message.getStaffUser());
                teams1.setPartner(message.getPartner());
                teamsRepository.save(teams1);
                logger.info("Teams updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update teams with name " + message.getName(), e.getMessage());
        }
    }
}
