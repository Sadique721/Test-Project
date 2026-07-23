package com.savbill.revenuemanagement.core.service.Inventory;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.entity.inventory.Product;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.inventory.ProductReporsitory;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.CustomerInventoryRevenueMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.ProductMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductReporsitory productRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;

    @Autowired
    private Tracer tracer;

    public void configureProductReceiveMessage(ProductMessage productMessage) {
        Product product = new Product(productMessage);
        if(product.getId() != null && product.getIsUpdate()) {
            //update existing product
            productRepository.save(product);
        } else if(product.getId() != null && product.getIsDeleted()) {
            //delete existing product
            productRepository.save(product);
        } else {
            //save new product
            productRepository.save(product);
        }
    }

    public void configureCustomerInventoryReceiveMessage(CustomerInventoryRevenueMessage message) {
        TraceContext traceContext = tracer.currentSpan().context();
        CustomerInventoryMapping inventoryMapping = new CustomerInventoryMapping(message);
        customerInventoryMappingRepo.save(inventoryMapping);
        CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
        customerBillingMessage.setType(Constants.INVOICE_TYPE.INVENTORY);
        customerBillingMessage.setTraceContext(traceContext);
        Map<String, Object> data = new HashMap<>();
        data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, inventoryMapping.getLoggedInUserId());
        data.put(CustomerBillingMessage.CUST_ID, inventoryMapping.getCustomerId());
        data.put(CustomerBillingMessage.CUSTOMER_INVENTORY_MAPP_ID, Collections.singletonList(inventoryMapping.getId()));
        customerBillingMessage.setData(data);
        messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
    }

}
