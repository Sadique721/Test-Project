package com.savbill.partnermanagement.customers;


import com.savbill.partnermanagement.MicroSeviceDataShare.SaveCustomerDataShareMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.UpdateCustomerShareDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private static String MODULE = " [CustomerService] ";

    private static Log log = LogFactory.getLog(CustomerService.class);

    @Autowired
    private CustomersRepository customersRepository;

    public void saveCustomers(SaveCustomerDataShareMessage message){
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
            if(message.getServiceAreaId()!=null)
                customer.setServiceAreaId(message.getServiceAreaId());
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
            customer.setPartnerId(message.getParnterId());
            customer.setCalendarType(message.getCalendarType());
            customer.setDunningCategory(message.getDunningCategory());
            customer.setFeasibilityRequired(message.getFeasibilityRequired());
            customer.setValleyType(message.getValleyType());
            customer.setCustomerArea(message.getCustomerArea());
            customer.setCustcategory(message.getCustcategory());
            customer.setContactperson(message.getFirstname());
            customer.setBlockNo(message.getBlockNo());
            customersRepository.save(customer);
            log.info(MODULE + "Customer Saved Successfully");
        }catch (Exception e){
            log.error("Error while Creating Customer,"+e.getMessage());
        }
    }


    public void updateCustomers(UpdateCustomerShareDataMessage message){
        try {
            Customers customer  = customersRepository.findById(message.getId()).orElse(null);
            if (customer != null) {
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                customer.setCustname(message.getCustname() != null ? message.getCustname() : "-");
                customer.setEmail(message.getEmail());
                customer.setMobile(message.getMobile());
                customer.setCountryCode(message.getCountryCode());
                if(message.getServiceAreaId()!=null)
                    customer.setServiceAreaId(message.getServiceAreaId());
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
                customer.setPartnerId(message.getParnterId());
                customer.setCalendarType(message.getCalendarType());
                customer.setDunningCategory(message.getDunningCategory());
                customer.setFeasibilityRequired(message.getFeasibilityRequired());
                customer.setValleyType(message.getValleyType());
                customer.setCustomerArea(message.getCustomerArea());
                customer.setCustcategory(message.getCustcategory());
                customer.setDunningCategory(message.getDunningCategory());
                customer.setContactperson(message.getFirstname());
                customer.setBlockNo(message.getBlockNo());
                try {
                    customersRepository.save(customer);
                    log.info(MODULE + "Customer Updated Successfully");
                }catch (Exception e){
                    log.error("Error while updating Customer,"+e.getMessage());
                }
            }
        }catch (Exception e){
            log.error("Error while updating Customer,"+e.getMessage());
        }
    }

    public Long getCustomerCount(Integer id) {
        Long count=customersRepository.countByPartnerId(id);
        log.info(MODULE + "Customer Count "+count);
        return count;
    }
}
