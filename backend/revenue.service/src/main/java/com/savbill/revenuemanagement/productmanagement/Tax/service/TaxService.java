package com.savbill.revenuemanagement.productmanagement.Tax.service;

import com.savbill.revenuemanagement.core.entity.customers.CustChargeDetails;
import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxTypeSlabRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxTypeTierRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SaveTaxSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdateTaxSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaxService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(TaxService.class);

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    @Autowired
    private TrialDebitDocumentTAXRelRepository trialDebitDocumentTAXRelRepository;

    @Autowired
    TaxRepository taxRepository;
    @Autowired
    TaxTypeTierRepository taxTypeTierRepository;
    @Autowired
    TaxTypeSlabRepository taxTypeSlabRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    public DebitDocumentTAXRel setTaxAmountFromCharge(DebitDocument debitDocument, Integer chargeId, Double discountPercentage, Long docDetailId, String billTo) {
        DebitDocumentTAXRel debitDocumentTAXRel = new DebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setDebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            debitDocumentTAXRel.setPlanName("");
            if(discountPercentage!=null && discountPercentage>0)
                debitDocumentTAXRel.setDiscount(discountPercentage);
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            charge.get().setPrice(debitDocument.getSubtotal());
            debitDocumentTAXRel = setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }


    public DebitDocumentTAXRel setTaxAmountFromCharge1( DebitDocument debitDocument,Integer chargeId,Double discountPercentage,Long docDetailId,String planId) {
        DebitDocumentTAXRel debitDocumentTAXRel = new DebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setDebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            if(planId!=null)
            {
                PostpaidPlan plan=postpaidPlanRepo.findById(Integer.parseInt(planId)).orElse(null);
                debitDocumentTAXRel.setPlanName(plan.getName());
            }
            if(discountPercentage!=null)
                debitDocumentTAXRel.setDiscount(discountPercentage);
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            DebitDocDetails details=debitDocument.getDebitDocDetailsList().stream().filter(x->(docDetailId!=null && x.getDebitdocdetailid().equals(docDetailId.intValue()))).collect(Collectors.toList()).get(0);
            if(details!=null)
                charge.get().setPrice(details.getSubtotal());
            debitDocumentTAXRel =setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }


    public TrialDebitDocumentTAXRel setTaxAmountFromCharge(TrialDebitDocument debitDocument, Integer chargeId, CustomerChargeHistory chargeHistory, Long docDetailId) {
        TrialDebitDocumentTAXRel debitDocumentTAXRel = new TrialDebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setTrialdebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            if(chargeHistory!=null && chargeHistory.getDiscount()!=null && chargeHistory.getDiscount()>0)
                debitDocumentTAXRel.setDiscount(chargeHistory.getDiscount());
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            charge.get().setPrice(debitDocument.getSubtotal());
            setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }


    public TrialDebitDocumentTAXRel setTaxAmountFromCharge1(TrialDebitDocument debitDocument, Integer chargeId,Double discountPercentage,Long docDetailId) {
        TrialDebitDocumentTAXRel debitDocumentTAXRel = new TrialDebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setTrialdebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            if(discountPercentage!=null && discountPercentage>0)
                debitDocumentTAXRel.setDiscount(discountPercentage);
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            TrialDebitDocumentDetail details=debitDocument.getTrialDebitDocumentDetails().stream().filter(x->(docDetailId!=null && x.getDebitdocdetailid().equals(docDetailId.intValue()))).collect(Collectors.toList()).get(0);
            if(details!=null)
                charge.get().setPrice(details.getSubtotal());
            debitDocumentTAXRel = setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }

    /**
     set Debitdoc tax level detail
     * @Author Yogesh
     * @param debitDocumentTAXRel
     * @param charge
     * @return DebitDocumentTAXRel
     */
    public DebitDocumentTAXRel setDebitDocTaxDetails(DebitDocumentTAXRel debitDocumentTAXRel, Charge charge) {
        try {
//            Tax tax = charge.getTax();
            Tax tax = taxRepository.findById(charge.getTax().getId()).get();
            debitDocumentTAXRel.setChargeid(charge.getId());
            debitDocumentTAXRel.setTaxid(tax.getId());
            debitDocumentTAXRel.setTaxname(tax.getName());
            debitDocumentTAXRel.setChargeAmount(charge.getPrice());
            debitDocumentTAXRel.setTaxTypeTiers(tax.getTieredList());
            debitDocumentTAXRel = saveDebitDocTaxLevelDetails1(tax, debitDocumentTAXRel);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getDebitDocTaxDetails: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }

    public TrialDebitDocumentTAXRel setDebitDocTaxDetails(TrialDebitDocumentTAXRel debitDocumentTAXRel, Charge charge) {
        try {
//            Tax tax = charge.getTax();
            Tax tax = taxRepository.findById(charge.getTax().getId()).get();
            debitDocumentTAXRel.setChargeid(charge.getId());
            debitDocumentTAXRel.setTaxid(tax.getId());
            debitDocumentTAXRel.setTaxname(tax.getName());
            debitDocumentTAXRel.setChargeAmount(charge.getPrice());
            debitDocumentTAXRel.setTaxTypeTiers(tax.getTieredList());
            debitDocumentTAXRel = saveDebitDocTaxLevelDetails1(tax, debitDocumentTAXRel);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getDebitDocTaxDetails: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }



    public DebitDocumentTAXRel saveDebitDocTaxLevelDetails1(Tax tax, final DebitDocumentTAXRel debitDocumentTAXRel)
    {
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;
        DebitDocumentTAXRel documentTAXRel = new DebitDocumentTAXRel(debitDocumentTAXRel);

        if(tax.getTaxtype().equalsIgnoreCase("Compound"))
        {
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {

                documentTAXRel.setDebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxData.getTaxLedgerId());
                documentTAXRel.setPercentage(taxData.getRate());
                documentTAXRel.setDescription(tax.getDesc());
                Double price = documentTAXRel.getChargeAmount();


                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((price + totalTax - documentTAXRel.getDiscountAmount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                    documentTAXRel.setDiscountAmount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((price + totalTax - documentTAXRel.getDiscountAmount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((price + totalTax) * (taxData.getRate() / 100.0f));
                    if(!isDiscountCalculated) {
                        Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                        documentTAXRel.setDiscountAmount(discountAmount);
                        isDiscountCalculated=true;
                    }
                    totalTax=totalTax+tier1;
                }

                documentTAXRel.setAmount(tier1);
                documentTAXRel.setTaxname(taxData.getName());
                documentTAXRel.setTaxlevel(1d);
                debitDocumentTAXRelRepository.save(documentTAXRel);
            }
        }
        else if(tax.getTaxtype().equalsIgnoreCase("TIER"))
        {
            List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count=levelOneList.stream().count();
            if(level1Count>0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1=levelOneList.get(0).getBeforeDiscount();

            if(level1Count>1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2=levelOneList.get(1).getBeforeDiscount();

            if(level1Count>2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3=levelOneList.get(2).getBeforeDiscount();
            int count = 0;

            for (TaxTypeTier taxTypeTier:tax.getTieredList())
            {
                count++;

                documentTAXRel.setDebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxTypeTier.getTaxLedgerId());
                documentTAXRel.setPercentage(taxTypeTier.getRate());
                documentTAXRel.setDescription(tax.getDesc());

                Double price = documentTAXRel.getChargeAmount();

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    if(taxTypeTier.getBeforeDiscount() != null)
                        taxTypeTier.setBeforeDiscount(taxTypeTier.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxTypeTier.getBeforeDiscount()))
                    {
                        if(level1Count==1)
                        {
                            Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && (!isBefore1 && !isBefore2))
                        {
                            if(count==1 && !isBefore1 && !isBefore2)
                            {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                            tier1 = ((price - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1- documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && (!isBefore1 && isBefore2))
                        {
                            if(count==1 && !isBefore1 && isBefore2)
                            {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }

                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                    } else {

                        if(level1Count==1)
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            Double discountAmount = (documentTAXRel.getChargeAmount()+tier1) * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                        }

                        if(level1Count==2 && (isBefore1 && isBefore2))
                        {
                            Double tmp=tier1;
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            tmp=tier1+tmp;
                            if(count==2 && isBefore1 && isBefore2)
                            {
                                Double discountAmount = (documentTAXRel.getChargeAmount()+tmp) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if(level1Count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            if(count==1 && isBefore1 && !isBefore2)
                            {
                                Double discountAmount = (documentTAXRel.getChargeAmount()+tier1) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                        }
                    }

                    documentTAXRel.setAmount(tier1);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(1d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    if (taxTypeTier.getBeforeDiscount()!=null && !taxTypeTier.getBeforeDiscount())
                        tier2 = tier2  + ((tier1) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier2);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(2d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    if (taxTypeTier.getBeforeDiscount()!=null && !taxTypeTier.getBeforeDiscount())
                        tier3 = tier3  + ((tier2) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier3);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(3d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }
            }
        }
        return documentTAXRel;
    }



    public TrialDebitDocumentTAXRel saveDebitDocTaxLevelDetails1(Tax tax,  TrialDebitDocumentTAXRel debitDocumentTAXRel)
    {
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;

        if(tax.getTaxtype().equalsIgnoreCase("Compound"))
        {
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;
            Double discountAmount=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {
                TrialDebitDocumentTAXRel documentTAXRel = new TrialDebitDocumentTAXRel(debitDocumentTAXRel);
                if(documentTAXRel.getDiscount()==null)
                    documentTAXRel.setDiscount(0.0);

                documentTAXRel.setDiscountAmount(discountAmount);
                documentTAXRel.setTrialdebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxData.getTaxLedgerId());
                documentTAXRel.setPercentage(taxData.getRate());
                documentTAXRel.setDescription(tax.getDesc());
                Double price = documentTAXRel.getChargeAmount();


                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((price + totalTax - discountAmount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                    documentTAXRel.setDiscountAmount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((price + totalTax - discountAmount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((price + totalTax) * (taxData.getRate() / 100.0f));
                    if(!isDiscountCalculated) {
                        discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                        documentTAXRel.setDiscountAmount(discountAmount);
                        isDiscountCalculated=true;
                    }
                    totalTax=totalTax+tier1;
                }

                documentTAXRel.setAmount(tier1);
                documentTAXRel.setTaxname(taxData.getName());
                documentTAXRel.setTaxlevel(1d);
                debitDocumentTAXRel = trialDebitDocumentTAXRelRepository.save(documentTAXRel);
            }

        } else if(tax.getTaxtype().equalsIgnoreCase("TIER")) {

            List<TaxTypeTier> levelOneList = tax.getTieredList().stream().filter(x -> x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count = levelOneList.stream().count();
            if (level1Count > 0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1 = levelOneList.get(0).getBeforeDiscount();

            if (level1Count > 1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2 = levelOneList.get(1).getBeforeDiscount();

            if (level1Count > 2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3 = levelOneList.get(2).getBeforeDiscount();
            int count = 0;

            TrialDebitDocumentTAXRel documentTAXRel = new TrialDebitDocumentTAXRel(debitDocumentTAXRel);

            for (TaxTypeTier taxTypeTier : tax.getTieredList()) {
                count++;

                documentTAXRel.setTrialdebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxTypeTier.getTaxLedgerId());
                documentTAXRel.setPercentage(taxTypeTier.getRate());
                documentTAXRel.setDescription(tax.getDesc());

                Double price = documentTAXRel.getChargeAmount();

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    if (taxTypeTier.getBeforeDiscount() != null)
                        taxTypeTier.setBeforeDiscount(taxTypeTier.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxTypeTier.getBeforeDiscount())) {
                        if (level1Count == 1) {
                            Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if (level1Count == 2 && (!isBefore1 && !isBefore2)) {
                            if (count == 1 && !isBefore1 && !isBefore2) {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                            tier1 = ((price - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if (level1Count == 2 && count == 2 && (isBefore1 && !isBefore2)) {
                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if (level1Count == 2 && (!isBefore1 && isBefore2)) {
                            if (count == 1 && !isBefore1 && isBefore2) {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }

                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                    } else {

                        if (level1Count == 1) {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            Double discountAmount = (documentTAXRel.getChargeAmount() + tier1) * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                        }

                        if (level1Count == 2 && (isBefore1 && isBefore2)) {
                            Double tmp = tier1;
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            tmp = tier1 + tmp;
                            if (count == 2 && isBefore1 && isBefore2) {
                                Double discountAmount = (documentTAXRel.getChargeAmount() + tmp) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if (level1Count == 2 && (isBefore1 && !isBefore2)) {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            if (count == 1 && isBefore1 && !isBefore2) {
                                Double discountAmount = (documentTAXRel.getChargeAmount() + tier1) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if (level1Count == 2 && count == 2 && (!isBefore1 && isBefore2)) {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                        }
                    }

                    documentTAXRel.setAmount(tier1);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(1d);
                    debitDocumentTAXRel = trialDebitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    if (taxTypeTier.getBeforeDiscount() != null && !taxTypeTier.getBeforeDiscount())
                        tier2 = tier2 + ((tier1) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier2);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(2d);
                    debitDocumentTAXRel = trialDebitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    if (taxTypeTier.getBeforeDiscount() != null && !taxTypeTier.getBeforeDiscount())
                        tier3 = tier3 + ((tier2) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier3);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(3d);
                    debitDocumentTAXRel = trialDebitDocumentTAXRelRepository.save(documentTAXRel);
                }
            }
        }
        return debitDocumentTAXRel;
    }




    /**
     Save Debitdoc tax level detail
     * @Author Yogesh
     * @param tax
     * @param debitDocumentTAXRel
     * @return DebitDocumentTAXRel
     */
    public DebitDocumentTAXRel saveDebitDocTaxLevelDetails(Tax tax, final DebitDocumentTAXRel debitDocumentTAXRel) {
        try {
            List<TaxTypeTier> taxTypeTiers = tax.getTieredList();
            Double price = debitDocumentTAXRel.getChargeAmount();
            Double tier1 = 0.0;
            Double tier2 = 0.0;
            Double tier3 = 0.0;
            for(TaxTypeTier taxTypeTier: taxTypeTiers) {
                DebitDocumentTAXRel documentTAXRel = new DebitDocumentTAXRel(debitDocumentTAXRel);
                documentTAXRel.setTaxLedgerId(taxTypeTier.getTaxLedgerId());
                documentTAXRel.setPercentage(taxTypeTier.getRate());
                documentTAXRel.setDescription(tax.getDesc());
                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    tier1 = ((documentTAXRel.getChargeAmount() + tier1) * (taxTypeTier.getRate() / 100.0f));
                    documentTAXRel.setAmount(tier1);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(1d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                } else if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
                    tier2 = ((tier1) * (taxTypeTier.getRate() / 100.0f));
                    documentTAXRel.setAmount(tier2);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(2d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                } else if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER3") && tier2 != 0) {
                    tier3 = ((tier2) * (taxTypeTier.getRate() / 100.0f));
                    documentTAXRel.setAmount(tier3);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(3d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on saveDebitDocTaxLevelDetails: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }

    /**
     Calculate tax amount from give charge and price
     * @param charge
     * @param price
     * @return
     */
    public Double getTaxAmountFromChargeAndPrice(Charge charge,Double price) {
        Double totalAmount = 0.0;
        if (charge.getTax().getTaxtype().equalsIgnoreCase("Compound")) {
            if(charge.getTax()!=null)
            {
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
                    Double taxAmount = (price  * (tax.getRate() / 100.0f));
                    price=price+taxAmount;
                    totalAmount=totalAmount+taxAmount;
                }
            }
        }
        else if (charge.getTax().getTaxtype().equalsIgnoreCase("TIER")) {
            if (charge.getTax() != null) {
//                Double price = charge.getActualprice();
                Double tier1 = 0.0;
                Double tier2 = 0.0;
                Double tier3 = 0.0;
                for (TaxTypeTier tax : charge.getTax().getTieredList()) {
//                    Double calPrice =
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1")) {
                        tier1 = tier1 + ((price + tier1) * (tax.getRate() / 100.0f));
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER2") && tier1 != 0) {
                        tier2 = tier2 + ((tier1) * (tax.getRate() / 100.0f));
                    }
                    if (tax.getTaxGroup().equalsIgnoreCase("TIER1") && tier2 != 0) {
                        tier3 = tier3 + ((tier2) * (tax.getRate() / 100.0f));
                    }
                }
                totalAmount = tier1 + tier2 + tier3;
            }
        } else if (charge.getTax().getTaxtype().equalsIgnoreCase("SLAB")) {
            //TODO: Update slab tax once done on billing engine
            if (charge.getTax() != null) {
//                Double price = charge.getActualprice();
                for (TaxTypeSlab tax : charge.getTax().getSlabList()) {
                    price = Double.valueOf((price * (tax.getRate() / 100.0f)));
                    totalAmount += price;
                }
            }
        }

        return totalAmount;
    }


    public void calculateTierTax(CustomerChargeHistory chargeHistory, Integer taxId)
    {
        logger.info("Initiating calculateTierTax process for  CustomerChargeHistory ID :  "+ chargeHistory.getId());
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;

        Tax tax=taxRepository.findById(taxId).get();
        logger.info("Tax Id applied to CustomerChargeHistory is TAX ID :  "+ taxId);

        if(tax.getTaxtype().equalsIgnoreCase("Compound")) {
            Double price = chargeHistory.getChargeAmount();
            chargeHistory.setTaxAmount(0d);
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {
                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((price + totalTax - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
                    chargeHistory.setDiscount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((price + totalTax - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((price + totalTax) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                    if(!isDiscountCalculated) {
                        Double discountAmount = (chargeHistory.getChargeAmount()+totalTax) * (chargeHistory.getDiscount() / 100);
                        chargeHistory.setDiscount(discountAmount);
                        isDiscountCalculated=true;
                    }
                }
            }
            chargeHistory.setTaxAmount(totalTax);
        }
        else if(tax.getTaxtype().equalsIgnoreCase("TIER")){
            List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count=levelOneList.stream().count();
            if(level1Count>0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1=levelOneList.get(0).getBeforeDiscount();

            if(level1Count>1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2=levelOneList.get(1).getBeforeDiscount();

            if(level1Count>2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3=levelOneList.get(2).getBeforeDiscount();
            int count = 0;
            Double price = chargeHistory.getChargeAmount();
            Double taxAmount = chargeHistory.getTaxAmount();
            chargeHistory.setTaxAmount(0d);

            for (TaxTypeTier taxData:tax.getTieredList())
            {
                count++;
                if (taxData.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 1 tax");
                    if(taxData.getBeforeDiscount() != null)
                        taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxData.getBeforeDiscount()))
                    {
                        logger.debug("TAX ID :  "+ taxId + " has TaxBeforeDiscount flag as False");
                        if(level1Count==1)
                        {
                            Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
                            chargeHistory.setDiscount(discountAmount);
                            tier1 = ((price + tier1 - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && !isBefore2))
                        {
                            if(count==1 && !isBefore1 && !isBefore2)
                            {
                                Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
                                chargeHistory.setDiscount(discountAmount);
                            }
                            tier1 = ((price - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1- chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && isBefore2))
                        {
                            if(count==1 && !isBefore1 && isBefore2)
                            {
                                Double discountAmount = chargeHistory.getChargeAmount() * (chargeHistory.getDiscount() / 100);
                                chargeHistory.setDiscount(discountAmount);
                            }

                            tier1 = ((price + tier1 - chargeHistory.getDiscount()) * (taxData.getRate() / 100.0f));
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                            price = price-chargeHistory.getDiscount();
                        }
                    } else {

                        if(level1Count==1)
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(tier1);
                            Double discountAmount = (chargeHistory.getChargeAmount()+tier1) * (chargeHistory.getDiscount() / 100);
                            chargeHistory.setDiscount(discountAmount);
                        }

                        if(level1Count==2 && (isBefore1 && isBefore2))
                        {
                            Double tmp=tier1;
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                            tmp=tier1+tmp;
                            if(count==2 && isBefore1 && isBefore2)
                            {
                                Double discountAmount = (chargeHistory.getChargeAmount()+tmp) * (chargeHistory.getDiscount() / 100);
                                chargeHistory.setDiscount(discountAmount);
                            }
                        }

                        if(level1Count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                            if(count==1 && isBefore1 && !isBefore2)
                            {
                                Double discountAmount = (chargeHistory.getChargeAmount()+tier1) * (chargeHistory.getDiscount() / 100);
                                chargeHistory.setDiscount(discountAmount);
                            }
                        }

                        if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier1);
                        }
                    }
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 2 tax");
                    if (taxData.getBeforeDiscount()!=null && !taxData.getBeforeDiscount())
                        tier2 = tier2  + ((tier1) * (taxData.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxData.getRate() / 100.0f));
                    chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier2);
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 3 tax");
                    if (taxData.getBeforeDiscount()!=null && !taxData.getBeforeDiscount())
                        tier3 = tier3  + ((tier2) * (taxData.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxData.getRate() / 100.0f));
                    chargeHistory.setTaxAmount(chargeHistory.getTaxAmount() + tier3);
                }
            }
        }
    }


    public void calculateTierTax(CustChargeDetails chargeDetails, Integer taxId)
    {
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;

        Tax tax=taxRepository.findById(taxId).get();
        Double taxAmount = chargeDetails.getTaxamount();
        chargeDetails.setTaxamount(0d);

        if(tax.getTaxtype().equalsIgnoreCase("Compound")) {
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {
                Double price = chargeDetails.getPrice();
                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((chargeDetails.getPrice() + totalTax - chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    Double discountAmount = chargeDetails.getPrice() * (chargeDetails.getDiscount() / 100);
                    chargeDetails.setDiscount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((chargeDetails.getPrice() + totalTax - chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((chargeDetails.getPrice() + totalTax) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                    if(!isDiscountCalculated) {
                        Double discountAmount = (chargeDetails.getPrice()+totalTax) * (chargeDetails.getDiscount() / 100);
                        chargeDetails.setDiscount(discountAmount);
                        isDiscountCalculated=true;
                    }
                }
            }
            chargeDetails.setTaxamount(totalTax);
        }
        else if (tax.getTaxtype().equalsIgnoreCase("TIER")) {
            List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count=levelOneList.stream().count();
            if(level1Count>0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1=levelOneList.get(0).getBeforeDiscount();

            if(level1Count>1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2=levelOneList.get(1).getBeforeDiscount();

            if(level1Count>2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3=levelOneList.get(2).getBeforeDiscount();
            int count = 0;

            for (TaxTypeTier taxData:tax.getTieredList())
            {
                count++;
                Double price = chargeDetails.getPrice();
                if (taxData.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    if(taxData.getBeforeDiscount() != null)
                        taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxData.getBeforeDiscount()))
                    {
                        if(level1Count==1)
                        {
                            Double discountAmount = chargeDetails.getPrice() * (chargeDetails.getDiscount() / 100);
                            chargeDetails.setDiscount(discountAmount);
                            tier1 = ((price + tier1 - chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && !isBefore2))
                        {
                            if(count==1 && !isBefore1 && !isBefore2)
                            {
                                Double discountAmount = chargeDetails.getPrice() * (chargeDetails.getDiscount() / 100);
                                chargeDetails.setDiscount(discountAmount);
                            }
                            tier1 = ((price +tier1- chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1- chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && isBefore2))
                        {
                            if(count==1 && !isBefore1 && isBefore2)
                            {
                                Double discountAmount = chargeDetails.getPrice() * (chargeDetails.getDiscount() / 100);
                                chargeDetails.setDiscount(discountAmount);
                            }

                            tier1 = ((price + tier1 - chargeDetails.getDiscount()) * (taxData.getRate() / 100.0f));
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }
                    } else {

                        if(level1Count==1)
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            Double discountAmount = (chargeDetails.getPrice()+tier1) * (chargeDetails.getDiscount() / 100);
                            chargeDetails.setDiscount(discountAmount);
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && (isBefore1 && isBefore2))
                        {
                            Double tmp=tier1;
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            tmp=tier1+tmp;
                            if(count==2 && isBefore1 && isBefore2)
                            {
                                Double discountAmount = (chargeDetails.getPrice()+tmp) * (chargeDetails.getDiscount() / 100);
                                chargeDetails.setDiscount(discountAmount);
                            }
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            if(count==1 && isBefore1 && !isBefore2)
                            {
                                Double discountAmount = (chargeDetails.getPrice()+tier1) * (chargeDetails.getDiscount() / 100);
                                chargeDetails.setDiscount(discountAmount);
                            }
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }

                        if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier1);
                        }
                    }
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    if (!taxData.getBeforeDiscount())
                        tier2 = tier2  + ((tier1) * (taxData.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxData.getRate() / 100.0f));
                    chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier2);
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    if (!taxData.getBeforeDiscount())
                        tier3 = tier3  + ((tier2) * (taxData.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxData.getRate() / 100.0f));
                    chargeDetails.setTaxamount(chargeDetails.getTaxamount() + tier3);
                }
            }
        }
    }

    public void saveTaxData(SaveTaxSharedDataMessage saveTaxSharedDataMessage) {
        Tax tax=new Tax();

        tax.setId(saveTaxSharedDataMessage.getId());
        tax.setName(saveTaxSharedDataMessage.getName());
        tax.setDesc(saveTaxSharedDataMessage.getDesc());
        tax.setTaxtype(saveTaxSharedDataMessage.getTaxtype());
        tax.setStatus(saveTaxSharedDataMessage.getStatus());
        tax.setMvnoId(saveTaxSharedDataMessage.getMvnoId());
        tax.setBuId(saveTaxSharedDataMessage.getBuId());
        tax.setIsDelete(saveTaxSharedDataMessage.getIsDelete());
        tax.setCreatedById(saveTaxSharedDataMessage.getCreatedById());
        tax.setLastModifiedById(saveTaxSharedDataMessage.getLastModifiedById());
        tax.setIsDelete(saveTaxSharedDataMessage.getIsDelete());
        tax = taxRepository.save(tax);
        if(saveTaxSharedDataMessage.getTieredList()!=null) {
            Tax finalTax = tax;
            saveTaxSharedDataMessage.getTieredList().stream().forEach(x->{x.setTax(finalTax);});
        }

        if(saveTaxSharedDataMessage.getSlabList()!=null) {
            Tax finalTax = tax;
            saveTaxSharedDataMessage.getSlabList().stream().forEach(x->{x.setTax(finalTax);});
        }

        tax.setTieredList(saveTaxSharedDataMessage.getTieredList());
        tax.setSlabList(saveTaxSharedDataMessage.getSlabList());
        taxRepository.save(tax);
    }

    public void updateTaxData(UpdateTaxSharedDataMessage updateTaxSharedDataMessage) {
        Tax tax=taxRepository.findById(updateTaxSharedDataMessage.getId()).orElse(null);
        tax.setName(updateTaxSharedDataMessage.getName());
        tax.setDesc(updateTaxSharedDataMessage.getDesc());
        tax.setTaxtype(updateTaxSharedDataMessage.getTaxtype());
        tax.setStatus(updateTaxSharedDataMessage.getStatus());
        tax.setMvnoId(updateTaxSharedDataMessage.getMvnoId());
        tax.setBuId(updateTaxSharedDataMessage.getBuId());
        if(updateTaxSharedDataMessage.getTieredList()!=null) {
            updateTaxSharedDataMessage.getTieredList().stream().forEach(x->{x.setTax(tax);});
        }

        if(updateTaxSharedDataMessage.getSlabList()!=null) {
            updateTaxSharedDataMessage.getSlabList().stream().forEach(x->{x.setTax(tax);});
        }
        tax.setTieredList(updateTaxSharedDataMessage.getTieredList());
        tax.setSlabList(updateTaxSharedDataMessage.getSlabList());
        tax.setIsDelete(updateTaxSharedDataMessage.getIsDelete());
        tax.setCreatedById(updateTaxSharedDataMessage.getCreatedById());
        tax.setLastModifiedById(updateTaxSharedDataMessage.getLastModifiedById());
        tax.setIsDelete(updateTaxSharedDataMessage.getIsDelete());
        taxRepository.save(tax);
    }

    public TrialDebitDocumentTAXRel setTrialTaxAmountFromCharge( TrialDebitDocument debitDocument,Integer chargeId,Double discountPercentage,Long docDetailId, String billTo) {
        TrialDebitDocumentTAXRel debitDocumentTAXRel = new TrialDebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setTrialdebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            if(discountPercentage!=null && discountPercentage>0)
                debitDocumentTAXRel.setDiscount(discountPercentage);
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            charge.get().setPrice(debitDocument.getSubtotal());
            setDebitDocTaxDetails(debitDocumentTAXRel, charge.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRel;
    }


    public List<DebitDocumentTAXRel> setTaxAmountFromCharge2( DebitDocument debitDocument,Integer chargeId,Double discountPercentage,Long docDetailId, String billTo,List<DebitDocumentTAXRel> debitDocumentTAXRelList) {
        DebitDocumentTAXRel debitDocumentTAXRel = new DebitDocumentTAXRel();
        try {
            debitDocumentTAXRel.setDebitdocumentid(debitDocument.getId());
            debitDocumentTAXRel.setStartdate(debitDocument.getStartdate());
            debitDocumentTAXRel.setEnddate(debitDocument.getEndate());
            debitDocumentTAXRel.setDocumentDetailId(docDetailId);
            debitDocumentTAXRel.setPlanName("");
            if(discountPercentage!=null && discountPercentage>0)
                debitDocumentTAXRel.setDiscount(discountPercentage);
            else
                debitDocumentTAXRel.setDiscount(0.0);
            Optional<Charge> charge = chargeRepository.findById(chargeId);
            charge.get().setPrice(debitDocument.getSubtotal());
            debitDocumentTAXRelList = setDebitDocTaxDetails2(debitDocumentTAXRel, charge.get(),debitDocumentTAXRelList);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getTaxAmountFromCharge: "+ex.getMessage());
        }
        return debitDocumentTAXRelList;
    }

    public List<DebitDocumentTAXRel> setDebitDocTaxDetails2(DebitDocumentTAXRel debitDocumentTAXRel, Charge charge,List<DebitDocumentTAXRel> debitDocumentTAXRelList) {
        try {
            Tax tax = taxRepository.findById(charge.getTax().getId()).get();
            debitDocumentTAXRel.setChargeid(charge.getId());
            debitDocumentTAXRel.setTaxid(tax.getId());
            debitDocumentTAXRel.setTaxname(tax.getName());
            debitDocumentTAXRel.setChargeAmount(charge.getPrice());
            debitDocumentTAXRel.setTaxTypeTiers(tax.getTieredList());
            debitDocumentTAXRelList = saveDebitDocTaxLevelDetails2(tax, debitDocumentTAXRel,debitDocumentTAXRelList);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception on getDebitDocTaxDetails: "+ex.getMessage());
        }
        return debitDocumentTAXRelList;
    }


    public List<DebitDocumentTAXRel> saveDebitDocTaxLevelDetails2(Tax tax, final DebitDocumentTAXRel debitDocumentTAXRel,List<DebitDocumentTAXRel> debitDocumentTAXRelList)
    {
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;

        if(tax.getTaxtype().equalsIgnoreCase("Compound"))
        {
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;
            Double discountAmount=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {

                DebitDocumentTAXRel documentTAXRel = new DebitDocumentTAXRel(debitDocumentTAXRel);
                if(documentTAXRel.getDiscountAmount()==null)
                    documentTAXRel.setDiscountAmount(0.0);
                if(discountAmount>0.0)
                    documentTAXRel.setDiscountAmount(discountAmount);

                documentTAXRel.setDebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxData.getTaxLedgerId());
                documentTAXRel.setPercentage(taxData.getRate());
                documentTAXRel.setDescription(tax.getDesc());
                Double price = documentTAXRel.getChargeAmount();

                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((price + totalTax - discountAmount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                    documentTAXRel.setDiscountAmount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((price + totalTax - discountAmount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((price + totalTax) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                    if(!isDiscountCalculated) {
                        discountAmount = (documentTAXRel.getChargeAmount() + totalTax)* (documentTAXRel.getDiscount() / 100);
                        documentTAXRel.setDiscountAmount(discountAmount);
                        isDiscountCalculated=true;
                    }
                }

                documentTAXRel.setAmount(tier1);
                documentTAXRel.setTaxname(taxData.getName());
                documentTAXRel.setTaxlevel(1d);
                debitDocumentTAXRelRepository.save(documentTAXRel);
                debitDocumentTAXRelList.add(documentTAXRel);
            }
        }
        else if(tax.getTaxtype().equalsIgnoreCase("TIER"))
        {
            List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count=levelOneList.stream().count();
            if(level1Count>0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1=levelOneList.get(0).getBeforeDiscount();

            if(level1Count>1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2=levelOneList.get(1).getBeforeDiscount();

            if(level1Count>2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3=levelOneList.get(2).getBeforeDiscount();
            int count = 0;


            for (TaxTypeTier taxTypeTier:tax.getTieredList())
            {
                DebitDocumentTAXRel documentTAXRel = new DebitDocumentTAXRel(debitDocumentTAXRel);
                count++;

                documentTAXRel.setDebitdoctaxid(null);
                documentTAXRel.setTaxLedgerId(taxTypeTier.getTaxLedgerId());
                documentTAXRel.setPercentage(taxTypeTier.getRate());
                documentTAXRel.setDescription(tax.getDesc());

                Double price = documentTAXRel.getChargeAmount();

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    if(taxTypeTier.getBeforeDiscount() != null)
                        taxTypeTier.setBeforeDiscount(taxTypeTier.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxTypeTier.getBeforeDiscount()))
                    {
                        if(level1Count==1)
                        {
                            Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && (!isBefore1 && !isBefore2))
                        {
                            if(count==1 && !isBefore1 && !isBefore2)
                            {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                            tier1 = ((price - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1- documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                        if(level1Count==2 && (!isBefore1 && isBefore2))
                        {
                            if(count==1 && !isBefore1 && isBefore2)
                            {
                                Double discountAmount = documentTAXRel.getChargeAmount() * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }

                            tier1 = ((price + tier1 - documentTAXRel.getDiscountAmount()) * (taxTypeTier.getRate() / 100.0f));
                        }

                    } else {

                        if(level1Count==1)
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            Double discountAmount = (documentTAXRel.getChargeAmount()+tier1) * (documentTAXRel.getDiscount() / 100);
                            documentTAXRel.setDiscountAmount(discountAmount);
                        }

                        if(level1Count==2 && (isBefore1 && isBefore2))
                        {
                            Double tmp=tier1;
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            tmp=tier1+tmp;
                            if(count==2 && isBefore1 && isBefore2)
                            {
                                Double discountAmount = (documentTAXRel.getChargeAmount()+tmp) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if(level1Count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                            if(count==1 && isBefore1 && !isBefore2)
                            {
                                Double discountAmount = (documentTAXRel.getChargeAmount()+tier1) * (documentTAXRel.getDiscount() / 100);
                                documentTAXRel.setDiscountAmount(discountAmount);
                            }
                        }

                        if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxTypeTier.getRate() / 100.0f));
                        }
                    }

                    documentTAXRel.setAmount(tier1);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(1d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    if (taxTypeTier.getBeforeDiscount()!=null && !taxTypeTier.getBeforeDiscount())
                        tier2 = tier2  + ((tier1) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier2);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(2d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }

                if (taxTypeTier.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    if (taxTypeTier.getBeforeDiscount()!=null && !taxTypeTier.getBeforeDiscount())
                        tier3 = tier3  + ((tier2) * (taxTypeTier.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxTypeTier.getRate() / 100.0f));

                    documentTAXRel.setAmount(tier3);
                    documentTAXRel.setTaxname(taxTypeTier.getName());
                    documentTAXRel.setTaxlevel(3d);
                    debitDocumentTAXRelRepository.save(documentTAXRel);
                }

                debitDocumentTAXRelList.add(documentTAXRel);
            }
        }

        return debitDocumentTAXRelList;
    }
}
