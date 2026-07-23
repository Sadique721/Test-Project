package com.savbill.cpm.modules.TechnicalDetails.controller;

import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController2;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.TechnicalDetails.domain.TechnicalDetails;
import com.savbill.cpm.modules.TechnicalDetails.model.TechnicalDetailsDto;
import com.savbill.cpm.modules.TechnicalDetails.service.TechnicalDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TECHNICAL_DETAILS)
public class TechnicalDetailsController extends ExBaseAbstractController2<TechnicalDetailsDto> {
    public TechnicalDetailsController(TechnicalDetailsService service) {
        super(service);
    }

    @Autowired
    TechnicalDetailsService technicalDetailsService;

    private static final Logger logger= LoggerFactory.getLogger(TechnicalDetailsController.class);

    @Override
    public GenericDataDTO getAllWithoutPagination () {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<TechnicalDetails> list = technicalDetailsService.getAll();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
