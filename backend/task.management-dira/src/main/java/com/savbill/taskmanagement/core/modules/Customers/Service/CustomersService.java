package com.savbill.taskmanagement.core.modules.Customers.Service;


import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.dto.CustomerDto;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Plan.domain.CustPlanMappping;
import com.savbill.taskmanagement.core.modules.Plan.domain.CustPlanMapppingPojo;
import com.savbill.taskmanagement.core.modules.Plan.repository.CustPlanMappingRepository;
import com.savbill.taskmanagement.core.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustPlanMappingRevenue;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustServiceMapppingPojo;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustomerServiceMapping;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustomerServiceMappingRevenue;
import com.savbill.taskmanagement.core.modules.PlanService.repository.CustomerServiceMappingRepository;
import com.savbill.taskmanagement.core.service.AbstractService;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.CAFCustomerStatusMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.ChangePlanMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveCustomerDataShareMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateCustomerShareDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomersService extends AbstractService<Customers, CustomerDto, Integer> {
    private static Log log = LogFactory.getLog(CustomersService.class);
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;


    public Customers getcustForCwsc(Integer id) {
        Customers customers = super.get(id);
        return customers;
    }
    public Customers getcustForEmail(Integer id) {
        Customers customers = customerRepository.findById(id).get();
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

    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    CustPlanMappping custPlanMappping = new CustPlanMappping();
    CustPlanMapppingPojo custPlanMapppingdto = new CustPlanMapppingPojo();

    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
    CustServiceMapppingPojo custServiceMapppingdto = new CustServiceMapppingPojo();


    public void saveCustomers(SaveCustomerDataShareMessage message){
        try {
            Customers customer = new Customers();

            // Set values from message to customer object
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
            customer.setParentCustUsername(message.getParentCustUsername());
            customer.setFeasibilityRequired(message.getFeasibilityRequired());
            customer.setValleyType(message.getValleyType());
            customer.setCustomerArea(message.getCustomerArea());
            customer.setCustcategory(message.getCustcategory());
            customer.setCreatedById(message.getCreatedById());
            customer.setPlanPurchaseType(message.getPlanPurchaseType());
            customer.setLastModifiedById(message.getLastModifiedById());
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (int i = 0; i < message.getCustPlanMapppingList().size(); i++) {

                this.custPlanMapppingdto.setId(message.getCustPlanMapppingList().get(i).getId());
                this.custPlanMapppingdto.setCustid(message.getCustPlanMapppingList().get(i).getCustid());
                this.custPlanMapppingdto.setPlanId(message.getCustPlanMapppingList().get(i).getPlanId());
                this.custPlanMapppingdto.setBillTo(message.getCustPlanMapppingList().get(i).getBillTo());
                this.custPlanMapppingdto.setIsInvoiceToOrg(message.getCustPlanMapppingList().get(i).getIsInvoiceToOrg());
                this.custPlanMapppingdto.setService(message.getCustPlanMapppingList().get(i).getService());
                this.custPlanMapppingdto.setIsDelete(message.getCustPlanMapppingList().get(i).getIsDelete());
                this.custPlanMappping = convertDtoToDomainCustPlanMapping(custPlanMapppingdto);
                custPlanMapppingList.add(custPlanMappping);
            }

            List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
            for (int i = 0; i < message.getCustomerServiceMappingList().size(); i++) {
                this.custServiceMapppingdto.setId(message.getCustomerServiceMappingList().get(i).getId());
                this.custServiceMapppingdto.setCustId(message.getCustomerServiceMappingList().get(i).getCustId());
                this.custServiceMapppingdto.setServiceId(message.getCustomerServiceMappingList().get(i).getServiceId());
                this.customerServiceMapping = convertDtoToDomainCustomerServiceMapping(custServiceMapppingdto);
                customerServiceMappingList.add(customerServiceMapping);
            }


            // Save the customer using the repository
            customerRepository.save(customer);
            custPlanMappingRepository.saveAll(custPlanMapppingList);
            customerServiceMappingRepository.saveAll(customerServiceMappingList);
        }catch (Exception e){
            log.error("Error while Creating Customer,"+e.getMessage());

        }
    }

    public void updateCustomers(UpdateCustomerShareDataMessage message){

        try {
            Customers customer = new Customers();


            customer = customerRepository.findById(message.getId()).orElse(null);
            // Set values from message to customer object

            if (customer != null) {
//                customer = new Customers();

                // Set values from message to customer object
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
                customer.setParentCustUsername(message.getParentCustUsername());
                customer.setFeasibilityRequired(message.getFeasibilityRequired());
                customer.setValleyType(message.getValleyType());
                customer.setCustomerArea(message.getCustomerArea());
                customer.setCustcategory(message.getCustcategory());
                /** Below code comment because plan mapping and service mapping list are getting invalid
                 * and whenever try to save in db, data will be removed from db. **/
//
//                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
//                for (int i = 0; i < message.getCustPlanMapppingList().size(); i++) {
//
//                    this.custPlanMapppingdto.setId(message.getCustPlanMapppingList().get(i).getId());
//                    this.custPlanMapppingdto.setCustid(message.getCustPlanMapppingList().get(i).getCustid());
//                    this.custPlanMapppingdto.setPlanId(message.getCustPlanMapppingList().get(i).getPlanId());
//                    this.custPlanMapppingdto.setBillTo(message.getCustPlanMapppingList().get(i).getBillTo());
//                    this.custPlanMapppingdto.setIsInvoiceToOrg(message.getCustPlanMapppingList().get(i).getIsInvoiceToOrg());
//                    this.custPlanMapppingdto.setService(message.getCustPlanMapppingList().get(i).getService());
//                    this.custPlanMapppingdto.setIsDelete(message.getCustPlanMapppingList().get(i).getIsDelete());
//                    this.custPlanMappping = convertDtoToDomainCustPlanMapping(custPlanMapppingdto);
//                    custPlanMapppingList.add(custPlanMappping);
//                }

//                List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
//                for (int i = 0; i < message.getCustServiceMapppingList().size(); i++) {
//                    this.custServiceMapppingdto.setId(message.getCustServiceMapppingList().get(i).getId());
//                    this.custServiceMapppingdto.setCustId(message.getCustServiceMapppingList().get(i).getCustId());
//                    this.custServiceMapppingdto.setServiceId(message.getCustServiceMapppingList().get(i).getServiceId());
//                    this.customerServiceMapping = convertDtoToDomainCustomerServiceMapping(custServiceMapppingdto);
//                    customerServiceMappingList.add(customerServiceMapping);
//                }


                // Save the customer using the repository
                customerRepository.save(customer);
//                custPlanMappingRepository.saveAll(custPlanMapppingList);
//                customerServiceMappingRepository.saveAll(customerServiceMappingList);
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

    public CustPlanMappping convertDtoToDomainCustPlanMapping(CustPlanMapppingPojo custPlanMapppingPojo) {


        CustPlanMappping custPlanMappping = new CustPlanMappping();
        custPlanMappping.setId(custPlanMapppingPojo.getId());
        custPlanMappping.setCustid(custPlanMapppingPojo.getCustid());
        custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
        custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
        custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
        custPlanMappping.setService(custPlanMapppingPojo.getService());
        custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());


        return custPlanMappping;
    }

    public CustomerServiceMapping convertDtoToDomainCustomerServiceMapping(CustServiceMapppingPojo custServiceMapppingPojo) {


        CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
        customerServiceMapping.setId(custServiceMapppingPojo.getId());
        customerServiceMapping.setCustId(custServiceMapppingPojo.getCustId());
        customerServiceMapping.setServiceId(custServiceMapppingPojo.getServiceId());


        return customerServiceMapping;
    }




    @Override
    public Customers get(Integer id) {
        //Customers customers = super.get(id);
        Customers customers = customerRepository.findById(id).orElse(null);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if(mvnoId==null){
            return customers;
        }
        if(customers.getId().equals(1) || customers.getId().equals(2) ){
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1))))
                return customers;
        }else {
            if (customers != null && ((mvnoId == 1 || (customers.getMvnoId().equals(mvnoId) || customers.getMvnoId() == 1)) && (customers.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(customers.getBuId()))))
                return customers;
        }
        return null;
    }



    public List<Customers> findAllCustomerByEmailAndMvnoId(String email , Long mvnoId){
        List<Customers> customersList = customerRepository.findAllByEmailAndMvnoId(email , mvnoId.intValue());
        return  customersList;
    }

    public List<Customers> findAllCustomerByEmailAndBuId(String email, Long buId){
        List<Customers> customersList = customerRepository.findAllByEmailAndBuId(email,buId);
        return  customersList;
    }

    public List<Customers> findAllCustomerByEmailAndBuIdAndMvnoId(String email, Long buId,Long mvnoId){
        List<Customers> customersList = customerRepository.findAllByEmailAndBuIdAndMvnoId(email,buId,mvnoId.intValue());
        return  customersList;
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
            e.printStackTrace();
        }
    }

    public void saveCustomersPlanAndServiceData(ChangePlanMessage message){
        if (message.getNewCustPlanMappingRevenues().size() > 0) {
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            for (CustPlanMappingRevenue data : message.getNewCustPlanMappingRevenues()) {
                CustPlanMappping custPlanMapping = new CustPlanMappping(data);
                custPlanMapppings.add(custPlanMapping);
            }
            custPlanMappingRepository.saveAll(custPlanMapppings);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (message.getOldCustPlanMappingRevenues().size() > 0) {
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (CustPlanMappingRevenue data : message.getOldCustPlanMappingRevenues()) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(data.getId());
                custPlanMappping.setEndDate(LocalDateTime.parse(data.getEndDate().substring(0, 19), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(data.getExpiryDate().substring(0, 19), formatter));
                custPlanMappping.setCustPlanStatus(data.getCustPlanStatus());
                custPlanMapppings.add(custPlanMappping);
                ids.add(data.getId());
            }
            custPlanMappingRepository.saveAll(custPlanMapppings);
        }


        if (message.getCustomerServiceMappingRevenues().size() > 0) {
            List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
            for (CustomerServiceMappingRevenue data : message.getCustomerServiceMappingRevenues()) {
                CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping(data);
                customerServiceMappings.add(customerServiceMapping);
            }
            customerServiceMappingRepository.saveAll(customerServiceMappings);
        }
    }




}
