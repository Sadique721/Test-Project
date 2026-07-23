package com.savbill.radius.controller;

import com.savbill.radius.entity.ProxyServer;
import com.savbill.radius.helper.ProxyServerDto;
import com.savbill.radius.services.ProxyServerService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class ProxyServerControllerTest {
      @InjectMocks
    ProxyServerController proxyServerController;
      @Mock
    ProxyServerService proxyServerService;
      @Mock
      APIResponseController apiResponseController;
      @Mock
      HttpServletRequest httpServletRequest;

    @Test
    public void getAllTest(){
    ProxyServer proxyServer=getproxyServer();
        List<ProxyServer> proxyServerList=new ArrayList<>();
         proxyServerList.add(proxyServer);
        Map<String, Object> response = new HashMap<>();
        response.put("proxyServerList",proxyServerList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(proxyServerService.getAll(1)).thenReturn(proxyServerList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=proxyServerController.getAll(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void addTest(){
        ProxyServer proxyServer=getproxyServer();
        ProxyServerDto proxyServerDto=new ProxyServerDto();
        proxyServerDto.setAcctport("ADPT");
        proxyServerDto.setIp("33LL33");
        proxyServerDto.setStatus("Active");
         Map<String, Object> response = new HashMap<>();
        response.put("proxyServer",proxyServer);
        response.put(RadiusConstants.MESSAGE, "Proxy server has been added successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(proxyServerService.save(proxyServerDto,1)).thenReturn(proxyServer);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=proxyServerController.add(proxyServerDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    @Test
    public void updateTest(){
        ProxyServer proxyServer=getproxyServer();
        ProxyServerDto proxyServerDto=new ProxyServerDto();
        proxyServerDto.setAcctport("ADPT");
        proxyServerDto.setIp("33LL33");
        proxyServerDto.setStatus("Active");
        Map<String, Object> response = new HashMap<>();
        response.put("proxyServer",proxyServer);
        response.put(RadiusConstants.MESSAGE, "proxy server has been updated successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(proxyServerService.update(1L,proxyServerDto,1,httpServletRequest)).thenReturn(proxyServer);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=proxyServerController.update(1L,proxyServerDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
        @Test
        public void deleteTest(){
            Map<String, Object> response = new HashMap<>();
            response.put(RadiusConstants.MESSAGE, "proxy server has been deleted successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=proxyServerController.delete(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void getTest(){
            ProxyServer proxyServer=getproxyServer();
            Map<String, Object> response = new HashMap<>();
            response.put("proxyServer",proxyServer);
             ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(proxyServerService.getById(1L,1)).thenReturn(proxyServer);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=proxyServerController.get(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }

        @Test
        public void getByNameTest(){
            ProxyServer proxyServer=getproxyServer();
            List<ProxyServer> proxyServerList=new ArrayList<>();
            proxyServerList.add(proxyServer);
            Map<String, Object> response = new HashMap<>();
            response.put("proxyServerList",proxyServerList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(proxyServerService.getByName("RDRD",1)).thenReturn(proxyServerList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=proxyServerController.getByName("RDRD",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }

    @Test
    public void updateStatus(){
        ProxyServer proxyServer=getproxyServer();
        List<ProxyServer> proxyServerList=new ArrayList<>();
        proxyServerList.add(proxyServer);
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "proxy server status has been updated successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=proxyServerController.updateStatus(1L,"Active",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }





    ProxyServer getproxyServer(){
        ProxyServer proxyServer=new ProxyServer();
        proxyServer.setAcctport("ADPT");
        proxyServer.setAuthport("1212");
        proxyServer.setIp("1111");
        proxyServer.setId(1L);
        proxyServer.setName("RDRD");
        return  proxyServer;

    }

}
