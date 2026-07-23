package com.savbill.integrationsystem.SOAPService.service;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;

import com.savbill.integrationsystem.generated.changeservice.WsChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
public class ChangeServService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    public RadiusClientService radiusClient;
    @Autowired
    private JwtUtil jwtUtil;
    public boolean checkUsageEntry(String userName) {
        CustomerData customerData = customerRepository.findByUsername(userName);
            if(userName.equalsIgnoreCase(customerData.getUsername())) {
            return true;
        }
        return false;
    }

    public boolean changeServiceValidator(ResponseEntity request) {
        if (request.getBody() instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) request.getBody();
            if (map.get("override").toString().equalsIgnoreCase("SUCCESS")) {
                return true;
            }
        }
        return false;
    }
    public CustomerData getCustomerQoutaDetails(String userName) {
        CustomerData customerData = customerRepository.findByUsername(userName);
        return customerData;
    }

    public Boolean velidateInputData(WsChangeService request) {
        return false;
    }

    public boolean checkCustEntryInUsageQuota(String userName) {
        GenericDataDTO genericDataDTO =radiusClient.GetBalanceApi(userName, SoapConstants.MVNOID);
        if (genericDataDTO.getResponseMessage().equalsIgnoreCase("SUCCESS")){
            return true;
        }
        return false;
    }

    public Boolean checkCustomerEntryInCustTBL(String userName) throws Exception , SQLException {
        GenericDataDTO genericDataDTO =radiusClient.getCustomerDetails(userName, SoapConstants.MVNOID);
        if (genericDataDTO.getResponseMessage().equalsIgnoreCase("SUCCESS")){
            return true;
        }
        return false;
    }
}
