package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerInventoryUtil {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceUtil.class);

    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private CustPlanMapppingRepository planMapppingRepository;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private TaxService taxService;

    public InvoiceDetails prepareInvoiceInventoryDetail(Customers customers, List<CustomerInventoryMapping> inventoryMappings) {
        try {
            DebitDocument debitDocument = new DebitDocument();
            List<DebitDocDetails> debitDocDetailsList = new ArrayList<>();
            for(CustomerInventoryMapping inventoryMapping: inventoryMappings) {
                DebitDocDetails debitDocDetails = setDebitDocDetailsForInventory(inventoryMapping);
                if(debitDocDetails != null) {
                    debitDocDetailsList.add(debitDocDetails);
                }
            }
            debitDocument = setDebitDocBasicDetails(debitDocument, debitDocDetailsList, customers, inventoryMappings.get(0).getId());
            return new InvoiceDetails(debitDocument, debitDocDetailsList, null);
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception during create customer charge invoice: "+ex.getMessage());
        }
        return null;
    }

    public DebitDocument setDebitDocBasicDetails(DebitDocument debitDocument, List<DebitDocDetails> debitDocDetailsList, Customers customers, Long inventoryMappingId) {
        double totalCharge = debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getSubtotal).sum();
        double totalTax = debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getTax).sum();
        double totalDiscount = debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getDiscount).sum();
        double total = totalCharge + totalTax - totalDiscount;//debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getTotalamount).sum();
        debitDocument.setDebitDocDetailsList(debitDocDetailsList);
        debitDocument.setSubtotal(totalCharge);
        debitDocument.setTax(totalTax);
        debitDocument.setTotalamount(total);
        debitDocument.setDiscount(totalDiscount);
        debitDocument.setTotalCustomerDiscount(totalDiscount);
        debitDocument.setBilldate(LocalDateTime.now());
        debitDocument.setAdjustedAmount(0d);
        debitDocument.setCustomer(customers);
        debitDocument.setBillrunstatus(Constants.INVOICE_STATUS.GENERATED.status());
//            debitDocument.setBillrunid(); TODO: need to understand
        debitDocument.setBillableToName(customers.getFullName());//TODO: Need to check
        debitDocument.setInventoryMappingId(inventoryMappingId);
//            debitDocument.setDebitDocumentTAXRels(); TODO: need to add
        debitDocument.setIsDelete(false);
        debitDocument.setBuId(customers.getBuId());
//        debitDocument.setDocnumber(invoiceUtil.getInvoiceNo());
        boolean isLco = false;
        if(customers.getLcoId() != null) {
            isLco = true;
        }
        //debitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumber(isLco, customers.getPartner(), customers.getMvnoId()));
        debitDocument.setDocnumber("");
        debitDocument.setUsedByThread(false);
        debitDocument.setLcoId(customers.getLcoId());
        debitDocument.setPendingAmt(debitDocument.getTotalamount());
        debitDocument.setTotaldue(debitDocument.getTotalamount());
        debitDocument.setCreatedate(LocalDateTime.now());
        if (customers.getCreatedById() != null) {
            debitDocument.setCreatedById(customers.getCreatedById());
        }
        debitDocument.setUpdatedate(LocalDateTime.now());
        debitDocument.setLastModifiedByName(customers.getLastModifiedByName());
        debitDocument.setCreatedByName(customers.getCreatedByName());
        debitDocument.setLastModifiedById(customers.getCreatedById());
        if (customers.getLastModifiedById() != null) {
            debitDocument.setLastModifiedById(customers.getLastModifiedById());
        }
        if(customers.getLastBillDate() != null)
            debitDocument.setFirstbill("N");
        else
            debitDocument.setFirstbill("Y");
        debitDocument.setPaymentStatus(Constants.INVOICE_PAYMENT_STATUS.UNPAID.status());
        debitDocument.setIsDirectChargeInvoice(false);
        if (customers.getCreatedById() != null) {
            debitDocument.setStaffid(customers.getCreatedById());
        }
        debitDocument.setPromiseToPayHoldDays("0");
        debitDocument.setIsPromiseToPayInOldCPR(false);
        debitDocument.setIsCNEnable(false);
      //  debitDocument.setLocalbilldate(debitDocument.getBilldate().getDayOfMonth() + " " + debitDocument.getBilldate().getMonth().name() + " " + debitDocument.getBilldate().getYear());
        if(debitDocument.getStartdate() == null)
            debitDocument.setStartdate(LocalDateTime.now());
        debitDocument.setLocalstartdate(debitDocument.getStartdate().getDayOfMonth() + " " + debitDocument.getStartdate().getMonth().name() + " " + debitDocument.getStartdate().getYear());
        if(debitDocument.getEndate() == null)
            debitDocument.setEndate(LocalDateTime.now());
        debitDocument.setLocalenddate(debitDocument.getEndate().getDayOfMonth() + " " + debitDocument.getEndate().getMonth().name() + " " + debitDocument.getEndate().getYear());
        if(customers.getCurrency() != null){
            String centCurrDynamic = invoiceUtil.getSubunitName(customers.getCurrency());
            debitDocument.setTotalamountinwords(invoiceUtil.convertToAmount((debitDocument.getTotalamount() * 100) / 100, customers.getCurrency(), centCurrDynamic) + " Only");
            debitDocument.setTotaldueinwords(invoiceUtil.convertToAmount(debitDocument.getTotaldue(), customers.getCurrency(), centCurrDynamic) + " Only");
        } else {
            debitDocument.setTotalamountinwords(invoiceUtil.convertToAmount((debitDocument.getTotalamount() * 100) / 100, curr, centCurr) + " Only");
            debitDocument.setTotaldueinwords(invoiceUtil.convertToAmount(debitDocument.getTotaldue(), curr, centCurr) + " Only");
        }
        debitDocument.setPreviousbalance(0.0);
        debitDocument.setLatepaymentfee(0.0);
        debitDocument.setCurrentpayment(0.0);
        debitDocument.setCurrentdebit(0.0);
        return debitDocument;
    }

    public DebitDocDetails setDebitDocDetailsForInventory(CustomerInventoryMapping inventoryMapping) {
        try {
            Optional<Charge> charge = chargeRepository.findById(inventoryMapping.getChargeId().intValue());
            if(!charge.isPresent()) {
                logger.error("charge not available for given mapping id: "+inventoryMapping.getId());
            }
            DebitDocDetails debitDocDetails = new DebitDocDetails();
            if(inventoryMapping.getCustPackId() != null)    {
                Optional<CustPlanMappping> custPlanMappping = planMapppingRepository.findById(inventoryMapping.getCustPackId().intValue());
                custPlanMappping.ifPresent(planMappping -> debitDocDetails.setCustServiceId(Long.valueOf(planMappping.getCustServiceMappingId())));
            }
            debitDocDetails.setDiscountPercentage(inventoryMapping.getDiscount());
            debitDocDetails.setChargename(charge.get().getName());
            debitDocDetails.setChargeid(charge.get().getId());
            debitDocDetails.setDescription(charge.get().getDesc());
            debitDocDetails.setChargetype(charge.get().getChargetype());
            if(inventoryMapping.getExpDate() != null)
                debitDocDetails.setEnddate(inventoryMapping.getExpDate());
            else if(inventoryMapping.getExpiryDateTime() != null)
                debitDocDetails.setEnddate(inventoryMapping.getExpiryDateTime());

            debitDocDetails.setDescription(inventoryMapping.getApprovalRemark());
            Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge.get(), inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setTax(taxAmount);
            debitDocDetails.setDiscount(0d); //TODO: once discount issue fixed in inventory we will add discount;
            debitDocDetails.setOfferPrice(inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setStartdate(inventoryMapping.getAssignedDateTime());
            debitDocDetails.setServiceId(inventoryMapping.getServiceId());
            debitDocDetails.setSubtotal(inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() + debitDocDetails.getTax());
            return debitDocDetails;
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while get inventory detail: "+ex.getMessage());
        }
        return null;
    }

    public TrialInvoiceDetails prepareCafInvoiceInventoryDetail(Customers customers, List<CustomerInventoryMapping> inventoryMappings) {
        try {
            TrialDebitDocument debitDocument = new TrialDebitDocument();
            List<TrialDebitDocumentDetail> debitDocDetailsList = new ArrayList<>();
            for(CustomerInventoryMapping inventoryMapping: inventoryMappings) {
                TrialDebitDocumentDetail debitDocDetails = setTrialDebitDocDetailsForInventory(inventoryMapping);
                if(debitDocDetails != null) {
                    debitDocDetailsList.add(debitDocDetails);
                }
            }
            debitDocument = setTrialDebitDocBasicDetails(debitDocument, debitDocDetailsList, customers, inventoryMappings.get(0).getId());
            return new TrialInvoiceDetails(debitDocument, debitDocDetailsList);
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception during create customer charge invoice: "+ex.getMessage());
        }
        return null;
    }

    public TrialDebitDocumentDetail setTrialDebitDocDetailsForInventory(CustomerInventoryMapping inventoryMapping) {
        try {
            Optional<Charge> charge = chargeRepository.findById(inventoryMapping.getChargeId().intValue());
            if(!charge.isPresent()) {
                logger.error("charge not available for given mapping id: "+inventoryMapping.getId());
            }
            TrialDebitDocumentDetail debitDocDetails = new TrialDebitDocumentDetail();
            if(inventoryMapping.getCustPackId() != null)    {
                Optional<CustPlanMappping> custPlanMappping = planMapppingRepository.findById(inventoryMapping.getCustPackId().intValue());
//                custPlanMappping.ifPresent(planMappping -> debitDocDetails.setCustServiceId(Long.valueOf(planMappping.getCustServiceMappingId())));
            }
//            debitDocDetails.setDiscountPercentage(inventoryMapping.getDiscount());
            debitDocDetails.setChargename(charge.get().getName());
            debitDocDetails.setChargeid(charge.get().getId());

            if(inventoryMapping.getExpDate() != null)
                debitDocDetails.setEnddate(inventoryMapping.getExpDate());
            else if(inventoryMapping.getExpiryDateTime() != null)
                debitDocDetails.setEnddate(inventoryMapping.getExpiryDateTime());

            debitDocDetails.setDescription(inventoryMapping.getApprovalRemark());
            Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge.get(), inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setTax(taxAmount);
            debitDocDetails.setDiscount(0d); //TODO: once discount issue fixed in inventory we will add discount;
//            debitDocDetails.setOfferPrice(inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setStartdate(inventoryMapping.getAssignedDateTime());
//            debitDocDetails.setServiceId(inventoryMapping.getServiceId());
            debitDocDetails.setSubtotal(inventoryMapping.getNewAmount() * inventoryMapping.getQty());
            debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() + debitDocDetails.getTax());
            return debitDocDetails;
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while get inventory detail: "+ex.getMessage());
        }
        return null;
    }

    public TrialDebitDocument setTrialDebitDocBasicDetails(TrialDebitDocument debitDocument, List<TrialDebitDocumentDetail> debitDocDetailsList, Customers customers, Long inventoryMappingId) {
        double totalCharge = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getSubtotal).sum();
        double totalTax = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getTax).sum();
        double totalDiscount = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getDiscount).sum();
        double total = totalCharge + totalTax - totalDiscount;//debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getTotalamount).sum();
        debitDocument.setTrialDebitDocumentDetails(debitDocDetailsList);
        debitDocument.setSubtotal(totalCharge);
        debitDocument.setTax(totalTax);
        debitDocument.setTotalamount(total);
        debitDocument.setDiscount(totalDiscount);
        debitDocument.setBilldate(LocalDateTime.now());
        debitDocument.setCustomer(customers);
        debitDocument.setInventoryMappingId(inventoryMappingId);
        debitDocument.setBillrunstatus(Constants.INVOICE_STATUS.GENERATED.status());
        debitDocument.setBillableToName(customers.getFullName());//TODO: Need to check
        debitDocument.setIsDelete(false);
        Boolean isLco = false;
        if(customers.getLcoId() != null) {
            isLco = true;
        }
        debitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumberForTrial(isLco, customers.getPartner(), customers.getMvnoId()));
        debitDocument.setTotaldue(debitDocument.getTotalamount());
        debitDocument.setCreatedate(LocalDateTime.now());
        debitDocument.setLastModifiedByName(customers.getLastModifiedByName());
        debitDocument.setCreatedByName(customers.getCreatedByName());if(debitDocument.getStartdate() == null)
            debitDocument.setStartdate(LocalDateTime.now());
        if(debitDocument.getEndate() == null)
            debitDocument.setEndate(LocalDateTime.now());
        if(customers.getCurrency() != null){
            String centCurrDynamic = invoiceUtil.getSubunitName(customers.getCurrency());
            debitDocument.setAmountinwords(invoiceUtil.convertToAmount((debitDocument.getTotalamount() * 100) / 100, customers.getCurrency(), centCurrDynamic) + " Only");
            debitDocument.setDueinwords(invoiceUtil.convertToAmount(debitDocument.getTotaldue(), customers.getCurrency(), centCurrDynamic) + " Only");
        } else {
            debitDocument.setAmountinwords(invoiceUtil.convertToAmount((debitDocument.getTotalamount() * 100) / 100, curr, centCurr) + " Only");
            debitDocument.setDueinwords(invoiceUtil.convertToAmount(debitDocument.getTotaldue(), curr, centCurr) + " Only");
        }
        debitDocument.setPreviousbalance(0.0);
        debitDocument.setLatepaymentfee(0.0);
        debitDocument.setCurrentpayment(0.0);
        debitDocument.setCurrentdebit(0.0);
        return debitDocument;
    }
}
