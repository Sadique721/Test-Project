package com.savbill.radius.controller;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.services.ResellerService;
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
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class ReselerControllerTest {
    @InjectMocks
    ResellerController resellerController;
    @Mock
    ResellerService resellerService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Test
    public void findAllAcctCdrsTest(){
        AcctCdr acctCdr=getacctCdr();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(2);
        List<AcctCdr> acctCdrList=new ArrayList<>();
        acctCdrList.add(acctCdr);
        Page<AcctCdr>page=new PageImpl<>(acctCdrList);
        Map<String, Object> response = new HashMap<>();
        response.put("acctCdr",page);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(resellerService.findAllAcctCdr(1,paginationDTO,1L)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=resellerController.findAllAcctCdrs(paginationDTO,1,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findAcctCdrByUserNameTest(){
        AcctCdr acctCdr=getacctCdr();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(2);
        List<AcctCdr> acctCdrList=new ArrayList<>();
        acctCdrList.add(acctCdr);
        Page<AcctCdr>page=new PageImpl<>(acctCdrList);
        Map<String, Object> response = new HashMap<>();
        response.put("acctCdr",page);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(resellerService.findAcctCrdByUserName("ADPT","11ww12",1,paginationDTO,1L)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=resellerController.findAcctCdrByUserName(paginationDTO,"ADPT","11ww12",1,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    @Test
    public void deleteAcctCdrTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "AcctCdr has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=resellerController.deleteAcctCdr(1L,1,1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
@Test
public void getCdrDetailTest() {
    AcctCdr acctCdr = getacctCdr();
    Map<String, Object> response = new HashMap<>();
    response.put("cdrDetail",acctCdr);
    ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
    Mockito.when(resellerService.findAcctCdrById(1L,1,1L)).thenReturn(acctCdr);
    Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
    ResponseEntity<Map<String, Object>> output = resellerController.getCdrDetail(1L,1,1L,httpServletRequest);
    assertNotNull(output);
    assertEquals(output.getStatusCode().value(), 200);
}

    @Test
    public void  getAllTest() {
        LiveUser liveUser=getliveUser();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(2);
        List<LiveUser>liveUsers=new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> page=new PageImpl<>(liveUsers);
        Map<String, Object> response = new HashMap<>();
        response.put("liveUser",page);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(resellerService.getAll(1,paginationDTO,1L)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = resellerController.getAll(paginationDTO,1,httpServletRequest,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }

    @Test
    public void  deleteTest() {
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "liveuser has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
         Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = resellerController.delete(1L,1,httpServletRequest,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void findByUserNameTest() {
        LiveUser liveUser=getliveUser();
        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setPage(1);
        paginationDTO.setSize(2);
        List<LiveUser>liveUsers=new ArrayList<>();
        liveUsers.add(liveUser);
        Page<LiveUser> page=new PageImpl<>(liveUsers);
        Map<String, Object> response = new HashMap<>();
        response.put("liveUser",page);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(resellerService.findByUserName("ADPT","11ww11",1,paginationDTO,1L)).thenReturn(page);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = resellerController.findByUserName(paginationDTO,"ADPT","11ww11",1,httpServletRequest,1L);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }
    @Test
    public void getLiveUserDetailTest() {
        LiveUser liveUser = getliveUser();
        Map<String, Object> response = new HashMap<>();
        response.put("liveUserDetail", liveUser);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(resellerService.findLiveUserById(1L, 1, 1L)).thenReturn(liveUser);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = resellerController.getLiveUserDetail(1L, 1, httpServletRequest, 1L);
        assertNotNull(output);

    }




    AcctCdr getacctCdr(){
        AcctCdr acctCdr=new AcctCdr();
        acctCdr.setCdrId(1L);
        acctCdr.setUserName("ADPT");
        acctCdr.setUserPassword("111");
        acctCdr.setAcctInputPackets("RDRD");
        acctCdr.setFramedIpAddress("11ww11");
        return acctCdr;
    }

    LiveUser getliveUser(){
        LiveUser liveUser=new LiveUser();
        liveUser.setUserName("ADPT");
        liveUser.setUserPassword("1212");
        liveUser.setAcctInputPackets("RDRD");
        liveUser.setCallbackId("1212");
        liveUser.setLocationId(1L);
        return liveUser;

    }

}
