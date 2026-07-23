package com.savbill.taskmanagement.core.modules.tasks.controller;


import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchDTO;
import com.savbill.taskmanagement.core.dto.PaginationRequestDTO;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseReasonConfigPojo;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseReasonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE_REASON_CONFIG)
public class CaseReasonConfigController extends ExBaseAbstractController<CaseReasonConfigPojo> {

    @Autowired
    private CaseReasonConfigService caseReasonConfigService;

    public CaseReasonConfigController(CaseReasonConfigService service) {
        super(service);
    }

    @Deprecated
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
        return super.search(page, pageSize, sortOrder, sortBy, filter);
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @Override
    public GenericDataDTO delete(@RequestBody CaseReasonConfigPojo entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        return super.delete(entityDTO, authentication, req);
    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody CaseReasonConfigPojo entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
    	if(getMvnoIdFromCurrentStaff() != null) {
    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
    	}
    	return super.save(entityDTO, result, authentication, req);
    }

    @Override
    public GenericDataDTO update(@Valid @RequestBody CaseReasonConfigPojo entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
    	if(getMvnoIdFromCurrentStaff() != null) {
    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
    	}
    	return super.update(entityDTO, result, authentication, req);
    }

    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        return super.getEntityById(id, req);
    }

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req) {
        return super.getAll(requestDTO,req);
    }

//    @GetMapping("/byCaseReasonId/{caseReasonId}")
//    public GenericDataDTO getEntityByCaseReasonId(@PathVariable Long caseReasonId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            return GenericDataDTO.getGenericDataDTO(caseReasonConfigService.getEntityByCaseReasonId(caseReasonId));
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            if (e instanceof DataNotFoundException) {
//                genericDataDTO.setResponseMessage("Data Not Found");
//            } else {
//                genericDataDTO.setResponseMessage(e.getMessage());
//            }
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setDataList(null);
//        }
//        return genericDataDTO;
//    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseReasonConfigController]";
    }
}
