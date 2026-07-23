package com.savbill.revenuemanagement.core.service.customeraddress;

import com.savbill.revenuemanagement.core.entity.customers.CustomerAddress;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.QCustomerAddress;
import com.savbill.revenuemanagement.core.repository.customer.CustomerAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerAddressService {
    @Autowired
    CustomerAddressRepository customerAddressRepository;

    public CustomerAddress findByAddressTypeAndCustomer(String addressType, Customers customer, String version) {
        return customerAddressRepository.findByAddressTypeAndCustomerAndVersion(addressType, customer, version);
    }

    public List<CustomerAddress> findAddressByCustomerId(Integer customerId) {
        return customerAddressRepository.findAddressesByCustomerId(customerId);
    }
}
