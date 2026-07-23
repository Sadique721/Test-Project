package com.savbill.commonGateway.moules.TeamsManagement.Teams;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping.TeamHierarchyMappingRepo;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.QTeamUserMapping;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.TeamUserMapping;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping.WareHouseTeamsMappingRepo;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamsService extends ExBaseAbstractService<TeamsDTO, Teams, Long> {
    public TeamsService(TeamsRepository repository, TeamsMapper mapper) {
        super(repository, mapper);
    }
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    TeamsMapper teamsMapper;
    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Override
    public String getModuleNameForLog() {
        return "[TeamsService]";
    }
    private static final Logger logger = LoggerFactory.getLogger(TeamsService.class);
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
            List<Teams> teamList = new ArrayList<>();
            Page<Teams> teamsPage = null;
            if (getLoggedInUser().getLco()) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndNameContainingIgnoreCase(getLoggedInUser().getPartnerId(), name, pageRequest);
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndNameContainingIgnoreCase(getLoggedInUser().getPartnerId(), getMvnoIdFromCurrentStaff(), name, pageRequest);
                    } else {
                        teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndLcoIdAndNameContainingIgnoreCase(getLoggedInUserPartnerId(), getMvnoIdFromCurrentStaff(), getLoggedInUser().getPartnerId(), name, pageRequest);
                    }
                }
            } else {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndNameContainsIgnoreCase(name, pageRequest);
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndMvnoIdAndNameContainingIgnoreCase(getMvnoIdFromCurrentStaff(), name, pageRequest);
                    } else {
                        teamsPage = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndNameContainingIgnoreCase(getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(), name, pageRequest);
                    }
                }
            }
            if (null != teamsPage && 0 < teamsPage.getSize()) {
                makeGenericResponse(genericDataDTO, teamsPage);
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
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY) && !searchModel.getFilterValue().equals("")) {
                        return getTeamByName(searchModel.getFilterValue(), pageRequest);
                    } else {
                        return getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public GenericDataDTO searchByProduct(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String productType) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "name", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getTeams(searchModel.getFilterValue(), pageRequest,  productType);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public GenericDataDTO getTeams(String name, PageRequest pageRequest, String productType) {
        String SUBMODULE = getModuleNameForLog() + " [getTeamByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QTeams qTeams = QTeams.teams;
            BooleanExpression booleanExpression = qTeams.isNotNull().and(qTeams.isDeleted.eq(false)).and(qTeams.name.likeIgnoreCase("%" + name + "%"));
            if(getLoggedInUser().getLco())
                booleanExpression=booleanExpression.and(qTeams.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression=booleanExpression.and(qTeams.lcoId.isNull());
            if(productType!= null){
                booleanExpression = booleanExpression.and(qTeams.product.containsIgnoreCase(productType));
            }

            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qTeams.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            Page<Teams> teamList = teamsRepository.findAll(booleanExpression, pageRequest);
            if (0 < teamList.getSize()) {
                makeGenericResponse(genericDataDTO, teamList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public Teams getById(Long id){
        return teamsRepository.findById(id).get();
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
            if (getLoggedInUser().getLco()) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_Id(getLoggedInUser().getPartnerId(), pageRequest);
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoId(getLoggedInUser().getPartnerId(), getMvnoIdFromCurrentStaff(), pageRequest);
                    } else {
                        paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndLcoId(getLoggedInUserPartnerId(), getMvnoIdFromCurrentStaff(), getLoggedInUser().getPartnerId(), pageRequest);
                    }
                }
            } else {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    paginationList = teamsRepository.findAllByIsDeletedIsFalse(pageRequest);
                } else {
                    if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                        paginationList = teamsRepository.findAllByIsDeletedIsFalseAndMvnoId(getMvnoIdFromCurrentStaff(), pageRequest);
                    } else {
                        paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoId(getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(), pageRequest);
                    }
                }
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

    public GenericDataDTO getPagination(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String productType) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Teams> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            StaffUser staffUser= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);
            if(getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment()!=null) {
                paginationList = teamsRepository.findByDepartmentId(staffUser.getDepartment(), pageRequest);
            }else {
                if (getLoggedInUser().getLco()) {
                    if (getMvnoIdFromCurrentStaff() == 1) {

                        paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_Id(getLoggedInUser().getPartnerId(), pageRequest);
                    } else {
                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                            paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoId(getLoggedInUser().getPartnerId(), getMvnoIdFromCurrentStaff(), pageRequest);
                        } else {
                            paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndLcoId(getLoggedInUserPartnerId(), getMvnoIdFromCurrentStaff(), getLoggedInUser().getPartnerId(), pageRequest);
                        }
                    }
                } else {
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        paginationList = teamsRepository.findAllByIsDeletedIsFalse(pageRequest);
                    } else {
                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                            paginationList = teamsRepository.findAllByIsDeletedIsFalseAndMvnoId(getMvnoIdFromCurrentStaff(), pageRequest);
                        } else {
                            paginationList = teamsRepository.findAllByIsDeletedIsFalseAndPartner_IdAndMvnoId(getLoggedInUserPartnerId(), getMvnoIdFromCurrentStaff(), pageRequest);
                        }
                    }
                }
                if(productType!= null){
                    paginationList=teamsRepository.findAllByProductAndIsDeletedFalseAndMvnoIdIn(productType,pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(),1));
                }
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
    public List<TeamsDTO> getAllTeams(){
        List<Teams> teamsList = new ArrayList<>();
        if (getLoggedInUser().getLco()) {
            if (getMvnoIdFromCurrentStaff() == 1) {
                teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatusAndPartner_Id(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId());
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdInAndLcoId(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
                }
            }
        } else {
            if (getMvnoIdFromCurrentStaff() == 1) {
                teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoId(CommonConstants.ACTIVE_STATUS, getMvnoIdFromCurrentStaff());
                } else {
                    teamsList = teamsRepository.findAllByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(),Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
        }
        List<Teams> newTeamsList =  new ArrayList<>();
        for(Teams teams:teamsList){
            Set<StaffUser> staffUsers =  teams.getStaffUser().stream().filter(staffUser -> !staffUser.getStatus().equalsIgnoreCase("TERMINATED")).collect(Collectors.toSet());
            teams.setStaffUser(staffUsers);
            newTeamsList.add(teams);
        }

        List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(newTeamsList, new CycleAvoidingMappingContext());
        return teamsDTOS;
    }
    public List<TeamsMinimalDTO> getAllTeamsMinimal() {
        List<TeamsMinimalDTO> teamsList;
        Long partnerId = (long) getLoggedInUserPartnerId();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        // If partnerId is DEFAULT_PARTNER_ID, set it to null
        partnerId = (partnerId.equals(CommonConstants.DEFAULT_PARTNER_ID)) ? null : partnerId;
        if (getLoggedInUser().getLco()) {
            if (mvnoId == 1) {
                teamsList = (partnerId != null)
                        ? teamsRepository.findAllMinimalByStatusAndPartnerId(CommonConstants.ACTIVE_STATUS, partnerId)
                        : teamsRepository.findAllMinimalByStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                teamsList = (partnerId != null)
                        ? teamsRepository.findAllMinimalByStatusAndPartnerIdAndMvno(CommonConstants.ACTIVE_STATUS, partnerId, mvnoId)
                        : teamsRepository.findAllMinimalByStatusAndMvno(CommonConstants.ACTIVE_STATUS, Arrays.asList(mvnoId,1));
            }
        } else {
            if (mvnoId == 1) {
                StaffUser staffUser= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);
                if(getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment()!=null) {
                    teamsList = teamsRepository.findByDepartmentIdWithoutPagination(CommonConstants.ACTIVE_STATUS, partnerId,staffUser.getDepartment());
                }else {
                    teamsList = (partnerId != null)
                            ? teamsRepository.findAllMinimalByStatusAndPartnerId(CommonConstants.ACTIVE_STATUS, partnerId)
                            : teamsRepository.findAllMinimalByStatus(CommonConstants.ACTIVE_STATUS);
                }
            } else {
                teamsList = (partnerId != null)
                        ? teamsRepository.findAllMinimalByStatusAndPartnerIdAndMvno(CommonConstants.ACTIVE_STATUS, partnerId, mvnoId)
                        : teamsRepository.findAllMinimalByStatusAndMvno(CommonConstants.ACTIVE_STATUS, Arrays.asList(mvnoId,1));
            }
        }
        return teamsList;
    }
    public List<TeamsDTO> getAllTeamByProduct(String productType) {
        List<Teams> teamsList = new ArrayList<>();
        if (getLoggedInUser().getLco()) {
            if (getMvnoIdFromCurrentStaff() == 1) {
                teamsList = teamsRepository.findAllByStatusAndPartenerId(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId());
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    teamsList = teamsRepository.findAllByStatusAndPartner_IdAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUser().getPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    teamsList = teamsRepository.findByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdInAndLcoId(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
                }
            }
        } else {
            if (getMvnoIdFromCurrentStaff() == 1) {
                teamsList = teamsRepository.findByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else {

                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    teamsList = teamsRepository.findAllByStatusAndMvnoIdInAndProduct(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),productType);
                } else {
                    teamsList = teamsRepository.findAllByStatusAndPartner_IdAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(),Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
        }
//        teamsList = teamsRepository.findByProduct(productType,CommonConstants.ACTIVE_STATUS);
//        List<Teams> newTeamsList =  new ArrayList<>();
//        for(Teams teams:teamsList){
//            Set<StaffUser> staffUsers =  teams.getStaffUser().stream().filter(staffUser -> !staffUser.getStatus().equalsIgnoreCase("TERMINATED")).collect(Collectors.toSet());
//            teams.setStaffUser(staffUsers);
//            newTeamsList.add(teams);
//        }
        List<TeamsDTO> teamsDTOS = teamsMapper.domainToDTO(teamsList, new CycleAvoidingMappingContext());
        return teamsDTOS;
    }


    public void sharedTeamData(Teams teams, Integer operation) {
        Set<StaffUser> staffUsers = new HashSet<>();
        List<Long> staffUserIds = teamUserMappingsRepocitory.findAllByTeamId(teams.getId()).stream().map(TeamUserMapping::getStaffId).collect(Collectors.toList());
        if (!staffUserIds.isEmpty()) {
            for (Long id : staffUserIds) {
                StaffUser staffUser = new StaffUser();
                staffUser.setId(Math.toIntExact(id));
                staffUsers.add(staffUser);
            }
            teams.setStaffUser(staffUsers);
        }
        if (operation.equals(CommonConstants.OPERATION_ADD)) {
            createDataSharedService.sendEntitySaveDataForAllMicroService(teams);
        } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
            createDataSharedService.updateEntityDataForAllMicroService(teams);
        } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
            createDataSharedService.deleteEntityDataForAllMicroService(teams);
        }
    }

    public void setTeamStaffUserMapping(TeamsDTO teamsDTO) {
        for (Long id : teamsDTO.getStaffUserIds()) {
            TeamUserMapping teamUserMapping = new TeamUserMapping();
            teamUserMapping.setTeamId(teamsDTO.getId());
            teamUserMapping.setStaffId(id);
            teamUserMappingsRepocitory.save(teamUserMapping);
        }
    }
    public boolean deleteVerificationByTeamUser(Integer id) throws Exception {
        boolean flag = false;
        Integer teamUserCount = teamUserMappingsRepocitory.countByTeamId(id.longValue());
        if (teamUserCount == 0) {
            flag = true;
        }
        return flag;
    }
    public boolean deleteVerificationByTeamHierarchy(Integer id) throws Exception {
        boolean flag = false;
        Integer teamHierarchyCount = teamHierarchyMappingRepo.countByTeamId(id);
        if (teamHierarchyCount == 0) {
            flag = true;
        }
        return flag;
    }
    public boolean deleteVerificationByTeamWarehouse(Integer id) throws Exception {
        boolean flag = false;
        Integer teamWarehouseCount = wareHouseTeamsMappingRepo.countByTeamId(id.longValue());
        if (teamWarehouseCount == 0) {
            flag = true;
        }
        return flag;
    }
}
