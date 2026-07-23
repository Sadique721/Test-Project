package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMapping;
import lombok.Data;

import java.util.List;

@Data
public class CustomerInvetoryChildParantCustomerDto {

    List<CustomerServiceMapping> childcustomerServiceMappings;
    List<CustomerServiceMapping> parentcustomerServiceMappings;


}
