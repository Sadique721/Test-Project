package com.savbill.commonGateway.common.service;


import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.mapper.ClientServiceMapper;
import com.savbill.commonGateway.common.model.ClientServicePojo;
import com.savbill.commonGateway.common.repository.ClientServiceRepository;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.SystemConfigurationMessage;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    CreateDataSharedService createDataSharedService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Override
    protected JpaRepository<ClientService, Integer> getRepository() {
        return entityRepository;
    }

    public ClientService searchByName(String name) {
        return entityRepository.getByNameAndMvnoId(name,getMvnoIdFromCurrentStaff());
    }


    public ClientService searchByName(String name,Integer mvnoId) {
        return entityRepository.getByNameAndMvnoId(name,mvnoId);
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceSrv.class);

    public List<ClientService> getAllEntity() {
        return entityRepository.findAllByMvnoId(getMvnoIdFromCurrentStaff());
//        .stream().filter(clientService -> clientService.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || clientService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    public List<ClientService> saveAllEntity(List<ClientService> list) {
        return entityRepository.saveAll(list);
    }

    public ClientService getByName(String name) {
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        return entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name,mvnoId);
    }

    public ClientService getByNameAndMvnoId(String name, Integer mvnoId) {
        return entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name,mvnoId);
    }

    public ClientService getByNameMVNO(String name) {
        ClientService clientService=entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name, getMvnoIdFromCurrentStaff());
        if(clientService==null){
            clientService=entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name, 1);
        }
        return clientService;
    }

    public ClientServicePojo getByClientServicePojoName(String name) {

        return clientServiceMapper.domainToDTO(entityRepository.getByNameAndMvnoId(name, getMvnoIdFromCurrentStaff()), new
                CycleAvoidingMappingContext());
    }
    public List<ClientServicePojo> getByClientServicePojoNamelike(String name) {
        List<ClientService> clientServiceList=new ArrayList<>();

        clientServiceList=entityRepository.getByNameContainingIgnoreCaseAndMvnoId(name, getMvnoIdFromCurrentStaff());

        return clientServiceList.stream().map(clientService ->clientServiceMapper.domainToDTO(clientService,new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '1')")
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

    public void createClientService(Mvno mvno) throws Exception{
        ArrayList<String>al=new ArrayList<>();
        ArrayList<String>val=new ArrayList<>();
        List<ClientService> defaultClientServiceList = new ArrayList<>();
        defaultClientServiceList = entityRepository.findAllByMvnoId(2);


//        al.add("MOBILE_NUMBER");
//        al.add("CURRENCY_SYMBOL");
//        al.add("CURRENCY_FOR_PAYMENT");
//        al.add("TICKET_COUNT");
//        al.add("TICKET_COUNT_IN_LAST_DAYS");
//        al.add("TICKET_COUNT_SAME_CATEGORY");
//        al.add("TICKET_COUNT_SAME_CATEGORY_DAYS");
//        al.add("ALLOWED_TICKET_DOCUMENT_SIZE");
//        al.add("ticketFollowUpReminderTime");
//        al.add("TicketsRaisedoption");
//        al.add("ServiceThroughEmail");
//        al.add("ServiceThroughLead");
//        al.add("EMAIL_STAFF_BU");
//        al.add("maildocpath");
//        al.add("ticketpath");
//        al.add("noLeadFollowupSendNotificationTimeInDays");
//        al.add("followUpReminderTime");
//        al.add("previousVendorType");
//        al.add("servicerType");
//        al.add("leadType");
//        al.add("leadCategory");
//        al.add("feasibility");
//        al.add("leadOriginTypes");
//        al.add("requireServiceTypes");
//        al.add("rescheduleFollowupRemarks");
//        al.add("leadcustomergender");
//        al.add("reOpenLeadInDays");
//        al.add("allowedDocumentSize");
//        al.add("document_verify_sandbox_key");
//        al.add("document_verify_sandbox_secret");
//        //Inventory
//        al.add("InventoryAssetOrganization");
//        al.add("trialPlanPeriodThreshold");
//        al.add("AllowNumberofTimeTrail");
//        al.add("EMAIL_TEAM");
//        al.add("ticketpathread");
//        al.add("PAYMENT_GATEWAY_FOR_CAPTIVE");
//        al.add("PAYMENT_GATEWAY_FOR_CWSC");
//        al.add("PAYMENT_GATEWAY_FOR_ADMIN");
//        al.add("PAYEMNT_GATEWAY");
//        al.add("PAYMENTOWNERREQUIRED");
//        al.add("COMMISSION_LEVEL");
//        al.add("suspend_cust_after_days");
//
//        val.add("10");
//        val.add("₹");
//        val.add("₹");
//        val.add("3");
//        val.add("30");
//        val.add("3");
//        val.add("30");
//        val.add("500");
//        val.add("1");
//        val.add("Re-open ticket,Sales Problem domain,Technical Problem domain");
//        val.add("1");
//        val.add("1");
//        val.add("RIBU");
//        val.add("//var/document/maildoc/");
//        val.add("//var/document/ticketdoc/");
//        val.add("1");
//        val.add("5");
//        val.add("GTPL,BSNL,Airtel,JIO");
//        val.add("DTV,Internet");
//        val.add("Hot,Warm,Cold");
//        val.add("New Lead, Existing Customer");
//        val.add("N/A,Feasibility At Lead,Feasibility At Booking");
//        val.add("Phone,Call,Email");
//        val.add("FTTH,Clear TV,Child,FTTH with DTV");
//        val.add("Confirm Later,Do Not Call,Expensive Package,Call rejected by Client");
//        val.add("Male,Female");
//        val.add("7");
//        val.add("2");
//        val.add("key_live_DPgZXgKsFrjltdfBAnse4rjtqhdYt97K");
//        val.add("secret_live_2T9zDvua7CZ92ZWJbwollNwDredvx18N");
//        //Inventory
//        val.add("Organization");
//        val.add("3");
//        val.add("3");
//        val.add("Email Team");
//        val.add("//var/document/ticketdoc/");
//        val.add("DEFUALT");
//        val.add("DEFUALT");
//        val.add("DEFUALT");
//        val.add("DEFUALT");
//        val.add("true");
//        val.add("1");
//        val.add("30");
        for (int i = 0; i < defaultClientServiceList.size() ; i++) {
            ClientServicePojo cp=new ClientServicePojo();
            cp.setMvnoId(mvno.getId().intValue());
            cp.setName(defaultClientServiceList.get(i).getName());
            cp.setValue(defaultClientServiceList.get(i).getValue());
            save(cp, CommonConstants.OPEARTION_MVNO_CREATE);
            logger.info("Successfully client service save with name " + cp.getName() + " with mvno " + mvno.getName());
        }
    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
    public ClientServicePojo save(ClientServicePojo pojo, Integer operation) throws Exception {
        try {
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            } else if (operation.equals(CommonConstants.OPEARTION_MVNO_CREATE)) {
                pojo.setMvnoId(pojo.getMvnoId());
            }
            ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
            obj = saveClientService(obj);
            pojo = convertConfigurationModelToConfigurationPojo(obj);
            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
            return pojo;
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
    public ClientServicePojo update(ClientServicePojo pojo) throws Exception {
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        ClientService obj = convertClientServicePojoToClientServiceModel(pojo);
        getEntityForUpdateAndDelete(pojo.getId());
        obj = saveClientService(obj);
        pojo = convertConfigurationModelToConfigurationPojo(obj);
        createDataSharedService.updateEntityDataForAllMicroService(obj);
        return pojo;
    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.ClientService', '2')")
    public ClientService saveClientService(ClientService clientService) throws Exception {
        ClientService save = entityRepository.save(clientService);
        if(save.getName().equalsIgnoreCase("radius_quota_notification_configuration")) {
            SystemConfigurationMessage systemConfigurationMessage = new SystemConfigurationMessage(Long.valueOf(save.getId()),save.getName(),save.getValue(),save.getMvnoId());
            //messageSender.send(systemConfigurationMessage, RabbitMqConstants.QUEUE_SEND_SYSTEM_CONFIGURATION_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(systemConfigurationMessage,systemConfigurationMessage.getClass().getSimpleName()));
        }
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
//    public List<ClientServicePojo> getClientSrvByName(String name) {
//        List<Integer> mvnoIds = new ArrayList<>();
//        mvnoIds.add(1);
//        mvnoIds.add(getMvnoIdFromCurrentStaff());
//        return entityRepository.findAll().stream().filter(data -> data.getName().
//                equalsIgnoreCase(name)).map(data -> clientServiceMapper.domainToDTO
//                (data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
//                .stream().filter(clientServicePojo -> clientServicePojo.getMvnoId() == 1|| clientServicePojo.getMvnoId().equals(mvnoIds.get(1)) || mvnoIds.get(0) == 1 ).collect(Collectors.toList());
//    }
   public List<ClientServicePojo> getClientSrvByName(String name) {
       Integer mvnoId = getMvnoIdFromCurrentStaff();
       List<Integer> mvnoIds = Arrays.asList(1, mvnoId);

       List<ClientService> clientServices = entityRepository.findByNameAndMvnoIds(name, mvnoIds);

       return clientServices.stream()
               .map(cs -> clientServiceMapper.domainToDTO(cs, new CycleAvoidingMappingContext()))
               .collect(Collectors.toList());
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

//    public String getValueByName(String name) {
//        return entityRepository.findValueByName(name);
//    }

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

    public boolean duplicateVarification(ClientServicePojo pojo, Integer operation) {
        boolean flag = false;
        String name = pojo.getName();
        name = name.trim();
        Integer count = null;
        if( pojo.getName() != null && name.equalsIgnoreCase("google_maps_key")){
            count = entityRepository.countByNameAndMvnoIdEquals(name, getMvnoIdFromCurrentStaff());
            if(count==0){
                count = entityRepository.countByNameAndMvnoIdEquals(name, 1);
            }
        }else {
            if (pojo.getName() != null) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = entityRepository.countByNameAndMvnoId(name,getMvnoIdFromCurrentStaff());
                } else {
                    count = entityRepository.countByNameAndMvnoId(name, getMvnoIdFromCurrentStaff());
                }
            }
        }
        if (operation == CommonConstants.OPERATION_ADD){
            if (count == 0) {
                flag = true;
            }
        }else if (operation == CommonConstants.OPERATION_UPDATE) {
            if (count >=1) {
                Integer countEdit = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    countEdit = entityRepository.countByIdAndNameAndMvnoId(pojo.getId(), name,getMvnoIdFromCurrentStaff());
                }
                else {
                    countEdit = entityRepository.countByIdAndNameAndMvnoId(pojo.getId(), name, getMvnoIdFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
}
