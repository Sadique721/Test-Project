package com.savbill.inventorymanagement.modules.ChargeManagement;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveChargeSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateChargeSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeService extends ExBaseAbstractService<ChargePojo, Charge, Integer> {

    public ChargeService(ChargeRepository repository, ChargeMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ChargeService]";
    }

    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    ProductRepository productRepository;
    private static final Logger logger = Logger.getLogger(ChargeService.class);

    public void saveChargeEntity(SaveChargeSharedDataMessage message) throws Exception {
        try {
            Charge charge = new Charge();
            charge.setId(message.getId());
            charge.setName(message.getName());
            charge.setDesc(message.getDesc());
            charge.setCreatedById(message.getCreatedById());
            charge.setLastModifiedById(message.getLastModifiedById());
            charge.setChargetype(message.getChargetype());
            charge.setChargetype(message.getChargetype());
            charge.setPrice(message.getPrice());
            charge.setActualprice(message.getActualprice());
            charge.setTaxId(message.getTaxId());
            charge.setIsDelete(message.getIsDelete());
            charge.setMvnoId(message.getMvnoId());
            charge.setStatus(message.getStatus());
            charge.setTaxamount(message.getTaxamount());
            charge.setChargecategory(message.getChargecategory());
            charge.setIsinventorycharge(message.getIsinventorycharge());
            Charge charge1 = chargeRepository.save(charge);
            if (message.getIsinventorycharge().equals(true)) {
                if (message.getInventoryChargeType().contains(CommonConstants.NEW)) {
                    Product product = productRepository.findById(message.getProductId()).orElse(null);
                    product.setNewProductCharge(charge1);
                    productRepository.save(product);
                } else if (message.getInventoryChargeType().contains(CommonConstants.REFURBISHED)) {
                    Product product = productRepository.findById(message.getProductId()).orElse(null);
                    product.setRefurburshiedProductCharge(charge1);
                    productRepository.save(product);
                }
            }
            logger.info("Charge created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create charge with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateChargeEntity(UpdateChargeSharedDataMessage message) throws Exception {
        try {
            Charge charge = chargeRepository.findById(message.getId()).orElse(null);
            if(charge != null) {
                charge.setId(message.getId());
                charge.setName(message.getName());
                charge.setDesc(message.getDesc());
                charge.setChargetype(message.getChargetype());
                charge.setChargetype(message.getChargetype());
                charge.setPrice(message.getPrice());
                charge.setCreatedById(message.getCreatedById());
                charge.setLastModifiedById(message.getLastModifiedById());
                charge.setActualprice(message.getActualprice());
                charge.setTaxId(message.getTaxId());
                charge.setIsDelete(message.getIsDelete());
                charge.setMvnoId(message.getMvnoId());
                charge.setStatus(message.getStatus());
                charge.setTaxamount(message.getTaxamount());
                charge.setChargecategory(message.getChargecategory());
                charge.setIsinventorycharge(message.getIsinventorycharge());
                Charge charge1 = chargeRepository.save(charge);
                if (message.getIsinventorycharge().equals(true)) {
                    if (message.getInventoryChargeType().contains(CommonConstants.NEW)) {
                        Product product = productRepository.findById(message.getProductId()).orElse(null);
                        product.setNewProductCharge(charge1);
                        productRepository.save(product);
                    } else if (message.getInventoryChargeType().contains(CommonConstants.REFURBISHED)) {
                        Product product = productRepository.findById(message.getProductId()).orElse(null);
                        product.setRefurburshiedProductCharge(charge1);
                        productRepository.save(product);
                    }
                }
                logger.info("Charge updated successfully with name " + message.getName());
            } else {
                Charge charge1 = new Charge();
                charge1.setId(message.getId());
                charge1.setName(message.getName());
                charge1.setDesc(message.getDesc());
                charge1.setCreatedById(message.getCreatedById());
                charge1.setLastModifiedById(message.getLastModifiedById());
                charge1.setChargetype(message.getChargetype());
                charge1.setChargetype(message.getChargetype());
                charge1.setPrice(message.getPrice());
                charge1.setActualprice(message.getActualprice());
                charge1.setTaxId(message.getTaxId());
                charge1.setIsDelete(message.getIsDelete());
                charge1.setMvnoId(message.getMvnoId());
                charge1.setStatus(message.getStatus());
                charge1.setTaxamount(message.getTaxamount());
                charge1.setChargecategory(message.getChargecategory());
                charge1.setIsinventorycharge(message.getIsinventorycharge());
                Charge charge2 = chargeRepository.save(charge1);
                if (message.getIsinventorycharge().equals(true)) {
                    if (message.getInventoryChargeType().contains(CommonConstants.NEW)) {
                        Product product = productRepository.findById(message.getProductId()).orElse(null);
                        product.setNewProductCharge(charge2);
                        productRepository.save(product);
                    } else if (message.getInventoryChargeType().contains(CommonConstants.REFURBISHED) && message.getIsinventorycharge().equals(true)) {
                        Product product = productRepository.findById(message.getProductId()).orElse(null);
                        product.setRefurburshiedProductCharge(charge2);
                        productRepository.save(product);
                    }
                }
                logger.info("Charge updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update charge with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
