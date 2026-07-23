package com.savbill.integrationsystem.acsmaster.controller;


import com.savbill.integrationsystem.acsmaster.entity.AcsMaster;
import com.savbill.integrationsystem.acsmaster.mapper.AcsMasterMapper;
import com.savbill.integrationsystem.acsmaster.model.AcsMasterDTO;
import com.savbill.integrationsystem.acsmaster.repository.AcsMasterRepository;
import com.savbill.integrationsystem.acsmaster.service.AcsMasterService;
import com.savbill.integrationsystem.core.controller.ExBaseAbstractController;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController(value = "AcsMasterController")
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.ACS_MASTER)
@Api(value = "AcsMasterController", description = "REST APIs related to Acs Master !!!!", tags = "AcsMasterController")
public class AcsMasterController extends ExBaseAbstractController<AcsMasterDTO> {

    @Autowired
    private AcsMasterRepository acsMasterRepository;

    @Autowired
    private AcsMasterMapper acsMasterMapper;

    private static final Logger logger = LoggerFactory.getLogger(AcsMasterController.class);
    private String MODULE = "[ProfileController]";
    @Autowired
    AcsMasterService acsMasterService;

    public AcsMasterController(AcsMasterService acsMasterService) {
        super(acsMasterService);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }

//    @Override
//    public GenericDataDTO save(AcsMasterDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        AcsMaster acsMaster=acsMasterMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//        genericDataDTO.setData(acsMasterRepository.save(acsMaster));
//        return genericDataDTO;
//    }


    @GetMapping(value = "/getMacAddress")
    public GenericDataDTO getMacAddress(@RequestParam(name = "vendorId") Long vendorId, @RequestParam(name = "serialNumber") String serialNumber, @RequestParam(name = "apiName") String apiName, @RequestParam(name = "mvnoId") Long mvnoId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = acsMasterService.getMacAddress(vendorId, serialNumber, apiName, mvnoId);
        } catch (CustomValidationException ce) {
            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getAcsMasterByVendorId")
    public GenericDataDTO getAcsMasterByVendorId(@RequestParam(name = "vendorId") Long vendorId, @RequestParam(name = "mvnoId") Long mvnoId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = acsMasterService.getAcsMasterByVendorId(vendorId, mvnoId);
        } catch (CustomValidationException ce) {
            logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getAll(PaginationRequestDTO requestDTO, HttpServletRequest req) {
        return super.getAll(requestDTO, req);
    }

    @Override
    @GetMapping(path = "/all")
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            genericDataDTO.setData(acsMasterService.getAll());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getEntityById(String id, HttpServletRequest req) throws Exception {
        String authTokenHeader = req.getHeader("Authorization");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            AcsMaster acsMaster = acsMasterService.getAcsMasterByIdAndMvnoIdAndIsdeleteFalse(Long.valueOf(id), getMvnoId(authTokenHeader));
            genericDataDTO.setData(acsMaster);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
//            genericDataDTO.setData("Not Found");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
//        return acsMasterService.getEntityById(Long.valueOf(id), );
    }


    @DeleteMapping(value = "/delete")
    public GenericDataDTO delete(@RequestParam(name = "id") Long id) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        acsMasterRepository.deleteById(id);
        dataDTO.setResponseCode(HttpStatus.OK.value());
        dataDTO.setResponseMessage("Acs Deleted Successfully");
        return dataDTO;
    }

//    @Override
//    @PostMapping(value = "/search")
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "1") Integer page
//            , @RequestParam(required = false, defaultValue = "5") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "0") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "id") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

}
