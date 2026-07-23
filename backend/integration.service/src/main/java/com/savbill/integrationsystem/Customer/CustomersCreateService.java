package com.savbill.integrationsystem.Customer;



import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.repository.CustomRepository;
import com.savbill.integrationsystem.deviceveri.domain.CustomersData;
import com.savbill.integrationsystem.deviceveri.repository.CustomersRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomersCreateService {
    private static Log log = LogFactory.getLog(CustomersCreateService.class);
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CmsClient cmsClient;


    @Autowired
    private CustomersRepo customersRepo;







//


    public CustomersData saveCustomers(SaveCustomerDataShareMessage message){
        try {
            CustomersData customer = new CustomersData();
            customer.setId(message.getId());
            customer.setTitle(message.getTitle());
            customer.setUsername(message.getUsername());
            customer.setPassword(message.getPassword());
            customer.setFirstname(message.getFirstname());
            customer.setLastname(message.getLastname());
            customer.setEmail(message.getEmail());
            customer.setMobile(message.getMobile());
            customer.setCountryCode(message.getCountryCode());
            customer.setServiceAreaId(message.getServiceAreaId());
            customer.setStatus(message.getStatus());
            customer.setCusttype(message.getCusttype());
            customer.setMvnoId(message.getMvnoId());
            customer.setBuId(message.getBuId());
            customer.setParnterId(message.getParnterId());
            customer.setBlockNo(message.getBlockNo());
            customer = customersRepo.save(customer);
            return customer;

        }catch (Exception e){
            log.error("Error while Creating Customer,"+e.getMessage());

        }
        return null;
    }

    public void updateCustomers(UpdateCustomerShareDataMessage message){
        try {
            CustomersData customer  = customersRepo.findById(message.getId()).orElse(null);
            if (customer != null) {
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                customer.setEmail(message.getEmail());
                customer.setMobile(message.getMobile());
                customer.setCountryCode(message.getCountryCode());
                customer.setServiceAreaId(message.getServiceAreaId());
                customer.setStatus(message.getStatus());
                customer.setCusttype(message.getCusttype());
                customer.setMvnoId(message.getMvnoId());
                customer.setBuId(message.getBuId());
                customer.setParnterId(message.getParnterId());
                customer.setBlockNo(message.getBlockNo());

                // Save the customer using the repository
                try {
                    customersRepo.save(customer);
                }catch (Exception e){
                    e.getMessage();
                }

            }
            else{
                CustomersData customers = new CustomersData();
                customers.setId(message.getId());
                customers.setTitle(message.getTitle());
                customers.setUsername(message.getUsername());
                customers.setPassword(message.getPassword());
                customers.setFirstname(message.getFirstname());
                customers.setLastname(message.getLastname());
                customers.setEmail(message.getEmail());
                customers.setMobile(message.getMobile());
                customers.setCountryCode(message.getCountryCode());
                customers.setServiceAreaId(message.getServiceAreaId());
                customers.setStatus(message.getStatus());
                customers.setCusttype(message.getCusttype());
                customers.setMvnoId(message.getMvnoId());
                customers.setBuId(message.getBuId());
                customers.setParnterId(message.getParnterId());
                customers.setBlockNo(message.getBlockNo());
                customersRepo.save(customers);
            }
        }catch (Exception e){
            log.error("Error while updating Customer,"+e.getMessage());
        }

    }

    public void saveCafToCustomer(CAFCustomerStatusMessage message) throws Exception{
        try {
            CustomersData cafCustomer = customersRepo.findById(message.getCustomerId()).orElse(null);
            if (cafCustomer != null) {
                cafCustomer.setStatus(message.getStatus());
                customersRepo.save(cafCustomer);
                log.info("Successfully convert caf to customer with id " + message.getCustomerId());
            } else {
                log.error("Data not found for convert caf to customer with id " + message.getCustomerId());
            }
        } catch (CustomValidationException e) {
            log.error("Unable to convert caf to customer with id " + message.getCustomerId());
        }
    }
}
