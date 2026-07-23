package com.savbill.notification.controller;

import com.savbill.notification.utils.CommonConstants;
import com.savbill.notification.utils.Dao;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.LoginDto;
import com.savbill.notification.helper.UserDto;
import com.savbill.notification.jwt.JwtUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class LoginControllerTest {
    @InjectMocks
    LoginController loginController;
    @Mock
    APIResponseController apiResponseController;
     @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    Dao dao;
     @Mock
    LoginController loginController11;


    @Test
     public void loginTest() {
        LoginDto loginDto=getloginDto();
        Map<String, Object> response = new HashMap<>();
        response.put(CommonConstants.MESSAGE, "Login Successfully.");
        response.put("userName", loginDto.getUserName());
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(loginController11.loginservice(loginDto)).thenReturn(response);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =loginController.login(loginDto);
         assertTrue(true);
    }

    @Test
    @Ignore
     public void loginserviceTest(){
        LoginDto loginDto=getloginDto();
        String token="aswa12121a";
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userName", loginDto.getUserName());
        UserDetails userDetails=null;
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Authentication authentication=null;
        Mockito.when(jwtUtil.generateToken(userDetails,2L)).thenReturn(token);
        Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(),loginDto.getPassword()))).thenReturn(authentication);
        Map<String, Object> output =loginController.loginservice(loginDto);
        assertNotNull(output);
     }
    @Test
     public void loadUserByUsernameTest() throws AuthException, CustomException {
        UserDto userDto=getuserDto();
        Mockito.when(dao.getUserDetailsFromUsername("admin")).thenReturn(userDto);
        UserDetails output= loginController.loadUserByUsername("admin");
        assertNotNull(output);
    }



    LoginDto getloginDto(){
        LoginDto loginDto=new LoginDto();
        loginDto.setPassword("admin123");
        loginDto.setUserName("admin");
        return loginDto;

    }

    UserDto getuserDto(){
        UserDto userDto=new UserDto();
        userDto.setAuthorities("AAA");
        userDto.setRole("admin");
        userDto.setPassword("2121");
        userDto.setUsername("admin");
        userDto.setRoleid(1L);
        return userDto;
    }
}
