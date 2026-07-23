package com.savbill.cpm.modules.purchaseDetails.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.savbill.cpm.modules.purchaseDetails.model.PurchaseHistoryReqDTO;
import com.savbill.cpm.modules.purchaseDetails.service.PurchaseDetailsService;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PURCHASE_DETAILS)
public class PurchaseDetailsController extends ExBaseAbstractController<PurchaseDetailsDTO> {

    private static String MODULE = " [PurchaseDetailsController] ";
    @Autowired
    private PurchaseDetailsService purchaseDetailsService;

    public PurchaseDetailsController(PurchaseDetailsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return " [PurchaseDetailsController] ";
    }
    private static final Logger logger = LoggerFactory.getLogger(PurchaseDetailsController.class);
    @GetMapping("/txnStatus/{txnId}")
    public GenericDataDTO getTxnStatus(@PathVariable String txnId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            PurchaseDetailsDTO dto = purchaseDetailsService.getPurchaseBYTxnId(txnId);
            if (dto == null) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Txn Id can not be null or not found!!");
                logger.error("unable to fetch transaction Status for transaction id "+txnId+":  request: { From : {}, }; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            genericDataDTO.setData(dto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching Transaction details from transaction id "+txnId +" is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to fetch transaction Status for transaction id "+txnId+":  request: { From : {},}; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/history")
    public GenericDataDTO getPurchaseHistoryByParam(@RequestBody PurchaseHistoryReqDTO reqDTO) {
        MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getPurchaseHistoryByParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == reqDTO) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.error("unable to fetch purchase history using details "+reqDTO+":  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == reqDTO.getPage())
                reqDTO.setPage(paginationRequestDTO.getPage());
            if (null == reqDTO.getPageSize())
                reqDTO.setPageSize(paginationRequestDTO.getPageSize());
            if (null == reqDTO.getSortOrder())
                reqDTO.setSortOrder(paginationRequestDTO.getSortOrder());
            if (null == reqDTO.getSortBy())
                reqDTO.setSortBy(paginationRequestDTO.getSortBy());
            if (null != reqDTO.getPageSize() && reqDTO.getPageSize() > MAX_PAGE_SIZE)
                reqDTO.setPageSize(MAX_PAGE_SIZE);
            logger.info("Fetching Purchaseed data by History "+reqDTO+" is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            return purchaseDetailsService.getAllPurchaseHistoryByParam(reqDTO);
        } catch (Exception ex) {
           // ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("unable to fetch purchase history using details "+reqDTO+"::  request: { From : {}}; Response : {{}{};}Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
            MDC.remove("type");
            return genericDataDTO;
        }
    }
}
