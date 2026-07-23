package com.savbill.commonGateway.moules.Communication.service;

import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.moules.Communication.Constants.CommunicationConstant;
import com.savbill.commonGateway.moules.Communication.Repository.CommunicationRepository;
import com.savbill.commonGateway.moules.Communication.domain.Communication;
import com.savbill.commonGateway.moules.Communication.dto.CommunicationDTO;
import com.savbill.commonGateway.moules.Communication.mapper.CommunicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonCommunicationService extends ExBaseAbstractService<CommunicationDTO, Communication, Long> {

    @Autowired
    private CommunicationRepository communicationRepository;
//    @Autowired
//    private CommunicationPoolExecutor executor;
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    public CommonCommunicationService(CommunicationRepository repository, CommunicationMapper mapper) {
        super(repository, mapper);
    }

    public void previousSchedules() {
        List<Communication> tempCommunicationListEmail = new ArrayList<>();
        List<Communication> tempCommunicationListSMS = new ArrayList<>();
        List<Communication> communicationList = communicationRepository.getBySendedAndError();
        communicationList.forEach(data -> {
            if (data.getChannel().equalsIgnoreCase(CommunicationConstant.COMMUNICATION_CHANNEL_EMAIL)) {
                tempCommunicationListEmail.add(data);
            }
            if (data.getChannel().equalsIgnoreCase(CommunicationConstant.COMMUNICATION_CHANNEL_SMS)) {
                tempCommunicationListSMS.add(data);
            }
        });
      //  this.createEmailThreads(tempCommunicationListEmail);
      //  this.createSmsThreads(tempCommunicationListSMS);
    }

//    public void createSmsThreads(List<Communication> communicationList) {
//        communicationList.forEach(data -> {
//            executor.execute(new SmsWorkerThread(data, restTemplateBuilder));
//        });
//    }

//    public void createEmailThreads(List<Communication> communicationList) {
//        communicationList.forEach(data -> {
//            executor.execute(new EmailWorkerThread(data));
//        });
//    }

    @Override
    public String getModuleNameForLog() {
        return "[Communication Service]";
    }
}
