package com.savbill.inventorymanagement.modules.ClientService;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
//import com.savbill.inventorymanagement.rabbitmq.MessageReceiver;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveClientServMessge;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateClientServMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceService extends ExBaseAbstractService<ClientServicePojo, ClientService, Integer> {

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private ClientServiceMapper clientServiceMapper;

    public ClientServiceService(ClientServiceRepository repository, ClientServiceMapper mapper) {
        super(repository, mapper);
    }
    private static final Logger logger = Logger.getLogger(ClientServiceService.class);
    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public List<ClientServicePojo> getClientSrvByName(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        mvnoIds.add(getMvnoIdFromCurrentStaff());
        return clientServiceRepository.findAll().stream().filter(data -> data.getName().
                        equalsIgnoreCase(name)).map(data -> clientServiceMapper.domainToDTO
                        (data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
    }

    public ClientService getByName(String name , Integer mvnoId) {
        if(getMvnoIdFromCurrentStaff() == null)
            return clientServiceRepository.getByNameAndMvnoId(name , 1);
        return clientServiceRepository.getByNameAndMvnoId(name, mvnoId);
    }

    // Shared Data From Common APIGW to CMS
    public void saveSharedClientService(SaveClientServMessge message) throws Exception{
        try {
            ClientService clientService = new ClientService();
            clientService.setId(message.getId());
            clientService.setName(message.getName());
            clientService.setValue(message.getValue());
            clientService.setMvnoId(message.getMvnoId());
            clientServiceRepository.save(clientService);
            logger.info("Client Service created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create client service with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateSharedClientService(UpdateClientServMessage message) throws Exception {
        try {
            ClientService clientService = clientServiceRepository.getByNameAndMvnoId(message.getName(),message.getMvnoId());
            if (clientService != null) {
                clientService.setName(message.getName());
                clientService.setValue(message.getValue());
                clientService.setMvnoId(message.getMvnoId());
                clientServiceRepository.save(clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            } else {
                ClientService clientService1 = new ClientService();
                clientService1.setId(message.getId());
                clientService1.setName(message.getName());
                clientService1.setValue(message.getValue());
                clientService1.setMvnoId(message.getMvnoId());
                clientServiceRepository.save(clientService1);
                logger.info("Client service updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update client service with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
