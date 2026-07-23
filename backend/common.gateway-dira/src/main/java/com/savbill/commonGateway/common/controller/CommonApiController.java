package com.savbill.commonGateway.common.controller;

import com.savbill.commonGateway.common.model.LoginPojo;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.repository.AreaRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserController;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.spring.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class CommonApiController extends BaseController{


    private static final Logger logger = LoggerFactory.getLogger(StaffUserController.class);
    private static String MODULE = " [StaffUserController] ";
    private StaffUserService staffUserService;
    @Autowired
    AreaRepository areaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public CommonApiController(StaffUserService staffUserService) {
        this.staffUserService = staffUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            List<StaffUser> dbStaffUserList = staffUserService.getStaffUserFromUsername(pojo.getUsername());
            if (dbStaffUserList == null || dbStaffUserList.size() <= 0) {
                //		ApplicationLogger.logger.info("Username or Password not matched");
                response.put(CommonConstants.RESPONSE_MESSAGE, "Username or Password not matched");
                RESP_CODE = APIConstants.FAIL;
                logger.error("Unable to login for staf " + pojo.getUsername() + "  request: { From : {}}; Response : {message:{}, code:{}}", MODULE, HttpStatus.OK, APIConstants.SUCCESS);
            } else {
                StaffUser dbstaffuser = (StaffUser) dbStaffUserList.get(0);
                ApplicationLogger.logger.info(
                        "Entered Password:" + pojo.getPassword() + ":In DB Password:" + dbstaffuser.getPassword());
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(pojo.getPassword(), dbstaffuser.getPassword())) {
                    //		ApplicationLogger.logger.info("Login Success");
                    LocalDateTime ldt = LocalDateTime.now();
                    dbstaffuser.setLast_login_time(ldt);
                    dbstaffuser.setFailcount(0);
                    staffUserService.save(dbstaffuser);
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Login Success.");
                    RESP_CODE = APIConstants.SUCCESS;
                    logger.info("Login Success for customer " + pojo.getUsername() + " : { From : {}}; Response : {message:{}, code:{}}", MODULE, HttpStatus.OK, APIConstants.SUCCESS);
                } else {
                    //		ApplicationLogger.logger.info("Password Not Match");

                    int intFailCount = 0;//dbstaffuser.getFailcount();
                    if(dbstaffuser.getFailcount() != null)
                        intFailCount = dbstaffuser.getFailcount();
                    intFailCount++;
                    dbstaffuser.setFailcount(intFailCount);
                    staffUserService.save(dbstaffuser);
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Password Not Match");
                    RESP_CODE = APIConstants.FAIL;
                    logger.error("Unable to login " + pojo.getUsername() + ": { From : {}}; Response : {message:{}; code:{}}", MODULE, response, RESP_CODE);
                }
            }
        } catch (CustomValidationException ce) {
            //	ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to login for user " + pojo.getUsername() + "  request: { From : {}}; Response : {message:{}; code:{}; Exception:{}}", MODULE, response, RESP_CODE, ce.getStackTrace(), ce.getStackTrace());
        } catch (Exception ex) {
            //	ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to login for user " + pojo.getUsername() + "   request: { From : {}}; Response : {message:{}; code:{}; Exception:{}}", MODULE, response, RESP_CODE, ex.getStackTrace(), ex.getStackTrace());
        }

        return apiResponse(RESP_CODE, response);
    }

//    @GetMapping("areas/pincode")
//    public ResponseEntity<?> getAreaByPincode(@RequestParam(required = true) Long pincodeId) throws Exception {
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "fetch");
//        try {
//            List<Area> areaList = areaRepository.findAreasByPincode(pincodeId);
//            Integer responseCode = APIConstants.SUCCESS;
//            response.put("areaList", areaList);
//            MDC.remove("type");
//            logger.info("City With name " + areaList + "  is Successfull   :  request: { From : {}}; Response : {message:{};Code:{}}", MODULE, APIConstants.SUCCESS, response);
//            return apiResponse(responseCode, response);
//        } catch (Exception e) {
//            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            MDC.remove("type");
//            logger.error("Unable to Fetch Area With pincode " + pincodeId + " :  request: { From : {}}; Response : {{}Error :{} ;Exception:{}}", MODULE, APIConstants.ERROR_TAG, response, e.getStackTrace());
//            return apiResponse(responseCode, response);
//        }
//    }

    @GetMapping(value = "/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            String token = jwtUtil.getRefreshToken(request);
            RESP_CODE = APIConstants.SUCCESS;
            response.put(CommonConstants.RESPONSE_MESSAGE, "Login Success.");
            response.put("accessToken", token);
            logger.info("Refresh token successful : { From : {}}; Response : {message:{}, code:{}}", MODULE, HttpStatus.OK, APIConstants.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to refresh token request: { From : {}}; Response : {message:{}; code:{}; Exception:{}}", MODULE, response, RESP_CODE, ex.getStackTrace());
        }
        return apiResponse(RESP_CODE, response);
    }
}
