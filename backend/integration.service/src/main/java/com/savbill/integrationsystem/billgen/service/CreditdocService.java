package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentAPIMappings;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentIntegrationMaster;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.repository.GovernmentIntegrationMasterRepository;
import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.ReverseBusinessPromotionRawData;
import com.savbill.integrationsystem.ReverseBusinessPromotion.entity.ReverseBusinessPromotionRawDataRepository;
import com.savbill.integrationsystem.SendCreditNoteToGovernment.entity.SendCreditNoteToGovernment;
import com.savbill.integrationsystem.billgen.entity.*;
import com.savbill.integrationsystem.billgen.entity.*;
import com.savbill.integrationsystem.billgen.mapper.CreditDocumentMapper;
import com.savbill.integrationsystem.billgen.mapper.CustomerMapper;
import com.savbill.integrationsystem.billgen.mapper.DebitDocumentMapper;
import com.savbill.integrationsystem.billgen.model.CreditDocumentDTO;
import com.savbill.integrationsystem.billgen.model.CustomerDTO;
import com.savbill.integrationsystem.billgen.model.DebitDocumentDTO;
import com.savbill.integrationsystem.billgen.model.DebitDocumentTAXRelDTO;
import com.savbill.integrationsystem.billgen.repository.*;
import com.savbill.integrationsystem.billgen.repository.*;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.utillity.Helper;
import com.savbill.integrationsystem.paymentgen.entity.PaymentGenRawData;
import com.savbill.integrationsystem.paymentgen.entity.repository.PaymentGenRawDataRepo;
import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import com.savbill.integrationsystem.rabbitmq.CreditNoteMessageIntegrationSystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CreditdocService {

    private final CreditDocumentMapper mapper;
    private final CreditDocRepocitory creditDocRepocitory;
    private final CreditDebtMappingRepository creditDebtMappingRepository;
    private final CustomersDataRepository customersDataRepository;
    private final BranchRepository branchRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final CreditNoteGenRawDataRepository creditNoteGenRawDataRepository;
    private final DebitDocumentRepository debitDocumentRepository;
    private final ReverseBusinessPromotionRawDataRepository reverseBusinessPromotionRawDataRepository;
    private final PaymentGenRawDataRepo paymentGenRawDataRepo;
    private final GovernmentIntegrationMasterRepository governmentIntegrationMasterRepository;
    private final CustomerMapper customerMapper;
    private final DebitDocumentMapper debitDocumentMapper;
    private final ObjectMapper objectMapper;

    public void save(CreditDocMessage message) {
        CreditDocumentData creditDocumentData = new CreditDocumentData(message);
        List<CreditDebitDocMapping> creditDebitDocMappingList = message.getCreditDebitDocMappingList();
        creditDocumentData = creditDocRepocitory.save(creditDocumentData);
        CreditDocumentDTO creditDocumentDTO = mapper.domainToDTO(creditDocumentData, new CycleAvoidingMappingContext());
        DebitDocument debitDocument = creditDocumentData.getInvoiceId() != null ? debitDocumentRepository.findById(creditDocumentData.getInvoiceId()).orElse(null) : null;
        if (creditDebitDocMappingList != null && creditDebitDocMappingList.size() > 0) {
            creditDebtMappingRepository.saveAll(creditDebitDocMappingList);
        }
        if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.BUSINESS_PROMOTION)) {
            CustomerData actualCustomer = customersDataRepository.findCustomerDataByUsername(debitDocument.getCustRefName());
            Branch branch = null;
            if ((actualCustomer != null ? actualCustomer.getBranch() : null) != null) {
                branch = branchRepository.findById(actualCustomer.getBranch()).orElse(null);
            }
            if (debitDocument.getDebitDocDetailsList().size() > 0 && actualCustomer != null) {
                for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                    ReverseBusinessPromotionRawData businessPromotionRawData = new ReverseBusinessPromotionRawData(LocalDate.now(), debitDocument.getStartdate(), debitDocument.getEndate(), "Business Promotion", debitDocument.getDebitdocumentnumber(), actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()), actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(), actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(), debitDocDetails.getChargename(), branch != null ? branch.getBranch_code() : null, CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE, debitDocDetails.getLedgerId(), -(debitDocDetails.getSubtotal() + debitDocDetails.getDiscount()), debitDocument.getId().longValue(), actualCustomer.getServicearea().intValue(), false);
                    reverseBusinessPromotionRawDataRepository.save(businessPromotionRawData);
                }
            }
            if (debitDocument.getDebitDocumentTAXRels().size() > 0 && actualCustomer != null) {
                for (DebitDocumentTAXRel debitDocumentTAXRel : debitDocument.getDebitDocumentTAXRels()) {
                    ReverseBusinessPromotionRawData reverseBusinessPromotionRawData = new ReverseBusinessPromotionRawData(LocalDate.now(), debitDocument.getStartdate(), debitDocument.getEndate(), debitDocumentTAXRel.getTaxname().equalsIgnoreCase("TSC") ? "Business Promotion" : "TAX" + "-" + debitDocumentTAXRel.getTaxname(), debitDocument.getDebitdocumentnumber(), actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()), actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(), actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(), debitDocumentTAXRel.getTaxname(), CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE, debitDocumentTAXRel.getTaxLedgerId(), debitDocumentTAXRel.getTaxname().equalsIgnoreCase("TSC") ? debitDocumentTAXRel.getAmount() : -debitDocumentTAXRel.getAmount(), debitDocument.getId().longValue(), 0, false);
                    reverseBusinessPromotionRawDataRepository.save(reverseBusinessPromotionRawData);
                }
            }
            ReverseBusinessPromotionRawData reverseBusinessPromotionRawData = new ReverseBusinessPromotionRawData(LocalDate.now(), debitDocument.getStartdate(), debitDocument.getEndate(), "Sundry Debtors", debitDocument.getDebitdocumentnumber(), actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()), actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(), actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(), "Sundry Debtors", CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, debitDocument.getTotalamount(), debitDocument.getId().longValue(), 0, false);
            reverseBusinessPromotionRawDataRepository.save(reverseBusinessPromotionRawData);
        } else if (creditDocumentData.getType().equalsIgnoreCase(CommonConstant.PAYMENT_TYPE.PAYMENT)) {
            CustomerData customers = customersDataRepository.findById(message.getCustomer()).orElse(null);
            Branch branch = null;
            BusinessUnit businessUnit = null;
            PaymentGenRawData paymentGenRawData = new PaymentGenRawData();
            if ((customers != null ? customers.getBranch() : null) != null) {
                branch = branchRepository.findById(customers.getBranch()).orElse(null);
            }
//                if ((customers != null ? customers.getBuId() : null) != null) {
//                    businessUnit = businessUnitRepository.findById(customers.getBuId()).orElse(null);
//                }
            paymentGenRawData.setPaymentdate(LocalDate.parse(creditDocumentData.getPaymentdate()));
            paymentGenRawData.setAmount(creditDocumentData.getAmount());
            paymentGenRawData.setBranchCode(branch == null ? CommonConstant.COMMON_DATA.BRANCH : branch.getBranch_code());
            paymentGenRawData.setBusinessCode(CommonConstant.COMMON_DATA.BU);
            paymentGenRawData.setICCode(CommonConstant.COMMON_DATA.ICCODE);
            paymentGenRawData.setNAVLedgerId(creditDocumentData.getLedgerId());
            paymentGenRawData.setServiceAreaId(customers != null ? customers.getServicearea() : null);
            paymentGenRawData.setIsPushed(false);
            paymentGenRawData.setPaymentMode(creditDocumentData.getPaymode());
            paymentGenRawData.setPaymentSource(creditDocumentData.getOnlinesource());
            paymentGenRawData.setOlt(CommonConstant.COMMON_DATA.OLT);
            paymentGenRawData.setPop(CommonConstant.COMMON_DATA.POP);
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.OTHER_ADJUSTMENT)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.ABBS)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.OPG_ADJUSTMENT)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.VAT_RECEIVEABLE)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.DIRECT_DEPOSIT)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.BARTER)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.TDS)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.CHEQUE)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getPaydetails2());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.ONLINE)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getOnlinesource());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.CASH)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.NEFT_RTGS)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.DEBIT_CARD)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            if (creditDocumentData.getPaymode().equalsIgnoreCase(CommonConstant.PAYMENT_MODE.CREDIT_CARD)) {
                paymentGenRawData.setOtherDetails(creditDocumentData.getReciptNo());
            }
            paymentGenRawDataRepo.save(paymentGenRawData);
            if (creditDocumentData.getAbbsAmount() != null && creditDocumentData.getAbbsAmount() != 0) {
                addABBSinPaymentrawData(creditDocumentData);
            }
            if (creditDocumentData.getTdsAmount() != null && creditDocumentData.getTdsAmount() != 0) {
                addTDSinPaymentrawData(creditDocumentData);
            }

            addSundryDebtorsinPaymentrawData(creditDocumentData);

        }
        if (creditDocumentData.getType().equalsIgnoreCase(CommonConstant.CREDITDOC_TYPE_CREDIT_NOTE)) {
            log.debug("Inside the if condition to send the creditnote");
            DebitDocumentDTO debitDocumentDTO = debitDocumentMapper.domainToDTO(debitDocument, new CycleAvoidingMappingContext());
            CustomerData customers = customersDataRepository.findById(message.getCustomer()).orElse(null);
            CustomerDTO customerDTO = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            sendCreditToGovernment(customerDTO, debitDocumentDTO, creditDocumentDTO, Boolean.TRUE);
        }
    }


    @Transactional
    public void saveCreditNoteGenData(CreditNoteMessageIntegrationSystem message) {
        Map<String, Double> map = message.getData();
        String branchCode = "", buCode = "";
        CustomerData customerData = customersDataRepository.findById(message.getCustomerId()).orElse(null);
        Branch branch;
        BusinessUnit businessUnit;
        if ((customerData != null ? customerData.getBranch() : null) != null) {
            branch = branchRepository.findById(customerData.getBranch()).orElse(null);
            branchCode = Objects.requireNonNull(branch).getBranch_code();
        }
        if ((customerData != null ? customerData.getBuId() : null) != null) {
            businessUnit = businessUnitRepository.findById(customerData.getBuId()).orElse(null);
            buCode = Objects.requireNonNull(businessUnit).getBucode();
        }
        if (customerData != null) {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                String s = entry.getKey();
                Double s2 = entry.getValue();
                String transactionType = s.split("#")[0];
                String transactionName = s.split("#")[1];
                String ledgerId = s.split("#")[2];
                String iccode = s.split("#")[3];
                String pushableLedgerId = s.split("#")[4];
                if (transactionType.equalsIgnoreCase("TAX")) {
                    branchCode = CommonConstant.COMMON_DATA.BRANCH;
                    buCode = CommonConstant.COMMON_DATA.BU;
                    iccode = CommonConstant.COMMON_DATA.ICCODE;
                }
                if (transactionType.equalsIgnoreCase("DISCOUNT")) {
                    iccode = CommonConstant.COMMON_DATA.ICCODE;
                }
                CreditNoteGenRawData creditNoteGenRawData = new CreditNoteGenRawData(LocalDate.now(), transactionType, message.getDocumentNumber(), customerData.getFirstname().concat(" " + customerData.getLastname()), customerData.getUsername(), customerData.getAccountNumber(), customerData.getCustomerType(), transactionName, branchCode, buCode, iccode, ledgerId, transactionType.contains("TAX") || transactionType.equalsIgnoreCase("PREPAID") || transactionType.equalsIgnoreCase("REVENUE") ? s2 : -s2, message.getCreditDocId().longValue(), transactionType.contains("TAX") ? 0 : customerData.getServicearea().intValue(), false, pushableLedgerId);
                creditNoteGenRawDataRepository.save(creditNoteGenRawData);
            }
            //Sundry debtors entry

            CreditNoteGenRawData creditNoteGenRawData = new CreditNoteGenRawData(LocalDate.now(), "Sundry Debtors", message.getDocumentNumber(), customerData.getFirstname().concat(" " + customerData.getLastname()), customerData.getUsername(), customerData.getAccountNumber(), customerData.getCustomerType(), "Sundry Debtors", CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, -message.getAmount(), message.getCreditDocId().longValue(), 0, false, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS);
            creditNoteGenRawDataRepository.save(creditNoteGenRawData);
        }
    }

    public void sendCreditToGovernment(CustomerDTO customerDTO, DebitDocumentDTO debitDocumentDTO, CreditDocumentDTO creditDocumentDTO, Boolean isRealTime) {
        log.debug("Inside sendCreditToGovernment customerDTO: {} creditDocumentDTO: {}", customerDTO.getId(), creditDocumentDTO.getId());
        GovernmentIntegrationMaster governmentIntegrationMaster = governmentIntegrationMasterRepository.getGovernmentIntegrationMasterByMvnoIdAndIsdeleteFalse(Long.valueOf(customerDTO.getMvnoId()));
        if (governmentIntegrationMaster != null) {
            String url = "https://cbapi.ird.gov.np/api/billreturn";
            for (GovernmentAPIMappings governmentAPIMappings : governmentIntegrationMaster.getGovernmentAPIMappings()) {
                if (governmentAPIMappings.getApiName().equalsIgnoreCase(CommonConstant.SEND_OPTIONS_GOVERNMENT.SEND_INVOICE)) {
                    url = governmentAPIMappings.getEndpoint();
                }
            }
            SendCreditNoteToGovernment sendCreditNoteToGovernment = new SendCreditNoteToGovernment();
            sendCreditNoteToGovernment.setUsername(governmentIntegrationMaster.getUsername());
            sendCreditNoteToGovernment.setPassword(governmentIntegrationMaster.getPassword());
            sendCreditNoteToGovernment.setSellerPan(governmentIntegrationMaster.getPan());
            sendCreditNoteToGovernment.setBuyerName((customerDTO.getFirstname() + " " + customerDTO.getLastname().trim()));
            sendCreditNoteToGovernment.setBuyerPan(StringUtils.isEmpty(customerDTO.getPan()) ? null : customerDTO.getPan());
            sendCreditNoteToGovernment.setRefInvoiceNumber(debitDocumentDTO.getDebitdocumentnumber());
            sendCreditNoteToGovernment.setCreditNoteNumber(creditDocumentDTO.getCreditdocumentno());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime creditNoteDate = LocalDateTime.parse(creditDocumentDTO.getCreatedate(), formatter);
            sendCreditNoteToGovernment.setReasonForReturn(creditDocumentDTO.getRemarks());
            sendCreditNoteToGovernment.setTotalSales(creditDocumentDTO.getAmount());

            setTaxableVatAmounts(debitDocumentDTO, creditDocumentDTO.getAmount(), sendCreditNoteToGovernment);
            sendCreditNoteToGovernment.setExcisableAmount(0D);
            sendCreditNoteToGovernment.setExcise(0D);
            sendCreditNoteToGovernment.setTaxableSalesHst(0D);
            sendCreditNoteToGovernment.setHst(0D);
            sendCreditNoteToGovernment.setEsf(0D);
            sendCreditNoteToGovernment.setAmountForEsf(0D);
            sendCreditNoteToGovernment.setExportSales(0D);
            sendCreditNoteToGovernment.setTaxExemptedSales(0D);
            sendCreditNoteToGovernment.setIsrealtime(isRealTime);
            DateTimeFormatter zonedTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");
            sendCreditNoteToGovernment.setDatetimeclient(ZonedDateTime.now().format(zonedTimeFormatter));
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                String json = convertJson(sendCreditNoteToGovernment);
                log.debug("jsonString: {}", json);

                HttpUriRequest httpGetForAccessToken = RequestBuilder.post().setUri(url).setEntity(new StringEntity(json, ContentType.APPLICATION_JSON)).build();
                CloseableHttpResponse result = null;
                result = client.execute(httpGetForAccessToken);
                HttpEntity entity = result.getEntity();
                String content = EntityUtils.toString(entity);

                System.out.println("======================================================Request SendCreditNoteId "+ creditDocumentDTO.getId() +".================================================================\n");
                System.out.println("Request : " + json);
                System.out.println("======================================================Request End.================================================================\n");
                System.out.println("======================================================Response SendCreditNoteId "+ creditDocumentDTO.getId() +".================================================================\n");
                System.out.println("Response : " + content);
                System.out.println("======================================================Response End.================================================================\n");

                String responseCode = content;
                String irdSynch = CommonConstant.IRD_SYNCH_NO;

                if (content.equalsIgnoreCase("101")) {
                    irdSynch = CommonConstant.IRD_SYNCH_YES;
                }
                if (content.equalsIgnoreCase("200")) {
                    irdSynch = CommonConstant.IRD_SYNCH_YES;
                }

                creditDocumentDTO.setIrdRespCode(responseCode);
                creditDocumentDTO.setIrdSync(irdSynch);
                CreditDocumentData creditDocumentData = mapper.dtoToDomain(creditDocumentDTO, new CycleAvoidingMappingContext());
                creditDocRepocitory.save(creditDocumentData);
            } catch (Exception e) {
                throw new CustomValidationException(org.springframework.http.HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        log.debug("Leaving sendCreditToGovernment");
    }

    private void setTaxableVatAmounts(DebitDocumentDTO debitDocumentDTO, Double creditNoteAmount, SendCreditNoteToGovernment sendCreditNoteToGovernment) {
        Double taxableSalesVatAmt = creditNoteAmount;
        Double vat = 0D;
        for (DebitDocumentTAXRelDTO debitDocumentTAXRelDTO : debitDocumentDTO.getDebitDocumentTAXRels()) {
            if (debitDocumentTAXRelDTO.getTaxname().equalsIgnoreCase(CommonConstant.TAX_NAME_VAT)) {
                Double invoiceAmount = debitDocumentDTO.getTotalamount();
                Double invoiceVat = debitDocumentTAXRelDTO.getAmount();
                Double creditVatAmt = (invoiceVat * creditNoteAmount) / invoiceAmount;
                taxableSalesVatAmt -= creditVatAmt;
                vat += creditVatAmt;
            }
        }
        sendCreditNoteToGovernment.setTaxableSalesVat(taxableSalesVatAmt);
        sendCreditNoteToGovernment.setVat(vat);
        ;
    }

    public List<CreditDocumentDTO> findByIrdSyncAndTypeAndIsDeleteFalse(String irdSync, String type) {
        List<CreditDocumentData> list = creditDocRepocitory.findNotSynched(irdSync, type);
        return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    private String convertJson(SendCreditNoteToGovernment sendCreditNoteToGovernment) {
        try {
            return objectMapper.writeValueAsString(sendCreditNoteToGovernment);
        } catch (JsonProcessingException e) {
            log.error("Exception occurred while converting to json", e);
            return "{}";
        }
    }


    private void addTDSinPaymentrawData(CreditDocumentData creditDocumentData) {
        PaymentGenRawData paymentGenRawData = new PaymentGenRawData();
        paymentGenRawData.setPaymentdate(LocalDate.parse(creditDocumentData.getPaymentdate()));
        paymentGenRawData.setAmount(creditDocumentData.getTdsAmount());
        paymentGenRawData.setBranchCode(CommonConstant.COMMON_DATA.BRANCH);
        paymentGenRawData.setBusinessCode(CommonConstant.COMMON_DATA.BU);
        paymentGenRawData.setICCode(CommonConstant.COMMON_DATA.ICCODE);
        paymentGenRawData.setNAVLedgerId(CommonConstant.COMMON_DATA.NAV_LEDGER_FOR_TDS);
        paymentGenRawData.setServiceAreaId(0L);
        paymentGenRawData.setIsPushed(false);
        paymentGenRawData.setPaymentMode("TDS");
//        paymentGenRawData.setPaymentSource("TDS");
        paymentGenRawData.setOlt(CommonConstant.COMMON_DATA.OLT);
        paymentGenRawData.setPop(CommonConstant.COMMON_DATA.POP);
        paymentGenRawData.setPaymentSource(creditDocumentData.getReciptNo());
        paymentGenRawDataRepo.save(paymentGenRawData);
    }

    private void addABBSinPaymentrawData(CreditDocumentData creditDocumentData) {
        PaymentGenRawData paymentGenRawData = new PaymentGenRawData();
        paymentGenRawData.setPaymentdate(LocalDate.parse(creditDocumentData.getPaymentdate()));
        paymentGenRawData.setAmount(creditDocumentData.getAbbsAmount());
        paymentGenRawData.setBranchCode(CommonConstant.COMMON_DATA.BRANCH);
        paymentGenRawData.setBusinessCode(CommonConstant.COMMON_DATA.BU);
        paymentGenRawData.setICCode(CommonConstant.COMMON_DATA.ICCODE);
        paymentGenRawData.setNAVLedgerId(CommonConstant.COMMON_DATA.NAV_LEDGER_FOR_ABBS);
        paymentGenRawData.setServiceAreaId(0L);
        paymentGenRawData.setIsPushed(false);
        paymentGenRawData.setPaymentMode("ABBS");
//        paymentGenRawData.setPaymentSource("ABBS");
        paymentGenRawData.setOlt(CommonConstant.COMMON_DATA.OLT);
        paymentGenRawData.setPop(CommonConstant.COMMON_DATA.POP);
        paymentGenRawData.setPaymentSource(creditDocumentData.getReciptNo());
        paymentGenRawDataRepo.save(paymentGenRawData);
    }

    private void addSundryDebtorsinPaymentrawData(CreditDocumentData creditDocumentData) {
        PaymentGenRawData paymentGenRawData = new PaymentGenRawData();
        paymentGenRawData.setPaymentdate(LocalDate.parse(creditDocumentData.getPaymentdate()));
        paymentGenRawData.setAmount(creditDocumentData.getAmount());
        paymentGenRawData.setBranchCode(CommonConstant.COMMON_DATA.BRANCH);
        paymentGenRawData.setBusinessCode(CommonConstant.COMMON_DATA.BU);
        paymentGenRawData.setICCode(CommonConstant.COMMON_DATA.ICCODE);
        paymentGenRawData.setNAVLedgerId(CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS);
        paymentGenRawData.setServiceAreaId(0L);
        paymentGenRawData.setIsPushed(false);
        paymentGenRawData.setPaymentMode("Sundry Debtors");
//        paymentGenRawData.setPaymentSource("ABBS");
        paymentGenRawData.setOlt(CommonConstant.COMMON_DATA.OLT);
        paymentGenRawData.setPop(CommonConstant.COMMON_DATA.POP);
        paymentGenRawData.setPaymentSource(creditDocumentData.getReciptNo());
        paymentGenRawDataRepo.save(paymentGenRawData);
    }
}
