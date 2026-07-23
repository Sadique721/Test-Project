package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentAPIMappings;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentIntegrationMaster;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.repository.GovernmentIntegrationMasterRepository;
import com.savbill.integrationsystem.SendBiilToGovernment.entity.SendBillToGovernment;
import com.savbill.integrationsystem.SendCreditNoteToGovernment.entity.SendCreditNoteToGovernment;
import com.savbill.integrationsystem.billgen.entity.*;
import com.savbill.integrationsystem.billgen.entity.*;
import com.savbill.integrationsystem.billgen.mapper.CustomerMapper;
import com.savbill.integrationsystem.billgen.mapper.DebitDocumentMapper;
import com.savbill.integrationsystem.billgen.model.CustomerDTO;
import com.savbill.integrationsystem.billgen.model.DebitDocumentDTO;
import com.savbill.integrationsystem.billgen.model.DebitDocumentTAXRelDTO;
import com.savbill.integrationsystem.billgen.repository.*;
import com.savbill.integrationsystem.billgen.repository.*;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionRawData;
import com.savbill.integrationsystem.businessPromotion.entity.BusinessPromotionRawDataRepository;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.utillity.Helper;
import com.savbill.integrationsystem.rabbitmq.CancelRegenerateInvoice;
import com.savbill.integrationsystem.rabbitmq.DebitDocumentMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DebitDocumentService {

    private final DebitDocumentRepo repo;
    private final CustomersDataRepository customersDataRepository;
    private final BillGenRawDataRepository billGenRawDataRepository;
    private final BranchRepository branchRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final BusinessPromotionRawDataRepository businessPromotionRawDataRepository;
    private final GovernmentIntegrationMasterRepository governmentIntegrationMasterRepository;
    private final ChargeDataRepository chargeDataRepository;
    private final DebitDocumentMapper mapper;
    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;

    public List<DebitDocumentDTO> findByIrdSyncAndIsDeleteFalse(String irdSync) {
        List<DebitDocument> list = repo.findByIrdSyncAndIsDeleteFalse(irdSync);
        return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<DebitDocumentDTO> findByDebitdocumentidAndIsDeleteFalse(Integer debitdocid) {
        List<DebitDocument> list = repo.findByIdAndIsDeleteFalse(debitdocid);
        return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Transactional
    public void save(DebitDocumentMessage message) {
        try {
            DebitDocument debitDocument = new DebitDocument(message);
            debitDocument = repo.save(debitDocument);
            DebitDocumentDTO debitDocumentDTO = mapper.domainToDTO(debitDocument, new CycleAvoidingMappingContext());
            CustomerData customersData = customersDataRepository.findById(debitDocument.getCustomerId()).orElse(null);
            Branch branch = null;
            BusinessUnit businessUnit = null;
            if (customersData.getIsorgcust().booleanValue()) {
                CustomerData actualCustomer = customersDataRepository
                        .findCustomerDataByUsername(debitDocument.getCustRefName());
                if (debitDocument.getDebitDocDetailsList().size() > 0 && customersData != null) {
                    for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                        BusinessPromotionRawData businessPromotionRawData = new BusinessPromotionRawData(
                                LocalDate.now(), debitDocument.getStartdate(), debitDocument.getEndate(),
                                "Business Promotion", debitDocument.getDebitdocumentnumber(),
                                actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()),
                                actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(),
                                actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(),
                                debitDocDetails.getChargename(), CommonConstant.COMMON_DATA.BRANCH,
                                CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE,
                                debitDocDetails.getLedgerId(),
                                debitDocDetails.getSubtotal() + debitDocDetails.getDiscount(),
                                debitDocument.getId().longValue(), 0, false,
                                CommonConstant.COMMON_DATA.NAV_LEDGER_FOR_BUSINESS_PROMOTION, null, null);
                        businessPromotionRawDataRepository.save(businessPromotionRawData);
                    }
                }
                if (debitDocument.getDebitDocumentTAXRels().size() > 0 && customersData != null) {
                    for (DebitDocumentTAXRel debitDocumentTAXRel : debitDocument.getDebitDocumentTAXRels()) {
                        BusinessPromotionRawData businessPromotionRawData = new BusinessPromotionRawData(
                                LocalDate.now(), debitDocument.getStartdate(), debitDocument.getEndate(),
                                debitDocumentTAXRel.getTaxname().equalsIgnoreCase("TSC") ? "Business Promotion"
                                        : "TAX" + "-" + debitDocumentTAXRel.getTaxname(),
                                debitDocument.getDebitdocumentnumber(),
                                actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()),
                                actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(),
                                actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(),
                                debitDocumentTAXRel.getTaxname(), CommonConstant.COMMON_DATA.BRANCH,
                                CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE,
                                debitDocumentTAXRel.getTaxname().equalsIgnoreCase("TSC")
                                        ? CommonConstant.COMMON_DATA.NAV_LEDGER_FOR_BUSINESS_PROMOTION
                                        : debitDocumentTAXRel.getTaxLedgerId(),
                                debitDocumentTAXRel.getAmount(), debitDocument.getId().longValue(), 0, false,
                                debitDocumentTAXRel.getTaxname().equalsIgnoreCase("TSC")
                                        ? CommonConstant.COMMON_DATA.NAV_LEDGER_FOR_BUSINESS_PROMOTION
                                        : debitDocumentTAXRel.getTaxLedgerId(), null, null);
                        businessPromotionRawDataRepository.save(businessPromotionRawData);
                    }
                }
                BusinessPromotionRawData businessPromotionRawData = new BusinessPromotionRawData(LocalDate.now(),
                        debitDocument.getStartdate(), debitDocument.getEndate(), "Sundry Debtors",
                        debitDocument.getDebitdocumentnumber(),
                        actualCustomer.getFirstname().concat(" " + actualCustomer.getLastname()),
                        actualCustomer.getUsername(), debitDocument.getBillrunid().longValue(),
                        actualCustomer.getAccountNumber(), actualCustomer.getCustomerType(), "Sundry Debtors",
                        CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU,
                        CommonConstant.COMMON_DATA.ICCODE, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, -debitDocument.getTotalamount(),
                        debitDocument.getId().longValue(), 0, false, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, null, null);
                businessPromotionRawDataRepository.save(businessPromotionRawData);
                makeEntryToBillGen(debitDocument, customersData);
            } else {
                if ((customersData != null ? customersData.getBranch() : null) != null) {
                    branch = branchRepository.findById(customersData.getBranch()).orElse(null);
                }
                if ((customersData != null ? customersData.getBuId() : null) != null) {
                    businessUnit = businessUnitRepository.findById(customersData.getBuId()).orElse(null);
                }
                // Charges entry
                if (debitDocument.getDebitDocDetailsList().size() > 0 && customersData != null) {
                    for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                        ChargeData chargeData = chargeDataRepository.findById(debitDocDetails.getChargeid()).get();
                        BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(),
                                debitDocument.getStartdate(), debitDocument.getEndate(),
                                debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") ? "REVENUE"
                                        : "PREPAID",
                                debitDocument.getDebitdocumentnumber(),
                                customersData.getFirstname().concat(" " + customersData.getLastname()),
                                customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                                customersData.getAccountNumber(), customersData.getCustomerType(),
                                debitDocDetails.getChargename(), branch != null ? branch.getBranch_code() : null,
                                businessUnit != null ? businessUnit.getBucode() : null, debitDocDetails.getIcCode(),
                                debitDocDetails.getLedgerId(),
                                -(debitDocDetails.getSubtotal() + debitDocDetails.getDiscount()),
                                debitDocument.getId().longValue(), customersData.getServicearea().intValue(), false,
                                chargeData.getPushableLedgerId() == null ? chargeData.getLedgerId()
                                        : chargeData.getPushableLedgerId(), customersData.getOlt(), customersData.getPop());
                        billGenRawDataRepository.save(billGenRawData);
                    }

                }
                // Tax entry
                if (debitDocument.getDebitDocumentTAXRels().size() > 0 && customersData != null) {
                    for (DebitDocumentTAXRel debitDocumentTAXRel : debitDocument.getDebitDocumentTAXRels()) {
                        BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(),
                                debitDocument.getStartdate(), debitDocument.getEndate(),
                                "TAX" + "-" + debitDocumentTAXRel.getTaxname(), debitDocument.getDebitdocumentnumber(),
                                customersData.getFirstname().concat(" " + customersData.getLastname()),
                                customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                                customersData.getAccountNumber(), customersData.getCustomerType(),
                                debitDocumentTAXRel.getTaxname(), CommonConstant.COMMON_DATA.BRANCH,
                                CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE,
                                debitDocumentTAXRel.getTaxLedgerId(), -debitDocumentTAXRel.getAmount(),
                                debitDocument.getId().longValue(), 0, false, debitDocumentTAXRel.getTaxLedgerId(), null, null);
                        billGenRawDataRepository.save(billGenRawData);
                    }
                }
                // Disocunt Entry
                if (debitDocument.getDiscount() != null && debitDocument.getDiscount() != 0) {
                    BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(), debitDocument.getStartdate(),
                            debitDocument.getEndate(), "DISCOUNT", debitDocument.getDebitdocumentnumber(),
                            customersData.getFirstname().concat(" " + customersData.getLastname()),
                            customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                            customersData.getAccountNumber(), customersData.getCustomerType(), "Discount",
                            CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU,
                            CommonConstant.COMMON_DATA.ICCODE, "9215200", debitDocument.getDiscount(),
                            debitDocument.getId().longValue(), 0, false, "9215200", null, null);
                    billGenRawDataRepository.save(billGenRawData);
                }

                // Sundry debtors entry
                if (customersData != null) {
                    BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(), debitDocument.getStartdate(),
                            debitDocument.getEndate(), "Sundry Debtors", debitDocument.getDebitdocumentnumber(),
                            customersData.getFirstname().concat(" " + customersData.getLastname()),
                            customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                            customersData.getAccountNumber(), customersData.getCustomerType(), "Sundry Debtors",
                            CommonConstant.COMMON_DATA.BRANCH, CommonConstant.COMMON_DATA.BU,
                            CommonConstant.COMMON_DATA.ICCODE, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, debitDocument.getTotalamount(),
                            debitDocument.getId().longValue(), 0, false, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, null, null);
                    billGenRawDataRepository.save(billGenRawData);
                }
				CustomerDTO customerDTO = customerMapper.domainToDTO(customersData, new CycleAvoidingMappingContext());
				sendBillTOGovernment(customerDTO, debitDocumentDTO, Boolean.TRUE);
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, e.getMessage(), null);
        }
    }

    @Transactional
    public void deleteflag(CancelRegenerateInvoice message) {
        List<BillGenRawData> list1 = billGenRawDataRepository.findBydebitDocId(message.getId());
        for (BillGenRawData billGenRawData : list1) {
            billGenRawData.setIsdelete(true);
            billGenRawDataRepository.save(billGenRawData);
        }
    }

    public void sendBillTOGovernment(CustomerDTO customersDTO, DebitDocumentDTO debitDocumentDTO, Boolean isRealTime) {
        GovernmentIntegrationMaster governmentIntegrationMaster = governmentIntegrationMasterRepository
                .getGovernmentIntegrationMasterByMvnoIdAndIsdeleteFalse(Long.valueOf(customersDTO.getMvnoId()));
        if (governmentIntegrationMaster != null) {
            String url = "https://cbapi.ird.gov.np/api/bill";
            for (GovernmentAPIMappings governmentAPIMappings : governmentIntegrationMaster.getGovernmentAPIMappings()) {
                if (governmentAPIMappings.getApiName()
                        .equalsIgnoreCase(CommonConstant.SEND_OPTIONS_GOVERNMENT.SEND_INVOICE)) {
                    url = governmentAPIMappings.getEndpoint();
                }
            }
            SendBillToGovernment sendBillToGovernment = new SendBillToGovernment();
            sendBillToGovernment.setUsername(governmentIntegrationMaster.getUsername());
            sendBillToGovernment.setPassword(governmentIntegrationMaster.getPassword());
            sendBillToGovernment.setSellerPan(governmentIntegrationMaster.getPan());

            sendBillToGovernment.setBuyerPan(StringUtils.isEmpty(customersDTO.getPan()) ? null : customersDTO.getPan());
            sendBillToGovernment.setBuyerName((customersDTO.getFirstname() + " " + customersDTO.getLastname()).trim());
            sendBillToGovernment.setInvoiceNumber(debitDocumentDTO.getDebitdocumentnumber());
            sendBillToGovernment.setTotalSales(debitDocumentDTO.getTotalamount());
            sendBillToGovernment.setTaxableSalesVat(getTaxableSalesVat(debitDocumentDTO));
            sendBillToGovernment.setVat(getParticularTax(debitDocumentDTO, CommonConstant.TAX_NAME_VAT));
            sendBillToGovernment.setExcisableAmount(0D);
            sendBillToGovernment.setExcise(0D);
            sendBillToGovernment.setTaxableSalesHst(0D);
            sendBillToGovernment.setHst(0D);
            sendBillToGovernment.setAmountForEsf(0D);
            sendBillToGovernment.setEsf(0D);
            sendBillToGovernment.setExportSales(0D);
            sendBillToGovernment.setTaxExemptedSales(0D);
            sendBillToGovernment.setIsrealtime(isRealTime);

            DateTimeFormatter zonedTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");
            sendBillToGovernment.setDatetimeClient(ZonedDateTime.now().format(zonedTimeFormatter));
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                String jsonString = objectMapper.writeValueAsString(sendBillToGovernment);
                HttpUriRequest httpGetForAccessToken = RequestBuilder.post().setUri(url)
                        .setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON)).build();
                log.debug("jsonString: {}", jsonString);
                CloseableHttpResponse result;
                result = client.execute(httpGetForAccessToken);
                HttpEntity entity = result.getEntity();
                String content = EntityUtils.toString(entity);

                System.out.println(
                        "======================================================Request SendBillId "+ debitDocumentDTO.getId() +".================================================================\n");
                System.out.println("Request : " + jsonString);
                System.out.println(
                        "======================================================Request End.================================================================\n");
                System.out.println(
                        "======================================================Response  SendBillId "+ debitDocumentDTO.getId() +".================================================================\n");
                System.out.println("Response : " + content);
                System.out.println(
                        "======================================================Response End.================================================================\n");

                String responseCode = content;
                String irdSync = CommonConstant.IRD_SYNCH_NO;
                if (content.equalsIgnoreCase("101")) {
                    irdSync = CommonConstant.IRD_SYNCH_YES;
                }
                if (content.equalsIgnoreCase("200")) {
                    irdSync = CommonConstant.IRD_SYNCH_YES;
                }
                log.debug("SendBillId: {} irdSync: {} responseCode: {}", debitDocumentDTO.getId(), irdSync, responseCode);
                debitDocumentDTO.setIrdRespCode(responseCode);
                debitDocumentDTO.setIrdSync(irdSync);
				DebitDocument debitDocument = mapper.dtoToDomain(debitDocumentDTO, new CycleAvoidingMappingContext());
				repo.save(debitDocument);
            } catch (Exception e) {
                throw new CustomValidationException(org.springframework.http.HttpStatus.EXPECTATION_FAILED.value(),
                        e.getMessage(), null);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private Double getTaxableSalesVat(DebitDocumentDTO debitDocumentDTO) {
        Double taxableSalesVat = debitDocumentDTO.getTotalamount();
        for (DebitDocumentTAXRelDTO debitDocumentTAXRelDTO : debitDocumentDTO.getDebitDocumentTAXRels()) {
            if (debitDocumentTAXRelDTO.getTaxname().equalsIgnoreCase("VAT")) {
                taxableSalesVat = taxableSalesVat - debitDocumentTAXRelDTO.getAmount();
            }
        }
        return taxableSalesVat;
    }

    private Double getParticularTax(DebitDocumentDTO debitDocumentDTO, String taxName) {
        double taxableSalesVat = 0D;
        for (DebitDocumentTAXRelDTO debitDocumentTAXRelDTO : debitDocumentDTO.getDebitDocumentTAXRels()) {
            if (debitDocumentTAXRelDTO.getTaxname().equalsIgnoreCase(taxName)) {
                taxableSalesVat = taxableSalesVat + debitDocumentTAXRelDTO.getAmount();
            }
        }
        return taxableSalesVat;
    }

    private void makeEntryToBillGen(DebitDocument debitDocument, CustomerData customersData) {
        Branch branch = null;
        BusinessUnit businessUnit = null;
        if ((customersData != null ? customersData.getBranch() : null) != null) {
            branch = branchRepository.findById(customersData.getBranch()).orElse(null);
        }
        if ((customersData != null ? customersData.getBuId() : null) != null) {
            businessUnit = businessUnitRepository.findById(customersData.getBuId()).orElse(null);
        }
        // Charges entry
        if (debitDocument.getDebitDocDetailsList().size() > 0 && customersData != null) {
            for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                ChargeData chargeData = chargeDataRepository.findById(debitDocDetails.getChargeid()).get();
                BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(), debitDocument.getStartdate(),
                        debitDocument.getEndate(), "REVENUE", debitDocument.getDebitdocumentnumber(),
                        customersData.getFirstname().concat(" " + customersData.getLastname()),
                        customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                        customersData.getAccountNumber(), customersData.getCustomerType(),
                        debitDocDetails.getChargename(),
                        branch == null ? CommonConstant.COMMON_DATA.BRANCH : branch.getBranch_code(),
                        businessUnit == null ? CommonConstant.COMMON_DATA.BU : businessUnit.getBucode(),
                        debitDocDetails.getIcCode(), debitDocDetails.getLedgerId(),
                        -(debitDocDetails.getSubtotal() + debitDocDetails.getDiscount()),
                        debitDocument.getId().longValue(), customersData.getServicearea().intValue(), false,
                        debitDocDetails.getLedgerId(), customersData.getOlt(), customersData.getPop());
                billGenRawDataRepository.save(billGenRawData);
            }

        }
        // Tax entry
        if (debitDocument.getDebitDocumentTAXRels().size() > 0 && customersData != null) {
            for (DebitDocumentTAXRel debitDocumentTAXRel : debitDocument.getDebitDocumentTAXRels()) {
                BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(), debitDocument.getStartdate(),
                        debitDocument.getEndate(), "TAX" + "-" + debitDocumentTAXRel.getTaxname(),
                        debitDocument.getDebitdocumentnumber(),
                        customersData.getFirstname().concat(" " + customersData.getLastname()),
                        customersData.getUsername(), debitDocument.getBillrunid().longValue(),
                        customersData.getAccountNumber(), customersData.getCustomerType(),
                        debitDocumentTAXRel.getTaxname(), CommonConstant.COMMON_DATA.BRANCH,
                        CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE,
                        debitDocumentTAXRel.getTaxLedgerId(), -debitDocumentTAXRel.getAmount(),
                        debitDocument.getId().longValue(), 0, false, debitDocumentTAXRel.getTaxLedgerId(), null, null);
                billGenRawDataRepository.save(billGenRawData);
            }
        }
        // Sundry debtors entry
        if (customersData != null) {
            BillGenRawData billGenRawData = new BillGenRawData(LocalDate.now(), debitDocument.getStartdate(),
                    debitDocument.getEndate(), "Sundry Debtors", debitDocument.getDebitdocumentnumber(),
                    customersData.getFirstname().concat(" " + customersData.getLastname()), customersData.getUsername(),
                    debitDocument.getBillrunid().longValue(), customersData.getAccountNumber(),
                    customersData.getCustomerType(), "Sundry Debtors", CommonConstant.COMMON_DATA.BRANCH,
                    CommonConstant.COMMON_DATA.BU, CommonConstant.COMMON_DATA.ICCODE, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS,
                    debitDocument.getTotalamount(), debitDocument.getId().longValue(), 0, false, CommonConstant.COMMON_DATA.NAV_LEDGER_SUNDRY_DEBTORS, null, null);
            billGenRawDataRepository.save(billGenRawData);
        }
    }

    private String convertJson(SendCreditNoteToGovernment sendCreditNoteToGovernment) {
        try {
            return objectMapper.writeValueAsString(sendCreditNoteToGovernment);
        } catch (JsonProcessingException e) {
            log.error("Exception occurred while converting to json", e);
            return "{}";
        }
    }
}
