package com.savbill.integrationsystem.deviceveri.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.core.dto.CustomerResponseDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.mvno.RevenueClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.deviceveri.domain.CustomersData;
import com.savbill.integrationsystem.deviceveri.mapper.CustomersMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomersDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomersRepo;

@Service
public class CustomersService{


    @Autowired
    private CustomersRepo repo;

    @Autowired
    private CustomersMapper mapper;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private RevenueClient revenueClient;



    public String getModuleNameForLog() {
        return "CustomersService[]";
    }

    public List<CustomersDTO> findByCustid(Long custId){
    	List<CustomersData> list = repo.findAllById(Collections.singleton(custId.intValue()));
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }
    
    public List<CustomersDTO> findByParentcustid(String custId){
    	List<CustomersData> list = repo.findByParentcustid(custId);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CustomersDTO> findByUsername(String username){
    	List<CustomersData> list = repo.findByUsername(username);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public CustomerResponseDTO getCustomerByAccountNo(String accountNo,String token){
        AirtelAppToCRMDTO airtelAppToCRMDTO = new AirtelAppToCRMDTO();
        airtelAppToCRMDTO.setAccountNo(accountNo);
        ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(airtelAppToCRMDTO, token);
        if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , accountNo + " can not be duplicate.Please connect the administrator.", null);
        }
        if (!customerByAccountNumber.getBody().isEmpty()) {
            airtelAppToCRMDTO = customerByAccountNumber.getBody().get(0);
            Double walletAmount = revenueClient.getWalletBalanceByCustId(airtelAppToCRMDTO.getCustId(), token);
            airtelAppToCRMDTO.setWalletBalance(walletAmount.toString());
            CustomerResponseDTO customerResponseDTO = convertCMSResponseToSendResponse(airtelAppToCRMDTO);
            return customerResponseDTO;
        }
        else{
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Account number is not found in system.",null);
        }

    }

    public CustomerResponseDTO convertCMSResponseToSendResponse(AirtelAppToCRMDTO airtelAppToCRMDTO){
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setAccountNo(airtelAppToCRMDTO.getAccountNo());
        customerResponseDTO.setFirstname(airtelAppToCRMDTO.getFirstName());
        customerResponseDTO.setLastname(airtelAppToCRMDTO.getLastName());
        customerResponseDTO.setStatus(airtelAppToCRMDTO.getStatus());
        customerResponseDTO.setWalletBalance(airtelAppToCRMDTO.getWalletBalance());
        customerResponseDTO.setUsername(airtelAppToCRMDTO.getUsername());
        customerResponseDTO.setPassword(airtelAppToCRMDTO.getPassword());
        customerResponseDTO.setCustId(airtelAppToCRMDTO.getCustId());
        customerResponseDTO.setMobileNumber(airtelAppToCRMDTO.getCustomerMsisdn());
        customerResponseDTO.setMvnoId(airtelAppToCRMDTO.getMvnoId());
        customerResponseDTO.setCusttype(airtelAppToCRMDTO.getCusttype());

        return customerResponseDTO;


    }

}
