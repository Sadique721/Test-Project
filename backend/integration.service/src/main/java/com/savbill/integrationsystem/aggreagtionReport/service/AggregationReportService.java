package com.savbill.integrationsystem.aggreagtionReport.service;

import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.QReverseBusinessPromotionFinalData;
import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.QReverseBusinessPromotionRawData;
import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.ReverseBusinessPromotionFinalData;
import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.ReverseBusinessPromotionFinalDataRepository;
import com.savbill.integrationsystem.billgen.entity.*;
import com.savbill.integrationsystem.billgen.entity.BillGenFinalData;
import com.savbill.integrationsystem.billgen.entity.BillGenRawData;
import com.savbill.integrationsystem.billgen.entity.CreditNoteFinalData;
import com.savbill.integrationsystem.billgen.entity.CreditNoteGenRawData;
import com.savbill.integrationsystem.billgen.mapper.BillGenMapper;
import com.savbill.integrationsystem.billgen.repository.BillGenFinalDataRepo;
import com.savbill.integrationsystem.billgen.repository.BillGenRawDataRepository;
import com.savbill.integrationsystem.billgen.repository.CreditNoteFinalDataRepository;
import com.savbill.integrationsystem.billgen.repository.CreditNoteGenRawDataRepository;
import com.savbill.integrationsystem.businessPromotion.entity.*;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionFinalData;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionFinalDataRepository;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionRawData;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionRawDataRepository;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.utillity.Helper;
import com.savbill.integrationsystem.navmaster.entity.NAVMaster;
import com.savbill.integrationsystem.navmaster.entity.NAVMasterAggregationParamMapping;
import com.savbill.integrationsystem.navmaster.model.NAVMasterDTO;
import com.savbill.integrationsystem.navmaster.service.NAVMasterService;
import com.savbill.integrationsystem.paymentgen.entity.*;
import com.savbill.integrationsystem.paymentgen.entity.PaymentGenFinalData;
import com.savbill.integrationsystem.paymentgen.entity.PaymentGenFinalDataRepository;
import com.savbill.integrationsystem.paymentgen.entity.PaymentGenRawData;
import com.savbill.integrationsystem.paymentgen.entity.repository.PaymentGenRawDataRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AggregationReportService {

    public String getModuleNameForLog() {
        return "AggregationReportService[]";
    }

    @Autowired
    NAVMasterService navMasterService;

    @Autowired
    EntityManager entityManager;


    @Autowired
    BillGenFinalDataRepo billGenFinalDataRepo;
    @Autowired
    private BillGenRawDataRepository billGenRawDataRepository;

    @Autowired
    BillGenMapper billGenMapper;


    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    private CreditNoteFinalDataRepository creditNoteFinalDataRepository;
    @Autowired
    private CreditNoteGenRawDataRepository creditNoteGenRawDataRepository;
    @Autowired
    private BusinessPromotionFinalDataRepository businessPromotionFinalDataRepository;
    @Autowired
    private BusinessPromotionRawDataRepository businessPromotionRawDataRepository;
    @Autowired
    private ReverseBusinessPromotionFinalDataRepository reverseBusinessPromotionFinalDataRepository;
    @Autowired
    private PaymentGenFinalDataRepository paymentGenFinalDataRepository;
    @Autowired
    private PaymentGenRawDataRepo paymentGenRawDataRepo;


    public GenericDataDTO fetchAggregationReport(String startDate, String endDate, Long navMasterId, PaginationRequestDTO paginationRequestDTO, Long mvnoId) {
        try {
            NAVMasterDTO navMaster = navMasterService.getEntityById(navMasterId, mvnoId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
            JPAQuery<?> jpaQuery = new JPAQuery<>(entityManager);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            LocalDate fromDate = LocalDate.parse(startDate, formatter);
            LocalDate toDate = LocalDate.parse(endDate, formatter);
            if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BILLGEN)) {
                getBillGenAggregationReport(jpaQuery, genericDataDTO, fromDate, toDate, navMaster);
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.CREDITNOTE)) {
                getCreditNoteAggregationReport(jpaQuery, genericDataDTO, fromDate, toDate, navMaster);
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BUSINESSPR)) {
                getBusinessPromotionAggregationReport(jpaQuery, genericDataDTO, fromDate, toDate, navMaster);
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.REBUSINESSPR)) {
                getBusinessPromotionAggregationReport(jpaQuery, genericDataDTO, fromDate, toDate, navMaster);
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.RCPT)) {
                getPaymentAggregationReport(jpaQuery, genericDataDTO, fromDate, toDate, navMaster);
            }
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }

    }


    private GenericDataDTO getBillGenAggregationReport(JPAQuery<?> jpaQuery, GenericDataDTO genericDataDTO, LocalDate fromDate, LocalDate toDate, NAVMasterDTO navMaster) {
        try {
            QBillGenRawData billGenRawData = QBillGenRawData.billGenRawData;
            BooleanExpression booleanExpression = null;
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            booleanExpression = billGenRawData.isNotNull().and(billGenRawData.addedDate.between(fromDate, toDate)).and(billGenRawData.isPushed.eq(false));
            jpaQuery.groupBy(billGenRawData.transactionType);
            for (NAVMasterAggregationParamMapping aggregationParam : navMaster.getNavMasterAggregationParamMappingList()) {
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BUSINESS_UNIT)) {
                    jpaQuery.groupBy(billGenRawData.businessCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.DATE_WISE)) {
                    jpaQuery.groupBy(billGenRawData.addedDate);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.IC)) {
                    jpaQuery.groupBy(billGenRawData.ICCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BRANCH_CODE)) {
                    jpaQuery.groupBy(billGenRawData.branchCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.NAV_LEDGER_ID)) {
                    jpaQuery.groupBy(billGenRawData.pushableLedgerId);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.SERVICE_AREA)) {
                    jpaQuery.groupBy(billGenRawData.serviceAreaId);
                    jpaQuery.groupBy(qServiceArea.name);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.POP)) {
                    jpaQuery.groupBy(billGenRawData.pop);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.OLT)) {
                    jpaQuery.groupBy(billGenRawData.olt);
                }
            }

            List<Tuple> billGenFinalDataList = jpaQuery.select(billGenRawData.addedDate, billGenRawData.businessCode, billGenRawData.ICCode, billGenRawData.branchCode, billGenRawData.serviceAreaId, billGenRawData.count(), billGenRawData.amount.sum(), billGenRawData.transactionType, qServiceArea.name, billGenRawData.pushableLedgerId, billGenRawData.olt, billGenRawData.pop).from(billGenRawData).leftJoin(qServiceArea).on(qServiceArea.id.eq(billGenRawData.serviceAreaId.longValue())).orderBy(billGenRawData.addedDate.asc()).where(booleanExpression).fetch();
            List<BillGenFinalData> billGenFinalData = new ArrayList<>();
            for (Tuple tuple : billGenFinalDataList) {
                billGenFinalData.add(new BillGenFinalData(tuple.get(billGenRawData.addedDate), tuple.get(billGenRawData.businessCode), tuple.get(billGenRawData.ICCode), tuple.get(billGenRawData.branchCode), "", tuple.get(billGenRawData.serviceAreaId), tuple.get(billGenRawData.count()), tuple.get(billGenRawData.amount.sum()), tuple.get(billGenRawData.transactionType), tuple.get(qServiceArea.name), tuple.get(billGenRawData.pushableLedgerId), tuple.get(billGenRawData.olt), tuple.get(billGenRawData.pop)));
            }
            if (billGenFinalDataList.size() > 0) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            }
            genericDataDTO.setDataList(billGenFinalData);
            genericDataDTO.setTotalRecords(billGenFinalData.size());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    @Transactional
    public GenericDataDTO push(List<Object> billGenFinalDataList, Long navMasterId, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            NAVMasterDTO navMaster = navMasterService.getEntityById(navMasterId, mvnoId);
//            String documentNumber;
            JPAQuery<?> jpaQuery = new JPAQuery<>(entityManager);
            int responseCode = 0;
            for (int i = 0; i < billGenFinalDataList.size(); i++) {
                ObjectMapper objectMapper = new ObjectMapper();
                if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BILLGEN)) {
                    BillGenFinalData billGenFinalData = objectMapper.convertValue(billGenFinalDataList.get(i), BillGenFinalData.class);
                    BillGenFinalData existingDateData = billGenFinalDataRepo.findFirstByAddedDateAndIsPushedTrue(billGenFinalData.getAddedDate());
                    if (existingDateData != null) {
                        billGenFinalData.setDocumentNumber(existingDateData.getDocumentNumber());
                    } else {
                        billGenFinalData.setDocumentNumber(getBillGenBatchNumber(navMaster.getBatchName()));
                    }
                    billGenFinalData = billGenFinalDataRepo.save(billGenFinalData);
                    if (sendHTTPRequest(billGenFinalData.getSerialNumber(), billGenFinalData.getAddedDate(), billGenFinalData.getDocumentNumber(), billGenFinalData.getPushableLedgerId(), billGenFinalData.getAmount(), billGenFinalData.getBranchCode(), billGenFinalData.getICCode(), billGenFinalData.getBusinessCode(), navMaster) == HttpStatus.CREATED.value()) {
                        billGenFinalData.setIsPushed(true);
                        billGenFinalDataRepo.save(billGenFinalData);
                        updateRawDateBillGen(billGenFinalData);
                    }
                } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.CREDITNOTE)) {
                    CreditNoteFinalData creditNoteFinalData = objectMapper.convertValue(billGenFinalDataList.get(i), CreditNoteFinalData.class);
                    CreditNoteFinalData existingDateData = creditNoteFinalDataRepository.findFirstByAddedDateAndIsPushedTrue(creditNoteFinalData.getAddedDate());
                    if (existingDateData != null) {
                        creditNoteFinalData.setDocumentNumber(existingDateData.getDocumentNumber());
                    } else {
                        creditNoteFinalData.setDocumentNumber(getBillGenBatchNumber(navMaster.getBatchName()));
                    }
                    creditNoteFinalData = creditNoteFinalDataRepository.save(creditNoteFinalData);
                    if (sendHTTPRequest(creditNoteFinalData.getSerialNumber(), creditNoteFinalData.getAddedDate(), existingDateData.getDocumentNumber(), creditNoteFinalData.getNAVLedgerId(), creditNoteFinalData.getAmount(), creditNoteFinalData.getBranchCode(), creditNoteFinalData.getICCode(), creditNoteFinalData.getBusinessCode(), navMaster) == HttpStatus.CREATED.value()) {
                        creditNoteFinalData.setIsPushed(true);
                        creditNoteFinalDataRepository.save(creditNoteFinalData);
                        updateRawDateCreditNote(creditNoteFinalData);
                    }
                } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BUSINESSPR)) {
                    BusinessPromotionFinalData businessPromotionFinalData = objectMapper.convertValue(billGenFinalDataList.get(i), BusinessPromotionFinalData.class);
                    BusinessPromotionFinalData existingDateData = businessPromotionFinalDataRepository.findFirstByAddedDateAndIsPushedTrue(businessPromotionFinalData.getAddedDate());
                    if (existingDateData != null) {
                        businessPromotionFinalData.setDocumentNumber(existingDateData.getDocumentNumber());
                    } else {
                        businessPromotionFinalData.setDocumentNumber(getBillGenBatchNumber(navMaster.getBatchName()));
                    }
                    businessPromotionFinalData = businessPromotionFinalDataRepository.save(businessPromotionFinalData);
                    if (sendHTTPRequest(businessPromotionFinalData.getSerialNumber(), businessPromotionFinalData.getAddedDate(), businessPromotionFinalData.getDocumentNumber(), businessPromotionFinalData.getPushableLedgerId(), businessPromotionFinalData.getAmount(), businessPromotionFinalData.getBranchCode(), businessPromotionFinalData.getICCode(), businessPromotionFinalData.getBusinessCode(), navMaster) == HttpStatus.CREATED.value()) {
                        businessPromotionFinalData.setIsPushed(true);
                        businessPromotionFinalDataRepository.save(businessPromotionFinalData);
                        updateRawDataBusinessPromotion(businessPromotionFinalData);
                    }
                } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.REBUSINESSPR)) {
                    ReverseBusinessPromotionFinalData reverseBusinessPromotionFinalData = objectMapper.convertValue(billGenFinalDataList.get(i), ReverseBusinessPromotionFinalData.class);
                    ReverseBusinessPromotionFinalData existingDateData = reverseBusinessPromotionFinalDataRepository.findFirstByAddedDateAndIsPushedTrue(reverseBusinessPromotionFinalData.getAddedDate());
                    if (existingDateData != null) {
                        reverseBusinessPromotionFinalData.setDocumentNumber(existingDateData.getDocumentNumber());
                    } else {
                        reverseBusinessPromotionFinalData.setDocumentNumber(getBillGenBatchNumber(navMaster.getBatchName()));
                    }
                    reverseBusinessPromotionFinalData = reverseBusinessPromotionFinalDataRepository.save(reverseBusinessPromotionFinalData);
                    if (sendHTTPRequest(reverseBusinessPromotionFinalData.getSerialNumber(), reverseBusinessPromotionFinalData.getAddedDate(), reverseBusinessPromotionFinalData.getDocumentNumber(), reverseBusinessPromotionFinalData.getNAVLedgerId(), reverseBusinessPromotionFinalData.getAmount(), reverseBusinessPromotionFinalData.getBranchCode(), reverseBusinessPromotionFinalData.getICCode(), reverseBusinessPromotionFinalData.getBusinessCode(), navMaster) == HttpStatus.CREATED.value()) {
                        reverseBusinessPromotionFinalDataRepository.save(reverseBusinessPromotionFinalData);
                        updateRawDataReverseBusinessPromotion(reverseBusinessPromotionFinalData);
                    }
                } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.RCPT)) {
                    PaymentGenFinalData paymentGenFinalData = objectMapper.convertValue(billGenFinalDataList.get(i), PaymentGenFinalData.class);
                    PaymentGenFinalData existingDateData = paymentGenFinalDataRepository.findFirstByPaymentDateAndIsPushedTrue(paymentGenFinalData.getPaymentDate());
                    if (existingDateData != null) {
                        paymentGenFinalData.setDocumentNumber(existingDateData.getDocumentNumber());
                    } else {
                        paymentGenFinalData.setDocumentNumber(getBillGenBatchNumber(navMaster.getBatchName()));
                    }
                    paymentGenFinalData = paymentGenFinalDataRepository.save(paymentGenFinalData);
                    if (sendHTTPRequest(paymentGenFinalData.getSerialNumber(), paymentGenFinalData.getPaymentDate(), paymentGenFinalData.getDocumentNumber(), paymentGenFinalData.getNAVLedgerId(), paymentGenFinalData.getAmount(), paymentGenFinalData.getBranchCode(), paymentGenFinalData.getICCode(), paymentGenFinalData.getBusinessCode(), navMaster) == HttpStatus.CREATED.value()) {
                        paymentGenFinalData.setIsPushed(true);
                        paymentGenFinalDataRepository.save(paymentGenFinalData);
                        updateRawDataReceiptEntry(paymentGenFinalData);
                    }
                }

            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Pushed data successfully.");
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    private void updateRawDateBillGen(BillGenFinalData billGenFinalData) {
        QBillGenRawData billGenRawData = QBillGenRawData.billGenRawData;
        BooleanExpression booleanExpression = billGenRawData.isNotNull().and(billGenRawData.isPushed.eq(false));
        if (billGenFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.serviceAreaId.eq(billGenFinalData.getServiceAreaId()));
        }
        if (billGenFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.branchCode.equalsIgnoreCase(billGenFinalData.getBranchCode()));
        }
        if (billGenFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.businessCode.equalsIgnoreCase(billGenFinalData.getBusinessCode()));
        }
        if (billGenFinalData.getPushableLedgerId() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.pushableLedgerId.equalsIgnoreCase(billGenFinalData.getPushableLedgerId()));
        }
        if (billGenFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.ICCode.equalsIgnoreCase(billGenFinalData.getICCode()));
        }
        if (billGenFinalData.getAddedDate() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.addedDate.eq(billGenFinalData.getAddedDate()));
        }
        if (billGenFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.transactionType.equalsIgnoreCase(billGenFinalData.getTransactionType()));
        }
        if (billGenFinalData.getOlt() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.olt.equalsIgnoreCase(billGenFinalData.getOlt()));
        }
        if (billGenFinalData.getPop() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.pop.equalsIgnoreCase(billGenFinalData.getPop()));
        }
        Iterable<BillGenRawData> billGenRawDataList = billGenRawDataRepository.findAll(booleanExpression);
        for (BillGenRawData billGenRawData1 : billGenRawDataList) {
            billGenRawData1.setIsPushed(true);
            billGenRawData1.setSerialNumberBillGenFinal(billGenFinalData.getSerialNumber());
            billGenRawDataRepository.save(billGenRawData1);
        }
    }

    private void updateRawDateCreditNote(CreditNoteFinalData creditNoteFinalData) {
        QCreditNoteGenRawData qCreditNoteGenRawData = QCreditNoteGenRawData.creditNoteGenRawData;
        BooleanExpression booleanExpression = qCreditNoteGenRawData.isNotNull().and(qCreditNoteGenRawData.isPushed.eq(false));
        if (creditNoteFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.serviceAreaId.eq(creditNoteFinalData.getServiceAreaId()));
        }
        if (creditNoteFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.branchCode.equalsIgnoreCase(creditNoteFinalData.getBranchCode()));
        }
        if (creditNoteFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.businessCode.equalsIgnoreCase(creditNoteFinalData.getBusinessCode()));
        }
        if (creditNoteFinalData.getPushableLedgerId() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.pushableLedgerId.equalsIgnoreCase(creditNoteFinalData.getPushableLedgerId()));
        }
        if (creditNoteFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.ICCode.equalsIgnoreCase(creditNoteFinalData.getICCode()));
        }
        if (creditNoteFinalData.getAddedDate() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.addedDate.eq(creditNoteFinalData.getAddedDate()));
        }
        if (creditNoteFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.transactionType.equalsIgnoreCase(creditNoteFinalData.getTransactionType()));
        }
        Iterable<CreditNoteGenRawData> creditNoteGenRawDataList = creditNoteGenRawDataRepository.findAll(booleanExpression);
        for (CreditNoteGenRawData creditNoteGenRawData : creditNoteGenRawDataList) {
            creditNoteGenRawData.setIsPushed(true);
            creditNoteGenRawData.setSerialNumberCreditNotFinal(creditNoteFinalData.getSerialNumber());
            creditNoteGenRawDataRepository.save(creditNoteGenRawData);
        }
    }

    public String getBillGenBatchNumber(String batchName) {
        return null;
    }


    public GenericDataDTO fetchAggregationPushedReport(PaginationRequestDTO paginationRequestDTO, String startDate, String endDate, Long navMasterId, Long mvnoId) {
        try {
            NAVMasterDTO navMaster = navMasterService.getEntityById(navMasterId, mvnoId);
            PageRequest pageRequest = navMasterService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.RCPT) ? "paymentDate" : "addedDate", 0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BILLGEN)) {
                getBillGenPushedAggregationReport(genericDataDTO, pageRequest, LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.CREDITNOTE)) {
                getCreditNotePushedAggregationReport(genericDataDTO, pageRequest, LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BUSINESSPR)) {
                getBusinessPromotionPushedAggregationReport(genericDataDTO, pageRequest, LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.REBUSINESSPR)) {
                getReverseBusinessPromotionPushedAggregationReport(genericDataDTO, pageRequest, LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
            } else if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.RCPT)) {
                getReceiptPushedAggregationReport(genericDataDTO, pageRequest, LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
            }
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    private void getReverseBusinessPromotionPushedAggregationReport(GenericDataDTO genericDataDTO, PageRequest pageRequest, LocalDate startDate, LocalDate endDate) {
        QReverseBusinessPromotionFinalData qReverseBusinessPromotionFinalData = QReverseBusinessPromotionFinalData.reverseBusinessPromotionFinalData;
        BooleanExpression booleanExpression = qReverseBusinessPromotionFinalData.isNotNull().and(qReverseBusinessPromotionFinalData.isPushed.eq(true)).and(qReverseBusinessPromotionFinalData.addedDate.between(startDate, endDate));
        Page<ReverseBusinessPromotionFinalData> paginationList = reverseBusinessPromotionFinalDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        if (genericDataDTO.getDataList().size() > 0) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {

            genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }

        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }

    private void getBusinessPromotionPushedAggregationReport(GenericDataDTO genericDataDTO, PageRequest pageRequest, LocalDate startDate, LocalDate endDate) {
        QBusinessPromotionFinalData businessPromotionFinalData = QBusinessPromotionFinalData.businessPromotionFinalData;
        BooleanExpression booleanExpression = businessPromotionFinalData.isNotNull().and(businessPromotionFinalData.isPushed.eq(true)).and(businessPromotionFinalData.addedDate.between(startDate, endDate));
        Page<BusinessPromotionFinalData> paginationList = businessPromotionFinalDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        if (genericDataDTO.getDataList().size() > 0) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {

            genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }

        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }

    private void getCreditNotePushedAggregationReport(GenericDataDTO genericDataDTO, PageRequest pageRequest, LocalDate startDate, LocalDate endDate) {
        QCreditNoteFinalData qCreditNoteFinalData = QCreditNoteFinalData.creditNoteFinalData;
        BooleanExpression booleanExpression = qCreditNoteFinalData.isNotNull().and(qCreditNoteFinalData.isPushed.eq(true)).and(qCreditNoteFinalData.addedDate.between(startDate, endDate));
        Page<CreditNoteFinalData> paginationList = creditNoteFinalDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        if (genericDataDTO.getDataList().size() > 0) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {

            genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }

        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }

    private void getBillGenPushedAggregationReport(GenericDataDTO genericDataDTO, PageRequest pageRequest, LocalDate startDate, LocalDate endDate) {
        QBillGenFinalData billGenFinalData = QBillGenFinalData.billGenFinalData;
        BooleanExpression booleanExpression = billGenFinalData.isNotNull().and(billGenFinalData.isPushed.eq(true)).and(billGenFinalData.addedDate.between(startDate, endDate));
        Page<BillGenFinalData> paginationList = billGenFinalDataRepo.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        if (genericDataDTO.getDataList().size() > 0) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {
            genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }

    public GenericDataDTO getRawDataOfFinalData(PaginationRequestDTO paginationRequestDTO, Object obj, NAVMaster navMaster, boolean isPushed) {
        PageRequest pageRequest = navMasterService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "addedDate", 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BILLGEN)) {
            getRawDataBillGen(genericDataDTO, objectMapper.convertValue(obj, BillGenFinalData.class), pageRequest, isPushed);
        }
        if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.BUSINESSPR)) {
            getRawDataBusinessPromotion(genericDataDTO, objectMapper.convertValue(obj, BusinessPromotionFinalData.class), pageRequest, isPushed);
        }
        if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.CREDITNOTE)) {
            getRawDataCredirNote(genericDataDTO, objectMapper.convertValue(obj, CreditNoteFinalData.class), pageRequest, isPushed);
        }
        if (navMaster.getBatchName().equalsIgnoreCase(CommonConstant.NAV_BATCH_NAME.RCPT)) {
            getRawDataPayment(genericDataDTO, objectMapper.convertValue(obj, PaymentGenFinalData.class), pageRequest, isPushed);
        }
        return genericDataDTO;
    }


    private GenericDataDTO getCreditNoteAggregationReport(JPAQuery<?> jpaQuery, GenericDataDTO genericDataDTO, LocalDate fromDate, LocalDate toDate, NAVMasterDTO navMaster) {
        try {
            QCreditNoteGenRawData qCreditNoteGenRawData = QCreditNoteGenRawData.creditNoteGenRawData;
            BooleanExpression booleanExpression = null;
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            booleanExpression = qCreditNoteGenRawData.isNotNull().and(qCreditNoteGenRawData.addedDate.between(fromDate, toDate)).and(qCreditNoteGenRawData.isPushed.eq(false));
            jpaQuery.groupBy(qCreditNoteGenRawData.transactionType);
            for (NAVMasterAggregationParamMapping aggregationParam : navMaster.getNavMasterAggregationParamMappingList()) {
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BUSINESS_UNIT)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.businessCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.DATE_WISE)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.addedDate);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.IC)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.ICCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BRANCH_CODE)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.branchCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.NAV_LEDGER_ID)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.pushableLedgerId);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.SERVICE_AREA)) {
                    jpaQuery.groupBy(qCreditNoteGenRawData.serviceAreaId);
                    jpaQuery.groupBy(qServiceArea.name);
                }
            }

            List<Tuple> billGenFinalDataList = jpaQuery.select(qCreditNoteGenRawData.addedDate, qCreditNoteGenRawData.businessCode, qCreditNoteGenRawData.ICCode, qCreditNoteGenRawData.branchCode, qCreditNoteGenRawData.serviceAreaId, qCreditNoteGenRawData.count(), qCreditNoteGenRawData.amount.sum(), qCreditNoteGenRawData.transactionType, qServiceArea.name, qCreditNoteGenRawData.pushableLedgerId).from(qCreditNoteGenRawData).leftJoin(qServiceArea).on(qServiceArea.id.eq(qCreditNoteGenRawData.serviceAreaId.longValue())).orderBy(qCreditNoteGenRawData.addedDate.asc()).where(booleanExpression).fetch();
            List<CreditNoteFinalData> creditNoteFinalData = new ArrayList<>();
            for (Tuple tuple : billGenFinalDataList) {
                creditNoteFinalData.add(new CreditNoteFinalData(tuple.get(qCreditNoteGenRawData.addedDate), tuple.get(qCreditNoteGenRawData.businessCode), tuple.get(qCreditNoteGenRawData.ICCode), tuple.get(qCreditNoteGenRawData.branchCode), "", tuple.get(qCreditNoteGenRawData.serviceAreaId), tuple.get(qCreditNoteGenRawData.count()), tuple.get(qCreditNoteGenRawData.amount.sum()), tuple.get(qCreditNoteGenRawData.transactionType), tuple.get(qServiceArea.name), tuple.get(qCreditNoteGenRawData.pushableLedgerId)));
            }
            if (billGenFinalDataList.size() > 0) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            }
            genericDataDTO.setDataList(creditNoteFinalData);
            genericDataDTO.setTotalRecords(creditNoteFinalData.size());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    public int sendHTTPRequest(Long serialNumber, LocalDate addedDate, String documentNumber, String NAVLedgerId, Double amount, String branchCode, String icCode, String buCode, NAVMasterDTO navMaster) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Journal_Batch_Name", navMaster.getBatchName());
            jsonObject.put("Line_No", serialNumber);
            jsonObject.put("Posting_Date", addedDate);
//            jsonObject.put("Document_No", documentNumber);
            jsonObject.put("Account_Type", "G/L Account");
            jsonObject.put("Account_No", NAVLedgerId);
            jsonObject.put("Amount", amount);
            jsonObject.put("Branch_Franchise_Code", branchCode);
            jsonObject.put("Cost_Center_Code", "CC131");
            jsonObject.put("Investment_Code", icCode);
            jsonObject.put("Businesunit_Code", buCode);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(navMaster.getUserName(), navMaster.getPwd());
            Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, BasicScheme.authenticate(credentials, "UTF-8", false).getValue());
            HttpUriRequest httpGetForAccessToken = RequestBuilder.post().setUri(navMaster.getUrl()).setHeader(header).setEntity(new StringEntity(String.valueOf(jsonObject), ContentType.APPLICATION_JSON)).build();
            CloseableHttpResponse result = null;
            result = client.execute(httpGetForAccessToken);
            HttpEntity entity = result.getEntity();
            String content = EntityUtils.toString(entity);
            JSONObject response = (JSONObject) new JSONObject(content);
            JSONObject request = (JSONObject) new JSONObject(jsonObject);
            System.out.println("======================================================Request.================================================================\n");
            System.out.println("Request : " + request);
            System.out.println("======================================================Request End.================================================================\n");
            System.out.println("======================================================Response.================================================================\n");
            System.out.println("Response : " + response);
            System.out.println("======================================================Response End.================================================================\n");
            if (result.getStatusLine().getStatusCode() != HttpStatus.CREATED.value()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), result.getStatusLine().getReasonPhrase(), null);
            }
            return result.getStatusLine().getStatusCode();

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);

        } finally {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private GenericDataDTO getBusinessPromotionAggregationReport(JPAQuery<?> jpaQuery, GenericDataDTO genericDataDTO, LocalDate fromDate, LocalDate toDate, NAVMasterDTO navMaster) {
        try {
            QBusinessPromotionRawData businessPromotionRawData = QBusinessPromotionRawData.businessPromotionRawData;
            BooleanExpression booleanExpression = null;
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            booleanExpression = businessPromotionRawData.isNotNull().and(businessPromotionRawData.addedDate.between(fromDate, toDate)).and(businessPromotionRawData.isPushed.eq(false));
            jpaQuery.groupBy(businessPromotionRawData.transactionType);
            for (NAVMasterAggregationParamMapping aggregationParam : navMaster.getNavMasterAggregationParamMappingList()) {
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BUSINESS_UNIT)) {
                    jpaQuery.groupBy(businessPromotionRawData.businessCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.DATE_WISE)) {
                    jpaQuery.groupBy(businessPromotionRawData.addedDate);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.IC)) {
                    jpaQuery.groupBy(businessPromotionRawData.ICCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BRANCH_CODE)) {
                    jpaQuery.groupBy(businessPromotionRawData.branchCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.NAV_LEDGER_ID)) {
                    jpaQuery.groupBy(businessPromotionRawData.pushableLedgerId);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.SERVICE_AREA)) {
                    jpaQuery.groupBy(businessPromotionRawData.serviceAreaId);
                    jpaQuery.groupBy(qServiceArea.name);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.OLT)) {
                    jpaQuery.groupBy(businessPromotionRawData.olt);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.POP)) {
                    jpaQuery.groupBy(businessPromotionRawData.pop);
                }
            }

            List<Tuple> billGenFinalDataList = jpaQuery.select(businessPromotionRawData.addedDate, businessPromotionRawData.businessCode, businessPromotionRawData.ICCode, businessPromotionRawData.branchCode, businessPromotionRawData.serviceAreaId, businessPromotionRawData.count(), businessPromotionRawData.amount.sum(), businessPromotionRawData.transactionType, qServiceArea.name, businessPromotionRawData.pushableLedgerId, businessPromotionRawData.olt, businessPromotionRawData.pop).from(businessPromotionRawData).leftJoin(qServiceArea).on(qServiceArea.id.eq(businessPromotionRawData.serviceAreaId.longValue())).orderBy(businessPromotionRawData.addedDate.asc()).where(booleanExpression).fetch();
            List<BusinessPromotionFinalData> businessPromotionFinalData = new ArrayList<>();
            for (Tuple tuple : billGenFinalDataList) {
                businessPromotionFinalData.add(new BusinessPromotionFinalData(tuple.get(businessPromotionRawData.addedDate), tuple.get(businessPromotionRawData.businessCode), tuple.get(businessPromotionRawData.ICCode), tuple.get(businessPromotionRawData.branchCode), "", tuple.get(businessPromotionRawData.serviceAreaId), tuple.get(businessPromotionRawData.count()), tuple.get(businessPromotionRawData.amount.sum()), tuple.get(businessPromotionRawData.transactionType), tuple.get(qServiceArea.name), tuple.get(businessPromotionRawData.pushableLedgerId), tuple.get(businessPromotionRawData.olt), tuple.get(businessPromotionRawData.pop)));
            }
            if (billGenFinalDataList.size() > 0) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            }
            genericDataDTO.setDataList(businessPromotionFinalData);
            genericDataDTO.setTotalRecords(businessPromotionFinalData.size());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    private void updateRawDataBusinessPromotion(BusinessPromotionFinalData businessPromotionFinalData) {
        QBusinessPromotionRawData qBusinessPromotionRawData = QBusinessPromotionRawData.businessPromotionRawData;
        BooleanExpression booleanExpression = qBusinessPromotionRawData.isNotNull().and(qBusinessPromotionRawData.isPushed).eq(false);
        if (businessPromotionFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.serviceAreaId.eq(businessPromotionFinalData.getServiceAreaId()));
        }
        if (businessPromotionFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.branchCode.equalsIgnoreCase(businessPromotionFinalData.getBranchCode()));
        }
        if (businessPromotionFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.businessCode.equalsIgnoreCase(businessPromotionFinalData.getBusinessCode()));
        }
        if (businessPromotionFinalData.getPushableLedgerId() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.pushableLedgerId.equalsIgnoreCase(businessPromotionFinalData.getPushableLedgerId()));
        }
        if (businessPromotionFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.ICCode.equalsIgnoreCase(businessPromotionFinalData.getICCode()));
        }
        if (businessPromotionFinalData.getAddedDate() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.addedDate.eq(businessPromotionFinalData.getAddedDate()));
        }
        if (businessPromotionFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.transactionType.equalsIgnoreCase(businessPromotionFinalData.getTransactionType()));
        }
        if (businessPromotionFinalData.getPop() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.pop.equalsIgnoreCase(businessPromotionFinalData.getPop()));
        }
        if (businessPromotionFinalData.getOlt() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.olt.equalsIgnoreCase(businessPromotionFinalData.getOlt()));
        }
        Iterable<BusinessPromotionRawData> businessPromotionRawDataIterable = businessPromotionRawDataRepository.findAll(booleanExpression);
        for (BusinessPromotionRawData businessPromotionRawData : businessPromotionRawDataIterable) {
            businessPromotionRawData.setIsPushed(true);
            businessPromotionRawData.setSerialNumberBusinessPromotionFinal(businessPromotionFinalData.getSerialNumber());
            businessPromotionRawDataRepository.save(businessPromotionRawData);
        }
    }

    private GenericDataDTO getReverseBusinessPromotionAggregationReport(JPAQuery<?> jpaQuery, GenericDataDTO genericDataDTO, LocalDate fromDate, LocalDate toDate, NAVMasterDTO navMaster) {
        try {
            QReverseBusinessPromotionRawData QReverseBusinessPromotionRawData = com.savbill.integrationsystem.ReverseBusinessPromotion.entity.QReverseBusinessPromotionRawData.reverseBusinessPromotionRawData;
            BooleanExpression booleanExpression = null;
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            booleanExpression = QReverseBusinessPromotionRawData.isNotNull().and(QReverseBusinessPromotionRawData.addedDate.between(fromDate, toDate)).and(QReverseBusinessPromotionRawData.isPushed.eq(false));
            jpaQuery.groupBy(QReverseBusinessPromotionRawData.transactionType);
            for (NAVMasterAggregationParamMapping aggregationParam : navMaster.getNavMasterAggregationParamMappingList()) {
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BUSINESS_UNIT)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.businessCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.DATE_WISE)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.addedDate);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.IC)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.ICCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BRANCH_CODE)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.branchCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.NAV_LEDGER_ID)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.NAVLedgerId);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.SERVICE_AREA)) {
                    jpaQuery.groupBy(QReverseBusinessPromotionRawData.serviceAreaId);
                    jpaQuery.groupBy(qServiceArea.name);
                }
            }

            List<Tuple> billGenFinalDataList = jpaQuery.select(QReverseBusinessPromotionRawData.addedDate, QReverseBusinessPromotionRawData.businessCode, QReverseBusinessPromotionRawData.ICCode, QReverseBusinessPromotionRawData.branchCode, QReverseBusinessPromotionRawData.NAVLedgerId, QReverseBusinessPromotionRawData.serviceAreaId, QReverseBusinessPromotionRawData.count(), QReverseBusinessPromotionRawData.amount.sum(), QReverseBusinessPromotionRawData.transactionType, qServiceArea.name).from(QReverseBusinessPromotionRawData).leftJoin(qServiceArea).on(qServiceArea.id.eq(QReverseBusinessPromotionRawData.serviceAreaId.longValue())).orderBy(QReverseBusinessPromotionRawData.addedDate.asc()).where(booleanExpression).fetch();
            List<ReverseBusinessPromotionFinalData> reverseBusinessPromotionFinalData = new ArrayList<>();
            for (Tuple tuple : billGenFinalDataList) {
                reverseBusinessPromotionFinalData.add(new ReverseBusinessPromotionFinalData(tuple.get(QReverseBusinessPromotionRawData.addedDate), tuple.get(QReverseBusinessPromotionRawData.businessCode), tuple.get(QReverseBusinessPromotionRawData.ICCode), tuple.get(QReverseBusinessPromotionRawData.branchCode), tuple.get(QReverseBusinessPromotionRawData.NAVLedgerId), tuple.get(QReverseBusinessPromotionRawData.serviceAreaId), tuple.get(QReverseBusinessPromotionRawData.count()), tuple.get(QReverseBusinessPromotionRawData.amount.sum()), tuple.get(QReverseBusinessPromotionRawData.transactionType), tuple.get(qServiceArea.name)));
            }
            if (billGenFinalDataList.size() > 0) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            }
            genericDataDTO.setDataList(reverseBusinessPromotionFinalData);
            genericDataDTO.setTotalRecords(reverseBusinessPromotionFinalData.size());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    private void updateRawDataReverseBusinessPromotion(ReverseBusinessPromotionFinalData reverseBusinessPromotionFinalData) {
        QReverseBusinessPromotionRawData qReverseBusinessPromotionRawData = QReverseBusinessPromotionRawData.reverseBusinessPromotionRawData;
        BooleanExpression booleanExpression = qReverseBusinessPromotionRawData.isNotNull().and(qReverseBusinessPromotionRawData.isPushed).eq(false);
        if (reverseBusinessPromotionFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.serviceAreaId.eq(reverseBusinessPromotionFinalData.getServiceAreaId()));
        }
        if (reverseBusinessPromotionFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.branchCode.equalsIgnoreCase(reverseBusinessPromotionFinalData.getBranchCode()));
        }
        if (reverseBusinessPromotionFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.businessCode.equalsIgnoreCase(reverseBusinessPromotionFinalData.getBusinessCode()));
        }
        if (reverseBusinessPromotionFinalData.getNAVLedgerId() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.NAVLedgerId.equalsIgnoreCase(reverseBusinessPromotionFinalData.getNAVLedgerId()));
        }
        if (reverseBusinessPromotionFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.ICCode.equalsIgnoreCase(reverseBusinessPromotionFinalData.getICCode()));
        }
        if (reverseBusinessPromotionFinalData.getAddedDate() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.addedDate.eq(reverseBusinessPromotionFinalData.getAddedDate()));
        }
        if (reverseBusinessPromotionFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(qReverseBusinessPromotionRawData.transactionType.equalsIgnoreCase(reverseBusinessPromotionFinalData.getTransactionType()));
        }
        Iterable<BusinessPromotionRawData> businessPromotionRawDataIterable = businessPromotionRawDataRepository.findAll(booleanExpression);
        for (BusinessPromotionRawData businessPromotionRawData : businessPromotionRawDataIterable) {
            businessPromotionRawData.setIsPushed(true);
            businessPromotionRawData.setSerialNumberBusinessPromotionFinal(reverseBusinessPromotionFinalData.getSerialNumber());
            businessPromotionRawDataRepository.save(businessPromotionRawData);
        }
    }

    private void getPaymentAggregationReport(JPAQuery<?> jpaQuery, GenericDataDTO genericDataDTO, LocalDate fromDate, LocalDate toDate, NAVMasterDTO navMaster) {
        try {
            QPaymentGenRawData qPaymentGenRawData = QPaymentGenRawData.paymentGenRawData;
            BooleanExpression booleanExpression = null;
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            booleanExpression = qPaymentGenRawData.isNotNull().and(qPaymentGenRawData.paymentdate.between(fromDate, toDate)).and(qPaymentGenRawData.isPushed.eq(false));
            jpaQuery.groupBy(qPaymentGenRawData.otherDetails, qPaymentGenRawData.paymentMode);
            for (NAVMasterAggregationParamMapping aggregationParam : navMaster.getNavMasterAggregationParamMappingList()) {
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BUSINESS_UNIT)) {
                    jpaQuery.groupBy(qPaymentGenRawData.businessCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.DATE_WISE)) {
                    jpaQuery.groupBy(qPaymentGenRawData.paymentdate);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.IC)) {
                    jpaQuery.groupBy(qPaymentGenRawData.ICCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.BRANCH_CODE)) {
                    jpaQuery.groupBy(qPaymentGenRawData.branchCode);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.NAV_LEDGER_ID)) {
                    jpaQuery.groupBy(qPaymentGenRawData.NAVLedgerId);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.SERVICE_AREA)) {
                    jpaQuery.groupBy(qPaymentGenRawData.serviceAreaId);
                    jpaQuery.groupBy(qServiceArea.name);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.POP)) {
                    jpaQuery.groupBy(qPaymentGenRawData.pop);
                }
                if (aggregationParam.getParamName().equalsIgnoreCase(CommonConstant.AGGREGATION_PARAMS.OLT)) {
                    jpaQuery.groupBy(qPaymentGenRawData.olt);
                }
            }

            List<Tuple> paymentRawDataFinal = jpaQuery.select(qPaymentGenRawData.paymentdate, qPaymentGenRawData.businessCode, qPaymentGenRawData.ICCode, qPaymentGenRawData.branchCode, qPaymentGenRawData.NAVLedgerId, qPaymentGenRawData.serviceAreaId, qPaymentGenRawData.count(), qPaymentGenRawData.amount.sum(), qPaymentGenRawData.paymentMode, qPaymentGenRawData.otherDetails, qServiceArea.name, qPaymentGenRawData.pop, qPaymentGenRawData.olt).from(qPaymentGenRawData).leftJoin(qServiceArea).on(qServiceArea.id.eq(qPaymentGenRawData.serviceAreaId.longValue())).orderBy(qPaymentGenRawData.paymentdate.asc()).where(booleanExpression).fetch();
            List<PaymentGenFinalData> paymentGenRawData = new ArrayList<>();
            for (Tuple tuple : paymentRawDataFinal) {
                paymentGenRawData.add(new PaymentGenFinalData(tuple.get(qPaymentGenRawData.paymentdate), tuple.get(qPaymentGenRawData.amount.sum()), tuple.get(qPaymentGenRawData.branchCode), tuple.get(qPaymentGenRawData.businessCode), tuple.get(qPaymentGenRawData.ICCode), tuple.get(qPaymentGenRawData.NAVLedgerId), tuple.get(qPaymentGenRawData.count()), tuple.get(qPaymentGenRawData.serviceAreaId), tuple.get(qServiceArea.name), tuple.get(qPaymentGenRawData.paymentMode), tuple.get(qPaymentGenRawData.otherDetails), tuple.get(qPaymentGenRawData.olt), tuple.get(qPaymentGenRawData.pop)));
            }
            if (paymentGenRawData.size() > 0) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            }
            genericDataDTO.setDataList(paymentGenRawData);
            genericDataDTO.setTotalRecords(paymentGenRawData.size());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
//        return genericDataDTO;
    }

    private void updateRawDataReceiptEntry(PaymentGenFinalData paymentGenFinalData) {
        QPaymentGenRawData qPaymentGenRawData = QPaymentGenRawData.paymentGenRawData;
        BooleanExpression booleanExpression = qPaymentGenRawData.isNotNull().and(qPaymentGenRawData.isPushed.eq(false));
        if (paymentGenFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.serviceAreaId.eq(paymentGenFinalData.getServiceAreaId()));
        }
        if (paymentGenFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.branchCode.equalsIgnoreCase(paymentGenFinalData.getBranchCode()));
        }
        if (paymentGenFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.businessCode.equalsIgnoreCase(paymentGenFinalData.getBusinessCode()));
        }
        if (paymentGenFinalData.getNAVLedgerId() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.NAVLedgerId.equalsIgnoreCase(paymentGenFinalData.getNAVLedgerId()));
        }
        if (paymentGenFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.ICCode.equalsIgnoreCase(paymentGenFinalData.getICCode()));
        }
        if (paymentGenFinalData.getPaymentDate() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.paymentdate.eq(paymentGenFinalData.getPaymentDate()));
        }
        if (paymentGenFinalData.getPaymentMode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.paymentMode.equalsIgnoreCase(paymentGenFinalData.getPaymentMode()));
        }
        if (paymentGenFinalData.getOtherDetails() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.otherDetails.equalsIgnoreCase(paymentGenFinalData.getOtherDetails()));
        }
        Iterable<PaymentGenRawData> paymentGenRawDataList = paymentGenRawDataRepo.findAll(booleanExpression);
        for (PaymentGenRawData paymentGenRawData : paymentGenRawDataList) {
            paymentGenRawData.setIsPushed(true);
            paymentGenRawData.setSerialNumberPaymentGenFinal(paymentGenFinalData.getSerialNumber());
            paymentGenRawData.setDocumentNumber(paymentGenFinalData.getDocumentNumber());
            paymentGenRawDataRepo.save(paymentGenRawData);
        }
    }

    private void getReceiptPushedAggregationReport(GenericDataDTO genericDataDTO, PageRequest pageRequest, LocalDate startDate, LocalDate endDate) {
        QPaymentGenFinalData qPaymentGenFinalData = QPaymentGenFinalData.paymentGenFinalData;
        BooleanExpression booleanExpression = qPaymentGenFinalData.isNotNull().and(qPaymentGenFinalData.isPushed.eq(true)).and(qPaymentGenFinalData.paymentDate.between(startDate, endDate));
        Page<PaymentGenFinalData> paginationList = paymentGenFinalDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        if (genericDataDTO.getDataList().size() > 0) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } else {

            genericDataDTO.setResponseMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }

        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }

    public void getRawDataBillGen(GenericDataDTO genericDataDTO, BillGenFinalData billGenFinalData, PageRequest pageRequest, boolean isPushed) {
        QBillGenRawData billGenRawData = QBillGenRawData.billGenRawData;
        BooleanExpression booleanExpression = billGenRawData.addedDate.eq(billGenFinalData.getAddedDate()).and(billGenRawData.isPushed.eq(isPushed));
        if (billGenFinalData.getSerialNumber() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.serialNumberBillGenFinal.eq(billGenFinalData.getSerialNumber()));
        }
        if (billGenFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.serviceAreaId.eq(billGenFinalData.getServiceAreaId()));
        }
        if (billGenFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.branchCode.eq(billGenFinalData.getBranchCode()));
        }
        if (billGenFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.businessCode.eq(billGenFinalData.getBusinessCode()));
        }
        if (billGenFinalData.getPushableLedgerId() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.pushableLedgerId.eq(billGenFinalData.getPushableLedgerId()));
        }
        if (billGenFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.transactionType.eq(billGenFinalData.getTransactionType()));
        }
        if (billGenFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.ICCode.eq(billGenFinalData.getICCode()));
        }
        if (billGenFinalData.getOlt() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.olt.eq(billGenFinalData.getOlt()));
        }
        if (billGenFinalData.getPop() != null) {
            booleanExpression = booleanExpression.and(billGenRawData.pop.eq(billGenFinalData.getPop()));
        }
        Page<BillGenRawData> paginationList = billGenRawDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(billGenRawData1 -> billGenMapper.domainToDTO(billGenRawData1, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());

    }

    public void getRawDataBusinessPromotion(GenericDataDTO genericDataDTO, BusinessPromotionFinalData promotionFinalData, PageRequest pageRequest, boolean isPushed) {
        QBusinessPromotionRawData qBusinessPromotionRawData = QBusinessPromotionRawData.businessPromotionRawData;
        BooleanExpression booleanExpression = qBusinessPromotionRawData.addedDate.eq(promotionFinalData.getAddedDate()).and(qBusinessPromotionRawData.isPushed.eq(isPushed));
        if (promotionFinalData.getSerialNumber() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.serialNumberBusinessPromotionFinal.eq(promotionFinalData.getSerialNumber()));
        }
        if (promotionFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.serviceAreaId.eq(promotionFinalData.getServiceAreaId()));
        }
        if (promotionFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.branchCode.eq(promotionFinalData.getBranchCode()));
        }
        if (promotionFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.businessCode.eq(promotionFinalData.getBusinessCode()));
        }
        if (promotionFinalData.getNAVLedgerId() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.pushableLedgerId.eq(promotionFinalData.getPushableLedgerId()));
        }
        if (promotionFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.transactionType.eq(promotionFinalData.getTransactionType()));
        }
        if (promotionFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qBusinessPromotionRawData.ICCode.eq(promotionFinalData.getICCode()));
        }
        Page<BusinessPromotionRawData> paginationList = businessPromotionRawDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());

    }


    private void getRawDataCredirNote(GenericDataDTO genericDataDTO, CreditNoteFinalData creditNoteFinalData, PageRequest pageRequest, boolean isPushed) {
        QCreditNoteGenRawData qCreditNoteGenRawData = QCreditNoteGenRawData.creditNoteGenRawData;
        BooleanExpression booleanExpression = qCreditNoteGenRawData.addedDate.eq(creditNoteFinalData.getAddedDate()).and(qCreditNoteGenRawData.isPushed.eq(isPushed));
        if (creditNoteFinalData.getSerialNumber() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.serialNumberCreditNotFinal.eq(creditNoteFinalData.getSerialNumber()));
        }
        if (creditNoteFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.serviceAreaId.eq(creditNoteFinalData.getServiceAreaId()));
        }
        if (creditNoteFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.branchCode.eq(creditNoteFinalData.getBranchCode()));
        }
        if (creditNoteFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.businessCode.eq(creditNoteFinalData.getBusinessCode()));
        }
        if (creditNoteFinalData.getNAVLedgerId() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.NAVLedgerId.eq(creditNoteFinalData.getNAVLedgerId()));
        }
        if (creditNoteFinalData.getTransactionType() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.transactionType.eq(creditNoteFinalData.getTransactionType()));
        }
        if (creditNoteFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qCreditNoteGenRawData.ICCode.eq(creditNoteFinalData.getICCode()));
        }
        Page<CreditNoteGenRawData> paginationList = creditNoteGenRawDataRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());

    }


    private void getRawDataPayment(GenericDataDTO genericDataDTO, PaymentGenFinalData paymentGenFinalData, PageRequest pageRequest, boolean isPushed) {

        QPaymentGenRawData qPaymentGenRawData = QPaymentGenRawData.paymentGenRawData;
        BooleanExpression booleanExpression = qPaymentGenRawData.paymentdate.eq(qPaymentGenRawData.paymentdate).and(qPaymentGenRawData.isPushed.eq(isPushed));
        if (paymentGenFinalData.getSerialNumber() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.serialNumberPaymentGenFinal.eq(paymentGenFinalData.getSerialNumber()));
        }
        if (paymentGenFinalData.getServiceAreaId() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.serviceAreaId.eq(paymentGenFinalData.getServiceAreaId()));
        }
        if (paymentGenFinalData.getBranchCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.branchCode.eq(paymentGenFinalData.getBranchCode()));
        }
        if (paymentGenFinalData.getBusinessCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.businessCode.eq(paymentGenFinalData.getBusinessCode()));
        }
        if (paymentGenFinalData.getNAVLedgerId() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.NAVLedgerId.eq(paymentGenFinalData.getNAVLedgerId()));
        }
        if (paymentGenFinalData.getPaymentMode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.paymentMode.eq(paymentGenFinalData.getPaymentMode()));
        }
        if (paymentGenFinalData.getOtherDetails() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.otherDetails.eq(paymentGenFinalData.getOtherDetails()));
        }
        if (paymentGenFinalData.getICCode() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.ICCode.equalsIgnoreCase(paymentGenFinalData.getICCode()));
        }
        if (paymentGenFinalData.getOlt() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.olt.equalsIgnoreCase(paymentGenFinalData.getOlt()));
        }
        if (paymentGenFinalData.getPop() != null) {
            booleanExpression = booleanExpression.and(qPaymentGenRawData.pop.equalsIgnoreCase(paymentGenFinalData.getPop()));
        }
        Page<PaymentGenRawData> paginationList = paymentGenRawDataRepo.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
    }


}

