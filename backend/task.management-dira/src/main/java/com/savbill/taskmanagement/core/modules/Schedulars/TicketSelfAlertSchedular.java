package com.savbill.taskmanagement.core.modules.Schedulars;

import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.constants.CommonConstants;
import com.savbill.taskmanagement.core.dto.SendTicketDetailDTO;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.security.spring.SpringContext;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.TicketAlertStaffMessage;
import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.ticketselfalert.scheduling")
public class TicketSelfAlertSchedular {
    final Logger log = LoggerFactory.getLogger(TicketSelfAlertSchedular.class);

    @Scheduled(cron = "${cronjobtimeforticketselfalert}")
    public void ticketselfalertschdular(){
        System.out.println("*******Ticket Self Alert started********");
        sendticketselfalertnotification();
        System.out.println("*******Ticket Self Alert ended********");
    }

    public void sendticketselfalertnotification() {
        CaseRepository caseRepository = SpringContext.getBean(CaseRepository.class);
        List<String> caseStatusList = new ArrayList<>();
        caseStatusList.add(CaseConstants.STATUS_RESOLVED);
        caseStatusList.add(CaseConstants.STATUS_OPEN);
        caseStatusList.add(CaseConstants.STATUS_CLOSED);
        caseStatusList.add(CaseConstants.STATUS_ON_HOLD);
        caseStatusList.add(CaseConstants.STATUS_OUT_OF_DOMAIN);
        List<Case> getAllUnresolveTicket = caseRepository.findAllByCaseStatusNotInAndIsDeleteIsFalseAndCurrentAssignee_IdIsNotNull(caseStatusList);
        if (!getAllUnresolveTicket.isEmpty()) {
            log.info("InProgress ticket found: " + getAllUnresolveTicket.size());
            findAllTicketFromstaff(getAllUnresolveTicket);
        } else {
            log.info("No ticket available In In-progress");
        }
    }

    /**@Author Dhaval Khalasi
     * This method is for find all staff with there respective assigned ticket
     * **/
    public void findAllTicketFromstaff(List<Case> caseList){
      List<StaffUser> staffUserList  =   caseList.stream().map(Case::getCurrentAssignee).collect(Collectors.toList());
      staffUserList = staffUserList.stream().filter(staffUser -> staffUser.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && staffUser.getIsDelete().equals(false)).distinct().collect(Collectors.toList());
      for(StaffUser staffUser : staffUserList){
          List<Case> filterCaseList = new ArrayList<>();
          filterCaseList = caseList.stream().filter(aCase -> aCase.getCurrentAssignee() == staffUser).collect(Collectors.toList());
          sendAllNotificationToStaffForTicketAlert(filterCaseList , staffUser);
      }

    }

    /**@Author Dhaval Khalasi
     * This is method for sending notification to staff
     * **/
    public void sendAllNotificationToStaffForTicketAlert(List<Case> caseList , StaffUser staffUser){
        Integer mvnoId = caseList.stream().map(Case::getMvnoId).collect(Collectors.toList()).get(0);
        Long buId = caseList.stream().map(Case::getBuId).collect(Collectors.toList()).get(0);
        List<SendTicketDetailDTO> sendTicketDetailDTOList = getAllTicketDTO(caseList);
        String parentStaffEmail = null;
        if(staffUser.getParentStaffId() != null){
            StaffUserRepository staffUserRepository = SpringContext.getBean(StaffUserRepository.class);
            StaffUser parentStaffUser = staffUserRepository.findById(staffUser.getParentStaffId()).get();
            parentStaffEmail = parentStaffUser.getEmail();
        }
        TicketAlertStaffMessage ticketAlertStaffMessage = new TicketAlertStaffMessage(staffUser.getUsername(), staffUser.getPhone() , staffUser.getEmail() , staffUser.getCountryCode(),parentStaffEmail, sendTicketDetailDTOList , mvnoId , buId );
        Gson gson = new Gson();
        gson.toJson(ticketAlertStaffMessage);
//        MessageSender messageSender = SpringContext.getBean(MessageSender.class);
//        messageSender.send(ticketAlertStaffMessage , RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF);
        KafkaMessageSender kafkaMessageSender = SpringContext.getBean(KafkaMessageSender.class);
        kafkaMessageSender.send(new KafkaMessageData(ticketAlertStaffMessage, TicketAlertStaffMessage.class.getSimpleName(), KafkaConstant.SEND_TASK_ALERT_REMARK) );
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
