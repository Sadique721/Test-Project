package com.savbill.taskmanagement.core.modules.tasks.service;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.taskmanagement.core.auditLog.model.AuditForResponseModel;
import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.constants.ClientServiceConstant;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.dto.*;
import com.savbill.taskmanagement.core.dto.*;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.taskmanagement.core.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.taskmanagement.core.modules.ClientServ.repository.ClientServiceRepository;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.domain.QCustomers;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Mail.service.MailService;
import com.savbill.taskmanagement.core.modules.MailConfigration.service.ReceiveMailServiceImpl;
import com.savbill.taskmanagement.core.modules.MailDocument.service.MailDocumentService;
import com.savbill.taskmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.taskmanagement.core.modules.Mvno.repository.MvnoRepository;
import com.savbill.taskmanagement.core.modules.Teams.domain.*;
import com.savbill.taskmanagement.core.modules.Teams.domain.QueryFieldMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamUserMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.mapper.TeamsMapper;
import com.savbill.taskmanagement.core.modules.Teams.model.TeamsDTO;
import com.savbill.taskmanagement.core.modules.Teams.repository.HierarchyRepository;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamUserMappingsRepository;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
//import com.savbill.ticketmanagement.core.modules.common.LoggedInUser;
import com.savbill.taskmanagement.core.modules.staffuser.domain.QStaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.domain.QStaffUserServiceAreaMapping;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUserServiceAreaMapping;
import com.savbill.taskmanagement.core.modules.staffuser.dto.StaffUserPojo;
import com.savbill.taskmanagement.core.modules.staffuser.mapper.StaffUserMapper;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserServiceAreaMappingRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.utils.*;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.utils.*;
import com.savbill.taskmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.CaseUtility.CaseSpecification;
import com.savbill.taskmanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.savbill.taskmanagement.rabbitmq.messages.CloseTicketCheckMessage;
import com.savbill.taskmanagement.rabbitmq.messages.TicketETRMsg;
import com.savbill.taskmanagement.rabbitmq.messages.TicketMessageIntegration;
import com.savbill.taskmanagement.rabbitmq.messages.TicketRescheduleMsg;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
import com.google.gson.Gson;
import com.itextpdf.text.Document;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.MDC;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseService extends ExBaseAbstractService<CaseDTO, Case, Long> {
//    public CaseService(JpaRepository<Case, Long> repository, IBaseMapper<CaseDTO, Case> mapper) {
//        super(repository, mapper);
//    }

    public CaseService(CaseRepository repository, CaseMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "case_id");
    }


    @Autowired
    private CaseRepository caseRepository;


    @Autowired
    TeamsMapper  teamsMapper;

    @Autowired
    private CaseFeedbackRelRepository caseFeedbackRelRepository;

//    @Autowired
//    private LeadMasterRepository leadMasterRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public static final String MODULE = " [CaseService] ";

    @Autowired
    private CaseMapper caseMapper;

    @Autowired
    private CaseAssignmentService assignmentService;

    @Autowired
    private StaffUserService staffUserService;  //this data is needed

    @Autowired
    private TatAuditRepository tatAuditRepository;


//    @Autowired
//    private CaseDTO caseDTO;

//    @Autowired
//    private CaseReasonConfigService caseReasonConfigService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CaseUpdateService caseUpdateService;

    @Autowired
    TeamsService teamsService;


    @Autowired
    ClientServiceSrv clientService;

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    MvnoRepository mvnoRepository;

//    @Autowired
//    HierarchyService hierarchyService;

    @Autowired
    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    TicketAssignStaffMappingRepo ticketAssignStaffMappingRepo;

    @Autowired
    private CaseService caseService;

    private String PATH;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CaseDocDetailsService caseDocDetailsService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    StaffUserMapper staffUserMapper;

    @Autowired
    TeamUserMappingsRepository teamUserMappingsRepository;

    //below code is commented because of no use anywhare
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    //    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    TicketETRAuditRepository ticketETRAuditRepository;

    @Autowired
    private CaseCategoryRepository caseCategoryRepository;
    @Autowired
    private CaseSubCategoryRepository caseSubCategoryRepository;


//    @Autowired
//    private TicketReasonSubCategoryRepo ticketReasonSubCategoryRepo;
//
//    @Autowired
//    private TicketReasonCategoryRepo ticketReasonCategoryRepo;

    @Autowired
    private TatQueryFieldMappingRepo tatQueryFieldMappingRepo;

//    @Autowired
//    private WorkFlowQueryUtils workFlowQueryUtils;

//    @Autowired
//    private EnterpriseETRAuditRepository enterpriseETRAuditRepository;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;



    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CaseUpdateRepository caseUpdateRepository;

    @Autowired
    CaseCategoryTatMappingRepo caseCategoryTatMappingRepo;

    @Autowired
    CaseDocDetailsRepository caseDocDetailsRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ReceiveMailServiceImpl receiveMailService;

    @Autowired
    private MailDocumentService mailDocumentService;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    CaseSpecification caseSpecification;

    @Autowired
    ExternalTicketLinkRepository externalTicketLinkRepository;





    @Transactional
    public CaseDTO saveEntity(CaseDTO entity, List<MultipartFile> file) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + " [SaveEntity]";
        try {
            ApplicationLogger.logger.debug("entering case entity { Module : {}; }",SUB_MODULE);

            if (entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("EMAIL")) {
                generateToken(entity.getTeamId());
            }



            ApplicationLogger.logger.debug("current assignee id " + ((entity.getCurrentAssigneeId() != null)?entity.getCurrentAssigneeId():"NOT-FOUND"));

            if (Objects.nonNull(entity.getCreatedFrom()) && entity.getCreatedFrom().equalsIgnoreCase("EMAIL")) {
                ApplicationLogger.logger.debug("generating token for email : Module : {}; Create From : {} }",SUB_MODULE, entity.getCreatedFrom());
                generateToken(entity.getTeamId());
            }


            if (entity.getFirstRemark() != null) {
                if (entity.getFirstRemark().length() > 0) {
                    ApplicationLogger.logger.debug("setting first remark for entity {}; Module : {}; ",entity.getCaseTitle(),SUB_MODULE);
                    entity.setFirstRemark(caseUpdateService.trimToMaxLength(entity.getFirstRemark(), 4000));
                }
            }
            boolean flag = false;
            if (!entity.getIsFromCalender()) {
                ApplicationLogger.logger.debug("Verifying duplicate entity at save { Module: {}; }", SUB_MODULE);
                flag = duplicateVerifyDomainAtSave(entity);
            }
            Customers customers;
            //StaffUser ticketForStaffUser = new StaffUser();
            String ticketCreatedFrom = null;
            if (!flag) {
//                if (entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC")) {
//                    customers = customersService.getcustForCwsc(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                } else if (entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("EMAIL")) {
//                    customers = customersService.getcustForEmail(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                } else {
//                    customers = customersService.get(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                }

//                if (entity.getCreatedFrom() != null && ((entity.getCreatedFrom().equalsIgnoreCase("CWSC") || entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC") || entity.getCreatedFrom().equalsIgnoreCase("EMAIL")))) {
//                    List<GrantedAuthority> role_name = new ArrayList<>();
//                    List<Long> buid = new ArrayList<>();
//                    buid.add(customers.getBuId());
//                    role_name.add(new SimpleGrantedAuthority("ADMIN"));
//                    String mvnoName = null;
//                    if (customers.getMvnoId() != null) {
//                        mvnoName = mvnoRepository.findMvnoNameById(Long.valueOf(customers.getMvnoId()));
//                    }
//                    LoggedInUser user = new LoggedInUser(customers.getUsername(), customers.getPassword(), true, true, true, true, role_name, customers.getFirstname(), customers.getLastname(), LocalDateTime.now(), customers.getId(), customers.getParnterId(), "ADMIN", customers.getServiceAreaId().longValue(), customers.getMvnoId(), null, customers.getId(), buid, false, new ArrayList<String>(), new ArrayList<Long>(), mvnoName);
//                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }


                //Open complain communication

//               CommunicationHelper communicationHelper1 = new CommunicationHelper();
//               Map<String, String> map1 = new HashMap<>();
//               map1.put(CommunicationConstant.USERNAME, customers.getUsername());
//               map1.put(CommunicationConstant.COMPLAIN_NO, entity.getCaseNumber());
//               map1.put(CommunicationConstant.DESTINATION, customers.getMobile());
//               map1.put(CommunicationConstant.EMAIL, customers.getEmail());
//               communicationHelper1.generateCommunicationDetails(CommunicationConstant.OPEN_COMPLAINT, Collections.singletonList(map1));

                //ticketForStaffUser = staffUserService.get(entity.getStaffId());
                CaseCategory caseCategory = new CaseCategory();
                CaseSubCategory caseSubCategory = new CaseSubCategory();
                if (entity.getIsFromCalender() == true) {
                    ApplicationLogger.logger.debug("setting default category { entity created From Calender: {}; Module : {};  }",entity.getIsFromCalender(),SUB_MODULE);
                    caseCategory = caseCategoryRepository.findByIsDefaultCaseCategoryTrueAndMvnoId(entity.getMvnoId());
                    caseSubCategory = caseSubCategoryRepository.findByIsDefaultCaseSubCategoryTrueAndMvnoId(entity.getMvnoId());
                    if(Objects.nonNull(caseCategory)){
                        entity.setCaseCategoryId(caseCategory.getCategoryId());
                    }else{
                        ApplicationLogger.logger.error("Default category not found for mvno id: {}; Module : {};", entity.getMvnoId(),SUB_MODULE);
                    }
                    //entity.setIsFromCalender(true);
                } else if (entity.getIsFromCalender() == false) {
                    ApplicationLogger.logger.debug("Setting category for non-calendar case { Module: {}; CategoryId: {}; SubCategoryId: {} }",SUB_MODULE, entity.getCaseCategoryId(), entity.getCaseSubCategoryId());
                    caseCategory = caseCategoryRepository.findById(entity.getCaseCategoryId().longValue()).orElse(null);
                    caseSubCategory = caseSubCategoryRepository.findById(entity.getCaseSubCategoryId()).orElse(null);
                    //entity.setIsFromCalender(false);
                }
                //TicketReasonCategory ticketReasonCategory = ticketReasonCategoryService.getRepository().findById(entity.getTicketReasonCategoryId()).orElse(null);
                //TicketReasonCategoryTATMapping ticketReasonCategoryTATMapping = ticketReasonCategory.getTicketReasonCategoryTATMappingList().stream().sorted(Comparator.comparing(TicketReasonCategoryTATMapping::getOrderNumber)).collect(Collectors.toList()).get(0);

                if (caseCategory != null && caseSubCategory != null) {
                    entity.setCaseCategoryName(caseCategory.getCategoryName());
                    entity.setCaseSubCategoryName(caseSubCategory.getSubCategoryName());
                    entity.setCaseSubCategoryId(caseSubCategory.getSubCategoryId());
                }

                // check and set priority
                if (!Objects.equals(entity.getPriority(), "High")) {
                    ApplicationLogger.logger.debug("Checking ticket priority { Module: {}; }", SUB_MODULE);
                    entity.setPriority(checkTickePriority(entity, entity.getTeamId()));
                }
                //if (ticketReasonCategoryTATMapping != null) {
                // entity.setTatMappingId(ticketReasonCategoryTATMapping.getMappingId());

                //Set Prefix
                String prefix = "";
                if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_ISSUE))
                    prefix = CaseConstants.PREFIX_TKT;
                else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_REQUEST))
                    prefix = CaseConstants.PREFIX_REQ;
                else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_INQUIRY))
                    prefix = CaseConstants.PREFIX_INQ;
                if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_TASK))
                    prefix = CaseConstants.PREFIX_TASK;


                //Set CaseNumber
                CaseDTO caseDTO = getCaseByCaseType(entity.getCaseType(), entity);
                if (null != caseDTO && caseDTO.getCaseNumber() != null) {
                    String number = caseDTO.getCaseNumber().split("-")[1];
                    entity.setCaseNumber(prefix + "-" + Integer.parseInt(String.valueOf(Long.parseLong(number) + 1)));
                } else entity.setCaseNumber(prefix + "-" + "1");

//                        if (entity.getNextFollowupDate() != null && entity.getNextFollowupTime() != null) {
//                            entity.setNextFollowupDate(entity.getNextFollowupDate());
//                            entity.setNextFollowupTime(entity.getNextFollowupTime());
//                            //send message frome here
//                            if (entity.getCreatedFrom() == null) {
//                                StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//
//                            }
//                        } else {
//                            switch (ticketReasonCategoryTATMapping.getTimeUnit()) {
//                                case "Day":
//                                    entity.setNextFollowupDate(LocalDate.now().plusDays(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                    entity.setNextFollowupTime(LocalTime.now());
//                                    break;
//                                case "Hour":
//                                    entity.setNextFollowupDate(LocalDate.now());
//                                    entity.setNextFollowupTime(LocalTime.now().plusHours(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                    break;
//                                case "Min":
//                                    entity.setNextFollowupDate(LocalDate.now());
//                                    entity.setNextFollowupTime(LocalTime.now().plusMinutes(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                    break;
//                            }
//                        }

                PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();

                if (null != caseDTO && file != null && file.size() > 0) {
                    for (MultipartFile multipartFile : file) {
                        CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
//                String subFolderName = aCase.getCaseNumber().trim().replace("-","_") + "/";
                        //       PATH = "D:/";
                        String path = PATH;
                        caseDoc.setTicketId(Math.toIntExact(caseDTO.getCaseId()));
                        caseDoc.setDocStatus("Active");
                        MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
                        if (file1 != null) {
                            caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
                            caseDoc.setFilename(file1.getOriginalFilename());
                            caseDoc = caseDocDetailsService.saveEntity(caseDoc);
                        }
                    }

                    ApplicationLogger.logger.debug(LogConstants.REQUEST_FOR + " create case entity " + " , {processing attached files }" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " Sub-Module : "+ SUB_MODULE);
                }

                //Set helperName
                if (null != caseDTO && caseDTO.getHelperName() != null) {
                    if (caseDTO.getHelperName().equals("")) {
                        entity.setHelperName(caseDTO.getHelperName());
                    }
                }
                entity.setCase_order(1L);
                entity = super.saveEntity(entity);
                ApplicationLogger.logger.info("saved case entity { Module: {}; CaseNumber: {} }", SUB_MODULE, entity.getCaseNumber());


                CaseUpdateDTO caseUpdateDTO = setFirstRemarkInUpdate(entity);
                StaffUser assignedUser;
                Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();


                //getting lcoId from the customer
//                           if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
//                               customers.setLcoId(getLoggedInUser().getPartnerId());
//                           } else {
//                               customers.setLcoId(null);
//                           }
                //if(caseUpdateDTO.getTeamHierarchyMappingId() == null /*&& customers.getLcoId()==null*/){
//                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                            //Make RabbitMq Call For the below service
//                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                                //   updateTicketLevel(entity,map,null);
//                                caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                                StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                                caseUpdateDTO.setAssignee(staffUser.getId());
//                                assignedUser = staffUser;
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                                TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                                Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                                String nextFollowupDate = entity.getNextFollowupDate().toString();
//                                String nextFollowupTime = entity.getNextFollowupTime().toString();
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), teams.getName(), nextFollowupDate, customers.getUsername(), nextFollowupTime, entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                                if (assignedUser.getParentStaffId() != null && !CollectionUtils.isEmpty(map)) {
//                                    tatUtils.saveOrUpdateTicketTatMatrix(entity, map, assignedUser, false);
//                                }
//                                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + entity.getCustomerName() + " '";
////                                       Long buId = null;
////                                       if(staffUser!=null){
////                                           if(!staffUser.getBusinessUnitNameList().isEmpty()){
////                                               buId  = staffUser.getBusinessUnitNameList().get(0).getId();
////                                           }
////                                       }
//                                if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                    hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), entity.getMvnoId(), staffUser.getFullName(), action, entity.getBuId());
//                                }
//
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                            } else {
//                                StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//                                caseUpdateDTO.setAssignee(getLoggedInUserId());
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
////                                    caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//
//                            }
//                        }
//                        else if(clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("FALSE"))
//                        {
//                            Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                            if (map.containsKey("assignableStaff")) {
//                                //updateTicketLevel(entity,null,map);
//                                List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//                                //caseUpdateDTO.setCase_order(caseUpdateDTO.getCase_order()+1);
//                                caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
//                                for (int i = 0; i < staffUserPojos.size(); i++) {
//                                    TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                    ticketAssignStaffMapping.setStaffId(staffUserPojos.get(i).getId());
//                                    ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                    ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                    workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojos.get(i).getId(), staffUserPojos.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojos.get(i).getUsername());
//                                    if (staffUserPojos.get(i).getParentStaffId() != null) {
//                                        staffByParentStaffId.put(staffUserPojos.get(i).getParentStaffId(), staffUserPojos.get(i));
//                                    }
//                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                    if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                        hierarchyService.sendWorkflowAssignActionMessage(staffUserPojos.get(i).getCountryCode(), staffUserPojos.get(i).getPhone(), staffUserPojos.get(i).getEmail(), entity.getMvnoId(), staffUserPojos.get(i).getFullName(), action, entity.getBuId());
//                                    }
//                                }
//                            }
//                            else {
//                                StaffUser staffUserPojo;
//                                if (ticketCreatedFrom != null && !ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                    staffUserPojo = staffUserService.get(customers.getCreatedById());
//                                } else if (ticketCreatedFrom != null && ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                    staffUserPojo = staffUserService.get(customers.getCreatedById());
//                                } else {
//                                    staffUserPojo = staffUserService.get(getLoggedInUserId());
//                                }
//                                TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
//                                ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
//                                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                Long buId = null;
//
//                                if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                    hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), entity.getMvnoId(), staffUserPojo.getFullName(), action, entity.getBuId());
//                                }
//                            }
//                            caseUpdateDTO.setAssignee(null);
//                            caseUpdateDTO.setStatus(entity.getCaseStatus());
//                            if (Objects.isNull(ticketCreatedFrom)) {
//                                StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUser.getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            } else if (ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            } else if (ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            }
//                            //caseUpdateService.sendCreateTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(),caseDTO.getCaseNumber());
//                        }
                /* Task Management Approval Assignment Code */
                //else{


                if (entity.getTeamId() != null && entity.getCurrentAssigneeId() == null) {
                    ApplicationLogger.logger.debug("Assigning case to team members { Module: {}; TeamId: {} }", SUB_MODULE, entity.getTeamId());
                    List<Integer> staffIdList = teamsService.getStaffIdListFromTeams(entity.getTeamId());
                    List<StaffUser> staffUserList = staffUserRepository.findAllById(staffIdList);
                    Teams teams = teamsRepository.getOne(entity.getTeamId().longValue());

                    if (staffUserList != null) {
                        for (int i = 0; i < staffIdList.size(); i++) {
                            TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
                            ticketAssignStaffMapping.setStaffId(staffUserList.get(i).getId());
                            ticketAssignStaffMapping.setTicketId(entity.getCaseId());
                            ApplicationLogger.logger.info(LogConstants.REQUEST_FOR + " create case entity " + " , {setting task Assignee Staff }" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() +" Sub-Module : "+ SUB_MODULE);
                            ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
                            //Add Audit here
                            if (staffUserList.get(i).getParentStaffId() != null) {
                                staffByParentStaffId.put(staffUserList.get(i).getParentStaffId(), staffUserMapper.domainToDTO(staffUserList.get(i), new CycleAvoidingMappingContext()));
                            }
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with Task Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffUserList.get(i).getUsername() + " '";
                            if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
                                for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
                                    StaffUser staffUsers = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
                                    if (!entity.getIsFromCalender()) {
                                        tatUtils.changeTicketTatAssignee(entity, staffUsers, true, false);
                                    }
                                }
                            }
                            if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                                //change this message to staff
                                ApplicationLogger.logger.debug("Sending workflow assign action message { Module: {}; StaffId: {}; Action: {} }", SUB_MODULE, staffUserList.get(i).getId(), action);
                                hierarchyService.sendWorkflowAssignActionMessage(staffUserList.get(i).getCountryCode(), staffUserList.get(i).getPhone(), staffUserList.get(i).getEmail(), entity.getMvnoId(), staffUserList.get(i).getFullName(), action, entity.getBuId());
                            }
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserList.get(i).getId(), staffUserList.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Member :- " + staffUserList.get(i).getUsername() + "of the Team:- " + teams.getName());
                            ApplicationLogger.logger.info("saved workflow audit { Module: {}; CaseId: {}; StaffId: {}; Action: ASSIGNED }", SUB_MODULE, entity.getCaseId(), staffUserList.get(i).getId());

                        }
                    }
                } else if (entity.getTeamId() != null && entity.getCurrentAssigneeId() != null) {
                    ApplicationLogger.logger.debug("Assigning case to specific staff member { Module: {}; TeamId: {}; CurrentAssigneeId: {} }", SUB_MODULE, entity.getTeamId(), entity.getCurrentAssigneeId());
                    StaffUser staffUserPojo;
//                            if (ticketCreatedFrom != null && !ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                staffUserPojo = staffUserService.get(customers.getCreatedById());
//                            } else if (ticketCreatedFrom != null && ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                staffUserPojo = staffUserService.get(customers.getCreatedById());
//                            } else {
                    staffUserPojo = staffUserService.get(entity.getCurrentAssigneeId());
                    System.out.println(staffUserPojo);
                    //}
                    TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
                    ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
                    ticketAssignStaffMapping.setTicketId(entity.getCaseId());
                    ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
                    ApplicationLogger.logger.info("Saved Ticket Assign Staff Mapping { Module: {}; StaffId: {}; CaseId: {} }", SUB_MODULE, staffUserPojo.getId(), entity.getCaseId());
                    if (staffUserPojo.getParentStaffId() != null) {
                        staffByParentStaffId.put(staffUserPojo.getParentStaffId(), staffUserMapper.domainToDTO(staffUserPojo, new CycleAvoidingMappingContext()));
                    }
                    //Add audit here
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffUserPojo.getUsername() + " '";
                    Long buId = null;
                    //add entry in tatmatrixworkflowdetauils
                    if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty() && entity.getIsFromCalender().equals(false)) {
                        for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
                            StaffUser staffUsers = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
                            if (!entity.getIsFromCalender()) {
                                tatUtils.changeTicketTatAssignee(entity, staffUsers, false, true);
                            }
                        }
                    }
                    if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                        ApplicationLogger.logger.debug("Sending workflow assign action message { Module: {}; StaffId: {}; Action: {} }", SUB_MODULE, staffUserPojo.getId(), action);
                        hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), entity.getMvnoId(), staffUserPojo.getFullName(), action, entity.getBuId());
                    }
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Staff :- " + staffUserPojo.getUsername());
                    ApplicationLogger.logger.info("Saved workflow audit { Module: {}; CaseId: {}; StaffId: {}; Action: ASSIGNED }", SUB_MODULE, entity.getCaseId(), staffUserPojo.getId());

                }
                caseUpdateDTO.setAssignee(null);
                caseUpdateDTO.setStatus(entity.getCaseStatus());



//                if (Objects.isNull(ticketCreatedFrom)) {
//                    StaffUser staffUsers = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//                    //Send message to created staff that task is created successfully
//                    caseUpdateService.sendAssignTicketMessege(staffUsers.getUsername(), staffUsers.getPhone(), staffUsers.getEmail(), staffUsers.getMvnoId(), entity.getCaseNumber(), ticketForStaffUser.getFullName(), entity.getNextFollowupDate().toString(), staffUsers.getUsername(), entity.getNextFollowupTime().toString(), entity.getStaffAdditionalEmail(), entity.getSerialNumber(), null);
//                }
//                        else if (ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
//                            caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                        } else if (ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                            caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                        }

                // }
                //  }
//                           else{
//                               StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//                               caseUpdateDTO.setAssignee(getLoggedInUserId());
//                               caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                               TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                               ticketAssignStaffMapping.setStaffId(getLoggedInUserId());
//                               ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                               ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                               caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString());
//                               //workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                           }

                ApplicationLogger.logger.debug("Getting TicketTatMatrix(TAT) for case { Module: {}; CaseId: {} }", SUB_MODULE, entity.getCaseId());
                TicketTatMatrix tatMatrix = getTicketTatMatrixFromSubReasonId(entity);
                if (tatMatrix != null) {
                    ApplicationLogger.logger.debug("TAT found for case { Module: {}; CaseId: {}; TatMatrixId: {} }", SUB_MODULE, entity.getCaseId(), tatMatrix.getId());
                    if (entity.getPriority().equalsIgnoreCase("high")) {
                        ApplicationLogger.logger.debug("Setting high priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUB_MODULE, entity.getCaseId(), tatMatrix.getSunitp1(), tatMatrix.getSlaTimep1());
                        caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp1());
                        caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep1()));
                    } else if (entity.getPriority().equalsIgnoreCase("medium")) {
                        ApplicationLogger.logger.debug("Setting medium priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUB_MODULE, entity.getCaseId(), tatMatrix.getSunitp2(), tatMatrix.getSlaTimep2());
                        caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp2());
                        caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep2()));
                    } else {
                        ApplicationLogger.logger.debug("Setting low priority SLA { Module: {}; CaseId: {}; SlaUnit: {}; SlaTime: {} }", SUB_MODULE, entity.getCaseId(), tatMatrix.getSunitp3(), tatMatrix.getSlaTime3());
                        caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp3());
                        caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTime3()));
                    }
                }else {
                    ApplicationLogger.logger.warn("No TAT found for case { Module: {}; CaseId: {} }", SUB_MODULE, entity.getCaseId());
                }

                entity = caseUpdateService.updateEntity(caseUpdateDTO, file, false, null);
                //Send Data to API GateWay

                //CloseTicketCheckMessage message = new CloseTicketCheckMessage(ticketForStaffUser.getId(), entity.getCaseId().intValue(), entity.getCaseNumber(), entity.getCaseStatus());
//                            messageSender.send(message, RMQConstants.QUEUE_SEND_TICKET_DATA_TO_APIGW);
                //kafkaMessageSender.send(new KafkaMessageData(message, CloseTicketCheckMessage.class.getSimpleName()));
                getCaseDataFromStrig(entity);


                //updating followup date and time based on tat which selected based on condition check
                if (!entity.getCaseStatus().equalsIgnoreCase("Follow Up") && Objects.isNull(entity.getCurrentAssigneeId())) {
                    ApplicationLogger.logger.debug("updating follow-up date and time based on TAT and Current Assignee is null { Module : {}; } ", SUB_MODULE);
                    updateFollowUpDateAndTimeForTicketBeforePickedUp(caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                }
                if (!entity.getCaseStatus().equalsIgnoreCase("Follow Up") && Objects.nonNull(entity.getCurrentAssigneeId()) && !entity.getIsFromCalender()) {
                    ApplicationLogger.logger.debug("updating follow-up date and time based on TAT and Current Assignee is available and case is not created from calender { Module : {}; } ", SUB_MODULE);
                    updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                }
//                        else {
//                            caseUpdateService.updateTatAtStatusChangeToFollowUp(null, entity);
//                            if (ticketCreatedFrom == null) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber().toString(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), entity.getBuId());
//                            }
//                        }

                //}
//                    if (!ticketCreatedFrom.isEmpty() && ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
//                        Case aCase = caseRepository.findById(entity.getCaseId()).orElse(null);
//                        aCase.setMvnoId(customers.getMvnoId());
//                        aCase.setBuId(customers.getBuId());
//                        caseRepository.save(aCase);
//                    }


            } else {
                ApplicationLogger.logger.error("Task for this category and sub category is already exist! category : {}; Module : {};",entity.getCaseCategoryId(), SUB_MODULE);
                throw new CustomValidationException(APIConstants.FAIL, "Task for this Category and Sub Category is already Exist! ", null);
                //Save Entry In Assignment Table
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CustomValidationException(APIConstants.FAIL, exception.getMessage(), null);
        }
        StaffUser ticketOwnerStaff = staffUserRepository.getOne(entity.getCreatedById());
        Map<String,String> map = setTimeFormatForNotificationSent(entity);
        if (Objects.nonNull(entity.getCurrentAssigneeId())) {
            Optional<StaffUser> staffUser = staffUserRepository.findById(entity.getCurrentAssigneeId());
            ApplicationLogger.logger.debug("Retrieved staff user: {}; Module ", staffUser.isPresent() ? staffUser.get().getFullName() : "Not found",SUB_MODULE);
            if(Objects.nonNull(entity.getCustomersId())) {
                Customers customers = customerRepository.findById(entity.getCustomersId()).get();
                if (Objects.nonNull(customers)) {
                    ApplicationLogger.logger.debug("Sending create ticket message to customer: {}; Module : {};", customers.getFirstname(),SUB_MODULE);
                    caseUpdateService.sendCreateTicketMessege(customers.getFirstname(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), entity.getCaseStatus(), entity.getPriority(), entity.getFirstRemark(), map.get("startDate"), map.get("endDate"), "Customer",ticketOwnerStaff.getFirstname());
                }
            }
            if (staffUser.isPresent()) {
                ApplicationLogger.logger.debug("Sending create ticket message to staff: {}; Module : {};", staffUser.get().getFullName(), SUB_MODULE);
                caseUpdateService.sendCreateTicketMessege(ticketOwnerStaff.getFirstname(), staffUser.get().getPhone(), staffUser.get().getEmail(), staffUser.get().getMvnoId(), entity.getCaseNumber(), entity.getCaseStatus(), entity.getPriority(), entity.getFirstRemark(), map.get("startDate"), map.get("endDate"),"Staff",ticketOwnerStaff.getFirstname());
                //caseUpdateService.sendCreateTicketMessege(ticketOwnerStaff.getFirstname(), staffUser.get().getPhone(), staffUser.get().getEmail(), staffUser.get().getMvnoId(), entity.getCaseNumber(), entity.getCaseStatus(), entity.getPriority(), entity.getFirstRemark(), map.get("startDate"), map.get("endDate"),"Staff");
            }
        }
        ApplicationLogger.logger.debug("Sending create ticket message to ticket owner staff: {}", ticketOwnerStaff.getFullName());
        caseUpdateService.sendCreateTicketMessege(ticketOwnerStaff.getFullName(),ticketOwnerStaff.getPhone(),ticketOwnerStaff.getEmail(),ticketOwnerStaff.getMvnoId(),entity.getCaseNumber(),entity.getCaseStatus(),entity.getPriority(),entity.getFirstRemark(), map.get("startDate"),map.get("endDate"),"Staff",ticketOwnerStaff.getFirstname());

        //caseUpdateService.sendCreateTicketMessege(ticketOwnerStaff.getFullName(),ticketOwnerStaff.getPhone(),ticketOwnerStaff.getEmail(),ticketOwnerStaff.getMvnoId(),entity.getCaseNumber(),entity.getCaseStatus(),entity.getPriority(),entity.getFirstRemark(), map.get("startDate"),map.get("endDate"),"Staff");
        return entity;
    }

    public void updateTicketLevel(CaseDTO entity, Map<String, Object> map) {
        String SUB_MODULE = getModuleNameForLog() + " [updateTicketLevel] ";
        ApplicationLogger.logger.debug("Starting update Ticket Level for Entity Case ID: {}; Module : {} ", entity.getCaseId(), SUB_MODULE);
        QCase qCase = QCase.case$;
        //boolean flag=false;
        HashMap<String, String> convertedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            // Add the converted value to the new Map
            convertedMap.put(entry.getKey(), entry.getValue().toString());
        }

        StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
        if (staffUser == null) {
            ApplicationLogger.logger.error("StaffUser not found with name : {}; Module : {} ", getLoggedInUser().getFirstName(),SUB_MODULE);
            throw new CustomValidationException(APIConstants.FAIL, "Invalid User !No staff user found for logged-in userID", null);
        }
        int lastDayscount = 0;
        if (getMvnoIdFromCurrentStaff() == 1) {
            lastDayscount = Integer.parseInt(clientService.getByNameAndMvnoId(CommonConstants.TASK_COUNT, entity.getMvnoId()).getValue());
        } else {
            lastDayscount = Integer.parseInt(clientService.getByName(CommonConstants.TASK_COUNT).getValue());
        }
        ApplicationLogger.logger.debug("Last days count: {}; Module : {} ", lastDayscount,SUB_MODULE);
        BooleanExpression booleanExpression = qCase.isNotNull()
                //.and(qCase.staffUser.id.eq(entity.getStaffId()))
//                .and(qCase.caseCategoryId.eq(entity.getcaseCategoryId()))
                .and(qCase.caseStatus.eq(CaseConstants.TASK_STATUS_DISCARDED).or(qCase.caseStatus.eq(CaseConstants.TASK_STATUS_DONE)));
        List<Case> caseList = IterableUtils.toList(caseRepository.findAll(booleanExpression));
        ApplicationLogger.logger.debug("Number of cases found: {}; Module : {};", caseList.size(),SUB_MODULE);
        if (caseList.size() >= lastDayscount) {
            //flag=true;
            if (staffUser.getParentStaffId() != null) {
                if (!convertedMap.isEmpty()) {
                    TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(entity);
                    if (ticketTatMatrix != null) {
                        List<TicketTatMatrixMapping> tatMatrixMappings = ticketTatMatrix.getTatMatrixMappings();
                        Case newcase = caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
                        for (int i = 0; i < tatMatrixMappings.size(); i++) {
                            if (tatMatrixMappings.get(i).getOrderNo() == 2) {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime1()));
                                if (entity.getPriority().equalsIgnoreCase("high")) {
                                    ApplicationLogger.logger.debug("Creating TatMatrix WorkFlow Details for high priority case { Module : {}; }",SUB_MODULE);
                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(), null,
                                            staffUser.getParentStaffId(), LocalDateTime.now(),
                                            String.valueOf(tatMatrixMappings.get(i).getMtime1()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(), Integer.valueOf(convertedMap.get("eventId").toString()),
                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(convertedMap.get("teamId")), true);
                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                                } else if (entity.getPriority().equalsIgnoreCase("medium")) {
                                    ApplicationLogger.logger.debug("Creating TatMatrix WorkFlow Details for medium priority case { Module : {}; }", SUB_MODULE);
                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(), null,
                                            staffUser.getParentStaffId(), LocalDateTime.now(),
                                            String.valueOf(tatMatrixMappings.get(i).getMtime2()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(), Integer.valueOf(convertedMap.get("eventId")),
                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(convertedMap.get("teamId")), true);
                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                                } else {
                                    ApplicationLogger.logger.debug("Creating Tat Matrix WorkFlow Details for low priority case { Module : {}; }",SUB_MODULE);
                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(), null,
                                            staffUser.getParentStaffId(), LocalDateTime.now(),
                                            String.valueOf(tatMatrixMappings.get(i).getMtime3()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(), Integer.valueOf(convertedMap.get("eventId")),
                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(convertedMap.get("teamId")), true);
                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                                }
                            }
                        }
                    }else{
                        ApplicationLogger.logger.warn("No Ticket Tat Matrix found , Entity Case ID: {}; Module : {};", entity.getCaseId(),SUB_MODULE);
                    }
//                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
//                        new TatMatrixWorkFlowDetails(new Long(1), "Level 2", getLoggedInUserId(),
//                                Long.valueOf(map.get("workFlowId")), null,
//                                staffUser.getStaffUserparent().getId(), LocalDateTime.now(),
//                                String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), masterTicketTat.getTatMatrixMappings().get(0).getAction(), true, null,
//                                entity.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
//                                CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")),true);
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                }else{
                    ApplicationLogger.logger.warn("convertedMap , Entity Case ID: {}; Module : {};", entity.getCaseId(),SUB_MODULE);
                }
            }else{
                ApplicationLogger.logger.warn("No parent staff ID found for staff user, { Module : {}; } ",SUB_MODULE);
            }

        }else{
            ApplicationLogger.logger.debug("case list size is less than last Days count { Module : {}; }",SUB_MODULE);
            //flag=false;
        }

    }

    public boolean duplicateVerifyDomainAtSave(CaseDTO entity) {
        String SUB_MODULE = getModuleNameForLog() + " [duplicateVerifyDomainAtSave()] ";
        ApplicationLogger.logger.debug("checking duplicate verify domain at save { Module : {}; }",SUB_MODULE);
        QCase qCase = QCase.case$;
        boolean flag = false;
        int ticketCreationCount = 0;
        BooleanExpression booleanExpression = qCase.isNotNull()
                //.and(qCase.staffUser.id.eq(entity.getStaffId()))
//                .and(qCase.caseCategoryId.eq(entity.getcaseCategoryId()))
                .and(qCase.caseStatus.notEqualsIgnoreCase(CaseConstants.TASK_STATUS_DISCARDED)).and(qCase.caseStatus.notEqualsIgnoreCase(CaseConstants.TASK_STATUS_DONE))
                .and(qCase.caseType.equalsIgnoreCase(entity.getCaseType()));
        List<Case> caseList = IterableUtils.toList(caseRepository.findAll(booleanExpression));
        if (caseList.size() > 0) {
            ticketCreationCount = Integer.parseInt(clientService.getByNameAndMvnoId(CommonConstants.TASK_COUNT, entity.getMvnoId()).getValue());
        }
        if (caseList.size() == ticketCreationCount) {
            flag = true;
        }
        if (ticketCreationCount == 0) {
            flag = false;
        }
        ApplicationLogger.logger.debug("checking done for duplicate verify domain at save with flag : {}; Module : {}; ",flag,SUB_MODULE);
        return flag;
    }


    public CaseDTO getCaseByCaseType(String caseType, CaseDTO entity) {
        String SUB_MODULE = getModuleNameForLog() + " [getCaseByCaseType()] ";
        ApplicationLogger.logger.debug(" Starting get Case By Case Type { Module : {}; }",SUB_MODULE);
        QCase qCase = QCase.case$;
        BooleanExpression booleanExpression = null;
        if (entity.getBuId() != null) {
            booleanExpression = qCase.isNotNull().and(qCase.caseType.equalsIgnoreCase(caseType)).and(qCase.mvnoId.eq(entity.getMvnoId())).and(qCase.buId.eq(Long.valueOf(entity.getBuId())));
        } else {
            booleanExpression = qCase.isNotNull().and(qCase.caseType.equalsIgnoreCase(caseType)).and(qCase.mvnoId.eq(entity.getMvnoId()));
        }
        JPAQuery<Case> caseJPAQuery = new JPAQuery<>(entityManager);
        List<Case> caseList = caseJPAQuery.select(qCase).from(qCase).where(booleanExpression).limit(1).orderBy(qCase.caseId.desc()).fetch();
        if (null != caseList && 0 < caseList.size()) {
            ApplicationLogger.logger.debug("fetched all Cases by CaseType : {}; Total cases: {}; Module : {}; ",caseType,caseList.size(),SUB_MODULE);
            return caseMapper.domainToDTO(caseList.get(0), new CycleAvoidingMappingContext());
        }else{
            ApplicationLogger.logger.debug("No Cases found by CaseType : {}; Module : {}; ",caseType,SUB_MODULE);
        }
        return null;
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseService]";
    }

    public List<CaseDTO> getAllCaseByStaff(Integer loggedInUser) {
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStaff()]";
        List<Case> caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(loggedInUser);
        if (getMvnoIdFromCurrentStaff() != 1)
            caseList = caseList.stream().filter(cases -> cases.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() && (cases.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(cases.getBuId()))).collect(Collectors.toList());
        if (null != caseList && 0 < caseList.size()) {
            ApplicationLogger.logger.debug("fetched all Cases by Staff : {}; Total cases: {}; Module : {}; ", loggedInUser, caseList.size(), SUBMODULE);
            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }else{
            ApplicationLogger.logger.debug("No Cases found by Staff : {}; Module : {}; ", loggedInUser, SUBMODULE);
        }
        return new ArrayList<>();
    }

    public GenericDataDTO getAllCaseByStaffWithPagination(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStaffWithPagination()]";
        PageRequest pageRequest;
        Page<Case> caseList;
        try {
            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() == 1)
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());


            if (null != caseList && 0 < caseList.getSize()) {
                ApplicationLogger.logger.debug("fetched all Cases by Staff with pagination : {}; Total cases: {}; Module : {}; ", getLoggedInUserId(), caseList.getSize(), SUBMODULE);
                makeGenericResponse(genericDataDTO, caseList);
            }else{
                ApplicationLogger.logger.debug("No Cases found by Staff with pagination : {}; Module : {}; ", getLoggedInUserId(), SUBMODULE);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public List<CaseDTO> getAllCaseByStatus(String caseStatus) {
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStatus()]";
        List<Case> caseList = caseRepository.findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(caseStatus);
        if (null != caseList && 0 < caseList.size()) {
            ApplicationLogger.logger.debug("fetched all Cases by Status : {}; Total cases: {}; Module : {}; ", caseStatus, caseList.size(), SUBMODULE);
            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }else{
            ApplicationLogger.logger.debug("No Cases found by Status : {}; Module : {}; ", caseStatus, SUBMODULE);
        }
        return new ArrayList<>();
    }

    public GenericDataDTO getAllCaseByStatusWithPagination(String caseStatus, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStatusWithPagination()]";
        PageRequest pageRequest;
        Page<Case> caseList;
        try {
            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() == 1)
                caseList = caseRepository.findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(caseStatus, pageRequest);

            else if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());

            if (null != caseList && 0 < caseList.getSize()) {
                ApplicationLogger.logger.debug("fetched all Cases by Status with pagination : {}; Total cases: {}; Module : {}; ", caseStatus, caseList.getSize(), SUBMODULE);
                makeGenericResponse(genericDataDTO, caseList);
            }else{
                ApplicationLogger.logger.debug("No Cases found by Status with pagination : {}; Module : {}; ", caseStatus, SUBMODULE);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getAllCaseByStatusAndMyCasesWithPagination(String caseStatus, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStatusAndMyCasesWithPagination()]";
        PageRequest pageRequest;
        Page<Case> caseList;
        try {
            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            caseList = caseRepository.findAllByCurrentAssignee_IdAndCaseStatusAndIsDeleteIsFalse(getLoggedInUserId(), caseStatus, pageRequest);

            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(getLoggedInUserId(), pageRequest);
            else
                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());

            caseList.get().forEach(item -> {
                if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getParentStaffId() != null) {
                    item.setParentId(item.getCurrentAssignee().getParentStaffId().longValue());
                }
            });

            if (getMvnoIdFromCurrentStaff() != 1) if (null != caseList && 0 < caseList.getSize()) {
                ApplicationLogger.logger.debug("fetched all Cases by Status and My Cases with pagination : {}; Total cases: {}; Module : {}; ", caseStatus, caseList.getSize(), SUBMODULE);
                makeGenericResponse(genericDataDTO, caseList);
            }else{
                ApplicationLogger.logger.debug("No Cases found by Status and My Cases with pagination : {}; Module : {}; ", caseStatus, SUBMODULE);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error("getting error while fetching cases with case Status :{}; Module : {}; Message : {}; ",caseStatus,SUBMODULE,ex.getMessage());
            throw ex;
        }
        return genericDataDTO;
    }

    public List<CaseDTO> getAllCaseByWorkingStaff(Integer teamId) {
        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByWorkingStaff()]";
        List<Case> caseList = caseRepository.findAllByTeamId(teamId);
        if (getMvnoIdFromCurrentStaff() != 1)
            caseList = caseList.stream().filter(cases -> cases.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() && (cases.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(cases.getBuId()))).collect(Collectors.toList());
        if (null != caseList && 0 < caseList.size()) {
            ApplicationLogger.logger.debug("fetched all Cases by Working StaffID : {}; Total cases: {}; Module : {}; ", teamId, caseList.size(), SUBMODULE);
            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }else{
            ApplicationLogger.logger.debug("No Cases found by Working StaffID : {}; Module : {}; ", teamId, SUBMODULE);
        }
        return new ArrayList<>();
    }

//    public Page<Case> searchCaseBYCaseType(List<GenericSearchModel> filterList, Integer page,
//                                           Integer pageSize, String sortBy, Integer sortOrder
//    ) {
//        String SUBMODULE = MODULE + " [search()] ";
//        Pageable pageable = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
////        StringBuilder commonQuery = new StringBuilder(SubscriberSearchQueryScript.COMMON_QUERY);
//        StringBuilder whereCondition = new StringBuilder("");
//        StringBuilder join = new StringBuilder("");
//        String finalQuery = "";
//
//        QCase qCase = QCase.case$;
//
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
//        if (Objects.nonNull(qCase.serviceAreaName) &&
//                Objects.nonNull(qCase.case "parentCategoryName":
//                        booleanExpression = booleanExpression.and(qTicketReasonSubCategory.parentCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                        break;) &&
//                Objects.nonNull(qCase.caseStatus)) {
//        }
//
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (getLoggedInUserId() != 1) {
//                    List<java.lang.Long> idList = getServiceAreaIdList();
//                    if (!CollectionUtils.isEmpty(idList))
//                        booleanExpression = booleanExpression.and(qCase.caseId.in(idList));
//                }
//                if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                    booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                }
//
//                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                    if (!searchModel.getFilterValue().isEmpty()) {
//                        String s1 = searchModel.getFilterValue();
////                        booleanExpression = booleanExpression
////                                .and(((qCase.serviceAreaName.like("%" + s1 + "%"))
////                                .and(qCase.caseStatus.contains(searchModel.getFilterValue()))
////                                .and(qCase.ticketReasonCategoryId.in(Long.valueOf(searchModel.getFilterValue())))));
//
//                        booleanExpression = booleanExpression
//                                .and(((qCase.customers.servicearea.id.in(Long.valueOf(searchModel.getFilterValue())))
//                                        //   .and(qCase.caseStatus.contains(searchModel.getFilterValue()))
//                                        //  .and(qCase.ticketReasonCategoryId.in(Long.valueOf(searchModel.getFilterValue())))
//                                ));
//
//
//                        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                            booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                            booleanExpression = booleanExpression.or(qCase.partner.name.eq(s1));
//                        }
//                        return caseRepository.findAll(booleanExpression, pageRequest);
//                    }
//                }
//                try {
//                    if (null != searchModel.getFilterCondition()) {
//                        if (searchModel.getFilterCondition().equalsIgnoreCase(SearchConstants.AND)) {
//                            return caseRepository.findAll(generateAndCondition(searchModel, booleanExpression), pageable);
//                        } else {
//                            generateOrCondition(searchModel, whereCondition, join);
//                        }
//                    } else {
//                        return caseRepository.findAll(generateAndCondition(searchModel, booleanExpression), pageable);
//                    }
//                } catch (Exception ex) {
//
//                }
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error("Unable to search  case by type response{}exceeption{}", APIConstants.FAIL, ex.getStackTrace());
//            throw ex;
//        }
//        return null;
//    }


    //search


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
//        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
//        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QCase qCase = QCase.case$;
        QTeams qTeams = QTeams.teams;
        BooleanExpression booleanExpression = QCase.case$.isNotNull().and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.isFromCalender.eq(false));
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TEAM_NAME)) {
//                    booleanExpression = booleanExpression.and(qCase.staffUser.username.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TICKET_STATUS)) {
                    booleanExpression = booleanExpression.and(qCase.caseStatus.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.IS_CALANDER_FALSE)) {
                    booleanExpression = booleanExpression.and(qCase.isFromCalender.eq(Boolean.valueOf(genericSearchModel.getFilterValue().toString())));
                }
                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_PRIORITY)) {
                    booleanExpression = booleanExpression.and(qCase.priority.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.ASSIGNED_TEAM)) {
                    List<Teams> teams = teamsRepository.findAllByNameContainingIgnoreCase(genericSearchModel.getFilterValue());
                    List<StaffUser> staffUsers = new ArrayList<>();
                    if (teams.size() > 0) {
                        staffUsers = teams.stream().flatMap(t -> t.getStaffUser().stream()).collect(Collectors.toList());
                    }
                    booleanExpression = booleanExpression.and(qCase.currentAssignee.in(staffUsers));
                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.CUSTOMER_SERVICE_AREA)) {
//                    booleanExpression = booleanExpression.and(qCase.customers.servicearea.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_NUMBER)) {
                    booleanExpression = booleanExpression.and(qCase.caseNumber.containsIgnoreCase(genericSearchModel.getFilterValue()));
                }
                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_CATEGORY)) {
                    List<CaseCategory> caseCategoryList = caseCategoryRepository.findAllByCategoryNameContainingIgnoreCase(genericSearchModel.getFilterValue());
                    List<Long> longArrayList = new ArrayList<>();
                    if (caseCategoryList.size() > 0) {
                        longArrayList = caseCategoryList.stream().map(t -> t.getCategoryId()).collect(Collectors.toList());
                    }
                    booleanExpression = booleanExpression.and(qCase.caseCategoryId.in(longArrayList));
                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.USER_ID)) {
//                    booleanExpression = booleanExpression.and(qCase.staffUser.id.eq(Integer.valueOf(genericSearchModel.getFilterValue())));
//                }
                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TICKET_LEVEL)) {
                    List<TatMatrixWorkFlowDetails> ldlist = tatMatrixWorkFlowDetailsRepo.findAllByLevelAndIsActive(genericSearchModel.getFilterValue(), true);
                    List<Long> idlist1 = ldlist.stream().map(TatMatrixWorkFlowDetails::getEntityId).map(integer -> integer.longValue()).collect(Collectors.toList());
                    booleanExpression = booleanExpression.and(qCase.caseId.in((idlist1)));
                }
                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.RESPONSE_TIME_BREACH)) {
                    List<Long> caselist = tatMatrixWorkFlowDetailsRepo.getAlltatBreachdetails();
                    booleanExpression = booleanExpression.and(qCase.caseId.in(caselist));
                }

                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TAT_BREATCH)) {
                    BooleanExpression booleanExpression1 = qCase.isNotNull().and(qCase.caseStatus.notEqualsIgnoreCase("Closed"));
                    List<Case> caseList = IterableUtils.toList(caseRepository.findAll(booleanExpression1));
                    List<Long> expiredlist = new ArrayList<>();
                    List<Long> caseidlist = caseList.stream().map(caselist2 -> caselist2.getCaseId()).collect(Collectors.toList());
                    if (caseidlist.size() > 0) {
                        for (Case idlist : caseList) {
                            //TicketReasonSubCategory ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(idlist.getCaseId()).orElse(null);
                            CaseCategory caseCategory = caseCategoryRepository.findById(idlist.getCaseId()).orElse(null);
                            if (caseCategory != null) {
                                caseCategory.getCaseCategoryTatMappingList();
                                if (!caseCategory.getCaseCategoryTatMappingList().isEmpty()) {
                                    List<CaseCategoryTatMapping> caseCategoryTatMappings = caseCategory.getCaseCategoryTatMappingList();
                                    for (CaseCategoryTatMapping caseCategoryTatMapping : caseCategoryTatMappings) {
                                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(caseCategoryTatMapping.getId().intValue()));
                                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                                            //If query not matched then skip
                                            if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseMapper.domainToDTO(idlist, new CycleAvoidingMappingContext())))
                                                continue;
                                        }
                                        TicketTatMatrix masterTicketTat = caseCategoryTatMapping.getTicketTatMatrix();
                                        Long rtime = masterTicketTat.getRtime();
                                        String runit = masterTicketTat.getRunit();
                                        if (!idlist.getCaseStatus().equalsIgnoreCase("Closed")) {
                                            if (runit.equalsIgnoreCase("Day")) {
                                                if ((idlist.getCreatedate().plusDays(rtime).compareTo(LocalDateTime.now())) > 0) {
                                                    expiredlist.add(idlist.getCaseId());
                                                }
                                            } else if (runit.equalsIgnoreCase("Hour")) {
                                                if ((idlist.getNextFollowupTime().plusHours(rtime).compareTo(LocalTime.now())) > 0) {
                                                    expiredlist.add(idlist.getCaseId());
                                                }
                                            } else {
                                                if ((idlist.getNextFollowupTime().plusMinutes(rtime).compareTo(LocalTime.now())) > 0) {
                                                    expiredlist.add(idlist.getCaseId());
                                                }
                                            }
                                        }
                                    }

                                }
                            }


                        }

                    }
                    booleanExpression = booleanExpression.and(qCase.caseId.in(expiredlist));

                }
                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.MY_TICKETS)) {
                    booleanExpression = booleanExpression.and(qCase.createdById.eq(getLoggedInUserId()));
                }
            }
        }
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCase.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if (getLoggedInUser().getLco())
            booleanExpression = booleanExpression.and(qCase.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression = booleanExpression.and(qCase.lcoId.isNull());

        return makeGenericResponse(genericDataDTO, caseRepository.findAll(booleanExpression, pageRequest));
    }

//  public void getserviceAreaByCust(List<Case> caseList){
//    }

    private BooleanExpression generateAndCondition(GenericSearchModel searchModel, BooleanExpression booleanExpression) {
        return booleanExpression;
    }

    public CaseDTO assignedTo(Long caseId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [assignedTo()] ";
        try {
            CaseDTO caseDTO = getEntityForUpdateAndDelete(caseId);
            if (null != caseDTO) {
                StaffUser staffUser = staffUserService.get(getLoggedInUserId());
                if (null != staffUser) {
                    //Update Case
                    caseDTO.setCurrentAssigneeId(staffUser.getId());
                    caseDTO.setCaseStatus(CaseConstants.STATUS_ASSIGNED);

                    //Initialize CaseUpdate
                    CaseUpdateDTO caseUpdate = new CaseUpdateDTO();
                    List<CaseUpdateDetailsDTO> updateDetailsList = new ArrayList<>();

                    //Set UpdateDetails For Assignee
                    ApplicationLogger.logger.debug("setting update details for assignee Module : {};",SUBMODULE);
                    CaseUpdateDetailsDTO assigneeDetails = new CaseUpdateDetailsDTO();
                    assigneeDetails.setOperation(CaseConstants.OPERATION_CHANGE_ASSIGNEE);
                    assigneeDetails.setNewvalue(staffUser.getFirstname() + " " + staffUser.getLastname());
                    assigneeDetails.setEntitytype(CaseConstants.ENTITY_ASSIGNEE);
                    assigneeDetails.setCaseUpdate(caseUpdate);

                    //Set UpdateDetails For Status
                    ApplicationLogger.logger.debug("setting update details for status Module : {};",SUBMODULE);
                    CaseUpdateDetailsDTO changeStatus = new CaseUpdateDetailsDTO();
                    changeStatus.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
                    changeStatus.setNewvalue(CaseConstants.STATUS_ASSIGNED);
                    changeStatus.setEntitytype(CaseConstants.ENTITY_STATUS);
                    changeStatus.setCaseUpdate(caseUpdate);

                    updateDetailsList.add(assigneeDetails);
                    updateDetailsList.add(changeStatus);

                    caseUpdate.setTicketId(caseDTO.getCaseId());
                    caseUpdate.setCreateby(staffUser.getFullName());
                    caseUpdate.setUpdateby(staffUser.getFullName());
                    caseUpdate.setUpdateDetails(updateDetailsList);
                    caseDTO.getCaseUpdateList().add(caseUpdate);

                    CaseDTO updatedDTO = updateEntity(caseDTO);
                    ApplicationLogger.logger.info("saving case assigned to updated dto; Module: {} ", SUBMODULE);
                    assignmentService.saveEntity(new CaseAssignmentDTO(updatedDTO.getCaseId(), staffUser.getId(), LocalDate.now()));
                    return updatedDTO;
                } else {
                    ApplicationLogger.logger.error("Assignee Not Found! with name : {}; Module : {}",getLoggedInUser().getFirstName(),SUBMODULE);
                    throw new DataNotFoundException("Assignee Not Found!");
                }
            } else throw new DataNotFoundException("Case Not Found!");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;

        try {
            if (sortBy == null || "".equals(sortBy) || "id".equals(sortBy)) {
                sortBy = "caseId";
            }
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            QCase qCase = QCase.case$;
            BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
            booleanExpression=booleanExpression.and(qCase.isFromCalender.eq(false));

            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
            }


            if (getLoggedInUser().getLco())
                booleanExpression = booleanExpression.and(qCase.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression = booleanExpression.and(qCase.lcoId.isNull());

//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//                paginationList = setMvnoName(paginationList);
//
//            } else {
//                booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//                paginationList = setMvnoName(paginationList);
//            }

            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
            }
            Page<CaseListDTO> paginationList = caseRepository.findCaseList(booleanExpression, pageRequest);
//            paginationList.get().forEach(item -> {
//                if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getParentStaffId() != null) {
//                    item.setParentId(item.getCurrentAssignee().getParentStaffId().longValue());
//                }
//            });


            if (paginationList != null && !paginationList.isEmpty()) {
                ApplicationLogger.logger.debug("successfully retrieved list by pagination { Module : {}; }",SUBMODULE);
//                makeGenericResponse(genericDataDTO, paginationList);
                genericDataDTO.setDataList(paginationList.getContent());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(paginationList.getTotalElements());
                genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
                genericDataDTO.setTotalPages(paginationList.getTotalPages());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Cases");
        createExcel(workbook, sheet, CaseDTO.class, getFields());
    }

    public void excelGenerateForMyCases(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("MyCases");
        new ExcelUtil<CaseDTO>().generateExcel(workbook, sheet, CaseDTO.class, getAllCaseByStaff(getLoggedInUserId()), getFields());
    }

    public void pdfGenerateForMyCases(Document doc) throws Exception {
        new PdfUtil<CaseDTO>().generatePdf(doc, CaseDTO.class, getAllCaseByStaff(getLoggedInUserId()), getFields());
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, CaseDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{CaseDTO.class.getDeclaredField("caseId"), CaseDTO.class.getDeclaredField("caseNumber"), CaseDTO.class.getDeclaredField("caseStartedOnString"), CaseDTO.class.getDeclaredField("caseFor"), CaseDTO.class.getDeclaredField("caseStatus"), CaseDTO.class.getDeclaredField("caseTitle"), CaseDTO.class.getDeclaredField("caseType"), CaseDTO.class.getDeclaredField("caseOrigin"), CaseDTO.class.getDeclaredField("priority"), CaseDTO.class.getDeclaredField("userName"), CaseDTO.class.getDeclaredField("customerName"), CaseDTO.class.getDeclaredField("mobile"), CaseDTO.class.getDeclaredField("currentAssigneeName"), CaseDTO.class.getDeclaredField("partnerName")};
    }

    /* This method is not used anywhere hence commented */
//       @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Pageable pageable = generatePageRequest(page, pageSize, "caseId", sortOrder);
//        StringBuilder commonQuery = new StringBuilder(CaseSearchQueryScript.COMMON_QUERY);
//        StringBuilder whereCondition = new StringBuilder("");
//        StringBuilder join = new StringBuilder("");
//        String finalQuery = "";
//        try {
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        QCase qCase = QCase.case$;
//                        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
//                        BooleanExpression booleanExpression1 = qCase.isNotNull().and(qCase.isDelete.eq(false));
//                        if (getLoggedInUserId() != 1) {
//                            booleanExpression = booleanExpression.and(qCase.customers.serviceAreaId.in((Number) getServiceAreaIdList()));
//                            booleanExpression1 = booleanExpression1.and(qCase.customers.serviceAreaId.in((Number) getServiceAreaIdList()));
//
//                        }
//                        if (getMvnoIdFromCurrentStaff() != 1) {
//                            booleanExpression = booleanExpression.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                            booleanExpression1 = booleanExpression1.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                        }
//                        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//                            booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//                            booleanExpression1 = booleanExpression1.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//                        }
//                        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                            booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                            booleanExpression1 = booleanExpression1.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//
//                        }
//                        if (!searchModel.getFilterValue().isEmpty()) {
//                            String searchKey = searchModel.getFilterValue();
//                            booleanExpression = booleanExpression.and((qCase.caseNumber.contains(searchKey).or(qCase.customers.username.contains(searchKey).or(qCase.customers.firstname.contains(searchKey).or(qCase.customers.mobile.contains(searchKey).or(qCase.caseStatus.eq(searchKey)
////                                                                    .or(qCase.currentAssignee.firstname.contains(searchKey)
////                                                                            .or(qCase.currentAssignee.lastname.contains(searchKey)
//                                    .or(qCase.caseOrigin.contains(searchKey).or(qCase.priority.contains(searchKey).or(qCase.caseType.contains(searchKey).or(qCase.caseTitle.contains(searchKey)))))))))));
//                            Page<Case> caseList = caseRepository.findAll(booleanExpression, pageable);
//
//                            if (null != caseList && 0 < caseList.getSize() && !caseList.isEmpty()) {
//                                makeGenericResponse(genericDataDTO, caseList);
//                                return genericDataDTO;
//                            }
//                            booleanExpression1 = booleanExpression1.and(qCase.currentAssignee.firstname.contains(searchKey).or(qCase.currentAssignee.lastname.contains(searchKey)));
//                        }
//                        Page<Case> caseListByAsignee = caseRepository.findAll(booleanExpression1, pageable);
//                        if (null != caseListByAsignee && 0 < caseListByAsignee.getSize()) {
//                            makeGenericResponse(genericDataDTO, caseListByAsignee);
//                        }
//                        return genericDataDTO;
//                    }
////                    if (null != searchModel.getFilterCondition()) {
////                        if (searchModel.getFilterCondition().equalsIgnoreCase(SearchConstants.AND)) {
////                            generateAndCondition(searchModel, whereCondition, join);
////                        } else {
////                            generateOrCondition(searchModel, whereCondition, join);
////                        }
////                    } else {
////                        generateAndCondition(searchModel, whereCondition, join);
////                    }
//                }
////                if (whereCondition.length() > 0) {
////                    commonQuery.append(" " + (join.length() >= 0 ? join : " ") + SubscriberSearchQueryScript.WHERE + whereCondition);
////                    finalQuery = commonQuery.toString();
////                }
////                Query q = entityManager.createNativeQuery(finalQuery, GenericIdModel.class);
////                List<GenericIdModel> resultList = q.getResultList();
////                if (null != resultList && 0 < resultList.size()) {
////                    List<String> idList = new ArrayList<>();
////                    for (GenericIdModel idModel : resultList) {
////                        idList.add(idModel.getId().toString());
////                    }
////                    return makeGenericResponse(genericDataDTO, caseRepository.findAllBy(idList, pageable));
////                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }

    /* This method is not used anywhere hence commented */


//    public GenericDataDTO getCaseByNumberOrTypeOrOriginOrCustomerDetailsByPartner(String s1, Pageable pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getCaseByNumberOrCustomerDetailsByPartner()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Case> caseList = caseRepository.findAllByCaseNumberOrCaseStatusOrCurrentAssignee_FirstnameByPartner(pageRequest
//                    , s1, s1, s1, s1, s1, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(),s1);
//            if (null != caseList && 0 < caseList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }


    public Case getEntityByCaseNumber(String caseNumber) {
        String SUBMODULE = getModuleNameForLog() + " [getEntityByCaseNumber()] ";
        ApplicationLogger.logger.debug("getting entity by case Number : {}; Module : {}; ",SUBMODULE);
        return caseRepository.findByCaseNumber(caseNumber);
    }


    /* This methods  is not used anywhere hence commented */
//    private String generateAndCondition(GenericSearchModel searchModel, StringBuilder whereCondition, StringBuilder join) {
//        if (null != searchModel.getFilterOperator()) {
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.EQUAL_TO)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.NOT_EQUAL)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.CONTAINS)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.STARTS_WITH)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//            }
//        }
//        return whereCondition.toString();
//    }

//    private String generateOrCondition(GenericSearchModel searchModel, StringBuilder whereCondition, StringBuilder join) {
//        if (null != searchModel.getFilterOperator()) {
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.EQUAL_TO)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.NOT_EQUAL)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.CONTAINS)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.STARTS_WITH)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//        }
//        return whereCondition.toString();
//    }

    private CaseUpdateDTO setFirstRemarkInUpdate(CaseDTO entity) {
        String SUBMODULE = getModuleNameForLog() + " [setFirstRemarkInUpdate()] ";
        ApplicationLogger.logger.debug("setting first remark for update entity with { caseID : {}; Module : {}; }",entity.getCaseId(),SUBMODULE);
        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
        caseUpdateDTO.setTicketId(entity.getCaseId());
        if (entity.getFirstRemark() != null) {
            if (entity.getFirstRemark().length() <= 15000) {
                caseUpdateDTO.setRemark(entity.getFirstRemark());
            } else {
                ApplicationLogger.logger.error("Remark message is more then 15000 characters with caseId : {}; Module : {};",entity.getCaseId(),SUBMODULE);
                throw new CustomValidationException(APIConstants.FAIL, "Remark message should be less then 15000 characters", null);
            }
        }else{
            ApplicationLogger.logger.warn("First Remark is null with caseId : {}; Module : {};",entity.getCaseId(),SUBMODULE);
        }
        if(getLoggedInUser()!=null){
            if (null != getLoggedInUser().getRolesList() && 0 < getLoggedInUser().getRolesList().length()) {
                String[] roleArray = getLoggedInUser().getRolesList().split(",");
                if (roleArray.length > 0) {
                    List<String> list = Arrays.stream(roleArray).filter("8"::equalsIgnoreCase).collect(Collectors.toList());
                    if (null != list && 0 < list.size()) {
                        caseUpdateDTO.setCommentBy(CaseConstants.COMMENT_BY_CUSTOMER);
                        ApplicationLogger.logger.debug("CommentBy set to CUSTOMER for caseID: {}; Module : {};", entity.getCaseId(),SUBMODULE);
                    }
                }
            }

        }else{
            StaffUser  staffUser = staffUserRepository.findById(entity.getCreatedById()).orElse(null);

                String[] roleArray = staffUser.getRoles().toArray().toString().split(",");
                if (roleArray.length > 0) {
                    List<String> list = Arrays.stream(roleArray).filter("8"::equalsIgnoreCase).collect(Collectors.toList());
                    if (null != list && 0 < list.size()) {
                        caseUpdateDTO.setCommentBy(CaseConstants.COMMENT_BY_CUSTOMER);
                        ApplicationLogger.logger.debug("CommentBy set to CUSTOMER for caseID: {} based on createdByStaffId : {}; Module : {};", entity.getCaseId(),(staffUser != null)?staffUser.getUsername():"Not found",SUBMODULE);
                    }
                }
        }
        if (caseUpdateDTO.getCommentBy() == null) {
            caseUpdateDTO.setCommentBy(CaseConstants.COMMENT_BY_STAFF);
            ApplicationLogger.logger.debug("CommentBy set to STAFF for caseID: {}; , Module : {}", entity.getCaseId(),SUBMODULE);
        }
        caseUpdateDTO.setRemarkType(CaseConstants.REMARK_TYPE_EXTERNAL);
        ApplicationLogger.logger.debug("First remark update completed for caseID: {}; Module: {}", entity.getCaseId(), SUBMODULE);
        return caseUpdateDTO;
    }

    public Long findMinimumAssignReuqestByStaff(Integer id) {
        String SUBMODULE = getModuleNameForLog() + " [findMinimumAssignReuqestByStaff()] ";
        ApplicationLogger.logger.debug("Fetching minimum assign request for staff with id: {}; Module: {}", id, SUBMODULE);
        return caseRepository.findMinimumAssignReuqestByStaff(id);
    }

    public CaseDTO ticketRating(@Valid CaseFeedbackRel caseFeedbackDTO) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [ticketRating()] ";
        CaseDTO dto = null;
        Optional<Case> caseOptional = caseRepository.findById(caseFeedbackDTO.getTicketid());
        if (caseOptional.isPresent()) {
            Case caseDb = caseOptional.get();
            if (caseDb.getCaseStatus().equalsIgnoreCase("Closed")) {
//                caseDb.setRating(caseFeedbackDTO.getRating());
//                caseDb.setCustomerFeedback(caseFeedbackDTO.getCustomerFeedback());
//                caseDb = caseRepository.save(caseDb);
                caseFeedbackDTO.setCreated_date(LocalDateTime.now());
                ApplicationLogger.logger.info("saving case feedback rating for case : {}; Module : {};",caseDb.getCaseTitle(),SUBMODULE);
                caseFeedbackRelRepository.save(caseFeedbackDTO);
                dto = getMapper().domainToDTO(caseDb, new CycleAvoidingMappingContext());
            } else {
                throw new CustomValidationException(APIConstants.FAIL, "Sorry! You can only rate for closed ticket.", null);
            }
        } else {
            throw new CustomValidationException(APIConstants.FAIL, "Sorry! Ticket is not found for id : " + caseFeedbackDTO.getTicketid(), null);
        }
        return dto;
    }


    private String checkTickePriority(CaseDTO entity, Integer teamId) {
        String SUBMODULE = getModuleNameForLog() + " [checkTickePriority()] ";
        ApplicationLogger.logger.debug("Checking ticket priority for caseId: {}; Case-Title : {}; teamId: {}; Module: {}",  (entity.getCaseId() != null)?entity.getCaseId():"NOT FOUND",entity.getCaseTitle(), teamId, SUBMODULE);
        int lastDayscount = Integer.parseInt(clientService.getByName(CommonConstants.TASK_COUNT).getValue());
        int lastDayscountSameCategory = Integer.parseInt(clientService.getByName(CommonConstants.TASK_COUNT_IN_LAST_DAYS).getValue());
        int lastDaysCountTeam = Integer.parseInt(clientService.getByName(CommonConstants.TASK_COUNT_SAME_CATEGORY).getValue());
        int lastDaysCountSameCategory = Integer.parseInt(clientService.getByName(CommonConstants.TASK_COUNT_SAME_CATEGORY_DAYS).getValue());

        ApplicationLogger.logger.debug("lastDayscount: {}; lastDayscountSameCategory: {}; lastDaysCountTeam: {}; lastDaysCountSameCategory: {}",lastDayscount, lastDayscountSameCategory, lastDaysCountTeam, lastDaysCountSameCategory);

        QCase qCase = QCase.case$;
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.teamId.eq(teamId));
        long ticketCount = caseRepository.count(booleanExpression.and(qCase.createdate.between(LocalDateTime.now().minusDays(lastDaysCountTeam), LocalDateTime.now())));
        ApplicationLogger.logger.debug("Ticket count for teamId {}: {};, Module : {}", teamId, ticketCount,SUBMODULE);
        if (ticketCount >= lastDayscount) {
            ApplicationLogger.logger.debug("Priority set to High for caseId: {}; Case-Title : {}; teamId: {}; Module: {}", (entity.getCaseId() != null)?entity.getCaseId():"NOT FOUND",entity.getCaseTitle(), teamId, SUBMODULE);
            return "High";
        } else {
            booleanExpression = booleanExpression
                    .and(qCase.caseCategoryId.eq(entity.getCaseCategoryId()))
                    .and(qCase.createdate.between(LocalDateTime.now().minusDays(lastDaysCountSameCategory), LocalDateTime.now()));
            if (caseRepository.count(booleanExpression) >= lastDaysCountTeam) {
                ApplicationLogger.logger.debug("Priority set to High for caseId: {};Case-Title : {}; teamId: {}; Module: {}", (entity.getCaseId() != null)?entity.getCaseId():"NOT FOUND",entity.getCaseTitle(), teamId, SUBMODULE);
                return "High";
            }
        }
        ApplicationLogger.logger.debug("Priority set to Low for caseId: {}; case-title: {};  teamId: {}; Module: {}", (entity.getCaseId() != null)?entity.getCaseId():"NOT FOUND",entity.getCaseTitle(), teamId, SUBMODULE);
        return "Low";
    }

//    @Transactional
//    public GenericDataDTO approveTicket(Long caseId, boolean isApproveRequest, String remarks) {
//        String SUBMODULE = getModuleNameForLog() + " [approveTicket()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        //addding entry to tat
//        HashMap<String, String> tatMapDetails = new HashMap<>();
//        StaffUser getCurrentStaffuser = new StaffUser();
//
//        try {
//            Case aCase = caseRepository.findById(caseId).orElse(null);
//
//            StaffUser loggedInStaffUser = staffUserService.get(getLoggedInUserId());
//            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//            caseUpdateDTO.setTicketId(caseId);
//            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue();
//            CaseDTO caseDTO = caseService.getEntityForUpdateAndDelete(caseUpdateDTO.getTicketId());
//            List<CaseDocDetailsDTO> finalResponseList = new ArrayList<>();
//
////            if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
////                loggedInStaffUser.setLcoId(getLoggedInUser().getPartnerId());
////            } else {
////                loggedInStaffUser.setLcoId(null);
////            }
//            //  if(loggedInStaffUser.getLcoId()==null){
//            if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
//                if (!loggedInStaffUser.getUsername().equalsIgnoreCase("admin")) {
//                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    int staffId = 0;
//                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                        caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                        staffId = Integer.parseInt(map.get("staffId"));
//                        StaffUser assignedStaffUser = staffUserService.get(staffId);
//                        caseUpdateDTO.setAssignee(staffId);
//                        caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                        Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                        String nextFollowupDate = aCase.getNextFollowupDate().toString();
//                        String nextFollowupTime = aCase.getNextFollowupTime().toString();
//                        if (isApproveRequest) {
//                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                                aCase.setCaseStatus(CaseConstants.STATUS_IN_PROGRESS);
//                                caseRepository.save(aCase);
//                            }
//                        }
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), assignedStaffUser.getId(), assignedStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaffUser.getUsername());
////                        caseUpdateService.sendAssignTicketMessege(aCase.getCustomers().getUsername(), aCase.getCustomers().getMobile(), aCase.getCustomers().getEmail(), aCase.getCustomers().getMvnoId(), aCase.getCaseNumber(), teams.getName(), nextFollowupDate, aCase.getCustomers().getUsername(), nextFollowupTime, aCase.getCustomerAdditionalEmail(), aCase.getSerialNumber(), aCase.getCustomers().getBuId());
//                    } else {
//                        if (isApproveRequest) {
//                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
//                                aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                                caseRepository.save(aCase);
//                            }
//                            caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                        } else {
//                            caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                        }
//                        aCase.setCurrentAssignee(null);
//                        aCase.setFinalClosedBy(loggedInStaffUser);
//                        aCase.setFinalClosedDate(LocalDateTime.now());
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//
//                    }
//                    caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                } else {
//
//                    if (isApproveRequest) {
//                        caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                    } else {
//                        caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                    }
//                    aCase.setCurrentAssignee(null);
//                    aCase.setFinalResolvedBy(loggedInStaffUser);
//                    aCase.setFinalResolutionDate(LocalDateTime.now());
//                    caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                }
//            } else {
//                if (!loggedInStaffUser.getUsername().equalsIgnoreCase("admin")) {
//                    Map<String, Object> map = hierarchyService.getTeamForNextApprove(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    if (map.containsKey("assignableStaff")) {
//                        List<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findAllByHierarchyId(Integer.valueOf(String.valueOf(map.get("workFlowId"))));
//
//
//                        genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                        tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
//                        tatMapDetails.put("eventId", caseDTO.getCaseId().toString());
//                        tatMapDetails.put("eventName", map.get("eventName").toString());
//                        getCurrentStaffuser = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
//                        tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, tatMapDetails, getCurrentStaffuser, false);
//                        if (isApproveRequest) {
//                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                                aCase.setCaseStatus(CaseConstants.STATUS_IN_PROGRESS);
//                                aCase.setNextFollowupDate(LocalDate.now());
//                                aCase.setNextFollowupTime(LocalTime.now());
//                                caseRepository.save(aCase);
//                            }
//                        }
//
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                        //}
//                    } else {
//                        if (isApproveRequest) {
//                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
//                                aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                                caseRepository.save(aCase);
//                            }
//                            caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                        } else {
//                            caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                        }
//
//
//                        aCase.setFinalResolvedBy(loggedInStaffUser);
//                        aCase.setFinalResolutionDate(LocalDateTime.now());
//
//                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(aCase.getCaseId().intValue(), true);
//                        if (tatMatrixWorkFlowDetails != null && !caseUpdateDTO.getStatus().equalsIgnoreCase("rejected") && !caseUpdateDTO.getStatus().equalsIgnoreCase("closed")) {
//                            tatMapDetails.put("workFlowId", tatMatrixWorkFlowDetails.getWorkFlowId().toString());
//                            tatMapDetails.put("eventId", tatMatrixWorkFlowDetails.getEventId().toString());
//                            tatMapDetails.put("eventName", tatMatrixWorkFlowDetails.getEventName());
//                        }
//
//                        getCurrentStaffuser = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
//
//                        aCase.setCurrentAssignee(null);
//                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                        if (tatMapDetails != null && !tatMapDetails.isEmpty()) {
//                            tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, tatMapDetails, getCurrentStaffuser, false);
//                        }
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : " remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                    }
//                } else {
//                    if (isApproveRequest) {
//                        caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                    } else {
//                        caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                    }
//                    aCase.setCurrentAssignee(null);
//
//                    aCase.setFinalResolvedBy(loggedInStaffUser);
//                    aCase.setFinalResolutionDate(LocalDateTime.now());
//                    caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                }
//            }
//
//            return genericDataDTO;
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//
//        }
//    }


    @Transactional
    public CaseDTO approveTaskByFinalStatus(CaseDTO aCase,String finalRemarks) {
        String SUBMODULE = getModuleNameForLog() + " [approveTaskByFinalStatus()] ";
        try {
            ApplicationLogger.logger.debug("inside approve Task By Final Status for caseId: {}; Module : {};", aCase.getCaseId(), SUBMODULE);
            StaffUser loggedInStaffUser = staffUserService.get(getLoggedInUserId());
            ApplicationLogger.logger.debug("fetched logged in staff: {};, Module : {};", loggedInStaffUser.getUsername(),SUBMODULE);
            // aCase.setCurrentAssigneeId(null);
            aCase.setFinalResolvedById(getLoggedInUserId());
            aCase.setFinalResolutionDate(LocalDateTime.now());
            aCase.setFinalTaskCompletionRemark(finalRemarks);
            ApplicationLogger.logger.debug("Case final resolution set by staff: {};, Module : {};", loggedInStaffUser.getUsername(), SUBMODULE);
            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), getLoggedInUserId(), loggedInStaffUser.getUsername(), false ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), false ? "remarks :- " + aCase.getFinalTaskCompletionRemark() + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + aCase.getFinalTaskCompletionRemark() + " Approved By :- " + loggedInStaffUser.getUsername()+" Final Done Remarks : "+ aCase.getFinalTaskCompletionRemark());
            ApplicationLogger.logger.info("Audit saved for caseId: {} by staff : {};, Module : {};", aCase.getCaseId(),loggedInStaffUser.getUsername(),SUBMODULE);
            return aCase;
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error("an error occurred while approving task by final status; Message : {}; Module : {}", ex.getMessage(), SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Exception occurred Message : {}; Module : {}", ex.getMessage(), SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);

        }
    }


    @Transactional
    public GenericDataDTO assignPickedTicket(Long caseId, Integer staffId, String remark) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [assignPickedTicket()] ";
        ApplicationLogger.logger.debug("inside assignPickedTicket for caseId: {}, staffId: {}; Module: {}", caseId, staffId, SUBMODULE);
        Case aCase = caseRepository.findById(caseId).orElse(null);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        StaffUser pickedUser = staffUserRepository.findById(staffId).orElse(null);

        if (aCase == null) {
            ApplicationLogger.logger.warn("Case not found for caseId: {}; Module: {}", caseId, SUBMODULE);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Task not found..");
            return genericDataDTO;
        } else {
            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
            caseUpdateDTO.setRemarkType("Picked by remarks ");
            caseUpdateDTO.setRemark(remark);
            caseUpdateDTO.setAssignee(staffId);
            caseUpdateDTO.setTicketId(caseId);
            Case newCase = new Case();


            TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
            ApplicationLogger.logger.debug("TAT retrieved for caseId: {}; Module: {}", caseId, SUBMODULE);
            Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix.getTatMatrixMappings().get(0).getMtime3()));
            TicketTatMatrixMapping ticketTatMatrixMapping = ticketTatMatrix.getTatMatrixMappings().get(0);

            caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(aCase);
            ApplicationLogger.logger.debug("Follow-up date and time updated for caseId: {}; Module: {}", caseId, SUBMODULE);
            caseUpdateDTO.setStatus(CaseConstants.STATUS_IN_PROGRESS);

            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), pickedUser.getId(), pickedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Remark : -" + remark + " Picked By :- " + pickedUser.getUsername());
            ApplicationLogger.logger.info("Audit saved for caseId: {}; Picked by: {}; Module: {}", caseId, pickedUser.getUsername(), SUBMODULE);
            Map<String, Object> detailsMap = getDetailsforTatWorkflowDetailsEntry(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), true);
            caseService.updateTicketLevel(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), detailsMap);
            caseUpdateService.updateEntity(caseUpdateDTO, null, true, null);
            ApplicationLogger.logger.debug("Case updated for caseId: {}; Module: {}", caseId, SUBMODULE);
            for (TicketAssignStaffMapping ticketAssignStaffMapping : aCase.getTicketAssignStaffMappings()) {
                ticketAssignStaffMappingRepo.delete(ticketAssignStaffMapping);
            }
            ApplicationLogger.logger.info("ticket assignee staff mapping deleted for caseId: {}; Module: {}", caseId, SUBMODULE);
            //if (aCase.getTeamHierarchyMappingId() != null) {
            tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), pickedUser, false, true);
            //}
            ApplicationLogger.logger.debug("Ticket TAT assignee changed for caseId: {}; Module: {}", caseId, SUBMODULE);
            //Status update send to custcasedetails
            CloseTicketCheckMessage message = new CloseTicketCheckMessage();
            message.setCaseId(caseId.intValue());
            message.setStatus(caseUpdateDTO.getStatus());
//            messageSender.send(message, RabbitCallConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW);
            kafkaMessageSender.send(new KafkaMessageData(message, CloseTicketCheckMessage.class.getSimpleName()));
            ApplicationLogger.logger.debug("Kafka message sent for caseId: {}; Status: {}; Module: {}", caseId, caseUpdateDTO.getStatus(), SUBMODULE);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        }
        ApplicationLogger.logger.debug("Completed assignPickedTicket for caseId: {}; Module: {}", caseId, SUBMODULE);
        return genericDataDTO;
    }





    @Transactional
    public GenericDataDTO assignEveryStaffFromList(Long caseId, String remark, Boolean isApproveRequest) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [assignEveryStaffFromList()] ";
        ApplicationLogger.logger.debug("inside assignEveryStaffFromList for caseId: {}, remark: {}; isApproveRequest : {}; Module: {}", caseId, remark,isApproveRequest, SUBMODULE);
        Case aCase = caseRepository.findById(caseId).orElse(null);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
        if (isApproveRequest) {
            aCase.setCase_order(aCase.getCase_order() + 1);
            ApplicationLogger.logger.debug(" Approve request with Case : {}; set case order : {}; , Module : {}; ", aCase.getCaseTitle(), aCase.getCase_order(), SUBMODULE);
        } else {
            aCase.setCase_order(aCase.getCase_order() - 1);
            ApplicationLogger.logger.debug(" Approve request with Case : {}; set case order : {}; , Module : {}; ", aCase.getCaseTitle(), aCase.getCase_order(), SUBMODULE);
        }
//        Map<String, Object> map = hierarchyService.getTeamForNextApprove(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//        if (map.containsKey("assignableStaff")) {
//            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//            caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
//            caseUpdateDTO.setTicketId(caseId);
//            caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//            aCase.setCurrentAssignee(null);
//            Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
//            //List<StaffUserPojo> staffByUniqueParent = new ArrayList<>();
//            for (StaffUserPojo staffUserPojo : staffUserPojos) {
//                TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
//                ticketAssignStaffMapping.setTicketId(aCase.getCaseId());
//                ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) (map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
//                if (staffUserPojo.getParentStaffId() != null) {
//                    staffByParentStaffId.put(staffUserPojo.getParentStaffId(), staffUserPojo);
//                }
//                //caseService.updateTicketLevel(caseMapper.domainToDTO(aCase,new CycleAvoidingMappingContext()),map);
//                //after assign to all responstime is set in followupdate and followuptime
//                if (!aCase.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                    caseService.updateFollowUpDateAndTimeForTicketBeforePickedUp(aCase);
//                }
//                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + aCase.getCustomerName() + " '";
//                hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), aCase.getMvnoId(), staffUserPojo.getFullName(), action, aCase.getBuId());
//            }
//
//            caseRepository.save(aCase);
//            caseUpdateService.updateEntity(caseUpdateDTO, null, true);
//            if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
//                for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
//                    StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
//                    tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), staffUser, true, false);
//                }
//            }
//        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;
    }

//    public GenericDataDTO reassignTicket(Long caseId) throws Exception {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<StaffUserPojo> staffUserList = new ArrayList<>();
//        if (aCase.getCurrentAssignee() != null) {
//            if (getLoggedInUserId() == aCase.getCurrentAssignee().getId()) {
//                if (aCase.getTeamHierarchyMappingId() != null) {
//                    staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    genericDataDTO.setDataList(staffUserList);
//                }
//            } else if (aCase.getCurrentAssignee().getParentStaffId() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                while (staffUser.getParentStaffId() != null && staffUserList.size() == 0) {
//                    if (staffUser.getParentStaffId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_CATEGORY);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        }
//                    } else {
//                        staffUser = staffUserRepository.findById(staffUser.getParentStaffId()).orElse(null);
//                    }
//                }
//            }
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }


    public GenericDataDTO assignTasks(TasksAssignDTO tasksAssignDTO) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [assignTasks()] ";
        try {
            if(tasksAssignDTO.getCaseId() != null && tasksAssignDTO.getTeamId() != null){
                ApplicationLogger.logger.debug("starting assign tasks for case ID : {}; TeamID : {}; Module : {};",tasksAssignDTO.getCaseId(),tasksAssignDTO.getTeamId(), SUBMODULE);
            }else{
                ApplicationLogger.logger.error("Invalid request. Case ID and Team ID are required.; { Module : {} }",SUBMODULE);
                throw new IllegalArgumentException("Invalid request. Case ID and Team ID are required.");
            }
            Case aCase = caseRepository.findById(tasksAssignDTO.getCaseId()).orElse(null);
            if(Objects.isNull(aCase)){
                ApplicationLogger.logger.error("Case not found for caseID : {}; Module : {};",tasksAssignDTO.getCaseId(), SUBMODULE);
                throw new Exception("Case not found for ID : " + tasksAssignDTO.getCaseId());
            }
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<StaffUserPojo> staffUserList = new ArrayList<>();
            Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
            if (aCase.getCurrentAssignee() != null || aCase.getCurrentAssignee().getParentStaffId() != null) {
                ApplicationLogger.logger.debug("Current assignee found for caseId: {}; Current Assignee: {}; Module: {}", tasksAssignDTO.getCaseId(),(aCase.getCurrentAssignee() != null)?aCase.getCurrentAssignee():aCase.getCurrentAssignee().getParentStaffId() ,SUBMODULE);
                if (getLoggedInUserId() == aCase.getCurrentAssignee().getId() || getLoggedInUserId() == aCase.getCurrentAssignee().getParentStaffId()) {
                    if (tasksAssignDTO.getStaffId() == null && tasksAssignDTO.getTeamId() != null) {
                        ApplicationLogger.logger.debug("Assigning tasks to team ID: {}; Module: {}", tasksAssignDTO.getTeamId(), SUBMODULE);
                        staffUserList = hierarchyService.getStaffFromTeam(aCase.getTeamId(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
                        Teams teams =teamsRepository.getOne(tasksAssignDTO.getTeamId().longValue());
                        for (int i = 0; i < staffUserList.size(); i++) {
                            removeEntriesForOldStaffFromTaskAssignment(tasksAssignDTO.getCaseId());
                            TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
                            ticketAssignStaffMapping.setStaffId(staffUserList.get(i).getId());
                            ticketAssignStaffMapping.setTicketId(aCase.getCaseId());
                            ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
                            //Add Audit here
                            if (staffUserList.get(i).getParentStaffId() != null) {
                                staffByParentStaffId.put(staffUserList.get(i).getParentStaffId(), staffUserList.get(i));
                            }
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffUserList.get(i).getUsername() + " '";
                            if (!aCase.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                                //change this message to staff
                                hierarchyService.sendWorkflowAssignActionMessage(staffUserList.get(i).getCountryCode(), staffUserList.get(i).getPhone(), staffUserList.get(i).getEmail(), staffUserList.get(i).getMvnoId(), staffUserList.get(i).getFullName(), action, staffUserList.get(i).getBusinessunitid());
                            }
                            if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
                                for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
                                    StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
                                    tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), staffUser, true, false);
                                }
                            }
                            ApplicationLogger.logger.info("saving workflow audit for Case : {}; Module : {};",aCase.getCaseId(),SUBMODULE);
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUserList.get(i).getId(), staffUserList.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Memeber :- "+staffUserList.get(i).getUsername()+" of the Team : "+teams.getName());

                        }
                    } else if (tasksAssignDTO.getStaffId() != null && tasksAssignDTO.getTeamId() != null) {
                        StaffUser staffUser = staffUserRepository.findById(tasksAssignDTO.getStaffId()).orElse(null);
                        if(Objects.nonNull(staffUser)){
                            ApplicationLogger.logger.debug("Assigning task to staff : {}; Module: {}", staffUser.getUsername(), SUBMODULE);
                        }else{
                            ApplicationLogger.logger.error("Staff not found for ID : {}; Module : {};", tasksAssignDTO.getStaffId(), SUBMODULE);
                            throw new Exception("Staff not found for ID : " + tasksAssignDTO.getStaffId());
                        }
                        //Remove old entries from the ticketassignstaffmapping
                        removeEntriesForOldStaffFromTaskAssignment(tasksAssignDTO.getCaseId());
                        if (staffUser != null) {
                            aCase.setCurrentAssignee(staffUser);
                            ApplicationLogger.logger.info("saving current assigee for Case : {}; StaffUser: {}; Module : {};",(aCase.getCaseNumber() != null)?aCase.getCaseNumber():aCase.getCaseTitle(),staffUser.getUsername(),SUBMODULE);
                            caseRepository.save(aCase);
                        }
                        //Add Audit here
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "staff name : " + " ' " + staffUser.getUsername() + " '";
                        if (!aCase.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                            //change this message to staff
                            if(staffUser.getBusinessUnit()!=null)
                                hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action, staffUser.getBusinessUnit().getId());
                            else
                                hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action, null);

                            ApplicationLogger.logger.debug("sent work flow assign action message Module : {}; ", SUBMODULE);
                        }
                        if (staffUser.getParentStaffId() != null) {
                            tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), staffUser, false, true);
                        }
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to Staff :- "+staffUser.getUsername());
                        ApplicationLogger.logger.info("audit saved for Case: {}; by Staff : {}; Module : {}",aCase.getCaseTitle(), staffUser.getUsername(),SUBMODULE);
                    }

                }
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            ApplicationLogger.logger.debug("Task assign completed for case ID: {}; Module: {}", tasksAssignDTO.getCaseId(), SUBMODULE);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error occurred during task assign Message: {}; Module: {}", e.getMessage(), SUBMODULE, e);
            e.printStackTrace();
        }
        return null;

    }


    public void removeEntriesForOldStaffFromTaskAssignment(Long caseId) {
        String SUBMODULE = getModuleNameForLog() + " [removeEntriesForOldStaffFromTaskAssignment()] ";
        List<TicketAssignStaffMapping> ticketAssignStaffMappings = ticketAssignStaffMappingRepo.findAllByTicketIdIn(Arrays.asList(caseId));
        if (ticketAssignStaffMappings != null) {
            ApplicationLogger.logger.debug("removing entries for old staff from task assignments; Module : {};",SUBMODULE);
            ticketAssignStaffMappingRepo.deleteAll(ticketAssignStaffMappings);
        }
    }


    /* This method is not used anywhere hence commented */

//    public GenericDataDTO reassignLead(Long caseId) throws Exception {
//        //please remove Case aCase for reassign lead
//        LeadMaster leadMaster = leadMasterRepository.findById(caseId).orElse(null);
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<StaffUserPojo> staffUserList = new ArrayList<>();
//        if (leadMaster.getCreatedBy() != null) {
//            int id = Integer.parseInt(leadMaster.getCreatedBy());
//            if (getLoggedInUserId() == id) {
//                if (leadMaster.getCreatedBy() != null) {
//                    staffUserList = hierarchyService.getStaffFromCurrentTeammapping(leadMaster.getNextApproveStaffId(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    genericDataDTO.setDataList(staffUserList);
//                }
//            } else if (aCase.getCurrentAssignee().getStaffUserparent() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                while (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
//                    if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        }
//                    } else {
//                        staffUser = staffUser.getStaffUserparent();
//                    }
//                }
//            }
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

    public GenericDataDTO linkTicket(Long caseId, Integer linkTicketId) {
        String SUBMODULE = getModuleNameForLog() + " [linkTicket()] ";
        if(caseId != null) {
            ApplicationLogger.logger.debug("inside linkTicket method; module: {} ; caseId: {}, linkTicketId: {}", SUBMODULE, caseId, (linkTicketId != null) ? linkTicketId : "not found");
        }
        Case aCase = caseRepository.findById(caseId).orElse(null);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (aCase != null) {
            ApplicationLogger.logger.debug("Case found with ID: {}; module: {}", caseId, SUBMODULE);
            aCase.setParentTicketId(linkTicketId);
            caseRepository.save(aCase);
            ApplicationLogger.logger.info("Case updated with parent ticket ID: {}; module: {}", linkTicketId, SUBMODULE);
        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        ApplicationLogger.logger.debug("Finished linkTicket method; module: {} ; responseCode: {}", SUBMODULE, genericDataDTO.getResponseCode());
        return genericDataDTO;

    }

    @Transactional
    public GenericDataDTO updateDocumentDetails(Long caseId, List<MultipartFile> file, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateDocumentDetails()] ";
        ApplicationLogger.logger.debug("Starting updateDocumentDetails method; module: {} ; " , SUBMODULE);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        ApplicationLogger.logger.debug("Starting document update for case ID: {}; module: {}", caseId,SUBMODULE);
        try {
            Case aCase = caseRepository.findById(caseId).orElse(null);

            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
            CaseDTO dbObj = caseService.getEntityForUpdateAndDelete(aCase.getCaseId());
            if (null != aCase && file != null && file.size() > 0) {
                for (MultipartFile multipartFile : file) {
                    CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
//                String subFolderName = aCase.getCaseNumber().trim().replace("-","_") + "/";
                    String path = PATH;
                    caseDoc.setTicketId(Math.toIntExact(aCase.getCaseId()));
                    caseDoc.setDocStatus("Active");
                    MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
                    if (file1 != null) {
                        caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
                        caseDoc.setFilename(file1.getOriginalFilename());
                        caseDoc = caseDocDetailsService.saveEntity(caseDoc);
                        ApplicationLogger.logger.info("File '{}' saved successfully for case ID: {}; module: {}", file1.getOriginalFilename(), caseId,SUBMODULE);
                    }
                }


            }
//            List<CaseUpdate> caseUpdate = aCase.getCaseUpdateList();
//            CaseUpdate caseUpdate1 = new CaseUpdate();
//            caseUpdate1.setTicket(aCase);
//            caseUpdate1.setLastModifiedByName(getLoggedInUser().getUsername());
//            caseUpdate1.setLastModifiedById(getLoggedInUserId());
//            caseUpdate1.setUpdatedate(LocalDateTime.now());
//            caseUpdate.add(caseUpdate1);
//            caseUpdateRepository.saveAll(caseUpdate);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            RESP_CODE = APIConstants.SUCCESS;
            ApplicationLogger.logger.debug(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Document Details " + LogConstants.LOG_BY_NAME + aCase.getCaseNumber() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Document Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Document Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        ApplicationLogger.logger.debug("Finished processing document update request for case ID: {};module: {}", caseId,SUBMODULE);
        return genericDataDTO;

    }

    public GenericDataDTO getTaskApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [getTaskApprovals()] ";
        ApplicationLogger.logger.debug("Starting getTaskApprovals method; module: {} ; " , SUBMODULE);
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
        QCase qCase = QCase.case$;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QTeams qTeams = QTeams.teams;
        QCaseSubCategory qCaseSubCategory = QCaseSubCategory.caseSubCategory;
        JPAQueryFactory jpaQueryFactory =new JPAQueryFactory(entityManager);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false)) .and(qCase.caseStatus.ne(CaseConstants.TASK_STATUS_DISCARDED).or(qCase.caseStatus.ne(CaseConstants.TASK_STATUS_DONE)));
        StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
        Set<Integer> collectedTeamIds = staffUser.getTeam().stream().map(Teams::getId).map(Long::intValue).collect(Collectors.toSet());

        booleanExpression = booleanExpression.and(qCase.teamId.in(collectedTeamIds).or(qCase.currentAssignee.id.eq(getLoggedInUserId())));

            QueryResults<CaseDTO>  queryResults = jpaQueryFactory
                    .select(Projections.constructor(
                            CaseDTO.class,
                            qCase.caseId,
                            qCase.caseTitle,
                            qStaffUser.firstname.concat("").concat(qStaffUser.lastname),
                            qCaseSubCategory.subCategoryName,
                            qCase.teamId,
                            qTeams.name,
                            qCase.caseSubCategoryId,
                            qCase.currentAssignee.id
                    )).from(qCase).
                    leftJoin(qStaffUser).on(qStaffUser.id.eq(qCase.currentAssignee.id))
                            .leftJoin(qTeams).on(qCase.teamId.eq(qTeams.id.intValue())).
                    leftJoin(qCaseSubCategory).on(qCaseSubCategory.subCategoryId.eq(qCase.caseSubCategoryId))
                            .where(booleanExpression)
                                    .orderBy(qCase.caseId.desc())
                                            .fetchResults();

          List<CaseDTO> results = queryResults.getResults();

//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qCase.staffUser.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qCase.staffUser.mvnoId.eq(1).or(qCase.staffUser.mvnoId.eq(getMvnoIdFromCurrentStaff()))); //.and(qCase.staffUser.businessUnit.in(getBUIdsFromCurrentStaff()))
//        }
       // Page<Case> paginationList = caseRepository.findAll(booleanExpression, pageRequest);
        Page<CaseDTO> paginationList = new PageImpl<>(results,PageRequest.of(page - 1, pageSize),queryResults.getTotal());
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(results.size());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        ApplicationLogger.logger.debug( "GenericData set Successfully"+LogConstants.LOG_STATUS_CODE+genericDataDTO.getResponseCode()+"module: "+SUBMODULE);
        return genericDataDTO;
    }

    /**
     * Added this method similar to getTaskApprovals, to optimise api
     * @param filters
     * @param page
     * @param pageSize
     * @return
     */
    public GenericDataDTO getTaskApprovalsList(List<GenericSearchModel> filters, Integer page, Integer pageSize) {
        String SUBMODULE = getModuleNameForLog() + " [getTaskApprovals()] ";
        ApplicationLogger.logger.debug("Starting getTaskApprovals method; module: {} ; ", SUBMODULE);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "caseId"));
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Set<Integer> collectedTeamIds = staffUserRepository.findTeamIdsByStaffId(getLoggedInUserId());

        Page<TaskApprovalProjection> paginationList = caseRepository.getTaskApprovalsNative(collectedTeamIds, getLoggedInUserId(), pageRequest);

        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords((int) paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());

        ApplicationLogger.logger.debug("GenericData set Successfully" + LogConstants.LOG_STATUS_CODE + genericDataDTO.getResponseCode() + " module: " + SUBMODULE);
        return genericDataDTO;
    }




    public GenericDataDTO sendETRTicketNotification(TicketETRPojo ticketETRPojo) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [sendETRTicketNotification()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        ApplicationLogger.logger.debug("Fetching case data for ticketId: {};module:{}", ticketETRPojo.getTicketId(),SUBMODULE);
        // gettitng nacessery data from pojo
        Case aCase = caseRepository.findById(ticketETRPojo.getTicketId().longValue()).orElse(null);
        if (aCase == null) {
            ApplicationLogger.logger.warn("Case not found for ticketId: {}; Module: {}", ticketETRPojo.getTicketId(),SUBMODULE);
            throw new Exception("Case not found");
        }
        ticketETRPojo.setTaskOwnerStaffId(aCase.getCreatedById());
        StaffUser taskOwnerStaff = staffUserRepository.findById(aCase.getCreatedById()).orElse(null);
        StaffUser staffUser = staffUserRepository.findById(aCase.getCurrentAssignee().getId()).orElse(null);

        //setting template notification content
        HashMap<Object, Object> data = new HashMap<>();

        //adding customers data
        data.put("taskOwnerStaffId", taskOwnerStaff.getId());
        data.put("customerName", taskOwnerStaff.getUsername());
        data.put("mobileNumber", taskOwnerStaff.getPhone());
        data.put("email", taskOwnerStaff.getEmail());
        data.put("mvnoId", taskOwnerStaff.getMvnoId());
        if(taskOwnerStaff.getBusinessUnit()!=null)
            data.put("buId", taskOwnerStaff.getBusinessUnit().getId());
        else
            data.put("buId", null);
        //data.put("altEmail", aCase.getStaffAdditionalEmail());

        //adding case data
        data.put("caseId", aCase.getCaseId());
        data.put("caseNumber", aCase.getCaseNumber());
        data.put("staffId", staffUser.getId());
        data.put("staffName", staffUser.getFullName());
        data.put("additionalDate", ticketETRPojo.getNotificationDate());
        data.put("additionalTime", ticketETRPojo.getNotificationTime());
        data.put("remark", ticketETRPojo.getRemark());
        data.put("status", ticketETRPojo.getStatus());
        data.put("sender", ticketETRPojo.getSender());


        //adding addtional data
        data.put("typeNotification", ticketETRPojo.getSelectedNotificationType());
        data.put("isTemplateDynamic", ticketETRPojo.getIsTemplateDynamic());

        //auditdata
        HashMap<String, Boolean> notificationType = new HashMap<>();
        notificationType.putAll((Map<String, Boolean>) data.get("typeNotification"));
//        if (notificationType.get("sms")) {
//            data.put("notificationMode", "sms");
//        } else if (notificationType.get("email")) {
//            data.put("notificationMode", "email");
//        }
        if ((Boolean) data.get("isTemplateDynamic")) {
            data.put("messageMode", "Dynamic");
        } else {
            data.put("messageMode", "Static");
        }

        //calling senderFunction
        ApplicationLogger.logger.debug("Fetched staff user data and Sending ETR ticket message; Module : {}",SUBMODULE);
        sendETRTicketMessege(data);

        //setiitng value in generic data
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        ApplicationLogger.logger.debug("ETR ticket notification sent successfully"+LogConstants.LOG_STATUS_CODE+genericDataDTO.getResponseCode()+"; module: {}", SUBMODULE);
        return genericDataDTO;

    }


    public void sendETRTicketMessege(HashMap<Object, Object> data) {
        String SUBMODULE = getModuleNameForLog() + " [sendETRTicketMessege()] ";
        try {
            ApplicationLogger.logger.debug("Starting sendETRTicketMessege method; module: {}", SUBMODULE);
            TemplateNotification optionalTemplate = new TemplateNotification();
            ApplicationLogger.logger.debug("Fetching static ETR template; module: {}", SUBMODULE);
            if (!(Boolean) data.get("isTemplateDynamic")) {
                optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_ETR_TEMPLATE).orElse(null);
            } else {
                optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_ETR_TEMPLATE_DYNAMIC).orElse(null);
            }

            HashMap<String, Boolean> notificationType = new HashMap<>();
            notificationType.putAll((Map<String, Boolean>) data.get("typeNotification"));

            ApplicationLogger.logger.debug("Configuring notification types; module: {}", SUBMODULE);
            if (notificationType.get("sms") && notificationType.get("email")) {
                optionalTemplate.setSmsEventConfigured(true);
                optionalTemplate.setEmailEventConfigured(true);

            } else if (notificationType.get("email") && !notificationType.get("sms")) {
                optionalTemplate.setEmailEventConfigured(true);
                optionalTemplate.setSmsEventConfigured(false);
            } else if (notificationType.get("sms") && !notificationType.get("email")) {
                optionalTemplate.setEmailEventConfigured(false);
                optionalTemplate.setSmsEventConfigured(true);
            }


            if (optionalTemplate != null) {

//                Long buId = null;
//                if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                    buId =  getBUIdsFromCurrentStaff().get(0);
//                }

                TicketETRMsg ticketETRMsg = new TicketETRMsg(data.get("customerName").toString(),
                        data.get("mobileNumber").toString(),
                        data.get("email").toString(),
                        Integer.valueOf(data.get("mvnoId").toString()),
                        RMQConstants.TICKET_ETR_TEMPLATE,
                        optionalTemplate,
                        RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY,
                        data.get("caseNumber").toString(),
                        data.get("additionalDate").toString(),
                        data.get("additionalTime").toString(),
                        data.get("staffName").toString(),
                        data.get("remark").toString(),
                        data.get("status").toString(),
                        data.get("sender").toString(),
                        (Boolean) data.get("isTemplateDynamic"),
                        //data.get("notificationMode").toString(),
                        data.get("messageMode").toString(),
                        (Integer) data.get("taskOwnerStaffId"),
                        (Integer) data.get("staffId"),
                        (Long) data.get("caseId"),
                        (Long) data.get("buId"),
                        data.get("altEmail") != null ? data.get("altEmail").toString() : null);
                Gson gson = new Gson();
                gson.toJson(ticketETRMsg);
//                messageSender.send(ticketETRMsg, RMQConstants.QUEUE_TICKET_ETR);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRMsg, TicketETRMsg.class.getSimpleName(),KafkaConstant.TASK_ETR_MSG));

            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket ETR Failed, There might be templeate issues");
                ApplicationLogger.logger.error("Ticket ETR Failed, Template not found;There might template issues; module: {}", SUBMODULE);
            }


        } catch (Throwable e) {
            ApplicationLogger.logger.error("Error in sendETRTicketMessege: {}; module: {}", e.getMessage(), SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }

    }

    @Transactional
    public void saveETRAudit(HashMap<String, Object> data) {
        String SUBMODULE = getModuleNameForLog() + " [saveETRAudit()] ";
        try {
            EtrAudit etrAudit = new EtrAudit();
            etrAudit.setCaseNumber(data.get("caseNumber").toString());
            etrAudit.setCaseId((int) Double.parseDouble(data.get("caseId").toString()));
            etrAudit.setCustId(data.get("custId") != null ? (int) Double.parseDouble(data.get("custId").toString()) : null);
            etrAudit.setCustUserName(data.get("customerName").toString());
            etrAudit.setStaffId((int) Double.parseDouble(data.get("staffId").toString()));
            etrAudit.setStaffPersonName(data.get("staffPersonName").toString());
            etrAudit.setNotificationSentTime(LocalTime.now());
            etrAudit.setNotificationSentDate(LocalDate.now());
            etrAudit.setNotificationMessage(data.get("notificationMessage").toString());
            etrAudit.setNotificationMode(data.get("notificationMode").toString());
            etrAudit.setMessageMode(data.get("messageMode").toString());
            etrAudit.setNotificationStatus(data.get("notificationStatus").toString());
            ticketETRAuditRepository.save(etrAudit);
            ApplicationLogger.logger.info("ETR audit saved successfully; module: {}", SUBMODULE);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error saving ETR audit: {}; error details: {} ;module: {}", e.getMessage(),e, SUBMODULE);
            e.getMessage();
        }
    }

    /* This method is not used anywhere hence commented */

//    public void saveEnterpriseETRAudit(HashMap<String, Object> data) {
//        EnterpriseETRAudit etrAudit = new EnterpriseETRAudit();
//        etrAudit.setCustId((Long) data.get("custId"));
//        etrAudit.setCustUserName(data.get("customerName").toString());
//        etrAudit.setStaffId((Long) data.get("custId"));
//        etrAudit.setStaffPersonName(data.get("staffPersonName").toString());
//        etrAudit.setNotificationSentTime(LocalTime.now());
//        etrAudit.setNotificationSentDate(LocalDate.now());
//        etrAudit.setNotificationMessage(data.get("notificationMessage").toString());
//        etrAudit.setNotificationMode(data.get("notificationMode").toString());
//        etrAudit.setMessageMode(data.get("messageMode").toString());
//        etrAudit.setNotificationStatus(data.get("notificationStatus").toString());
//        enterpriseETRAuditRepository.save(etrAudit);
//    }

    public GenericDataDTO getETRDetailsForCase(Long caseId) {
        String SUBMODULE = getModuleNameForLog() + " [getETRDetailsForCase()] ";
        if(caseId == null){
            ApplicationLogger.logger.error("CaseId is null to get Etr details for case; module: {}", SUBMODULE);
            throw new IllegalArgumentException("CaseId cannot be null");
        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<EtrAudit> etrAudits = new ArrayList<>();
        try {
            QEtrAudit qEtrAudit = QEtrAudit.etrAudit;
            BooleanExpression qEtrAuditBool = qEtrAudit.isNotNull().and(qEtrAudit.caseId.eq(Math.toIntExact(caseId)));
            ApplicationLogger.logger.debug("finding all ETR tickets for caseId: {}; module: {}", caseId, SUBMODULE);
            etrAudits = (List<EtrAudit>) ticketETRAuditRepository.findAll(qEtrAuditBool);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error fetching ETR audits for caseId: {}; Error: {}; module: {}", caseId, e.getMessage(), SUBMODULE);
            throw new RuntimeException(e);
        }
        genericDataDTO.setDataList(etrAudits);
        return genericDataDTO;
    }

    @Transactional
    public void updateFollowUpDateAndTimeForTicketBeforePickedUp(Case aCase) {
        String SUBMODULE = getModuleNameForLog() + " [updateFollowUpDateAndTimeForTicketBeforePickedUp()] ";
        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());
        try {
            if (aCase.getCaseCategoryId() != null) {
                ApplicationLogger.logger.debug("Fetching TicketTatMatrix  for case category: {}; module: {}", aCase.getCaseCategoryId(), SUBMODULE);
                TicketTatMatrix masterTicketTat = getTicketTatMatrixFromSubReasonId(caseDTO);
                if (masterTicketTat != null) {
                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    Long responseTime = masterTicketTat.getRtime();
                    String responsUnit = masterTicketTat.getRunit();
                    LocalDate localDate = LocalDate.now();
                    LocalTime localTime = LocalTime.now();
                    ApplicationLogger.logger.debug("follow-up date/time. Response time: {}, Response unit: {}; module: {}", responseTime, responsUnit, SUBMODULE);
                    if (!responsUnit.isEmpty()) {
                        if (responsUnit.equals("Day")) {
                            aCase.setNextFollowupDate(localDate.plusDays(responseTime));
                            ApplicationLogger.logger.debug("Set next follow-up date to {} days from now; module: {}", responseTime, SUBMODULE);
                        } else if (responsUnit.equals("Hour")) {
                            if (responseTime >= 24) {
                                long days = responseTime / 24;
                                long remainingHours = responseTime % 24;
                                aCase.setNextFollowupDate(localDate.plusDays(days));
                                aCase.setNextFollowupTime(localTime.plusHours(remainingHours));
                                ApplicationLogger.logger.debug("Set next follow-up to {} days and {} hours from now; module: {}", days, remainingHours, SUBMODULE);
                            }else if(localTime.getHour()+responseTime>=24){
                                long totalHours = localTime.getHour() + responseTime;
                                long days = totalHours / 24;
                                long remainingHours = totalHours % 24;
                                aCase.setNextFollowupDate(localDate.plusDays(days));
                                aCase.setNextFollowupTime(localTime.plusHours(remainingHours - localTime.getHour()));
                                ApplicationLogger.logger.debug("Set next follow-up to {} days and {} hours from now (crossing midnight); module: {}", days, remainingHours - localTime.getHour(), SUBMODULE);
                            }
                            else {
                                aCase.setNextFollowupTime(localTime.plusHours(responseTime));
                                ApplicationLogger.logger.debug("Set next follow-up time to {} hours from now; module: {}", responseTime, SUBMODULE);
                            }
                        } else if (responsUnit.equals("Min")) {
                            aCase.setNextFollowupDate(LocalDate.now());
                            if (responseTime >= 60) {
                                long hours = responseTime / 60;
                                long remainingMinutes = responseTime % 60;
                                aCase.setNextFollowupTime(localTime.plusHours(hours).plusMinutes(remainingMinutes));
                                ApplicationLogger.logger.debug("Set next follow-up time to {} hours and {} minutes from now; module: {}", hours, remainingMinutes, SUBMODULE);
                            } else {
                                aCase.setNextFollowupTime(localTime.plusMinutes(responseTime));
                                ApplicationLogger.logger.debug("Set next follow-up time to {} minutes from now; module: {}", responseTime, SUBMODULE);
                            }
                        }

                        try {
                            caseService.saveEntity(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
                            ApplicationLogger.logger.info("Saved updated case entity; module: {}", SUBMODULE);
                        } catch (Exception e) {
                            ApplicationLogger.logger.error("Error while saving case entity for update follow up date and time: {}; module: {}", e.getMessage(), SUBMODULE);
                            throw new RuntimeException(e);
                        }
                    }

                } else {
                    ApplicationLogger.logger.error("TaT not found for case category: {}; module: {}", aCase.getCaseCategoryId(), SUBMODULE);
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "There might be an issue in TAT selection query condition !! after correction please try again", null);
                }
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("error while updating follow up date-time, Message: {}; Response Code : {}; module: {}", e.getMessage(),e.getErrCode(), SUBMODULE);
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        }
    }


    public void updateFollowUpDateAndTimeForTicketAfterPickedUp(Case aCase) {
        String SUBMODULE = getModuleNameForLog() + " [updateFollowUpDateAndTimeForTicketAfterPickedUp()] ";
        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());

        try {
            if (aCase.getCaseCategoryId() != null) {
                ApplicationLogger.logger.debug("Starting updateFollowUpDateAndTimeForTicketAfterPickedUp for caseId: {}; module: {}", aCase.getCaseId(), SUBMODULE);
//            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(aCase.getReasonSubCategoryId());
                TicketTatMatrix masterTicketTat = getTicketTatMatrixFromSubReasonId(caseDTO);

                if (masterTicketTat != null) {
                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    if (tatMatrixMappings.size() > 0) {
                        for (TicketTatMatrixMapping tatMatrixMapping : tatMatrixMappings) {

                            if (tatMatrixMapping.getOrderNo() == 1) {
                                Long primarySLATime = 0L;
                                if (aCase.getPriority().equals("High")) {
                                    primarySLATime = tatMatrixMapping.getMtime1();
                                } else if (aCase.getPriority().equals("Medium")) {
                                    primarySLATime = tatMatrixMapping.getMtime2();
                                } else {
                                    primarySLATime = tatMatrixMapping.getMtime3();
                                }
                                String primarySLAUnit = tatMatrixMapping.getMunit();
                                LocalDate localDate = LocalDate.now();
                                LocalTime localTime = LocalTime.now();
                                if (!primarySLAUnit.isEmpty()) {
                                    if (primarySLAUnit.equals("Day")) {
                                        aCase.setNextFollowupTime(localTime);
                                        aCase.setNextFollowupDate(localDate.plusDays(primarySLATime));
                                    } else if (primarySLAUnit.equals("Hour")) {
                                        if (primarySLATime >= 24) {
                                            long days = primarySLATime / 24;
                                            long remainingHours = primarySLATime % 24;
                                            aCase.setNextFollowupDate(localDate.plusDays(days));
                                            aCase.setNextFollowupTime(localTime.plusHours(remainingHours));
                                            ApplicationLogger.logger.debug("Set next follow-up to {} days and {} hours from now; module: {}", days, remainingHours, SUBMODULE);
                                        }else if(localTime.getHour()+primarySLATime>=24){
                                            long totalHours = localTime.getHour() + primarySLATime;
                                            long days = totalHours / 24;
                                            long remainingHours = totalHours % 24;
                                            aCase.setNextFollowupDate(localDate.plusDays(days));
                                            aCase.setNextFollowupTime(localTime.plusHours(remainingHours - localTime.getHour()));
                                            ApplicationLogger.logger.debug("Set next follow-up to {} days and {} hours from now (crossing midnight); module: {}", days, remainingHours - localTime.getHour(), SUBMODULE);
                                        }else{
                                            aCase.setNextFollowupTime(localTime.plusHours(primarySLATime));
                                            aCase.setNextFollowupDate(LocalDate.now());
                                            ApplicationLogger.logger.debug("Set next follow-up time to {} minutes from now; module: {}", primarySLATime, SUBMODULE);
                                        }

                                    } else if (primarySLAUnit.equals("Min")) {
                                        aCase.setNextFollowupDate(localDate);
                                        aCase.setNextFollowupTime(localTime.plusMinutes(primarySLATime));
                                    }
                                    try {
                                        caseRepository.save(aCase);
                                        ApplicationLogger.logger.info("saved updated case for update FollowUp Date And Time For Ticket After Picked Up ; Module: {}", SUBMODULE);
                                    } catch (Exception e) {
                                        ApplicationLogger.logger.error("error while saving case entity: {}; module: {}", e.getMessage(), SUBMODULE);
                                        throw new RuntimeException(e);
                                    }
                                }

                            }
                        }

                    }
                } else {
                    ApplicationLogger.logger.error("TicketTatMatrix not found for case category: {}; module: {}", aCase.getCaseCategoryId(), SUBMODULE);
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "There might be an issue in TAT selection query condition !! after correction please try again", null);
                }
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("CustomValidationException occurred: {}; module: {}", e.getMessage(), SUBMODULE);
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        }
    }


    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, Case caseDTO) {
        String SUBMODULE = getModuleNameForLog() + " [checkTicketTatCondition()] ";
        List<QueryFieldMapping> queryFieldMappingList = new ArrayList<>();
        for (TatQueryFieldMapping queryFieldMapping : tatQueryFieldMappingList) {
            QueryFieldMapping mapping = new QueryFieldMapping();
            mapping.setQueryOperator(queryFieldMapping.getQueryOperator());
            mapping.setQueryCondition(queryFieldMapping.getQueryCondition());
            mapping.setQueryValue(queryFieldMapping.getQueryValue());
            mapping.setQueryField(queryFieldMapping.getQueryField());
            queryFieldMappingList.add(mapping);
        }
        if (CollectionUtils.isEmpty(queryFieldMappingList))
            return false;
        //RabbitMq Call
        // return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);
        return false;  //this is added as defaut return statment ,remove it after above is done
    }

    public TicketTatMatrix getTicketTatMatrixFromSubReasonId(CaseDTO caseDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getTicketTatMatrixFromSubReasonId()] ";
        if(caseDTO.getCaseCategoryId() == null){
            ApplicationLogger.logger.error(" case categoryId is getting null for case : {}; Module: {};",caseDTO.getCaseId(),SUBMODULE);
        }
        Optional<CaseCategory> ticketSubReasonCategory = caseCategoryRepository.findById(caseDTO.getCaseCategoryId());
        if (ticketSubReasonCategory.isPresent()) {
            if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getCaseCategoryTatMappingList())) {
                List<CaseCategoryTatMapping> caseCategoryTatMappings = ticketSubReasonCategory.get().getCaseCategoryTatMappingList();
                for (CaseCategoryTatMapping caseCategoryTatMapping : caseCategoryTatMappings) {
                    QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                    BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(caseCategoryTatMapping.getId().intValue()));
                    List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                    if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                        //If query not matched then skip
                        if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                            continue;
                    }
                    TicketTatMatrix masterTicketTat = caseCategoryTatMapping.getTicketTatMatrix();
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    ApplicationLogger.logger.debug("Retrieved TicketTatMatrix for caseCategoryId: {}; module: {}", caseDTO.getCaseCategoryId(), SUBMODULE);
                    return masterTicketTat;
                }
            }
        }
        ApplicationLogger.logger.debug("No TicketTatMatrix found for caseCategoryId: {}; module: {}", caseDTO.getCaseCategoryId(), SUBMODULE);
        return null;
    }

    public GenericDataDTO getTatDetails(Long caseId) {
        String SUBMODULE = getModuleNameForLog() + " [getTatDetails()] ";
        if(caseId == null){
            ApplicationLogger.logger.error("caseId is for get Tat details; Module : {}", SUBMODULE);
        }
        Case aCase = caseRepository.findById(caseId).orElse(null);
        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (aCase != null) {
            Optional<CaseCategory> ticketSubReasonCategory = caseCategoryRepository.findById(caseDTO.getCaseCategoryId());
            if (ticketSubReasonCategory.isPresent()) {
                if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getCaseCategoryTatMappingList())) {
                    List<CaseCategoryTatMapping> caseCategoryTatMappings = ticketSubReasonCategory.get().getCaseCategoryTatMappingList();
                    for (CaseCategoryTatMapping caseCategoryTatMapping : caseCategoryTatMappings) {
                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(caseCategoryTatMapping.getId().intValue()));
                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                            //If query not matched then skip
                            if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                                continue;
                        }
                        TicketTatMatrix masterTicketTat = caseCategoryTatMapping.getTicketTatMatrix();
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                        genericDataDTO.setData(masterTicketTat);
                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        ApplicationLogger.logger.debug("Retrieved TicketTatMatrix for caseCategoryId: {}; module: {}", caseDTO.getCaseCategoryId(), SUBMODULE);
                        return genericDataDTO;
                    }
                }
            }else{
                ApplicationLogger.logger.error("Case Category not found for get Tat details; Module : {}", SUBMODULE);
                genericDataDTO.setResponseMessage("Case Category not found");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }

        }else{
            ApplicationLogger.logger.error("Case not found for get Tat details; Module : {}", SUBMODULE);
            genericDataDTO.setResponseMessage("Case not found");
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            return genericDataDTO;
        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;

    }


    public void sendTicketRescheduleMessege(String staffusername, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String nextFollowupDate, String nextFollowUpTime) {
        String SUBMODULE = "[sendTicketRescheduleMessege()] ";
        try {
            if (caseNumber!=null && staffusername!=null){
                ApplicationLogger.logger.debug("ticket reschedule message process for staffUsername: {}; caseNumber: {}; module: {}", staffusername, caseNumber, SUBMODULE);
            }
            String followUpDateAndTime = nextFollowupDate + "," + nextFollowUpTime;

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_RESCHEDULE_MESSAGE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    Long buId = null;
                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                        buId = getBUIdsFromCurrentStaff().get(0);
                    }
                    TicketRescheduleMsg ticketRescheduleMsg = new TicketRescheduleMsg(staffusername, mobileNumber, emailId, RMQConstants.TICKET_RESCHEDULE_SUCCESS_MSG, optionalTemplate.get(), caseNumber, followUpDateAndTime, mvnoId, buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketRescheduleMsg);
//                    messageSender.send(ticketRescheduleMsg, RMQConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG);
                    kafkaMessageSender.send(new KafkaMessageData(ticketRescheduleMsg, TicketRescheduleMsg.class.getSimpleName()));
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                ApplicationLogger.logger.error(" Ticket is not assigned. Template for ticket reschedule message is not present; module: {}",SUBMODULE);
                System.out.println("Ticket is not assigned.");
            }


        } catch (Throwable e) {
            ApplicationLogger.logger.error("Error during processing ticket reschedule message; Message : {}; module: {}", e.getMessage(), SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<StaffUserPojo> findAllStaffUser(Integer serviceAreaId) {
        String SUBMODULE = getModuleNameForLog() + " [findAllStaffUser()] ";
        if (serviceAreaId!=null)
            ApplicationLogger.logger.debug("Starting findAllStaffUser for serviceAreaId: {}; module: {}", serviceAreaId, SUBMODULE);
        try {
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            BooleanExpression exp = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId));
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = (List<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(exp);
            List<Integer> serviceArea = new ArrayList<>();
            if (staffUserServiceAreaMappingList.size() > 0) {
                for (StaffUserServiceAreaMapping ids : staffUserServiceAreaMappingList) {
                    Integer num = ids.getStaffId();
                    serviceArea.add(num);
                }
            }
            QStaffUser qStaffUser = QStaffUser.staffUser;
            BooleanExpression exp1 = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false).and(qStaffUser.id.in(serviceArea)));
            List<StaffUser> staffUserList = (List<StaffUser>) staffUserRepository.findAll(exp1);
            ApplicationLogger.logger.debug("Retrieved {} StaffUsers for serviceArea; module: {}",staffUserList.size(), SUBMODULE);
            return staffUserService.convertResponseModelIntoPojo(staffUserList);

        } catch (Exception e) {
            ApplicationLogger.logger.error("Error in findAllStaffUser by service area; Message : {} for serviceAreaId: {}; module: {}", e.getMessage(),serviceAreaId, SUBMODULE);
            throw new RuntimeException(e);
        }
    }


    public GenericDataDTO linkBulkTicket(List<Integer> caseUpdateDTOList, Integer taskId) {
        String SUBMODULE = getModuleNameForLog() +" [linkBulkTicket()] ";
        if(taskId!=null)
            ApplicationLogger.logger.debug("linkBulkTicket with taskId: {}; module: {}", taskId, SUBMODULE);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        // Validate input
        if (caseUpdateDTOList == null || caseUpdateDTOList.isEmpty() || taskId == null) {
            ApplicationLogger.logger.error("Invalid input: link ticket id or taskId cannot is null; module: {}", SUBMODULE);
            throw new IllegalArgumentException("Invalid input: link ticket id or taskId cannot be null or empty.");
        }

        List<ExternalTicketLinkMapping> externalTicketLinkMappingList = caseUpdateDTOList.stream()
                .map(caseId -> {
                    ExternalTicketLinkMapping mapping = new ExternalTicketLinkMapping();
                    mapping.setLinkedTicketId(caseId);
                    mapping.setTaskId(taskId);
                    return mapping;
                })
                .collect(Collectors.toList());

        // Save the mappings if the list is not empty
        if (!externalTicketLinkMappingList.isEmpty()) {
            externalTicketLinkRepository.saveAll(externalTicketLinkMappingList);
            ApplicationLogger.logger.info("Saved {} ticket link mappings to the repository; module: {}", externalTicketLinkMappingList.size(), SUBMODULE);
        }
        // Build and return the response
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        ApplicationLogger.logger.debug("link bulk ticket completed successfully with ResponseCode: {}; ResponseMessage: {};",genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        return genericDataDTO;
    }

//    public GenericDataDTO reassignTicketInBulk(List<Long> caseIds) throws Exception {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        for (int i = 0; i < caseIds.size(); i++) {
//            Case aCase = caseRepository.findById(caseIds.get(i)).orElse(null);
//            List<StaffUserPojo> staffUserList = new ArrayList<>();
//            if (aCase.getCurrentAssignee().getParentStaffId() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                if (staffUser.getParentStaffId() != null) {
//                    if (staffUser.getParentStaffId() == getLoggedInUserId()) {
//                        //genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        } else {
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "WorkFlow not found for this ticket !!", null);
//                        }
//                    } else {
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent staff can reassign bulk tickets !!", null);
//                    }
//
//                } else {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent staff can reassign bulk tickets !!", null);
//                }
//            }
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

    public GenericDataDTO reassignTaskInBulk(List<Long> caseIds) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        for (int i = 0; i < caseIds.size(); i++) {
//            Case aCase = caseRepository.findById(caseIds.get(i)).orElse(null);
//            List<StaffUserPojo> staffUserList = new ArrayList<>();
//            if (aCase.getCurrentAssignee().getParentStaffId() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                if (staffUser.getParentStaffId() != null) {
//                    if (staffUser.getParentStaffId() == getLoggedInUserId()) {
//                        //genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        } else {
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "WorkFlow not found for this ticket !!", null);
//                        }
//                    } else {
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent staff can reassign bulk tickets !!", null);
//                    }
//
//                } else {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only Parent staff can reassign bulk tickets !!", null);
//                }
//            }
//        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;
    }

    public GenericDataDTO filterCase(String filter, PaginationRequestDTO requestDTO) {
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        QHierarchy qHierarchy = QHierarchy.hierarchy;
        QCase qCase = QCase.case$;
        BooleanExpression booleanExpression = qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isFromCalender.eq(false));
        if (filter.equalsIgnoreCase(CaseConstants.ASSIGNED_TO_ME)) {
            booleanExpression = booleanExpression.and(qCase.currentAssignee.id.eq(getLoggedInUserId()));
        }
        if (filter.equalsIgnoreCase(CaseConstants.ASSIGN_TO_MY_TEAM)) {
            List<Long> teamIdList = teamsRepository.findAllByStaff(getLoggedInUserId());
            if(Objects.nonNull(teamIdList)){
                List<Integer>  idList=teamIdList.stream().map(i->i.intValue()).collect(Collectors.toList());
                booleanExpression = booleanExpression.and(qCase.teamId.in(idList));
                booleanExpression=booleanExpression.and(qCase.caseStatus.equalsIgnoreCase(CaseConstants.TASK_STATUS_OPEN)
                        .or(qCase.caseStatus.equalsIgnoreCase(CaseConstants.TASK_STATUS_IN_PROGRESS)));
            }

//            List<Long> teamHierarchyMappingList = new ArrayList<>();
//            BooleanExpression booleanExpression1 = qHierarchy.isNotNull().and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.eventName.equalsIgnoreCase("Task"));
//            List<Hierarchy> hierarchyList = IterableUtils.toList(hierarchyRepository.findAll(booleanExpression1));
//            for (Hierarchy hierarchy : hierarchyList) {
//                for (TeamHierarchyMapping teamMapping : hierarchy.getTeamHierarchyMappingList()) {
//                    if (teamIdList.contains(teamMapping.getTeamId().longValue())) {
//                        teamHierarchyMappingList.add(teamMapping.getId().longValue());
//                    }
//                }
//            }

        }
        if (filter.equalsIgnoreCase(CaseConstants.UNPICKED)) {
            QTicketAssignStaffMapping qTicketAssignStaffMapping = QTicketAssignStaffMapping.ticketAssignStaffMapping;
            List<Long> teamIdList = teamsRepository.findAllByStaff(getLoggedInUserId());
            List<TeamUserMapping> teamUserMappings = teamUserMappingsRepository.findAllByTeamIdIsIn(teamIdList);

            List<Integer> integerList = teamUserMappings.stream().map(i -> i.getStaffId().intValue()).collect(Collectors.toList());
            BooleanExpression booleanExpression1 = qTicketAssignStaffMapping.isNotNull()
                    .and(qTicketAssignStaffMapping.staffId.in(integerList));
            //booleanExpression=booleanExpression.and()
            List<TicketAssignStaffMapping> ticketAssignStaffMappings = IterableUtils.toList(ticketAssignStaffMappingRepo.findAll(booleanExpression1));
            List<Long> caseidlist = ticketAssignStaffMappings.stream().map(i -> i.getTicketId()).collect(Collectors.toList());
            List<Integer> idList = teamIdList.stream().map(Long::intValue).collect(Collectors.toList());

            booleanExpression=booleanExpression.and(qCase.currentAssignee.isNull());
            booleanExpression=booleanExpression.and(qCase.isFromCalender.eq(false));
            booleanExpression = booleanExpression.and(qCase.caseId.in(caseidlist).and(qCase.teamId.in(idList)));
            booleanExpression = booleanExpression.and(qCase.caseStatus.equalsIgnoreCase(CaseConstants.TASK_STATUS_OPEN));
            booleanExpression=booleanExpression.and(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()));
        }
        Page<Case> paginationList = caseRepository.findAll(booleanExpression, pageRequest);
        paginationList = setMvnoName(paginationList);
        paginationList.getContent().forEach(item -> {
            if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getParentStaffId() != null) {
                item.setParentId(item.getCurrentAssignee().getParentStaffId().longValue());
            }
        });
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }


    public GenericDataDTO getChildTickets(Long caseId) {
        String SUBMODULE = "[getChildTickets] ";
        if(caseId!=null)
            ApplicationLogger.logger.debug("Starting getChildTickets for caseId: {}; module: {}", caseId, SUBMODULE);
        //Case aCase = caseRepository.findById(caseId).orElse(null);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QCase qCase = QCase.case$;
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.parentTicketId.eq(caseId.intValue()));
        List<Case> childCaseList = (List<Case>) caseRepository.findAll(booleanExpression);
        List<CaseDTO> caseDTOS = new ArrayList<>();
        for (Case childCase : childCaseList) {
            caseDTOS.add(caseMapper.domainToDTO(childCase, new CycleAvoidingMappingContext()));
        }
        System.out.println("CASE_DTO$" + caseDTOS);
        if (childCaseList.size() > 0) {
            genericDataDTO.setDataList(caseDTOS);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } else {
            ApplicationLogger.logger.debug("No child tickets linked with this ticket Case");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("No child tickets linked with this ticket !!!!");
        }

        return genericDataDTO;

    }

    public void getCaseDataFromStrig(CaseDTO entity) {
        String SUBMODULE = getModuleNameForLog() +  " [getCaseDataFromStrig] ";
        Case acase = new Case();

        try {
            acase = caseRepository.findById(entity.getCaseId()).orElse(null);
            if(Objects.isNull(acase)){
                ApplicationLogger.logger.warn("No case found for caseId: {}; module: {}", entity.getCaseId(), SUBMODULE);
                return;
            }
            List<CaseUpdateDTO> caseUpdateList = entity.getCaseUpdateList();
            if (caseUpdateList != null) {
                ApplicationLogger.logger.debug("Processing {} case updates for caseId: {}; module: {}", caseUpdateList.size(), entity.getCaseId(), SUBMODULE);
                List<CaseUpdateDTO> caseUpdateDTOS = new ArrayList<>();
                for (CaseUpdateDTO caseUpdateDTO : caseUpdateList) {
                    CaseUpdateDTO caseUpdateDTOSlist = new CaseUpdateDTO(caseUpdateDTO);
                    caseUpdateDTOS.add(caseUpdateDTOSlist);
                }

         /*   caseUpdateList.get(0).setCreateDateString(null);
            caseUpdateList.get(0).setCreatedate(null);
            caseUpdateList.get(0).setUpdatedate(null);*/

                //List<TicketServicemapping> ticketServicemappingList = entity.getTicketServicemappingList();
                List<TicketAssignStaffMapping> ticketAssignStaffMappings = entity.getTicketAssignStaffMappings();

                //TicketReasonCategory ticketReasonCategory = ticketReasonCategoryRepo.findById(acase.getTicketReasonCategoryId()).orElse(null);

                CaseCategory caseCategory = caseCategoryRepository.findById(acase.getCaseCategoryId()).orElse(null);

                //acase.setTicketReasonCategoryName(ticketReasonCategory.getCategoryName());
                //acase.setReasonSubCategoryName(caseCategory.getCategoryName());


                TicketMessageIntegration ticketMessageIntegration = new TicketMessageIntegration(acase, caseUpdateDTOS, null);  //ticketAssignStaffMappings
                //messageSender.send(ticketMessageIntegration, RMQConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM);
            }else {
            ApplicationLogger.logger.debug("No case updates found for caseId: {}; module: {}", entity.getCaseId(), SUBMODULE);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("error in getCaseDataFromString for caseId: {}; Error: {}; module: {}", entity.getCaseId(), e.getMessage(), SUBMODULE);
            e.printStackTrace();
        }
    }


    public Map<String, Object> getDetailsforTatWorkflowDetailsEntry(CaseDTO caseDTO, Boolean isPickeddUp) {
        String SUBMODULE = getModuleNameForLog() +  "[getDetailsforTatWorkflowDetailsEntry] ";
        Map<String, Object> map = new HashMap<>();
        if (caseDTO.getTeamHierarchyMappingId() != null) {
            Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(caseDTO.getTeamHierarchyMappingId().intValue());
            if (teamHierarchyMapping.isPresent()) {
                map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
                map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.CASE);
                map.put("eventId", caseDTO.getCaseId().toString());
                map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
                if (isPickeddUp)
                    map.put("fromPickedUp", "true");
                else
                    map.put("fromPickedUp", "false");
                // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
            }else {
                ApplicationLogger.logger.error("No TeamHierarchyMapping found for id: {}; module: {}", caseDTO.getTeamHierarchyMappingId(), SUBMODULE);
            }
        }else {
            ApplicationLogger.logger.error("No TeamHierarchyMappingId present for caseId: {}; module: {}", caseDTO.getCaseId(), SUBMODULE);
        }
        return map;
    }

    public void saveTATAudit(Map<String, Object> data) {
        String SUBMODULE = getModuleNameForLog()+" [saveTATAudit] ";
        if (data == null || data.isEmpty()) {
            ApplicationLogger.logger.warn("No data provided to save TAT audit; module: {}",SUBMODULE);
        }
        try {
            TicketTatAudits tatAudits = new TicketTatAudits();
            tatAudits.setCaseId(((Double) data.get("tatAudit_caseId")).intValue());
            tatAudits.setCaseStatus(data.get("tatAudit_caseStatus").toString());
            tatAudits.setTatTime(((Double) data.get("tatAudit_tatTime")).intValue());
            tatAudits.setTatAction(data.get("tatAudit_tatAction").toString());
            tatAudits.setTatUnit(data.get("tatAudit_tatUnit").toString());
            tatAudits.setSlaTime(((Double) data.get("tatAudit_slaTime")).intValue());
            tatAudits.setSlaUnit(data.get("tatAudit_slaUnit").toString());
            tatAudits.setTatStartTime(data.get("tatAudit_tatStartTime").toString());

            tatAudits.setAssignStaffId(((Double) data.get("tatAudit_assignStaffId")).intValue());
            tatAudits.setAssignStaffParentId((Integer) data.get("tatAudit_assignParentStaffId"));
            ;
            tatAudits.setCaseLevel(data.get("tatAudit_caseLevel").toString());
            tatAudits.setNotificationFor(data.get("tatAudit_notificationFor").toString());
            tatAudits.setIsTatBreached(data.get("tatAudit_isTatBreached").toString());
            tatAudits.setIsSlaBreached(data.get("tatAudit_isSlaBreaced").toString());
            if (tatAudits != null) {
                tatAudits.setTatMessage((String) data.get("notificationMessage"));
                tatAudits.setMessageMode(data.get("notificationMode").toString());
                tatAudits.setMessageStatus(data.get("notificationStatus").toString());
                tatAuditRepository.save(tatAudits);
            }
            ApplicationLogger.logger.info("TAT audit saved successfully for case ID: {};module: {}", tatAudits.getCaseId(), SUBMODULE);
        }catch(Exception ex){
            ApplicationLogger.logger.error("Error while saving TAT audit: {};module: {}", ex.getMessage(),SUBMODULE );
        }
    }


//    public GenericDataDTO getTatAuditDetails(Long caseId) throws Exception {
//        String SUBMODULE = getModuleNameForLog() + " [getTatAuditDetails()] ";
//        ApplicationLogger.logger.debug("Fetching TAT audit details for caseId: {}; Module : {};", caseId,SUBMODULE);
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        if (aCase == null) {
//            ApplicationLogger.logger.warn("Case not found for caseId: {}; Module : {};", caseId,SUBMODULE);
//            throw new Exception("Case not found");
//        }
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<TicketTatAudits> ticketTatAuditsList = new ArrayList<>();
//        ticketTatAuditsList = tatAuditRepository.findAllByCaseId(Math.toIntExact(aCase.getCaseId()));
//        ApplicationLogger.logger.debug("Found {} TAT audits for caseId: {}; Module : {};", ticketTatAuditsList.size(), aCase.getCaseId(),SUBMODULE);
//        for (TicketTatAudits tatAuditDetails : ticketTatAuditsList) {
//            StaffUser staffUser = staffUserRepository.findById(tatAuditDetails.getAssignStaffId()).orElse(null);
//            tatAuditDetails.setStaffName(staffUser.getFullName());
//        }
//        if (ticketTatAuditsList.size() > 0) {
//            genericDataDTO.setDataList(ticketTatAuditsList);
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            ApplicationLogger.logger.debug("Successfully retrieved TAT audit details for caseId: {}; Module : {};", caseId,SUBMODULE);
//        }else {
//            ApplicationLogger.logger.debug("No TAT audit details found for caseId: {}; Module : {};", caseId,SUBMODULE);
//        }
//        return genericDataDTO;
//    }

    public GenericDataDTO getTatAuditDetails(Long caseId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getTatAuditDetails()] ";
        ApplicationLogger.logger.debug("Fetching TAT audit details for caseId: {}; Module : {};", caseId, SUBMODULE);
        if (!caseRepository.existsById(caseId)) {
            ApplicationLogger.logger.warn("Case not found for caseId: {}; Module : {};", caseId, SUBMODULE);
            throw new Exception("Case not found");
        }
        List<Object[]> results = tatAuditRepository.findTatAuditWithStaffName(Math.toIntExact(caseId));
        List<TicketTatAudits> ticketTatAuditsList = new ArrayList<>();
        for (Object[] row : results) {
            TicketTatAudits tatAudit = (TicketTatAudits) row[0];
            String staffName = (String) row[1];
            tatAudit.setStaffName(staffName);
            ticketTatAuditsList.add(tatAudit);
        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(ticketTatAuditsList);
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        ApplicationLogger.logger.debug("Successfully retrieved {} TAT audit details for caseId: {}; Module : {};", ticketTatAuditsList.size(), caseId, SUBMODULE);
        return genericDataDTO;
    }


    public void saveSelfCareTicket(TicketMessageIntegration message) {
        try {
            System.out.println("Selfcare Ticket message :-> " + message);
            CaseDTO selfCareCaseDTO = convertSelfCareCreateCaseRequestToCaseDTO(message);
            caseService.saveEntity(selfCareCaseDTO, null);
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }

    private CaseDTO convertSelfCareCreateCaseRequestToCaseDTO(TicketMessageIntegration message) {
        String SUBMODULE = getModuleNameForLog()+"[convertSelfCareCreateCaseRequestToCaseDTO] ";
        if(message.getCaseId() != null)
            ApplicationLogger.logger.debug("Starting convertSelfCareCreateCaseRequestToCaseDTO with data id: {}; module: {}", message.getCaseId(), SUBMODULE);
        try {
            CaseDTO selfCareCaseDTO = new CaseDTO();
            selfCareCaseDTO.setCreatedFrom("Selfcare CWSC");
            selfCareCaseDTO.setCaseTitle(message.getCaseTitle());
            selfCareCaseDTO.setCaseType(message.getCaseType());
            selfCareCaseDTO.setCaseFor(message.getCaseFor());
            selfCareCaseDTO.setCaseOrigin(message.getCaseOrigin());
            selfCareCaseDTO.setCaseStatus(message.getCaseStatus());
            selfCareCaseDTO.setPriority(message.getPriority());
            //selfCareCaseDTO.setStaffId(message.getCustomers());
            selfCareCaseDTO.setCaseForPartner(message.getCaseForPartner());
            selfCareCaseDTO.setNextFollowupDate(LocalDate.parse(message.getNextFollowupDate()));
            selfCareCaseDTO.setNextFollowupTime(LocalTime.parse(message.getNextFollowupTime()));
            selfCareCaseDTO.setFirstRemark(message.getFirstRemark());
            selfCareCaseDTO = setCustomerSpecificCaseParams(selfCareCaseDTO, message);
            selfCareCaseDTO = setReasonAndSubReasonCategory(selfCareCaseDTO);
            selfCareCaseDTO.setSource(message.getSource());
            selfCareCaseDTO.setSubSource(message.getSubSource());
            selfCareCaseDTO.setDepartment(message.getDepartment());
            //selfCareCaseDTO.setStaffAdditionalEmail(message.getCustomerAdditionalEmail());
            //setStaffAdditionalMobileNumber(message.getCustomerAdditionalMobileNumber());
            selfCareCaseDTO.setCase_order(1L);
            //selfCareCaseDTO.setTicketServicemappingList(message.getTicketServicemappingList());
            return selfCareCaseDTO;

        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error("Custom validation error while converting message to CaseDTO; Message : {}; Module : {};", ex.getMessage(),SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Unexpected error while converting message to CaseDTO; Message : {}; Module : {};", ex.getMessage(),SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);

        }
    }

    private CaseDTO setReasonAndSubReasonCategory(CaseDTO selfCareCaseDTO) {
        String SUBMODULE = getModuleNameForLog()+"[setReasonAndSubReasonCategory]";
        try {
            //List<TicketReasonCategory> reasonCategoryList = ticketReasonCategoryRepo.findAllByCategoryNameContainingIgnoreCase("Default");
            List<CaseCategory> caseCategoryList = caseCategoryRepository.findAllByCategoryNameEqualsIgnoreCase("Default");
            if (caseCategoryList != null) {
                //selfCareCaseDTO.setTicketReasonCategoryId(reasonCategoryList.get(0).getId());
                selfCareCaseDTO.setCaseCategoryId(caseCategoryList.get(0).getCategoryId());
                ApplicationLogger.logger.debug("Successfully set case category ID: {},module: {}", selfCareCaseDTO.getCaseCategoryId(),SUBMODULE);
            }
            return selfCareCaseDTO;
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error("Custom validation error while setting case category ID: {};error message: {}; module: {}", selfCareCaseDTO.getCaseId(),ex.getMessage(),SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Unexpected error while setting case category ID: {} ;error message: {}; module: {}", selfCareCaseDTO.getCaseId(),ex.getMessage(),SUBMODULE);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }

    private CaseDTO setCustomerSpecificCaseParams(CaseDTO selfCareCaseDTO, TicketMessageIntegration message) {
        try {
            Customers customer = customersService.getByUserName(message.getUserName());
            if (customer != null) {
                if (customer.getServiceAreaId() != null) {
                    selfCareCaseDTO.setServiceAreaId(Long.valueOf(customer.getServiceAreaId()));
                    selfCareCaseDTO.setServiceAreaName(customer.getServiceAreaName());
                }
                selfCareCaseDTO.setMobile(customer.getMobile());
                selfCareCaseDTO.setUserName(customer.getUsername());
                //selfCareCaseDTO.setStaffName(customer.getCustname());
                if (customer.getParnterId() != null) {
                    selfCareCaseDTO.setPartnerid(customer.getParnterId());
                    selfCareCaseDTO.setPartnerName(customer.getPartnerName());
                }
                if (customer.getMvnoId() != null) {
                    selfCareCaseDTO.setMvnoId(customer.getMvnoId());
                }
                if (customer.getBuId() != null) {
                    selfCareCaseDTO.setBuId(customer.getBuId());
                }
                if (customer.getLcoId() != null) {
                    selfCareCaseDTO.setLcoId(customer.getLcoId());
                }
                return selfCareCaseDTO;
            }
            return selfCareCaseDTO;
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }

    public CaseDTO findCaseByCaseId(Long ticketid) {
        String SUBMODULE = getModuleNameForLog() + " [findCaseByCaseId] ";
        if(ticketid == null) {
            ApplicationLogger.logger.debug("ticket Id is getting null; Module : {}",SUBMODULE);
        }
        CaseDTO ticket = new CaseDTO();
        ticket = caseMapper.domainToDTO(caseRepository.findById(ticketid).orElse(null), new CycleAvoidingMappingContext());
        return ticket;
    }

    public CaseDocDetails updateDocumentDetailsFromEmail(CaseDocDetails caseDoc, MultipartFile file) throws Exception {
        String SUBMODULE = getModuleNameForLog()+"[updateDocumentDetailsFromEmail] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
        if (null != caseDoc && file != null) {
            String path = PATH;
            ApplicationLogger.logger.debug("Setting document status to Active; module: {}", SUBMODULE);
            caseDoc.setDocStatus("Active");
            MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
            if (file1 != null) {
                caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
                caseDoc.setFilename(file1.getOriginalFilename());
                caseDoc = caseDocDetailsRepository.save(caseDoc);
                ApplicationLogger.logger.info("Document details saved successfully; module: {}", SUBMODULE);
            }
        }
        ApplicationLogger.logger.debug("Exiting updateDocumentDetailsFromEmail method; module: {}", SUBMODULE);
        return caseDoc;
    }


//    @Transactional
//    public CaseDTO saveEntityWithAttchment(CaseDTO entity, List<MultipartFile> file) throws Exception {
//        try {
//
//            if (entity.getFirstRemark() != null) {
//                if (entity.getFirstRemark().length() > 0) {
//                    entity.setFirstRemark(caseUpdateService.trimToMaxLength(entity.getFirstRemark(), 4000));
//                }
//            }
//            boolean flag = duplicateVerifyDomainAtSave(entity);
//            Customers customers;
//            String ticketCreatedFrom = null;
//            if (!flag) {
//                if (entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC")) {
//                    customers = customersService.getcustForCwsc(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                } else if (entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("EMAIL")) {
//                    customers = customersService.getcustForEmail(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                } else {
//                    customers = customersService.get(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                }
//
//                if (entity.getCreatedFrom() != null && ((entity.getCreatedFrom().equalsIgnoreCase("CWSC") || entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC") || entity.getCreatedFrom().equalsIgnoreCase("EMAIL")))) {
//                    List<GrantedAuthority> role_name = new ArrayList<>();
//                    List<Long> buid = new ArrayList<>();
//                    buid.add(customers.getBuId());
//                    entity.setBuId(customers.getBuId());
//                    role_name.add(new SimpleGrantedAuthority("ADMIN"));
//                    String mvnoName = null;
//                    if (customers.getMvnoId() != null) {
//                        mvnoName = mvnoRepository.findMvnoNameById(Long.valueOf(customers.getMvnoId()));
//                    }
//                    LoggedInUser user = new LoggedInUser(customers.getUsername(), customers.getPassword(), true, true, true, true, role_name, customers.getFirstname(), customers.getLastname(), LocalDateTime.now(), customers.getId(), customers.getParnterId(), "ADMIN", customers.getServiceAreaId().longValue(), customers.getMvnoId(), null, customers.getId(), buid, false, new ArrayList<String>(), new ArrayList<Long>(), mvnoName);
//                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//
//
//                //Open complain communication
//
//                if (null != entity.getCustomersId()) {
//                    customers = customersService.get(entity.getCustomersId());
//
//                    if (customers != null && !customers.getIsDeleted()) {
//                        //TicketReasonCategory ticketReasonCategory = ticketReasonCategoryService.getRepository().findById(entity.getTicketReasonCategoryId()).orElse(null);
//                        //TicketReasonCategoryTATMapping ticketReasonCategoryTATMapping = ticketReasonCategory.getTicketReasonCategoryTATMappingList().stream().sorted(Comparator.comparing(TicketReasonCategoryTATMapping::getOrderNumber)).collect(Collectors.toList()).get(0);
////                        TicketReasonSubCategory ticketReasonSubCategory = ticketReasonSubCategoryRepo.findById(entity.getReasonSubCategoryId()).orElse(null);
////                        List<CaseCategoryTatMapping> ticketSubCategoryTatMapping = ticketSubCategoryTatMappingRepo.findByTicketReasonSubCategoryId(ticketReasonSubCategory.getId());
//
//                        if (!Objects.equals(entity.getPriority(), "High")) {
//                            entity.setPriority(checkTickePriority(entity, customers));
//                        }
//                        //if (ticketReasonCategoryTATMapping != null) {
//                        //entity.setTatMappingId(ticketReasonCategoryTATMapping.getMappingId());
//
//                        //Set Prefix
//                        String prefix = "";
//                        if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_ISSUE))
//                            prefix = CaseConstants.PREFIX_TKT;
//                        else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_REQUEST))
//                            prefix = CaseConstants.PREFIX_REQ;
//                        else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_INQUIRY))
//                            prefix = CaseConstants.PREFIX_INQ;
//
//
//                        //Set CaseNumber
//                        CaseDTO caseDTO = getCaseByCaseType(entity.getCaseType(), entity);
//                        if (null != caseDTO && caseDTO.getCaseNumber() != null) {
//                            String number = caseDTO.getCaseNumber().split("-")[1];
//                            entity.setCaseNumber(prefix + "-" + Integer.parseInt(String.valueOf(Long.parseLong(number) + 1)));
//                        } else entity.setCaseNumber(prefix + "-" + "1");
//
//                        if (entity.getNextFollowupDate() != null && entity.getNextFollowupTime() != null) {
//                            entity.setNextFollowupDate(entity.getNextFollowupDate());
//                            entity.setNextFollowupTime(entity.getNextFollowupTime());
//                            //send message frome here
//                            if (entity.getCreatedFrom() == null) {
//                                StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//
//                            }
//                        }
////                            } else {
////                                switch (ticketReasonCategoryTATMapping.getTimeUnit()) {
////                                    case "Day":
////                                        entity.setNextFollowupDate(LocalDate.now().plusDays(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
////                                        entity.setNextFollowupTime(LocalTime.now());
////                                        break;
////                                    case "Hour":
////                                        entity.setNextFollowupDate(LocalDate.now());
////                                        entity.setNextFollowupTime(LocalTime.now().plusHours(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
////                                        break;
////                                    case "Min":
////                                        entity.setNextFollowupDate(LocalDate.now());
////                                        entity.setNextFollowupTime(LocalTime.now().plusMinutes(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
////                                        break;
////                                }
////                            }
//                        if (entity.getMessageId() != null) {
//                            List<MultipartFile> multipartFileList = mailDocumentService.getMultipartfilelistfromMessageId(entity.getMessageId().toString());
//                            file = multipartFileList;
//                        }
//
//                        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TASK_PATH).get(0).getValue();
//
//                        if (null != caseDTO && file != null && file.size() > 0) {
//                            for (MultipartFile multipartFile : file) {
//                                CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
////                String subFolderName = aCase.getCaseNumber().trim().replace("-","_") + "/";
//                                //       PATH = "D:/";
//                                String path = PATH;
//                                caseDoc.setTicketId(Math.toIntExact(caseDTO.getCaseId()));
//                                caseDoc.setDocStatus("Active");
//                                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
//                                if (file1 != null) {
//                                    caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
//                                    caseDoc.setFilename(file1.getOriginalFilename());
//                                    caseDoc = caseDocDetailsService.saveEntity(caseDoc);
//                                }
//                            }
//
//
//                        }
//
//                        //Set helperName
//                        if (null != caseDTO && caseDTO.getHelperName() != null) {
//                            if (caseDTO.getHelperName().equals("")) {
//                                entity.setHelperName(caseDTO.getHelperName());
//                            }
//                        }
//                        entity.setCase_order(1L);
//
//                        String messageId = null;
//                        if (entity.getMessageId() != null) {
//                            messageId = entity.getMessageId();
//                        }
//
//                        entity = super.saveEntity(entity);
//
//                        if (messageId != null) {
//                            String remark = mailService.getRemarkFromMessageId(messageId);
//                            receiveMailService.createTicketFollowUpDetails(entity.getCaseId(), remark);
//                        }
//
//
//                        CaseUpdateDTO caseUpdateDTO = setFirstRemarkInUpdate(entity);
//                        StaffUser assignedUser;
//                        Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
//
//                        //getting lcoId from the customer
////                           if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
////                               customers.setLcoId(getLoggedInUser().getPartnerId());
////                           } else {
////                               customers.setLcoId(null);
////                           }
//                        // if(caseUpdateDTO.getTeamHierarchyMappingId() == null /*&& customers.getLcoId()==null*/){
//                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                                //   updateTicketLevel(entity,map,null);
//                                caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                                StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                                caseUpdateDTO.setAssignee(staffUser.getId());
//                                assignedUser = staffUser;
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                                TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                                Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                                String nextFollowupDate = entity.getNextFollowupDate().toString();
//                                String nextFollowupTime = entity.getNextFollowupTime().toString();
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), teams.getName(), nextFollowupDate, customers.getUsername(), nextFollowupTime, entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                                if (assignedUser.getParentStaffId() != null && !CollectionUtils.isEmpty(map)) {
//                                    tatUtils.saveOrUpdateTicketTatMatrix(entity, map, assignedUser, false);
//                                }
//                                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + entity.getCustomerName() + " '";
//                                Long buId = null;
//                                if (staffUser != null) {
//                                    if (!staffUser.getBusinessUnitNameList().isEmpty()) {
//                                        buId = staffUser.getBusinessUnitNameList().get(0).getId();
//                                    }
//                                }
//                                if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                    hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), entity.getMvnoId(), staffUser.getFullName(), action, entity.getBuId());
//                                }
//
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                            } else {
//                                StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//                                caseUpdateDTO.setAssignee(getLoggedInUserId());
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
////                                    caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//
//                            }
//                        } else {
//                            Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                            if (map.containsKey("assignableStaff")) {
//                                //updateTicketLevel(entity,null,map);
//                                List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//                                //caseUpdateDTO.setCase_order(caseUpdateDTO.getCase_order()+1);
//                                caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
//                                for (int i = 0; i < staffUserPojos.size(); i++) {
//                                    TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                    ticketAssignStaffMapping.setStaffId(staffUserPojos.get(i).getId());
//                                    ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                    ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                    workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojos.get(i).getId(), staffUserPojos.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojos.get(i).getUsername());
//                                    if (staffUserPojos.get(i).getParentStaffId() != null) {
//                                        staffByParentStaffId.put(staffUserPojos.get(i).getParentStaffId(), staffUserPojos.get(i));
//                                    }
//                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                    if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                        hierarchyService.sendWorkflowAssignActionMessage(staffUserPojos.get(i).getCountryCode(), staffUserPojos.get(i).getPhone(), staffUserPojos.get(i).getEmail(), entity.getMvnoId(), staffUserPojos.get(i).getFullName(), action, entity.getBuId());
//                                    }
//                                }
//                            } else {
//                                StaffUser staffUserPojo;
//                                if (ticketCreatedFrom != null && !ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                    staffUserPojo = staffUserService.get(customers.getCreatedById());
//                                } else if (ticketCreatedFrom != null && ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                    staffUserPojo = staffUserService.get(customers.getCreatedById());
//                                } else {
//                                    staffUserPojo = staffUserService.get(getLoggedInUserId());
//                                }
//                                TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
//                                ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
//                                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                Long buId = null;
//                                if (!getBUIdsFromCurrentStaff().isEmpty() && getBUIdsFromCurrentStaff() != null) {
//                                    buId = getBUIdsFromCurrentStaff().get(0);
//                                }
//                                if (!entity.getCaseStatus().equalsIgnoreCase("Raise and Close")) {
//                                    hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), entity.getMvnoId(), staffUserPojo.getFullName(), action, entity.getBuId());
//                                }
//                            }
//                            caseUpdateDTO.setAssignee(null);
//                            caseUpdateDTO.setStatus(entity.getCaseStatus());
//                            if (ticketCreatedFrom == null) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            } else if (ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            } else if (ticketCreatedFrom.equalsIgnoreCase("EMAIL")) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            }
//
//                            //caseUpdateService.sendCreateTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(),caseDTO.getCaseNumber());
//                        }
//                        //  }
////                           else{
////                               StaffUser staffUser = staffUserService.get(getLoggedInUserId());
////                               caseUpdateDTO.setAssignee(getLoggedInUserId());
////                               caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
////                               TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
////                               ticketAssignStaffMapping.setStaffId(getLoggedInUserId());
////                               ticketAssignStaffMapping.setTicketId(entity.getCaseId());
////                               ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
////                               caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString());
////                               //workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
////                           }
//
//
//                        TicketTatMatrix tatMatrix = getTicketTatMatrixFromSubReasonId(entity);
//                        if (tatMatrix != null) {
//                            if (entity.getPriority().equalsIgnoreCase("high")) {
//                                caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp1());
//                                caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep1()));
//                            } else if (entity.getPriority().equalsIgnoreCase("medium")) {
//                                caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp2());
//                                caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep2()));
//                            } else {
//                                caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp3());
//                                caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTime3()));
//                            }
//                        }
//                        entity = caseUpdateService.updateEntity(caseUpdateDTO, file, false);
//                        getCaseDataFromStrig(entity);
//
//                        if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
//                            for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
//                                StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
//                                tatUtils.changeTicketTatAssignee(entity, staffUser, true, false);
//                            }
//                        }
//
//                        //updating followup date and time based on tat which selected based on condition check
//                        if (!entity.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                            updateFollowUpDateAndTimeForTicketBeforePickedUp(caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                        } else {
//                            caseUpdateService.updateTatAtStatusChangeToFollowUp(null, entity);
//                            if (ticketCreatedFrom == null) {
//                                caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber().toString(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(), entity.getCustomerAdditionalEmail(), entity.getSerialNumber(), customers.getBuId());
//                            }
//                        }
//
//                        //}
////                    if (!ticketCreatedFrom.isEmpty() && ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
////                        Case aCase = caseRepository.findById(entity.getCaseId()).orElse(null);
////                        aCase.setMvnoId(customers.getMvnoId());
////                        aCase.setBuId(customers.getBuId());
////                        caseRepository.save(aCase);
////                    }
//                    } else {
//                        throw new DataNotFoundException("Customer Not Found!");
//                    }
//                } else {
//                    throw new DataNotFoundException("Customer is not found by given id : " + entity.getCustomersId());
//                }
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, "Ticket for this Problem Domain and Sub Problem Domain and Service is already Exist! ", null);
//                //Save Entry In Assignment Table
//            }
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            throw new CustomValidationException(APIConstants.FAIL, exception.getMessage(), null);
//        }
//
//        return entity;
//    }

    public TicketTatMatrix getTicketTatMatrixFromSubReasonIdForUpdate(CaseUpdateDTO caseDTO) {
        Optional<CaseCategory> ticketSubReasonCategory = caseCategoryRepository.findById(caseDTO.getCaseCategoryId());
        if (ticketSubReasonCategory.isPresent()) {
            if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getCaseCategoryTatMappingList())) {
                List<CaseCategoryTatMapping> caseCategoryTatMappings = ticketSubReasonCategory.get().getCaseCategoryTatMappingList();
                for (CaseCategoryTatMapping caseCategoryTatMapping : caseCategoryTatMappings) {
                    QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                    BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(caseCategoryTatMapping.getId().intValue()));
                    List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                    if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                        //If query not matched then skip
                        if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                            continue;
                    }
                    TicketTatMatrix masterTicketTat = caseCategoryTatMapping.getTicketTatMatrix();
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    return masterTicketTat;
                }
            }
        }
        return null;
    }

    public SendTicketDetailDTO convertCaseToSendTicketDetailDTO(Case ticket) {
        SendTicketDetailDTO sendTicketDetailDTO = new SendTicketDetailDTO();
        sendTicketDetailDTO.setCaseNumber(ticket.getCaseNumber());
        sendTicketDetailDTO.setCaseStatus(ticket.getCaseStatus());
        return sendTicketDetailDTO;
    }


    public List<Case> getAllCases() {
        String SUBMODULE = getModuleNameForLog() + " [getAllCases()] ";
        List<Case> caseList = null;

        caseList = caseRepository.findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(CommonConstants.ACTIVE_STATUS).stream().filter(cases -> cases.getMvnoId() == getMvnoIdFromCurrentStaff() || cases.getMvnoId() == null).collect(Collectors.toList());

        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        caseList = caseList.stream().filter(cases -> (cases.getMvnoId() == 1
                || getMvnoIdFromCurrentStaff() == 1
                || cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue())
                && (cases.getMvnoId() == 1
                || getBUIdsFromCurrentStaff().size() == 0
                || getBUIdsFromCurrentStaff().contains(cases.getBuId()))
                && (Objects.isNull(cases.getBuId()))).collect(Collectors.toList());

        if(caseList.size() > 0) {
            ApplicationLogger.logger.debug("Returning list of total cases : {} ; Module: {}",caseList.size(), SUBMODULE);
        }else{
            ApplicationLogger.logger.debug("No case found; Module: {}", SUBMODULE);
        }
        return caseList;
    }


    public List<AuditForResponseModel> getCaseListForAuditFor() {
        String SUBMODULE = MODULE + " [getCaseListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            List<Case> caseList = getAllCases();
            if (null != caseList && 0 < caseList.size()) {
                for (Case cases : caseList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(cases.getCaseId().intValue());
                    responseModel.setName(cases.getCaseTitle());
                    responseList.add(responseModel);
                }
            }else{
                ApplicationLogger.logger.debug("No case found for audit response; Module: {}", SUBMODULE);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Unable to get customer list for Audit response{};exception{}", APIConstants.FAIL, ex.getStackTrace());
            throw ex;
        }
        return responseList;
    }


    public Page<Case> setMvnoName(Page<Case> cases) {
        for (Case casse : cases) {
            casse.setMvnoName(mvnoRepository.findMvnoNameById(casse.getMvnoId().longValue()));
        }
        return cases;
    }


    public GenericDataDTO getCaseEntitiyById(Long id){
        String SUBMODULE = getModuleNameForLog() + " [getCaseEntitiyById()] ";
        try{
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if(id!=null){
                ApplicationLogger.logger.debug("Getting case entity by id : {} ; Module: {}", id, SUBMODULE);
                Case aCase = caseRepository.findById(id).orElse(null);
                CaseDTO caseDTO = caseMapper.domainToDTO(aCase,new CycleAvoidingMappingContext());
                CaseCategory caseCategory = caseCategoryRepository.getOne(caseDTO.getCaseCategoryId());
                CaseSubCategory caseSubCategory = caseSubCategoryRepository.getOne(caseDTO.getCaseSubCategoryId());
                List<CaseDocDetails> caseDocDetails= caseDocDetailsRepository.findAllByTicketId(aCase.getCaseId());
                caseDTO.setCaseDocDetails(caseDocDetails);
                if(caseCategory!=null && caseSubCategory!=null){
                    caseDTO.setCaseCategoryName(caseCategory.getCategoryName());
                    caseDTO.setCaseSubCategoryName(caseSubCategory.getSubCategoryName());
                }else{
                    ApplicationLogger.logger.error("Case category or sub category is null; CaseID : {};, Module: {}", id,SUBMODULE);
                }
                ApplicationLogger.logger.debug("found Case with id : {}; Module : {}",id,SUBMODULE);
                genericDataDTO.setData(caseDTO);
                return genericDataDTO;
            }else{
                ApplicationLogger.logger.error("Case id is null; Module: {}", SUBMODULE);
                throw new IllegalArgumentException("Case id is null");
            }


        }catch (Exception e){
            ApplicationLogger.logger.error("Unable to get case entity by id : {} ; Module: {}",(id !=null)?id:"not found",SUBMODULE);
            e.printStackTrace();
        }
        return null;
    }


    public GenericDataDTO  getAllCalanderCases(){
        String SUBMODULE = getModuleNameForLog() + " [getAllCalanderCases()] ";
        try{
            ApplicationLogger.logger.debug("Starting to get all calendar cases; Module: {}", SUBMODULE);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QCase qCase=QCase.case$;
            QCustomers qCustomers=QCustomers.customers;
            QStaffUser qStaffUser =QStaffUser.staffUser;
            QTeams qTeams=QTeams.teams;
            Set<Integer> childStaffList = staffUserRepository.findAllByParentStaffIdAndIsDeleteFalse(getLoggedInUserId());
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            BooleanExpression booleanExpression=qCase.isNotNull().and(qCase.isDelete.eq(false));
            booleanExpression=booleanExpression
                    .and(qCase.isFromCalender.eq(true))
                    .and(qCase.createdById.eq(getLoggedInUserId()))
                    .or(qCase.currentAssignee.id.in(childStaffList))
                    .or(qCase.currentAssignee.id.eq(getLoggedInUserId())).and(qCase.isDelete.eq(false));
            if(getLoggedInUser().getMvnoId()!=1){
                booleanExpression=booleanExpression.and(qCase.mvnoId.in(1,getLoggedInUser().getMvnoId()));
            }
            QueryResults<CaseDTO> queryResults = queryFactory
                    .select(Projections.constructor(
                            CaseDTO.class,
                            qCase.caseId,
                            qCase.caseTitle,
                            qCase.caseType,
                            qCase.caseNumber,
                            qCase.caseFor,
                            qCase.caseOrigin,
                            qCase.caseStatus,
                            qCase.priority,
                            qCase.startDate,
                            qCase.endDate,
                            qCase.customers.id,
                            qCase.teamId,
                            qTeams.name,
                            qStaffUser.firstname.concat(" ").concat(qStaffUser.lastname),
                            qCustomers.title.concat(" ").concat(qCustomers.firstname).concat(" ").concat(qCustomers.lastname),
                            qCase.currentAssignee.id,
                            qCase.isFromCalender,
                            qCase.firstRemark
                    ))
                    .from(qCase)
                    .leftJoin(qCase.customers, qCustomers)
                    .leftJoin(qTeams).on(qCase.teamId.eq(qTeams.id.intValue()))
                    .leftJoin(qStaffUser).on(qStaffUser.id.eq(qCase.currentAssignee.id))
                    .where(booleanExpression)
                    .orderBy(qCase.caseId.desc())
                    .fetchResults();
            List<CaseDTO> caseDTOList = queryResults.getResults();
            ApplicationLogger.logger.debug("Total number of calendar cases found: {}; Module: {}", caseDTOList.size(), SUBMODULE);
            genericDataDTO.setDataList(caseDTOList);
            return genericDataDTO;
        }catch (Exception e){
            ApplicationLogger.logger.error("Error while fetching all calendar cases; Message: {}; Module: {}", e.getMessage(), SUBMODULE);
            ApplicationLogger.logger.error(e.getMessage());
        }
        return null;
    }

    public GenericDataDTO getAllCalanderCaseByStaffIdAndCaseStatus(CalanderCasePojo calanderCasePojo){
        String SUBMODULE = getModuleNameForLog() + " [getAllCalanderCaseByStaffIdAndCaseStatus] ";
        try{
            ApplicationLogger.logger.debug("getting all calender cases by staffId and case status Module : {};",SUBMODULE);
            GenericDataDTO  genericDataDTO = new GenericDataDTO();
            if(calanderCasePojo.getCurrentAssigneeId()!=null){
                StaffUser staffUser = staffUserRepository.findById(calanderCasePojo.getCurrentAssigneeId()).orElse(null);
                if(staffUser!=null){
                    if(calanderCasePojo.getCaseStatus()!=null){
                        List<Case> caseList = caseRepository.findAllByIsDeleteIsFalseAndIsFromCalenderIsTrueAndCurrentAssigneeAndCaseStatus(staffUser, calanderCasePojo.getCaseStatus());
                        if(caseList!=null){
                            ApplicationLogger.logger.debug("Number of Calendar Cases found : {}; , by staffID : {}; and By CaseStatus : {}; Module : {}",caseList.size(),calanderCasePojo.getCurrentAssigneeId(),calanderCasePojo.getCaseStatus(),SUBMODULE);
                            genericDataDTO.setDataList(caseList);
                            genericDataDTO.setResponseCode(HttpStatus.OK.value());
                            return genericDataDTO;
                        }else{
                            ApplicationLogger.logger.error("No calendar case found for the selected staff and status , { Module : {}; }",SUBMODULE);
                            throw  new CustomValidationException(404,"No calander case found for the selected staff and status",null);
                        }
                    }else{
                        List<Case> caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseAndIsFromCalenderTrue(staffUser.getId());
                        if(caseList!=null){
                            ApplicationLogger.logger.debug("Number of Calender Cases found : {}; by staffID : {}; and Is From Calender : True; , Module : {}",caseList.size(),calanderCasePojo.getCurrentAssigneeId(),SUBMODULE);
                            genericDataDTO.setDataList(caseList);
                            genericDataDTO.setResponseCode(HttpStatus.OK.value());
                            return genericDataDTO;
                        }else{
                            ApplicationLogger.logger.error("No calender case found for the selected staff and status , { Module : {}; }",SUBMODULE);
                            throw  new CustomValidationException(404,"No calander case found for the selected staff",null);
                        }
                    }

                }else{
                    ApplicationLogger.logger.warn("StaffUser is getting null with StaffUser ID : {}; Module : {}",calanderCasePojo.getCurrentAssigneeId(),SUBMODULE);
                }
            }else{
                ApplicationLogger.logger.warn("current assignee ID is getting null , { Module : {}; }",SUBMODULE);
            }

        }catch (Exception e){
            ApplicationLogger.logger.error("getting error while fetching All calender cases { Message : {} }",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public GenericDataDTO searchCases(CalanderCasePojo calanderCasePojo) {
        String SUBMODULE = getModuleNameForLog() + " [searchCases] ";
        GenericDataDTO  genericDataDTO  = new GenericDataDTO();
        List<CaseDTO> specification = caseSpecification.getCasesByCriteria(calanderCasePojo);
        if(specification.isEmpty()){
            ApplicationLogger.logger.debug("No cases found for the given search criteria : {}; Module : {}",calanderCasePojo, SUBMODULE);
        }else{
            ApplicationLogger.logger.debug("Found for the given search criteria Total Cases : {}; Module : {}", specification.size(), SUBMODULE);
        }
        genericDataDTO.setDataList(specification);
        return genericDataDTO;
    }



    public GenericDataDTO getAllTeamsForCurrentStaff(Integer staffId){
        String SUBMODULE = getModuleNameForLog() + " [getAllTeamsForCurrentStaff] ";
        if (staffId == null) {
            ApplicationLogger.logger.error("Staff ID is required but getting null; Module: {}", SUBMODULE);
            throw new CustomValidationException(APIConstants.NULL_VALUE, "Staff ID is required but getting null", null);
        }
        if(staffId != null){
            ApplicationLogger.logger.debug("getting all teams for current staff with staffID : {}; Module : {};",staffId, SUBMODULE);
        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Set<Integer> childStaffIdList = new HashSet<>();


        //adding own staff to get team
        childStaffIdList.add(staffId);
        //adding child staff to get there team
        childStaffIdList = staffUserRepository.findAllByParentStaffIdAndIsDeleteFalse(staffId);

        if(childStaffIdList.size()!=0){
            List<TeamUserMapping> teamIdList = teamUserMappingsRepository.findAllByStaffIdIn(childStaffIdList.stream().map(integer -> integer.longValue()).collect(Collectors.toList()));
            List<Teams> finalTeamList = teamsRepository.findAllByIdInAndIsDeletedIsFalse(teamIdList.stream().map(teamUserMapping -> teamUserMapping.getTeamId()).collect(Collectors.toList()));
            List<TeamsDTO> finalTeamDTOList = finalTeamList.stream().map(teams ->teamsMapper.domainToDTO(teams,new CycleAvoidingMappingContext()) ).collect(Collectors.toList());
            genericDataDTO.setDataList(finalTeamDTOList);
            ApplicationLogger.logger.debug("Found {} teams for staff ID: {}; Module: {}", finalTeamDTOList.size(), staffId, SUBMODULE);
        }else{
            List<TeamUserMapping> teamUserMappings = teamUserMappingsRepository.findAllByStaffId(staffId.longValue());
            List<Teams> finalTeamList = teamsRepository.findAllByIdInAndIsDeletedIsFalse(teamUserMappings.stream().map(teamUserMapping -> teamUserMapping.getTeamId()).collect(Collectors.toList()));
            List<TeamsDTO> finalTeamDTOList = finalTeamList.stream().map(teams ->teamsMapper.domainToDTO(teams,new CycleAvoidingMappingContext()) ).collect(Collectors.toList());
            genericDataDTO.setDataList(finalTeamDTOList);
            ApplicationLogger.logger.debug("Found {} teams for staff ID: {}; Module: {}", finalTeamDTOList.size(), staffId, SUBMODULE);
        }

        if(getMvnoIdFromCurrentStaff()!=null && getMvnoIdFromCurrentStaff() ==1){  // fetch all teams
            List<Teams> allTeamsByMvnoId = teamsRepository.findAllByIsDeletedFalse();
            List<TeamsDTO> finalTeamDTOList = allTeamsByMvnoId.stream().map(teams ->teamsMapper.domainToDTO(teams,new CycleAvoidingMappingContext()) ).collect(Collectors.toList());
            genericDataDTO.setDataList(finalTeamDTOList);
            ApplicationLogger.logger.debug("Found {} teams for staff ID: {}; And MvnoID : {}; Module: {}", finalTeamDTOList.size(), staffId,getMvnoIdFromCurrentStaff(),SUBMODULE);
        }

        return genericDataDTO;
    }

    public GenericDataDTO getCaseByCustomersId(Integer customersId,PaginationRequestDTO requestDTO){
        String SUBMODULE = getModuleNameForLog() + " [getCaseByCustomersId()] ";
        try {
            ApplicationLogger.logger.debug("getting Case by Customers ID : {}; Module : {}; ",customersId,SUBMODULE);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QCase qCase=QCase.case$;
            QCustomers qCustomers=QCustomers.customers;
            QStaffUser qStaffUser =QStaffUser.staffUser;
            QTeams qTeams=QTeams.teams;

            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            BooleanExpression booleanExpression=qCase.isNotNull().and(qCase.isDelete.eq(false));
            booleanExpression=booleanExpression.and(qCase.isFromCalender.eq(true));
            booleanExpression=booleanExpression.and(qCase.customers.id.eq(customersId));

            QueryResults<CaseDTO> queryResults = queryFactory
                    .select(Projections.constructor(
                            CaseDTO.class,
                            qCase.caseId,
                            qCase.caseTitle,
                            qCase.caseType,
                            qCase.caseNumber,
                            qCase.caseFor,
                            qCase.caseOrigin,
                            qCase.caseStatus,
                            qCase.priority,
                            qCase.startDate,
                            qCase.endDate,
                            qCase.customers.id,
                            qCase.teamId,
                            qTeams.name,
                            qStaffUser.firstname.concat(" ").concat(qStaffUser.lastname),
                            qCustomers.title.concat(" ").concat(qCustomers.firstname).concat(" ").concat(qCustomers.lastname),
                            qCase.currentAssignee.id,
                            qCase.isFromCalender,
                            qCase.firstRemark
                    ))
                    .from(qCase)
                    .leftJoin(qCase.customers, qCustomers)
                    .leftJoin(qTeams).on(qCase.teamId.eq(qTeams.id.intValue()))
                    .leftJoin(qStaffUser).on(qStaffUser.id.eq(qCase.currentAssignee.id))
                    .where(booleanExpression)
                    .offset((requestDTO.getPage() - 1) * requestDTO.getPageSize())
                    .limit(requestDTO.getPageSize())
                    .orderBy(qCase.caseId.desc())
                    .fetchResults();
            List<CaseDTO> caseDTOList = queryResults.getResults();
            if(caseDTOList.size() > 0){
                ApplicationLogger.logger.debug("Found {} records for CustomerID : {}; Module : {};", caseDTOList.size(), customersId, SUBMODULE);
            }else{
                ApplicationLogger.logger.debug("No records found for CustomerID : {}; Module : {};", customersId, SUBMODULE);
            }
            long totalRecords = queryResults.getTotal();
            genericDataDTO.setDataList(caseDTOList);
            genericDataDTO.setTotalRecords(totalRecords);
            genericDataDTO.setPageRecords(requestDTO.getPageSize());
            return genericDataDTO;
        }catch (Exception exception){
            ApplicationLogger.logger.error(exception.getMessage());
        }
        return null;
    }

        public void generateToken(Integer defaultTeamId){
            Teams teams = teamsRepository.findById(defaultTeamId.longValue()).orElse(null);
            if(teams!=null){
                List<TeamUserMapping> teamUserMappings = teamUserMappingsRepository.findAllByTeamId(teams.getId().longValue());
                if(!teamUserMappings.isEmpty()){
                    StaffUser staffUser = staffUserRepository.findById(teamUserMappings.get(0).getStaffId().intValue()).orElse(null);
                    List<GrantedAuthority> role_name = new ArrayList<>();
                    role_name.add(new SimpleGrantedAuthority("ADMIN"));
                    if(staffUser!=null){
                        LoggedInUser user = new LoggedInUser();
                        if(staffUser.getServiceAreaNameList()!=null && staffUser.getServiceAreaNameList().size()>0){
                            user = new LoggedInUser(staffUser.getUsername(), staffUser.getPassword(), true, true, true, true, role_name, staffUser.getFirstname(), staffUser.getLastname(), LocalDateTime.now(), staffUser.getId(), staffUser.getPartnerid(), "ADMIN", staffUser.getServiceAreaNameList().get(0).getId().longValue(), staffUser.getMvnoId(), null, staffUser.getId(), null, false, new ArrayList<String>(), new ArrayList<Long>(), "Default",null,null);

                        }else{
                            user = new LoggedInUser(staffUser.getUsername(), staffUser.getPassword(), true, true, true, true, role_name, staffUser.getFirstname(), staffUser.getLastname(), LocalDateTime.now(), staffUser.getId(), staffUser.getPartnerid(), "ADMIN", null, staffUser.getMvnoId(), null, staffUser.getId(), null, false, new ArrayList<String>(), new ArrayList<Long>(), "Default",null,null);

                        }
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }else{
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Staffs is not available for the DEFAULT_TEAM, Please add atleast one staff to perform email ticket creation operation",null);
                }

            }

        }


        public Teams getDefaultTeam(Integer mvnoId){
            //StaffUser staffUser = staffUserRepository.findAllByEmailAndIsDeleteFalse(emailId).get(0);
            if(mvnoId!=null){
                String teamName = clientService.getByNameAndMvnoId(CommonConstants.DEFAULT_TEAM,mvnoId).getValue();
                Teams teams = teamsRepository.findByNameAndMvnoIdAndIsDeletedFalse(teamName,mvnoId);
                return teams;
            }
            return null;
        }


    public GenericDataDTO getStaffAvailibity(StaffUser staffUser, LocalDateTime startingTime, LocalDateTime endingTime) {
        String SUBMODULE = getModuleNameForLog() + " [getStaffAvailibity()] ";
        ApplicationLogger.logger.debug("Entering getStaffAvailibity method with staffUser : {}, startingTime: {}, endingTime: {}",
                staffUser.getUsername(), startingTime, endingTime);
       GenericDataDTO genericDataDTO=new GenericDataDTO();
        QCase qCase = QCase.case$;

        BooleanExpression booleanExpression = qCase.isNotNull()
                .and(qCase.isDelete.eq(false));

        if (Objects.isNull(endingTime)) {
            LocalDateTime endOfDay = startingTime.toLocalDate().atTime(23, 59, 59);
            booleanExpression = booleanExpression.and(qCase.startDate.between(startingTime, endOfDay))
                    .or(qCase.endDate.between(startingTime, endOfDay));
            ApplicationLogger.logger.debug("Calculated endOfDay: {}; Module : {};", endOfDay,SUBMODULE);
        } else {
            booleanExpression = booleanExpression.and(
                    qCase.startDate.before(endingTime)
                            .and(qCase.endDate.after(startingTime))
            );
        }
        booleanExpression=booleanExpression.and(qCase.currentAssignee.eq(staffUser));
        List<Case> caseList= IterableUtils.toList(caseRepository.findAll(booleanExpression));
        ApplicationLogger.logger.debug("Number of cases found: {}", caseList.size(),booleanExpression);
        List<CaseDTO>caseDTOList=new ArrayList<>();
        for(Case cases :caseList){
            caseDTOList.add(caseMapper.domainToDTO(cases,new CycleAvoidingMappingContext()));
        }
        genericDataDTO.setDataList(caseDTOList);
        return genericDataDTO;
    }

    public LocalDateTime calculateTatEndDate(TatMatrixWorkFlowDetails details) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = details.getStartDateTime();
        long hours = 0;
        long minutes = 0;
        switch (details.getMunit().toLowerCase(Locale.ROOT)) {
            case "day":
                endDate = startDate.plusDays(Long.valueOf(details.getMtime()));
                break;
            case "hour":
                hours = Long.valueOf(details.getMtime());
                if (hours >= 24) {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    endDate = startDate.plusDays(days).plusHours(remainingHours);
                } else {
                    endDate = startDate.plusHours(hours);
                }
                break;
            case "min":
                minutes = Long.valueOf(details.getMtime());
                if (minutes >= 60) {
                    long remainingMinutes = minutes % 60;
                    hours += minutes / 60;  // Use the existing 'hours' variable
                    if (hours >= 24) {
                        long days = hours / 24;
                        long remainingHours = hours % 24;
                        endDate = startDate.plusDays(days).plusHours(remainingHours).plusMinutes(remainingMinutes);
                    } else {
                        endDate = startDate.plusHours(hours).plusMinutes(remainingMinutes);
                    }
                } else {
                    endDate = startDate.plusMinutes(minutes);
                }
                break;
        }
        return endDate;
    }

    public Map<String,String> setTimeFormatForNotificationSent(CaseDTO entity){
        Map<String,String> dates = new HashMap<>();
        String startDate = entity.getStartDate() != null ? entity.getStartDate().toString() : null;
        String endDate =null;
        if (Objects.nonNull(startDate)){

            if (entity.getEndDate() != null) {
                endDate = entity.getEndDate().toString();
            } else if (startDate != null) {
                endDate = startDate + "T23:59";
            } else {
                endDate = null;
            }
        }
        dates.put("startDate",startDate);
        dates.put("endDate",endDate);

        return dates;

    }
}


