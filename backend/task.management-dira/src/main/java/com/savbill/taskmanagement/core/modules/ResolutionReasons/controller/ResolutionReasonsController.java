package com.savbill.taskmanagement.core.modules.ResolutionReasons.controller;


import com.savbill.taskmanagement.core.constants.DeleteContant;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.constants.MenuConstants;
import com.savbill.taskmanagement.core.constants.MessageConstants;
import com.savbill.taskmanagement.core.controller.APIResponseController;
import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.PaginationRequestDTO;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.mapper.ResolutionReasonsMapper;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.repository.ResolutionReasonsRepository;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.service.ResolutionReasonsService;
//import com.savbill.ticketmanagement.core.modules.common.LoggedInUser;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;

import com.savbill.taskmanagement.core.modules.tasks.repository.ResoSubCategoryMappingRepo;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseSubCategoryCategoryMappingRepository;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.modules.utils.UpdateDiffFinder;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.security.spring.SpringContext;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import brave.Tracer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.RESOLUTION_REASONS)
public class ResolutionReasonsController extends ExBaseAbstractController<ResolutionReasonsDTO> {


	private static String MODULE = " [ResolutionReasonsController] ";

	@Autowired
	private ResolutionReasonsRepository resolutionReasonsRepository;

	@Autowired
	private ResolutionReasonsService resolutionReasonsService;

	@Autowired
	private ResolutionReasonsMapper resolutionReasonsMapper;
	@Autowired
    CaseSubCategoryCategoryMappingRepository mappingRepository;
	@Autowired
	ResoSubCategoryMappingRepo resoSubCategoryMappingRepo;
	@Autowired
	private APIResponseController responseController;


	private Tracer tracer;

	public ResolutionReasonsController(ResolutionReasonsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ResolutionReasons Controller]";
    }



	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody ResolutionReasonsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
    	ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
    	GenericDataDTO genericDataDTO = new GenericDataDTO();
		Integer RESP_CODE = APIConstants.FAIL;
//		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Create");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
    	try {
			boolean flag = resolutionReasonsService.duplicateVerifyAtSave(entityDTO.getName());

			if (flag) {
				if (getMvnoIdFromCurrentStaff() != null) {
					entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());

					if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
						log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "create root cause  With name : "+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE + RESP_CODE);
						throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
					}else if (getBUIdsFromCurrentStaff().size() == 1) {
						entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
					}
				}


				if(getLoggedInUser().getLco())
					entityDTO.setLcoId(getLoggedInUser().getPartnerId());
				else
					entityDTO.setLcoId(null);

				genericDataDTO = super.save(entityDTO, result, authentication, req);
				resolutionReasonsService.createDir(entityDTO);
					RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "create root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Root cause with same name already exist"+LogConstants.LOG_STATUS_CODE + RESP_CODE);

			}
		}
        catch(CustomValidationException e)
		{
			genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
			genericDataDTO.setResponseMessage(e.getMessage());
			RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
			log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+   LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);

		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
    	return genericDataDTO;
    }
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody ResolutionReasonsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
    	ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
    	GenericDataDTO genericDataDTO = new GenericDataDTO();
//		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Update");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		try{
			ResolutionReasonsDTO dtoData = resolutionReasonsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());

			boolean flag = resolutionReasonsService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
			if (flag) {
				if(getMvnoIdFromCurrentStaff() != null) {
					entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
				}
				ResolutionReasons olddata = resolutionReasonsService.getRepository().getOne(entityDTO.getId());
				ResolutionReasonsDTO olddatadto = resolutionReasonsMapper.domainToDTO(olddata , new CycleAvoidingMappingContext());
				genericDataDTO = super.update(entityDTO, result, authentication, req);if (olddatadto != null) {
					log.info("Ticket Root Cause update details: " + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO));
				}

				//	logger.info("Updating resolution reasone With name "+entityDTO.getName() +"is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);

			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR+LogConstants.LOG_STATUS_CODE + RESP_CODE);

			}
		}catch (Exception ex){
			if (ex instanceof DataNotFoundException) {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
				genericDataDTO.setResponseMessage("Not Found");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);

			} else if (ex instanceof CustomValidationException){
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
				genericDataDTO.setResponseMessage(ex.getMessage());
				RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update  root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);

			} else {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update  root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE + RESP_CODE);

			}
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
    	return genericDataDTO;
    }
	@GetMapping("/searchByStatus")
	public GenericDataDTO getAllByStatus(HttpServletRequest req) {
		String SUBMODULE = getModuleNameForLog() + " [getALlByStatus] ";
//		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Search");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO = GenericDataDTO.getGenericDataDTO(resolutionReasonsService.findByStatus());
			if (null != genericDataDTO) {

				if (genericDataDTO.getDataList().isEmpty())
				{
					genericDataDTO = new GenericDataDTO();
					genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
					genericDataDTO.setResponseMessage("No Record Found!");
					genericDataDTO.setDataList(new ArrayList<>());
					genericDataDTO.setTotalRecords(0);
					genericDataDTO.setPageRecords(0);
					genericDataDTO.setCurrentPageNumber(1);
					genericDataDTO.setTotalPages(1);
					RESP_CODE = APIConstants.NOT_FOUND;
					log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+ "search Root case  by status : " + LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);


				}

				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Root case  by status : " +  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
				return genericDataDTO;
			}

		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Root case  by status : " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			return genericDataDTO;
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE + "\")")
	@PostMapping(value = "/searchAll")
	public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req) {
//		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Search");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		try {
			RESP_CODE = APIConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Root case"+paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			return resolutionReasonsService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(),
					paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
		}catch (Exception e){
			RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
			log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "search Root case"+paginationRequestDTO.getFilters().get(0).getFilterValue() +LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE +RESP_CODE );

		} finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
return null;
	}

	public LoggedInUser getLoggedInUser() {
		LoggedInUser loggedInUser = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
		}
		return loggedInUser;
	}


	@Override
//	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE + "\")")
	@PostMapping
	public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req) {
		String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
		Integer RESP_CODE = APIConstants.FAIL;

//		TraceContext traceContext = tracer.currentSpan().context();
		HashMap<String, Object> response = new HashMap<>();
		MDC.put("type", "Search");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		try {
			genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
			requestDTO = setDefaultPaginationValues(requestDTO);

			if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
				genericDataDTO = resolutionReasonsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
						, requestDTO.getPageSize()
						, requestDTO.getSortBy()
						, requestDTO.getSortOrder()
						, requestDTO.getFilters());
			else
				genericDataDTO = resolutionReasonsService.search(requestDTO.getFilters()
						, requestDTO.getPage(), requestDTO.getPageSize()
						, requestDTO.getSortBy()
						, requestDTO.getSortOrder());


			if (null != genericDataDTO) {
//				log.info("Fetching All Entities records:  request: { Module : {}}; Response : {Code :{}; Message : {}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
				RESP_CODE = APIConstants.SUCCESS;
				log.info( LogConstants.REQUEST_FOR + "Search All RootCause records : "+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
				return genericDataDTO;
			} else {
				genericDataDTO = new GenericDataDTO();
				genericDataDTO.setDataList(new ArrayList<>());
				genericDataDTO.setTotalRecords(0);
				genericDataDTO.setPageRecords(0);
				genericDataDTO.setCurrentPageNumber(1);
				genericDataDTO.setTotalPages(1);
				RESP_CODE = APIConstants.NOT_FOUND;
				log.info(LogConstants.REQUEST_FOR+ "Search All RootCause records : " + LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		} catch (Exception ex) {
			genericDataDTO = new GenericDataDTO();
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			genericDataDTO.setTotalRecords(0);
			log.error( LogConstants.REQUEST_FOR + "Search All RootCause records : "+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
		}
		return genericDataDTO;
	}
	@GetMapping("/searchBySubCategory/{id}")
	public GenericDataDTO getAllByResoReasons(@PathVariable Long id, HttpServletRequest req) {
		String SUBMODULE = getModuleNameForLog() + " [getALlByResoReasons] ";
//		TraceContext traceContext = tracer.currentSpan().context();
		HashMap<String, Object> response = new HashMap<>();
		MDC.put("type", "Search");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		Integer RESP_CODE = APIConstants.FAIL;
		try {
			genericDataDTO = GenericDataDTO.getGenericDataDTO(resolutionReasonsService.findByResoReasons(id));
			if (null != genericDataDTO) {

				if (genericDataDTO.getDataList().isEmpty())
				{
					genericDataDTO = new GenericDataDTO();
					genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
					genericDataDTO.setResponseMessage("No Record Found!");
					genericDataDTO.setDataList(new ArrayList<>());
					genericDataDTO.setTotalRecords(0);
					genericDataDTO.setPageRecords(0);
					genericDataDTO.setCurrentPageNumber(1);
					genericDataDTO.setTotalPages(1);
					RESP_CODE = APIConstants.NOT_FOUND;
					log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+ "search Root cause by SubCategory" + LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);


				}

				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Root cause by SubCategory" +  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
				return genericDataDTO;
			}

		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Root cause by SubCategory id : " +id+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			return genericDataDTO;
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}



	@Override
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_DELETE + "\")")
	@PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
	public GenericDataDTO delete(@RequestBody ResolutionReasonsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
		GenericDataDTO genericDataDTO = new GenericDataDTO();
//		TraceContext traceContext = tracer.currentSpan().context();
		HashMap<String, Object> response = new HashMap<>();
		Integer RESP_CODE = APIConstants.FAIL;
		MDC.put("type", "Delete");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());

		try {
			ResolutionReasonsDTO dtoData = resolutionReasonsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
//			ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
			boolean isAlreadyInUse = resolutionReasonsService.isReasonAlreadyInUse(entityDTO.getId());
//            entityDTO.setMvnoId(dtoData.getMvnoId());
			if(isAlreadyInUse){
			resolutionReasonsService.deleteEntity(entityDTO);
				genericDataDTO.setData(entityDTO);
			genericDataDTO.setTotalRecords(1);
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Success");
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
			else{
				genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
				genericDataDTO.setResponseMessage("Root Cause Already in use");
				RESP_CODE =HttpStatus.METHOD_NOT_ALLOWED.value();
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_INFO + DeleteContant.MATRIX_EXIST +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}


		} catch (Exception ex) {
			if (ex instanceof DataNotFoundException) {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
				genericDataDTO.setResponseMessage("Not Found");
				RESP_CODE = HttpStatus.NOT_FOUND.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE +RESP_CODE );
			}
            else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
				RESP_CODE =HttpStatus.EXPECTATION_FAILED.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete root cause"+LogConstants.LOG_BY_NAME+entityDTO.getName()+LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO +"Unable too update tat "+LogConstants.LOG_STATUS_CODE +RESP_CODE );
			}
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	@Override
	public GenericDataDTO getAllWithoutPagination() {
		return resolutionReasonsService.getresolutionResonList();
	}

	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_EDIT + "\")")
	@PostMapping("/uploadFile/{rootCauseId}")
	public GenericDataDTO uploadFiles(@Valid @PathVariable("rootCauseId") Long rootCauseId, @RequestPart("fileList") List<MultipartFile> fileList, Authentication authentication, HttpServletRequest req) throws Exception {
		ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
		GenericDataDTO genericDataDTO = new GenericDataDTO();
//		TraceContext traceContext = tracer.currentSpan().context();
		org.apache.log4j.MDC.put("type", "Update");
		org.apache.log4j.MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		try{
			ResolutionReasons	entityDTO= resolutionReasonsRepository.findById(rootCauseId).orElse(null);
			ResolutionReasonsDTO dtoData = resolutionReasonsService.getEntityForUpdateAndDelete(rootCauseId);
			if (Objects.nonNull(dtoData)) {
				genericDataDTO = resolutionReasonsService.uploadDoccuments(entityDTO,fileList, authentication, req);
				RESP_CODE = APIConstants.SUCCESS;
				genericDataDTO.setResponseCode(RESP_CODE);
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Upload Doccument to root cause"+LogConstants.LOG_BY_NAME+rootCauseId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Upload Doccument to root cause"+LogConstants.LOG_BY_NAME+dtoData.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}catch (Exception ex){
			if (ex instanceof DataNotFoundException) {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
				genericDataDTO.setResponseMessage("Not Found");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Upload Doccument to root cause"+LogConstants.LOG_BY_NAME+rootCauseId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else if (ex instanceof CustomValidationException){
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
				genericDataDTO.setResponseMessage(ex.getMessage());
				RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Upload Doccument to root cause"+LogConstants.LOG_BY_NAME+rootCauseId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage("Failed to Upload document. Please try after some time");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Upload Doccument to root cause"+LogConstants.LOG_BY_NAME+rootCauseId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}
		finally {
			org.apache.log4j.MDC.remove("type");
			org.apache.log4j.MDC.remove("userName");
			org.apache.log4j.MDC.remove("traceId");
			org.apache.log4j.MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_EDIT + "\")")
	@GetMapping("/downloadfile/{resolutionMappingId}/{uniqueName}")
	public ResponseEntity<Resource> downloadFiles(@Valid @PathVariable("resolutionMappingId") Long resolutionMappingId, @PathVariable("uniqueName") String uniqueName, Authentication authentication, HttpServletRequest req) throws Exception {
		ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
//		TraceContext traceContext = tracer.currentSpan().context();
		org.apache.log4j.MDC.put("type", "Update");
		org.apache.log4j.MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Resource resource = null;
		Integer RESP_CODE = APIConstants.FAIL;
		try {
			if (Objects.nonNull(resolutionMappingId)) {
				resource = resolutionReasonsService.downloadDocument(resolutionMappingId, uniqueName);
				String contentType = "application/octet-stream";
				if (resource != null && resource.exists()) {
					log.info("Downloading document with  " + resolutionMappingId + " downloaded Successfully  :  request: { From : {} }; Response : {{}}");
					return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
				} else {
					String errorMessage = "File not found: " + uniqueName + " for resolutionMappingId: " + resolutionMappingId;
					log.error(errorMessage);
					return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error-Message", errorMessage).build();
				}
			}
		} catch (Exception ex) {
			log.error("Unable to downloadDocument " + resolutionMappingId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
			ApplicationLogger.logger.error(ex.getMessage());
		} finally {
			org.apache.log4j.MDC.remove("type");
			org.apache.log4j.MDC.remove("userName");
			org.apache.log4j.MDC.remove("traceId");
			org.apache.log4j.MDC.remove("spanId");
		}
		return null;
	}
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_EDIT + "\")")
	@DeleteMapping("/deletefiles/{resolutionMappingId}")
	public GenericDataDTO deleteFiles(@Valid @PathVariable("resolutionMappingId") Long resolutionMappingId,  Authentication authentication, HttpServletRequest req) throws Exception {
		ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
		GenericDataDTO genericDataDTO = new GenericDataDTO();
//		TraceContext traceContext = tracer.currentSpan().context();
		org.apache.log4j.MDC.put("type", "Update");
		org.apache.log4j.MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		try{
			if (Objects.nonNull(resolutionMappingId)) {
				resolutionReasonsService.deleteDocument(resolutionMappingId);
				genericDataDTO.setResponseCode(APIConstants.SUCCESS);
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}catch (Exception ex){
			if (ex instanceof DataNotFoundException) {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
				genericDataDTO.setResponseMessage("Not Found");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else if (ex instanceof CustomValidationException){
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
				genericDataDTO.setResponseMessage(ex.getMessage());
				RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage("Failed to Delete document. Please try after some time");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Document for root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}
		finally {
			org.apache.log4j.MDC.remove("type");
			org.apache.log4j.MDC.remove("userName");
			org.apache.log4j.MDC.remove("traceId");
			org.apache.log4j.MDC.remove("spanId");
		}
		return genericDataDTO;
	}
	@GetMapping("/exportResolutionDetails/{resolutionId}")
	public ResponseEntity<Map<String, Object>> exportResolutionDetails(@PathVariable(name = "resolutionId") Long resolutionId, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		org.slf4j.MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
		try {
			List<Map<String, String>> dataToExport = resolutionReasonsService.dataToExport(resolutionId);
			response.put("dataToExport", dataToExport);
			return responseController.apiResponse(APIConstants.SUCCESS, response);
		} catch (Exception e) {
			log.error("Error while fetch Resolution Data By Id name: " + resolutionId + "" + e.getMessage());
			Integer responseCode = APIConstants.FAIL;
			response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
			return responseController.apiResponse(responseCode, response);
		} finally {
			org.slf4j.MDC.remove(APIConstants.TYPE);
		}
	}
	@PreAuthorize("validatePermission(\"" + MenuConstants.rootcause.ROOT_CAUSE_EDIT + "\")")
	@GetMapping("/fileList/{resolutionMappingId}")
	public GenericDataDTO getFiles(@Valid @PathVariable("resolutionMappingId") Long resolutionMappingId,  Authentication authentication, HttpServletRequest req) throws Exception {
		ResolutionReasonsService resolutionReasonsService = SpringContext.getBean(ResolutionReasonsService.class);
		GenericDataDTO genericDataDTO = new GenericDataDTO();
//		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("type", "Update");
		MDC.put("userName", getLoggedInUser().getFirstName());
//		MDC.put("traceId",traceContext.traceIdString());
//		MDC.put("spanId",traceContext.spanIdString());
		Integer RESP_CODE = APIConstants.FAIL;
		try{
			if (Objects.nonNull(resolutionMappingId)) {
				genericDataDTO.setDataList(resolutionReasonsService.getFileList(resolutionMappingId));
				genericDataDTO.setResponseCode(APIConstants.SUCCESS);
				RESP_CODE = APIConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage(MessageConstants.RESOLUTION_REASON_NAME_EXITS);
				RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}catch (Exception ex){
			if (ex instanceof DataNotFoundException) {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
				genericDataDTO.setResponseMessage("Not Found");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else if (ex instanceof CustomValidationException){
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
				genericDataDTO.setResponseMessage(ex.getMessage());
				RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Doccument to root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
			} else {
				ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
				genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
				genericDataDTO.setResponseMessage("Failed to Delete document. Please try after some time");
				RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
				log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Document for root cause"+LogConstants.LOG_BY_NAME+resolutionMappingId+  LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE + RESP_CODE);
			}
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}
}
