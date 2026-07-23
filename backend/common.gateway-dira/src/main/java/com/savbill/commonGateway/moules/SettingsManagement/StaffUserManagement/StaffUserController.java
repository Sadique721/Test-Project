package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.exceptions.CustomMessageException;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.StaffUserAllPojo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.StaffUserDropdownDTO;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.UserPasswordChangePojo;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class StaffUserController extends ExBaseAbstractController<StaffUserPojo> {
    public static final String DOMAINNAME = StaffUser.class.getName() + Long.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffUserController.class);
    private static final String MODULE = " [StaffUserController] ";
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    private StaffUserService staffUserService;
    @Autowired
    private MessagesPropertyConfig messagesProperty;
    private RoleService roleService;
    @Autowired
    private StaffUserMapper staffUserMapper;
    @Autowired
    private Tracer tracer;
    @Autowired
    StaffUserRepository staffUserRepository;

    public StaffUserController(StaffUserService service) {
        super(service);
    }

    @Autowired
    public void setStaffUser(StaffUserService staffUserService, RoleService roleService) {
        this.staffUserService = staffUserService;
        this.roleService = roleService;
    }

//    @RequestMapping(value = "/staffuser")
//    public String index() {
//        return "redirect:/staffuser/1";
//    }

//    @RequestMapping(value = {"/staffuser/{pageNumber}"}, method = RequestMethod.GET)
//    public String list(@PathVariable Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsg") String flashMsg, Model model) {
//
//        Page<StaffUser> page =null;
//        if(search!=null && !"".equalsIgnoreCase(search)){
//            page = staffUserService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
//        }else{
//            page = staffUserService.getList(pageNumber, CommonConstants.DB_PAGE_SIZE,"id",CommonConstants.SORT_ORDER_ASC,null);
//        }
//        model.addAttribute("custStatusMap", CommonUtils.getCustStatusMap());
//        setPaginationParameters("User", flashMsg, search, model, page);
//        return "common/staffuser/stafflist";
//    }
//    public void setPaginationParameters(String entity, String flashMsg, String search, Model model, Page<StaffUser> page) {
//        Integer dbPageSize = CommonConstants.DB_PAGE_SIZE;
//        Integer dispPageSize = CommonConstants.DISP_PAGE_SIZE;
//        setPaginationParameters(entity, flashMsg, search, model, page, dbPageSize, dispPageSize);
//    }
//    public void setPaginationParameters(String entity, String flashMsg, String search, Model model, Page<StaffUser> page, Integer databasePageSize, Integer displayPageSize) {
//
//        Integer dbPageSize = databasePageSize;
//        Integer dispPageSize = displayPageSize;
//
//        int current = page.getNumber() + 1;
//        int begin = Math.max(1, current - dispPageSize + 1);
//        int end = Math.min(begin + dispPageSize - 1, page.getTotalPages());
//        int next = -1;
//        int prev = -1;
//
//        if (end < page.getTotalPages()) {
//            next = current + 1;
//        }
//        if (current > 1) {
//            prev = current - 1;
//        }
//
//        if (end < begin) {
//            end = begin;
//        }
//
//        long recordStart = (((current - 1) * dbPageSize) + 1);
//        long recordEnd = current * dbPageSize;
//        if (recordEnd > page.getTotalElements())
//            recordEnd = page.getTotalElements();
//
//        String recordMsg = "Showing " + recordStart + " to " + recordEnd + " Entities of " + page.getTotalElements();
//        model.addAttribute("list", page);
//        model.addAttribute("beginIndex", begin);
//        model.addAttribute("endIndex", end);
//        model.addAttribute("currentIndex", current);
//        model.addAttribute("nextIndex", next);
//        model.addAttribute("prevIndex", prev);
//        model.addAttribute("lastIndex", ((page.getTotalPages() == 0) ? 1 : page.getTotalPages()));
//        model.addAttribute("recordMsg", recordMsg);
//        model.addAttribute("searchtext", search);
//        if (page.getTotalElements() == 0) {
//            model.addAttribute("norecords", "false");
//            model.addAttribute("noRecordMsg", "No Records Found.");
//        }
//
//        if (search != null && !"".equalsIgnoreCase(search)) {
//            model.addAttribute("search", "?s=" + search);
//        } else {
//            model.addAttribute("search", "");
//        }
//
//
//        //flashMsg="error";
//
//        if (flashMsg != null && !"".equalsIgnoreCase(flashMsg)) {
//            if (flashMsg.equalsIgnoreCase("AddSuccess")) {
//                model.addAttribute("successFlash", entity + " Added Successfully");
//            } else if (flashMsg.equalsIgnoreCase("DelSuccess")) {
//                model.addAttribute("successFlash", entity + " Deleted Successfully");
//            } else if (flashMsg.equalsIgnoreCase("EditSuccess")) {
//                model.addAttribute("successFlash", entity + " Updated Successfully");
//            } else if (flashMsg.equalsIgnoreCase("sucess")) {
//                model.addAttribute("successFlash", "Opereation Performed Successfully");
//            } else if (flashMsg.equalsIgnoreCase("error")) {
//                model.addAttribute("errorFlash", "Error performing operation, Please try again later");
//
//            }
//        }
//    }

//    @RequestMapping("/staffuser/add")
//    public String add(Model model) throws Exception {
//        model.addAttribute("staffuser", staffUserService.getStaffUserForAdd());
//        model.addAttribute("custStatusMap", CommonUtils.getCustStatusMap());
//        model.addAttribute("roleMap", roleService.getAllEntities());
//        return "common/staffuser/staffform";
//    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.StaffUser', '2')")
//    @RequestMapping("/staffuser/changepassword")
//    public String changepassword(Model model, final RedirectAttributes ra, @ModelAttribute("flashMsg") String flashMsg) {
//        logger.info("In Change Passowrd");
//        model.addAttribute("staffuser", new StaffUser());
//        if (flashMsg.equalsIgnoreCase("ERROR")){
//            model.addAttribute("infoFlash", "Password Change Failed");
//        }
//        if (flashMsg.equalsIgnoreCase("SUCCESS")){
//            model.addAttribute("infoFlash", "Password Change Success");
//        }
//        return "common/staffuser/changepassword";
//
//    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.common.StaffUser', '2')")
//    @RequestMapping(value = "/staffuser/updatepassword",method = RequestMethod.POST)
//    public String updatepassword(StaffUser staffuser, final RedirectAttributes ra) {
//        logger.info("In updatepassword");
//        List dbStaffUserList=staffUserService.getStaffUserFromUsername(staffuser.getUsername());
//        if(dbStaffUserList==null || dbStaffUserList.size()<=0) {
//            logger.info("User Not Found");
//            String flashMsg="ERROR";
//            ra.addFlashAttribute("flashMsg", flashMsg);
//        }
//        else {
//            StaffUser dbstaffuser=(StaffUser) dbStaffUserList.get(0);
//            logger.info("Entered Old Password:"+dbstaffuser.getPassword()+":In DB Password:"+dbstaffuser.getPassword());
//            if(dbstaffuser.getPassword().equals(dbstaffuser.getPassword())) {
//                logger.info("Password Matched. Changing Password");
//
//                PasswordEncoder encoder = new BCryptPasswordEncoder();
//                staffuser.setNewpassword(encoder.encode(staffuser.getNewpassword()));
//
//                dbstaffuser.setPassword(staffuser.getNewpassword());
//                staffUserService.save(dbstaffuser);
//                String flashMsg="SUCCESS";
//                ra.addFlashAttribute("flashMsg", flashMsg);
//            }
//            else {
//                String flashMsg="ERROR";
//                ra.addFlashAttribute("flashMsg", flashMsg);
//                logger.info("Old and New Password dont match");
//            }
//        }
//        return "redirect:/staffuser/changepassword";
//    }


//    @RequestMapping("/staffuser/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) throws Exception{
//        model.addAttribute("staffuser", staffUserService.getStaffUserForEdit(id));
//        model.addAttribute("custStatusMap", CommonUtils.getCustStatusMap());
//        model.addAttribute("roleMap", roleService.getAllEntities());
//        return "common/staffuser/staffform";
//    }

//    @PostMapping("/authenticateStaff")
//    public String authenticateStaff(@ModelAttribute StaffUser staffUser, Model model, HttpServletRequest request)
//    {
//        logger.info("I am in authenticateStaff:"+staffUser.getUsername());
//
//        List dbStaffUserList=staffUserService.getStaffUserFromUsername(staffUser.getUsername());
//        if(dbStaffUserList==null || dbStaffUserList.size()<=0) {
//            logger.info("User Not Found");
//            model.addAttribute("staffuser",new StaffUser());
//            model.addAttribute("errorFlash", "Username or Password not matched");
//            return "login";
//        }
//        else {
//            StaffUser dbstaffuser=(StaffUser) dbStaffUserList.get(0);
//            logger.info("Entered Password:"+staffUser.getPassword()+":In DB Password:"+dbstaffuser.getPassword());
//            if(staffUser.getPassword().equals(dbstaffuser.getPassword())) {
//                logger.info("Login Success");
//                LocalDateTime ldt = LocalDateTime.now();
//                dbstaffuser.setLast_login_time(ldt);
//                dbstaffuser.setFailcount(0);
//                staffUserService.save(dbstaffuser);
//                request.getSession().setAttribute("SESSIONDETAIL","SUCCESS");
//                request.getSession().setAttribute("USERNAME",staffUser.getUsername());
//                return "dashboard";
//            }
//            else
//            {
//                logger.info("Password Not Match");
//                model.addAttribute("staffuser",new StaffUser());
//                model.addAttribute("errorFlash", "Username or Password not matched");
//
//                int intFailCount=dbstaffuser.getFailcount();
//                intFailCount++;
//                dbstaffuser.setFailcount(intFailCount);
//                staffUserService.save(dbstaffuser);
//
//                return "login";
//            }
//        }
//    }


    @GetMapping("/login")
    public String authenticateUserForm(Model model) {
        model.addAttribute("staffuser", new StaffUser());
        return "login";
    }

    @GetMapping("/logoutUser")
    public String logout(Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        model.addAttribute("successFlash", "Logout Successfully");
        return "index";
    }

//    @RequestMapping(value = "/staffuser/save", method = RequestMethod.POST)
//    public String save(StaffUser staffUser, final RedirectAttributes ra) {
//        String operation="edit";
//        String flashMsg="";
//
//        try{
//            if(staffUser !=null && staffUser.getId()==null){
//                operation="add";
//            }
//            StaffUser save = staffUserService.saveStaffUser(staffUser);
//            if(save !=null){
//                if(operation.equalsIgnoreCase("add")){
//                    flashMsg="AddSuccess";
//                }else{
//                    flashMsg="EditSuccess";
//                }
//            }else{
//                flashMsg="error";
//            }
//        }catch(Exception e){
//            flashMsg="error";
//        }
//        ra.addFlashAttribute("flashMsg", flashMsg);
//        return "redirect:/staffuser/1";
//
//    }

//    @RequestMapping("/staffuser/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) {
//        try{
//            staffUserService.deleteStaffUser(id);
//            ra.addFlashAttribute("flashMsg", "DelSuccess");
//        }catch(Exception e){
//            ra.addFlashAttribute("flashMsg", "error");
//        }
//        return "redirect:/staffuser/1";
//
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @PostMapping("/staffuser/list")
    public ResponseEntity<?> getAllStaffUsers(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "product", defaultValue = "BSS", required = true) String product, @RequestParam(name = "staffId", required = false) Long staffId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        HashMap<String, Object> response = new HashMap<>();
        Page<StaffUserPojo> staffUserList = null;
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            staffUserList = staffUserService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), product, staffId);
            if (null != staffUserList && 0 < staffUserList.getSize())
                response.put("staffUserlist", staffUserList.getContent());
            else response.put("staffUserlist", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, staffUserList);
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//            + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")

    // TODO Remove PreAuthorize due to this API is also dependent API
//@PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER + "\")")
    @PostMapping("/staffuser/search")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "product", defaultValue = "BSS", required = false) String product, @RequestParam(name = "staffId", required = false) Long staffId, HttpServletRequest req) {
        GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
        genericSearchDTO.setFilter(requestDTO.getFilters());
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Search");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            int adjustedPage = Math.max(0, requestDTO.getPage() - 1);
            genericDataDTO = staffUserService.search(requestDTO.getFilters(), adjustedPage, requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), product, staffId);
            if (genericSearchDTO.getFilter().isEmpty()) {
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }
            if (genericDataDTO != null && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseMessage("Successfully Fetched");
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PostMapping("/staffuser/byServiceAreaWithPagination")
    public GenericDataDTO byServiceAreaWithPagination(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "product", defaultValue = "BSS", required = false) String product, @RequestParam(name = "staffId", required = false) Long staffId, HttpServletRequest req) {
        GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
        genericSearchDTO.setFilter(requestDTO.getFilters());
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Search");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            int adjustedPage = Math.max(0, requestDTO.getPage() - 1);
//            genericDataDTO = staffUserService.search(requestDTO.getFilters(), adjustedPage, requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), product, staffId);
            genericDataDTO = staffUserService.searchStaffUserByServiceAreaWithPagination(requestDTO.getFilters(), adjustedPage, requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), product, staffId);
            if (genericSearchDTO.getFilter().isEmpty()) {
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }
            if (genericDataDTO != null && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseMessage("Successfully Fetched");
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//            + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
// TODO Remove PreAuthorize due to this API is also dependent API
//@PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER + "\")")
    @GetMapping("/staffuser/allActive")
    public ResponseEntity<?> getAllActiveStaff(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            response.put("staffUserlist", staffUserService.getAllActiveEntities());
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/staffuser/allActiveByServiceArea")
    public ResponseEntity<?> getAllActiveStaff(@RequestBody List<Long> serviceAreaIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            response.put("staffUserlist", staffUserService.getAllActiveEntitiesByServiceAreaIds(serviceAreaIds));
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser by ServiceArea " + serviceAreaIds + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser by ServiceArea " + serviceAreaIds + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser by ServiceArea " + serviceAreaIds + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    //    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER + "\")")
    @GetMapping("/staffuser/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocalDateTime startTime=LocalDateTime.now();
           StaffUserPojo staffUserPojo = staffUserRepository.findByStaffUserId(id);
            LocalDateTime endTime=LocalDateTime.now();
            Duration duration=Duration.between(startTime,endTime);
            System.out.println("Total Time Taken For Fetch Staff"+duration.toMillis());
//            StaffUser staffUser=staffUserMapper.dtoToDomain(staffUserPojo ,new CycleAvoidingMappingContext());
            if (staffUserPojo == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Staff Not Found!");
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
//                response.put("Staff", staffUser);
//                response.put("Staff", staffUserService.convertStaffUserModelToStaffUserPojo(staffUser));
                response.put("Staff", staffUserService.convertStaffUserToStaffUserPojo(staffUserPojo));
                RESP_CODE = APIConstants.SUCCESS;
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER,
//                        AclConstants.OPERATION_STAFF_USER_VIEW, req.getRemoteAddr(), null, staffUser.getId().longValue(), staffUser.getFullName());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }

        } catch (CustomValidationException ce) {
            //        ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (EntityNotFoundException enfe) {
            ApplicationLogger.logger.error(MODULE + enfe.getStackTrace(), enfe);
            enfe.printStackTrace();
            RESP_CODE = 404;
            response.put(APIConstants.ERROR_TAG, "Not Found!");
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + enfe.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getStaffUser/{id}")
    public ResponseEntity<?> getStaffUserById(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE;
        HashMap<String, Object> response = new HashMap<>();
        try {
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            LocalDateTime startTime1=LocalDateTime.now();
            StaffUserPojo staffUserPojo = staffUserRepository.findByStaffUserId(id);
            StaffUser user=staffUserMapper.dtoToDomain(staffUserPojo,new CycleAvoidingMappingContext());
            LocalDateTime endTime1=LocalDateTime.now();
            Duration duration=Duration.between(startTime1,endTime1);
            System.out.println("Find By Id" +duration.toMillis());
            if (user == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Staff Not Found!");
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                LocalDateTime startTime=LocalDateTime.now();
                System.out.println("Domain to dto sterted");
                StaffUserPojo pojo = staffUserService.convertStaffUserModelToStaffUserPojo(user);
                LocalDateTime endTime=LocalDateTime.now();
                System.out.println("Domain to dto Ended");
                Duration duration1=Duration.between(startTime,endTime);
                System.out.println("Time taken to conver entity to dto" +duration1.toMillis());
                if (pojo.getServiceAreaIdsList() != null && pojo.getServiceAreaIdsList().isEmpty()) {
                    Integer mvnoId = getMvnoIdFromCurrentStaff();
                    pojo.setServiceAreasNameList(serviceAreaRepository.findServiceAreaNameByMvnoId(mvnoId));
                    pojo.setServiceAreaIdsList(serviceAreaRepository.findServiceAreaIdByMvnoId(mvnoId));

//                    List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByMvnoId(mvnoId);
//                    if (serviceAreaList != null && !serviceAreaList.isEmpty()) {
//                        pojo.setServiceAreasNameList(serviceAreaList.stream().map(x -> x.getName()).collect(Collectors.toList()));
//                        pojo.setServiceAreaIdsList(serviceAreaList.stream().map(x -> x.getId()).collect(Collectors.toList()));
//                    }
                }
                response.put("Staff", pojo);
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (EntityNotFoundException enfe) {
            ApplicationLogger.logger.error(MODULE + enfe.getStackTrace(), enfe);
            enfe.printStackTrace();
            RESP_CODE = 404;
            response.put(APIConstants.ERROR_TAG, "Not Found!");
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + enfe.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//            + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
//    @GetMapping("/staffuserByRole/{roleId}")
//    public ResponseEntity<?> getStaffUsersByRole(@PathVariable Long roleId) {
//        MDC.put("type", "Fetch" );
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            if (null == roleId) {
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                response.put(APIConstants.ERROR_TAG, "Please Provide Role!");
//                logger.error("Unable to Fetch Staff user role " + roleId + " :  request: { From : {},}; Response : {{}};Error :{} ;", MODULE, RESP_CODE, response);
//                return apiResponse(RESP_CODE, response);
//            }
//            List<StaffUserPojo> staffUserlist = staffUserService.findStaffUserByRoleId(roleId);
//            response.put("staffUserlist", staffUserlist);
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Fetching Staffuser role for role " + roleId + "  :  request: { From : {},}; Response : {{}}", MODULE, RESP_CODE, response);
//        } catch (Exception ce) {
//            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
//            ce.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Fetch Staff user role " + roleId + " :  request: { From : {}, }; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//            + AclConstants.OPERATION_STAFF_USER_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER_CREATE + "\", \"" + MenuConstants.IwfSettings.STAFFUSER_CREATE + "\")")
    @PostMapping("/staffuser")
    public ResponseEntity<?> createStaffUser(@Valid @RequestBody StaffUserPojo pojo, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            staffUserService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = staffUserService.duplicateVerifyAtSave(pojo.getUsername());
            if (flag) {
                pojo = staffUserService.save(pojo, req);
                response.put("staffuser", pojo);
                response.put(CommonConstants.RESPONSE_MESSAGE, "Successfully Created");
                RESP_CODE = APIConstants.SUCCESS;
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER, AclConstants.OPERATION_STAFF_USER_ADD,
//                        req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getFullName());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_STAFF_USER);
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//        + AclConstants.OPERATION_STAFF_USER_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER_EDIT + "\", \"" + MenuConstants.IwfSettings.STAFFUSER_EDIT + "\")")
    @PutMapping("/staffuser/{id}")
    public ResponseEntity<?> updateStaffUser(@Valid @RequestBody StaffUserPojo pojo, @PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserService.getStaffForUpdateAndDelete(id);
            StaffUser old = staffUserService.get(id);
            StaffUser oldClone = new StaffUser(old);
            if (null != staffUser)
//            pojo.setPassword(staffUser.getPassword());
                pojo.setId(id);
//        String updatedValues = CommonUtils.getUpdatedDiff(staffUserService.get(id), pojo);
            staffUserService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = staffUserService.update(pojo);
            response.put("staffuser", pojo);
            response.put(CommonConstants.RESPONSE_MESSAGE, "Successfully Updated");
            RESP_CODE = APIConstants.SUCCESS;
            StaffUser staffUser1 = staffUserMapper.dtoToDomain(new StaffUserPojo(), new CycleAvoidingMappingContext());
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER, AclConstants.OPERATION_STAFF_USER_EDIT,
//                req.getRemoteAddr(), updatedValues, pojo.getId().longValue(), pojo.getFullName());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + " , Updated StaffUser Details " + UpdateDiffFinder.getUpdatedDiff(oldClone, staffUser1) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
//            + AclConstants.OPERATION_STAFF_USER_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.STAFFUSER_DELETE + "\", \"" + MenuConstants.IwfSettings.STAFFUSER_DELETE + "\")")
    @DeleteMapping("/staffuser/{id}")
    public ResponseEntity<?> deleteStaffUser(@RequestParam Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserService.getStaffForUpdateAndDelete(id);
            StaffUserPojo pojo = staffUserService.convertStaffUserModelToStaffUserPojo(staffUser);

            if (staffUser != null) {
                staffUserService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                staffUserService.deleteStaffUser(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.staffuser.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER,
//                        AclConstants.OPERATION_STAFF_USER_DELETE, req.getRemoteAddr(), null, pojo.getId().longValue(),
//                        pojo.getFullName());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_CHANGE_PASSWORD + "\")")

    @PutMapping("/staffuser/changepassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserPasswordChangePojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserService.changePassword(pojo);
            if (staffUser != null) {
                response.put("data", null);
                response.put("responseCode", HttpStatus.OK.value());
                RESP_CODE = APIConstants.SUCCESS;
                response.put("responseMessage", "Success");
            } else {
                response.put("data", null);
                response.put("responseCode", HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put("responseMessage", "Data Not Found");
            }
        } catch (AlreadyExistException e) {
            RESP_CODE = HttpStatus.CONFLICT.value();
            response.put(APIConstants.MESSAGE, e.getMessage());
        } catch (CustomMessageException cex) {
            RESP_CODE = HttpStatus.CONFLICT.value();
            response.put(APIConstants.MESSAGE, cex.getMessage());
        } catch (Exception ex) {
            response.put("responseCode", HttpStatus.EXPECTATION_FAILED.value());
            response.put("responseMessage", ex.getMessage());
            RESP_CODE = APIConstants.EXPECTATION_FAILED;
        }
        return apiResponse(RESP_CODE, response);
    }
//    @GetMapping("/staff/search")
//    public GenericDataDTO searchStaff(@RequestParam(name = "s", defaultValue = "") String search) throws Exception {
//        MDC.put("type", "Fetch" );
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = MODULE + " [searchStaff()] ";
//        StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//        try {
//            if ("".equals(search)) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please provide search criteria!");
//                logger.error("Unable to Fetch searchStaff by " + search + " :  request: { From : {}, }; Response : {{}};Error :{} ", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            }
//            MDC.remove("type");
//            logger.info("Searcing staff by " + search + "   :  request: { From : {},}; Response : {{}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return GenericDataDTO.getGenericDataDTO(staffUserService.searchStaff(search));
//        } catch (Exception ex) {
//            ///		ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Fetch searchStaff by " + search + "  :  request: { From : {}, }; Response : {{}};Error :{} ;Exception:{}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
//            return genericDataDTO;
//        }
//    }

//    @GetMapping(value = "/staffUser/excel")
//    public void staffUserExcel(HttpServletResponse response) throws Exception {
//        StaffUserService service = SpringContext.getBean(StaffUserService.class);
//        exportToExcel(service, response);
//    }
//
//    @GetMapping(value = "/staffUser/pdf")
//    public void staffUserPDF(HttpServletResponse response) throws Exception {
//        StaffUserService service = SpringContext.getBean(StaffUserService.class);
//        exportToPDF(service, response);
//    }

    //    private void exportToExcel(StaffUserService service, HttpServletResponse response) throws Exception {
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=Excel_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//        Workbook workbook = new XSSFWorkbook();
//        service.excelGenerate(workbook);
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//        outputStream.close();
//    }
//    private void exportToPDF(StaffUserService service, HttpServletResponse response) throws Exception {
//        response.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=Pdf_" + currentDateTime + ".pdf";
//        response.setHeader(headerKey, headerValue);
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        Document pdfDoc = new Document();
//        PdfWriter.getInstance(pdfDoc, response.getOutputStream());
//        service.pdfGenerate(pdfDoc);
//    }
    @GetMapping("/staffList/all")
    public GenericDataDTO getAllActiveStaffList(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            List<StaffUser> staffUserList = staffUserService.getAllActiveEntitiesStaff().stream().filter(x -> x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoStaffUserAllPojo(staffUserList).stream().sorted(Comparator.comparing(StaffUserAllPojo::getId).reversed()).collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
            RESP_CODE = APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {

        return "[StaffUserController]";
    }


    @GetMapping(value = "/staffsByServiceAreaId/{id}")
    public GenericDataDTO staffsByServiceAreaId(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            List<StaffUserPojo> lists = staffUserService.getStaffUserByServiceAreaId(id);
            genericDataDTO.setDataList(lists);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(lists.size());
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getstaffuserbyserviceareaid/{id}")
    public GenericDataDTO getStaffUserByServiceAreaId(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            genericDataDTO.setDataList(staffUserService.getAllStaffByServiceAreaId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserService.getAllStaffByServiceAreaId(id).size());
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/staff/uploadProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadStaffImage(@RequestParam("staffId") Integer staffId, @RequestParam("file") MultipartFile image, HttpServletRequest req) throws IOException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserService.getByStaffId(staffId);
            if (image != null && !(image.getBytes().length == 0)) {
                staffUser.setProfileImage(image.getBytes());
                staffUserService.save(staffUser);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload image StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException exception) {
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setResponseCode(exception.getErrCode());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload image StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + exception.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE);

        } catch (Exception exception) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload image StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + exception.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     * This method returns the profile picture of the staff based on the staff id.
     *
     * @param staffId
     * @return Profile Image of the loggedIn staff
     */
    @GetMapping("/staff/profileImage/{id}")
    public GenericDataDTO getProfilePictureByStaffId(@PathVariable("id") Integer staffId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            Byte[] profileImage = staffUserService.getProfilePictureByStaffId(staffId);
            genericDataDTO.setData(Base64.getEncoder().encodeToString(ArrayUtils.toPrimitive(profileImage)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch staff profile" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception exception) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch staff profile" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + exception.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/staff/generateTokenByMvnoId/{mvnoId}")
    public GenericDataDTO generateTokenByMvnoId(@PathVariable("mvnoId") Integer mvnoId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", "ISP");
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("token Generated !!");
            String token = staffUserService.getRefreshTokenByMvno(mvnoId.longValue());
            genericDataDTO.setData(token);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch staff profile" + LogConstants.REQUEST_BY + "ISP" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;

        } catch (Exception exception) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch staff profile" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + exception.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/staffuser/Activestaff")
    public ResponseEntity<?> getAllActiveStaffUser(HttpServletRequest req, @RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "product", defaultValue = "BSS", required = false) String product) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<StaffUser> staffUserlist = null;
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            staffUserlist = staffUserService.getAllActiveEntities(requestDTO.getPage(), requestDTO.getPageSize(), product);
            response.put("staffUserlist", staffUserService.convertResponseModelIntoPojo(staffUserlist.getContent()));
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, staffUserlist);
    }


    @GetMapping("/staffuser/ActivestaffWithoutPaggination")
    public GenericDataDTO getAllActiveStaffUserWithoutPagination(HttpServletRequest req, @RequestParam(name = "product", defaultValue = "BSS", required = false) String product) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<StaffUser> staffUserlist = null;
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            staffUserlist = staffUserService.getAllActiveEntitiesWithoutPagination(product);
            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoPojo(staffUserlist));
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/staffuser/getByTeamId")
    public GenericDataDTO getByTeamId(HttpServletRequest req, @RequestParam(name = "teamId", required = false) Long teamId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<StaffUser> staffUserlist = null;
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            staffUserlist = staffUserService.getByTeamId(teamId);
            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoPojo(staffUserlist));
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom ") + LogConstants.REQUEST_FOR + "fetch StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/staffList/dropdown/all")
    public GenericDataDTO getAllActiveStaffListForDropdown(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            Integer mvnoId =  staffUserService.getLoggedInUser().getMvnoId();
            List<StaffUserDropdownDTO> staffUserList = staffUserService.getAllActiveEntitiesStaffForDropdown(mvnoId);
            staffUserList.sort(
                    Comparator.comparing(
                            u -> {
                                String username = u.getUsername();
                                return username == null ? null :
                                        username.replaceAll("\\p{C}", "").trim().toLowerCase();
                            },
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    )
            );
            genericDataDTO.setDataList(staffUserList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
            RESP_CODE = APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/staffList/dropdown/all2")
    public GenericDataDTO getAllActiveStaffListForDropdownSortByFullName(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            Integer mvnoId =  staffUserService.getLoggedInUser().getMvnoId();
            List<StaffUserDropdownDTO> staffUserList = staffUserService.getAllActiveEntitiesStaffForDropdown(mvnoId);
            staffUserList.sort(
                    Comparator.comparing(
                            u -> {
                                String fullName = u.getFullName();
                                return fullName == null ? null :
                                        fullName.replaceAll("\\p{C}", "").trim().toLowerCase();
                            },
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    )
            );
            genericDataDTO.setDataList(staffUserList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
            RESP_CODE = APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/staffList/serviceArea/notBind/{serviceAreaId}")
    public GenericDataDTO getAllActiveNonBindedServiceAraStaff(@PathVariable Integer serviceAreaId, HttpServletRequest req, HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<StaffUserPojo> staffUserList = staffUserService.findStaffIdsWithoutServiceArea(serviceAreaId);
            genericDataDTO.setDataList(staffUserList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
            RESP_CODE = APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

    @DeleteMapping("/staffuser/delete/{id}")
    public ResponseEntity<?> deletedStaffUser(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserService.getStaffForUpdateAndDelete(id);
            StaffUserPojo pojo = staffUserService.convertStaffUserModelToStaffUserPojo(staffUser);

            if (staffUser != null) {
                staffUserService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                staffUserService.deleteStaffUser(id);
                response.put(CommonConstants.RESPONSE_MESSAGE,"Deleted successfully");
                RESP_CODE = APIConstants.SUCCESS;
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STAFF_USER,
//                        AclConstants.OPERATION_STAFF_USER_DELETE, req.getRemoteAddr(), null, pojo.getId().longValue(),
//                        pojo.getFullName());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete StaffUser" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/staffList/{parentStaffId}")
    public GenericDataDTO getAllActiveChildStaffListForDropdown(@PathVariable("parentStaffId") Integer parentStaffId,HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.traceIdString());
        try {
            List<StaffUserDropdownDTO> staffUserList = staffUserService.getAllActiveChildStaffForDropdown(parentStaffId);
            genericDataDTO.setDataList(staffUserList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
            RESP_CODE = APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch StaffUser for dropdown" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }



}
