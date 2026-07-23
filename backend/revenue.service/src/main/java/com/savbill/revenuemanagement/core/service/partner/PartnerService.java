package com.savbill.revenuemanagement.core.service.partner;


import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.SubscriberConstants;
import com.savbill.revenuemanagement.core.dto.common.ResponseObject;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.repository.PriceBookRepository;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.nepaliCalendarUtils.service.DateConverterService;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.*;
import com.savbill.revenuemanagement.core.repository.partner.*;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.common.NumberSequenceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.prepaid.PartnerCommissionService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.kafka.KafkaConstant;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.PartnerAmountMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SavePartnerSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdatePartnerSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.SaveVoucherBatchSharedDataMessage;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The type Partner service.
 */
@Service
public class PartnerService extends ExBaseAbstractService<PartnerPojo, Partner, Integer> {
    /**
     * Instantiates a new Partner service.
     * @param repository the repository
     * @param mapper the mapper
     */
    public PartnerService(PartnerRepository repository, PartnerMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerService]";
    }

    private static String MODULE = " [PartnerService] ";
    /**
     * The Partner repository.
     */
    @Autowired
    PartnerRepository partnerRepository;
    /**
     * The Staff user service.
     */
    @Autowired
    StaffUserService staffUserService;
    /**
     * The Price book repository.
     */
    @Autowired
    PriceBookRepository priceBookRepository;
    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;
    /**
     * The Debit doc repository.
     */
    @Autowired
    DebitDocRepository debitDocRepository;
    /**
     * The Credit doc service.
     */
    @Autowired
    CreditDocService creditDocService;
    /**
     * The Credit debt mapping repository.
     */
    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;
    /**
     * The Credit doc repository.
     */
    @Autowired
    CreditDocRepository creditDocRepository;
    /**
     * The Partner commission service.
     */
    @Autowired
    PartnerCommissionService partnerCommissionService;
    /**
     * The Partner ledger details service.
     */
    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;
    /**
     * The Customers repository.
     */
    @Autowired
    CustomersRepository customersRepository;
    /**
     * The Temp partner ledger details repository.
     */
//@Autowired
    //MessageSender messageSender;
    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;
    /**
     * The Partner debit doc repository.
     */
    @Autowired
    PartnerDebitDocRepository partnerDebitDocRepository;
    /**
     * The Partner credit doc repository.
     */
    @Autowired
    PartnerCreditDocRepository partnerCreditDocRepository;
    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private DateConverterService dateConverterService;

    /**
     * The Kafka message sender.
     */
    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private SchedulerLockService schedulerLockService;


    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;
    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    /**
     * Save partner entiry.
     * @param message the message
     * @throws Exception the exception
     */
    public void savePartnerEntiry(SavePartnerSharedDataMessage message) throws Exception {
        try {
            Partner partner = new Partner();
            partner.setId(message.getId());
            partner.setName(message.getName());
            partner.setStatus(message.getStatus());
            partner.setCity(message.getCity());
            partner.setCountry(message.getCountry());
            partner.setState(message.getState());
            partner.setPincode(message.getPincode());
            partner.setEmail(message.getEmail());
            partner.setPartnerType(message.getPartnerType());
            if (message.getParentPartnerId() != null) {
                Partner parentPartner = partnerRepository.findById(message.getParentPartnerId()).orElse(null);
                partner.setParentPartner(parentPartner);
            }
            partner.setIsDelete(message.getIsDelete());
            partner.setCreatedById(message.getCreatedById());
            partner.setLastModifiedById(message.getLastModifiedById());
            partner.setBuId(message.getBuId());
            partner.setMvnoId(message.getMvnoId());
            partner.setBranch(message.getBranch());
            partner.setMobile(message.getMobile());

            partner.setTaxid(message.getTaxid());
            partner.setBalance(message.getBalance());
            partner.setCommrelvalue(message.getCommrelvalue());
            partner.setCreditConsume(message.getCreditConsume());
            partner.setCredit(message.getCredit());
            partner.setCommissionShareType(message.getCommissionShareType());
            partner.setCommtype(message.getCommtype());
            partner.setCommissionInterval(message.getCommissionInterval());
            partner.setCommdueday(message.getCommdueday());

            partner.setLastbilldate(null);
            LocalDateTime nextBilldate = LocalDateTime.now();
            if (message.getCommdueday() != null) {
                nextBilldate = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                if (message.getCommissionInterval() != null) {
                    nextBilldate = LocalDateTime.now().toLocalDate().atStartOfDay();
                    if (message.getCommissionInterval() != null && message.getCommissionInterval().equalsIgnoreCase("Monthly"))
                        nextBilldate = nextBilldate.plusMonths(1).withDayOfMonth(message.getCommdueday());
                    ;
                    if (message.getCommissionInterval() != null && message.getCommissionInterval().equalsIgnoreCase("Quarterly"))
                        nextBilldate = nextBilldate.plusMonths(3).withDayOfMonth(message.getCommdueday());
                    ;
                    if (message.getCommissionInterval() != null && message.getCommissionInterval().equalsIgnoreCase("Half-Yearly"))
                        nextBilldate = nextBilldate.plusMonths(6).withDayOfMonth(message.getCommdueday());
                    ;
                    if (message.getCommissionInterval() != null && message.getCommissionInterval().equalsIgnoreCase("Yearly"))
                        nextBilldate = nextBilldate.plusMonths(12).withDayOfMonth(message.getCommdueday());
                }
            }


//            if(message.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
//                NepaliDateDTO nepaliDateDTO = dateConverterService.getNepaliDateFromEnglishDate(nextBilldate.getDayOfMonth() + "-" + nextBilldate.getMonthValue() + "-" + nextBilldate.getYear() + " "
//                        + nextBilldate.getHour() + ":" + nextBilldate.getMinute() + ":" + nextBilldate.getSecond());
//                int monthDay = dateConverterService.getDaysInMonth(nepaliDateDTO.getSaal(), nepaliDateDTO.getMahina());
//                if (message.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
//                    nextBilldate = nextBilldate.plusDays(monthDay - nepaliDateDTO.getGatey());
//                else
//                    nextBilldate = nextBilldate.plusDays(monthDay);
//            }
//            } else {
//                if(message.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
//                    nextBilldate = LocalDate.now().withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear())).atStartOfDay();
//                else
//                    nextBilldate = LocalDate.now().atStartOfDay().plusDays(30);
//            }
            partner.setNextbilldate(LocalDate.from(nextBilldate));
            if (message.getPriceBookId() != null) {
                PriceBook priceBook = priceBookRepository.findById(message.getPriceBookId()).orElse(null);
                if (priceBook != null)
                    partner.setPriceBookId(priceBook);
            }

            Partner savedPartner = partnerRepository.save(partner);
            if (savedPartner.getPartnerType().equalsIgnoreCase("LCO"))
                numberSequenceUtil.createInvoiceFunctionForPartner(savedPartner);
            logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create partner with name " + message.getName(), e.getMessage());
        }
    }

    /**
     * Update partner data.
     * @param message the message
     */
    public void updatePartnerData(UpdatePartnerSharedDataMessage message) {

        try {
            Partner partner = partnerRepository.findById(message.getId()).orElse(null);
            partner.setName(message.getName());
            partner.setStatus(message.getStatus());
            partner.setCity(message.getCity());
            partner.setCountry(message.getCountry());
            partner.setState(message.getState());
            partner.setPincode(message.getPincode());
            partner.setEmail(message.getEmail());
            partner.setPartnerType(message.getPartnerType());
            if (message.getParentPartnerId() != null) {
                Partner parentPartner = partnerRepository.findById(message.getParentPartnerId()).orElse(null);
                partner.setParentPartner(parentPartner);
            }
            partner.setIsDelete(message.getIsDelete());
            partner.setCreatedById(message.getCreatedById());
            partner.setLastModifiedById(message.getLastModifiedById());
            partner.setBuId(message.getBuId());
            partner.setMvnoId(message.getMvnoId());
            partner.setBranch(message.getBranch());
            partner.setMobile(message.getMobile());

            partner.setTaxid(message.getTaxid());
            partner.setBalance(message.getBalance());
            partner.setCommrelvalue(message.getCommrelvalue());
            partner.setCreditConsume(message.getCreditConsume());
            partner.setCredit(message.getCredit());
            partner.setCommissionShareType(message.getCommissionShareType());
            partner.setCommtype(message.getCommtype());
            partner.setCommissionInterval(message.getCommissionInterval());
            if (message.getPriceBookId() != null) {
                PriceBook priceBook = priceBookRepository.findById(message.getPriceBookId()).orElse(null);
                if (priceBook != null)
                    partner.setPriceBookId(priceBook);
            }

            Partner savedPartner = partnerRepository.save(partner);

            if (savedPartner.getIsDelete() == false) {
                List<Customers> custList = new ArrayList<Customers>();
                if (message.getServiceAreaIds() != null && message.getServiceAreaIds().size() > 0) {
                    if (message.getBuId() == null)
                        custList = customersRepository.findByServiceAreaIdInA(message.getServiceAreaIds(), Arrays.asList(message.getMvnoId(), 1));
                    else
                        custList = customersRepository.findByServiceAreaIdInA(message.getServiceAreaIds(), message.getMvnoId(), Arrays.asList(message.getBuId()));

                    if (custList != null && custList.size() > 0) {
                        for (Customers customers : custList)
                            customers.setPartner(savedPartner.getId());
                        customersRepository.saveAll(custList);
                    }
                }
            }
            logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create partner with name " + message.getName(), e.getMessage());
        }
    }


    /**
     * Approvebalance.
     * @param partnerPaymentDTO the partner payment dto
     * @param partnerPayment the partner payment
     * @throws Exception the exception
     */
    public void approvebalance(PartnerPaymentDTO partnerPaymentDTO, PartnerPayment partnerPayment) throws Exception {
        Partner partner = partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
        PartnerAmountMessage partnerAmountMessage = new PartnerAmountMessage();
        if (partnerPaymentDTO.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
            if (partnerPaymentDTO.getAmount() != null && partnerPaymentDTO.getAmount() > 0 && partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)) {
                if (partner.getCreditConsume() == 0) {
                    partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
                    partner.setCredit(partnerPaymentDTO.getCredit() + partner.getCredit());
                    partnerRepository.save(partner);
                    partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partner.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
                } else if (partner.getCreditConsume() < (partner.getBalance() + partnerPaymentDTO.getAmount())) {
                    //partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount() - partner.getCreditConsume());
                    partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
                    partner.setCreditConsume(0.0d);
                    partner.setCredit(partnerPaymentDTO.getCredit() + partner.getCredit());
                    partnerRepository.save(partner);
                    partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partner.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
                    if (partner.getPartnerType().equals(CommonConstants.PARTNER_TYPE_FRANCHISE))
                        adjustPaymentAndAddCommissionAgainstPwscPartner(partner);
                } else if (partner.getCreditConsume() > (partner.getBalance() + partnerPaymentDTO.getAmount())) {
                    partner.setBalance(0.0d);
                    partner.setCreditConsume(partner.getCreditConsume() - (partner.getBalance() + partnerPaymentDTO.getAmount()));
                    partner.setCredit(partnerPaymentDTO.getCredit() + partner.getCredit());
                    partnerRepository.save(partner);
                    partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partner.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
                }
            }


            if (partnerPaymentDTO.getAmount() != null && partnerPaymentDTO.getAmount() > 0 && partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_CREDIT)) {
                partner.setCredit(partnerPaymentDTO.getAmount() + partner.getCredit());
                partnerRepository.save(partner);
                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setNewCustomer_count(0);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
            }

            if (partnerPaymentDTO.getTranscategory().equalsIgnoreCase("Withdraw")) {
                partner.setCommrelvalue(partner.getCommrelvalue() - partnerPayment.getAmount());
                partnerRepository.save(partner);
                PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
                dto.setPartner_id(partnerPaymentDTO.getPartnerId());
                dto.setPaymentdate(LocalDate.now());
                dto.setAmount(partnerPaymentDTO.getAmount());
                //partnerLedgerDetailsService.addBalance(dto);
                partnerRepository.save(partner);
                partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerPaymentDTO.getAmount(), partnerPaymentDTO.getPartnerId(), CommonConstants.WITHDRAW_COMMISSION, "Commission Withdraw");
                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setNewCustomer_count(0);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
            }

            if (partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.BALANCE_TRANSFER) || partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.BALANCE_TRANSFER1)) {
                if (partnerPayment.getAmount() <= partner.getBalance()) {
                    partner.setBalance(partner.getBalance() - partnerPaymentDTO.getAmount());
                    if (partner.getCommrelvalue() != null)
                        partner.setCommrelvalue(partner.getCommrelvalue() + partnerPaymentDTO.getAmount());
                    else partner.setCommrelvalue(partnerPaymentDTO.getAmount());
                    PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
                    dto.setPartner_id(partnerPaymentDTO.getPartnerId());
                    dto.setPaymentdate(LocalDate.now());
                    dto.setAmount(partnerPaymentDTO.getAmount());
                    partnerLedgerDetailsService.addBalance(dto);
                    partnerRepository.save(partner);
                    partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerPaymentDTO.getAmount(), partnerPaymentDTO.getPartnerId(), CommonConstants.BALANCE_TRANSFER, "Deduct Balance From Partner Wallet");
                    partnerAmountMessage.setPartnerId(partner.getId());
                    partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                    partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                    partnerAmountMessage.setBalance(partner.getBalance());
                    partnerAmountMessage.setCredit(partner.getCredit());
                    partnerAmountMessage.setRenewcust_count(0);
                    partnerAmountMessage.setNewCustomer_count(0);
//                    messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                    messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                    kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
//                    kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),"BALANCE_DATA_API"));

                }
            }
            if (partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.COMMISSION_TRANSFER) || partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.COMMISSION_TRANSFER1)) {

                PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
                dto.setPartner_id(partnerPaymentDTO.getPartnerId());
                dto.setPaymentdate(LocalDate.now());
                dto.setAmount(partnerPaymentDTO.getAmount());

                if (partnerPayment.getAmount() <= partner.getCommrelvalue()) {
                    partner.setCommrelvalue(partner.getCommrelvalue() - partnerPaymentDTO.getAmount());
                    if (partner.getBalance() != null)
                        partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
                    else partner.setBalance(partnerPaymentDTO.getAmount());
                }
                partnerRepository.save(partner);
                partnerLedgerDetailsService.reverseBalance(null, partnerPaymentDTO.getAmount(), 0.0, partnerPaymentDTO.getPartnerId(), CommonConstants.COMMISSION_TRANSFER, "Transfer Amount From Commission");
                partnerLedgerDetailsService.addBalance(dto);

                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setNewCustomer_count(0);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
            }
            partner = partnerRepository.findById(partner.getId()).orElse(null);
            if (partner != null) {
                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setNewCustomer_count(0);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
            }
        }
    }

    /**
     * Adjust payment and add commission against pwsc partner.
     * @param partner the partner
     */
    public void adjustPaymentAndAddCommissionAgainstPwscPartner(Partner partner) {
        List<TempPartnerLedgerDetail> list = tempPartnerLedgerDetailsRepository.findAllByPartner_Id(partner.getId());
        if (list != null && !list.isEmpty()) {
            list = list.stream().filter(x -> x.getPaymentStatus() == 2).collect(Collectors.toList());
            list.stream().forEach(record -> {
                if (record.getCustid() != null) {
                    Optional<Customers> customers = customersRepository.findById(record.getCustid());
                    if (customers.isPresent()) {
                        Optional<DebitDocument> debitDocument = debitDocRepository.findById(record.getDebitDocId().intValue());
                        if (debitDocument.isPresent()) {
                            DebitDocument document = debitDocument.get();
                            Double amount = document.getTotalamount();
                            if (document.getAdjustedAmount() != null)
                                amount = document.getTotalamount() - document.getAdjustedAmount();
                            else
                                amount = document.getTotalamount();

                            Optional<StaffUser> staffUser = staffUserRepository.findById(record.getStaffUserId());
                            if (staffUser.isPresent()) {
                                adjustPaymentAgainstInvoiceAmount(customers.get(), amount, record.getDebitDocId(), staffUser.get());
                                partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(customers.get(), amount, record.getDebitDocId());
                                //partnerCommissionService.addPartnerLedgerDetailAgainstInvoiceAmount(amount, customers.get(), partner,record.getDebitDocId());
                                List<TempPartnerLedgerDetail> list1 = new ArrayList<>();
                                list1.add(record);

                                List<TempPartnerLedgerDetail> parentList = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(document.getId());
                                if (parentList != null && !parentList.isEmpty())
                                    parentList = parentList.stream().filter(x -> !x.getPartner().getId().equals(partner.getId())).collect(Collectors.toList());
                                if (parentList != null && !parentList.isEmpty())
                                    list1.addAll(parentList);

                                partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(list1);
                                for (TempPartnerLedgerDetail detail : parentList)
                                    tempPartnerLedgerDetailsRepository.delete(detail);
                                tempPartnerLedgerDetailsRepository.delete(record);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Adjust payment against invoice amount boolean.
     * @param customers the customers
     * @param totalInvoiceAmount the total invoice amount
     * @param invoiceId the invoice id
     * @param staffUser the staff user
     * @return the boolean
     */
    public boolean adjustPaymentAgainstInvoiceAmount(Customers customers, Double totalInvoiceAmount, Long invoiceId, StaffUser staffUser) {
        Optional<DebitDocument> document = debitDocRepository.findById(invoiceId.intValue());
        if (document.isPresent()) {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(0.0d);
            creditDocument.setAmount(totalInvoiceAmount);
            creditDocument.setCustomer(customers);
            creditDocument.setStatus(CommonUtils.PAYMENT_STATUS_APPROVED);
            creditDocument.setLcoid(customers.getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType(CommonUtils.PAYMENT_TYPE);
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
            creditDocument.setPaydetails4("Received By Partner : " + customers.getPartner());
            creditDocument.setPaytype(Constants.ADVANCE);
            creditDocument.setApproverid(staffUser.getId());
            creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            creditDocument.setPaymode(CommonConstants.PAYMENT_MODE_TYPE_CASH);
            creditDocument.setTds_received(false);
            creditDocument.setCreatedById(staffUser.getId());
            creditDocument.setCreatedByName(staffUser.getFullName());
            creditDocument.setMvnoId(staffUser.getMvnoId());
            creditDocument.setLastModifiedById(staffUser.getId());
            creditDocument.setLastModifiedByName(staffUser.getFullName());
            DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).get();
            // creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument,CommonUtils.ADDR_TYPE_PRESENT,null,debitDocument));
            creditDocument.setAdjustedAmount(totalInvoiceAmount);
            creditDocument = creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAdjustedAmount());
            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(invoiceId.intValue());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);
            if (debitDocument.getAdjustedAmount() != null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + totalInvoiceAmount);
            else
                debitDocument.setAdjustedAmount(totalInvoiceAmount);
            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
            debitDocument = debitDocRepository.save(debitDocument);
            //creditDocService.addLedgerAndLedgerDetailEntry(creditDocument,customers,false);
            partnerCommissionService.addLedgerAndLedgerDetailEntry(creditDocument, customers, false);
            return true;
        }
        return false;
    }

    /**
     * Gets by partner id.
     * @param partnerId the partner id
     * @return the by partner id
     */
    public List<PartnerDebitDocument> getByPartnerId(Integer partnerId) {
        Partner partners = partnerRepository.findById(partnerId).orElse(null);
        List<PartnerDebitDocument> partnerCreditDocuments = partnerDebitDocRepository.getAllByPartner(partners);
        return partnerCreditDocuments;
    }


    /**
     * Gets by lco id.
     * @param partnerId the partner id
     * @return the by lco id
     */
    public List<PartnerCreditDocument> getByLcoId(Integer partnerId) {
        List<PartnerCreditDocument> partnerCreditDocuments = partnerCreditDocRepository.getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(partnerId, "CREDITNOTE", "creditnote");
        for (int i = 0; i < partnerCreditDocuments.size(); i++) {
            PartnerDebitDocument partnerDebitDocument = partnerDebitDocRepository.findAllInDebitDocId(partnerCreditDocuments.get(i).getInvoiceId());
            partnerCreditDocuments.get(i).setInvoiceNumber(partnerDebitDocument.getDocnumber());
        }
        return partnerCreditDocuments;
    }

    /**
     * Update partner balance for voucher batch.
     * @param saveVoucherBatchSharedDataMessage the save voucher batch shared data message
     */
    public void updatePartnerBalanceForVoucherBatch(SaveVoucherBatchSharedDataMessage saveVoucherBatchSharedDataMessage) {
        if (saveVoucherBatchSharedDataMessage.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(saveVoucherBatchSharedDataMessage.getPartnerId()).orElse(null);
            if (partner != null) {
                Double voucherBatchTotalAmount = saveVoucherBatchSharedDataMessage.getPrice() * saveVoucherBatchSharedDataMessage.getVoucherQuantity();
                String planId = null;
                if (saveVoucherBatchSharedDataMessage.getPlanId() != null)
                    planId = saveVoucherBatchSharedDataMessage.getPlanId().toString();
                String voucherBatchName = saveVoucherBatchSharedDataMessage.getBatchName();
                partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(partner, voucherBatchTotalAmount, planId, voucherBatchName);
            }
        }
    }


    /**
     * Is partner invoice will generate boolean.
     * @param nextBillDate the next bill date
     * @param startOfMonth the start of month
     * @param endOfMonth the end of month
     * @param mvnoId the mvno id
     * @return the boolean
     */
    public Boolean isPartnerInvoiceWillGenerate(LocalDate nextBillDate, LocalDate startOfMonth, LocalDate endOfMonth, Integer mvnoId) {
        ResponseObject response = new ResponseObject();
        AtomicReference<Integer> count = new AtomicReference<>(0);
        try {
            List<Partner> partnerList = partnerRepository.findAll();
            if (mvnoId != null)
                partnerList = partnerList.stream().filter(x -> x.getMvnoId() != null && x.getMvnoId().intValue() == mvnoId.intValue() && x.getIsDelete() != null && x.getIsDelete().equals(false)).collect(Collectors.toList());
            else
                partnerList = partnerList.stream().filter(x -> x.getIsDelete() != null && x.getIsDelete().equals(false)).collect(Collectors.toList());

            partnerList.stream().forEach(partner -> {
                List<PartnerLedgerDetails> partnerLedgerDetails = partnerLedgerDetailsRepository.findAllByPartner_IdOrderByCreateDateAsc(partner.getId());
                if (partner.getNextbilldate() != null && partner.getNextbilldate().equals(nextBillDate)) {
                    LocalDate startDate = null;
                    LocalDate endDate = nextBillDate.minusDays(1);
                    if (partner.getCommissionInterval().equalsIgnoreCase("Monthly"))
                        startDate = partner.getNextbilldate().minusMonths(1).atStartOfDay().toLocalDate();
                    if (partner.getCommissionInterval().equalsIgnoreCase("Quarterly"))
                        startDate = partner.getNextbilldate().minusMonths(3).atStartOfDay().toLocalDate();
                    if (partner.getCommissionInterval().equalsIgnoreCase("Half-Yearly"))
                        startDate = partner.getNextbilldate().minusMonths(6).atStartOfDay().toLocalDate();
                    if (partner.getCommissionInterval().equalsIgnoreCase("Yearly"))
                        startDate = partner.getNextbilldate().minusMonths(12).atStartOfDay().toLocalDate();

                    LocalDate finalStartDate = startDate;
                    partnerLedgerDetails = partnerLedgerDetails.stream().filter(x -> x.getIsUsed().equals(false) && x.getTranscategory() != null && (x.getTranscategory().equalsIgnoreCase("Commision") || x.getTranscategory().equalsIgnoreCase("Revert Commission") || x.getTranscategory().equalsIgnoreCase("TransferCommission")) && (x.getCreateDate().toLocalDate().equals(finalStartDate) || x.getCreateDate().toLocalDate().isAfter(finalStartDate)) && (x.getCreateDate().toLocalDate().equals(endDate) || x.getCreateDate().toLocalDate().isBefore(endDate))).collect(Collectors.toList());
                    if (partnerLedgerDetails != null && !partnerLedgerDetails.isEmpty())
                        count.getAndSet(count.get() + 1);
                } else if (partner.getNextbilldate() == null && partner.getCommissionInterval() == null) {
                    partnerLedgerDetails = partnerLedgerDetails.stream().filter(x -> x.getIsUsed().equals(false) && x.getTranscategory() != null && (x.getTranscategory().equalsIgnoreCase("Commision") || x.getTranscategory().equalsIgnoreCase("Revert Commission") || x.getTranscategory().equalsIgnoreCase("TransferCommission")) && (x.getCreateDate().toLocalDate().equals(startOfMonth) || x.getCreateDate().toLocalDate().isAfter(startOfMonth)) && (x.getCreateDate().toLocalDate().equals(endOfMonth) || x.getCreateDate().toLocalDate().isBefore(endOfMonth))).collect(Collectors.toList());
                    if (partnerLedgerDetails != null && !partnerLedgerDetails.isEmpty())
                        count.getAndSet(count.get() + 1);
                }
            });
        } catch (Exception ex) {
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
            response.setResponseMessage(Constants.FAIL_REPLY);
            response.setResponseObject(new Object());
            logger.error(ex.toString(), ex);
        }
        return count.get() > 0 ? true : false;
    }

    /**
     * Generate partner commission invoice.
     * @param nextBillDate the next bill date
     * @param startOfMonth the start of month
     * @param endOfMonth the end of month
     * @param mvnoId the mvno id
     */
    public void generatePartnerCommissionInvoice(LocalDate nextBillDate, LocalDate startOfMonth, LocalDate endOfMonth, Integer mvnoId) {
        logger.info("XXXXXXXXXXXX----------Partner Commission Invoice Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_PARTNER_COMMISSION);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.PARTNER_COMMISSION_INVOICE)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.PARTNER_COMMISSION_INVOICE);
            try {
                schedulerAudit.setStartTime(LocalDateTime.now());
                schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_GENERATE_PARTNER_COMMISSION_INVOICE);
                List<Partner> partnerList = partnerRepository.findAll();
                if (mvnoId != null)
                    partnerList = partnerList.stream().filter(x -> x.getMvnoId() != null && x.getMvnoId().intValue() == mvnoId.intValue() && x.getIsDelete() != null && x.getIsDelete().equals(false)).collect(Collectors.toList());
                else
                    partnerList = partnerList.stream().filter(x -> x.getIsDelete() != null && x.getIsDelete().equals(false)).collect(Collectors.toList());


                partnerList.stream().forEach(partner -> {
                    List<PartnerLedgerDetails> partnerLedgerDetails = partnerLedgerDetailsRepository.findAllByPartner_IdOrderByCreateDateAsc(partner.getId());

                    LocalDate startDate = null;
                    LocalDate endDate = nextBillDate.minusDays(1);
                    if (partner.getNextbilldate() != null && partner.getNextbilldate().equals(nextBillDate)) {
                        if (partner.getCommissionInterval().equalsIgnoreCase("Monthly"))
                            startDate = partner.getNextbilldate().minusMonths(1).atStartOfDay().toLocalDate();
                        if (partner.getCommissionInterval().equalsIgnoreCase("Quarterly"))
                            startDate = partner.getNextbilldate().minusMonths(3).atStartOfDay().toLocalDate();
                        if (partner.getCommissionInterval().equalsIgnoreCase("Half-Yearly"))
                            startDate = partner.getNextbilldate().minusMonths(6).atStartOfDay().toLocalDate();
                        if (partner.getCommissionInterval().equalsIgnoreCase("Yearly"))
                            startDate = partner.getNextbilldate().minusMonths(12).atStartOfDay().toLocalDate();

                        LocalDate finalStartDate = startDate;
                        LocalDate finalEndDate = endDate;
                        partnerLedgerDetails = partnerLedgerDetails.stream().filter(x -> x.getIsUsed().equals(false) && x.getTranscategory() != null && (x.getTranscategory().equalsIgnoreCase("Commision") || x.getTranscategory().equalsIgnoreCase("Revert Commission") || x.getTranscategory().equalsIgnoreCase("TransferCommission")) && (x.getCreateDate().toLocalDate().equals(finalStartDate) || x.getCreateDate().toLocalDate().isAfter(finalStartDate)) && (x.getCreateDate().toLocalDate().equals(finalEndDate) || x.getCreateDate().toLocalDate().isBefore(finalEndDate))).collect(Collectors.toList());
                    } else if (partner.getNextbilldate() == null && partner.getCommissionInterval() == null) {
                        LocalDate finalStartDate = startOfMonth.minusMonths(12).withDayOfMonth(1);
                        partnerLedgerDetails = partnerLedgerDetails.stream().filter(x -> x.getIsUsed().equals(false) && x.getTranscategory() != null && (x.getTranscategory().equalsIgnoreCase("Commision") || x.getTranscategory().equalsIgnoreCase("Revert Commission") || x.getTranscategory().equalsIgnoreCase("TransferCommission")) && (x.getCreateDate().toLocalDate().equals(finalStartDate) || x.getCreateDate().toLocalDate().isAfter(finalStartDate)) && (x.getCreateDate().toLocalDate().equals(endOfMonth) || x.getCreateDate().toLocalDate().isBefore(endOfMonth))).collect(Collectors.toList());
                    }

                    if (partnerLedgerDetails != null && !partnerLedgerDetails.isEmpty()) {
                        List<PartnerLedgerDetails> commissionList = partnerLedgerDetails.stream().filter(x -> x.getTranscategory() != null && x.getTranscategory().equalsIgnoreCase("Commision")).collect(Collectors.toList());
                        List<PartnerLedgerDetails> revertCommissionList = partnerLedgerDetails.stream().filter(x -> x.getTranscategory() != null && x.getTranscategory().equalsIgnoreCase("Revert Commission")).collect(Collectors.toList());
                        List<PartnerLedgerDetails> transfercommissionList = partnerLedgerDetails.stream().filter(x -> x.getTranscategory() != null && x.getTranscategory().equalsIgnoreCase("TransferCommission")).collect(Collectors.toList());

                        Double commissionAmount = commissionList.stream().mapToDouble(x -> x.getCommission()).sum();
                        Double revertCommissionAmount = revertCommissionList.stream().mapToDouble(x -> x.getCommission()).sum();
                        Double transferCreditCommissionAmount = transfercommissionList.stream().filter(x -> x.getTranstype() != null && x.getTranstype().equalsIgnoreCase("CR")).mapToDouble(x -> x.getAmount()).sum();
                        Double transferDebitCommissionAmount = transfercommissionList.stream().filter(x -> x.getTranstype() != null && x.getTranstype().equalsIgnoreCase("DR")).mapToDouble(x -> x.getAmount()).sum();

                        Double totalCommissionAmount = commissionAmount - revertCommissionAmount + transferCreditCommissionAmount - transferDebitCommissionAmount;
                        Double totalPartnerTax = commissionList.stream().filter(x -> x.getPartnerTax() != null).mapToDouble(x -> x.getPartnerTax()).sum();
                        Double totalTds = commissionList.stream().filter(x -> x.getTds_amount() != null).mapToDouble(x -> x.getTds_amount()).sum();

                        PartnerDebitDocument partnerDebitDocument = new PartnerDebitDocument();
                        partnerDebitDocument.setPartner(partner);
                        partnerDebitDocument.setTotalamount(totalCommissionAmount);
                        partnerDebitDocument.setTotaldue(totalCommissionAmount);
                        partnerDebitDocument.setPartnerTax(totalPartnerTax);
                        partnerDebitDocument.setTds(totalTds);
                        partnerDebitDocument.setBilldate(LocalDateTime.now());
                        partnerDebitDocument.setStartdate(startOfMonth.atStartOfDay());
                        partnerDebitDocument.setEndate(endOfMonth.atStartOfDay());
                        partnerDebitDocument.setDuedate(endOfMonth.atStartOfDay());
                        partnerDebitDocument.setIsDelete(false);
                        partnerDebitDocument.setPhone(partner.getMobile());
                        partnerDebitDocument.setEmail(partner.getEmail());
                        partnerDebitDocument.setAdjustedamount(0.0d);
                        partnerDebitDocument.setAmountinwords(invoiceUtil.convertToAmount((partnerDebitDocument.getTotalamount() * 100) / 100, curr, centCurr) + " Only");
                        partnerDebitDocument.setDueinwords(invoiceUtil.convertToAmount(partnerDebitDocument.getTotaldue(), curr, centCurr) + " Only");
                        partnerDebitDocument.setBillrunstatus(Constants.BILL_RUN_STATUS.GENERATED.status());
                        partnerDebitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                        partnerDebitDocument.setMvnoId(partner.getMvnoId());
                        partnerDebitDocument.setBuId(partner.getBuId());
                        Partner parentPartner = null;
                        if (partner.getParentPartner() != null) {
                            partnerDebitDocument.setToOperatorName(partner.getParentPartner().getName());
                            parentPartner = partner.getParentPartner();
                        } else {
                            ClientService clientServiceSrv = clientServiceRepository.findByNameAndMvnoId("ORGANIZATION", partner.getMvnoId());
                            if (clientServiceSrv != null)
                                partnerDebitDocument.setToOperatorName(clientServiceSrv.getValue());
                        }
                        partnerDebitDocument = partnerDebitDocRepository.save(partnerDebitDocument);
                        Boolean isLCO = false;
                        if (partner.getPartnerType() != null && partner.getPartnerType().equalsIgnoreCase("LCO"))
                            isLCO = true;
                        partnerDebitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumber(isLCO, partner.getId(), partner.getMvnoId()));
                        partnerDebitDocument.setDocument(prepaidInvoiceService.setPartnerInvoiceXml(partnerDebitDocument, parentPartner, commissionList, revertCommissionList, transfercommissionList, transferCreditCommissionAmount, transferDebitCommissionAmount));
                        partnerDebitDocument = partnerDebitDocRepository.save(partnerDebitDocument);

                        PartnerDebitDocument finalPartnerDebitDocument = partnerDebitDocument;
                        partnerLedgerDetails.stream().forEach(x -> {
                            x.setIsUsed(true);
                            x.setPartnerInvoiceId(finalPartnerDebitDocument.getDocnumber());
                        });
                        partnerLedgerDetailsRepository.saveAll(partnerLedgerDetails);

                        if (startDate != null && endDate != null) {
                            if (partner.getCommissionInterval().equalsIgnoreCase("Monthly"))
                                endDate = partner.getNextbilldate().plusMonths(1).atStartOfDay().toLocalDate();
                            if (partner.getCommissionInterval().equalsIgnoreCase("Quarterly"))
                                endDate = partner.getNextbilldate().plusMonths(3).atStartOfDay().toLocalDate();
                            if (partner.getCommissionInterval().equalsIgnoreCase("Half-Yearly"))
                                endDate = partner.getNextbilldate().plusMonths(6).atStartOfDay().toLocalDate();
                            if (partner.getCommissionInterval().equalsIgnoreCase("Yearly"))
                                endDate = partner.getNextbilldate().plusMonths(12).atStartOfDay().toLocalDate();
                            partner.setNextbilldate(endDate);
                            partner.setLastbilldate(nextBillDate);
                            partnerRepository.save(partner);
                        } else {
                            partner.setCommissionInterval("Monthly");
                            partner.setNextbilldate(nextBillDate.plusMonths(1));
                            partner.setLastbilldate(nextBillDate);
                            partner.setCommdueday(nextBillDate.getDayOfMonth());
                            partnerRepository.save(partner);
                        }
                    }
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Generate Partner Commission Invoice Successfull");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(partnerList.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.PARTNER_COMMISSION_INVOICE);
                logger.info("XXXXXXXXXXXX---------- Partner Commission Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Partner Commission Invoice Scheduler Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX----------Partner Commission Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    /**
     * Gets all partner debit document.
     * @param partnerId the partner id
     * @return the all partner debit document
     */
    public List<PartnerDebitDocument> getAllPartnerDebitDocument(Integer partnerId) {
        List<PartnerDebitDocument> debitDocuments = new ArrayList<>();
        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner != null && partner.getIsDelete().equals(false)) {
            debitDocuments = partnerDebitDocRepository.getAllByPartner(partner);
            return debitDocuments;
        }
        return debitDocuments;
    }


    /**
     * Gets logged in user.
     * @return the logged in user
     */
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
}
