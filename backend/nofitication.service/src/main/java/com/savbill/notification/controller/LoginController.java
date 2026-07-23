package com.savbill.notification.controller;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.savbill.notification.entity.Staff;
import com.savbill.notification.helper.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.notification.helper.LoginDto;
import com.savbill.notification.jwt.JwtUtil;
import com.savbill.notification.repository.StaffRepository;
import com.savbill.notification.utils.CommonConstants;
import com.savbill.notification.utils.Dao;
import com.savbill.notification.utils.ValidateCrudTransactionData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Login", description = "REST APIs related to login!!!!", tags = "Login")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class LoginController {
    @Autowired
    APIResponseController apiResponseController;


    @Autowired
    StaffRepository staffRepository;

    @Autowired
    Dao dao;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    public static final String BAD_CREDENTIALS = "Bad Credentials";

    //final Logger log = Logger.getLogger(EventController.class);


    @ApiOperation(value = "Validate user and generate token")
    @PostMapping("/staff/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginData) {
        Map<String, Object> response = new HashMap<>();
        try {
            response = loginservice(loginData);
            response.put(CommonConstants.MESSAGE, "Login Successfully.");
            response.put("userName", loginData.getUserName());
            log.info("Login Successful, Attempted by Username : "+loginData.getUserName());
            return apiResponseController.apiResponse(CommonConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.info("Login failed, Attempted by Username : "+loginData.getUserName()+ " ERROR : "+e.getMessage());
            log.error("TRACE ERROR : "+ CommonConstants.GetError(e));
            return apiResponseController.apiResponse(CommonConstants.FAIL, response);
        }
    }

    public UserDetails loadUserByUsername(String userName) {

        UserDetails userDetails = null;

        UserDto userDto = dao.getUserDetailsFromUsername(userName); /** we get username,password,roleid From this **/
        if (userDto != null) {
            String role = dao.getRoleFromRoleId(userDto.getRoleid());
            if (role != null) {
                userDto.setRole(role);
                userDto.setAuthorities(role);
            }

            userDetails = new org.springframework.security.core.userdetails.User(userDto.getUsername(), userDto.getPassword(), userDto.getAuthorities());
        }
        return userDetails;
    }

    public Map<String, Object> loginservice(LoginDto loginData) {
        try {
            Map<String, Object> response = new HashMap<>();
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginData.getUserName())) {
                throw new IllegalArgumentException(CommonConstants.BASIC_STRING_MSG + "User name is mandatory.Please enter valid user name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(loginData.getPassword())) {
                throw new IllegalArgumentException(CommonConstants.BASIC_STRING_MSG + "Password is mandatory.Please enter valid password.");
            } else {
                this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getUserName(), loginData.getPassword()));
                UserDetails userDetails = loadUserByUsername(loginData.getUserName());

                Optional<Staff> staff=staffRepository.findByUserName(loginData.getUserName());
                String token = this.jwtUtil.generateToken(userDetails,staff.get().getMvnoId());

                response.put("token", token);
                response.put("userName", loginData.getUserName());


            }
            return response;
        } catch (UsernameNotFoundException e) {

            // sendLoginFailureMessage(loginData.getUserName(), loginData.getPassword());
            throw new RuntimeException(BAD_CREDENTIALS);


        } catch (Throwable e) {StringWriter stringWriter= new StringWriter();

            // sendLoginFailureMessage(loginData.getUserName(), loginData.getPassword());
            throw new RuntimeException(BAD_CREDENTIALS);
        }
    }


}
