package com.savbill.inventorymanagement.modules.WorkflowManagement.Teams;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanRepo;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyMappingRepo;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveTeamsSharedSharedData;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateTeamsSharedData;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

//    @Autowired
//    NotificationTemplateRepository templateRepository;

//    @Autowired
//    private StaffUserService staffUserService;

//    @Autowired
//    CommonListRepository commonListRepository;

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

//    @Autowired
//    CustomerPackageRepository customerPackageRepository;

//    @Autowired
//    CreditDocRepository creditDocRepository;

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
        return "[TeamsService]";
    }

    private static final Logger logger = Logger.getLogger(TeamsService.class);

    public Teams getById(Long id) {
        return teamsRepository.findById(id).get();
    }

//    public boolean checkTeamIsAlreadyParentTeam(Long parentTeamId) {
//        Long result = teamsRepository.checkTeamIsAlreadyParentTeam(parentTeamId);
//        if (result != null && result != 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public GenericDataDTO getTeamByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Teams> teamList = null;
//            if(getLoggedInUser().getLco()) {
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    teamList = teamsRepository.findAllBy(name, name, pageRequest,getLoggedInUser().getPartnerId());
//                else
//                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
//
//            }
//            else {
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    teamList = teamsRepository.findAllBy(name, name, pageRequest);
//                else
//                    teamList = teamsRepository.findAllBy(name, name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//
//            }
//
//            if (null != teamList && 0 < teamList.getSize()) {
//                makeGenericResponse(genericDataDTO, teamList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }

//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getTeamByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }

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


//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<Teams> paginationList = null;
//        try {
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if(getLoggedInUser().getLco())
//            {
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    paginationList = teamsRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
//                else if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
//                    paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
//                else
//                    paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
//
//            }
//            else
//            {
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    paginationList = teamsRepository.findAll(pageRequest);
//                else if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
//                    paginationList = teamsRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    paginationList = teamsRepository.findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(getLoggedInUserPartnerId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//
//            }
//
//            if (null != paginationList && 0 < paginationList.getSize()) {
//                makeGenericResponse(genericDataDTO, paginationList);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }


//    @Override
//    public TeamsDTO saveEntity(TeamsDTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
//        entity.setPartnerid((long) getLoggedInUserPartnerId());
//        return super.saveEntity(entity);
//    }
//
//    @Override
//    public TeamsDTO updateEntity(TeamsDTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
//        entity.setPartnerid((long) getLoggedInUserPartnerId());
//        return super.updateEntity(entity);
//    }

//    public List<Teams> findChildTeams(Teams team, List<Teams> teamList) {
//        if (team != null && team.getParentTeams() != null) {
//            teamList.add(team.getParentTeams());
//            findChildTeams(team.getParentTeams(), teamList);
//        }
//        return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//    }
//
//    public List<Teams> findParentTeams(Teams team, List<Teams> teamList) {
//        Teams secondlastTeams = teamsRepository.findByParentTeams(team);
//        if (secondlastTeams != null) {
//            secondlastTeams.setCafStatus("Approved");
//            teamList.add(secondlastTeams);
//            findParentTeams(secondlastTeams, teamList);
//        }
//        return teamList.stream().filter(teams -> teams.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || teams.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//    }

//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = teamsRepository.duplicateVerifyAtSave(name);
//            else count = teamsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = teamsRepository.duplicateVerifyAtSave(name);
//            else count = teamsRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1) countEdit = teamsRepository.duplicateVerifyAtEdit(name, id);
//                else
//                    countEdit = teamsRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
    public GenericDataDTO getAllTeamBasedOnAttchedStaff() {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
            genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
//            List<Teams> teamsList = new ArrayList<>();
            List<Object[]> results = new ArrayList<>();
            if (getLoggedInUser().getLco()) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    results = teamsRepository.findTeamIdAndNameByStatusAndPartnerId(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId());
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        results = teamsRepository.findTeamIdAndNameByStatusAndPartnerIdAndMvnoId(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId(), getMvnoIdFromCurrentStaff());
                    } else {
                        results = teamsRepository.findTeamIdAndNameByStatusAndPartnerIdAndMvnoIdAndLcoId(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), getMvnoIdFromCurrentStaff(), getLoggedInUser().getPartnerId());
                    }
                }
            } else {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    results = teamsRepository.findTeamIdAndNameByStatus(CommonConstants.ACTIVE_STATUS);
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        results = teamsRepository.findTeamIdAndNameByStatusAndMvnoId(CommonConstants.ACTIVE_STATUS, getMvnoIdFromCurrentStaff());
                    } else {
                        results = teamsRepository.findTeamIdAndNameByStatusAndPartnerIdAndMvnoId(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff());
                    }
                }
            }
            List<TeamsDTO> newTeamsList = results.stream()
                   .map(row -> new TeamsDTO(((Number) row[0]).longValue(), (String) row[1]))
                    .sorted(Comparator.comparing(TeamsDTO::getId).reversed())
                   .collect(Collectors.toList());
            genericDataDTO.setDataList(newTeamsList);
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

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
            if (message.getTeamType()!=null) {
                teams.setTeamType(message.getTeamType());
            }
            teams.setPartner(message.getPartner());
            teamsRepository.save(teams);
            logger.info("Teams created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create teams with name " + message.getName() + " , Error: " + e.getMessage());
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
            logger.error("Unable to update teams with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
