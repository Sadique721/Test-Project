package com.savbill.revenuemanagement.KRA;

import com.savbill.revenuemanagement.KRA.Dtos.ETimsCreditNoteDTO;
import com.savbill.revenuemanagement.KRA.Dtos.ETimsCreditNoteItemDTO;
import com.savbill.revenuemanagement.KRA.Dtos.ETimsCreditNoteListDTO;
import com.savbill.revenuemanagement.KRA.Dtos.ETimsInvoiceListDTO;
import com.savbill.revenuemanagement.KRA.Dtos.ETimsSaleDTO;
import com.savbill.revenuemanagement.KRA.Dtos.ETimsSaleItemDTO;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocumentDetail;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocumentDetailRepository;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class KRAUtils {

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;
    @Autowired
    private DebitDocRepository debitDocRepository;
    public void processEtimsAddInvoice(List<DebitDocument> debitDocuments) {
        if (debitDocuments == null || debitDocuments.isEmpty()) {
            return;
        }

        ETimsInvoiceListDTO invoiceListDTO = new ETimsInvoiceListDTO();
        List<ETimsSaleDTO> saleDTOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        for (DebitDocument doc : debitDocuments) {
            if (doc == null || Boolean.TRUE.equals(doc.getIsKraSynced())) {
                continue;
            }

            ETimsSaleDTO saleDTO = new ETimsSaleDTO();
            Customers customer = doc.getCustomer();
            saleDTO.setCustomerNo(String.valueOf(customer.getId())!=null?String.valueOf(customer.getId()):"");
            saleDTO.setCustomerTin(customer.getPan()!=null?customer.getPan():"");
            String fullName = Stream.of(customer.getFirstname(), customer.getLastname())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
            saleDTO.setCustomerName(fullName+" - ("+customer.getAcctno()+")");
            saleDTO.setCustomerMobileNo(customer.getMobile()!=null?customer.getMobile():"");
            saleDTO.setPaymentType("06"); //
            saleDTO.setConfirmDate(LocalDateTime.now().format(formatter) + "000000");
            saleDTO.setSalesDate(doc.getStartdate().format(formatter));
//            saleDTO.setConfirmDate(customer.getUpdatedate().format(formatter)+"000000");
            saleDTO.setMvnoId(customer.getMvnoId());
            saleDTO.setSalesType("N");
            saleDTO.setTraderInvoiceNo(String.valueOf(doc.getId()));
            saleDTO.setStockReleseDate(doc.getStartdate().format(formatter)+"000000");
            saleDTO.setOccurredDate(doc.getStartdate().format(formatter));
            saleDTO.setReceiptPublishDate(LocalDateTime.now().format(formatter)+"000000");
            saleDTO.setInvoiceStatusCode("02");
            saleDTO.setRemark(doc.getDocnumber());
            saleDTO.setIsPurchaseAccept(true);
            saleDTO.setMapping("");
            ETimsSaleItemDTO itemDTO = new ETimsSaleItemDTO();

            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(doc.getId());
            debitDocDetails = debitDocDetails.stream().peek(debitDocDetail ->
            {
                if (debitDocDetail.getDiscount() < 0) {
                    debitDocDetail.setSubtotal(debitDocDetail.getSubtotal() - debitDocDetail.getDiscount());
                    debitDocDetail.setDiscount(0d);
                }
            }).collect(Collectors.toList());
            if (debitDocDetails.isEmpty()) {
                log.warn("No debit document details found for KRA invoice debitDocId: {}", doc.getId());
                continue;
            }
            DebitDocDetails debitDocDetail = debitDocDetails.get(0);
            if (debitDocDetail.getPlanId() != null && !debitDocDetail.getPlanId().trim().isEmpty()) {
                PostpaidPlan plan = postpaidPlanRepo.findPostpaidPlanById(Integer.valueOf(debitDocDetail.getPlanId()));
                itemDTO.setItemCode(plan.getId().toString());
                itemDTO.setItemName(plan.getName());
            } else {
                Charge charge = chargeRepository.findById(debitDocDetail.getChargeid()).orElse(null);
                if (charge == null) {
                    log.warn("Charge not found for KRA invoice debitDocId: {}, chargeId: {}", doc.getId(), debitDocDetail.getChargeid());
                    continue;
                }
                itemDTO.setItemCode(charge.getKraSyncId());
                itemDTO.setItemName(charge.getName());
            }
            itemDTO.setPkgQuantity(1);
            itemDTO.setQuantity(1);
            itemDTO.setTaxTypeCode("B");
            itemDTO.setUnitPrice(doc.getTotalamount());
            itemDTO.setDiscountRate(doc.getDiscount() != null ? doc.getDiscount() : 0.0);
            saleDTO.setSaleItemList(Collections.singletonList(itemDTO));
            log.info("Mapped ETimsSaleDTO for Add Invoice: {}", saleDTO);
            saleDTOList.add(saleDTO);
        }
        if (saleDTOList.isEmpty()) {
            log.info("No unsynced KRA invoices found to send.");
            return;
        }

        invoiceListDTO.setETimsInvoiceListDTO(saleDTOList);

            try {

                kafkaMessageSender.send(new KafkaMessageData(
                        invoiceListDTO,
                        ETimsInvoiceListDTO.class.getSimpleName(),
                        KRAConstant.ADD_INVOICE
                ));
            } catch (Exception e) {
                log.error("Failed to send Kafka message for ADD_INVOICE", e);
            }

    }

    public void processEtimsAddCreditNote(List<CreditDocument> creditDocuments) {
        if (creditDocuments == null || creditDocuments.isEmpty()) {
            return;
        }

        ETimsCreditNoteListDTO creditNoteListDTO = new ETimsCreditNoteListDTO();
        List<ETimsCreditNoteDTO> creditNoteDTOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (CreditDocument doc : creditDocuments) {
            if (doc == null || Boolean.TRUE.equals(doc.getIsKraSynced())) {
                continue;
            }

            ETimsCreditNoteDTO creditNoteDTO = new ETimsCreditNoteDTO();
            Customers customer = doc.getCustomer();


            List<Object[]> debitDocument=debitDocRepository.getKraAndPlanDetails(doc.getInvoiceId());
            Object value = debitDocument.get(0)[1];

            if (value != null) {
                    creditNoteDTO.setOrgInvoiceNo(Integer.valueOf(value.toString()));
            }
            creditNoteDTO.setOrgInvoiceNo(Integer.valueOf(debitDocument.get(0)[0].toString()));
            creditNoteDTO.setTraderInvoiceNo(doc.getId().toString());
            creditNoteDTO.setSalesType("N");
            creditNoteDTO.setPaymentType("06");
            creditNoteDTO.setCreditNoteDate(doc.getPaymentdate().format(formatter)+"000000");
            creditNoteDTO.setConfirmDate(customer.getUpdatedate().format(formatter)+"000000");
            creditNoteDTO.setSalesDate(customer.getCreatedate().format(formatter));
            creditNoteDTO.setStockReleseDate(doc.getPaymentdate().format(formatter)+"000000");
            creditNoteDTO.setReceiptPublishDate(customer.getUpdatedate().format(formatter)+"000000");
            creditNoteDTO.setOccurredDate(doc.getPaymentdate().format(formatter));
            creditNoteDTO.setCreditNoteReason("06");
            creditNoteDTO.setInvoiceStatusCode("02");
            creditNoteDTO.setIsPurchaseAccept(true);
            creditNoteDTO.setRemark(doc.getRemarks());
            creditNoteDTO.setMapping("");
            creditNoteDTO.setMvnoId(customer.getMvnoId());

            ETimsCreditNoteItemDTO itemDTO = new ETimsCreditNoteItemDTO();
            itemDTO.setItemCode((String) debitDocument.get(0)[1]);
            BigDecimal bd = (BigDecimal) debitDocument.get(0)[4];
            itemDTO.setUnitPrice(bd != null ? bd.doubleValue() : null);
            itemDTO.setQuantity(1);
            itemDTO.setDiscountRate(0.0);

            creditNoteDTO.setCreditNoteItemsList(Collections.singletonList(itemDTO));
            log.info("Mapped ETimsCreditNoteDTO for Add Credit Note: {}", creditNoteDTO);
            creditNoteDTOList.add(creditNoteDTO);
        }
        if (creditNoteDTOList.isEmpty()) {
            log.info("No unsynced KRA credit notes found to send.");
            return;
        }

        creditNoteListDTO.setETimsCreditNoteListDTO(creditNoteDTOList);

        try {
            KafkaMessageData kafkaMessageData = new KafkaMessageData(
                    creditNoteListDTO,
                    ETimsCreditNoteListDTO.class.getSimpleName(),
                    KRAConstant.ADD_CREDIT
            );
            kafkaMessageSender.send(kafkaMessageData);
        } catch (Exception e) {
            log.error("Failed to send Kafka message for ADD_CREDIT_NOTE", e);
        }
    }

}


