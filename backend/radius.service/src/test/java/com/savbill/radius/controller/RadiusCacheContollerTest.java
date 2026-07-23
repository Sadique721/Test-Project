package com.savbill.radius.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.savbill.radius.services.impl.RadiusCacheServiceImpl;
import com.netflix.appinfo.InstanceInfo;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringRunner.class)
@Ignore
public class RadiusCacheContollerTest {
    @InjectMocks
    RadiusCacheController radiusCacheController;
    @Mock
    RadiusCacheServiceImpl radiusCacheService;
     @Mock
    APIResponseController apiResponseController;
     @Mock
    HttpServletRequest httpServletRequest;
        @Test
        public void refreshRadiusCacheTest(){
            Map<String, Object> response = new HashMap<>();
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=radiusCacheController.refreshRadiusCache(httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
    @Test
    public void getRadiusInstanceTest(){
        List<InstanceInfo> instances = new ArrayList<InstanceInfo>();
        Map<String, Object> response = new HashMap<>();
        response.put("instances",instances);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(radiusCacheService.getRadiusInstances()).thenReturn(instances);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=radiusCacheController.getRadiusInstance(httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
}


