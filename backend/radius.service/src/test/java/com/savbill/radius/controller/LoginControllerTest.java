package com.savbill.radius.controller;

import com.savbill.radius.helper.LoginDto;
import com.savbill.radius.services.LoginService;
import com.savbill.radius.utils.RadiusConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class LoginControllerTest {
    @InjectMocks
    LoginController loginController;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    LoginService loginService;
    @Mock
    HttpServletRequest httpServletRequest;
@Test
public void loginTest(){
    String token="LOGIN SUCCESSFULLY";
    LoginDto loginDto=new LoginDto();
    loginDto.setPassword("admin");
    loginDto.setUserName("admin");
    Map<String, Object> response = new HashMap<>();
    response.put(RadiusConstants.MESSAGE, "Login Successfully.");
    response.put("token", token);
    response.put("userName", loginDto.getUserName());
     ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
    Mockito.when(loginService.login(loginDto)).thenReturn(token);
    Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
    ResponseEntity<Map<String, Object>>output=loginController.login(loginDto,httpServletRequest);
    assertNotNull(output);
    assertEquals(output.getStatusCode().value(),200);
}



}
