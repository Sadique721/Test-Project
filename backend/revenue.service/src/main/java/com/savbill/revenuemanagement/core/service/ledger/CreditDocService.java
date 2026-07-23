package com.savbill.revenuemanagement.core.service.ledger;

import com.savbill.revenuemanagement.CommonList.domain.CommonList;
import com.savbill.revenuemanagement.CommonList.repository.CommonListRepository;
import com.savbill.revenuemanagement.KRA.KRAUtils;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanService;
import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.domain.CustomerOnlinePaymentAudit;
import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.repository.CustomerOnlinePaymentAuditRepository;
import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.service.CustomerOnlinePaymentAuditService;
import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.Mvno.domain.QMvno;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.CreditDocumentMapper;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.PaymentHistoryDTO;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.SearchPaymentPojo;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustPayDTOMessage;
import com.savbill.revenuemanagement.core.dto.customer.CustomerVoucherDTO;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.entity.ladger.*;
import com.savbill.revenuemanagement.core.entity.ladger.*;
import com.savbill.revenuemanagement.core.entity.partner.TempPartnerLedgerDetail;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.mapper.invoice.CreditDocChargeRelMapper;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.partner.TempPartnerLedgerDetailsRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.service.MessagesPropertyConfig;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.common.TransactionUtil;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.core.xmlconversion.PaymentDetailsXml;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.BankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.repository.BankManagementRepository;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.service.BankManagementService;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;

import com.savbill.revenuemanagement.rabbitmq.ServiceChnageStatus;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.WorkFlowAutoApprovalMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.RecordPaymentMessage;
import com.savbill.revenuemanagement.utils.CommonUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreditDocService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(CreditDocService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;
    @Autowired
    private KRAUtils kraUtils;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private BankManagementRepository bankManagementRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    CreditDocumentMapper creditDocumentMapper;

    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    private CreditDocChargeRelRepository creditDocChargeRelRepository;

    @Autowired
    private CreditDocTaxRelRepository creditDocTaxRelRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private DbrService dbrService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CommonListRepository commonListRepository;

    @Autowired
    private CustomerChargeDBRRepository customerChargeDBRRepository;

    @Autowired
    private CustomerDBRRepository customerDBRRepository;

    @Autowired
    private CustomerLedgerRepository customerLedgerRepository;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    private BankManagementService bankManagementService;

    //@Autowired
    // private MessageSender messageSender;

    @Autowired
    private CreditDocChargeRelMapper creditDocChargeRelMapper;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CustomerLedgerDtlsService ledgerService;

    @Autowired
    private CustomerLedgerRepository ledgerRepository;


    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    public String PATH;

    @Autowired
    private CustomerLedgerDtlsService ledgerDtlsService;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private TrialDebitDocService trialDebitDocService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private TransactionUtil transactionUtil;

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    public Map<String, String> sortColMap = new HashMap<>();
    public PageRequest pageRequest = null;
    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;

    @Autowired
    private CustomerOnlinePaymentAuditService customerOnlinePaymentAuditService;

    @Autowired
    private CustomerOnlinePaymentAuditRepository customerOnlinePaymentAuditRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private PaymentDetailsXml paymentDetailsXml;


    public CreditDocument creatCreditNotAsPerService(DebitDocument debitDocument, DebitDocument newDebitDocument, List<CustomerServiceMapping> customerServiceMappings, String remarks, Boolean forViewOnly, List<Long> custInvMappingIds, String type, String chargeType, Double invenRefAmount, LocalDate billDate) {
        try {
            logger.info("===================== CN START :- debitDocId: "
                    + (debitDocument != null ? debitDocument.getId() : "NULL")
                    + " newDebitDocId: "
                    + (newDebitDocument != null ? newDebitDocument.getId() : "NULL")
                    + " customerServiceMappings size: "
                    + (customerServiceMappings != null ? customerServiceMappings.size() : "NULL")
                    + " type: " + type
                    + "=====================");

            if (debitDocument == null) {
                logger.info("===================== ERROR :- debitDocument is NULL =====================");
            }

            Double cnAmount = 0d;
            Double remainingAmount = debitDocument.getTotalamount();
//            debitDocument.setAdjustedAmount(0d);
            LocalDate currentDate;
            //commenting bcuz on change plan full CN not generated if we have add payment
//            if (debitDocument.getAdjustedAmount() != null) {
//                remainingAmount = remainingAmount - debitDocument.getAdjustedAmount();
//            }
            if (type != null && type.equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                currentDate = debitDocument.getCreatedate().toLocalDate();
            }
//            else if (billDate!=null) {
//                currentDate = debitDocument.getStartdate().toLocalDate();
//            }
            else {
                currentDate = LocalDate.now();
            }

            logger.info("===================== DATE INFO :- currentDate: " + currentDate
                    + " startDate: " + debitDocument.getStartdate()
                    + " endDate: " + debitDocument.getEndate()
                    + "=====================");

            List<Integer> cprIds = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#.00");
            List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();

            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                List<Integer> custServiceIds = customerServiceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());

                logger.info("===================== 1 custServiceIds :- " + custServiceIds + "=====================");

                if (!CollectionUtils.isEmpty(custServiceIds)) {
                    //CN as per service
                    if (type != null && type.equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                        cprIds = custPlanMappingRepository.getAllByCustServiceMappingIdInForCancelAndRegen(custServiceIds);
                    } else {
                        cprIds = custPlanMappingRepository.getAllByCustServiceMappingIdIn(custServiceIds);
                    }

                    logger.info("===================== 2 CPR IDS :- " + cprIds + "=====================");

                    if (!CollectionUtils.isEmpty(cprIds)) {
                        for (Integer cprId : cprIds) {
//                            List<CustomerChargeDBR> customerChargeDBR = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(currentDate, debitDocument, Collections.singletonList(Long.valueOf(cprId)));

                            List<CustomerChargeDBR> customerChargeDBR = customerChargeDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateBetween(Collections.singletonList(Long.valueOf(cprId)), Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());

                            logger.info("===================== 3 DBR FETCH (CustomerChargeDBR) :- cprId: " + cprId
                                    + " result size: " + (customerChargeDBR != null ? customerChargeDBR.size() : "NULL")
                                    + "=====================");

                            if (!CollectionUtils.isEmpty(customerChargeDBR)) {
                                customerChargeDBRList.addAll(customerChargeDBR);
                            }
                        }

                        logger.info("===================== 4 TOTAL CustomerChargeDBRList SIZE :- "
                                + (customerChargeDBRList != null ? customerChargeDBRList.size() : "NULL")
                                + "=====================");

                        if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                            cnAmount = customerChargeDBRList.stream().mapToDouble(CustomerChargeDBR::getDbr).sum();
                        } else {
//                            List<CustomerDBR> customerDBRS = dbrService.getCustomerDBRListBetweenStartDateAndEndDateAndByService(currentDate, debitDocument, cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
                            List<CustomerDBR> customerDBRS = customerDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()), Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());

                            logger.info("===================== 5 DBR FETCH (CustomerDBR fallback) SIZE :- "
                                    + (customerDBRS != null ? customerDBRS.size() : "NULL")
                                    + "=====================");

                            if (!CollectionUtils.isEmpty(customerDBRS))
                                cnAmount = customerDBRS.stream().mapToDouble(CustomerDBR::getDbr).sum();
                        }
                    }
                } else {
                    //CN as per invoice
//                    customerChargeDBRList = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDate(currentDate, debitDocument);
                    customerChargeDBRList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());

                    logger.info("===================== 6 Invoice Level DBR SIZE :- "
                            + (customerChargeDBRList != null ? customerChargeDBRList.size() : "NULL")
                            + "=====================");

                    if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        LocalDate finalCurrentDate = currentDate;
                        cnAmount = Double.parseDouble(df.format(customerChargeDBRList.stream().filter(x -> x.getStartdate().equals(finalCurrentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                        cnAmount = Double.parseDouble(df.format(cnAmount));
                    } else {
//                        List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(currentDate, debitDocument);
                        List<CustomerDBR> customerDBRList = customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());

                        logger.info("===================== 7 Invoice Level fallback DBR SIZE :- "
                                + (customerDBRList != null ? customerDBRList.size() : "NULL")
                                + "=====================");

                        if (!CollectionUtils.isEmpty(customerDBRList)) {
                            LocalDate finalCurrentDate1 = currentDate;
                            cnAmount = Double.parseDouble(df.format(customerChargeDBRList.stream().filter(x -> x.getStartdate().equals(finalCurrentDate1)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                            cnAmount = Double.parseDouble(df.format(cnAmount));
                        }
                    }
                }
            }

            logger.info("===================== 8 INVENTORY CASE :- custInvMappingIds size: "
                    + (custInvMappingIds != null ? custInvMappingIds.size() : "NULL")
                    + " invenRefAmount: " + invenRefAmount
                    + "=====================");

            if (CollectionUtils.isEmpty(custInvMappingIds) && debitDocument.getIsDirectChargeInvoice()) {
                List<CustomerChargeDBR> customerChargeDBR = dbrService.findAllCustomerChargedbrByDebitDoc(debitDocument);//customerChargeDBRRepository.(Collections.singletonList(Long.valueOf(cprId)), Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());
                cnAmount = customerChargeDBR.stream().mapToDouble(CustomerChargeDBR::getDbr).sum();
                cnAmount = Double.parseDouble(df.format(cnAmount));
            }
            if (!CollectionUtils.isEmpty(custInvMappingIds)) {
                if (invenRefAmount != null) {
                    if (invenRefAmount != 0d)
                        cnAmount = invenRefAmount;
                } else {
                    if (custInvMappingIds != null && !custInvMappingIds.isEmpty()) {
                        List<CustomerInventoryMapping> customerServiceMappings1 = customerInventoryMappingRepo.findByIds(custInvMappingIds);
                        for (CustomerInventoryMapping mapping : customerServiceMappings1) {
                            cnAmount = cnAmount + mapping.getOfferPrice();
                        }
                    }

                }
            }

            logger.info("===================== 9 CN AMOUNT BEFORE TAX :- " + cnAmount + "=====================");

            if (cnAmount != 0) {
                Double invoiceWithoutTax = debitDocument.getTotalamount() - debitDocument.getTax() + debitDocument.getDiscount();
                Double newDiscount = cnAmount * (debitDocument.getDiscount() / invoiceWithoutTax);
                //ANG-11160
//                if (CollectionUtils.isEmpty(custInvMappingIds)) {
                Double percentage = (debitDocument.getTax() * 100.0d) / (debitDocument.getTotalamount() - debitDocument.getTax());
                Double prorateTaxAmount = ((cnAmount - newDiscount) * percentage) / 100.0d;
                cnAmount = cnAmount - newDiscount + prorateTaxAmount;

                logger.info("===================== 10 CN AMOUNT AFTER TAX :- " + cnAmount + "=====================");

//                }
                CreditDocument creditDocument = new CreditDocument();
                Double remainAmount = 0d;
                if (customerServiceMappings.get(0).getServiceHoldDate() != null
                        && customerServiceMappings.get(0).getServiceResumeDate() != null
                        && !customerChargeDBRList.isEmpty()) {
                    long difference = ChronoUnit.DAYS.between(
                            customerServiceMappings.get(0).getServiceHoldDate().toLocalDate(),
                            customerServiceMappings.get(0).getServiceResumeDate().toLocalDate()
                    );
                    remainAmount = difference * customerChargeDBRList.get(0).getDbr();
                }
                creditDocument.setAmount(cnAmount + remainAmount);
                //Adjust fraction less than 0.1
                if (remainingAmount - cnAmount < 0.1 && remainingAmount != 0) {
                    creditDocument.setAmount(remainingAmount);
                }

                logger.info("===================== 11 CN CREATED :- Amount: " + creditDocument.getAmount()
                        + " InvoiceId: " + debitDocument.getId()
                        + "=====================");

                creditDocument.setInvoiceId(debitDocument.getId());
                creditDocument.setTdsamount(0d);
                creditDocument.setAbbsAmount(0d);
                creditDocument.setCustomer(debitDocument.getCustomer());
                creditDocument.setPaymode(Constants.PAYMENT_MODE.CREDIT_NOTE);
                creditDocument.setPaymentdate(LocalDate.now());
                creditDocument.setPaytype("creditnote");
                creditDocument.setType(Constants.TRANS_CREDIT_NOTE);
                creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.setIsDelete(false);
                creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
                creditDocument.setBuID(debitDocument.getCustomer().getBuId());
                creditDocument.setRemarks(remarks + "\n Payment adjusted :-" + creditDocument.getAmount());
                creditDocument.setLcoid(debitDocument.getLcoId());
                creditDocument.setCreditdocumentno(getInvoiceNo());
                creditDocument.setCreatedById(debitDocument.getCreatedById());
                creditDocument.setCreatedByName(debitDocument.getCreatedByName());
                //              creditDocument.setXmldocument(assemblePaymentXML(creditDocument, CommonUtils.ADDR_TYPE_PRESENT));
                //AdjustmentAmount
                if (!forViewOnly) {
                    creditDocument = creditDocRepository.save(creditDocument);
                    ClientService clientService = null;
                    try {
                        clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.REVENUE_AUTHORITY_NAME, debitDocument.getCustomer().getMvnoId());
                    } catch (Exception e) {
                        // Log the exception but continue gracefully
                        logger.warn("ClientService not found for REVENUE_AUTHORITY_NAME and mvnoId: " + debitDocument.getCustomer().getMvnoId(), e);
                        clientService = null;
                    }
                    try {
                        if(clientService!=null && "KRA".equalsIgnoreCase(clientService.getValue())) {
                            kraUtils.processEtimsAddCreditNote(Collections.singletonList(creditDocument));
                        }
                    } catch (Exception e) {
                        ApplicationLogger.logger.error("Some Exception occured while integrating to {}",e);
                    }
                    //if old debit doc fully paid than adjust new debit doc else adjust old debit doc
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    if (debitDocument.getPaymentStatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                        creditDebitDocMapping = adjustCNPaymentAmountAgainstDebitDocForChangePlan(newDebitDocument, creditDocument, true, debitDocument);
                    } else {
                        creditDebitDocMapping = adjustCNPaymentAmountAgainstDebitDocForChangePlan(debitDocument, creditDocument, false, newDebitDocument);
                    }
                    addLedgeAfterApproval(creditDocument);
                    creditDebtMappingRepository.save(creditDebitDocMapping);

                   /* if(debitDocument.getBillrunstatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.CANCELLED) && (debitDocument.getTotalamount() > debitDocument.getAdjustedAmount())) {
                        debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                        debitDocRepository.save(debitDocument);
                    }*/
                    Double creditAmountExcludeTax = dbrService.getCreditNotePriceExcludingTax(debitDocument, creditDocument.getAmount());
                    if (!CollectionUtils.isEmpty(cprIds) && !debitDocument.getIsDirectChargeInvoice()) {
                        dbrService.removeDbrByCPRListAndInvoiceIdStartDateAtChargeLevel(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), debitDocument.getId(), currentDate, debitDocument.getEndate().toLocalDate());
                        dbrService.removedbrByCPRListAndInvoiceIdStartDate(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), debitDocument.getId(), currentDate, debitDocument.getEndate().toLocalDate());
                    }
                    dbrService.addDbrEntry(debitDocument, debitDocument.getId().longValue(), creditAmountExcludeTax, type, chargeType);

                    try {
                        partnerCommissionService.revertPartnerCommission(debitDocument, creditDocument.getAmount());
                    } catch (Exception e) {
                        logger.error("Error in Partner Revert Commission :  " + e.getStackTrace());
                    }
                    // Add tax, discount and charge in table
                    if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        setCreditNoteDataToTable(customerChargeDBRList, creditDocument, debitDocument);
                    }
                    //TODO: Once Nav approval done will start
//                    try {
//                        sendDataToNAV(Optional.of(creditDocument), debitDocument);
//                    } catch (Exception ex) {
//                        logger.error("Error in integration" + ex.getStackTrace());
//                    }
                }
                try {
                    CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
                    List<CreditDocMessage> creditDocMessage = new ArrayList<>();
                    CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
                    creditDocMessage.add(creditDoc);
                    creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//                    messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                    kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return creditDocument;
            } else {
                logger.error("CN Amount get 0 so CN not created! for invoice number: " + debitDocument.getDocnumber());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while creating CN for debitDoc: " + debitDocument.getDocnumber() + " exception: " + ex.getMessage());
        }
        return null;
    }


    /**
     * Method to caluculate credit not amount
     *
     * @param debitDocId
     * @return
     */
    public String previewCreditNoteAmount(Integer debitDocId) {
        Optional<DebitDocument> optionalDebitDocument = debitDocRepository.findById(debitDocId);
        if (optionalDebitDocument.isPresent()) {
            DebitDocument debitDocument = optionalDebitDocument.get();
            try {
                double remainingAmount = debitDocument.getTotalamount();
                DecimalFormat df = new DecimalFormat("#.00");
                List<CustomerChargeDBR> dbrList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(
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
                    return "No credit note will be generated (amount = 0).";
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
                String currencySymbol = clientServiceRepository.findValueByNameAndMvnoId(CommonConstants.CURRENCY_SYMBOL, getMvnoIdFromCurrentStaff());
                cnAmount = Double.parseDouble(df.format(cnAmount));
                return "Credit note of amount " + currencySymbol + " " + df.format(cnAmount) + " will be generated.";
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Error while previewing CN for invoice: " + debitDocument.getDocnumber() + " exception: " + ex.getMessage());
                return "Error while calculating credit note preview.";
        }}
        return "No such invoice found";
    }

    public List<CreditDebitDocMapping> adjustCNPayementWithDebitDoc(List<Integer> creditDocIds, DebitDocument debitDocument, List<Integer> oldDebitDocumentIds) {
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        try {
            for (Integer cdid : creditDocIds) {
                Optional<CreditDocument> creditDocument = creditDocRepository.findById(cdid);
                double remainingAmount = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
                if (creditDocument.isPresent() && remainingAmount > 0) {
                    //creditDocument.get().setAdjustedAmount(0d);
                    if (!creditDocument.get().getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                        CreditDebitDocMapping debitDocMapping = adjustPaymentAmountAgainstDebitDoc(debitDocument, creditDocument.get(), oldDebitDocumentIds);
                        //CreditDebitDocMapping debitDocMapping = adjustCNPaymentAmountAgainstDebitDoc(debitDocument, creditDocument.get());

                        if (debitDocMapping != null) {
                            creditDebitDocMappings.add(debitDocMapping);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while adjust CN/payment with invoice:" + debitDocument.getDocnumber());
        }
        return creditDebitDocMappings;
    }

    public void adjustCreditNote(CreditDocument creditDocument) {
        System.out.println("====================Inside method : adjustCreditNote===========================");
        List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
            CreditDocument creditDocument1 = creditDocRepository.findById(creditDebitDocMapping.getCreditDocId()).orElse(null);
            if(creditDocument1!=null && creditDocument1.getType().equalsIgnoreCase("creditnote"))
            {
                DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                System.out.println("=============== Fetched debitDocument=" + debitDocument + "===============");
                List<CustomerChargeDBR> customerChargeDBRList = dbrService.findAllCustomerChargedbrByDebitDoc(debitDocument);
                Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                System.out.println("=============== totalCreditNoteGenerated= " + totalCreditNoteGenerated + "================");
//            if (totalCreditNoteGenerated == 0) {
//                if (creditDocument.getAmount() > debitDocument.getTotalamount()) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becauae invoice amount exceeds", null);
//                }
//            } else if (creditDocument.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note because invoice amount exceeds", null);
//            }

                if (creditDocument != null && creditDocument.getCustomer().getId().intValue() == 1) {
                    if (creditDocument.getInvoiceId() != null)
                    {
                        if (debitDocument.getIsCNEnable()) {
                            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                            if (creditDocument.getAdjustedAmount() != null)
                                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                            else
                                creditDocument.setAdjustedAmount(creditDocument.getAmount());
                            creditDocRepository.save(creditDocument);

                            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                            creditDebitDocMapping.setIsDeleted(false);
                            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
                            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
                            creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

                            List<CreditDebitDocMapping> debitDocMapping = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());

                            if(debitDocMapping!=null && !debitDocMapping.isEmpty())
                            {
                                debitDocMapping.get(0).setAdjustedAmount(creditDocument.getAmount());
                                creditDebtMappingRepository.save(debitDocMapping.get(0));
                            }
                            if (debitDocument.getAdjustedAmount() != null)
                                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
                            else
                                debitDocument.setAdjustedAmount(creditDocument.getAmount());
                            reversalPaymentForOrg(creditDocument.getAmount(), debitDocument, true);
                            return;
                        }
                    }
                    return;
                }

                DecimalFormat df = new DecimalFormat("#.00");
                List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
                Double pendingRevenueWithTax = dbrService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);
                System.out.println("=============== pendingRevenueWithTax=  ===============" + pendingRevenueWithTax + "=============");


                List<CustPlanMappping> custPlanMapppings = IterableUtils.toList(custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId()));
                Set<Integer> cprids = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toSet());
                System.out.println("========outside 592======= cprids= " + cprids + "=============");


                if ((totalCreditNoteGenerated >= debitDocument.getTotalamount()) || Math.abs(pendingRevenueWithTax.doubleValue() - creditDocument.getAmount().doubleValue()) < 0.1 || creditDocument.getAmount().doubleValue() > pendingRevenueWithTax.doubleValue()) {
                    System.out.println("Inside condition for sending kafka call to CPM");
                    custPlanMapppings.forEach(custPlanMappping -> {
                        System.out.println("=============== Stopping CustPlanMappping id= " + custPlanMappping.getId() + "===============");
                        custPlanMappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                        custPlanMappping.setIsVoid(true);
                        //ANG-4987: resolved
                        if (custPlanMappping.getStartDate().isAfter(LocalDateTime.now())) {
                            custPlanMappping.setStartDate(LocalDateTime.now().minusMinutes(1));
                            custPlanMappping.setEndDate(LocalDateTime.now());
                            custPlanMappping.setExpiryDate(LocalDateTime.now());
                        } else {
                            custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
                            custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
                        }
                        if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                            custPlanMappping.setStartDate(LocalDateTime.now());
                            custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                            custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                        }

                        custPlanMappingRepository.save(custPlanMappping);
                        System.out.println("=============== Saved custPlanMappping id= " + custPlanMappping.getId() + "==============");
                        // sending the message to cms
                        CustPlanStatusMessage custPlanStatusMessage = new CustPlanStatusMessage(custPlanMappping.getId());
//                        String messagStatus = messageSender.send(custPlanMappping.getId(),RabbitMqConstants.QUEUE_CHANGE_PLAN_STATUS_CMS);
                        System.out.println("===========================+++++++++Message to CPM : custPlanStatusMessage ++++++++++++==============================");
                        String messagStatus = kafkaMessageSender.send(new KafkaMessageData(custPlanStatusMessage, CustPlanStatusMessage.class.getSimpleName()));
                        System.out.println("=============== Kafka : CustPlanStatus update sent, status= " + messagStatus + "===============");
                        logger.info(messagStatus);
                    });
                }

                Double amountToBePaid = 0d;
                Double remainingAmount = 0d;
                if (debitDocument.getAdjustedAmount() == null) {
                    amountToBePaid = debitDocument.getTotalamount();
                } else {
                    amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
                }
                remainingAmount = creditDocument.getAmount() - amountToBePaid;

//            if all amount from credit note adjusted with invoice
                if (remainingAmount == 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    if (debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount()) || (pendingRevenueWithTax.doubleValue() == creditDocument.getAmount().doubleValue()))
                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    creditDocument.setAdjustedAmount(creditDocument.getAmount());
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                }
//            when amount from credit note is greater than pending amount of invoice
                else if (remainingAmount > 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    creditDocument.setAdjustedAmount(creditDocument.getAmount() - remainingAmount);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
                    creditDebitDocMapping.setAdjustedAmount(amountToBePaid);
                }
//            when amount from credit note is fully adjusted but invoice has some amount left to adjust
                else {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(creditDocument.getAmount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
                    }
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAmount());
                    creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                }

//           if total amount of invoice adjusted through credit note or total amount of credit note generated for same invoice set invoice status cancelled

                if (creditDocument.getAmount() + totalCreditNoteGenerated == debitDocument.getTotalamount() || (pendingRevenueWithTax.doubleValue() == creditDocument.getAmount().doubleValue())) {
                    debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    if (cprids.size() > 0) {
                        List<Integer> debitdocids = custPlanMappingRepository.findAllByCustRefId(cprids);
                        if (debitdocids.size() > 0) {
                            List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debitdocids);
                            debitDocuments.stream().forEach(i -> {
                                i.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                i.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                            });
                            debitDocRepository.saveAll(debitDocuments);
                            debitDocuments.forEach(debitDocument1 -> {
                                PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument1.getCustomer().getId(), debitDocument1.getCustomer().getUsername(), null, debitDocument1.getTotalamount(), debitDocument1.getId().longValue(), null, true, debitDocument1.getTotalamount(), null, null, null, "null", "false", "false", 0L, debitDocument1, debitDocument1.getCustomer().getWalletbalance(), debitDocument1.getPaymentStatus(), debitDocument1.getBillrunid(), null, null, debitDocument1.getAdjustedAmount(), debitDocument1.getBillrunstatus(), true, debitDocument1.getIsDirectChargeInvoice(), null, null, null, null);
//                                messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
                                kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
                            });
                        }
                    }
                }
//          if after all adjustment there is some amount left in credit note set inovice as payable
                if (remainingAmount > 0) {
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
                }
                creditDebtMappingRepository.save(creditDebitDocMapping);
                debitDocRepository.save(debitDocument);
                creditDocRepository.save(creditDocument);
                try {
                    if (!creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)) {
                        setCreditNotdataToTable(customerChargeDBRList, creditDocument, debitDocument);
                    }
                } catch (Exception ex) {
                    logger.error("Error while adding CN charge rel data: " + ex.getMessage());
                }
//            try {
//                sendDataToNAV(creditDocument, debitDocument);
//            } catch (Exception e) {
//                logger.error("Error in integration" + e.getStackTrace());
//            }
                try {
                    partnerCommissionService.revertPartnerCommission(debitDocument,creditDocument.getAmount());}
                catch (Exception ex){logger.error("Error while Revert Commission against Creditnote: " + ex.getMessage());}
                dbrService.creditNoteDbrEntry(debitDocument, creditDocument.getAmount(), true);
                prepaidInvoiceService.adjustBillToSubisuInvoiceWithCreditNote(creditDocument.getAmount(), debitDocument);

                PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", "false", 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), null, null, null, null);
//                messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
                kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
            }
        }
    }

    @Transactional
    public void setCreditNotdataToTable(List<CustomerChargeDBR> customerChargeDBRS, CreditDocument document, DebitDocument debitDocument) {
        try {
            Set<Long> chargeIds = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId() != null).map(CustomerChargeDBR::getChargeId).collect(Collectors.toSet());
            List<CreditDocChargeRel> creditDocChargeRels = new ArrayList<>();
            Double discountInPer = 0d;
            if (debitDocument.getCustpackrelid() != null) {
                discountInPer = custPlanMappingRepository.findDiscountById(debitDocument.getCustpackrelid());
            }
            for (Long chargeId : chargeIds) {
                Double totalChargeAmount = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId().equals(chargeId)).mapToDouble(CustomerChargeDBR::getDbr).sum();
                Double discountAmount = getAmountFromPer(totalChargeAmount, discountInPer);
                //totalAmount after discount
                totalChargeAmount = totalChargeAmount - discountAmount;
                Charge charge = chargeRepository.findById(chargeId.intValue()).get();
                Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge, totalChargeAmount);
                Double totalAmountForCharge = totalChargeAmount + taxAmount;

                CreditDocChargeRel creditDocChargeRel = calculateCNChargeRelData(debitDocument, totalAmountForCharge, document, discountInPer, charge);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while adding CN charge rel amount");
        }
    }

    public void adjustCreditNoteForBillToSubisu(Double newAdjustAmount, DebitDocument debitDocument) {
        if(debitDocument!=null && (debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING) || debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.APPROVED)))
        {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(newAdjustAmount);
            creditDocument.setAmount(newAdjustAmount);
            creditDocument.setCustomer(debitDocument.getCustomer());
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            creditDocument.setLcoid(debitDocument.getCustomer().getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType("creditnote");
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
            creditDocument.setCreditdocumentno(getInvoiceNo());
            creditDocument.setPaydetails4(null);
            creditDocument.setPaytype("creditnote");
            creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            creditDocument.setPaymode(CommonConstants.TRANS_CREDIT_NOTE1);
            creditDocument.setTds_received(false);
            creditDocument.setCreatedById(debitDocument.getCreatedById());
            creditDocument.setCreatedByName(debitDocument.getCreatedByName());
            creditDocument.setLastModifiedById(debitDocument.getLastModifiedById());
            creditDocument.setLastModifiedByName(debitDocument.getLastModifiedByName());
//            creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument,CommonUtils.ADDR_TYPE_PRESENT,null,debitDocument));
            creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
            creditDocument.setBuID(debitDocument.getCustomer().getBuId());
            creditDocument = creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setAdjustedAmount(newAdjustAmount);
            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

            if (!debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING))
                reversalPaymentForOrg(newAdjustAmount, debitDocument, false);

            addLedgerAndLedgerDetailEntryForCreditNoteOrg(creditDocument, creditDocument.getAmount(), debitDocument.getCustomer(), debitDocument);

            if (debitDocument.getAdjustedAmount() != null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + newAdjustAmount);
            else
                debitDocument.setAdjustedAmount(newAdjustAmount);
            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
            debitDocument = debitDocRepository.save(debitDocument);
        }
    }

    public void addLedgerAndLedgerDetailEntryForCreditNoteOrg(CreditDocument creditDocument, Double creditNoteAmount, Customers customers, DebitDocument document) {

        CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(creditNoteAmount);
        ledgerDtls.setPaymentMode("Credit Note");
        ledgerDtls.setBank(null);
        ledgerDtls.setBranch(null);
        //ledgerDtls.setDebitdocid(document.getId());
        ledgerDtls.setCreditdocid(creditDocument.getId());
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsVoid(false);
        ledgerDtls.setIsDelete(false);
        ledgerDtls.setTranscategory(CommonConstants.TRANS_CREDIT_NOTE);
        ledgerDtls.setDescription(creditDocument.getPaydetails4());
        ledgerDtls.setCustomer(customers);
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
        ledgerDtls.setDescription("CreditNote for Business Promotion Invoice.");
        customerLedgerDtlsRepository.save(ledgerDtls);
    }

    @Transactional
    public CreditDebitDocMapping adjustCNPaymentAmountAgainstDebitDoc(DebitDocument debitDocument, CreditDocument creditDocument) {
        List<CreditDebitDocMapping> debitDocMappings = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
        if(debitDocMappings!=null && !debitDocMappings.isEmpty())
        {
            debitDocMappings = debitDocMappings.stream().filter(x -> x.getAdjustedAmount().doubleValue() == 0.0).collect(Collectors.toList());
            if (debitDocMappings != null && !debitDocMappings.isEmpty())
                creditDebitDocMapping = debitDocMappings.get(0);
        }
        try {
            Double debitDocRemainingAmount = debitDocument.getTotalamount();
            Double cnRemainingAmount = creditDocument.getAmount();
            boolean isFirstCN = false;
            //DebitDoc adjusted amount
            DecimalFormat df = new DecimalFormat("0.00");
            if (debitDocument.getAdjustedAmount() != null) {
                debitDocRemainingAmount = debitDocRemainingAmount - debitDocument.getAdjustedAmount();
                debitDocRemainingAmount = Double.parseDouble(df.format(debitDocRemainingAmount));

            } else {
                debitDocument.setAdjustedAmount(0d);
                isFirstCN = true;
            }


            //CN adjusted amount
            if (creditDocument.getAdjustedAmount() != null) {
                cnRemainingAmount = cnRemainingAmount - creditDocument.getAdjustedAmount();
            } else {
                creditDocument.setAdjustedAmount(0d);
            }


            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setIsDeleted(Boolean.FALSE);

            //CN is partialy
            if (debitDocRemainingAmount > cnRemainingAmount) {
                //Full CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + cnRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(cnRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + cnRemainingAmount);
            }//CN is more than invoice amount
            else if (debitDocRemainingAmount < cnRemainingAmount) {
                //Partial CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }//CN amount is equal to invoice amount
            else {
                //Full CN adjusted
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }
            //status for CN
            if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findMappingBydebtDocId(debitDocument.getId(), Constants.PAYMENT_TYPE);
                Double invoiceAdjustedAmt = debitDocument.getAdjustedAmount();
                if (!CollectionUtils.isEmpty(creditDebitDocMappings)) {
                    if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                        Double oldAdjustedAmt = creditDebitDocMappings.stream()
                                .mapToDouble(mapping -> mapping.getAdjustedAmount() != null ? mapping.getAdjustedAmount() : 0.00)
                                .sum();
                        invoiceAdjustedAmt = invoiceAdjustedAmt - oldAdjustedAmt;
                    }
                }
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()) || creditDocument.getAmount().equals(debitDocument.getTotalamount())) {
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    if (creditDocument.getAmount().equals(debitDocument.getTotalamount()))
                        creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                }
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (invoiceAdjustedAmt <= creditDocument.getAmount() || invoiceAdjustedAmt.equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount() && !isFirstCN)
                        //partially adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            } else {
                //status for payment
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()))
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (debitDocRemainingAmount <= creditDocument.getAmount() || debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
                        if (debitDocument.getPaymentStatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                            debitDocument.setStatus(Constants.DEBIT_DOC_STATUS.APPROVED);
                        }
                        //StaffUser staffUser=null;
                        //if(creditDocument.getCreatedById()!=null)
                        //staffUser=staffUserRepository.findById(creditDocument.getCreatedById()).orElse(null);
                        //if(staffUser!=null && staffUser.getPartnerid()!=null && staffUser.getPartnerid()!=1)
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        List<TempPartnerLedgerDetail> details = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());

                        if (details != null && !details.isEmpty()) {
                            tempPartnerLedgerDetailsRepository.deleteAll(details);
                            partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details, debitDocument.getCustomer());
                        }

                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                        //partially adjusted
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    }
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            }
            if (debitDocument.getBillrunstatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.CANCELLED) && (debitDocument.getTotalamount() > debitDocument.getAdjustedAmount())) {
                debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
//                debitDocRepository.save(debitDocument);
            }
            creditDebitDocMapping.setAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            debitDocRepository.save(debitDocument);
            creditDocRepository.save(creditDocument);

            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), false, null, null, null);
            if (debitDocument != null && debitDocument.getBillrunstatus().equalsIgnoreCase("VOID"))
                prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), true, null, null, null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
        } catch (Exception ex) {
            logger.error("Error while adjust CN amount against invoice: " + ex.getMessage());
        }
        return creditDebitDocMapping;
    }

    @Transactional
    public CreditDebitDocMapping adjustCNPaymentAmountAgainstDebitDocForChangePlan(DebitDocument debitDocument, CreditDocument creditDocument, boolean isFullyPaidDebitDoc, DebitDocument oldDebiDocument) {
        List<CreditDebitDocMapping> debitDocMappings = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
        if(debitDocMappings!=null && !debitDocMappings.isEmpty())
        {
            debitDocMappings = debitDocMappings.stream().filter(x -> x.getAdjustedAmount().doubleValue() == 0.0).collect(Collectors.toList());
            if (debitDocMappings != null && !debitDocMappings.isEmpty())
                creditDebitDocMapping = debitDocMappings.get(0);
        }
        try {
            if (debitDocument == null) {
                debitDocument = oldDebiDocument;
            }
            Double debitDocRemainingAmount = debitDocument.getTotalamount();
            Double cnRemainingAmount = creditDocument.getAmount();
            boolean isFirstCN = false;
            //DebitDoc adjusted amount
            DecimalFormat df = new DecimalFormat("0.00");
            if (debitDocument.getAdjustedAmount() != null) {
                debitDocRemainingAmount = debitDocRemainingAmount - debitDocument.getAdjustedAmount();
                debitDocRemainingAmount = Double.parseDouble(df.format(debitDocRemainingAmount));
                if (!isFullyPaidDebitDoc) {
                    // set in adjustment amount in newdebit doc
                    if (oldDebiDocument == null) {
                        oldDebiDocument = debitDocument;
                    }
                    oldDebiDocument.setAdjustedAmount(debitDocument.getAdjustedAmount());
                    if (Math.abs(oldDebiDocument.getTotalamount() - oldDebiDocument.getAdjustedAmount()) < 0.1) {
                        oldDebiDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
                    } else if (Math.abs(oldDebiDocument.getTotalamount() - oldDebiDocument.getAdjustedAmount()) < oldDebiDocument.getTotalamount()) {
                        oldDebiDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    } else {
                        oldDebiDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                    }
                    debitDocRepository.save(oldDebiDocument);
                }
            } else {
                debitDocument.setAdjustedAmount(0d);
                isFirstCN = true;
            }


            //CN adjusted amount
            if (creditDocument.getAdjustedAmount() != null) {
                cnRemainingAmount = cnRemainingAmount - creditDocument.getAdjustedAmount();
            } else {
                creditDocument.setAdjustedAmount(0d);
            }


            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setIsDeleted(Boolean.FALSE);

            //CN is partialy
            if (debitDocRemainingAmount > cnRemainingAmount) {
                //Full CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + cnRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(cnRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + cnRemainingAmount);
            }//CN is more than invoice amount
            else if (debitDocRemainingAmount < cnRemainingAmount) {
                //Partial CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }//CN amount is equal to invoice amount
            else {
                //Full CN adjusted
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }
            //status for CN
            if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findMappingBydebtDocId(debitDocument.getId(), Constants.PAYMENT_TYPE);
                Double invoiceAdjustedAmt = debitDocument.getAdjustedAmount();
                if (!CollectionUtils.isEmpty(creditDebitDocMappings)) {
                    if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                        Double oldAdjustedAmt = creditDebitDocMappings.stream()
                                .mapToDouble(mapping -> mapping.getAdjustedAmount() != null ? mapping.getAdjustedAmount() : 0.00)
                                .sum();
                        invoiceAdjustedAmt = invoiceAdjustedAmt - oldAdjustedAmt;
                    }
                }
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()) || creditDocument.getAmount().equals(debitDocument.getTotalamount())) {
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    if (creditDocument.getAmount().equals(debitDocument.getTotalamount()))
                        creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                }
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (invoiceAdjustedAmt <= creditDocument.getAmount() || invoiceAdjustedAmt.equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        if (!isFullyPaidDebitDoc) {
                            debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                            debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                        } else {
                            oldDebiDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                            oldDebiDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                            debitDocRepository.save(oldDebiDocument);
                            if (Math.abs(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount()) < 0.1) {
                                debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PAYABLE);
                            } else {
                                debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                            }
                        }
                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount() && !isFirstCN)
                        //partially adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            } else {
                //status for payment
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()))
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (debitDocRemainingAmount <= creditDocument.getAmount() || debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
                        if (debitDocument.getPaymentStatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                            debitDocument.setStatus(Constants.DEBIT_DOC_STATUS.APPROVED);
                        }
                        //StaffUser staffUser=null;
                        //if(creditDocument.getCreatedById()!=null)
                        //staffUser=staffUserRepository.findById(creditDocument.getCreatedById()).orElse(null);
                        //if(staffUser!=null && staffUser.getPartnerid()!=null && staffUser.getPartnerid()!=1)
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        List<TempPartnerLedgerDetail> details = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());

                        if (details != null && !details.isEmpty()) {
                            tempPartnerLedgerDetailsRepository.deleteAll(details);
                            partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details, debitDocument.getCustomer());
                        }

                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                        //partially adjusted
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    }
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            }
            if (debitDocument.getBillrunstatus().equalsIgnoreCase(Constants.DEBIT_DOC_STATUS.CANCELLED) && (debitDocument.getTotalamount() > debitDocument.getAdjustedAmount())) {
                debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
//                debitDocRepository.save(debitDocument);
            }
            creditDebitDocMapping.setAmount(creditDocument.getAmount());
            //creditDebtMappingRepository.save(creditDebitDocMapping);
            debitDocRepository.save(debitDocument);
            creditDocRepository.save(creditDocument);

            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), false, null, null, null);
            if (debitDocument != null && debitDocument.getBillrunstatus().equalsIgnoreCase("VOID"))
                prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), true, null, null, null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
        } catch (Exception ex) {
            logger.error("Error while adjust CN amount against invoice: " + ex.getMessage());
        }
        return creditDebitDocMapping;
    }

    @Transactional
    public CreditDebitDocMapping adjustPaymentAmountAgainstDebitDoc(DebitDocument debitDocument, CreditDocument creditDocument, List<Integer> oldDebitDocumentIds) {
        List<CreditDebitDocMapping> debitDocMappings = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
        Double oldAdjustedAmount = 0.0;
        if(debitDocMappings!=null && !debitDocMappings.isEmpty())
        {
            for (CreditDebitDocMapping debitDocMapping : debitDocMappings) {
                if (!oldDebitDocumentIds.contains(debitDocMapping.getDebtDocId()))
                    debitDocMappings.remove(debitDocMapping);
            }

            oldAdjustedAmount = debitDocMappings.stream().filter(x -> x.getAdjustedAmount() != null).mapToDouble(y -> y.getAdjustedAmount()).sum();

            //debitDocMappings=finalDebitDocMappings;
            //debitDocMappings=debitDocMappings.stream().filter(x->x.getAdjustedAmount().doubleValue()==0.0).collect(Collectors.toList());
            //if(debitDocMappings!=null && !debitDocMappings.isEmpty())
            //creditDebitDocMapping=debitDocMappings.get(0);
        }
        try {
            creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() - oldAdjustedAmount);
            Double debitDocRemainingAmount = debitDocument.getTotalamount();
            Double cnRemainingAmount = creditDocument.getAmount();
            boolean isFirstCN = false;
            //DebitDoc adjusted amount
            DecimalFormat df = new DecimalFormat("0.00");
            if (debitDocument.getAdjustedAmount() != null) {
                debitDocRemainingAmount = debitDocRemainingAmount - debitDocument.getAdjustedAmount();
                debitDocRemainingAmount = Double.parseDouble(df.format(debitDocRemainingAmount));

            } else {
                debitDocument.setAdjustedAmount(0d);
                isFirstCN = true;
            }


            //CN adjusted amount
            if (creditDocument.getAdjustedAmount() != null) {
                cnRemainingAmount = oldAdjustedAmount;
            } else {
                creditDocument.setAdjustedAmount(0d);
            }


            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setIsDeleted(Boolean.FALSE);

            //CN is partialy
            if (debitDocRemainingAmount > cnRemainingAmount) {
                //Full CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + cnRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(cnRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + cnRemainingAmount);
            }//CN is more than invoice amount
            else if (debitDocRemainingAmount < cnRemainingAmount) {
                //Partial CN adjusted
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }//CN amount is equal to invoice amount
            else {
                //Full CN adjusted
                creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
                creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
            }
            //status for CN
            if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findMappingBydebtDocId(debitDocument.getId(), Constants.PAYMENT_TYPE);
                Double invoiceAdjustedAmt = debitDocument.getAdjustedAmount();
                if (!CollectionUtils.isEmpty(creditDebitDocMappings)) {
                    if (creditDocument.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                        Double oldAdjustedAmt = creditDebitDocMappings.stream().mapToDouble(CreditDebitDocMapping::getAdjustedAmount).sum();
                        invoiceAdjustedAmt = invoiceAdjustedAmt - oldAdjustedAmt;
                    }
                }
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()))
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (invoiceAdjustedAmt <= creditDocument.getAmount() || invoiceAdjustedAmt.equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocument.setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount() && !isFirstCN)
                        //partially adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            } else {
                //status for payment
                if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()))
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                else
                    creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                if (debitDocument.getAdjustedAmount() != null) {
                    if (debitDocRemainingAmount <= cnRemainingAmount || debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount())) {
                        //fully adjusted
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
                        //StaffUser staffUser=null;
                        //if(creditDocument.getCreatedById()!=null)
                        //staffUser=staffUserRepository.findById(creditDocument.getCreatedById()).orElse(null);
                        //if(staffUser!=null && staffUser.getPartnerid()!=null && staffUser.getPartnerid()!=1)
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        List<TempPartnerLedgerDetail> details = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());

                        if (details != null && !details.isEmpty()) {
                            tempPartnerLedgerDetailsRepository.deleteAll(details);
                            partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details, debitDocument.getCustomer());
                        }

                    } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                        //partially adjusted
                        //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(debitDocument.getCustomer(),creditDocument.getAmount(),debitDocument.getId().longValue());
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    }
                    if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            }
            creditDebitDocMapping.setAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            debitDocRepository.save(debitDocument);
            creditDocRepository.save(creditDocument);

            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), false, null, null, null);
            if (debitDocument != null && debitDocument.getBillrunstatus().equalsIgnoreCase("VOID"))
                prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), true, null, null, null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
        } catch (Exception ex) {
            logger.error("Error while adjust CN amount against invoice: " + ex.getMessage());
        }
        return creditDebitDocMapping;
    }


    @Transactional
    public void setCreditNoteDataToTable(List<CustomerChargeDBR> customerChargeDBRS, CreditDocument document, DebitDocument debitDocument) {
        try {
            Set<Long> chargeIds = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId() != null).map(CustomerChargeDBR::getChargeId).collect(Collectors.toSet());
            List<CreditDocChargeRel> creditDocChargeRels = new ArrayList<>();
            Double discountInPer = 0d;
            if (debitDocument.getCustpackrelid() != null) {
                if (custPlanMappingRepository.findDiscountById(debitDocument.getCustpackrelid()) != null) {
                    discountInPer = custPlanMappingRepository.findDiscountById(debitDocument.getCustpackrelid());
                }
            }
            for (Long chargeId : chargeIds) {
                Double totalChargeAmount = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId().equals(chargeId)).mapToDouble(CustomerChargeDBR::getDbr).sum();
                Double discountAmount = getAmountFromPer(totalChargeAmount, discountInPer);
                //totalAmount after discount
                totalChargeAmount = totalChargeAmount - discountAmount;
                Charge charge = chargeRepository.findById(chargeId.intValue()).get();
                Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge, totalChargeAmount);
                Double totalAmountForCharge = totalChargeAmount + taxAmount;

                CreditDocChargeRel creditDocChargeRel = calculateCNChargeRelData(debitDocument, totalAmountForCharge, document, discountInPer, charge);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while adding CN charge rel amount");
        }
    }

    public CreditDocChargeRel calculateCNChargeRelData(DebitDocument debitDocument, Double chargeAdjustedAmount, CreditDocument document, Double disPer, Charge charge) {
        CreditDocChargeRel creditDocChargeRel = new CreditDocChargeRel();
        try {

            Double invoiceAmount = debitDocument.getTotalamount();
            Double cnPerFromInvoice = (document.getAmount() * 100) / invoiceAmount;
            Double factor = (chargeAdjustedAmount * 100) / invoiceAmount;
            //charge amount
            Double chargeFact1 = (invoiceAmount * (factor / 100));
            Double chargeFact2 = (cnPerFromInvoice / 100);
            Double flatamount = chargeFact1 * chargeFact2;
            Double totalTax = 0.0;
            //Tax calculation
            List<CreditDocTaxRel> creditDocTaxRelList = calculatechargeTaxDetails(document, charge, flatamount, creditDocChargeRel);
            if (!CollectionUtils.isEmpty(creditDocTaxRelList)) {
                totalTax = creditDocTaxRelList.stream().mapToDouble(CreditDocTaxRel::getTaxAmount).sum();
//                creditDocChargeRel.setCreditDocTaxRel(creditDocTaxRelList);
            }
            //final amount
            Double taxTotalAmount = flatamount - totalTax;
            //Discount
            Double discount = (taxTotalAmount * disPer) / 100;

            //save data to entity
            creditDocChargeRel.setChargeAmount(taxTotalAmount);
            creditDocChargeRel.setDiscount(discount);
            creditDocChargeRel.setTaxAmount(totalTax);
            creditDocChargeRel.setTotalAmount(flatamount);
            creditDocChargeRel.setCreditdocid(document.getId());
            creditDocChargeRel.setChargeid(charge.getId());
            creditDocChargeRel.setDebitDocId(debitDocument.getId());
            creditDocChargeRel = creditDocChargeRelRepository.save(creditDocChargeRel);
            CreditDocChargeRel finalCreditDocChargeRel = creditDocChargeRel;
            creditDocTaxRelList.forEach(creditDocTaxRel -> {
                creditDocTaxRel.setCreditDocChargeRel(finalCreditDocChargeRel);
            });
            creditDocTaxRelRepository.saveAll(creditDocTaxRelList);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception when save CN detail: " + ex.getMessage());
        }
        return creditDocChargeRel;
    }

    public List<CreditDocTaxRel> calculatechargeTaxDetails(CreditDocument creditDocument, Charge charge, Double chargeFlatamount, CreditDocChargeRel creditDocChargeRel) {
        Double totalTaxAmount = 0.0;
        List<CreditDocTaxRel> creditDocTaxRelList = new ArrayList<>();

        Optional<Tax> primaryTax = taxRepository.findById(charge.getTax().getId());
        if (primaryTax.isPresent()) {
            Tax tierTax = primaryTax.get();
            List<TaxTypeTier> taxTypeTiers = tierTax.getTieredList();
            for (TaxTypeTier taxTypeTier : taxTypeTiers) {
                Double chargeWithPerAmt = (100 + taxTypeTier.getRate()) / 100;
                Double chargeWithFlatAmt = chargeFlatamount / chargeWithPerAmt;
                Double totalTax = chargeFlatamount - chargeWithFlatAmt;
                CreditDocTaxRel creditDocTaxRel = new CreditDocTaxRel(charge, creditDocument, totalTax, creditDocChargeRel);
                creditDocTaxRelList.add(creditDocTaxRel);
            }
        }
        return creditDocTaxRelList;
    }

    public Double getAmountFromPer(Double amount, Double disPer) {
        return (amount * disPer) / 100;
    }

    public void addLedgeAfterApproval(CreditDocument creditDocument) {

        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        Optional<Customers> customers = customersRepository.findById(creditDocument.getCustomer().getId());
        Double paymentAmount = creditDocument.getAmount();

        ledger = customerLedgerRepository.findByCustomer(customers.get());
        if (Objects.nonNull(ledger)) {
            ledger.setTotalpaid(ledger.getTotalpaid() + creditDocument.getAmount());
            ledger.setTotaldue(ledger.getTotaldue() - creditDocument.getAmount());
            customerLedgerRepository.save(ledger);
        }

        ledgerDtls = new CustomerLedgerDtls();
        if (creditDocument.getTdsamount() != null && creditDocument.getTdsamount() > 0) {
            paymentAmount = paymentAmount - creditDocument.getTdsamount();
        }
        if (creditDocument.getAbbsAmount() != null && creditDocument.getAbbsAmount() > 0) {
            paymentAmount = paymentAmount - creditDocument.getAbbsAmount();
        }
        ledgerDtls.setAmount(paymentAmount);
        ledgerDtls.setPaymentMode(creditDocument.getPaymode());
        //TODO: Need to add BankManagement
        if (creditDocument.getBankManagement() != null) {
            BankManagement bankManagement = bankManagementRepository.findById(creditDocument.getBankManagement()).orElse(null);
            if (bankManagement != null) {
                ledgerDtls.setBank(bankManagement.getBankname());
                ledgerDtls.setBranch(bankManagement.getBankcode());
            }
        }
        ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
        ledgerDtls.setCreditdocid(creditDocument.getId());
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsDelete(false);
        ledgerDtls.setDescription(creditDocument.getRemarks());
        if (creditDocument.getPaytype().equalsIgnoreCase("advance")) {
            if (customers.get() != null && (customers.get().getId().intValue() == 1 || customers.get().getId().intValue() == 2))
                ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_BUSINESS_PROMOTION);
            else
                ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_PAYMENT);
        } else if (creditDocument.getPaytype().equalsIgnoreCase("invoice")) {
            if (customers.get() != null && (customers.get().getId().intValue() == 1 || customers.get().getId().intValue() == 2))
                ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_BUSINESS_PROMOTION);
            else
                ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_PAYMENT);
        } else if (creditDocument.getPaytype().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
            ledgerDtls.setTranscategory(Constants.TRANS_CREDIT_NOTE);
        } else if (creditDocument.getPaytype().equalsIgnoreCase(Constants.transfer)) {
            ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_TRANSFER);
        }
        else {
            ledgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_REFUND);
        }

        if (customers.isPresent()) ledgerDtls.setCustomer(customers.get());
        ledgerDtls.setTranstype(Constants.CUSTOMER_LEDGER.TRANS_TYPE_CREDIT);
        if (creditDocument.getType().equalsIgnoreCase(Constants.CUSTOMER_LEDGER.TRANS_TYPE_DEBIT)) {
            ledgerDtls.setTranstype(Constants.CUSTOMER_LEDGER.TRANS_TYPE_DEBIT);
        }

        ledgerDtls = customerLedgerDtlsRepository.save(ledgerDtls);

        if (creditDocument.getTdsamount() != null && creditDocument.getTdsamount() > 0) {
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
            customerLedgerDtls.setCreditdocid(creditDocument.getId());
            customerLedgerDtls.setCREATE_DATE(LocalDateTime.now());
            customerLedgerDtls.setIsDelete(false);
            customerLedgerDtls.setTranscategory("TDS");
            customerLedgerDtls.setTranstype("CR");
            customerLedgerDtls.setCustomer(creditDocument.getCustomer());
            customerLedgerDtls.setDescription(creditDocument.getRemarks());
            customerLedgerDtls.setAmount(creditDocument.getTdsamount());
            customerLedgerDtls.setPaymentMode(creditDocument.getPaymode());
            customerLedgerDtlsRepository.save(customerLedgerDtls);
        }
        if (creditDocument.getAbbsAmount() != null && creditDocument.getAbbsAmount() > 0) {
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
            customerLedgerDtls.setCreditdocid(creditDocument.getId());
            customerLedgerDtls.setCREATE_DATE(LocalDateTime.now());
            customerLedgerDtls.setIsDelete(false);
            customerLedgerDtls.setTranscategory("ABBS");
            customerLedgerDtls.setTranstype("CR");
            customerLedgerDtls.setCustomer(creditDocument.getCustomer());
            customerLedgerDtls.setDescription(creditDocument.getRemarks());
            customerLedgerDtls.setAmount(creditDocument.getAbbsAmount());
            customerLedgerDtls.setPaymentMode(creditDocument.getPaymode());
            customerLedgerDtlsRepository.save(customerLedgerDtls);
        }
    }

    public String getInvoiceNo() {
        String currinvoiceNo = null;
        String newInvoiceNo = null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();

            currinvoiceNo = creditDocRepository.getFuction();

            StringBuilder sb = new StringBuilder();
            sb.append("CN");
            sb.append(current_Year);
            sb.append("-");
            while (sb.length() < 14 - currinvoiceNo.length()) {
                sb.append('0');
            }
            sb.append(currinvoiceNo);
            newInvoiceNo = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }


    public CreditDocument save(CreditDocMessage message) throws Exception {
        System.out.println("====================Save Credit doc start: " + message.toString() + "============================");
        Customers customers = customersRepository.findById(message.getCustomer()).get();
        customers.setWalletbalance(message.getWalletBalance());
        customersRepository.save(customers);
        CreditDocument creditDoc = new CreditDocument(message, customers);
        List<CreditDebitDocMapping> creditDebitDocMapping = message.getCreditDebitDocMappingList();

        /*if (creditDoc.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE) && creditDoc.getInvoiceId()!=null) {
            DebitDocument debitDocument=debitDocRepository.findById(creditDoc.getInvoiceId()).orElse(null);
            if(debitDocument!=null && debitDocument.getAdjustedAmount()!=null && (debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount()) || creditDoc.getAmount() > (debitDocument.getTotalamount()-debitDocument.getAdjustedAmount())))
                return creditDoc;
        }*/

        if (message.getStatus().equalsIgnoreCase("rejected")) {
            creditDoc.setStatus("rejected");
            creditDocRepository.save(creditDoc);
            for (CreditDebitDocMapping creditDebitDocMapping1 : creditDebitDocMapping) {
                creditDebtMappingRepository.save(creditDebitDocMapping1);
            }
            return creditDoc;
        } else {
            CreditDocument document = null;
            if (message.getId() != null) {
                document = creditDocRepository.findById(message.getId()).orElse(null);
                if (document.getWithDrawCreditdocId() != null) {
                    creditDoc.setWithDrawCreditdocId(document.getWithDrawCreditdocId());
                }
                if (document != null)
                    creditDoc.setXmldocument(document.getXmldocument());
            }
            creditDoc = creditDocRepository.save(creditDoc);
            if (creditDoc.getType().equalsIgnoreCase(Constants.TRANS_CREDIT_NOTE)) {
                try {
                    List<CreditDebitDocMapping> cnDebitDocMappingList = message.getCreditDebitDocMappingList();
                    if (!CollectionUtils.isEmpty(cnDebitDocMappingList)) {
                        CreditDocument finalCreditDoc = creditDocRepository.findById(creditDoc.getId()).get();
                        List<CreditDebitDocMapping> debitDocMappings = cnDebitDocMappingList.stream().peek(cdm -> {
                            cdm.setCreditDocId(finalCreditDoc.getId());
                            cdm.setDebtDocId(finalCreditDoc.getInvoiceId());
                            cdm.setAdjustedAmount(finalCreditDoc.getAdjustedAmount());
                            cdm.setAmount(finalCreditDoc.getAmount());
                        }).map(CreditDebitDocMapping::new).collect(Collectors.toList());
                        debitDocMappings = creditDebtMappingRepository.saveAll(debitDocMappings);
                        System.out.println("====================Save Credit doc calling : adjustCreditNote" + creditDoc.toString() + "===========================");
                        adjustCreditNote(creditDoc);
                        System.out.println("====================Save Credit doc returning from adjustCreditNote===========================");
                        ClientService clientService = null;
                        Integer MvnoId=customers.getMvnoId()!=null?customers.getMvnoId():1;
                        try {
                            clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.REVENUE_AUTHORITY_NAME, MvnoId);
                        } catch (Exception e) {
                            // Log the exception but continue gracefully
                            logger.warn("ClientService not found for REVENUE_AUTHORITY_NAME and mvnoId: " + MvnoId, e);
                            clientService = null;
                        }
                        try {
                            if(clientService!=null && "KRA".equalsIgnoreCase(clientService.getValue())) {
                                kraUtils.processEtimsAddCreditNote(Collections.singletonList(creditDoc));
                            }
                        } catch (Exception e) {
                            ApplicationLogger.logger.error("Some Exception occured while integrating to {}",e);
                        }
//                        if(message.getStatus()!=null && message.getStatus().equalsIgnoreCase("approved")) {
//                            CreditDocument creditDocument=creditDocRepository.findById(creditDoc.getId()).orElse(null);
//                            if(creditDocument!=null)
//                            {
//                                creditDocument.setStatus(message.getStatus());
//                                creditDoc = creditDocRepository.save(creditDocument);
//                            }
//                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("Error while adjust CN: " + ex.getMessage());
                }
            } else {
                CreditDocument creditDocument = creditDocRepository.findById(creditDoc.getId()).orElse(null);
                if(creditDocument!=null)
                {
                    creditDocument.setStatus(message.getStatus());
                    creditDoc = creditDocRepository.save(creditDocument);
                }
                List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
                if (creditDoc.getInvoiceId() != null) {
                    String customerStatus = customersRepository.findStatusById(creditDoc.getCustomer().getId());
                    if (customerStatus.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION) || customerStatus.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING)) {
                        CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                        creditDebitDocMappingPojo.setInvoiceId(creditDoc.getInvoiceId());
                        CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                        creditDebitDataPojo.setAmount(message.getAmount());
                        creditDebitDataPojo.setId(creditDoc.getId());
                        List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                        creditDebitDataPojoList.add(creditDebitDataPojo);
                        creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                        adjustManualPaymentToCafInvoice(creditDebitDocMappingPojo);
                    } else {
                        Optional<DebitDocument> optionalDebitDocument = debitDocRepository.findById(creditDoc.getInvoiceId());
                        if (optionalDebitDocument.isPresent()) {
                            creditDebitDocMappingList.add(adjustCNPaymentAmountAgainstDebitDoc(optionalDebitDocument.get(), creditDoc));
                        }
                    }
                }
            }

//            if (document.getWithDrawCreditdocId()!=null){
//                List<CreditDocument> docs = new ArrayList<>();
//                CreditDocument creditDocWithdrawAgnstId = creditDocRepository.findById(document.getWithDrawCreditdocId()).get();
//                double currentAdjusted = creditDocWithdrawAgnstId.getAdjustedAmount();
//                double amount = document.getAmount();
//                String custStatus = document.getCustomer().getStatus();
//                if (Constants.CUSTOMER_STATUS_NEW_ACTIVATION.equalsIgnoreCase(custStatus)) {
//                    if (currentAdjusted > (currentAdjusted + amount)) {
//                        creditDocWithdrawAgnstId.setAdjustedAmount(currentAdjusted + amount);
//                    }
//                } else if (Constants.CUSTOMER_STATUS_ACTIVE.equalsIgnoreCase(custStatus)) {
//                    creditDocWithdrawAgnstId.setAdjustedAmount(currentAdjusted + amount);
//                }
//                document.setAdjustedAmount(document.getAmount());
//                document.setStatus(CommonConstants.PAYMENT_STATUS_FULLY_ADJUSTED);
//                docs.add(creditDocWithdrawAgnstId);
//                docs.add(document);
//                creditDocRepository.saveAll(docs);
//
//            }
            //Added Logic to adjust Credit doc amount against transfer or withdraw
            if (("DR".equalsIgnoreCase(document.getType()) && "transfer".equalsIgnoreCase(document.getPaytype())) || ("DR".equalsIgnoreCase(document.getType()) && "Withdrawal".equalsIgnoreCase(document.getPaytype()))) {
                List<Integer> list = creditDebtMappingRepository.findCreditDocIdsByWithdrawId(document.getId());
                List<CreditDocument> creditDocumentList = creditDocRepository.findAllByIdInOrderByAmount(list);
                Double tranferedAmount = document.getAmount();
                for (CreditDocument doc : creditDocumentList) {
                    Double remainingAmount = doc.getAmount() - doc.getAdjustedAmount();
                    if (remainingAmount <= tranferedAmount) {
                        tranferedAmount = tranferedAmount - remainingAmount;
                        doc.setAdjustedAmount(doc.getAdjustedAmount() + remainingAmount);
                        doc.setStatus("Fully Adjusted");
                    } else {
                        doc.setAdjustedAmount(doc.getAdjustedAmount() + tranferedAmount);
                        tranferedAmount = 0.0;
                        creditDocRepository.save(doc);
                        doc.setStatus("Partialy Paid");
                        break;
                    }
                    creditDocRepository.save(doc);
                }

            }

            if (Constants.CUSTOMER_STATUS_NEW_ACTIVATION.equalsIgnoreCase(document.getCustomer().getStatus())) {
                if (("DR".equalsIgnoreCase(document.getType()) && "transfer".equalsIgnoreCase(document.getPaytype())) || ("DR".equalsIgnoreCase(document.getType()) && "Withdrawal".equalsIgnoreCase(document.getPaytype()))) {
                    List<Integer> creditDocIds = creditDebtMappingRepository.findCreditDocIdsByWithdrawId(document.getId());
                    Double tranferedAmount = document.getAmount();
                    for (Integer creditDocId : creditDocIds) {
                        if (tranferedAmount > 0.0) {
//                        List<Integer> trialDebitdocIdList = creditDebtMappingRepository.findTrialDebitDocumentIdByCreditDocId(creditDocId);
                            List<TrialDebitProjection> list = trialDebitDocRepository.findTrialDebitDocsWithAmount(creditDocId);
                            list.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));
                            if (CollectionUtils.isEmpty(list)) continue;
                            for (TrialDebitProjection trialDebitProj : list) {
                                Integer trialDebitdocId = trialDebitProj.getTrialDebitDocumentId();
                                Optional<TrialDebitDocument> trialDebitDoc = trialDebitDocRepository.findById(trialDebitdocId);
                                if (trialDebitDoc.isPresent()) {
                                    TrialDebitDocument doc = trialDebitDoc.get();
                                    if (doc.getAdjustedAmount() <= tranferedAmount) {
                                        tranferedAmount = tranferedAmount - doc.getAdjustedAmount();
                                        doc.setAdjustedAmount(0.0);
                                        doc.setPaymentStatus("Partialy Paid");
                                    } else {
                                        doc.setAdjustedAmount(doc.getAdjustedAmount() - tranferedAmount);
                                        tranferedAmount = 0.0;
                                        doc.setPaymentStatus("Partialy Paid");
                                        trialDebitDocRepository.save(doc);
                                        break;
                                    }
                                    trialDebitDocRepository.save(doc);
                                }
                            }
                        }
                    }
                }
            }
            if (!creditDoc.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)) {
                addLedgeAfterApproval(creditDoc);
            }
            if (!("DR".equalsIgnoreCase(document.getType())) && !("CREDITNOTE".equalsIgnoreCase(document.getPaytype()))){
                try {
                    subscriberService.automatePayment(customers.getId());
                } catch (Exception e) {
                    logger.error("Exception occurred while auto renew payment on payment approval for customer {}",customers.getId(),e);
                }
            }
            return creditDoc;
        }


//        if (creditDoc.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
//            adjustCreditNote(creditDoc, creditDebitDocMappingList);
//        } else {
//            if (creditDoc.getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
////                adjustWithDrawal(creditDoc);
//            } else {
//                adjustPayment(creditDoc, creditDebitDocMappingList);
//            }
//        }
    }

    public void adjustCreditNote(CreditDocument creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings) {
        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
            List<CustomerChargeDBR> customerChargeDBRList = dbrService.findAllCustomerChargedbrByDebitDoc(debitDocument);
            Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
            if (totalCreditNoteGenerated == 0) {
                if (creditDocument.getAmount() > debitDocument.getTotalamount()) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becauae invoice amount exceeds", null);
                }
            } else if (creditDocument.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note because invoice amount exceeds", null);
            }

            if (creditDocument != null && creditDocument.getCustomer().getId().intValue() == 1) {
                if (creditDocument.getInvoiceId() != null)
                {
                    if (debitDocument.getIsCNEnable()) {
                        creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                        if (creditDocument.getAdjustedAmount() != null)
                            creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                        else
                            creditDocument.setAdjustedAmount(creditDocument.getAmount());
                        creditDocRepository.save(creditDocument);

                        creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
                        creditDebitDocMapping.setIsDeleted(false);
                        creditDebitDocMapping.setDebtDocId(debitDocument.getId());
                        creditDebitDocMapping.setCreditDocId(creditDocument.getId());
                        creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

                        List<CreditDebitDocMapping> debitDocMapping = creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());

                        if(debitDocMapping!=null && !debitDocMapping.isEmpty())
                        {
                            debitDocMapping.get(0).setAdjustedAmount(creditDocument.getAmount());
                            creditDebtMappingRepository.save(debitDocMapping.get(0));
                        }
                        if (debitDocument.getAdjustedAmount() != null)
                            debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
                        else
                            debitDocument.setAdjustedAmount(creditDocument.getAmount());
                        reversalPaymentForOrg(creditDocument.getAmount(), debitDocument, true);
                        return;
                    }
                }
                return;
            }

            DecimalFormat df = new DecimalFormat("#.00");
            List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
            Double pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
            Double pendingRevenueWithTax = prepaidInvoiceService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);
            Double creditAmountExcludeTax = dbrService.getCreditNotePriceExcludingTax(debitDocument, creditDocument.getAmount());

            //if(creditDocument.getAmount() > pendingRevenueWithTax)
            //{
            // throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note because Pending Revenue amount exceeds", null);
            // }

            List<CustPlanMappping> custPlanMapppings = IterableUtils.toList(custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId().longValue()));
            Set<Integer> cprids = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toSet());


            if ((creditDocument.getAmount() + totalCreditNoteGenerated >= debitDocument.getTotalamount()) || (pendingRevenueWithTax.doubleValue() - creditDocument.getAmount().doubleValue() < 0)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    custPlanMappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                    //ANG-4987: resolved
                    if (custPlanMappping.getStartDate().isAfter(LocalDateTime.now())) {
                        custPlanMappping.setStartDate(LocalDateTime.now().minusMinutes(1));
                        custPlanMappping.setEndDate(LocalDateTime.now());
                        custPlanMappping.setExpiryDate(LocalDateTime.now());
                    } else {
                        custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
                        custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
                    }
                    if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                        custPlanMappping.setStartDate(LocalDateTime.now());
                        custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                        custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                    }
                });
            }

            Double amountToBePaid = 0d;
            Double remainingAmount = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            remainingAmount = creditDocument.getAmount() - amountToBePaid;
//            if all amount from credit note adjusted with invoice
            if (remainingAmount == 0) {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                if (debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount()) || (pendingRevenueWithTax.doubleValue() == creditDocument.getAmount().doubleValue()))
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                creditDocument.setAdjustedAmount(creditDocument.getAmount());
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            }
//            when amount from credit note is greater than pending amount of invoice
            else if (remainingAmount > 0) {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                creditDocument.setAdjustedAmount(creditDocument.getAmount() - remainingAmount);
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
                creditDebitDocMapping.setAdjustedAmount(amountToBePaid);
            }
//            when amount from credit note is fully adjusted but invoice has some amount left to adjust
            else {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(creditDocument.getAmount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
                }
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.setAdjustedAmount(creditDocument.getAmount());
                creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            }

//           if total amount of invoice adjusted through credit note or total amount of credit note generated for same invoice set invoice status cancelled

            if (creditDocument.getAmount() + totalCreditNoteGenerated == debitDocument.getTotalamount() || (pendingRevenueWithTax.doubleValue() == creditDocument.getAmount().doubleValue())) {
                debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                if (cprids.size() > 0) {
                    List<Integer> debitdocids = custPlanMappingRepository.findAllByCustRefId(cprids);
                    if (debitdocids.size() > 0) {
                        List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debitdocids);
                        debitDocuments.stream().forEach(i -> i.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED));
                        debitDocRepository.saveAll(debitDocuments);
                    }
                }
            }
//          if after all adjustment there is some amount left in credit note set inovice as payable
            if (remainingAmount > 0) {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
            }
            creditDebtMappingRepository.save(creditDebitDocMapping);
            debitDocRepository.save(debitDocument);

            dbrService.creditNoteDbrEntry(debitDocument, creditDocument.getAmount(), true);
//            debitDocService.adjustBillToSubisuInvoiceWithCreditNote(creditDocument.getAmount(),debitDocument);
        }
    }

    //    TODO : reversal for org
    public void reversalPaymentForOrg(Double creditNoteAmount, DebitDocument document, Boolean flag) {
        if (document != null) {
            if(document!=null && document.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.APPROVED))
            {
                CreditDocument creditDocument = new CreditDocument();
                creditDocument.setAdjustedAmount(-creditNoteAmount);
                creditDocument.setAmount(-creditNoteAmount);
                creditDocument.setCustomer(document.getCustomer());
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.setLcoid(null);
                creditDocument.setPaymentdate(LocalDate.now());
                creditDocument.setType(CommonConstants.TRANS_CATEGORY_PAYMENT);
                creditDocument.setCreatedate(LocalDateTime.now());
                creditDocument.setIsDelete(false);
                creditDocument.setTdsflag(false);
//                creditDocument.setCreditdocumentno(getPaymentInvoiceNo());
                creditDocument.setPaytype("invoice");
//                creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                creditDocument.setPaymode(CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION);
                creditDocument.setTds_received(false);
//                if(getLoggedInUser() != null) {
//                    creditDocument.setLastModifiedById(getLoggedInUser().getStaffId());
//                    creditDocument.setLastModifiedByName(getLoggedInUser().getFullName());
//                    creditDocument.setCreatedById(getLoggedInUser().getStaffId());
//                    creditDocument.setCreatedByName(getLoggedInUser().getFullName());
//                    creditDocument.setMvnoId(getLoggedInUser().getMvnoId());
//                }
//                creditDocument.setBuID(document.getCustomer().getBuId());
//                creditDocument.setXmldocument(assemblePaymentXML(creditDocument, CommonUtils.ADDR_TYPE_PRESENT));
                creditDocument = creditDocRepository.save(creditDocument);

                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                creditDebitDocMapping.setAdjustedAmount(-creditNoteAmount);
                creditDebitDocMapping.setIsDeleted(false);
                creditDebitDocMapping.setDebtDocId(document.getId());
                creditDebitDocMapping.setCreditDocId(creditDocument.getId());
                creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

                if (document.getAdjustedAmount() != null)
                    document.setAdjustedAmount(document.getAdjustedAmount() - creditNoteAmount);
                else
                    document.setAdjustedAmount(-creditNoteAmount);
                document.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                debitDocRepository.save(document);

//                addLedgerAndLedgerDetailEntryForOrg(creditDocument,creditNoteAmount,document.getCustomer(),document);
            }


            List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(document.getId());
            Boolean isFullCreditNote = false;
            if(!CollectionUtils.isEmpty(creditDebitDocMappings))
            {
                List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList()));
                creditDocuments = creditDocuments.stream().filter(x -> x.getType().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)).collect(Collectors.toList());
                List<CreditDocument> finalCreditDocuments = creditDocuments;
                creditDebitDocMappings = creditDebitDocMappings.stream().filter(x -> finalCreditDocuments.stream().map(y -> y.getId()).collect(Collectors.toList()).contains(x.getCreditDocId())).collect(Collectors.toList());
                Double amount = creditDebitDocMappings.stream().filter(x -> x.getAdjustedAmount() != null).mapToDouble(x -> (x.getAdjustedAmount())).sum();
                if (amount != null && amount.doubleValue() == document.getTotalamount().doubleValue())
                    isFullCreditNote = true;
            }
            if(isFullCreditNote)
            {
                if(flag)
                {
                    List<CustPlanMappping> mappping = custPlanMappingRepository.findAllByDebitdocid(document.getId().longValue());
                    if(mappping!=null && !mappping.isEmpty())
                    {
                        mappping.stream().forEach(record -> {
                            if(record.getCustomerCpr()!=null)
                            {
                                CustPlanMappping custPlan = custPlanMappingRepository.findById(record.getCustomerCpr()).get();
                                custPlan.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                custPlan.setStartDate(LocalDateTime.now().minusMinutes(1));
                                custPlan.setEndDate(LocalDateTime.now());
                                custPlan.setExpiryDate(LocalDateTime.now());
                                custPlanMappingRepository.save(custPlan);
                            }
                        });
                    }
                }
                document.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                document.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocRepository.save(document);
            }
        }
    }

    public List<CreditDocument> FindPaymentToMap(Integer invoiceId) {
        List<CreditDocument> creditDocumentList = new ArrayList<>();
        if (invoiceId != null) {
            Integer customerId = debitDocRepository.findById(invoiceId).get().getCustomer().getId();
            //    QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
            //     BooleanExpression booleanExpression = qCreditDocument.isNotNull().and(qCreditDocument.customer.id.eq(customerId)).and(qCreditDocument.status.ne("rejected")).and(qCreditDocument.paytype.ne(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)).and(qCreditDocument.amount.subtract(qCreditDocument.adjustedAmount).gt(0));
            creditDocumentList = IterableUtils.toList(creditDocRepository.findAllByCustomerIdAndStatus(customerId));

        }

        return creditDocumentList;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocumentSearchPojo> searchPayment(SearchPaymentPojo searchPaymentPojo, PaginationRequestDTO requestDTO) {
        Page<CreditDocumentSearchPojo> creditDocumentsList = null;
        try {
            if (searchPaymentPojo != null) {
                creditDocumentsList = this.getCreditDocuments(searchPaymentPojo, requestDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return creditDocumentsList;
    }

    public Page<CreditDocumentSearchPojo> getCreditDocuments(SearchPaymentPojo search, PaginationRequestDTO paginationDTO) {
        pageRequest = generatePageRequest(paginationDTO.getPage(), paginationDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        List<CreditDocument> debitdocList = new ArrayList<>();
        if (search.getCustomerid() != null) {
            Customers customers = customersRepository.findById(search.getCustomerid()).get();
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getPageSize());
            if (search.getType() != null) {
                debitdocList = creditDocRepository.findAllByCustomerAndTypeIgnoreCase(customers.getId(), search.getType().toLowerCase());//, pageable);
            } else {
                debitdocList = creditDocRepository.findAllByCustomer(customers);//, pageable);
            }
        } else {
            throw new RuntimeException("Customer Id is mandatory!");
        }
        List<CreditDocumentSearchPojo> leadMasterPojoList = new ArrayList<CreditDocumentSearchPojo>();
        debitdocList = debitdocList.stream().sorted((o1, o2) -> o1.getCreatedate().compareTo(o2.getCreatedate())).collect(Collectors.toList());
        for (CreditDocument debitDocument : debitdocList) {
            Optional<DebitDocument> debitDoc = debitDocRepository.findById(debitDocument.getInvoiceId());
            if (debitDoc.isPresent())
                debitDocument.setInvoiceNumber(debitDoc.get().getDocnumber());
            CreditDocumentSearchPojo debitDocSearchPojo = new CreditDocumentSearchPojo(debitDocument);
            leadMasterPojoList.add(debitDocSearchPojo);
        }
        return new PageImpl<CreditDocumentSearchPojo>(leadMasterPojoList, PageRequest.of(0, pageRequest.getPageSize()),
                debitdocList.size());

    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocumentSearchPojo> getCreditDocuments1(SearchPaymentPojo search, PaginationRequestDTO paginationDTO) {
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        BooleanExpression exp = qCreditDocument.isDelete.eq(false)
                .and(qCreditDocument.customer.isDeleted.eq(false));

        if (!StringUtils.isEmpty(search.getType()) && !"null".equalsIgnoreCase(search.getType())) {
            BooleanExpression typeExpression = qCreditDocument.type.startsWithIgnoreCase(search.getType());
            if ("payment".equalsIgnoreCase(search.getType())) {
                typeExpression = typeExpression
                        .or(qCreditDocument.paytype.startsWithIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL))
                        .or(qCreditDocument.paytype.startsWithIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED));
            }
            exp = exp.and(typeExpression);
        }

        if (!StringUtils.isEmpty(search.getReferenceno()) && !"null".equalsIgnoreCase(search.getReferenceno())) {
            exp = exp.and(qCreditDocument.paymentreferenceno.contains(search.getReferenceno()));
        }
        if (!StringUtils.isEmpty(search.getCreditDocumentNumber())
                && !"null".equalsIgnoreCase(search.getCreditDocumentNumber())) {
            exp = exp.and(qCreditDocument.creditdocumentno.equalsIgnoreCase(search.getCreditDocumentNumber()));
        }
        if (!StringUtils.isEmpty(search.getPaymode()) && !"-1".equalsIgnoreCase(search.getPaymode())) {
            exp = exp.and(qCreditDocument.paymode.eq(search.getPaymode()));
        }
        if (!StringUtils.isEmpty(search.getPaystatus())
                && !"null".equalsIgnoreCase(search.getPaystatus())
                && !"-1".equalsIgnoreCase(search.getPaystatus())) {
            if ("Approved".equalsIgnoreCase(search.getPaystatus())) {
                exp = exp.and(qCreditDocument.status.equalsIgnoreCase(search.getPaystatus())
                        .or(qCreditDocument.status.equalsIgnoreCase("Fully Adjusted")));
            } else {
                exp = exp.and(qCreditDocument.status.equalsIgnoreCase(search.getPaystatus()));
            }
        }
        if (search.getCustomerid() != null) {
            exp = exp.and(qCreditDocument.customer.id.eq(search.getCustomerid()));
        }
        if (search.getStaffId() != null) {
            exp = exp.and(qCreditDocument.createdById.eq(search.getStaffId()));
        }
        if (search.getApproveId() != null) {
            exp = exp.and(qCreditDocument.approverid.eq(search.getApproveId()));
        }
        if(search.getIsKraSynced()!=null){
            if (search.getIsKraSynced() ) {
                exp = exp.and(qCreditDocument.isKraSynced.eq(true));
            }
            else {
                exp = exp.and(qCreditDocument.isKraSynced.eq(false));
                exp = exp.and(
                        qCreditDocument.status.in("Fully Adjusted", "Partialy Adjusted","approved"));
            }
        }

        if (!StringUtils.isEmpty(search.getBranchname())) {
            List<Long> branchIds = entityManager.createQuery(
                            "SELECT b.id FROM Branch b WHERE lower(b.name) = lower(:branchName) AND b.isDeleted = false",
                            Long.class)
                    .setParameter("branchName", search.getBranchname())
                    .getResultList();
            if (branchIds.isEmpty()) {
                exp = exp.and(qCreditDocument.id.eq(Integer.MIN_VALUE));
            } else {
                exp = exp.and(qCreditDocument.customer.branch.in(branchIds));
            }
        }
        if (!CollectionUtils.isEmpty(search.getBuID())) {
            exp = exp.and(qCreditDocument.customer.buId.in(search.getBuID()));
        }
        if (!StringUtils.isEmpty(search.getUserName())) {
            exp = exp.and(qCreditDocument.customer.username.equalsIgnoreCase(search.getUserName()));
        }

        if (search.getRecordfromdate() != null && search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.between(
                    search.getRecordfromdate().atStartOfDay(),
                    search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.before(
                    search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordfromdate() != null) {
            exp = exp.and(qCreditDocument.createdate.after(search.getRecordfromdate().atStartOfDay()));
        }
        if (Boolean.TRUE.equals(getLoggedInUser().getLco())) {
            exp = exp.and(qCreditDocument.lcoid.eq(getLoggedInUser().getPartnerId()));
        } else {
            exp = exp.and(qCreditDocument.lcoid.isNull());
        }
        if (search.getPayfromdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.after(search.getPayfromdate().minusDays(1)));
        }
        if (search.getPaytodate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.before(search.getPaytodate().plusDays(1)));
        }
        if (search.getPaymentdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.eq(search.getPaymentdate()));
        }
        if (search.getChequedate() != null) {
            exp = exp.and(qCreditDocument.chequedate.eq(search.getChequedate()));
        }
        if (search.getPartnerid() != null) {
            exp = exp.and(qCreditDocument.customer.partner.eq(search.getPartnerid()));
        }
        if (!StringUtils.isEmpty(search.getMobileNumber())) {
            exp = exp.and(qCreditDocument.customer.mobile.eq(search.getMobileNumber()));
        }
        if (!StringUtils.isEmpty(search.getAcctno())) {
            exp = exp.and(qCreditDocument.customer.acctno.eq(search.getAcctno()));
        }

        if (!StringUtils.isEmpty(search.getInvoiceNumber())) {
            String invoiceQuery = "SELECT d.id FROM DebitDocument d WHERE d.docnumber = :invoiceNumber";
            if (getLoggedInMvnoId() != 1) {
                invoiceQuery += " AND d.customer.mvnoId = :mvnoId";
            }
            javax.persistence.TypedQuery<Integer> debitDocumentQuery =
                    entityManager.createQuery(invoiceQuery, Integer.class)
                            .setParameter("invoiceNumber", search.getInvoiceNumber());
            if (getLoggedInMvnoId() != 1) {
                debitDocumentQuery.setParameter("mvnoId", getLoggedInMvnoId());
            }
            List<Integer> invoiceIds = debitDocumentQuery.getResultList();
            if (invoiceIds.isEmpty()) {
                exp = exp.and(qCreditDocument.id.eq(Integer.MIN_VALUE));
            } else {
                exp = exp.and(qCreditDocument.invoiceId.in(invoiceIds));
            }
        }
        if (!StringUtils.isEmpty(search.getChequeNo())) {
            exp = exp.and(qCreditDocument.paydetails2.eq(search.getChequeNo()));
        }
        if (!StringUtils.isEmpty(search.getReceiptNo())) {
            exp = exp.and(qCreditDocument.reciptNo.eq(search.getReceiptNo()));
        }
        else if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
            exp = exp.and(qCreditDocument.customer.partner.eq(getLoggedInUserPartnerId()));
        }

        if (search.getServiceAreaId() != null) {
            exp = exp.and(qCreditDocument.customer.serviceAreaId.eq(search.getServiceAreaId()));
        }
        if (getLoggedInMvnoId() != 1) {
            exp = exp.and(qCreditDocument.mvnoId.eq(getLoggedInMvnoId()));
        }
        if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff())) {
            exp = exp.and(qCreditDocument.buID.in(getBUIdsFromCurrentStaff()));
        }

        pageRequest = generatePageRequest(
                paginationDTO.getPage(),
                paginationDTO.getPageSize(),
                "createdate",
                CommonConstants.SORT_ORDER_DESC);
        Page<CreditDocument> creditDocumentPage = creditDocRepository.findAll(exp, pageRequest);
        List<Integer> invoiceIds = creditDocumentPage.getContent().stream()
                .map(CreditDocument::getInvoiceId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, String> debitDocumentNumbers = new HashMap<>();
        if (!invoiceIds.isEmpty()) {
            debitDocRepository.findAllById(invoiceIds).forEach(debitDocument ->
                    debitDocumentNumbers.put(debitDocument.getId(), debitDocument.getDocnumber()));
        }
        return creditDocumentPage.map(creditDocument -> {
            CreditDocumentSearchPojo searchPojo = new CreditDocumentSearchPojo(creditDocument);
            if (creditDocument.getInvoiceId() != null) {
                searchPojo.setInvoiceNumber(debitDocumentNumbers.get(creditDocument.getInvoiceId()));
            }
            return searchPojo;
        });
    }


    @Override
    protected JpaRepository getRepository() {
        return null;
    }

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

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());
        if (pageSize > MAX_PAGE_SIZE)
            pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }

    public void adjustPayment(CreditDocument creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings) {
        Double paymentAmount2 = creditDocument.getAmount();
        int i = 0;
        while (i < IterableUtils.toList(creditDebitDocMappings).size() && paymentAmount2 > 0 && creditDebitDocMappings.size() > 0) {
            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMappings.get(i).getDebtDocId()).orElse(null);
            Double amountToBePaid = 0d;
            Double paymentAmount = creditDebitDocMappings.get(i).getAmount();
            DecimalFormat df = new DecimalFormat("0.00000");

            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }

            amountToBePaid = Double.parseDouble(df.format(amountToBePaid));

            Double remainingAmountFromPayment = paymentAmount - amountToBePaid;

            remainingAmountFromPayment = Double.parseDouble(df.format(remainingAmountFromPayment));

//            if payment is fully adjusted
            if (remainingAmountFromPayment == 0.0d) {
                changeStatusDisableToActive(creditDocument, creditDebitDocMappings);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);

//                QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
//                BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
//                exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(debitDocument.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
//                List<TempPartnerLedgerDetail> details = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);
//
//                if (details != null && !details.isEmpty()) {
//                    tempPartnerLedgerDetailsRepository.deleteAll(details);
//                    partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details);
//                }

                creditDebitDocMappings.get(i).setAdjustedAmount(paymentAmount);
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                /**New Method for disable to active if payment adjusted**/


            }
//            if payment is fully adjusted but some amount still left in invoice to be adjusted
            else if (remainingAmountFromPayment < 0) {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(paymentAmount);
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                }
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDebitDocMappings.get(i).setAdjustedAmount(paymentAmount);
            }
//          if after adjustment some amount left in payment to be adjusted
            else {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

//                QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
//                BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
//                exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(debitDocument.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
//                List<TempPartnerLedgerDetail> details = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);
//
//                if (details != null && !details.isEmpty()) {
//                    tempPartnerLedgerDetailsRepository.deleteAll(details);
//                    partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details);
//                }

                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    creditDebitDocMappings.get(i).setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    creditDebitDocMappings.get(i).setAdjustedAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }

            }

            paymentAmount = remainingAmountFromPayment;
            debitDocument = debitDocRepository.save(debitDocument);
            creditDebtMappingRepository.save(creditDebitDocMappings.get(i));
            i++;
            if (paymentAmount < 0) {
                creditDocument.setAdjustedAmount(creditDocument.getAmount());
            } else {
                creditDocument.setAdjustedAmount(creditDocument.getAmount() - paymentAmount);
            }
        }

    }


    public void changeStatusDisableToActive(CreditDocument creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings) {
        List<DebitDocument> debitDocumentList = debitDocRepository.findAllByIdIn(creditDebitDocMappings.stream().filter(creditDebitDocMapping -> creditDebitDocMapping.getCreditDocId().equals(creditDocument.getId())).map(creditDebitDocMapping -> creditDebitDocMapping.getDebtDocId()).collect(Collectors.toList()));
        if (!debitDocumentList.isEmpty()) {
            List<Integer> cprids = custPlanMappingRepository.getAllByCustPlanMappingIdInDebitDocIds(debitDocumentList.stream().map(debitDocument -> debitDocument.getId()).map(integer -> integer.longValue()).collect(Collectors.toList()));
            if (!cprids.isEmpty()) {
                List<Integer> finalcprids = new ArrayList<>();
                for (Integer cprid : cprids) {
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(cprid).get();
                    if (custPlanMappping.getCustPlanStatus().equals(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE)) {
                        finalcprids.add(custPlanMappping.getId());
                    }
                }
                if (!finalcprids.isEmpty()) {
                    List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(finalcprids);
                    if (!CollectionUtils.isEmpty(serviceMappingIds)) {
                        String remark = "Payment Done";
                        custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE, remark, false, true);
                    }
                }
            }

            //TODO Payment inventory
//            List<Integer> inventoryServiceMappingIds = customerInventoryMappingService.getServiceInventoryMapping(debitDocumentList.get(0).getCustomer().getId());
//            if(!CollectionUtils.isEmpty(inventoryServiceMappingIds)){
//                String remark = "Payment Done";
//                custPlanMappingService.changeStatusOfCustServices(inventoryServiceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE, remark, false);
//
//            }
        }
    }

    public CreditDocument save(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked, Integer mvnoId, Integer partnerId, List<Long> buId, Boolean isLco, Integer getCreatedById, String getCreatedByName) throws Exception {
        CreditDocument savedCreditDocument = null;
//        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        BankManagement bankManagement = validateBankManagement(pojo.getBankManagement());
        List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
        String customerName = null;
        Integer custMvnoId = null;
        String mobileNumber = null;
        String emailId = null;
        String countryCode = null;
      /*  if (dbrService.getLoggedInUser() != null) {
            StaffUser loggedInUser = staffUserRepository.findById(dbrService.getLoggedInUser().getUserId()).orElse(null);
        }*/
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();

        if(pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE))
        {
//            List<CreditDocument> creditDocuments=creditDocRepository.findAllByInvoiceIdIn(pojo.getInvoiceId());
            List<String> creditDocuments = creditDocRepository.findStatusByInvoiceIdIn(pojo.getInvoiceId());
//            creditDocuments=creditDocuments.stream().filter(x->x.getStatus().equalsIgnoreCase("Pending")).collect(Collectors.toList());
            creditDocuments = creditDocuments.stream()
                    .filter(status -> "Pending".equalsIgnoreCase(status))
                    .collect(Collectors.toList());
            if(creditDocuments!=null && !creditDocuments.isEmpty())
            {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CreditNote Already Generated previously and waiting for Approval", null);
            }
        }
        if (!pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && !pojo.getPaytype().equalsIgnoreCase("Withdrawal")) {
            for (PaymentListPojo paymentPojo : pojo.getPaymentListPojos()) {
//                DebitDocument debitDocument = debitDocRepository.findById(paymentPojo.getInvoiceId()).orElse(null);
                DebitDocCustDTO debitDocData = debitDocRepository.findDebitDocById(paymentPojo.getInvoiceId());
                if (pojo.getType().equalsIgnoreCase("Payment") && debitDocData != null) {
                    String msg = checkPaymentValid(debitDocData);
                    if (!msg.equalsIgnoreCase("success")) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                    }
                }


                if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                    if (debitDocData != null) {
                        Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                        if (totalCreditNoteGenerated == 0) {
                            if (pojo.getAmount() > debitDocData.getTotalamount()) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                            }
                        } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocData.getTotalamount()) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                        List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                        if (!debitDocumentList.isEmpty()) {
                            ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                        }
                    }
                }
                CreditDocument creditDocument = new CreditDocument(pojo);
                if (dbrService.getLoggedInUser() != null) {
                    pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                } else {
                    pojo.setMvnoId(mvnoId);
                }
                //TODO:Bank
                if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                    if (bankManagement != null) {
                        if (!bankManagement.getStatus().equals("Active")) {
                            throw new RuntimeException("Status change at run time");
                        }
                    }

                }

                if (Objects.nonNull(pojo.getBankManagement()) && pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                    creditDocument.setBankManagement(bankManagement.getId());
                }
                if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                    creditDocument.setDestinationBank(pojo.getDestinationBank());
                }
                if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                    String text = commonListRepository.findTextByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                    creditDocument.setOnlinesource(text);
                }
                if (pojo.getReferenceno() != null) {
                    creditDocument.setReferenceno(pojo.getReferenceno());
                }

                RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, paymentPojo);
                CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
                if(doc.getPaymentreferenceno()==null){
                    doc.setPaymentreferenceno(doc.getReferenceno());
                }
                if (doc.getReferenceno() != null) {
                    String updatedReferenceNo = pojo.getReferenceno();
                    if (pojo.getPaymentListPojos() != null && pojo.getPaymentListPojos().size() > 1 && paymentPojo.getInvoiceId() != null) {
                        updatedReferenceNo = pojo.getReferenceno() + "-" + paymentPojo.getInvoiceId();
                    }
                    doc.setReferenceno(updatedReferenceNo);
                    creditDocument.setReferenceno(updatedReferenceNo);
                }
                if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                    doc.setInvoiceId(paymentPojo.getInvoiceId());
                }
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);

                if (doc != null) {
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getMvnoId() != null) {
                            doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                        }
                    } else {
                        if (mvnoId != null) {
                            doc.setMvnoId(mvnoId);
                        }
                    }
                    if (bankManagement != null) {
                        if (bankManagement.getId() != null) {
                            doc.setBankManagement(bankManagement.getId());
                        }
                    }
                    if (pojo.getDestinationBank() != null) {
                        if (pojo.getDestinationBank() != null) {
                            doc.setDestinationBank(pojo.getDestinationBank());
                        }
                    }


                    Integer lcoId;
                    if (getLoggedInUser() != null) {
                        if (getLoggedInUser().getLco() == true) {
                            doc.setLcoid(getLoggedInUser().getPartnerId());
                        }
                    } else if (isLco != null) {
                        if (isLco) {
                            if (partnerId != null) {
                                doc.setLcoid(partnerId);
                            }
                        } else {
                            doc.setLcoid(null);
                        }
                    } else doc.setLcoid(null);


                    if (Objects.isNull(doc.getReferenceno())) {
                        doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                    }
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                            doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                        }
                    } else {
                        if (buId != null) {
                            doc.setBuID(buId.get(0));
                        }
                    }
                    if (iswithdrawal) {
                        doc.setType("DR");
                        pojo.setType("DR");
                    }
                    if (isRevoked) {
                        doc.setIsDelete(false);
                        doc.setStatus("Fully Adjusted");
                        doc.setAdjustedAmount(paymentPojo.getAmountAgainstInvoice());
                    }

                    if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                    if (pojo.getOnlinesource() != null) {
                        List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                        if (commonList != null && !commonList.isEmpty()) {
                            RecordPaymentPojo finalPojo = pojo;
                            commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                            if (commonList != null && !commonList.isEmpty())
                                doc.setLedgerId(commonList.get(0).getValue());
                        }
                    }
                    if (dbrService.getLoggedInUser() != null) {
                        doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                        doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                    } else {
                        if (getCreatedById != null && getCreatedByName != null) {
                            doc.setCreatedById(getCreatedById);
                            doc.setCreatedByName(getCreatedByName);
                        }
                    }
                    doc = creditDocRepository.save(doc);
                    doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                    doc = creditDocRepository.save(doc);
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                    creditDebitDocMapping.setCreditDocId(doc.getId());
                    creditDebitDocMapping.setAmount(doc.getAmount());
                    creditDebitDocMapping.setIsDeleted(false);
                    creditDebitDocMapping.setAdjustedAmount(0.0);
                    creditDebitDocMapping.setAbbsAmount(doc.getAbbsAmount());
                    creditDebitDocMapping.setTdsAmount(doc.getTdsamount());
                    CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                    savedCreditDocument = doc;
                    List<CreditDebitDocMapping> mappings = new ArrayList<>();
                    mappings.add(creditDebitDocMappings);
                    CreditDocMessage creditDoc = new CreditDocMessage(doc, mappings);
                    creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                    creditDocMessage.add(creditDoc);
                }
            }
            creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//            messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));

        } else {
            DebitDocCustDTO debitDocument = null;
            if (pojo.getInvoiceId() != null) {
//                debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).orElse(null);
                debitDocument = debitDocRepository.findDebitDocById(pojo.getInvoiceId().get(0));
            }
            if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
                String msg = checkPaymentValid(debitDocument);
                if (!msg.equalsIgnoreCase("success")) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                }
            }


            if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                if (debitDocument != null) {
                    Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                    if (totalCreditNoteGenerated == 0) {
                        // if (pojo.getAmount() > debitDocument.getTotalamount()) {
                        if (pojo.getAmount() > Double.parseDouble(String.format("%.2f", debitDocument.getTotalamount()))) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                    } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                    }
                    //  List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                    List<DebitDocument> debitDocumentList = debitDocRepository.findActiveDebitDocs(pojo.getCustomerid(), LocalDateTime.now());

                    if (!debitDocumentList.isEmpty()) {
                        ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                    }
                }
            }
            CreditDocument creditDocument = new CreditDocument(pojo);
            if (dbrService.getLoggedInUser() != null) {
                pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
            } else {
                if (mvnoId != null) {
                    pojo.setMvnoId(mvnoId);
                }
            }
            //TODO:Bank
            if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                if (bankManagement != null) {
                    if (!bankManagement.getStatus().equals("Active")) {
                        throw new RuntimeException("Status change at run time");
                    }
                }

            }

            if (pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                creditDocument.setBankManagement(bankManagement.getId());
            }
            if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                creditDocument.setDestinationBank(pojo.getDestinationBank());
            }
            if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                creditDocument.setOnlinesource(commonList.getText());
            }
            if (pojo.getReferenceno() != null) {
                creditDocument.setReferenceno(pojo.getReferenceno());
            }

            RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, null);
            CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
            if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                doc.setInvoiceId(pojo.getInvoiceId().get(0));
            }
            if (getLoggedInUser() != null) {
                if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
            } else if (isLco != null) {
                if (isLco) {
                    if (partnerId != null) {
                        doc.setLcoid(partnerId);
                    }
                } else {
                    doc.setLcoid(null);
                }
            } else doc.setLcoid(null);

            if (doc != null) {
                if (getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getMvnoId() != null) {
                        doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                    }
                } else {
                    if (mvnoId != null) {
                        doc.setMvnoId(mvnoId);
                    }
                }
                if (bankManagement != null) {
                    if (bankManagement.getId() != null) {
                        doc.setBankManagement(bankManagement.getId());
                    }
                }
                if (pojo.getDestinationBank() != null) {
                    if (pojo.getDestinationBank() != null) {
                        doc.setDestinationBank(pojo.getDestinationBank());
                    }
                }


                Integer lcoId;
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco() == true) {
                        doc.setLcoid(getLoggedInUser().getPartnerId());
                    }
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);


                if (Objects.isNull(doc.getReferenceno())) {
                    doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                }
                if (dbrService.getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                        doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                    }
                } else {
                    if (buId != null) {
                        doc.setBuID(buId.get(0));
                    }
                }
                if (iswithdrawal) {
                    doc.setType("DR");
                    pojo.setType("DR");
                }
                if (isRevoked) {
                    doc.setIsDelete(false);
                    doc.setStatus("Fully Adjusted");
                    doc.setAdjustedAmount(pojo.getAmount());
                }

                if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                if (pojo.getOnlinesource() != null) {
                    List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                    if (commonList != null && !commonList.isEmpty()) {
                        RecordPaymentPojo finalPojo = pojo;
                        commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                        if (commonList != null && !commonList.isEmpty())
                            doc.setLedgerId(commonList.get(0).getValue());
                    }
                }
                if (dbrService.getLoggedInUser() != null) {
                    doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                    doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                } else {
                    if (getCreatedById != null && getCreatedByName != null) {
                        doc.setCreatedById(getCreatedById);
                        doc.setCreatedByName(getCreatedByName);
                    }
                }

                doc = creditDocRepository.save(doc);
                doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                doc = creditDocRepository.save(doc);
                CreditDocMessage creditDoc = new CreditDocMessage(doc);
                if (iswithdrawal && pojo.getPaytype().equalsIgnoreCase("Withdrawal")) {
                    List<CreditDebitDocMapping> credDebMapList = new ArrayList<>();
                    for (Integer withdrawCredDocId : pojo.getWithDrawCreditdocId()) {
                        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                        if (pojo.getInvoiceId() != null) {
                            creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                        }
                        creditDebitDocMapping.setCreditDocId(withdrawCredDocId);
                        creditDebitDocMapping.setWithdrawId(doc.getId());
                        creditDebitDocMapping.setAmount(doc.getAmount());
                        creditDebitDocMapping.setIsDeleted(false);
                        creditDebitDocMapping.setAdjustedAmount(0.0);
                        CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                        credDebMapList.add(creditDebitDocMappings);
                    }
                    creditDoc.setCreditDebitDocMappingList(credDebMapList);
                }
                else{
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    if (pojo.getInvoiceId() != null) {
                        creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                    }
                    creditDebitDocMapping.setCreditDocId(doc.getId());
                    creditDebitDocMapping.setAmount(doc.getAmount());
                    creditDebitDocMapping.setIsDeleted(false);
                    creditDebitDocMapping.setAdjustedAmount(0.0);
                    CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                    creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                }
                creditDocMessage.add(creditDoc);
                savedCreditDocument = doc;
                creditDocMessageList.setCreditDocMessageList(creditDocMessage);

//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            }
        }
        return savedCreditDocument;
    }

    /**
     * Save Method for wallet Transfer
     *
     * @param pojo
     * @param iswalletTransfer
     * @param isInvoiceVoid
     * @param isRevoked
     * @param mvnoId
     * @param partnerId
     * @param buId
     * @param isLco
     * @param getCreatedById
     * @param getCreatedByName
     * @return
     * @throws Exception
     */
    public CreditDocument saveTransferWalletWithdraw(RecordPaymentPojo pojo, boolean iswalletTransfer, boolean isInvoiceVoid, boolean isRevoked, Integer mvnoId, Integer partnerId, List<Long> buId, Boolean isLco, Integer getCreatedById, String getCreatedByName) throws Exception {
        CreditDocument savedCreditDocument = null;
//        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
        String customerName = null;
        Integer custMvnoId = null;
        String mobileNumber = null;
        String emailId = null;
        String countryCode = null;
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();

        DebitDocCustDTO debitDocument = null;
        if (pojo.getInvoiceId() != null) {
//                debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).orElse(null);
            debitDocument = debitDocRepository.findDebitDocById(pojo.getInvoiceId().get(0));
        }

        if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
            String msg = checkPaymentValid(debitDocument);
            if (!msg.equalsIgnoreCase("success")) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
            }
        }

        CreditDocument creditDocument = new CreditDocument(pojo);
        if (dbrService.getLoggedInUser() != null) {
            pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
        } else {
            if (mvnoId != null) {
                pojo.setMvnoId(mvnoId);
            }
        }

        if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
            CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
            creditDocument.setOnlinesource(commonList.getText());
        }
        if (pojo.getReferenceno() != null) {
            creditDocument.setReferenceno(pojo.getReferenceno());
        }

        RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, null);
        CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
        if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
            doc.setInvoiceId(pojo.getInvoiceId().get(0));
        }
        if (getLoggedInUser() != null) {
            if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
        } else if (isLco != null) {
            if (isLco) {
                if (partnerId != null) {
                    doc.setLcoid(partnerId);
                }
            } else {
                doc.setLcoid(null);
            }
        } else doc.setLcoid(null);

        if (doc != null) {
            if (getLoggedInUser() != null) {
                if (dbrService.getLoggedInUser().getMvnoId() != null) {
                    doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                }
            } else {
                if (mvnoId != null) {
                    doc.setMvnoId(mvnoId);
                }
            }

            Integer lcoId;
            if (getLoggedInUser() != null) {
                if (getLoggedInUser().getLco() == true) {
                    doc.setLcoid(getLoggedInUser().getPartnerId());
                }
            } else if (isLco != null) {
                if (isLco) {
                    if (partnerId != null) {
                        doc.setLcoid(partnerId);
                    }
                } else {
                    doc.setLcoid(null);
                }
            } else doc.setLcoid(null);


            if (Objects.isNull(doc.getReferenceno())) {
                doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            }
            if (dbrService.getLoggedInUser() != null) {
                if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                    doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                }
            } else {
                if (buId != null) {
                    doc.setBuID(buId.get(0));
                }
            }
            if (iswalletTransfer) {
                doc.setType("DR");
                pojo.setType("DR");
            }
            if (isRevoked) {
                doc.setIsDelete(false);
                doc.setStatus("Fully Adjusted");
                doc.setAdjustedAmount(pojo.getAmount());
            }

            if (pojo.getOnlinesource() != null) {
                List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                if (commonList != null && !commonList.isEmpty()) {
                    RecordPaymentPojo finalPojo = pojo;
                    commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                    if (commonList != null && !commonList.isEmpty())
                        doc.setLedgerId(commonList.get(0).getValue());
                }
            }
            if (dbrService.getLoggedInUser() != null) {
                doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
            } else {
                if (getCreatedById != null && getCreatedByName != null) {
                    doc.setCreatedById(getCreatedById);
                    doc.setCreatedByName(getCreatedByName);
                }
            }

            doc = creditDocRepository.save(doc);
            doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
            doc = creditDocRepository.save(doc);
            CreditDocMessage creditDoc = new CreditDocMessage(doc);
            if (iswalletTransfer && pojo.getPaytype().equalsIgnoreCase("transfer")) {
                List<CreditDebitDocMapping> credDebMapList = new ArrayList<>();
                for (Integer withdrawCredDocId : pojo.getWithDrawCreditdocId()) {
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    if (pojo.getInvoiceId() != null) {
                        creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                    }
                    creditDebitDocMapping.setCreditDocId(withdrawCredDocId);
                    creditDebitDocMapping.setWithdrawId(doc.getId());
                    creditDebitDocMapping.setAmount(doc.getAmount());
                    creditDebitDocMapping.setIsDeleted(false);
                    creditDebitDocMapping.setAdjustedAmount(0.0);
                    CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                    credDebMapList.add(creditDebitDocMappings);
                }
                creditDoc.setCreditDebitDocMappingList(credDebMapList);
                }
                else{
                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                if (pojo.getInvoiceId() != null) {
                    creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                }
                creditDebitDocMapping.setCreditDocId(doc.getId());
                creditDebitDocMapping.setAmount(doc.getAmount());
                creditDebitDocMapping.setIsDeleted(false);
                creditDebitDocMapping.setAdjustedAmount(0.0);
                CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
            }
            creditDocMessage.add(creditDoc);
            savedCreditDocument = doc;
            creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
        }
        return savedCreditDocument;
    }


    public CreditDocument saveAuto(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked, Integer mvnoId, Integer partnerId, List<Long> buId, Boolean isLco, Integer getCreatedById, String getCreatedByName, Boolean isApproved) throws Exception {
        CreditDocument savedCreditDocument = null;
        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        BankManagement bankManagement = validateBankManagement(pojo.getBankManagement());
        List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
        String customerName = null;
        Integer custMvnoId = null;
        String mobileNumber = null;
        String emailId = null;
        String countryCode = null;
//        if (dbrService.getLoggedInUser() != null) {
//            StaffUser loggedInUser = staffUserRepository.findById(dbrService.getLoggedInUser().getUserId()).orElse(null);
//        }
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();

        if(pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE))
        {
            List<CreditDocument> creditDocuments = creditDocRepository.findAllByInvoiceIdIn(pojo.getInvoiceId());
            creditDocuments = creditDocuments.stream().filter(x -> x.getStatus().equalsIgnoreCase("Pending")).collect(Collectors.toList());
            if(creditDocuments!=null && !creditDocuments.isEmpty())
            {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CreditNote Already Generated previously and waiting for Approval", null);
            }
        }
        if (!pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && !pojo.getPaytype().equalsIgnoreCase("Withdrawal")) {
            for (PaymentListPojo paymentPojo : pojo.getPaymentListPojos()) {
//                DebitDocument debitDocument = debitDocRepository.findById(paymentPojo.getInvoiceId()).orElse(null);
                DebitDocCustDTO debitDocument = debitDocRepository.findDebitDocById(paymentPojo.getInvoiceId());
                if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
                    String msg = checkPaymentValid(debitDocument);
                    if (!msg.equalsIgnoreCase("success")) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                    }
                }


                if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                    if (debitDocument != null) {
                        Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                        if (totalCreditNoteGenerated == 0) {
                            if (pojo.getAmount() > debitDocument.getTotalamount()) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                            }
                        } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                        List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                        if (!debitDocumentList.isEmpty()) {
                            ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                        }
                    }
                }
                CreditDocument creditDocument = new CreditDocument(pojo);
                if (dbrService.getLoggedInUser() != null) {
                    pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                } else {
                    pojo.setMvnoId(mvnoId);
                }
                //TODO:Bank
                if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                    if (bankManagement != null) {
                        if (!bankManagement.getStatus().equals("Active")) {
                            throw new RuntimeException("Status change at run time");
                        }
                    }

                }

                if (Objects.nonNull(pojo.getBankManagement()) && pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                    creditDocument.setBankManagement(bankManagement.getId());
                }
                if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                    creditDocument.setDestinationBank(pojo.getDestinationBank());
                }
                if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                    CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                    creditDocument.setOnlinesource(commonList.getText());
                }
                if (pojo.getReferenceno() != null) {
                    creditDocument.setReferenceno(pojo.getReferenceno());
                }

                RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, paymentPojo);
                CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
                if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                    doc.setInvoiceId(paymentPojo.getInvoiceId());
                }
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);

                if (doc != null) {
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getMvnoId() != null) {
                            doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                        }
                    } else {
                        if (mvnoId != null) {
                            doc.setMvnoId(mvnoId);
                        }
                    }
                    if (bankManagement != null) {
                        if (bankManagement.getId() != null) {
                            doc.setBankManagement(bankManagement.getId());
                        }
                    }
                    if (pojo.getDestinationBank() != null) {
                        if (pojo.getDestinationBank() != null) {
                            doc.setDestinationBank(pojo.getDestinationBank());
                        }
                    }


                    Integer lcoId;
                    if (getLoggedInUser() != null) {
                        if (getLoggedInUser().getLco() == true) {
                            doc.setLcoid(getLoggedInUser().getPartnerId());
                        }
                    } else if (isLco != null) {
                        if (isLco) {
                            if (partnerId != null) {
                                doc.setLcoid(partnerId);
                            }
                        } else {
                            doc.setLcoid(null);
                        }
                    } else doc.setLcoid(null);


                    if (Objects.isNull(doc.getReferenceno())) {
                        doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                    }
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                            doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                        }
                    } else {
                        if (buId != null) {
                            doc.setBuID(buId.get(0));
                        }
                    }
                    if (iswithdrawal) {
                        doc.setType("DR");
                        pojo.setType("DR");
                    }
                    if (isRevoked) {
                        doc.setIsDelete(false);
                        doc.setStatus("Fully Adjusted");
                        doc.setAdjustedAmount(paymentPojo.getAmountAgainstInvoice());
                    }
                    if (isApproved) {
                        doc.setStatus("approved");
                    }

                    if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                    if (pojo.getOnlinesource() != null) {
                        List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                        if (commonList != null && !commonList.isEmpty()) {
                            RecordPaymentPojo finalPojo = pojo;
                            commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                            if (commonList != null && !commonList.isEmpty())
                                doc.setLedgerId(commonList.get(0).getValue());
                        }
                    }
                    if (dbrService.getLoggedInUser() != null) {
                        doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                        doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                    } else {
                        if (getCreatedById != null && getCreatedByName != null) {
                            doc.setCreatedById(getCreatedById);
                            doc.setCreatedByName(getCreatedByName);
                        }
                    }
                    doc = creditDocRepository.save(doc);
                    doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                    doc = creditDocRepository.save(doc);
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                    creditDebitDocMapping.setCreditDocId(doc.getId());
                    creditDebitDocMapping.setAmount(doc.getAmount());
                    creditDebitDocMapping.setIsDeleted(false);
                    creditDebitDocMapping.setAdjustedAmount(0.0);
                    creditDebitDocMapping.setAbbsAmount(doc.getAbbsAmount());
                    creditDebitDocMapping.setTdsAmount(doc.getTdsamount());
                    CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                    savedCreditDocument = doc;
                    List<CreditDebitDocMapping> mappings = new ArrayList<>();
                    mappings.add(creditDebitDocMappings);
                    CreditDocMessage creditDoc = new CreditDocMessage(doc, mappings);
                    creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                    creditDocMessage.add(creditDoc);
                }
            }
//            creditDocMessageList.setCreditDocMessageList(creditDocMessage);
////            messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
//            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));

        } else {
            DebitDocCustDTO debitDocument = null;
            if (pojo.getInvoiceId() != null) {
                debitDocument = debitDocRepository.findDebitDocById(pojo.getInvoiceId().get(0));
            }
            if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
                String msg = checkPaymentValid(debitDocument);
                if (!msg.equalsIgnoreCase("success")) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                }
            }


            if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                if (debitDocument != null) {
                    Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                    if (totalCreditNoteGenerated == 0) {
                        // if (pojo.getAmount() > debitDocument.getTotalamount()) {
                        if (pojo.getAmount() > Double.parseDouble(String.format("%.2f", debitDocument.getTotalamount()))) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                    } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                    }
                    List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                    if (!debitDocumentList.isEmpty()) {
                        ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                    }
                }
            }
            CreditDocument creditDocument = new CreditDocument(pojo);
            if (dbrService.getLoggedInUser() != null) {
                pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
            } else {
                if (mvnoId != null) {
                    pojo.setMvnoId(mvnoId);
                }
            }
            //TODO:Bank
            if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                if (bankManagement != null) {
                    if (!bankManagement.getStatus().equals("Active")) {
                        throw new RuntimeException("Status change at run time");
                    }
                }

            }

            if (pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                creditDocument.setBankManagement(bankManagement.getId());
            }
            if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                creditDocument.setDestinationBank(pojo.getDestinationBank());
            }
            if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                creditDocument.setOnlinesource(commonList.getText());
            }
            if (pojo.getReferenceno() != null) {
                creditDocument.setReferenceno(pojo.getReferenceno());
            }

            RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, null);
            CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
            if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                doc.setInvoiceId(pojo.getInvoiceId().get(0));
            }
            if (getLoggedInUser() != null) {
                if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
            } else if (isLco != null) {
                if (isLco) {
                    if (partnerId != null) {
                        doc.setLcoid(partnerId);
                    }
                } else {
                    doc.setLcoid(null);
                }
            } else doc.setLcoid(null);

            if (doc != null) {
                if (getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getMvnoId() != null) {
                        doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                    }
                } else {
                    if (mvnoId != null) {
                        doc.setMvnoId(mvnoId);
                    }
                }
                if (bankManagement != null) {
                    if (bankManagement.getId() != null) {
                        doc.setBankManagement(bankManagement.getId());
                    }
                }
                if (pojo.getDestinationBank() != null) {
                    if (pojo.getDestinationBank() != null) {
                        doc.setDestinationBank(pojo.getDestinationBank());
                    }
                }


                Integer lcoId;
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco() == true) {
                        doc.setLcoid(getLoggedInUser().getPartnerId());
                    }
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);


                if (Objects.isNull(doc.getReferenceno())) {
                    doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                }
                if (dbrService.getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                        doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                    }
                } else {
                    if (buId != null) {
                        doc.setBuID(buId.get(0));
                    }
                }
                if (iswithdrawal) {
                    doc.setType("DR");
                    pojo.setType("DR");
                }
                if (isRevoked) {
                    doc.setIsDelete(false);
                    doc.setStatus("Fully Adjusted");
                    doc.setAdjustedAmount(pojo.getAmount());
                }
                if (isApproved) {
                    doc.setStatus("approved");
                }

                if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                if (pojo.getOnlinesource() != null) {
                    List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                    if (commonList != null && !commonList.isEmpty()) {
                        RecordPaymentPojo finalPojo = pojo;
                        commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                        if (commonList != null && !commonList.isEmpty())
                            doc.setLedgerId(commonList.get(0).getValue());
                    }
                }
                if (dbrService.getLoggedInUser() != null) {
                    doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                    doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                } else {
                    if (getCreatedById != null && getCreatedByName != null) {
                        doc.setCreatedById(getCreatedById);
                        doc.setCreatedByName(getCreatedByName);
                    }
                }

                doc = creditDocRepository.save(doc);
                doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                doc = creditDocRepository.save(doc);
                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                if (pojo.getInvoiceId() != null) {
                    creditDebitDocMapping.setDebtDocId(pojo.getInvoiceId().get(0));
                }
                creditDebitDocMapping.setCreditDocId(doc.getId());
                creditDebitDocMapping.setAmount(doc.getAmount());
                creditDebitDocMapping.setIsDeleted(false);
                creditDebitDocMapping.setAdjustedAmount(0.0);
                CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                CreditDocMessage creditDoc = new CreditDocMessage(doc);
                creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                creditDocMessage.add(creditDoc);
                savedCreditDocument = doc;
                creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
//                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            }
        }
        return savedCreditDocument;
    }


    public String checkPaymentValid(DebitDocCustDTO debitDocument) {
        Integer custpackrelid = debitDocument.getCustpackrelid();

//            Customers customers=debitDocument.getCustomer();
        QMvno qMvno = QMvno.mvno;
        BooleanExpression booleanExpression = qMvno.isNotNull().and(qMvno.username.equalsIgnoreCase(debitDocument.getCustomerUsername()));
        List<Mvno> mvno = IterableUtils.toList(mvnoRepository.findAll(booleanExpression));
        if (mvno.size() > 0) {
            return "Success";
        }

        if (custpackrelid != null && custpackrelid != 0) {
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(custpackrelid).get();
            if (custPlanMappping != null) {
                if (!custPlanMappping.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER) && custPlanMappping.getIsInvoiceToOrg()) {
                    List<Integer> debitdocIds = custPlanMappingRepository.findAllByCustRefId(Collections.singleton(custPlanMappping.getId()));
                    if (!CollectionUtils.isEmpty(debitdocIds)) {
                        if (debitDocRepository.existsByIdInAndStatus(debitdocIds, "pending")) {
                            return "As Organization invoice is pending, not able to do payment!";
                        }
                    }
                }
            }
        }

        return "Success";
    }


    public void ifCreditNoteIsAllowed(RecordPaymentPojo pojo) {
        DebitDocument debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).get();
        if (Objects.nonNull(debitDocument)) {
            if (debitDocument.getStartdate().isBefore(LocalDateTime.now()) || debitDocument.getStartdate().isEqual(LocalDateTime.now())) {
                if (Objects.isNull(debitDocument.getAdjustedAmount())) {
                    debitDocument.setAdjustedAmount(0.0);
                }
                List<Integer> cprIds = custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId().longValue()).stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
                Double cnAmount = 0.0;
                if (!cprIds.isEmpty()) {
                    List<Integer> custServiceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(cprIds);
                    List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();
                    customerChargeDBRList = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(LocalDateTime.now().toLocalDate(), debitDocument, cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
                    if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        cnAmount = customerChargeDBRList.stream().mapToDouble(CustomerChargeDBR::getDbr).sum();
                    }

                }
                if(debitDocument.getCustomer().getId()!=1 && debitDocument.getCustomer().getId()!=2)
                {
                    if (cnAmount - pojo.getAmount() <= 0.1) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please cancel Future Plan Invoice First.", null);
                    }
                }
            }
        }
    }


    public RecordPayment convertRecordPaymentPojoToRecordPaymentModel(RecordPaymentPojo recordPaymentPojo, PaymentListPojo paymentPojo) {
        RecordPayment recordPayment = null;
        if (recordPaymentPojo != null) {
            recordPayment = new RecordPayment();
            recordPayment.setChequedate(recordPaymentPojo.getChequedate());
            if (Objects.nonNull(recordPaymentPojo.getChequedatestr())) {
                recordPayment.setChequedate(LocalDate.parse(recordPaymentPojo.getChequedatestr()));
            }
            recordPayment.setPaymentdate(recordPaymentPojo.getPaymentdate());
            recordPayment.setChequeno(recordPaymentPojo.getChequeno());
            recordPayment.setBank(recordPaymentPojo.getBank());
            if (paymentPojo == null) {
                if (Objects.isNull(recordPaymentPojo.getAmount())) {
                    recordPayment.setAmount(0D);
                } else {
                    recordPayment.setAmount(recordPaymentPojo.getAmount());
                }
            } else {
                recordPayment.setAmount(paymentPojo.getAmountAgainstInvoice());
            }
            recordPayment.setPaymentreferenceno(recordPaymentPojo.getPaymentreferenceno());
            recordPayment.setRemark(recordPaymentPojo.getRemark());
            recordPayment.setReciptNo(recordPaymentPojo.getReciptNo());
            if (recordPaymentPojo.getReferenceno() != null)
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            if (recordPaymentPojo.getCustomerid() != null) {
                recordPayment.setCustomer(customersRepository.findById(recordPaymentPojo.getCustomerid()).get());//customerService.get(recordPaymentPojo.getCustomerid()));
                recordPayment.setCustomerid(String.valueOf(recordPaymentPojo.getCustomerid()));
            }
            if (paymentPojo == null && recordPaymentPojo.getInvoiceId() != null) {
                recordPayment.setInvoiceId(recordPaymentPojo.getInvoiceId().get(0));
            } else {
                if (paymentPojo != null && paymentPojo.getInvoiceId() != null) {
                    recordPayment.setInvoiceId(paymentPojo.getInvoiceId());
                }
            }
            if (recordPaymentPojo.getPaytype() != null) {
                recordPayment.setPaytype(recordPaymentPojo.getPaytype());
            }
            if (recordPaymentPojo.getType() != null) {
                recordPayment.setType(recordPaymentPojo.getType());
            }
            if (recordPaymentPojo.getFilename() != null) {
                recordPayment.setFilename(recordPaymentPojo.getFilename());
            }
            if (recordPaymentPojo.getUniquename() != null) {
                recordPayment.setUniquename(recordPaymentPojo.getUniquename());
            }
            if (recordPaymentPojo.getBarteramount() != null) {
                recordPayment.setBarteramount(recordPaymentPojo.getBarteramount());
            }
            recordPayment.setMvnoId(recordPaymentPojo.getMvnoId());
            if (paymentPojo != null) {
                recordPayment.setTdsAmount(paymentPojo.getTdsAmountAgainstInvoice());
                recordPayment.setAbbsAmount(paymentPojo.getAbbsAmountAgainstInvoice());
            }
            if (recordPaymentPojo.getOnlinesource() != null && !recordPaymentPojo.equals("")) {
                recordPayment.setOnlinesource(recordPaymentPojo.getOnlinesource());
            }
            if (recordPaymentPojo.getReferenceno() != null) {
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            }
            if (recordPaymentPojo.getWithDrawCreditdocId() != null && recordPaymentPojo.getWithDrawCreditdocId().size() > 0) {
                recordPayment.setWithDrawCreditdocId(recordPaymentPojo.getWithDrawCreditdocId().get(0));
            }
            if(recordPaymentPojo.getPaytype()!=null)
            {
                recordPayment.setPaymode(recordPaymentPojo.getPaymode());
            }
            recordPayment.setBranch(recordPaymentPojo.getBranch());
            recordPayment.setCustomerid(Integer.toString(recordPaymentPojo.getCustomerid()));
            recordPayment.setPaymode(recordPaymentPojo.getPaymode());
        }
        return recordPayment;
    }


    public CreditDocument covertPaymentReqToCreditDoc(RecordPayment payment) throws Exception {

        CreditDocument doc = null;
        if (payment != null && payment.getCustomerid() != null) {
            doc = new CreditDocument();
            if (getMvnoIdFromCurrentStaff() != null) {
//                doc.setCustomer(subscriberService.get(Integer.valueOf(payment.getCustomerid())));
                doc.setCustomer(payment.getCustomer());
            }
            else {
//                doc.setCustomer(subscriberService.getCustomers(Integer.valueOf(payment.getCustomerid())));
                doc.setCustomer(payment.getCustomer());
            }
            doc.setChequedate(payment.getChequedate());
            doc.setPaymentdate(payment.getPaymentdate());
            doc.setPaymode(payment.getPaymode());
            doc.setStatus(CommonUtils.PAYMENT_STATUS_PENDING);
            doc.setIsDelete(false);
            doc.setRemarks(payment.getRemark());
            doc.setAdjustedAmount(Double.valueOf(CommonUtils.INITIAL_PAYMENT_ADJUST));
            doc.setReciptNo(payment.getReciptNo());
            if (payment.getMvnoId() != null) {
                doc.setMvnoId(payment.getMvnoId());
            }
            if (CommonUtils.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(doc.getPaymode())) {
                doc.setPaydetails1(payment.getBank());
                doc.setPaydetails2(payment.getChequeno());
                doc.setPaydetails3(payment.getChequedate() != null ? payment.getChequedate().toString() : null);
                doc.setBranchname(payment.getBranch());
            } else if (CommonUtils.PAYMENT_MODE_DIRECTDEPOSIT.equalsIgnoreCase(doc.getPaymode().replaceAll("\\s", ""))) {
                doc.setBranchname(payment.getBranch());
            } else {
                doc.setPaydetails4(payment.getReferenceno());
            }
//            if (payment.getInvoiceId() != null) {
//                doc.setInvoiceId(payment.getInvoiceId());
//            }
            if (payment.getPaytype() != null) {
                doc.setPaytype(payment.getPaytype());
            }
            if (payment.getType() != null) {
                doc.setType(payment.getType());
            }

        }
        if (payment.getFilename() != null) {
            doc.setFilename(payment.getFilename());
        }
        if (payment.getUniquename() != null) {
            doc.setUniquename(payment.getUniquename());
        }
        if (payment.getBarteramount() != null) {
            doc.setBarteramount(payment.getBarteramount());
        }
        if (getLoggedInUser() != null) {
            if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
            else doc.setLcoid(null);
        }

        //TODO : commonlist
        if (payment.getOnlinesource() != null && !payment.getOnlinesource().equals("")) {
            String commonList = commonListRepository.findTextByValueAndType(payment.getOnlinesource(), payment.getPaymode());
            doc.setOnlinesource(commonList);
        }

        CreditDocument result = applyTdsAndAbbs(payment);
        doc.setTdsamount(result.getTdsamount());
        doc.setAbbsAmount(result.getAbbsAmount());
        doc.setAmount(result.getAmount());

        /*if (payment.getType().equalsIgnoreCase("creditnote")) {
            doc.setCreditdocumentno(getInvoiceNo());
        }*/

        if (payment.getType().equalsIgnoreCase("Payment")) {
            if(payment.getInvoiceId()!=null && payment.getInvoiceId()!=0 && payment.getCustomerid() != null)
            {
                Customers customers = subscriberService.getCustomers(Integer.valueOf(payment.getCustomerid()));
                if (customers.getStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                    Optional<TrialDebitDocument> trialDebitDocument = trialDebitDocRepository.findById(payment.getInvoiceId());
                    if (trialDebitDocument.isPresent()) {
                        doc.setInvoiceId(payment.getInvoiceId());
                        doc.setInvoiceNumber(trialDebitDocument.get().getDocnumber());
                    }
                }
                else{
//                    Optional<DebitDocument> debitDocument = debitDocRepository.findById(payment.getInvoiceId());
                    String docnumber = debitDocRepository.findDocnumberById(payment.getInvoiceId());
                    if (docnumber != null) {
                        doc.setInvoiceId(payment.getInvoiceId());
                        doc.setInvoiceNumber(docnumber);
                    }
                }
            }
        }
        if (payment.getReferenceno() != null) {
            doc.setReferenceno(payment.getReferenceno());
        }
        if (payment.getPaymentreferenceno() != null) {
            doc.setPaymentreferenceno(payment.getPaymentreferenceno());
        }
        if (payment.getWithDrawCreditdocId() != null) {
            doc.setWithDrawCreditdocId(payment.getWithDrawCreditdocId());
        }

        return doc;
    }

    public CreditDocument applyTdsAndAbbs(RecordPayment payment) {

        double remainingTdsAmount = 0;
        double remainingAbbsAmount = 0;

        double previousTdsApplied = 0;
        double previousAbbsApplied = 0;

        // double tdsPercent= Double.parseDouble(clientServiceRepository.findValueByNameAndMvnoId("TDS",payment.getMvnoId()));
        //double abbsPercent = Double.parseDouble(clientServiceRepository.findValueByNameAndMvnoId("ABBS",payment.getMvnoId()));
        String tdsValue = clientServiceRepository.findValueByNameAndMvnoId("TDS", payment.getMvnoId());
        double tdsPercent = 0.0;
        if (tdsValue != null && !tdsValue.trim().isEmpty()) {
            try {
                tdsPercent = Double.parseDouble(tdsValue.trim());
            } catch (NumberFormatException e) {
                // Log and fall back to 0 or handle appropriately
                System.err.println("Invalid TDS value: " + tdsValue);
            }
        }

        String abbsValue = clientServiceRepository.findValueByNameAndMvnoId("ABBS", payment.getMvnoId());
        double abbsPercent = 0.0;
        if (abbsValue != null && !abbsValue.trim().isEmpty()) {
            try {
                abbsPercent = Double.parseDouble(abbsValue.trim());
            } catch (NumberFormatException e) {
                // Log and fall back to 0 or handle appropriately
                System.err.println("Invalid ABBS value: " + abbsValue);
            }
        }


        Double totalAmount = null;
        if (Objects.nonNull(payment.getWithDrawCreditdocId())) {
            totalAmount = debitDocRepository.findTotalAmountById(payment.getWithDrawCreditdocId());

        } else {
            totalAmount = debitDocRepository.findTotalAmountById(payment.getInvoiceId());
        }
//        List<CreditDocument> creditDocument=creditDocRepository.findByCustomerId(payment.getCustomer().getId());
        List<Object[]> result = creditDocRepository.findTdsAndAbbsAmountsByCustomerId(payment.getCustomer().getId());


        double totalTds = 0;
        double totalAbbs = 0;
        // Get the total amounts of TDS and ABBS
        if (totalAmount != null) {
            totalTds = (totalAmount * tdsPercent) / 100;
            totalAbbs = (totalAmount * abbsPercent) / 100;
        }


//        for (CreditDocument creditDocuments : creditDocument) {
//            previousTdsApplied += creditDocuments.getTdsamount() != null ? creditDocuments.getTdsamount() : 0.0;
//            previousAbbsApplied += creditDocuments.getAbbsAmount() != null ? creditDocuments.getAbbsAmount() : 0.0;
//        }
        for (Object[] row : result) {
            Double tds = row[0] != null ? (Double) row[0] : 0.0;
            Double abbs = row[1] != null ? (Double) row[1] : 0.0;
            previousTdsApplied += tds;
            previousAbbsApplied += abbs;
        }

        double remainingPaymentAmount = payment.getAmount();

        if (payment.getTdsAmount() != null && payment.getTdsAmount() > 0) {
            if (previousTdsApplied < totalTds) {
                remainingTdsAmount = totalTds - previousTdsApplied;
                if (remainingTdsAmount > remainingPaymentAmount) {
                    remainingTdsAmount = remainingPaymentAmount;
                }
                remainingPaymentAmount -= remainingTdsAmount;
            }
        }
        if (payment.getAbbsAmount() != null && payment.getAbbsAmount() > 0) {
            if (remainingPaymentAmount > 0 && previousAbbsApplied < totalAbbs) {
                remainingAbbsAmount = totalAbbs - previousAbbsApplied;
                if (remainingAbbsAmount > remainingPaymentAmount) {
                    remainingAbbsAmount = remainingPaymentAmount;
                }
                remainingPaymentAmount -= remainingAbbsAmount;
            }
        }

        CreditDocument doc = new CreditDocument();
        doc.setAbbsAmount(remainingAbbsAmount);
        doc.setTdsamount(remainingTdsAmount);

        double finalAmount = payment.getAmount() - (remainingTdsAmount + remainingAbbsAmount);
        doc.setAmount(finalAmount);

        return doc;
    }

    public String getPaymentInvoiceNo() {
        String currinvoiceNo = null;
        String newInvoiceNo = null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();
            try {
                currinvoiceNo = creditDocRepository.getPaymentFuction();
            }
            catch (Exception e){
                logger.error("Payment Function not found ");
            }

            if (currinvoiceNo == null) {
                currinvoiceNo = String.valueOf(System.currentTimeMillis() % 100000000L);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("PY");
            sb.append(current_Year);
            sb.append("-");
            if (currinvoiceNo != null) {
                while (sb.length() < 14 - currinvoiceNo.length()) {
                    sb.append('0');
                }
                sb.append(currinvoiceNo);
                newInvoiceNo = sb.toString();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }

    private BankManagement validateBankManagement(String name) {
        try {
            if (name != null && !name.isEmpty()) {
                return bankManagementService.validateBankByName(name);
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<PaymentHistoryDTO> getByCustId(Integer custId) {
        List<String> excludedPayTypes = Arrays.asList("creditnote");
        List<CreditDocument> creditDocuments = creditDocRepository.findAllByCustomerIdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseAndStatusNotIgnoreCaseOrderByIdDescLightCreditdoc(custId, excludedPayTypes, "creditnote", "Payment Failed").stream().filter(creditDic -> creditDic.getMvnoId() == getLoggedInUser().getMvnoId().intValue() || creditDic.getMvnoId() == 1 || getLoggedInUser().getMvnoId() == 1).collect(Collectors.toList());
        for (CreditDocument creditdocument : creditDocuments) {
            List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
//            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
//            BooleanExpression booleanExpression1 = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(creditdocument.getId()));
            creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findCreditDebitDocMappingsForDebitDocument(creditdocument.getId());
            //for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
            if (creditdocument.getInvoiceId() != null) {
                DebitDocument debitDocument = debitDocRepository.findById(creditdocument.getInvoiceId()).orElse(null);
                if (Objects.nonNull(debitDocument)) {
                    creditdocument.setInvoiceId(debitDocument.getId());
                    creditdocument.setInvoiceNumber(debitDocument.getDocnumber());
                }
                // }
            }
        }
        List<PaymentHistoryDTO> paymentHistories = creditDocuments.stream().map(data -> creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        for (PaymentHistoryDTO paymentHistory : paymentHistories) {
            if (paymentHistory.getAmount() != null && paymentHistory.getAdjustedAmount() != null) {
                paymentHistory.setUnsettledAmount(paymentHistory.getAmount() - paymentHistory.getAdjustedAmount());
            }
            if (paymentHistory.getBankManagement() != null) {
                BankManagement bank = bankManagementRepository.findById(paymentHistory.getBankManagement()).orElse(null);
                if (bank != null) {
                    paymentHistory.setBankName(bank.getBankname());
                }
            }
        }
        return paymentHistories;
    }

    public List<PaymentHistoryDTO> getByCustIdForFailedPayments(Integer custId) {
        List<CreditDocument> creditDocuments = creditDocRepository.getAllByCustomer_IdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseAndStatusOrderByIdDesc(custId, "CREDITNOTE", "creditnote", "Payment Failed").stream().filter(creditDic -> creditDic.getMvnoId() == getLoggedInUser().getMvnoId().intValue() || creditDic.getMvnoId() == 1 || getLoggedInUser().getMvnoId() == 1).collect(Collectors.toList());
        List<PaymentHistoryDTO> paymentHistories = creditDocuments.stream().map(data -> creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return paymentHistories;
    }

    public String adjustManualPaymentToInvoice(CreditDebitMappingPojo creditDebitDocMappingPojo) throws Exception {
        Integer invoiceId = creditDebitDocMappingPojo.getInvoiceId();
        List<CreditDebitDataPojo> creditDocumentList = creditDebitDocMappingPojo.getCreditDocumentList();
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId).orElse(null);
        if (debitDocument != null) {
            Double amountToBePaid = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            int i = 0;
            boolean adjusted = false;
            while (!adjusted && i < creditDocumentList.size()) {

                CreditDebitDocMapping creditDebitDocMappings = new CreditDebitDocMapping();
                CreditDocument creditDocument = creditDocRepository.findById(creditDocumentList.get(i).getId()).orElse(null);
                Double paymentAmount = 0d;
                if (creditDocument.getAdjustedAmount() != null) {
                    paymentAmount = creditDocument.getAmount() - creditDocument.getAdjustedAmount();
                } else {
                    paymentAmount = creditDocument.getAmount();
                }
                Double remainingAmountFromPayment = paymentAmount - amountToBePaid;
                if (Math.abs(remainingAmountFromPayment) < 0.1) {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    if (Objects.isNull(creditDocument.getAdjustedAmount())) {
                        creditDocument.setAdjustedAmount(0.0000);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);
                    List<TempPartnerLedgerDetail> details1 = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());
                    if(!details1.isEmpty())
                    {
                        partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details1);
                        tempPartnerLedgerDetailsRepository.deleteAll(details1);
                    }


                } else if (remainingAmountFromPayment < 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(paymentAmount);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + paymentAmount);
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(paymentAmount);
                } else {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(amountToBePaid);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + amountToBePaid);
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);

                }
                i++;
                if (creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED)
                        && creditDocument.getInvoiceId() != null) {
                    Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(creditDocument.getInvoiceId());
                    if (oldDebitDoc.isPresent() && creditDocument.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    } else {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                    }
                    debitDocRepository.save(oldDebitDoc.get());
                }
                creditDocument = creditDocRepository.save(creditDocument);
                creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMappings);
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();

                CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                ledgerDtls.setCustomer(creditDocument.getCustomer());
                ledgerDtls.setDebitdocid(debitDocument.getId());
                ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                ledgerDtls.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls.setAmount(paymentAmount);
                ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsRepository.save(ledgerDtls);

                CustomerLedgerDtls ledgerDtls1 = new CustomerLedgerDtls();
                ledgerDtls1.setCustomer(creditDocument.getCustomer());
                ledgerDtls1.setDebitdocid(debitDocument.getId());
                ledgerDtls1.setCreditdocid(creditDocument.getId());
                ledgerDtls1.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                ledgerDtls1.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls1.setAmount(paymentAmount);
                ledgerDtls1.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsRepository.save(ledgerDtls1);
                List<CreditDocMessage> creditDocMessages = new ArrayList<>();
                CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
                creditDocMessages.add(creditDoc);
                CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
                creditDocMessageList.setCreditDocMessageList(creditDocMessages);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            }
            debitDocument = debitDocRepository.save(debitDocument);
            Customers customers = debitDocument.getCustomer();
            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(customers.getId(), customers.getUsername(), customers.getCustomerType(), debitDocument.getTotalamount(), debitDocument.getId().longValue(), customers.getUsername(), true, debitDocument.getTotalamount(), 2, null, null, "null", "false", null, 0L, debitDocument, customers.getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), debitDocument.getCreatedByName(), null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), null, null, null, null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));

            return "success";
        } else {
            return "Not found invoice with given id";
        }
    }

    public List<ViewAdjustedInvoicePojo> FindInvoiceToPayment(Integer paymentId) {
        List<ViewAdjustedInvoicePojo> ViewAdjustedInvoicePojos = new ArrayList<>();
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        if (paymentId != null) {
            creditDebitDocMappings = creditDebtMappingRepository.findAllByCreditDocIdAndAdjustedAmountNotNull(paymentId);
            creditDebitDocMappings.removeIf(creditDebitDocMapping -> creditDebitDocMapping.getAdjustedAmount() <= 0);
            for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
                if (creditDebitDocMapping.getDebtDocId() != null) {
                    DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                    CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getCreditDocId()).orElse(null);
                    if (Objects.nonNull(debitDocument)) {
                        ViewAdjustedInvoicePojo ViewAdjustedInvoicePojo = new ViewAdjustedInvoicePojo();
                        ViewAdjustedInvoicePojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
                        ViewAdjustedInvoicePojo.setTotalamount(debitDocument.getTotalamount());
                        ViewAdjustedInvoicePojo.setBilldate(debitDocument.getBilldate());
                        ViewAdjustedInvoicePojo.setInvoiceNumber(debitDocument.getDocnumber());
                        List<CreditDocChargeRel> creditDocChargeRels = creditDocChargeRelRepository.findAllByCreditdocid(creditDocument.getId());
                        if (!CollectionUtils.isEmpty(creditDocChargeRels)) {
                            List<CreditDocChargeRelDTO> creditDocChargeRelDTOS = creditDocChargeRels.stream()
                                    .map(creditDocChargeRel -> creditDocChargeRelMapper.domainToDTO(creditDocChargeRel, new CycleAvoidingMappingContext())).collect(Collectors.toList());//.collect(Collectors.toList());
                            ViewAdjustedInvoicePojo.setCreditDocChargeRelDTOList(creditDocChargeRelDTOS);
                        }
                        if (creditDebitDocMapping.getCreditDocId() != null)
                            ViewAdjustedInvoicePojo.setDocnumber(creditDocument.getCreditdocumentno());
                        ViewAdjustedInvoicePojos.add(ViewAdjustedInvoicePojo);
                    }
                } else if (creditDebitDocMapping.getWithdrawId() != null) {
                    CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getWithdrawId()).orElse(null);
                    if (creditDocument != null) {
                        ViewAdjustedInvoicePojo ViewAdjustedInvoicePojo = new ViewAdjustedInvoicePojo();
                        ViewAdjustedInvoicePojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
                        ViewAdjustedInvoicePojo.setTotalamount(creditDocument.getAmount());
                        ViewAdjustedInvoicePojo.setBilldate(creditDocument.getCreatedate());
                        ViewAdjustedInvoicePojo.setDocnumber(creditDocument.getCreditdocumentno());
                        ViewAdjustedInvoicePojos.add(ViewAdjustedInvoicePojo);
                    }
                }
            }
        }
        // ViewAdjustedInvoicePojos = ViewAdjustedInvoicePojos.stream().filter(ViewAdjustedInvoicePojo -> ViewAdjustedInvoicePojo.getAdjustedAmount() > 0).collect(Collectors.toList());

        return ViewAdjustedInvoicePojos;
    }

    public List<CreditDocument> getWithdrawPayments(Integer customerId, PaginationRequestDTO paginationRequestDTO) {
//        super.generatePageRequest(paginationRequestDTO.getPage(),paginationRequestDTO.getPageSize(),"createdate",1);
        Pageable pageable = super.generatePageRequest(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(),
                "createdate",
                1
        );

        // Retrieve the customer's cstatus from CustomerRepository
        String cstatus = customersRepository.findStatusById(customerId);
        if (cstatus == null) {
            throw new RuntimeException("Customer not found or no status available");
        }

        List<CreditDocument> resultList;
        if ("NewActivation".equalsIgnoreCase(cstatus)) {
            resultList = creditDocRepository.getAllPaymentsForCustomer(customerId, pageable);
//            resultList = resultList.stream().map(cd -> {
//                Double getWithDrawCreditdocAmountSum = creditDocRepository.findTotalAmountByWithdrawCreditDocId(Collections.singletonList(cd.getId()));
//                cd.setRemainingAmount(cd.getAmount() - getWithDrawCreditdocAmountSum);
//                return cd;
//            }).collect(Collectors.toList());

        } else {
            resultList = creditDocRepository.getWithdrawPayments(customerId, pageable);
        }

        return resultList.stream().map(x -> {
            BigDecimal remainingAmount = BigDecimal.valueOf(x.getRemainingAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            x.setRemainingAmount(remainingAmount.doubleValue());
            return x;
        }).collect(Collectors.toList());
    }


    @Transactional
    public RecordPaymentPojo withDrawal(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked) throws Exception {
        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        if (customers != null) {
            double withDrawalAmount = pojo.getAmount();
            if (withDrawalAmount <= 0) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw -ve or 0 amount.", null);
            }
            if (pojo.getWithDrawCreditdocId().size() == 0) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw as payment not selected.", null);
            } else {
                double totalRemainAmount;
                double totalPendingAmount;
                if ("NewActivation".equalsIgnoreCase(customersRepository.findStatusById(pojo.getCustomerid()))) {
                    totalRemainAmount = creditDocRepository.totalWithDrawAmountCaf(pojo.getCustomerid());
                    totalPendingAmount = creditDocRepository.totalPendingAmountCaf(pojo.getCustomerid());
                } else {
                    totalRemainAmount = creditDocRepository.totalWithDrawAmount(pojo.getCustomerid());
                    totalPendingAmount = creditDocRepository.totalPendingAmount(pojo.getCustomerid());
                }

                double remeaningAmount = totalRemainAmount - totalPendingAmount;

                if (pojo.getAmount() - remeaningAmount > 0.1) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw more than remaining amount.", null);
                } else {
//                    List<CreditDocument> creditDocumentList = IterableUtils.toList(creditDocRepository.findAllByInvoiceIdIn(pojo.getInvoiceId()));
                    pojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL);
                    save(pojo, true, false, false, null, null, null, null, null, null);

                }
            }
        }
        return pojo;
    }

    @Transactional
    public RecordPaymentPojo walletTransfer(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked) throws Exception {
        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        Customers toCustomers = customersRepository.findById(pojo.getToCustomerId()).orElse(null);
        if (customers != null && toCustomers != null) {
            double transferAmount = pojo.getAmount();
            if (transferAmount <= 0) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not transfer -ve or 0 amount.", null);
            }
            if (pojo.getWithDrawCreditdocId().size() == 0) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw as payment not selected.", null);
            } else {


                double totalRemainAmount;
                double totalPendingAmount;
                if ("NewActivation".equalsIgnoreCase(customersRepository.findStatusById(pojo.getCustomerid()))) {
                    totalRemainAmount = creditDocRepository.totalWithDrawAmountCaf(pojo.getCustomerid());
                    totalPendingAmount = creditDocRepository.totalPendingAmountCaf(pojo.getCustomerid());
                } else {
                    totalRemainAmount = creditDocRepository.totalWithDrawAmount(pojo.getCustomerid());
                    totalPendingAmount = creditDocRepository.totalPendingAmount(pojo.getCustomerid());
                }
                double remainingAmount = totalRemainAmount - totalPendingAmount;

                Double withDrawCreditDocTotal = creditDocRepository.findTotalAmountByCreditDocIds(pojo.getWithDrawCreditdocId());

                if (pojo.getAmount() - remainingAmount > 0.1) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not tranfer more than remaining amount.", null);
                }
                else if(pojo.getAmount()>withDrawCreditDocTotal){
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please select more payments â€” amount exceeds selected payment balance.", null);
                } else {
//                    List<CreditDocument> creditDocumentList = IterableUtils.toList(creditDocRepository.findAllByInvoiceIdIn(pojo.getInvoiceId()));
                    pojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
                    pojo.setPaymode(CommonConstants.PAYMENT_MODE.Transfer);
                    //With from the fromustomer
                    saveTransferWalletWithdraw(pojo, true, false, false, null, null, null, null, null, null);

                    // Transfer to the toCustomer
                    RecordPaymentPojo toCustPojo = new RecordPaymentPojo();
                    toCustPojo.setAmount(pojo.getAmount());
                    toCustPojo.setBank(pojo.getBank());
                    toCustPojo.setCustomerid(pojo.getToCustomerId());
                    toCustPojo.setPaymode(pojo.getPaymode());
                    toCustPojo.setReferenceno(pojo.getReferenceno());
                    toCustPojo.setPaymentreferenceno(pojo.getReferenceno());
                    toCustPojo.setRemark(pojo.getRemark());
                    toCustPojo.setReciptNo(pojo.getReciptNo());
                    toCustPojo.setType("Payment");
                    toCustPojo.setPaytype("advance");
                    toCustPojo.setPaymode(CommonConstants.PAYMENT_MODE.Transfer);
                    toCustPojo.setTdsAmount(pojo.getTdsAmount());
                    toCustPojo.setAbbsAmount(pojo.getAbbsAmount());
                    toCustPojo.setInvoiceId(Collections.singletonList(0));
                    toCustPojo.setOnlinesource("Transfer");
                    PaymentListPojo paymentListPojo = new PaymentListPojo(pojo.getAbbsAmount(), pojo.getAbbsAmount(), 0, pojo.getAmount());
                    toCustPojo.setPaymentListPojos(Collections.singletonList(paymentListPojo));
                    save(toCustPojo, false, false, false, null, null, null, false, null, null);
                }
            }
        }
        return pojo;
    }

    public void validateRequest(RecordPaymentPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
    }


    public RecordPaymentPojo createPaymentForOnline(DebitDocument debitDocument, String onlineSource, String transactionNumber) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(debitDocument.getTotalamount());
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(debitDocument.getCustomer().getId());
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setReferenceno(debitDocument.getDocnumber());
        recordPaymentPojo.setRemark("payment for" + onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(debitDocument.getId()));
        recordPaymentPojo.setPaytype("invoice");
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(debitDocument.getId());
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(debitDocument.getTotalamount());
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPaymentPojo createPaymentForOnlineWithAmount(DebitDocument debitDocument, String onlineSource, String transactionNumber, Double amount) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(debitDocument.getCustomer().getId());
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setReferenceno(debitDocument.getDocnumber());
        recordPaymentPojo.setRemark("payment for" + onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(debitDocument.getId()));
        recordPaymentPojo.setPaytype("invoice");
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(debitDocument.getId());
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPaymentPojo createPaymentForOnlineCaf(TrialDebitDocument debitDocument, String onlineSource, String transactionNumber, Double amount) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(debitDocument.getCustomer().getId());
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setReferenceno(debitDocument.getDocnumber());
        recordPaymentPojo.setRemark("payment for" + onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(debitDocument.getId()));
        recordPaymentPojo.setPaytype("invoice");
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(debitDocument.getId());
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPaymentPojo createPaymentForAddWallet(Integer custId, String referenceno, Double amount, String onlineSource, String transactionNumber, String pgTransactionId) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(custId);
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setRemark(onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(0));
        recordPaymentPojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(referenceno != null ? referenceno : transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        recordPaymentPojo.setPaymentreferenceno(pgTransactionId);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(0);
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPaymentPojo createPaymentForWriteOffWallet(Integer custId, String referenceno, Double amount, String onlineSource, String transactionNumber, Integer debitDocId, String remarks) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(custId);
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setRemark(remarks);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(0));
        recordPaymentPojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(referenceno != null ? referenceno : transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(debitDocId);
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPaymentPojo createPaymentForAddWalletWithInvoice(Integer custId, String referenceno, Double amount, String onlineSource, String transactionNumber, Integer invoiceId) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(custId);
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setRemark(onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(0));
        recordPaymentPojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.INVOICE);
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        recordPaymentPojo.setReciptNo(referenceno != null ? referenceno : transactionNumber);
        recordPaymentPojo.setReferenceno(transactionNumber);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(invoiceId);
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public RecordPayment convertRecordPaymentPojoToRecordPaymentModel(RecordPaymentPojo recordPaymentPojo) {
        RecordPayment recordPayment = null;
        if (recordPaymentPojo != null) {
            recordPayment = new RecordPayment();
            recordPayment.setChequedate(recordPaymentPojo.getChequedate());
            if (Objects.nonNull(recordPaymentPojo.getChequedatestr())) {
                recordPayment.setChequedate(LocalDate.parse(recordPaymentPojo.getChequedatestr()));
            }
            recordPayment.setPaymentdate(recordPaymentPojo.getPaymentdate());
            recordPayment.setChequeno(recordPaymentPojo.getChequeno());
            recordPayment.setBank(recordPaymentPojo.getBank());
            recordPayment.setBranch(recordPaymentPojo.getBranch());
            recordPayment.setCustomerid(Integer.toString(recordPaymentPojo.getCustomerid()));
            recordPayment.setPaymode(recordPaymentPojo.getPaymode());
            if (Objects.isNull(recordPaymentPojo.getAmount())) {
                recordPayment.setAmount(0D);
            } else {
                recordPayment.setAmount(recordPaymentPojo.getAmount());
            }
            recordPayment.setPaymentreferenceno(recordPaymentPojo.getPaymentreferenceno());
            recordPayment.setRemark(recordPaymentPojo.getRemark());
            recordPayment.setReciptNo(recordPaymentPojo.getReciptNo());
            if (recordPaymentPojo.getReferenceno() != null)
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            if (recordPaymentPojo.getCustomerid() != null) {
                recordPayment.setCustomer(customersRepository.findById(recordPaymentPojo.getCustomerid()).get());//customerService.get(recordPaymentPojo.getCustomerid()));
            }
//            if (recordPaymentPojo.getInvoiceId() != null) {
//                recordPayment.setInvoiceId(recordPaymentPojo.getInvoiceId());
//            }
            if (recordPaymentPojo.getPaytype() != null) {
                recordPayment.setPaytype(recordPaymentPojo.getPaytype());
            }
            if (recordPaymentPojo.getType() != null) {
                recordPayment.setType(recordPaymentPojo.getType());
            }
            if (recordPaymentPojo.getFilename() != null) {
                recordPayment.setFilename(recordPaymentPojo.getFilename());
            }
            if (recordPaymentPojo.getUniquename() != null) {
                recordPayment.setUniquename(recordPaymentPojo.getUniquename());
            }
            if (recordPaymentPojo.getBarteramount() != null) {
                recordPayment.setBarteramount(recordPaymentPojo.getBarteramount());
            }
            recordPayment.setMvnoId(recordPaymentPojo.getMvnoId());
            recordPayment.setTdsAmount(recordPaymentPojo.getTdsAmount());
            recordPayment.setAbbsAmount(recordPaymentPojo.getAbbsAmount());
            if (recordPaymentPojo.getOnlinesource() != null) {
                recordPayment.setOnlinesource(recordPaymentPojo.getOnlinesource());
            }
            if (recordPaymentPojo.getReferenceno() != null) {
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            }
        }
        return recordPayment;
    }


    public void uploadDocument(RecordPaymentPojo pojo, MultipartFile file) throws Exception {
        String SUBMODULE = "Payment" + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).getValue();
        List<RecordPayment> finalResponseList = new ArrayList<>();
        try {
            Customers customers = customersRepository.findById(pojo.getCustomerid()).get();//getById(pojo.getCustomerid());
            String subFolderName = "/" + customers.getUsername().trim() + "/";
            String path = PATH + subFolderName;
            logger.debug(SUBMODULE + ":File Path:" + path);
            if (null != pojo.getFilename()) {
                System.out.println(file.getSize());

                MultipartFile file1 = fileUtility.getFileFromArrayForTickets(file);
                if (null != file1) {
                    pojo.setUniquename(fileUtility.saveFileToServer(file1, path));
                    pojo.setFilename(pojo.getUniquename());
                }


            } else {
                if (null != pojo) {
                    if (null != pojo.getFilename() && null != pojo.getFilename() && !pojo.getFilename().equalsIgnoreCase(pojo.getFilename())) {
                        fileUtility.removeFileAtServer(pojo.getUniquename(), path);
                    }

                    MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                    if (null != file1) {
                        pojo.setUniquename(fileUtility.saveFileToServer(file1, path));
                        pojo.setFilename(pojo.getUniquename());
                    }
                    RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo);

                    finalResponseList.add(obj);
                }


            }

        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CreditDocument', '2')")
    public RecordPaymentPojo save(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked) throws Exception {
        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
//        BankManagement bankManagement = validateBankManagement(pojo.getBankManagement());
        List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
        String customerName = null;
        Integer custMvnoId = null;
        String mobileNumber = null;
        String emailId = null;
        String countryCode = null;

        StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).orElse(null);

//        DebitDocument debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).orElse(null);
//        if(pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
//            String msg = checkPaymentValid(debitDocument);
//            if(!msg.equalsIgnoreCase("success")) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),msg, null);
//            }
//        }
//
//        if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
//            if (debitDocument != null) {
//                Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
//                if (totalCreditNoteGenerated == 0) {
//                    if (pojo.getAmount() > debitDocument.getTotalamount()) {
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
//                    }
//                } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
//                }
//                QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//                BooleanExpression booleanExpression = qDebitDocument.isNotNull();
//                booleanExpression = booleanExpression.and(qDebitDocument.customer.id.eq(pojo.getCustomerid()));
//                booleanExpression = booleanExpression.and(qDebitDocument.startdate.after(LocalDateTime.now()));
//                List<DebitDocument> debitDocumentList = IterableUtils.toList(debitDocRepository.findAll(booleanExpression));
//                if(!debitDocumentList.isEmpty()){
//                    ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
//                }
//
//
//
//            }
//        }
//        CreditDocument creditDocument = new CreditDocument(pojo);
//        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//        if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
//            if (bankManagement != null) {
//                if (!bankManagement.getStatus().equals("Active")) {
//                    throw new RuntimeException("Status change at run time");
//                }
//            }
//
//        }
//        if (pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
//
//            creditDocument.setBankManagement(bankManagement.getId());
//
//
//        }
//        if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
//            creditDocument.setDestinationBank(pojo.getDestinationBank());
//        }
//        if (pojo.getOnlinesource() != null) {
//            CommonList commonList = commonListRepository.findByValue(pojo.getOnlinesource());
//            creditDocument.setOnlinesource(commonList.getText());
//        }
//        if (pojo.getReferenceno() != null) {
//            creditDocument.setReferenceno(pojo.getReferenceno());
//        }
//
//
////        creditDocument.setCreditdocumentno(getInvoiceNo());
//
//
        RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo);
//        CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
//        if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
//            doc.setInvoiceId(pojo.getInvoiceId().get(0));
//        }
//        if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
//        else doc.setLcoid(null);
        //tyoe == withdrawl then doc.settype(DR)
        if (pojo.getCreditDocId() != null) {
            CreditDocument doc = creditDocRepository.findById(pojo.getCreditDocId()).orElse(null);

            if (doc != null) {
                Integer creditDocid = doc.getId();
                List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
                if (pojo.getInvoiceId().size() != 0) {
                    if (pojo.getInvoiceId().stream().anyMatch(integer -> integer == CommonUtils.PAYMENT_STATUS_ADVANCED)) {
                        pojo.setInvoiceId(null);
                    } else {
                        if (!CollectionUtils.isEmpty(pojo.getPaymentListPojos())) {
                            for (PaymentListPojo paymentListPojo : pojo.getPaymentListPojos()) {
                                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                                creditDebitDocMapping.setCreditDocId(doc.getId());
                                if (paymentListPojo.getInvoiceId() != CommonUtils.PAYMENT_STATUS_ADVANCED) {
                                    creditDebitDocMapping.setDebtDocId(paymentListPojo.getInvoiceId());
                                    creditDebitDocMapping.setAmount(paymentListPojo.getAmountAgainstInvoice());
                                    if (paymentListPojo.getAbbsAmountAgainstInvoice() != null) {
                                        creditDebitDocMapping.setAbbsAmount(paymentListPojo.getAbbsAmountAgainstInvoice());
                                    }
                                    if (paymentListPojo.getTdsAmountAgainstInvoice() != null) {
                                        creditDebitDocMapping.setTdsAmount(paymentListPojo.getTdsAmountAgainstInvoice());
                                    }
                                    if (isRevoked) creditDebitDocMapping.setAdjustedAmount(pojo.getAmount());
                                    creditDebtMappingRepository.save(creditDebitDocMapping);
                                    creditDebitDocMappings.add(creditDebtMappingRepository.save(creditDebitDocMapping));
                                }
                            }
                        }

                        if (CollectionUtils.isEmpty(pojo.getPaymentListPojos())) {
                            for (int i = 0; i < pojo.getInvoiceId().size(); i++) {
                                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                                Integer debitDocid = pojo.getInvoiceId().get(i);
                                if (doc.getAmount() != doc.getAdjustedAmount() && doc.getAdjustedAmount() < doc.getAmount()) {
                                    if (creditDocid != null && debitDocid != null) {
                                        if (debitDocid != CommonUtils.PAYMENT_STATUS_ADVANCED) {
                                            creditDebitDocMapping.setCreditDocId(doc.getId());
                                            creditDebitDocMapping.setDebtDocId(debitDocid);
                                            creditDebitDocMapping.setAdjustedAmount(0d);
                                            if (isRevoked) creditDebitDocMapping.setAdjustedAmount(pojo.getAmount());
                                            creditDebtMappingRepository.save(creditDebitDocMapping);
                                            creditDebitDocMappings.add(creditDebtMappingRepository.save(creditDebitDocMapping));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (Objects.nonNull(customers)) {
                    customerName = customers.getUsername();
                    custMvnoId = customers.getMvnoId();
                    mobileNumber = customers.getMobile();
                    emailId = customers.getEmail();
                    countryCode = customers.getCountryCode();
                }


            }
            doc = creditDocRepository.save(doc);
//            CreditDocMessage creditDocMessage = new CreditDocMessage(doc, creditDebitDocMappingList);
//            messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);


            obj.setReferenceno(doc.getReferenceno());
            obj.setRemark(doc.getRemarks());
            pojo = convertRecordPaymentModelToRecordPaymentPojo(obj);
            if (iswithdrawal) {
                pojo.setType("DR");
            }
            if (isInvoiceVoid) {
                if (isRevoked) addLedgerAndLedgerDetailEntry(pojo, doc.getId(), false);
                else addLedgerAndLedgerDetailEntry(pojo, doc.getId(), isInvoiceVoid);
            }
//            customerService.sendCustPaymentSuccessMessage(RabbitMqConstants.CUSTOMER_PAYMENT_SUCCESS, customers.getUsername(), pojo.getAmount(), pojo.getPaymode(), customers.getMvnoId(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getId(), pojo.getReciptNo(), String.valueOf(pojo.getPaymentdate()));
            if (doc != null) pojo.setCreditDocId(doc.getId());
            if (doc.getOnlinesource() != null) {
                pojo.setOnlinesource(doc.getOnlinesource());
            }
            if (doc.getDestinationBank() != null) {
                pojo.setDestinationBank(doc.getDestinationBank());
            }
        }
        return pojo;
    }

    public RecordPaymentPojo convertRecordPaymentModelToRecordPaymentPojo(RecordPayment recordPayment) {
        RecordPaymentPojo recordPaymentPojo = null;
        if (recordPayment != null) {
            recordPaymentPojo = new RecordPaymentPojo();
            recordPaymentPojo.setChequedate(recordPayment.getChequedate());
            recordPaymentPojo.setPaymentdate(recordPayment.getPaymentdate());
            recordPaymentPojo.setChequeno(recordPayment.getChequeno());
            recordPaymentPojo.setBank(recordPayment.getBank());
            recordPaymentPojo.setPaymode(recordPayment.getPaymode());
            if (Objects.isNull(recordPayment.getAmount())) {
                recordPaymentPojo.setAmount(0D);
            } else {
                recordPaymentPojo.setAmount(recordPayment.getAmount());
            }
            recordPaymentPojo.setPaymentreferenceno(recordPayment.getPaymentreferenceno());
            recordPaymentPojo.setBranch(recordPayment.getBranch());
            recordPaymentPojo.setReferenceno(recordPayment.getReferenceno());
            recordPaymentPojo.setRemark(recordPayment.getRemark());
            recordPaymentPojo.setReciptNo(recordPayment.getReciptNo());
            if (recordPayment.getCustomer() != null) {
                recordPaymentPojo.setCustomerid(recordPayment.getCustomer().getId());
            }

//            if (recordPayment.getInvoiceId() != null) {
//                recordPaymentPojo.setInvoiceId(recordPayment.getInvoiceId());
//            }

            if (recordPayment.getPaytype() != null) {
                recordPaymentPojo.setPaytype(recordPayment.getPaytype());
            }
            if (recordPayment.getType() != null) {
                recordPaymentPojo.setType(recordPayment.getType());
            }
            if (recordPayment.getFilename() != null) {
                recordPaymentPojo.setFilename(recordPayment.getFilename());
            }
            if (recordPayment.getUniquename() != null) {
                recordPaymentPojo.setUniquename(recordPayment.getUniquename());
            }
            if (recordPayment.getBarteramount() != null) {
                recordPaymentPojo.setBarteramount(recordPayment.getBarteramount());
            }
        }
        return recordPaymentPojo;
    }

    public void addLedgerAndLedgerDetailEntry(RecordPaymentPojo recordPayment, Integer creditDocumentId, boolean isVoidInvoice) {
        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        Optional<Customers> customers = customersRepository.findById(recordPayment.getCustomerid());
//        QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
//        BooleanExpression booleanExpression = qCustomerLedger.isNotNull().and(qCustomerLedger.customer.id.eq(recordPayment.getCustomerid()));

//        CustomerLedger list=ledgerRepository.findByCustomerId(recordPayment.getCustomerid());
        ledger = ledgerRepository.findByCustomerId(recordPayment.getCustomerid()).orElse(null);

        if (Objects.nonNull(ledger)) {
            ledger.setTotalpaid(ledger.getTotalpaid() + recordPayment.getAmount());
            ledger.setTotaldue(ledger.getTotaldue() - recordPayment.getAmount());
            ledgerRepository.save(ledger);
        }

        ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(recordPayment.getAmount());
        ledgerDtls.setPaymentMode(recordPayment.getPaymode());
        ledgerDtls.setBank(recordPayment.getBank());
        ledgerDtls.setBranch(recordPayment.getBranch());
        ledgerDtls.setPaymentRefNo(recordPayment.getPaymentreferenceno());
        ledgerDtls.setCreditdocid(creditDocumentId);
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsVoid(isVoidInvoice);
        ledgerDtls.setIsDelete(isVoidInvoice);


        if (recordPayment.getPaytype().equalsIgnoreCase("advance")) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
        } else if (recordPayment.getPaytype().equalsIgnoreCase("invoice")) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE);
        } else if (recordPayment.getPaytype().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CREDIT_NOTE);
        } else {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
        }

        if (recordPayment.getRemark() != null) ledgerDtls.setDescription(recordPayment.getRemark());
        if (customers.isPresent()) ledgerDtls.setCustomer(customers.get());
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        if (recordPayment.getType().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT))
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);

        ledgerDtlsService.save(ledgerDtls);
    }

    public RecordPaymentPojo createPaymentFromAmount(Integer custId, Double amount, String onlineSource, List<DebitDocument> debitDocumentList) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(custId);
        recordPaymentPojo.setInvoiceId(debitDocumentList.stream().map(debitDocument -> debitDocument.getId()).collect(Collectors.toList()));
        recordPaymentPojo.setPaymode(onlineSource);
        recordPaymentPojo.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
        recordPaymentPojo.setRemark("payment for" + onlineSource);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());;
        recordPaymentPojo.setPaytype("invoice");
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType("Payment");
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        for (DebitDocument debitDocument : debitDocumentList) {
            PaymentListPojo paymentListPojo = new PaymentListPojo();
            paymentListPojo.setInvoiceId(debitDocument.getId());
            paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
            paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
            paymentListPojo.setAmountAgainstInvoice(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
            paymentListPojoList.add(paymentListPojo);
        }
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public void addPaymentInCustomerLedger(Customers customers, CreditDocument doc) {
        CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(doc.getAmount());
        ledgerDtls.setCreditdocid(doc.getId());
        ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
        ledgerDtls.setCustomer(customers);
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        ledgerDtls = customerLedgerDtlsRepository.save(ledgerDtls);
    }

    public void addPaymentInCustomerLedger(Customers customers, List<CreditDocument> document) {
        List<CustomerLedgerDtls> customerLedgerDtls = new ArrayList<>();
        for (CreditDocument doc : document) {
            CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
            ledgerDtls.setAmount(doc.getAmount());
            ledgerDtls.setCreditdocid(doc.getId());
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
            ledgerDtls.setCustomer(customers);
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            customerLedgerDtls.add(ledgerDtls);
        }
        customerLedgerDtls = customerLedgerDtlsRepository.saveAll(customerLedgerDtls);
    }


    public String assemblePaymentXML(CreditDocument doc, String addressType) {
        return assemblePaymentXML(doc, addressType, null, doc.getInvoiceId());
    }


    public String assemblePaymentXML(CreditDocument doc, String addressType, CustomerAddress address, Integer debitDocId) {
        try {
            if (debitDocId != null) {
                DebitDocument docDebit = debitDocRepository.findById(debitDocId).orElse(null);
                return paymentDetailsXml.getPaymentDetails(doc, addressType, address, docDebit);
            } else {
                return paymentDetailsXml.getPaymentDetails(doc, addressType, address, null);
            }
        } catch (Exception ex) {
            logger.error("Error while assemble payment xml: " + ex.getMessage());
        }
        return "";
    }

    /**
     * Adjust Credit Note for Inventory
     *
     * @param message
     * @throws Exception
     */
    public void adjustCreditNoteForInventory(RecordPaymentMessage message) throws Exception {
        try {
            if(message.getIsCaf()!=null && message.getIsCaf())
            {
                List<TrialDebitDocument> trialDebitDocuments = trialDebitDocRepository.findAllByInventoryMappingId(message.getCustomerMappingId());
                if(trialDebitDocuments!=null && !trialDebitDocuments.isEmpty())
                {
                    trialDebitDocuments.stream().forEach(x -> {
                        x.setBillrunstatus(Constants.DEBIT_DOC_STATUS.VOID);
                        trialDebitDocRepository.save(x);

                    });
                }

                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(message.getCustomerMappingId()).orElse(null);
                if(customerInventoryMapping!=null)
                {
                    customerInventoryMapping.setIsDeleted(true);
                    //customerInventoryMapping.setIsInvoiceCreated(true);
                    customerInventoryMappingRepo.save(customerInventoryMapping);
                }
                return;
            }
            List<DebitDocument> debitDocumentList = debitDocRepository.findAllByInventoryMappingId(message.getCustomerMappingId());
            for (DebitDocument item : debitDocumentList) {
                creatCreditNotAsPerService(item, null, null, message.getRemark(), false, Collections.singletonList(message.getCustomerMappingId()), message.getType(), message.getPaytype(), message.getAmount(), null);
            }
        } catch (CustomValidationException ex) {
            logger.error("Error while adjust credit note for inventory: " + ex.getMessage());
        }
    }

    public void adjustWithDrawal(CreditDocument creditDocument) {
        Double withDrawedAmount = creditDocument.getAmount();
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
//        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
//        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.withdrawId.eq(creditDocument.getId()));
        creditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAllByWithdrawId(creditDocument.getId()));
        int i = 0;
        while (i < IterableUtils.toList(creditDebitDocMappings).size() && withDrawedAmount > 0 && creditDebitDocMappings.size() > 0) {
            CreditDocument selectedCreditDoc = creditDocRepository.findById(creditDebitDocMappings.get(i).getCreditDocId()).orElse(null);
            Double withDrawalableAmount = 0d;
            if (selectedCreditDoc.getAdjustedAmount() == null) {
                withDrawalableAmount = selectedCreditDoc.getAmount();
            } else {
                withDrawalableAmount = selectedCreditDoc.getAmount() - selectedCreditDoc.getAdjustedAmount();
            }
            Double remainingAmount = withDrawedAmount - withDrawalableAmount;
            if (remainingAmount == 0) {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawedAmount);
                selectedCreditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            } else if (remainingAmount < 0) {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawedAmount);
            } else {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawalableAmount);
                selectedCreditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            }
            if (selectedCreditDoc.getAdjustedAmount() == null) {
                selectedCreditDoc.setAdjustedAmount(withDrawedAmount);
            } else {
                selectedCreditDoc.setAdjustedAmount(selectedCreditDoc.getAdjustedAmount() + withDrawedAmount);
            }
            withDrawedAmount = remainingAmount;
            creditDebtMappingRepository.save(creditDebitDocMappings.get(i));
            selectedCreditDoc = creditDocRepository.save(selectedCreditDoc);
            i++;
            if (selectedCreditDoc.getPaymode().equalsIgnoreCase("Credit Note") && (selectedCreditDoc.getAmount() - selectedCreditDoc.getAdjustedAmount() == 0)) {

                List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(selectedCreditDoc.getId());
                if (creditDebitDocMappingList.size() > 0) {
                    creditDebitDocMappingList.forEach(creditDebitDocMapping -> {
                        if (creditDebitDocMapping.getDebtDocId() != null) {
                            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                            if (debitDocument != null) {
                                Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                                if (totalCreditNoteGenerated.equals(debitDocument.getTotalamount())) {
                                    debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                } else {
                                    double adjustedAmount = 0;
                                    List<CreditDebitDocMapping> creditDebitmappingForDebitDocument = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                                    for (CreditDebitDocMapping debitDocMapping : creditDebitmappingForDebitDocument) {
                                        CreditDocument creditDocument1 = creditDocRepository.findById(debitDocMapping.getCreditDocId()).orElse(null);
                                        if (!creditDocument.getPaymode().equalsIgnoreCase("Credit Note")) {
                                            adjustedAmount = adjustedAmount + debitDocMapping.getAdjustedAmount();
                                        }
                                    }
                                    if (debitDocument.getTotalamount().equals(adjustedAmount)) {
                                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                                    } else if (debitDocument.getTotalamount() > adjustedAmount) {
                                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                                    } else if (adjustedAmount == 0) {
                                        debitDocument.setPaymentStatus(null);
                                    }
                                }
                            }
                        }
                    });
                }


            }
            if (selectedCreditDoc.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && selectedCreditDoc.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED) && selectedCreditDoc.getInvoiceId() != null) {
                Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(selectedCreditDoc.getInvoiceId());
                if (oldDebitDoc.isPresent() && selectedCreditDoc.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                    oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                } else {
                    oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                }
                debitDocRepository.save(oldDebitDoc.get());
            }
        }
        //  CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument, creditDebitDocMappings);
//        messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS);
//        messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);
    }

    public void adjustCreditdebitDoc(DebitDocument debitDocument, CreditDocument creditDocument) throws Exception {
        CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
        creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
        CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
        creditDebitDataPojo.setAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
        creditDebitDataPojo.setId(creditDocument.getId());
        List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
        creditDebitDataPojoList.add(creditDebitDataPojo);
        creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
        adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
    }

    public boolean isCreditDebitDocAdjusted(DebitDocument debitDocument, CreditDocument creditDocument) throws Exception {
        boolean isExecuted = false;
        try {
            CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
            creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
            CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
            creditDebitDataPojo.setAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
            creditDebitDataPojo.setId(creditDocument.getId());
            List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
            creditDebitDataPojoList.add(creditDebitDataPojo);
            creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
            adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
            isExecuted = true;
        } catch (Exception e) {
            logger.error("Error in isCreditDebitDocAdjusted :  " + e.getStackTrace());
        }
        return isExecuted;
    }

    public void createCreditNoteForOrgCustomer(List<CustomerServiceMapping> customerServiceMappings, CustomerBillingMessage customerBillingMessage) {
        if(customerBillingMessage.getData().get("custId")!=null)
        {
            Integer refCustId = Integer.parseInt(customerBillingMessage.getData().get("custId").toString());
            Customers customers = customersRepository.findById(refCustId).orElse(null);
            List<CustPlanMappping> custPlanMapppings = customers.getPlanMappingList().stream().filter(i -> i.getBillTo().equalsIgnoreCase("ORGANIZATION")).collect(Collectors.toList());
            if(customers!=null && custPlanMapppings.size()>0)
            {
                List<DebitDocument> documents = debitDocRepository.findByCustomerId(1);
                if(documents!=null && !documents.isEmpty())
                {
                    documents = documents.stream().filter(x -> x.getCustRefName() != null && x.getCustRefName().equalsIgnoreCase(customers.getUsername())).collect(Collectors.toList());
                    documents.forEach(doc -> {
                        AdjustCreditNoteForOrg(doc);
                    });
                }
            }
        }
    }

    private void AdjustCreditNoteForOrg(DebitDocument debitDocument) {
        if(debitDocument!=null && !debitDocument.getBillrunstatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.CANCELLED))
        {
            List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
            if(creditDebitDocMappings!=null && !creditDebitDocMappings.isEmpty())
            {
                List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList()));
                List<Integer> paymentIds = creditDocuments.stream().filter(x -> x.getType().equalsIgnoreCase("Payment")).map(x -> x.getId()).collect(Collectors.toList());
                Double payment = 0.0;
                if (paymentIds != null && !paymentIds.isEmpty())
                    payment = creditDebitDocMappings.stream().filter(x -> (paymentIds.contains(x.getCreditDocId())) && x.getAdjustedAmount() != null).mapToDouble(x -> x.getAdjustedAmount()).sum();

                if (payment > 0)
                    createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION, "Automatic Reversal Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);

                createCreditNote(debitDocument, CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE, false);

                debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                debitDocRepository.save(debitDocument);
                Customers customers = debitDocument.getCustomer();
                PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(customers.getId(), customers.getUsername(), customers.getCustomerType(), debitDocument.getTotalamount(), debitDocument.getId().longValue(), customers.getUsername(), true, debitDocument.getTotalamount(), 2, null, null, "null", "false", null, 0L, debitDocument, customers.getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), debitDocument.getCreatedByName(), null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), null, null, null, null);
//                messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges,PrepaidInvoiceCharges.class.getSimpleName()));
                kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));


            }
        }
    }


    public void createCreditNote(DebitDocument debitDocument, String payType, String type, String remarks, String mode, boolean isDbrRequired) {
        try {
            CreditDocument creditDocument = new CreditDocument();
            if (type != null && type.equalsIgnoreCase(CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION))
                creditDocument.setAmount(-debitDocument.getTotalamount());
            else
                creditDocument.setAmount(debitDocument.getTotalamount());
            creditDocument.setCustomer(debitDocument.getCustomer());
            creditDocument.setAdjustedAmount(creditDocument.getAmount());
            creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            List<Integer> invoiceId = new ArrayList<>();
            invoiceId.add(debitDocument.getId());
            creditDocument.setInvoiceId(debitDocument.getId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setPaymode(mode);
            creditDocument.setPaytype(payType);
            creditDocument.setType(type);
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            creditDocument.setIsDelete(false);
            creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
            creditDocument.setBuID(debitDocument.getCustomer().getBuId());
            creditDocument.setRemarks(remarks);
            creditDocument.setTdsamount(0d);
            creditDocument.setAbbsAmount(0d);
            creditDocument.setLcoid(debitDocument.getLcoId());
            creditDocument.setCreatedById(debitDocument.getCreatedById());
            creditDocument.setCreatedByName(debitDocument.getCreatedByName());
            creditDocument.setXmldocument(assemblePaymentXML(creditDocument, CommonConstants.ADDR_TYPE_PRESENT));
            if ((type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_PAYMENT) || type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERSAL_PAYMENT)) && mode.equalsIgnoreCase(CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION))
                creditDocument.setCreditdocumentno(getPaymentInvoiceNo());
            else
                creditDocument.setCreditdocumentno(getInvoiceNo());

            DecimalFormat df = new DecimalFormat("#.00");
            //if (debitDocument.getAdjustedAmount() != null)
            //creditDocument.setAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
            //else
            //creditDocument.setAmount(debitDocument.getTotalamount());
            //creditDocument.setAdjustedAmount(creditDocument.getAmount());


            creditDocument = creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setAmount(creditDocument.getAmount());
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            addLedgeAfterApproval(creditDocument);
        } catch (Exception e) {
            throw new RuntimeException("Exception when creating credit note for invoice: " + debitDocument.getId());
        }
    }

    public void processBudPaychangePlanMessage(BudpayChangePlanMessage message) throws Exception {
        Customers customers = customersRepository.findById(message.getCustomerId()).orElse(null);
        StaffUser staffUser = staffUserRepository.findById(message.getStaffId()).orElse(null);
        createBudPayPaymentFromAmount(customers, message.getAmount(), staffUser, message.getReferenceNumber());

    }

    public void createBudPayPaymentFromAmount(Customers customers, Double amount, StaffUser staffUser, String referenceNo) throws Exception {
        try {
            CreditDocument doc = new CreditDocument();
            doc.setCustomer(customers);
            doc.setCreatedByName(staffUser.getUsername());
            doc.setPaymode("Online");
            doc.setPaymentdate(LocalDate.now());
            doc.setAmount(amount);
            doc.setStatus(CommonConstants.PAYMENT_STATUS_FULLY_ADJUSTED);
            doc.setRemarks("Verified");
            doc.setIsDelete(false);
            doc.setTdsamount(0.0);
            doc.setInvoiceId(null);
            doc.setPaytype("advance");
            doc.setType("Payment");
            doc.setAdjustedAmount(amount);
            doc.setRemainingAmount(0.0);
            doc.setCreatedate(LocalDateTime.now());
            doc.setUpdatedate(LocalDateTime.now());
            doc.setCreatedByName(staffUser.getUsername());
            doc.setReferenceno(referenceNo);
            doc.setMvnoId(customers.getMvnoId());
            doc = creditDocRepository.save(doc);
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setCREATE_DATE(LocalDateTime.now());
            customerLedgerDtls.setAmount(amount);
            customerLedgerDtls.setCustomer(customers);
            customerLedgerDtls.setCreditdocid(doc.getId());
            customerLedgerDtls.setIsVoid(false);
            customerLedgerDtls.setDebitdocid(null);
            customerLedgerDtls.setTranstype("CR");
            customerLedgerDtls.setTranscategory("PAYMENT");
            customerLedgerDtlsRepository.save(customerLedgerDtls);
        }
        catch(Exception e){
            e.getStackTrace();
        }

    }

    public void deleteDuplicateEntry(List<CreditDebitDocMapping> creditDebitDocMappingList)
    {
        List<CreditDebitDocMapping> creditDebitDocMappings = creditDebitDocMappingList.stream().filter(creditDebitDocMapping -> creditDebitDocMapping.getAdjustedAmount().equals(0.0000)).collect(Collectors.toList());
        creditDebtMappingRepository.deleteInBatch(creditDebitDocMappings);
    }


    public RecordPaymentPojo getrecordPaymentPojo(PaymentDto paymentDto) {
        try {
            RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
            DebitDocCustDto debitDocCustDto = debitDocRepository.findDebitDocumentIdByDocNumberAndMvnoId(paymentDto.getInvoiceNo(), paymentDto.getClientId());
            PaymentListPojo paymentListPojo = new PaymentListPojo();
            if (debitDocCustDto != null) {
                recordPaymentPojo.setAmount(paymentDto.getAmount());
                recordPaymentPojo.setBank("");
                recordPaymentPojo.setCustomerid(debitDocCustDto.getCustomerId());
                recordPaymentPojo.setPaymode("Online");
                recordPaymentPojo.setReferenceno("");
                recordPaymentPojo.setPaytype("Payment");
                recordPaymentPojo.setPaytype("invoice");
                recordPaymentPojo.setTdsAmount(0d);
                recordPaymentPojo.setAbbsAmount(0d);
                recordPaymentPojo.setInvoiceId(Arrays.asList(debitDocCustDto.getDebitDocumentId()));
                recordPaymentPojo.setOnlinesource("");
                recordPaymentPojo.setType("Payment");
                recordPaymentPojo.setPaymentListPojos(new ArrayList<>());
                paymentListPojo.setTdsAmountAgainstInvoice(0d);
                paymentListPojo.setAbbsAmountAgainstInvoice(0d);
                paymentListPojo.setAmountAgainstInvoice(paymentDto.getAmount());
                paymentListPojo.setInvoiceId(debitDocCustDto.getDebitDocumentId());
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invoice id not found !!", null);
            }

            recordPaymentPojo.setPaymentListPojos(Arrays.asList(paymentListPojo));
            return recordPaymentPojo;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String adjustManualPaymentToCafInvoice(CreditDebitMappingPojo creditDebitDocMappingPojo) throws Exception {
        Integer invoiceId = creditDebitDocMappingPojo.getInvoiceId();
        List<CreditDebitDataPojo> creditDocumentList = creditDebitDocMappingPojo.getCreditDocumentList();
        TrialDebitDocument debitDocument = trialDebitDocRepository.findById(invoiceId).orElse(null);
        if (debitDocument != null) {
            Double amountToBePaid = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            int i = 0;
            boolean adjusted = false;
            while (!adjusted && i < creditDocumentList.size()) {

                CreditDebitDocMapping creditDebitDocMappings = new CreditDebitDocMapping();
                CreditDocument creditDocument = creditDocRepository.findById(creditDocumentList.get(i).getId()).orElse(null);
                Double paymentAmount = 0d;
                if (creditDocument.getAdjustedAmount() != null) {
                    paymentAmount = creditDocument.getAmount() - creditDocument.getAdjustedAmount();
                } else {
                    paymentAmount = creditDocument.getAmount();
                }
                Double remainingAmountFromPayment = paymentAmount - amountToBePaid;
                if (Math.abs(remainingAmountFromPayment) < 0.1) {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    if (Objects.isNull(creditDocument.getAdjustedAmount())) {
                        creditDocument.setAdjustedAmount(0.0000);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
//                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                    creditDebitDocMappings.setTrialDebitDocumentId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);
                    List<TempPartnerLedgerDetail> details1 = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());
                    if(!details1.isEmpty())
                    {
                        partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details1);
                        tempPartnerLedgerDetailsRepository.deleteAll(details1);
                    }


                } else if (remainingAmountFromPayment < 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(paymentAmount);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
//                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + paymentAmount);
                    creditDebitDocMappings.setTrialDebitDocumentId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(paymentAmount);
                } else {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(amountToBePaid);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
//                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + amountToBePaid);
                    creditDebitDocMappings.setTrialDebitDocumentId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);

                }
                i++;
                if (creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED)
                        && creditDocument.getInvoiceId() != null) {
                    Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(creditDocument.getInvoiceId());
                    if (oldDebitDoc.isPresent() && creditDocument.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    } else {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                    }
                    debitDocRepository.save(oldDebitDoc.get());
                }
                creditDocument = creditDocRepository.save(creditDocument);
                creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMappings);
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
                List<CreditDocMessage> creditDocMessages = new ArrayList<>();
                CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
                creditDocMessages.add(creditDoc);
                CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
                creditDocMessageList.setCreditDocMessageList(creditDocMessages);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
                // send auto approval call to cms to approve the CAF and sent to next immidiate team
                if (debitDocument.getPaymentStatus().equals(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                    logger.info("Initiating the auto approval process kafka call");
                    WorkFlowAutoApprovalMessage workFlowAutoApprovalMessage = new WorkFlowAutoApprovalMessage();
                    workFlowAutoApprovalMessage.setCustomerId(debitDocument.getCustomer().getId());
                    workFlowAutoApprovalMessage.setTriggeredAction(CommonConstants.CAF_ACTION.WALLET_SETTLEMENT);
                    workFlowAutoApprovalMessage.setMvnoId(debitDocument.getCustomer().getMvnoId());
                    logger.info("************** Message send kafka to CMS for Renew for this CustId : " + debitDocument.getCustomer().getId() + "**************");
                    if (debitDocument.getCustomer().getBuId() != null)
                        workFlowAutoApprovalMessage.setBuId(debitDocument.getCustomer().getBuId().intValue());
                    kafkaMessageSender.send(new KafkaMessageData(workFlowAutoApprovalMessage, WorkFlowAutoApprovalMessage.class.getSimpleName()));
                }
            }
            debitDocument = trialDebitDocRepository.save(debitDocument);
            return "success";
        } else {
            return "Not found invoice with given id";
        }
    }

    public CreditDocument saveTrialCreditDocument(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked, Integer mvnoId, Integer partnerId, List<Long> buId, Boolean isLco, Integer getCreatedById, String getCreatedByName) throws Exception {
        CreditDocument savedCreditDocument = null;
        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
        BankManagement bankManagement = validateBankManagement(pojo.getBankManagement());
        List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
        String customerName = null;
        Integer custMvnoId = null;
        String mobileNumber = null;
        String emailId = null;
        String countryCode = null;
        if (dbrService.getLoggedInUser() != null) {
            StaffUser loggedInUser = staffUserRepository.findById(dbrService.getLoggedInUser().getUserId()).orElse(null);
        }
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();

        if(pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE))
        {
            List<CreditDocument> creditDocuments = creditDocRepository.findAllByInvoiceIdIn(pojo.getInvoiceId());
            creditDocuments = creditDocuments.stream().filter(x -> x.getStatus().equalsIgnoreCase("Pending")).collect(Collectors.toList());
            if(creditDocuments!=null && !creditDocuments.isEmpty())
            {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CreditNote Already Generated previously and waiting for Approval", null);
            }
        }
        if (!pojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && !pojo.getPaytype().equalsIgnoreCase("Withdrawal")) {
            for (PaymentListPojo paymentPojo : pojo.getPaymentListPojos()) {
                DebitDocCustDTO debitDocument = debitDocRepository.findDebitDocById(paymentPojo.getInvoiceId());
                if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
                    String msg = checkPaymentValid(debitDocument);
                    if (!msg.equalsIgnoreCase("success")) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                    }
                }


                if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                    if (debitDocument != null) {
                        Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                        if (totalCreditNoteGenerated == 0) {
                            if (pojo.getAmount() > debitDocument.getTotalamount()) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                            }
                        } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                        List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                        if (!debitDocumentList.isEmpty()) {
                            ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                        }
                    }
                }
                CreditDocument creditDocument = new CreditDocument(pojo);
                if (dbrService.getLoggedInUser() != null) {
                    pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                } else {
                    pojo.setMvnoId(mvnoId);
                }
                //TODO:Bank
                if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                    if (bankManagement != null) {
                        if (!bankManagement.getStatus().equals("Active")) {
                            throw new RuntimeException("Status change at run time");
                        }
                    }

                }

                if (Objects.nonNull(pojo.getBankManagement()) && pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                    creditDocument.setBankManagement(bankManagement.getId());
                }
                if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                    creditDocument.setDestinationBank(pojo.getDestinationBank());
                }
                if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                    CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                    creditDocument.setOnlinesource(commonList.getText());
                }
                if (pojo.getReferenceno() != null) {
                    creditDocument.setReferenceno(pojo.getReferenceno());
                }

                RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, paymentPojo);
                CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
                if (doc.getReferenceno() != null) {
                    String updatedReferenceNo = pojo.getReferenceno();
                    if (pojo.getPaymentListPojos() != null && pojo.getPaymentListPojos().size() > 1 && paymentPojo.getInvoiceId() != null) {
                        updatedReferenceNo = pojo.getReferenceno() + "-" + paymentPojo.getInvoiceId();
                    }
                    doc.setReferenceno(updatedReferenceNo);
                    creditDocument.setReferenceno(updatedReferenceNo);
                }
                if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                    doc.setTrialDebitdocId(paymentPojo.getInvoiceId());
                }
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);

                if (doc != null) {
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getMvnoId() != null) {
                            doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                        }
                    } else {
                        if (mvnoId != null) {
                            doc.setMvnoId(mvnoId);
                        }
                    }
                    if (bankManagement != null) {
                        if (bankManagement.getId() != null) {
                            doc.setBankManagement(bankManagement.getId());
                        }
                    }
                    if (pojo.getDestinationBank() != null) {
                        if (pojo.getDestinationBank() != null) {
                            doc.setDestinationBank(pojo.getDestinationBank());
                        }
                    }


                    Integer lcoId;
                    if (getLoggedInUser() != null) {
                        if (getLoggedInUser().getLco() == true) {
                            doc.setLcoid(getLoggedInUser().getPartnerId());
                        }
                    } else if (isLco != null) {
                        if (isLco) {
                            if (partnerId != null) {
                                doc.setLcoid(partnerId);
                            }
                        } else {
                            doc.setLcoid(null);
                        }
                    } else doc.setLcoid(null);


                    if (Objects.isNull(doc.getReferenceno())) {
                        doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                    }
                    if (getLoggedInUser() != null) {
                        if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                            doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                        }
                    } else {
                        if (buId != null) {
                            doc.setBuID(buId.get(0));
                        }
                    }
                    if (iswithdrawal) {
                        doc.setType("DR");
                        pojo.setType("DR");
                    }
                    if (isRevoked) {
                        doc.setIsDelete(false);
                        doc.setStatus("Fully Adjusted");
                        doc.setAdjustedAmount(paymentPojo.getAmountAgainstInvoice());
                    }

                    if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                    if (pojo.getOnlinesource() != null) {
                        List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                        if (commonList != null && !commonList.isEmpty()) {
                            RecordPaymentPojo finalPojo = pojo;
                            commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                            if (commonList != null && !commonList.isEmpty())
                                doc.setLedgerId(commonList.get(0).getValue());
                        }
                    }
                    if (dbrService.getLoggedInUser() != null) {
                        doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                        doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                    } else {
                        if (getCreatedById != null && getCreatedByName != null) {
                            doc.setCreatedById(getCreatedById);
                            doc.setCreatedByName(getCreatedByName);
                        }
                    }
                    doc = creditDocRepository.save(doc);
                    doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                    doc = creditDocRepository.save(doc);
                    CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                    creditDebitDocMapping.setTrialDebitDocumentId(pojo.getInvoiceId().get(0));
                    creditDebitDocMapping.setCreditDocId(doc.getId());
                    creditDebitDocMapping.setAmount(doc.getAmount());
                    creditDebitDocMapping.setIsDeleted(false);
                    creditDebitDocMapping.setAdjustedAmount(0.0);
                    creditDebitDocMapping.setAbbsAmount(doc.getAbbsAmount());
                    creditDebitDocMapping.setTdsAmount(doc.getTdsamount());
                    CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                    savedCreditDocument = doc;
                    List<CreditDebitDocMapping> mappings = new ArrayList<>();
                    mappings.add(creditDebitDocMappings);
                    CreditDocMessage creditDoc = new CreditDocMessage(doc, mappings);
                    creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                    creditDocMessage.add(creditDoc);
                }
            }
            creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//            messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));

        } else {
            DebitDocCustDTO debitDocument = null;
            if (pojo.getInvoiceId() != null) {
                debitDocument = debitDocRepository.findDebitDocById(pojo.getInvoiceId().get(0));
            }
            if (pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
                String msg = checkPaymentValid(debitDocument);
                if (!msg.equalsIgnoreCase("success")) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), msg, null);
                }
            }


            if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                if (debitDocument != null) {
                    Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                    if (totalCreditNoteGenerated == 0) {
                        // if (pojo.getAmount() > debitDocument.getTotalamount()) {
                        if (pojo.getAmount() > Double.parseDouble(String.format("%.2f", debitDocument.getTotalamount()))) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                        }
                    } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
                    }
                    List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerIdAndStartdateAfter(pojo.getCustomerid(), LocalDateTime.now());
                    if (!debitDocumentList.isEmpty()) {
                        ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
                    }
                }
            }
            CreditDocument creditDocument = new CreditDocument(pojo);
            if (dbrService.getLoggedInUser() != null) {
                pojo.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
            } else {
                if (mvnoId != null) {
                    pojo.setMvnoId(mvnoId);
                }
            }
            //TODO:Bank
            if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
                if (bankManagement != null) {
                    if (!bankManagement.getStatus().equals("Active")) {
                        throw new RuntimeException("Status change at run time");
                    }
                }

            }

            if (pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
                creditDocument.setBankManagement(bankManagement.getId());
            }
            if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
                creditDocument.setDestinationBank(pojo.getDestinationBank());
            }
            if (pojo.getOnlinesource() != null && !pojo.getOnlinesource().equals("")) {
                CommonList commonList = commonListRepository.findByValueAndType(pojo.getOnlinesource(), pojo.getPaymode());
                creditDocument.setOnlinesource(commonList.getText());
            }
            if (pojo.getReferenceno() != null) {
                creditDocument.setReferenceno(pojo.getReferenceno());
            }

            RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo, null);
            CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
            if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
                doc.setTrialDebitdocId(pojo.getInvoiceId().get(0));
            }
            if (getLoggedInUser() != null) {
                if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
            } else if (isLco != null) {
                if (isLco) {
                    if (partnerId != null) {
                        doc.setLcoid(partnerId);
                    }
                } else {
                    doc.setLcoid(null);
                }
            } else doc.setLcoid(null);

            if (doc != null) {
                if (getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getMvnoId() != null) {
                        doc.setMvnoId(dbrService.getLoggedInUser().getMvnoId());
                    }
                } else {
                    if (mvnoId != null) {
                        doc.setMvnoId(mvnoId);
                    }
                }
                if (bankManagement != null) {
                    if (bankManagement.getId() != null) {
                        doc.setBankManagement(bankManagement.getId());
                    }
                }
                if (pojo.getDestinationBank() != null) {
                    if (pojo.getDestinationBank() != null) {
                        doc.setDestinationBank(pojo.getDestinationBank());
                    }
                }


                Integer lcoId;
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getLco() == true) {
                        doc.setLcoid(getLoggedInUser().getPartnerId());
                    }
                } else if (isLco != null) {
                    if (isLco) {
                        if (partnerId != null) {
                            doc.setLcoid(partnerId);
                        }
                    } else {
                        doc.setLcoid(null);
                    }
                } else doc.setLcoid(null);


                if (Objects.isNull(doc.getReferenceno())) {
                    doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
                }
                if (dbrService.getLoggedInUser() != null) {
                    if (dbrService.getLoggedInUser().getBuIds().size() == 1) {
                        doc.setBuID(dbrService.getLoggedInUser().getBuIds().get(0));
                    }
                } else {
                    if (buId != null) {
                        doc.setBuID(buId.get(0));
                    }
                }
                if (iswithdrawal) {
                    doc.setType("DR");
                    pojo.setType("DR");
                }
                if (isRevoked) {
                    doc.setIsDelete(false);
                    doc.setStatus("Fully Adjusted");
                    doc.setAdjustedAmount(pojo.getAmount());
                }

                if (bankManagement != null) doc.setLedgerId(bankManagement.getBankcode());

                if (pojo.getOnlinesource() != null) {
                    List<CommonList> commonList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("PAYMENT_MODE_LEDGER_ID", CommonConstants.ACTIVE_STATUS);
                    if (commonList != null && !commonList.isEmpty()) {
                        RecordPaymentPojo finalPojo = pojo;
                        commonList = commonList.stream().filter(x -> x.getText().equalsIgnoreCase(finalPojo.getOnlinesource())).collect(Collectors.toList());
                        if (commonList != null && !commonList.isEmpty())
                            doc.setLedgerId(commonList.get(0).getValue());
                    }
                }
                if (dbrService.getLoggedInUser() != null) {
                    doc.setCreatedById(dbrService.getLoggedInUser().getUserId());
                    doc.setCreatedByName(dbrService.getLoggedInUser().getUsername());
                } else {
                    if (getCreatedById != null && getCreatedByName != null) {
                        doc.setCreatedById(getCreatedById);
                        doc.setCreatedByName(getCreatedByName);
                    }
                }

                doc = creditDocRepository.save(doc);
                doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
                doc = creditDocRepository.save(doc);
                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                if (pojo.getInvoiceId() != null) {
                    creditDebitDocMapping.setTrialDebitDocumentId(pojo.getInvoiceId().get(0));
                }
                creditDebitDocMapping.setCreditDocId(doc.getId());
                creditDebitDocMapping.setAmount(doc.getAmount());
                creditDebitDocMapping.setIsDeleted(false);
                creditDebitDocMapping.setAdjustedAmount(0.0);
                CreditDebitDocMapping creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMapping);
                CreditDocMessage creditDoc = new CreditDocMessage(doc);
                creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMappings));
                creditDocMessage.add(creditDoc);
                savedCreditDocument = doc;
                creditDocMessageList.setCreditDocMessageList(creditDocMessage);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            }
        }
        return savedCreditDocument;
    }

    public void addWalletAmount(CustPayDTOMessage custPayDTOMessage) throws Exception {
        logger.info("Customer Payment dto for advance payment : " + custPayDTOMessage);
        RecordPaymentPojo recordPaymentPojo = createPaymentForAddWallet(custPayDTOMessage.getCustId(), custPayDTOMessage.getPgTransactionId(), custPayDTOMessage.getPayment(), custPayDTOMessage.getPaymentGatewayName(), custPayDTOMessage.getOrderId().toString(), custPayDTOMessage.getPgTransactionId());
        CreditDocument creditDocument = saveAuto(recordPaymentPojo, false, false, false, custPayDTOMessage.getMvnoid(), custPayDTOMessage.getPartnerId(), null, false, custPayDTOMessage.getCreatedById(), custPayDTOMessage.getCreatedByName(), true);
        creditDocument.setStatus("approved");
        if (custPayDTOMessage.getChildId() != null) {
            creditDocument.setFromId(custPayDTOMessage.getChildId());
            creditDocument.setRemarks("Add  child wallet amount");
        }

        creditDocRepository.save(creditDocument); /**Save status here for approval**/
        if (custPayDTOMessage.getChildId() != null) {
            Double wallet = customerLedgerDtlsRepository.findWalletAmt(creditDocument.getCustomer().getId());
            if (wallet > 0) {
                saveChildLedger(creditDocument); /**add amount in child ledger**/
            }
            else{
                logger.info("Parent wallet is not settle.Going to settle the parent wallet.");
                addLedgeAfterApproval(creditDocument);
            }
        }
        else {
            addLedgeAfterApproval(creditDocument); /**Auto approve**/
        }
        if (Objects.nonNull(custPayDTOMessage.getId())) {
            CustomerOnlinePaymentAudit customerOnlinePaymentAudit = customerOnlinePaymentAuditService.convertMessageToEntity(custPayDTOMessage);
            customerOnlinePaymentAudit.setPaymentDate(LocalDateTime.now());
            customerOnlinePaymentAudit.setCreditDocumentId(creditDocument.getId());
            customerOnlinePaymentAuditRepository.save(customerOnlinePaymentAudit);
        }
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();
        CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
        creditDocMessage.add(creditDoc);
        creditDocMessageList.setCreditDocMessageList(creditDocMessage);
        kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
        try {
            subscriberService.automatePayment(custPayDTOMessage.getCustId());
        } catch (Exception e) {
            logger.error("Exception occurred while auto renew payment on payment approval for customer {} for eventType=ADD_WALLET",custPayDTOMessage.getCustId(),e);
        }
        ClientService clientService = null;
        LoggedInUser loggedInUser = getLoggedInUser();
        try {
            clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.REVENUE_AUTHORITY_NAME,creditDocument.getMvnoId());
        } catch (Exception e) {
            // Log the exception but continue gracefully
            logger.warn("ClientService not found for REVENUE_AUTHORITY_NAME and mvnoId: " + creditDocument.getMvnoId(), e);
            clientService = null;
        }
        try {
            if(clientService!=null && "KRA".equalsIgnoreCase(clientService.getValue())) {
                kraUtils.processEtimsAddCreditNote(Collections.singletonList(creditDocument));
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Some Exception occured while integrating to {}",e);
        }


    }

    public void addWalletAmountWithInvoice(CustPayDTOMessage custPayDTOMessage, String remarks) throws Exception {
        logger.info("Customer Payment dto for advance payment : " + custPayDTOMessage);
        RecordPaymentPojo recordPaymentPojo = createPaymentForWriteOffWallet(custPayDTOMessage.getCustId(), custPayDTOMessage.getPgTransactionId(), custPayDTOMessage.getPayment(), CommonConstants.WRITE_OFF, custPayDTOMessage.getOrderId().toString(), custPayDTOMessage.getInvoiceId(), remarks);
        CreditDocument creditDocument = save(recordPaymentPojo, false, false, false, custPayDTOMessage.getMvnoid(), custPayDTOMessage.getPartnerId(), null, false, custPayDTOMessage.getCreatedById(), custPayDTOMessage.getCreatedByName());
        creditDocRepository.save(creditDocument); /**Save status here for pending**/
        if (Objects.nonNull(custPayDTOMessage.getId())) {
            CustomerOnlinePaymentAudit customerOnlinePaymentAudit = customerOnlinePaymentAuditService.convertMessageToEntity(custPayDTOMessage);
            customerOnlinePaymentAudit.setPaymentDate(LocalDateTime.now());
            customerOnlinePaymentAudit.setCreditDocumentId(creditDocument.getId());
            customerOnlinePaymentAuditRepository.save(customerOnlinePaymentAudit);
        }
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();
        CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
        creditDocMessage.add(creditDoc);
        creditDocMessageList.setCreditDocMessageList(creditDocMessage);
        kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
    }

    public void adjustAllCreditDebitDoc(List<CreditDocument> creditDocumentList, List<DebitDocument> debitDocumentList) throws Exception {
        for (CreditDocument creditDocument : creditDocumentList) {
            for (DebitDocument debitDocument : debitDocumentList) {
                adjustCreditdebitDoc(debitDocument, creditDocument);
            }
        }
    }

    public String adjustManualPaymentToInvoiceWithWallet(CreditDebitMappingPojo creditDebitDocMappingPojo) throws Exception {
        Integer invoiceId = creditDebitDocMappingPojo.getInvoiceId();
        List<CreditDebitDataPojo> creditDocumentList = creditDebitDocMappingPojo.getCreditDocumentList();
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId).orElse(null);
        if (debitDocument != null) {
            Double amountToBePaid = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            int i = 0;
            boolean adjusted = false;
            while (!adjusted && i < creditDocumentList.size()) {

                CreditDebitDocMapping creditDebitDocMappings = new CreditDebitDocMapping();
                CreditDocument creditDocument = creditDocRepository.findById(creditDocumentList.get(i).getId()).orElse(null);
                Double paymentAmount = 0d;
                if (creditDocument.getAdjustedAmount() != null) {
                    paymentAmount = creditDocument.getAmount() - creditDocument.getAdjustedAmount();
                } else {
                    paymentAmount = creditDocument.getAmount();
                }
//                Double walletAmount = autoRenewOrAddonPlanService.checkWalletBalanceByCustIdWithPositiveBalance(debitDocument.getCustomer().getId());
//                paymentAmount += walletAmount;
                Double remainingAmountFromPayment = paymentAmount - amountToBePaid;
                if (Math.abs(remainingAmountFromPayment) < 0.1) {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    if (Objects.isNull(creditDocument.getAdjustedAmount())) {
                        creditDocument.setAdjustedAmount(0.0000);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);
                    List<TempPartnerLedgerDetail> details1 = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());
                    if(!details1.isEmpty())
                    {
                        partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details1);
                        tempPartnerLedgerDetailsRepository.deleteAll(details1);
                    }


                } else if (remainingAmountFromPayment < 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(paymentAmount);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + paymentAmount);
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(paymentAmount);
                } else {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(amountToBePaid);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + amountToBePaid);
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);

                }
                i++;
                if (creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED)
                        && creditDocument.getInvoiceId() != null) {
                    Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(creditDocument.getInvoiceId());
                    if (oldDebitDoc.isPresent() && creditDocument.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    } else {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                    }
                    debitDocRepository.save(oldDebitDoc.get());
                }
                creditDocument = creditDocRepository.save(creditDocument);
                creditDebitDocMappings = creditDebtMappingRepository.save(creditDebitDocMappings);
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();

                CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                ledgerDtls.setCustomer(creditDocument.getCustomer());
                ledgerDtls.setDebitdocid(debitDocument.getId());
                ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                ledgerDtls.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls.setAmount(paymentAmount);
                ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsRepository.save(ledgerDtls);

                CustomerLedgerDtls ledgerDtls1 = new CustomerLedgerDtls();
                ledgerDtls1.setCustomer(creditDocument.getCustomer());
                ledgerDtls1.setDebitdocid(debitDocument.getId());
                ledgerDtls1.setCreditdocid(creditDocument.getId());
                ledgerDtls1.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                ledgerDtls1.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls1.setAmount(paymentAmount);
                ledgerDtls1.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsRepository.save(ledgerDtls1);
                List<CreditDocMessage> creditDocMessages = new ArrayList<>();
                CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
                creditDocMessages.add(creditDoc);
                CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
                creditDocMessageList.setCreditDocMessageList(creditDocMessages);
//                messageSender.send(creditDocMessageList, SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
            }
            debitDocument = debitDocRepository.save(debitDocument);
            Customers customers = debitDocument.getCustomer();
            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(customers.getId(), customers.getUsername(), customers.getCustomerType(), debitDocument.getTotalamount(), debitDocument.getId().longValue(), customers.getUsername(), true, debitDocument.getTotalamount(), 2, null, null, "null", "false", null, 0L, debitDocument, customers.getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), debitDocument.getCreatedByName(), null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), null, null, null, null);
//            messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));

            return "success";
        } else {
            return "Not found invoice with given id";
        }
    }

    public void adjustCreditDebitDocs(List<DebitDocumentDTOForAdjustment> debitDocs,
                                      List<CreditDocumentDTOForAdjustment> creditDocs) throws Exception {

        // Sort lists by ID to maintain sequential processing
        debitDocs.sort(Comparator.comparing(DebitDocumentDTOForAdjustment::getId));
        creditDocs.sort(Comparator.comparing(CreditDocumentDTOForAdjustment::getId));

        int debitIndex = 0, creditIndex = 0;
        List<Integer> fullyPaidDebitIds = new ArrayList<>();
        List<Integer> partiallyPaidDebitIds = new ArrayList<>();
        List<Integer> fullyAdjustedCreditIds = new ArrayList<>();
        List<Integer> partiallyAdjustedCreditIds = new ArrayList<>();
        List<CreditDebitDocMapping> mappings = new ArrayList<>();

        while (debitIndex < debitDocs.size() && creditIndex < creditDocs.size()) {
            DebitDocumentDTOForAdjustment debitDoc = debitDocs.get(debitIndex);
            CreditDocumentDTOForAdjustment creditDoc = creditDocs.get(creditIndex);
            if (debitDoc.getAdjustedAmount() == null) {
                debitDoc.setAdjustedAmount(0.0000);
            }
            if (creditDoc.getAdjustedAmount() == null) {
                creditDoc.setAdjustedAmount(0.0000);
            }
            double remainingDebitAmount = debitDoc.getTotalAmount() - debitDoc.getAdjustedAmount();
            double remainingCreditAmount = creditDoc.getAmount() - creditDoc.getAdjustedAmount();

            if (remainingDebitAmount <= 0) {
                debitIndex++; // Move to next debit doc
                continue;
            }
            if (remainingCreditAmount <= 0) {
                creditIndex++; // Move to next credit doc
                continue;
            }

            // Determine how much can be adjusted
            double adjustment = Math.min(remainingDebitAmount, remainingCreditAmount);
            CreditDebitDocMapping mapping = new CreditDebitDocMapping();
            mapping.setDebtDocId(debitDoc.getId());
            mapping.setCreditDocId(creditDoc.getId());
            mapping.setAdjustedAmount(adjustment);
            mappings.add(mapping);

            // Apply adjustment
            debitDoc.setAdjustedAmount(debitDoc.getAdjustedAmount() + adjustment);
            creditDoc.setAdjustedAmount(creditDoc.getAdjustedAmount() + adjustment);

            if (debitDoc.getTotalAmount().equals(debitDoc.getAdjustedAmount())) {
                debitDoc.setStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                fullyPaidDebitIds.add(debitDoc.getId());
                debitIndex++;
            } else {
                debitDoc.setStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                partiallyPaidDebitIds.add(debitDoc.getId());
            }

            // Update credit doc status
            if (creditDoc.getAmount().equals(creditDoc.getAdjustedAmount())) {
                creditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                fullyAdjustedCreditIds.add(creditDoc.getId());
                creditIndex++;
            } else {
                creditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                partiallyAdjustedCreditIds.add(creditDoc.getId());
            }

        }

        // Perform batch updates
        debitDocService.updateDebitDocuments(debitDocs);
        transactionUtil.updateCreditDocuments(creditDocs);
        transactionUtil.markDebitsAsPartiallyPaid(partiallyPaidDebitIds);
        transactionUtil.markDebitsAsFullyPaid(fullyPaidDebitIds);
        transactionUtil.markCreditsAsPartiallyAdjusted(partiallyAdjustedCreditIds);
        transactionUtil.markCreditsAsFullyAdjusted(fullyAdjustedCreditIds);
        transactionUtil.saveCreditDebitMappings(mappings);
    }

    public void adjustCreditTrailDebitDocs(List<TrailDebitDocumentDTOForAdjustment> debitDocs,
                                           List<CreditDocumentDTOForAdjustment> creditDocs) throws Exception {

        // Sort lists by ID to maintain sequential processing
        debitDocs.sort(Comparator.comparing(TrailDebitDocumentDTOForAdjustment::getId));
        creditDocs.sort(Comparator.comparing(CreditDocumentDTOForAdjustment::getId));

        int debitIndex = 0, creditIndex = 0;
        List<Integer> fullyPaidDebitIds = new ArrayList<>();
        List<Integer> partiallyPaidDebitIds = new ArrayList<>();
        List<Integer> fullyAdjustedCreditIds = new ArrayList<>();
        List<Integer> partiallyAdjustedCreditIds = new ArrayList<>();
        List<CreditDebitDocMapping> mappings = new ArrayList<>();
        List<TrailDebitDocumentDTOForAdjustment> fullypaidDtos = new ArrayList<>();

        while (debitIndex < debitDocs.size() && creditIndex < creditDocs.size()) {
            TrailDebitDocumentDTOForAdjustment debitDoc = debitDocs.get(debitIndex);
            CreditDocumentDTOForAdjustment creditDoc = creditDocs.get(creditIndex);
            if (debitDoc.getAdjustedAmount() == null) {
                debitDoc.setAdjustedAmount(0.0000);
            }
            if (creditDoc.getAdjustedAmount() == null) {
                creditDoc.setAdjustedAmount(0.0000);
            }
            double remainingDebitAmount = debitDoc.getTotalAmount() - debitDoc.getAdjustedAmount();
            double totalAdjustedForCredit = creditDebtMappingRepository.getTotalAdjustedForCredit(creditDoc.getId());
            double remainingCreditAmount = creditDoc.getAmount() - totalAdjustedForCredit;

            if (remainingDebitAmount <= 0) {
                debitIndex++; // Move to next debit doc
                continue;
            }
            if (Math.abs(remainingCreditAmount) <= 0) {
                creditIndex++; // Move to next credit doc
                continue;
            }

            // Determine how much can be adjusted
            double adjustment = Math.min(remainingDebitAmount, remainingCreditAmount);
            CreditDebitDocMapping mapping = new CreditDebitDocMapping();
            mapping.setTrialDebitDocumentId(debitDoc.getId());
            mapping.setCreditDocId(creditDoc.getId());
            mapping.setAdjustedAmount(adjustment);
            mappings.add(mapping);

            // Apply adjustment
            debitDoc.setAdjustedAmount(debitDoc.getAdjustedAmount() + adjustment);
//            creditDoc.setAdjustedAmount(creditDoc.getAdjustedAmount() + adjustment);

            if (debitDoc.getTotalAmount().equals(debitDoc.getAdjustedAmount())) {
                debitDoc.setStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                fullyPaidDebitIds.add(debitDoc.getId());
                fullypaidDtos.add(debitDoc);
                debitIndex++;
            } else {
                debitDoc.setStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                partiallyPaidDebitIds.add(debitDoc.getId());
            }

            // Update credit doc status
            double remainingCreditAfter = remainingCreditAmount - adjustment;

            if (Math.abs(remainingCreditAfter) <= 0) {
//                creditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
//                fullyAdjustedCreditIds.add(creditDoc.getId());
                creditIndex++;
            }
            /* else {
                creditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                partiallyAdjustedCreditIds.add(creditDoc.getId());
            }*/
        }

        // Perform batch updates
        trialDebitDocService.updateTrialDebitDocuments(debitDocs);
        //transactionUtil.updateCreditDocuments(creditDocs);
        transactionUtil.markTrailDebitsAsPartiallyPaid(partiallyPaidDebitIds);
        transactionUtil.markTrailDebitsAsFullyPaid(fullyPaidDebitIds);
        //transactionUtil.markCreditsAsPartiallyAdjusted(partiallyAdjustedCreditIds);
        //transactionUtil.markCreditsAsFullyAdjusted(fullyAdjustedCreditIds);
        transactionUtil.saveCreditDebitMappings(mappings);
        if (!fullypaidDtos.isEmpty()) {
            for (TrailDebitDocumentDTOForAdjustment debitDoc : fullypaidDtos) {
                logger.info("Initiating the auto approval process kafka call in trail function");
                Customers customers = customersRepository.findCustomerDataById(debitDoc.getCustId());
                if (customers != null) {
                    WorkFlowAutoApprovalMessage workFlowAutoApprovalMessage = new WorkFlowAutoApprovalMessage();
                    workFlowAutoApprovalMessage.setCustomerId(debitDoc.getCustId());
                    workFlowAutoApprovalMessage.setTriggeredAction(CommonConstants.CAF_ACTION.WALLET_SETTLEMENT);
                    workFlowAutoApprovalMessage.setMvnoId(customers.getMvnoId());
                    if (customers.getBuId() != null)
                        workFlowAutoApprovalMessage.setBuId(customers.getBuId().intValue());
                    kafkaMessageSender.send(new KafkaMessageData(workFlowAutoApprovalMessage, WorkFlowAutoApprovalMessage.class.getSimpleName()));
                    logger.info("************** Message send kafka to CMS for Renew for this CustId : " + debitDoc.getCustId() + "**************");
                    System.out.println("Kafka Message for WorkFlow AutoApproval for Wallet Settlement Action :" + workFlowAutoApprovalMessage);
                }
            }
        }
    }


    public String addToWallet(CustomerVoucherDTO pojo) {
        try {
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            custPayDTOMessage.setCustId(pojo.getCustId());
            custPayDTOMessage.setPayment(pojo.getVoucherAmount());
            custPayDTOMessage.setPaymentGatewayName("Wallet");
            String transactionNumber = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 1000));
            custPayDTOMessage.setPgTransactionId(transactionNumber);
            custPayDTOMessage.setOrderId(Long.valueOf(transactionNumber));
            Customers customers = customersRepository.findCustomerDataById(pojo.getCustId());
            custPayDTOMessage.setCreatedById(pojo.getCustId());
            custPayDTOMessage.setCreatedByName(customers.getFirstname());
            custPayDTOMessage.setMvnoid(customers.getMvnoId());
            custPayDTOMessage.setPartnerId(pojo.getPartnerId());
            addWalletAmount(custPayDTOMessage);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "Success";
    }

    public GenericDataDTO writeOffByDebitDocId(WriteOffRequestDTO request) {
        logger.info("********** writeOffByDebitDocId method start **********");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (request.getDebitDocId() == null) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invoice id not found", null);
            }
            if (request.getRemarks() == null) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Remarks can not empty", null);
            }
            Optional<DebitDocument> optionalDebitDocument = debitDocRepository.findById(request.getDebitDocId());
            if (optionalDebitDocument.isPresent()) {
                DebitDocument debitDocument = optionalDebitDocument.get();
                CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                custPayDTOMessage.setCustId(debitDocument.getCustomer().getId());
                custPayDTOMessage.setPayment(request.getWriteOffAmount());
                custPayDTOMessage.setMerchantName(CommonConstants.WRITE_OFF);
                custPayDTOMessage.setPaymentGatewayName(CommonConstants.WRITE_OFF_AMOUNT + " " + getLoggedInUser().getUsername());
                String transactionNumber = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 1000));
                custPayDTOMessage.setPgTransactionId(transactionNumber);
                custPayDTOMessage.setOrderId(Long.valueOf(transactionNumber));
                Customers customers = customersRepository.findCustomerDataById(debitDocument.getCustomer().getId());
                custPayDTOMessage.setCreatedById(debitDocument.getCustomer().getId());
                custPayDTOMessage.setCreatedByName(customers.getFirstname());
                custPayDTOMessage.setMvnoid(customers.getMvnoId());
                custPayDTOMessage.setInvoiceId(request.getDebitDocId());
                String remarks = CommonConstants.WRITE_OFF_AMOUNT + " " + getLoggedInUser().getUsername() + ": " + request.getRemarks();
                addWalletAmountWithInvoice(custPayDTOMessage, remarks);
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invoice not found", null);
            }
        } catch (Exception e) {
            e.getStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.info("********** get error while WriteOffByDebitDocId by debitDocId **********" + request.getDebitDocId());
        }
        genericDataDTO.setResponseCode(RESP_CODE);
        genericDataDTO.setResponseMessage("Write off successfully done");
        logger.info("********** writeOffByDebitDocId method end **********");
        return genericDataDTO;
    }

    public List<CreditDocument> creatCreditNotAsPauseSevicePerService(List<ServiceChnageStatus> serviceChnageStatus) {
        List<CreditDocument> creditDocumentList = new ArrayList<>();
        List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<Integer> cprIds = new ArrayList<>();
        Double cnAmount = 0.0;
        try {
            if (!CollectionUtils.isEmpty(serviceChnageStatus)) {
                for (ServiceChnageStatus status : serviceChnageStatus) {
                    System.out.println("Service Status" + status);
                    cprIds = custPlanMappingRepository.getAllByCustServiceMappingIdIn(Collections.singletonList(status.getServiceMappingId()));
//
                    Optional<DebitDocument> debitDocumentOptional = debitDocRepository.findById(Math.toIntExact(status.getDebitDocId()));
                    if (debitDocumentOptional.isPresent()) {
                        DebitDocument debitDocument = debitDocumentOptional.get();
                        Double remainingAmount = debitDocument.getTotalamount();
                        System.out.println("Starting Date" + status.getStartTime());
                        List<CustomerChargeDBR> customerChargeDBR = customerChargeDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateBetween(Collections.singletonList(Long.valueOf(status.getCustPlanmappigId())), Long.valueOf(debitDocument.getId()), LocalDate.now(), LocalDateTime.now().plusDays(1).toLocalDate());
                        if (!CollectionUtils.isEmpty(customerChargeDBR)) {
                            customerChargeDBRList.addAll(customerChargeDBR);
                        }

                        if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                            cnAmount = customerChargeDBRList.get(0).getDbr();
                        }
                        Period period = Period.between(status.getStartTime().toLocalDate(), LocalDate.now());
                        cnAmount = cnAmount * period.getDays();
                        System.out.println("CN Amount" + cnAmount);
                        if (cnAmount != 0) {
                            Double invoiceWithoutTax = debitDocument.getTotalamount() - debitDocument.getTax() + debitDocument.getDiscount();
                            Double newDiscount = cnAmount * (debitDocument.getDiscount() / invoiceWithoutTax);
                            //ANG-11160
//                if (CollectionUtils.isEmpty(custInvMappingIds)) {
                            Double percentage = (debitDocument.getTax() * 100.0d) / (debitDocument.getTotalamount() - debitDocument.getTax());
                            Double prorateTaxAmount = ((cnAmount - newDiscount) * percentage) / 100.0d;
                            cnAmount = cnAmount - newDiscount + prorateTaxAmount;
//                }
                            CreditDocument creditDocument = new CreditDocument();
                            creditDocument.setAmount(cnAmount);

                            if (remainingAmount - cnAmount < 0.1 && remainingAmount != 0) {
                                creditDocument.setAmount(remainingAmount);
                            }
                            creditDocument.setInvoiceId(debitDocument.getId());
                            creditDocument.setTdsamount(0d);
                            creditDocument.setAbbsAmount(0d);
                            creditDocument.setCustomer(debitDocument.getCustomer());
                            creditDocument.setPaymode(Constants.PAYMENT_MODE.CREDIT_NOTE);
                            creditDocument.setPaymentdate(LocalDate.now());
                            creditDocument.setPaytype("creditnote");
                            creditDocument.setType(Constants.TRANS_CREDIT_NOTE);
                            creditDocument.setStatus(Constants.CREDIT_DOC_STATUS.PENDING);
                            creditDocument.setIsDelete(false);
                            creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
                            creditDocument.setBuID(debitDocument.getCustomer().getBuId());
                            creditDocument.setRemarks("\n Payment Created :-" + creditDocument.getAmount());
                            creditDocument.setLcoid(debitDocument.getLcoId());
                            creditDocument.setCreditdocumentno(getInvoiceNo());
                            creditDocument.setCreatedById(debitDocument.getCreatedById());
                            creditDocument.setCreatedByName(debitDocument.getCreatedByName());
//                        if (!forViewOnly) {
                            creditDocument = creditDocRepository.save(creditDocument);
                            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                            creditDebitDocMapping.setAdjustedAmount(0.00);
                            creditDebitDocMapping.setIsDeleted(false);
                            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
                            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
                            creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

//                            addLedgeAfterApproval(creditDocument);
                            creditDebtMappingRepository.save(creditDebitDocMapping);

                            Double creditAmountExcludeTax = dbrService.getCreditNotePriceExcludingTax(debitDocument, creditDocument.getAmount());
                            if (!CollectionUtils.isEmpty(cprIds) && !debitDocument.getIsDirectChargeInvoice()) {
                                dbrService.removeDbrByCPRListAndInvoiceIdStartDateAtChargeLevel(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), debitDocument.getId(), status.getStartTime().toLocalDate(), LocalDate.now());
                                dbrService.removedbrByCPRListAndInvoiceIdStartDate(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), debitDocument.getId(), status.getStartTime().toLocalDate(), LocalDate.now());
                            }
                            dbrService.addDbrEntry(debitDocument, debitDocument.getId().longValue(), creditAmountExcludeTax, null, null);

                            try {
                                partnerCommissionService.revertPartnerCommission(debitDocument, creditDocument.getAmount());
                            } catch (Exception e) {
                                logger.error("Error in Partner Revert Commission :  " + e.getStackTrace());
                            }
                            if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                                setCreditNoteDataToTable(customerChargeDBRList, creditDocument, debitDocument);
                            }
                            try {

                                List<CreditDocMessage> creditDocMessage = new ArrayList<>();
                                CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
                                creditDoc.setCreditDebitDocMappingList(Collections.singletonList(creditDebitDocMapping));
                                creditDocMessage.add(creditDoc);
                                creditDocMessageList.setCreditDocMessageList(creditDocMessage);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            logger.error("CN Amount get 0 so CN not created! for invoice number: " + debitDocument.getDocnumber());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        if (Objects.nonNull(creditDocMessageList)) {
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
        }
        return creditDocumentList;
    }

    public RecordPaymentPojo createPaymentForTransfer(Integer custId, String referenceno, Double amount) throws Exception {
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(amount);
        recordPaymentPojo.setBank("");
        recordPaymentPojo.setCustomerid(custId);
        recordPaymentPojo.setPaymode(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
        recordPaymentPojo.setRemark(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
        recordPaymentPojo.setChequedate(LocalDateTime.now().toLocalDate());
        recordPaymentPojo.setInvoiceId(Collections.singletonList(0));
        recordPaymentPojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
        recordPaymentPojo.setTdsAmount(0.0000);
        recordPaymentPojo.setAbbsAmount(0.0000);
        recordPaymentPojo.setType(CommonConstants.CREDIT_DOC_STATUS.TRANSFERRED);
        recordPaymentPojo.setReciptNo(referenceno);
        recordPaymentPojo.setReferenceno(referenceno);
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setInvoiceId(0);
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(amount);
        List<PaymentListPojo> paymentListPojoList = new ArrayList<>();
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        return recordPaymentPojo;
    }

    public void saveChildLedger(CreditDocument creditDocument) {
        CustomerLedgerDtls toCustomerLedgerDtls = new CustomerLedgerDtls();
        toCustomerLedgerDtls.setAmount(creditDocument.getAmount());
        toCustomerLedgerDtls.setFromId(creditDocument.getFromId());
        toCustomerLedgerDtls.setCreditdocid(creditDocument.getId());
        toCustomerLedgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_PAYMENT);
        toCustomerLedgerDtls.setTranstype("CR");
        toCustomerLedgerDtls.setDescription("Add wallet from child customer");
        customerLedgerDtlsRepository.save(toCustomerLedgerDtls);
        /**For main ledger added**/
        CustomerLedgerDtls mainLedgerDtls = new CustomerLedgerDtls();
        mainLedgerDtls.setAmount(creditDocument.getAmount());
        mainLedgerDtls.setCreditdocid(creditDocument.getId());
        mainLedgerDtls.setCustomer(creditDocument.getCustomer());
        mainLedgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.CHILD_BUY_PLAN);
        mainLedgerDtls.setTranstype("CHWALLETAUDIT");
        mainLedgerDtls.setDescription("Add wallet from child customer");
        customerLedgerDtlsRepository.save(mainLedgerDtls);
    }

    public void validatePaymentActionRequest(SearchPaymentPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && (pojo.getIdlist() == null || pojo.getIdlist().trim().length() <= 0)) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.improper.value.for.idList"), null);
        }
    }

    public GenericDataDTO approvePayment(SearchPaymentPojo entity) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CreditDocument doc = null;
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(Integer.valueOf(entity.getIdlist())));
        creditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAll(booleanExpression)).stream().sorted((o1, o2) -> o1.getDebtDocId().compareTo(o2.getDebtDocId())).collect(Collectors.toList());
        try {
            if (entity.getIdlist() != null) {
                String idList[] = entity.getIdlist().split(",");
                if (idList.length > 0) {
                    for (String id : idList) {
                        doc = null;

                        Optional<CreditDocument> creditDoc = creditDocRepository.findById(Integer.valueOf(id));
                        if(creditDoc.isPresent()) {
                            doc=creditDoc.get();
                            CreditDocMessage creditDocMessage = new CreditDocMessage(doc, IterableUtils.toList(creditDebitDocMappings));
                            creditDocMessage.setPaymentdate(LocalDate.now().toString());
                            creditDocMessage.setStatus(CommonConstants.CREDIT_DOC_STATUS.APPROVED);
                            save(creditDocMessage);
                        }

                    }
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    
//    @Transactional
//    public Integer addCreditDoc(CreditDocMessage message) {
//
//        Customers customer = customersRepository.findById(message.getCustomer()).orElse(null);
//
//        CreditDocument creditDocument =
//                new CreditDocument(
//                        message,
//                        customer);
//        // if (customer == null) {
//        //     creditDocument.setCustId(message.getCustomer());
//        // }
//        /*
//         * IDENTITY column
//         */
//        creditDocument.setId(null);
//
//        /*
//         * Mandatory fields
//         */
//        creditDocument.setStatus("approved");
//
//        if (creditDocument.getAdjustedAmount() == null) {
//            creditDocument.setAdjustedAmount(
//                    creditDocument.getAmount());
//        }
//
//        if (creditDocument.getCreditdocumentno() == null) {
//            String creditDocNo = null;
//            try {
//                creditDocNo = jdbcTemplate.queryForObject("SELECT nextvalpayment('paymentno')", String.class);
//            } catch (Exception e) {
//                // Fallback to java generation since database function nextvalpayment might be missing
//                // PY + Year + - + 8-digit timestamp = 15 chars max
//                LocalDate current_date = LocalDate.now();
//                int current_Year = current_date.getYear();
//                String randVal = String.valueOf(System.currentTimeMillis() % 100000000L);
//                StringBuilder sb = new StringBuilder();
//                sb.append("PY");
//                sb.append(current_Year);
//                sb.append("-");
//                while (sb.length() < 15 - randVal.length()) {
//                    sb.append('0');
//                }
//                sb.append(randVal);
//                creditDocNo = sb.toString();
//            }
//            creditDocument.setCreditdocumentno(creditDocNo);
//        }
//
//        CreditDocument saved =
//                creditDocRepository.save(
//                        creditDocument);
//
//        this.addLedgeAfterApproval(saved);
//
//        return saved.getId();
//    }

}
