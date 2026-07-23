package com.savbill.commonGateway.moules.PaymentConfig.service;

import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.PaymentConfig.entity.PaymentConfig;
import com.savbill.commonGateway.moules.PaymentConfig.model.ChangeStatusDTO;
import com.savbill.commonGateway.moules.PaymentConfig.model.PaymentConfigDTO;
import com.savbill.commonGateway.moules.PaymentConfig.model.SendPaymentConfigDTO;
import com.savbill.commonGateway.moules.PaymentConfig.repository.PaymentConfigRepository;
import com.savbill.commonGateway.moules.PaymentConfigMapping.entity.PaymentConfigMapping;
import com.savbill.commonGateway.moules.PaymentConfigMapping.repository.PaymentConfigMappingRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.PaymentConfigMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PaymentConfigService {

    @Autowired
    private PaymentConfigRepository paymentConfigRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PaymentConfigMappingRepository paymentConfigMappingRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


  /**@Author Dhaval Khalasi
   * Here it validate save request with custom validation
   * If any new validatation for save comes add here
   * **/
   public void validateSaveRequest(PaymentConfigDTO paymentConfigDTO){
       if(paymentConfigDTO.getPaymentConfigName().isEmpty() || paymentConfigDTO.getPaymentConfigName() == null || paymentConfigDTO.getPaymentConfigName().equalsIgnoreCase("")){
           throw new CustomValidationException(APIConstants.FAIL,"Payment gateway configuration name can't be empty",null);
       }
       if(paymentConfigDTO.getPaymentConfigMappingList().isEmpty()){
           throw new CustomValidationException(APIConstants.FAIL,"Payment gateway parameter list can't be empty",null);
       }
       if(!paymentConfigDTO.getPaymentConfigMappingList().isEmpty()){
           for(PaymentConfigMapping paymentConfigMapping : paymentConfigDTO.getPaymentConfigMappingList()){
               if(paymentConfigMapping.getPaymentParameterName() == null || paymentConfigMapping.getPaymentParameterName().equalsIgnoreCase("")||paymentConfigMapping.getPaymentParameterValue() == null || paymentConfigMapping.getPaymentParameterValue().equalsIgnoreCase("")){
                   throw new CustomValidationException(APIConstants.FAIL,"Payment Gateway Parameter : "+ paymentConfigMapping.getPaymentParameterName() +" can't be empty.",null);
               }
           }
       }
       if(paymentConfigDTO.getPaymentConfigName() != null || !paymentConfigDTO.getPaymentConfigName().equalsIgnoreCase("")){
           Integer mvnoId = customersService.getMvnoIdFromCurrentStaff();
           List<PaymentConfig> paymentConfigList =  paymentConfigRepository.findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(paymentConfigDTO.getPaymentConfigName() , mvnoId.longValue());
           if(!paymentConfigList.isEmpty()){
               throw new CustomValidationException(APIConstants.FAIL , "Payment Gateway Configuration with name : "+paymentConfigDTO.getPaymentConfigName() + " already exist.",null);
           }
       }


   }

    /**@Author Dhaval Khalasi
     * Here it validate update request with custom validation
     * If any new validatation for update comes add here
     * **/
   public void validateUpdateRequest(PaymentConfigDTO paymentConfigDTO){
       if(paymentConfigDTO.getPaymentConfigName().isEmpty() || paymentConfigDTO.getPaymentConfigName() == null || paymentConfigDTO.getPaymentConfigName().equalsIgnoreCase("")){
           throw new CustomValidationException(APIConstants.FAIL,"Payment gateway configuration name can't be empty",null);
       }
       if(paymentConfigDTO.getPaymentConfigMappingList().isEmpty()){
           throw new CustomValidationException(APIConstants.FAIL,"Payment gateway parameter list can't be empty",null);
       }
       if(!paymentConfigDTO.getPaymentConfigMappingList().isEmpty()){
           for(PaymentConfigMapping paymentConfigMapping : paymentConfigDTO.getPaymentConfigMappingList()){
               if(paymentConfigMapping.getPaymentParameterValue() == null || paymentConfigMapping.getPaymentParameterValue().equalsIgnoreCase("")){
                   throw new CustomValidationException(APIConstants.FAIL,"Payment gateway Parameter : "+ paymentConfigMapping.getPaymentParameterName() +"can't be empty.",null);
               }
           }
       }
       if(paymentConfigDTO.getPaymentConfigId() == null){
           throw new CustomValidationException(APIConstants.FAIL,"Payment gateway config Id can't be null",null);
       }

   }
   /**@Author Dhaval Khalasi
    * This method will validate change status request
    * In Line no 108 it will be condtion about check if payment config is found.
    * If any other payment gateway is active because only one payment gateway
    * is active in system.
    * **/
   public void validateChangeStatusRequest(ChangeStatusDTO changeStatusDTO){
       if(changeStatusDTO.getPaymentConfigId() == null){
           throw new CustomValidationException(APIConstants.FAIL , "Payment config id can't be null",null);
       }

       if(changeStatusDTO.getIsActive() == null){
           throw new CustomValidationException(APIConstants.FAIL , "Payment config status can't be null",null);

       }
       else{
           /**here it check its condtion**/
           Optional<PaymentConfig> paymentConfig = paymentConfigRepository.findById(changeStatusDTO.getPaymentConfigId());
           if(!paymentConfig.isPresent()){
               throw new CustomValidationException(APIConstants.FAIL , "Payment config not found by given id",null);
           }
       }
   }
   /**@Author Dhaval Khalasi
    * This method save payment config
    * It's Also save Payment config mapping here
    * **/

   @Transactional
   public PaymentConfigDTO savePaymentConfig(PaymentConfigDTO paymentConfigDTO){
       PaymentConfig paymentConfig = DtoToDomain(paymentConfigDTO);      /**convert  dto to domain here**/
       PaymentConfig savedPaymentConfig = paymentConfigRepository.save(paymentConfig);
       List<PaymentConfigMapping> paymentConfigMappingList = paymentConfig.getPaymentConfigMappingList();
       /**here it set payment config id in payment config mapping table**/
       paymentConfigMappingList = paymentConfigMappingList.stream().peek(paymentConfigMapping -> paymentConfigMapping.setPaymentConfigId(savedPaymentConfig.getPaymentConfigId())).collect(Collectors.toList());
       List<PaymentConfigMapping> savedPaymentConfigMappings = paymentConfigMappingRepository.saveAll(paymentConfigMappingList);
       savedPaymentConfig.setPaymentConfigMappingList(savedPaymentConfigMappings); /**here set latest payment config mapping after save**/
       PaymentConfigDTO returnPaymentConfigDto = DomainToDto(savedPaymentConfig);
       sendPaymentConfigToCMS(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.CREATE);
       sendPaymentConfigToIntegration(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.CREATE);
       return returnPaymentConfigDto;
   }

    /**@Author Dhaval Khalasi
     * This method update payment config
     * It's Also update Payment config mapping here
     * **/
   @Transactional
   public PaymentConfigDTO updatePaymentConfig(PaymentConfigDTO paymentConfigDTO){
       List<PaymentConfigMapping> oldpaymentConfigMappingList = paymentConfigMappingRepository.findAllByPaymentConfigId(paymentConfigDTO.getPaymentConfigId());
       paymentConfigMappingRepository.deleteInBatch(oldpaymentConfigMappingList); /**delete old payment config mapping list**/
       PaymentConfig paymentConfig = paymentConfigRepository.findById(paymentConfigDTO.getPaymentConfigId()).get();
       paymentConfig.setPaymentConfigName(paymentConfigDTO.getPaymentConfigName());
       List<PaymentConfigMapping> paymentConfigMappingList = paymentConfigDTO.getPaymentConfigMappingList();
       /**set payment config id in payment mapping list also set null in mapping id because of primary key **/
       paymentConfigMappingList = paymentConfigMappingList.stream().peek(paymentConfigMapping -> {
           paymentConfigMapping.setPaymentConfigId(paymentConfigDTO.getPaymentConfigId());
           paymentConfigMapping.setPaymentConfigMappingId(null);
           paymentConfigMapping.setPaymentParameterFor(getParameterFor(paymentConfigMapping.getPaymentParameterName()));
       }).collect(Collectors.toList());
       List<PaymentConfigMapping> savedPaymentConfigMappingList = paymentConfigMappingRepository.saveAll(paymentConfigMappingList);
       paymentConfig.setPaymentConfigMappingList(savedPaymentConfigMappingList);
       if(paymentConfigDTO.getPaymentGatewayInfo() != null && !paymentConfigDTO.getPaymentGatewayInfo().equalsIgnoreCase(" ")){
           paymentConfig.setPaymentGatewayInfo(paymentConfigDTO.getPaymentGatewayInfo());
       }
       paymentConfigRepository.save(paymentConfig);
       PaymentConfigDTO returnPaymentConfigDto = DomainToDto(paymentConfig);
       sendPaymentConfigToCMS(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.UPDATE);
       sendPaymentConfigToIntegration(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.UPDATE);
       return returnPaymentConfigDto;

   }
    /**@Author Dhaval Khalasi
     * This method hard delete payment config
     * **/
   @Transactional
   public void deletePaymentConfig(Long paymentConfigId){
       PaymentConfig paymentConfig = paymentConfigRepository.findById(paymentConfigId).get();
       List<PaymentConfigMapping> paymentConfigMappingList = paymentConfig.getPaymentConfigMappingList();
       PaymentConfigDTO returnPaymentConfigDto =DomainToDto(paymentConfig);
       paymentConfigRepository.delete(paymentConfig);
       paymentConfigMappingRepository.deleteInBatch(paymentConfigMappingList);
       sendPaymentConfigToCMS(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.DELETE);
       sendPaymentConfigToIntegration(returnPaymentConfigDto , CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.UPDATE);

   }
   /**@Author Dhaval Khalasi
    * this method will get payment config by id else return null
    * **/
   @Transactional
   public PaymentConfigDTO findPaymentConfigById(Long paymentConfigId){
       Optional<PaymentConfig> paymentConfig = paymentConfigRepository.findById(paymentConfigId);
       if(paymentConfig.isPresent()){
           return DomainToDto(paymentConfig.get());
       }
       else{
           return null;
       }
   }

    /**@Author Dhaval Khalasi
     * this method will get all payment config list by mvnoId
     * **/
    @Transactional
    public GenericDataDTO findAllPaymentConfig(PaginationRequestDTO requestDTO){
        Integer mvnoId = customersService.getMvnoIdFromCurrentStaff();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "paymentConfigId"));
        Page<PaymentConfig> paymentConfigList = paymentConfigRepository.findAllByMvnoIdAndIsDeleteIsFalse(mvnoId.longValue(),pageable);
        GenericDataDTO genericDataDTO = convertPagableResponseToGenericDataDTO(paymentConfigList,requestDTO);
        return genericDataDTO;
    }
    /**@Author Dhaval Khalasi
     * this method  will return parameter list by given payment gateway name
     * note that defualt parameter make by changelog and that mvno is null.
     * **/
    @Transactional
    public PaymentConfigDTO findAllPaymentGatewayParameterByName(String name){
        List<PaymentConfig> paymentConfig = paymentConfigRepository.findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(name , null);
        if(paymentConfig.isEmpty()){
            throw  new CustomValidationException(APIConstants.EXPECTATION_FAILED,"Defualt Payment Gateway Configuration is not found",null);
        }
        PaymentConfigDTO paymentConfigDTO = DomainToDto(paymentConfig.get(0));
        return paymentConfigDTO;
    }
    /**@Author Dhaval Khalasi
     * It will change status of payment config
     * **/
    @Transactional
    public PaymentConfigDTO changeStatus(ChangeStatusDTO changeStatusDTO){
        PaymentConfig paymentConfig = paymentConfigRepository.findById(changeStatusDTO.getPaymentConfigId()).get();
        paymentConfig.setIsActive(changeStatusDTO.getIsActive());
        PaymentConfig savedPaymentConfig = paymentConfigRepository.save(paymentConfig);
        return DomainToDto(savedPaymentConfig);
    }
    /**@Author Dhaval Khalasi
     * This will return active payment config
     * **/
    public List<PaymentConfigDTO> getActivePaymentConfig(String paymentGatewayFor, Long mvnoId){
//        Integer mvnoId = customersService.getMvnoIdFromCurrentStaff();
        List<PaymentConfig> getActivePaymentConfigs = paymentConfigRepository.findAllByMvnoIdAndIsActiveIsTrueAndIsDeleteIsFalse(mvnoId);
        List<PaymentConfigDTO> paymentConfigDTOList =  new ArrayList<>();
        String paymentGateways = clientServiceSrv.getByNameAndMvnoId(paymentGatewayFor, mvnoId.intValue()).getValue();
        String[] items = Arrays.stream(paymentGateways.split(","))
                .map(String::trim)
                .toArray(String[]::new);
        List<String> paymentGatewayNameList = Arrays.asList(items);
        if(!paymentGatewayNameList.isEmpty()) {
            if (!getActivePaymentConfigs.isEmpty()) {
                for (PaymentConfig paymentConfig : getActivePaymentConfigs) {
                    for(String paymentGateway : paymentGatewayNameList) {
                        if(paymentConfig.getPaymentConfigName().equalsIgnoreCase(paymentGateway)) {
                            PaymentConfigDTO paymentConfigDTO = new PaymentConfigDTO();
                            paymentConfigDTO = DomainToDto(paymentConfig);
                            paymentConfigDTOList.add(paymentConfigDTO);
                        }
                    }
                }
            }
        }
        return paymentConfigDTOList;
    }

   /**@Author Dhaval Khalasi
    * this method  convert dto to entity
    * **/
   @Transactional
    public  PaymentConfig DtoToDomain(PaymentConfigDTO paymentConfigDTO){
       PaymentConfig paymentConfig = new PaymentConfig();
       if(paymentConfigDTO.getPaymentConfigId() != null){
           paymentConfig.setPaymentConfigId(paymentConfigDTO.getPaymentConfigId());
       }
       paymentConfig.setPaymentConfigName(paymentConfigDTO.getPaymentConfigName());
       /**Here it set payment gateway parameter for save in new entry**/
       List<PaymentConfigMapping> paymentConfigMappingList  = paymentConfigDTO.getPaymentConfigMappingList();
       paymentConfigMappingList = paymentConfigMappingList.stream().peek(paymentConfigMapping -> paymentConfigMapping.setPaymentParameterFor(getParameterFor(paymentConfigMapping.getPaymentParameterName()))).collect(Collectors.toList());
       paymentConfig.setPaymentConfigMappingList(paymentConfigMappingList);
       Integer mvnoId = customersService.getMvnoIdFromCurrentStaff();
       paymentConfig.setMvnoId(Long.valueOf(mvnoId));
       paymentConfig.setCreateDate(LocalDateTime.now());
       if(paymentConfigDTO.getIsDelete() == null){
           paymentConfig.setIsDelete(false);
       }
       else{
           paymentConfig.setIsDelete(paymentConfigDTO.getIsDelete());
       }
       if(paymentConfigDTO.getIsActive() != null){
           paymentConfig.setIsActive(paymentConfigDTO.getIsActive());
       }
       else{
           paymentConfig.setIsActive(true);
       }
       if(paymentConfigDTO.getPaymentGatewayInfo() != null && !paymentConfigDTO.getPaymentGatewayInfo().equalsIgnoreCase("")){
           paymentConfig.setPaymentGatewayInfo(paymentConfigDTO.getPaymentGatewayInfo());
       }
       return  paymentConfig;

   }

    /**@Author Dhaval Khalasi
     * this method  convert entity to dto
     * **/
   @Transactional
   public PaymentConfigDTO DomainToDto(PaymentConfig paymentConfig){
       PaymentConfigDTO paymentConfigDTO = new PaymentConfigDTO();
       if(paymentConfig.getPaymentConfigId() != null){
           paymentConfigDTO.setPaymentConfigId(paymentConfig.getPaymentConfigId());
       }
       paymentConfigDTO.setPaymentConfigName(paymentConfig.getPaymentConfigName());
       List<PaymentConfigMapping> paymentConfigMappingList = paymentConfig.getPaymentConfigMappingList();
       /**need to convert constant to title case and set that list to dto**/
       paymentConfigMappingList = paymentConfigMappingList.stream().peek(
               paymentConfigMapping -> paymentConfigMapping.setParameterDisplayName(convertToTitleCase(paymentConfigMapping.getPaymentParameterName()))
       ).collect(Collectors.toList());
       paymentConfigDTO.setPaymentConfigMappingList(paymentConfigMappingList);
       paymentConfigDTO.setCreateDate(paymentConfig.getCreateDate());
       paymentConfigDTO.setMvnoId(paymentConfig.getMvnoId());
       paymentConfigDTO.setIsDelete(paymentConfig.getIsDelete());
       paymentConfigDTO.setIsActive(paymentConfig.getIsActive());
       paymentConfigDTO.setPaymentGatewayInfo(paymentConfig.getPaymentGatewayInfo());
       return paymentConfigDTO;
   }
   /**@Author Dhaval Khalasi
    * This is rabbitmq message send to CMS microservice
    * It will handle 3 operation in same rabbitmq
    * CREATE ,UPDATE , DELETE
    * **/
   @Transactional
    public void sendPaymentConfigToCMS(PaymentConfigDTO paymentConfigDTO , String flag){
       PaymentConfigMessage paymentConfigMessage = new PaymentConfigMessage();
       SendPaymentConfigDTO sendPaymentConfigDTO = convertDTOToSendPaymentConfigDTO(paymentConfigDTO); /**convert for rabbitmq**/
       paymentConfigMessage.setPaymentConfigDTO(sendPaymentConfigDTO);
       paymentConfigMessage.setFlag(flag);
       Gson gson = new Gson();
       gson.toJson(paymentConfigMessage);
       //messageSender.send(paymentConfigMessage , RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS);
       kafkaMessageSender.send(new KafkaMessageData(paymentConfigMessage,paymentConfigMessage.getClass().getSimpleName()));

   }


    /**@Author Dhaval Khalasi
     * This is rabbitmq message send to CMS microservice
     * It will handle 3 operation in same rabbitmq
     * CREATE ,UPDATE , DELETE
     * **/
    @Transactional
    public void sendPaymentConfigToIntegration(PaymentConfigDTO paymentConfigDTO , String flag){
        PaymentConfigMessage paymentConfigMessage = new PaymentConfigMessage();
        SendPaymentConfigDTO sendPaymentConfigDTO = convertDTOToSendPaymentConfigDTO(paymentConfigDTO); /**convert for rabbitmq**/
        paymentConfigMessage.setPaymentConfigDTO(sendPaymentConfigDTO);
        paymentConfigMessage.setFlag(flag);
        Gson gson = new Gson();
        gson.toJson(paymentConfigMessage);
        //messageSender.send(paymentConfigMessage , RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_INTEGRATION); pending via kafka
        kafkaMessageSender.send(new KafkaMessageData(paymentConfigMessage,paymentConfigMessage.getClass().getSimpleName()));

    }

   /**@Author Dhaval Khalasi
    * This will convert constant to title case
    * exp. CWSC_REDIRECT_URL to Cwsc Redirect Url
    * **/
    public  String convertToTitleCase(String input) {
        String[] words = input.toLowerCase().split("_");
        StringBuilder titleCase = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (titleCase.length() > 0) {
                    titleCase.append(" ");
                }
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return titleCase.toString();
    }
    /**@Author Dhaval Khalasi
     * this will convert payment config to sendable object for rabbitmq
     * because rabbitmq doesnt handle localdatetime
     * **/
    public SendPaymentConfigDTO convertDTOToSendPaymentConfigDTO(PaymentConfigDTO paymentConfigDTO){
        SendPaymentConfigDTO sendPaymentConfigDTO =  new SendPaymentConfigDTO();
        sendPaymentConfigDTO.setPaymentConfigId(paymentConfigDTO.getPaymentConfigId());
        sendPaymentConfigDTO.setPaymentConfigName(paymentConfigDTO.getPaymentConfigName());
        sendPaymentConfigDTO.setPaymentConfigMappingList(paymentConfigDTO.getPaymentConfigMappingList());
        sendPaymentConfigDTO.setMvnoId(paymentConfigDTO.getMvnoId());
        sendPaymentConfigDTO.setIsActive(paymentConfigDTO.getIsActive());
        sendPaymentConfigDTO.setIsDelete(paymentConfigDTO.getIsDelete());
        return sendPaymentConfigDTO;
    }

    /**@Author Dhaval Khalasi
     * This will convert pageable record to generic data dto
     * it is generic method it will take any object as paginationList
     * **/
    public <T> GenericDataDTO  convertPagableResponseToGenericDataDTO(Page<? super T> paginationList , PaginationRequestDTO requestDTO){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    /**
     * get current login staff for logs
     * **/
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
    /**@Author
     * Dhaval Khalasi
     * Here it is method that convert parameter for
     * it will use to fetch from first entry and used to save in new entry
     * it will help to seprate in gui for system or gateway parameter
     * **/
    public String getParameterFor(String parameterName){
        String paremeterFor ="";
        List<PaymentConfigMapping> paymentConfigMappingList = paymentConfigMappingRepository.findAllByPaymentParameterName(parameterName);
        if(!paymentConfigMappingList.isEmpty()){
            PaymentConfigMapping paymentConfigMapping = paymentConfigMappingList.get(0);
            paremeterFor = paymentConfigMapping.getPaymentParameterFor();
        }
        return paremeterFor;
    }




}
