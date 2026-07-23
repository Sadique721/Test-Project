package com.savbill.partnermanagement.modules.Tax.service;

import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.customers.*;
import com.savbill.partnermanagement.modules.Tax.domain.Tax;
import com.savbill.partnermanagement.modules.Tax.repository.TaxRepository;
import com.savbill.partnermanagement.modules.Tax.repository.TaxTypeSlabRepository;
import com.savbill.partnermanagement.modules.Tax.repository.TaxTypeTierRepository;
import com.savbill.partnermanagement.rabbitmq.product.SaveTaxSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdateTaxSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxService extends AbstractService<Tax, Tax, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TaxService.class);

    @Autowired
    TaxRepository taxRepository;
    @Autowired
    TaxTypeTierRepository taxTypeTierRepository;
    @Autowired
    TaxTypeSlabRepository taxTypeSlabRepository;

    @Override
    protected JpaRepository<Tax, Integer> getRepository() {
        return taxRepository;
    }

    public void saveTaxData(SaveTaxSharedDataMessage saveTaxSharedDataMessage) {
        logger.info("Saving new Tax with ID: {}", saveTaxSharedDataMessage.getId());
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
        logger.info("Tax saved with generated ID: {}", tax.getId());

        if(saveTaxSharedDataMessage.getTieredList()!=null) {
            Tax finalTax = tax;
            saveTaxSharedDataMessage.getTieredList().stream().forEach(x->{x.setTax(finalTax);});
            logger.info("Associated TieredList with Tax ID: {}", tax.getId());
        }

        if(saveTaxSharedDataMessage.getSlabList()!=null) {
            Tax finalTax = tax;
            saveTaxSharedDataMessage.getSlabList().stream().forEach(x->{x.setTax(finalTax);});
            logger.info("Associated SlabList with Tax ID: {}", tax.getId());

        }

        tax.setTieredList(saveTaxSharedDataMessage.getTieredList());
        tax.setSlabList(saveTaxSharedDataMessage.getSlabList());
        taxRepository.save(tax);
        logger.info("Final Tax object saved with TieredList and SlabList, Tax ID: {}", tax.getId());
    }

    public void updateTaxData(UpdateTaxSharedDataMessage updateTaxSharedDataMessage) {
        logger.info("Updating Tax with ID: {}", updateTaxSharedDataMessage.getId());
        Tax tax=taxRepository.findById(updateTaxSharedDataMessage.getId()).orElse(null);
        if (tax == null) {
            logger.warn("Tax not found with ID: {}", updateTaxSharedDataMessage.getId());
            return;
        }
        tax.setName(updateTaxSharedDataMessage.getName());
        tax.setDesc(updateTaxSharedDataMessage.getDesc());
        tax.setTaxtype(updateTaxSharedDataMessage.getTaxtype());
        tax.setStatus(updateTaxSharedDataMessage.getStatus());
        tax.setMvnoId(updateTaxSharedDataMessage.getMvnoId());
        tax.setBuId(updateTaxSharedDataMessage.getBuId());

        if(updateTaxSharedDataMessage.getTieredList()!=null) {
            updateTaxSharedDataMessage.getTieredList().stream().forEach(x->{x.setTax(tax);});
            logger.info("Updated TieredList associated with Tax ID: {}", tax.getId());
        }

        if(updateTaxSharedDataMessage.getSlabList()!=null) {
            updateTaxSharedDataMessage.getSlabList().stream().forEach(x->{x.setTax(tax);});
            logger.info("Updated SlabList associated with Tax ID: {}", tax.getId());

        }
        tax.setTieredList(updateTaxSharedDataMessage.getTieredList());
        tax.setSlabList(updateTaxSharedDataMessage.getSlabList());
        tax.setIsDelete(updateTaxSharedDataMessage.getIsDelete());
        tax.setCreatedById(updateTaxSharedDataMessage.getCreatedById());
        tax.setLastModifiedById(updateTaxSharedDataMessage.getLastModifiedById());
        tax.setIsDelete(updateTaxSharedDataMessage.getIsDelete());
        taxRepository.save(tax);
        logger.info("Tax updated successfully with ID: {}", tax.getId());

    }
}
