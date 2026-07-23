package com.savbill.radius.controller;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AuthResponse;
import com.savbill.radius.helper.RequestDto;
import com.savbill.radius.services.AuthResponseService;
import com.savbill.radius.utils.RadiusConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class AuthResponceControllerTest {
    @InjectMocks
    AuthResponseController authResponseController;
    @Mock
    AuthResponseService authResponseService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllAuthResponsesTest(){
        AuthResponse authResponse=getauthResponse();
        List<AuthResponse>authResponsesList=new ArrayList<>();
        authResponsesList.add(authResponse);
        Page<AuthResponse> authResponsePage=new PageImpl<>(authResponsesList);
        PaginationDTO paginationDTO=getpaginationDTO();
        RequestDto requestDto = getrequestDTO();
        Map<String, Object> response = new HashMap<>();
        response.put("authResponse",authResponsePage);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(authResponseService.findAllAuthResponse(1,paginationDTO,requestDto,httpServletRequest)).thenReturn((authResponsePage));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = authResponseController.findAllAuthResponses(paginationDTO,1 , requestDto,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findAuthResponseByUserNameTest(){
        AuthResponse authResponse=getauthResponse();
        List<AuthResponse>authResponsesList=new ArrayList<>();
        authResponsesList.add(authResponse);
        Page<AuthResponse> authResponsePage=new PageImpl<>(authResponsesList);
        PaginationDTO paginationDTO=getpaginationDTO();
        Map<String, Object> response = new HashMap<>();
        response.put("authResponse",authResponsePage);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(authResponseService.findAuthResponseByUserName(paginationDTO,"Responce",1)).thenReturn((authResponsePage));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = authResponseController.findAuthResponseByUserName(paginationDTO,"Responce",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteAuthResponseTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "AUth Response has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = authResponseController.deleteAuthResponse(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    AuthResponse getauthResponse(){
        AuthResponse authResponse=new AuthResponse();
        authResponse.setMvnoId(1);
        authResponse.setAuthresId(1L);
        authResponse.setClientGroup("Active");
        authResponse.setUserName("Responce");
        authResponse.setClientIp("1212NN");
        authResponse.setEventTime(new Timestamp(1));
        authResponse.setReplyMessage("Successfully");
        authResponse.setLastModifiedOn(new Timestamp(4));
        authResponse.setPacketType("Meadium");
        return authResponse;
    }

    PaginationDTO getpaginationDTO(){
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setSize(1);
        paginationDTO.setPage(1);
        paginationDTO.setFromDate("2018/8/2");
        paginationDTO.setToDate("2020/8/2");
        return paginationDTO;
    }
    RequestDto getrequestDTO(){
        RequestDto requestDto = new RequestDto();
        requestDto.setUsername("test");
        requestDto.setFromDate("2018/8/2");
        requestDto.setToDate("2020/8/2");
        return requestDto;
    }


}
