package com.savbill.taskmanagement.core.modules.TicketFollowupDetail.service;



import com.savbill.taskmanagement.core.constants.CommonConstants;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.MailConfigration.service.ReceiveMailService;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamUserMappingsRepository;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.mapper.TicketFollowupDetailMapper;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.repository.TicketFollowupDetailRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.taskmanagement.rabbitmq.messages.SendFollowUpRemarkMsg;
import com.savbill.taskmanagement.rabbitmq.messages.TicketExternalRemarkCustomerMessage;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketFollowupDetailService extends ExBaseAbstractService<TicketFollowupDetailDTO, TicketFollowupDetail, Long> {

    public TicketFollowupDetailService(TicketFollowupDetailRepository repository, TicketFollowupDetailMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TicketFollowupDetailService]";
    }

    @Autowired
    private TicketFollowupDetailRepository ticketFollowupDetailRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private NotificationTemplateRepository templateRepository;

//    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    TeamUserMappingsRepository teamUserMappingsRepository;

    @Autowired
    ReceiveMailService receiveMailService;

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("TicketFollowupDetail");
        createExcel(workbook, sheet, TicketFollowupDetailDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                TicketFollowupDetailDTO.class.getDeclaredField("id"),
                TicketFollowupDetailDTO.class.getDeclaredField("remarks"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, TicketFollowupDetailDTO.class, getFields());
    }

    public TicketFollowupDetail getById(Long id) {
        return ticketFollowupDetailRepository.findById(id).get();
    }

    public List<TicketFollowupDetail> getAllByCaseId(Long caseId) throws Exception {
        List<TicketFollowupDetail> list = ticketFollowupDetailRepository.getAllByCaseId(caseId);
        if(list != null && list.size() > 0) {
            for(TicketFollowupDetail ticketFollowupDetail : list) {
                if (ticketFollowupDetail.getCaseId() != null) {
                    CaseDTO caseDb = caseService.getEntityById(ticketFollowupDetail.getCaseId());
                    if(caseDb != null) {
                        ticketFollowupDetail.setCaseTitle(caseDb.getCaseTitle());
                        ticketFollowupDetail.setCaseId(caseDb.getCaseId());
                    }
                }
                if (ticketFollowupDetail.getCustId() != null) {
                    Customers customers = customersService.get(ticketFollowupDetail.getCustId());
                    if(customers != null) {
                        ticketFollowupDetail.setCustomersName(customers.getFirstname()+ " " + customers.getLastname());
                        ticketFollowupDetail.setCustId(customers.getId());
                    }
                }
                if (ticketFollowupDetail.getStaffId() != null) {
                    StaffUser staffUser = staffUserService.get(ticketFollowupDetail.getStaffId());
                    if(staffUser != null) {
                        ticketFollowupDetail.setStaffUserName(staffUser.getFullName());
                        ticketFollowupDetail.setStaffId(staffUser.getId());
                    }
                }
            }
        }
        return list;
    }


    public void sendFollowUpRemarkMsg(String parentStaffPersonName, String ticketNumber, String remark, String staffPersonName, String parentMobileNumber, String parentEmailId, Integer mvnoId, String teamStaffName,Long buId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.FOLLOWUP_REMARK_MSG);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    SendFollowUpRemarkMsg sendFollowUpRemarkMsg = new SendFollowUpRemarkMsg(parentMobileNumber,parentEmailId, RMQConstants.FOLLOWUP_TASK_MSG,optionalTemplate.get(),parentStaffPersonName,staffPersonName,remark,mvnoId,ticketNumber,teamStaffName,buId);
                    Gson gson = new Gson();
                    gson.toJson(sendFollowUpRemarkMsg);
//                    messageSender.send(sendFollowUpRemarkMsg, RMQConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG);
                    kafkaMessageSender.send(new KafkaMessageData(sendFollowUpRemarkMsg, SendFollowUpRemarkMsg.class.getSimpleName(), KafkaConstant.FOLLOWUP_TASK_MSG));

                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TicketFollowupDetailDTO saveEntity(TicketFollowupDetailDTO entity) throws Exception {
        CaseDTO dbObj = new CaseDTO();
        if (Objects.nonNull(entity.getIsFromCustomer()) && entity.getIsFromCustomer()) {
            dbObj = caseService.findCaseByCaseId(entity.getCaseId());
        } else {
            dbObj = caseService.getEntityForUpdateAndDelete(entity.getCaseId());
        }

        StaffUser staffuser = new StaffUser();
        if (dbObj.getCurrentAssigneeId() != null) {
            staffuser = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).get();
        }

        StaffUser parentStaffuser = new StaffUser();
        if (staffuser.getParentStaffId() != null) {
            parentStaffuser = staffUserRepository.findById(staffuser.getParentStaffId()).orElse(null);
        }
    entity.setStaffId(getLoggedInUser().getStaffId());
        if (Objects.nonNull(entity.getRemarkType()) && entity.getRemarkType().equalsIgnoreCase(CommonConstants.TICKET_REMARK_TYPE_INTERNAL)) {
            if (dbObj.getCurrentAssigneeId() != null) {
                Case aCase = caseService.getRepository().findById(entity.getCaseId()).orElse(null);
                if (aCase != null) {
                    if (staffuser != null) {
                        //Message is from staff then sent it to parentstaff
                        if (getLoggedInUser().getStaffId().equals(staffuser.getId())) {
                            if (aCase.getBuId() != null) {
                                sendFollowUpRemarkMsg(parentStaffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffuser.getFirstname(), parentStaffuser.getPhone(), parentStaffuser.getEmail(), aCase.getMvnoId(), getLoggedInUser().getFullName(), aCase.getBuId());
                            } else {
                                sendFollowUpRemarkMsg(parentStaffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffuser.getFirstname(), parentStaffuser.getPhone(), parentStaffuser.getEmail(), aCase.getMvnoId(), getLoggedInUser().getFullName(), null);
                            }
                        } else if (getLoggedInUser().getStaffId().equals(parentStaffuser.getId())) {
                            //Message is from parentstaff then sent it to staff
                            if (aCase.getBuId() != null) {
                                sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), parentStaffuser.getFirstname(), staffuser.getPhone(), staffuser.getEmail(), aCase.getMvnoId(), staffuser.getFullName(), aCase.getBuId());
                            } else {
                                sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), parentStaffuser.getFirstname(), staffuser.getPhone(), staffuser.getEmail(), aCase.getMvnoId(), staffuser.getFullName(), null);
                            }
                        } else {
                            //Message is from other staff then sent it to currentAssign staff
                            StaffUser otherStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                            if (otherStaff != null) {
                                if (aCase.getBuId() != null) {
                                    sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), otherStaff.getFirstname(), staffuser.getPhone(), staffuser.getEmail(), aCase.getMvnoId(), staffuser.getFullName(), aCase.getBuId());
                                } else {
                                    sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), otherStaff.getFirstname(), staffuser.getPhone(), staffuser.getEmail(), aCase.getMvnoId(), staffuser.getFullName(), null);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (Objects.nonNull(entity.getIsFromCustomer()) && entity.getIsFromCustomer() == false) {
                final CaseDTO newdbObj = dbObj;
                Customers customer = customersService.getcustForEmail(entity.getCustId());
                sendFollowUpExternalMsgToCustomer(customer.getUsername(), dbObj.getCaseNumber(), entity.getRemark(), staffuser.getFirstname(), customer.getMobile(), customer.getEmail(), staffuser.getMvnoId(), getLoggedInUser().getFullName(), null);
            }
        }
        entity.setStaffId(getLoggedInUserId());
        return super.saveEntity(entity);
    }

    public List<String> getTeamListByStaffId(Long staffId) {
        List<String> teamnameList=new ArrayList<>();
        List<Long>teamids= teamUserMappingsRepository.teamIds(staffId);
        if(teamids.size()>0){
            for (int i=0;i<teamids.size();i++) {
                teamnameList.add(teamsRepository.findById(teamids.get(i)).get().getName());
            }
        }
        return teamnameList;
    }

    public void sendFollowUpExternalMsgToCustomer(String customerName, String ticketNumber, String remark, String staffPersonName, String customerMobileNumber, String customerEmail, Integer mvnoId, String teamStaffName, String altEmail) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.EXTERNAL_TICKET_REMARK_TO_CUSTOMER_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    Long buId = null;
                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
                        buId =  getBUIdsFromCurrentStaff().get(0);
                    }
                    TicketExternalRemarkCustomerMessage ticketExternalRemarkCustomerMessage = new TicketExternalRemarkCustomerMessage(customerMobileNumber,customerEmail,RabbitMqConstants.TICKET_FOLLOWUP_REMARK_CUSTOMER_TEMPLATE_HEADER,optionalTemplate.get(),customerName,staffPersonName,remark,mvnoId,ticketNumber,teamStaffName,buId, altEmail);
                    Gson gson = new Gson();
                    gson.toJson(ticketExternalRemarkCustomerMessage);
//                    messageSender.send(ticketExternalRemarkCustomerMessage, RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER);
                    kafkaMessageSender.send(new KafkaMessageData(ticketExternalRemarkCustomerMessage,TicketExternalRemarkCustomerMessage.class.getSimpleName(),KafkaConstant.SEND_EXTERNAL_REMARK_FOR_TASK));
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
