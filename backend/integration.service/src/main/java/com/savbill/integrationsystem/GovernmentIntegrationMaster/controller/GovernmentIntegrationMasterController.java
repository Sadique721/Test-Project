package com.savbill.integrationsystem.GovernmentIntegrationMaster.controller;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.model.GovernmentIntegrationMasterDto;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.service.GovernmentIntegrationMasterService;
import com.savbill.integrationsystem.core.controller.ExBaseAbstractController;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController(value = "GovernmentIntegrationMasterController")
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.GOVERNMENT_INTEGRATION_MASTER)
public class GovernmentIntegrationMasterController extends ExBaseAbstractController<GovernmentIntegrationMasterDto> {
    public GovernmentIntegrationMasterController(GovernmentIntegrationMasterService service) {
        super(service);
    }

    private static final Logger logger = LoggerFactory.getLogger(GovernmentIntegrationMasterController.class);

    @Override
    public String getModuleNameForLog() {
        return " [InvestmentCodeController] ";
    }

    @Autowired
    GovernmentIntegrationMasterService governmentIntegrationMasterService;

    @Override
    public GenericDataDTO save(@Valid @RequestBody GovernmentIntegrationMasterDto entityDTO, BindingResult result,
                               HttpServletRequest req) throws Exception {
        MDC.put("type", "Create");
        GenericDataDTO dataDTO = new GenericDataDTO();
        dataDTO = super.save(entityDTO, result, req);
        GovernmentIntegrationMasterDto governmentIntegrationMasterDto = (GovernmentIntegrationMasterDto) dataDTO
                .getData();
        logger.info("Government Master created Successfully With name " + entityDTO.getUsername()
                + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"));
        return dataDTO;
    }

//    @Override
//    public GenericDataDTO update(@Valid @RequestBody GovernmentIntegrationMasterDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
//        String oldname = governmentIntegrationMasterService.getById(entityDTO.getId()).getUsername();
//        org.slf4j.MDC.put("type", "Update");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        dataDTO = super.update(entityDTO, result, req);
//        GovernmentIntegrationMasterDto investmentCodeDto = (GovernmentIntegrationMasterDto) dataDTO.getData();
//        return dataDTO;
//    }
@Override
public GenericDataDTO update(@Valid @RequestBody GovernmentIntegrationMasterDto entityDTO, BindingResult result,
                             HttpServletRequest req) throws Exception {
    String oldname = governmentIntegrationMasterService.getById(entityDTO.getId()).getUsername();
    org.slf4j.MDC.put("type", "Update");
    GenericDataDTO dataDTO = new GenericDataDTO();
    dataDTO = super.update(entityDTO, result, req);
    GovernmentIntegrationMasterDto investmentCodeDto = (GovernmentIntegrationMasterDto) dataDTO.getData();
    return dataDTO;
}

    @Override
    public GenericDataDTO getAllWithoutPagination() {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<GovernmentIntegrationMasterDto> list = governmentIntegrationMasterService.getAllEntities().stream()
                    .filter(investmentCodeDto -> !investmentCodeDto.getIsdelete()).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info(
                    "Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}",
                    getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}",
                    getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),
                    ex.getStackTrace());

        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())

                genericDataDTO = governmentIntegrationMasterService.getListByPageAndSizeAndSortByAndOrderBy(
                        requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                        requestDTO.getSortOrder(), requestDTO.getFilters(), request);

            else
                genericDataDTO = governmentIntegrationMasterService.search(requestDTO.getFilters(),
                        requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                        requestDTO.getSortOrder(), request);

            if (null != genericDataDTO) {
                // logger.info("Fetching data : request: { From : {}}; Response :
                // {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(),
                // genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                // logger.info("Unable to fetch all Entities : request: { module : {}}; Response
                // : {Code{},Message:{};}}",
                // getModuleNameForLog(),genericDataDTO.getResponseCode(),
                // genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(
                    "Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}",
                    getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),
                    ex.getStackTrace());
        }
        return genericDataDTO;
    }

    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "1") List<GenericSearchModel> page,
                                 @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                 @RequestParam(required = false, defaultValue = "0") Integer sortOrder,
                                 @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestBody Integer filter,
                                 HttpServletRequest request) {
        return governmentIntegrationMasterService.search(page, pageSize, sortOrder, sortBy, filter, request);
    }

    @Override
    public GenericDataDTO delete(@RequestBody GovernmentIntegrationMasterDto entityDTO, HttpServletRequest req)
            throws Exception {
        org.slf4j.MDC.put("type", "Delete");
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = governmentIntegrationMasterService.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, req);
            GovernmentIntegrationMasterDto investmentCodeDto = (GovernmentIntegrationMasterDto) dataDTO.getData();
            if (investmentCodeDto != null) {
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                // AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null,
                // businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
                logger.info(
                        "Region  With name " + entityDTO.getUsername()
                                + " is deleted Successfully  :  request: { From : {}}; Response : {{}}",
                        req.getHeader("requestFrom"));
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            logger.error(
                    "Unable to Delete Region With name: " + entityDTO.getUsername()
                            + "  request: { From : {}}; Response : {{}}",
                    req.getHeader("requestFrom"), HttpStatus.NOT_ACCEPTABLE.value());
        }

        org.slf4j.MDC.remove("type");
        return dataDTO;

    }

}
