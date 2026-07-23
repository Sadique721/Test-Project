package com.savbill.radius.controller;

import com.savbill.radius.entity.Customer;
import com.savbill.radius.entity.UpdateCustomerDto;
import com.savbill.radius.helper.CustomerDto;
import com.savbill.radius.helper.CustomerPasswordDto;
import com.savbill.radius.helper.LoginDto;
import com.savbill.radius.services.CustomerService;
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
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class CustromerControllerTest {
        @InjectMocks
        CustomerController customerController;
        @Mock
        CustomerService customerService;
        @Mock
        APIResponseController apiResponseController;
        @Mock
        HttpServletRequest httpServletRequest;

//        @Test
//        @Ignore
//        public void findAllCustomersTest(){
//            Customer customer=getcustomer();
//            List<Customer> customerList=new ArrayList<>();
//            customerList.add(customer);
//            Map<String, Object> response = new HashMap<>();
//            response.put("customerList",customerList);
//            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
//            Mockito.when(customerService.findAllCustomer(1)).thenReturn(customerList);
//            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
//            ResponseEntity<Map<String, Object>> output=customerController.findAllCustomers(1,httpServletRequest);
//            assertNotNull(output);
//            assertEquals(output.getStatusCode().value(),200);
//        }
        @Test
        @Ignore
        public void findCustomerByIdTest(){
            Customer customer=getcustomer();
            Map<String, Object> response = new HashMap<>();
            response.put("customer",customer);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(customerService.findCustomerById(1L,1)).thenReturn(customer);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=customerController.findCustomerById(1,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);


        }
//        @Test
//        @Ignore
//        public void findCustomerByNameTest(){
//            Customer customer=getcustomer();
//            List<Customer>customerList=new ArrayList<>();
//            customerList.add(customer);
//            Map<String, Object> response = new HashMap<>();
//            response.put("customerList",customerList);
//            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
//            Mockito.when(customerService.searchCustomerByName("ADPT",1)).thenReturn(customerList);
//            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
//            ResponseEntity<Map<String, Object>> output=customerController.findCustomerByName(,"ADPT",1,httpServletRequest);
//            assertNotNull(output);
//            assertEquals(output.getStatusCode().value(),200);
//        }
        @Test
        @Ignore
        public void addNewCustomerTest(){
            Customer customer=getcustomer();
            CustomerDto customerDto=getcustomerDto();
            Map<String, Object> response = new HashMap<>();
            response.put("customer",customer);
            response.put(RadiusConstants.MESSAGE, "Customer has been added successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(customerService.addCustomer(customerDto,1)).thenReturn( customer);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=customerController.addNewCustomer(customerDto,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
            @Test
            @Ignore
           public  void updateCustomerTest(){
                Customer customer=getcustomer();
                UpdateCustomerDto customerDto=getupdateCustomerDto();
                Map<String, Object> response = new HashMap<>();
                response.put("customer",customer);
                response.put(RadiusConstants.MESSAGE, "Customer has been updated successfully.");
                ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
                Mockito.when(customerService.updateCustomer(customerDto,1)).thenReturn( customer);
                Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
                ResponseEntity<Map<String, Object>> output=customerController.updateCustomer(customerDto,1,httpServletRequest);
                assertNotNull(output);
                assertEquals(output.getStatusCode().value(),200);
           }
           @Test
           @Ignore
        public void deleteCustomerTest(){
               Map<String, Object> response = new HashMap<>();
               response.put(RadiusConstants.MESSAGE, "Customer has been deleted successfully.");
               ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
               Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
               ResponseEntity<Map<String, Object>> output=customerController.deleteCustomer("ADPT",1,httpServletRequest);
               assertNotNull(output);
               assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        @Ignore
        public void updateCustomerStatusTest(){
             String msg="Active";
            Map<String, Object> response = new HashMap<>();
            response.put("message",msg);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(customerService.updateCustomerStatus("ADPT","Active",1)).thenReturn(msg);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=customerController.updateCustomerStatus("ADPT","Active",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);


        }
        @Test
        @Ignore
        public void rechargeQuotaTest(){
            Customer customer=getcustomer();
            Map<String, Object> response = new HashMap<>();
            response.put(RadiusConstants.MESSAGE, "Customer Quota Recharge has been updated successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=customerController.crossRechargeQuota(1L,false,1L,customer,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        @Ignore
        public void changePasswordTest(){
            CustomerPasswordDto customerPasswordDto=new CustomerPasswordDto();
            customerPasswordDto.setNewPassword("ND12");
            customerPasswordDto.setNewPassword("ND12");
            customerPasswordDto.setMvnoId(1);
            customerPasswordDto.setUserName("ADPT");
            Map<String, Object> response = new HashMap<>();
            response.put(RadiusConstants.MESSAGE, "Password has been updated successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=customerController.changePassword(customerPasswordDto,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
          @Test
          @Ignore
        public void validateCustomerTest(){
              Customer customer=getcustomer();
              LoginDto loginDto= getloginDto();
              Map<String, Object> response = new HashMap<>();
              response.put("Customer",customer);
              response.put(RadiusConstants.MESSAGE, "Login User successfully.");

              ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
              Mockito.when(customerService.validateLoginUser(loginDto,1)).thenReturn(customer);
              Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
              ResponseEntity<Map<String, Object>> output=customerController.validateCustomer(loginDto,1,httpServletRequest);
              assertNotNull(output);
              assertEquals(output.getStatusCode().value(),200);

        }

    Customer getcustomer(){
        Customer customer=new Customer();
        customer.setCustomerId(1L);
        customer.setCustomerStatus("Active");
        customer.setBaseDownloadQos(2000L);
        customer.setCountryCode("+91");
        customer.setDownloadSpeed("1000");
        customer.setEmailAddress("nk@gmail.com");
        customer.setUserName("ADPT");
        customer.setBaseUploadQos(111L);
        customer.setBaseDownloadQos(111L);
        customer.setConcurrentPolicyCount(2);
        customer.setCustomerQosPolicyMappings(new ArrayList<>());
        customer.setCreatedOn(new Timestamp(1));
        customer.setCustomerReplyList(new HashSet<>());
        customer.setFailCount(1L);
        customer.setVoucherId(111L);
        customer.setUploadSpeed("1212");
        return customer;

    }
    LoginDto getloginDto(){
        LoginDto loginDto=new LoginDto();
        loginDto.setUserName("admin");
        loginDto.setPassword("admin123");
        return loginDto;
    }

    CustomerDto getcustomerDto(){
         CustomerDto customer=new CustomerDto();
        customer.setCustomerStatus("Active");
        customer.setBaseDownloadQos(2000L);
        customer.setCountryCode("+91");
        customer.setDownloadSpeed("1000");
        customer.setEmailAddress("nk@gmail.com");
        customer.setUserName("ADPT");
        customer.setBaseUploadQos(111L);
        customer.setBaseDownloadQos(111L);
        customer.setConcurrentPolicyCount(2);
        customer.setCustomerQosPolicyMappings(new ArrayList<>());
        customer.setCustomerReplyList(new HashSet<>());
        customer.setFailCount(1L);
        customer.setVoucherId(111L);
        customer.setUploadSpeed("1212");
        return customer;
    }
    UpdateCustomerDto getupdateCustomerDto(){
        UpdateCustomerDto customer=new UpdateCustomerDto();
        customer.setCustomerStatus("Active");
        customer.setBaseDownloadQos(2000L);
        customer.setCountryCode("+91");
        customer.setDownloadSpeed("1000");
        customer.setEmailAddress("nk@gmail.com");
        customer.setUserName("ADPT");
        customer.setBaseUploadQos(111L);
        customer.setBaseDownloadQos(111L);
        customer.setConcurrentPolicyCount(2);
        customer.setCustomerQosPolicyMappings(new ArrayList<>());
        customer.setCustomerReplyList(new HashSet<>());
        customer.setFailCount(1L);
        customer.setVoucherId(111L);
        customer.setUploadSpeed("1212");
        return customer;
    }
}
