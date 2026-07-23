package com.savbill.ticketmanagement.core.modules.ServiceParameters.controller;

import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.ticketmanagement.core.modules.ServiceParameters.service.ServiceParametersService;
import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.SERVICE_PARAMETERS)
public class Serviceparameterscontroller{
//    public Serviceparameterscontroller(ServiceParametersService service) {
//        super(service);
//    }
    private static final Logger logger = LoggerFactory.getLogger(Serviceparameterscontroller.class);
    private static String SUBMODULE = " [Serviceparameterscontroller] ";
    @Autowired
    ServiceParametersService serviceParametersService;

    @GetMapping("/all")
    public GenericDataDTO getAllWithoutPagination () {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getAllWithoutPagination()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AllWithoutPagination .Data[" + SUBMODULE.toString() + "]");
        try {
            List<ServiceParameter> list = serviceParametersService.findall();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }

        return genericDataDTO;

    }

//    @Override
    public String getModuleNameForLog() {
        return "[Serviceparameterscontroller]";
    }
}
