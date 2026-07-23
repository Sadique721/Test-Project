package com.savbill.revenuemanagement.core.service.ledger;

import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO;
import com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PartnerLedgerDetails;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerLedgerDetailsRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.isp.IspMainPayload;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DebitDocService {

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    CustPlanMapppingRepository custPlanMappingRepository;

    @Autowired
    PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    private MvnoRepository mvnoRepository;
    @Autowired
    private PdfUtil pdfUtil;

    public static List<DebitDocDetailDTO> convertToRequiredResponse(List<DebitDocDetailDTO> inputList) {
        // Grouping by chargeType and summing up totalAmount for each group
        Map<String, Double> totalAmountsByChargeType = inputList.stream().filter(i -> i.getChargeType() != null).collect(Collectors.groupingBy(DebitDocDetailDTO::getChargeType, Collectors.summingDouble(DebitDocDetailDTO::getTotalAmount)));

        // Creating DebitDocDetailDTO objects for each group
        List<DebitDocDetailDTO> resultList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totalAmountsByChargeType.entrySet()) {
            String chargeType = entry.getKey();
            double totalAmount = entry.getValue();
            List<Integer> debitDocDetailIds = inputList.stream().filter(dto -> dto.getChargeType() != null && dto.getChargeType().equals(chargeType) && dto.getDebitDocDetailId() != null).map(DebitDocDetailDTO::getDebitDocDetailId).collect(Collectors.toList());
            resultList.add(new DebitDocDetailDTO(chargeType, totalAmount, debitDocDetailIds));
        }
        return resultList;
    }

    public Boolean isAllInvoiceStatusClearedForCustomer(Customers customers) {
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (customers.getLcoId() != null) return true;
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> (!x.getBillrunstatus().equalsIgnoreCase("VOID"))).collect(Collectors.toList());
            debitDocuments = debitDocuments.stream().filter(x -> !x.getBillrunstatus().equalsIgnoreCase("Cancelled")).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                for (int i = 0; i < debitDocuments.size(); i++) {
                    DebitDocument document = debitDocuments.get(i);
                    List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(document.getId());
                    List<Integer> creditDocIdList = creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList());
                    List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDocIdList);
                    creditDocuments = creditDocuments.stream().filter(x -> (x.getStatus().equalsIgnoreCase(CommonConstants.PAYMENT_STATUS_PENDDING) || x.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_REJECTED))).collect(Collectors.toList());
                    creditDocIdList = creditDocuments.stream().map(x -> x.getId()).collect(Collectors.toList());

                    creditDocIdList.stream().forEach(id -> {
                        for (int j = 0; j < creditDebitDocMappings.size(); j++) {
                            if (creditDebitDocMappings.get(j).getCreditDocId().equals(id)) {
                                creditDebitDocMappings.remove(creditDebitDocMappings.get(j));
                            }
                        }
                    });

                    Double adjustedAmount = creditDebitDocMappings.stream().filter(x -> x.getAdjustedAmount() != null).mapToDouble(x -> x.getAdjustedAmount()).sum();
                    adjustedAmount = Precision.round(adjustedAmount.doubleValue(), 2);
                    Double totalAmount = Precision.round(document.getTotalamount().doubleValue(), 2);
                    if (Precision.round(adjustedAmount.doubleValue(), 2) != totalAmount) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Double getTransferableCommission(Customers customers, Partner partner) {
        AtomicReference<Double> transferableCommission = new AtomicReference<>(0.0);
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> !x.getIsDirectChargeInvoice()).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                debitDocuments.stream().forEach(x -> {
                    List<CustPlanMappping> mappings = custPlanMappingRepository.findAllByDebitdocid(x.getId());
                    if (mappings != null && !mappings.isEmpty()) {
                        mappings.stream().forEach(data -> {
                            List<PartnerLedgerDetails> detailsList = partnerLedgerDetailsRepository.findAllByDebitDocumentId(x.getId());
                            detailsList = detailsList.stream().filter(y -> (y.getPlanid() == null && y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERT_COMMISSION)) || (y.getPlanid() != null && y.getPlanid().equalsIgnoreCase(data.getPlanId().toString()))).collect(Collectors.toList());

                            if (detailsList != null && !detailsList.isEmpty()) {
                                List<PartnerLedgerDetails> detailsList1 = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
                                List<PartnerLedgerDetails> revertDetailsList = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERT_COMMISSION)).collect(Collectors.toList());

                                Double revertCommission = 0.0;
                                if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                    revertCommission = revertDetailsList.stream().mapToDouble(z -> z.getCommission()).sum();
                                else
                                    revertCommission = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();

                                Double finalRevertCommission = revertCommission;
                                detailsList1.stream().forEach(ledgerDetail -> {
                                    Double commission = 0.0;
                                    if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                        commission = ledgerDetail.getAmount();
                                    else commission = ledgerDetail.getCommission();

                                    commission = commission - finalRevertCommission;
                                    LocalDate planStartDate = data.getStartDate().toLocalDate();
                                    LocalDate planEndDate = data.getEndDate().toLocalDate();
                                    LocalDate todayDate = LocalDate.now();
                                    if (!todayDate.isBefore(planStartDate) && !todayDate.isAfter(planEndDate)) {
                                        Long planDays = ChronoUnit.DAYS.between(planStartDate, planEndDate);
                                        Long remainingDays = ChronoUnit.DAYS.between(todayDate, planEndDate);
                                        if (planDays.intValue() == 0) planDays = 1L;
                                        if (remainingDays.intValue() == 0) remainingDays = 1L;
                                        Double proCommission = (commission / planDays) * remainingDays;
                                        transferableCommission.updateAndGet(v -> v + proCommission);

                                    } else if (todayDate.isBefore(planStartDate)) {
                                        Double finalCommission = commission;
                                        transferableCommission.updateAndGet(v -> v + finalCommission);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return transferableCommission.get();
    }

    public Double getTransferableBalance(Customers customers, Partner partner) {
        AtomicReference<Double> transferableBalance = new AtomicReference<>(0.0);
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> !x.getIsDirectChargeInvoice()).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                debitDocuments.stream().forEach(x -> {
                    List<CustPlanMappping> mappings = custPlanMappingRepository.findAllByDebitdocid(x.getId());
                    if (mappings != null && !mappings.isEmpty()) {
                        mappings.stream().forEach(data -> {
                            List<PartnerLedgerDetails> detailsList = partnerLedgerDetailsRepository.findAllByDebitDocumentId(x.getId());
                            //detailsList=detailsList.stream().filter(y->y.getPlanid().equalsIgnoreCase(data.getPlanId().toString())).collect(Collectors.toList());
                            if (detailsList != null && !detailsList.isEmpty()) {
                                List<PartnerLedgerDetails> detailsList1 = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)).collect(Collectors.toList());
                                List<PartnerLedgerDetails> revertDetailsList = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERSE_BALANCE)).collect(Collectors.toList());

                                Double revertBalance = 0.0;
                                if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                    revertBalance = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();
                                else revertBalance = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();

                                Double finalRevertCommission = revertBalance;
                                detailsList1.stream().forEach(ledgerDetail -> {
                                    Double balance = 0.0;
                                    if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                        balance = ledgerDetail.getAmount();
                                    else balance = ledgerDetail.getAmount();

                                    balance = balance - finalRevertCommission;
                                    LocalDate planStartDate = data.getStartDate().toLocalDate();
                                    LocalDate planEndDate = data.getEndDate().toLocalDate();
                                    LocalDate todayDate = LocalDate.now();
                                    if (!todayDate.isBefore(planStartDate) && !todayDate.isAfter(planEndDate)) {
                                        Long planDays = ChronoUnit.DAYS.between(planStartDate, planEndDate);
                                        Long remainingDays = ChronoUnit.DAYS.between(todayDate, planEndDate);
                                        if (planDays.intValue() == 0) planDays = 1L;
                                        if (remainingDays.intValue() == 0) remainingDays = 1L;
                                        Double proCommission = (balance / planDays) * remainingDays;
                                        transferableBalance.updateAndGet(v -> v + proCommission);

                                    } else if (todayDate.isBefore(planStartDate)) {
                                        Double finalCommission = balance;
                                        transferableBalance.updateAndGet(v -> v + finalCommission);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return transferableBalance.get();
    }

    public List<CustomDebitDocumentDTO> getDebitDocumentsByMvno(Integer mvnoId, Boolean isInvoiceVoid) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        if (mvno.isPresent()) {
            if (mvno.get().getCustInvoiceRefId() != null) {
                List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                if (!CollectionUtils.isEmpty(lastInvoice)) {
                    DebitDocument debitDocument = lastInvoice.get(0);
                    LocalDateTime billDate = debitDocument.getBilldate();
                    if (isInvoiceVoid) return debitDocRepository.findAllByMvnoIdAndBillDate(mvnoId, billDate);
                    else return debitDocRepository.findAllByMvnoIdAndStatusIsNotVoidAndBillDate(mvnoId, billDate);
                } else {
                    if (isInvoiceVoid) return debitDocRepository.findAllByMvnoId(mvnoId);
                    else return debitDocRepository.findAllByMvnoIdAndStatusIsNotVoid(mvnoId);
                }
            } else {
                throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Billing customer not available for Mvno", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mvno Not available..!", null);
        }

    }

    public Double getTotalAmountDebitDocumentsByMvno(Integer mvnoId, Boolean isInvoiceVoid) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        if (mvno.isPresent()) {
            if (mvno.get().getCustInvoiceRefId() != null) {
                List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                if (!CollectionUtils.isEmpty(lastInvoice)) {
                    DebitDocument debitDocument = lastInvoice.get(0);
                    LocalDateTime billDate = debitDocument.getBilldate();
                    if (isInvoiceVoid) return debitDocRepository.getAmountByMvnoIdAndBillDate(mvnoId, billDate);
                    else return debitDocRepository.getAmountByMvnoIdAndStatusIsNotVoidAndBillDate(mvnoId, billDate);
                } else {
                    if (isInvoiceVoid) return debitDocRepository.getAmountByMvnoId(mvnoId);
                    else return debitDocRepository.getAmountByMvnoIdAndStatusIsNotVoid(mvnoId);
                }
            } else {
                throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Billing customer not available for Mvno", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mvno Not available..!", null);
        }

    }

    public List<DebitDocDetailDTO> getDebitDocDetailByChargeAndMvno1(Integer mvnoId, Boolean isInvoiceVoid) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        if (mvno.isPresent()) {
            if (mvno.get().getCustInvoiceRefId() != null) {
                List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                if (!CollectionUtils.isEmpty(lastInvoice)) {
                    DebitDocument debitDocument = lastInvoice.get(0);
                    LocalDateTime billDate = debitDocument.getBilldate();
                    if (isInvoiceVoid)
                        return debitDocRepository.getAmountFromDebitDocdetailByMvnoIdAndBillDate(mvnoId, billDate);
                    else {
                        List<Object[]> results = debitDocRepository.findChargetypeTotalAmount(mvnoId, billDate);
                        List<DebitDocDetailDTO> dtos = new ArrayList<>();

                        for (Object[] result : results) {
                            String chargetype = (String) result[0];
                            Double totalamount = (Double) result[1];
                            dtos.add(new DebitDocDetailDTO(chargetype, totalamount));
                        }
                        return dtos;
                    }
//                        return debitDocRepository.getAmountFromDebitDocdetailByMvnoIdAndStatusIsNotVoidAndBillDate(mvnoId,billDate);
                } else {
                    if (isInvoiceVoid) return debitDocRepository.getAmountFromDebitDocdetailByMvnoId(mvnoId);
                    else return debitDocRepository.getAmountFromDebitDocdetailByMvnoIdAndStatusIsNotVoid(mvnoId);
                }
            } else {
                throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Billing customer not available for Mvno", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mvno Not available..!", null);
        }

    }

    public List<DebitDocDetailDTO> getDebitDocDetailByChargeAndMvno(Integer mvnoId, Boolean isInvoiceVoid, LocalDateTime fromDate, LocalDateTime toDate) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        if (mvno.isPresent()) {
            if (mvno.get().getCustInvoiceRefId() != null) {
                if (toDate == null) {
                    toDate = LocalDate.now().atTime(LocalTime.MAX);
                }
                if (fromDate == null) {
                    List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                    if (!CollectionUtils.isEmpty(lastInvoice)) {
                        fromDate = lastInvoice.get(0).getBilldate().toLocalDate().atStartOfDay();
                    } else {
                        fromDate = mvno.get().getCreatedate();
                    }
                }
                List<DebitDocDetailDTO> debitDocDetails = debitDocRepository.getDebitDocDTOByMvnoAndBillDate(mvnoId, fromDate, toDate);
                return convertToRequiredResponse(debitDocDetails);
            } else {
                throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Billing customer not available for Mvno", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mvno Not available..!", null);
        }

    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest;
        Map<String, String> sortColMap = new HashMap<>();
        Integer MAX_PAGE_SIZE = Integer.parseInt(clientServiceRepository.getClientServiceByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }

    public void validate(DebitDocument debitDocument1) {

        try {
            Integer integer = debitDocRepository.lastInvoiceForCancelAndRegen(debitDocument1.getCustomer().getId());
            if (!integer.equals(debitDocument1.getId())) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only latest invoice can be regeneate and cancelled", null);
            }
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(ce.getErrCode(), ce.getMessage(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Double getWalletBalanceForCaf(List<TrialDebitDocument> trialDebitDocumentList) {
        Double walletBalance = 0.0;
        if (trialDebitDocumentList != null && trialDebitDocumentList.size() > 0) {
            walletBalance = -trialDebitDocumentList.stream().mapToDouble(x -> x.getTotalamount() - (x.getAdjustedAmount() != null ? x.getAdjustedAmount() : 0.0)).sum();
            System.out.println("::::::::::::::::::::::::::Initiation of wallet settlement action ::::::::::::::::::::::::::::::");
            // code for manual payment adjustment check
            if (walletBalance < 0) {
                System.out.println(":::::::::::::::::Checking wallet settlement for manual adjustment ::::::::::::::::::::");
                List<CreditDocument> creditDocumentList = creditDocRepository.findByCustomerIdAndStatusIn(trialDebitDocumentList.get(0).getCustomer().getId(), Arrays.asList(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED, CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED, CommonConstants.CREDIT_DOC_STATUS.APPROVED));
                Double totalCreditedAmount = creditDocumentList.stream().mapToDouble(x -> x.getAmount() - x.getAdjustedAmount()).sum();
                if (totalCreditedAmount != null && totalCreditedAmount > 0) {
                    walletBalance = -trialDebitDocumentList.stream().mapToDouble(x -> x.getTotalamount() - (totalCreditedAmount != null ? totalCreditedAmount : 0.0)).sum();
                    System.out.println(":::::::::::::::: walletBalance:- " + walletBalance + " " + "totalCreditedAmount: -" + totalCreditedAmount + " ::::::::::::::::::::::::::::::::::");
                }
            }
            System.out.println(":::::::::::::::::::::::::::::::::: Completion of wallet settlement action ::::::::::::::::::::::::::::::::::::::::");
            return walletBalance;
        } else {
            System.out.println(":::::::::::::::::::::::::::::::::: No Trial Debit Document Found !! while performing wallet settlement action hence wallet amount returning : 0.00 ::::::::::::::::::::::::::::::::::::::::");
            return walletBalance;
        }

    }

    public void updateStatusCode(IspMainPayload dataMessage) {
        try {
            if (dataMessage != null && dataMessage.getInvoiceIds() != null && !dataMessage.getInvoiceIds().isEmpty()) {
                List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(dataMessage.getInvoiceIds());
                if (debitDocuments != null && !debitDocuments.isEmpty()) {
                    debitDocuments.stream().forEach(debitDocument -> {
                        debitDocument.setIspPayloadStatusCode(dataMessage.getResponseCode());
                    });
                    debitDocRepository.saveAll(debitDocuments);
                }
            }
        } catch (Exception e) {

        }
    }

    @Transactional
    public void updateDebitDocuments(List<DebitDocumentDTOForAdjustment> debitDocs) {
        if (debitDocs.isEmpty()) return;

        Map<Integer, Double> idToAdjustedAmount = debitDocs.stream().collect(Collectors.toMap(DebitDocumentDTOForAdjustment::getId, DebitDocumentDTOForAdjustment::getAdjustedAmount));

        entityManager.createQuery("UPDATE DebitDocument d SET d.adjustedAmount = CASE d.id " + idToAdjustedAmount.entrySet().stream().map(entry -> "WHEN " + entry.getKey() + " THEN " + entry.getValue()).collect(Collectors.joining(" ")) + " ELSE d.adjustedAmount END WHERE d.id IN (:ids)").setParameter("ids", idToAdjustedAmount.keySet()).executeUpdate();
    }

    public GenericDataDTO getLatestUnpaidInvoiceByUsername(String username) {
        GenericDataDTO dto = new GenericDataDTO();

        Optional<Customers> customerOpt = customersRepository.findByUsername(username);
        if (!customerOpt.isPresent()) {
            dto.setResponseCode(HttpStatus.NOT_FOUND.value());
            dto.setResponseMessage("Customer with username '" + username + "' not found");
            dto.setData(null);
            return dto;
        }

        Integer custId = customerOpt.get().getId();

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdate"));
        List<DebitDocument> unpaidInvoices = debitDocRepository.findUnpaidInvoicesBySubscriberId(custId, pageable);

        if (unpaidInvoices.isEmpty()) {
            dto.setResponseCode(HttpStatus.NO_CONTENT.value());
            dto.setResponseMessage("No unpaid invoice found for username: " + username);
            dto.setData(null);
            dto.setTotalRecords(0);
            return dto;
        }

        DebitDocument latestInvoice = unpaidInvoices.get(0);

        // ✅ Only required invoice fields in API response
        Map<String, Object> invoiceData = new LinkedHashMap<>();
        invoiceData.put("invoiceId", latestInvoice.getId());
        invoiceData.put("invoiceNumber", latestInvoice.getDocnumber());
        invoiceData.put("billDate", latestInvoice.getBilldate());
        invoiceData.put("startDate", latestInvoice.getStartdate());
        invoiceData.put("endDate", latestInvoice.getEndate());
        invoiceData.put("dueDate", latestInvoice.getDuedate());
        invoiceData.put("subTotal", latestInvoice.getSubtotal());
        invoiceData.put("tax", latestInvoice.getTax());
        invoiceData.put("totalAmount", latestInvoice.getTotalamount());
        invoiceData.put("totalDue", latestInvoice.getTotaldue());
        invoiceData.put("paymentStatus", latestInvoice.getPaymentStatus());
        invoiceData.put("billTo", latestInvoice.getBillableToName());
        invoiceData.put("status", latestInvoice.getStatus());

        dto.setResponseCode(HttpStatus.OK.value());
        dto.setResponseMessage("Latest unpaid invoice fetched successfully");
        dto.setData(invoiceData);
        dto.setTotalRecords(1);

        return dto;
    }

    public GenericDataDTO getDueDateAndTotalAmountByAcctno(String acctno) {
        GenericDataDTO response = new GenericDataDTO();

        List<Customers> customers = customersRepository.findByAcctno(acctno);

        if (customers.isEmpty()) {
            response.setResponseCode(HttpStatus.NOT_FOUND.value());
            response.setResponseMessage("Customer not found for acctno: " + acctno);
            response.setData(null);
            return response;
        }

        if (customers.size() > 1) {
            response.setResponseCode(HttpStatus.CONFLICT.value());
            response.setResponseMessage("Multiple customers found for account number: " + acctno + ". Please specify the exact customer.");
            response.setData(null);
            return response;
        }

        Customers customer = customers.get(0);
        Integer custId = customer.getId();

        List<DebitDocument> debitDocuments = debitDocRepository.findByCustomer_Id(custId);

        if (debitDocuments.isEmpty()) {
            response.setResponseCode(HttpStatus.NOT_FOUND.value());
            response.setResponseMessage("No DebitDocument found for customer id: " + custId);
            response.setData(null);
            return response;
        }

        if (debitDocuments.size() > 1) {
            response.setResponseCode(HttpStatus.CONFLICT.value());
            response.setResponseMessage("Multiple DebitDocuments found for customer id: " + custId + ". Please specify which one should be used.");
            response.setData(null);
            return response;
        }

        // ✅ Single DebitDocument Found
        DebitDocument debitDocument = debitDocuments.get(0);

        // Yeh object paymentDetails json banayega
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("dueDate", debitDocument.getDuedate());
        paymentDetails.put("totalAmount", debitDocument.getTotalamount());

        response.setResponseCode(HttpStatus.OK.value());
        response.setResponseMessage("Due date and total amount fetched successfully");
        response.setData(paymentDetails);

        return response;
    }

    public GenericDataDTO getAllDueDateAndTotalAmountByAcctno(String acctno) {
        GenericDataDTO response = new GenericDataDTO();
        List<Customers> customers = customersRepository.findByAcctno(acctno);
        if (customers.isEmpty()) {
            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Customer not found for acctno: " + acctno);
            response.setDataList(null);
            return response;
        }
        Customers customer = customers.get(0);
        Integer custId = customer.getId();
        List<DebitDocument> debitDocuments = debitDocRepository.findByCustomer_Id(custId);
        if (debitDocuments.isEmpty()) {
            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("No DebitDocument found for customer id: " + custId);
            response.setDataList(null);
            return response;
        }
        List<Map<String, Object>> paymentDataDetailsList = new ArrayList<>();
        for (DebitDocument debitDocument : debitDocuments) {
            Map<String, Object> paymentDetails = new HashMap<>();
            paymentDetails.put("debitDocumentId", debitDocument.getId());
            paymentDetails.put("debitDocumentNumber", debitDocument.getDocnumber());
            paymentDetails.put("dueDate", debitDocument.getDuedate());
            paymentDetails.put("totalAmount", debitDocument.getTotalamount());
            paymentDataDetailsList.add(paymentDetails);
        }
        response.setResponseCode(HttpStatus.OK.value());
        response.setResponseMessage("Due dates and total amounts fetched successfully");
        response.setDataList(paymentDataDetailsList);
        return response;
    }
}
