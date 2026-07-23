package com.savbill.cpm.modules.Teams.service;

import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.SaveTeamsSharedSharedData;
import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.UpdateTeamsSharedData;
import com.savbill.cpm.constants.SearchConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.QStaffUser;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.CommonList.repository.CommonListRepository;
import com.savbill.cpm.modules.Teams.domain.*;
import com.savbill.cpm.modules.Teams.domain.QTeamUserMapping;
import com.savbill.cpm.modules.Teams.domain.QTeams;
import com.savbill.cpm.modules.Teams.domain.TeamUserMapping;
import com.savbill.cpm.modules.Teams.domain.Teams;
import com.savbill.cpm.modules.Teams.mapper.TeamsMapper;
import com.savbill.cpm.modules.Teams.model.TeamDtoFinance;
import com.savbill.cpm.modules.Teams.model.TeamsDTO;
import com.savbill.cpm.modules.Teams.repository.HierarchyRepository;
import com.savbill.cpm.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.cpm.modules.Teams.repository.TeamUserMappingsRepocitory;
import com.savbill.cpm.modules.Teams.repository.TeamsRepository;
import com.savbill.cpm.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.cpm.modules.planUpdate.repository.CustomerPackageRepository;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.repository.common.StaffUserRepository;
import com.savbill.cpm.repository.postpaid.CreditDocRepository;
import com.savbill.cpm.repository.postpaid.PostpaidPlanRepo;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.utils.CommonConstants;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.savbill.cpm.core.utillity.log.ApplicationLogger.logger;

@Service
public class TeamsService extends ExBaseAbstractService<TeamsDTO, Teams, Long> {

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    CommonListRepository commonListRepository;

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    CustomerPackageRepository customerPackageRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    private TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    HierarchyService hierarchyService;

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
        return "[Teams Service]";
    }

    public Teams getById(Long id) {
        return teamsRepository.findById(id).get();
    }

    public boolean checkTeamIsAlreadyParentTeam(Long parentTeamId) {
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
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Teams> teamList = null;
            if(getLoggedInUser().getLco()) {
                if (getMvnoIdFromCurrentStaff() == 1)
                    teamList = teamsRepository.findAllBy(name, name, pageRequest,getLoggedInUser().getPartnerId());
                else
                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());

            }
            else {
                if (getMvnoIdFromCurrentStaff() == 1)
                    teamList = teamsRepository.findAllBy(name, name, pageRequest);
                else
                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));

            }

            if (null != teamList && 0 < teamList.getSize()) {
                makeGenericResponse(genericDataDTO, teamList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getTeamByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Teams");
        createExcel(workbook, sheet, TeamsDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, TeamsDTO.class, null);
    }

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
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if(getLoggedInUser().getLco())
            {
                if (getMvnoIdFromCurrentStaff() == 1)
                    paginationList = teamsRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
                else if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
                    paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
                else
                    paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());

            }
            else
            {
                if (getMvnoIdFromCurrentStaff() == 1)
                    paginationList = teamsRepository.findAll(pageRequest);
                else if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
                    paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));

            }

            if (null != paginationList && 0 < paginationList.getSize()) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }


    @Override
    public TeamsDTO saveEntity(TeamsDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        entity.setPartnerid((long) getLoggedInUserPartnerId());
        return super.saveEntity(entity);
    }

    @Override
    public TeamsDTO updateEntity(TeamsDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        entity.setPartnerid((long) getLoggedInUserPartnerId());
        return super.updateEntity(entity);
    }

    public List<Teams> findChildTeams(Teams team, List<Teams> teamList) {
        if (team != null && team.getParentTeams() != null) {
            teamList.add(team.getParentTeams());
            findChildTeams(team.getParentTeams(), teamList);
        }
        return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    public List<Teams> findParentTeams(Teams team, List<Teams> teamList) {
        Teams secondlastTeams = teamsRepository.findByParentTeams(team);
        if (secondlastTeams != null) {
            secondlastTeams.setCafStatus("Approved");
            teamList.add(secondlastTeams);
            findParentTeams(secondlastTeams, teamList);
        }
        return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = teamsRepository.duplicateVerifyAtSave(name);
            else count = teamsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public GenericDataDTO getStaffUsersFromTeamId(Long teamId) {
        try {

            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
            genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
            QStaffUser qStaffUser = QStaffUser.staffUser;
            Teams teams = teamsRepository.findById(teamId).orElse(null);
            BooleanExpression booleanExpression = qStaffUser.isNotNull().and(qStaffUser.team.contains(teams));
            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoPojo((List<StaffUser>) staffUserRepository.findAll(booleanExpression)));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
//                    AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, teams.getId().longValue(), teams.getName());
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public GenericDataDTO getAllTeamBasedOnAttchedStaff() {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
            genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
            QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
            BooleanExpression booleanExpression = qTeamUserMapping.isNotNull();
            booleanExpression = booleanExpression.and(qTeamUserMapping.teamId.isNotNull().and(qTeamUserMapping.staffId.isNotNull()));
            List<TeamUserMapping> teamUserMappingList = (List<TeamUserMapping>) teamUserMappingsRepocitory.findAll(booleanExpression);
            List<Long> teamIdList = teamUserMappingList.stream().map(TeamUserMapping::getTeamId).collect(Collectors.toList());
            List<Teams> teamsList = teamsRepository.findAllByIdIn(teamIdList);
            List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(teamsList, new CycleAvoidingMappingContext());
            genericDataDTO.setDataList(teamsDTOS);
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public List<TeamsDTO> getAllTeams(String teamType){
        QTeams qTeams = QTeams.teams;
        Integer mvnoId = getLoggedInUser().getMvnoId();
        BooleanExpression booleanExpression1 = qTeams.isNotNull().and(qTeams.isDeleted.eq(false));
        if (teamType!=null) {
             booleanExpression1 = qTeams.teamType.equalsIgnoreCase("Finance");
        }
        if(mvnoId!=null && mvnoId!=1){
            booleanExpression1 = booleanExpression1.and(qTeams.mvnoId.in(mvnoId,1));
        }
        if(getLoggedInUser().getLco()){
            booleanExpression1 =booleanExpression1.and(qTeams.lcoId.eq(getLoggedInUser().getPartnerId()));
        }
        List<Teams> teamsList = (List<Teams>) teamsRepository.findAll(booleanExpression1);
        List<Teams> newTeamsList =  new ArrayList<>();
        for(Teams teams:teamsList){
            Set<StaffUser> staffUsers =  teams.getStaffUser().stream().filter(staffUser -> !staffUser.getStatus().equalsIgnoreCase("TERMINATED")).collect(Collectors.toSet());
            teams.setStaffUser(staffUsers);
            newTeamsList.add(teams);
        }

        List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(newTeamsList, new CycleAvoidingMappingContext());
        return teamsDTOS;
    }

    public List<TeamDtoFinance> getAllTeamsForFinance(String teamType){
        QTeams qTeams = QTeams.teams;
        Integer mvnoId = getLoggedInUser().getMvnoId();
        BooleanExpression booleanExpression1 = qTeams.isNotNull().and(qTeams.isDeleted.eq(false));
        if (teamType!=null) {
             booleanExpression1 = qTeams.teamType.equalsIgnoreCase("Finance");
        }
        if(mvnoId!=null && mvnoId!=1){
            booleanExpression1 = booleanExpression1.and(qTeams.mvnoId.eq(mvnoId));
        }
        if(getLoggedInUser().getLco()){
            booleanExpression1 =booleanExpression1.and(qTeams.lcoId.eq(getLoggedInUser().getPartnerId()));
        }
        List<Teams> teamsList = (List<Teams>) teamsRepository.findAll(booleanExpression1);
        List<Teams> newTeamsList =  new ArrayList<>();
        for(Teams teams:teamsList){
            Set<StaffUser> staffUsers =  teams.getStaffUser().stream().filter(staffUser -> !staffUser.getStatus().equalsIgnoreCase("TERMINATED")).collect(Collectors.toSet());
            teams.setStaffUser(staffUsers);
            newTeamsList.add(teams);
        }
        List<TeamDtoFinance> teamsDTOS = new ArrayList<>();
        for (Teams team : teamsList){
            TeamDtoFinance teamsDTO= new TeamDtoFinance();
            teamsDTO.setId(team.getId());
            teamsDTO.setName(team.getName());
            teamsDTO.setStatus(team.getStatus());
            teamsDTO.setMvnoId(team.getMvnoId());
            teamsDTO.setDisplayName(team.getName());
            teamsDTO.setDisplayId(team.getId());
            List<Long> staffIdList = new ArrayList<>();
            List<String> staffNameList = new ArrayList<>();
            for (StaffUser staffUser : team.getStaffUser()){
                if (!staffUser.getStatus().equalsIgnoreCase("TERMINATED")){
                    staffIdList.add(staffUser.getId().longValue());
                    staffNameList.add(staffUser.getFirstname() +" " + staffUser.getLastname());
                }
            }
            teamsDTO.setStaffUserIds(staffIdList);
            teamsDTO.setStaffNameList(staffNameList);

            teamsDTOS.add(teamsDTO);
        }

//        List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(newTeamsList, new CycleAvoidingMappingContext());
        return teamsDTOS;
    }


    // Shared Data From Common APIGW to CMS
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
//            teams.setStaffUser(message.getStaffUser());
            teams.setPartner(message.getPartner());
            if (message.getTeamType()!=null) {
                teams.setTeamType(message.getTeamType());
            }
            teamsRepository.save(teams);
            logger.info("Teams created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create teams with name " + message.getName(), e.getMessage());
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
//                teams.setStaffUser(message.getStaffUser());
                teams.setPartner(message.getPartner());
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
//                teams1.setStaffUser(message.getStaffUser());
                teams1.setPartner(message.getPartner());
                if (message.getTeamType()!=null) {
                    teams.setTeamType(message.getTeamType());
                }
                teamsRepository.save(teams1);
                logger.info("Teams updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update teams with name " + message.getName(), e.getMessage());
        }
    }
}
