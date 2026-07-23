package com.savbill.commonGateway.moules.TeamsManagement.Teams;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.TeamsMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TEAMS)
public class TeamsController extends ExBaseAbstractController<TeamsDTO> {

//    @Autowired
//    private AuditLogService auditLogService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private MessageSender messageSender;


    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    TeamsMapper teamsMapper;


    @Autowired
    KafkaMessageSender kafkaMessageSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamsController.class);

    public TeamsController(TeamsService service) {
        super(service);
    }
    @Override
    public String getModuleNameForLog() {
        return "[Teams Controller]";
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")

    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            return teamsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage(),requestDTO.getPageSize(),requestDTO.getSortBy(),requestDTO.getSortOrder(),requestDTO.getFilters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")
   @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\", \"" + MenuConstants.IwfTeams.TEAMS + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
       TraceContext traceContext = tracer.currentSpan().context();
       MDC.put("type", "Fetch");
       MDC.put("userName", teamsService.getLoggedInUser().getUsername());
       MDC.put("traceId", traceContext.traceIdString());
       MDC.put("spanId", traceContext.spanIdString());
       long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TeamsDTO entityDTO = new TeamsDTO();
        try {
            genericDataDTO = super.getEntityById(id, req,res);

            TeamsDTO teams = (TeamsDTO) genericDataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
//                AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, teams.getId().longValue(), teams.getName());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestForm" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH +"Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + teamsService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS));
        }catch (Exception ex ){
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }
       return genericDataDTO;

   }

 //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_ADD + "\")")
 @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS_CREATE  + "\", \"" + MenuConstants.IwfTeams.TEAMS_CREATE +"\")")
    @Override
    public GenericDataDTO save(@RequestBody TeamsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
     TraceContext traceContext = tracer.currentSpan().context();
     MDC.put("type", "Create");
     MDC.put("userName", teamsService.getLoggedInUser().getUsername());
     MDC.put("traceId", traceContext.traceIdString());
     MDC.put("spanId", traceContext.spanIdString());
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }

        if(getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = teamsService.duplicateVerifyAtSave(entityDTO.getName());
     long startTime = System.nanoTime();  // Start measuring
     try{
        if (flag) {
            genericDataDTO = super.save(entityDTO, result, authentication, req,res);
            TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
            //send message
            TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
            //this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS,RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);
            kafkaMessageSender.send(new KafkaMessageData(teamsMessage,teamsMessage.getClass().getSimpleName()));
            //data share for all microservices
            Teams teams = teamsMapper.dtoToDomain(teamsDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(teams);
            teamsService.sharedTeamData(teams, CommonConstants.OPERATION_ADD);
            genericDataDTO.setResponseMessage("Successfully Created");
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
//                    AclConstants.OPERATION_TEAMS_ADD, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestForm" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE +"Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + teamsService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS));
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.TEAM_NAME_EXITS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestForm" + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE +"Team" + LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + teamsService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL));
        }}catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE + APIConstants.ERROR_MESSAGE);
        }finally
         {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
             long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
             res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }
        return genericDataDTO;
    }

   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_EDIT + "\")")
   @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS_EDIT + "\", \"" + MenuConstants.IwfTeams.TEAMS_EDIT + "\")")
    @Override
    public GenericDataDTO update(@RequestBody TeamsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
       TraceContext traceContext = tracer.currentSpan().context();
       MDC.put("type", "Update");
       MDC.put("userName", teamsService.getLoggedInUser().getUsername());
       MDC.put("traceId", traceContext.traceIdString());
       MDC.put("spanId", traceContext.spanIdString());
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        Teams old1 = teamsService.getById(entityDTO.getId());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
       long startTime = System.nanoTime();  // Start measuring
       try {
            TeamsDTO teamsDTO1 = teamsService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = teamsService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
            if (flag) {
                genericDataDTO = super.update(entityDTO, result, authentication, req,res);
                if (!teamsDTO1.getStaffUserIds().isEmpty()) {
                    teamsService.setTeamStaffUserMapping(teamsDTO1);
                }
                TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
                //send message
                TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
                //this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS, RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);
                //data share for all microservices
                Teams teams = teamsMapper.dtoToDomain(teamsDTO, new CycleAvoidingMappingContext());
//                createDataSharedService.updateEntityDataForAllMicroService(teams);
                teamsService.sharedTeamData(teams, CommonConstants.OPERATION_UPDATE);
                genericDataDTO.setResponseMessage("Successfully Updated");
                if (teamsDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
//                        AclConstants.OPERATION_TEAMS_EDIT, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+"Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated Country Details " + UpdateDiffFinder.getUpdatedDiff(old1,teams)+ LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );

                }
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TEAM_NAME_EXITS);
                LOGGER.error(LogConstants.REQUEST_FROM +"requestFrom" + LogConstants.REQUEST_FOR +LogConstants.REQUEST_TO_UPDATE+"Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE +HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        }finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS_DELETE + "\", \"" + MenuConstants.IwfTeams.TEAMS_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody TeamsDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", teamsService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            teamsService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean teamUserFlag = teamsService.deleteVerificationByTeamUser(entityDTO.getId().intValue());
            boolean teamHierarchyFlag = teamsService.deleteVerificationByTeamHierarchy(entityDTO.getId().intValue());
            boolean teamWareHouseFlag = teamsService.deleteVerificationByTeamWarehouse(entityDTO.getId().intValue());
            if (teamUserFlag && teamHierarchyFlag && teamWareHouseFlag) {
                genericDataDTO = super.delete(entityDTO, authentication, req,res);
                TeamsDTO teamsDTO = (TeamsDTO) genericDataDTO.getData();
                genericDataDTO.setResponseMessage("Successfully Deleted");
                if (teamsDTO != null) {
                    //send message
                    TeamsMessage teamsMessage = new TeamsMessage(teamsDTO);
                    teamsMessage.setIsDeleted(true);
                    //this.messageSender.send(teamsMessage, RabbitMqConstants.QUEUE_APIGW_SEND_TEAMS, RabbitMqConstants.QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS);
                    //data share for all microservices
                    Teams teams = teamsMapper.dtoToDomain(teamsDTO, new CycleAvoidingMappingContext());
                    teams.setIsDeleted(true);
//                createDataSharedService.deleteEntityDataForAllMicroService(teams);
                    teamsService.sharedTeamData(teams, CommonConstants.OPERATION_DELETE);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS,
//                    AclConstants.OPERATION_TEAMS_DELETE, req.getRemoteAddr(), null, teamsDTO.getId(), entityDTO.getName());
                    LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                }
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.TEAMS_DELETE_EXIST);
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName() +LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ce.getMessage() +LogConstants.LOG_STATUS_CODE + APIConstants.ERROR_MESSAGE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader( "requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE + "Team"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +  LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE + APIConstants.ERROR_MESSAGE);

        }finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }
        return genericDataDTO;

    }

    @GetMapping("/checkTeamIsAlreadyParentTeam/{parentTeamId}")
    public GenericDataDTO checkTeamIsAlreadyParentTeam(@PathVariable Long parentTeamId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setData(teamsService.checkTeamIsAlreadyParentTeam(parentTeamId));
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TEAMS, AclConstants.OPERATION_TEAMS_VIEW, req.getRemoteAddr(), null, parentTeamId, "");
        return genericDataDTO;
    }
    @GetMapping("/getAllTeamsWithoutPagination")
    public ResponseEntity<GenericDataDTO> getTeamsList() {
        GenericDataDTO response = new GenericDataDTO();
        try {
            List<TeamsMinimalDTO> teams = teamsService.getAllTeamsMinimal();
            teams.sort(
                    Comparator.comparing(
                            TeamsMinimalDTO::getName,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    )
            );
            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Success");
            response.setDataList(teams);
            response.setTotalRecords(teams.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Failed to load data");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEAMS_ALL + "\",\"" + AclConstants.OPERATION_TEAMS_VIEW + "\")")
//    @GetMapping("/getStaffUsersFromTeamId/{teamId}")
//    public GenericDataDTO getStaffUsersFromTeamId(@PathVariable Long teamId, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = teamsService.getStaffUsersFromTeamId(teamId);
//        return genericDataDTO;
//    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return teamsService.search( paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(),
                    paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),paginationRequestDTO.getSortOrder());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }



    @GetMapping("/getAllTeamBasedOnAttchedStaff")
    public GenericDataDTO getAllTeamBasedOnAttchedStaff(HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = teamsService.getAllTeamBasedOnAttchedStaff();
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<TeamsDTO> list=new ArrayList<>();
        long startTime = System.nanoTime();  // Start measuring
        try {
//            if(getLoggedInUser().getLco())
//                list = teamsService.getAllEntities().stream().filter(x->x.getLcoId()!=null && x.getLcoId().intValue()==getLoggedInUser().getPartnerId()).collect(Collectors.toList());
//            else
//                list = teamsService.getAllEntities().stream().filter(x->x.getLcoId()==null).collect(Collectors.toList());
            list = teamsService.getAllTeams();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
      //      logger.info("Fetching ALL DATA without pagination request: { Module{}}; Response{Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
    //        logger.error("Unable to load data  request: { module{}}; Response{Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
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
    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\", \"" + MenuConstants.IwfTeams.TEAMS + "\")")
    @PostMapping(value = "permissions")
    public GenericDataDTO getAllTeamByProduct( HttpServletRequest req,
                                               @RequestBody PaginationRequestDTO requestDTO,
                                               @RequestParam(name = "productType") String productType) {
        String SUBMODULE = getModuleNameForLog() + " [getAllTeamByProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getFirstName());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());

        try {
             teamsService = SpringContext.getBean(TeamsService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = teamsService.getPagination(
                    requestDTO.getPage(),
                    requestDTO.getPageSize(),
                    requestDTO.getSortBy(),
                    requestDTO.getSortOrder(),
                    requestDTO.getFilters(),
                    productType);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Teams " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ce) {
            ce.printStackTrace();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Teams " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE +ce.getMessage()+LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);

        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\", \"" + MenuConstants.IwfTeams.TEAMS + "\")")
    @PostMapping(value = "/searchByProduct")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "productType") String productType) {
        return teamsService.searchByProduct( paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),paginationRequestDTO.getSortOrder(),productType);
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.teams.TEAMS + "\", \"" + MenuConstants.IwfTeams.TEAMS + "\")")
    @GetMapping()
    public GenericDataDTO getAllTeamsWithoutPagination(@RequestParam String productType) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getFirstName());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<TeamsDTO> list=new ArrayList<>();
        try {
            list = teamsService.getAllTeamByProduct(productType);
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }


}
