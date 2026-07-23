package com.savbill.radius.controller;

import com.savbill.radius.entity.CustomerReply;
import com.savbill.radius.services.CustomerReplyService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class CustomerReplyControllerTest {
    @InjectMocks
    CustomerReplyController customerReplyController;
    @Mock
    CustomerReplyService customerReplyService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllCustomerRepliesTest(){
        CustomerReply customerReply=getcustomerReply();
        List<CustomerReply> customerReplyList=new ArrayList<>();
        customerReplyList.add(customerReply);
        Map<String, Object> response = new HashMap<>();
        response.put("customerReplyList",customerReplyList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerReplyService.findAllCustomerReply(1)).thenReturn(customerReplyList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.findAllCustomerReplies(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findCustomerReplyByIdTest(){
        CustomerReply customerReply=getcustomerReply();
        Map<String, Object> response = new HashMap<>();
        response.put("customerReply",customerReply);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerReplyService.findCustomerReplyById(1L,1)).thenReturn(customerReply);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.findCustomerReplyById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findCustomerReplyByCustId(){
        CustomerReply customerReply=getcustomerReply();
        List<CustomerReply> customerReplyList=new ArrayList<>();
        customerReplyList.add(customerReply);
        Map<String, Object> response = new HashMap<>();
        response.put("customerReplyList",customerReplyList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerReplyService.findCustomerReplyByCustomerId(1L,1)).thenReturn(customerReplyList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.findCustomerReplyByCustId(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void addNewCustomerReply(){
        CustomerReply customerReply=getcustomerReply();
        Map<String, Object> response = new HashMap<>();
        response.put("customerReply",customerReply);
        response.put(RadiusConstants.MESSAGE, "Customer reply has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerReplyService.addCustomerReply(customerReply,1)).thenReturn(customerReply);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.addNewCustomerReply(customerReply,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void updateCustomerReply(){
        CustomerReply customerReply=getcustomerReply();
        Map<String, Object> response = new HashMap<>();
        response.put("customerReply",customerReply);
        response.put(RadiusConstants.MESSAGE, "Customer reply has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(customerReplyService.updateCustomerReply(customerReply,1)).thenReturn(customerReply);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.updateCustomerReply(customerReply,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteCustomerReplyTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Customer reply has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output =customerReplyController.deleteCustomerReply(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

        CustomerReply getcustomerReply(){
            CustomerReply customerReply=new CustomerReply();
            customerReply.setCustomerId(1L);
            customerReply.setMvnoId(1);
            customerReply.setAttribute("Active");
            customerReply.setAttributeId(1L);
            customerReply.setAttributeValue("Active");
            customerReply.setCreatedOn(new Timestamp(1));
            customerReply.setLastModifiedOn(new Timestamp(1L));
            return customerReply;

        }

}
