package com.savbill.integrationsystem.waveMoney;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class WaveMoneyController {
    @Autowired
    WaveMoneyService waveMoneyService;


    @PostMapping("/waveMoneyPay")
    public GenericDataDTO InitiateWaveMoneyPayRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
            genericDataDTO  = waveMoneyService.waveMoneyPaymentInitiateService(customerPaymentDTO, authToken);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage("Something Went Wrong Regarding Selcom Pay Initiation...");
            ApplicationLogger.logger.error("WaveMoney Payment initiation failed " + e.getMessage());
            return genericDataDTO;
        }
    }
}
