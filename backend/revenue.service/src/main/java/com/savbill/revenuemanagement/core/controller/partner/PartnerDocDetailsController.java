package com.savbill.revenuemanagement.core.controller.partner;

import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PartnerCreditDocument;
import com.savbill.revenuemanagement.core.entity.partner.PartnerDebitDocument;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.service.partner.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController

@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_DOC)
public class PartnerDocDetailsController{
    private static String MODULE = " [PartnerDocDetailsController] ";

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    PartnerService partnerService;

    private String PATH;
    private  static final Logger logger= LoggerFactory.getLogger(PartnerDocDetailsController.class);


    @GetMapping(value = "/partnerPaymentHistory/{partnerId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer partnerId) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch payment History of customer " + partnerId + " :  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Partner partners = partnerRepository.findById(partnerId).orElse(null);
            if (partners == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch payment History of customer " + partners.getName() + "  :  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            List<PartnerCreditDocument> paymentHistories = partnerService.getByLcoId(partnerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            logger.info("Fetching payment history for customer " + partners.getName() + ":  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch payment History of customer " + partnerRepository.findById(partnerId).get().getName() + "  :  request: { From : {}, }; Response : {{} code:{};Exception:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


    @GetMapping(value = "/partnerInvoiceHistory/{partnerId}")
    public GenericDataDTO getPartnerInvoiceHistory(@PathVariable Integer partnerId) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch payment History of customer " + partnerId + " :  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Partner partners = partnerRepository.findById(partnerId).orElse(null);
            if (partners == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch payment History of customer " + partners.getName() + "  :  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            List<PartnerDebitDocument> paymentHistories = partnerService.getByPartnerId(partnerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            logger.info("Fetching payment history for customer " + partners.getName() + ":  request: { From : {}}; Response : {{} code:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch payment History of customer " + partnerRepository.findById(partnerId).get().getName() + "  :  request: { From : {}, }; Response : {{} code:{};Exception:{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
