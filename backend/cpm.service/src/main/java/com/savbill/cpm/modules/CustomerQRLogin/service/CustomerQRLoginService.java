package com.savbill.cpm.modules.CustomerQRLogin.service;


import com.savbill.cpm.Socket.SendSocketService;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.CustomerQRLogin.domain.CustomerQRLogin;
import com.savbill.cpm.modules.CustomerQRLogin.model.CustomerQRLoginDTO;
import com.savbill.cpm.modules.CustomerQRLogin.model.SendCustomerQR;
import com.savbill.cpm.modules.CustomerQRLogin.repository.CustomerQRLoginRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerQRLoginService {

    @Autowired
    private CustomerQRLoginRepository customerQRLoginRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private SendSocketService sendSocketService;

    @Autowired
    private CustomersRepository customersRepository;






    public void validateSaveGeneratedCode(CustomerQRLoginDTO customerQRLoginDTO){
        if(Objects.isNull(customerQRLoginDTO.getCode()) || customerQRLoginDTO.getCode().equalsIgnoreCase("")){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"QR code can't be null or empty",null);
        }
    }

    public void validateSaveCustomerLogin(CustomerQRLoginDTO customerQRLoginDTO){
        if(Objects.isNull(customerQRLoginDTO.getCode()) || customerQRLoginDTO.getCode().equalsIgnoreCase("")){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"QR code can't be null or empty",null);
        }

        if(Objects.isNull(customerQRLoginDTO.getUsername()) || customerQRLoginDTO.getUsername().equalsIgnoreCase("")){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"Username can't be null or empty",null);
        }

    }


    public CustomerQRLogin saveGeneratedCode(CustomerQRLoginDTO customerQRLoginDTO){
        CustomerQRLogin customerQRLogin = new CustomerQRLogin();
        customerQRLogin.setCode(customerQRLoginDTO.getCode());
        customerQRLogin.setStatus("GENERATED");
        customerQRLogin.setCreatedate(LocalDateTime.now());
        Integer mvnoId = customersService.getMvnoIdFromCurrentStaff();
        customerQRLogin.setMvnoId(mvnoId);
        customerQRLogin = customerQRLoginRepository.save(customerQRLogin);
        return customerQRLogin;
    }

    public CustomerQRLogin expireGeneratedCode(CustomerQRLoginDTO customerQRLoginDTO){
        CustomerQRLogin customerQRLogin = customerQRLoginRepository.findByCode(customerQRLoginDTO.getCode());
        if(customerQRLogin != null) {
            customerQRLogin.setStatus("EXPIRED");
            customerQRLogin = customerQRLoginRepository.save(customerQRLogin);
            return customerQRLogin;
        }
        else{
            return null;
        }
    }


    public CustomerQRLogin savecustomerUsernamePassword(CustomerQRLoginDTO customerQRLoginDTO) throws MalformedURLException {
        CustomerQRLogin customerQRLogin = customerQRLoginRepository.findByCodeAndStatusEqualsIgnoreCase(customerQRLoginDTO.getCode() , "GENERATED");
        if(customerQRLogin != null) {
            customerQRLogin.setStatus("USED");
            customerQRLogin.setUsername(customerQRLoginDTO.getUsername());
            Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(customerQRLoginDTO.getUsername() , customerQRLogin.getMvnoId());
            if(customers.isPresent()){
                customerQRLogin.setPassword(customers.get().getPassword());
            }
            else{
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED ,"Customer not found given username",null);
            }
            customerQRLogin = customerQRLoginRepository.save(customerQRLogin);
            sendCustomerLoginQrMessageToCommon(customerQRLogin);
            return customerQRLogin;
        }
        else{
            return null;
        }
    }

    public CustomerQRLogin getQrStatus(String code){
        CustomerQRLogin customerQRLogin = customerQRLoginRepository.findByCode(code);
        if(customerQRLogin != null) {
            if (customerQRLogin.getStatus().equalsIgnoreCase("USED")) {
                return customerQRLogin;
            }
            else {
                return null;
            }
        }
        else{
            return null;
        }
    }

    public SendCustomerQR convertEntityToSendCustomerQrDTO(CustomerQRLogin customerQRLogin){
        SendCustomerQR sendCustomerQR = new SendCustomerQR();
        sendCustomerQR.setCode(customerQRLogin.getCode());
        sendCustomerQR.setUsername(customerQRLogin.getUsername());
        sendCustomerQR.setPassword(customerQRLogin.getPassword());
        sendCustomerQR.setStatus(customerQRLogin.getStatus());
        return sendCustomerQR;
    }

    public void sendCustomerLoginQrMessageToCommon(CustomerQRLogin customerQRLogin){
        SendCustomerQR sendCustomerQR = convertEntityToSendCustomerQrDTO(customerQRLogin);
        sendSocketService.SendMessageToCommonForSocket(sendCustomerQR  , CommonConstants.SOCKET_URL_CONSTANT.QR_URL);
    }

}
