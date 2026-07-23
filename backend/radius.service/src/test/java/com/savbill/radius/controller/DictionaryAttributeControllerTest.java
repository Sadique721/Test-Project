package com.savbill.radius.controller;
import com.savbill.radius.entity.DictionaryAttribute;
import com.savbill.radius.helper.DictionaryAttributeDto;
import com.savbill.radius.helper.UpdateDictionaryAttributeDto;
import com.savbill.radius.services.DictionaryAttributeService;
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
public class DictionaryAttributeControllerTest {
    @InjectMocks
    DictionaryAttributeController dictionaryAttributeController;
    @Mock
    DictionaryAttributeService dictionaryAttributeService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
        @Test
       public void findAllDictionaryAttributes(){
            DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
            List<DictionaryAttribute> dictionaryAttributeList=new ArrayList<>();
            dictionaryAttributeList.add(dictionaryAttribute);
            Map<String, Object> response = new HashMap<>();
            response.put("dictionaryAttributeList",dictionaryAttributeList);
            ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(dictionaryAttributeService.findAllDictionaryAttributes(1)).thenReturn((dictionaryAttributeList));
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.findAllDictionaryAttributes(1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
       }

    @Test
    public void findDictionaryAttributeByIdTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttribute",dictionaryAttribute);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.findDictionaryAttributeById(1L,1)).thenReturn((dictionaryAttribute));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.findDictionaryAttributeById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findDictionaryAttributeByNameTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        List<DictionaryAttribute> dictionaryAttributeList=new ArrayList<>();
        dictionaryAttributeList.add(dictionaryAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttributeList",dictionaryAttributeList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.findByName("Radius",1)).thenReturn((dictionaryAttributeList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.findDictionaryAttributeByName("Radius",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }


    @Test
    public void saveDictionaryAttributeTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        DictionaryAttributeDto dictionaryAttributeDto=new DictionaryAttributeDto();
        dictionaryAttribute.setAttributeId("1");
        dictionaryAttribute.setName("Active");
//        dictionaryAttribute.setMvnoId(1);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttribute",dictionaryAttribute);
        response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.saveDictionaryAttribute(dictionaryAttributeDto,1)).thenReturn((dictionaryAttribute));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.saveDictionaryAttribute(dictionaryAttributeDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateDictionaryAttributeTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        UpdateDictionaryAttributeDto dictionaryAttributeDto=new UpdateDictionaryAttributeDto();
        dictionaryAttributeDto.setAttributeId("1");
        dictionaryAttributeDto.setVendor("ASAS");
        dictionaryAttributeDto.setName("ADPT");
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttribute",dictionaryAttribute);
        response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.updateDictionaryAttribute(dictionaryAttributeDto,1)).thenReturn((dictionaryAttribute));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.updateDictionaryAttribute(dictionaryAttributeDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void deleteDictionaryAttributeTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.deleteDictionaryAttribute(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findDictionaryAttributeByDictionaryIdTest(){
        DictionaryAttribute dictionaryAttribute=getdictionaryAttribute();
        List<DictionaryAttribute> dictionaryAttributeList=new ArrayList<>();
        dictionaryAttributeList.add(dictionaryAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttributeList",dictionaryAttributeList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.findByDictionaryId(1L,1)).thenReturn((dictionaryAttributeList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.findDictionaryAttributeByDictionaryId(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void getAttributeCategoriesTest() {
         List<String> dictionaryAttributeList = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        response.put("attributeCategoryList", dictionaryAttributeList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.getAttributeCategories()).thenReturn((dictionaryAttributeList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.getAttributeCategories(httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }




        DictionaryAttribute getdictionaryAttribute(){
        DictionaryAttribute dictionaryAttribute=new DictionaryAttribute();
        dictionaryAttribute.setAttributeId("1");
        dictionaryAttribute.setDictionaryAttributeId(1L);
//        dictionaryAttribute.setMvnoId(1);
        dictionaryAttribute.setName("Radius");
        dictionaryAttribute.setType("Parellel");
        return dictionaryAttribute;
    }
    @Test
    public void searchDictionaryAttributeTest() {
        DictionaryAttribute  dictionaryAttribute=getdictionaryAttribute();
        List<DictionaryAttribute> dictionaryAttributeList = new ArrayList<>();
        dictionaryAttributeList.add(dictionaryAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryAttributeList", dictionaryAttributeList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryAttributeService.searchDictionaryAttribute("Radius",1L,1)).thenReturn((dictionaryAttributeList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryAttributeController.searchDictionaryAttribute("Radius",1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(), 200);
    }


}
