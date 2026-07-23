package com.savbill.integrationsystem.apiAudits.controller;

import com.savbill.integrationsystem.apiAudits.model.ApiAuditsDTO;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.controller.ExBaseAbstractController;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.dto.ValidationData;
import com.savbill.integrationsystem.core.exceptions.DataNotFoundException;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( URLConstants.BASE_PORTAL_API_URL)
public class APIAuditsController extends ExBaseAbstractController<ApiAuditsDTO> {

    @Autowired
    ApiAuditsService apiAuditsService;

    private static final Logger logger = LoggerFactory.getLogger(ExBaseAbstractController.class);

    public APIAuditsController(ApiAuditsService apiAuditsService) {
        super(apiAuditsService);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }


    // contoroller to create an api call audit:
    @Override
    @PostMapping(value = "/saveApiAudit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@RequestBody ApiAuditsDTO entityDTO, BindingResult result, HttpServletRequest request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};}", request.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ValidationData validation = validateSave(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};}", request.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto :: " + entityDTO);
            String authTokenHeader = request.getHeader("Authorization");
            entityDTO.setMvnoId(getMvnoId(authTokenHeader));
            ApiAuditsDTO dtoData = apiAuditsService.saveEntity(entityDTO);
            genericDataDTO.setData(dtoData);
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {{} {};Exception:{}}", request.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getMessage());
        }

        return genericDataDTO;
    }


    //controller read all api audit data:
    @Override
    @PostMapping("/getAllApiAudits")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size()) {
                requestDTO.setSortBy("id");
                genericDataDTO = apiAuditsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters(), request);

            } else {
                genericDataDTO = apiAuditsService.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder(), request);
            }


            if (null != genericDataDTO) {
                logger.info("Fetching All ApiAudits records:  request: { Module : {}}; Response : {Code :{}; Message : {}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to fetch all ApiAudits No records found:  request: { Module : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch all Entities:  request: { module : {}}; Response : {Code :{}; Message : {};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getMessage());
        }
        return genericDataDTO;
    }


    //controller to update all api audit data:
    @Override
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@RequestBody ApiAuditsDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (result.hasErrors()) {
//               ApplicationLogger.logger.debug("Base Controller Error"+result.getFieldErrors());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("Unable to Update Entity by id " + entityDTO.getIdentityKey() + " :  request: { From : {}}; Response : {Code :{}; Message : {}}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to Update Entity by id " + entityDTO.getIdentityKey() + "   :  request: { From : {}}; Response : {Code :{}; Message : {};}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            String authTokenHeader = req.getHeader("Authorization");
            ApiAuditsDTO dtoData = apiAuditsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(), getMvnoId(authTokenHeader));
//            String updatedValues = CommonUtils.getUpdatedDiff(dtoData,entityDTO);
            entityDTO.setMvnoId(getMvnoId(authTokenHeader));
            genericDataDTO.setData(apiAuditsService.updateEntity(entityDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            logger.info("Updating Entity With   " + "updatedValues" + " :  request: { From : {}}; Response : {Code :{}; Message : {}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Update Entity by id " + entityDTO.getIdentityKey() + "   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getMessage());
            }
//            else if (ex instanceof CustomValidationException){
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
//            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
//                logger.error("Unable to fetch all Entities   :  request: { From : {}{}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
//            }
        }
        return genericDataDTO;
    }


    //controller to delete api audit data :
    @Override
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO delete(@RequestBody ApiAuditsDTO entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authTokenHeader = req.getHeader("Authorization");
            ApiAuditsDTO dtoData = apiAuditsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(), getMvnoId(authTokenHeader));
            ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
            entityDTO.setMvnoId(dtoData.getMvnoId());
            apiAuditsService.deleteEntity(entityDTO);
            genericDataDTO.setData(entityDTO);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Deleting Entity  With  id " + entityDTO.getIdentityKey() + " is Successfull :  request: { From : {}}; Response : {Code :{}; Message : {}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());


        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Delete Entity with id " + entityDTO.getIdentityKey() + "   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getMessage());
            }
//            else if (ex instanceof CustomValidationException){
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to Delete Entity with id "+entityDTO.getIdentityKey()+"  :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
//            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
//                logger.error("Unable to Delete Entity with id "+entityDTO.getIdentityKey()+"  :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
//            }
        }
        return genericDataDTO;
    }



    @PostMapping(value = "/search")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "1") Integer page
            , @RequestParam(required = false, defaultValue = "5") Integer pageSize
            , @RequestParam(required = false, defaultValue = "0") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "createdate") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
            if (genericDataDTO.getResponseCode() == 406) {
                List<ApiAuditsDTO> list = apiAuditsService.getAllEntities();
                genericDataDTO.setDataList(list);
                genericDataDTO.setTotalRecords(list.size());
                return genericDataDTO;
            }
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
                pageSize = MAX_PAGE_SIZE;
            genericDataDTO = apiAuditsService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder, request);

            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);

                }
                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }




}
