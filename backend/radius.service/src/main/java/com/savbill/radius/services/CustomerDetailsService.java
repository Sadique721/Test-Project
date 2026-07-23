package com.savbill.radius.services;

import com.savbill.radius.SoapApi.Dto.GenericDataDTO;
import com.savbill.radius.dto.CustomerDetailsDto;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.repository.CustQuotaDetailsRepository;
import com.savbill.radius.repository.CustomersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomerDetailsService {


    @Autowired
    CustomersRepository customerRepository;
    @Autowired
    CustQuotaDetailsRepository custQuotaDetailsRepository;

    public CustomerDetailsDto GetCustomerDetails(String username , Integer mvnoId) {
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();
        log.debug("In Get Customer Details: " + username);

        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<Object[]> customerDetails = customerRepository.findByUsernameAndMvno(username , mvnoId);
            Customers customerDetails = customerRepository.findCustomerDetailsByUsernameAndMvnoIdAndStatus(username, mvnoId, "ACTIVE");

            if (customerDetails == null) {
                log.warn("No customer found for username: " + username);
                Integer responsecode = 404;
                String responseMessage = "Customer not available or inactive";
                customerDetailsDto.setResponseCode(responsecode);
                customerDetailsDto.setResponseMessage(responseMessage);
                return customerDetailsDto;
            }
//        List<CustomerDetailsDto> custQuotaDetails = custQuotaDetailsRepository.getCustomerPlanDetails((Integer) customerDetails.get(0)[0]);
            List<Object[]> custQuotaDetails = custQuotaDetailsRepository.getCustomerPlanDetails(customerDetails.getId());
            if (custQuotaDetails.isEmpty()) {
                log.warn("No quota details found for customer ID: " + customerDetails.getId());
                Integer responsecode = 404;
                String responseMessage = "No quota details found for customer";
                customerDetailsDto.setResponseCode(responsecode);
                customerDetailsDto.setResponseMessage(responseMessage);
                return customerDetailsDto;
            }

            List<CustomerDetailsDto.PlanDetails> planDetailsList = new ArrayList<>();

            Integer responsecode = 200;
            String responseMessage = "SUCCESS";
            customerDetailsDto.setResponseCode(responsecode);
            customerDetailsDto.setResponseMessage(responseMessage);
            customerDetailsDto.setMacAddress(customerDetails.getMACADDRESS() != null ? customerDetails.getMACADDRESS().toString() : null);
            customerDetailsDto.setFramedIp(customerDetails.getFramedIp() != null ? customerDetails.getFramedIp().toString() : null);
            customerDetailsDto.setName(customerDetails.getFullName() != null ? customerDetails.getFullName().toString() : null);
            customerDetailsDto.setUsername(customerDetails.getUsername() != null ? customerDetails.getUsername().toString() : null);
            customerDetailsDto.setCafno(customerDetails.getCafno() != null ? customerDetails.getCafno().toString() : null);
            customerDetailsDto.setCusttype(customerDetails.getCusttype() != null ? customerDetails.getCusttype().toString() : null);
            customerDetailsDto.setAdditionalPolicy(customerDetails.getBillday() != null ? customerDetails.getBillday() : null);
            customerDetailsDto.setFirstActivationDate(customerDetails.getFirstActivationDate() != null ? customerDetails.getFirstActivationDate() : null);
            customerDetailsDto.setMobile(customerDetails.getMobile() != null ? customerDetails.getMobile().toString() : null);
            customerDetailsDto.setFax(customerDetails.getFax() != null ? customerDetails.getFax().toString() : null);
            customerDetailsDto.setEmail(customerDetails.getEmail() != null ? customerDetails.getEmail().toString() : null);
            customerDetailsDto.setCustcategory(customerDetails.getCustcategory() != null ? customerDetails.getCustcategory().toString() : null);
            customerDetailsDto.setExpirydate(customerDetails.getExpirydate() != null ? customerDetails.getExpirydate().toString() : null);
            customerDetailsDto.setContactperson(customerDetails.getContactperson() != null ? customerDetails.getContactperson().toString() : null);
            customerDetailsDto.setCUI(customerDetails.getAcctno() != null ? customerDetails.getAcctno().toString() : null);
            customerDetailsDto.setStatus(customerDetails.getStatus() != null ? customerDetails.getStatus().toString() : null);
            customerDetailsDto.setCalendarType(customerDetails.getCalendarType() != null ? customerDetails.getCalendarType().toString() : null);
            customerDetailsDto.setNextBillDate(customerDetails.getNextBillDate() != null ? customerDetails.getNextBillDate() : null);
            customerDetailsDto.setAddress(customerDetails.getAddress1() != null ? customerDetails.getAddress1().toString() : null);
            customerDetailsDto.setPanNo(customerDetails.getPan() != null ? customerDetails.getPan().toString() : null);
            customerDetailsDto.setNextQuotaResetDate(customerDetails.getNextQuotaResetDate() != null ? customerDetails.getNextQuotaResetDate() : null);

            customerDetailsDto.setGeoLocation(customerDetails.getVLANID() != null ? customerDetails.getVLANID() : null);
            customerDetailsDto.setPARAM1(customerDetails.getFramedIp() != null ? customerDetails.getFramedIp() : null);
            customerDetailsDto.setPARAM2(customerDetails.getFramedIPNetmask() != null ? customerDetails.getFramedIPNetmask() : null);
            customerDetailsDto.setPARAM3(customerDetails.getFramedroute() != null ? customerDetails.getFramedroute() : null);
            customerDetailsDto.setPARAM4(customerDetails.getNasPortId() != null ? customerDetails.getNasPortId() : null);
            customerDetailsDto.setPARAM6(customerDetails.getGatewayip() != null ? customerDetails.getGatewayip() : null);
            customerDetailsDto.setPrimaryDNS(customerDetails.getPrimaryDNS() != null ? customerDetails.getPrimaryDNS() : null);
            customerDetailsDto.setSecondaryDNS(customerDetails.getSecondaryDNS() != null ? customerDetails.getSecondaryDNS() : null);
            customerDetailsDto.setPrimaryIPv6DNS(customerDetails.getPrimaryIPv6DNS() != null ? customerDetails.getPrimaryIPv6DNS() : null);
            customerDetailsDto.setSecondaryIPv6DNS(customerDetails.getSecondaryIPv6DNS() != null ? customerDetails.getSecondaryIPv6DNS() : null);
            customerDetailsDto.setGROUPNAME(customerDetails.getFramedIpv6Address() != null ? customerDetails.getFramedIpv6Address() : null);
            customerDetailsDto.setCUSTOMERREPLYITEM(customerDetails.getDelegatedprefix() != null ? customerDetails.getDelegatedprefix() : null);
            customerDetailsDto.setMac_auth_enable(customerDetails.getMac_auth_enable() != null ? customerDetails.getMac_auth_enable() : null);
            customerDetailsDto.setMac_provision(customerDetails.getMac_provision() != null ? customerDetails.getMac_provision() : null);
            customerDetailsDto.setConcurrentPolicy(customerDetails.getMaxconcurrentsession() != null ? customerDetails.getMaxconcurrentsession() : null);


            CustomerDetailsDto.PlanDetails lastAddedPlan = null;

            for (Object[] row : custQuotaDetails) {
                String planName = row[0] != null ? row[0].toString() : null;

                // Check if it's a new plan or a repeated one
//            if (lastAddedPlan == null || !lastAddedPlan.getPlanName().equals(planName)) {
                lastAddedPlan = new CustomerDetailsDto.PlanDetails(
                        planName,
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null ? (LocalDate) row[2] : null,
                        row[3] != null ? (LocalDate) row[3] : null,
                        row[4] != null ? row[4].toString() : null,
                        new ArrayList<>()
                );

                planDetailsList.add(lastAddedPlan);
//            }

                lastAddedPlan.getQuotaDetails().add(new CustomerDetailsDto.QuotaDetails(
                        row[5] != null ? (Double) row[5] : 0.0,
                        row[6] != null ? (Double) row[6] : 0.0,
                        row[7] != null ? (Double) row[7] : 0.0
                ));
            }

            customerDetailsDto.setPlanDetails(planDetailsList);
            return customerDetailsDto;


        }catch (Exception e) {
            log.info("GetCustomerDetails With Response Message:{}");
            e.printStackTrace();
        }
        return customerDetailsDto;
    }
}
