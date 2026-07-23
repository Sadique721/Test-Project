package com.savbill.revenuemanagement.productmanagement.childcustomer.service;

import com.savbill.revenuemanagement.productmanagement.childcustomer.UpdateChildCustometMessesge;
import com.savbill.revenuemanagement.productmanagement.childcustomer.entity.ChildCustomer;
import com.savbill.revenuemanagement.productmanagement.childcustomer.repo.ChildCustomerRepo;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.CafChildCustomerApproveMessege;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappingRel;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ChildCustomerService {
    @Autowired
    private ChildCustomerRepo childCustomerRepo;
    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;
    private Logger log = LoggerFactory.getLogger(ChildCustomerService.class);

//    @Transactional
    public void save(ChildCustomer childCustomer) {
        childCustomerRepo.saveAndFlush(childCustomer);
    }
    @Transactional
    public void update(UpdateChildCustometMessesge data) {
        try {
            Optional<ChildCustomer> optionalCustomer = childCustomerRepo.findById(data.getId());

            if (optionalCustomer.isPresent()) {
                ChildCustomer dbCustomer = optionalCustomer.get();

                // Only update required fields
                dbCustomer.setFirstName(data.getFirstName());
                dbCustomer.setLastName(data.getLastName());
                dbCustomer.setUserName(data.getUserName());
                dbCustomer.setPassword(data.getPassword());
                dbCustomer.setEmail(data.getEmail());
                dbCustomer.setParentCustId(data.getParentCustId());
                dbCustomer.setStatus(data.getStatus());
                dbCustomer.setMobileNumber(data.getMobileNumber());
                dbCustomer.setIsParent(data.getIsParent());

                childCustomerRepo.save(dbCustomer);
                log.info("Child customer updated successfully. ID: {}", data.getId());
            } else {
                log.warn("Child customer not found for ID: {}", data.getId());
            }
        } catch (Exception e) {
            log.error("Error updating child customer with ID: {}. Exception: {}", data.getId(), e.getMessage(), e);
        }
    }

    public void cafChildCustomerApprove(CafChildCustomerApproveMessege data) {
        Long customerId = data.getCustomerId().longValue();
        String newStatus = data.getStatus();
        try {
            Optional<ChildCustomer> optionalCustomer = childCustomerRepo.findById(customerId);

            if (optionalCustomer.isPresent()) {
                ChildCustomer dbCustomer = optionalCustomer.get();
                List<ParentChildMappingRel> mappingList = parentChildMappingRepo.findAllByChildUsernameAndParentCustomerAndMvno(dbCustomer.getUserName(),dbCustomer.getParentCustId(),dbCustomer.getMvnoId());
                if (!CollectionUtils.isEmpty(mappingList)) {
                    mappingList.forEach(mapping -> mapping.setStatus(newStatus));
                    parentChildMappingRepo.saveAll(mappingList);
                    log.info("Updated {} ParentChildMappingRel records with status '{}'", mappingList.size(), newStatus);
                } else {
                    log.info("No ParentChildMappingRel records found for child '{}'", dbCustomer.getUserName());
                }
                dbCustomer.setStatus(newStatus);
                childCustomerRepo.saveAndFlush(dbCustomer);  // Save the change
                log.info("CAF approval processed successfully. Updated status to '{}' for ChildCustomer ID: {}", newStatus, customerId);
            } else {
                log.warn("ChildCustomer not found with ID: {}", customerId);
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing CAF approval for ChildCustomer ID: {}", customerId, e);
        }
    }

}
