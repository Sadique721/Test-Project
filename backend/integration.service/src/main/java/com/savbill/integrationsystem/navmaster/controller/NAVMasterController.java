package com.savbill.integrationsystem.navmaster.controller;

import com.savbill.integrationsystem.core.controller.ExBaseAbstractController;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.navmaster.model.NAVMasterDTO;
import com.savbill.integrationsystem.navmaster.service.NAVMasterService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.NAV_MASTER)
@Api(value = "NAVMasterController", description = "REST APIs related to NAV Master !!!!", tags = "NAVMasterController")
public class NAVMasterController extends ExBaseAbstractController<NAVMasterDTO> {

    private static final Logger logger = LoggerFactory.getLogger(NAVMasterController.class);
    private String MODULE = "[NAVMasterController]";

    @Autowired
    private NAVMasterService navMasterService;

    public NAVMasterController(NAVMasterService navMasterService) {
        super(navMasterService);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())

                genericDataDTO = navMasterService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters(), request);

            else
                genericDataDTO = navMasterService.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder(), request);


            if (null != genericDataDTO) {
                //logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                //logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
        }
        return genericDataDTO;
    }
}
