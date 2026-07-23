package com.savbill.radius.controller;

import com.savbill.radius.entity.CustomerQosPolicyMapping;
import com.savbill.radius.services.CustomerQosPolicyMappingService;
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
public class CustomerQosPolicyMappingControllerTest {
    @InjectMocks
    CustomerQosPolicyMappingController customerQosPolicyMappingController;
    @Mock
    CustomerQosPolicyMappingService customerQosPolicyMappingService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllQosPolicyMappingsTest(){
        CustomerQosPolicyMapping qosPolicyMapping=getqosPolicyMapping();
        List<CustomerQosPolicyMapping> customerQosPolicyMappings=new ArrayList<>();
        customerQosPolicyMappings.add(qosPolicyMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("customerQosMappingList",customerQosPolicyMappings);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerQosPolicyMappingService.findAllQosPolicyMappings(1)).thenReturn(customerQosPolicyMappings);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.findAllQosPolicyMappings(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findQosPolicyMappingByIdTest(){
        CustomerQosPolicyMapping qosPolicyMapping=getqosPolicyMapping();
        Map<String, Object> response = new HashMap<>();
        response.put("customerQosMapping",qosPolicyMapping);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerQosPolicyMappingService.findQosPolicyMappingById(1L,1)).thenReturn(qosPolicyMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.findQosPolicyMappingById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findQosPolicyMappingByCustIdTest(){
        CustomerQosPolicyMapping qosPolicyMapping=getqosPolicyMapping();
        List<CustomerQosPolicyMapping> customerQosPolicyMappings=new ArrayList<>();
        customerQosPolicyMappings.add(qosPolicyMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("customerQosMappingList",customerQosPolicyMappings);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerQosPolicyMappingService.findQosPolicyMappingByCustId(1L,1)).thenReturn(customerQosPolicyMappings);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.findQosPolicyMappingByCustId(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void addNewQosPolicyMappingTest(){
        CustomerQosPolicyMapping qosPolicyMapping=getqosPolicyMapping();
        Map<String, Object> response = new HashMap<>();
        response.put("customerQosMapping",qosPolicyMapping);
        response.put(RadiusConstants.MESSAGE, "Qos Policy Mapping has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerQosPolicyMappingService.addQosPolicyMapping(qosPolicyMapping,1)).thenReturn(qosPolicyMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.addNewQosPolicyMapping(qosPolicyMapping,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateCustomerReplyTest(){
        CustomerQosPolicyMapping qosPolicyMapping=getqosPolicyMapping();
        Map<String, Object> response = new HashMap<>();
        response.put("customerQosMapping",qosPolicyMapping);
        response.put(RadiusConstants.MESSAGE, "Qos Policy Mapping has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerQosPolicyMappingService.updateQosPolicyMapping(qosPolicyMapping,1)).thenReturn(qosPolicyMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.updateCustomerReply(qosPolicyMapping,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void deleteQosPolicyMappingTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Qos Policy Mapping has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerQosPolicyMappingController.deleteQosPolicyMapping(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    CustomerQosPolicyMapping getqosPolicyMapping(){
        CustomerQosPolicyMapping qosPolicyMapping=new CustomerQosPolicyMapping();
        qosPolicyMapping.setQosPolicyMappingId(1L);
        qosPolicyMapping.setQosTo(1L);
        qosPolicyMapping.setMvnoId(1);
        qosPolicyMapping.setQosTo(1L);
        qosPolicyMapping.setQosFrom(1L);
        qosPolicyMapping.setDownloadQos(1L);
        qosPolicyMapping.setCustId(1L);
        qosPolicyMapping.setUploadQos(1L);
        return qosPolicyMapping;
    }


}
