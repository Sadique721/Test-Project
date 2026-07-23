package com.savbill.taskmanagement.core.modules.tasks.service;


import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.constants.ClientServiceConstant;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.TimeUnitWithTotal;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;

import com.savbill.taskmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.service.ResolutionReasonsService;
import com.savbill.taskmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Model.TicketFollowUpDTO;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Service.TicketFollowUpService;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.service.TicketFollowupDetailService;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
//import com.savbill.ticketmanagement.core.modules.common.LoggedInUser;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.dto.StaffUserPojo;
import com.savbill.taskmanagement.core.modules.staffuser.mapper.StaffUserMapper;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseUpdateDetailsMapper;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseUpdateMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.CommonConstants;
import com.savbill.taskmanagement.core.modules.utils.TatUtils;
import com.savbill.taskmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseUpdateService extends ExBaseAbstractService<CaseUpdateDTO, CaseUpdate, Long> {

    public CaseUpdateService(CaseUpdateRepository repository, CaseUpdateMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [CaseUpdateService()] ";
    }

    @Autowired
    private CaseService caseService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private CaseAssignmentService assignmentService;
    @Autowired
    private CustomersService customersService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    //    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CaseUpdateRepository caseUpdateRepository;
    @Autowired
    private CaseUpdateDetailsService caseUpdateDetailsService;
    @Autowired
    private CaseUpdateDetailsMapper mapper;
    @Autowired
    CaseUpdateDetailsMapper caseUpdateDetailsMapper;
    @Autowired
    CaseUpdateMapper caseUpdateMapper;
    @Autowired
    CustomerRepository customerRepository;
    private String PATH;

    //Whatsapp no configuration
    private String WHATSAPPNO1;
    private String WHATSAPPNO2;
    private String CLIENTCONTACTNO;
    @Autowired
    private FileUtility fileUtility;

//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;
//    @Autowired
//    TicketReasonSubCategoryService ticketReasonSubCategoryService;

    @Autowired
    CaseCategoryService caseCategoryService;

    @Autowired
    ResolutionReasonsService resolutionReasonsService;

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    CaseMapper caseMapper;

    @Autowired
    GroupReasonMappingRepository groupReasonMappingRepository;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    CaseUpdateDetailsRepository caseUpdateDetailsRepository;

    @Autowired
    private CaseDocDetailsService caseDocDetailsService;

    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    TicketFollowUpService ticketFollowUpService;
    @Autowired
    TicketFollowupDetailService ticketFollowupDetailService;

    @Autowired
    RootCauseReasonRepository rootCauseReasonRepository;

    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    TicketTatMatrixRepository ticketTatMatrixRepository;
    @Autowired
    private Tracer tracer;
    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    TeamsService teamsService;
    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    TicketAssignStaffMappingRepo ticketAssignStaffMappingRepo;
    @Autowired
    ExternalTicketLinkService externalTicketLinkService;

    @Autowired
    CustomerTaskFileMappingRepo customerTaskFileMappingRepo;
    @Autowired
    ResoultionFileMappingRepocitory resoultionFileMappingRepocitory;


    public CaseUpdate get(Long id) {
        CaseUpdate caseUpdate = super.getId(id);
        if (getBUIdsFromCurrentStaff() != null && getMvnoIdFromCurrentStaff() != null) {
            if (getMvnoIdFromCurrentStaff() == 1 || (caseUpdate.getTicket().getMvnoId() == getMvnoIdFromCurrentStaff().intValue() ||
                    caseUpdate.getTicket().getMvnoId() == 1) && (caseUpdate.getTicket().getMvnoId() == 1 ||
                    getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(caseUpdate.getTicket().getBuId())))
                return caseUpdate;
        } else {
            return caseUpdate;
        }
        return null;
    }


    @Transactional
    public CaseDTO updateEntity(CaseUpdateDTO entity, List<MultipartFile> file, Boolean isPickedUp, TaskFileUploadDTO taskFileUploadDTO) {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
        try {
            CaseDTO dbObj = caseService.getEntityForUpdateAndDelete(entity.getTicketId());
            String remark = "";
            StaffUser assigneeStaff = null;
            StaffUser cretedByStaff = null;
            Customers taskLinkedCustomer = null;
            Teams newAssigneeTeam =null;
            if (dbObj.getFirstRemark() != null) {
                remark = trimToMaxLength(dbObj.getFirstRemark(), 4000);
            }
            dbObj.setFirstRemark(remark);
            String prePriority = dbObj.getPriority();
            Map<String,Object> notificationMap=new HashMap<>();
            //Customers customers = customersService.get(dbObj.getCustomersId());
            //StaffUser staffUsers = staffUserService.get(dbObj.getStaffId());
            String Email = null;
            StaffUser staffUser = null;
//            if (staffUsers != null) {
//                Email = staffUsers.getEmail();
//            }

            if(dbObj.getCurrentAssigneeId()!=null) {
                assigneeStaff = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).orElse(null);
            }
            if(dbObj.getCreatedById()!=null){
                cretedByStaff = staffUserRepository.findById(dbObj.getCreatedById()).orElse(null);
            }
            if(dbObj.getCustomersId()!=null){
                taskLinkedCustomer = customerRepository.findById(dbObj.getCustomersId()).orElse(null);
            }


            if (null != dbObj) {
                Map<String, Map<String, String>> mainMap = new HashMap<>();

                //Status
                if (null != dbObj.getCaseStatus() && null != entity.getStatus() && !dbObj.getCaseStatus().equalsIgnoreCase(entity.getStatus())) {
                    Map<String, String> valueMap = new HashMap<>();
                    valueMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseStatus());
                    // Open -> In-Progress is allowed only
                    if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_OPEN) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())) {
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_IN_PROGRESS);
                                break;
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Discarded' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Cancelled' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_DONE:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Done' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'On-Hold' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Rejected' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Under-FollowUp' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Resolved' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Re-Open' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Open'.", null);
                            default:
                                break;
                        }

                    }
                    //Rejected -> ReOpen
                    //Rejected -> Re-Open,Cancelled
                    else if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_REJECTED) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())){
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_RE_OPEN);
                                break;
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                Optional<StaffUser> staff =staffUserRepository.findById(dbObj.getFinalResolvedById() != null ? dbObj.getFinalResolvedById() : dbObj.getFinalClosedById());
                                if(Objects.nonNull(staff.get().getParentStaffId()) && getLoggedInUserId()==staff.get().getParentStaffId()){
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    break;
                                }else if(Objects.isNull(staff.get().getParentStaffId())){
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    break;
                            }else{
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parant can change status 'Rejected' to 'Cancel'.", null);
                            }
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'In-Progress' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'On-Hold' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_DONE:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'Done' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Under-FollowUp'.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'Under-FollowUp' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'Resolved' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Rejected' to 'Discarded' is not allowed.", null);
                            default:
                                break;
                        }
                    }
                    // In-Progress -> Reasolved,On-Hold,Cancelled,Rejectedare allowed only
                    else if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_IN_PROGRESS) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())) {
                        Optional<StaffUser> staff =staffUserRepository.findById(dbObj.getCurrentAssigneeId());
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                if( Objects.nonNull( staff.get().getParentStaffId()) && staff.get().getParentStaffId().equals(getLoggedInUserId())) {
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                }else if(Objects.isNull( staff.get().getParentStaffId())){
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                } else{
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent can Change Status from .'In Progress to Cancel'", null);
                                }
                                break;
                            case CaseConstants.TASK_STATUS_DONE:
                                if( Objects.nonNull( staff.get().getParentStaffId()) && staff.get().getParentStaffId().equals(getLoggedInUserId())){
                                    bulkOperationPerform(entity);
                                    sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                    externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                    tatUtils.changeTicketTatStatus(dbObj, false);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                                    dbObj.setNextFollowupDate(LocalDate.now());
                                    dbObj.setNextFollowupTime(LocalTime.now());
                                    dbObj.setCurrentAssigneeId(null);
                                    dbObj = caseService.approveTaskByFinalStatus(dbObj,entity.getRemark());


                                }else if(Objects.isNull( staff.get().getParentStaffId())){
                                    bulkOperationPerform(entity);
                                    externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                    sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                    tatUtils.changeTicketTatStatus(dbObj, false);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    dbObj.setNextFollowupDate(LocalDate.now());
                                    dbObj.setNextFollowupTime(LocalTime.now());
                                    dbObj.setCurrentAssigneeId(null);
                                    dbObj = caseService.approveTaskByFinalStatus(dbObj,entity.getRemark());
                                }else{
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent can Change Status from .'In Progress to Done'", null);
                                }
                                break;
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                setOnHoldStartTime(entity);
                                dbObj.setFinalClosedById(getLoggedInUserId());
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_ON_HOLD);
                                break;
                            case CaseConstants.TASK_STATUS_REJECTED:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_REJECTED);
                                dbObj.setFinalClosedById(getLoggedInUserId());
                                break;
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_UNDER_FOLLOWUP);
                                break;
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_RESOLVED);
                                dbObj.setFinalResolvedById(getLoggedInUserId());
                                break;
                            case CaseConstants.TASK_STATUS_REOPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'In-Progress' to 'Re-Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'In-Progress' to 'Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'In-Progress'.", null);
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'In-Progress' to 'Discarded' is not allowed. Please update the status to 'Done' before proceeding to 'Closed'.", null);
                            default:
                                break;
                        }
                    }
                    //Resolved - > Done, ReOpen,allowed only
                    else if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_RESOLVED) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())) {
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_DONE:
                                bulkOperationPerform(entity);
                                Optional<StaffUser> loggedinUser=staffUserRepository.findById(dbObj.getFinalResolvedById() != null ? dbObj.getFinalResolvedById() : dbObj.getFinalClosedById());
                                boolean isParent = loggedinUser
                                        .map(user -> Objects.equals(getLoggedInUser().getUserId(), user.getParentStaffId()))
                                        .orElse(false);
                                if(Objects.nonNull(loggedinUser.get())){
                                    if(isParent) {
                                        tatUtils.changeTicketTatStatus(dbObj, false);
                                        dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                                        dbObj.setNextFollowupDate(LocalDate.now());
                                        dbObj.setNextFollowupTime(LocalTime.now());
                                        dbObj.setFinalClosedById(null);
                                        dbObj.setCurrentAssigneeId(null);
                                        dbObj = caseService.approveTaskByFinalStatus(dbObj, entity.getRemark());
                                        externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                        sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                        break;
                                    }else if(Objects.isNull(loggedinUser.get().getParentStaffId())){
                                        tatUtils.changeTicketTatStatus(dbObj, false);
                                        dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                                        dbObj.setNextFollowupDate(LocalDate.now());
                                        dbObj.setNextFollowupTime(LocalTime.now());
                                        dbObj.setFinalClosedById(null);
                                        dbObj.setCurrentAssigneeId(null);
                                        dbObj = caseService.approveTaskByFinalStatus(dbObj, entity.getRemark());
                                        externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                        sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                        break;
                                    }

                                    else{
                                        log.error("Cannot change status from: {} to Done ,Only Parent Staff can Change status to Done for Case : {}; Module : {};",dbObj.getCaseStatus(),dbObj.getCaseNumber(),SUBMODULE);
                                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent Staff can Change status to Done", null);
                                    }
                                }
                                break;
                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_RE_OPEN);
                                break;
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Cancelled' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Rejected' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'In-Progress' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Discarded' is not allowed. Please update the status to 'Done' before proceeding to 'Closed'.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'On-Hold' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Under-FollowUp' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Resolved'.", null);
                            default:
                                break;
                        }

                    } else if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_DISCARDED)) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Discarded' to any other status is not allowed ", null);
                    }
                    //Cancelled -> Discard
                    else if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_CANCELLED) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())){
                        Optional<StaffUser> loggedinUser=staffUserRepository.findById(dbObj.getFinalResolvedById() != null ? dbObj.getFinalResolvedById() : dbObj.getFinalClosedById());
                        Optional<StaffUser> currentStaff=staffUserRepository.findById(dbObj.getCurrentAssigneeId());
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                if( Objects.nonNull(loggedinUser.get().getParentStaffId()) && loggedinUser.get().getParentStaffId().equals(getLoggedInUserId())){
                                    tatUtils.changeTicketTatStatus(dbObj, false);
                                    externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                    sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DISCARDED);
                                    dbObj.setNextFollowupDate(LocalDate.now());
                                    dbObj.setNextFollowupTime(LocalTime.now());
                                    dbObj.setCurrentAssigneeId(null);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    dbObj = caseService.approveTaskByFinalStatus(dbObj,entity.getRemark());
                                    break;
                                } else if(Objects.nonNull(currentStaff.get().getParentStaffId()) && currentStaff.get().getParentStaffId().equals(getLoggedInUserId())){
                                    tatUtils.changeTicketTatStatus(dbObj, false);
                                    externalTicketLinkService.externalTicketClose(dbObj.getCaseId().intValue());
                                    sentNotificaitonOnTaskClose(entity,dbObj,assigneeStaff,cretedByStaff,taskLinkedCustomer);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DISCARDED);
                                    dbObj.setNextFollowupDate(LocalDate.now());
                                    dbObj.setNextFollowupTime(LocalTime.now());
                                    dbObj.setCurrentAssigneeId(null);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    dbObj = caseService.approveTaskByFinalStatus(dbObj,entity.getRemark());
                                    break;
                                }else if(Objects.isNull(loggedinUser.get().getParentStaffId())){
                                    tatUtils.changeTicketTatStatus(dbObj, false);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DISCARDED);
                                    dbObj.setNextFollowupDate(LocalDate.now());
                                    dbObj.setNextFollowupTime(LocalTime.now());
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    dbObj.setCurrentAssigneeId(null);
                                    dbObj = caseService.approveTaskByFinalStatus(dbObj,entity.getRemark());
                                    break;
                                }else{
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent can change status from 'Cancelled' to 'Discard' is not allowed.", null);
                                }

                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Re-Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'In-Progress' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'On-Hold' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_DONE:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Done' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Under-FollowUp'.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Rejected' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Under-FollowUp' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Cancelled' to 'Resolved' is not allowed.", null);
                            default:
                                break;
                        }
                    }
                    //On-Hold-> In-Progress,Cancelled allowed only
                    else if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_ON_HOLD) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())){
                        Optional<StaffUser> loggedinUser=staffUserRepository.findById(dbObj.getFinalResolvedById() != null ? dbObj.getFinalResolvedById() : dbObj.getFinalClosedById());
                       Optional<StaffUser>currentStaff=staffUserRepository.findById(dbObj.getCurrentAssigneeId());
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                setOnHoldEndTime(entity);
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_IN_PROGRESS);
                                break;
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                if(Objects.nonNull(loggedinUser.get().getParentStaffId()) && loggedinUser.get().getParentStaffId().equals(getLoggedInUserId())){
                                    setOnHoldEndTime(entity);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    break;
                                }else if(Objects.nonNull(currentStaff.get().getParentStaffId()) && currentStaff.get().getParentStaffId().equals(getLoggedInUserId())){
                                    setOnHoldEndTime(entity);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    break;
                                }
                                else if(Objects.isNull(loggedinUser.get().getParentStaffId())){
                                    setOnHoldEndTime(entity);
                                    dbObj.setCaseStatus(CaseConstants.TASK_STATUS_CANCELLED);
                                    dbObj.setFinalClosedById(getLoggedInUserId());
                                    break;
                                }else{
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent can change status from 'Cancelled' to 'Discard' is not allowed.", null);
                                }
                            case CaseConstants.TASK_STATUS_DONE:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Done' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Re-Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Open' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'On-Hold'.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Rejected' is not allowed.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Under-FollowUp' is not allowed. Please update the status to 'Open' before proceeding to 'Under-FollowUp'.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'On-Hold' to 'Resolved' is not allowed. Please update the status to 'Open' before proceeding to 'Under-FollowUp'.", null);
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Resolved' to 'Discarded' is not allowed. Please update the status to 'Done' before proceeding to 'Closed'.", null);
                            default:
                                break;
                        }
                    }
                    //Done -> final statment
                    else if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_DONE) && !entity.getStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_DONE)) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Done' to any other status is not allowed ", null);
                    }
//                    //Under-FollowUp -> In-Progress
//                    else if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_UNDER_FOLLOWUP)){
//                        switch (entity.getStatus().toLowerCase()) {
//                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
//                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_IN_PROGRESS);
//                            case CaseConstants.TASK_STATUS_CANCELLED:
//                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_IN_PROGRESS);
//                            case CaseConstants.TASK_STATUS_ON_HOLD:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'On-Hold' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_OPEN:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Open' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_RE_OPEN:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Re-Open' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_DONE:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Done' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_REJECTED:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Rejected' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Under-FollowUp'.", null);
//                            case CaseConstants.TASK_STATUS_RESOLVED:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Resolved' is not allowed.", null);
//                            case CaseConstants.TASK_STATUS_DISCARDED:
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Under-FollowUp' to 'Discarded' is not allowed.", null);
//                            default:
//                                break;
//                        }
//                    }

                    if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_RE_OPEN) && !entity.getStatus().equalsIgnoreCase(dbObj.getCaseStatus())) {
                        switch (entity.getStatus()) {
                            case CaseConstants.TASK_STATUS_IN_PROGRESS:
                                dbObj.setCaseStatus(CaseConstants.TASK_STATUS_IN_PROGRESS);
                                break;
                            case CaseConstants.TASK_STATUS_DISCARDED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Discarded' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_CANCELLED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Cancelled' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_DONE:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Done' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_ON_HOLD:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'On-Hold' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_REJECTED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Rejected' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_UNDER_FOLLOWUP:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Under-FollowUp' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_RESOLVED:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Resolved' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_RE_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Status transition from 'Open' to 'Re-Open' is not allowed. Please update the status to 'In-Progress' before proceeding to 'Re-Open'.", null);
                            case CaseConstants.TASK_STATUS_OPEN:
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The task already has the status 'Open'.", null);
                            default:
                                break;
                        }

                    }


                    //Closed -> Rejected, On-Hold


//                    //Status = Closed
//                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)) {
//                        dbObj.setFinalClosedById(getLoggedInUserId());
//
//                        dbObj.setFinalClosedDate(LocalDateTime.now());
//
////                        CommunicationHelper communicationHelper = new CommunicationHelper();
////                        Map<String, String> map = new HashMap<>();
////                        map.put(CommunicationConstant.USERNAME, dbObj.getUserName());
////                        map.put(CommunicationConstant.COMPLAIN_NO, dbObj.getCaseNumber());
////                        map.put(CommunicationConstant.DESTINATION, dbObj.getMobile());
////                        map.put(CommunicationConstant.EMAIL, Email);
////                        CLIENTCONTACTNO = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CLIENT_CONTACT_NO).get(0).getValue();
////                        map.put(CommunicationConstant.CONTACT_NO, CLIENTCONTACTNO);
////                        WHATSAPPNO1 = clientServiceSrv.getClientSrvByName(ClientServiceConstant.WHATSAPPNO1).get(0).getValue();
////                        WHATSAPPNO2 = clientServiceSrv.getClientSrvByName(ClientServiceConstant.WHATSAPPNO2).get(0).getValue();
////                        map.put(CommunicationConstant.WHATSAPP_NO_1, WHATSAPPNO1);
////                        map.put(CommunicationConstant.WHATSAPP_NO_2, WHATSAPPNO2);
////                        communicationHelper.generateCommunicationDetails(CommunicationConstant.TICKET_CLOSED, Collections.singletonList(map));
//
//                        //bulk operation for linked ticket
//                        bulkOperationPerform(entity);
//                    }
//
//                    //Status = FollowUp
//                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                        if (entity.getNextFollowupDate() != null && entity.getNextFollowupTime() != null) {
//                            Case case1=caseRepository.findById(entity.getTicketId()).get();
//                            CaseUpdateDTO caseUpdateDTO = updateTatAtStatusChangeToFollowUps(entity);
//                            dbObj.setNextFollowupDate(caseUpdateDTO.getNextFollowupDate());
//                            dbObj.setNextFollowupTime(caseUpdateDTO.getNextFollowupTime());
//                        }
//                    }
//                    //Status = Resolved
//                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_RESOLVED)) {
//                        if(entity.getFinalResolutionId()!=null){
//                            dbObj.setFinalResolutionId(entity.getFinalResolutionId().intValue());
//                            dbObj.setFinalResolutionDate(LocalDateTime.now());
//                            dbObj.setFinalResolvedById(getLoggedInUserId());
//                        }
//
//
////                        CommunicationHelper communicationHelper1 = new CommunicationHelper();
////                        Map<String, String> map1 = new HashMap<>();
////                        map1.put(CommunicationConstant.USERNAME, dbObj.getUserName());
////                        map1.put(CommunicationConstant.COMPLAIN_NO, dbObj.getCaseNumber());
////                        map1.put(CommunicationConstant.DESTINATION, dbObj.getMobile());
////                        map1.put(CommunicationConstant.EMAIL, Email);
////                        communicationHelper1.generateCommunicationDetails(CommunicationConstant.COMPLAIN_RESOLUTION, Collections.singletonList(map1));
//
//                        // bulk operation perform on resolve status
//                        //bulkOperationPerform(entity);
//
//                    }
//
//                    if (dbObj.getCaseStatus().equals(CaseConstants.STATUS_IN_PROGRESS)){
//                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ticket can only Reopen if it is Resolved.", null);
//                        }
//                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)){
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can't close the ticket as Ticket is In-Progress.", null);
//                        }
//                    }
//
//                    //On hold status update
//                    if ((entity.getStatus().equalsIgnoreCase("On hold") || entity.getStatus().equalsIgnoreCase("pending") || entity.getStatus().equalsIgnoreCase("Out of domain")) &&
//                            !(dbObj.getCaseStatus().equalsIgnoreCase("Pending") || dbObj.getCaseStatus().equalsIgnoreCase("On hold") || dbObj.getCaseStatus().equalsIgnoreCase("Out of Domain")))
//                    {
//                        setOnHoldStartTime(entity);
//                    } else if (!(entity.getStatus().equalsIgnoreCase("Pending") || entity.getStatus().equalsIgnoreCase("On hold") || entity.getStatus().equalsIgnoreCase("Out Of Domain")) &&
//                            (dbObj.getCaseStatus().equalsIgnoreCase("On hold") || dbObj.getCaseStatus().equalsIgnoreCase("pending") || dbObj.getCaseStatus().equalsIgnoreCase("Out of domain"))) {
//
//                        if (dbObj.getCaseSlaTime() != null && dbObj.getCaseSlaUnit() != null) {
//                            entity.setCaseSlaTime(dbObj.getCaseSlaTime());
//                            entity.setCaseSlaUnit(dbObj.getCaseSlaUnit());
//                            entity.setPriority(dbObj.getPriority());
//                        }
//                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)){
//                            removeStartStopTimeWhenFolloupSet(entity);
//                        }else{
//                            CaseUpdateDTO caseUpdateDTOs = setOnHoldEndTime(entity);
//                            dbObj.setNextFollowupDate(caseUpdateDTOs.getNextFollowupDate());
//                            dbObj.setNextFollowupTime(caseUpdateDTOs.getNextFollowupTime());
//                            dbObj.setCaseSlaTime(caseUpdateDTOs.getCaseSlaTime());
//                            dbObj.setCaseSlaUnit(caseUpdateDTOs.getCaseSlaUnit());
//
//                        }
//
//                    }
//                    if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP) && !entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)){
//                        CaseUpdateDTO caseUpdateDTO = restartTATbeforeFollowupEndAndStatusChage(entity);
//                        dbObj.setNextFollowupDate(entity.getNextFollowupDate());
//                        dbObj.setNextFollowupTime(entity.getNextFollowupTime());
//                    }
//
//                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
//                        if(!dbObj.getCaseStatus().equals(CaseConstants.STATUS_RESOLVED)){
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ticket can only Reopen if it is Resolved.", null);
//                        }
//                    }
//                    if(entity.getStatus().equalsIgnoreCase("Resolved")){
//                        dbObj.setFinalResolvedById(getLoggedInUserId());
//                    }
//                    valueMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseStatus());
                    valueMap.put(CaseConstants.NEW_VALUE, entity.getStatus());
                    mainMap.put(CaseConstants.STATUS, valueMap);

                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " change status, change From : " + valueMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + valueMap.get(CaseConstants.NEW_VALUE) + " ,SUB-Module : " + SUBMODULE);
//                    if(dbObj.getCaseStatus().equalsIgnoreCase("pending") && !entity.getStatus().equalsIgnoreCase("pending")||dbObj.getCaseStatus().equalsIgnoreCase("On Hold") && !entity.getStatus().equalsIgnoreCase("On Hold")){
//                        restartTat(dbObj,entity);
//                    }
                    dbObj.setCaseStatus(entity.getStatus());
                    if (entity.getIs_closed() != null) {
                        if (entity.getIs_closed() == true) {
                            dbObj.setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                            log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " change status, change From : " + valueMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + dbObj.getCaseStatus() + " ,SUB-Module : " + SUBMODULE);
                        }
                    }

                    /* called method for closed ticket notification*/
//                    if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)){
//                        Long buId = null;
//                        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                            buId =  getBUIdsFromCurrentStaff().get(0);
//                        }
//                        sendCustTicketCloseMessage(dbObj.getUserName(), dbObj.getMobile(), dbObj.getEmail(), dbObj.getCaseStatus(), dbObj.getMvnoId(), dbObj.getCaseNumber(),buId, dbObj.getCaseTitle());
//                    }

                }

                if (Objects.nonNull(entity.getCaseSlaUnit()) && !entity.getCaseSlaUnit().toString().isEmpty() && entity.getCaseSlaUnit() != null && entity.getCaseSlaTime() != null && entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN)) {
                    log.debug("setting Case SLA time and SLA unit for Case : {}, Module : {}", dbObj.getCaseId(), SUBMODULE);
                    dbObj.setCaseSlaTime(entity.getCaseSlaTime());
                    dbObj.setCaseSlaUnit(entity.getCaseSlaUnit());
                }

                //Assignee
                if (null != entity.getAssignee() && !entity.getAssignee().equals(dbObj.getCurrentAssigneeId())) {
                    Map<String, String> valueMap = new HashMap<>();

                    StaffUser newAssignee = staffUserRepository.findById(entity.getAssignee()).orElse(null);
                    if (null != dbObj.getCurrentAssigneeId()) {
                        StaffUser oldAssignee = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).orElse(null);
                        valueMap.put(CaseConstants.OLD_VALUE, oldAssignee.getFirstname() + " " + oldAssignee.getLastname());
                    } else {
                        valueMap.put(CaseConstants.OLD_VALUE, "-");
                    }
                    if(entity.getTeamId()!=null){
                         newAssigneeTeam= teamsRepository.findById(entity.getTeamId().longValue()).orElse(null);
                        if (!newAssignee.getTeam().isEmpty()) {
                            newAssigneeTeam = newAssignee.getTeam().iterator().next();

                        }
                    }
                    if (newAssignee != null) {
                        dbObj.setIs_processed(false);
                        tatUtils.changeTicketTatStatus(dbObj, false);
                        valueMap.put(CaseConstants.NEW_VALUE, newAssignee.getFirstname() + " " + newAssignee.getLastname());
                        dbObj.setCurrentAssigneeId(newAssignee.getId());
                        dbObj.setCurrentAssigneeName(newAssignee.getFullName());
//                        tatUtils.changeTicketTatAssignee(entity, newAssignee, false, false);
                        assignmentService.saveEntity(new CaseAssignmentDTO(entity.getTicketId(), newAssignee.getId(), LocalDate.now()));
                        log.info("saved new assignee for case : {}; New-Assignee : {}; Module : {};",dbObj.getCaseNumber(),dbObj.getCurrentAssigneeName(), SUBMODULE);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with Task Number : " + " ' " + dbObj.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + newAssignee.getUsername() + " '";
                        if(Objects.nonNull(newAssignee)){
                            hierarchyService.sendWorkflowAssignActionMessage(newAssignee.getCountryCode(),newAssignee.getPhone(),newAssignee.getEmail(),newAssignee.getMvnoId(),newAssignee.getFullName(),action,entity.getBuId());
                        }
//                        if(dbObj.getTeamHierarchyMappingId() != null) {
                            if(!isPickedUp)
                                tatUtils.changeTicketTatAssignee(dbObj, newAssignee, false, false);
                            else
                                tatUtils.changeTicketTatAssignee(dbObj, newAssignee, true, true);
//                        }
                    }
                    if(newAssignee!=null){
                        if(newAssigneeTeam!=null){
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(dbObj.getCaseId()), dbObj.getCaseNumber(), newAssignee.getId(), newAssignee.getFullName(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Member :- " + newAssignee.getFullName() + " of the Team:- " + newAssigneeTeam.getName());
                        }else{
                            newAssigneeTeam = teamsRepository.findById(dbObj.getTeamId().longValue()).get();
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(dbObj.getCaseId()), dbObj.getCaseNumber(), newAssignee.getId(), newAssignee.getFullName(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Member :- " + newAssignee.getFullName() + " of the Team:- " + newAssigneeTeam.getName());
                        }
                        log.info("saving work flow audit service for Case : {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                    }
                    if(entity.getTeamId()!=null && !dbObj.getTeamId().equals(entity.getTeamId())){
                        dbObj.setTeamId(entity.getTeamId());
                    }
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " " + CaseConstants.ASSIGNEE + " " + valueMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + valueMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                    mainMap.put(CaseConstants.ASSIGNEE, valueMap);

                }
                if (entity.getTeamId() != null && entity.getAssignee() == null) {
                    log.debug("Processing case assigning to all staff for Team ID: {}; Module : {};", entity.getTeamId(),SUBMODULE);
                    Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
                    List<Integer> staffIdList = teamsService.getStaffIdListFromTeams(entity.getTeamId());
                    List<StaffUser> staffUserList = staffUserRepository.findAllById(staffIdList);
                    Teams teams = teamsRepository.getOne(entity.getTeamId().longValue());
                    dbObj.setCurrentAssigneeId(null);

                    if (staffUserList != null) {
                        for (int i = 0; i < staffIdList.size(); i++) {
                            TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
                            ticketAssignStaffMapping.setStaffId(staffUserList.get(i).getId());
                            ticketAssignStaffMapping.setTicketId(entity.getTicketId());
                            ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
                            //Add Audit here
                            if (staffUserList.get(i).getParentStaffId() != null) {
                                staffByParentStaffId.put(staffUserList.get(i).getParentStaffId(), staffUserMapper.domainToDTO(staffUserList.get(i), new CycleAvoidingMappingContext()));
                            }
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with Task Number : " + " ' " + dbObj.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffUserList.get(i).getUsername() + " '";
                            if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
                                for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
                                    StaffUser staffUsers = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
                                    tatUtils.changeTicketTatAssignee(dbObj, staffUsers, true, false);
                                }
                            }

                            if ( Objects.nonNull(entity.getStatus()) && !entity.getStatus().equalsIgnoreCase("Raise and Close")) {
                                hierarchyService.sendWorkflowAssignActionMessage(staffUserList.get(i).getCountryCode(), staffUserList.get(i).getPhone(), staffUserList.get(i).getEmail(), entity.getMvnoId(), staffUserList.get(i).getFullName(), action, entity.getBuId());
                            }
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(dbObj.getCaseId()), dbObj.getCaseNumber(), staffUserList.get(i).getId(), staffUserList.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Member :- "+staffUserList.get(i).getUsername()+"of the Team:- " + teams.getName());

                        }
                        log.info("work flow audit saved while processing assigning to all staff for Case : {}; TeamID: {}: Module : {};", dbObj.getCaseNumber(),entity.getTeamId(),SUBMODULE);
                    }
                }else if(entity.getTeamId() != null && entity.getAssignee() != null &&  !dbObj.getCurrentAssigneeId().equals(entity.getAssignee())){
                    log.debug("Processing team assigning with specific staff assignee for Team ID: {} and StaffUser: {}; Module : {};", entity.getTeamId(), entity.getAssignee(), SUBMODULE);
                    StaffUser staffById = staffUserRepository.findById(entity.getAssignee()).orElse(null);
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with Task Number : " + " ' " + dbObj.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffById.getUsername() + " '";
                    if(Objects.nonNull(staffById)){
                        hierarchyService.sendWorkflowAssignActionMessage(staffById.getCountryCode(),staffById.getPhone(),staffById.getEmail(),staffById.getMvnoId(),staffById.getFullName(),action,entity.getBuId());
                    }
                }
                //Priority
                if (null != dbObj.getPriority() && null != entity.getPriority() && !entity.getPriority().isEmpty() && !dbObj.getPriority().equalsIgnoreCase(entity.getPriority()) && dbObj.getIsFromCalender()==false) {
                    Map<String, String> valueMap = new HashMap<>();
                    valueMap.put(CaseConstants.OLD_VALUE, dbObj.getPriority());
                    valueMap.put(CaseConstants.NEW_VALUE, entity.getPriority());
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " change Priority, change From : " + valueMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + valueMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                    mainMap.put(CaseConstants.PRIORITY, valueMap);
                    //notificationMap.put(CaseConstants.PRIORITY,dbObj.getPriority());
                    // TicketTatMatrix ticketTatMatrix=ticketTatMatrixRepository.findById(dbObj.getTatMappingId()).orElse(null);
                    TicketTatMatrix tatMatrix = caseService.getTicketTatMatrixFromSubReasonIdForUpdate(entity);
                    dbObj.setPriority(entity.getPriority());
                    if (dbObj.getPriority().equalsIgnoreCase("High")) {
                        log.debug("Setting high priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUBMODULE, dbObj.getCaseId(), tatMatrix.getSunitp1(), tatMatrix.getSlaTimep1());
                        dbObj.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep1()));
                        dbObj.setCaseSlaUnit(tatMatrix.getSunitp1());
                    } else if (dbObj.getPriority().equalsIgnoreCase("Medium")) {
                        log.debug("Setting medium priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUBMODULE, dbObj.getCaseId(), tatMatrix.getSunitp2(), tatMatrix.getSlaTimep2());
                        dbObj.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep2()));
                        dbObj.setCaseSlaUnit(tatMatrix.getSunitp2());
                    } else {
                        log.debug("Setting low priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUBMODULE, dbObj.getCaseId(), tatMatrix.getSunitp2(), tatMatrix.getSlaTimep2());
                        dbObj.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTime3()));
                        dbObj.setCaseSlaUnit(tatMatrix.getSunitp3());
                    }
                }else if (null != dbObj.getPriority() && null != entity.getPriority() && !entity.getPriority().isEmpty() && !dbObj.getPriority().equalsIgnoreCase(entity.getPriority()) && dbObj.getIsFromCalender()==true) {
                    if(assigneeStaff.getParentStaffId() == caseService.getLoggedInUserId()){
                        log.debug("setting priority for Case : {}; Old priority : {}; New priority : {}; Module : {};",dbObj.getCaseNumber(),dbObj.getPriority(), entity.getPriority(),SUBMODULE);
                        dbObj.setPriority(entity.getPriority());
                    }else{
                        log.error("You are not authorize to change the priority, Only ParentStaff can able to perform this operation or change the priority; Module : {};",SUBMODULE);
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "You are not authorize to change the priority, Only ParentStaff can able to perform this operation",null);
                    }
                }

                //Source
                if (null != entity.getSource() && !entity.getSource().isEmpty()) {
                    log.debug("Setting source for Case : {}; New source : {}; Module : {};", dbObj.getCaseNumber(), entity.getSource(), SUBMODULE);
                    dbObj.setSource(entity.getSource());
                }

                //Sub-Source
                if (null != entity.getSubSource() && !entity.getSubSource().isEmpty()) {
                    log.debug("Setting sub source for Case : {}; New Sub-Source  {}; Module : {};",dbObj.getCaseNumber(),entity.getSubSource(),SUBMODULE);
                    dbObj.setSubSource(entity.getSubSource());
                }

                //Remark
                if (null != entity.getRemark() && null != entity.getRemarkType() && !entity.getRemark().isEmpty()) {
                    Map<String, String> remarkMap = new HashMap<>();
                    remarkMap.put(CaseConstants.NEW_VALUE, entity.getRemark());
                    remarkMap.put(CaseConstants.REMARK_TYPE, entity.getRemarkType());
                    remarkMap.put(CaseConstants.UNIQUE_FILENAME, entity.getAttachment());
                    remarkMap.put(CaseConstants.FILENAME, entity.getFilename());

                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " change remark, change From : " + getLatestNewRemark(entity.getTicketId()) + " ,TO: " + remarkMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                    mainMap.put(CaseConstants.REMARK, remarkMap);
                }
                if( Objects.nonNull(entity.getRemarkType()) && entity.getRemarkType().equalsIgnoreCase("Internal remark")){
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " Internal remark  " +  "Sub-Module : " + SUBMODULE);
                    Optional<StaffUser> staffUser1=staffUserRepository.findById(entity.getAssignee());
                    if(Objects.nonNull(staffUser1.get().getParentStaffId())) {
                        Optional<StaffUser> parentstaff = staffUserRepository.findById(staffUser1.get().getParentStaffId());
                        if (entity.getBuId() != null) {
                            log.debug("sending Case follow up remark message; StaffUser:{}; Case : {}; Remark : {}; Module : {};", parentstaff.get().getFirstname(), dbObj.getCaseNumber(), entity.getRemark(),SUBMODULE);
                            ticketFollowupDetailService.sendFollowUpRemarkMsg(parentstaff.get().getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffUser1.get().getFirstname(), parentstaff.get().getPhone(), parentstaff.get().getEmail(), getMvnoIdFromCurrentStaff(), staffUser1.get().getFirstname(), entity.getBuId());
                        } else {
                            log.debug("sending Case follow up remark message; StaffUser:{}; Case : {}; Remark : {}; Module : {};", parentstaff.get().getFirstname(), dbObj.getCaseNumber(), entity.getRemark(),SUBMODULE);
                            ticketFollowupDetailService.sendFollowUpRemarkMsg(parentstaff.get().getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffUser1.get().getFirstname(), parentstaff.get().getPhone(), parentstaff.get().getEmail(), getMvnoIdFromCurrentStaff(), staffUser1.get().getFirstname(), null);
                        }
                    }
                }

                if (null != entity && file != null && file.size() > 0) {
                    ApplicationLogger.logger.debug(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + "file documents" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " Sub-Module : "+ SUBMODULE);
                    Case aCase = caseRepository.findById(dbObj.getCaseId()).orElse(null);
                    if (aCase != null) {
                        for (MultipartFile multipartFile : file) {
                            CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
                            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
//                            String subFolderName = aCase.getCaseNumber().trim().replace("-", "_") + "/";
                            String path = PATH;
                            caseDoc.setTicketId(Math.toIntExact(dbObj.getCaseId()));
                            caseDoc.setDocStatus("Active");
                            MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
                            if (file1 != null) {
                                caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
                                caseDoc.setFilename(caseDoc.getUniquename());
                                caseDoc = caseDocDetailsService.saveEntity(caseDoc);
                            }
                        }
                    }


                }

                if(taskFileUploadDTO != null){
                    if(taskFileUploadDTO.getSections() != null){
                        List<CustomerTaskFileMapping> fileMappings = new ArrayList<>();
                        for (SectionUploadDTO sectionUploadRequest : taskFileUploadDTO.getSections()) {
                            if (sectionUploadRequest.getFiles() != null) {
                                String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
                                String subFolderName = File.separator + dbObj.getCaseId() + File.separator+sectionUploadRequest.getName()+ File.separator;
                                String path = PATH + subFolderName;
                                ApplicationLogger.logger.debug("****************:File Path:" + path);
                                for (MultipartFile file2 : sectionUploadRequest.getFiles()) {
                                    if (!file2.isEmpty()) {
                                        if (!isValidFileExtension(file2.getOriginalFilename())) {
                                            throw new CustomValidationException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                                                    "Unsupported file type: " + file2.getOriginalFilename(), null);
                                        }

                                        String uniqueName = fileUtility.saveFileToServerForTicket(file2, path);

                                        CustomerTaskFileMapping fileMapping = new CustomerTaskFileMapping();
                                        fileMapping.setCustomerTaskMapping(dbObj.getCaseId());
                                        fileMapping.setFilename(file2.getOriginalFilename());
                                        fileMapping.setUniquename(uniqueName);
                                        fileMapping.setSection(sectionUploadRequest.getName());
                                        if(sectionUploadRequest.getLatitude() != null && !sectionUploadRequest.getLatitude().isEmpty()){
                                            fileMapping.setLatitiude(sectionUploadRequest.getLatitude());
                                        }
                                        if(sectionUploadRequest.getLongitude() != null && !sectionUploadRequest.getLongitude().isEmpty()){
                                            fileMapping.setLongitude(sectionUploadRequest.getLongitude());
                                        }
                                        if(sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty()){
                                            fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                                        }
                                        fileMappings.add(fileMapping);
                                    }
                                }
                            } else {
                                Optional<CustomerTaskFileMapping> existingMappingOpt = customerTaskFileMappingRepo.findByCustomerTaskMappingAndSection(dbObj.getCaseId(), sectionUploadRequest.getName());
                                CustomerTaskFileMapping fileMapping;
                                if (existingMappingOpt.isPresent()) {
                                    fileMapping = existingMappingOpt.get();
                                    if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty()) {
                                        fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                                    }
                                } else {
                                    fileMapping = new CustomerTaskFileMapping();
                                    fileMapping.setCustomerTaskMapping(dbObj.getCaseId());
                                    fileMapping.setSection(sectionUploadRequest.getName());
                                    if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty()) {
                                        fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                                    }
                                }
                                fileMappings.add(fileMapping);
                            }
                        }
                        customerTaskFileMappingRepo.saveAll(fileMappings);
                    }
                }

                //On reason category change
                if (null != entity.getCaseCategoryId() && !dbObj.getCaseCategoryId().equals(entity.getCaseCategoryId())) {
                    Map<String, String> reasonCategoryMap = new HashMap<>();
                    reasonCategoryMap.put(CaseConstants.NEW_VALUE, caseCategoryService.getEntityById(entity.getCaseCategoryId()).getCategoryName());
                    reasonCategoryMap.put(CaseConstants.OLD_VALUE, caseCategoryService.getEntityById(dbObj.getCaseCategoryId()).getCategoryName());
                    dbObj.setCaseCategoryId(entity.getCaseCategoryId());
                    mainMap.put(CaseConstants.REASON_CATEGORY, reasonCategoryMap);
                    //adding notification when change ticket problem domain
                    StaffUser stffuser = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).get();
                    Long buId = null;
                    if (stffuser != null) {
                        if (Objects.nonNull(stffuser.getBusinessUnit())) {
                            buId = stffuser.getBusinessUnit().getId();
                        }
                    }
                    Case updatedCase = tatUtils.updateticketTatMatrix(dbObj);
                    dbObj.setNextFollowupTime(updatedCase.getNextFollowupTime());
                    dbObj.setNextFollowupDate(updatedCase.getNextFollowupDate());
                    if (dbObj.getBuId() != null) {
                        sendProblemDomainChangeMsg(reasonCategoryMap.get(CaseConstants.NEW_VALUE), dbObj.getCaseNumber(), reasonCategoryMap.get(CaseConstants.OLD_VALUE), stffuser.getFirstname(), stffuser.getPhone(), stffuser.getEmail(), dbObj.getMvnoId(), dbObj.getBuId());
                    } else {
                        sendProblemDomainChangeMsg(reasonCategoryMap.get(CaseConstants.NEW_VALUE), dbObj.getCaseNumber(), reasonCategoryMap.get(CaseConstants.OLD_VALUE), stffuser.getFirstname(), stffuser.getPhone(), stffuser.getEmail(), dbObj.getMvnoId(), null);
                    }
                    notificationMap.put(CaseConstants.ENTITY_REASON_CATEGORY,reasonCategoryMap.get(CaseConstants.NEW_VALUE));

                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + CaseConstants.REASON_CATEGORY + reasonCategoryMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + reasonCategoryMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                }

                //  On reason sub category change
                if (null != entity.getCaseSubCategoryId() && dbObj.getCaseSubCategoryId() != null && !dbObj.getCaseSubCategoryId().equals(entity.getCaseSubCategoryId())) {
                    Map<String, String> reasonCategoryMap = new HashMap<>();
                    reasonCategoryMap.put(CaseConstants.NEW_VALUE, caseCategoryService.getEntityById(entity.getCaseCategoryId()).getCategoryName());
                    reasonCategoryMap.put(CaseConstants.OLD_VALUE, caseCategoryService.getEntityById(dbObj.getCaseCategoryId()).getCategoryName());
                    mainMap.put(CaseConstants.REASON_SUB_CATEGORY, reasonCategoryMap);
                    dbObj.setCaseCategoryId(entity.getCaseCategoryId());
                    dbObj.setCaseSubCategoryId(entity.getCaseSubCategoryId());
                    //update TAT matrix for existing tickets
                    notificationMap.put(CaseConstants.ENTITY_REASON_CATEGORY,reasonCategoryMap.get(CaseConstants.NEW_VALUE));

                    Case updatedCase = tatUtils.updateticketTatMatrix(dbObj);
                    dbObj.setNextFollowupTime(updatedCase.getNextFollowupTime());
                    dbObj.setNextFollowupDate(updatedCase.getNextFollowupDate());

                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + CaseConstants.REASON_SUB_CATEGORY + reasonCategoryMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + reasonCategoryMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                }

//                On reason change
//                if (null != entity.getGroupReasonId() && dbObj.getGroupReasonId() != null && !dbObj.getGroupReasonId().equals(entity.getGroupReasonId())) {
//                    Map<String, String> reasonMap = new HashMap<>();
//                    String newValue = ticketReasonSubCategoryService.getEntityById(entity.getReasonSubCategoryId()).getTicketSubCategoryGroupReasonMappingList().stream().filter(t -> t.getId().equals(entity.getGroupReasonId())).findAny().get().getReason();
//                    Optional<TicketSubCategoryGroupReasonMapping> oldReason = groupReasonMappingRepository.findById(dbObj.getGroupReasonId());
//                    String oldValue = "-";
//                    if (oldReason.isPresent()) {
//                        oldValue = oldReason.get().getReason();
//                    }
//                    reasonMap.put(CaseConstants.NEW_VALUE, newValue);
//                    reasonMap.put(CaseConstants.OLD_VALUE, oldValue);
//                    mainMap.put(CaseConstants.REASON, reasonMap);
//                    dbObj.setGroupReasonId(entity.getGroupReasonId());
//                }else if (null != entity.getGroupReasonId() && dbObj.getGroupReasonId() == null){
//                    Map<String, String> reasonMap = new HashMap<>();
//                    String newValue = ticketReasonSubCategoryService.getEntityById(entity.getReasonSubCategoryId()).getTicketSubCategoryGroupReasonMappingList().stream().filter(t -> t.getId().equals(entity.getGroupReasonId())).findAny().get().getReason();
//                  //  Optional<TicketSubCategoryGroupReasonMapping> oldReason = groupReasonMappingRepository.findById(dbObj.getGroupReasonId());
//                    String oldValue = "-";
////                    if (oldReason.isPresent()) {
////                        oldValue = oldReason.get().getReason();
////                    }
//                    reasonMap.put(CaseConstants.NEW_VALUE, newValue);
//                    reasonMap.put(CaseConstants.OLD_VALUE, oldValue);
//                    mainMap.put(CaseConstants.REASON, reasonMap);
//                    dbObj.setGroupReasonId(entity.getGroupReasonId());
//                }


//                Type change
                if (null != entity.getCaseType() && !entity.getCaseType().isEmpty() && !dbObj.getCaseType().equals(entity.getCaseType())) {
                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put(CaseConstants.NEW_VALUE, entity.getCaseType());
                    typeMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseType());
                    mainMap.put(CaseConstants.CASE_TYPE, typeMap);
                    notificationMap.put(CaseConstants.CASE_TYPE,entity.getCaseType());
                    dbObj.setCaseType(entity.getCaseType());
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + CaseConstants.CASE_TYPE + typeMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + typeMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                }

//                Resolution change
                if (null != entity.getFinalResolutionId()) {
                    Map<String, String> typeMap = new HashMap<>();
                    if (Objects.isNull(dbObj.getFinalResolutionId())) {
                        typeMap.put(CaseConstants.OLD_VALUE, "-");
                        typeMap.put(CaseConstants.NEW_VALUE, resolutionReasonsService.getEntityById(entity.getFinalResolutionId().longValue()).getName());
                        mainMap.put(CaseConstants.RESOLUTION_REASON, typeMap);
                        dbObj.setFinalResolutionId(entity.getFinalResolutionId());
                    } else if (!dbObj.getFinalResolutionId().equals(entity.getFinalResolutionId())) {
                        typeMap.put(CaseConstants.OLD_VALUE, resolutionReasonsService.getEntityById(dbObj.getFinalResolutionId().longValue()).getName());
                        typeMap.put(CaseConstants.NEW_VALUE, resolutionReasonsService.getEntityById(entity.getFinalResolutionId().longValue()).getName());
                        mainMap.put(CaseConstants.RESOLUTION_REASON, typeMap);
                        dbObj.setFinalResolutionId(entity.getFinalResolutionId());
                    }

                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + CaseConstants.RESOLUTION_REASON + typeMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + typeMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                }

//               TAT mapping changed
                if (null != entity.getTatMappingId() && !dbObj.getTatMappingId().equals(entity.getTatMappingId())) {
                    log.debug("tat mapping change for case : {}; OlD TatMapping ID : {}; NEW TatMapping ID : {}; Module : {}; ",dbObj.getCaseNumber(),dbObj.getTatMappingId(),entity.getTatMappingId(),SUBMODULE);
                    dbObj.setTatMappingId(entity.getTatMappingId());
                }

//                Title changes

                if (null != entity.getCaseTitle() && !dbObj.getCaseTitle().equals(entity.getCaseTitle())) {
                    Map<String, String> typeMap = new HashMap<>();
                    typeMap.put(CaseConstants.NEW_VALUE, entity.getCaseTitle());
                    typeMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseTitle());
                    mainMap.put(CaseConstants.CASE_TITLE, typeMap);
                    dbObj.setCaseTitle(entity.getCaseTitle());
//                        dbObj.setTatMappingId(entity.getTatMappingId());
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + " " + CaseConstants.CASE_TITLE + " : " +typeMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + typeMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);
                }

                // Root cause changed

                if (null != entity.getRootCauseReasonId()) {
                    Map<String, String> typeMap = new HashMap<>();
                    if (Objects.isNull(dbObj.getRootCauseReasonId())) {
                        typeMap.put(CaseConstants.OLD_VALUE, "-");
                        typeMap.put(CaseConstants.NEW_VALUE, rootCauseReasonRepository.findById(entity.getRootCauseReasonId()).get().getRootCauseReason());
                        mainMap.put(CaseConstants.CASE_ROOT_CAUSE, typeMap);
                        dbObj.setRootCauseReasonId(entity.getRootCauseReasonId());
                    } else if (!dbObj.getRootCauseReasonId().equals(entity.getRootCauseReasonId())) {
                        typeMap.put(CaseConstants.OLD_VALUE, rootCauseReasonRepository.findById(dbObj.getRootCauseReasonId()).get().getRootCauseReason());
                        typeMap.put(CaseConstants.NEW_VALUE, rootCauseReasonRepository.findById(entity.getRootCauseReasonId()).get().getRootCauseReason());
                        mainMap.put(CaseConstants.CASE_ROOT_CAUSE, typeMap);
                        dbObj.setRootCauseReasonId(entity.getRootCauseReasonId());
                    }
                    log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + CaseConstants.CASE_ROOT_CAUSE + typeMap.get(CaseConstants.OLD_VALUE) + " ,TO: " + typeMap.get(CaseConstants.NEW_VALUE) + " SUB-Module : " + SUBMODULE);

                }

                if (entity.getSource() != null) {
                    dbObj.setSource(entity.getSource());
                }
                if (entity.getSubSource() != null) {
                    dbObj.setSubSource(entity.getSubSource());
                }
//                if (entity.getStaffAdditionalEmail() != null) {
//                    dbObj.setStaffAdditionalEmail(entity.getStaffAdditionalEmail());
//                }
//                if (entity.getSubSource() != null) {
//                    dbObj.setStaffAdditionalMobileNumber(entity.getStaffAdditionalMobileNumber());
//                }

                // Update Helper Name
                if (entity.getHelperName() != null) {
                    dbObj.setHelperName(entity.getHelperName());
                    log.debug("Update Helper Name for case : {}; New Helper Name: {}; Module : {}; ", dbObj.getCaseNumber(),entity.getHelperName(),SUBMODULE);
                }
                if((entity.getEndDate() != null && !entity.getEndDate().equals(dbObj.getEndDate())) ||
                        (entity.getStartDate() != null && !entity.getStartDate().equals(dbObj.getStartDate()))){
                    log.debug("change in start or end date for case: {}; Module : {};", dbObj.getCaseNumber(),SUBMODULE);
                    if (dbObj.getCustomersId() != null) {
                        try {
                            Optional<Customers> customersOptional = customerRepository.findById(dbObj.getCustomersId());
                            Customers  customers = customersOptional.orElseThrow(() -> new RuntimeException("Customer not found"));
                            String endDateTime = entity.getEndDate() != null
                                    ? entity.getEndDate().toString()
                                    : (entity.getStartDate() != null
                                    ? entity.getStartDate().toLocalDate().atStartOfDay().toString()
                                    : null);
                            if (customers != null) {
                                log.debug("sending reschedule message To customer : {}; Case : {}; Module : {};" + customers.getUsername(),dbObj.getCaseNumber(),SUBMODULE);
                                System.out.println("****************taskUpdateMessage for customer sent successfully *********************"+ customers.getUsername());
                                sendRescheduleMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), dbObj.getCaseNumber(), dbObj.getCaseStatus(), entity.getPriority(), entity.getRemark(), entity.getStartDate().toString(), endDateTime, RabbitMqConstants.TASK_FOR_CUSTOMER);
                            }
                            Optional<StaffUser> staffUser1=staffUserRepository.findById(dbObj.getCreatedById());
                            if(staffUser1.isPresent()){
                                log.debug("sending reschedule message To staffuser : {}; Case : {}; Module : {};" + staffUser1.get().getUsername(),dbObj.getCaseNumber(),SUBMODULE);
                                System.out.println("****************taskUpdateMessage for Staff sent successfully *********************"+ staffUser1.get().getUsername());
                                sendRescheduleMessage(staffUser1.get().getUsername(), staffUser1.get().getPhone(), staffUser1.get().getEmail(), staffUser1.get().getMvnoId(), dbObj.getCaseNumber(), dbObj.getCaseStatus(), entity.getPriority(), entity.getRemark(),entity.getStartDate().toString(), endDateTime,RabbitMqConstants.TASK_FOR_STAFF);
                            }
                            Optional<StaffUser> parentStaff=staffUserRepository.findById(dbObj.getCurrentAssigneeId());
                            if(parentStaff.isPresent()){
                                log.debug("sending reschedule message To parent staff : {}; Case : {}; Module : {};" + parentStaff.get().getUsername(),dbObj.getCaseNumber(),SUBMODULE);
                                System.out.println("****************taskUpdateMessage for Staff sent successfully *********************"+ parentStaff.get().getUsername());
                                sendRescheduleMessage(parentStaff.get().getUsername(), parentStaff.get().getPhone(), parentStaff.get().getEmail(), parentStaff.get().getMvnoId(), dbObj.getCaseNumber(), dbObj.getCaseStatus(), entity.getPriority(), entity.getRemark(), entity.getStartDate().toString(),endDateTime,RabbitMqConstants.TASK_FOR_STAFF);
                            }
                        } catch (Exception exception) {
                            log.error("error while updating date and time for Case : {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                            exception.printStackTrace();
                            throw new CustomValidationException(APIConstants.FAIL, exception.getMessage(), null);
                        }
                    }

                }
                //change startdate
                if(null!=entity.getStartDate() && entity.getStartDate() != dbObj.getStartDate()) {
                    log.debug("changing start date for Case : {}; Old-startDate : {}, New-startDate : {}; Module : {};",dbObj.getCaseNumber(),dbObj.getStartDate(),entity.getStartDate(),SUBMODULE);
                    dbObj.setStartDate(entity.getStartDate());
                }
                //change endDate
                if(null!=entity.getEndDate() && entity.getEndDate() != dbObj.getEndDate()) {
                    log.debug("changing end date for Case : {}; Old-endDate : {}, New-endDate : {}; Module : {};",dbObj.getCaseNumber(),dbObj.getEndDate(),entity.getEndDate(),SUBMODULE);
                    dbObj.setEndDate(entity.getEndDate());
                }

                //send notification
//              All the changes for ticket should me contain in this map and it will generate update details according to that

                if (null != mainMap && mainMap.size() > 0) {
                    List<CaseUpdateDetailsDTO> detailsList = new ArrayList<>();
                    for (Entry<String, Map<String, String>> map : mainMap.entrySet()) {
//                        CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
//                        switch (map.getKey()) {
//                            case CaseConstants.STATUS:
//                                statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
//                                statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
//                                break;
//                            case CaseConstants.REASON:
//                                statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
//                                statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
//                                break;
//                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.STATUS)) {
                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
                            statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
                            statusDetails.setCaseUpdate(entity);
                            notificationMap.put(CaseConstants.ENTITY_STATUS,map.getValue().get(CaseConstants.NEW_VALUE));
//                            statusDetails.setResolutionId(entity.getResolutionId().longValue());
                            detailsList.add(statusDetails);
//                            Optional<StaffUser> user=staffUserRepository.findById(entity.getAssignee());
//                            sendCustomerTicketStatusChangeMessage(user.get().getUsername(), entity.getStatus(), user.get().getCountryCode(), user.get().getPhone(), user.get().getEmail(), user.get().getMvnoId(), RMQConstants.SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE, RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, Integer.valueOf(entity.getTicketId().intValue()),null, null);//add parameters

                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.ASSIGNEE)) {
                            CaseUpdateDetailsDTO assigneeDetails = new CaseUpdateDetailsDTO();
                            assigneeDetails.setEntitytype(CaseConstants.ENTITY_ASSIGNEE);
                            assigneeDetails.setOperation(CaseConstants.OPERATION_CHANGE_ASSIGNEE);
                            assigneeDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            assigneeDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
                            assigneeDetails.setCaseUpdate(entity);
                            detailsList.add(assigneeDetails);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON)) {
                            CaseUpdateDetailsDTO reasonDetails = new CaseUpdateDetailsDTO();
                            reasonDetails.setEntitytype(CaseConstants.ENTITY_REASON);
                            reasonDetails.setOperation(CaseConstants.OPERATION_CHANGE_REASON);
                            reasonDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            reasonDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
//                            notificationMap.put(CaseConstants.OPERATION_CHANGE_REASON,map.getValue().get(CaseConstants.NEW_VALUE));
                            reasonDetails.setCaseUpdate(entity);
                            detailsList.add(reasonDetails);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.PRIORITY)) {
                            CaseUpdateDetailsDTO priorityDetails = new CaseUpdateDetailsDTO();
                            priorityDetails.setEntitytype(CaseConstants.ENTITY_PRIORITY);
                            priorityDetails.setOperation(CaseConstants.OPERATION_CHANGE_PRIORITY);
                            priorityDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            priorityDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
                            priorityDetails.setCaseUpdate(entity);
                            notificationMap.put(CaseConstants.PRIORITY,map.getValue().get(CaseConstants.NEW_VALUE));
                            detailsList.add(priorityDetails);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.REMARK)) {
                            CaseUpdateDetailsDTO remarkDetails = new CaseUpdateDetailsDTO();
                            remarkDetails.setEntitytype(CaseConstants.ENTITY_REMARKS);
                            remarkDetails.setOperation(CaseConstants.OPERATION_ADD_REMARKS);
                            remarkDetails.setOldvalue(getLatestNewRemark(entity.getTicketId()));
                            remarkDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            remarkDetails.setRemarktype(map.getValue().get(CaseConstants.REMARK_TYPE));
                            remarkDetails.setAttachment(map.getValue().get(CaseConstants.UNIQUE_FILENAME));
                            remarkDetails.setFilename(map.getValue().get(CaseConstants.FILENAME));
//                            notificationMap.put(CaseConstants.REMARK,map.getValue().get(CaseConstants.NEW_VALUE));
                            remarkDetails.setCaseUpdate(entity);

                            detailsList.add(remarkDetails);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON_CATEGORY)) {
                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
                            updates.setEntitytype(CaseConstants.ENTITY_REASON_CATEGORY);
                            updates.setOperation(CaseConstants.OPERATION_CHANGE_REASON_CATEGORY);
                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
                            updates.setCaseUpdate(entity);
                            detailsList.add(updates);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON_SUB_CATEGORY)) {
                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
                            updates.setEntitytype(CaseConstants.ENTITY_REASON_SUB_CATEGORY);
                            updates.setOperation(CaseConstants.OPERATION_CHANGE_REASON_SUB_CATEGORY);
                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
                            updates.setCaseUpdate(entity);
                            detailsList.add(updates);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_TYPE)) {
                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
                            updates.setEntitytype(CaseConstants.ENTITY_CASE_TYPE);
                            updates.setOperation(CaseConstants.OPERATION_CHANGE_TYPE);
                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
//                            notificationMap.put(CaseConstants.CASE_TYPE,map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setCaseUpdate(entity);
                            detailsList.add(updates);
                        }

                        //add category update code here

                        if (map.getKey().equalsIgnoreCase(CaseConstants.RESOLUTION_REASON)) {
                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
                            updates.setEntitytype(CaseConstants.ENTITY_RESOLUTION_REASON);
                            updates.setOperation(CaseConstants.OPERATION_RESOLUTION);
                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
//                            notificationMap.put(CaseConstants.RESOLUTION_REASON,map.getValue().get(CaseConstants.NEW_VALUE));
                            updates.setCaseUpdate(entity);
                            detailsList.add(updates);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_TITLE)) {
                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
                            statusDetails.setEntitytype(CaseConstants.ENTITY_CASE_TITLE);
                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_TITLE);
                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
//                            notificationMap.put(CaseConstants.CASE_TITLE,map.getValue().get(CaseConstants.NEW_VALUE));
                            statusDetails.setCaseUpdate(entity);
                            detailsList.add(statusDetails);
                        }

                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_ROOT_CAUSE)) {
                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
                            statusDetails.setEntitytype(CaseConstants.ENTITY_CASE_ROOT_CAUSE);
                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_CASE_ROOT_CAUSE);
                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
//                            notificationMap.put(CaseConstants.CASE_ROOT_CAUSE,map.getValue().get(CaseConstants.NEW_VALUE));
                            statusDetails.setCaseUpdate(entity);
                            detailsList.add(statusDetails);
                        }

                        log.debug("setting case updated details to Case : {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                        entity.setUpdateDetails(detailsList);
                    }
                }
                if (null == entity.getCommentBy()) entity.setCommentBy(CaseConstants.COMMENT_BY_CUSTOMER);
                //commented as we are already doing this on creation
//                staffUser = staffUserService.get(getLoggedInUserId());
                if (null != staffUser) {
                    entity.setUpdateby(staffUser.getFullName());
                    entity.setCreateby(staffUser.getFullName());
                }

//                dbObj.setNextFollowupDate(null != entity.getNextFollowupDate() ? entity.getNextFollowupDate() : dbObj.getNextFollowupDate());
//                dbObj.setNextFollowupTime(null != entity.getNextFollowupTime() ? entity.getNextFollowupTime() : dbObj.getNextFollowupTime());

                entity.setTicket(dbObj);
                dbObj.getCaseUpdateList().add(0, entity);
            }
            if (dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
                log.debug("Case status is FOLLOW_UP for Case ID: {}; Module : {};", dbObj.getCaseId(), SUBMODULE);
                GenericDataDTO folloupname = ticketFollowUpService.generateNameOfTheFollowUp(dbObj.getCaseId().intValue());
                TicketFollowUpDTO ticketFollowUpDTO = new TicketFollowUpDTO();
                ticketFollowUpDTO.setFollowUpName(folloupname.getData().toString());
                ticketFollowUpDTO.setCaseId(dbObj.getCaseId().intValue());
                ticketFollowUpDTO.setStatus("pending");
                ticketFollowUpDTO.setMvnoId(dbObj.getMvnoId());
                ticketFollowUpDTO.setIsMissed(true);
                ticketFollowUpDTO.setIsSend(true);
                if (entity.getRemark() != null && entity.getRemark().length() > 0) {
                    ticketFollowUpDTO.setRemarks(entity.getRemark());
                    log.debug("Setting follow-up remarks: {}; Module : {};", entity.getRemark(),SUBMODULE);
                } else {
                    ticketFollowUpDTO.setRemarks("");
                    log.debug("No remarks provided For : "+ CaseConstants.STATUS_FOLLOW_UP + ", setting empty remarks. for Case : {}; Module : {};",dbObj.getCaseNumber() ,SUBMODULE);
                }
                ticketFollowUpDTO.setCreatedBy(getLoggedInUserId());
                ticketFollowUpDTO.setStaffUserId(getLoggedInUserId());
                ticketFollowUpDTO.setFollowUpDatetime(dbObj.getNextFollowupTime().atDate(dbObj.getNextFollowupDate()));
                log.debug("Follow-up datetime set to: {}; for Case : {}; Module : {};", ticketFollowUpDTO.getFollowUpDatetime(),dbObj.getCaseNumber(),SUBMODULE);
                ticketFollowUpService.save(ticketFollowUpDTO);
                log.info(LogConstants.REQUEST_FOR + " update entity " + LogConstants.REQUEST_TO_UPDATE + ":" + CaseConstants.STATUS_FOLLOW_UP + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + " Message : Follow-up DTO saved for Case : {}", dbObj.getCaseNumber());

            }
            if (dbObj.getCurrentAssigneeId() != null && (!prePriority.equalsIgnoreCase(dbObj.getPriority()))) {
                log.debug("Current assignee ID is not null and priority has changed for Case ID: {}; Module : {};", dbObj.getCaseId(),SUBMODULE);
                StaffUser currentStaff = new StaffUser();
                currentStaff = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).orElse(null);
                if (currentStaff != null) {
                    if (currentStaff.getParentStaffId() != null) {
                        if (getLoggedInUserId() == currentStaff.getParentStaffId()) {
                            log.debug("logged in user or  parent staff. Updating ticket TAT matrix for Case ID: {}; Module : {};", dbObj.getCaseId(),SUBMODULE);
                            Case updatedCase = tatUtils.updateticketTatMatrix(dbObj);
                            dbObj.setNextFollowupTime(updatedCase.getNextFollowupTime());
                            dbObj.setNextFollowupDate(updatedCase.getNextFollowupDate());
                        } else {
                            log.warn("Only Parent Staff can change the ticket priority for Case ID: {}; Module : {};", dbObj.getCaseId(),SUBMODULE);
                            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Only Parent Staff can change the ticket priority !!", null);
                        }
                    }

                }else{
                    log.debug("Current staff not found with StaffID: {}; Module : {};", currentStaff.getId(),SUBMODULE);
                }
                dbObj.setCurrentAssigneeName(currentStaff.getFullName());


            }

//            else if (dbObj.getCaseStatus().equalsIgnoreCase("pending")) {
//                tatUtils.changeTicketTatStatus(dbObj, false);
//            }
//            else if (entity.getStatus() != null && !entity.getStatus().equalsIgnoreCase("ON HOLD")) {
//                tatUtils.changeTicketTatStatus(dbObj, true);
//            }
            if (entity.getTeamHierarchyMappingId() != null) {
                log.debug("setting team hierarchy mapping for case : {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                dbObj.setTeamHierarchyMappingId(entity.getTeamHierarchyMappingId());
            }
            if (entity.getCaseFeedbackRel() != null) {
                List<CaseFeedbackRel> caseFeedbackRelList = new ArrayList<>();
                for (CaseFeedbackRel caseFeedbackRel : entity.getCaseFeedbackRel()) {
                    if (caseFeedbackRel != null) {
                        caseFeedbackRel.setCreated_date(LocalDateTime.now());
                        caseFeedbackRelList.add(caseFeedbackRel);
                    }
                }
                log.debug("setting case feedback list for case : {}; Module : {};", dbObj.getCaseNumber(),SUBMODULE);
                dbObj.setCaseFeedbackRel(caseFeedbackRelList);


            };
            if(entity.getCreatedById()!=null){
                if(dbObj.getCreatedByName()!=null)
                    log.debug("setting created by name For case {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                    dbObj.setCreatedByName(dbObj.getCreatedByName());
            }
            if (notificationMap.containsKey(CaseConstants.ENTITY_STATUS)) {
                String status = notificationMap.get(CaseConstants.ENTITY_STATUS).toString();
                if ("Done".equals(status) || "Discarded".equals(status)) {
                    log.debug("removing notification for status from notificationMap while status is Done or Discarded for Case: {}; Module : {};",dbObj.getCaseNumber(),SUBMODULE);
                    notificationMap.remove(CaseConstants.ENTITY_STATUS);
                }
            }
            if(!notificationMap.isEmpty()){
                if(Objects.nonNull(dbObj.getCustomersId())){
                    Customers customers=customerRepository.findById(dbObj.getCustomersId()).orElse(null);
                    if(Objects.nonNull(customers)) {
                        log.debug("sending task update for customer : {}; Case:{}; Module : {};",customers.getUsername(),dbObj.getCaseNumber(),SUBMODULE);
                        sendTaskUpdateforCustomer(notificationMap,customers,dbObj);
                    }
                }
                if(Objects.nonNull(dbObj.getCurrentAssigneeId())){
                    StaffUser currentAssignee=staffUserRepository.findById(dbObj.getCurrentAssigneeId()).orElse(null);
//                    if(Objects.nonNull(currentAssigneeId)) {
//                        if(Objects.nonNull(currentAssigneeId.getParentStaffId())){
//                            if(currentAssigneeId.getParentStaffId().equals(dbObj.getCreatedById())) {
//                                StaffUser assigneeParentStaff = staffUserRepository.findById(currentAssigneeId.getParentStaffId()).orElse(null);
//                                if (Objects.nonNull(assigneeParentStaff)) {
//                                    sendUpdatetaskForStaff(notificationMap, assigneeParentStaff, dbObj);
//                                }
//                            }else if(currentAssigneeId.getId().equals(dbObj.getCreatedById())){
//                                sendUpdatetaskForStaff(notificationMap,currentAssigneeId,dbObj);
//                            }
//                        }
//                        else{
//                            StaffUser creteadStaff=staffUserRepository.findById(dbObj.getCreatedById()).orElse(null);
//                            sendUpdatetaskForStaff(notificationMap,creteadStaff,dbObj);
//                        }
//                    }
                    if(Objects.nonNull(currentAssignee) && dbObj.getIsFromCalender()){
                        sendUpdatetaskForStaff(notificationMap,assigneeStaff,dbObj);
                        log.info("Update notifcation sent to currentAssignee name :"+currentAssignee.getFirstname()+"and staff id:"+currentAssignee.getId());
                        if(currentAssignee.getParentStaffId()!=null){
                            StaffUser parentStaff=staffUserRepository.findById(currentAssignee.getParentStaffId()).orElse(null);
                            sendUpdatetaskForStaff(notificationMap,parentStaff,dbObj);
                            log.info("Update notifcation sent to parentStaff name :"+parentStaff.getFirstname()+"and staff id:"+parentStaff.getId());
                            if(dbObj.getCreatedById()!=currentAssignee.getId() && dbObj.getCreatedById()!= parentStaff.getId()){
                                StaffUser creatdByStaff=staffUserRepository.findById(dbObj.getCreatedById()).orElse(null);
                                sendUpdatetaskForStaff(notificationMap,creatdByStaff,dbObj);
                                log.info("Update notifcation sent to currentAssignee id : "+ currentAssignee.getId()+" parentStaff id : "+ parentStaff.getId()+ " CreatedByStaff Id: "+creatdByStaff.getId());
                                log.info("Update notifcation sent to creatdByStaff name :"+creatdByStaff.getFirstname()+" and staff id:"+creatdByStaff.getId());

                            }
                        }else{
                            if(dbObj.getCreatedById()!=currentAssignee.getId()){
                                StaffUser creatdByStaff=staffUserRepository.findById(dbObj.getCreatedById()).orElse(null);
                                sendUpdatetaskForStaff(notificationMap,creatdByStaff,dbObj);
                                log.info("Update notifcation sent to currentAssignee id : "+ currentAssignee.getId()+ "CreatedByStaff Id:"+creatdByStaff.getId());
                                log.info("Update notifcation sent to creatdByStaff name :"+creatdByStaff.getFirstname()+"and staff id:"+creatdByStaff.getId());
                            }

                        }
                    }

                }


            }

            //Send Data to API GateWay
            //CloseTicketCheckMessage message = new CloseTicketCheckMessage(dbObj.getStaffId(),dbObj.getCaseId().intValue(),dbObj.getCaseNumber(),dbObj.getCaseStatus());
//            messageSender.send(message, RMQConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW);
            //kafkaMessageSender.send(new KafkaMessageData(message,CloseTicketCheckMessage.class.getSimpleName()));
            return caseService.updateEntity(dbObj);
        } catch (CustomValidationException ce) {
            log.error(" error during updating entity" + SUBMODULE + ce.getMessage(), ce);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (JsonProcessingException e) {
            log.error(" error during updating entity" + SUBMODULE + e.getMessage(), e);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, e.getMessage(), null);
        } catch (Exception ex) {
            log.error(" error during updating entity" + SUBMODULE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private void sendUpdatetaskForStaff(Map<String, Object> notificationMap, StaffUser staffUser1, CaseDTO dbObj) {
        String SUBMODULE = getModuleNameForLog() + " [sendUpdatetaskForStaff()] ";
        if(!CollectionUtils.isEmpty(notificationMap)) {
            notificationMap.put("userName", staffUser1.getUsername());
            notificationMap.put("emailId", staffUser1.getEmail());
            notificationMap.put("mobileNumber", staffUser1.getPhone());
            notificationMap.put("mvnoId", dbObj.getMvnoId().toString());
            if(Objects.nonNull(dbObj.getBuId())){
                notificationMap.put("buId", dbObj.getBuId());
            }
            notificationMap.put("caseId", dbObj.getCaseId());
            notificationMap.put("caseNumber", dbObj.getCaseNumber());
            notificationMap.put("notificationFor", "Staff");
            TaskUpdateMessage taskUpdateMessage = new TaskUpdateMessage("Task Update", notificationMap, "Update Task", null, null, null, true, true);
            log.debug("TaskUpdateMessage created successfully, caseId: {}; module: {};", dbObj.getCaseId(), SUBMODULE);
            System.out.println("*******************TaskUpdateMessage for staff created successfully******************"+taskUpdateMessage);
            kafkaMessageSender.send(new KafkaMessageData(taskUpdateMessage, TaskUpdateMessage.class.getSimpleName(), KafkaConstant.TASK_FOR_STAFF ));
        }else {
            log.debug("Notification map is empty,for task update notification for staff, module: {};", SUBMODULE);
        }
    }

    private void sendTaskUpdateforCustomer(Map<String, Object> notificationMap, Customers customers, CaseDTO caseDTO) {
        String SUBMODULE=getModuleNameForLog()+" [sendTaskUpdateforCustomer()] ";
        if(caseDTO.getCaseId()!=null)
            log.debug("send Task Update for Customer  with case id: {} , module: {}", caseDTO.getCaseId(),SUBMODULE);
        if(!CollectionUtils.isEmpty(notificationMap)) {
            notificationMap.put("userName", customers.getUsername());
            notificationMap.put("emailId", customers.getEmail());
            notificationMap.put("mobileNumber", customers.getPhone());
            notificationMap.put("mvnoId", caseDTO.getMvnoId().toString());

            if(Objects.nonNull(caseDTO.getBuId())){
                notificationMap.put("buId", caseDTO.getBuId());
            }

            notificationMap.put("taskId", caseDTO.getCaseId());
            notificationMap.put("caseNumber", caseDTO.getCaseNumber());
            notificationMap.put("notificationFor", "Customer");
            TaskUpdateMessage taskUpdateMessage = new TaskUpdateMessage("Task Update", notificationMap, "Update Task", null, null, null, true, true);
            log.debug("Task Update Message created successfully, caseId: {}, module: {}", caseDTO.getCaseId(), SUBMODULE);
            System.out.println(" ********************Task Update for customer is sent to notification******************"+taskUpdateMessage);
            kafkaMessageSender.send(new KafkaMessageData(taskUpdateMessage, TaskUpdateMessage.class.getSimpleName(), KafkaConstant.TASK_FOR_CUSTOMER));
        }
    }

    private void sendRescheduleMessage(String username, String mobile, String email, Integer mvnoId, String caseNumber, String caseStatus, String priority, String remark, String startDate, String endDate, String customer) {
        String SUBMODULE=getModuleNameForLog()+" [sendRescheduleMessage()] ";
        try {
            TicketCreationMessage ticketCreationMessage = new TicketCreationMessage(username, mobile, email, RMQConstants.TASK_RESCHEDUKE_NOTIFICATION,null, caseNumber, mvnoId, caseStatus, priority, remark,startDate,endDate,customer,null);
            log.debug("Ticket Creation Message created successfully for username:{} , module: {}",(username != null)?username:"NOT Found", SUBMODULE);
            System.out.println("**************************Task Reschedule Notification*******************"+ ticketCreationMessage);
            Gson gson = new Gson();
            gson.toJson(ticketCreationMessage);
            kafkaMessageSender.send(new KafkaMessageData(ticketCreationMessage, TicketCreationMessage.class.getSimpleName(), KafkaConstant.TASK_RESCHEDUKE_NOTIFICATION));
        } catch (Throwable e) {
            log.error("error while send Reschedule Message for username: {}, error message: {}, module: {}", (username != null)?username:"NOT Found", e.getMessage(), SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }
    }


    public void sendAssignTicketMessege(String username, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String name, String nextFollowupDate, String staffusername, String nextFollowUpTime, String altEmail, String serialNumber, Long buId) {
        String SUBMODULE = getModuleNameForLog()+" [sendAssignTicketMessege()] ";
        try {
              String folleoupdateAndTime  = nextFollowupDate;

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(username, mobileNumber, emailId, mvnoId, RMQConstants.TICKET_ASSIGN_SUCCESS, optionalTemplate.get(), RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, caseNumber, name, folleoupdateAndTime, staffusername, altEmail, serialNumber, buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
                    log.debug("sending assign ticket message to username : {}; Module : {};", (username != null)?username:"NOT Found",SUBMODULE);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(), KafkaConstant.TASK_ASSIGN_TEAM_SUCCESS));
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Task is not assigned.");
                log.warn("task is not assigned to send assign ticket message; caseNumber : {}; Module : {};",(caseNumber != null)?caseNumber:"NOT Found",SUBMODULE);
            }


        } catch (Throwable e) {
            log.error("error while send Assign ticket message;caseNumber: {}; Message: {}; Module : {};",(caseNumber != null)?caseNumber:"NOT Found",e.getMessage(),SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }

    }

    public void sendAssignTicketMessege(String username, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String name, String nextFollowupDate, String staffusername, String nextFollowUpTime) {
        try {
            String folleoupdateAndTime = nextFollowupDate + "," + nextFollowUpTime;

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(username, mobileNumber, emailId, mvnoId, RMQConstants.TICKET_ASSIGN_SUCCESS, optionalTemplate.get(), RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, caseNumber, name, folleoupdateAndTime, staffusername);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(), KafkaConstant.TASK_ASSIGN_TEAM_SUCCESS));
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket is not assigned.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Integer assingTicketToStaffFromTeam(Teams team, Customers customers) {
        String SUBMODULE = getModuleNameForLog() + " [assingTicketToStaffFromTeam()] ";
        Integer staffId = null;
        Set<StaffUser> staffList = team.getStaffUser();
        if (staffList != null && staffList.size() > 0) {
            List<StaffUser> tempStaffList = new ArrayList<StaffUser>();
            for (StaffUser staff : staffList) {
                if (staff.getServiceAreaNameList() != null && staff.getServiceAreaNameList().size() > 0) {
                    for (ServiceArea serviceArea : staff.getServiceAreaNameList()) {
                        if (Objects.equals(customers.getServiceAreaId(), serviceArea.getId())) {
                            if (staff.getBusinessUnitNameList().size() > 0) {
                                if (customers.getBuId() != null) {
                                    for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                        if (customers.getBuId().equals(businessUnit.getId())) {
                                            tempStaffList.add(staff);
                                        }
                                    }
                                }
                            } else {
                                tempStaffList.add(staff);
                            }
                        }
                    }
                } else if (staff.getServiceAreaNameList().size() == 0) {
                    if (staff.getBusinessUnitNameList().size() > 0) {
                        if (customers.getBuId() != null) {
                            for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                if (customers.getBuId().equals(businessUnit.getId())) {
                                    tempStaffList.add(staff);
                                }
                            }
                        }
                    } else {
                        tempStaffList.add(staff);
                    }

                }
            }

            if (tempStaffList != null && tempStaffList.size() > 0) {
                HashMap<Integer, Long> countListmap = new HashMap<Integer, Long>();
                for (StaffUser staffUserTemp : tempStaffList) {
                    Long assingmentCount = caseService.findMinimumAssignReuqestByStaff(staffUserTemp.getId());
                    if (assingmentCount != null) {
                        countListmap.put(staffUserTemp.getId(), assingmentCount);
                    }

                }
                Long minValueInMap = (Collections.min(countListmap.values()));  // This will return min value in the HashMap
                for (Entry<Integer, Long> entry : countListmap.entrySet()) {  // Iterate through HashMap
                    if (entry.getValue() == minValueInMap) {
                        staffId = entry.getKey();     // staff id with minimum reuqest
                    }
                }
                if (countListmap.size() > 0 && staffId != null) {
                    return staffId;
                }
            } else {
                log.error("Staff service is mismatch with customer's service area.. Module : {}",SUBMODULE);
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Staff service area is mismatch with customer's service area.", null);
            }
        }
        return staffId;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public CaseDTO assignTicketFromTeam(Long caseId, Integer teamId, String remark) {
//        try {
//            Case aCase = caseRepository.findById(caseId).orElse(null);
//            Teams teams = teamsRepository.findById(teamId.longValue()).orElse(null);
//            Customers customers = new Customers();
//            if (null != aCase.getCustomers().getId()) {
//                customers = customersService.get(aCase.getCustomers().getId());
//            }
//            TicketReasonCategoryDTO ticketReasonCategoryDTO = ticketReasonCategoryService.getEntityById(aCase.getTicketReasonCategoryId());
//            TicketReasonCategoryTATMapping ticketReasonCategoryTATMapping = ticketReasonCategoryDTO.getTicketReasonCategoryTATMappingList().stream().filter(tatMapping -> tatMapping.getTeamId() == teamId).findFirst().get();
//            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//            caseUpdateDTO.setTicketId(caseId);
//            caseUpdateDTO.setAssignee(assingTicketToStaffFromTeam(teams, customers));
//            caseUpdateDTO.setRemark(remark);
//            caseUpdateDTO.setRemarkType("Change Assignee");
//            caseUpdateDTO.setTatMappingId(ticketReasonCategoryTATMapping.getMappingId());
//            return updateEntity(caseUpdateDTO, null);
//        } catch (Exception ex) {
//            log.error(getModuleNameForLog() + ex.getMessage(), ex);
//
//
//        }
        return null;
    }

    public GenericDataDTO bulkUpdateDetails(List<Long> caseids, String status, String remark) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            for (Long caseid : caseids) {
                Case dbObj = caseRepository.findById(caseid).orElse(null);
                CaseUpdate caseUpdate = caseUpdateRepository.findById(dbObj.getPrimaryKey()).get();
                CaseUpdateDetails caseUpdateDetails = caseUpdateDetailsRepository.findById(dbObj.getCaseId()).orElse(null);

                caseUpdate.setRemarkType("Change Status");

                caseUpdateDetails.setNewvalue(status);
                CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
                caseUpdateDTO.setRemark(remark);//caseUpdateMapper.domainToDTO(caseUpdate, new CycleAvoidingMappingContext());
                caseUpdateDTO.setStatus(status);
                caseUpdateDTO.setTicketId(caseid);
                caseUpdateDTO.setRemarkType(caseUpdate.getRemarkType());

                // caseUpdateDTO.setCaseType("Change Status");
                caseUpdateDTO.setCaseType(dbObj.getCaseType());
                updateEntity(caseUpdateDTO, null, false, null);

                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());

            }

        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return genericDataDTO;
    }

    public void sendCustomerTicketStatusChangeMessage(String username, String status, String countryCode, String mobileNumber, String emailId, Integer mvnoId, String message, String sourceName, Integer ticketnumber, Long buId, String altEmail) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    // Set message in queue to send notification after ticket status change .
                    if (!status.equalsIgnoreCase("Closed")) {
                        Case ticket = caseRepository.findById(Long.valueOf(ticketnumber)).get();
                        Map<String, Object> customerData = new HashMap<>();
                        CustTicketStatusMessage custTicketStatusMessage = new CustTicketStatusMessage(username, status, customerData, countryCode, mobileNumber, emailId, mvnoId, message, optionalTemplate.get(), sourceName, ticketnumber, buId, ticket.getCaseNumber(), altEmail);
                        Gson gson = new Gson();
                        gson.toJson(custTicketStatusMessage);
//                        messageSender.send(custTicketStatusMessage, RMQConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE);
                        kafkaMessageSender.send(new KafkaMessageData(custTicketStatusMessage, CustTicketStatusMessage.class.getSimpleName()));
                    }
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void bulkOperationPerform(CaseUpdateDTO entity) {
        String SUBMODULE = getModuleNameForLog() + " [bulkOperationPerform()] ";
        log.debug("Starting bulk operation for CaseID: {}; module: {}", entity.getTicketId(),SUBMODULE);
        QCase qCase = QCase.case$;
        BooleanExpression linkedTicketBoolExp = qCase.isNotNull().and(qCase.isDelete.eq(false))
                .and(qCase.parentTicketId.eq(Math.toIntExact(entity.getTicketId())));
        List<Case> cases = new ArrayList<>();
        cases = (List<Case>) caseRepository.findAll(linkedTicketBoolExp);
        if (!cases.isEmpty()) {
            log.debug("cases found for bulk operation : {}; module: {}", cases.size(),SUBMODULE);
            for (int i = 0; i < cases.size(); i++) {
                if (entity.getStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_DONE)) {
                    cases.get(i).setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                } else if (entity.getStatus().equalsIgnoreCase(CaseConstants.TASK_STATUS_DISCARDED)) {
                    cases.get(i).setCaseStatus(CaseConstants.TASK_STATUS_DONE);
                }
                // cases.get(i).setTeamHierarchyMappingId(null);
                cases.get(i).setAssigneeName(null);
                cases.get(i).setCurrentAssignee(null);
                caseRepository.save(cases.get(i));
                log.debug("Case saved successfully after change status for bulk operation for case: {}; module: {}", cases.get(i).getCaseNumber(),SUBMODULE);
            }
            log.info("Cases saved successfully After bulk Operation Perform; module: {}",SUBMODULE);
        }
    }


    public void sendProblemDomainChangeMsg(String newValue, String ticketNumber, String oldValue, String staffPersonName, String parentMobileNumber, String parentEmailId, Integer mvnoId, Long buId) {
        String SUBMODULE = getModuleNameForLog() + " [sendProblemDomainChangeMsg()] ";
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.SEND_PROBLEM_DOMAIN_TEMPLATE_NAME);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    SendProblemDomainChangeMsg sendProblemDomainChangeMsg = new SendProblemDomainChangeMsg(parentMobileNumber, parentEmailId, RMQConstants.SEND_PROBLEM_DOMAIN_REMARK_MSG, optionalTemplate.get(), oldValue, staffPersonName, newValue, mvnoId, ticketNumber, buId);
                    Gson gson = new Gson();
                    gson.toJson(sendProblemDomainChangeMsg);
//                    messageSender.send(sendProblemDomainChangeMsg, RMQConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG);
                    System.out.println("************ Task Category change message sent to notification service  ***************"+sendProblemDomainChangeMsg.getMessage());
                    kafkaMessageSender.send(new KafkaMessageData(sendProblemDomainChangeMsg, SendProblemDomainChangeMsg.class.getSimpleName(), KafkaConstant.SEND_TASK_CATEGORY_CHANGE_MSG));
                    // public static final String SEND_TASK_CATEGORY_CHANGE_MSG="SEND_TASK_CATEGORY_CHANGE_MSG";
                    log.debug("Problem domain change message successfully sent for Case: {}, module: {}", ticketNumber, SUBMODULE);
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                log.warn("TAT Template not available for problem domain change is not present; message not sent; Case: {}, module: {}", ticketNumber, SUBMODULE);
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            log.error("An error occurred while sending problem domain change message for Case: {}; error message: {}; module: {}", ticketNumber, e.getMessage(), SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }
    }


    public void sendCreateTicketMessege(String staffName, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String caseStatus, String casePriority, String caseRemark,String startDate,String endDate,String taskFor,String createdByName) {
        String SUBMODULE = getModuleNameForLog() + "[sendCreateTicketMessege()]";
        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_CREATION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketCreationMessage ticketCreationMessage = new TicketCreationMessage(staffName, mobileNumber, emailId, RMQConstants.TASK_CREATION_NOTIFICATION, optionalTemplate.get(), caseNumber, mvnoId, caseStatus, casePriority, caseRemark,startDate,endDate,taskFor,createdByName);
                    Gson gson = new Gson();
                    gson.toJson(ticketCreationMessage);
                    log.debug("Ticket creation message successfully sent for case number: {}; StaffName : {};, module: {}", caseNumber,staffName,SUBMODULE);
                    System.out.println("*****************Task creation notification, sent to notification service ******************"+ ticketCreationMessage);
                    kafkaMessageSender.send(new KafkaMessageData(ticketCreationMessage, TicketCreationMessage.class.getSimpleName(),KafkaConstant.TASK_CREATION_NOTIFICATION));
//                    messageSender.send(ticketCreationMessage, RMQConstants.QUEUE_TICKET_CREATION_SUCCESS);
                }
            } else {
                log.debug("Template for ticket creation is not present; Ticket is not assigned; case number: {}, module: {}", (caseNumber != null)?caseNumber:caseNumber, SUBMODULE);
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket is not assigned.");
            }


        } catch (Throwable e) {
            log.error("An Error occurred while sending ticket creation message for case number: {}; error message: {}; module: {}", (caseNumber != null)?caseNumber:caseNumber, e.getMessage(), SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }

    }

    public void restartTat(CaseDTO oldCaseDto, CaseUpdateDTO caseUpdateDTO) {
        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
                findAllByStaffIdAndEntityIdAndEventNameAndIsActiveAndNotificationType(oldCaseDto.getCurrentAssigneeId(), oldCaseDto.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, false, "Staff");
        if (tatMatrixWorkFlowDetails != null) {
            tatMatrixWorkFlowDetails.setIsActive(true);
            tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
        }

    }

    public void updateTatAtStatusChangeToFollowUp(Case acase, CaseDTO caseDTO) {

        LocalDateTime followupDateTime;
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
        List<TatMatrixWorkFlowDetails> updatedTatmatrixList = new ArrayList<>();

        if (acase != null) {
            followupDateTime = LocalDateTime.of(acase.getNextFollowupDate(), acase.getNextFollowupTime());
            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(acase.getCaseId().intValue(), true, "CASE");

        } else if (caseDTO != null) {
            followupDateTime = LocalDateTime.of(caseDTO.getNextFollowupDate(), caseDTO.getNextFollowupTime());
            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(Math.toIntExact(caseDTO.getCaseId()), true, "CASE");
        } else {
            return;
        }
        // updateing start date first

        for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails : tatMatrixWorkFlowDetailsList) {
            tatMatrixWorkFlowDetails.setStartDateTime(followupDateTime);
            updatedTatmatrixList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails));
        }


        for (int i = 0; i < updatedTatmatrixList.size(); i++) {

            String munit = updatedTatmatrixList.get(i).getMunit();
            long mtime = Long.parseLong(updatedTatmatrixList.get(i).getMtime());

            if (munit.equalsIgnoreCase("Day")) {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusDays(mtime);
            } else if (munit.equalsIgnoreCase("Hours")) {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusHours(mtime);
            } else {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusMinutes(mtime);
            }
        }
        if (acase != null) {
            acase.setNextFollowupDate(followupDateTime.toLocalDate());
            acase.setNextFollowupTime(followupDateTime.toLocalTime());
            caseRepository.save(acase);
        } else if (caseDTO != null) {
            caseDTO.setNextFollowupDate(followupDateTime.toLocalDate());
            caseDTO.setNextFollowupTime(followupDateTime.toLocalTime());
            caseRepository.save(caseMapper.dtoToDomain(caseDTO, new CycleAvoidingMappingContext()));
        }

    }

    public CaseUpdateDTO updateTatAtStatusChangeToFollowUps(CaseUpdateDTO acase) {

        LocalDateTime followupDateTime = null;
        LocalDateTime followUpStrtTime = LocalDateTime.now();
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
        List<TatMatrixWorkFlowDetails> updatedTatmatrixList = new ArrayList<>();

        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();

        if (acase != null) {
            followupDateTime = LocalDateTime.of(acase.getNextFollowupDate(), acase.getNextFollowupTime());
            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(acase.getTicketId().intValue(), true, "CASE");

            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails : tatMatrixWorkFlowDetailsList) {

                //calculateTimeDifference() is substracting the total worked time on ticket
                TimeUnitWithTotal timeUnitWithTotal = calculateTotalWorkedTime(followUpStrtTime, tatMatrixWorkFlowDetails.getStartDateTime());
                if (timeUnitWithTotal == null || timeUnitWithTotal.getUnit() == null) {
                    throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "You can't switch to 'Follow-Up' right after 'InProgress.' Please wait a few minutes before trying again.", null);
                }
                TimeUnitWithTotal finalTimeunitWitTotal = calculateTimeDifference(Long.parseLong(tatMatrixWorkFlowDetails.getMtime()), tatMatrixWorkFlowDetails.getMunit(), timeUnitWithTotal.getUnit(), timeUnitWithTotal.getTotalTime());
                /*for next message send we are setting the followup date in start time, hence once followup is schedule we are adding the
                  ther remaing time into OLA time */
                tatMatrixWorkFlowDetails.setStartDateTime(followupDateTime);
                tatMatrixWorkFlowDetails.setMunit(finalTimeunitWitTotal.getUnit());
                tatMatrixWorkFlowDetails.setMtime(String.valueOf(finalTimeunitWitTotal.getTotalTime()));
                updatedTatmatrixList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails));
            }
        }

        // This loop will iteratate and set the next followup date and time including OLA time to show on GUI counter
        for (int i = 0; i < updatedTatmatrixList.size(); i++) {

            String munit = updatedTatmatrixList.get(i).getMunit();
            long mtime = Long.parseLong(updatedTatmatrixList.get(i).getMtime());

            if (munit.equalsIgnoreCase("Day")) {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusDays(mtime);
            } else if (munit.equalsIgnoreCase("Hours")) {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusHours(mtime);
            } else {
                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusMinutes(mtime);
            }
        }
        if (acase != null) {
            caseUpdateDTO.setNextFollowupDate(followupDateTime.toLocalDate());
            caseUpdateDTO.setNextFollowupTime(followupDateTime.toLocalTime());
            // caseRepository.save(acase);
        }
        return caseUpdateDTO;
    }


    /* method for send notification ticket close*/
    public void sendCustTicketCloseMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId, String caseNumber, Long buId, String caseTitle, String altEmail) {
        String SUBMODULE = getModuleNameForLog() + " [sendCustTicketCloseMessage()] ";
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.CUSTOMER_TICKET_CLOSE_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustTicketCloseMsg custTicketCloseMsg = new CustTicketCloseMsg(username, mobileNumber, emailId, status, mvnoId, caseNumber, RMQConstants.CUSTOMER_TICKET_CLOSE_EVENT, optionalTemplate.get(), RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, buId, caseTitle, altEmail);
                    Gson gson = new Gson();
                    gson.toJson(custTicketCloseMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custTicketCloseMsg, CustTicketCloseMsg.class.getSimpleName()));
//                    messageSender.send(custTicketCloseMsg, RMQConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION);
                }
            } else {
                log.warn("Message of Customer Ticket Close is not sent because template is not present. Module : {};",SUBMODULE);
            }
        } catch (Throwable e) {
            log.error("Error occurred while sending Customer Ticket Close Message. Module : {}; Error Message: {}", SUBMODULE, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    public CaseUpdateDTO setOnHoldEndTime(CaseUpdateDTO caseUpdateDTO) {
        String SUBMODULE = getModuleNameForLog() + " [setOnHoldEndTime()] ";
        if(caseUpdateDTO.getTicketId()!=null)
            log.debug("setting on-hold end time for case with CaseID: {}, module: {}", caseUpdateDTO.getTicketId(), SUBMODULE);
        CaseUpdateDTO caseUpdateDTOs = new CaseUpdateDTO();

        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();

        tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(caseUpdateDTO.getTicketId().intValue(), true, "CASE");

        LocalDateTime localDateTime = LocalDateTime.now();

        List<TatMatrixWorkFlowDetails> savedTatMatrixWorkflowdetailsList = new ArrayList<>();

        if (tatMatrixWorkFlowDetailsList != null) {
            for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
                tatMatrixWorkFlowDetailsList.get(i).setTicketHoldTimeEnd(localDateTime);
                savedTatMatrixWorkflowdetailsList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i)));
            }
            caseUpdateDTOs = calcRemainingTATForResumedTasks(savedTatMatrixWorkflowdetailsList, caseUpdateDTO);
            log.debug("Remaining TAT calculated for resumed tasks for CaseId : {}; Module : {};",caseUpdateDTO.getTicketId(), SUBMODULE);
        }
        log.debug("Exiting setOnHoldEndTime method with updated Case SLA details, module: {}", SUBMODULE);
        return caseUpdateDTOs;
    }

    public void setOnHoldStartTime(CaseUpdateDTO caseUpdateDTO) {
        String SUBMODULE = getModuleNameForLog() + " [setOnHoldStartTime()] ";
        if(caseUpdateDTO.getTicketId()!=null)
            log.debug("Setting On Hold Start Time for CAse: {}; module: {};", caseUpdateDTO.getTicketId().intValue(), SUBMODULE);
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();

        tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(caseUpdateDTO.getTicketId().intValue(), true, "CASE");

        LocalDateTime localDateTime = LocalDateTime.now();

        if (tatMatrixWorkFlowDetailsList != null) {
            for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
                tatMatrixWorkFlowDetailsList.get(i).setTicketHoldTimeInit(localDateTime);
                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i));
            }
            log.info("tat Matrix Work Flow Details List for set on hold start time saved for CaseId : {}; Module : {};",caseUpdateDTO.getTicketId(),SUBMODULE);
        }else {
            log.debug("No active workflow details found for CaseID: {}, module: {}", caseUpdateDTO.getTicketId(), SUBMODULE);
        }

    }

//This service is no use hence commented

//    public CaseUpdateDTO addAdditionalTimeInTicket (List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO)
//    {
//        CaseUpdateDTO caseUpdateDTO1 = new CaseUpdateDTO();
//        Long totalAddtionalTime = null;
//        if(tatMatrixWorkFlowDetails.size()>0){
//
//            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1:tatMatrixWorkFlowDetails) {
//                Duration additionalTime = Duration.between(tatMatrixWorkFlowDetails1.getTicketHoldTimeInit(), tatMatrixWorkFlowDetails1.getTicketHoldTimeEnd());
//
//                long days = additionalTime.toDays();
//                long hours = additionalTime.toHours() % 24;
//                long minutes = additionalTime.toMinutes() % 60;
//                long seconds = additionalTime.getSeconds() % 60;
//
//                Integer previourMtime = Integer.valueOf(tatMatrixWorkFlowDetails1.getMtime());   // 12
//                String previousMunit = tatMatrixWorkFlowDetails1.getMunit();     // M : Minutes , H: Hours , D : Day
//
//                if (days > 0) {
//                    tatMatrixWorkFlowDetails1.setMtime(String.valueOf(days+(previourMtime)));
//                    tatMatrixWorkFlowDetails1.setMunit("Day");
//                    totalAddtionalTime = days;
//                } else {
//                    if (hours > 0) {
//                        tatMatrixWorkFlowDetails1.setMtime(String.valueOf(previourMtime+hours));
//                        tatMatrixWorkFlowDetails1.setMunit("Hours");
//                        totalAddtionalTime = hours;
//
//                    } else {
//                        if (minutes > 0) {
//                            tatMatrixWorkFlowDetails1.setMtime(String.valueOf(previourMtime+minutes));
//                            tatMatrixWorkFlowDetails1.setMunit("Min");
//                            totalAddtionalTime = minutes;
//                        }
//                    }
//                }
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails1);
//                caseUpdateDTO = caseFolloupdateUpdate(tatMatrixWorkFlowDetails1, caseUpdateDTO);
//
//                caseUpdateDTO1 = updateSlaTime(caseUpdateDTO,totalAddtionalTime,"",tatMatrixWorkFlowDetails.get(0).getEntityId());
//            }
//
//        }
//        caseUpdateDTO.setCaseSlaTime(caseUpdateDTO1.getCaseSlaTime());
//        caseUpdateDTO.setCaseSlaUnit(caseUpdateDTO1.getCaseSlaUnit());
//        return caseUpdateDTO;
//    }

    public CaseUpdateDTO addAdditionalTimeInTicket(List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO) {
        CaseUpdateDTO caseUpdateDTO1 = new CaseUpdateDTO();
        Long totalAdditionalTime = null;
        String totalAdditionalTimeUnit = null;

        if (tatMatrixWorkFlowDetails.size() > 0) {
            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1 : tatMatrixWorkFlowDetails) {
                Duration additionalTime = Duration.between(tatMatrixWorkFlowDetails1.getTicketHoldTimeInit(), tatMatrixWorkFlowDetails1.getTicketHoldTimeEnd());

                long days = additionalTime.toDays();
                long hours = additionalTime.toHours() % 24;
                long minutes = additionalTime.toMinutes() % 60;
                long seconds = additionalTime.getSeconds() % 60;

                Integer previousMTime = Integer.valueOf(tatMatrixWorkFlowDetails1.getMtime());
                String previousMUnit = tatMatrixWorkFlowDetails1.getMunit();

                // Convert Mtime to the smaller unit
                if (days > 0) {
                    totalAdditionalTimeUnit = "Day";
                    totalAdditionalTime = days;
                } else if (hours > 0) {
                    totalAdditionalTimeUnit = "Hour";
                    totalAdditionalTime = hours;
                } else if (minutes > 0) {
                    totalAdditionalTimeUnit = "Min";
                    totalAdditionalTime = minutes;
                }

                TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(previousMTime, previousMUnit, totalAdditionalTime.intValue(), totalAdditionalTimeUnit);


                // Set the Mtime and Munit
                tatMatrixWorkFlowDetails1.setMtime(String.valueOf(timeUnitWithTotal.getTotalTime()));
                tatMatrixWorkFlowDetails1.setMunit(timeUnitWithTotal.getUnit());

                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails1);
                // caseUpdateDTO = caseFolloupdateUpdate(tatMatrixWorkFlowDetails1, caseUpdateDTO);

                caseUpdateDTO1 = updateSlaTime(caseUpdateDTO, totalAdditionalTime, totalAdditionalTimeUnit, "", tatMatrixWorkFlowDetails.get(0).getEntityId());
            }
        }

        caseUpdateDTO.setCaseSlaTime(caseUpdateDTO1.getCaseSlaTime());
        caseUpdateDTO.setCaseSlaUnit(caseUpdateDTO1.getCaseSlaUnit());
        return caseUpdateDTO;
    }


    public CaseUpdateDTO calcRemainingTATForResumedTasks(List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO) {
        String SUBMODULE = getModuleNameForLog() + " [calcRemainingTATForResumedTasks()] ";
        if(caseUpdateDTO.getTicketId()!=null)
            log.debug("Calculating Remaining TAT For Resumed Tasks with {} workflow details, module: {}", tatMatrixWorkFlowDetails.size(), SUBMODULE);
        CaseUpdateDTO caseUpdateDTO1 = new CaseUpdateDTO();
        Long totalWorkTime = null;
        String totalWorkTimeUnit = null;

        if (tatMatrixWorkFlowDetails.size() > 0) {
            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1 : tatMatrixWorkFlowDetails) {

                Duration totalWorkingTime = Duration.between(tatMatrixWorkFlowDetails1.getStartDateTime(), tatMatrixWorkFlowDetails1.getTicketHoldTimeInit()).abs();

                long days = totalWorkingTime.toDays();
                long hours = totalWorkingTime.toHours() % 24;
                long minutes = totalWorkingTime.toMinutes() % 60;
                long seconds = totalWorkingTime.getSeconds() % 60;

                Integer previousMTime = Integer.valueOf(tatMatrixWorkFlowDetails1.getMtime());
                String previousMUnit = tatMatrixWorkFlowDetails1.getMunit();

                // Convert Mtime to the smaller unit
                if (days > 0) {
                    totalWorkTimeUnit = "Day";
                    totalWorkTime = days;
                } else if (hours > 0) {
                    totalWorkTimeUnit = "Hour";
                    totalWorkTime = hours;
                } else if (minutes > 0) {
                    totalWorkTimeUnit = "Min";
                    totalWorkTime = minutes;
                }

                TimeUnitWithTotal timeUnitWithTotal = removeTimeInSmallerUnit(previousMTime,previousMUnit,totalWorkTime == null ? 0 : totalWorkTime.intValue(),(totalWorkTimeUnit == null) ? "min" : totalWorkTimeUnit);


                // Set the Mtime and Munit
                tatMatrixWorkFlowDetails1.setMtime(String.valueOf(timeUnitWithTotal.getTotalTime()));
                tatMatrixWorkFlowDetails1.setMunit(timeUnitWithTotal.getUnit());

                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails1);
                // caseUpdateDTO = caseFolloupdateUpdate(tatMatrixWorkFlowDetails1, caseUpdateDTO);

                caseUpdateDTO1 = updateSlaTime(caseUpdateDTO, totalWorkTime, totalWorkTimeUnit, "", tatMatrixWorkFlowDetails.get(0).getEntityId());
            }
        }

        caseUpdateDTO.setCaseSlaTime(caseUpdateDTO1.getCaseSlaTime());
        caseUpdateDTO.setCaseSlaUnit(caseUpdateDTO1.getCaseSlaUnit());
        log.debug("Exiting calcRemainingTATForResumedTasks with updated case SLA time: {}; SLA unit: {}; module: {};",caseUpdateDTO.getCaseSlaTime(), caseUpdateDTO.getCaseSlaUnit(), SUBMODULE);
        return caseUpdateDTO;
    }


    private TimeUnitWithTotal addTimeInSmallerUnit(int previousTime, String previousUnit, int additionalTime, String additionalUnit) {
        String SUBMODULE = getModuleNameForLog() + "[addTimeInSmallerUnit()]";
        long totalTime = 0;
        String smallerUnit;

        if (previousUnit.equals("Day") && (additionalUnit.equals("Hour") || additionalUnit.equals("Min"))) {
            smallerUnit = additionalUnit;
        } else if (previousUnit.equals("Hour") && additionalUnit.equals("Min")) {
            smallerUnit = additionalUnit;
        } else {
            smallerUnit = previousUnit;
        }

        if (smallerUnit.equalsIgnoreCase("Day")) {
            long previousTimeInDays = convertToDays(previousTime, previousUnit);
            long additionalTimeInDays = convertToDays(additionalTime, additionalUnit);
            totalTime = previousTimeInDays + additionalTimeInDays;
        } else if (smallerUnit.equalsIgnoreCase("Hour")) {
            long previousTimeInHours = convertToHours(previousTime, previousUnit);
            long additionalTimeInHours = convertToHours(additionalTime, additionalUnit);
            totalTime = previousTimeInHours + additionalTimeInHours;
        } else if (smallerUnit.equalsIgnoreCase("Min")) {
            long previousTimeInMinutes = convertToMinutes(previousTime, previousUnit);
            long additionalTimeInMinutes = convertToMinutes(additionalTime, additionalUnit);
            totalTime = previousTimeInMinutes + additionalTimeInMinutes;
        }
        log.debug("add Time In Smaller Unit with totalTime: {}; unit: {}; module: {};", totalTime, smallerUnit, SUBMODULE);
        return new TimeUnitWithTotal(totalTime, smallerUnit);
    }

    private TimeUnitWithTotal removeworkingTimeInSmallerUnit(int previousTime, String previousUnit, int additionalTime, String additionalUnit) {
        String SUBMODULE = getModuleNameForLog() + "[removeWorkingTimeInSmallerUnit()]";
        long totalTime = 0;
        String smallerUnit;

        if (previousUnit.equals("Day") && (additionalUnit.equals("Hour") || additionalUnit.equals("Min"))) {
            smallerUnit = additionalUnit;
        } else if (previousUnit.equals("Hour") && additionalUnit.equals("Min")) {
            smallerUnit = additionalUnit;
        } else {
            smallerUnit = previousUnit;
        }

        if (smallerUnit.equalsIgnoreCase("Day")) {
            long previousTimeInDays = convertToDays(previousTime, previousUnit);
            long additionalTimeInDays = convertToDays(additionalTime, additionalUnit);
            totalTime = previousTimeInDays + additionalTimeInDays;
        } else if (smallerUnit.equalsIgnoreCase("Hour")) {
            long previousTimeInHours = convertToHours(previousTime, previousUnit);
            long additionalTimeInHours = convertToHours(additionalTime, additionalUnit);
            totalTime = previousTimeInHours + additionalTimeInHours;
        } else if (smallerUnit.equalsIgnoreCase("Min")) {
            long previousTimeInMinutes = convertToMinutes(previousTime, previousUnit);
            long additionalTimeInMinutes = convertToMinutes(additionalTime, additionalUnit);
            totalTime = previousTimeInMinutes + additionalTimeInMinutes;
        }
        log.debug("remove Working Time In Smaller Unit with totalTime: {}; unit: {}; module: {};", totalTime, smallerUnit, SUBMODULE);
        return new TimeUnitWithTotal(totalTime, smallerUnit);
    }

    private long convertToDays(int time, String unit) {
        if (unit.equals("Hour")) {
            return time / 24;
        } else if (unit.equals("Min")) {
            return time / (24 * 60);
        }
        return time;
    }

    private long convertToHours(int time, String unit) {
        if (unit.equals("Day")) {
            return time * 24;
        } else if (unit.equals("Min")) {
            return time / 60;
        }
        return time;
    }

    private long convertToMinutes(int time, String unit) {
        if (unit.equals("Day")) {
            return time * 24 * 60;
        } else if (unit.equals("Hour")) {
            return time * 60;
        }
        return time;
    }


    public CaseUpdateDTO caseFolloupdateUpdate(TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO) {

        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1 = tatMatrixWorkFlowDetailsRepo.findById(tatMatrixWorkFlowDetails.getId()).orElse(null);

        //finding case
        Case cases = caseRepository.findById(Long.valueOf(tatMatrixWorkFlowDetails1.getEntityId())).orElse(null);


        // calculate upcoming date
        LocalDateTime localDate = tatMatrixWorkFlowDetails1.getStartDateTime();
        LocalDateTime updateFollowupDateTime = null;
        if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Min")) {
            updateFollowupDateTime = localDate.plusMinutes(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
        } else if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Hours")) {
            updateFollowupDateTime = localDate.plusHours(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
        } else if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Day")) {
            updateFollowupDateTime = localDate.plusDays(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
        }
//        caseMapper.domainToDTO(cases,new CycleAvoidingMappingContext());
        caseUpdateDTO.setNextFollowupDate(updateFollowupDateTime.toLocalDate());
        caseUpdateDTO.setNextFollowupTime(updateFollowupDateTime.toLocalTime());
        //caseUpdateRepository.save(caseUpdateMapper.dtoToDomain(caseUpdateDTO,new CycleAvoidingMappingContext()));
        return caseUpdateDTO;
    }


    public CaseUpdateDTO updateSlaTime(CaseUpdateDTO caseUpdateDTO, Long totalAdditionalTime, String totalAdditionalUnit, String priority, Integer caseId) {
        String SUBMODULE = getModuleNameForLog() + " [updateSlaTime()] ";
        TicketTatMatrix tatMatrix = new TicketTatMatrix();
        totalAdditionalTime = (totalAdditionalTime == null) ? 0L : totalAdditionalTime;
        Case cases = new Case();
        CaseDTO caseDTO = new CaseDTO();

        if (caseId != null) {
            cases = caseRepository.findById(Long.valueOf(caseId)).orElse(null);
            caseDTO = caseMapper.domainToDTO(cases, new CycleAvoidingMappingContext());
            tatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseDTO);
        }
        if (caseUpdateDTO.getCaseSlaTime() == null && caseUpdateDTO.getCaseSlaUnit() == null) {
            if (tatMatrix != null) {
                if (cases.getPriority().equalsIgnoreCase("Medium")) {
                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTimep2()));
                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp2());
                    log.debug("Setting SLA for Medium priority: time: {};, unit: {}; module: {};", caseUpdateDTO.getCaseSlaTime(), caseUpdateDTO.getCaseSlaUnit(), SUBMODULE);
                } else if (cases.getPriority().equalsIgnoreCase("High")) {
                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTimep1()));
                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp1());
                    log.debug("Set SLA for High priority: time: {}; unit: {}; module: {};", caseUpdateDTO.getCaseSlaTime(), caseUpdateDTO.getCaseSlaUnit(), SUBMODULE);
                } else {
                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTime3()));
                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp3());
                    log.debug("Set SLA for Low priority: time: {}; unit: {}; module: {};", caseUpdateDTO.getCaseSlaTime(), caseUpdateDTO.getCaseSlaUnit(), SUBMODULE);
                }
            }

        } else {
            if (tatMatrix != null) {
                if (caseUpdateDTO.getPriority().equalsIgnoreCase("High")) {
                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTimep1().intValue(), tatMatrix.getSunitp1(), totalAdditionalTime.intValue(), totalAdditionalUnit);
                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
                } else if (caseUpdateDTO.getPriority().equalsIgnoreCase("Medium")) {
                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTimep2().intValue(), tatMatrix.getSunitp2(), totalAdditionalTime.intValue(), totalAdditionalUnit);
                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
                } else {
                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTime3().intValue(), tatMatrix.getSunitp3(), totalAdditionalTime.intValue(), totalAdditionalUnit);
                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
                }
                log.debug("Updated SLA: time : {}; unit: {}; module: {};", caseUpdateDTO.getCaseSlaTime(), caseUpdateDTO.getCaseSlaUnit(), SUBMODULE);
            }

        }
        log.debug("exiting updateSlaTime with updated CaseUpdateDTO: {}; module: {};", caseUpdateDTO, SUBMODULE);
        return caseUpdateDTO;
    }

    public void removeStartStopTimeWhenFolloupSet(CaseUpdateDTO caseUpdateDTO) {

        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(caseUpdateDTO.getTicketId().intValue(), true);

        if (tatMatrixWorkFlowDetails != null) {
            if (tatMatrixWorkFlowDetails.getTicketHoldTimeInit() != null || tatMatrixWorkFlowDetails.getTicketHoldTimeEnd() != null) {
                tatMatrixWorkFlowDetails.setTicketHoldTimeInit(null);
                tatMatrixWorkFlowDetails.setTicketHoldTimeEnd(null);
                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
            }
        }


    }


    public static TimeUnitWithTotal calculateTotalWorkedTime(LocalDateTime currentTime, LocalDateTime previousTime) {
        String SUBMODULE = "[CaseUpdateService()]" + "[calculateTotalWorkedTime()]";
        Duration duration = Duration.between(currentTime, previousTime);
        long totalSeconds = duration.getSeconds();

        TimeUnitWithTotal timeUnitWithTotal = new TimeUnitWithTotal();

        if (totalSeconds < 0) {
            totalSeconds *= -1;
        }

        if (totalSeconds >= 86400) {
            timeUnitWithTotal.setTotalTime(totalSeconds / 86400);
            timeUnitWithTotal.setUnit("Day");
        } else if (totalSeconds >= 3600) {
            timeUnitWithTotal.setTotalTime(totalSeconds / 3600);
            timeUnitWithTotal.setUnit("Hour");
        } else if (totalSeconds >= 60) {
            timeUnitWithTotal.setTotalTime(totalSeconds / 60);
            timeUnitWithTotal.setUnit("Min");
        }
        log.debug("calculated Total Worked Time, totalTime: {}; unit: {}; module: {}", timeUnitWithTotal.getTotalTime(), timeUnitWithTotal.getUnit(), SUBMODULE);
        return timeUnitWithTotal;
    }


    public TimeUnitWithTotal calculateTimeDifference(long mtime, String mUnit, String workedUnit, long workedTime) {
        String SUBMODULE = getModuleNameForLog() + " [calculateTimeDifference()] ";
        log.debug("Calculating time difference for updateTatAtStatusChangeToFollowUps mtime: {}, mUnit: {}, workedUnit: {}, workedTime: {}, module: {}",mtime, mUnit, workedUnit, workedTime, SUBMODULE);
        TimeUnitWithTotal timeUnitWithTotal = new TimeUnitWithTotal();

        TimeUnitWithTotal result = removeTimeInSmallerUnit(Math.toIntExact(mtime), mUnit, (int) workedTime, workedUnit);
        timeUnitWithTotal.setTotalTime(result.getTotalTime());
        timeUnitWithTotal.setUnit(result.getUnit());
        log.debug("calculated Time Difference , totalTime: {}, unit: {}, module: {}",timeUnitWithTotal.getTotalTime(), timeUnitWithTotal.getUnit(), SUBMODULE);
        return timeUnitWithTotal;
    }


    private TimeUnitWithTotal removeTimeInSmallerUnit(int previousTime, String previousUnit, int workingTime, String workingUnit) {
        String SUBMODULE = getModuleNameForLog() + "[removeTimeInSmallerUnit]";
        long totalTime = 0;
        String smallerUnit;

        if (previousUnit.equalsIgnoreCase("Day") && (workingUnit.equalsIgnoreCase("Hour") || workingUnit.equalsIgnoreCase("Min"))) {
            smallerUnit = workingUnit;
        } else if (previousUnit.equalsIgnoreCase("Hour") && workingUnit.equalsIgnoreCase("Min")) {
            smallerUnit = workingUnit;
        } else {
            smallerUnit = previousUnit;
        }

        if (smallerUnit.equalsIgnoreCase("Day")) {
            long previousTimeInDays = convertToDays(previousTime, previousUnit);
            long additionalTimeInDays = convertToDays(workingTime, workingUnit);
            totalTime = previousTimeInDays - additionalTimeInDays;
        } else if (smallerUnit.equalsIgnoreCase("Hour")) {
            long previousTimeInHours = convertToHours(previousTime, previousUnit);
            long additionalTimeInHours = convertToHours(workingTime, workingUnit);
            totalTime = previousTimeInHours - additionalTimeInHours;
        } else if (smallerUnit.equalsIgnoreCase("Min")) {
            long previousTimeInMinutes = convertToMinutes(previousTime, previousUnit);
            long additionalTimeInMinutes = convertToMinutes(workingTime, workingUnit);
            totalTime = previousTimeInMinutes - additionalTimeInMinutes;
        }
        log.debug("remove Time In Smaller Unit with totalTime: {}; smallerUnit : {}; module: {};", totalTime, smallerUnit, SUBMODULE);
        return new TimeUnitWithTotal(totalTime, smallerUnit);
    }


    public CaseUpdateDTO restartTATbeforeFollowupEndAndStatusChage(CaseUpdateDTO caseUpdateDTO) {
        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(caseUpdateDTO.getTicketId().intValue(), true);
        if (tatMatrixWorkFlowDetails != null) {
            tatMatrixWorkFlowDetails.setStartDateTime(LocalDateTime.now());
            tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);

            LocalDateTime localDate = tatMatrixWorkFlowDetails.getStartDateTime();
            LocalDateTime updateFollowupDateTime = null;
            if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Min")) {
                updateFollowupDateTime = localDate.plusMinutes(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
            } else if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Hours")) {
                updateFollowupDateTime = localDate.plusHours(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
            } else if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Day")) {
                updateFollowupDateTime = localDate.plusDays(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
            }

            caseUpdateDTO.setNextFollowupDate(updateFollowupDateTime.toLocalDate());
            caseUpdateDTO.setNextFollowupTime(updateFollowupDateTime.toLocalTime());

        }
        return caseUpdateDTO;
    }

    public String trimToMaxLength(String input, int maxLength) {
        String SUBMODULE = getModuleNameForLog() + " [trimToMaxLength] ";
        if (input.length() > maxLength) {
            return input.substring(0, maxLength);
        }
        return input;
    }

    public String generateUrl(String serverIp, String serverPort, String httpType) {
        //http://192.168.24.6:4200/#/home/ticketManagement
        String finalUrl = "";
        if (!serverIp.isEmpty() && !serverPort.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(httpType);
            stringBuilder.append("://");
            stringBuilder.append(serverIp);
            stringBuilder.append(":");
            stringBuilder.append(serverPort);
            stringBuilder.append("/#");
            stringBuilder.append("/home");
            stringBuilder.append("/ticketManagement");
            return stringBuilder.toString();
        }
        return null;
    }

    public List<CaseUpdate> getcaseUpdatesByCaseAndCustomerId(Long caseId, Integer customerId) {
        String SUBMODULE = getModuleNameForLog()+" [getCaseUpdatesByCaseAndCustomerId()] ";
        try {
            log.debug("getting case updates by case and customerId for caseId:{}, customerId: {}; Module : {}; ", caseId, customerId, SUBMODULE);
            List<CaseUpdate> caseUpdateListByTicketIdAndTicketCustomersId = caseUpdateRepository.findAllByCaseAndCustomerId(caseId, customerId);
            return caseUpdateListByTicketIdAndTicketCustomersId;
        } catch (Exception e) {
            log.error("Unexpected error occurred while getting case updates for caseId:{}, customerId: {}; Module : {}; Message : {};",caseId, customerId, SUBMODULE, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    //String staffName, String mobileNumber, String emailId, String message, TemplateNotification template, String caseNumber, Integer mvnoId, String caseStatus, String casePriority, String caseRemark, String startDate, String endDate, String taskFor

    public void sentNotificaitonOnTaskClose( CaseUpdateDTO entity, CaseDTO dbObj,StaffUser assineeStaff, StaffUser cretedByStaff, Customers linkedCustomers){
        String SUBMODULE =getModuleNameForLog()+" [sendNotificaitonOnTaskClose()] ";
        if(assineeStaff!=null){
            log.debug("Task Close Message created successfully for caseId:{}, assinee staff username: {}, module: {}", dbObj.getCaseId(),assineeStaff.getUsername(), SUBMODULE);
            TaskCloseMessage message = new TaskCloseMessage(assineeStaff.getFirstname(),assineeStaff.getPhone(),assineeStaff.getEmail(),null,null,dbObj.getCaseNumber(),assineeStaff.getMvnoId(),entity.getStatus(),dbObj.getPriority(),dbObj.getRemark(),null,null,"staff");
            System.out.println("******************Task close notification for assinee staff****************************"+message);
            kafkaMessageSender.send(new KafkaMessageData(message,TaskCloseMessage.class.getSimpleName(),KafkaConstant.TASK_CLOSE_MESSAGE));
        }if(linkedCustomers!=null){
            log.debug("Task Close Message created successfully for caseId:{},linkedCustomers  username: {}, module: {}", dbObj.getCaseId(),linkedCustomers.getUsername(), SUBMODULE);
            TaskCloseMessage message = new TaskCloseMessage(linkedCustomers.getFirstname(),linkedCustomers.getPhone(),linkedCustomers.getEmail(),null,null,dbObj.getCaseNumber(),linkedCustomers.getMvnoId(),entity.getStatus(),dbObj.getPriority(),dbObj.getRemark(),null,null,"customer");
            System.out.println("******************Task close notification for Linked Customer****************************"+message);
            kafkaMessageSender.send(new KafkaMessageData(message,TaskCloseMessage.class.getSimpleName(),KafkaConstant.TASK_CLOSE_MESSAGE));
        }if(cretedByStaff!=null){
            log.debug("Task Close Message created successfully for caseId:{}, staff username: {}, module: {}", dbObj.getCaseId(),cretedByStaff.getUsername(), SUBMODULE);
            TaskCloseMessage message = new TaskCloseMessage(cretedByStaff.getFirstname(),cretedByStaff.getPhone(),cretedByStaff.getEmail(),null,null,dbObj.getCaseNumber(),assineeStaff.getMvnoId(),entity.getStatus(),dbObj.getPriority(),dbObj.getRemark(),null,null,"staff");
            System.out.println("******************Task close notification for staff****************************"+message);
            kafkaMessageSender.send(new KafkaMessageData(message,TaskCloseMessage.class.getSimpleName(),KafkaConstant.TASK_CLOSE_MESSAGE));
        }
    }


    public String getLatestNewRemark(Long caseUpdateId) {
        String SUBMODULE = getModuleNameForLog() + " [getLatestNewRemark()] ";
        if(caseUpdateId!=null)
            log.debug("getting Latest New Remark for, caseUpdateId: {}, module: {}", caseUpdateId, SUBMODULE);
        String oldRemark = "-";
        CaseUpdateDetails caseUpdateDtls = caseUpdateDetailsRepository.findAllByCaseUpdateId(caseUpdateId);
        if (caseUpdateDtls != null) {
            oldRemark = caseUpdateDtls.getNewvalue();
            log.debug("found latest remark for caseUpdateId: {};Remark : {}; Module : {};",caseUpdateId,oldRemark,SUBMODULE);
        }
        return oldRemark;
    }

    private boolean isValidFileExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".png") ||
                lowerCaseFilename.endsWith(".jpeg") ||
                lowerCaseFilename.endsWith(".jpg") ||
                lowerCaseFilename.endsWith(".pdf");
    }
    public List<FileMappingListDTO> getFilesByTaskId(Long ticketId){
        List<FileMappingListDTO> fileMappingList = new ArrayList<>();
        List<CustomerTaskFileMapping> customerInventoryFileMappingList = customerTaskFileMappingRepo.findByCustomerTaskMappingId(ticketId);
        if(!customerInventoryFileMappingList.isEmpty()){
            fileMappingList = convertToFileMappingList(customerInventoryFileMappingList, ticketId);
        }

        return fileMappingList;
    }

    public  List<FileMappingListDTO> convertToFileMappingList(List<CustomerTaskFileMapping> mappings, Long ticketId) {
        return mappings.stream()
                .collect(Collectors.groupingBy(CustomerTaskFileMapping::getSection))
                .entrySet().stream()
                .map(entry -> {
                    FileMappingListDTO fileMappingList = new FileMappingListDTO();
                    fileMappingList.setSectionName(entry.getKey());

                    List<FileDetailsDTO> fileDetailsList = entry.getValue().stream()
                            .map(mapping -> {
                                FileDetailsDTO details = new FileDetailsDTO();
                                details.setFileName(mapping.getFilename());
                                details.setUniqueName(mapping.getUniquename());
                                details.setLatitude(mapping.getLatitiude());
                                details.setLongitude(mapping.getLongitude());
                                details.setTicketId(ticketId);
                                details.setOpticalRange(mapping.getOpticalRange());
                                return details;
                            })
                            .collect(Collectors.toList());

                    fileMappingList.setFileDetails(fileDetailsList);
                    return fileMappingList;
                })
                .collect(Collectors.toList());
    }

    public File getAssignTaskFile(CustomerTaskFileMapping customerInventoryMapping, String uniqueName,String section, Integer ticketId) {
        String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
        if (customerInventoryMapping == null) {
            String errorMessage = "Invalid customer task mapping or unique name is null.";
            log.error(errorMessage);
            throw new CustomValidationException(400, errorMessage, null);
        }
        try {
            String subFolderName = File.separator + ticketId + File.separator + section + File.separator ;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            return filePath.toFile();
        } catch (Exception ex) {
            String errorMessage = "Error while retrieving the assigned task file: " + ex.getMessage();
            log.error(errorMessage, ex);
            throw new CustomValidationException(500, errorMessage, ex);
        }
    }

    public void deleteFileFromDatabase(String uniquename){
        CustomerTaskFileMapping customerInventoryFileMapping = customerTaskFileMappingRepo.findByCustomerTaskByUniqueName(uniquename);
        if(customerInventoryFileMapping != null){
            customerTaskFileMappingRepo.delete(customerInventoryFileMapping);
        }
    }

    public Resource getAssignTaskDoc(CustomerTaskFileMapping customerTaskFileMapping, String uniqueName, String section, Integer ticketId) {
        Resource resource = null;
        String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();

        try {
            String subFolderName = File.separator + ticketId + File.separator + section + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            resource = new UrlResource(filePath.toUri());
            return resource;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
            log.error("Error while get Assign Task Doc : " + ex.getMessage() + " for taskId : " + customerTaskFileMapping.getId());
        }
        return resource;
    }

    public void uploadResolutionDocuments(ResoultionFileMappingDTO resoultionFileMappingDTO,List<MultipartFile> resoltionFiles) {
        List<ResoultionFileMapping> resoultionFileMappingList = new ArrayList<>();
        if (resoltionFiles.size() > 0) {
            try {
                ResolutionReasonsDTO resolutionReasonsDTO=resolutionReasonsService.getEntityById(resoultionFileMappingDTO.getResolutionId());
                List<ResoultionFileMapping> mappings = new ArrayList<>();
                if(resoltionFiles.size()>0){
                    for (MultipartFile file : resoltionFiles) {
                        ResoultionFileMapping mapping = new ResoultionFileMapping();
                        mapping.setFilename(file.getOriginalFilename());
                        mapping.setResolution(new ResolutionReasons(resoultionFileMappingDTO.getResolutionId()));
                        mapping.setCaseId(resoultionFileMappingDTO.getCaseId());
                        mapping.setStaffId(Long.valueOf(getLoggedInUserId()));
                        mapping.setResolutionTime(LocalDateTime.now());
                        String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TICKET_PATH).get(0).getValue();
                        String subfolderName=PATH+ File.separator+resolutionReasonsDTO.getName()+ File.separator;
                        MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                        if (file1 != null) {
                            mapping.setUniquename(fileUtility.saveFileToServerForTicket(file1, subfolderName));
                        }
                        mapping.setLongitude(resoultionFileMappingDTO.getLongitude());
                        mapping.setLatitiude(resoultionFileMappingDTO.getLatitude());
                        mapping.setRemarks(resoultionFileMappingDTO.getRemarks());
                        mappings.add(mapping);
                    }
                }else{
                    ResoultionFileMapping mapping=new ResoultionFileMapping();
                    mapping.setLongitude(resoultionFileMappingDTO.getLongitude());
                    mapping.setLatitiude(resoultionFileMappingDTO.getLatitude());
                    mapping.setRemarks(resoultionFileMappingDTO.getRemarks());
                    mapping.setResolution(new ResolutionReasons(resoultionFileMappingDTO.getResolutionId()));
                    mapping.setCaseId(resoultionFileMappingDTO.getCaseId());
                    mapping.setStaffId(Long.valueOf(getLoggedInUserId()));
                    mapping.setResolutionTime(LocalDateTime.now());
                    mappings.add(mapping);
                }
                resoultionFileMappingRepocitory.saveAll(mappings);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
