package com.savbill.radius.controller;
import com.savbill.radius.entity.ConcurrentPolicy;
import com.savbill.radius.helper.ConcurrentPolicyDto;
import com.savbill.radius.services.ConcurrentPolicyService;
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
public class ConcurrentPolicyControllerTest {
    @InjectMocks
    ConcurrentPolicyController concurrentPolicyController;
    @Mock
    ConcurrentPolicyService concurrentPolicyService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findAllTest(){
        ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
        List<ConcurrentPolicy>concurrentPolicyList=new ArrayList<>();
        concurrentPolicyList.add(concurrentPolicy);
        Map<String, Object> response = new HashMap<>();
        response.put("concurrentPolicyList",concurrentPolicyList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.findAll(1)).thenReturn(concurrentPolicyList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.findAll(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findByIdTest(){

        ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
        Map<String, Object> response = new HashMap<>();
        response.put("concurrentPolicy",concurrentPolicy);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.findById(1L,1)).thenReturn(concurrentPolicy);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.findById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
        @Test
        public void findByNameTest(){
            ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
            List<ConcurrentPolicy>concurrentPolicyList=new ArrayList<>();
            concurrentPolicyList.add(concurrentPolicy);
            Map<String, Object> response = new HashMap<>();
            response.put("concurrentPolicyList",concurrentPolicyList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(concurrentPolicyService.searchByPolicyName("Active",1)).thenReturn(concurrentPolicyList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=concurrentPolicyController.findByName("Active",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
    @Test
    public void addDeviceTest(){
        ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
        ConcurrentPolicyDto concurrentPolicyDto=getconcurrentPolicyDto();
        Map<String, Object> response = new HashMap<>();
        response.put("concurrentPolicy",concurrentPolicy);
        response.put("message","Concurrent policy has been added successfully");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.add(concurrentPolicyDto,1)).thenReturn(concurrentPolicy);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.addDevice(concurrentPolicyDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateTest(){
        ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
        ConcurrentPolicyDto concurrentPolicyDto=getconcurrentPolicyDto();
        Map<String, Object> response = new HashMap<>();
        response.put("concurrentPolicy",concurrentPolicy);
        response.put("message","Concurrent Policy has been updated successfully");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.update(concurrentPolicyDto,1)).thenReturn(concurrentPolicy);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.updateCustomer(concurrentPolicyDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }

    @Test
    public void deleteTest(){
        Map<String, Object> response = new HashMap<>();
        response.put("message","Policy has been deleted successfully");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.deleteCustomer(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void changeDeviceStatus(){
         String message="Active";
        Map<String, Object> response = new HashMap<>();
        response.put("message",message);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.changePolicyStatus(1L,"Active",1)).thenReturn(message);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.changeDeviceStatus(1L,"Active",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void getConcurrentPolicies(){
        ConcurrentPolicy concurrentPolicy=getconcurrentPolicy();
        List<ConcurrentPolicy> concurrentPolicyList=new ArrayList<>();
        concurrentPolicyList.add(concurrentPolicy);
        Map<String, Object> response = new HashMap<>();
        response.put("policyList",concurrentPolicyList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(concurrentPolicyService.getConcurrentPolicies(1)).thenReturn(concurrentPolicyList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=concurrentPolicyController.getConcurrentPolicies(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

        ConcurrentPolicy getconcurrentPolicy(){
        ConcurrentPolicy concurrentPolicy=new ConcurrentPolicy();
        concurrentPolicy.setConcurrentPolicyId(1L);
        concurrentPolicy.setNoOfConcurrentConnections(2L);
        concurrentPolicy.setName("Active");
        concurrentPolicy.setStatus("Active");
        concurrentPolicy.setMvnoId(1);
        return concurrentPolicy;
        }

        ConcurrentPolicyDto getconcurrentPolicyDto(){
        ConcurrentPolicyDto concurrentPolicyDto=new ConcurrentPolicyDto();
        concurrentPolicyDto.setNoOfConcurrentConnections(2L);
        concurrentPolicyDto.setName("Active");
        concurrentPolicyDto.setStatus("Active");
        return concurrentPolicyDto;
        }



}
