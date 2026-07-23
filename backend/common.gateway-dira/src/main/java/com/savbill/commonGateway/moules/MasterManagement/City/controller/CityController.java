package com.savbill.commonGateway.moules.MasterManagement.City.controller;


import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.model.CityPojo;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import brave.Tracer;
import java.util.stream.Collectors;

@Controller
@RequestMapping(UrlConstants.BASE_API_URL)
public class CityController extends ApiBaseController {


	
	private static final String MODEL_DISP_NAME="City";
	private static final String MODEL_URI_NAME="city";    
    private static final String RETURN_URI_INDEX="redirect:/city/1";
    private static final String RETURN_URI_LIST="postpaid/address/citylist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/address/cityform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private CityService entityService;

    @Autowired
    private CountryService countryService;
    
    @Autowired
    private StateService stateService;

    private Logger log = LoggerFactory.getLogger(CityController.class);

    @Autowired
    private   Tracer tracer;
    @Autowired
    private MessagesPropertyConfig messagesProperty;

//    @ModelAttribute("statusMap")
//    TreeMap<String, String> getStatusMap(){
//    	return CommonUtils.getYesNoStatusMap();
//    }
//
//    @ModelAttribute("countryList")
//    List<Country> getCountryList(){
//    	return countryService.getAllActiveEntities();
//    }
//
//    @RequestMapping(value = {"/city/{pageNumber}","/city"}, method = RequestMethod.GET)
//    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	if(pageNumber==null) {
//    		pageNumber=1;
//    	}
//
//    	Page<City> page =null;
//    	if(search!=null && !"".equalsIgnoreCase(search)){
//    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber, CommonConstants.DB_PAGE_SIZE);
//    	}else{
//    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
//    	}
//        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
//    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
//
//        return RETURN_URI_LIST;
//    }
//
//    @RequestMapping("/city/add")
//    public String add(Model model) {
//        model.addAttribute("entity", entityService.getCityForAdd());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping("/city/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) throws Exception{
//        model.addAttribute("entity", entityService.getCityForEdit(id));
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping(value = "/city/save", method = RequestMethod.POST)
//    public String save(City bean,final RedirectAttributes ra) {
//
//    	String operation="edit";
//    	String flashMsg="";
//    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
//
//    	try{
//	    	if(bean !=null && bean.getId()==null){
//	    		operation="add";
////	    		bean.setCreatedById(getLoggedInUserId());
//	    	}else {
////	    		bean.setLastModifiedById(getLoggedInUserId());
//	    	}
//
//    		City save = entityService.saveCity(bean);
//	    	if(save !=null){
//	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="City Added Successfully";
//	        	}else{
//	        		flashMsg="City Updated Successfully";
//	        	}
//	        }else{
//	    		flashMsg="Error Performing operation, Please try after sometime !!!";
//	        }
//    	}catch(Exception e){
//    		flashMsg="error";
//    	}
//
//        ra.addFlashAttribute("flashMsg", flashMsg);
//        ra.addFlashAttribute("flashMsgType", flashMsgType);
//        return RETURN_URI_INDEX;
//
//
//    }
//
//    @RequestMapping("/city/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
//    	entityService.deleteCity(id);
//        ra.addFlashAttribute("flashMsg", "DelSuccess");
//        return RETURN_URI_INDEX;
//    }
//
//    @RequestMapping(value = "/city/searchbystate/{sid}", method = RequestMethod.GET)
//    public @ResponseBody List<City> findCityByState(@PathVariable Integer sid) {
//    	if(sid !=null) {
//    		State s= stateService.get(sid);
//    		if(s!=null)
//    			return entityService.findByState(s);
//    		else
//    			return null;
//    	}else {
//    		return null;
//    	}
//    }


    // from api controller
    // City

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_VIEW
//            + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY + "\")")
    @PostMapping("/city/list")
    public ResponseEntity<?> getCityList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<City> cityList = null;
        CityPojo pojo = new CityPojo();
        try {
            CityService cityService = SpringContext.getBean(CityService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            cityList = cityService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                    requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != cityList && 0 < cityList.getSize()) {
                List<CityPojo> list = cityService.convertResponseModelIntoPojo(cityList.getContent());
                list.sort((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
                response.put("cityList", list);
            } else
                response.put("cityList", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All City list"+LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All City list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All City list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response, cityList);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY + "\")")
    @PostMapping("/city/search")
    public ResponseEntity<?> searchCity(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Page<City> cityList = null;
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search City using keyword : "+requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            CityService cityService = SpringContext.getBean(CityService.class);
            cityList = cityService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (cityList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("countryList", new ArrayList<>());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search City using keyword :  " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +Response);
                return apiResponse(Response, response, cityList);

            }
            if (null != cityList && 0 < cityList.getSize()) {
                response.put("cityList", cityService.convertResponseModelIntoPojo(cityList.getContent()));
            } else {
                response.put("cityList", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search City using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE  + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search City using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (RuntimeException re) {
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search City using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage()+ LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search City using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response, cityList);
    }

    @GetMapping("/city/all")
    public ResponseEntity<?> getAllCityList(HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        CityPojo pojo = new CityPojo();
        try {
            CityService cityService = SpringContext.getBean(CityService.class);
            List<CityPojo> cityList = cityService.getAllActiveEntities();
//            response.put("cityList", cityService.convertResponseModelIntoPojo(cityList).stream()
//                    .sorted(Comparator.comparing(CityPojo::getId).reversed()).collect(Collectors.toList()));
            response.put("cityList",cityList.stream().sorted(Comparator.comparing(CityPojo::getId).reversed()).collect(Collectors.toList()));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "fetch All City list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All City list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All City list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY + "\")")
    @GetMapping("/city/{id}")
    public ResponseEntity<?> getCityById(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        CityPojo pojo = new CityPojo();
        long startTime = System.nanoTime();  // Start measuring
        try {
            CityService cityService = SpringContext.getBean(CityService.class);
            City city = cityService.get(id);
            if (city == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "City Not Found!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch City"+LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                response.put("cityList", cityService.convertCityModelToCityPojo(city));
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch City"+ LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +RESP_CODE);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CITY, AclConstants.OPERATION_CITY_VIEW,
//                        req.getRemoteAddr(), null, city.getId().longValue(), city.getName());

            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch City by" +LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch City"+LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }


        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_ADD + "\")")

    /**
     * Create City API
     * @Author Darshan
     * @param pojo
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY_CREATE + "\")")
    @PostMapping("/city")
    public ResponseEntity<?> createCity(@Valid @RequestBody CityPojo pojo, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        long startTime = System.nanoTime();  // Start measuring
        try {
            CityService cityService = SpringContext.getBean(CityService.class);
            cityService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            boolean flag = cityService.duplicateVerifyStateAtSave(pojo.getName(), pojo.getCountryId(),
//                    pojo.getStatePojo().getId());
            boolean flag = cityService.duplicateVerification(pojo.getName(), null,pojo.getCountryId(), pojo.getStatePojo().getId(), CommonConstants.OPERATION_ADD);
            if (flag) {
                pojo = cityService.save(pojo);
                response.put("city", pojo);
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create City"+LogConstants.LOG_BY_NAME+pojo.getName() +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +RESP_CODE);
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create City" +LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Input size is Exceeded" + LogConstants.LOG_STATUS_CODE +RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CITY, AclConstants.OPERATION_CITY_ADD,
//                    req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create City" +LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }catch(DataIntegrityViolationException exc){
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.info(LogConstants.REQUEST_FROM+req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Create City" + LogConstants.LOG_BY_NAME+pojo.getName() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR+exc.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create City" + LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_EDIT + "\")")

    /**
     * Update City API
     * @Author Darshan
     * @param pojo
     * @param id
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY_EDIT + "\")")
    @PutMapping("/city/{id}")
    public ResponseEntity<?> updateCity(@Valid @RequestBody CityPojo pojo, @PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        long startTime = System.nanoTime();  // Start measuring
        try {
            CityService cityService = SpringContext.getBean(CityService.class);
            pojo.setId(id);
            cityService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            boolean flag = cityService.duplicateVerifyStateAtEdit(pojo.getName(), pojo.getCountryId(),
//                    pojo.getStatePojo().getId(), pojo.getId());
            boolean flag = cityService.duplicateVerification(pojo.getName(), pojo.getId(), pojo.getCountryId(), pojo.getStatePojo().getId(), CommonConstants.OPERATION_UPDATE);
            if (flag) {
                pojo = cityService.update(pojo, req);
                response.put("city", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Updated");
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Update City"+ LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "Access denined for Update operation " + LogConstants.LOG_STATUS_CODE +RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CITY, AclConstants.OPERATION_CITY_EDIT,
//                    req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {

            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Update City"+ LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {

            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Update City"+ LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CITY_ALL + "\",\"" + AclConstants.OPERATION_CITY_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.CITY_DELETE + "\")")
    @DeleteMapping("/city/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        CityService cityService = SpringContext.getBean(CityService.class);
        long startTime = System.nanoTime();  // Start measuring
        try {
            City city = cityService.getCityForUpdateAndDelete(id);
            CityPojo pojo = cityService.convertCityModelToCityPojo(city);
            if (city != null) {
                cityService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                cityService.deleteCity(id);
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete City"+ LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +RESP_CODE);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CITY, AclConstants.OPERATION_CITY_DELETE,
//                        req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
            } else {
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete City"+ LogConstants.LOG_BY_NAME+pojo.getName() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +RESP_CODE);

            }
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete City"+LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete City By Id : "+id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);

            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete City By Id : " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +RESP_CODE);

            }
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping(value = "/city/excel")
    public void cityExcel(HttpServletResponse response) throws Exception {
        CityService service = SpringContext.getBean(CityService.class);
        exportToExcel(service, response);
    }

    @GetMapping(value = "/city/pdf")
    public void cityPDF(HttpServletResponse response) throws Exception {
        CityService service = SpringContext.getBean(CityService.class);
        exportToPDF(service, response);
    }




}
