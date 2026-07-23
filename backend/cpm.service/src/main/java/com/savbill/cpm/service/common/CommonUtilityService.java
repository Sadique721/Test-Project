package com.savbill.cpm.service.common;


import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.cpm.modules.InventoryManagement.item.Item;
import com.savbill.cpm.modules.InventoryManagement.item.ItemRepository;
import com.savbill.cpm.pojo.CommonUtility.CustomerCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonUtilityService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;


    public CustomerCredentials getCustomerCredentials(String deviceSerialNumber) throws Exception {
        CustomerCredentials customerCredentials = new CustomerCredentials();
        try {
            List<Item> itemList = itemRepository.findAllItemBySerialNumberAndIsDeletedFalse(deviceSerialNumber);
            if (itemList.size() >= 1) {

                /*
                If in case of, serialNumber is bind with the multiple items,
                the first value in the list will be selected as default unit value
                */

                Item item = itemList.get(0);
                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findByItemIdAndIsDeletedFalse(item.getId());
                if (customerInventoryMapping != null) {
                    Customers customer = customerInventoryMapping.getCustomer();
                    customerCredentials.setDeviceSerialNumber(item.getSerialNumber());
                    customerCredentials.setCustomerPassword(customer.getPassword());
                    customerCredentials.setCustomerUserName(customer.getUsername());
                    ApplicationLogger.logger.info("credentials with device serial number : " + deviceSerialNumber + " fetch successfully !!");

                    return customerCredentials;
                } else {
                    ApplicationLogger.logger.error("Customer with the device serial number : " + deviceSerialNumber + " not found");
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Customer with the device serial number : " + deviceSerialNumber + " not found", null);
                }
            } else {
                ApplicationLogger.logger.error("Item with the device serial number : " + deviceSerialNumber + " not found");
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Item with the device serial number : " + deviceSerialNumber + " not found", null);
            }

        } catch (Exception e) {
            ApplicationLogger.logger.error("Unable to customers fetch the credentials : " + e.getMessage());
        }
        return null;
    }
}
