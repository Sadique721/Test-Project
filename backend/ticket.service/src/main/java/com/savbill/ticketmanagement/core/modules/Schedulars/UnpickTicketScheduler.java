package com.savbill.ticketmanagement.core.modules.Schedulars;

import com.savbill.ticketmanagement.core.constants.CaseConstants;
import com.savbill.ticketmanagement.core.constants.CommonConstants;
import com.savbill.ticketmanagement.core.dto.SendTicketDetailDTO;
import com.savbill.ticketmanagement.core.modules.ClientServ.repository.ClientServiceRepository;
import com.savbill.ticketmanagement.core.modules.Mvno.repository.MvnoRepository;
import com.savbill.ticketmanagement.core.modules.Teams.repository.TeamUserMappingsRepocitory;
import com.savbill.ticketmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.ticketmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.ticketmanagement.core.modules.tickets.domain.Case;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketAssignStaffMapping;
import com.savbill.ticketmanagement.core.modules.tickets.repository.CaseRepository;
import com.savbill.ticketmanagement.core.modules.tickets.repository.TicketAssignStaffMappingRepo;
import com.savbill.ticketmanagement.core.modules.tickets.service.CaseService;
import com.savbill.ticketmanagement.core.security.spring.SpringContext;
import com.savbill.ticketmanagement.kafka.KafkaConstant;
import com.savbill.ticketmanagement.kafka.KafkaMessageData;
import com.savbill.ticketmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.ticketmanagement.rabbitmq.messages.UnPickTicketAlertStaffMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.unpickticket.scheduling")
public class UnpickTicketScheduler {

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    @Autowired
    StaffUserRepository staffUserRepository;


    final Logger log = LoggerFactory.getLogger(UnpickTicketScheduler.class);

    @Scheduled(cron = "${cronjobtimeforunpickticket}")
    public void unPickScheduler(){
        System.out.println("******* UnPick/UnAssigned Ticket Scheduler started ********");
        sendUnPickTicketNotification();
        System.out.println("******* UnPick/UnAssigned Ticket Scheduler ended********");
    }

    public void sendUnPickTicketNotification(){
        CaseRepository caseRepository = SpringContext.getBean(CaseRepository.class);
        List<String> caseStatusList = new ArrayList<>();
        caseStatusList.add(CaseConstants.STATUS_OPEN);
        caseStatusList.add(CaseConstants.STATUS_FOLLOW_UP);
       // caseStatusList.add(CaseConstants.STATUS_RAISE_AND_CLOSE);
        List<Case> getAllUnPickTicket = caseRepository.findAllByCaseStatusInAndIsDeleteIsFalseAndCurrentAssignee_IdIsNull(caseStatusList);
        if (!getAllUnPickTicket.isEmpty()) {
            log.info("UnPick ticket found: " + getAllUnPickTicket.size());
            findAllStaffFromTicket(getAllUnPickTicket);

        } else {
            log.info("There is no UnPick Ticket found");
        }
    }

    public void findAllStaffFromTicket(List<Case> unPickCaseList){
        TicketAssignStaffMappingRepo ticketAssignStaffMappingRepo = SpringContext.getBean(TicketAssignStaffMappingRepo.class);
        List<Long> caseIdList = unPickCaseList.stream().map(Case::getCaseId).collect(Collectors.toList());
        List<TicketAssignStaffMapping> ticketAssignStaffMappingList = ticketAssignStaffMappingRepo.findAllByTicketIdIn(caseIdList);
        List<Integer> staffUserIdList = ticketAssignStaffMappingList.stream().map(TicketAssignStaffMapping::getStaffId).collect(Collectors.toList());
        List<StaffUser> staffUserList = staffUserRepository.findByIdIn(staffUserIdList);
        staffUserList = staffUserList.stream().filter(staffUser -> staffUser.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && staffUser.getIsDelete().equals(false)).distinct().collect(Collectors.toList());
        if(!staffUserList.isEmpty()) {
            log.info("staffuser list is found");
            for (StaffUser staffUser : staffUserList){
                List<Long> unpickCaseIdListByStaff = ticketAssignStaffMappingList.stream().filter(ticketAssignStaffMapping -> Objects.equals(ticketAssignStaffMapping.getStaffId(), staffUser.getId())).map(TicketAssignStaffMapping::getTicketId).collect(Collectors.toList());
                List<Case> filterCaseListByStaff = unPickCaseList.stream().filter(aCase ->  unpickCaseIdListByStaff.contains(aCase.getCaseId())).collect(Collectors.toList());
                List<SendTicketDetailDTO> sendTicketDetailDTOList = getAllTicketDTO(filterCaseListByStaff);
                String parentStaffEmail = null;
                if(staffUser.getParentStaffId() != null){
                    StaffUser parentStaffUser1 =  staffUserRepository.findById(staffUser.getParentStaffId()).get();
                    parentStaffEmail = parentStaffUser1.getEmail();
                }
                Integer mvnoId = filterCaseListByStaff.get(0).getMvnoId();
                Long buId = filterCaseListByStaff.get(0).getBuId();
                sendUnPickTicketMessage(staffUser.getUsername() , staffUser.getPhone() , staffUser.getEmail() , staffUser.getCountryCode() , parentStaffEmail , sendTicketDetailDTOList , mvnoId , buId);
            }
        }
        else{
            log.info("nothing happened staffuserlist is empty");
        }
    }

    public void sendUnPickTicketMessage(String staffName , String staffMobileNumber , String staffEmail , String countryCode , String parentStaffEmail ,List<SendTicketDetailDTO> sendTicketDetailDTOList, Integer mvnoId , Long buId){
        UnPickTicketAlertStaffMessage unPickTicketAlertStaffMessage = new UnPickTicketAlertStaffMessage(staffName , staffMobileNumber , staffEmail , countryCode,parentStaffEmail , sendTicketDetailDTOList , mvnoId , buId);
        Gson gson = new Gson();
        gson.toJson(unPickTicketAlertStaffMessage);
//        MessageSender messageSender = SpringContext.getBean(MessageSender.class);
//        messageSender.send(unPickTicketAlertStaffMessage , RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF);
        KafkaMessageSender kafkaMessageSender = SpringContext.getBean(KafkaMessageSender.class);
        kafkaMessageSender.send(new KafkaMessageData(unPickTicketAlertStaffMessage, UnPickTicketAlertStaffMessage.class.getSimpleName(), KafkaConstant.SEND_TICKET_ALERT_REMARK));
    }

    /**@Author
     * Dhaval Khalasi
     * This method called convert list ticket data to send ticket dto
     * **/

    public List<SendTicketDetailDTO> getAllTicketDTO(List<Case> caseList){
        CaseService caseService = SpringContext.getBean(CaseService.class);
        List<SendTicketDetailDTO> sendTicketDetailDTOList = new ArrayList<>();
        for(Case ticket : caseList){
            sendTicketDetailDTOList.add(caseService.convertCaseToSendTicketDetailDTO(ticket));
        }
        return sendTicketDetailDTOList;
    }
}
