package com.savbill.notification.services.impl;

import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.QEmailConfig;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.kafka.KafkaMessageData;
import com.savbill.notification.kafka.KafkaMessageSender;
//import com.savbill.notification.rabbitmq.MessageSender;
import com.savbill.notification.rabbitmq.message.EmailConfigSendToAPIGWMsg;
import com.savbill.notification.repository.EmailConfigRepository;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.services.EmailConfigService;
import com.savbill.notification.snmp.SNMPCounters;
import com.savbill.notification.utils.CommonConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.PasswordGenerator;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class EmailConfigServiceImpl extends PasswordGenerator implements EmailConfigService {
    private final Logger log = Logger.getLogger(EmailConfigServiceImpl.class);
    private final SNMPCounters snmpCounters = new SNMPCounters();
    @Autowired
    EmailConfigRepository emailConfigRepository;
    @Autowired
    EventRepository eventRepository;
//    @Autowired
//    MessageSender messageSender;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * Method: Add/ Create/ Save Email Configuration
     *
     * @param emailConfigDto
     * @param mvnoId
     * @param buId
     * @return
     */
    @Override
    public EmailConfig addEmailConfig(EmailConfigDto emailConfigDto, Long mvnoId, Long buId) {
        try {
            if (buId == 0L) {
                buId = null;
            }
            /** To validate Event Name is Exist or Not*/
            List<EmailConfig> emailConfigList = new ArrayList<>();
            /** To validate Event Name is Exist or Not*/
            if (buId != null) {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdEquals(mvnoId, buId);
            } else {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdIsNull(mvnoId);
            }
            if (emailConfigList.stream().anyMatch(emailConfig ->
                    emailConfig.getUserName().trim().equals(emailConfigDto.getUserName().trim())
                            && emailConfig.getMvnoId().equals(mvnoId))) {
                throw new RuntimeException("Username is already exist!");
            }
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getCreatedBy()))
                log.info("Created By is Missing!");
            EmailConfig emailConfigVo = new EmailConfig(emailConfigDto, mvnoId);
            emailConfigVo.setCreateDate(LocalDateTime.now());
            emailConfigVo.setCreatedBy(emailConfigDto.getCreatedBy());
            emailConfigVo.setServiceType(emailConfigDto.getServiceType());
            if (Objects.nonNull(buId)) {
                emailConfigVo.setBuId(buId);
            }
            emailConfigVo = emailConfigRepository.save(emailConfigVo);
            if (!emailConfigDto.getServiceType().equalsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF)) {
                sendMessageandSNMPCounter(emailConfigVo, CommonConstants.OPERATION.OPERATION_ADD);
            }
            return emailConfigVo;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Update Email Configuration
     *
     * @param emailConfigDto
     * @param mvnoId
     * @return
     */
    @Override
    public EmailConfig updateEmailConfig(UpdateEmailConfigDto emailConfigDto, Long mvnoId, Long buId) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        try {
            List<EmailConfig> emailConfigList = new ArrayList<>();
            /** To validate Event Name is Exist or Not*/
            if (buId != null || buId!=0) {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdEquals(mvnoId, buId);
            } else {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndIsActiveIsTrueAndMvnoIdEqualsAndBuIdIsNull(mvnoId);
            }
            if (emailConfigList.stream().anyMatch(emailConfig ->
                    emailConfig.getUserName().trim().equals(emailConfigDto.getUserName().trim())
                            && emailConfig.getMvnoId().equals(mvnoId)
                            && !emailConfig.getEmailConfigId().equals(emailConfigDto.getEmailConfigId()))) {
                throw new RuntimeException("Username is already exist!");
            }
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getLastModifiedBy()))
                log.error("LastModifiedBy value is Missing");
            EmailConfig optionalEmailConfig = emailConfigRepository.findByEmailConfigIdAndMvnoId(emailConfigDto.getEmailConfigId(), mvnoId).orElse(null);
            if (Objects.isNull(optionalEmailConfig)) {
                throw new IllegalArgumentException("No record found to update email configurations with user name : '" + emailConfigDto.getUserName() + "'");
            } else {
                EmailConfig emailConfigVo = new EmailConfig(emailConfigDto, mvnoId);
                emailConfigVo.setLastModifiedDate(LocalDateTime.now());
                emailConfigVo.setLastModifiedBy(emailConfigDto.getLastModifiedBy());
                emailConfigVo.setCreateDate(optionalEmailConfig.getCreateDate());
                emailConfigVo.setServiceType(optionalEmailConfig.getServiceType());
                if (mvnoId == 1) {
                    emailConfigVo.setMvnoId(optionalEmailConfig.getMvnoId());
                }
                emailConfigVo.setEmailConfigId(optionalEmailConfig.getEmailConfigId());
                if (!emailConfigDto.getServiceType().equalsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF)) {
                    sendMessageandSNMPCounter(emailConfigVo, CommonConstants.OPERATION.OPERATION_UPDATE);
                }
                return emailConfigRepository.save(emailConfigVo);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Get All Email Configuration Without Pagination
     *
     * @param mvnoId
     * @param buId
     * @return
     */
    @Override
    public List<EmailConfig> findAllEmailConfig(Long mvnoId, Long buId, String serviceType) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                QEmailConfig qEmailConfig = QEmailConfig.emailConfig;
                BooleanExpression boolExp = qEmailConfig.isNotNull().and(qEmailConfig.isDelete.eq(false));
                if (mvnoId != 1) {
                    if (buId == 0) {
                        buId = null;
                    }
                    boolExp = boolExp.and(qEmailConfig.mvnoId.eq(mvnoId).or(qEmailConfig.mvnoId.eq(1L)));
                    if (Objects.nonNull(buId)) {
                        boolExp = boolExp.and(qEmailConfig.buId.eq(buId));
                    }
                    if (Objects.isNull(buId)) {
                        boolExp = boolExp.and(qEmailConfig.buId.isNull());
                    }
                }
                boolExp = boolExp.and(qEmailConfig.serviceType.containsIgnoreCase(serviceType));
                return (List<EmailConfig>) emailConfigRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "emailConfigId"));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Change Password
     *
     * @param passwordDto
     */
    @Override
    public void changePassword(PasswordDto passwordDto) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getLastModifiedBy()))
                log.error("LastModifiedBy value is Missing");
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getUserName())) {
                throw new IllegalArgumentException(NotificationConstants.BASIC_NUMERIC_MSG + "Please enter valid user name");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getNewPassword())) {
                throw new IllegalArgumentException(NotificationConstants.BASIC_STRING_MSG + "Please enter valid password");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException(
                        NotificationConstants.BASIC_STRING_MSG + "Please enter valid confirm password");
            } else if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException(
                        "Please enter valid password. New password and confirm password value must be same.");
            } else if (passwordDto.getUserName() != null) {
                Optional<EmailConfig> emailConfigOptional = emailConfigRepository.findByUserName(passwordDto.getUserName());
                if (!emailConfigOptional.isPresent()) {
                    throw new IllegalArgumentException("No record found with user : '" + passwordDto.getUserName()
                            + "'. Please enter valid user name.");
                } else {
                    emailConfigOptional.get().setPassword(encryptPassword(passwordDto.getNewPassword()));
                    emailConfigOptional.get().setLastModifiedDate(LocalDateTime.now());
                    emailConfigOptional.get().setLastModifiedBy(passwordDto.getLastModifiedBy());
                    emailConfigRepository.save(emailConfigOptional.get());
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Find Email Configuration By Id
     *
     * @param emailConfigId
     * @param mvnoId
     * @return
     */
    @Override
    public EmailConfig findEmailConfigById(Long emailConfigId, Long mvnoId) {
        try {
            QEmailConfig qEmailConfig = QEmailConfig.emailConfig;
            BooleanExpression boolExp = qEmailConfig.isNotNull();
            boolExp = boolExp.and(qEmailConfig.emailConfigId.eq(emailConfigId));
            if (mvnoId != 1)
                boolExp = boolExp.and(qEmailConfig.mvnoId.in(mvnoId, 1));
            Optional<EmailConfig> emailConfig = emailConfigRepository.findOne(boolExp);
            if (emailConfig.isPresent()) {
                return emailConfig.get();
            } else {
                throw new IllegalArgumentException(
                        "No record found with id " + emailConfigId + " . or you might not have access to update/delete this record.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * method: Validate Email Configuration Data
     *
     * @param emailConfigDto
     * @param mvnoId
     */
    public void validateEmailConfigData(EmailConfigDto emailConfigDto, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getAuthType())) {
                throw new IllegalArgumentException("Auth type is mandatory. Please enter valid auth type");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getHostServer())) {
                throw new IllegalArgumentException("Host server is mandatory. Please enter valid host server");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getPassword())) {
                throw new IllegalArgumentException("Password is mandatory. Please enter valid password");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getPort())) {
                throw new IllegalArgumentException("Port is mandatory. Please enter valid port number");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getUserName())) {
                throw new IllegalArgumentException("User name is mandatory. Please enter valid user name");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Validate Email Configuration Data At Update
     *
     * @param emailConfigDto
     * @param mvnoId
     */
    public void validateEmailConfigDataOnUpdate(UpdateEmailConfigDto emailConfigDto, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(emailConfigDto.getEmailConfigId())) {
                throw new IllegalArgumentException("Email config id is required to update the record.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getAuthType())) {
                throw new IllegalArgumentException("Auth type is mandatory. Please enter valid auth type");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getHostServer())) {
                throw new IllegalArgumentException("Host server is mandatory. Please enter valid host server");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getPassword())) {
                throw new IllegalArgumentException("Password is mandatory. Please enter valid password");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getPort())) {
                throw new IllegalArgumentException("Port is mandatory. Please enter valid port number");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailConfigDto.getUserName())) {
                throw new IllegalArgumentException("User name is mandatory. Please enter valid user name");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Remove/ Delete Email Configuration by Email Configuration Id
     *
     * @param id
     */
    @Override
    public void removeEmailConfigById(Long id) {
        List<Event> eventList = eventRepository.findAllByEmailConfigId(id);
        if (!eventList.isEmpty()) {
            throw new RuntimeException("Email Configuration is Already in Use..");
        }
        Optional<EmailConfig> emailConfigurationOptional = emailConfigRepository.findById(id);
        if (emailConfigurationOptional.isPresent()) {
            EmailConfig configuration = emailConfigurationOptional.get();
            configuration.setIsActive(false);
            configuration.setIsDelete(true);
            emailConfigRepository.save(configuration);
        }
    }

    /**
     * Method: Filter Email Configartion By Name
     *
     * @param criteriaMap
     * @return
     */
    @Override
    public Page<EmailConfigDto> filterEmailConfigByName(Map<String, Object> criteriaMap) {
        List<EmailConfigDto> emailConfigDtos = new ArrayList<>();
        if (criteriaMap != null) {
            if (criteriaMap.get("username") != null && !String.valueOf(criteriaMap.get("username")).equalsIgnoreCase("")) {
                List<EmailConfig> emailConfigurationList = emailConfigRepository.findByUserNameLike("%" + String.valueOf(criteriaMap.get("username")) + "%");
                emailConfigDtos = emailConfigurationList.stream()
                        .filter(emailConfig -> emailConfig.getIsDelete().equals(false) && emailConfig.getServiceType().equalsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF))
                        .map(emailConfiguration -> {
                            EmailConfigDto emailConfigDto = modelMapper.map(emailConfiguration, EmailConfigDto.class);
                            return emailConfigDto;
                        }).collect(Collectors.toList());
            }
        }
        return emailConfigDtos != null && emailConfigDtos.size() > 0 ? new PageImpl<>(emailConfigDtos) : new PageImpl<>(new ArrayList<>());
    }

    /**
     * Method: Get Email Configuration  With Pagination
     *
     * @param page
     * @param size
     * @param mvnoId
     * @param buId
     * @param serviceType
     * @return
     */
    @Override
    public Page<EmailConfigDto> getEmailConfigWithPagination(Integer page, Integer size, Long mvnoId, Long buId, String serviceType) {
        page = page + 1;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("emailConfigId")));
        Page<EmailConfig> emailConfigList;
        if (mvnoId == 1) {
            emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCase(serviceType, pageable);
        } else {
            if (buId == null || buId == 0) {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCaseAndMvnoIdIn(serviceType, Arrays.asList(mvnoId, 1L), pageable);
            } else {
                emailConfigList = emailConfigRepository.findAllByIsDeleteIsFalseAndServiceTypeContainingIgnoreCaseAndMvnoIdInAndBuIdIn(serviceType, Arrays.asList(mvnoId, 1L), Arrays.asList(buId), pageable);
            }
        }
//		if (emailConfigList.isEmpty()) {
//			throw new RuntimeException("No Record Found");
//		}
        List<EmailConfigDto> emailConfigDtos = new ArrayList<>();
        if (!emailConfigList.isEmpty()) {
            emailConfigDtos = emailConfigList.getContent().stream()
                    .map(this::createEmailConfigurationDTO)
                    .collect(Collectors.toList());
        }
        return new PageImpl<>(emailConfigDtos, pageable, emailConfigList.getTotalElements());
    }

    @Override
    public Page<EmailConfigDto> searchEmailConfig(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType) {
        Integer page = requestDTO.getPage();
        Integer size = requestDTO.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("emailConfigId")));
        Specification<EmailConfig> spec = Specification.where(null);
        Page<EmailConfig> configs = null;
        if (mvnoId != 1) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.EmailConfigSearch.MVNO_ID), mvnoId));
        }
        if (!serviceType.trim().isEmpty()) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.EmailConfigSearch.SERVICE_TYPE), serviceType));
        }
        if (null != requestDTO.getFilters() && requestDTO.getFilters().size() > 0) {
            for (GenericSearchModel searchModel : requestDTO.getFilters()) {
                spec = getEmailConfigByfilter(searchModel.getFilterValue().trim(), searchModel.getFilterCondition().trim(), searchModel.getFilterColumn().trim(), spec, pageable, mvnoId, serviceType.trim());
            }
        }
        // Adding condition to filter out deleted data
        spec = spec.and((root, query, builder) -> builder.equal(root.get("isDelete"), false));
        if (configs == null || (configs.isEmpty())) {
            configs = emailConfigRepository.findAll(spec, pageable);
        }
//		if (configs == null || (configs.isEmpty())) {
//			throw new RuntimeException("No Record Found!");
//		}
        Page<EmailConfigDto> map = configs.map(this::createEmailConfigurationDTO);
        return map;
    }

    public Specification<EmailConfig> getEmailConfigByfilter(String value, String filterCondition, String filterColumn, Specification<EmailConfig> spec, Pageable pageable, Long mvnoId, String serviceType) {
//		Specification specification = null;
        if ((filterColumn != null) && (!filterColumn.trim().isEmpty())) {
            if (!filterColumn.equalsIgnoreCase("ANY")) {
                if (filterCondition.equalsIgnoreCase("OR")) {
                    if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.EmailConfigSearch.DATE)) {
                        spec = dateFilter(value, filterCondition, spec, filterColumn);
                    } else {
                        spec = spec.or((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                    }
                } else {
                    if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.EmailConfigSearch.DATE)) {
                        spec = dateFilter(value, filterCondition, spec, filterColumn);

                    } else {
                        spec = spec.and((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                    }
                }
            } else {
                //if Any Execute this
                spec = spec.or((root, query, builder) -> builder.like(root.get(NotificationConstants.EmailConfigSearch.USER_NAME), value.trim()));
                spec = spec.or((root, query, builder) -> builder.like(root.get("createdBy"), "%" + value.trim() + "%"));
                spec = spec.or((root, query, builder) -> builder.like(root.get("hostServer"), "%" + value.trim() + "%"));
            }
        }
        return spec;
    }

    public Specification<EmailConfig> dateFilter(String value, String filterCondition, Specification<EmailConfig> spec, String filterColumn) {
//		Specification specification = null;
        JSONObject filterValue = new JSONObject(value);
        String fromDate = filterValue.getString("from") + "T00:00:00";
        String toDate = filterValue.getString("to") + "T23:59:59";
        LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
        LocalDateTime toDateTime = LocalDateTime.parse(toDate);
        if (filterCondition.equalsIgnoreCase("OR")) {
            spec = spec.or((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        } else {
            spec = spec.and((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        }
        return spec;
    }

    @Override
    public boolean validation(PaginationRequestDTO requestDTO) {
        for (GenericSearchModel searchModel : requestDTO.getFilters()) {
            if (searchModel.getFilterValue() == null || searchModel.getFilterValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param emailConfigDetails
     * @return
     */
    @Override
    public boolean isSmtpAuthenticated(boolean isSmtpAuthenticated, String authenticationType,
                                       String hostServer, String port, String userName, String password) {
        Properties props = initializeProperties(isSmtpAuthenticated, authenticationType, hostServer, port);
        // Set timeout values to prevent long hanging connections
        props.put("mail.smtp.connectiontimeout", "10000"); // Connection timeout in ms (10 seconds)
        props.put("mail.smtp.timeout", "10000"); // Socket read timeout
        Session session = createMailSession(props, userName, password);
        return authenticateSmtp(session, userName);
    }

    @Override
    public void validateSMTPAuthentication(boolean smtpAuth, String authType, String hostServer, String port, String userName, String password) {
        /** Call To Validate SMTP Configuration Data Method */
        if (!isSmtpAuthenticated(smtpAuth, authType, hostServer, port, userName, password)) {
            throw new RuntimeException("SMTP authentication failed with configuration username: " + userName);
        }
    }

    private Properties initializeProperties(boolean isSmtpAuthenticated, String authenticationType,
                                            String hostServer, String port) {
        Properties props = new Properties();
        props.put(NotificationConstants.AUTH_PARAM, isSmtpAuthenticated);
        props.put(NotificationConstants.HOST_PARAM, hostServer);
        props.put(NotificationConstants.PORT_PARAM, port);
        // Set authentication type
        if (authenticationType.equalsIgnoreCase(NotificationConstants.START_TLS)) {
            props.put(NotificationConstants.STARTTLS_PARAM, true);
        } else {
            props.put(NotificationConstants.SSL_PARAM, true);
        }
        // Add additional optimizing properties
        props.put("mail.smtp.connectionpool.enable", "true");
        props.put("mail.smtp.connectionpool.size", "5");
        props.put("mail.smtp.connectionpooltimeout", "300000"); // 5 minutes
        return props;
    }

    private Session createMailSession(Properties props, String userName, String password) {
        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
    }

    private boolean authenticateSmtp(Session session, String userName) {
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect();
            return true;
        } catch (MessagingException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Method: Create Email Configuration DTO
     *
     * @param emailConfig
     * @return
     */
    private EmailConfigDto createEmailConfigurationDTO(EmailConfig emailConfig) {
        return new EmailConfigDto(
                emailConfig.getEmailConfigId(),
                emailConfig.getUserName(),
                emailConfig.getPassword(),
                emailConfig.isSmtpAuth(),
                emailConfig.getAuthType(),
                emailConfig.getHostServer(),
                emailConfig.getPort(),
                emailConfig.getCreatedBy(),
                emailConfig.getIsActive(),
                emailConfig.getIsDelete(),
                emailConfig.getServiceType()
        );
    }

    /**
     * Method: Send Message and SNMP Counter
     *
     * @param emailConfigVo
     * @param operation
     */
    public void sendMessageandSNMPCounter(EmailConfig emailConfigVo, Integer operation) {
        List<Long> buid = emailConfigRepository.getBuIdFromConfigId(emailConfigVo.getEmailConfigId());
        if (buid != null && !buid.isEmpty()) {
            emailConfigVo.setBuId(buid.get(0));
        }
        EmailConfigSendToAPIGWMsg emailConfigSendToAPIGWMsg = new EmailConfigSendToAPIGWMsg(emailConfigVo);
//        messageSender.send(emailConfigSendToAPIGWMsg, RabbitMqConstants.QUEUE_SEND_EMAIL_CONFIG_TO_APIGW);
        kafkaMessageSender.send(new KafkaMessageData(emailConfigSendToAPIGWMsg,emailConfigSendToAPIGWMsg.getClass().getSimpleName()));
        if (operation.equals(CommonConstants.OPERATION.OPERATION_ADD)) {
            snmpCounters.incrementCreateEmailConfigSuccess();
        } else if (operation.equals(CommonConstants.OPERATION.OPERATION_UPDATE)) {
            snmpCounters.incrementUpdateEmailConfigSuccess();
        }
    }
}
