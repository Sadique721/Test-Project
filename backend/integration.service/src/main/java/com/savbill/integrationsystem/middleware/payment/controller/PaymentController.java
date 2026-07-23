package com.savbill.integrationsystem.middleware.payment.controller;

import com.savbill.integrationsystem.middleware.payment.dto.customerdetail.CustomerDetailsResponse;
import com.savbill.integrationsystem.middleware.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<CustomerDetailsResponse> getCustomerDetails(@RequestParam String username) {
        CustomerDetailsResponse customerDetailsResponse = paymentService.getCustomerDetail(username);
        return ResponseEntity.ok(customerDetailsResponse);
    }
}
