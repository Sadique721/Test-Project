package com.savbill.cpm.service.common;

import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.UpdateClientServMessage;
import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.cacheKeys;
import com.savbill.cpm.controller.api.APIController;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.mapper.ClientServiceMapper;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.modules.Mvno.domain.Mvno;
import com.savbill.cpm.pojo.ClientServicePojo;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.ClientServiceMessage;
import com.savbill.cpm.repository.common.ClientServiceRepository;
import com.savbill.cpm.service.CacheService;
import com.savbill.cpm.service.radius.AbstractService;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.savbill.cpm.core.utillity.log.ApplicationLogger.logger;

@Service
public class ClientServiceSrv extends AbstractService<ClientService, ClientServicePojo, Integer> {

    @Autowired
    private ClientServiceRepository entityRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private ClientServiceMapper clientServiceMapper;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    CacheService cacheService;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<ClientService, Integer> getRepository() {
        return entityRepository;
    }

    public ClientService searchByName(String name) {
        return entityRepository.findByNameAndMvnoId(name,getLoggedInMvnoId());
    }

    public List<ClientService> getAllEntity() {
        return entityRepository.findAll().stream().filter(clientService -> clientService.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || clientService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    public List<ClientService> saveAllEntity(List<ClientService> list) {
        return entityRepository.saveAll(list);
    }

    public ClientService getByName(String name) {

        return entityRepository.getByNameAndMvnoId(name, getMvnoIdFromCurrentStaff());
    }
    public ClientService getByNameAndMvnoId(String name,Integer mvnoId) {

        return entityRepository.getByNameAndMvnoId(name, mvnoId);
    }
    public ClientService getCurrencyByName(String name) {

        return entityRepository.getByNameAndMvnoIdEquals(name, getMvnoIdFromCurrentStaff());
    }


    public ClientServicePojo getByClientServicePojoName(String name) {

        return clientServiceMapper.domainToDTO(entityRepository.getByNameAndMvnoId(name, getMvnoIdFromCurrentStaff()), new
                CycleAvoidingMappingContext());
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.common.ClientService', '1')")
    public List<ClientServicePojo> convertResponseModelIntoPojo(List<ClientService> clinetServiceList) throws Exception {
        List<ClientServicePojo> pojoListRes = new ArrayList<ClientServicePojo>();
        if (clinetServiceList != null && clinetServiceList.size() > 0) {
            for (ClientService clientServ : clinetServiceList) {
                pojoListRes.add(convertConfigurationModelToConfigurationPojo(clientServ));
            }
        }
        return pojoListRes;
    }

    public ClientServicePojo convertConfigurationModelToConfigurationPojo(ClientService clientService) throws Exception {
        ClientServicePojo pojo = null;
        if (clientService != null) {
            pojo = new ClientServicePojo();
            pojo.setId(clientService.getId());
            pojo.setName(clientService.getName());
            pojo.setValue(clientService.getValue());
            if(clientService.getMvnoId() != null) {
            	pojo.setMvnoId(clientService.getMvnoId());
            }
        }
        return pojo;
    }


    @PreAuthorize("hasPermission('com.savbill.cpm.model.common.ClientService', '2')")
    public ClientServicePojo save(ClientServicePojo pojo) throws Exception {
        ClientService oldObj = null;
        if (pojo.getId() != null) {
            oldObj = get(pojo.getId());
        }
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
        log.info("ClientService update details " + UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        obj = saveClientService(obj);
        pojo = convertConfigurationModelToConfigurationPojo(obj);
        createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.common.ClientService', '2')")
    public ClientServicePojo update(ClientServicePojo pojo) throws Exception {
//        ClientService oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
        getEntityForUpdateAndDelete(pojo.getId());
//        if(oldObj!=null) {
//            log.info("ClientService update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//        }
        obj = saveClientService(obj);
        pojo = convertConfigurationModelToConfigurationPojo(obj);
        ClientService save = entityRepository.save(obj);
        String cacheKey = cacheKeys.CLIENTSERVICE + save.getName() ; // Create a unique cache key
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);

        String valueToCache = save.getValue();
        String cacheKey_name = cacheKeys.CLIENTSERVICE_NAME_MVNO + save.getName() + "_" + save.getMvnoId();
        cacheService.saveOrUpdateInCacheAsync(obj.getName(),cacheKey_name);
        createDataSharedService.updateEntityDataForAllMicroService(obj);
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.common.ClientService', '2')")
    public ClientService saveClientService(ClientService clientService) throws Exception {
        // String operation="edit";
        // if(clientService !=null && clientService.getId()==null){
        //  operation = "add";
        //}
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		 clientService.setMvnoId(getMvnoIdFromCurrentStaff());
//     	}
        ClientService save = entityRepository.save(clientService);
        String cacheKey = cacheKeys.CLIENTSERVICE + save.getName() ; // Create a unique cache key
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);

        String valueToCache = save.getValue();
        String cacheKey_name = cacheKeys.CLIENTSERVICE_NAME_MVNO + save.getName() + "_" + save.getMvnoId();
        cacheService.saveOrUpdateInCacheAsync(valueToCache,cacheKey_name);
        //send message
        ClientServiceMessage clientServiceMessage = new ClientServiceMessage(save.getId(),save.getName(),save.getValue(),save.getMvnoId());
      kafkaMessageSender.send(new KafkaMessageData(clientServiceMessage, ClientServiceMessage.class.getSimpleName()));
//        messageSender.send(clientServiceMessage, RabbitMqConstants.QUEUE_CLIENT_SERVICE_UPDATE);

return save;
    }

    public ClientService convertClientServicePojoToClientServiceModel(ClientServicePojo clientServicePojo) throws Exception {
        ClientService clientService = null;
        if (clientServicePojo != null) {
            clientService = new ClientService();
            if (clientServicePojo.getId() != null) {
                clientService.setId(clientServicePojo.getId());
            }
            clientService.setName(clientServicePojo.getName());
            clientService.setValue(clientServicePojo.getValue());
            if(clientServicePojo.getMvnoId() != null) {
            	clientService.setMvnoId(clientServicePojo.getMvnoId());
            }
        }
        return clientService;
    }

   // @Cacheable(cacheNames = "clientSrv", key = "#name")
    public List<ClientServicePojo> getClientSrvByNameLight(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        mvnoIds.add(getMvnoIdFromCurrentStaff());
        return entityRepository.findAll()
                .stream()
                .filter(data -> data.getName().
                equalsIgnoreCase(name))
                .map(data -> clientServiceMapper.domainToDTO
                (data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList())
                .stream()
                .filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
    }


    public List<ClientServicePojo> getClientSrvByName(String name) {
        List<Integer> mvnoIds = new ArrayList<>();
        List<ClientServicePojo> servicePojoList = new ArrayList<>();
        mvnoIds.add(1);
        mvnoIds.add(getMvnoIdFromCurrentStaff());
        List<ClientService> clientServicePojoList = entityRepository.findAllByNameAndMvnoIdIn(name,mvnoIds);
        for(ClientService clientService : clientServicePojoList){
            ClientServicePojo pojo = new ClientServicePojo();
            pojo = convertToClientServicePojo(clientService);
            servicePojoList.add(pojo);
        }

        return servicePojoList;
    }

    @Cacheable(cacheNames = "clientSrv")
    public List<ClientServicePojo> getAllClientSrv() {
        return entityRepository.findAll().stream().map(data -> clientServiceMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public void validateRequest(ClientServicePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }

        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Client Service");
        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, ClientServicePojo.class, clientServicePojos, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<ClientServicePojo> clientServicePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, ClientServicePojo.class, clientServicePojos, null);
    }

    public String getValueByName(String name) {
        return entityRepository.findValueByNameandMvnoId(name,getLoggedInMvnoId());
    }
    public String getValueByNameAndmvnoId(String name,Integer mvnoId) {
        String cacheKey = cacheKeys.CLIENTSERVICE_NAME_MVNO + name + "_" + mvnoId;
        String value = null;

        try {
            value = (String) cacheService.getFromCache(cacheKey, String.class);

            if (value != null) {
                return value;
            }
            value = entityRepository.findValueByNameandMvnoId(name, mvnoId);

            if (value != null) {
                cacheService.putInCache(cacheKey, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }


    @Override
    public ClientService get(Integer id) {
        ClientService clientService = super.get(id);
        if (getMvnoIdFromCurrentStaff() == null)
            return clientService;
        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (clientService.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || clientService.getMvnoId().intValue() == 1))
            return clientService;
        return null;
    }

    public ClientService getEntityForUpdateAndDelete(Integer id) {
        ClientService clientService = get(id);
        if(clientService == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == clientService.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return clientService;
    }

    // Shared Data From Common APIGW to CMS
    public void saveSharedClientService(SaveClientServMessge message) throws Exception{
        try {
            ClientService clientService = new ClientService();
            clientService.setId(message.getId());
            clientService.setName(message.getName());
            clientService.setValue(message.getValue());
            clientService.setMvnoId(message.getMvnoId());
            entityRepository.save(clientService);
            String cacheKey = cacheKeys.CLIENTSERVICE + clientService.getName() + ":" + clientService.getMvnoId(); // Create a unique cache key
            cacheService.putInCache(cacheKey, clientService);
            logger.info("Client Service created successfully with name " + message.getName());
        }catch (CustomValidationException e) {
            logger.error("Unable to create client service with name " + message.getName(), e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
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
                String cacheKey = cacheKeys.CLIENTSERVICE + clientService.getName() + ":" + clientService.getMvnoId(); // Create a unique cache key
                cacheService.putInCache(cacheKey, clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            } else {
                ClientService clientService1 = new ClientService();
                clientService1.setId(entityRepository.findlast()+1);
                clientService1.setName(message.getName());
                clientService1.setValue(message.getValue());
                clientService1.setMvnoId(message.getMvnoId());
                entityRepository.save(clientService1);
                String cacheKey = cacheKeys.CLIENTSERVICE + clientService1.getName() + ":" + clientService1.getMvnoId(); // Create a unique cache key
                cacheService.putInCache(cacheKey, clientService);
                logger.info("Client service updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update client service with name " + message.getName(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to update client service with name " + message.getName(), e.getMessage());
        }
    }
    public ClientService getCurrencyByNameAndMvnoId(String name,Integer mvnoId) {
        return entityRepository.getByNameAndMvnoIdEquals(name, mvnoId);
    }

    public ClientService getByNameAndMvnoIdEquals(String name, Integer mvnoId) {
        String cacheKey = cacheKeys.CLIENTSERVICE + name + ":" + mvnoId; // Create a unique cache key
        ClientService clientService = null;

        try {
            clientService = (ClientService) cacheService.getFromCache(cacheKey, ClientService.class);
            if (clientService != null) {
                return clientService;
            }
            clientService = entityRepository.getByNameAndMvnoIdEquals(name, mvnoId);

            if (clientService != null) {
                cacheService.putInCache(cacheKey, clientService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return clientService;
    }

    public void addDefaultPathWhenMvnoCreated(Mvno mvno) {
        List<ClientService> clientServices = new ArrayList<>();
        Integer clientService =entityRepository.findlast();
        if(!entityRepository.existsByNameAndMvnoId("mvnodocpathread", mvno.getId().intValue())) {
            ClientService service = new ClientService("mvnodocpathread", "//var/document/mvnodocpath/",mvno.getId().intValue());
          service.setId(clientService+1);
            clientServices.add(service);
        }
        if(!entityRepository.existsByNameAndMvnoId("mvnodocpath", mvno.getId().intValue())) {
            ClientService service = new ClientService("mvnodocpath", "//var/document/mvnodocpath/",mvno.getId().intValue());
            service.setId(clientService+2);
            clientServices.add(service);
        }
        if(!CollectionUtils.isEmpty(clientServices)) {
            entityRepository.saveAll(clientServices);
        }
    }

    private ClientServicePojo convertToClientServicePojo(ClientService clientService) {
        ClientServicePojo pojo = new ClientServicePojo();
        pojo.setId(clientService.getId());
        pojo.setName((clientService.getName()));
        pojo.setMvnoId(clientService.getMvnoId());
        pojo.setValue(clientService.getValue());
        pojo.setDisplayName(clientService.getName());
        pojo.setDisplayId(clientService.getId());

        return pojo;
    }

    public ClientService getByNames(String name , Integer mvnoId) {
        if(getMvnoIdFromCurrentStaff() == null)
            return entityRepository.getByNameAndMvnoId(name , 2);
        return entityRepository.getByNameAndMvnoId(name, mvnoId);
    }

}
