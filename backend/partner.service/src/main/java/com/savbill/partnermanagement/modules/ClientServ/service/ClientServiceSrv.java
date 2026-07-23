package com.savbill.partnermanagement.modules.ClientServ.service;

import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.UpdateClientServMessage;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.ClientServ.domain.ClientService;
import com.savbill.partnermanagement.modules.ClientServ.dto.ClientServicePojo;
import com.savbill.partnermanagement.modules.ClientServ.mapper.ClientServiceMapper;
import com.savbill.partnermanagement.modules.ClientServ.repository.ClientServiceRepository;
//import com.savbill.partnermanagement.rabbitmq.MessageReceiver;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClientServiceSrv  {

    @Autowired
    private ClientServiceRepository entityRepository;

    @Autowired
    private ClientServiceMapper clientServiceMapper;

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceSrv.class);
    public ClientService save(ClientService clientService) {
        return this.entityRepository.save(clientService);
    }


    public ClientService getByName(String name , Integer mvnoId) {
        return this.entityRepository.getByNameAndMvnoId(name , mvnoId);
    }

    public ClientService searchByName(String name , Integer mvnoId) {
        return entityRepository.getByNameAndMvnoId(name , mvnoId);
    }



    private static Log log = LogFactory.getLog(ClientService.class);


    public ClientService update(ClientService clientService) {
        ClientService existingClientService = this.entityRepository.getByNameAndMvnoId(clientService.getName() , clientService.getMvnoId());
        if(existingClientService != null) {
            existingClientService.setName(clientService.getName());
            existingClientService.setValue(clientService.getValue());
            existingClientService.setMvnoId(clientService.getMvnoId());
            logger.info("Client service updated successfully with name " + clientService.getName());
            return this.entityRepository.save(existingClientService);
        }
        logger.error("Unable to update client service with name " + clientService.getName());
        return null;
    }


    public List<ClientServicePojo> getClientSrvByName(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        mvnoIds.add(getMvnoIdFromCurrentStaff());
        logger.info("Client service updated successfully with name " + name);
        return entityRepository.findAll().stream().filter(data -> data.getName().
                        equalsIgnoreCase(name)).map(data -> clientServiceMapper.domainToDTO
                        (data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
    }


    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                logger.info("getMvnoIdFromCurrentStaff()");
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    // Shared Data From Common APIGW to Partner
    public void saveSharedClientService(SaveClientServMessge message) {
        try {
            ClientService clientService = new ClientService();
            clientService.setId(message.getId());
            clientService.setName(message.getName());
            clientService.setValue(message.getValue());
            clientService.setMvnoId(message.getMvnoId());
            entityRepository.save(clientService);
            logger.info("Client Service created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create client service with name " + message.getName(), e.getMessage());
        }
    }

    public void updateSharedClientService(UpdateClientServMessage message) throws Exception {
        try {
            ClientService clientService = entityRepository.getByNameAndMvnoId(message.getName(),message.getMvnoId());
            if (clientService != null) {
                clientService.setName(message.getName());
                clientService.setValue(message.getValue());
                clientService.setMvnoId(message.getMvnoId());
                entityRepository.save(clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            } else {
                ClientService clientService1 = new ClientService();
                clientService1.setId(message.getId());
                clientService1.setName(message.getName());
                clientService1.setValue(message.getValue());
                clientService1.setMvnoId(message.getMvnoId());
                entityRepository.save(clientService1);
                logger.info("Client service updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update client service with name " + message.getName(), e.getMessage());
        }
    }
}
