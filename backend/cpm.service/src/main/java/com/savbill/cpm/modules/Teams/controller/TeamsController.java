package com.savbill.cpm.modules.Teams.controller;

import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.modules.Teams.domain.Teams;
import com.savbill.cpm.modules.Teams.mapper.TeamsMapper;
import com.savbill.cpm.modules.Teams.model.TeamDtoFinance;
import com.savbill.cpm.spring.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.savbill.cpm.constants.MessageConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.Teams.model.TeamsDTO;
import com.savbill.cpm.modules.Teams.service.TeamsService;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.TeamsMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TEAMS)
public class TeamsController extends ExBaseAbstractController<TeamsDTO> {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    TeamsMapper teamsMapper;

    public TeamsController(TeamsService service) {
        super(service);
    }
    private static final Logger logger = LoggerFactory.getLogger(HierarchyController.class);
    @Override
    public String getModuleNameForLog() {
        return "[Teams Controller]";
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        requestDTO = setDefaultPaginationValues(requestDTO);
        return teamsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage(),requestDTO.getPageSize(),requestDTO.getSortBy(),requestDTO.getSortOrder(),requestDTO.getFilters());
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
        TeamsDTO teams = (TeamsDTO) genericDataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
                AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, teams.getId().longValue(), teams.getName());
     MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_ADD + "\")")
    @Override
    public GenericDataDTO save(@RequestBody TeamsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }

        if(getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = teamsService.duplicateVerifyAtSave(entityDTO.getName());
        if (flag) {
            genericDataDTO = super.save(entityDTO, result, authentication, req);
            TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
            //send message
            TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
            kafkaMessageSender.send(new KafkaMessageData(teamsMessage,TeamsMessage.class.getSimpleName()));
//            this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS,RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);

            //data share for all microservices
            Teams teams = teamsMapper.dtoToDomain(teamsDTO,new CycleAvoidingMappingContext());
            createDataSharedService.sendEntitySaveDataForAllMicroService(teams);
            genericDataDTO.setResponseMessage("Successfully Created");
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
                    AclConstants.OPERATION_TEAMS_ADD, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.TEAM_NAME_EXITS);
            logger.error("Unable to create new team with name "+entityDTO.getName()+"   :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_EDIT + "\")")
    @Override
    public GenericDataDTO update(@RequestBody TeamsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = teamsService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
        if (flag) {
            genericDataDTO = super.update(entityDTO, result, authentication, req);
            TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
            //send message
            TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
//            this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS,RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);
            kafkaMessageSender.send(new KafkaMessageData(teamsMessage,TeamsMessage.class.getSimpleName()));
            //data share for all microservices
            Teams teams = teamsMapper.dtoToDomain(teamsDTO,new CycleAvoidingMappingContext());
            createDataSharedService.updateEntityDataForAllMicroService(teams);
            genericDataDTO.setResponseMessage("Successfully Updated");
            if (teamsDTO != null)
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
                        AclConstants.OPERATION_TEAMS_EDIT, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.TEAM_NAME_EXITS);
            logger.error("Unable to update team with  With name  "+entityDTO.getName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody TeamsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req);
        TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
        genericDataDTO.setResponseMessage("Successfully Deleted");
        if (teamsDTO != null) {
            //send message
            TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
            teamsMessage.setIsDeleted(true);
//            this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS,RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);
            kafkaMessageSender.send(new KafkaMessageData(teamsMessage,TeamsMessage.class.getSimpleName()));
            //data share for all microservices
            Teams teams = teamsMapper.dtoToDomain(teamsDTO,new CycleAvoidingMappingContext());
            teams.setIsDeleted(true);
            createDataSharedService.updateEntityDataForAllMicroService(teams);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
            		AclConstants.OPERATION_TEAMS_DELETE, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
        }
        return genericDataDTO;

    }

    @GetMapping("/checkTeamIsAlreadyParentTeam/{parentTeamId}")
    public GenericDataDTO checkTeamIsAlreadyParentTeam(@PathVariable Long parentTeamId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setData(teamsService.checkTeamIsAlreadyParentTeam(parentTeamId));
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS, AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, parentTeamId, "");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")
    @GetMapping("/getStaffUsersFromTeamId/{teamId}")
    public GenericDataDTO getStaffUsersFromTeamId(@PathVariable Long teamId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = teamsService.getStaffUsersFromTeamId(teamId);

        return genericDataDTO;
    }

    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO) {
        return teamsService.search( paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),paginationRequestDTO.getSortOrder());
    }



    @GetMapping("/getAllTeamBasedOnAttchedStaff")
    public GenericDataDTO getAllTeamBasedOnAttchedStaff() throws Exception {
        GenericDataDTO genericDataDTO = teamsService.getAllTeamBasedOnAttchedStaff();
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<TeamsDTO> list=new ArrayList<>();
        try {
//            if(getLoggedInUser().getLco())
//                list = teamsService.getAllEntities().stream().filter(x->x.getLcoId()!=null && x.getLcoId().intValue()==getLoggedInUser().getPartnerId()).collect(Collectors.toList());
//            else
//                list = teamsService.getAllEntities().stream().filter(x->x.getLcoId()==null).collect(Collectors.toList());
            list = teamsService.getAllTeams(null);
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }

        return genericDataDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    @GetMapping(value = "/getAllFinanceTeam")
    public GenericDataDTO getAllTeamBasedOnAttchedStaff(HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<TeamDtoFinance> list=new ArrayList<>();
        try {

            list = teamsService.getAllTeamsForFinance("Finance");
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }

        return genericDataDTO;
    }

}
