package com.savbill.radius.controller;

import com.savbill.radius.dto.CustomerDetailsDto;
import com.savbill.radius.services.CustomerDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/SavbillRadius")
public class CustomerDetailsController {

    @Autowired
    CustomerDetailsService customerDetailsService;

    @GetMapping("/GetCustomerDetails/{username}/{mvnoId}")
    public CustomerDetailsDto GetCustomerDetails(@PathVariable String username, @PathVariable("mvnoId") Integer mvnoId) {
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();
        try {
            customerDetailsDto = customerDetailsService.GetCustomerDetails(username , mvnoId);
            return customerDetailsDto;
        } catch (Exception e) {
            log.info("GetCustomerDetails With Response Message:{}");
            e.printStackTrace();
        }
        return customerDetailsDto;
    }

}
