package com.savbill.partnermanagement.modules.partner.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.customers.CustomerService;
import com.savbill.partnermanagement.modules.MasterManagement.City.City;
import com.savbill.partnermanagement.modules.MasterManagement.City.CityRepository;
import com.savbill.partnermanagement.modules.MasterManagement.City.QCity;
import com.savbill.partnermanagement.modules.MasterManagement.Country.CountryRepository;
import com.savbill.partnermanagement.modules.MasterManagement.Country.QCountry;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.QServiceArea;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.MicroSeviceDataShare.PartnerAmountMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.MasterManagement.City.CityService;
import com.savbill.partnermanagement.modules.MasterManagement.Country.Country;
import com.savbill.partnermanagement.modules.MasterManagement.Country.CountryService;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.partnermanagement.modules.MasterManagement.State.QState;
import com.savbill.partnermanagement.modules.MasterManagement.State.State;
import com.savbill.partnermanagement.modules.MasterManagement.State.StateRepository;
import com.savbill.partnermanagement.modules.MasterManagement.State.StateService;
import com.savbill.partnermanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMapping;
import com.savbill.partnermanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMappingRepo;
import com.savbill.partnermanagement.modules.PartnerServiceAreaMapping.QPartnerServiceAreaMapping;
import com.savbill.partnermanagement.modules.StaffUser.StaffUser;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserService;
import com.savbill.partnermanagement.modules.Tax.domain.QTax;
import com.savbill.partnermanagement.modules.Tax.domain.Tax;
import com.savbill.partnermanagement.modules.Tax.repository.TaxRepository;
import com.savbill.partnermanagement.modules.Tax.service.TaxService;
import com.savbill.partnermanagement.modules.partner.dto.PartnerHierarchy;
import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.*;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partner.entity.PriceBook1;
import com.savbill.partnermanagement.modules.partner.entity.QPartner;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.modules.partner.repository.PriceBookRepository1;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerCreditDocument;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerDebitDocument;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.QPartnerDebitDocument;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerCreditDocRepository;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerCreditDocumentRepository;
import com.savbill.partnermanagement.nepaliCalendarUtils.model.NepaliDateDTO;
import com.savbill.partnermanagement.nepaliCalendarUtils.service.DateConverterService;
import com.savbill.partnermanagement.security.spring.MessagesPropertyConfig;
import com.savbill.partnermanagement.utils.UpdateDiffFinder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class PartnerService extends AbstractService<Partner,PartnerPojo, Integer> {

    private static String MODULE = " [PartnerService] ";

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    CityService cityService;

    @Autowired
    TaxService taxService;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    PriceBookRepository1 priceBookRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    DateConverterService dateConverterService;


    @Autowired
    PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    PartnerCreditDocumentRepository partnerCreditDocumentRepository;

    @Autowired
    PartnerCreditDocRepository partnerCreditDocRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomerService customerService;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    TaxRepository taxRepository;



    @Autowired
    private Tracer tracer;

    private final Logger log = Logger.getLogger(PartnerService.class);



    @Override
    protected JpaRepository<Partner, Integer> getRepository() {
        return partnerRepository;
    }


    public PartnerService() {
        sortColMap.put("areaName", "srn.concatname");
        sortColMap.put("id", "partnerid");
        sortColMap.put("name", "PARTNERNAME");
    }

    public void validateRequest(PartnerPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }

        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }

        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }

        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (!(pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PERCUST_FLAT) || pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PERCUST_PERCENTAGE) || pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PRICEBOOK))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.partner.commission.types.error"), null);
        }
        if (!(pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT) || pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PERMANENT) || pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PAYMENT))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.address.type"), null);
        }
        if (operation.equals(CommonConstants.OPERATION_ADD) || operation.equals(CommonConstants.OPERATION_UPDATE)) {
            if (pojo.getCountry() != null && countryService.get(pojo.getCountry()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.found"), null);
            }
            if (pojo.getState() != null && stateService.get(pojo.getState()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
            }
            if (pojo.getCity() != null && cityService.get(pojo.getCity()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
            }
            if (pojo.getTaxid() != null && taxService.get(pojo.getTaxid()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.tax.not.found"), null);
            }
            if (pojo.getParentpartnerid() != null && get(pojo.getParentpartnerid().intValue()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.parent.partner.not.found"), null);
            }

//            if (pojo.getIsShitPartner().equals(false) && (pojo.getServiceAreaIds() == null || (pojo.getServiceAreaIds() != null && pojo.getServiceAreaIds().isEmpty()))) {
//                throw new CustomValidationException(APIConstants.FAIL,"Please Select Service Area", null);
//            }
        }
    }

    public boolean isSameStaff(String name) throws Exception {
        boolean flag = true;
        Integer userId = getLoggedInUserId();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (name != null) {
            name = name.trim();
            Integer createdById;
            if (getBUIdsFromCurrentStaff().size() == 0)
                createdById = partnerRepository.getCreatedBy(name, mvnoId);
            else
                createdById = partnerRepository.getCreatedBy(name, mvnoId, getBUIdsFromCurrentStaff());
            if(createdById != userId) {
                flag = false;
            }
        }
        return flag;
    }

    public PartnerPojo save(PartnerPojo pojo , HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";

        Partner old1 = null;
        // Check if this is an update by checking if the ID is already set
        if (pojo.getId() != null) {
            old1 = get(pojo.getId());
        }
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            Partner obj = convertPartnerPojoToPartnerModel(pojo);

            if(getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));

            if (old1 != null) {
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Partner update details: " + UpdateDiffFinder.getUpdatedDiff(old1, obj)+LogConstants.LOG_BY_NAME + pojo.getName()+" Partner update details: "+ UpdateDiffFinder.getUpdatedDiff(old1, pojo)+LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            obj = savePartner(obj);
            //partnerLedgerService.setPartnerLedger(obj.getId());
//            Partner originalCopy = new Partner(old1);
            pojo = convertPartnerModelToPartnerPojo(obj);
            //PartnerMessage partnerMessage = new PartnerMessage(pojo.getId(),pojo.getName(),pojo.getStatus(),pojo.getIsDelete());
            //this.messageSender.send(partnerMessage, RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER);

            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    @Transactional
    public Partner savePartner(Partner partner) throws Exception {
        String SUBMODULE = MODULE + " [savePartner()] ";
        String operation = "edit";
        try {
            if (partner != null && partner.getId() == null) {
                operation = "add";
                partner.setLastbilldate(null);
                LocalDateTime nextBilldate = LocalDateTime.now();
                if(partner.getCommdueday()!=null)
                {
                    nextBilldate = LocalDateTime.now().plusMonths(1).withDayOfMonth(partner.getCommdueday()).toLocalDate().atStartOfDay();
                    if(partner.getCommissionInterval()!=null)
                    {
                        nextBilldate = LocalDateTime.now().toLocalDate().atStartOfDay();
                        if(partner.getCommissionInterval().equalsIgnoreCase("Monthly"))
                            nextBilldate=nextBilldate.plusMonths(1).withDayOfMonth(partner.getCommdueday());;
                        if(partner.getCommissionInterval().equalsIgnoreCase("Quarterly"))
                            nextBilldate=nextBilldate.plusMonths(3).withDayOfMonth(partner.getCommdueday());;
                        if(partner.getCommissionInterval().equalsIgnoreCase("Half-Yearly"))
                            nextBilldate=nextBilldate.plusMonths(6).withDayOfMonth(partner.getCommdueday());;
                        if(partner.getCommissionInterval().equalsIgnoreCase("Yearly"))
                            nextBilldate=nextBilldate.plusMonths(12).withDayOfMonth(partner.getCommdueday());
                    }
                }


                if(partner.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                    NepaliDateDTO nepaliDateDTO = dateConverterService.getNepaliDateFromEnglishDate(nextBilldate.getDayOfMonth() + "-" + nextBilldate.getMonthValue() + "-" + nextBilldate.getYear() + " "
                            + nextBilldate.getHour() + ":" + nextBilldate.getMinute() + ":" + nextBilldate.getSecond());
                    int monthDay = dateConverterService.getDaysInMonth(nepaliDateDTO.getSaal(), nepaliDateDTO.getMahina());
                    if (partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                        nextBilldate = nextBilldate.plusDays(monthDay - nepaliDateDTO.getGatey());
                    else
                        nextBilldate = nextBilldate.plusDays(monthDay);
                }
//                } else {
//                    if(partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
//                        nextBilldate = LocalDate.now().withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear())).atStartOfDay();
//                    else
//                        nextBilldate = LocalDate.now().atStartOfDay().plusDays(30);
//                }
                partner.setNextbilldate(LocalDate.from(nextBilldate));
                if (!partner.getEmail().isEmpty()) {
                    QPartner qPartner = QPartner.partner;
                    BooleanExpression booleanExpression = qPartner.isDelete.eq(false).and(qPartner.isNotNull()).and(qPartner.email.equalsIgnoreCase(partner.getEmail().replaceAll("\\s", "")));
                    Optional<Partner> partner1 = partnerRepository.findOne(booleanExpression);
                    if (partner1.isPresent()) {
                        throw new CustomValidationException(APIConstants.FAIL, "Partner is already added with same email.", null);
                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, "Please enter valid email address.", null);
                }
            }
            if (getMvnoIdFromCurrentStaff() != null) {
                partner.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            partner.setResetDate(getResetDate(partner.getCalendarType(), LocalDate.now()));
            Partner save = partnerRepository.save(partner);

            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }



    public void deletePartner(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deletePartner()] ";
        try {

            QPartnerServiceAreaMapping qPartnerServiceAreaMapping=QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            BooleanExpression exp = qPartnerServiceAreaMapping.isNotNull();
            exp=exp.and(qPartnerServiceAreaMapping.partnerId.eq(id));
            List<PartnerServiceAreaMapping> partnerServiceAreaMapping= (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp);
            partnerServiceAreaMappingRepo.deleteAll(partnerServiceAreaMapping);

            Partner partner = getEntityForUpdateAndDelete(id);
            partner.setIsDelete(true);
            partnerRepository.save(partner);

            if(partner.getIsDelete().equals(true)){
                List<StaffUser> staffUser = staffUserService.getActiveStaffUserFromUsername(partner.getEmail());
                if(staffUser != null)
                {
                    if(!staffUser.isEmpty())
                        staffUserService.deleteStaffUser(staffUser.get(0).getId());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<Partner> getPartnerByServiceAreaId(Integer serviceAreaId) {
        QPartner qPartner = QPartner.partner;
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = new ArrayList<Integer>();
            serviceIDs.add(serviceAreaId);
            List<Integer> partnerId = partnerServiceAreaMappingRepo.partnerIdList(serviceIDs);
            booleanExpression = booleanExpression.and(qPartner.id.in(partnerId));
        }
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        if(getBUIdsFromCurrentStaff().size() !=0)
            booleanExpression = booleanExpression
                    .and(qPartner.mvnoId.eq(1)
                            .or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));

        List<Partner> list = (List<Partner>) partnerRepository.findAll(booleanExpression);
        list.add(partnerRepository.findById(1).get());
        return list;
    }


    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = partnerRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    count = partnerRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = partnerRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = partnerRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = partnerRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = partnerRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
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


    public Partner convertPartnerPojoToPartnerModel(PartnerPojo partnerPojo) throws Exception {
        String SUBMODULE = MODULE + " [convertPartnerPojoToPartnerModel()] ";
        Partner partner = null;
        try {
            if (partnerPojo != null) {
                partner = new Partner();
                if (partnerPojo.getId() != null) {
                    partner.setId(partnerPojo.getId());
                }

                partner.setBalance(partnerPojo.getOutcomeBalance());
                partner.setName(partnerPojo.getName());
                partner.setStatus(partnerPojo.getStatus());
                partner.setAddress1(partnerPojo.getAddress1());
                partner.setAddress2(partnerPojo.getAddress2());
                partner.setAddresstype(partnerPojo.getAddresstype());
                partner.setCredit(partnerPojo.getCredit());
                partner.setCity(partnerPojo.getCity());
                partner.setState(partnerPojo.getState());
                partner.setCountry(partnerPojo.getCountry());
                partner.setPincode(partnerPojo.getPincode());
                partner.setTaxid(partnerPojo.getTaxid());
                partner.setCommdueday(partnerPojo.getCommdueday());
                partner.setCommtype(partnerPojo.getCommissionShareType());
                partner.setNextbilldate(partnerPojo.getNextbilldate());
                partner.setEmail(partnerPojo.getEmail());
                partner.setMobile(partnerPojo.getMobile());
                partner.setCountryCode(partnerPojo.getCountryCode());
                partner.setLastbilldate(partnerPojo.getLastbilldate());
                partner.setIsDelete(partnerPojo.getIsDelete());
                partner.setCalendarType(partnerPojo.getCalendarType());
                partner.setPrcode(partnerPojo.getPrcode());
                partner.setPartnerType(partnerPojo.getPartnerType());
                partner.setPanName(partnerPojo.getPanName());
                partner.setCname(partnerPojo.getCname());
                partner.setCpName(partnerPojo.getCpName());
                partner.setBranch(partnerPojo.getBranch());
                partner.setBussinessvertical(partnerPojo.getBussinessvertical());
                partner.setRegion(partnerPojo.getRegion());
                partner.setCommissionInterval(partnerPojo.getCommissionInterval());

                if (partnerPojo.getMvnoId() != null) {
                    partner.setMvnoId(partnerPojo.getMvnoId());
                }
                if (partnerPojo.getBuId() != null) {
                    partner.setBuId(partnerPojo.getBuId());
                }
                if(Objects.nonNull(partnerPojo.getServiceAreaIds())) {
                    partner.setServiceAreaList(serviceAreaRepository.findAllById(partnerPojo.getServiceAreaIds()));
                }
                if (partnerPojo.getParentpartnerid() != null) {
                    partner.setParentPartner(this.get(partnerPojo.getParentpartnerid().intValue()));
                }
                if (partnerPojo.getPricebookId() != null) {
                    PriceBook1 priceBook = priceBookRepository.getOne(partnerPojo.getPricebookId());
                    partner.setPriceBookId(priceBook);
                }
                partner.setCommrelvalue(0.0);
                partner.setCommissionShareType(partnerPojo.getCommissionShareType());
                partner.setTotalCustomerCount(partnerPojo.getTotalCustomerCount());
                partner.setRenewCustomerCount(partnerPojo.getRenewCustomerCount());
                partner.setNewCustomerCount(partnerPojo.getNewCustomerCount());
                partner.setIsVisibleToIsp(partnerPojo.getIsVisibleToIsp());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return partner;
    }
    public PartnerPojo convertPartnerModelToPartnerPojo(Partner partner) throws Exception {
        String SUBMODULE = MODULE + " [convertPartnerModelToPartnerPojo()] ";
        PartnerPojo pojo = null;
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            if (partner != null) {
                pojo = new PartnerPojo();
                if (partner.getBalance() != null) {
                    pojo.setBalance(partner.getBalance());
                    pojo.setOutcomeBalance(partner.getBalance());
                } else {
                    pojo.setOutcomeBalance(0.0);
                    pojo.setBalance(0.0);
                }

                if(partner.getCommrelvalue()!=null)
                    pojo.setCommrelvalue(Double.parseDouble(df.format(partner.getCommrelvalue())));
                else
                    pojo.setCommrelvalue(0.0);
                pojo.setId(partner.getId());
                pojo.setName(partner.getName());
                pojo.setStatus(partner.getStatus());
                pojo.setAddress1(partner.getAddress1());
                pojo.setAddress2(partner.getAddress2());
                pojo.setAddresstype(partner.getAddresstype());
                pojo.setCredit(partner.getCredit());
                pojo.setCity(partner.getCity());
                pojo.setState(partner.getState());
                pojo.setCountry(partner.getCountry());
                pojo.setPincode(partner.getPincode());
                pojo.setTaxid(partner.getTaxid());
                pojo.setCommdueday(partner.getCommdueday());
                pojo.setCommtype(partner.getCommissionShareType());
                pojo.setNextbilldate(partner.getNextbilldate());
                pojo.setEmail(partner.getEmail());
                pojo.setMobile(partner.getMobile());
                pojo.setCountryCode(partner.getCountryCode());
                pojo.setLastbilldate(partner.getLastbilldate());
                pojo.setIsDelete(partner.getIsDelete());
                pojo.setCreatedById(partner.getCreatedById());
                pojo.setCreatedate(partner.getCreatedate());
                pojo.setCreatedByName(partner.getCreatedByName());
                pojo.setLastModifiedById(partner.getLastModifiedById());
                pojo.setLastModifiedByName(partner.getLastModifiedByName());
                pojo.setUpdatedate(partner.getUpdatedate());
                pojo.setCommissionShareType(partner.getCommissionShareType());
                pojo.setCalendarType(partner.getCalendarType());
                pojo.setTotalCustomerCount(partner.getTotalCustomerCount());
                pojo.setRenewCustomerCount(partner.getRenewCustomerCount());
                pojo.setNewCustomerCount(partner.getNewCustomerCount());
                pojo.setPrcode(partner.getPrcode());
                pojo.setPartnerType(partner.getPartnerType());
                pojo.setCname(partner.getCname());
                pojo.setCpName(partner.getCpName());
                pojo.setPanName(partner.getPanName());
                pojo.setCreditConsume(partner.getCreditConsume());
                pojo.setDisplayId(partner.getId());
                pojo.setDisplayName(partner.getName());
                pojo.setBranch(partner.getBranch());
                pojo.setRegion(partner.getRegion());
                pojo.setBussinessvertical(partner.getBussinessvertical());
                pojo.setCommissionInterval(partner.getCommissionInterval());
                pojo.setIsVisibleToIsp(partner.getIsVisibleToIsp());

                if (partner.getMvnoId() != null) {
                    pojo.setMvnoId(partner.getMvnoId());
                }
                if (partner.getBuId()!= null) {
                    pojo.setBuId(partner.getBuId());
                }
                if (partner.getPriceBookId() != null) {
                    Long priceBookId = partner.getPriceBookId().getId();
                    pojo.setPricebookId(priceBookId);
                    pojo.setPricebookname(partner.getPriceBookId().getBookname());
                }

                if (null != partner.getServiceAreaList() && 0 < partner.getServiceAreaList().size()) {
                    pojo.setServiceAreaIds(partner.getServiceAreaList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                    pojo.setServiceAreaNameList(partner.getServiceAreaList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
                }

                if (partner.getParentPartner() != null) {
                    pojo.setParentpartnerid(partner.getParentPartner().getId());
                    pojo.setParentPartnerName(partner.getParentPartner().getName());
                } else pojo.setParentPartnerName("-");

                if (null != partner.getCity()) {
                    City city = cityService.get(partner.getCity());
                    pojo.setCityName(null != city ? city.getName() : "-");
                } else pojo.setCityName("-");

                if (null != partner.getCountry()) {
                    Country country=countryRepository.findById(partner.getCountry()).get();
                    pojo.setCountryName(null != country ? country.getName() : "-");
                } else pojo.setCountryName("-");


                if (null != partner.getState()) {
                    State state = stateService.get(partner.getState());
                    pojo.setStateName(null != state ? state.getName() : "-");
                } else pojo.setStateName("-");

                if (null != partner.getTaxid()) {
                    Tax tax = taxService.get(partner.getTaxid());
                    pojo.setTaxName(null != tax ? tax.getName() : "-");
                } else {
                    pojo.setTaxName("-");
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<PartnerPojo> convertResponseModelIntoPojoOptimized(List<Partner> partners) throws Exception {
        if (partners == null || partners.isEmpty()) return Collections.emptyList();

        List<Integer> cityIds = partners.stream().map(Partner::getCity).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> stateIds = partners.stream().map(Partner::getState).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> countryIds = partners.stream().map(Partner::getCountry).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> taxIds = partners.stream().map(Partner::getTaxid).filter(Objects::nonNull).collect(Collectors.toList());

        Map<Long, String> cityMap = cityRepository.findIdNamePairs(cityIds)
                .stream().collect(Collectors.toMap(
                        m -> ((Number)m.get("id")).longValue(),
                        m -> (String)m.get("name")
                ));
        Map<Long, String> stateMap = stateRepository.findIdNamePairs(stateIds)
                .stream().collect(Collectors.toMap(
                        m -> ((Number)m.get("id")).longValue(),
                        m -> (String)m.get("name")
                ));
        Map<Long, String> countryMap = countryRepository.findIdNamePairs(countryIds)
                .stream().collect(Collectors.toMap(
                        m -> ((Number)m.get("id")).longValue(),
                        m -> (String)m.get("name")
                ));
        Map<Long, String> taxMap = taxRepository.findIdNamePairs(taxIds)
                .stream().collect(Collectors.toMap(
                        m -> ((Number)m.get("id")).longValue(),
                        m -> (String)m.get("name")
                ));

        DecimalFormat df = new DecimalFormat("0.00");
        List<PartnerPojo> pojoList = new ArrayList<>();

        for (Partner partner : partners) {
            PartnerPojo pojo = new PartnerPojo();

            pojo.setId(partner.getId());
            pojo.setName(partner.getName());
            pojo.setStatus(partner.getStatus());
            pojo.setBalance(Optional.ofNullable(partner.getBalance()).orElse(0.0));
            pojo.setOutcomeBalance(Optional.ofNullable(partner.getBalance()).orElse(0.0));
            pojo.setCommrelvalue(Optional.ofNullable(partner.getCommrelvalue()).map(v -> Double.parseDouble(df.format(v))).orElse(0.0));
            pojo.setAddress1(partner.getAddress1());
            pojo.setAddress2(partner.getAddress2());
            pojo.setAddresstype(partner.getAddresstype());
            pojo.setCredit(partner.getCredit());
            pojo.setCity(partner.getCity());
            pojo.setState(partner.getState());
            pojo.setCountry(partner.getCountry());
            pojo.setPincode(partner.getPincode());
            pojo.setTaxid(partner.getTaxid());
            pojo.setCommdueday(partner.getCommdueday());
            pojo.setCommtype(partner.getCommissionShareType());
            pojo.setNextbilldate(partner.getNextbilldate());
            pojo.setEmail(partner.getEmail());
            pojo.setMobile(partner.getMobile());
            pojo.setCountryCode(partner.getCountryCode());
            pojo.setLastbilldate(partner.getLastbilldate());
            pojo.setIsDelete(partner.getIsDelete());
            pojo.setCreatedById(partner.getCreatedById());
            pojo.setCreatedate(partner.getCreatedate());
            pojo.setCreatedByName(partner.getCreatedByName());
            pojo.setLastModifiedById(partner.getLastModifiedById());
            pojo.setLastModifiedByName(partner.getLastModifiedByName());
            pojo.setUpdatedate(partner.getUpdatedate());
            pojo.setCommissionShareType(partner.getCommissionShareType());
            pojo.setCalendarType(partner.getCalendarType());
            pojo.setTotalCustomerCount(partner.getTotalCustomerCount());
            pojo.setRenewCustomerCount(partner.getRenewCustomerCount());
            pojo.setNewCustomerCount(partner.getNewCustomerCount());
            pojo.setPrcode(partner.getPrcode());
            pojo.setPartnerType(partner.getPartnerType());
            pojo.setCname(partner.getCname());
            pojo.setCpName(partner.getCpName());
            pojo.setPanName(partner.getPanName());
            pojo.setCreditConsume(partner.getCreditConsume());
            pojo.setDisplayId(partner.getId());
            pojo.setDisplayName(partner.getName());
            pojo.setBranch(partner.getBranch());
            pojo.setRegion(partner.getRegion());
            pojo.setBussinessvertical(partner.getBussinessvertical());
            pojo.setCommissionInterval(partner.getCommissionInterval());
            pojo.setIsVisibleToIsp(partner.getIsVisibleToIsp());

            if (partner.getMvnoId() != null) pojo.setMvnoId(partner.getMvnoId());
            if (partner.getBuId() != null) pojo.setBuId(partner.getBuId());
            if (partner.getPriceBookId() != null) {
                pojo.setPricebookId(partner.getPriceBookId().getId());
                pojo.setPricebookname(partner.getPriceBookId().getBookname());
            }

            if (partner.getServiceAreaList() != null && !partner.getServiceAreaList().isEmpty()) {
                pojo.setServiceAreaIds(partner.getServiceAreaList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                pojo.setServiceAreaNameList(partner.getServiceAreaList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
            }

            if (partner.getParentPartner() != null) {
                pojo.setParentpartnerid(partner.getParentPartner().getId());
                pojo.setParentPartnerName(partner.getParentPartner().getName());
            } else {
                pojo.setParentPartnerName("-");
            }

            pojo.setCityName(cityMap.getOrDefault(partner.getCity(), "-"));
            pojo.setStateName(stateMap.getOrDefault(partner.getState(), "-"));
            pojo.setCountryName(countryMap.getOrDefault(partner.getCountry(), "-"));
            pojo.setTaxName(taxMap.getOrDefault(partner.getTaxid(), "-"));

            pojoList.add(pojo);
        }

        return pojoList;
    }




    @Transient
    public void createInvoiceFunctionForPartner(PartnerPojo pojo) {
        try {
            String queryForInsertSequence = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoiceno_"+pojo.getId()+"', 1, 1,9999999,1);";

            String queryForInvoiceNo =  "CREATE FUNCTION `nextval_"+pojo.getId()+"` (`seq_name` varchar(100))" +
                    "RETURNS bigint " +
                    "BEGIN" +
                    "    DECLARE cur_val bigint;" +
                    "    SELECT" +
                    "        cur_value INTO cur_val" +
                    "    FROM" +
                    "        sequence"+
                    "    WHERE" +
                    "        name = seq_name;" +
                    "    IF cur_val IS NOT NULL THEN" +
                    "        UPDATE" +
                    "            sequence" +
                    "        SET" +
                    "            cur_value = IF (\n" +
                    "                (cur_value + increment) > max_value OR (cur_value + increment) < min_value," +
                    "                IF (" +
                    "                    cycle = TRUE," +
                    "                    IF (" +
                    "                        (cur_value + increment) > max_value," +
                    "                        min_value, " +
                    "                        max_value " +
                    "                    )," +
                    "                    NULL" +
                    "                )," +
                    "                cur_value + increment" +
                    "            )" +
                    "        WHERE" +
                    "            name = seq_name;" +
                    "    END IF; " +
                    "    RETURN cur_val;" +
                    "END;";
            jdbcTemplate.execute(queryForInvoiceNo);
            jdbcTemplate.execute(queryForInsertSequence);
        } catch (Exception ex) {
            System.out.println("Error to create Partner Invoice no function"+ex.getMessage());
        }
    }


    public void sendCreateDataShared(Integer id, PartnerPojo pojo, Integer operation) throws Exception {
        try {
            Partner partnerEntity = convertPartnerPojoToPartnerModel(pojo);
            partnerEntity.setCreatedById(getLoggedInUserId());
            partnerEntity.setLastModifiedById(getLoggedInUserId());
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(partnerEntity);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(partnerEntity,pojo);
            }
//            else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
//                Partner deletePartnerEntity = getEntityForUpdateAndDelete(id);
//                createDataSharedService.deleteEntityDataForAllMicroService(deletePartnerEntity);
//            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public void sendDeletePartnerDataShared(Integer id) throws Exception {
        try {
            Partner deletePartnerEntity = getEntityForUpdateAndDelete(id);
            deletePartnerEntity.setCreatedById(getLoggedInUserId());
            deletePartnerEntity.setLastModifiedById(getLoggedInUserId());
            createDataSharedService.deleteEntityDataForAllMicroService(deletePartnerEntity);
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }


    public Partner getEntityForUpdateAndDelete(Integer id) {
        Partner partner = get(id);
        if(partner == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == partner.getMvnoId().intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return partner;
    }


    @Override
    public Partner get(Integer id) {
        Partner partner = super.get(id);
        if(getBUIdsFromCurrentStaff() != null && getMvnoIdFromCurrentStaff() != null) {
            if (getMvnoIdFromCurrentStaff() == 1 || (partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || partner.getMvnoId() == 1) && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1))
                return partner;
        } else {
            return partner;
        }
        return null;
    }


    public LocalDate getResetDate(String calendarType, LocalDate currentDate) {
        LocalDate resetDate = currentDate.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear()));
        if(calendarType.equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
            LocalDateTime date = LocalDateTime.now();
            String currentDateAndTime = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
            NepaliDateDTO resetDateDTO = dateConverterService.getNepaliDateFromEnglishDate(currentDateAndTime);
            int monthVal = dateConverterService.getDaysInMonth(resetDateDTO.getSaal(), resetDateDTO.getMahina());
            resetDate = resetDate.plusDays(monthVal - currentDate.getMonth().length(currentDate.isLeapYear()));
        }
        return resetDate;
    }

    public List<PartnerPojo> convertResponseModelIntoPojo(List<Partner> partnerList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<PartnerPojo> pojoListRes = new ArrayList<PartnerPojo>();
        try {
            if (partnerList != null && partnerList.size() > 0) {
                for (Partner partner : partnerList) {
                    pojoListRes.add(convertPartnerModelToPartnerPojo(partner));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }


    public List<Partner> getAllEntities(String type) {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            List<Integer>partnerIdList=new ArrayList<>();
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.toLowerCase().eq(CommonConstants.ACTIVE_STATUS.toLowerCase()));
            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();

                if (type.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                    aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_LCO));

                if (type.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_FRANCHISE))
                    aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_FRANCHISE));
                if (getLoggedInMvnoId() != 1) {
                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().isEmpty())
                        aBoolean = aBoolean.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    else
                        aBoolean = aBoolean.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff())).and(qPartner.buId.in(getBUIdsFromCurrentStaff()));
                }
                if (serviceIDs != null && !serviceIDs.isEmpty())
                    partnerIdList=query.distinct().select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs) ).fetch();
                else
                    partnerIdList=query.distinct().select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.notIn(serviceIDs) ).fetch();
            }
            aBoolean = aBoolean.or(qPartner.id.in(1));
            List<Integer> finalPartnerIdList = partnerIdList;
            return IterableUtils.toList(partnerRepository.findAll(aBoolean)).stream().filter(i-> finalPartnerIdList.contains(i.getId())).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<Partner> getAllEntities() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();
                if(serviceIDs!=null && !serviceIDs.isEmpty())
                    aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }

            if(!getLoggedInUser().getLco())
                aBoolean = aBoolean.and(qPartner.partnerType.ne(CommonConstants.PARTNER_TYPE_LCO));

            aBoolean = aBoolean.or(qPartner.id.in(1));
            return IterableUtils.toList(partnerRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() &&  (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PartnerPojo> getAllPartnersAsPojo() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;

            JPAQuery<PartnerPojo> query = new JPAQuery<>(entityManager);

            BooleanExpression predicate = qPartner.isNotNull()
                    .and(qPartner.isDelete.eq(false))
                    .and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));

            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();
                if (serviceIDs != null && !serviceIDs.isEmpty()) {
                    predicate = predicate.and(qPartner.id.in(
                            JPAExpressions.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping)
                                    .where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))
                    ));
                }
            }

            if (!getLoggedInUser().getLco()) {
                predicate = predicate.and(qPartner.partnerType.ne(CommonConstants.PARTNER_TYPE_LCO));
            }

            predicate = predicate.or(qPartner.id.eq(1));

            List<PartnerPojo> result = query.select(Projections.bean(
                            PartnerPojo.class,
                            qPartner.id,
                            qPartner.name,
                            qPartner.status,
                            qPartner.partnerType,
                            qPartner.commrelvalue,
                            qPartner.balance,
                            qPartner.nextbilldate,
                            qPartner.lastbilldate,
                            qPartner.taxid,
                            qPartner.credit,
                            qPartner.address1,
                            qPartner.address2,
                            qPartner.city,
                            qPartner.state,
                            qPartner.country,
                            qPartner.pincode,
                            qPartner.mobile,
                            qPartner.countryCode,
                            qPartner.mvnoId,
                            qPartner.buId,
                            qPartner.id.as("displayId"),
                            qPartner.name.as("displayName"),
                            qPartner.region,
                            qPartner.branch,
                            qPartner.bussinessvertical,
                            qPartner.isVisibleToIsp,
                            qPartner.commissionInterval,
                            qPartner.commissionShareType,
                            qPartner.calendarType,
                            qPartner.resetDate,
                            qPartner.createdate,
                            qPartner.updatedate,
                            qPartner.totalCustomerCount,
                            qPartner.renewCustomerCount,
                            qPartner.newCustomerCount,
                            qPartner.creditConsume
                    ))
                    .from(qPartner)
                    .where(predicate)
                    .fetch();

            return result.stream()
                    .filter(partner ->
                            partner.getMvnoId() == 1
                                    || getMvnoIdFromCurrentStaff() == 1
                                    || (partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()
                                    && (partner.getMvnoId() == 1
                                    || getBUIdsFromCurrentStaff().isEmpty()
                                    || getBUIdsFromCurrentStaff().contains(partner.getBuId())
                                    || partner.getId() == 1))
                    )
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--Error while getting partner pojo list: " + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<Partner> getAllActiveEntities() {
        List<Partner> partners=partnerRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || partner.getMvnoId() == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()
                && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()))).collect(Collectors.toList());
        if(!super.getLoggedInUser().getLco())
            partners=partners.stream().filter(x->!x.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO)).collect(Collectors.toList());
        return partners;
    }


    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = partnerRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    count = partnerRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = partnerRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


    public Page<Partner> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        List<Integer>serviareaId=getLoggedInUser().getServiceAreaIdList();
        if (null == filterList || 0 == filterList.size())
            if(getMvnoIdFromCurrentStaff() == 1)
                return partnerRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            {
                if(serviareaId!=null && !serviareaId.isEmpty())
                    return partnerRepository.findAll(pageRequest,serviareaId ,Arrays.asList(1, getMvnoIdFromCurrentStaff()));
                else
                    return partnerRepository.findAll(pageRequest,Arrays.asList(1, getMvnoIdFromCurrentStaff()));
            }
            else
                return partnerRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
        else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }

    public Page<PartnerPojo> getListPartnerPojo(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(Sort.Direction.DESC, "id"));
        List<Integer>serviareaId=getLoggedInUser().getServiceAreaIdList();
        if (null == filterList || 0 == filterList.size())
            if(getMvnoIdFromCurrentStaff() == 1)
                return partnerRepository.findAllPartnerPojo(pageRequest);
        if (null == filterList || 0 == filterList.size())
            if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            {
                if(serviareaId!=null && !serviareaId.isEmpty())
                    return partnerRepository.findAllPartnerPojo(pageRequest,serviareaId ,Arrays.asList(1, getMvnoIdFromCurrentStaff()));
                else
                    return partnerRepository.findAllPartnerPojo(pageRequest,Arrays.asList(1, getMvnoIdFromCurrentStaff()));
            }
            else
                return partnerRepository.findAllPartnerPojo(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
        else return searchPartnerPojo(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }


    public Page<Partner> getChildPartnerList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer partnerId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        return partnerRepository.findAll(pageRequest,partnerId);
    }


    @Override
    public Page<Partner> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    return getPartnerByNameOrEmailOrMobile(searchModel.getFilterValue(), searchModel.getFilterValue(), searchModel.getFilterValue(), pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<PartnerPojo> searchPartnerPojo(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    return getPartnerByNameOrEmailOrMobilePartnerPojo(searchModel.getFilterValue(), searchModel.getFilterValue(), searchModel.getFilterValue(), pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }


    public Page<Partner> getPartnerByNameOrEmailOrMobile(String s1, String s2, String s3, PageRequest pageRequest) {
        QPartner qPartner = QPartner.partner;
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
        if (!s1.isEmpty()) {
            QCountry qCountry = QCountry.country;
            BooleanExpression expression = qCountry.isNotNull().and(qCountry.isDelete.eq(false)).and(qCountry.name.like("%" + s1 + "%"));
            if(getMvnoIdFromCurrentStaff() != 1)
                expression = expression.and(qCountry.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            List<Country> countries=(List<Country>) countryRepository.findAll(expression);
            List<Integer> countyCodeList=countries.stream().map(x->x.getId()).collect(Collectors.toList());
            booleanExpression = booleanExpression.and((qPartner.name.likeIgnoreCase("%" + s1 + "%").or(qPartner.mobile.eq("%" + s1 + "%").or(qPartner.email.like("%" + s1 + "%")).or(qPartner.country.in(countyCodeList)).or(qPartner.commissionShareType.like("%" + s1 + "%")).or(qPartner.status.like("%" + s1 + "%")))));
        }

        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        if(getBUIdsFromCurrentStaff().size() !=0)
            booleanExpression = booleanExpression.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));
        return partnerRepository.findAll(booleanExpression, pageRequest);
    }

    public Page<PartnerPojo> getPartnerByNameOrEmailOrMobilePartnerPojo(String s1, String s2, String s3, PageRequest pageRequest) {
        QPartner qPartner = QPartner.partner;
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        QPartner qParentPartner = new QPartner("parentPartner");
        QPriceBook1 qPriceBook = QPriceBook1.priceBook1;

        QCity qCity = QCity.city;
        QState qState = QState.state;
        QCountry qCountry = QCountry.country;
        QTax qTax = QTax.tax;

        BooleanExpression predicate = qPartner.isNotNull().and(qPartner.isDelete.eq(false));

        if (s1 != null && !s1.isEmpty()) {
            predicate = predicate.and(
                    qPartner.name.containsIgnoreCase(s1)
                            .or(qPartner.email.containsIgnoreCase(s1))
                            .or(qPartner.mobile.containsIgnoreCase(s1))
            );
        }

        if (getMvnoIdFromCurrentStaff() != 1) {
            predicate = predicate.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        }

        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            predicate = predicate.and(
                    qPartner.mvnoId.eq(1)
                            .or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff())
                                    .and(qPartner.buId.in(getBUIdsFromCurrentStaff())))
            );
        }

        JPAQuery<PartnerPojo> query = new JPAQuery<>(entityManager);

        query.from(qPartner)
                .leftJoin(qPartner.serviceAreaList, qServiceArea)
                .leftJoin(qPartner.parentPartner, qParentPartner)
                .leftJoin(qPartner.priceBookId, qPriceBook)
                .where(predicate)
                .orderBy(qPartner.name.asc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        StringExpression cityName = (StringExpression) JPAExpressions.select(qCity.name).from(qCity).where(qCity.id.eq(qPartner.city));
        StringExpression stateName = (StringExpression) JPAExpressions.select(qState.name).from(qState).where(qState.id.eq(qPartner.state));
        StringExpression countryName = (StringExpression) JPAExpressions.select(qCountry.name).from(qCountry).where(qCountry.id.eq(qPartner.country));
        StringExpression taxName = (StringExpression) JPAExpressions.select(qTax.name).from(qTax).where(qTax.id.eq(qPartner.taxid));

        query.select(Projections.constructor(
                PartnerPojo.class,
                qPartner.id,
                qPartner.name,
                qPartner.status,
                qPartner.commtype,
                qPartner.commrelvalue,
                qPartner.balance,
                qPartner.commdueday,
                qPartner.nextbilldate,
                qPartner.lastbilldate,
                qPartner.taxid,
                qPartner.credit,
                qPartner.addresstype,
                qPartner.address1,
                qPartner.address2,
                qPartner.city,
                qPartner.state,
                qPartner.country,
                qPartner.pincode,
                qPartner.mobile,
                qPartner.countryCode,
                qPartner.prcode,
                qPartner.partnerType,
                qPartner.email,
                qPartner.parentPartner.id,
                qPartner.isDelete,
                qPartner.priceBookId.id,
                qPartner.calendarType,
                qPartner.commissionShareType,
                qPartner.mvnoId,
                qPartner.buId,
                qPartner.creditConsume,
                qPartner.id.as("displayId"),
                qPartner.name.as("displayName"),
                qPartner.region,
                qPartner.branch,
                qPartner.bussinessvertical,
                qPartner.commissionInterval,
                qPartner.isVisibleToIsp,
                qPartner.createdate,
                qPartner.updatedate,
                cityName,
                countryName,
                stateName,
                taxName,
                qParentPartner.name.as("parentPartnerName"),
                qPartner.balance.as("outcomeBalance"),
                qPartner.totalCustomerCount,
                qPartner.renewCustomerCount,
                qPartner.newCustomerCount,
                qPriceBook.bookname.as("pricebookname"),
                qServiceArea.id.as("serviceAreaId"),
                qServiceArea.name.as("serviceAreaName")
        ));

        List<PartnerPojo> results = query.fetch();

        Map<Integer, PartnerPojo> merged = new LinkedHashMap<>();
        for (PartnerPojo pojo : results) {
            merged.computeIfAbsent(pojo.getId(), k -> {
                pojo.setServiceAreaIds(new ArrayList<>());
                pojo.setServiceAreaNameList(new ArrayList<>());
                return pojo;
            });
            merged.get(pojo.getId()).getServiceAreaIds().add(Long.valueOf(pojo.getServiceAreaIds().toString()));
            merged.get(pojo.getId()).getServiceAreaNameList().add(pojo.getServiceAreaNameList().toString());
        }

        List<PartnerPojo> finalList = new ArrayList<>(merged.values());
        long total = merged.size();

        return new PageImpl<>(finalList, pageRequest, total);
    }


    public void updateAmount(PartnerAmountMessage message) {
        Partner partner=partnerRepository.findById(message.getPartnerId()).orElse(null);
        if(partner!=null){
            partner.setCreditConsume(message.getCreditconsume());
            partner.setBalance(message.getBalance());
            if(message.getComrelval()!=null && message.getComrelval().doubleValue()<0.02d)
                partner.setCommrelvalue(0.0d);
            partner.setCommrelvalue(message.getComrelval());
            partner.setCredit(message.getCredit());
            if(message.getRenewcust_count()!=null)
                partner.setRenewCustomerCount(message.getRenewcust_count().longValue());
            else
                partner.setRenewCustomerCount(0l);
            if(message.getNewCustomer_count()!=null)
                partner.setNewCustomerCount(message.getNewCustomer_count().longValue());
            else
                partner.setNewCustomerCount(0l);

            partner.setTotalCustomerCount(partner.getNewCustomerCount()+partner.getRenewCustomerCount());
            partnerRepository.save(partner);
        }
    }


    public List<Partner> getAllParentPartners(Integer id) {
        return partnerRepository.getAllParentPartners(id).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff() || partner.getMvnoId() == null).collect(Collectors.toList());
    }


    public List<PartnerPojo> searchPartner(String searchText) {
        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
        try {
            List<Partner> partnerList = partnerRepository.searchPartner(searchText, searchText, searchText).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff() || partner.getMvnoId() == null).collect(Collectors.toList());
            if (null != partnerList && 0 < partnerList.size()) {
                return partnerList.stream().map(data -> {
                    try {
                        return convertPartnerModelToPartnerPojo(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<PartnerCreditDocument> getByLcoId(Integer partnerId) {
        List<PartnerCreditDocument> partnerCreditDocuments = partnerCreditDocumentRepository.getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(partnerId, "CREDITNOTE", "creditnote");
        //setting invoice number
        for(int i = 0; i<partnerCreditDocuments.size();i++){
            QPartnerDebitDocument qPartnerDebitDocument = QPartnerDebitDocument.partnerDebitDocument;
            BooleanExpression booleanExpression = qPartnerDebitDocument.isDelete.eq(false).and(qPartnerDebitDocument.id.in(partnerCreditDocuments.get(i).getInvoiceId()));
            PartnerDebitDocument partnerDebitDocument = partnerCreditDocRepository.findOne(booleanExpression).get();
            partnerCreditDocuments.get(i).setInvoiceNumber(partnerDebitDocument.getDocnumber());
        }
        return partnerCreditDocuments;
    }


    public List<PartnerDebitDocument> getByPartnerId(Integer partnerId) {
        Partner partners = this.get(partnerId);
        List<PartnerDebitDocument> partnerCreditDocuments = partnerCreditDocRepository.getAllByPartner(partners);
        return partnerCreditDocuments;
    }


    public boolean isEmailAvailable(String emailId) {
        Integer count = partnerRepository.emailCount(emailId);
        if(count>=1){
            return false;
        }
        return true;
    }


    public boolean isPartnerUsedAsParentPartner(Partner partner) {
        if(partner!=null)
        {
            List<Partner> childPartnerList=partnerRepository.getAllChildPartners(partner.getId());
            childPartnerList=childPartnerList.stream().filter(x->x.getIsDelete().equals(false) && x.getPartnerType().equalsIgnoreCase("Franchise")).collect(Collectors.toList());
            if(childPartnerList!=null && !childPartnerList.isEmpty())
                return true;
            else
                return false;
        }
        return false;
    }


    public List<PartnerHierarchy> getPartnerHierarchyList(Integer childPartnerId) {
        List<PartnerHierarchy> partnerHierarchyList=new ArrayList<>();
        if(childPartnerId!=null){
            Partner parentPartner=partnerRepository.findById(childPartnerId).orElse(null);

            while(parentPartner!=null && parentPartner.getParentPartner()!=null)
                parentPartner=parentPartner.getParentPartner();

            List<PartnerHierarchy> hierarchy=getChildPartnerHierarchyList(parentPartner.getId());
            String parentPartnerName=null;

            if(parentPartner!=null && parentPartner.getParentPartner()==null)
                parentPartnerName=parentPartner.getName();

            if(parentPartnerName!=null)
                partnerHierarchyList.add(new PartnerHierarchy(parentPartnerName,true,hierarchy));
            else
                return hierarchy;
        }
        return partnerHierarchyList;
    }


    public List<PartnerHierarchy> getChildPartnerHierarchyList(Integer parentPartnerId) {
        List<Partner> childPartnerList=new ArrayList<>();
        List<PartnerHierarchy> children=new ArrayList<>();
        if(parentPartnerId!=null)
        {
            Partner partner=partnerRepository.findById(parentPartnerId).orElse(null);
            if(partner!=null)
            {
                childPartnerList=partnerRepository.getAllChildPartners(parentPartnerId);
                childPartnerList.stream().forEach(x->{children.add(new PartnerHierarchy(x.getName(),true,getChildPartnerHierarchyList(x.getId())));});
            }
        }
        return children;
    }

    public Boolean canPartnerDelete(Integer id) {
        Long count=customerService.getCustomerCount(id);
        if(count!=null && count>0)
            return true;
        return false;
    }

    public void updateChildPartnerBundle(Partner partner, PriceBook1 priceBook1) {
        List<Partner> childPartners=partnerRepository.getAllChildPartners(partner.getId());
        childPartners.forEach(childPartner -> {
            childPartner.setPriceBookId(priceBook1);
            partnerRepository.save(childPartner);
            try {
                PartnerPojo partnerPojo=convertPartnerModelToPartnerPojo(childPartner);
                sendCreateDataShared(partnerPojo.getId(), partnerPojo, CommonConstants.OPERATION_UPDATE);
                updateChildPartnerBundle(childPartner,priceBook1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

public Page<Partner> searchByColumns(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.NAME)) {
                    return getPartnerByFilter(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.EMAIL)) {
                    return getPartnerByFilter(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.COMMTYPE)) {
                    return getPartnerByFilter(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.COUNTRY)) {
                    return getPartnerByFilter(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.STATUS)) {
                    return getPartnerByFilter(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }


    public Page<PartnerPojo> searchByColumnsPartnerPojo(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.NAME)) {
                    return getPartnerByFilterPojo(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.EMAIL)) {
                    return getPartnerByFilterPojo(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.COMMTYPE)) {
                    return getPartnerByFilterPojo(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.COUNTRY)) {
                    return getPartnerByFilterPojo(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.STATUS)) {
                    return getPartnerByFilterPojo(searchModel.getFilterColumn(), searchModel.getFilterValue(), pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<Partner> getPartnerByFilter(String filterColumn, String filterValue, PageRequest pageRequest) {
        QPartner qPartner = QPartner.partner;
        JPAQuery<Partner> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
        if (filterValue != null && !filterValue.isEmpty()) {
            switch (filterColumn.toLowerCase()) {
                case "name":
                    booleanExpression = booleanExpression.and(qPartner.name.likeIgnoreCase("%" + filterValue + "%"));
                    break;
                case "email":
                    booleanExpression = booleanExpression.and(qPartner.email.likeIgnoreCase("%" + filterValue + "%"));
                    break;
                case "country":
                    QCountry qCountry = QCountry.country;
                    BooleanExpression countryExpression = qCountry.isNotNull().and(qCountry.isDelete.eq(false)).and(qCountry.name.like("%" + filterValue + "%"));
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        countryExpression = countryExpression.and(qCountry.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                    List<Country> countries = (List<Country>) countryRepository.findAll(countryExpression);
                    List<Integer> countryCodeList = countries.stream().map(Country::getId).collect(Collectors.toList());
                    booleanExpression = booleanExpression.and(qPartner.country.in(countryCodeList));
                    break;
                case "commtype":
                    booleanExpression = booleanExpression.and(qPartner.commissionShareType.likeIgnoreCase("%" + filterValue + "%"));
                    break;
                case "status":
                    booleanExpression = booleanExpression.and(qPartner.status.likeIgnoreCase("%" + filterValue + "%"));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid filter column: " + filterColumn);
            }
        }
        if (getMvnoIdFromCurrentStaff() != 1) {
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        }
        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qPartner.mvnoId.eq(1)
                    .or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff())
                            .and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));
        }
        return partnerRepository.findAll(booleanExpression, pageRequest);
    }

    public Page<PartnerPojo> getPartnerByFilterPojo(String filterColumn, String filterValue, PageRequest pageRequest) {
        QPartner qPartner = QPartner.partner;
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        QPartner qParentPartner = new QPartner("parentPartner");
        QPriceBook1 qPriceBook = QPriceBook1.priceBook1;

        QCity qCity = QCity.city;
        QState qState = QState.state;
        QCountry qCountry = QCountry.country;
        QTax qTax = QTax.tax;

        BooleanExpression predicate = qPartner.isNotNull().and(qPartner.isDelete.eq(false));

        if (filterValue != null && !filterValue.isEmpty()) {
            switch (filterColumn.toLowerCase()) {
                case "name":
                    predicate = predicate.and(qPartner.name.containsIgnoreCase(filterValue));
                    break;
                case "email":
                    predicate = predicate.and(qPartner.email.containsIgnoreCase(filterValue));
                    break;
                case "country":
                    Iterable<Country> countriesIterable = countryRepository.findAll(
                            QCountry.country.isNotNull()
                                    .and(QCountry.country.isDelete.eq(false))
                                    .and(QCountry.country.name.contains(filterValue))
                                    .and(getMvnoIdFromCurrentStaff() != 1
                                            ? QCountry.country.mvnoId.in(getMvnoIdFromCurrentStaff(), 1)
                                            : null)
                    );
                    List<Integer> countryCodeList = StreamSupport.stream(countriesIterable.spliterator(), false)
                            .map(Country::getId)
                            .collect(Collectors.toList());

                    predicate = predicate.and(qPartner.country.in(countryCodeList));
                    break;
                case "commtype":
                    predicate = predicate.and(qPartner.commissionShareType.containsIgnoreCase(filterValue));
                    break;
                case "status":
                    predicate = predicate.and(qPartner.status.containsIgnoreCase(filterValue));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid filter column: " + filterColumn);
            }
        }

        if (getMvnoIdFromCurrentStaff() != 1) {
            predicate = predicate.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        }

        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            predicate = predicate.and(
                    qPartner.mvnoId.eq(1)
                            .or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff())
                                    .and(qPartner.buId.in(getBUIdsFromCurrentStaff())))
            );
        }

        JPAQuery<PartnerPojo> query = new JPAQuery<>(entityManager);

        query.from(qPartner)
                .leftJoin(qPartner.serviceAreaList, qServiceArea)
                .leftJoin(qPartner.parentPartner, qParentPartner)
                .leftJoin(qPartner.priceBookId, qPriceBook)
                .where(predicate)
                .orderBy(qPartner.id.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        StringExpression cityName = Expressions.stringTemplate(
                "(select c.name from City c where c.id = {0})", qPartner.city);
        StringExpression stateName = Expressions.stringTemplate(
                "(select s.name from State s where s.id = {0})", qPartner.state);
        StringExpression countryName = Expressions.stringTemplate(
                "(select co.name from Country co where co.id = {0})", qPartner.country);
        StringExpression taxName = Expressions.stringTemplate(
                "(select t.name from Tax t where t.id = {0})", qPartner.taxid);

        query.select(Projections.constructor(
                PartnerPojo.class,
                qPartner.id,
                qPartner.name,
                qPartner.status,
                qPartner.commtype,
                qPartner.commrelvalue,
                qPartner.balance,
                qPartner.commdueday,
                qPartner.nextbilldate,
                qPartner.lastbilldate,
                qPartner.taxid,
                qPartner.credit,
                qPartner.addresstype,
                qPartner.address1,
                qPartner.address2,
                qPartner.city,
                qPartner.state,
                qPartner.country,
                qPartner.pincode,
                qPartner.mobile,
                qPartner.countryCode,
                qPartner.prcode,
                qPartner.partnerType,
                qPartner.email,
                qPartner.parentPartner.id,
                qPartner.isDelete,
                qPartner.priceBookId.id,
                qPartner.calendarType,
                qPartner.commissionShareType,
                qPartner.mvnoId,
                qPartner.buId,
                qPartner.creditConsume,
                qPartner.id.as("displayId"),
                qPartner.name.as("displayName"),
                qPartner.region,
                qPartner.branch,
                qPartner.bussinessvertical,
                qPartner.commissionInterval,
                qPartner.isVisibleToIsp,
                qPartner.createdate,
                qPartner.updatedate,
                cityName,
                countryName,
                stateName,
                taxName,
                qParentPartner.name.as("parentPartnerName"),
                qPartner.balance.as("outcomeBalance"),
                qPartner.totalCustomerCount,
                qPartner.renewCustomerCount,
                qPartner.newCustomerCount,
                qPriceBook.bookname.as("pricebookname"),
                qServiceArea.id,
                qServiceArea.name
                ));

        List<PartnerPojo> results = query.fetch();

        Map<Integer, PartnerPojo> merged = new LinkedHashMap<>();

        for (PartnerPojo pojo : results) {
            merged.computeIfAbsent(pojo.getId(), k -> {
                pojo.setServiceAreaIds(new ArrayList<>());
                pojo.setServiceAreaNameList(new ArrayList<>());
                return pojo;
            });

            if (pojo.getServiceAreaId() != null) {
                merged.get(pojo.getId()).getServiceAreaIds().add(pojo.getServiceAreaId());
            }
            if (pojo.getServiceAreaName() != null) {
                merged.get(pojo.getId()).getServiceAreaNameList().add(pojo.getServiceAreaName());
            }
        }

        List<PartnerPojo> finalList = new ArrayList<>(merged.values());
        long total = merged.size();

        return new PageImpl<>(finalList, pageRequest, total);
    }


    public PartnerPojo getPartnerPojoById(Integer id) {
        List<PartnerPojo> rows = partnerRepository.findPartnerPojoListById(id);
        if (rows.isEmpty()) return null;

        PartnerPojo base = rows.get(0);

        List<Long> serviceAreaIds = rows.stream()
                .map(PartnerPojo::getServiceAreaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> serviceAreaNames = rows.stream()
                .map(PartnerPojo::getServiceAreaName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        base.setServiceAreaIds(serviceAreaIds);
        base.setServiceAreaNameList(serviceAreaNames);

        return base;
    }


}

