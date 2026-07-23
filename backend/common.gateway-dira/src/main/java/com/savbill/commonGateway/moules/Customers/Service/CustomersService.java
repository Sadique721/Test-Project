package com.savbill.commonGateway.moules.Customers.Service;


import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveCustomerDataShareMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdateCustomerShareDataMessage;
import com.savbill.commonGateway.common.Interfaces.CmsClient;
import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.repository.CustomRepository;
import com.savbill.commonGateway.moules.Customers.domain.Customers;
import com.savbill.commonGateway.moules.Customers.dto.CustomerDto;
import com.savbill.commonGateway.moules.Customers.repository.CustomerRepository;
import com.savbill.commonGateway.rabbitmq.messages.CAFCustomerStatusMessage;
import com.savbill.commonGateway.rabbitmq.messages.MvnoStatusMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomersService extends AbstractService<Customers, CustomerDto, Integer> {
    private static Log log = LogFactory.getLog(CustomersService.class);
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CmsClient cmsClient;




    public Customers getcustForCwsc(Integer id) {
        Customers customers = super.get(id);
        return customers;
    }

    @Override
    protected JpaRepository<Customers, Integer> getRepository() {
        return null;
    }

    public Customers getByUserName(String uname) throws Exception {
        Customers customers = customerRepository.findByUserName(uname);
        return customers;
    }

//


    public Customers saveCustomers(SaveCustomerDataShareMessage message){
        try {
            Customers customer = new Customers();
            customer.setId(message.getId());
            customer.setTitle(message.getTitle());
            customer.setUsername(message.getUsername());
            customer.setPassword(message.getPassword());
            customer.setFirstname(message.getFirstname());
            customer.setLastname(message.getLastname());
            customer.setCustname(message.getCustname());
            customer.setEmail(message.getEmail());
            customer.setMobile(message.getMobile());
            customer.setCountryCode(message.getCountryCode());
            customer.setServiceAreaId(message.getServiceAreaId());
            customer.setNetworkdevicesId(message.getNetworkdevicesId());
            customer.setStatus(message.getStatus());
            customer.setCusttype(message.getCusttype());
            customer.setPhone(message.getPhone());
            customer.setMvnoId(message.getMvnoId());
            customer.setBuId(message.getBuId());
            customer.setLcoId(message.getLcoId());
            customer.setIs_from_pwc(message.getIs_from_pwc());
            customer.setIsDeleted(message.getIsDeleted());
            customer.setOltportid(message.getOltportid());
            customer.setOltslotid(message.getOltslotid());
            customer.setFullName(message.getFullName());
            customer.setParnterId(message.getParnterId());
            customer.setCalendarType(message.getCalendarType());
            customer.setDunningCategory(message.getDunningCategory());
            customer.setFeasibilityRequired(message.getFeasibilityRequired());
            customer.setValleyType(message.getValleyType());
            customer.setCustomerArea(message.getCustomerArea());
            customer.setCustcategory(message.getCustcategory());
            customer.setBlockNo(message.getBlockNo());
            // Save the customer using the repository
            customer = customerRepository.save(customer);
            return customer;

        }catch (Exception e){
            log.error("Error while Creating Customer,"+e.getMessage());

        }
        return null;
    }

    public void updateCustomers(UpdateCustomerShareDataMessage message){
        try {
            Customers customer  = customerRepository.findById(message.getId()).orElse(null);
            // Set values from message to customer object
            if (customer != null) {
                //customer = new Customers();
                // Set values from message to customer object
                //customer.setId(message.getId());
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                customer.setCustname(message.getCustname() != null ? message.getCustname() : "-");
                customer.setEmail(message.getEmail());
                customer.setMobile(message.getMobile());
                customer.setCountryCode(message.getCountryCode());
                customer.setServiceAreaId(message.getServiceAreaId());
                customer.setNetworkdevicesId(message.getNetworkdevicesId());
                customer.setStatus(message.getStatus());
                customer.setCusttype(message.getCusttype());
                customer.setPhone(message.getPhone());
                customer.setMvnoId(message.getMvnoId());
                customer.setBuId(message.getBuId());
                customer.setLcoId(message.getLcoId());
                customer.setIs_from_pwc(message.getIs_from_pwc());
                customer.setIsDeleted(message.getIsDeleted());
                customer.setOltportid(message.getOltportid());
                customer.setOltslotid(message.getOltslotid());
                customer.setFullName(message.getFullName());
                customer.setParnterId(message.getParnterId());
                customer.setCalendarType(message.getCalendarType());
                customer.setDunningCategory(message.getDunningCategory());
                customer.setFeasibilityRequired(message.getFeasibilityRequired());
                customer.setValleyType(message.getValleyType());
                customer.setCustomerArea(message.getCustomerArea());
                customer.setCustcategory(message.getCustcategory());
                customer.setDunningCategory(message.getDunningCategory());
                customer.setBlockNo(message.getBlockNo());

                // Save the customer using the repository
                try {
                    customerRepository.save(customer);
                }catch (Exception e){
                    e.getMessage();
                }

            }
        }catch (Exception e){
            log.error("Error while updating Customer,"+e.getMessage());
        }

    }

//    public CustomersPojo findById(Integer id) {
//        try {
//            Customers customer = customerRepository.findById(id).get();
//            CustomersPojo customerPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
//            return customerPojo;
//        } catch (Throwable exception) {
//            throw new RuntimeException();
//        }
//
//    }






    @Override
    public Customers get(Integer id) {
        //Customers customers = super.get(id);
        Customers customers = customerRepository.findById(id).orElse(null);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if(customers.getId().equals(1) || customers.getId().equals(2) ){
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1))))
                return customers;
        }else {
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1)) && (customers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(customers.getBuId()))))
                return customers;
        }
        return null;
    }


    public List<Customers> getActiveSubscriberFromUsername(String username) {
        return customerRepository.findByUsernameAndIsDeletedIsFalse(username).stream().filter(cust -> cust != null && !cust.getStatus().equalsIgnoreCase("Terminate")).collect(Collectors.toList());
    }

    public void saveCafToCustomer(CAFCustomerStatusMessage message) throws Exception{
        try {
            Customers cafCustomer = customerRepository.findById(message.getCustomerId()).orElse(null);
            if (cafCustomer != null) {
                cafCustomer.setStatus(message.getStatus());
                customerRepository.save(cafCustomer);
                log.info("Successfully convert caf to customer with id " + message.getCustomerId());
            } else {
                log.error("Data not found for convert caf to customer with id " + message.getCustomerId());
            }
        } catch (CustomValidationException e) {
            log.error("Unable to convert caf to customer with id " + message.getCustomerId());
        }
    }



    public void customerDeactivationWhenMvnoIsInActive(MvnoStatusMessage mvnoStatusMessage) {
        if (!mvnoStatusMessage.getObjectList().isEmpty()) {
            customRepository.updateMvnoStatusForCustomer(mvnoStatusMessage.getObjectList(), mvnoStatusMessage.getStatus(),mvnoStatusMessage.getMvnoDeactivationFlag());
        }
    }


    public GenericDataDTO callWorkFlowInProgressAPI(String Token, Integer mvnoid){
        return  cmsClient.callWorkFlowInProgressFunction(Token,mvnoid);
    }

    public GenericDataDTO getUsedBuildingIds(String token, Integer buidingMgmtId){
        return cmsClient.getUsedBuildingIds(token,buidingMgmtId);
    }
}
