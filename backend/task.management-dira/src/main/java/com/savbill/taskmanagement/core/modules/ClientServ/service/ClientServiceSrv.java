package com.savbill.taskmanagement.core.modules.ClientServ.service;


import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.taskmanagement.core.modules.ClientServ.dto.ClientServicePojo;
import com.savbill.taskmanagement.core.modules.ClientServ.mapper.ClientServiceMapper;
import com.savbill.taskmanagement.core.modules.ClientServ.repository.ClientServiceRepository;
import com.savbill.taskmanagement.core.service.AbstractService;
////import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveClientServMessge;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateClientServMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClientServiceSrv extends AbstractService<ClientService, ClientServicePojo, Integer> {


    @Autowired
    private ClientServiceRepository clientServiceRepository;



    @Autowired
    private ClientServiceMapper clientServiceMapper;


    public ClientService searchByName(String name) {
        return this.clientServiceRepository.findByNameAndMvnoId(name,getMvnoIdFromCurrentStaff());
    }


    public List<ClientService> getAllEntity() {
        return this.clientServiceRepository.findAll();
    }


    public List<ClientService> saveAllEntity(List<ClientService> list) {
        return this.clientServiceRepository.saveAll(list);
    }


    @Override
    protected JpaRepository<ClientService, Integer> getRepository() {
        return clientServiceRepository;
    }

    public ClientService save(ClientService clientService) {
        return this.clientServiceRepository.save(clientService);
    }


    public ClientService getByName(String name) {
        if(getMvnoIdFromCurrentStaff() == null)
            return clientServiceRepository.getByNameAndMvnoIdIn(name, Collections.singletonList(2));
        return clientServiceRepository.findByNameEqualsIgnoreCaseAndMvnoId(name, getMvnoIdFromCurrentStaff());
    }

    public ClientService getByNameAndMvnoId(String name,Integer mvnoid) {
        return clientServiceRepository.getByNameAndMvnoIdIn(name, Arrays.asList(mvnoid));
    }

//    public List<ClientService> getClientSrvByName(String name) {
//        return this.clientServiceRepository.findAll().stream().filter(data -> data.getName().
//                equalsIgnoreCase(name)).collect(Collectors.toList());
//    }

    private static Log log = LogFactory.getLog(ClientServiceSrv.class);


    public ClientService update(ClientService clientService) {
        ClientService existingClientService = this.clientServiceRepository.findByNameAndMvnoId(clientService.getName(),getMvnoIdFromCurrentStaff());
        if(existingClientService != null) {
            existingClientService.setName(clientService.getName());
            existingClientService.setValue(clientService.getValue());
            existingClientService.setMvnoId(clientService.getMvnoId());
            return this.clientServiceRepository.save(existingClientService);
        }
        return null;
    }

    public String getValueByName(String name) {
        return clientServiceRepository.findValueByNameAndMvnoId(name,getLoggedInMvnoId());
    }


    public List<ClientServicePojo> getClientSrvByName(String name) {
        Integer loggedMvnoId = getMvnoIdFromCurrentStaff();
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        mvnoIds.add(getMvnoIdFromCurrentStaff());
//        return clientServiceRepository.findAll().stream().filter(data -> data.getName().
//                        equalsIgnoreCase(name)).map(data -> clientServiceMapper.domainToDTO
//                        (data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
//                .stream().filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());

        return clientServiceRepository.findAll().stream()
                .filter(data -> data.getName().equalsIgnoreCase(name))
                .map(data -> clientServiceMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                .filter(clientServicePojo ->
                        clientServicePojo.getMvnoId() == 1 ||
                                clientServicePojo.getMvnoId().equals(loggedMvnoId) ||
                                mvnoIds.get(0) == 1
                )
                .collect(Collectors.toList());
    }


    public void saveClientServData(SaveClientServMessge messge){
        try {
            ClientService clientService = new ClientService();
            clientService.setId(messge.getId());
            clientService.setName(messge.getName());
            clientService.setValue(messge.getValue());
            clientService.setMvnoId(messge.getMvnoId());
            clientServiceRepository.save(clientService);
        }catch (Exception exception){
            log.error("Error saving clienServ data in to ticket microservice"+ exception.getMessage());
        }
    }
    public void updateClientServData(UpdateClientServMessage messge){
        try {
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId(messge.getName(),getMvnoIdFromCurrentStaff());
            if(clientService!=null){
                clientService.setName(messge.getName());
                clientService.setValue(messge.getValue());
                clientService.setMvnoId(messge.getMvnoId());
                clientServiceRepository.save(clientService);
            }
        }catch (Exception exception){
            log.error("Error updating clienServ data in to ticket microservice"+ exception.getMessage());
        }
    }


//    @Autowired
//    private ClientServiceRepository entityRepository;
//
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private ClientServiceMapper clientServiceMapper;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Override
//    protected JpaRepository<ClientService, Integer> getRepository() {
//        return entityRepository;
//    }
//
//    public ClientService searchByName(String name) {
//        return entityRepository.findByName(name);
//    }
//
//    public List<ClientService> getAllEntity() {
//        return entityRepository.findAll().stream().filter(clientService -> clientService.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || clientService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//    }
//
//    public List<ClientService> saveAllEntity(List<ClientService> list) {
//        return entityRepository.saveAll(list);
//    }
//
//    public ClientService getByName(String name) {
//        if(getMvnoIdFromCurrentStaff() == null)
//            return clientServiceRepository.findByName(name);
//        return clientServiceRepository.getByNameAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//    }
//
//    public ClientServicePojo getByClientServicePojoName(String name) {
//        if(getMvnoIdFromCurrentStaff() == null)
//            return clientServiceMapper.domainToDTO(entityRepository.findByName(name), new CycleAvoidingMappingContext());
//        return clientServiceMapper.domainToDTO(entityRepository.getByNameAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)), new
//                CycleAvoidingMappingContext());
//    }
//
//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '1')")
//    public List<ClientServicePojo> convertResponseModelIntoPojo(List<ClientService> clinetServiceList) throws Exception {
//        List<ClientServicePojo> pojoListRes = new ArrayList<ClientServicePojo>();
//        if (clinetServiceList != null && clinetServiceList.size() > 0) {
//            for (ClientService clientServ : clinetServiceList) {
//                pojoListRes.add(convertConfigurationModelToConfigurationPojo(clientServ));
//            }
//        }
//        return pojoListRes;
//    }
//
//    public ClientServicePojo convertConfigurationModelToConfigurationPojo(ClientService clientService) throws Exception {
//        ClientServicePojo pojo = null;
//        if (clientService != null) {
//            pojo = new ClientServicePojo();
//            pojo.setId(clientService.getId());
//            pojo.setName(clientService.getName());
//            pojo.setValue(clientService.getValue());
//            if(clientService.getMvnoId() != null) {
//            	pojo.setMvnoId(clientService.getMvnoId());
//            }
//        }
//        return pojo;
//    }
//
//
//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
//    public ClientServicePojo save(ClientServicePojo pojo) throws Exception {
//        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
//        obj = saveClientService(obj);
//        pojo = convertConfigurationModelToConfigurationPojo(obj);
//        return pojo;
//    }
//
//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
//    public ClientServicePojo update(ClientServicePojo pojo) throws Exception {
//        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
//        getEntityForUpdateAndDelete(pojo.getId());
//        obj = saveClientService(obj);
//        pojo = convertConfigurationModelToConfigurationPojo(obj);
//        return pojo;
//    }
//
//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
//    public ClientService saveClientService(ClientService clientService) throws Exception {
//        // String operation="edit";
//        // if(clientService !=null && clientService.getId()==null){
//        //  operation = "add";
//        //}
////    	if(getMvnoIdFromCurrentStaff() != null) {
////    		 clientService.setMvnoId(getMvnoIdFromCurrentStaff());
////     	}
//        ClientService save = entityRepository.save(clientService);
//        //send message
//        ClientServiceMessage clientServiceMessage = new ClientServiceMessage(save.getId(),save.getName(),save.getValue(),save.getMvnoId());
//        messageSender.send(clientServiceMessage, RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE);
//        return save;
//    }
//
//    public ClientService convertClientServicePojoToClientServiceModel(ClientServicePojo clientServicePojo) throws Exception {
//        ClientService clientService = null;
//        if (clientServicePojo != null) {
//            clientService = new ClientService();
//            if (clientServicePojo.getId() != null) {
//                clientService.setId(clientServicePojo.getId());
//            }
//            clientService.setName(clientServicePojo.getName());
//            clientService.setValue(clientServicePojo.getValue());
//            if(clientServicePojo.getMvnoId() != null) {
//            	clientService.setMvnoId(clientServicePojo.getMvnoId());
//            }
//        }
//        return clientService;
//    }
//
//   // @Cacheable(cacheNames = "clientSrv", key = "#name")
//    public List<ClientServicePojo> getClientSrvByName(String name) {
//        List<Integer> mvnoIds = new ArrayList<>();
//        mvnoIds.add(1);
//        mvnoIds.add(getMvnoIdFromCurrentStaff());
//        return entityRepository.findAll().stream().filter(data -> data.getName().
//                equalsIgnoreCase(name)).map(data -> clientServiceMapper.domainToDTO
//                (data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
//                .stream().filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
//    }
//
//    @Cacheable(cacheNames = "clientSrv")
//    public List<ClientServicePojo> getAllClientSrv() {
//        return entityRepository.findAll().stream().map(data -> clientServiceMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//    }
//
//    public void validateRequest(ClientServicePojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
//            if (pojo.getId() != null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//            }
//        }
//
//        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Client Service");
//        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, ClientServicePojo.class, clientServicePojos, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, ClientServicePojo.class, clientServicePojos, null);
//    }
//
//    public String getValueByName(String name) {
//        return entityRepository.findValueByName(name);
//    }
//
//    @Override
//    public ClientService get(Integer id) {
//        ClientService clientService = super.get(id);
//        if (getMvnoIdFromCurrentStaff() == null)
//            return clientService;
//        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (clientService.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || clientService.getMvnoId().intValue() == 1))
//            return clientService;
//        return null;
//    }
//
//    public ClientService getEntityForUpdateAndDelete(Integer id) {
//        ClientService clientService = get(id);
//        if(clientService == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == clientService.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return clientService;
//    }

    // Shared Data From Common APIGW to CMS
    public void saveSharedClientService(SaveClientServMessge message) throws Exception{
        try {
            ClientService clientService = new ClientService();
            clientService.setId(message.getId());
            clientService.setName(message.getName());
            clientService.setValue(message.getValue());
            clientService.setMvnoId(message.getMvnoId());
            clientServiceRepository.save(clientService);
            log.info("Client Service created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            log.error("Unable to create client service with name " + message.getName());
        }
    }

    public void updateSharedClientService(UpdateClientServMessage message) throws Exception {
        try {
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId(message.getName(), message.getMvnoId());
            if (clientService != null) {
                clientService.setName(message.getName());
                clientService.setValue(message.getValue());
                clientService.setMvnoId(message.getMvnoId());
                clientServiceRepository.save(clientService);
                log.info("Client service updated successfully with name " + message.getName());
            } else {
                ClientService clientService1 = new ClientService();
                Integer Id = clientServiceRepository.getLatestClientServiceId();
                clientService1.setId(Id);
                clientService1.setName(message.getName());
                clientService1.setValue(message.getValue());
                clientService1.setMvnoId(message.getMvnoId());
                clientServiceRepository.save(clientService1);
                log.info("Client service updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            log.error("Unable to update client service with name " + message.getName());
        }
    }
}
