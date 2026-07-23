package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.TransacteasePaymentService;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL + URLConstants.Transactease.TRANSACTEASE_PAY)
public class TransacteasePaymentController {

    private final TransacteasePaymentService transacteasePaymentService;

    public TransacteasePaymentController(TransacteasePaymentService transacteasePaymentService) {
        this.transacteasePaymentService = transacteasePaymentService;
    }

    @PostMapping(value = URLConstants.Transactease.INITIATE_PAYMENT)
    public ResponseEntity<?> getTransacteasePayment(@RequestBody CustomerPaymentDTO paymentDTO, HttpServletRequest request) {
        MDC.put("type","Initiate Payment");
        Object data = new Object();
        try {
            transacteasePaymentService.validateRequestForInitiatePayment(paymentDTO);
            data = transacteasePaymentService.initiatePayment(paymentDTO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.OK);
        }finally {
            MDC.clear();
        }
        return ResponseEntity.ok().body(data);
    }
}
