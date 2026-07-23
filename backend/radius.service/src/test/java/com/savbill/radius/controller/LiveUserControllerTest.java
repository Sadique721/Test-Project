package com.savbill.radius.controller;

import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.services.LiveUserService;
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
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class LiveUserControllerTest{
    @InjectMocks
    LiveUserController liveUserController;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    LiveUserService liveUserService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Test
    public void getAllTest() {
        LiveUser liveUser = new LiveUser();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(1);
        List<LiveUser> liveUsers = new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> liveUserPage = new PageImpl<>(liveUsers);
        Map<String, Object> response = new HashMap<>();
        response.put("liveUser",liveUserPage);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(liveUserService.getAll(1,paginationDTO,httpServletRequest)).thenReturn(liveUserPage);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = liveUserController.getAll(paginationDTO,1,httpServletRequest);
        //assertNotNull(output);
        //assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteTest(){
        LiveUser liveUser = new LiveUser();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(1);
        List<LiveUser> liveUsers = new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> liveUserPage = new PageImpl<>(liveUsers);
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "liveuser has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = liveUserController.delete(1L,1,httpServletRequest);
        //assertNotNull(output);
        //assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findByUserNameTest(){
        LiveUser liveUser = new LiveUser();
        liveUser.setUserName("ADPT");
        liveUser.setUserPassword("1212");
        liveUser.setCallbackId("1212");
        liveUser.setFramedIpAddress("1212");
        LiveUserSearchDTO paginationDTO=new LiveUserSearchDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(1);
        paginationDTO.setUserName("ADPT");
        paginationDTO.setFramedIpAddress("1212");
        List<LiveUser> liveUsers = new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> liveUserPage = new PageImpl<>(liveUsers);
        Map<String, Object> response = new HashMap<>();
         response.put("liveUser", liveUserPage);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(liveUserService.findLiveUsersUsingFilter(paginationDTO, 1)).thenReturn(liveUserPage);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = liveUserController.findByUserName(paginationDTO,1,httpServletRequest);
        //assertNotNull(output);
        //assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void  getLiveUserDetailTest() {
        LiveUser liveUser = new LiveUser();
        liveUser.setUserName("ADPT");
        liveUser.setUserPassword("1212");
        liveUser.setCallbackId("1212");
        liveUser.setFramedIpAddress("1212");
        Map<String, Object> response = new HashMap<>();
        response.put("liveUserDetail", liveUser);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(liveUserService.findLiveUserById(1L, 1)).thenReturn(liveUser);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = liveUserController.getLiveUserDetail(1L, 1, httpServletRequest);
        //assertNotNull(output);
        //assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    @Ignore
    public void exportExcelTest(){
        LiveUserSearchDTO paginationDTO=null;
       /* paginationDTO.setToDate("2019-02-17");
        paginationDTO.setFromDate("2018-02-17");
        paginationDTO.setPage(1);
        paginationDTO.setSize(1);*/
        LiveUser liveUser=new LiveUser();
        liveUser.setUserName("ADPT");
        liveUser.setUserPassword("1212");
        liveUser.setCallbackId("1212");
        liveUser.setFramedIpAddress("1212");
        liveUser.setMvnoId(1);
        List<LiveUser> liveUsers = new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> liveUserPage = new PageImpl<>(liveUsers);
        httpServletResponse.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        int responseCode = RadiusConstants.SUCCESS;
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=LiveUsers" + currentDateTime + ".xlsx";
        httpServletResponse.setHeader(headerKey, headerValue);
        Map<String, Object> response = new HashMap<>();
        responseCode=RadiusConstants.NULL_VALUE;
        response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(liveUserService.findLiveUsersUsingFilter(paginationDTO, 1)).thenReturn(liveUserPage);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);

        //ResponseEntity<Map<String, Object>> output=liveUserController.exportExcel("ADPT","1212",httpServletResponse,1,httpServletRequest);
        //assertNotNull(output);
        //assertEquals(output.getStatusCode().value(), 200);
        }



}
