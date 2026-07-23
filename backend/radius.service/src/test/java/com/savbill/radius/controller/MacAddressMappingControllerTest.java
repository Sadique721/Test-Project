package com.savbill.radius.controller;

import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.MacAddressMappingDto;
import com.savbill.radius.services.MacAddressMappingService;
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
public class MacAddressMappingControllerTest {
    @InjectMocks
    MacAddressMappingController macAddressMappingController;
    @Mock
    MacAddressMappingService macAddressMappingService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllMacAddressMappingsTest(){
        MacAddressMapping macAddressMapping= new MacAddressMapping();
        macAddressMapping.setMacAddress("NN77VV88");
        macAddressMapping.setMacAddressId(121212L);
        macAddressMapping.setCustomerId(121L);
        List<MacAddressMapping> macAddressMappingList=new ArrayList<>();
        macAddressMappingList.add(macAddressMapping);
        Map<String,Object> response = new HashMap<>();
        response.put("macAddressMappingList",macAddressMappingList);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(macAddressMappingService.findAllMacAddressMapping()).thenReturn(macAddressMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=macAddressMappingController.findAllMacAddressMappings(httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findMacAddressMappingByCustomerIdTest(){
        MacAddressMapping macAddressMapping= new MacAddressMapping();
        macAddressMapping.setMacAddress("NN77VV88");
        macAddressMapping.setMacAddressId(121212L);
        macAddressMapping.setCustomerId(121L);
        List<MacAddressMapping> macAddressMappingList=new ArrayList<>();
        macAddressMappingList.add(macAddressMapping);
        Map<String,Object> response = new HashMap<>();
        response.put("macAddressMappingList",macAddressMappingList);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(macAddressMappingService.findMacAddressMappingByCustomerId(121L)).thenReturn(macAddressMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=macAddressMappingController.findMacAddressMappingByCustomerId(121L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void saveMacAddressMappingTest(){
        MacAddressMapping macAddressMapping= new MacAddressMapping();
        macAddressMapping.setMacAddress("NN77VV88");
        macAddressMapping.setMacAddressId(121212L);
        macAddressMapping.setCustomerId(121L);
        MacAddressMappingDto macAddressMappingDto=new MacAddressMappingDto();
        macAddressMappingDto.setMacAddress("VDC123");
        macAddressMappingDto.setCustomerId(111L);
         Map<String,Object> response = new HashMap<>();
        response.put("macAddressMappingList", macAddressMapping);
        response.put(RadiusConstants.MESSAGE, "Mac Address has been created successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(macAddressMappingService.saveMacAddressMapping(macAddressMappingDto)).thenReturn(macAddressMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=macAddressMappingController.saveMacAddressMapping(macAddressMappingDto,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateMacAddressMappingTest(){
        MacAddressMapping macAddressMapping= new MacAddressMapping();
        macAddressMapping.setMacAddress("NN77VV88");
        macAddressMapping.setMacAddressId(121212L);
        macAddressMapping.setCustomerId(121L);
        List<MacAddressMapping>macAddressMappingList=new ArrayList<>();
        macAddressMappingList.add(macAddressMapping);
        MacAddressMappingDto macAddressMappingDto=new MacAddressMappingDto();
        macAddressMappingDto.setMacAddress("VDC123");
        macAddressMappingDto.setCustomerId(111L);
        Map<String,Object> response = new HashMap<>();
        response.put("macAddressMappingList", macAddressMappingList);
        response.put(RadiusConstants.MESSAGE, "Mac Address has been updated successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(macAddressMappingService.updateMacAddressMapping(macAddressMappingList)).thenReturn(macAddressMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=macAddressMappingController.updateMacAddressMapping(macAddressMappingList,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteMacAddressMappingTest(){
        Map<String,Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Mac Address has been deleted successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=macAddressMappingController.deleteMacAddressMapping(1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }


}
