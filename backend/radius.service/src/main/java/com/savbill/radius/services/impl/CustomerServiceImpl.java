package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.constant.RadiusAttributes;
import com.savbill.radius.aaa.data.CustomerCreateData;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerDetails;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.server.RadiusAsyncUtility;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.dto.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.dto.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.kafka.*;
import com.savbill.radius.kafka.*;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.kafka.CustomerMessage;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.mvno.Entity.Mvno;
import com.savbill.radius.mvno.Repository.MvnoRepository;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.*;
import com.savbill.radius.spring.SpringContext;
import com.savbill.radius.utils.*;
import com.google.gson.Gson;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.radius.utils.*;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends PasswordGenerator implements CustomerService {
    private static final String REGISTRATION_SUCCESS = "Registration Success";
    private static final String REGISTRATION_FAILURE = "Registration Failure";
    private static final String USER_NAME = "userName";
    private static final String CUSTOMER_STATUS = "customerStatus";
    private static final String INVALID_CUSTOMER_DATA = "Invalid customer data";
    private static final String MVNO_ID = "mvnoId";
    private static final String LOGIN_SUCCESS = "Login Success";
    private static final String LOGIN_FAILURE = "Login Failure";
    private static final String PLAN_TYPE = "planType";
    private static final String CUST_ID = "custId";

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomerReplyService customerReplyService;
    //    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    CustomerReplyRepository custReplyRepo;
    @Autowired
    CustomerQosPolicyMappingService qosPolicyMappingService;

    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;

    @Autowired
    CustomerTimBasePolicyMappingRepository customerTimBasePolicyMappingRepository;

    @Autowired
    private CustomerLocationMappingRepository customerLocationMappingRepository;

    @Autowired
    private CustomerQosPolicyMappingRepository customerQosPolicyMappingRepository;

    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    PostpaidPlanRepository postpaidPlanRepository;

    @Autowired
    private PlanUsagePercentageMappingService planUsagePercentageMappingService;


    @Autowired
    private StaffBusinessUnitMappingRepo staffBusinessUnitMappingRepo;

    @Autowired
    private StaffUserServiceAreaMappingRepo staffUserServiceAreaMappingRepo;

    @Autowired
    private CustomRepository customRepository;

    @Autowired
    private CustQuotaResetDetailsRepository custQuotaResetDetailsRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustIpMappingRepo custIpMappingRepo;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustPlanMappingServiceImpl custPlanMappingService;

    @Autowired
    private CustomerServiceHelper customerServiceHelper;

    @Autowired
    private LiveUserService liverUserService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    DeviceService deviceService;

    @Override
    public Customer findCustomerById(Long id, Integer mvnoId) {

        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid CoA/DM profile id.");
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCustomer.customerId.eq(id));
            Optional<Customer> customer = customerRepository.findOne(boolExp);
            if (!customer.isPresent()) {
                throw new IllegalArgumentException("No record found with Customer id " + id + " . Please enter valid Customer id.");
            }
            return customer.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Customers findCustomersByid(Integer id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter Customer id.");
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression boolExp = qCustomers.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomers.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCustomers.customers.id.eq(id));

            Optional<Customers> customer = customersRepository.findOne(boolExp);
            if (!customer.isPresent()) {
                throw new IllegalArgumentException("No record found with Customer id " + id + " . Please enter valid Customer id.");
            }
            return customer.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Customer findCustomerByName(String name, Integer mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException("Please enter valid customer name.");
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCustomer.userName.eq(name));

            Optional<Customer> customerOptional = customerRepository.findOne(boolExp);
            if (!customerOptional.isPresent()) {
                throw new IllegalArgumentException("No record found with customer name '" + name + "'. Please enter valid customer name");
            }
            return customerOptional.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
//    public List<Customers> findCustomersByName(String name, Integer mvnoId) {
//        try {
//
//            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
//                throw new IllegalArgumentException("Please enter valid customer name.");
//            QCustomers qCustomer = QCustomers.customers;
//            BooleanExpression boolExp = qCustomer.isNotNull();
//            if (mvnoId == null || mvnoId != 1)
//                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
//            boolExp = boolExp.and(qCustomer.username.containsIgnoreCase(name)
//                                  .or(qCustomer.firstname.containsIgnoreCase(name))
//                                   .or(qCustomer.lastname.containsIgnoreCase(name)));
//
//            List<Customers> customersList = customersRepository.findAll(boolExp);
//            if (customersList.isEmpty()) {
//                throw new IllegalArgumentException("No record found with customer name '" + name + "'. Please enter valid customer name");
//            }
//            return customersList;
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    public Customer validateCustomerForUpdateOrDelete(String name, Integer mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException("Please enter valid customer name.");
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomer.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qCustomer.userName.eq(name));

            Optional<Customer> customerOptional = customerRepository.findOne(boolExp);
            if (!customerOptional.isPresent()) {
                throw new IllegalArgumentException("You do not have access to update or delete this record.");
            }
            return customerOptional.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Customer> searchCustomerByName(String name, Integer mvnoId) {
        try {
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

            if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                return (List<Customer>) customerRepository.findAll(boolExp);
            } else {
                boolExp = boolExp.and(qCustomer.userName.containsIgnoreCase(name));
                return (List<Customer>) customerRepository.findAll(boolExp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Customers> searchCustomersByName(String name, Integer mvnoId, PaginationDTO paginationDTO) {
        try {
            QCustomers qCustomer = QCustomers.customers;
            BooleanExpression boolExp = qCustomer.isNotNull();
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
            if (name == null && paginationDTO.getFromDate() == null && paginationDTO.getToDate() == null
                    && paginationDTO.getPage() != 0 && paginationDTO.getSize() != 0) {
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }

                return customersRepository.findAll(pageable);
            }

            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

            if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null") || name == null) {
                return customersRepository.findAll(boolExp, pageable);
            } else {
                boolExp = boolExp.and(qCustomer.username.containsIgnoreCase(name)
                        .or(qCustomer.firstname.containsIgnoreCase(name))
                        .or(qCustomer.lastname.containsIgnoreCase(name)));

                Predicate builder = boolExp;
                if (paginationDTO.getSize() < 1) {
                    Page<Customers> page = new PageImpl<Customers>((List<Customers>) customersRepository.findAll(boolExp));
                    return page;
                }
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable1 = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
                return customersRepository.findAll(builder, pageable1);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Customer> findAllCustomer(Integer mvnoId) {

        try {
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression exp = qCustomer.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return customerRepository.findAll();
            else {
                exp = exp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<Customer>) customerRepository.findAll(exp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Customers> findAllCustomers(Integer mvnoId, Integer staffId, PaginationDTO paginationDTO) {
        try {
            List<Long> buIds = staffBusinessUnitMappingRepo.findAllByStaffId(staffId).stream()
                    .map(StaffUserBusinessUnitMapping::getBusinessunitId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            List<Long> serviceAreaIds = staffUserServiceAreaMappingRepo.findAllByStaffId(staffId).stream()
                    .map(StaffUserServiceAreaMapping::getServiceId)
                    .collect(Collectors.toList());
            QCustomers qCustomer = QCustomers.customers;
            BooleanExpression exp = qCustomer.isNotNull().and(qCustomer.isDeleted.eq(false));
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
            if (mvnoId != null && mvnoId == 1) {
                if (paginationDTO.getFromDate() == null && paginationDTO.getToDate() == null
                        && paginationDTO.getPage() != 0 && paginationDTO.getSize() != 0) {
                    if (paginationDTO.getPage() > 0) {
                        paginationDTO.setPage(paginationDTO.getPage() - 1);
                    }

                }
                return customersRepository.findAll(pageable);
            } else {
                exp = exp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                if (!buIds.isEmpty()) {
                    exp = exp.and(qCustomer.buId.in(buIds));
                }
                if (!serviceAreaIds.isEmpty()) {
                    exp = exp.and(qCustomer.servicearea.in(serviceAreaIds));
                }
                Predicate builder = exp;
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable1 = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
                return customersRepository.findAll(builder, pageable1);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Customer addCustomer(CustomerDto customerDto, Integer mvnoId) {
        validateEmailAddress(customerDto);
        Customer customer = new Customer(customerDto);
        customer.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
        try {
            validateCustomerDetail(customer, false);
            customer.setCreatedOn(new Timestamp(new Date().getTime()));
            customer.setLastLoginTime(new Timestamp(new Date().getTime()));
            customer.setLastModifiedOn(new Timestamp(new Date().getTime()));
            customer.setLastPasswordChange(new Timestamp(new Date().getTime()));
            String encryptPassword = encryptPassword(customer.getPassword());
            customer.setPassword(encryptPassword);
            sendCustRegSuccessMsg(customer, customerDto.getPassword());
            Customer customerVo = customerRepository.save(customer);
            if (customerDto.getCustomerReplyList() != null) {
                for (CustomerReply customerReply : customerDto.getCustomerReplyList()) {
                    customerReply.setCustomerId(customerVo.getCustomerId());
                    customerReplyService.addCustomerReply(customerReply, ValidateCrudTransactionData.validateMvnoId(mvnoId));
                }
            }
            saveMacAddressMapping(customerDto.getMacAddressMapping(), customerVo.getCustomerId(), mvnoId);
            saveCustomerLocationMapping(customerDto.getLocations(), customerVo.getCustomerId());
            saveTimeBasePolicyMapping(customerDto.getCustomerTimeBasePolicyMappings(), customerVo.getCustomerId());
            saveCustomerQosPolicyMapping(customerDto.getCustomerQosPolicyMappings(), customerVo.getCustomerId(), mvnoId);
            if (customerDto.getCustomerTimeBasePolicyMappings().size() > 0) ;
            MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
            // log.info("Radius Customer has been created successfully: " + customer.getUserName());
            return customerVo;
        } catch (RuntimeException e) {
            //sendCustRegFailureMsg(customer);
            if (!e.getMessage().contains(RadiusConstants.NOT_PUT_IN_QUEUE)) {
                //sendCustRegFailureMsg(customer,customerDto.getPassword());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveCustomerLocationMapping(List<Long> locationIdList, Long customerId) {
        try {
            List<CustomerLocationMapping> custLocMappingList = new ArrayList<>();
            if (locationIdList != null && !locationIdList.isEmpty()) {
                for (Long locationId : locationIdList) {
                    if (ValidateCrudTransactionData.validateLongTypeFieldValue(locationId)) {
                        CustomerLocationMapping locationVo = new CustomerLocationMapping();
                        locationVo.setCustId(customerId);
                        locationVo.setLocationId(locationId);
                        custLocMappingList.add(locationVo);
                    }
                }
                customerLocationMappingRepository.saveAll(custLocMappingList);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveMacAddressMapping(Set<MacAddressMapping> macAddressMappingDto, Long custID, Integer mvnoId) {
        try {
            if (macAddressMappingDto != null) {
                for (MacAddressMapping macAddressMapping : macAddressMappingDto) {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(macAddressMapping.getMacAddress())) {
                        MacAddressMapping macAddressMappingVo = new MacAddressMapping();
                        macAddressMappingVo.setMacAddress(macAddressMapping.getMacAddress());
                        macAddressMappingVo.setCustomerId(custID);
                        macAddressMappingVo.setCreateDate(new Timestamp(new Date().getTime()));
                        macAddressMappingVo.setLastModificationDate(new Timestamp(new Date().getTime()));
                        macAddressMappingVo.setCreatedBy("Admin");
                        macAddressMappingVo.setCustsermappingid(macAddressMapping.getCustsermappingid());
                        macAddressMappingVo.setNormalizeMac(normalizeMacAddress(macAddressMapping.getMacAddress()));
                        macAddressMappingRepository.save(macAddressMappingVo);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendCustRegSuccessMsg(Customer customerDto, String Password) {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(REGISTRATION_SUCCESS);
            if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                // Set message in queue to send notification after successful customer
                // registration.
                CustomerMessage customerMessage = new CustomerMessage(customerDto, Password, REGISTRATION_SUCCESS, optionalTemplate.get().getEmailTemplateData(), optionalTemplate.get().getSmsTemplateData(), optionalTemplate.get().getAppendUrl());
                //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS);
                kafkaMessageSender.send(new KafkaMessageData(customerMessage, customerMessage.getClass().getSimpleName(), "CUSTOMER_REGISTRATION_SUCCESS"));

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendCustRegFailureMsg(Customer customerDto, String password) {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(REGISTRATION_FAILURE);
            if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                // Set message in queue to send notification after customer registration has
                // been failed.
                CustomerMessage customerMessage = new CustomerMessage(customerDto, password, REGISTRATION_FAILURE, optionalTemplate.get().getEmailTemplateData(), optionalTemplate.get().getSmsTemplateData(), optionalTemplate.get().getAppendUrl());
                //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_REGISTRATION_FAILURE);
                kafkaMessageSender.send(new KafkaMessageData(customerMessage, customerMessage.getClass().getSimpleName(), "CUSTOMER_REGISTRATION_FAILURE"));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//	private void encryptPassword(Customer customer) {
//
//		try {
//
//			String encryptPassword = encryptPassword(customer.getPassword());
//			customer.setPassword(encryptPassword);
//		}
//		catch (RuntimeException e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}

    private void validateCustomerDetail(Customer customer, boolean isUpdate) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getCountryCode()) && !customer.getCountryCode().contains("+")) {
                throw new IllegalArgumentException("Please enter valid country code with prefix '+' sign." + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getUserName())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name." + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (!isUpdate && !ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getPassword())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Password is mandatory. Please enter valid password" + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (customer.getFailCount() == null) {
                customer.setFailCount((long) 0);
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getMobileNo())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG
                        + "Mobile number is mandatory. Please enter valid mobile number"
                        + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(customer.getSliceChunk())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "SliceChunk is mandatory. Please enter valid SLiceChunk Value " + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (customer.getSliceChunk() > 100 || customer.getSliceChunk() < 1) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "SliceChunk value should be between 1 to 100" + RadiusConstants.NOT_PUT_IN_QUEUE);
            } else if (isUpdate) {
                Customer customerVo = validateCustomerForUpdateOrDelete(customer.getUserName(), customer.getMvnoId());
                customer.setCreatedOn(customerVo.getCreatedOn());
                customer.setLastLoginTime(customerVo.getLastLoginTime());
                customer.setLastPasswordChange(customerVo.getLastPasswordChange());
                customer.setPassword(customerVo.getPassword());
                customer.setCustomerId(customerVo.getCustomerId());
            } else if (customer.getUserName() != null && !isUpdate) {
//                checkDuplicateUser(customer.getUserName(), customer.getMvnoId(), customer.getLocationId());
                checkDuplicateUserByMvnoId(customer.getUserName(), customer.getMvnoId());
                checkDuplicateUser(customer.getUserName(), customer.getCustomerId());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateEmailAddress(CustomerDto customerDto) {
        try {
            if (customerDto.getEmailAddress() != null && !customerDto.getEmailAddress().isEmpty() && customerDto.getEmailAddress() != "") {
                if (customerDto.getEmailAddress().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                    customerDto.setEmailAddress(null);
                }
                if (customerDto.getEmailAddress() != null && !validateEmailAddress(customerDto.getEmailAddress())) {
                    throw new IllegalArgumentException("Please enter valid email address." + RadiusConstants.NOT_PUT_IN_QUEUE);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateEmailAddressOnUpdate(UpdateCustomerDto customerDto) {
        try {
            if (customerDto.getEmailAddress() != null && !customerDto.getEmailAddress().isEmpty() && customerDto.getEmailAddress() != "") {
                if (customerDto.getEmailAddress().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                    customerDto.setEmailAddress(null);
                }
                if (customerDto.getEmailAddress() != null && !validateEmailAddress(customerDto.getEmailAddress())) {
                    throw new IllegalArgumentException("Please enter valid email address." + RadiusConstants.NOT_PUT_IN_QUEUE);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean validateEmailAddress(String emailId) {
        try {
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(emailId);
            return matcher.matches();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void checkDuplicateUserByMvnoId(String userName, Integer mvnoId) {

        try {
            String errMessage = "Customer already exist with username '" + userName + "'. Please enter unique username";
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            /*if(locationId != null)
                boolExp = boolExp.and(qCustomer.locationId.eq(locationId));
            else
                boolExp = boolExp.and(qCustomer.locationId.isNull());*/
            if (mvnoId == 1) {
                boolExp = boolExp.and(qCustomer.userName.eq(userName));
                List<Customer> customerList = (List<Customer>) customerRepository.findAll(boolExp);
                if (!customerList.isEmpty()) {
                    throw new IllegalArgumentException(errMessage);
                }
            } else {
                boolExp = boolExp.and(qCustomer.userName.eq(userName)).and((qCustomer.mvnoId.eq(mvnoId)).or(qCustomer.mvnoId.eq(1)));
                Optional<Customer> optionalCustomer = customerRepository.findOne(boolExp);
                if (optionalCustomer.isPresent()) {
                    throw new IllegalArgumentException(errMessage);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Customer updateCustomer(UpdateCustomerDto updateCustomerDto, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            Customer customer = findCustomerByName(updateCustomerDto.getUserName(), mvnoId);
            validateEmailAddressOnUpdate(updateCustomerDto);
            Customer customerVo = new Customer(updateCustomerDto, customer);
            customerVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            validateCustomerDetail(customerVo, true);
            customerVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
            deleteCustomerReply(updateCustomerDto, mvnoId, customerVo.getCustomerId());
            saveCustomerReply(updateCustomerDto, mvnoId, customerVo);
            // updateCustomerReply(customerVo, mvnoId);`
            deleteOldMacAddressMapping(customerVo.getCustomerId());
            deleteAllTimeBasePolicyMapping(customerVo.getCustomerId());
            qosPolicyMappingService.deleteByCustId(customer.getCustomerId(), mvnoId);
            customerVo.setCustomerTimeBasePolicyMappings(updateCustomerDto.getCustomerTimeBasePolicyMappings());
            saveMacAddressMapping(updateCustomerDto.getMacAddressMapping(), customerVo.getCustomerId(), customerVo.getMvnoId());
            // updateCustomerMacAddress(customerVo);
            saveCustomerQosPolicyMapping(updateCustomerDto.getCustomerQosPolicyMappings(), customerVo.getCustomerId(), customerVo.getMvnoId());
            Customer customerDetails = customerRepository.save(customerVo);
            MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
            // log.info("Radius Customer updated succefully, updated values " + customerVo.getUserName());
            return customerDetails;
        } catch (Throwable e) {
            e.printStackTrace();
            //   log.error("Error while updating radius customer: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public Customers updateCustomers(CustomerCreateData customer, Integer mvnoId, Boolean netconf) {
        try {
            Customers customers = customersRepository.findByCustomerId(customer.getCustId());
            if (customers != null) {
                Customers updatedCustomers = new Customers(customer);
                updatedCustomers.setId(customers.getId());
                updatedCustomers.setCafno(customers.getCafno());
                updatedCustomers.setMvnoId(customers.getMvnoId());
                updatedCustomers = customersRepository.save(updatedCustomers);
                if (!netconf)
                    customer.setMvnoId(mvnoId);
                //messageSender.send(customer, RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(customer, customer.getClass().getSimpleName(), "CUSTOMERS_UPDATE"));
                return updatedCustomers;
            } else {
                throw new RuntimeException("Customer not available: " + customer.getUsername());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Exception while update customer " + customer.getUsername());
        }
    }

    public void saveCustomerReply(UpdateCustomerDto customerDto, Integer mvnoId, Customer customerVo) {

        if (customerDto.getCustomerReplyList() != null) {
            for (CustomerReply customerReply : customerDto.getCustomerReplyList()) {
                customerReply.setCustomerId(customerVo.getCustomerId());
                customerReplyService.addCustomerReply(customerReply, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            }
        }
    }

    public void deleteCustomerReply(UpdateCustomerDto customerDto, Integer mvnoId, Long customerId) {

        List<CustomerReply> customerReplyList = new ArrayList<CustomerReply>();
        List<CustomerReply> customerRepliesToDelete = customerReplyService.findCustomerReplyByCustomerId(customerId, mvnoId);
        if (!customerRepliesToDelete.isEmpty()) {
            for (CustomerReply customerReply : customerRepliesToDelete) {
                customerReplyList.add(customerReply);
            }
            custReplyRepo.deleteAll(customerReplyList);
        }
    }

    private void deleteOldMacAddressMapping(Long customerId) {

        List<MacAddressMapping> macList = macAddressMappingRepository.findMacAddressMappingByCustomerId(customerId);
        if (!macList.isEmpty()) {
            macAddressMappingRepository.deleteAll(macList);
        }
    }

    private void updateCustomerReply(Customer customerVo, Integer mvnoId) {
        try {
            List<CustomerReply> oldCustmoerReplyList = custReplyRepo.findByCustomerId(customerVo.getCustomerId());
            Map<Long, CustomerReply> map = new HashMap<>();
            for (CustomerReply customerReply : oldCustmoerReplyList) {
                map.put(customerReply.getAttributeId(), customerReply);
            }

            if (customerVo.getCustomerReplyList() != null) {
                for (CustomerReply customerReply : customerVo.getCustomerReplyList()) {
                    customerReply.setMvnoId(mvnoId);
                    customerReply.setLastModifiedOn(new Timestamp(new Date().getTime()));
                    if (map.containsKey(customerReply.getAttributeId())) {
                        CustomerReply customerReplyVo = map.get(customerReply.getAttributeId());
                        customerReply.setCreatedOn(customerReplyVo.getCreatedOn());
                    } else {
                        customerReply.setCreatedOn(new Timestamp(new Date().getTime()));
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void updateCustomerMacAddress(Customer customerVo) {
        try {
            List<MacAddressMapping> oldMacAddressList = macAddressMappingRepository.findByCustomerId(customerVo.getCustomerId());
            if (customerVo.getMacAddressMapping() != null) {
                for (MacAddressMapping macAddressMapping : customerVo.getMacAddressMapping()) {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(macAddressMapping.getMacAddress())) {
                        boolean check = false;
                        Long macAddressId = null;
                        for (MacAddressMapping oldMacAddressMapping : oldMacAddressList) {
                            if (oldMacAddressMapping.getMacAddress().equals(macAddressMapping.getMacAddress())) {
                                check = true;
                                macAddressId = oldMacAddressMapping.getMacAddressId();
                                break;
                            }
                        }
                        if (check) {
                            macAddressMapping.setMacAddressId(macAddressId);
                            macAddressMapping.setCustomerId(customerVo.getCustomerId());
                            macAddressMapping.setLastModificationDate(new Timestamp(new Date().getTime()));
                            macAddressMapping.setLastModifiedBy("Admin");
                        } else {
                            macAddressMapping.setMacAddress(macAddressMapping.getMacAddress());
                            macAddressMapping.setCustomerId(customerVo.getCustomerId());
                            macAddressMapping.setCreateDate(new Timestamp(new Date().getTime()));
                            macAddressMapping.setCreatedBy("Admin");
                            macAddressMapping.setLastModificationDate(new Timestamp(new Date().getTime()));
                            macAddressMapping.setLastModifiedBy("Admin");
                        }
                    }
                }

                List<String> newUpdatedMacAddressList = new ArrayList<>();
                for (MacAddressMapping newMacAddressMapping : customerVo.getMacAddressMapping()) {
                    newUpdatedMacAddressList.add(newMacAddressMapping.getMacAddress());
                }

                List<MacAddressMapping> deleteMacAddressList = new ArrayList<>();
                for (MacAddressMapping oldMacAddressMapping : oldMacAddressList) {
                    if (!newUpdatedMacAddressList.contains(oldMacAddressMapping.getMacAddress())) {
                        if (!deleteMacAddressList.contains(oldMacAddressMapping)) {
                            deleteMacAddressList.add(oldMacAddressMapping);
                        }
                    }
                }
                macAddressMappingRepository.deleteAll(deleteMacAddressList);
            }
            if (!oldMacAddressList.isEmpty() && customerVo.getMacAddressMapping() == null) {
                macAddressMappingRepository.deleteAll(oldMacAddressList);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteCustomer(String userName, Integer mvnoId) {

        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid user name.");
            }

            Customer customer = validateCustomerForUpdateOrDelete(userName, mvnoId);
            List<MacAddressMapping> macVo = macAddressMappingRepository.findMacAddressMappingByCustomerId(customer.getCustomerId());
            if (macVo.size() > 0) {
                macAddressMappingRepository.deleteAll(macVo);
            }
            qosPolicyMappingService.deleteByCustId(customer.getCustomerId(), mvnoId);
            List<CustomerLocationMapping> custLocMappingList = customerLocationMappingRepository.findByCustId(customer.getCustomerId());
            if (!custLocMappingList.isEmpty()) {
                customerLocationMappingRepository.deleteAll(custLocMappingList);
            }
            customerRepository.delete(customer);
            //  log.info("Radius Customer deleted succefully: " + userName);
        } catch (RuntimeException e) {
            e.printStackTrace();
            //  log.error("Error while deleting radius customer: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public String updateCustomerStatus(String userName, String status, Integer mvnoId) {

        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
            }

            Customer customer = validateCustomerForUpdateOrDelete(userName, ValidateCrudTransactionData.validateMvnoId(mvnoId));

            customer.setCustomerStatus(status);
            customer.setLastModifiedOn(new Timestamp(new Date().getTime()));
            customerRepository.save(customer);
            String msg = "";
            if (status.equals("Active")) {
                msg = "Customer '" + customer.getUserName() + "' has been activated successfully.";
            } else {
                msg = "Customer '" + customer.getUserName() + "' has been inactivated successfully.";
            }
            return msg;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void changePassword(CustomerPasswordDto passwordDto, Integer mvnoId) {

        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getUserName())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid user name");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getNewPassword())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid password");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid confirm password");
            } else if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Please enter valid password. New password and confirm password value must be same.");
            } else if (passwordDto.getUserName() != null) {

                Customer customer = validateCustomerForUpdateOrDelete(passwordDto.getUserName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
                customer.setPassword(encryptPassword(passwordDto.getNewPassword()));
                customer.setLastPasswordChange(new Timestamp(new Date().getTime()));
                customerRepository.save(customer);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void changeBCCPassword(UpdatePasswordResetDto updatePasswordResetDto, Integer mvnoId) {
        try {

            if (updatePasswordResetDto.getId() != null) {

                Customers customer = customersRepository.findByCustomerId(updatePasswordResetDto.getId());
                customer.setPassword(updatePasswordResetDto.getPassword());
                customersRepository.save(customer);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Customer addWifiCustomer(CustomMessage message) {

        try {

            if (message.getCustomerData() != null) {

                CustomerDto customerDto = new CustomerDto(message);
                return addCustomer(customerDto, customerDto.getMvnoId());
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Customer updateWifiCustomer(CustomMessage message) {

        try {

            if (message.getCustomerData() != null) {
                UpdateCustomerDto customerDto = new UpdateCustomerDto(message);
                return updateCustomer(customerDto, customerDto.getMvnoId());
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteWifiCustomer(CustomMessage message) {

        try {

            if (message.getCustomerData() != null && message.getCustomerData().get(USER_NAME) != null) {
                deleteCustomer(message.getCustomerData().get(USER_NAME).toString(), Integer.parseInt(message.getCustomerData().get(MVNO_ID).toString()));
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //    @Override
//    public void rechargeQuota(Long custId, Customer plan, Long mvnoId) {
//        try {
//            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
//                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
//            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(custId)) {
//                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid customer");
//            }
//            Optional<Customer> customer = customerRepository.findById(custId);
//            if (plan != null) {
//                customer.get().setPlanId(plan.getPlanId());
//                //customer.get().setQuota(plan.getQuota());
//                customer.get().setQosPolicyName(customer.get().getQosPolicyName());
//                //customer.get().setUom(plan.getUom());
//                customer.get().setDownloadSpeed(plan.getDownloadSpeed());
//                if (plan.getPlanType() != null) {
//                    customer.get().setPlanType(plan.getPlanType());
//                    if (plan.getPlanType().equals(RadiusConstants.TIME)) {
//                        String timeBasedTotalQuota = calculateTimeBasedQuota(plan.getTimeBasedTotalQuota(), plan.getQosPolicyName());
//                        customer.get().setTimeBasedTotalQuota(customer.get().getTimeBasedTotalQuota() + timeBasedTotalQuota);
//                        customer.get().setTimeBasedUnusedQuota(customer.get().getTimeBasedUnusedQuota() + timeBasedTotalQuota);
//                    } else if (plan.getPlanType().equals(RadiusConstants.VOLUME)) {
//                        String volumeBasedTotalQuota = calculateVolumeBasedQuota(plan.getVolumeBasedTotalQuota(), plan.getQosPolicyName());
//                        customer.get().setVolumeBasedTotalQuota(customer.get().getVolumeBasedTotalQuota() + volumeBasedTotalQuota);
//                        customer.get().setVolumeBasedUnusedQuota(customer.get().getVolumeBasedUnusedQuota() + volumeBasedTotalQuota);
//                    }
//                }
//                customer.get().setBaseDownloadQos(plan.getBaseDownloadQos());
//                customer.get().setBaseUploadQos(plan.getBaseUploadQos());
//                customer.get().setUnlimitedPlan(plan.getUnlimitedPlan());
//                customer.get().setUploadSpeed(plan.getUploadSpeed());
//                customer.get().setPlanName(plan.getPlanName());
//                //customer.get().setStartDate(LocalDateTime.now());
//                //customer.get().setEndDate((LocalDateTime.now()).plusDays(plan.getValidity()));
//                customerRepository.save(customer.get());
//
//
//            }
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
    @Override
    public void rechargeQuota(Long custId, Customer customer, Boolean allowCrossRecharge, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(custId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid customer");
            }
            if (allowCrossRecharge) {
                //Customer customerplanDetailsVo = null;
                // Optional<CustomerPlanDetail> customerplanDetailsVo = customerPlanDetailRepository.findByCustomerId(custId);
                Optional<Customer> customerplanDetailsVo = customerRepository.findById(custId);
                if (customer != null) {
                    customerplanDetailsVo.get().setPlanId(customer.getPlanId());
                    customerplanDetailsVo.get().setCustomerId(custId);
                    //customerplanDetailsVo.setQuota(customer.getQuota());
                    customerplanDetailsVo.get().setQosPolicyName(customer.getQosPolicyName());
                    //customerplanDetailsVo.setUom(customer.getUom());
                    customerplanDetailsVo.get().setDownloadSpeed(customer.getDownloadSpeed());
                    if (customer.getPlanType() != null) {
//                	if(customer.getPlanType().equals(customerplanDetailsVo.get().getPlanType()))
//                	{
                        customerplanDetailsVo.get().setPlanType(customer.getPlanType());
                        if (customer.getPlanType().equals(RadiusConstants.TIME)) {
                            String timeBasedTotalQuota = calculateTimeBasedQuota(customer.getTimeBasedTotalQuota(), customer.getQosPolicyName());
                            customerplanDetailsVo.get().setTimeBasedTotalQuota(timeBasedTotalQuota);
                            customerplanDetailsVo.get().setTimeBasedUnusedQuota(timeBasedTotalQuota);
                        } else if (customer.getPlanType().equals(RadiusConstants.VOLUME)) {
                            String volumeBasedTotalQuota = calculateVolumeBasedQuota(customer.getVolumeBasedTotalQuota(), customer.getQosPolicyName());
                            customerplanDetailsVo.get().setVolumeBasedTotalQuota(volumeBasedTotalQuota);
                            customerplanDetailsVo.get().setVolumeBasedUnusedQuota(volumeBasedTotalQuota);
                        }
//                    }else {
//                    	throw new IllegalArgumentException(
//                                RadiusConstants.BASIC_STRING_MSG + "Cross plan recharge is allowed but old quota will be flushed, do you want to continue?.");
//                    }
                    }
//                customerplanDetailsVo.get().setThrottleDownload(customer.getThrottleDownload());
//                customerplanDetailsVo.get().setThrottleUpload(customer.getThrottleUpload());
                    customerplanDetailsVo.get().setUnlimitedPlan(customer.getUnlimitedPlan());
                    customerplanDetailsVo.get().setUploadSpeed(customer.getUploadSpeed());
                    customerplanDetailsVo.get().setPlanName(customer.getPlanName());
                    //customerplanDetailsVo.setStartDate(LocalDateTime.now());
                    //  customerplanDetailsVo.setEndDate((LocalDateTime.now()).plusDays(customer.getValidity()));
                    customerRepository.save(customerplanDetailsVo.get());

                }
            } else {
                Optional<Customer> customerVo = customerRepository.findById(custId);
                if (customer != null) {
                    customerVo.get().setPlanId(customer.getPlanId());
                    //customer.get().setQuota(plan.getQuota());
                    customerVo.get().setQosPolicyName(customerVo.get().getQosPolicyName());
                    //customer.get().setUom(plan.getUom());
                    customerVo.get().setDownloadSpeed(customer.getDownloadSpeed());
                    if (customer.getPlanType() != null) {
                        if (customer.getPlanType().equals(customerVo.get().getPlanType()))
//                        	{
                            customerVo.get().setPlanType(customer.getPlanType());
                        if (customer.getPlanType().equals(RadiusConstants.TIME)) {
                            String timeBasedTotalQuota = calculateTimeBasedQuota(customer.getTimeBasedTotalQuota(), customer.getQosPolicyName());
                            Double calQuota = Double.parseDouble(timeBasedTotalQuota.toString());
                            Double totalQuota = Double.parseDouble(customerVo.get().getTimeBasedTotalQuota().toString()) + calQuota;
                            Double unusedQuota = Double.parseDouble(customerVo.get().getTimeBasedUnusedQuota().toString()) + calQuota;

                            customerVo.get().setTimeBasedTotalQuota(totalQuota.toString());
                            customerVo.get().setTimeBasedUnusedQuota(unusedQuota.toString());
                        } else if (customer.getPlanType().equals(RadiusConstants.VOLUME)) {
                            String volumeBasedTotalQuota = calculateVolumeBasedQuota(customer.getVolumeBasedTotalQuota(), customer.getQosPolicyName());
                            Double calQuota = Double.parseDouble(volumeBasedTotalQuota.toString());
                            Double totalQuota = Double.parseDouble(customerVo.get().getVolumeBasedTotalQuota().toString()) + calQuota;
                            Double unusedQuota = Double.parseDouble(customerVo.get().getVolumeBasedUnusedQuota().toString()) + calQuota;
                            customerVo.get().setVolumeBasedTotalQuota(totalQuota.toString());
                            customerVo.get().setVolumeBasedUnusedQuota(unusedQuota.toString());
                        }
                    } else {
//                        	throw new IllegalArgumentException(
//                          RadiusConstants.BASIC_STRING_MSG + "Cross plan recharge is allowed but old quota will be flushed, do you want to continue?.");
//              }
                    }
                    customerVo.get().setBaseDownloadQos(customer.getBaseDownloadQos());
                    customerVo.get().setBaseUploadQos(customer.getBaseUploadQos());
                    customerVo.get().setUnlimitedPlan(customer.getUnlimitedPlan());
                    customerVo.get().setUploadSpeed(customer.getUploadSpeed());
                    customerVo.get().setPlanName(customer.getPlanName());
                    //customer.get().setStartDate(LocalDateTime.now());
                    //customer.get().setEndDate((LocalDateTime.now()).plusDays(plan.getValidity()));
                    customerRepository.save(customerVo.get());


                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String calculateVolumeBasedQuota(String quota, String uom) {
        String totalQuotaInBytes = "";
        if ((uom != null && !uom.equals("")) && (quota != null && !quota.equals(""))) {
            if (uom.equals(RadiusConstants.MB)) {
                totalQuotaInBytes = String.valueOf(Double.parseDouble(quota) * 1024 * 1024);
            } else {
                totalQuotaInBytes = String.valueOf(Double.parseDouble(quota) * 1024 * 1024 * 1024);
            }
        }
        return totalQuotaInBytes;
    }

    private String calculateTimeBasedQuota(String quota, String uom) {
        String totalQuotaInSeconds = "";
        if ((uom != null && !uom.equals("")) && (quota != null && !quota.equals(""))) {
            if (uom.equals(RadiusConstants.MIN)) {
                totalQuotaInSeconds = String.valueOf(Double.parseDouble(quota) * 60);
            } else if (uom.equals(RadiusConstants.HOUR)) {
                totalQuotaInSeconds = String.valueOf(Double.parseDouble(quota) * 60 * 60);
            } else {
                totalQuotaInSeconds = String.valueOf((24 * (Double.parseDouble(quota))) * 60 * 60);
            }
        }
        return totalQuotaInSeconds;
    }


    @Override
    public void updateWifiCustomerQuota(CustomMessage message) {
        try {
            if (message.getCustomerData() != null) {
                log.info("Update quota reset from CMS: " + message);
                UpdateCustomerQuotaDto customerDto = new UpdateCustomerQuotaDto(message);
                updateCustomerQuota(customerDto);
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateWifiCustomerPassword(CustomMessage message) {

        try {

            if (message.getCustomerData() != null) {
                CustomerPasswordDto passwordDto = new CustomerPasswordDto(message.getCustomerData());
                changePassword(passwordDto, passwordDto.getMvnoId());
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateBSSCustomerPassword(CustomMessage message) {
        try {
            if (message.getCustomerData() != null) {
                UpdatePasswordResetDto passwordDto = new UpdatePasswordResetDto(message);
                changeBCCPassword(passwordDto, passwordDto.getMvnoId().intValue());
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void terminateUserSession(List<TerminateUser> userList, Integer mvnoId) {

        CacheRetrival cacheRetrival = new CacheRetrival();
        RadiusUtility radiusUtility = new RadiusUtility();
        ValidateExpression validate = new ValidateExpression();

        try {
            RadiusUtility radUtil = new RadiusUtility();
            if (!userList.isEmpty()) {
                for (TerminateUser terminateUser : userList) {
                    LiveUserSearchDTO liveUserSearchDTO = new LiveUserSearchDTO();
                    liveUserSearchDTO.setUserName(terminateUser.getUserName());
                    liveUserSearchDTO.setPage(0);
                    liveUserSearchDTO.setSize(0);
                    Page<LiveUser> pageLiveUser = liveUserService.findLiveUsersUsingFilter(liveUserSearchDTO, mvnoId);
                    List<LiveUser> listLiveUser = new ArrayList<>(pageLiveUser.getContent());
                    log.debug("No of Live User Found:" + listLiveUser.size());
                    for (LiveUser liveuser : listLiveUser) {
                        CoaDMProfile dmProfileData = null;
                        RadiusPacket coaDMResponse = null;
                        try {

                            String strDMCoAIP = liveuser.getSourceipaddress();

                            log.debug("COA/DM request:" + liveuser.getUserName());
                            AccountingRequest request = getAccountingRequest(liveuser);

                            Client cltData = radiusUtility.identifyClient(liveuser.getSourceipaddress(), request);

                            ClientGroup cltGroupData = null;
                            Long CoAProfileId = 0L;
                            Long DMProfileId = 0L;

                            if (cltData != null) {
                                cltGroupData = cltData.getClientGroupData();
                                if (cltGroupData != null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("For Client Group:" + cltData.getClientGroupId() + ":DATA:" + cltGroupData + ":mvnoid:" + cltData.getMvnoId());
                                        DMProfileId = cltGroupData.getDMProfile();
                                        log.debug("For Client Group COA:" + CoAProfileId + ":CoA/DM:" + DMProfileId);
                                    }
                                } else {
                                    log.error("Client group configuration not found for request: " + request);
                                    return;
                                }
                            } else {
                                log.error("Client configuration not found for source-ip: " + liveuser.getSourceipaddress());
                                return;
                            }


                            List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
                            log.debug("CoA Profile Cache size:" + coaDMProfileDataList.size());
                            if (cltGroupData != null && !CollectionUtils.isEmpty(cltGroupData.getCoaDmProfileMappings())) {
                                CustomerData customerData = new CustomerData();
                                customerData.setUsername(liveuser.getUserName());
                                customerData.setFramedIPAddress(liveuser.getFramedIpAddress());
                                customerData.setNasIPAddress(liveuser.getNasIpAddress());
                                //COA Profile
                                List<CoaDmProfileMapping> coaProfileMappings = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null).collect(Collectors.toList());
                                for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                                    log.info(String.format("Expression Check For %s:", profileMapping.getCheckItem()));
                                    boolean response = validate.checkExpression(profileMapping.getCheckItem(), null, customerData);
                                    if (response) {
                                        CoAProfileId = profileMapping.getCoaProfileId();
                                    }
                                }
                                if (CoAProfileId == 0L) {
                                    Optional<CoaDmProfileMapping> coaProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                                    if (coaProfileMapping.isPresent()) {
                                        CoAProfileId = coaProfileMapping.get().getCoaProfileId();
                                    } else {
                                        CoAProfileId = 0L;
                                        log.error("COA Profile not found..!");
                                    }
                                }
                                //DM Profile
                                List<CoaDmProfileMapping> dmProfileMappings = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getDmProfileId() != null).collect(Collectors.toList());
                                for (CoaDmProfileMapping profileMapping : dmProfileMappings) {
                                    boolean response = validate.checkExpression(profileMapping.getCheckItem(), null, customerData);
                                    if (response) {
                                        DMProfileId = profileMapping.getDmProfileId();
                                    }
                                }
                                if (DMProfileId == 0L) {
                                    Optional<CoaDmProfileMapping> dmProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getDmProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                                    if (dmProfileMapping.isPresent()) {
                                        CoAProfileId = dmProfileMapping.get().getDmProfileId();
                                    } else {
                                        DMProfileId = 0L;
                                        log.error("DM Profile not found..!");
                                    }
                                }
                            }
                            if (dmProfileData == null && DMProfileId != 0L) {
                                if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                    for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                        if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                            dmProfileData = coaDMProfileDataList.get(i);
                                            dmProfileData.setGateway(strDMCoAIP);
                                        }
                                    }
                                }
                            }

                            log.debug("DM Profile data:" + dmProfileData);
                            coaDMResponse = radUtil.initiateCoADM(dmProfileData, request, liveuser.getUserName(), null, strDMCoAIP);
                            log.warn("COA/DM response:"+coaDMResponse+":For Event:"+"terminateUserSession API"+":");
                            if(coaDMResponse!=null){
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),"terminateUserSession API", cltData.getMvnoId(),coaDMResponse);
                            }
                            else{
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs","terminateUserSession API", cltData.getMvnoId(),coaDMResponse);
                            }
                        } catch (Exception ex) {
                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                            radaysn.coaRespnseProcess(dmProfileData.getGateway(),liveuser.getUserName(), "Error or Timeout","terminateUserSession API", liveuser.getMvnoId(),coaDMResponse);
                            log.error("CoA/DM Failed:" + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void CoADMSupport(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation) {
        if (custRetrunData != null)
            log.debug("In CoADMSupport type: " + type + " event: " + operation + " custRetrunData: " + custRetrunData.getUsername());
        else
            log.debug("In CoADMSupport type: " + type + " event: " + operation + " For Unknown USER");
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        CacheRetrival cacheRetrival = new CacheRetrival();
        RadiusUtility radiusUtility = new RadiusUtility();
        ValidateExpression validate = new ValidateExpression();
        try {
            if (custRetrunData != null && custRetrunData.getCustomerBasePlan() != null) {
                double volumeQuota = custRetrunData.getCustomerBasePlan().get(0).getVolumequota();
                volumeQuota = radiusUtility.convertUsageToBytes(volumeQuota, custRetrunData.getCustomerBasePlan().get(0).getQuotaunit());
                custRetrunData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
            }
            if (!userList.isEmpty()) {
                for (changeUserData changeuserData : userList) {
                    LiveUserSearchDTO liveUserSearchDTO = new LiveUserSearchDTO();
                    liveUserSearchDTO.setUserName(changeuserData.getUserName());
                    liveUserSearchDTO.setPage(0);
                    liveUserSearchDTO.setSize(0);
                    Page<LiveUser> pageLiveUser = liveUserService.findLiveUsersUsingFilter(liveUserSearchDTO, Math.toIntExact(changeuserData.getMvnoId()));
                    List<LiveUser> listLiveUser = new ArrayList<>(pageLiveUser.getContent()); // Create a new modifiable list
                    log.debug("No of Live User Found:" + listLiveUser.size());
                    List<Long> userIds = listLiveUser.stream().map(LiveUser::getCdrID).collect(Collectors.toList());
                    try {
                        List<MacAddressMapping> macAddressMappings = macAddressMappingRepository.findMacAddressMappingByCustomerId(Long.valueOf((custRetrunData.getCustid())));
                        if (!CollectionUtils.isEmpty(macAddressMappings)) {
                            log.debug("Add session with Mac for COA/DM: " + macAddressMappings.size());
                            for (MacAddressMapping mapping : macAddressMappings) {
                                log.debug("Add session with Mac for COA/DM MAc: " + mapping.getMacAddress());
                                liveUserSearchDTO = new LiveUserSearchDTO();
                                liveUserSearchDTO.setUserName(mapping.getMacAddress());
                                liveUserSearchDTO.setPage(0);
                                liveUserSearchDTO.setSize(0);
                                Page<LiveUser> LiveUsers = liveUserService.findLiveUsersUsingFilter(liveUserSearchDTO, null /*Math.toIntExact(changeuserData.getMvnoId())*/);
                                List<LiveUser> liveUsersByMac = new ArrayList<>(LiveUsers.getContent());//LiveUsers.getContent();
                                log.debug("Live user from Mac count: " + liveUsersByMac.size());
                                liveUsersByMac = liveUsersByMac.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                                log.debug("Live user from Mac after duplicate remove count: " + liveUsersByMac.size());
                                if (!CollectionUtils.isEmpty(liveUsersByMac)) {
                                    listLiveUser.addAll(liveUsersByMac);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("error while fetch Live user using mac");
                    }
                    //find by class
                    List<LiveUser> liveUsersByClass = liveUserService.findLiveUsersByLClass(changeuserData.getUserName());
                    if (!CollectionUtils.isEmpty(listLiveUser) && !CollectionUtils.isEmpty(liveUsersByClass)) {
                        liveUsersByClass = liveUsersByClass.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(liveUsersByClass))
                            listLiveUser.addAll(liveUsersByClass);
                    } else if (!CollectionUtils.isEmpty(liveUsersByClass))
                        listLiveUser = liveUsersByClass;

                    log.debug("Live User Count: " + listLiveUser.size());
                    if (!CollectionUtils.isEmpty(listLiveUser) && operation != null && operation.equalsIgnoreCase(CommonConstants.CoaDmResonContant.TERMINATE_SESSION)) {
                        listLiveUser = listLiveUser.stream().sorted(Comparator.comparing(LiveUser::getCdrID)).collect(Collectors.toList());
                        listLiveUser = Arrays.asList(listLiveUser.get(0));
                    }
                    for (LiveUser liveuser : listLiveUser) {
                        log.debug("CoA/DM For User:" + liveuser.getUserName() + ":IP:" + liveuser.getSourceipaddress() + ":id:" + liveuser.getCdrID() + ":Type:" + type);
                        String strDMCoAIP = liveuser.getSourceipaddress();

                        AccountingRequest request = getAccountingRequest(liveuser);

                        Client cltData = radiusUtility.identifyClient(liveuser.getSourceipaddress(), request);
                        ClientGroup cltGroupData = null;
                        Long CoAProfileId = 0L;
                        Long DMProfileId = 0L;
                        Long PermanentProfileId = 0L;
                        String clientGroupId = liveuser.getClientGroupId();
                        //SUP-1674: For COA/DM client group not matched with check-item
                        if (clientGroupId != null && !CollectionUtils.isEmpty(cltData.getClientGroupMappings())) {
                            log.debug("Client Group Id available in Live User: " + liveuser.getAcctSessionId() + " ,id: " + clientGroupId);
                            Optional<ClientGroup> matchCltGroup = cltData.getClientGroupMappings().stream().filter(clientGroupMapping -> clientGroupMapping.getClientGroupId().toString().equalsIgnoreCase(clientGroupId)).findFirst().map(ClientGroupMapping::getClientGroupData);
                            if (matchCltGroup.isPresent()) {
                                log.debug("Client Group Matched for Live User: " + liveuser.getAcctSessionId() + " ,group name: " + matchCltGroup.get().getName());
                                cltData.setClientGroupId(matchCltGroup.get().getClientGroupId());
                                cltData.setClientGroupData(matchCltGroup.get());
                            } else {
                                log.debug("Invalid Client Group or client in Live User: " + liveuser.getAcctSessionId() + " : " + liveuser.getAcctMultiSessionId());
                            }
                        }
                        if (cltData != null) {
                            cltGroupData = cltData.getClientGroupData();
                            if (cltGroupData != null && cltGroupData.getClientGroupId() != null) {
                                cltGroupData = cltData.getClientGroupData();
                                if (log.isDebugEnabled()) {
                                    log.debug("For Client Group:" + cltData.getClientGroupId() + ":DATA:" + cltGroupData + ":mvnoid:" + cltData.getMvnoId());
                                }
                                CoAProfileId = cltGroupData.getCoaDMProfile();
                                DMProfileId = cltGroupData.getDMProfile();
                                if (cltGroupData.getPermanentDisconnectProfileId() != null)
                                    PermanentProfileId = cltGroupData.getPermanentDisconnectProfileId();

                                log.debug("For Client Group COA:" + CoAProfileId + ":CoA/DM:" + DMProfileId);
                            } else {
                                log.error("Client group configuration not found for request: " + request);
                                return;
                            }
                        } else {
                            log.error("Client configuration not found for source-ip: " + liveuser.getSourceipaddress());
                            return;
                        }
                        CoaDMProfile coaProfileData = null;
                        CoaDMProfile dmProfileData = null;
                        CoaDMProfile PermanentProfileData = null;


                        try {
                            List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
                            log.debug("CoA Profile Cache size:" + coaDMProfileDataList.size());
                            if (cltGroupData != null && !CollectionUtils.isEmpty(cltGroupData.getCoaDmProfileMappings())) {
                                CustomerData customerData = new CustomerData();
                                customerData.setUsername(liveuser.getUserName());
                                customerData.setFramedIPAddress(liveuser.getFramedIpAddress());
                                customerData.setNasIPAddress(liveuser.getNasIpAddress());
                                List<CoaDmProfileMapping> profileMappings = cltGroupData.getCoaDmProfileMappings().stream().sorted(Comparator.comparing(CoaDmProfileMapping::getPriority).reversed()).collect(Collectors.toList());
                                //COA Profile
                                List<CoaDmProfileMapping> coaProfileMappings = profileMappings.stream().collect(Collectors.toList());
                                boolean triggerSNMP = false;
                                for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                                    log.info(String.format("Expression Check For %s:", profileMapping.getCheckItem()));
                                    boolean response = validate.checkExpression(profileMapping.getCheckItem(), null, custRetrunData, operation);
                                    if (response) {
                                        if (profileMapping.getCoaProfileId() != null) {
                                            CoAProfileId = profileMapping.getCoaProfileId();
                                            if (profileMapping.getDmProfileId() != null) {
                                                DMProfileId = profileMapping.getDmProfileId();
//                                            break;
                                            }
                                            break;
                                        } else if (profileMapping.getDmProfileId() != null) {
                                            DMProfileId = profileMapping.getDmProfileId();
                                            if (profileMapping.getCoaProfileId() != null) {
                                                CoAProfileId = profileMapping.getCoaProfileId();
                                            }
                                            break;
                                        } else if (profileMapping.getCoaDmSelection().equalsIgnoreCase("SNMP")) {
                                            triggerSNMP = true;
                                            break;
                                        }
                                    }
                                }
                                if (!triggerSNMP) {
                                    if (dmProfileData == null && DMProfileId != null && DMProfileId != 0L) {
                                        if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                            for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                                if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                                    dmProfileData = coaDMProfileDataList.get(i);
                                                    dmProfileData.setGateway(strDMCoAIP);
                                                }
                                            }
                                        }
                                    }

                                    if (coaProfileData == null && CoAProfileId != null && CoAProfileId != 0L) {
                                        if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                            for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                                if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                                    coaProfileData = coaDMProfileDataList.get(i);
                                                    coaProfileData.setGateway(strDMCoAIP);
                                                }
                                            }
                                        }
                                    }

                                    if (PermanentProfileData == null && PermanentProfileId != 0L) {
                                        if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                            for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                                if (PermanentProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                                    PermanentProfileData = coaDMProfileDataList.get(i);
                                                    PermanentProfileData.setGateway(strDMCoAIP);
                                                }
                                            }
                                        }
                                    }

                                    if (coaProfileData == null) {
                                        coaProfileData = dmProfileData;
                                    }

                                    if (PermanentProfileData == null) {
                                        PermanentProfileData = dmProfileData;
                                    }

                                    log.debug("coaProfileData:" + coaProfileData + ":Dm profile Data:" + dmProfileData + ":Permangenet profile Data:" + PermanentProfileData);
                                    if (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT)) {
                                        //Remove Mac From Cache if available
                                        RadiusAttribute radiusAttribute = request.getAttribute(RadiusAttributes.CALLING_STATION_ID.getName());
                                        //Remove Cache
                                        if (radiusAttribute != null) {
                                            CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                            String strCalling = radiusAttribute.getAttributeValue();
                                            if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                                log.debug("Cache remove on DM for mac: " + strCalling);
                                                cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                                            }
                                        }
                                    }
                                    if (type.equalsIgnoreCase("CoA")) {
                                        try {
                                            if ((operation != null && (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_SUSPEND) || operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_ACTIVE)))
                                                    && custRetrunData != null && custRetrunData.getMacAuthEnable() != null && !custRetrunData.getMacAuthEnable()) {
                                                //check faulty mac
                                                List<String> faultyMACS = cacheRetrival.getFaultyMacData();
                                                boolean faultyMacNotFound = true;
                                                if (!CollectionUtils.isEmpty(faultyMACS)) {
                                                    String normalizedTargetMac = liveuser.getCallingStationId();
                                                    if (normalizedTargetMac != null) {
                                                        normalizedTargetMac = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
                                                    }
                                                    boolean isPresent = faultyMACS.stream()
                                                            .map(radiusUtility::normalizeMacAddress) // Use instance method reference
                                                            .anyMatch(normalizedTargetMac::equals);
                                                    if (isPresent) {
//                                                    if (faultyMACS.contains(liveuser.getCallingStationId())) {
                                                        faultyMacNotFound = false;
                                                        log.error("Faulty Mac Found, So skipp Store Mac: " + liveuser.getCallingStationId());
                                                    }
                                                }
                                                if (faultyMacNotFound) {
                                                    //save mac in cache
                                                    CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                                    CustomerDetails customerDetails = new CustomerDetails(custRetrunData.getUsername(), liveuser.getCustid(), liveuser.getlClass());
                                                    cacheService.put(radiusUtility.normalizeMacAddress(liveuser.getCallingStationId()), customerDetails);
                                                    log.info("COA Cache for: " + customerDetails);
                                                }
                                            }
                                        } catch (Exception ex) {
                                            log.debug("Exception to store mac and username in cache: " + ex.getMessage());
                                            ex.printStackTrace();
                                        }
                                        if (coaProfileData != null) {
                                            RadiusPacket coaDMResponse = null;
                                             try {
                                                 coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, changeuserData.getUserName(), custRetrunData, strDMCoAIP);
                                                 log.warn("COA/DM response:"+coaDMResponse+":For Event:"+operation+":");
                                                 if(coaDMResponse!=null){
                                                     RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                     radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),operation, cltData.getMvnoId(),coaDMResponse);
                                                 }
                                                 else{
                                                     RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                     radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",operation, cltData.getMvnoId(),coaDMResponse);
                                                 }
                                             } catch (Exception ex) {
                                                 RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                 radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",operation, cltData.getMvnoId(),coaDMResponse);
                                                 log.error("CoA/DM Failed:" + ex.getMessage());
                                             }
                                        }
                                        else
                                            log.error("COA/DM Not Trigger as coaProfileData not found for event: " + operation);
                                    } else if (type.equalsIgnoreCase("Intiate")) {
                                        List<CoaDMProfileAttribute> authAttribute = new ArrayList<CoaDMProfileAttribute>();
                                        List<ClientReply> clientReplyAuthCoAItem = new ArrayList<ClientReply>();

                                        if (cltData != null & cltData.getClientGroupData() != null) {
                                            if (cltData.getClientGroupData().getClientReplyList() != null) {
                                                clientReplyAuthCoAItem = cltData.getClientGroupData().getClientReplyList();
                                            }
                                        }
                                        if (clientReplyAuthCoAItem != null) {
                                            log.debug("*********** Client Group Reply AUTH COA New*******************" + clientReplyAuthCoAItem.size());
                                            for (int i = 0; i < clientReplyAuthCoAItem.size(); i++) {
                                                ClientReply clientReplyData = clientReplyAuthCoAItem.get(i);
                                                if (clientReplyData.getType().equalsIgnoreCase("COA")) {
                                                    CoaDMProfileAttribute coaProData = new CoaDMProfileAttribute();
                                                    coaProData.setProfileAtt(clientReplyData.getAttributeValue());
                                                    coaProData.setRadiusAtt(clientReplyData.getAttribute());
                                                    authAttribute.add(coaProData);
                                                }
                                            }
                                        }
                                        coaProfileData.setCoaDMProfileAttributeList(authAttribute);
                                        if (coaProfileData != null) {
                                            RadiusPacket coaDMResponse =null;
                                            try {
                                                coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, changeuserData.getUserName(), custRetrunData, strDMCoAIP);
                                                log.warn("COA/DM response:"+coaDMResponse+":For Event:"+operation+":");
                                                if(coaDMResponse!=null){
                                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),operation, cltData.getMvnoId(),coaDMResponse);
                                                }
                                                else{
                                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",operation, cltData.getMvnoId(),coaDMResponse);
                                                }
                                            } catch (Exception ex) {
                                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",operation, cltData.getMvnoId(),coaDMResponse);
                                                log.error("CoA/DM Failed:" + ex.getMessage());
                                            }
                                        }else
                                            log.error("COA/DM Not Trigger as coaProfileData not found for event: " + operation);
                                    } else if (type.equalsIgnoreCase("Remove")) {
                                        RadiusPacket coaDMResponse =null;
                                        try {
                                            coaDMResponse = radiusUtility.initiateCoADM(PermanentProfileData, request, changeuserData.getUserName(), custRetrunData, strDMCoAIP);
                                            log.warn("COA/DM response:"+coaDMResponse+":For Event:"+operation+":");
                                            if(coaDMResponse!=null){
                                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),operation, cltData.getMvnoId(),coaDMResponse);
                                            }
                                            else{
                                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",operation, cltData.getMvnoId(),coaDMResponse);
                                            }
                                        } catch (Exception ex) {
                                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                            radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), "Error or Timeout",operation, cltData.getMvnoId(),coaDMResponse);
                                            log.error("CoA/DM Failed:" + ex.getMessage());
                                        }
                                    } else {
                                        RadiusPacket coaDMResponse =null;
                                        try {
                                            coaDMResponse = radiusUtility.initiateCoADM(dmProfileData, request, changeuserData.getUserName(), custRetrunData, strDMCoAIP);
                                            log.warn("COA/DM response:"+coaDMResponse+":For Event:"+operation+":");
                                            if(coaDMResponse!=null){
                                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),operation, cltData.getMvnoId(),coaDMResponse);
                                            }
                                            else{
                                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",operation, cltData.getMvnoId(),coaDMResponse);
                                            }
                                        } catch (Exception ex) {
                                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                            radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), "Error or Timeout",operation, cltData.getMvnoId(),coaDMResponse);
                                            log.error("CoA/DM Failed:" + ex.getMessage());
                                        }
                                    }
                                } else {
                                    log.debug("SNMP Profile captured");
                                    if (operation.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT)) {
                                        //Remove Mac From Cache if available
                                        //Remove Cache
                                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                        String strCalling = liveuser.getCallingStationId();
                                        if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                            log.debug("Cache remove on SNMP for mac: " + strCalling);
                                            cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                                        } else {
                                            log.info("No Cache found for Mac:" + strCalling);
                                        }
                                    }

                                    // trigger SNMP
                                    SNMPClientProfile snmpClientProfile = cltData.getSnmpClientProfile();
                                    if (snmpClientProfile != null) {
                                        log.info("SNMP Firing on:" + snmpClientProfile.getDestinationIp() + ":Key:" + cltData.getSharedKey() + ":Port:" + snmpClientProfile.getDestinationPort());
                                        radiusUtility.sendSNMP(liveuser.getAcctSessionId(), snmpClientProfile);
                                    } else {
                                        log.error("SNMP Profile Not found for client: " + cltData.getClientIpAddress());
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("CoA/DM Failed:" + e.getMessage());
                        }
                    }
                }
            } else {
                log.error("User not connected Skipping CoA/DM");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("CoA/DM Failed:" + e.getMessage());
        }
    }


    @Override
    public void triggerCOADMForSingleLiveSession(LiveUser liveuser, String event, String type, CustomerData custRetrunData) {
        try {
            DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
            CacheRetrival cacheRetrival = new CacheRetrival();
            RadiusUtility radiusUtility = new RadiusUtility();
            ValidateExpression validate = new ValidateExpression();
            log.debug("CoA/DM For User:" + liveuser.getUserName() + ":IP:" + liveuser.getSourceipaddress() + ":id:" + liveuser.getCdrID() + ":Type:" + type);
            String strDMCoAIP = liveuser.getSourceipaddress();

            AccountingRequest request = getAccountingRequest(liveuser);

            Client cltData = radiusUtility.identifyClient(liveuser.getSourceipaddress(), request);
            ClientGroup cltGroupData = null;
            Long CoAProfileId = 0L;
            Long DMProfileId = 0L;
            Long PermanentProfileId = 0L;

            if (cltData != null) {
                cltGroupData = cltData.getClientGroupData();
                if (cltGroupData != null && cltGroupData.getClientGroupId() != null) {
                    cltGroupData = cltData.getClientGroupData();
                    if (log.isDebugEnabled()) {
                        log.debug("For Client Group:" + cltData.getClientGroupId() + ":DATA:" + cltGroupData + ":mvnoid:" + cltData.getMvnoId());
                    }
                    CoAProfileId = cltGroupData.getCoaDMProfile();
                    DMProfileId = cltGroupData.getDMProfile();
                    if (cltGroupData.getPermanentDisconnectProfileId() != null)
                        PermanentProfileId = cltGroupData.getPermanentDisconnectProfileId();

                    log.debug("For Client Group COA:" + CoAProfileId + ":CoA/DM:" + DMProfileId);
                } else {
                    log.error("Client group configuration not found for request: " + request);
                    return;
                }
            } else {
                log.error("Client configuration not found for source-ip: " + liveuser.getSourceipaddress());
                return;
            }
            CoaDMProfile coaProfileData = null;
            CoaDMProfile dmProfileData = null;
            CoaDMProfile PermanentProfileData = null;


            try {
                List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
                log.debug("CoA Profile Cache size:" + coaDMProfileDataList.size());
                if (cltGroupData != null && !CollectionUtils.isEmpty(cltGroupData.getCoaDmProfileMappings())) {
                    CustomerData customerData = new CustomerData();
                    customerData.setUsername(liveuser.getUserName());
                    customerData.setFramedIPAddress(liveuser.getFramedIpAddress());
                    customerData.setNasIPAddress(liveuser.getNasIpAddress());
                    List<CoaDmProfileMapping> profileMappings = cltGroupData.getCoaDmProfileMappings().stream().sorted(Comparator.comparing(CoaDmProfileMapping::getPriority).reversed()).collect(Collectors.toList());
                    //COA Profile
                    List<CoaDmProfileMapping> coaProfileMappings = profileMappings.stream().collect(Collectors.toList());
                    boolean triggerSNMP = false;
                    for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                        log.info(String.format("Expression Check For %s:", profileMapping.getCheckItem()));
                        boolean response = validate.checkExpression(profileMapping.getCheckItem(), null, custRetrunData, event);
                        if (response) {
                            if (profileMapping.getCoaProfileId() != null) {
                                CoAProfileId = profileMapping.getCoaProfileId();
                                if (profileMapping.getDmProfileId() != null) {
                                    DMProfileId = profileMapping.getDmProfileId();
//                                            break;
                                }
                                break;
                            } else if (profileMapping.getDmProfileId() != null) {
                                DMProfileId = profileMapping.getDmProfileId();
                                if (profileMapping.getCoaProfileId() != null) {
                                    CoAProfileId = profileMapping.getCoaProfileId();
                                }
                                break;
                            } else if (profileMapping.getCoaDmSelection().equalsIgnoreCase("SNMP")) {
                                triggerSNMP = true;
                                break;
                            }
                        }
                    }
                    if (!triggerSNMP) {
                        if (dmProfileData == null && DMProfileId != null && DMProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        dmProfileData = coaDMProfileDataList.get(i);
                                        dmProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }

                        if (coaProfileData == null && CoAProfileId != null && CoAProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }

                        if (PermanentProfileData == null && PermanentProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (PermanentProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        PermanentProfileData = coaDMProfileDataList.get(i);
                                        PermanentProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }

                        if (coaProfileData == null) {
                            coaProfileData = dmProfileData;
                        }

                        if (PermanentProfileData == null) {
                            PermanentProfileData = dmProfileData;
                        }

                        log.debug("coaProfileData:" + coaProfileData + ":Dm profile Data:" + dmProfileData + ":Permangenet profile Data:" + PermanentProfileData);
                        if (event.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT)) {
                            //Remove Mac From Cache if available
                            RadiusAttribute radiusAttribute = request.getAttribute(RadiusAttributes.CALLING_STATION_ID.getName());
                            //Remove Cache
                            if (radiusAttribute != null) {
                                CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                String strCalling = radiusAttribute.getAttributeValue();
                                if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                    log.debug("Cache remove on DM for mac: " + strCalling);
                                    cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                                }
                            }
                        }
                        if (type.equalsIgnoreCase("CoA")) {
                            try {
                                if ((event != null && (event.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_SUSPEND) || event.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_ACTIVE)))
                                        && custRetrunData != null && custRetrunData.getMacAuthEnable() != null && !custRetrunData.getMacAuthEnable()) {
                                    //check faulty mac
                                    List<String> faultyMACS = cacheRetrival.getFaultyMacData();
                                    boolean faultyMacNotFound = true;
                                    if (!CollectionUtils.isEmpty(faultyMACS)) {
                                        String normalizedTargetMac = liveuser.getCallingStationId();
                                        if (normalizedTargetMac != null) {
                                            normalizedTargetMac = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
                                        }
                                        boolean isPresent = faultyMACS.stream()
                                                .map(radiusUtility::normalizeMacAddress) // Use instance method reference
                                                .anyMatch(normalizedTargetMac::equals);
                                        if (isPresent) {
//                                                    if (faultyMACS.contains(liveuser.getCallingStationId())) {
                                            faultyMacNotFound = false;
                                            log.error("Faulty Mac Found, So skipp Store Mac: " + liveuser.getCallingStationId());
                                        }
                                    }
                                    if (faultyMacNotFound) {
                                        //save mac in cache
                                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                        CustomerDetails customerDetails = new CustomerDetails(custRetrunData.getUsername(), liveuser.getCustid(), liveuser.getlClass());
                                        cacheService.put(radiusUtility.normalizeMacAddress(liveuser.getCallingStationId()), customerDetails);
                                        log.info("COA Cache for: " + customerDetails);
                                    }
                                }
                            } catch (Exception ex) {
                                log.debug("Exception to store mac and username in cache: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                            if (coaProfileData != null) {
                                RadiusPacket coaDMResponse = null;
                                try {
                                    coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, customerData.getUsername(), custRetrunData, strDMCoAIP);
                                    log.warn("COA/DM response:"+coaDMResponse+":For Event:"+event+":");
                                    if(coaDMResponse!=null){
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),event, cltData.getMvnoId(),coaDMResponse);
                                    }
                                    else{
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",event, cltData.getMvnoId(),coaDMResponse);
                                    }
                                } catch (Exception ex) {
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",event, cltData.getMvnoId(),coaDMResponse);
                                    log.error("CoA/DM Failed:" + ex.getMessage());
                                }
                            }
                            else
                                log.error("COA/DM Not Trigger as coaProfileData not found for event: " + event);
                        } else if (type.equalsIgnoreCase("Intiate")) {
                            List<CoaDMProfileAttribute> authAttribute = new ArrayList<CoaDMProfileAttribute>();
                            List<ClientReply> clientReplyAuthCoAItem = new ArrayList<ClientReply>();

                            if (cltData != null & cltData.getClientGroupData() != null) {
                                if (cltData.getClientGroupData().getClientReplyList() != null) {
                                    clientReplyAuthCoAItem = cltData.getClientGroupData().getClientReplyList();
                                }
                            }
                            if (clientReplyAuthCoAItem != null) {
                                log.debug("*********** Client Group Reply AUTH COA New*******************" + clientReplyAuthCoAItem.size());
                                for (int i = 0; i < clientReplyAuthCoAItem.size(); i++) {
                                    ClientReply clientReplyData = clientReplyAuthCoAItem.get(i);
                                    if (clientReplyData.getType().equalsIgnoreCase("COA")) {
                                        CoaDMProfileAttribute coaProData = new CoaDMProfileAttribute();
                                        coaProData.setProfileAtt(clientReplyData.getAttributeValue());
                                        coaProData.setRadiusAtt(clientReplyData.getAttribute());
                                        authAttribute.add(coaProData);
                                    }
                                }
                            }
                            coaProfileData.setCoaDMProfileAttributeList(authAttribute);
                            if (coaProfileData != null) {
                                RadiusPacket coaDMResponse =null;
                                try {
                                    coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, customerData.getUsername(), custRetrunData, strDMCoAIP);
                                    log.warn("COA/DM response:"+coaDMResponse+":For Event:"+event+":");
                                    if(coaDMResponse!=null){
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),event, cltData.getMvnoId(),coaDMResponse);
                                    }
                                    else{
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",event, cltData.getMvnoId(),coaDMResponse);
                                    }
                                } catch (Exception ex) {
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",event, cltData.getMvnoId(),coaDMResponse);
                                    log.error("CoA/DM Failed:" + ex.getMessage());
                                }
                            } else
                                log.error("COA/DM Not Trigger as coaProfileData not found for event: " + event);
                        } else if (type.equalsIgnoreCase("Remove")) {
                            RadiusPacket coaDMResponse = null;
                            try {
                                coaDMResponse = radiusUtility.initiateCoADM(PermanentProfileData, request, customerData.getUsername(), custRetrunData, strDMCoAIP);
                                log.warn("COA/DM response:"+coaDMResponse+":For Event:"+event+":");
                                if(coaDMResponse!=null){
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),event, cltData.getMvnoId(),coaDMResponse);
                                }
                                else{
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",event, cltData.getMvnoId(),coaDMResponse);
                                }
                            } catch (Exception ex) {
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(PermanentProfileData.getGateway(),request.getUserName(), "Error or Timeout",event, cltData.getMvnoId(),coaDMResponse);
                                log.error("CoA/DM Failed:" + ex.getMessage());
                            }
                        } else {
                            RadiusPacket coaDMResponse = null;
                            try {
                                coaDMResponse = radiusUtility.initiateCoADM(dmProfileData, request, customerData.getUsername(), custRetrunData, strDMCoAIP);
                                log.warn("COA/DM response:"+coaDMResponse+":For Event:"+event+":");
                                if(coaDMResponse!=null){
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),event, cltData.getMvnoId(),coaDMResponse);
                                }
                                else{
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",event, cltData.getMvnoId(),coaDMResponse);
                                }
                            } catch (Exception ex) {
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(dmProfileData.getGateway(),request.getUserName(), "Error or Timeout",event, cltData.getMvnoId(),coaDMResponse);
                                log.error("CoA/DM Failed:" + ex.getMessage());
                            }
                        }
                    } else {
                        log.debug("SNMP Profile captured");
                        if (event.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT)) {
                            //Remove Mac From Cache if available
                            //Remove Cache
                            CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                            String strCalling = liveuser.getCallingStationId();
                            if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                log.debug("Cache remove on SNMP for mac: " + strCalling);
                                cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                            } else {
                                log.info("No Cache found for Mac:" + strCalling);
                            }
                        }

                        // trigger SNMP
                        SNMPClientProfile snmpClientProfile = cltData.getSnmpClientProfile();
                        if (snmpClientProfile != null) {
                            log.info("SNMP Firing on:" + snmpClientProfile.getDestinationIp() + ":Key:" + cltData.getSharedKey() + ":Port:" + snmpClientProfile.getDestinationPort());
                            radiusUtility.sendSNMP(liveuser.getAcctSessionId(), snmpClientProfile);
                        } else {
                            log.error("SNMP Profile Not found for client: " + cltData.getClientIpAddress());
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                log.error("CoA/DM Failed:" + e.getMessage());
            }
        } catch (Exception ex) {
            log.error("Error To Trigger COADM for Event: " + event);
        }
    }

    private static AccountingRequest getAccountingRequest(LiveUser liveuser) {
        AccountingRequest request = new AccountingRequest();
        if (liveuser.getNasIpAddress() != null)
            request.addAttribute("NAS-IP-Address", liveuser.getNasIpAddress());
        if (liveuser.getAcctSessionId() != null)
            request.addAttribute("Acct-Session-Id", liveuser.getAcctSessionId());
        if (liveuser.getUserName() != null)
            request.addAttribute("User-Name", liveuser.getUserName());

        if (liveuser.getFramedIpAddress() != null && liveuser.getFramedIpAddress() != "") {
            request.addAttribute("Framed-IP-Address", liveuser.getFramedIpAddress());
        }

        if (liveuser.getLoginService() != null && liveuser.getLoginService() != "") {
            request.addAttribute("Login-Service", liveuser.getLoginService());
        }

        if (liveuser.getCallingStationId() != null && liveuser.getCallingStationId() != "") {
            request.addAttribute(RadiusAttributes.CALLING_STATION_ID.getName(), liveuser.getCallingStationId());
        }
        if (liveuser.getAcctSessionId() != null) {
            request.addAttribute("Acct-Session-Id", liveuser.getAcctSessionId());
        }

        if (liveuser.getNasPortId() != null) {
            request.addAttribute(RadiusAttributes.NAS_PORT_ID.getName(), liveuser.getNasPortId());
        }
        if (liveuser.getlClass() != null) {
            request.addAttribute(RadiusAttributes.CLASS.getName(), liveuser.getlClass());
        }
        if (liveuser.getNasIdentifier() != null) {
            request.addAttribute(RadiusAttributes.NAS_IDENTIFIER.getName(), liveuser.getNasIdentifier());
        }
        return request;
    }

    @Override
    public void updateWifiCustomerStatus(CustomMessage message) {

        try {

            if (message.getCustomerData() != null && message.getCustomerData().get(USER_NAME) != null && message.getCustomerData().get(CUSTOMER_STATUS) != null) {
                updateCustomerStatus(message.getCustomerData().get(USER_NAME).toString(), message.getCustomerData().get(CUSTOMER_STATUS).toString(), Integer.parseInt(message.getCustomerData().get(MVNO_ID).toString()));
            } else {
                throw new RuntimeException(INVALID_CUSTOMER_DATA);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private BooleanExpression checkDuplicateUser(String userName, Long customerId) {

        QCustomer qCustomer = QCustomer.customer;
        BooleanExpression boolExp = qCustomer.isNotNull();
        boolExp = boolExp.and(qCustomer.userName.eq(userName));

        if (customerId != null) {

            if (customerId != 0) {
                boolExp = boolExp.and(qCustomer.customerId.ne(customerId));
            }
        }

        return boolExp;
    }

    @Override
    public Customer validateLoginUser(LoginDto loginDto, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getUserName())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginDto.getPassword())) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Password is mandatory. Please enter valid password");
            } else {
                Optional<Customer> optionalCustomer = customerRepository.findByUserNameAndMvnoId(loginDto.getUserName(), mvnoId);
                if (!optionalCustomer.isPresent()) {
                    throw new IllegalArgumentException(RadiusConstants.NOT_FOUND + "No record found with user : '" + loginDto.getUserName() + "'. Please enter valid user name.");
                }
                if (!isPasswordMatched(loginDto.getPassword(), optionalCustomer.get().getPassword())) {
                    throw new IllegalArgumentException("Please enter valid customer Password.");
                }
//    				else if(!(optionalCustomer.get().getCustomerStatus()).equals(RadiusConstants.ACTIVE) && !(optionalCustomer.get().getCustomerStatus()).equals(RadiusConstants.IN_ACTIVE))
//    				{
//    					throw new IllegalArgumentException(RadiusConstants.EXPIRED_USER+"Please enter valid customer status. It should be '"+RadiusConstants.ACTIVE+"' or '"+RadiusConstants.IN_ACTIVE+"'");
//    				}
                else if ((optionalCustomer.get().getCustomerStatus()).equals(RadiusConstants.IN_ACTIVE)) {
                    throw new IllegalArgumentException("Your account is '" + RadiusConstants.IN_ACTIVE + "'");
                } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(optionalCustomer.get().getPlanType())) {
                    if ((optionalCustomer.get().getPlanType()).equals("Time")) {
                        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(optionalCustomer.get().getTimeBasedUnusedQuota())) {
                            throw new IllegalArgumentException(
                                    RadiusConstants.QUOTA_USED + "Your current quota is consumed and can not login");
                        } else {
                            Double timeQuota = Double.parseDouble((optionalCustomer.get().getTimeBasedUnusedQuota()).toString());
                            if (timeQuota <= 0) {
                                throw new IllegalArgumentException(
                                        RadiusConstants.QUOTA_USED + "Your current quota is consumed and can not login");
                            }
                        }
                    }

                    if ((optionalCustomer.get().getPlanType()).equals("Volume")) {
                        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(optionalCustomer.get().getVolumeBasedUnusedQuota())) {
                            throw new IllegalArgumentException(
                                    RadiusConstants.QUOTA_USED + "Your current quota is consumed and can not login");
                        } else {
                            Double volumeQuota = Double.parseDouble((optionalCustomer.get().getVolumeBasedUnusedQuota()).toString());
                            if (volumeQuota <= 0) {
                                throw new IllegalArgumentException(
                                        RadiusConstants.QUOTA_USED + "Your current quota is consumed and can not login");
                            }
                        }
                    }
                }
                if (optionalCustomer.get() != null && optionalCustomer.get().getEndDate() != null) {
                    if ((optionalCustomer.get().getEndDate()).isBefore(LocalDateTime.now())) {
                        throw new IllegalArgumentException(RadiusConstants.EXPIRED_USER + "Your account is expired, Please recharge.");
                    }
                }
//    		else if ((optionalCustomer.get().getEndDate()).isBefore(LocalDateTime.now())) {
//    		    throw new IllegalArgumentException(RadiusConstants.EXPIRED_USER + "User is expired");
//    		}
                sendLoginSuccessMessage(loginDto.getUserName(), loginDto.getPassword());
                return optionalCustomer.get();
            }
        } catch (RuntimeException e) {
            sendLoginFailureMessage(loginDto.getUserName(), loginDto.getPassword());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendLoginSuccessMessage(String userName, String password) {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(LOGIN_SUCCESS);
            if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                Optional<Customer> optionalCustomer = customerRepository.findByUserName(userName);
                // Set message in queue to send notification after successful login.
                CustomerMessage customerMessage = new CustomerMessage(optionalCustomer.get(), password, LOGIN_SUCCESS, optionalTemplate.get().getEmailTemplateData(), optionalTemplate.get().getSmsTemplateData(), optionalTemplate.get().getAppendUrl());
//		    LoginMessage loginMessage = new LoginMessage(optionalStaff.get(), LOGIN_SUCCESS, password,
//			    RabbitMqConstants.SOURCE_NAME_SAVBILL_WIFI);
                //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_LOGIN_SUCCESS);
                kafkaMessageSender.send(new KafkaMessageData(customerMessage, customerMessage.getClass().getSimpleName(), "LOGIN_SUCCESS"));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendLoginFailureMessage(String userName, String password) {
        try {
            Optional<Template> optionalTemplate = templateRepository.findByTemplateName(LOGIN_FAILURE);
            if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                Optional<Customer> optionalCustomer = customerRepository.findByUserName(userName);
                if (optionalCustomer.isPresent()) {
                    // Set message in queue to send notification after login failure.
//				LoginMessage loginMessage = new LoginMessage(optionalStaff.get(), LOGIN_FAILURE, password,
//					RabbitMqConstants.SOURCE_NAME_SAVBILL_COMMON);
                    CustomerMessage customerMessage = new CustomerMessage(optionalCustomer.get(), password, LOGIN_FAILURE, optionalTemplate.get().getEmailTemplateData(), optionalTemplate.get().getSmsTemplateData(), optionalTemplate.get().getAppendUrl());
                    //messageSender.send(customerMessage, RabbitMqConstants.QUEUE_LOGIN_FAILURE);
                    kafkaMessageSender.send(new KafkaMessageData(customerMessage, customerMessage.getClass().getSimpleName(), "LOGIN_FAILURE"));
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void validateLogoutUser(String userName) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "User name is mandatory. Please enter valid user name.");
            } else {
                Optional<Customer> optionalCustomer = customerRepository.findByUserName(userName);
                if (!optionalCustomer.isPresent()) {
                    throw new IllegalArgumentException("No record found with user : '" + userName + "'. Please enter valid user name.");
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveTimeBasePolicyMapping(List<CustomerTimeBasePolicyMapping> customerTimeBasePolicyMappings, Long custID) {
        try {
            if (customerTimeBasePolicyMappings != null) {
                for (CustomerTimeBasePolicyMapping entry : customerTimeBasePolicyMappings) {
                    CustomerTimeBasePolicyMapping details = new CustomerTimeBasePolicyMapping();
                    details.setFromTime(entry.getFromTime());
                    details.setFromDay(entry.getFromDay());
                    details.setToTime(entry.getToTime());
                    details.setCustomerId(custID);
                    details.setToDay(entry.getToDay());
                    details.setSpeed(entry.getSpeed());
                    details.setAccess(entry.getAccess());
                    customerTimBasePolicyMappingRepository.save(details);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveCustomerQosPolicyMapping(List<CustomerQosPolicyMapping> customerQosPolicyMappings, Long custID, Integer mvnoId) {
        try {
            if (customerQosPolicyMappings != null) {
                for (CustomerQosPolicyMapping qosPolicy : customerQosPolicyMappings) {
                    CustomerQosPolicyMapping policyMapping = new CustomerQosPolicyMapping();
                    policyMapping.setDownloadQos(qosPolicy.getDownloadQos());
                    policyMapping.setUploadQos(qosPolicy.getUploadQos());
                    policyMapping.setQosFrom(qosPolicy.getQosFrom());
                    policyMapping.setCustId(custID);
                    policyMapping.setQosTo(qosPolicy.getQosTo());
                    policyMapping.setMvnoId(mvnoId);
                    customerQosPolicyMappingRepository.save(policyMapping);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void deleteAllTimeBasePolicyMapping(Long customerId) {
        List<CustomerTimeBasePolicyMapping> list = customerTimBasePolicyMappingRepository.findCustomerTimeBasePolicyMappingByCustomerId(customerId);
        if (!list.isEmpty()) {
            customerTimBasePolicyMappingRepository.deleteAll(list);
        }
    }

    @Override
    public Customers updateCustomerEndDate(String endDate, String name, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(endDate)) {
                throw new IllegalArgumentException("End date is mandatory. Please enter valid end date.");
            } else if (!isValid(endDate, "yyyy-MM-dd HH:mm")) {
                throw new IllegalArgumentException("Invalid date format. Please enter valid end date in 'yyyy-MM-dd HH:mm' format.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                throw new IllegalArgumentException("Name is mandatory. Please enter valid customer name.");
            }

            Optional<Customers> optionalCustomer = customersRepository.findByUsernameAndMvnoId(name, mvnoId.intValue());
            if (!optionalCustomer.isPresent()) {
                throw new IllegalArgumentException("No record found for the customer to update the end date");
            } else {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(endDate);
                LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                Customers customer = optionalCustomer.get();
                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustid(customer.getId());
                for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
                    custPlanMappping.setEndDate(dateTime);
                    custPlanMappingRepository.save(custPlanMappping);
                }
                sendCustomerEnddateToCms(customer, endDate); /**Create new method to update enddate in cms**/
                return customersRepository.save(customer);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isValid(String dateStr, String dateFormat) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    @Override
    public void updateCustomerMacFromApiGTW(CustMacMappingMessage message) {
        // Add sleep to wait for customer save
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> data = message.getData();
        if (!CollectionUtils.isEmpty(data)) {
            MacAddressMapping macMapping = new MacAddressMapping();

            macMapping.setCreatedBy(message.getCurrentUser());
            if (message.getMessageDate() != null)
                macMapping.setCreateDate(new Timestamp(message.getMessageDate().getTime()));
            else
                macMapping.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
            macMapping.setCustomerId(Long.valueOf(data.get("custid").toString()));
            macMapping.setMacAddress(data.get("macAddress").toString());

            //New changes because of two same entity its getting error (Customer,Customers)

            QCustomers qCustomer = QCustomers.customers;
            BooleanExpression boolExp = qCustomer.isNotNull();
            if (Objects.nonNull(data.get("mvnoId"))) {
                Integer mvnoId = (Integer) data.get("mvnoId");
                boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(Integer.valueOf(data.get("mvnoId").toString())), 1));
            }
            boolExp = boolExp.and(qCustomer.username.eq(data.get("userName").toString()));
            Optional<Customers> customer = customersRepository.findOne(boolExp);
//            if (!customer.isPresent()) {
//                throw new IllegalArgumentException("No record found with customer name '" + data.get("userName") + "'. Please enter valid customer name");
//            }
            if (Objects.nonNull(data.get("custsermapid"))) {
                macMapping.setCustsermappingid((Integer) data.get("custsermapid"));
            }
            MacAddressMapping macAddressMappingVo = new MacAddressMapping();
            macAddressMappingVo.setMacAddress(macMapping.getMacAddress());
            macAddressMappingVo.setCustomerId(Long.valueOf(customer.get().getId()));
            macAddressMappingVo.setCreateDate(new Timestamp(new Date().getTime()));
            macAddressMappingVo.setLastModificationDate(new Timestamp(new Date().getTime()));
            macAddressMappingVo.setCreatedBy("Admin");
            macAddressMappingVo.setCustsermappingid(macMapping.getCustsermappingid());
            macAddressMappingVo.setNormalizeMac(normalizeMacAddress(macMapping.getMacAddress()));
            macAddressMappingRepository.save(macAddressMappingVo);
//            this.saveMacAddressMapping(Collections.singleton(macMapping), Long.valueOf(data.get("custid").toString()), Integer.valueOf(data.get("mvnoId").toString()));
            if (customer.isPresent()) {
                try {
                    terminateLiveUserSessionAfterMacUpdateOrDelete(macMapping.getMacAddress(), customer.get().getMvnoId(), customer.get().getUsername());
                } catch (Exception ex) {
                    log.error("Exception to trigger COA for mac update: " + ex.getMessage());
                }
            }
//            QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
//            List<MacAddressMapping> oldMac = IterableUtils.toList(macAddressMappingRepository.findAll(qMacAddressMapping.customerId.eq(customer.get().getId().longValue()).and(qMacAddressMapping.macAddress.eq(data.get("macAddress").toString()).or(qMacAddressMapping.custsermappingid.eq((Integer) data.get("custsermapid"))))));
//            if (oldMac.size() == 0) {
//                this.saveMacAddressMapping(new HashSet<MacAddressMapping>(oldMac), Long.valueOf(data.get("custid").toString()), Integer.valueOf(data.get("mvnoId").toString()));
//                oldMac.add(macMapping);
//            } else {
//                MacAddressMapping oldmacAddressMapping = macAddressMappingRepository.findByCustomerIdAndCustsermappingid(macMapping.getCustomerId().longValue(), macMapping.getCustsermappingid());
//                if (oldmacAddressMapping != null) {
//                    //macAddressMappingRepository.deleteById(oldmacAddressMapping.getMacAddressId());
//                    oldmacAddressMapping.setMacAddress(macMapping.getMacAddress());
//                    terminateLiveUserSessionAfterMacUpdateOrDelete(macMapping.getMacAddress(), customer.get().getMvnoId(), customer.get().getUsername());
//                    macAddressMappingRepository.save(oldmacAddressMapping);
//                }
//            }
        }
    }

    public void updateCustomerQuota(UpdateCustomerQuotaDto updateCustomerDto) {
        try {
            if (updateCustomerDto.getQuotaDetailId() != null) {
                CustQuotaDetails custQuotaDetail = custQuotaDetailsRepository.findById(updateCustomerDto.getQuotaDetailId()).orElse(null);
                Customers customers = customersRepository.findByCustomerId(custQuotaDetail.getCustid());
                Long liveSessioncount = liveUserService.countByCustId(String.valueOf(customers.getId()));
                log.debug("Update Customer quota method, liveSessioncount: " + liveSessioncount + " updateCustomerDto: " + updateCustomerDto.getUserName());
                custQuotaDetail.setLastQuotaReset(LocalDateTime.now());
                if (custQuotaDetail != null && custQuotaDetail.getCustid() != null && updateCustomerDto.getCustId() != null && custQuotaDetail.getCustid().equals(updateCustomerDto.getCustId())) {
                    custQuotaDetail.setCurrentSessionUsageVolume(0d);
                    custQuotaDetail.setCurrentSessionUsageTime(0d);
                    custQuotaDetail.setUsedQuota(0d);
                    custQuotaDetail.setUsedQuotaKB(0d);
                    custQuotaDetail.setTimeQuotaUsed(0d);
                    custQuotaDetail.setTimeUsedQuotaSec(0d);
                    CustomerQuotaInfo custQuotaInfo = new CustomerQuotaInfo();
                    custQuotaInfo.setMvnoId(Long.valueOf(customers.getMvnoId()));
                    custQuotaInfo.setCustId(customers.getId());
                    if (custQuotaDetail.getCustPlanMappping() != null)
                        custQuotaInfo.setCustpackageid(custQuotaDetail.getCustPlanMappping().getId().intValue());
                    custQuotaInfo.setUserName(customers.getUsername());
                    custQuotaInfo.setVolumeBasedUnusedQuota(0d);
                    custQuotaInfo.setVolumeBasedUsedQuota(0d);
                    custQuotaInfo.setVolumeBasedTotalQuota(custQuotaDetail.getTotalQuota());
                    custQuotaInfo.setTimeBasedTotalQuota(custQuotaDetail.getTimeTotalQuota());
                    custQuotaInfo.setTimeBasedUsedQuota(0d);
                    custQuotaInfo.setTimeBasedUnusedQuota(0d);
                    custQuotaInfo.setTimeBasedSessionUsedQuota(0d);
                    custQuotaInfo.setVolumeBasedSessionUsedQuota(0d);
                    if (liveSessioncount > 0 && updateCustomerDto.isSkipQuotaUpdate() && customers.getStatus().equalsIgnoreCase("Active")) {
//                        custQuotaDetail.setSkipQuotaUpdate(updateCustomerDto.isSkipQuotaUpdate());
                        custQuotaDetail.setIsQuotaUpdateSkipped(true);
                    } else {
                        custQuotaDetail.setIsQuotaUpdateSkipped(false);
                    }
                    addCustomerQuotaResetHistory(custQuotaDetail);
                    //Update same quota in CMS also
                    RadiusUtility radiusUtility = new RadiusUtility();
                    radiusUtility.SendUsedQotaInfo(custQuotaInfo);
                    custQuotaDetailsRepository.save(custQuotaDetail);
                    if (customers != null && !updateCustomerDto.isSkipQuotaUpdate()) {
                        log.debug("In updateCustomerQuota trigger COA/DM: " + updateCustomerDto.isSkipQuotaUpdate() + " customer: " + updateCustomerDto.getUserName());
                        terminateLiveUserSessionUsingUserName(customers.getUsername(), customers.getMvnoId());
                    }
                }
                MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
                log.debug("Radius Customer Quota updated successfully, customer: " + updateCustomerDto.getUserName());
            }

        } catch (Throwable e) {
            log.error("Error while updating radius customer Quota: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    public void addCustomerQuotaResetHistory(CustQuotaDetails custQuotaDetails) {
        try {
            CustQuotaResetDetails custQuotaResetDetails = new CustQuotaResetDetails();
            custQuotaResetDetails.setQuotaUnit(custQuotaDetails.getQuotaUnit());
            custQuotaResetDetails.setCustId(Long.valueOf(custQuotaDetails.getCustid()));
            custQuotaResetDetails.setCprId(custQuotaDetails.getCprId());
            //If current session is null or 0 then set original value
            if (custQuotaDetails.getCurrentSessionUsageVolume() != null)
                custQuotaResetDetails.setTotalQuotaUsed(custQuotaDetails.getUsedQuota() + custQuotaDetails.getCurrentSessionUsageVolume());
            else
                custQuotaResetDetails.setTotalQuotaUsed(custQuotaDetails.getUsedQuota());
            if (custQuotaDetails.getCurrentSessionUsageTime() != null)
                custQuotaResetDetails.setTotalTimeQuota(custQuotaDetails.getTimeQuotaUsed() + custQuotaDetails.getCurrentSessionUsageTime());
            else
                custQuotaResetDetails.setTotalTimeQuota(custQuotaDetails.getTimeQuotaUsed());

            custQuotaResetDetails = custQuotaResetDetailsRepository.save(custQuotaResetDetails);
            log.info("customer quota reset history add succefully: " + custQuotaResetDetails.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error to add customer quota history: " + ex.getMessage());
        }
    }

    @Override
    public void deleteCustomerMACFromApigateway(CustMacMappingMessage message) {
        Map<String, Object> data = message.getData();
        try {
            if (!CollectionUtils.isEmpty(data)) {
                Customers customers = null;
                List<String> mac = new ArrayList<>();
                if (Objects.nonNull(data.get("isMultipleDelete"))) {
                    boolean delete = (boolean) message.getData().get("isMultipleDelete");
                    if (delete) {
                        QCustomers qCustomer = QCustomers.customers;
                        BooleanExpression boolExp = qCustomer.isNotNull();
                        boolExp = boolExp.and(qCustomer.id.eq(Integer.parseInt(data.get("custid").toString())));
                        Optional<Customers> customer = customersRepository.findOne(boolExp);
                        if (Objects.isNull(customer)) {
                            throw new IllegalArgumentException("No record found with customer name '" + data.get("userName") + "'. Please enter valid customer name");
                        }
                        customers = customer.get();
                        QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
                        List<MacAddressMapping> macAddressMappings = (List<MacAddressMapping>) macAddressMappingRepository.findAll(qMacAddressMapping.customerId.eq(customer.get().getId().longValue()));
                        if (!CollectionUtils.isEmpty(macAddressMappings)) {
                            mac = macAddressMappings.stream().map(MacAddressMapping::getMacAddress).collect(Collectors.toList());
                        }
                        macAddressMappingRepository.deleteByCustomerId(customer.get().getId().longValue());
                    }
                } else {
                    QCustomers qCustomer = QCustomers.customers;
                    BooleanExpression boolExp = qCustomer.isNotNull();
                    if (Objects.nonNull(data.get("mvnoId"))) {
                        Integer mvnoId = (Integer) data.get("mvnoId");
                        boolExp = boolExp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(Integer.valueOf(data.get("mvnoId").toString())), 1));
                    }
                    boolExp = boolExp.and(qCustomer.username.eq(data.get("userName").toString()));
                    Optional<Customers> customer = customersRepository.findOne(boolExp);
                    customers = customer.get();
                    if (Objects.isNull(customer)) {
                        throw new IllegalArgumentException("No record found with customer name '" + data.get("userName") + "'. Please enter valid customer name");
                    }
                    QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
                    String macAddress = data.get("macAddress").toString();
                    mac = Collections.singletonList(macAddress);
                    macAddressMappingRepository.deleteInBatch(macAddressMappingRepository.findAll(qMacAddressMapping.customerId.eq(customer.get().getId().longValue()).and(qMacAddressMapping.macAddress.eq(macAddress))));
                }
                for (String m : mac) {
                    try {
                        terminateLiveUserSessionAfterMacUpdateOrDelete(m, customers.getMvnoId(), customers.getUsername());
                    } catch (Exception ex) {
                        log.error("Exception to trigger COA/DM for Delete Mac: " + ex.getMessage());
                    }
                }


            }
        } catch (Exception ex) {
            log.error("Exception to delte mac from CMS: " + ex.getMessage());
        }
    }

    public void terminateLiveUserSessionUsingUserName(String username, int mvnoId) throws SQLException {
        try {
            LiveUserSearchDTO liveUserSearchDTO = new LiveUserSearchDTO();
            liveUserSearchDTO.setUserName(username);
            liveUserSearchDTO.setPage(0);
            liveUserSearchDTO.setSize(0);
            Page<LiveUser> pageLiveUser = liveUserService.findLiveUsersUsingFilter(liveUserSearchDTO, null /*mvnoId*/);
            List<LiveUser> listLiveUser = new ArrayList<>(pageLiveUser.getContent());
            DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
            CustomerData custRetrunData = dbAuth.getDBCustomer(username, mvnoId, null, null, false);
            for (LiveUser liveUser : listLiveUser) {
                log.debug("Disconnect Live user: " + liveUser.getAcctSessionId() + " for username: " + username);
                if (custRetrunData != null) {
                    changeUserData changeuserData = new changeUserData(custRetrunData.getUsername(), Long.valueOf(custRetrunData.getMvnoId()));
                    List<changeUserData> userList = new ArrayList<changeUserData>();
                    userList.add(changeuserData);
                    CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.QUOTA_RESET); // As discusses for jira ANG-10246, On mac update or deleted Permanent Disconnection Profile should be trigger
                }
            }
        } catch (Exception ex) {
            log.error("Error on terminateLiveUserSessionUsingUserName: " + username + " ex: " + ex.getMessage());
        }
    }

    /**
     * Terminate Live user session if mac update or deleted
     *
     * @param macAddress
     * @param mvnoId
     * @throws SQLException
     */
    public void terminateLiveUserSessionAfterMacUpdateOrDelete(String macAddress, int mvnoId, String username) {
        try {
            List<LiveUser> liveUsers = liveUserService.findLiveUsersByMacAddress(macAddress);
            if (!CollectionUtils.isEmpty(liveUsers)) {
                RadiusUtility radiusUtility = new RadiusUtility();
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                CustomerData custRetrunData = dbAuth.getDBCustomer(username, mvnoId, null, null, false);
                for (LiveUser liveUser : liveUsers) {
                    log.info("Disconnect Live user: " + liveUser.getAcctSessionId() + " for mac: " + macAddress);
                    AccountingRequest request = getAccountingRequest(liveUser);
                    Client cltData = radiusUtility.identifyClient(liveUser.getNasIpAddress(), request);
                    String clientGroupId = liveUser.getClientGroupId();
                    //SUP-1674: For COA/DM client group not matched with check-item
                    if (clientGroupId != null && !CollectionUtils.isEmpty(cltData.getClientGroupMappings())) {
                        log.debug("Client Group Id available in Live User: " + liveUser.getAcctSessionId() + " ,id: " + clientGroupId);
                        Optional<ClientGroup> matchCltGroup = cltData.getClientGroupMappings().stream().filter(clientGroupMapping -> clientGroupMapping.getClientGroupId().toString().equalsIgnoreCase(clientGroupId)).findFirst().map(ClientGroupMapping::getClientGroupData);
                        if (matchCltGroup.isPresent()) {
                            log.debug("Client Group Matched for Live User: " + liveUser.getAcctSessionId() + " ,group name: " + matchCltGroup.get().getName());
                            cltData.setClientGroupId(matchCltGroup.get().getClientGroupId());
                            cltData.setClientGroupData(matchCltGroup.get());
                        } else {
                            log.debug("Invalid Client Group or client in Live User: " + liveUser.getAcctSessionId() + " : " + liveUser.getAcctMultiSessionId());
                        }
                    }

                    if (custRetrunData != null && cltData.getClientGroupData() != null && cltData.getClientGroupData().isTriggerCOADMOnMacRemove()) {
                        changeUserData changeuserData = new changeUserData(custRetrunData.getUsername(), Long.valueOf(custRetrunData.getMvnoId()));
                        List<changeUserData> userList = new ArrayList<changeUserData>();
                        userList.add(changeuserData);
                        CoADMSupport(userList, "CoA", custRetrunData, CommonConstants.EVENTCONSTANTS.MAC_REMOVE); // As discusses for jira ANG-10246, On mac update or deleted Permanent Disconnection Profile should be trigger
                    } else {
                        log.debug("Trigger COA DM On Mac Remove false");
                    }
                }
            } else {
                log.info("No Live user for mac: " + macAddress);
            }
        } catch (SQLException ex) {
            log.error("SQL Exception to terminate session for mac: " + macAddress + " exception: " + ex.getMessage());

        } catch (Exception ex) {
            log.error("Exception to terminate session for mac: " + macAddress + " exception: " + ex.getMessage());
        }
    }

    @Override
    public List<CustPlanMappping> findActiveByCustid(Integer id, LocalDateTime now) {
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression booleanExpression = qCustPlanMappping.custid.eq(id).and(qCustPlanMappping.startDate.before(now)).and(qCustPlanMappping.endDate.after(now));
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);
        custPlanMapppings.forEach(custPlanMappping -> {
            Integer planId = custPlanMappping.getPlanId();
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepository.findById(planId);
            if (postpaidPlan.isPresent()) {
                custPlanMappping.setPlanName(postpaidPlan.get().getName());
                custPlanMappping.setValidity(postpaidPlan.get().getValidity());
            }

        });
        return custPlanMapppings;
    }

    @Override
    public List<CustPlanMappping> findExpiredByCustid(Integer id, LocalDateTime now) {
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression booleanExpression = qCustPlanMappping.custid.eq(id).and(qCustPlanMappping.endDate.before(now));
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);
        custPlanMapppings.forEach(custPlanMappping -> {
            Integer planId = custPlanMappping.getPlanId();
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepository.findById(planId);
            if (postpaidPlan.isPresent()) {
                custPlanMappping.setPlanName(postpaidPlan.get().getName());
                custPlanMappping.setValidity(postpaidPlan.get().getValidity());
            }
            custPlanMappping.setCustPlanStatus("Expired");

        });
        return custPlanMapppings;
    }

    @Override
    public List<CustPlanMappping> findFutureByCustid(Integer id, LocalDateTime now) {
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression booleanExpression = qCustPlanMappping.custid.eq(id).and(qCustPlanMappping.startDate.after(now));
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);
        custPlanMapppings.forEach(custPlanMappping -> {
            Integer planId = custPlanMappping.getPlanId();
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepository.findById(planId);
            if (postpaidPlan.isPresent()) {
                custPlanMappping.setPlanName(postpaidPlan.get().getName());
                custPlanMappping.setValidity(postpaidPlan.get().getValidity());
            }

        });
        return custPlanMapppings;
    }

    public boolean isNotificationAppilicableToSend(Integer cprid, Double percentage) {
        Boolean flag = true;
        CustPlanMappingRepository custPlanMappingRepository1 = SpringContext.getBean(CustPlanMappingRepository.class);
        PlanUsagePercentageMappingService planUsagePercentageMappingService1 = SpringContext.getBean(PlanUsagePercentageMappingService.class);
        CustPlanMappping custPlanMappping = custPlanMappingRepository1.findByCprId(Long.valueOf(cprid));
        List<PlanUsagePercentageMapping> planUsagePercentageMappingList = planUsagePercentageMappingService1.findPlanUsageMappingByPlanId(custPlanMappping.getPlanId());
        if (!planUsagePercentageMappingList.isEmpty()) {
            Integer count = custPlanMappping.getNotificationLevel();
            count = (count == null || count == 0) ? 0 : count;
            PlanUsagePercentageMapping planUsagePercentageMapping = planUsagePercentageMappingService1.getPlanUsageMappinglevelBylevel(custPlanMappping.getPlanId(), count);
            if (planUsagePercentageMapping != null) {
                if (percentage >= planUsagePercentageMapping.getPercentage()) {
                    flag = true;
                    custPlanMappping.setNotificationLevel(count + 1);
                    custPlanMappingRepository1.save(custPlanMappping);
                } else {
                    flag = false;
                }
            } else {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public void sendCustQuotaDetailToApigw(Integer cprid, Double percentagequotaConsumed, Double totalQuota, Double usedQuota) {
        log.debug("enter in custquotadetailtoapigw");
        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
        radaysn.CustQuotaDetailUpdateProcess(cprid, percentagequotaConsumed, totalQuota, usedQuota, MessageConstants.QUEUE_SEND_QUOTA_FROM_RADIUS);
    }


    @Override
    public void sendReservedQuotaUpdateToAPIGateway(Integer cprid, boolean isChunkAvailable, double totalResrvedQuota) {
        log.debug("enter in sendReservedQuotaUpdateToAPIGateway");
        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
        radaysn.reservedQuotaUpdateProcess(totalResrvedQuota, isChunkAvailable, cprid, MessageConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS);
    }

    @Override
    public void sendCustQuotaIntrimDetailToApigw(Integer cprid, Double currentSessionUsageTime, Double currentSessionUsageVolume) {
        log.debug("enter in custquotadetailtoapigw");
        SendQuotaDTO sendQuotaDTO = new SendQuotaDTO();
        sendQuotaDTO.setCprId(cprid);
        sendQuotaDTO.setCurrentSessionUsageTime(currentSessionUsageTime);
        sendQuotaDTO.setCurrentSessionUsageVolume(currentSessionUsageVolume);
        log.debug("sendQuotaDTO" + sendQuotaDTO);
        SendQuotaIntrimMsg sendQuotaIntrimMsg = new SendQuotaIntrimMsg(sendQuotaDTO);
        Gson gson = new Gson();
        gson.toJson(sendQuotaIntrimMsg);
        KafkaMessageSender kafkaMessageSender1 = SpringContext.getBean(KafkaMessageSender.class);
        kafkaMessageSender1.send(new KafkaMessageData(sendQuotaIntrimMsg, sendQuotaIntrimMsg.getClass().getSimpleName()));
    }

    @Override
    public PageableResponse<Customers> findAllCustomerBySearch(Integer mvnoId, PaginationDTO paginationDTO, CustomerSearch customerSearch) {
        try {
            QCustomers qCustomer = QCustomers.customers;
            BooleanExpression exp = qCustomer.isNotNull();
            Page<Customers> customerPage = null;
            List<Long> custIdList = new ArrayList<>();

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.ASC, "username"));
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getUsername()) && !customerSearch.getUsername().equals("null")) {
//                exp = exp.and(qCustomer.userName.like("%" + customerSearch.getUserName() + "%"));
                exp = exp.and(qCustomer.username.contains(customerSearch.getUsername()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getName()) && !customerSearch.getName().equals("null")) {
                exp = exp.and(qCustomer.firstname.equalsIgnoreCase(customerSearch.getName()));
            }
            if ((ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getFullname()) && !customerSearch.getFullname().equals("null"))) {
                exp = exp.and(qCustomer.title.concat(" ").concat(qCustomer.firstname).concat(" ").concat(qCustomer.lastname).equalsIgnoreCase(customerSearch.getFullname()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getCustomerStatus()) && !customerSearch.getCustomerStatus().equals("null")) {
                exp = exp.and(qCustomer.status.equalsIgnoreCase(customerSearch.getCustomerStatus()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getEmail()) && !customerSearch.getEmail().equals("null")) {
                exp = exp.and(qCustomer.email.contains(customerSearch.getEmail()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getMobile()) && !customerSearch.getMobile().equals("null")) {
                exp = exp.and(qCustomer.mobile.eq(customerSearch.getMobile()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getParam1()) && !customerSearch.getParam1().equals("null")) {
                exp = exp.and(qCustomer.addparam1.contains(customerSearch.getParam1()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getParam2()) && !customerSearch.getParam2().equals("null")) {
                exp = exp.and(qCustomer.addparam2.contains(customerSearch.getParam2()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getParam3())) {
                exp = exp.and(qCustomer.addparam3.eq(customerSearch.getParam3()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getParam4())) {
                exp = exp.and(qCustomer.addparam4.eq(customerSearch.getParam4()));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getPlan())) {
                QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
                BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.name.containsIgnoreCase(customerSearch.getPlan()));
                List<PostpaidPlan> postpaidPlans = IterableUtils.toList(postpaidPlanRepository.findAll(booleanExpression));
                List<Integer> planIds = postpaidPlans.stream().map(PostpaidPlan::getId).collect(Collectors.toList());
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByPlanIdIn(planIds);
                List<Integer> custIds = custPlanMapppings.stream().map(CustPlanMappping::getCustid).collect(Collectors.toList());
                exp = exp.and(qCustomer.id.in(custIds));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getMacaddress()) && !customerSearch.getMacaddress().equals("null")) {
                QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
                BooleanExpression boolExp = qMacAddressMapping.isNotNull();
                boolExp = boolExp.and(qMacAddressMapping.macAddress.eq(customerSearch.getMacaddress()));
                List<MacAddressMapping> macAddressMappings = (List<MacAddressMapping>) macAddressMappingRepository.findAll(boolExp);
                Set<Long> customersId = macAddressMappings.stream().map(MacAddressMapping::getCustomerId).collect(Collectors.toSet());
                exp = exp.and(qCustomer.id.in(customersId.stream().map(Long::intValue).collect(Collectors.toList())));
            }
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerSearch.getFramedIpAddress())) {
                exp = exp.and(qCustomer.framedIp.eq(customerSearch.getFramedIpAddress()));
            }

            if (mvnoId != null && mvnoId == 1)
                customerPage = customersRepository.findAll(exp, pageable);
            else {
                exp = exp.and(qCustomer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                customerPage = customersRepository.findAll(exp, pageable);
            }
            PageableResponse<Customers> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<>(customerPage.getContent(), pageable, customerPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateCustomerConcurrency(UpdateCustomerDto customer) {
        Customers customers = customersRepository.findByCustomerId(customer.getCustId());
        if (customers != null) {
            customers.setMaxconcurrentsession(customer.getMaxconcurrentsession());
            customersRepository.save(customers);
            CustomerUpdateMessage customerUpdateMessage = new CustomerUpdateMessage(customers);
            //messageSender.send(customerUpdateMessage, RabbitMqConstants.QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS);
            kafkaMessageSender.send(new KafkaMessageData(customerUpdateMessage, customerUpdateMessage.getClass().getSimpleName(), KafkaConstant.UPDATE_CONCURRENCY));
        } else {
            throw new RuntimeException("Customer not found!");
        }
    }

    public void sendCustomerEnddateToCms(Customers customers, String endDate) {
        CustomerEndDateUpdateMessage customerEndDateUpdateMessage = new CustomerEndDateUpdateMessage(customers, endDate);
        // messageSender.send(customerEndDateUpdateMessage, RabbitMqConstants.QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS);
        kafkaMessageSender.send(new KafkaMessageData(customerEndDateUpdateMessage, customerEndDateUpdateMessage.getClass().getSimpleName(), KafkaConstant.CUSTOMER_ENDDATE));
    }


    @Override
    public void updateCustomerdata(UpdateCustomerShareDataMessage message) {
        try {
            Customers customer = customersRepository.findById(message.getId()).orElse(null);
            String original_username = customer.getUsername();
            boolean triggerCOA = true;
            // Set values from message to customer object
            if (customer != null) {
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                if (message.getStatus() != null) {
                    if (message.getStatus().equalsIgnoreCase(customer.getStatus())) {
                        triggerCOA = false;
                    }
                    customer.setStatus(message.getStatus());
                } else {
                    triggerCOA = false;
                }
                customer.setCustcategory(message.getCustcategory());
//                    customer.setMvnoId(message.getMvnoId());
                customer.setBuId(message.getBuId());
                customer.setIsDeleted(message.getIsDeleted());
                customer.setEmail(message.getEmail());
                customer.setContactperson(message.getContactperson());
                customer.setFramedIp(message.getFramedIp());
                customer.setFramedIpBind(message.getFramedIpBind());
                customer.setNasPort(message.getNasPort());
                customer.setIpPoolNameBind(message.getIpPoolNameBind());
                customer.setVLANID(message.getVlanId());
                customer.setFramedIpv6Address(message.getFramedIpv6Address());
                customer.setMaxconcurrentsession(message.getMaxconcurrentsession());
                customer.setDelegatedprefix(message.getDelegatedprefix());
                customer.setMac_provision(message.getMac_provision());
                customer.setMac_auth_enable(message.getMac_auth_enable());
                customer.setNasPortId(message.getNasPortId());
                customer.setMacRetentionPeriod(message.getMacRetentionPeriod());
                customer.setMacRetentionUnit(message.getMacRetentionUnit());
                customer.setSecondaryDNS(message.getSecondaryDNS());
                customer.setFramedIPNetmask(message.getFramedIPNetmask());
                customer.setFramedIPv6Prefix(message.getFramedIpv6Address());
                customer.setPrimaryDNS(message.getPrimaryDNS());
                customer.setPrimaryIPv6DNS(message.getPrimaryIPv6DNS());
                customer.setSecondaryIPv6DNS(message.getSecondaryIPv6DNS());
                customer.setMobile(message.getMobile());
                customer.setGatewayip(message.getGatewayIP());
                customer.setFramedroute(message.getFramedroute());
                customer.setAcctno(message.getAccountNo());
                customer.setBillday(message.getBillday());
                if (message.getNextbilldate() != null && !message.getNextbilldate().isEmpty()) {
                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    customer.setNextBillDate(LocalDate.parse(message.getNextbilldate(), pattern));
                }
                if (message.getQuotaResetDate() != null && !message.getQuotaResetDate().isEmpty()) {
                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    customer.setNextQuotaResetDate(LocalDate.parse(message.getQuotaResetDate(), pattern));
                }
                customer.setBillday(message.getBillday());
                if (message.getPassword() != null)
                    customer.setPassword(message.getPassword());

                if (message.getMac_auth_enable() != null)
                    customer.setMac_auth_enable(message.getMac_auth_enable());
                else
                    customer.setMac_auth_enable(true);
                // Save the customer using the repository
                try {
                    customersRepository.save(customer);
                } catch (Exception e) {
                    e.getMessage();
                }

                if (triggerCOA)
                    triggerCOADMoncustomerStatus(customer, original_username);

            }
        } catch (Exception e) {
            log.error("Error while updating Customer," + e.getMessage());
        }
    }

    @Override
    public String checkConcurrencyByCompare(String userName, String macAddress, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
                throw new IllegalArgumentException("Username is mandatory. Please enter valid user name.");
            }
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                QCustomers qCustomer = QCustomers.customers;
                BooleanExpression boolExp = qCustomer.isNotNull();
                if (mvnoId != 1)
                    boolExp = boolExp.and(qCustomer.mvnoId.in(mvnoId, 1));
                boolExp = boolExp.and(qCustomer.username.eq(userName));
                //boolExp = boolExp.and(qCustomer.customerStatus.eq(RadiusConstants.ACTIVE));
                Optional<Customers> optionalCustomer = customersRepository.findOne(boolExp);

                if (!optionalCustomer.isPresent()) {
                    throw new IllegalArgumentException("No record found for the user name: '" + userName + "'.");
                }

                if (ValidateCrudTransactionData.validateStringTypeFieldValue(macAddress)) {
                    String msg = "";
                    List<MacAddressMapping> macAddressMapping = macAddressMappingRepository
                            .findMacAddressMappingByCustomerId(Long.valueOf(optionalCustomer.get().getId()));
                    Set<String> macAddressesList = macAddressMapping.stream().map(MacAddressMapping::getMacAddress)
                            .collect(Collectors.toSet());

                    if (macAddress.contains(":")) {
                        macAddress = macAddress.replaceAll(":", "");
                    }
                    List<CustPlanMappping> mappping = custPlanMappingRepository.findAllByCustid(optionalCustomer.get().getId());
                    mappping = mappping.stream().filter(custPlanMappping -> custPlanMappping.getStartDate().isBefore(LocalDateTime.now()) && custPlanMappping.getEndDate().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                    if (macAddressesList.contains(macAddress)) {
                        return "User is Valid for Login";
                    } else {
                        if (optionalCustomer.get().getMaxconcurrentsession() != null
                                && optionalCustomer.get().getMaxconcurrentsession() != 0) {
                            int noOfconcurrentConnection = optionalCustomer.get().getMaxconcurrentsession();
                            if (noOfconcurrentConnection > macAddressesList.size()) {
                                msg = "User is Valid for Login";
                            } else {
                                throw new IllegalArgumentException("You have exceeded the device limit, Please contact to center manager");
                            }
                        } else if (!CollectionUtils.isEmpty(mappping)) {
                            Set<Integer> planIds = mappping.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toSet());
                            List<String> maxConcurrentSession = postpaidPlanRepository.getMaxconcurrentSessionByPlanIds(planIds);
                            if (!CollectionUtils.isEmpty(maxConcurrentSession)) {
                                int noOfconcurrentConnection = Integer.parseInt(maxConcurrentSession.get(0));
                                if (noOfconcurrentConnection > macAddressesList.size()) {
                                    msg = "User is Valid for Login";
                                } else {
                                    throw new IllegalArgumentException("You have exceeded the device limit, Please contact to center manager");
                                }
                            } else {
                                msg = "User is Valid for Login";
                            }
                        } else {
                            msg = "User is Valid for Login";
                        }
                        return msg;
                    }
                } else {
                    log.error("User is valid for login username: " + userName + ", but mac address not found");
                    return "Mac not found but User is Valid for Login!";
                }

            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable.getMessage());
        }
    }

    @Override
    public CustomerCreateData addNewCustomers(CustomerCreateData customerDto, Integer mvnoId, Boolean netconf) {
        try {
            Customers customers = new Customers(customerDto);
            Long custId = customersRepository.getNextCustomerId();
            if (custId == null)
                custId = 1L;
            customers.setId(custId.intValue());
            customers.setCafno(String.valueOf(custId));
            if (mvnoId != null)
                customers.setMvnoId(mvnoId);
            else
                customers.setMvnoId(2);
            customers = customersRepository.save(customers);
            if (!netconf) {
                customerDto.setCustId(customers.getId());
                customerDto.setMvnoId(customers.getMvnoId());
                //messageSender.send(customerDto, RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(customerDto, customerDto.getClass().getSimpleName(), "CUSTOMERS_CREATE_DATA_SHARE_RADIUS_MICROSERVICE"));
                // messageSender.send(customerDto, RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_RADIUS_MICROSERVICE);
            }
        } catch (Exception exception) {
            log.error("Error to create customer: " + customerDto.getUsername());
            throw new RuntimeException("Error while save customer: " + exception.getMessage());
        }
        return customerDto;
    }

    @Override
    public boolean customerUserNameExists(String userName, Integer mvnoId) {
        if (mvnoId != null)
            return customersRepository.existsByUsername(userName);
        else
            return customersRepository.existsByUsernameAndMvnoId(userName, mvnoId);
    }

    @Override
    public void deleteCustomers(Integer custid, Integer mvnoId, Boolean netconf) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {

            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(custid)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid custId.");
            }
            Customers customers = findCustomersByid(custid, mvnoId);
            customers.setId(custid);
            customers.setIsDeleted(true);
            customersRepository.save(customers);
            DeleteCustomerMessage message = new DeleteCustomerMessage();
            if (!netconf) {
                message.setCustid(custid);
                message.setMvnoId(mvnoId);
                //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_DELETE_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_DELETE"));

            }
            log.info("savbillnetconf Customer deleted succesfully: " + custid);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Error while deleting savbillnetconf customer: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public String updateCustomerStatus(Integer custId, String status, String remarks, Boolean netconf, String username) {
        Customers customers = customersRepository.findByCustomerId(custId);
        String original_username = customers.getUsername();
        boolean triggerCOA = true;
        if (customers != null) {
            if (status.equalsIgnoreCase(customers.getStatus())) {
                triggerCOA = false;
            }
            log.info("customer: " + customers.getUsername() + " status updateding from: " + customers.getStatus() + " to: " + status);
            if (status.equalsIgnoreCase("Terminate")) {
                customers.setUsername(username);
            }
            customers.setStatus(status);
            customers.setRemarks(remarks);
            customers = customerServiceHelper.saveCustomer(customers);
//            customersRepository.save(customers);
            CustomerStatusUpdateMessage message = new CustomerStatusUpdateMessage();
            if (!netconf) {
                message.setCustId(custId);
                message.setRemark(remarks);
                message.setStatus(status);
                //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_UPDATECUSTOMERSTATUS_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_UPDATECUSTOMERSTATUS"));

            }
            // Trigger COA/DM
            if (triggerCOA)
                triggerCOADMoncustomerStatus(customers, original_username);
        } else {
            throw new RuntimeException("Customer not found!");
        }
        return "Customer status updated succefully!";
    }

    public void triggerCOADMoncustomerStatus(Customers customer, String original_username) {
        try {
            if (!customer.getStatus().equalsIgnoreCase("Terminate")) {
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                CustomerData custRetrunData = dbAuth.getDBCustomer(null, customer.getMvnoId(), customer.getId().toString(), null, false);
                changeUserData changeUserData = new changeUserData(original_username,
                        Long.valueOf(customer.getMvnoId()));
                List<changeUserData> userList = new ArrayList<changeUserData>();
                userList.add(changeUserData);
                String event = null;
                if (customer.getStatus().equalsIgnoreCase("Active")) {
                    event = CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_ACTIVE;
                } else if (customer.getStatus().equalsIgnoreCase("Inactive")) {
                    event = CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_INACTIVE;
                } else if (customer.getStatus().equalsIgnoreCase("Suspend")) {
                    event = CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_SUSPEND;
                }
                if (event != null || !event.isEmpty()) {
                    CoADMSupport(userList, "COA", custRetrunData, event);
//                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
//                    radaysn.coaDMProcess(userList, "COA", custRetrunData, event);
                }
                //customerService.terminateUserSession(userList);
            } else {
                terminatSessionAfterCustomerStatusChange(customer, original_username);
            }
        } catch (Exception e) {
            log.error("Exception when terminate customer: " + customer.getUsername() + " status: "
                    + customer.getStatus());
        }
    }

    @Override
    public void terminatSessionAfterCustomerStatusChange(Customers customer, String originalUserName) {
        if (customer != null) {

            try {
                log.info("In COA/DM for terminate customer: " + customer.getUsername() + " id: " + customer.getId() + " originalUserName: " + originalUserName);
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                CustomerData custRetrunData = dbAuth.getDBCustomer(null, customer.getMvnoId(), String.valueOf(customer.getId()), null, false, true);
                if (custRetrunData != null) {
                    log.info("Customer Found to trigger COA/DM username: " + custRetrunData.getUsername());
                    changeUserData changeuserData = new changeUserData(originalUserName, Long.valueOf(custRetrunData.getMvnoId()));
                    List<changeUserData> userList = new ArrayList<changeUserData>();
                    userList.add(changeuserData);
                    CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_TERMINATE);
//                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
//                radaysn.coaDMProcess(userList, "Remove", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_STATUS_TERMINATE);
                } else {
                    log.info("Customer Not Found to trigger COA/DM username: " + customer.getUsername() + " originalUserName: " + originalUserName);
                }
            } catch (SQLException ex) {
                log.error("Exception while terminate Customer Status : " + ex.getMessage());
            } catch (Exception ex) {
                log.error("Exception while terminate Customer Status : " + ex.getMessage());
            }
        }
    }

    @Override
    public String updateCustomerStatusSoap(Integer custId, String status, String nasportId, String password, String remarks, Boolean netconf, String username) {
        Customers customers = customersRepository.findByCustomerId(custId);
        String original_username = customers.getUsername();
        boolean triggerCOA = true;
        if (customers != null) {
            if (status != null && status.equalsIgnoreCase(customers.getStatus())) {
                triggerCOA = false;
            }
            log.info("customer: " + customers.getUsername() + " status updateding from: " + customers.getStatus() + " to: " + status);

            if (status != null && !status.isEmpty()) {
                customers.setStatus(status);
                if (status.equalsIgnoreCase("Terminate")) {
                    customers.setUsername(username);
                }
            }
            customers.setRemarks(remarks);
//            if (nasportId != null && !nasportId.isEmpty()) {
            customers.setNasPortId(nasportId);
//            }
            if (password != null && !password.isEmpty()) {
                customers.setPassword(password);
            }
            customers = customerServiceHelper.saveCustomer(customers);
//            customersRepository.save(customers);
            CustomerStatusUpdateMessage message = new CustomerStatusUpdateMessage();
            if (!netconf) {
                message.setCustId(custId);
                message.setRemark(remarks);
                message.setStatus(status);
                //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_UPDATECUSTOMERSTATUS_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_UPDATECUSTOMERSTATUS"));

            }
            // Trigger COA/DM
            if (triggerCOA)
                triggerCOADMoncustomerStatus(customers, original_username);
        } else {
            throw new RuntimeException("Customer not found!");
        }
        return "Customer status updated succefully!";
    }

    @Override
    public CustomerCreateData defaultLeaseIPv4provision(CustomerCreateData customerCreateData, Boolean netconf) {
        try {
            Integer mvnoId = customerCreateData.getMvnoId();
            Customers customers = new Customers(customerCreateData);
            Customers customerByUsernameAndGatewayIP = customersRepository.findByUsernameAndGatewayIP(customers.getUsername(), customers.getGatewayip());
            if (customerByUsernameAndGatewayIP == null) {
                Long custId = customersRepository.getNextCustomerId();
                if (custId == null)
                    custId = 1L;

                customers.setId(custId.intValue());
                customers.setCafno(String.valueOf(custId));
                PostpaidPlan plan = postpaidPlanRepository.findAllByNameEqualsIgnoreCaseAndIsDeleteFalse(customerCreateData.getPlanName());
                List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
                if (Objects.nonNull(plan)) {
                    CustPlanMappping custPlanMappping = new CustPlanMappping();
                    if (custPlanMappingRepository.getNextCPRId() != null) {
                        custPlanMappping.setId(custPlanMappingRepository.getNextCPRId());
                    } else {
                        custPlanMappping.setId(1L);
                    }

                    custPlanMappping.setPlanId(plan.getId());
                    custPlanMappping.setValidity(plan.getValidity());
                    custPlanMappping.setCustid(custId.intValue());
                    custPlanMappping.setService(plan.getServiceName());
                    custPlanMappping.setCustPlanStatus("Active");
//                    custPlanMappping.setStatus("Active");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy h:mm a");
                    custPlanMappping.setExpiryDate(LocalDateTime.parse(customerCreateData.getEdate(), formatter));
                    custPlanMappping.setStartDate(LocalDateTime.now());
                    custPlanMappping.setIsDelete(false);
                    custPlanMappping.setQospolicyid(plan.getQosPolicyId().toString());
                    custPlanMappping.setUploadqos(plan.getUploadQOS());
                    custPlanMappping.setUploadts(plan.getUploadTs());
                    custPlanMappping.setEndDate(LocalDateTime.parse(customerCreateData.getEdate(), formatter));
                    custPlanMappping.setGraceDays(0);
                    custPlanMappping.setPurchaseType("New");
                    custPlanMappping.setOfferPrice(plan.getOfferprice());
                    custPlanMappping.setTaxAmount(plan.getTaxamount());
                    custPlanMappingRepository.save(custPlanMappping);
                    custPlanMappping.setQuotaList(getCustQuotaList(customers, plan, custPlanMappping));
                    custPlanMapppings.add(custPlanMappping);

                }
                if (mvnoId != null)
                    customers.setMvnoId(mvnoId);
                else
                    customers.setMvnoId(2);
                customers.setTitle("Mr");
                customers.setPlanMappingList(custPlanMapppings);
                customersRepository.save(customers);
                if (!netconf) {
                    customerCreateData.setMvnoId(mvnoId);
                    //messageSender.send(customerCreateData, RabbitMqConstants.QUEUE_CUSTOMERS_DEFOULTPROVISION_DATA_SHARE_RADIUS_MICROSERVICE);
                    kafkaMessageSender.send(new KafkaMessageData(customerCreateData, customerCreateData.getClass().getSimpleName(), "CUSTOMERS_DEFOULTPROVISION"));

                }
            } else {
                if (customerByUsernameAndGatewayIP.getIsDeleted()) {
                    customerByUsernameAndGatewayIP.setIsDeleted(false);
                    customersRepository.save(customerByUsernameAndGatewayIP);
                    if (!netconf) {
                        customerCreateData.setMvnoId(mvnoId);
                        //messageSender.send(customerCreateData, RabbitMqConstants.QUEUE_CUSTOMERS_DEFOULTPROVISION_DATA_SHARE_RADIUS_MICROSERVICE);
                        kafkaMessageSender.send(new KafkaMessageData(customerCreateData, customerCreateData.getClass().getSimpleName(), "CUSTOMERS_DEFOULTPROVISION"));
                    }
                } else {
                    throw new RuntimeException("Customer username allready exist");
                }
            }

        } catch (Exception exception) {
            log.error("Error to create customer: " + exception.getMessage());
            throw new RuntimeException("Error while save customer: " + exception.getMessage());
        }
        return customerCreateData;
    }


    @Override
    public Customers defaultLeaseIPv4Update(CustomerCreateData customer, String oldUsername, Boolean netconf) {
        try {
            Customers customersold = customersRepository.findByUsernameAndGatewayIP(oldUsername, customer.getGatewayIP());
            if (customersold != null) {
                customersold.setUsername(customer.getUsername());
                Customers customers = new Customers(customer);
                customers.setId(customersold.getId());
                customers.setMvnoId(customersold.getMvnoId());
                customers.setCafno(customersold.getCafno());
                customers.setBNGRouterinterface(customer.getBngrouterinterface());
                customers.setVLANID(customer.getVlanid());
                customers.setQOS(customer.getQos());
                List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustid(customersold.getId());
                custPlanMapppingList.stream().forEach(i -> {
                    i.setExpiryDate(LocalDateTime.now().minusSeconds(1));
                    i.setCustPlanStatus("Inactive");
                    i.setEndDate(LocalDateTime.now().minusSeconds(1));
                });
                custPlanMappingRepository.saveAll(custPlanMapppingList);
                custPlanMapppings.addAll(customersold.getPlanMappingList());
                if (Objects.nonNull(customer.getPlanName())) {
                    PostpaidPlan plan = postpaidPlanRepository.findAllByNameEqualsIgnoreCaseAndIsDeleteFalse(customer.getPlanName());
                    // &&
                    // !oldpalnmapping.stream().anyMatch(i->i.getPlanId().equals(plan.getId()))
                    if (Objects.nonNull(plan)) {
                        CustPlanMappping custPlanMappping = new CustPlanMappping();
                        if (custPlanMappingRepository.getNextCPRId() != null) {
                            custPlanMappping.setId(custPlanMappingRepository.getNextCPRId());
                        } else {
                            custPlanMappping.setId(1L);
                        }
                        LocalDateTime expirydate;
                        if (Objects.nonNull(customer.getEdate())) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy h:mm a");
                            expirydate = LocalDateTime.parse(customer.getEdate(), formatter);
                        } else {
                            expirydate = LocalDateTime.now().plusDays(plan.getValidity().longValue());
                        }
                        custPlanMappping.setPlanId(plan.getId());
                        custPlanMappping.setValidity(plan.getValidity());
                        custPlanMappping.setCustid(customersold.getId());
                        custPlanMappping.setService(plan.getServiceName());
                        custPlanMappping.setCustPlanStatus("Active");
//                    custPlanMappping.setStatus("Active");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy h:mm a");
                        custPlanMappping.setExpiryDate(LocalDateTime.parse(customer.getEdate(), formatter));
                        custPlanMappping.setStartDate(LocalDateTime.now());
                        custPlanMappping.setIsDelete(false);
                        custPlanMappping.setQospolicyid(plan.getQosPolicyId().toString());
                        custPlanMappping.setUploadqos(plan.getUploadQOS());
                        custPlanMappping.setUploadts(plan.getUploadTs());
                        custPlanMappping.setEndDate(LocalDateTime.now().plusDays(plan.getValidity().longValue()));
                        custPlanMappping.setGraceDays(0);
                        custPlanMappping.setPurchaseType("New");
                        custPlanMappping.setOfferPrice(plan.getOfferprice());
                        custPlanMappping.setTaxAmount(plan.getTaxamount());
                        custPlanMappingRepository.save(custPlanMappping);
                        custPlanMappping.setQuotaList(getCustQuotaList(customers, plan, custPlanMappping));
                        custPlanMapppings.add(custPlanMappping);
                        DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = expirydate.format(timeformatter);
                        CustomerPackageRelMessage customerPackageRelMessage = new CustomerPackageRelMessage();
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", custPlanMappping.getId().intValue());
                        data.put("endDate", formattedDate);
                        data.put("custid", customersold.getId());
                        data.put("expiryDate", formattedDate);
                        data.put("custPlanStatus", "Active");
                        data.put("operation", "Active");
                        customerPackageRelMessage.setData(data);
                        customerPackageRelMessage.setOperation("Active");
                        custPlanMappingService.Update(customerPackageRelMessage, customerPackageRelMessage.getOperation());
                    }
                }
                customers.setSkipnetconf(customer.getSkipnetconf());
                customers.setPlanMappingList(custPlanMapppings);
                customers.setGatewayip(customer.getGatewayIP());
                customers.setRemarks(customer.getRemarks());
                Customers customers1 = customersRepository.save(customers);
                DefaultUpdate message = new DefaultUpdate(customer, oldUsername);

                if (!netconf) {
                    //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_DEFOULTUPDATE_DATA_SHARE_RADIUS_MICROSERVICE);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_DEFOULTUPDATE"));

                }
                return customers1;
            } else {
                throw new RuntimeException("defoult lease IPv4 for update not available: " + customer.getUsername());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Exception while defaultLeaseIPv4Update " + customer.getUsername());
        }
    }

    @Override
    public void defoultDeprovision(String username, String gatewayIP, Boolean netconf) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(username)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid username.");
            }
            Customers customer = customersRepository.findByUsernameAndGatewayIP(username, gatewayIP);
            customer.setIsDeleted(true);
            customersRepository.save(customer);
            DeleteCustomerMessage message = new DeleteCustomerMessage();
            if (!netconf) {
                message.setUsername(customer.getUsername());
                message.setGatewayIpBind(customer.getGatewayip());
                message.setCustid(customer.getId());
                //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_DEFOULTDEPROVISION_DATA_SHARE_RADIUS_MICROSERVICE);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_DEFOULTDEPROVISION"));

            }
            log.info("savbillnetconf Customer deleted succesfully: " + username);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Error while deleting savbillnetconf customer: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public void customerDeactivationWhenMvnoIsInActive(MvnoStatusMessage mvnoStatusMessage) {
        if (!mvnoStatusMessage.getObjectList().isEmpty()) {
            customRepository.updateMvnoStatusForCustomer(mvnoStatusMessage.getObjectList(), mvnoStatusMessage.getStatus(), mvnoStatusMessage.getMvnoDeactivationFlag());
        }
    }


    @Override
    public void customerIpMappingUpdate(CustIPMessage custIPMessage) {

        try {
            List<CustIpMapping> oldCustIpMapping = new ArrayList<>();
            List<CustIpMapping> custIpMappingList = new ArrayList<>();

            custIpMappingList.addAll(custIPMessage.getCustIpMappingList());

            oldCustIpMapping = custIpMappingRepo.getAllByCustid(custIPMessage.getCustIpMappingList().get(0).getCustid());
            if (!oldCustIpMapping.isEmpty()) {
                custIpMappingRepo.deleteInBatch(oldCustIpMapping);
                custIpMappingRepo.saveAll(custIpMappingList);
                log.info("Customer IP mapping updated successfully");
            } else {
                custIpMappingRepo.saveAll(custIpMappingList);
                log.info("Customer IP mapping updated successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while updating cutomer ips: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


    }

    @Override
    public void customerIpMappingSave(CustIPMessage custIPMessage) {
        try {
            custIpMappingRepo.saveAll(custIPMessage.getCustIpMappingList());
            log.info("Customer Ip mapping saved sucessfully");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while saving cutomer ips: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void customerIpMappingDelete(CustIPMessage dataMessage) {
        try {
            if (dataMessage.isMultipleDelete()) {
                Integer custId = dataMessage.getCustIpMappingList().get(0).getCustid();
                List<CustIpMapping> mappingList = custIpMappingRepo.getAllByCustid(custId);
                custIpMappingRepo.deleteInBatch(mappingList);
            } else {
                Integer id = dataMessage.getCustIpMappingList().get(0).getId();
                CustIpMapping custIpMapping = custIpMappingRepo.findById(id).orElse(null);
                if (custIpMapping != null) {
                    custIpMappingRepo.deleteById(id);
                    log.info("Customer Ip " + custIpMapping.getIpAddress() + " mapping deleted sucessfully");
                } else {
                    log.info("No Ip found to perform delete activity");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while deleting cutomer ips: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateMvnoIdIsptoIsp(Integer oldMvno, Integer newMvno) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldMvno.longValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newMvno.longValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                customersRepository.UpdateMvnoidISP(oldMvno, newMvno);
                log.info("MVNO updated successfully " + oldMvno + " to " + newMvno);
            } else {
                log.error("Unable to update MVNO ID " + oldMvno);
            }
        } catch (Exception e) {
            log.error("Unexpected error while updating MVNO ID " + oldMvno + e);
        }
    }

    @Override
    public List<CustQuotaDetails> findAllByCustomersId(Integer custId) {
        List<CustQuotaDetails> custQuotaDetailsList = new ArrayList<>();
        try {
            custQuotaDetailsList = custQuotaDetailsRepository.findAllByCustidOrderByIdDesc(custId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while deleting cutomer ips: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return custQuotaDetailsList;
    }

    public PostpaidPlan findByPlanId(Integer planId) {
        PostpaidPlan postpaidPlan = new PostpaidPlan();
        try {
            postpaidPlan = postpaidPlanRepository.findById(planId).get();
        } catch (Exception e) {
            e.getMessage();
        }

        return postpaidPlan;
    }

    @Override
    public Boolean updateCustQuotaDetails(Integer custId, Long cprId) {
        Customers customers = customersRepository.findById(custId).orElse(null);
        if (customers != null) {
            if (cprId != null) {
                Optional<CustPlanMappping> mappping = custPlanMappingRepository.findById(cprId);
                if (mappping.isPresent()) {
                    return updateCustPlanQuota(mappping.get().getId());
                }
            } else {
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustid(customers.getId().intValue());
                if (!custPlanMapppings.isEmpty()) {
                    List<CustPlanMappping> activePlans = getActivePlansFromAllPlans(custPlanMapppings);
                    if (!activePlans.isEmpty()) {
                        for (CustPlanMappping custPlanMappping : activePlans) {
                            return updateCustPlanQuota(custPlanMappping.getId());
                        }
                    }
                }
            }

        }
        return false;
    }

    public boolean updateCustPlanQuota(Long cprId) {
        CustQuotaDetails custQuotaDetails = custQuotaDetailsRepository.findByCustPlanMapppingId(cprId);
        if (custQuotaDetails != null) {
            custQuotaDetails.setUsedQuota(Double.valueOf(0));
            custQuotaDetails.setCurrentSessionUsageVolume(Double.valueOf(0));
            custQuotaDetails.setCurrentSessionUsageTime(Double.valueOf(0));
            CustQuotaDetails savedCustQuotadetail = custQuotaDetailsRepository.save(custQuotaDetails);
            CustQuotaDetailMessage custQuotaDetailMessage = new CustQuotaDetailMessage(savedCustQuotadetail.getId(), savedCustQuotadetail.getCurrentSessionUsageVolume(), savedCustQuotadetail.getCurrentSessionUsageTime(), savedCustQuotadetail.getUsedQuota());
//            messageSender.send(custQuotaDetailMessage,RabbitMqConstants.QUEUE_SEND_ZERO_CUSTQUOTA_DATA_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(custQuotaDetailMessage, custQuotaDetailMessage.getClass().getSimpleName()));
            log.debug("CustqoutaDetails updated successfully and sent to CMS");
            return true;
        } else {
            return false;
        }
    }

    public List<CustPlanMappping> getActivePlansFromAllPlans(List<CustPlanMappping> custPlanMapppings) {
        List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
        for (CustPlanMappping custPlanMappping : custPlanMapppings) {
            if (custPlanMappping.getStartDate().isBefore(LocalDateTime.now()) || custPlanMappping.getStartDate().equals(LocalDateTime.now())) {
                if (custPlanMappping.getEndDate().isAfter(LocalDateTime.now()) || custPlanMappping.getEndDate().equals(LocalDateTime.now())) {
                    custPlanMapppingList.add(custPlanMappping);
                    log.debug("Active Plan found !!");
                }
            }
        }
        return custPlanMapppingList;
    }

    @Override
    public void deleteCustomers(DeleteCustomerMessage deleteCustomerMessage, Integer mvnoId, Boolean netconf) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            Optional<Customers> customer = customersRepository.findByUsernameAndGatewayipEqualsIgnoreCaseAndMvnoIdEquals(deleteCustomerMessage.getUsername(), deleteCustomerMessage.getGatewayIpBind(), mvnoId);
            if (customer.isPresent()) {
                if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(customer.get().getId())) {
                    throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid custId.");
                }
                customer.get().setIsDeleted(true);
                customersRepository.save(customer.get());
                DeleteCustomerMessage message = new DeleteCustomerMessage();
                if (!netconf) {
                    message.setCustid(customer.get().getId());
                    message.setMvnoId(mvnoId);
                    // messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMERS_DELETE_DATA_SHARE_RADIUS_MICROSERVICE);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName(), "CUSTOMERS_DELETE"));
                }
                log.info("savbillnetconf Customer deleted succesfully: " + customer.get().getId());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Error while deleting savbillnetconf customer: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private List<CustQuotaDetails> getCustQuotaList(Customers customers, PostpaidPlan plan, CustPlanMappping mappping) {
        List<CustQuotaDetails> quotaDetailsList = new ArrayList<>();
        CustQuotaDetails quotaDetails = new CustQuotaDetails();
        if (custQuotaDetailsRepository.getNextQuotaId() != null) {
            quotaDetails.setId(custQuotaDetailsRepository.getNextQuotaId());
        } else {
            quotaDetails.setId(1);
        }
        quotaDetails.setCustid(customers.getId());
        quotaDetails.setPlanId(plan.getId().longValue());
        quotaDetails.setIsDelete(false);
        quotaDetails.setCprId(mappping.getId());
        quotaDetails.setCustPlanMappping(mappping);
        quotaDetails.setTotalQuota(plan.getQuota().doubleValue());
        quotaDetails.setQuotaType(plan.getQuotatype());
        quotaDetails.setQuotaUnit(plan.getQuotaUnit());
        Double totalQuotaKb = null;
        if (plan.getQuotaUnit().equalsIgnoreCase("gb")) {
            totalQuotaKb = (double) (plan.getQuota() * 1024 * 1024);
        } else if (plan.getQuotaUnit().equalsIgnoreCase("mb")) {
            totalQuotaKb = (double) (plan.getQuota() * 1024);
        } else {
            totalQuotaKb = (double) (plan.getQuota());
        }
        //convert this kb
        quotaDetails.setTotalQuotaKB(totalQuotaKb);
        custQuotaDetailsRepository.save(quotaDetails);
        quotaDetailsList.add(quotaDetails);
        return quotaDetailsList;
    }

    public Map<String, Object> logoutCustomer(List<changeUserData> userList, LogoutCustomerDTO logoutCustomerDTO, CustomerData custRetrunData, String operation) {
        Map<String, Object> result = new HashMap<>();
        try {
            LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
            paginationDTO.setFramedIpAddress(logoutCustomerDTO.getFramedIP());
            paginationDTO.setCustId(logoutCustomerDTO.getCustId().toString());
            Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, logoutCustomerDTO.getMvnoId());
            List<LiveUser> liveUsersList = new ArrayList<>(liveUsersFromIp.getContent());

            if (!CollectionUtils.isEmpty(liveUsersList)) {
                LiveUser liveuser = liveUsersList.get(0);
                RadiusUtility radiusUtility = new RadiusUtility();
                AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                Client clientData = radiusUtility.identifyClient(liveuser.getSourceipaddress(), acctReq);
                Device device = new Device();
                if (clientData.getDeviceId() != null) {
                    device = deviceRepository.findById(clientData.getDeviceId()).orElse(null);
                }
                if (device != null && device.getDeviceProfileName() != null) {
                    if (device.getType().equalsIgnoreCase("http")) {
                        result.put("device", device);
                        result.put("status", "200");
                        return result;
                    } else if (device.getType().equalsIgnoreCase("COA")) {
                        CoADMSupport(userList, "COA", custRetrunData, operation);
                        result.put("status", "200");
                        result.put("msg", "Customer Logout successfully");
                    } else {
                        deviceService.generateSNMP(logoutCustomerDTO.getMvnoId(), logoutCustomerDTO.getUsername(), logoutCustomerDTO.getFramedIP(), false, true);
                        result.put("status", "200");
                        result.put("msg", "Customer Logout successfully");
                    }
                } else {
                    result.put("status", 417);
                    result.put("error", " Device not found!!");
                    return result;
                }
            } else {
                log.error("Live user not found for Framed Ip Address: " + liveUsersFromIp);
            }


        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", 500);
            result.put("errorMessage", "Error while logout customer");
            log.error("Error while logout customer: " + e.getMessage());
            return result;
        }
        return result;
    }

    public Customers findCustomerById(Integer custId) {
        return customersRepository.findByCustomerId(custId);
    }

    public String normalizeMacAddress(String macAddress) {
        if (macAddress != null)
            return macAddress.replace(":", "").replace("-", "").replace(".", "");
        return macAddress;
    }

    public String updateCustomerNextQuota(CustomerNextQuotaUpdateMessage dataMessage) {
        if(dataMessage.getCustId() != null){
            Customers customers = customersRepository.findByCustomerId(dataMessage.getCustId());
            if (customers != null) {
                log.info("customer: " + customers.getUsername() + " nextQuotaResetDate updating from: " + customers.getNextQuotaResetDate() + " to: " + dataMessage.getNextQuotaResetDate() );
                customers.setNextQuotaResetDate(dataMessage.getNextQuotaResetDate());
                customers.setNextBillDate(dataMessage.getNextBillDate());
                customersRepository.save(customers);
            } else {
                throw new RuntimeException("Customer not found!");
            }
        }
        return "Customer nextQuotaResetDate updated succefully!";
    }

    public String updatePlanWhileCafApproval(List<PlanUpdateCafApprovalMessage> planUpdateCafApprovalMessages) {
        if (planUpdateCafApprovalMessages == null || planUpdateCafApprovalMessages.isEmpty()) {
            log.warn("No plan update messages provided.");
            return "No data to process.";
        }
        int successCount = 0;
        for (PlanUpdateCafApprovalMessage dataMessage : planUpdateCafApprovalMessages) {
            try {
                if (dataMessage.getCprId() == null) {
                    log.error("Missing CPR ID in PlanUpdateCafApprovalMessage: {}", dataMessage);
                    continue;
                }
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findByCprId(dataMessage.getCprId().longValue());
                if (custPlanMappping == null) {
                    log.warn("No mapping found for CPR ID: {}", dataMessage.getCprId());
                    continue;
                }
                if (dataMessage.getStartDate() != null && dataMessage.getEndDate() != null && dataMessage.getExpiryDate() != null) {
                    custPlanMappping.setStartDate(dataMessage.getStartDate());
                    custPlanMappping.setEndDate(dataMessage.getEndDate());
                    custPlanMappping.setExpiryDate(dataMessage.getExpiryDate());
                    custPlanMappingRepository.save(custPlanMappping);
                    successCount++;
                } else {
                    log.warn("Incomplete dates for CPR ID: {}", dataMessage.getCprId());
                }
            } catch (Exception e) {
                log.error("Error updating plan for CPR ID {}: {}", dataMessage.getCprId(), e.getMessage(), e);
            }
        }
        return "Updated " + successCount + " customer plan(s) successfully.";
    }
}
