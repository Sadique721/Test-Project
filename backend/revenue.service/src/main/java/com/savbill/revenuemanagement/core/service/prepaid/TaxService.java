//package com.savbill.revenuemanagement.core.service.prepaid;
//
//import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
//import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
//import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
//import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentTAXRel;
//import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
//import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
//import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
//import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
//import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
//import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
//import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class TaxService {
//
//    private static final Logger logger = LoggerFactory.getLogger(TaxService.class);
//
//    @Autowired
//    private ChargeRepository chargeRepository;
//
//    @Autowired
//    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;
//
//    @Autowired
//    private TaxRepository taxRepository;
//
//    public DebitDocumentTAXRel setTaxAmountFromCharge( DebitDocument debitDocument,Integer chargeId) {
//        DebitDocumentTAXRel debitDocumentTAXRel = new DebitDocumentTAXRel();
//        try {
//            debitDocumentTAXRel.setDebitdocumentid(debitDocument.getId());
//            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
//            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
//            List<DebitDocDetails> debitDocDetails = debitDocument.getDebitDocDetailsList();
//            Optional<Charge> charge = chargeRepository.findById(chargeId);
//            setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
//        }
//        return debitDocumentTAXRel;
//    }
//
//    /**
//     set Debitdoc tax level detail
//     * @Author Yogesh
//     * @param debitDocumentTAXRel
//     * @param charge
//     * @return DebitDocumentTAXRel
//     */
//    public DebitDocumentTAXRel setDebitDocTaxDetails(DebitDocumentTAXRel debitDocumentTAXRel, Charge charge) {
//        try {
//            Tax tax = charge.getTax();
//            debitDocumentTAXRel.setChargeid(charge.getId());
//            debitDocumentTAXRel.setTaxid(tax.getId());
//            debitDocumentTAXRel.setTaxname(tax.getName());
//            debitDocumentTAXRel.setChargeAmount(charge.getPrice());
//            debitDocumentTAXRel.setTaxTypeTiers(tax.getTieredList());
//            saveDebitDocTaxLevelDetails(tax, debitDocumentTAXRel);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.error("Exception on getDebitDocTaxDetails: "+ex.getMessage());
//        }
//        return debitDocumentTAXRel;
//    }
//
//    /**
//     Save Debitdoc tax level detail
//     * @Author Yogesh
//     * @param tax
//     * @param debitDocumentTAXRel
//     * @return DebitDocumentTAXRel
//     */
//    public DebitDocumentTAXRel saveDebitDocTaxLevelDetails(Tax tax, final DebitDocumentTAXRel debitDocumentTAXRel) {
//        try {
//            List<TaxTypeTier> taxTypeTiers = tax.getTieredList();
//            Double price = debitDocumentTAXRel.getChargeAmount();
//            Double tier1 = 0.0;
//            Double tier2 = 0.0;
//            Double tier3 = 0.0;
//            for(TaxTypeTier taxTypeTier: taxTypeTiers) {
//                DebitDocumentTAXRel documentTAXRel = new DebitDocumentTAXRel(debitDocumentTAXRel);
//                documentTAXRel.setTaxLedgerId(taxTypeTier.getTaxLedgerId());
//                documentTAXRel.setPercentage(taxTypeTier.getRate());
//                documentTAXRel.setDescription(tax.getDesc());
//                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER1")) {
//                    tier1 = ((documentTAXRel.getChargeAmount() + tier1) * (taxTypeTier.getRate() / 100.0f));
//                    documentTAXRel.setAmount(tier1);
//                    documentTAXRel.setTaxname(taxTypeTier.getName());
//                    documentTAXRel.setTaxlevel(1d);
//                    debitDocumentTAXRelRepository.save(documentTAXRel);
//                } else if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
//                    tier2 = ((tier1) * (taxTypeTier.getRate() / 100.0f));
//                    documentTAXRel.setAmount(tier2);
//                    documentTAXRel.setTaxname(taxTypeTier.getName());
//                    documentTAXRel.setTaxlevel(2d);
//                    debitDocumentTAXRelRepository.save(documentTAXRel);
//                } else if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER3") && tier2 != 0) {
//                    tier3 = ((tier2) * (taxTypeTier.getRate() / 100.0f));
//                    documentTAXRel.setAmount(tier3);
//                    documentTAXRel.setTaxname(taxTypeTier.getName());
//                    documentTAXRel.setTaxlevel(3d);
//                    debitDocumentTAXRelRepository.save(documentTAXRel);
//                }
//            }
//        }catch (Exception ex) {
//            ex.printStackTrace();
//            logger.error("Exception on saveDebitDocTaxLevelDetails: "+ex.getMessage());
//        }
//        return debitDocumentTAXRel;
//    }
//
//    /**
//     Calculate tax amount from give charge and price
//     * @param charge
//     * @param price
//     * @return
//     */
//    public Double getTaxAmountFromChargeAndPrice(Charge charge,Double price) {
//        Double totalAmount = 0.0;
//        if (charge.getTax().getTaxtype().equalsIgnoreCase("TIER")) {
//            if (charge.getTax() != null) {
////                Double price = charge.getActualprice();
//                Double tier1 = 0.0;
//                Double tier2 = 0.0;
//                Double tier3 = 0.0;
//                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
////                    Double calPrice =
//                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1")) {
//                        tier1 = tier1 + ((price + tier1) * (tax.getRate() / 100.0f));
//                    }
//                    if (tax.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
//                        tier2 = tier2 + ((tier1) * (tax.getRate() / 100.0f));
//                    }
//                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1") && tier2 != 0) {
//                        tier3 = tier3 + ((tier2) * (tax.getRate() / 100.0f));
//                    }
//                }
//                totalAmount = tier1 + tier2 + tier3;
//            }
//        } else if (charge.getTax().getTaxtype().equalsIgnoreCase("SLAB")) {
//            //TODO: Update slab tax once done on billing engine
//            if (charge.getTax() != null) {
////                Double price = charge.getActualprice();
//                for (TaxTypeSlab tax : charge.getTax().getSlabList()) {
//                    price = Double.valueOf((price * (tax.getRate() / 100.0f)));
//                    totalAmount += price;
//                }
//            }
//        }
//        return totalAmount;
//    }
//
//
//    public void calculateTierTax(CustomerChargeHistory chargeHistory, Integer taxId)
//    {
//        Double calTax = 0.0;
//        Double tier1 = 0.0;
//        Double tier2 = 0.0;
//        Double tier3 = 0.0;
//
//        Boolean isBefore1 = false;
//        Boolean isBefore2 = false;
//        Boolean isBefore3 = false;
//
//        Tax tax=taxRepository.findById(taxId).get();
//
//        List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
//        Long level1Count=levelOneList.stream().count();
//        if(level1Count>0)
//            isBefore1=levelOneList.get(0).getBeforeDiscount();
//
//        if(level1Count>1)
//            isBefore2=levelOneList.get(1).getBeforeDiscount();
//
//        if(level1Count>2)
//            isBefore3=levelOneList.get(2).getBeforeDiscount();
//        int count = 0;
//
//        for (TaxTypeTier taxData:levelOneList)
//        {
//            count++;
//            Double price = chargeHistory.getChargeAmount();
//            if (taxData.getTaxGroup().equalsIgnoreCase("TIER1")) {
//                if (!taxData.getBeforeDiscount())
//                {
//                    if(level1Count==1)
//                    {
//                        Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
//                        chargeHistory.setDiscount(discountAmount);
//                        tier1 = ((price + tier1 - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
//                    }
//
//                    if(level1Count==2 && (!isBefore1 && !isBefore2))
//                    {
//                        if(count==1 && !isBefore1 && !isBefore2)
//                        {
//                            Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
//                            chargeHistory.setDiscount(discountAmount);
//                        }
//                        tier1 = ((price +tier1- chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
//                    }
//
//                    if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
//                    {
//                        tier1 = ((price + tier1- chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
//                    }
//
//                    if(level1Count==2 && (!isBefore1 && isBefore2))
//                    {
//                        if(count==1 && !isBefore1 && isBefore2)
//                        {
//                            Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
//                            chargeHistory.setDiscount(discountAmount);
//                        }
//
//                        tier1 = ((price + tier1 - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
//                    }
//                } else {
//
//                    if(level1Count==1)
//                    {
//                        tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
//                        Double discountAmount = (chargeHistory.getChargeAmount()+tier1) * (chargeHistory.getDiscount() / 100);
//                        chargeHistory.setDiscount(discountAmount);
//                    }
//
//                    if(level1Count==2 && (isBefore1 && isBefore2))
//                    {
//                        Double tmp=tier1;
//                        tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
//                        tmp=tier1+tmp;
//                        if(count==2 && isBefore1 && isBefore2)
//                        {
//                            Double discountAmount = (chargeHistory.getChargeAmount()+tmp) * (chargeHistory.getDiscount() / 100);
//                            chargeHistory.setDiscount(discountAmount);
//                        }
//                    }
//
//                    if(level1Count==2 && (isBefore1 && !isBefore2))
//                    {
//                        tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
//                        if(count==1 && isBefore1 && !isBefore2)
//                        {
//                            Double discountAmount = (chargeHistory.getChargeAmount()+tier1) * (chargeHistory.getDiscount() / 100);
//                            chargeHistory.setDiscount(discountAmount);
//                        }
//                    }
//
//                    if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
//                    {
//                        tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
//                    }
//                }
//            }
//
//            if (taxData.getTaxGroup().equalsIgnoreCase("TIER2")) {
//                if (!taxData.getBeforeDiscount())
//                    tier2 = tier2  + ((tier1) * (taxData.getRate() / 100.0f));
//                else
//                    tier2 = tier2 + ((tier1) * (taxData.getRate() / 100.0f));
//            }
//
//            if (taxData.getTaxGroup().equalsIgnoreCase("TIER3")) {
//                if (!taxData.getBeforeDiscount())
//                    tier3 = tier3  + ((tier2) * (taxData.getRate() / 100.0f));
//                else
//                    tier3 = tier3 + ((tier2) * (taxData.getRate() / 100.0f));
//            }
//
//            calTax = tier1 + tier2 + tier3;
//            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + calTax);
//        }
//    }
//}
