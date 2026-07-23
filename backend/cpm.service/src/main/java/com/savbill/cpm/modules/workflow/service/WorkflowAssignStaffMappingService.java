package com.savbill.cpm.modules.workflow.service;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.mapper.postpaid.StaffUserMapper;
import com.savbill.cpm.model.common.*;
import com.savbill.cpm.model.common.CustomerApprove;
import com.savbill.cpm.model.common.CustomerServiceMapping;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.model.lead.LeadMaster;
import com.savbill.cpm.model.lead.LeadQuotationDetails;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.modules.Matrix.domain.Matrix;
import com.savbill.cpm.modules.Matrix.domain.MatrixDetails;
import com.savbill.cpm.modules.Matrix.repository.MatrixRepository;
import com.savbill.cpm.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.cpm.modules.Mvno.domain.Mvno;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.modules.PartnerLedger.domain.PartnerPayment;
import com.savbill.cpm.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.savbill.cpm.modules.Teams.repository.HierarchyRepository;
import com.savbill.cpm.modules.Teams.service.HierarchyService;
import com.savbill.cpm.modules.customerDocDetails.domain.CustomerDocDetails;
import com.savbill.cpm.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.savbill.cpm.modules.workflow.domain.WorkflowAssignStaffMapping;
import com.savbill.cpm.modules.workflow.repository.WorkflowAssignStaffMappingRepo;
import com.savbill.cpm.pojo.api.LeadMgmtWfDTO;
import com.savbill.cpm.pojo.api.StaffUserPojo;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.LeadQuotationWfDTO;
import com.savbill.cpm.rabbitMq.message.SendLeadAssignMessage;
import com.savbill.cpm.rabbitMq.message.SendLeadQuotationMessage;
import com.savbill.cpm.repository.LeadMasterRepository;
import com.savbill.cpm.repository.LeadQuotationDetailsRepository;
import com.savbill.cpm.repository.common.CustomerApproveRepo;
import com.savbill.cpm.repository.common.StaffUserRepository;
import com.savbill.cpm.repository.postpaid.*;
import com.savbill.cpm.repository.postpaid.*;
import com.savbill.cpm.repository.radius.CustomerServiceMappingRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.CustomerCafAssignmentService;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.service.common.WorkflowAuditService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.TatUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class WorkflowAssignStaffMappingService {

    @Autowired
    private WorkflowAssignStaffMappingRepo workflowAssignStaffMappingRepo;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    @Autowired
    private CustomerCafAssignmentService customerCafAssignmentService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    TatUtils tatUtils;
    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    private CustomerDocDetailsRepository customerDocDetailsRepository;
    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    private CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;

    @Autowired
    LeadMasterRepository leadMasterRepository;

    @Autowired
    MessageSender messageSender;

    @Autowired
    CustomerApproveRepo customerApproveRepo;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    private LeadQuotationDetailsRepository leadQuotationDetailsRepository;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    HierarchyRepository hierarchyRepository;
    @Autowired
    MatrixRepository matrixRepository;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    MvnoRepository mvnoRepository;

    public void assignWorkflowToStaff(Integer eventId, String eventName, Object entityPojo, Map<String, Object> map) {
        try {
            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
            Integer nextTeamHierarchyMappingId = (Integer) map.get("nextTeamHierarchyMappingId");
            Integer entityId = 0;
            boolean isTeamAuditLogged = false;
            for (StaffUserPojo staffUserPojo : staffUserPojos) {
                switch (eventName) {
                    case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
                        if (entityPojo instanceof Customers) {
                            Customers customers = (Customers) entityPojo;
                            entityId = customers.getId();
                            if (nextTeamHierarchyMappingId != null) {
                                customers.setNextTeamHierarchyMapping(nextTeamHierarchyMappingId);
                            }
                            map.put("entityId",entityId);
                            map.put("eventId", entityId);
//                          tatUtils.changeTatAssignee(customers,staffUserMapper.dtoToDomain(staffUserPojo,new CycleAvoidingMappingContext()),false,true);
                            customers.setCurrentAssigneeId(null);

                            if (!isTeamAuditLogged) {
                                String teamName = map.containsKey("teamName") ? (String) map.get("teamName") : "";
                                workflowAuditService.saveAudit(eventId, eventName, entityId, customers.getUsername(), null, null, CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned Team:- " + teamName);
                                isTeamAuditLogged = true;
                            }

                            saveMappings(eventId, eventName, staffUserPojo, entityId, customers.getUsername(), CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER,customers.getMvnoId());
                            customersRepository.save(customers);
                        }

                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                        if (entityPojo instanceof PostpaidPlan) {
                            PostpaidPlan postpaidPlan = (PostpaidPlan) entityPojo;
                            entityId = postpaidPlan.getId();
                            if (nextTeamHierarchyMappingId != null) {
                                postpaidPlan.setNextTeamHierarchyMapping(nextTeamHierarchyMappingId);
                                postpaidPlan.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, postpaidPlan.getName(), CommonConstants.WORKFLOW_MSG_ACTION.PLAN,postpaidPlan.getMvnoId());
                            postpaidPlanRepo.save(postpaidPlan);

                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                        if (entityPojo instanceof PlanGroup) {
                            PlanGroup planGroup = (PlanGroup) entityPojo;
                            entityId = planGroup.getPlanGroupId();
                            if (nextTeamHierarchyMappingId != null) {
                                planGroup.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                planGroup.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, planGroup.getPlanGroupName(), CommonConstants.WORKFLOW_MSG_ACTION.PLAN_GROUP,planGroup.getMvnoId());
                            planGroupRepository.save(planGroup);

                        }
                        break;

                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                        if (entityPojo instanceof CustomerDocDetails) {
                            CustomerDocDetails customerDocDetails = (CustomerDocDetails) entityPojo;
                            entityId = Math.toIntExact(customerDocDetails.getDocId());
                            if (nextTeamHierarchyMappingId != null) {
                                customerDocDetails.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerDocDetails.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerDocDetails.getFilename(), CommonConstants.WORKFLOW_MSG_ACTION.PLAN,customerDocDetails.getCustomer().getMvnoId());
                            customerDocDetailsRepository.save(customerDocDetails);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                        if (entityPojo instanceof CreditDocument) {
                            CreditDocument creditDocument = (CreditDocument) entityPojo;
                            entityId = Math.toIntExact(creditDocument.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                creditDocument.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                creditDocument.setApproverid(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, creditDocument.getFilename(), CommonConstants.WORKFLOW_MSG_ACTION.PLAN,creditDocument.getMvnoId());
                            creditDocRepository.save(creditDocument);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                        if (entityPojo instanceof PartnerPayment) {
                            PartnerPayment partnerPayment = (PartnerPayment) entityPojo;
                            entityId = Math.toIntExact(partnerPayment.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                partnerPayment.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                partnerPayment.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, partnerPayment.getPartnerName(), CommonConstants.WORKFLOW_MSG_ACTION.PARTNER_BALANCE,partnerPayment.getPartner().getMvnoId());
                            partnerPaymentRepository.save(partnerPayment);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION:{
                        if (entityPojo instanceof CustomerServiceMapping) {
                            CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entityPojo;
                            entityId = Math.toIntExact(customerServiceMapping.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                customerServiceMapping.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerServiceMapping.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerServiceMapping.getConnectionNo(), CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_TERMINATION,customerServiceMapping.getMvnoId());
                           CustomerServiceMapping customerServiceMapping1 = customerServiceMappingRepository.save(customerServiceMapping);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                        if (entityPojo instanceof CustomerServiceMapping) {
                            CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entityPojo;
                            entityId = Math.toIntExact(customerServiceMapping.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                customerServiceMapping.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerServiceMapping.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerServiceMapping.getConnectionNo(), CommonConstants.WORKFLOW_MSG_ACTION.CHANGE_DISCOUNT,customerServiceMapping.getMvnoId());
                            customerServiceMappingRepository.save(customerServiceMapping);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {

                        if (entityPojo instanceof CustomerAddress) {
                            CustomerAddress customerAddress = (CustomerAddress) entityPojo;
                            entityId = Math.toIntExact(customerAddress.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                customerAddress.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerAddress.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerAddress.getCreatedByName(), CommonConstants.WORKFLOW_MSG_ACTION.SHIFT_LOCATION,customerAddress.getCustomer().getMvnoId());
                            customerAddressRepository.save(customerAddress);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                        if (entityPojo instanceof Customers) {
                            Customers customers = (Customers) entityPojo;
                            entityId = Math.toIntExact(customers.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                customers.setNextTeamHierarchyMapping(nextTeamHierarchyMappingId);
                                CustomerApprove customerApprove = customersService.finCustmerApproveForTermination(customers.getId());
                                customerApprove.setParentStaff(null);
                                customerApprove.setCurrentStaff(null);
                                customerApproveRepo.save(customerApprove);
                            }
                            map.put("entityId",entityId);
                            map.put("eventId", entityId);
                            tatUtils.changeTatAssignee(customers,staffUserMapper.dtoToDomain(staffUserPojo,new CycleAvoidingMappingContext()),false,true);
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customers.getUsername(), CommonConstants.WORKFLOW_MSG_ACTION.TERMINATION,customers.getMvnoId());
                            customersRepository.save(customers);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                        if (entityPojo instanceof DebitDocument) {
                            DebitDocument debitDocument = (DebitDocument) entityPojo;
                            entityId = Math.toIntExact(debitDocument.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                debitDocument.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                debitDocument.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, debitDocument.getCustomer().getUsername(), CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION,debitDocument.getCustomer().getMvnoId());
                            debitDocRepository.save(debitDocument);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                        if (entityPojo instanceof CustSpecialPlanRelMappping) {
                            CustSpecialPlanRelMappping custSpecialPlanRelMappping = (CustSpecialPlanRelMappping) entityPojo;
                            entityId = Math.toIntExact(custSpecialPlanRelMappping.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                custSpecialPlanRelMappping.setNextTeamHierarchyMapping(nextTeamHierarchyMappingId);
                                custSpecialPlanRelMappping.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, custSpecialPlanRelMappping.getMappingName(), CommonConstants.WORKFLOW_MSG_ACTION.SPECIAL_PLAN_MAPPING,custSpecialPlanRelMappping.getMvnoId());

                            custSpecialPlanRelMapppingRepository.save(custSpecialPlanRelMappping);
                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
                        if (entityPojo instanceof LeadMaster) {
                            LeadMaster leadMaster = (LeadMaster) entityPojo;
                            entityId = leadMaster.getId().intValue();
                            if (nextTeamHierarchyMappingId != null) {
                                leadMaster.setNextTeamMappingId(nextTeamHierarchyMappingId);
                            }
                            map.put("entityId",entityId);
                            map.put("eventId", entityId);
                            leadMaster.setNextApproveStaffId(null);
                            leadMasterRepository.save(leadMaster);
                            LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.leadMasterToLeadMgmtDTO(leadMaster);
                            SendLeadAssignMessage sendApproverForLeadMsg = new SendLeadAssignMessage(leadMgmtWfDTO);
                            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadAssignMessage.class.getSimpleName()));
//                            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE);
                            if (leadMaster.getUsername() == null) {
                                saveMappings(eventId, eventName, staffUserPojo, entityId, leadMaster.getFirstname(), CommonConstants.WORKFLOW_MSG_ACTION.LEAD,leadMaster.getMvnoId().intValue());
                            } else {
                                saveMappings(eventId, eventName, staffUserPojo, entityId, leadMaster.getUsername(), CommonConstants.WORKFLOW_MSG_ACTION.LEAD,leadMaster.getMvnoId().intValue());
                            }
                            leadMasterRepository.save(leadMaster);
                            //need to send updated data into lead microservice

                        }
                        break;
                    }
                    case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                        if (entityPojo instanceof CreditDocument) {
                            CreditDocument creditDocument = (CreditDocument) entityPojo;
                            entityId = Math.toIntExact(creditDocument.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                creditDocument.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                creditDocument.setApproverid(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, creditDocument.getFilename(), CommonConstants.WORKFLOW_MSG_ACTION.PLAN,creditDocument.getMvnoId());
                            creditDocRepository.save(creditDocument);
                        }
                        break;
                    }

                    case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD:{
                        if (entityPojo instanceof CustomerServiceMapping) {
                            CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entityPojo;
                            entityId = Math.toIntExact(customerServiceMapping.getId());
                            if (nextTeamHierarchyMappingId != null) {
                                customerServiceMapping.setNextTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerServiceMapping.setNextStaff(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerServiceMapping.getConnectionNo(), CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD,customerServiceMapping.getMvnoId());
                            CustomerServiceMapping customerServiceMapping1 = customerServiceMappingRepository.save(customerServiceMapping);
                        }
                        break;
                    }

                    case CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION: {
                        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
                        if (entityPojo instanceof LeadQuotationDetails) {
                            LeadQuotationDetails leadQuotationDetails = (LeadQuotationDetails) entityPojo;
                            entityId = leadQuotationDetails.getQuotationDetailId().intValue();
                            if (nextTeamHierarchyMappingId != null) {
                                leadQuotationDetails.setNextTeamMappingId(nextTeamHierarchyMappingId);
                            }
                            leadQuotationDetails.setNextApproveStaffId(null);
                            leadQuotationDetailsRepository.save(leadQuotationDetails);
                            LeadQuotationWfDTO leadQuotationWfDTO = hierarchyService.leadQuotationDetailsToLeadQuotationWfDTO(leadQuotationDetails);
                            SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(leadQuotationWfDTO);
//                            messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE);
                            kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage,SendLeadQuotationMessage.class.getSimpleName()));
                            saveMappings(eventId, eventName, staffUserPojo, entityId, leadQuotationDetails.getFirstName(), CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION,leadQuotationDetails.getMvnoId().intValue());
                            leadQuotationDetailsRepository.save(leadQuotationDetails);
                            //need to send updated data into lead microservice

                        }
                        break;
                    }

                }

                if (staffUserPojo.getParentStaffId() != null && !CollectionUtils.isEmpty(map)) {
                    Map<String, String> workFlowMap = new HashMap<>();

                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null") {
                        workFlowMap.put("tat_id", (String) map.get("current_tat_id"));
                        workFlowMap.put("nextTatMappingId", map.get("nextTeamHierarchyMappingId").toString());
                        workFlowMap.put("workFlowId", (String) map.get("workFlowId"));
                        workFlowMap.put("orderNo", (String) map.get("orderNo"));
                    }
                    workFlowMap.put("eventName", (String) map.get("eventName"));
                    workFlowMap.put("eventId", map.get("eventId").toString());

                    tatUtils.saveOrUpdateDataForTatMatrix(workFlowMap, staffUserService.convertStaffUserPojoToStaffUserModel(staffUserPojo), entityId, null);
                }
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, e.getMessage(), null);
        }
    }

    @Transactional
    public void pickAndAssignWorkflow(String eventName, Integer entityId) throws Exception {
        try {
            if (eventName != null && entityId != null) {
                Map <String,String> map=new HashMap<>();
                WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                Integer staffId = customersService.getLoggedInUserId();
                StaffUserPojo staffUserPojo = staffUserService.findByStaffId(staffId);
                workflowAssignStaffMapping = workflowAssignStaffMappingRepo.findByEventNameAndStaffIdAndEntityId(eventName, staffId, entityId);
                if (workflowAssignStaffMapping != null) {
                    switch (eventName) {
                        case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {

                            StaffUser staffUser=staffUserRepository.findById(staffId).orElse(null);
                            Customers customer = customersRepository.findById(entityId).orElse(null);
                            if (customer != null) {
                                customer.setCurrentAssigneeId(staffId);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, Math.toIntExact(customer.getId()), customer.getUsername(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);

                                map = hierarchyService.getTeamForNextApproveForAuto(customer.getMvnoId(), customer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext()));
                                if(map.get("tat_id").equals("null") || !map.get("current_tat_id").equals("null")  ) {
                                    if (!map.get("current_tat_id").equals("null")) {
                                        map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                                    }
                                }
                                if(!map.get("tat_id").equalsIgnoreCase("null")) {
                                 Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
                                  //  customer.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                                 Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
                                 if (matrixDetails.isPresent()) {
                                     Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                                     Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                     if (newMatrixDetails.isPresent()) {
                                         customer = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), customer, Nextvalue);
                                         //details.setStaffId(details.getParentId());
                                     }
                                 }
                             }
                                if(customer.getNextTeamHierarchyMapping()!=null) {
                                    tatUtils.changeTatAssignee(customer, staffUser, false, true);
                                }

                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No customer found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(entityId).orElse(null);
                            if (postpaidPlan != null) {
                                postpaidPlan.setNextStaff(staffId);
                                postpaidPlanRepo.save(postpaidPlan);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, Math.toIntExact(postpaidPlan.getId()), postpaidPlan.getName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No Plan found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                            PlanGroup planGroup = planGroupRepository.findById(entityId).orElse(null);
                            if (planGroup != null) {
                                planGroup.setNextStaff(staffId);
                                planGroupRepository.save(planGroup);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, Math.toIntExact(planGroup.getPlanGroupId()), planGroup.getPlanGroupName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No plangroup found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                            CustomerDocDetails customerDocDetails = customerDocDetailsRepository.findById(Long.valueOf(entityId)).orElse(null);
                            if (customerDocDetails != null) {
                                customerDocDetails.setNextStaff(staffId);
                                customerDocDetailsRepository.save(customerDocDetails);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getFilename(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No document found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                            CreditDocument creditDocument = creditDocRepository.findById(entityId).orElse(null);
                            if (creditDocument != null) {
                                creditDocument.setApproverid(staffId);
                                creditDocRepository.save(creditDocument);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, creditDocument.getId(), creditDocument.getReferenceno(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No payment found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                            PartnerPayment partnerPayment = partnerPaymentRepository.findById(entityId.longValue()).orElse(null);
                            if (partnerPayment != null) {
                                partnerPayment.setNextStaff(staffId);
                                partnerPaymentRepository.save(partnerPayment);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPayment.getId().intValue(), partnerPayment.getPartnerName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No partnerbalance found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION:{
                            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                            if (customerServiceMapping != null) {
                                customerServiceMapping.setNextStaff(staffId);
                                customerServiceMappingRepository.save(customerServiceMapping);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION, customerServiceMapping.getId().intValue(), customerServiceMapping.getConnectionNo(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No partnerbalance found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                            if (customerServiceMapping != null) {
                                customerServiceMapping.setNextStaff(staffId);
                                customerServiceMappingRepository.save(customerServiceMapping);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT, customerServiceMapping.getId().intValue(), customerServiceMapping.getConnectionNo(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No partnerbalance found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                            CustomerAddress customerAddress = customerAddressRepository.findById(entityId).orElse(null);
                            if (customerAddress != null) {
                                customerAddress.setNextStaff(staffId);
                                customerAddressRepository.save(customerAddress);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddress.getId().intValue(), customerAddress.getCreatedByName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No partnerbalance found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                            Customers customer = customersRepository.findById(entityId).orElse(null);
                           StaffUser staffUser=staffUserRepository.findById(staffUserPojo.getId()).orElse(null);
                            if (customer != null) {
                                CustomerApprove customerApprove = customerApproveRepo.findByCustomerIDAndStatus(customer.getId(), "pending");
                                if (staffUser.getStaffUserparent() != null) {
                                    customerApprove.setParentStaff(staffUser.getStaffUserparent().getUsername());
                                }
                                customerApprove.setCurrentStaff(staffUserPojo.getUsername());
                                customerApproveRepo.save(customerApprove);
                                customer.setCurrentAssigneeId(staffId);
                                map = hierarchyService.getTeamForNextApproveForAuto(customer.getMvnoId(), customer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, CommonConstants.HIERARCHY_TYPE, false, true, customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext()));
                                if(map.get("tat_id").equals("null") || !map.get("current_tat_id").equals("null")  ) {
                                    if (!map.get("current_tat_id").equals("null")) {
                                        map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                                    }
                                }
                                if(!map.get("tat_id").equalsIgnoreCase("null")) {
                                    Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
                                    Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
                                    if (matrixDetails.isPresent()) {
                                        Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                                        Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                        if (newMatrixDetails.isPresent()) {
                                            customer = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), customer, Nextvalue);
                                            //details.setStaffId(details.getParentId());
                                        }
                                    }
                                }
                                customersRepository.save(customer);
//                                customerCafAssignmentService.assignCustomerCaf(customer, staffId);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, Math.toIntExact(customer.getId()), customer.getUsername(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                                if(customer.getNextTeamHierarchyMapping()!=null) {
                                    tatUtils.changeTatAssignee(customer, staffUser, false, true);
                                }
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No termination found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                            DebitDocument debitDocument = debitDocRepository.findById(entityId).orElse(null);
                            if (debitDocument != null) {
                                debitDocument.setNextStaff(staffId);
                                debitDocRepository.save(debitDocument);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId().intValue(), debitDocument.getCustRefName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No partnerbalance found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                            CustSpecialPlanRelMappping custSpecialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(entityId.longValue()).orElse(null);
                            if (custSpecialPlanRelMappping != null) {
                                custSpecialPlanRelMappping.setNextStaff(staffId);
                                custSpecialPlanRelMapppingRepository.save(custSpecialPlanRelMappping);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, custSpecialPlanRelMappping.getId().intValue(), custSpecialPlanRelMappping.getMappingName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No specialplanmapping found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                            StaffUser staffUser=staffUserRepository.findById(staffId).orElse(null);
                            LeadMaster leadMaster = leadMasterRepository.findById(Long.valueOf(entityId)).orElse(null);
                            if (leadMaster != null) {
                                //customerCafAssignmentService.assignCustomerCaf(customer, staffId);
                                leadMaster.setNextApproveStaffId(staffId);
                                LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.leadMasterToLeadMgmtDTO(leadMaster);
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                                map = hierarchyService.getTeamForNextApproveForAuto(leadMaster.getMvnoId().intValue(), leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, false, true, leadMgmtWfDTO);
                                if(map.get("tat_id").equals("null") || !map.get("current_tat_id").equals("null")  ) {
                                    if (!map.get("current_tat_id").equals("null")) {
                                        map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                                    }
                                }
                                if(!map.get("tat_id").equalsIgnoreCase("null")) {
                                    Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
                                    Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
                                    if (matrixDetails.isPresent()) {
                                        Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                                        Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                        if (newMatrixDetails.isPresent()) {
                                            leadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
                                            //details.setStaffId(details.getParentId());
                                        }
                                    }
                                }
                                leadMasterRepository.save(leadMaster);
                                if(leadMaster.getNextfollowuptime()!=null){
                                    leadMgmtWfDTO.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
                                    leadMgmtWfDTO.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
                                }
                                SendLeadAssignMessage sendApproverForLeadMsg = new SendLeadAssignMessage(leadMgmtWfDTO);
                                kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadAssignMessage.class.getSimpleName()));
//                                messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.LEAD, Math.toIntExact(leadMaster.getId()), leadMaster.getUsername(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                if(leadMaster.getNextTeamMappingId()!=null) {
                                    tatUtils.changeTatAssignee(leadMaster, staffUser, false, true);
                                }
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No customer found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                            CreditDocument creditDocument = creditDocRepository.findById(entityId).orElse(null);
                            if (creditDocument != null) {
                                creditDocument.setApproverid(staffId);
                                creditDocRepository.save(creditDocument);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, creditDocument.getId(), creditDocument.getReferenceno(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No payment found. ", null);
                            }
                            break;
                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD:{
                            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                            if (customerServiceMapping != null) {
                                customerServiceMapping.setNextStaff(staffId);
                                customerServiceMappingRepository.save(customerServiceMapping);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, customerServiceMapping.getId().intValue(), customerServiceMapping.getConnectionNo(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No Sevcie To add found. ", null);
                            }
                            break;
                        }

                        case CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION: {
                            LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(Long.valueOf(entityId));
                            if (leadQuotationDetails != null) {
                                leadQuotationDetails.setNextApproveStaffId(staffId);
                                leadQuotationDetailsRepository.save(leadQuotationDetails);
                                LeadQuotationWfDTO leadQuotationWfDTO = hierarchyService.leadQuotationDetailsToLeadQuotationWfDTO(leadQuotationDetails);
                                SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(leadQuotationWfDTO);
                                kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage,SendLeadQuotationMessage.class.getSimpleName()));
//                                messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, Math.toIntExact(leadQuotationDetails.getId()), leadQuotationWfDTO.getFirstName(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No quotation found. ", null);
                            }
                            break;
                        }


                        default: {
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Event name is invalid. ", null);

                        }
                    }
                } else {
                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Your are not authorized to pick. ", null);
                }
            }
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Your are not authorized to pick. ", null);
        } catch (Exception exception) {
            throw new Exception(exception);
        }
    }

    @Transactional
    public void deleteAllByEventNameAndEntityId(String eventName, Integer entityId) {
        try {
            if (eventName != null && entityId != null) {
                List<WorkflowAssignStaffMapping> workflowAssignStaffMappings = workflowAssignStaffMappingRepo.findAllByEventNameAndEntityId(eventName, entityId);
                if (!workflowAssignStaffMappings.isEmpty()) {
                    workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);
                }
            }
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Your are not authorized to pick. ", null);
        } catch (Exception exception) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    private void saveMappings(Integer eventId, String eventName, StaffUserPojo staffUserPojo, Integer entityId, String entityName, String actionEntity,Integer mvnoId) {
        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
        workflowAssignStaffMapping.setEntityId(entityId);
        workflowAssignStaffMapping.setEventName(eventName);
        workflowAssignStaffMappingRepo.save(workflowAssignStaffMapping);

        if (!CommonConstants.WORKFLOW_EVENT_NAME.CAF.equals(eventName)) {
            workflowAuditService.saveAudit(eventId, eventName, Math.toIntExact(entityId), entityName, staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
        }

        String action = actionEntity + " " + entityName + " " + CommonConstants.WORKFLOW_ASSIGNED_FOR_APPROVAL;

      if(mvnoId!=null && staffUserPojo.getMvnoId()==1) {
          hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), mvnoId, staffUserPojo.getFullName(), action);
      }else{
          hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), staffUserPojo.getMvnoId(), staffUserPojo.getFullName(), action);
      }
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}" , APIConstants.FAIL,e.getStackTrace());
        }
        return mvnoIds;
    }


    public Object autoPickAndAssign(String eventName, Integer entityId){
        try{
            if (eventName != null && entityId != null) {
                Map<String, String> map = new HashMap<>();
                WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                List<WorkflowAssignStaffMapping> workflowAssignStaffMappingList = workflowAssignStaffMappingRepo.findAllByEventNameAndEntityId(eventName,entityId);
                Integer staffId = workflowAssignStaffMappingList.get(0).getStaffId();
                StaffUserPojo staffUserPojo = staffUserService.findByStaffId(workflowAssignStaffMappingList.get(0).getStaffId());
               //generate token
                generateToken(staffUserPojo);
                if (workflowAssignStaffMapping != null) {
                    switch (eventName) {
                        case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {

                            StaffUser staffUser = staffUserRepository.findById(staffId).orElse(null);
                            Customers customer = customersRepository.findById(entityId).orElse(null);
                            if (customer != null) {
                                customer.setCurrentAssigneeId(staffId);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, Math.toIntExact(customer.getId()), customer.getUsername(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By (Auto Assign) :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAll(workflowAssignStaffMappingList);
                                map = hierarchyService.getTeamForNextApproveForAuto(customer.getMvnoId(), customer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext()));
                                setTatForWorkflow(map,customer,CommonConstants.WORKFLOW_EVENT_NAME.CAF,staffUser);
                                return customer;
                            } else {
                                throw new CustomValidationException(org.apache.http.HttpStatus.SC_EXPECTATION_FAILED, "No customer found. ", null);
                            }

                        }
                        case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                            Customers customer = customersRepository.findById(entityId).orElse(null);
                            StaffUser staffUser=staffUserRepository.findById(staffUserPojo.getId()).orElse(null);
                            if (customer != null) {
                                CustomerApprove customerApprove = customerApproveRepo.findByCustomerIDAndStatus(customer.getId(), "pending");
                                if (staffUser.getStaffUserparent() != null) {
                                    customerApprove.setParentStaff(staffUser.getStaffUserparent().getUsername());
                                }
                                customerApprove.setCurrentStaff(staffUserPojo.getUsername());
                                customerApproveRepo.save(customerApprove);
                                customer.setCurrentAssigneeId(staffId);
                                map = hierarchyService.getTeamForNextApproveForAuto(customer.getMvnoId(), customer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, CommonConstants.HIERARCHY_TYPE, false, true, customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext()));
                                setTatForWorkflow(map,customer,CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION,staffUser);
                                customersRepository.save(customer);
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, Math.toIntExact(customer.getId()), customer.getUsername(), staffId, staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Picked By :- " + staffUserPojo.getUsername());
                                workflowAssignStaffMappingRepo.deleteAllByEventNameAndEntityId(eventName, entityId);

                                return customer;
                            } else {
                                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No termination found. ", null);
                            }
                        }
                    }
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            ApplicationLogger.logger.error("error while auto assigning :"+e.getMessage());
        }
        return null;
    }

    public void generateToken(StaffUserPojo staffUserPojo){
        String staffRoleName = "ADMIN";
        StaffUser staffUser = staffUserRepository.findById(staffUserPojo.getId()).orElse(null);
       if(!staffUser.getRoles().isEmpty())
           staffRoleName = staffUser.getRoles().stream().findFirst().get().getRolename();
        String mvnoName = mvnoRepository.findMvnoNameById(staffUser.getMvnoId().longValue());
        Mvno mvno = mvnoRepository.findById(staffUser.getMvnoId().longValue()).orElse(null);
        List<GrantedAuthority> role_name = new ArrayList<>();
        ApplicationLogger.logger.debug("***** token generated for auto approval *****");

        role_name.add(new SimpleGrantedAuthority(staffRoleName));
        LoggedInUser user = new LoggedInUser(staffUser.getUsername(), mvno.getName(), true, true, true, true, role_name, mvno.getName(), mvno.getName(), LocalDateTime.now(), staffUser.getId(), 1, "ADMIN", null, staffUser.getMvnoId(), null, staffUser.getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),mvno.getName(),null,null,null);        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
        ApplicationLogger.logger.info("mvno for auto approval"+hierarchyService.getMvnoIdFromCurrentStaff());

    }



    public void setTatForWorkflow(Map<String ,String > map, Object object, String eventName, StaffUser staffUser){
        if(map.get("tat_id").equals("null") || !map.get("current_tat_id").equals("null")  ) {
            if (!map.get("current_tat_id").equals("null")) {
                map.put("tat_id", String.valueOf(map.get("current_tat_id")));
            }
        }
        if(!map.get("tat_id").equalsIgnoreCase("null")) {
            Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
            Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
            if (matrixDetails.isPresent()) {
                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                if (newMatrixDetails.isPresent()) {
                    object = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), object, Nextvalue);
                }
            }
        }
        switch (eventName){
            case CommonConstants.EVENT_NAME.CAF:{
                Customers customer = (Customers) object;
                if(customer.getNextTeamHierarchyMapping()!=null) {
                    tatUtils.changeTatAssignee(customer, staffUser, false, true);
                }
            }
        }

    }


}
