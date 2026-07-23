package com.savbill.inventorymanagement.modules.TaxManagement.Tax;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanCharge;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanChargeRepo;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.QPostpaidPlanCharge;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab.TaxTypeSlab;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxTier.TaxTypeTier;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveTaxSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateTaxSharedDataMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaxService extends ExBaseAbstractService<TaxPojo, Tax, Integer> {

    public TaxService(TaxRepository repository, TaxMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TaxService]";
    }

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    PostpaidPlanChargeRepo planChargeRepo;

    private static final Logger logger = Logger.getLogger(TaxService.class);

    public void saveTaxEntity(SaveTaxSharedDataMessage message) {
        try {
            Tax tax = new Tax();
            tax.setId(message.getId());
            tax.setName(message.getName());
            tax.setDesc(message.getDesc());
            tax.setCreatedById(message.getCreatedById());
            tax.setLastModifiedById(message.getLastModifiedById());
            tax.setTaxtype(message.getTaxtype());
            tax.setStatus(message.getStatus());
            tax.setMvnoId(message.getMvnoId());
            tax.setBuId(message.getBuId());
            tax.setTieredList(setTaxType(message.getTieredList(), Long.valueOf(message.getId())));
            tax.setSlabList(setTaxSlab(message.getSlabList(), Long.valueOf(message.getId())));
            tax.setIsDelete(message.getIsDelete());
            taxRepository.save(tax);
            logger.info("Tax created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create tax with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateTaxEntity(UpdateTaxSharedDataMessage message) {
        try {
            Tax tax = taxRepository.findById(message.getId()).orElse(null);
            if (tax != null) {
                tax.setId(message.getId());
                tax.setName(message.getName());
                tax.setCreatedById(message.getCreatedById());
                tax.setLastModifiedById(message.getLastModifiedById());
                tax.setDesc(message.getDesc());
                tax.setTaxtype(message.getTaxtype());
                tax.setStatus(message.getStatus());
                tax.setMvnoId(message.getMvnoId());
                tax.setBuId(message.getBuId());
                tax.setTieredList(setTaxType(message.getTieredList(), Long.valueOf(message.getId())));
                tax.setSlabList(setTaxSlab(message.getSlabList(), Long.valueOf(message.getId())));
                tax.setIsDelete(message.getIsDelete());
                taxRepository.save(tax);
                logger.info("Tax updated successfully with name " + message.getName());
            } else {
                Tax tax1 = new Tax();
                tax1.setId(message.getId());
                tax1.setName(message.getName());
                tax1.setDesc(message.getDesc());
                tax1.setCreatedById(message.getCreatedById());
                tax1.setLastModifiedById(message.getLastModifiedById());
                tax1.setTaxtype(message.getTaxtype());
                tax1.setStatus(message.getStatus());
                tax1.setMvnoId(message.getMvnoId());
                tax1.setBuId(message.getBuId());
                tax1.setTieredList(setTaxType(message.getTieredList(), Long.valueOf(message.getId())));
                tax1.setSlabList(setTaxSlab(message.getSlabList(), Long.valueOf(message.getId())));
                tax1.setIsDelete(message.getIsDelete());
                taxRepository.save(tax1);
                logger.info("Tax updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update tax details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public List<TaxTypeTier> setTaxType(List<TaxTypeTier> taxTypeTiers, Long taxId) {
        List<TaxTypeTier> taxTypeTierList = new ArrayList<>();
        for (TaxTypeTier item : taxTypeTiers) {
            TaxTypeTier taxTypeTier = new TaxTypeTier();
            taxTypeTier.setId(item.getId());
            taxTypeTier.setTaxid(taxId);
            taxTypeTier.setTaxGroup(item.getTaxGroup());
            taxTypeTier.setTaxLedgerId(item.getTaxLedgerId());
            taxTypeTier.setRate(item.getRate());
            taxTypeTier.setName(item.getName());
            taxTypeTier.setIsDelete(item.getIsDelete());
            taxTypeTier.setBeforeDiscount(item.getBeforeDiscount());
            taxTypeTier.setTaxLedgerId(item.getTaxLedgerId());
            taxTypeTierList.add(taxTypeTier);
        }
        return taxTypeTierList;
    }

    public List<TaxTypeSlab> setTaxSlab(List<TaxTypeSlab> taxTypeSlabs, Long taxId) {
        List<TaxTypeSlab> taxTypeSlabList = new ArrayList<>();
        for (TaxTypeSlab item : taxTypeSlabs) {
            TaxTypeTier taxTypeTier = new TaxTypeTier();
            taxTypeTier.setId(item.getId());
            taxTypeTier.setTaxid(taxId);
            taxTypeTier.setRate(item.getRate());
            taxTypeTier.setName(item.getName());
            taxTypeTier.setBeforeDiscount(item.getBeforeDiscount());
        }
        return taxTypeSlabList;
    }
    public Double getPriceWithoutTax(int taxId,Long priceWithTax){
        Optional<Tax> newProducttaxO= taxRepository.findById(taxId);
        Double newPriceWithoutTax= Double.valueOf(priceWithTax);
        if(newProducttaxO.isPresent())
        {
            Tax newProducttax=newProducttaxO.get();
            List<TaxTypeTier> taxTypeTiers = new ArrayList<>();
            taxTypeTiers.addAll(newProducttax.getTieredList());
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            Double newProducttaxRate=taxTypeTiers.get(0).getRate();
            newPriceWithoutTax= priceWithTax*100/(100+newProducttaxRate);
        }
        return newPriceWithoutTax;
    }

    public Double getTaxPer(Charge charge) {
        Optional<Tax>  primaryTax = taxRepository.findById(charge.getTaxId());
        if(primaryTax.isPresent())
        {
            Tax tierTax=primaryTax.get();
            List<TaxTypeTier> taxTypeTiers = new ArrayList<>();
            taxTypeTiers.addAll(tierTax.getTieredList());
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            return taxTypeTiers.get(0).getRate();
        }
        return 0d;
    }

    public Double getPriceWithoutTax(int taxId,Double priceWithTax){
        Optional<Tax> newProducttaxO= taxRepository.findById(taxId);
        Double newPriceWithoutTax= priceWithTax;
        if(newProducttaxO.isPresent())
        {
            Tax newProducttax=newProducttaxO.get();
            List<TaxTypeTier> taxTypeTiers = new ArrayList<>();
            taxTypeTiers.addAll(newProducttax.getTieredList());
            //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
            Double newProducttaxRate=taxTypeTiers.get(0).getRate();
            newPriceWithoutTax= priceWithTax*100/(100+newProducttaxRate);
        }
        return newPriceWithoutTax;
    }

    public Double getTaxAmountFromCharge(Charge charge,Integer planId) {
        Double totalAmount = 0.0;
        Tax taxEntity = taxRepository.findById(charge.getTaxId()).get();
        if (taxEntity.getTaxtype().equalsIgnoreCase("TIER")) {
            if (charge.getTaxId() != null) {
                Double price = getChargeAmount(charge.getId(),planId,charge.getActualprice());
                Double tier1 = 0.0;
                Double tier2 = 0.0;
                Double tier3 = 0.0;
                for (TaxTypeTier tax : taxEntity.getTieredList()) {
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
        } else if (taxEntity.getTaxtype().equalsIgnoreCase("SLAB")) {
            //TODO: Update slab tax once done on billing engine
            if (charge.getTaxId() != null) {
                Double price = getChargeAmount(charge.getId(),planId,charge.getActualprice());
                for (TaxTypeSlab tax : taxEntity.getSlabList()) {
                    price = Double.valueOf((price * (tax.getRate() / 100.0f)));
                    totalAmount += price;
                }
            }
        }
        return totalAmount;
    }

    public Double getChargeAmount(Integer chargeId,Integer planId,Double actualPrice)
    {
        if(chargeId!=null && planId!=null)
        {
            QPostpaidPlanCharge qPostpaidPlanCharge=QPostpaidPlanCharge.postpaidPlanCharge;
            BooleanExpression expression=qPostpaidPlanCharge.isNotNull();
            expression=expression.and(qPostpaidPlanCharge.charge.id.eq(chargeId)).and(qPostpaidPlanCharge.plan.id.eq(planId));
            List<PostpaidPlanCharge> list= (List<PostpaidPlanCharge>) planChargeRepo.findAll(expression);
            if(list!=null && !list.isEmpty())
            {
                if(list.get(0).getChargeprice()!=null && list.get(0).getChargeprice()!=0)
                    return list.get(0).getChargeprice();
            }
        }
        return actualPrice;
    }
}
