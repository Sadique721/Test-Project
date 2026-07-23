package com.savbill.revenuemanagement.core.entity.customers;


import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanRequestDto;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanService;
import com.savbill.revenuemanagement.autoassign.CustomerWalletPojo;
import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.StatusConstants;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.TrialDebitDocShowPojo;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.dto.customer.CustomerChangePlanDueAmountDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustomerDto;
//import com.savbill.revenuemanagement.core.exception.CustomValidationException;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrailDebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerAllInfoPojo;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import com.savbill.revenuemanagement.kafka.KafkaConstant;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.google.gson.Gson;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.dto.common.CustServiceMappingDTO;
import com.savbill.revenuemanagement.core.dto.common.PromiseToPayPojo;
import com.savbill.revenuemanagement.core.dto.common.PromiseToPayPojoInBulk;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.mapper.customer.CustPlanMapppingMapper;

import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.customeraddress.CustomerAddressService;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.mastermanagement.Area.domain.Area;
import com.savbill.revenuemanagement.mastermanagement.Area.repository.AreaRepository;
import com.savbill.revenuemanagement.mastermanagement.City.domain.City;
import com.savbill.revenuemanagement.mastermanagement.City.repository.CityRepository;
import com.savbill.revenuemanagement.mastermanagement.Country.domain.Country;
import com.savbill.revenuemanagement.mastermanagement.Country.repository.CountryRepository;
import com.savbill.revenuemanagement.mastermanagement.Pincode.domain.Pincode;
import com.savbill.revenuemanagement.mastermanagement.Pincode.repository.PincodeRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.revenuemanagement.mastermanagement.State.domian.State;
import com.savbill.revenuemanagement.mastermanagement.State.repository.StateRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.CustPlanMapppingPojo;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.service.PlanGroupService;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.PlanServiceRepository;

import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.persistence.EntityManager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SubscriberService extends AbstractService<Customers, CustomersPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);


    @Value("${max.wallet.difference.allowed}")
    private int maxWalletDiffAllowed;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    CustPlanMapppingMapper custPlanMapppingMapper;
    @Autowired
    CustomerAddressService customerAddressService;

    @Autowired
    CustomerAddressRepository customerAddressRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    CustPlanMapppingRepository custPlanMapppingRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private CustomerChargeDBRRepository customerChargeDBRRepository;

    //@Autowired
    //MessageSender messageSender;
    @Autowired
    private EntityManager em;
    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    PlanGroupRepository planGroupRepository;

    @Autowired
    CreditDocService creditDocService;
    @Autowired
    PincodeRepository pincodeRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    CityRepository cityRepository;

    @Autowired
    AreaRepository areaRepository;


    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    CustomerServiceMapRepository customerServiceMapRepository;

    @Autowired
    CustPlanMappingService custPlanMappingService;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    DebitDocDetailRepository debitDocDetailRepository;


    @Autowired
CustomerChargeHistoryRepository customerChargeHistoryRepository;

    @Autowired
    private CustomerDBRRepository customerDBRRepository;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    public CustomerDto getCustomerByAccountNo(String accountNo, Integer mvnoid) {
        List<Customers> customersList = customersRepository.findByAcctnoAndMvnoId(accountNo, mvnoid);
        if (!customersList.isEmpty() && customersList.size() > 1) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Duplicate Account found", null);
        } else if (!customersList.isEmpty() && customersList.size() > 0) {
            Customers customer = customersList.get(0);
            Double walletPrice = autoRenewOrAddonPlanService.checkWalletBalanceByCustId(customer.getId());
            return new CustomerDto(customer.getAcctno(), walletPrice, customer.getEmail(), customer.getId(), customer.getFirstname(), customer.getLastname());
        } else {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Account not found", null);
        }
    }

    public CustomerDto getCustomerByPhoneNumber(String phoneNumber) {
        List<Customers> customers = customersRepository.findByMobile(phoneNumber);
        if (customers.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Account not found", null);
        } else {
            Customers customer = customers.get(0);
            return new CustomerDto(customer.getAcctno(), customer.getWalletbalance(), customer.getEmail(), customer.getId());
        }
    }

    public CustomerDto getCustomerByAccountNoAndPhoneNumber(String accountNo, String phoneNumber) {
        Optional<Customers> optionalCustomer = customersRepository.findByAcctnoAndMobile(accountNo,phoneNumber);
        if (optionalCustomer.isPresent()) {
            Customers customer = optionalCustomer.get();
            return new CustomerDto(customer.getAcctno(), customer.getWalletbalance(), customer.getEmail(), customer.getId());
        } else {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Account not found", null);
        }
    }

    public Customers saveCustomersData(SaveCustomerDataShareMessage saveCustomerDataShareMessage) {
        logger.info("Initiated saveCustomerFromAPI received from CMS to save customer and create customer for : " + saveCustomerDataShareMessage.getUsername());
        Customers customers = new Customers();
        customers.setAcctno(saveCustomerDataShareMessage.getAccountNumber());
        customers.setIsUsingByThread(false);
        customers.setIstrialplan(saveCustomerDataShareMessage.getIstrialplan());
        customers.setId(saveCustomerDataShareMessage.getId());
        customers.setContactperson(saveCustomerDataShareMessage.getContactperson());
        customers.setTitle(saveCustomerDataShareMessage.getTitle());
        customers.setPan(saveCustomerDataShareMessage.getPan());
        customers.setGraceDay(saveCustomerDataShareMessage.getGraceDay());
        if(saveCustomerDataShareMessage.getDepartmentId() != null){
            customers.setDepartmentId(saveCustomerDataShareMessage.getDepartmentId());
        }
        if (saveCustomerDataShareMessage.getIsCaptiveportal()!=null && saveCustomerDataShareMessage.getIsCaptiveportal() && saveCustomerDataShareMessage.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID) ){
            String billdays = clientServiceRepository.findValueByNameAndMvnoId("early_bill_days",saveCustomerDataShareMessage.getMvnoId());
            saveCustomerDataShareMessage.setEarlybillday(Integer.valueOf(billdays));
        }
        if(saveCustomerDataShareMessage.getEarlybilldays() == null){
            customers.setEarlyBillDays(0);
        }else {
            customers.setEarlyBillDays(saveCustomerDataShareMessage.getEarlybilldays());
        }
        if(saveCustomerDataShareMessage.getEarlybillday() == null){
            customers.setEarlyBillDay(0);
        }else {
            customers.setEarlyBillDay(saveCustomerDataShareMessage.getEarlybillday());
        }
        customers.setEarlyBilldate(saveCustomerDataShareMessage.getEarlybilldate());
        if (saveCustomerDataShareMessage.getBillDay()!=null) {
            customers.setBillday(saveCustomerDataShareMessage.getBillDay());
        }
        if(!saveCustomerDataShareMessage.getNextbilldate().isEmpty())
            customers.setNextBillDate(LocalDate.parse(saveCustomerDataShareMessage.getNextbilldate()));

        customers.setServiceAreaId(saveCustomerDataShareMessage.getServiceAreaId().longValue());

        List<CustomerAddress> customerAddressList=new ArrayList<>();
        for(CustomerAddress address:saveCustomerDataShareMessage.getAddress())
        {
            CustomerAddress customerAddress=new CustomerAddress();
            customerAddress.setId(address.getId());
            customerAddress.setCustomer(customers);
            customerAddress.setLandmark(address.getLandmark());
            customerAddress.setAddressType(address.getAddressType());
            customerAddress.setAddress1(address.getAddress1());
            customerAddress.setAddress2(address.getAddress2());
            customerAddress.setCityId(address.getCityId());
            customerAddress.setCountryId(address.getCountryId());
            customerAddress.setAreaId(address.getAreaId());
            customerAddress.setStateId(address.getStateId());
            customerAddress.setPincodeId(address.getPincodeId());
            customerAddress.setVersion(address.getVersion());
            customerAddress.setIsDelete(address.getIsDelete());
            customerAddress.setStatus(address.getStatus());

            Country country= new Country();
            if (address.getCountryId() != null) {
                country = countryRepository.findById(address.getCountryId()).orElse(null);
                customerAddress.setCountry(country);
            }

            State state = new State();
            if  (address.getStateId() != null) {
                state = stateRepository.findById(address.getStateId()).orElse(null);
                customerAddress.setState(state);
            }

            City city = new City();
            if(address.getCityId() !=null){
                city=cityRepository.findById(address.getCityId()).orElse(null);
                customerAddress.setCity(city);
            }
            Pincode pincode = new Pincode();
            if(address.getPincodeId() != null) {
                pincode = pincodeRepository.findById(address.getPincodeId().longValue()).orElse(null);
                customerAddress.setPincode(pincode);
            }
            Area area = new Area();
            if(address.getAreaId() != null){
                area=areaRepository.findById(address.getAreaId().longValue()).orElse(null);
                customerAddress.setArea(area);
            }

            customerAddressList.add(customerAddress);
        }

        customers.setUsername(saveCustomerDataShareMessage.getUsername());
        customers.setPassword(saveCustomerDataShareMessage.getPassword());
        customers.setFirstname(saveCustomerDataShareMessage.getFirstname());
        customers.setLastname(saveCustomerDataShareMessage.getLastname());
        customers.setCustname(saveCustomerDataShareMessage.getCustname());
        customers.setEmail(saveCustomerDataShareMessage.getEmail());
        customers.setMobile(saveCustomerDataShareMessage.getMobile());
        customers.setDebitDocList(saveCustomerDataShareMessage.getDebitDocument());
        customers.setCurrency(saveCustomerDataShareMessage.getCurrency());
        customers.setBillDayUpdated(saveCustomerDataShareMessage.isBillDayUpdated());
        if(saveCustomerDataShareMessage.getPreviousBillday() != null){
            customers.setPreviousBillday(saveCustomerDataShareMessage.getPreviousBillday());
        }
        if(saveCustomerDataShareMessage.getCountryCode() != null && !saveCustomerDataShareMessage.getCountryCode().isEmpty())
            customers.setCountry(Integer.parseInt(saveCustomerDataShareMessage.getCountryCode()));
         customers.setStatus ( saveCustomerDataShareMessage.getStatus());
        customers.setCusttype ( saveCustomerDataShareMessage.getCusttype());
        customers.setPhone ( saveCustomerDataShareMessage.getPhone());
        customers.setMvnoId ( saveCustomerDataShareMessage.getMvnoId());
        customers.setBuId ( saveCustomerDataShareMessage.getBuId());
        customers.setLcoId ( saveCustomerDataShareMessage.getLcoId());
        customers.setIs_from_pwc ( saveCustomerDataShareMessage.getIs_from_pwc());
        customers.setIsDeleted ( saveCustomerDataShareMessage.getIsDeleted());
        customers.setOltportid ( saveCustomerDataShareMessage.getOltportid());
        customers.setOltslotid ( saveCustomerDataShareMessage.getOltslotid());
        customers.setFullName ( saveCustomerDataShareMessage.getFullName());
        customers.setIndiChargeList(saveCustomerDataShareMessage.getIndicustChargeDetails());
        customers.setCreatedByName(saveCustomerDataShareMessage.getCreatedByName());
        customers.setLastModifiedById(saveCustomerDataShareMessage.getLastModifiedById());
        customers.setLastModifiedByName(saveCustomerDataShareMessage.getLastModifiedByName());
        customers.setBlockNo(saveCustomerDataShareMessage.getBlockNo());
        customers.setCustomerVrn(saveCustomerDataShareMessage.getCustomerVrn());
        customers.setRenewPlanLimit(saveCustomerDataShareMessage.getRenewPlanLimit());
        customers.setCustomerNid(saveCustomerDataShareMessage.getCustomerNid());
        customers.setDrivingLicence(saveCustomerDataShareMessage.getDrivingLicence());
        customers.setPassportNo(saveCustomerDataShareMessage.getPassportNo());
        List<CustChargeDetails>  overChargeDetailsList=new ArrayList<>();
        if(saveCustomerDataShareMessage.getOverChargeList()!=null) {
            for (CustChargeDetails pojo : saveCustomerDataShareMessage.getOverChargeList()) {
                overChargeDetailsList.add(convertDTOToDomain(pojo));
            }
        }
        customers.setOverChargeList(overChargeDetailsList);

        List<CustChargeDetails> indirectChargeDetailsList = new ArrayList<>();
        if(saveCustomerDataShareMessage.getIndicustChargeDetails()!=null) {
            for (CustChargeDetails pojo : saveCustomerDataShareMessage.getIndicustChargeDetails()) {
                indirectChargeDetailsList.add(convertDTOToDomain(pojo));
            }
        }
        customers.setIndiChargeList(indirectChargeDetailsList);


        if(saveCustomerDataShareMessage.getParnterId() != null && saveCustomerDataShareMessage.getParnterId() != 1) {
            logger.info("Initiated Partner Save  for customer : " + saveCustomerDataShareMessage.getUsername());
            customers.setPartner(saveCustomerDataShareMessage.getParnterId());
            Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);
            if(partner != null) {
                if(partner.getNewCustomerCount()!=null)
                    partner.setNewCustomerCount(partner.getNewCustomerCount().longValue() +1);
                else
                    partner.setNewCustomerCount(0l);
                partnerRepository.save(partner);
                PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
                partnerAmountMessage.setPartnerId(customers.getPartner());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setNewCustomer_count(partner.getNewCustomerCount().intValue());
                if(partner.getRenewCustomerCount()!=null)
                    partnerAmountMessage.setRenewcust_count(partner.getRenewCustomerCount().intValue());
                else
                    partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
//                messageSender.send(partnerAmountMessage,SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
//                messageSender.send(partnerAmountMessage,SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
//                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),"BALANCE_DATA_API"));
            }
        } else {
            customers.setPartner(1);

        }
        customers.setCalendarType(saveCustomerDataShareMessage.getCalendarType());
        customers.setDunningCategory(saveCustomerDataShareMessage.getDunningCategory());
        Customers parentcust =null;
        if(saveCustomerDataShareMessage.getParentCustId() != null) {
            parentcust = customersRepository.findById(saveCustomerDataShareMessage.getParentCustId()).orElse(null);
            customers.setParentCustomers(parentcust);
        }
//        customers.setParentCustomersId ( saveCustomerDataShareMessage.getParentCustId()!=null ? customers.getParentCustomers().getId() : null);
        customers.setFeasibilityRequired ( saveCustomerDataShareMessage.getFeasibilityRequired());
        customers.setValleyType ( saveCustomerDataShareMessage.getValleyType());
        customers.setCustomerArea ( saveCustomerDataShareMessage.getCustomerArea());
        customers.setCustcategory ( saveCustomerDataShareMessage.getCustcategory());
        customers.setCreatedById ( saveCustomerDataShareMessage.getCreatedById());
        customers.setLastModifiedById ( saveCustomerDataShareMessage.getLastModifiedById());
        customers.setPopid( saveCustomerDataShareMessage.getPopId() != null ? saveCustomerDataShareMessage.getPopId() : 0);
        customers.setMasterdbid ( saveCustomerDataShareMessage.getMasterdbid());
        customers.setSplitterid ( saveCustomerDataShareMessage.getSplitterid());
        customers.setOltid(saveCustomerDataShareMessage.getOltId());
        customers.setFramedIp(saveCustomerDataShareMessage.getFramedIp());
        customers.setIpPoolNameBind(saveCustomerDataShareMessage.getIpPoolNameBind());
        customers.setNasPort(saveCustomerDataShareMessage.getNasPort());
        customers.setFramedIpBind(saveCustomerDataShareMessage.getFramedIpBind());
        List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();

        for(CustPlanMapppingPojo pojo : saveCustomerDataShareMessage.getCustPlanMapppingList()){
            custPlanMapppingList.add(convertDTOToDomain(pojo,customers));
        }
        customers.setPlanMappingList(custPlanMapppingList);
        List<CustomerServiceMapping> customerServiceMappingList = saveCustomerDataShareMessage.getCustomerServiceMappingList().stream()
                .map(item -> {
                    if (item.getDiscountExpiryDateString() != null) {
                        item.setDiscountExpiryDate(LocalDate.parse(item.getDiscountExpiryDateString()));
                    }
                    return item;
                })
                .collect(Collectors.toList());
        customers.setCustomerServiceMappingList(customerServiceMappingList);

        List<CustomerChargeHistory> customerChargeHistoryList = new ArrayList<>();
        logger.info("Initiated customerChargeHistoryList save process  customer  : " + saveCustomerDataShareMessage.getUsername());
        for (CustomerChargeHistory chc : saveCustomerDataShareMessage.getCustomerChargeHistories()) {
            customerChargeHistoryList.add(convertDTOToDomain(chc,customers,parentcust));
        }
        customerChargeHistoryList = customerChargeHistoryRepository.saveAll(customerChargeHistoryList);
        customers.setCustomerChargeHistories(customerChargeHistoryList);

        if (saveCustomerDataShareMessage.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
            Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customers.getId(),CommonConstants.CHARGE_TYPE_ADVANCE);
            LocalDate earlyBilldate ;
            if (customers.getEarlyBillDays()>0 && !(count >0)) {
                earlyBilldate = customers.getNextBillDate().minusDays(customers.getEarlyBillDays());
                if (earlyBilldate.isEqual(LocalDate.now()) || earlyBilldate.isAfter(LocalDate.now())) {
                    customers.setEarlyBilldate(earlyBilldate);
                }
            }
        }
       try {
           customers = customersRepository.saveAndFlush(customers);
           customerAddressRepository.saveAll(customerAddressList);
           //logger.info("Load test customers: "+customers.getId());
       }catch (Exception ex) {
           System.out.println("....message: "+ex.getMessage());
           logger.error("Load test Error which save customer: "+ex.getMessage());
       }
       return customers;

    }
    public void updateCustomersData(UpdateCustomerShareDataMessage message) {
        try {
            Customers customer  = customersRepository.findById(message.getId()).orElse(null);
            // Set values from message to customer object
            if (customer != null) {
                //customer = new Customers();
                // Set values from message to customer object
                customer.setId(message.getId());
                customer.setIsUsingByThread(false);
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                customer.setCustname(message.getFirstname() != null ? message.getFirstname() : "-");
                customer.setEmail(message.getEmail());
                customer.setMobile(message.getMobile());
                customer.setCountryCode(message.getCountryCode());
                customer.setServiceAreaId(message.getServiceAreaId().longValue());
                customer.setContactperson(message.getContactperson());
//                customer.setNe(message.getNetworkdevicesId());
                customer.setStatus(message.getStatus());
                customer.setCusttype(message.getCusttype());
                customer.setPhone(message.getPhone());
                customer.setMvnoId(message.getMvnoId());
                customer.setBuId(message.getBuId());
                customer.setLcoId(message.getLcoId());
                customer.setIs_from_pwc(message.getIs_from_pwc());
                customer.setIsDeleted(message.getIsDeleted());
                customer.setOltportid(message.getOltportid());
                customer.setOltslotid(message.getOltslotid());
                customer.setFullName(message.getFullName());
                customer.setPartner(message.getParnterId());
                customer.setCalendarType(message.getCalendarType());
                customer.setDunningCategory(message.getDunningCategory());
                customer.setFeasibilityRequired(message.getFeasibilityRequired());
                customer.setValleyType(message.getValleyType());
                customer.setCustomerArea(message.getCustomerArea());
                customer.setCustcategory(message.getCustcategory());
                customer.setBlockNo(message.getBlockNo());
                customer.setCustomerVrn(message.getCustomerVrn());
                customer.setRenewPlanLimit(message.getRenewPlanLimit());
                customer.setCustomerNid(message.getCustomerNid());
                customer.setDrivingLicence(message.getDrivingLicence());
                customer.setPassportNo(message.getPassportNo());
                if(message.getPan() != null){
                    customer.setPan(message.getPan());
                }
                if(message.getBillday() != null && (customer.getBillday() != message.getBillday())) {
                    if (message.getPreviousBillday() != null) {
                        customer.setPreviousBillday(message.getPreviousBillday());
                    }
                    if (message.getBillday() != null) {
                        customer.setBillday(message.getBillday());
                    }
                    customer.setBillDayUpdated(message.isBillDayUpdated());
                }

                // Save the customer using the repository
                try {
                    customersRepository.save(customer);
                }catch (Exception e){
                    e.getMessage();
                }

            }
        }catch (Exception e){
            logger.error("Error while updating Customer,"+e.getMessage());
        }

    }


    public CustPlanMappping convertDTOToDomain(CustPlanMapppingPojo custPlanMapppingPojo, Customers customers) {
        CustPlanMappping custPlanMappping = new CustPlanMappping();
        if (custPlanMapppingPojo != null) {
            custPlanMappping.setBillableCustomerId(custPlanMapppingPojo.getBillableCustomerId());
            custPlanMappping.setId(custPlanMapppingPojo.getId());
            custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            custPlanMappping.setStartDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, custPlanMapppingPojo.getStartDateString()));
            custPlanMappping.setEndDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, custPlanMapppingPojo.getEndDateString()));
            if (custPlanMapppingPojo.getExpiryDateString() != null)
                custPlanMappping.setExpiryDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, custPlanMapppingPojo.getExpiryDateString()));
            custPlanMappping.setStatus(custPlanMapppingPojo.getStatus());

            custPlanMappping.setUploadqos(custPlanMapppingPojo.getUploadqos());
            custPlanMappping.setUploadts(custPlanMapppingPojo.getUploadts());
            custPlanMappping.setDownloadqos(custPlanMapppingPojo.getDownloadqos());
            custPlanMappping.setDownloadts(custPlanMapppingPojo.getDownloadts());
            custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());
            custPlanMappping.setService(custPlanMapppingPojo.getService());
            custPlanMappping.setCustomer(customers);
            if (custPlanMapppingPojo.getPlangroupid()!=null){
                custPlanMappping.setPlanGroup(planGroupRepository.findById(custPlanMapppingPojo.getPlangroupid()).get());
                custPlanMappping.setRenewalId(custPlanMapppingPojo.getRenewalId());
            }

            custPlanMappping.setDiscount(custPlanMapppingPojo.getDiscount());
            custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
            custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
            custPlanMappping.setNewAmount(custPlanMapppingPojo.getNewAmount());
            custPlanMappping.setTaxAmount(custPlanMapppingPojo.getTaxAmount());
            custPlanMappping.setPurchaseFrom(custPlanMapppingPojo.getPurchaseFrom());
            custPlanMappping.setPurchaseType("New");
            custPlanMappping.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
            custPlanMappping.setOfferPrice(custPlanMapppingPojo.getOfferPrice());
            custPlanMappping.setIsHold(custPlanMapppingPojo.getIsHold());
//            if (custPlanMapppingPojo.getPlangroupid() != null) {
//                Optional<PlanGroup> plangroup = planGroupRepository.findById(custPlanMapppingPojo.getPlangroupid());
//                if (plangroup.isPresent()) custPlanMappping.setPlanGroup(plangroup.get());
//            }
            custPlanMappping.setPlanValidityDays(custPlanMapppingPojo.getPlanValidityDays());
            custPlanMappping.setCustRefName(custPlanMapppingPojo.getCustRefName());
            custPlanMappping.setIsinvoicestop(custPlanMapppingPojo.getIsinvoicestop());
            custPlanMappping.setIstrialplan(custPlanMapppingPojo.getIstrialplan());
            if (custPlanMappping.getIstrialplan()) {
                custPlanMappping.setIsTrialValidityDays(custPlanMapppingPojo.getIsTrialValidityDays());
                custPlanMappping.setTrialPlanValidityCount(custPlanMapppingPojo.getTrialPlanValidityCount());
            } else if (!custPlanMappping.getIstrialplan()) {
                custPlanMappping.setIsTrialValidityDays(0.0);
                custPlanMappping.setTrialPlanValidityCount(0);


            }
            custPlanMappping.setIsInvoiceCreated(custPlanMapppingPojo.getIsInvoiceCreated());
            if (custPlanMapppingPojo.getCustServiceMappingId() != null) {
                custPlanMappping.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());
            }
            if (custPlanMapppingPojo.getInvoiceType() != null) {
                custPlanMappping.setInvoiceType(custPlanMapppingPojo.getInvoiceType());
            }
            if (custPlanMapppingPojo.getTraildebitdocid() != null) {
                custPlanMappping.setTraildebitdocid(custPlanMapppingPojo.getTraildebitdocid());
            }
            if (custPlanMapppingPojo.getRenewalId() != null) {
                custPlanMappping.setRenewalId(custPlanMapppingPojo.getRenewalId());
            }

            custPlanMappping.setIsContainsCustomerInvoice(custPlanMapppingPojo.getIsContainsCustomerInvoice());
            custPlanMappping.setCustomerCpr(custPlanMapppingPojo.getCustomerCpr());
        }
        return custPlanMappping;
    }


    public CustomerChargeHistory convertDTOToDomain(CustomerChargeHistory customerChargeHistory,Customers customers,Customers parentcust) {

        try {
            CustomerChargeHistory customerChargeHistory1 = new CustomerChargeHistory();
            customerChargeHistory1.setId(customerChargeHistory.getId());
            customerChargeHistory1.setCustomerId(customerChargeHistory.getCustomerId());
            customerChargeHistory1.setPlanId(customerChargeHistory.getPlanId());
            customerChargeHistory1.setChargeId(customerChargeHistory.getChargeId());
            customerChargeHistory1.setTaxId(customerChargeHistory.getTaxId());
            customerChargeHistory1.setChargeAmount(customerChargeHistory.getChargeAmount());
            customerChargeHistory1.setTaxAmount(customerChargeHistory.getTaxAmount());
            if(customerChargeHistory.getDiscount() != null)
                customerChargeHistory1.setDiscount(customerChargeHistory.getDiscount());
            else
                customerChargeHistory1.setDiscount(0d);
            customerChargeHistory1.setCustPlanMapppingId(customerChargeHistory.getCustPlanMapppingId());
            customerChargeHistory1.setPlanGroupId(customerChargeHistory.getPlanGroupId());
            customerChargeHistory1.setPlanName(customerChargeHistory.getPlanName());
            customerChargeHistory1.setChargeName(customerChargeHistory.getChargeName());
            customerChargeHistory1.setCharge_desc(customerChargeHistory.getCharge_desc());
            customerChargeHistory1.setChargeType(customerChargeHistory.getChargeType());
            customerChargeHistory1.setBillingCycle(customerChargeHistory.getBillingCycle());
            customerChargeHistory1.setSaccode(customerChargeHistory.getSaccode());
            if (customers.getCustomerServiceMappingList().get(0).getDiscount()!=null) {
                customerChargeHistory1.setDiscountExpDate(customers.getCustomerServiceMappingList().get(0).getDiscount() > 0 ? LocalDate.now() : null);
            }
            if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                LocalDate nextBillDate;
                LocalDate today = LocalDate.now();
                int billingCycle = customerChargeHistory.getBillingCycle();
                int billDay = customers.getBillday();
                if (today.getDayOfMonth() < billDay && billingCycle==1) {
                    nextBillDate = today.withDayOfMonth(billDay);
                } else {
                    nextBillDate = today.plusMonths(billingCycle).withDayOfMonth(billDay);
                }

                if (customerChargeHistory.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)) {
                    customerChargeHistory1.setLastBillDate(LocalDate.now());
                    customerChargeHistory1.setNextBillDate(nextBillDate);
                    if (customers.getCustomerServiceMappingList().get(0).getDiscount()!=null) {
                        customerChargeHistory1.setDiscountExpDate(customers.getCustomerServiceMappingList().get(0).getDiscount() !=null ? LocalDate.now() : null);
                    }
                }
                if (customerChargeHistory.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_RECURRING)) {
                    if(parentcust!=null){
                        nextBillDate = nextBillDate.isAfter(parentcust.getNextBillDate())?parentcust.getNextBillDate():nextBillDate;
                    }
                    customerChargeHistory1.setNextBillDate(nextBillDate);
                    if (customers.getCustomerServiceMappingList().get(0).getDiscount()!=null) {
                        customerChargeHistory1.setDiscountExpDate(customers.getCustomerServiceMappingList().get(0).getDiscount() !=null ? nextBillDate : null);
                    }
                }
                if (customerChargeHistory.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)) {
                    customerChargeHistory1.setLastBillDate(LocalDate.now());
                    customerChargeHistory1.setNextBillDate(customers.getNextBillDate());
                }
            }else {
                if (customers.getCustomerServiceMappingList().get(0).getDiscount()!=null) {
                    customerChargeHistory1.setDiscountExpDate(customers.getCustomerServiceMappingList().get(0).getDiscount() !=null  ? LocalDate.now() : null);
                }
            }
            if (customerChargeHistory.getCustomerBillDay() != null) {
                customerChargeHistory1.setCustomerBillDay(customerChargeHistory.getCustomerBillDay());
            }
            customerChargeHistory1.setIsFirstChargeApply(customerChargeHistory.getIsFirstChargeApply());
            customerChargeHistory1.setIsRoyaltyApply(customerChargeHistory.getIsRoyaltyApply());


            return customerChargeHistory1;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  CustChargeDetails convertDTOToDomain(CustChargeDetails custChargeDetails){

        try{
                CustChargeDetails custChargeDetailsSave = new CustChargeDetails();
                custChargeDetailsSave.setBillTo(custChargeDetails.getBillTo());
                custChargeDetailsSave.setId(custChargeDetails.getId());
                custChargeDetailsSave.setBillableCustomerId(custChargeDetails.getBillableCustomerId());
                custChargeDetailsSave.setBillTo(custChargeDetails.getBillTo());
                custChargeDetailsSave.setActualprice(custChargeDetails.getActualprice());
                custChargeDetailsSave.setChargeid(custChargeDetails.getChargeid());
                custChargeDetailsSave.setChargetype(custChargeDetails.getChargetype());
                custChargeDetailsSave.setBillingCycle(custChargeDetails.getBillingCycle());
                custChargeDetailsSave.setConnection_no(custChargeDetails.getConnection_no());
                custChargeDetailsSave.setCustPlanMapppingId(custChargeDetails.getCustPlanMapppingId());
                custChargeDetailsSave.setIsDeleted(custChargeDetails.getIsDeleted());
                custChargeDetailsSave.setDbr(custChargeDetails.getDbr());
                if(custChargeDetailsSave.getDiscount() >= 0)
                    custChargeDetailsSave.setDiscount(custChargeDetailsSave.getDiscount());
                else
                    custChargeDetailsSave.setDiscount(0d);
                custChargeDetailsSave.setDiscount(custChargeDetails.getDiscount());
                custChargeDetailsSave.setIppooldtlsid(custChargeDetails.getIppooldtlsid());
                custChargeDetailsSave.setIs_reversed(custChargeDetails.getIs_reversed());
                custChargeDetailsSave.setIsInvoiceToOrg(custChargeDetails.getIsInvoiceToOrg());
                custChargeDetailsSave.setIsUsed(custChargeDetails.getIsUsed());
                custChargeDetailsSave.setLastBillDate(LocalDate.parse(custChargeDetails.getLastBillDate().toString()));
                custChargeDetailsSave.setNextBillDate(LocalDate.parse(custChargeDetails.getNextBillDate().toString()));
                custChargeDetailsSave.setNewAmount(custChargeDetails.getNewAmount());
                custChargeDetailsSave.setPlanid(custChargeDetails.getPlanid());
                custChargeDetailsSave.setLastModifiedById(custChargeDetails.getLastModifiedById());
                custChargeDetailsSave.setCreatedByName(custChargeDetails.getCreatedByName());
                custChargeDetailsSave.setValidity(custChargeDetails.getValidity());
                custChargeDetailsSave.setPrice(custChargeDetails.getPrice());
                return  custChargeDetailsSave;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public CustomersPojo convertCustomersModelToCustomersPojo(Customers customer) throws Exception {
       // String SUBMODULE = MODULE + " [convertCustomersModelToCustomersPojo()] ";
        CustomersPojo pojo = null;
        try {
            if (customer != null) {
                pojo = new CustomersPojo();
                pojo.setId(customer.getId());
                pojo.setUsername(customer.getUsername());
                pojo.setPassword(customer.getPassword());
                pojo.setFirstname(customer.getFirstname());
                pojo.setLastname(customer.getLastname());
                pojo.setEmail(customer.getEmail());
                pojo.setStatus(customer.getStatus());
                pojo.setFailcount(customer.getFailcount());
                pojo.setAcctno(customer.getAcctno());
                pojo.setCusttype(customer.getCusttype());
                pojo.setPhone(customer.getPhone());
                pojo.setBillday(customer.getBillday());
                pojo.setPartnerid(customer.getPartner());
                pojo.setNextBillDate(customer.getNextBillDate());
                pojo.setLastBillDate(customer.getLastBillDate());
                pojo.setOutstanding(customer.getOutstanding());
                pojo.setAddresstype(customer.getAddresstype());
                pojo.setAddress1(customer.getAddress1());
                pojo.setAddress2(customer.getAddress2());
                pojo.setCity(customer.getCity());
                pojo.setState(customer.getState());
                pojo.setCountry(customer.getCountry());
                pojo.setPincode(customer.getPincode());
                pojo.setArea(customer.getArea());
                pojo.setLast_password_change(customer.getLast_password_change());
//                pojo.setAddressList(custAddressService.convertResponseModelIntoPojo(customer.getAddressList()));
                pojo.setOldpassword1(customer.getOldpassword1());
                pojo.setOldpassword2(customer.getOldpassword2());
                pojo.setOldpassword3(customer.getOldpassword3());
                pojo.setNewpassword(customer.getNewpassword());
                pojo.setSelfcarepwd(customer.getSelfcarepwd());
                pojo.setBuId(customer.getBuId());
                pojo.setStaffId(customer.getStaffId());
                //   pojo.setLinkAcceptanceList(linkAcceptanceMapper.domainToDTO(customer.getLinkAcceptanceList(), new CycleAvoidingMappingContext()));
                if (customer.getPassportNo() != null) {
                    pojo.setPassportNo(customer.getPassportNo());
                }
                if (customer.getNextTeamHierarchyMapping() != null) {
                    pojo.setNextTeamHierarchyMapping(customer.getNextTeamHierarchyMapping());
                }
                pojo.setCalendarType(customer.getCalendarType());
                if (customer.getPlanMappingList() != null && customer.getPlanMappingList().size() > 0) {
                    List<CustPlanMapppingPojo> custPlanMapppingsPojoList = new ArrayList<CustPlanMapppingPojo>();
                    CustPlanMapppingPojo custPlanMapppingPojo = null;
                    for (CustPlanMappping custPlanMappping : customer.getPlanMappingList()) {
                        custPlanMapppingPojo = new CustPlanMapppingPojo();
                        if (custPlanMappping.getId() != null) {
                            custPlanMapppingPojo.setId(custPlanMappping.getId());
                        }
                        custPlanMapppingPojo.setPlanId(custPlanMappping.getPlanId());
                        custPlanMapppingPojo.setStatus(custPlanMappping.getStatus());
                        custPlanMapppingPojo.setStartDate(custPlanMappping.getStartDate());
                        custPlanMapppingPojo.setEndDate(custPlanMappping.getEndDate());
                        custPlanMapppingPojo.setExpiryDate(custPlanMappping.getExpiryDate());
                        if (custPlanMappping.getCustomer() != null) {
                            custPlanMapppingPojo.setCustid(custPlanMappping.getCustomer().getId());
                        }
                        custPlanMapppingsPojoList.add(custPlanMapppingPojo);
                    }
                    pojo.setPlanMappingList(custPlanMapppingsPojoList);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Unable to convert customer model to customer pojo response{}exception{}", APIConstants.FAIL, ex.getStackTrace());
        }
        return pojo;
    }

    @Override
    protected JpaRepository<Customers, Integer> getRepository() {
        return customersRepository;
    }

    @Override
    public Customers get(Integer id) {
        Customers customers = super.get(id);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if(customers.getId().equals(1) || customers.getId().equals(2) ){
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1))))
                return customers;
        }else {
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1)) && (customers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(customers.getBuId()))))
                return customers;
        }
        return null;
    }

    public Customers getCustomers(Integer id){
        Customers customers = customersRepository.findById(id).get();
        return customers;
    }

    public void terminate(CustomerTerminationMessage message) {
        List<CreditDocMessage> creditdoc = new ArrayList<>();
        CreditDocMessageList creditDocMessageList =  new CreditDocMessageList();
        Customers customers=customersRepository.findById(message.getCustId()).orElse(null);
        customers.setStatus(message.getStatus());
        CustPlanMappping custPlanMappping=custPlanMapppingRepository.findAllByCustomerId(customers.getId()).get(0);
        List<CustPlanMappping> custPlanMapppingList=custPlanMapppingRepository.findAllByCustomerId(customers.getId());

        for(CustPlanMappping mappping:custPlanMapppingList){
            mappping.setStatus("STOP");
             mappping.setCustPlanStatus("STOP");
        }

        if(message.getStatus()!=null && message.getStatus().equalsIgnoreCase("Terminate"))
        {
            List<Customers> childCustomers=customersRepository.findAllByParentCustomers(customers);
            childCustomers.stream().forEach(customer->{
                List<CustPlanMappping> childCustPlanMapppingList=custPlanMapppingRepository.findAllByCustomerId(customer.getId());
                for(CustPlanMappping mappping:childCustPlanMapppingList){
                    if(mappping.getInvoiceType()!=null && mappping.getInvoiceType().equalsIgnoreCase("Group"))
                    {
                        mappping.setStatus("STOP");
                        mappping.setCustPlanStatus("STOP");
                        custPlanMapppingList.add(mappping);
                    }
                }
            });
        }

        PostpaidPlan postpaidPlan =postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
        customersRepository.save(customers);
        custPlanMapppingRepository.saveAll(custPlanMapppingList);


       List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerId(customers.getId())
               .stream()
               .filter(deb -> !deb.getStatus().equalsIgnoreCase("Cancel") ||
                       !deb.getStatus().equalsIgnoreCase("Void") ||
                       deb.getStartdate().isBefore(LocalDateTime.now()))
               .collect(Collectors.toList());
       for(DebitDocument deb : debitDocumentList ) {
           if (message.getGenerateCreditnote() && customers.getCreatedate().toLocalDate().isBefore(LocalDate.now())){
               CreditDocument doc = creditDocService.creatCreditNotAsPerService(deb, null, customers.getCustomerServiceMappingList(), "Credit Doc for Termination Customer", false, null, "terminate", postpaidPlan.getChargeList().get(0).getCharge().getChargetype(), null, null);
               if(doc!=null) {
                   CreditDocMessage creditDocMessage = new CreditDocMessage(doc);
                   creditdoc.add(creditDocMessage);
                   List<CreditDocument> creditDocuments = creditDocRepository.findAllByInvoiceIdAndType(deb.getId(), "Payment");
                   List<CreditDocument> creditDocumentsNew = creditDocuments.stream().peek(i -> i.setAdjustedAmount(0d)).collect(Collectors.toList());
                   creditDocRepository.saveAll(creditDocumentsNew);
               }
           }
       }
       if(!CollectionUtils.isEmpty(creditdoc)) {
           creditDocMessageList.setCreditDocMessageList(creditdoc);
//           messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
           kafkaMessageSender.send(new KafkaMessageData(message, CustomerPackageRelMessage.class.getSimpleName()));

       }



        List<DebitDocument> debitDocumentListvoid = customers.getDebitDocList()
                .stream()
                .filter(deb->deb.getStartdate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
       for(DebitDocument deb :debitDocumentListvoid ){
           prepaidInvoiceService.voidInvoice(deb.getId(),"Void",null);
       }

       if(custPlanMapppingList!=null && !custPlanMapppingList.isEmpty() && message.getStatus().equalsIgnoreCase("Terminate"))
       {
           CustPlanMappingStatusMessage statusMessage=new CustPlanMappingStatusMessage();
           custPlanMapppingList.stream().forEach(mapping->{
                CustPlanMappingMessage mappingMessage=new CustPlanMappingMessage(mapping.getId(),mapping.getCustPlanStatus(),mapping.getStatus());
                statusMessage.getCustPlanMappings().add(mappingMessage);
           });
//           messageSender.send(statusMessage, RabbitMqConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS);
           kafkaMessageSender.send(new KafkaMessageData(statusMessage, CustPlanMappingStatusMessage.class.getSimpleName()));
       }
    }

    public void updateCustomerDiscount(CustomerDiscountPojo updateDiscountSharedMessage) {
        List<Integer> custServIds = updateDiscountSharedMessage.getCustServiceIds();

        if(!CollectionUtils.isEmpty(custServIds)) {
            LocalDate expirydate = null;
            if(updateDiscountSharedMessage.getDiscountExpiryDateStr() != null && !updateDiscountSharedMessage.getDiscountExpiryDateStr().isEmpty()) {
                String dicountExpDate = updateDiscountSharedMessage.getDiscountExpiryDateStr();
                DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                expirydate = LocalDate.parse(dicountExpDate, parser);
            } else {
                expirydate = null;
            }
            List<CustomerServiceMapping> customerServiceMappings = customerServiceMapRepository.findAllByIdIn(custServIds);
            String custType = customersRepository.findCustTypeId(customerServiceMappings.get(0).getCustId());
            LocalDate finalExpirydate = expirydate;
            customerServiceMappings = customerServiceMappings.stream().peek(customerServiceMapping -> {
                customerServiceMapping.setDiscount(updateDiscountSharedMessage.getDiscount());
                customerServiceMapping.setNewDiscount(updateDiscountSharedMessage.getDiscount());
//                if(finalExpirydate != null)
                customerServiceMapping.setNewDiscountExpiryDate(finalExpirydate);
                customerServiceMapping.setDiscountExpiryDate(finalExpirydate);
                customerServiceMapping.setDiscountType(updateDiscountSharedMessage.getNewDiscountType());
                customerServiceMapping.setNewDiscountType(updateDiscountSharedMessage.getNewDiscountType());
            }).collect(Collectors.toList());
            customerServiceMapRepository.saveAll(customerServiceMappings);

            List<Integer> custpackIds = custPlanMapppingRepository.getIdByCustServiceMappingIdIn(custServIds);
            List<CustomerChargeHistory> customerChargeHistoryList = customerChargeHistoryRepository.findAllByCustPlanMapppingIdIn(custpackIds);
            if (custType.equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                customerChargeHistoryList = customerChargeHistoryList.stream().peek(i -> i.setDiscountExpDate(i.getNextBillDate())).collect(Collectors.toList());
            }else {
                customerChargeHistoryList = customerChargeHistoryList.stream().peek(i -> i.setDiscountExpDate(LocalDate.now())).collect(Collectors.toList());

            }
            customerChargeHistoryRepository.saveAll(customerChargeHistoryList);
        }
    }

    @Transactional
    public Set<CustServiceMappingDTO> addPromiseToPayInBulk(PromiseToPayPojoInBulk promiseToPayPojoInBulk) {
        try {

            List<Integer> custPlanIds = promiseToPayPojoInBulk.getPromiseToPay().stream().map(PromiseToPayPojo::getCustPlanMapping).collect(Collectors.toList());
            List<Integer> custSerIds = custPlanMapppingRepository.getAllByCustServiceMappingIdInCprIds(custPlanIds);
            List<CustomerServiceMapping> serviceMappings = custPlanMappingService.changeStatusOfCustServices(custSerIds, StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE, promiseToPayPojoInBulk.getPromise_to_pay_remarks(), Boolean.FALSE,Boolean.TRUE);
            return serviceMappings.stream().map(customerServiceMapping -> new CustServiceMappingDTO(customerServiceMapping)).collect(Collectors.toSet());
        }catch (CustomValidationException ex) {
            throw new CustomValidationException(ex.getErrCode(),ex.getMessage(), null);
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + e.getMessage(), null);
        }
    }

    public List<CustomerAddress> getCustomerAddresses(Integer customerId) throws CustomValidationException {
        if (customerId == null || customerId <= 0) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Invalid customer ID", null);
        }
        // Fetch customer with addresses
        List<CustomerAddress> customerAddress = customerAddressRepository.findAddressesByCustomerId(customerId);


        return customerAddress;
    }

    public void updateCustomerAddressData(Mvno mvno, UpdateMvnoSharedDataMessage mvnoSharedDataMessage) {
        try {
            if(mvno.getCustInvoiceRefId() != null){
                List<CustomerAddress> addressList = customerAddressService.findAddressByCustomerId(mvno.getCustInvoiceRefId());
                if(!addressList.isEmpty() && addressList != null){
                    CustomerAddress address = addressList.get(0);
                    address.setLandmark(mvnoSharedDataMessage.getAddress());
                    address.setVersion("NEW");
                    customerAddressRepository.save(address);
                }else{
                    Customers mvnoCustomers = customersRepository.findById(mvno.getCustInvoiceRefId()).orElse(null);
                    if(mvnoCustomers != null){
                        CustomerAddress addressForSave = new CustomerAddress();
                        addressForSave.setId(customerAddressRepository.findMaxId() + 1);
                        addressForSave.setCustomer(mvnoCustomers);
                        addressForSave.setAddressType("Present");
                        addressForSave.setVersion("NEW");
                        addressForSave.setLandmark(mvnoSharedDataMessage.getAddress());
                        customerAddressRepository.save(addressForSave);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateCustomerAddressData(Mvno mvno, SaveMvnoSharedDataMessage mvnoSharedDataMessage) {
        try {
            if(mvno.getCustInvoiceRefId() != null){
                List<CustomerAddress> addressList = customerAddressService.findAddressByCustomerId(mvno.getCustInvoiceRefId());
                if(!addressList.isEmpty() && addressList != null){
                    CustomerAddress address = addressList.get(0);
                    address.setLandmark(mvnoSharedDataMessage.getAddress());
                    address.setVersion("NEW");
                    customerAddressRepository.save(address);
                }else{
                    Customers mvnoCustomers = customersRepository.findById(mvno.getCustInvoiceRefId()).orElse(null);
                    if(mvnoCustomers != null){
                        CustomerAddress addressForSave = new CustomerAddress();
                        addressForSave.setId(customerAddressRepository.findMaxId() + 1);
                        addressForSave.setCustomer(mvnoCustomers);
                        addressForSave.setAddressType("Present");
                        addressForSave.setVersion("NEW");
                        addressForSave.setLandmark(mvnoSharedDataMessage.getAddress());
                        customerAddressRepository.save(addressForSave);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<MvnoDebitDocDetailsPojo> exportCustomerList(Integer invoiceId) throws Exception {
        List<Integer> custDebitDocIds = debitDocDetailRepository.findDebitDocIdByMvnoDebitDocId(invoiceId);
        List<MvnoDebitDocDetailsPojo> exportCustomr =customersRepository.findInvoiceDetailsofCusMvno(custDebitDocIds);
        return exportCustomr;
    }
    /**
     * @Author Dhaval Khalasi
     * This function will return dueamount for changeplan based on individual plan and new plan and wallet
     * This is incomplete fuction.This fuction on handle individual change plan now.Remaining case is in backlog
     * **/
    public Double getDueAmountFromChangePlan(CustomerChangePlanDueAmountDTO customerChangePlanDueAmountDTO){
        Double dueAmount = 0.0;
        Double offerPrice = 0.0;
        Double dailyPriceWithTax = 0.0;
        Double currentCustomerPrice = 0.0;
        Double newPlanPrice = 0.0;
        Double adjustedAmount = 0.0;
        Double getCustomerWallet = 0.0;
        if(customerChangePlanDueAmountDTO.getCustPackRelId() != null && customerChangePlanDueAmountDTO.getNewPlanId() != null && customerChangePlanDueAmountDTO.getOldPlanId() != null && customerChangePlanDueAmountDTO.getOldPlanGroupId() == null){
            logger.debug("Individual change plan due price");
            Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(customerChangePlanDueAmountDTO.getCustPackRelId());
            if(custPlanMappping.isPresent()){
                Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findProjectedById(custPlanMappping.get().getPlanId());
                Optional<PostpaidPlan> newPostpaidPlan = postpaidPlanRepo.findProjectedById(customerChangePlanDueAmountDTO.getNewPlanId());
                if(!newPostpaidPlan.isPresent()){
                    return dueAmount;
                }
                else{
                    newPlanPrice = newPostpaidPlan.get().getOfferprice();
                    if(CommonUtils.CHANGEPLANBILLINGCYCLECONSTANT.Existing_Billing_Cycle.equalsIgnoreCase(customerChangePlanDueAmountDTO.getChangePlanBillingCycle())) {
                        double totalNewPlanDays = newPostpaidPlan.get().getValidity();
                        String validityUnit = newPostpaidPlan.get().getUnitsOfValidity();
                        if (validityUnit != null) {
                            switch (validityUnit.trim().toLowerCase()) {
                                case "hours":
                                    totalNewPlanDays = totalNewPlanDays / 24.0; // convert hours → days
                                    break;
                                case "months":
                                    totalNewPlanDays = totalNewPlanDays * 30.0; // assume 30 days per month
                                    break;
                                case "years":
                                    totalNewPlanDays = totalNewPlanDays * 365.0; // assume 365 days per year
                                    break;
                                case "days":
                                default:
                                    break;
                            }
                        }

                        // Remaining days based on old plan’s expiry
                        long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), custPlanMappping.get().getExpiryDate());
                        if (remainingDays < 0) remainingDays = 0;

                        // Calculate prorated price
                        double newPlanDailyPrice = newPlanPrice / totalNewPlanDays;
                        newPlanPrice = newPlanDailyPrice * remainingDays;
                    }


                }
                if(postpaidPlan.isPresent()){
                    offerPrice = postpaidPlan.get().getOfferprice();
                    if(!postpaidPlan.get().getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                        long totalPlanDays = ChronoUnit.DAYS.between(custPlanMappping.get().getStartDate(), custPlanMappping.get().getExpiryDate());
                        if(totalPlanDays > 0) {
                            dailyPriceWithTax = offerPrice / totalPlanDays;
                        }
                        else{
                            dailyPriceWithTax = 0.0;
                        }
                        long customerPlanDays = ChronoUnit.DAYS.between(LocalDateTime.now(), custPlanMappping.get().getExpiryDate());
                        currentCustomerPrice = dailyPriceWithTax*customerPlanDays + dailyPriceWithTax;
                    }
                    else{
                        currentCustomerPrice = offerPrice;
                    }
                    Customers customers = customersRepository.findCustomerById(customerChangePlanDueAmountDTO.getCustId());
                    if(customers.getStatus().equalsIgnoreCase("NewActivation") || customers.getStatus().equalsIgnoreCase("ActivationPending")){
                        List<TrialDebitDocShowPojo> trialDebitDocShowPojoList = trialDebitDocRepository.findAllInvoiceByCustomerPlanMappingId(customerChangePlanDueAmountDTO.getCustPackRelId());
                        for(TrialDebitDocShowPojo trialDebitDocShowPojo : trialDebitDocShowPojoList){
                          if(trialDebitDocShowPojo.getAdjustedAmount() != null){
                              adjustedAmount = adjustedAmount + trialDebitDocShowPojo.getAdjustedAmount();
                          }
                      }
                      if(custPlanMappping.get().getEndDate().isBefore(LocalDateTime.now())){
                            adjustedAmount = 0.0;
                      }
                      dueAmount = newPlanPrice - (adjustedAmount + currentCustomerPrice);
                    }
                    else {


//                        List<DebitShowDocumentPojo> debitShowDocumentPojoList = debitDocRepository.findAllInvoiceByCustomerPlanMappingId(customerChangePlanDueAmountDTO.getCustPackRelId());
//                        if (debitShowDocumentPojoList != null && !debitShowDocumentPojoList.isEmpty()) {
//                            for (DebitShowDocumentPojo debitShowDocumentPojo : debitShowDocumentPojoList) {
//                                if (debitShowDocumentPojo.getAdjustedAmount() != null) {
//                                    adjustedAmount = adjustedAmount + debitShowDocumentPojo.getAdjustedAmount();
//                                }
//                            }
//                        }
//                        CustomerWalletPojo customerWalletPojo = autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(customerChangePlanDueAmountDTO.getCustId().longValue());
//                        if (customerWalletPojo != null) {
//                            getCustomerWallet = customerWalletPojo.getWalletAmount();
//                        }
//                        if(custPlanMappping.get().getEndDate().isBefore(LocalDateTime.now())){
//                            adjustedAmount = 0.0;
//                        }
//                        dueAmount = newPlanPrice - (adjustedAmount + currentCustomerPrice + getCustomerWallet);
                        List<DebitShowDocumentPojo> debitShowDocumentPojoList =
                                debitDocRepository.findAllInvoiceByCustomerPlanMappingId(customerChangePlanDueAmountDTO.getCustPackRelId());
                        Double cnAmount = 0.0;


                        if (debitShowDocumentPojoList != null && !debitShowDocumentPojoList.isEmpty()) {
                            DebitShowDocumentPojo latestDebitDoc = debitShowDocumentPojoList.get(0);
                            if (latestDebitDoc.getId() != null) {
                                Double previewedCnAmount = previewCreditNoteAmount(latestDebitDoc.getId());
                                if (previewedCnAmount != null) {
                                    cnAmount = previewedCnAmount;
                                }
                            }
                        }

                        try {
                            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext.getBean(CustomerLedgerDtlsService.class);
                            CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                            pojo.setCustId(customerChangePlanDueAmountDTO.getCustId());
                            pojo.setCREATE_DATE(null);
                            pojo.setEND_DATE(null);

                            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
                            CustomerLedgerAllInfoPojo ledgerAllInfoPojo =
                                    customerLedgerDtlsService.custInfoBytime(customerChangePlanDueAmountDTO.getCustId(), infoPojo);
                            if (ledgerAllInfoPojo != null && ledgerAllInfoPojo.getCustomerLedgerInfoPojo() != null) {
                                getCustomerWallet = -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance();
                            }
                        } catch (Exception ex) {
                            logger.error("Error fetching wallet for customer ID: " + customerChangePlanDueAmountDTO.getCustId(), ex);
                            getCustomerWallet = 0.0;
                        }

                        if (custPlanMappping.get().getEndDate().isBefore(LocalDateTime.now())) {
                            cnAmount = 0.0;
                        }

                        dueAmount = newPlanPrice - (cnAmount + getCustomerWallet);

                        if (dueAmount < 0.0) {
                            dueAmount = 0.0;
                        }
                    }

                }
                else {
                    logger.error("Plan not found in custPlanMapping");
                }

            }
            else{
                logger.error("No CustPlanMapping found for given Id");
            }

        }
        return dueAmount;
    }

    public Double previewCreditNoteAmount(Integer debitDocId) {
        Optional<DebitDocument> optionalDebitDocument = debitDocRepository.findById(debitDocId);
        if (optionalDebitDocument.isPresent()) {
            DebitDocument debitDocument = optionalDebitDocument.get();
            try {
                double remainingAmount = debitDocument.getTotalamount();
                DecimalFormat df = new DecimalFormat("#.00");
                List<CustomerChargeDBR> dbrList = customerChargeDBRRepository
                        .findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(
                                debitDocument.getId().longValue(),
                                LocalDate.now(),
                                debitDocument.getEndate().toLocalDate());

                double cnAmount = 0d;
                if (!CollectionUtils.isEmpty(dbrList)) {
                    cnAmount = dbrList.stream()
                            .mapToDouble(x -> x.getDbr())
                            .sum();
                }

                if (cnAmount == 0) {
                    return 0.0;
                }

                double invoiceWithoutTax = debitDocument.getTotalamount() - debitDocument.getTax() + debitDocument.getDiscount();
                double newDiscount = 0;
                if (invoiceWithoutTax > 0) {
                    newDiscount = cnAmount * (debitDocument.getDiscount() / invoiceWithoutTax);
                }

                double percentage = (debitDocument.getTax() * 100.0d) / (debitDocument.getTotalamount() - debitDocument.getTax());
                double prorateTaxAmount = ((cnAmount - newDiscount) * percentage) / 100.0d;
                cnAmount = cnAmount - newDiscount + prorateTaxAmount;

                if (remainingAmount - cnAmount < 0.1 && remainingAmount != 0) {
                    cnAmount = remainingAmount;
                }

                cnAmount = Double.parseDouble(df.format(cnAmount));
                return cnAmount;

            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Error while previewing CN for invoice: " + debitDocument.getDocnumber() + " exception: " + ex.getMessage());
                return null;
            }
        }
        return null;
    }


    public void adjustPrepaidCustomerDebitDocWithAdvancePayment() {
        try {
            logger.info("Enter in prepaid customer advance payment adjustment");
            List<Integer> custIds = customersRepository.findCustomerByStatusAndCreditDocument();
            if (custIds.isEmpty()) {
                logger.warn("No customer found for payment adjustment");
                return;
            }

            List<String> statusList = Arrays.asList(Constants.PAYMENT_STATUS_APPROVED, Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            List<String> paymentTypeList = Arrays.asList(Constants.PAYMENT_TYPE , Constants.TRANS_CREDIT_NOTE,CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
            List<String> payTypeList = Arrays.asList(Constants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT , Constants.CREDIT_DOC_TYPE_CREDITNOTE, Constants.CREDIT_DOC_STATUS.INVOICE);

            // Fetch credit & debit documents in parallel
            CompletableFuture<List<CreditDocumentDTOForAdjustment>> creditFuture = CompletableFuture.supplyAsync(() ->
                    creditDocRepository.findAllByCustomerInAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(
                            custIds, payTypeList, statusList, paymentTypeList
                    )
            );
            CompletableFuture<List<DebitDocumentDTOForAdjustment>> debitFuture = CompletableFuture.supplyAsync(() ->
                    debitDocRepository.getDebitDocForPaymentAdjustment(custIds)
            );

            // Combine both async queries & process results
            creditFuture.thenCombineAsync(debitFuture, (creditDocs, debitDocs) -> {
                Map<Integer, List<CreditDocumentDTOForAdjustment>> creditMap = creditDocs.stream()
                        .collect(Collectors.groupingBy(CreditDocumentDTOForAdjustment::getCustId));

                Map<Integer, List<DebitDocumentDTOForAdjustment>> debitMap = debitDocs.stream()
                        .collect(Collectors.groupingBy(DebitDocumentDTOForAdjustment::getCustId));

                custIds.parallelStream().forEach(custId -> {  // Parallel Processing for High TPS
                    List<CreditDocumentDTOForAdjustment> credits = creditMap.getOrDefault(custId, Collections.emptyList());
                    List<DebitDocumentDTOForAdjustment> debits = debitMap.getOrDefault(custId, Collections.emptyList());

                    if (!credits.isEmpty() && !debits.isEmpty()) {
                        try {
                           // creditDocService.adjustAllCreditDebitDoc(credits, debits);
                            creditDocService.adjustCreditDebitDocs(debits,credits);
                        } catch (Exception e) {
                            logger.error("Error processing customer: " + custId, e);
                        }
                    }
                    else{
                        logger.warn("either credit or debit is empty for all customer");
                    }
                });

                return null;
            });
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    public void adjustPrepaidCustomerTrialDebitDocWithAdvancePayment() {
        try {
            logger.debug("Enter in prepaid caf advance payment adjustment");
            List<Integer> custIds = customersRepository.findTrialCustomerByStatusAndCreditDocument();
            if (custIds.isEmpty()) {
                logger.warn("No caf found for payment adjustment");
                return;
            }

            List<String> statusList = Arrays.asList(Constants.PAYMENT_STATUS_APPROVED, Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            List<String> paymentStatusList = Arrays.asList(Constants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT, Constants.CREDIT_DOC_STATUS.INVOICE);

            // Fetch credit & debit documents in parallel
            CompletableFuture<List<CreditDocumentDTOForAdjustment>> creditFuture = CompletableFuture.supplyAsync(() ->
                    creditDocRepository.findAllByCustomerInAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(
                            custIds, paymentStatusList, statusList, Arrays.asList(Constants.PAYMENT_TYPE)
                    )
            );
            CompletableFuture<List<TrailDebitDocumentDTOForAdjustment>> debitFuture = CompletableFuture.supplyAsync(() ->
                    trialDebitDocRepository.getTrailDebitDocForPaymentAdjustment(custIds)
            );

            // Combine both async queries & process results
            creditFuture.thenCombineAsync(debitFuture, (creditDocs, debitDocs) -> {
                Map<Integer, List<CreditDocumentDTOForAdjustment>> creditMap = creditDocs.stream()
                        .collect(Collectors.groupingBy(CreditDocumentDTOForAdjustment::getCustId));

                Map<Integer, List<TrailDebitDocumentDTOForAdjustment>> debitMap = debitDocs.stream()
                        .collect(Collectors.groupingBy(TrailDebitDocumentDTOForAdjustment::getCustId));

                custIds.parallelStream().forEach(custId -> {  // Parallel Processing for High TPS
                    List<CreditDocumentDTOForAdjustment> credits = creditMap.getOrDefault(custId, Collections.emptyList());
                    List<TrailDebitDocumentDTOForAdjustment> debits = debitMap.getOrDefault(custId, Collections.emptyList());

                    if (!credits.isEmpty() && !debits.isEmpty()) {
                        try {
                            // creditDocService.adjustAllCreditDebitDoc(credits, debits);
                            creditDocService.adjustCreditTrailDebitDocs(debits,credits);
                        } catch (Exception e) {
                            logger.error("Error processing customer: " + custId, e);
                        }
                    }
                    else{
                        logger.warn("No caf has debit and creditdoc to adjust");
                    }
                });

                return null;
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void renewPrepaidCustomerWithAdvancePayment() {
        try {
            logger.debug("Enter in renew prepaid customer advance payment");
            String autoRenewFreePlan = clientServiceRepository.findValueByNameAndMvnoId(Constants.AUTO_RENEW_FREE_PLAN, getMvnoIdFromCurrentStaff());
            List<Object[]> customerData = new ArrayList<>();
            if(autoRenewFreePlan != null && autoRenewFreePlan.equalsIgnoreCase("true")){
                customerData = customersRepository.findCustomerByStatusAndDebitDocument();
            }else{
                customerData = customersRepository.findEligibleCustomerForRenewal();
            }
            
//            List<Object[]> customerData = customersRepository.findCustomerByStatusAndDebitDocument();
            if (customerData.isEmpty()) {
                logger.warn("No customer found for renew plan");
                return;
            }

            // Process customers in parallel for better performance
            customerData.parallelStream().forEach(customer -> {
                Integer customerId = (Integer) customer[0];
                Integer renewPlanLimit = (Integer) customer[1];
                Integer mvnoId = (Integer) customer[2];
                LocalDateTime latestEndDate = custPlanMapppingRepository.findLatestEndDateByCustId(customerId);
                if (latestEndDate != null) {
                    ClientService minDaysService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.RENEWAL_MIN_REMAINING_DAYS, mvnoId);
                    long minRemainingDays = (minDaysService != null) ? Long.parseLong(minDaysService.getValue()) : 0L;
                    long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), latestEndDate);
                    if (remainingDays > minRemainingDays) {
                        logger.info("Skipping auto-renewal for customerId {} as remaining days {} > configured RENEWAL_MIN_REMAINING_DAYS {}", customerId, remainingDays, minRemainingDays);
                        return;
                    }
                }
                Long renewPlanLimitFromClientService;
                ClientService clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.RENEWAL_PLAN_LIMIT_GLOBAL, mvnoId);
                if (clientService != null)
                    renewPlanLimitFromClientService = Long.parseLong(clientService.getValue());
                else {
                    renewPlanLimitFromClientService = 1L;
                }
                long autoRenewFuturePlanDays = (renewPlanLimit != null) ? renewPlanLimit : renewPlanLimitFromClientService;
                logger.info("************** Renew plan with customerId : "+ customerId + " --- customer renewPlanLimit : " + renewPlanLimit + " --- clientService renewPlanLimitFromClientService : " + renewPlanLimitFromClientService + " --- autoRenewFuturePlanDays : " + autoRenewFuturePlanDays + " --- mvnoId : " + mvnoId +"**************");
                processCustomerForRenewal(customerId, autoRenewFuturePlanDays);
            });
        } catch (Exception e) {
            logger.error("Error in renewPrepaidCustomerWithAdvancePayment", e);
        }
    }
    public void renewWalletForBoosterPlan()
    {
        try{
            List<Integer> custids = custPlanMapppingRepository.findCustomersWhereIsRenewalForBoosterFalse();
            List<CustPlanMapppingDto> expiredPlan = getexpireListByCustomerId(custids);

            if (expiredPlan != null && !expiredPlan.isEmpty()) {
                compareAndSendPayloadForAutoRenewalToCmsForAutoRenewalBoosterPlan(expiredPlan, -1L, true, true);
            }

        }catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Error in renewWalletForBoosterPlan", e);
        }
    }


    private void processCustomerForRenewal(Integer customerId, long autoRenewFuturePlanDays) {
        try {
            List<CustPlanMapppingDto> futurePlanList = autoRenewOrAddonPlanService.getFuturePlanListByCustomerId(customerId);
            if(autoRenewFuturePlanDays > 0){
                if (futurePlanList == null || futurePlanList.isEmpty() || futurePlanList.size() < autoRenewFuturePlanDays) {
                    logger.info("************** processCustomerForRenewal with customerId : "+ customerId + " --- autoRenewFuturePlanDays : " + autoRenewFuturePlanDays + " --- future Plan List size : " + futurePlanList.size() + " --- future Plan List : " + futurePlanList + "**************");
                    Double closingAmount = customerLedgerDtlsRepository.findClsoingAmountById(customerId);
                    logger.info("************** closingAmount of processCustomerForRenewal with customerId : {}  closingAmount: {} **************",customerId,closingAmount);
                    Double customerWallet = -closingAmount;
                    logger.info("**************customerWallet processCustomerForRenewal with customerId : {}  customerWallet: {} **************",customerId,customerWallet);
                    if (customerWallet > 0.0) {
                        logger.info("************** Inside customerWallet > 0.0 customerId : {} **************",customerId);
                        List<CustPlanMapppingDto> activePlanList = autoRenewOrAddonPlanService.getActivePlanListByCustomerId(customerId);
                        if (activePlanList != null && !activePlanList.isEmpty()) {
                            CustPlanMapppingDto dto = activePlanList.get(activePlanList.size() - 1);
                            compareAndSendPayloadForAutoRenewalToCms(customerId, dto, customerWallet, -1L, true, true);
                        } else {
                            List<CustPlanMapppingDto> expirePlanList = autoRenewOrAddonPlanService.getExpirePlanListByCustomerId(customerId);
                            if (expirePlanList != null && !expirePlanList.isEmpty()) {
                                CustPlanMapppingDto dto = expirePlanList.get(expirePlanList.size() - 1);
                                compareAndSendPayloadForAutoRenewalToCms(customerId, dto, customerWallet, -1L, false, true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing customer ID: " + customerId, e);
        }
    }


    public void compareAndSendPayloadForAutoRenewalToCms(Integer customerId, CustPlanMapppingDto dto, Double customerWallet, Long days, Boolean isActivePlan, Boolean isRequestFromScheduler) {
        try {
            if (dto.getPlanId() != null) {
                logger.info("************** Inside compareAndSendPayloadForAutoRenewalToCms customerId : {} **************",customerId);
                if (isRequestFromScheduler) {
                    logger.info("************** Inside isRequestFromScheduler customerId : {} **************",customerId);
                    Double planOfferPriceWithTax = autoRenewOrAddonPlanService.getPlanPriceByPlanId(dto.getPlanId(), null);
                    if (customerWallet >= planOfferPriceWithTax-maxWalletDiffAllowed) {
                        AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                        renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                        renewOrAddonPlanRequestDto.setCustomerId(customerId);
                        renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                        renewOrAddonPlanRequestDto.setAddonEndDate(null);
                        renewOrAddonPlanRequestDto.setAddonStartDate(null);
                        renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                        renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                        renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                        renewOrAddonPlanRequestDto.setIsParent(true);
                        renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                        renewOrAddonPlanRequestDto.setDiscount(0.0);
                        renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                        renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos());
                        kafkaMessageSender.send(new KafkaMessageData(renewOrAddonPlanRequestDto, renewOrAddonPlanRequestDto.getClass().getSimpleName()));
                    } else {
                        logger.info("************** Not sufficient amount to send kafka to CMS for Renew for this CustId : " + customerId + " with balance : "+ customerWallet + "**************");
                    }
                } else {
                    logger.info("************** Inside else of isRequestFromScheduler customerId : {} **************",customerId);
                    AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                    renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                    renewOrAddonPlanRequestDto.setCustomerId(customerId);
                    renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                    renewOrAddonPlanRequestDto.setAddonEndDate(null);
                    renewOrAddonPlanRequestDto.setAddonStartDate(null);
                    renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                    renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                    renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                    renewOrAddonPlanRequestDto.setIsParent(true);
                    renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                    renewOrAddonPlanRequestDto.setDiscount(0.0);
                    renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                    renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos());
                    kafkaMessageSender.send(new KafkaMessageData(renewOrAddonPlanRequestDto, renewOrAddonPlanRequestDto.getClass().getSimpleName()));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing during Auto assign renew : ", e);
        }
    }


    public void compareAndSendPayloadForAutoRenewalToCmsForAutoRenewalBoosterPlan(List<CustPlanMapppingDto> expiredPlanList, Long days, Boolean isActivePlan, Boolean isRequestFromScheduler) {
            try {
                Double walletPrice = null;
                if (expiredPlanList != null && !expiredPlanList.isEmpty()) {

                    List<AutoRenewOrAddonPlanRequestDto> kafkaPayloadList = new ArrayList<>();

                    for (CustPlanMapppingDto dto : expiredPlanList) {
                        Integer customerId = dto.getCustId(); // Assuming this field is present
                        Boolean isPrepaid = customersRepository.isCustomerPrepaid(customerId) == 1;
                        if (isPrepaid) {
                            walletPrice = -autoRenewOrAddonPlanService.checkWalletBalanceByCustId(dto.getCustId());
                        }

                        if (dto.getPlanId() != null) {
                            AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                            renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                            renewOrAddonPlanRequestDto.setCustomerId(customerId);
                            renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                            renewOrAddonPlanRequestDto.setAddonEndDate(null);
                            renewOrAddonPlanRequestDto.setAddonStartDate(null);
                            renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                            renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                            renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                            renewOrAddonPlanRequestDto.setIsParent(true);
                            renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                            renewOrAddonPlanRequestDto.setDiscount(0.0);
                            renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                            renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(
                                    autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos()
                            );

                            if (isRequestFromScheduler) {
                                Double planOfferPriceWithTax = autoRenewOrAddonPlanService.getPlanPriceByPlanId(dto.getPlanId(), null);
                                if (walletPrice >= planOfferPriceWithTax) {
                                    kafkaPayloadList.add(renewOrAddonPlanRequestDto);
                                } else {
                                    String message = "Not sufficient amount in wallet to renew plan ";
                                    Optional<Customers> customers = customersRepository.findById(dto.getCustId());
                                    CustInsufficientWalletMessage custInsufficientWalletMessage = new CustInsufficientWalletMessage(message,customers.get().getUsername(),customerId,customers.get().getMvnoId(), customers.get().getEmail(),customers.get().getBuId(),walletPrice,customers.get().getPhone());
                                    Gson gson = new Gson();
                                    gson.toJson(custInsufficientWalletMessage);
                                    System.out.println("***/**/*********** Not sufficient amount to send kafka***********/**/***");
                                    kafkaMessageSender.send(new KafkaMessageData(custInsufficientWalletMessage, CustInsufficientWalletMessage.class.getSimpleName()));
//
                                    logger.info("***/**/*********** Not sufficient amount to send kafka to CMS for Renew for this CustId : " + customerId + " with balance : " + walletPrice + " **************");
                                }
                            } else {
                                kafkaPayloadList.add(renewOrAddonPlanRequestDto);
                            }
                        }
                    }

                    if (!kafkaPayloadList.isEmpty()) {
                        kafkaMessageSender.send(new KafkaMessageData(kafkaPayloadList, AutoRenewOrAddonPlanRequestDto.class.getSimpleName()));
                    }

                }
            } catch (Exception e) {
                logger.error("Error processing during Auto assign renew : ", e);
            }
        }

    public List<CustPlanMapppingDto> getexpireListByCustomerId(List<Integer> custId) {
        List<CustPlanMappping> otherPlans = custPlanMapppingRepository.findOtherPlansByCustomerIds(
                custId
        );

        return otherPlans.stream()
                .map(custPlan -> {
                    CustPlanMapppingDto dto = new CustPlanMapppingDto();
                    BeanUtils.copyProperties(custPlan, dto);
                    if (custPlan.getCustomer() != null) {
                        dto.setCustId(custPlan.getCustomer().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

    }


    /**
     * Method to Adjust Debit doc with advance payment on approval of payment for a customer
     */
    public void adjustCustDebitDocWithAdvPay(Integer custId) {
        if (null == custId) {
            logger.info("Customer id is null, skipping debit adjustment");
            return;
        }

        try {
            logger.debug("Starting prepaid customer advance payment adjustment against debit doc for custId {}",custId);

            List<String> statusList = Arrays.asList(Constants.PAYMENT_STATUS_APPROVED, Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            List<String> paymentTypeList = Arrays.asList(Constants.PAYMENT_TYPE, Constants.TRANS_CREDIT_NOTE, CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
            List<String> payTypeList = Arrays.asList(Constants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT, Constants.CREDIT_DOC_TYPE_CREDITNOTE, Constants.CREDIT_DOC_STATUS.INVOICE);

            List<CreditDocumentDTOForAdjustment> credits = creditDocRepository.findAllByCustomerAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(custId, payTypeList, statusList, paymentTypeList);
            List<DebitDocumentDTOForAdjustment> debits = debitDocRepository.getDebitDocForPaymentAdjustmentByCustid(custId);

            if (!credits.isEmpty() && !debits.isEmpty()) {
                // creditDocService.adjustAllCreditDebitDoc(credits, debits);
                creditDocService.adjustCreditDebitDocs(debits, credits);
            } else {
                logger.warn("No credit or debit documents found for adjustment for custId {}",custId);
            }
        } catch (Exception e) {
            logger.error("Error adjusting debit docs with advance payment for custId {}", custId, e);
        }
    }


    /**
     * Method to Adjust Debit doc with advance payment on approval of payment for a customer
     */
    public void adjustCustTrialDebitDocWithAdvPay(Integer custId) {
        if (null == custId) {
            logger.info("Customer id is null, skipping trial debit adjustment");
            return;
        }

        try {
            logger.debug("Starting prepaid customer advance payment adjustment against trial debit doc for custId {}", custId);

            List<String> statusList = Arrays.asList(Constants.PAYMENT_STATUS_APPROVED, Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            List<String> paymentStatusList = Arrays.asList(Constants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT, Constants.CREDIT_DOC_STATUS.INVOICE);

            List<CreditDocumentDTOForAdjustment> credits = creditDocRepository.findAllByCustomerAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(custId, paymentStatusList, statusList, Arrays.asList(Constants.PAYMENT_TYPE));
            List<TrailDebitDocumentDTOForAdjustment> debits = trialDebitDocRepository.getTrailDebitDocForPaymentAdjustmentByCustid(custId);

            if (!credits.isEmpty() && !debits.isEmpty()) {
                // creditDocService.adjustAllCreditDebitDoc(credits, debits);
                creditDocService.adjustCreditTrailDebitDocs(debits, credits);
            } else {
                logger.warn("No credit or debit documents found for adjustment of trial debit doc for custId {}",custId);
            }
        } catch (Exception e) {
            logger.error("Error adjusting trial debit docs with advance payment for custId {}", custId, e);
        }
    }

    /**
     * Method to renew prepaid customer with advance payment on approval of payment for a customer
     */
    public void renewPrepaidCustWithAdvPay(Integer custId) {
        if (null == custId) {
            logger.info("Customer id is null, skipping renewal of prepaid plan for customer");
            return;
        }

        try {
            logger.debug("Starting renew prepaid customer advance payment for custId {}", custId);
            String autoRenewFreePlan = clientServiceRepository.findValueByNameAndMvnoId(Constants.AUTO_RENEW_FREE_PLAN, getMvnoIdFromCurrentStaff());
            Object[] customer = new Object[0];
            if(autoRenewFreePlan != null && autoRenewFreePlan.equalsIgnoreCase("true")){
                customer = customersRepository.findCustomerByStatusAndCustId(custId);
            }else{
                customer = customersRepository.findEligibleCustomerForRenewalByCustId(custId);
            }

            if (customer.length==0) {
                logger.warn("No customer found for renew plan");
                return;
            }
                Object[] row = (Object[]) customer[0];
                Integer customerId = (Integer) row[0];
                Integer renewPlanLimit = (Integer) row[1];
                Integer mvnoId = (Integer) row[2];
                LocalDateTime latestEndDate = custPlanMapppingRepository.findLatestEndDateByCustId(customerId);
                if (latestEndDate != null) {
                    ClientService minDaysService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.RENEWAL_MIN_REMAINING_DAYS, mvnoId);
                    long minRemainingDays = (minDaysService != null) ? Long.parseLong(minDaysService.getValue()) : 0L;
                    long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), latestEndDate);
                    if (remainingDays > minRemainingDays) {
                        logger.info("Skipping auto-renewal for customerId {} as remaining days {} > configured RENEWAL_MIN_REMAINING_DAYS {}", customerId, remainingDays, minRemainingDays);
                        return;
                    }
                }
                Long renewPlanLimitFromClientService;
                ClientService clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.RENEWAL_PLAN_LIMIT_GLOBAL, mvnoId);
                if (clientService != null)
                    renewPlanLimitFromClientService = Long.parseLong(clientService.getValue());
                else {
                    renewPlanLimitFromClientService = 1L;
                }
                long autoRenewFuturePlanDays = (renewPlanLimit != null) ? renewPlanLimit : renewPlanLimitFromClientService;
                logger.info("************** Renew plan with customerId : "+ customerId + " --- customer renewPlanLimit : " + renewPlanLimit + " --- clientService renewPlanLimitFromClientService : " + renewPlanLimitFromClientService + " --- autoRenewFuturePlanDays : " + autoRenewFuturePlanDays + " --- mvnoId : " + mvnoId +"**************");
                processCustomerForRenewal(customerId, autoRenewFuturePlanDays);
        } catch (Exception e) {
            logger.error("Error in renewPrepaidCustomerWithAdvancePayment", e);
        }
    }

    /**
     * Method to renew wallet for booster plan for a customer
     */
    public void renewWalletForBoosterPlanForCust(Integer custId)
    {
        try{
            if (null == custId) {
                logger.info("Customer id is null, skipping renewal of prepaid plan for customer");
                return;
            }
            List<CustPlanMapppingDto> expiredPlan = getexpireListByCustomerId(Collections.singletonList(custId));

            if (expiredPlan != null && !expiredPlan.isEmpty()) {
                compareAndSendPayloadForAutoRenewalToCmsForAutoRenewalBoosterPlan(expiredPlan, -1L, true, true);
            }

        }catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Error in renewWalletForBoosterPlan", e);
        }
    }

    /**
     * Method for calling methods of cronJobTimeForAutomatePayment, for particular customer on payment
     */
    public void automatePayment(Integer custId){
        adjustCustDebitDocWithAdvPay(custId);
        adjustCustTrialDebitDocWithAdvPay(custId);
        renewPrepaidCustWithAdvPay(custId);
        renewWalletForBoosterPlanForCust(custId);
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = 1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
}
