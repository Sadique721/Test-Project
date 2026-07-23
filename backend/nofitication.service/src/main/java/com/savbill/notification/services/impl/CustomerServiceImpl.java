package com.savbill.notification.services.impl;

import com.savbill.notification.entity.Customers;
import com.savbill.notification.entity.NotificationAudit;
import com.savbill.notification.entity.QNotificationAudit;
import com.savbill.notification.helper.GenericSearchModel;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.rabbitmq.message.CustomMessage;
import com.savbill.notification.repository.CustomerRepository;
import com.savbill.notification.repository.NotificationAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerServiceImpl {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationAuditRepository notificationAuditRepository;

    @Transactional
    public Customers saveSubscriber(CustomMessage message) {
        try {
            Map<String, Object> data = message.getCustomerData();
            if (message.getCustomerData() != null) {
                Customers customer = new Customers();
                List<Customers> customersList =customerRepository.findCustomerByUsername((String) message.getCustomerData().get("username"));
                if(customersList.isEmpty()) {
                    Customers customers = new Customers(message);
                    customer = customerRepository.save(customers);
                }
                else{
                    Customers customers = customersList.get(0);
                    customers.setNotificationEnable((Boolean) message.getCustomerData().get("isNotificationEnable"));
                    customer = customerRepository.save(customers);
                }
                return customer;
            } else {
                throw new RuntimeException("INVALID_CUSTOMER_DATA");

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean isCustomerNotificationEnable(Integer custId) {
        Boolean flag;
        Customers customers = customerRepository.findCustomerByCustId(custId);
        if (Objects.nonNull(customers)) {
            if (customers.getNotificationEnable()) {
                flag = true;
            } else {
                flag = false;
            }
            return flag;

        }
        return false;
    }

    public Boolean isCustomerNotificationEnableByUsername(String username) {
        Boolean flag;
        List<Customers> customers = customerRepository.findCustomerByUsername(username);
        if(!customers.isEmpty()) {
            if (Objects.nonNull(customers.get(0))) {
                if (customers.get(0).getNotificationEnable()) {
                    flag = true;
                } else {
                    flag = false;
                }
                return flag;

            }
            else{
                return  true;
            }
        }
        return true;
    }
    public Boolean isCustomerNotificationEnableByUsernameAndMvnoId(String username,Long mvnoid) {
        Boolean flag;
        Optional<Customers> customers = customerRepository.findAllByUsernameEqualsIgnoreCaseAndMvnoIdEquals(username,mvnoid);
        if(customers.isPresent()) {
            if (Objects.nonNull(customers.get())) {
                if (customers.get().getNotificationEnable()) {
                    flag = true;
                } else {
                    flag = false;
                }
                return flag;

            }
            else{
                return  true;
            }
        }
        return true;
    }

    public Page<NotificationAudit> findAllCustomerNotificationHistory(PaginationRequestDTO requestDTO){

        String username = null;
        List<GenericSearchModel> filters = requestDTO.getFilters();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        if(filters!=null && !filters.isEmpty()){
            GenericSearchModel filter = filters.get(0);
            if ("customer".equalsIgnoreCase(filter.getFilterColumn())) {
                username = filter.getFilterValue();
            }
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        if(username!=null){
            return notificationAuditRepository.findByUsername(username, pageable);
        }else{
            return notificationAuditRepository.findAll(pageable);
        }

    }



}
