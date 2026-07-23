package com.savbill.radius.controller;

import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.helper.DictionaryDto;
import com.savbill.radius.helper.UpdateDictionaryDto;
import com.savbill.radius.services.DictionaryService;
import com.savbill.radius.utils.RadiusConstants;
import liquibase.pro.packaged.D;
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
public class DictionaryControllerTest {
    @InjectMocks
    DictionaryController dictionaryController;
    @Mock
    DictionaryService dictionaryService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllDictionariesTest(){
        Dictionary dictionary=getdictionary();
        List<Dictionary> dictionaryList=new ArrayList<>();
        dictionaryList.add(dictionary);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryList",dictionaryList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.findAllDictionaries(1)).thenReturn((dictionaryList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.findAllDictionaries(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findDictionaryByIdTest(){
        Dictionary dictionary=getdictionary();
        Map<String, Object> response = new HashMap<>();
        response.put("dictionary",dictionary);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.findDictionaryById(1L,1)).thenReturn((dictionary));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.findDictionaryById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void findDictionaryByVendor(){
        Dictionary dictionary=getdictionary();
        List<Dictionary> dictionaryList=new ArrayList<>();
        dictionaryList.add(dictionary);
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryList",dictionaryList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.findByVendor("ADPT",1)).thenReturn((dictionaryList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.findDictionaryByVendor("ADPT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void saveDictionaryTest(){
        Dictionary dictionary=getdictionary();
        DictionaryDto dictionaryDto=new DictionaryDto();
        dictionaryDto.setVendor("ADPT");
        dictionaryDto.setVendorId("1");
        Map<String, Object> response = new HashMap<>();
        response.put("dictionary",dictionary);
        response.put(RadiusConstants.MESSAGE, "Dictionary has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.saveDictionary(dictionaryDto,1)).thenReturn((dictionary));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.saveDictionary(dictionaryDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void updateDictionaryTest(){
        Dictionary dictionary=getdictionary();
        UpdateDictionaryDto dictionaryDto=new UpdateDictionaryDto();
        dictionaryDto.setVendor("ADPT");
        dictionaryDto.setVendorId("1");
        Map<String, Object> response = new HashMap<>();
        response.put("dictionary",dictionary);
        response.put(RadiusConstants.MESSAGE, "Dictionary has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.updateDictionary(dictionaryDto,1,httpServletRequest)).thenReturn((dictionary));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.updateDictionary(dictionaryDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void  deletetTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Dictionary has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.deleteDictionary(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void getVendorTypeTest(){
         List<String> list=new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        response.put("vendorTypeList",list);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.getVendorType()).thenReturn((list));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.getVendorType(httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void searchDictionaryTest(){
        Dictionary dictionary=getdictionary();
        List<Dictionary>dictionaryList=new ArrayList<>();
        dictionaryList.add(dictionary);
        List<String> list=new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        response.put("dictionaryList",dictionaryList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dictionaryService.searchDictionary("ADPT","1","monthly",1)).thenReturn((dictionaryList));
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dictionaryController.searchDictionary("ADPT","1","monthly",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }


    Dictionary getdictionary(){
        Dictionary dictionary=new Dictionary();
        dictionary.setDictionaryId(1L);
        dictionary.setVendor("Parellel");
//        dictionary.setMvnoId(1);
        dictionary.setVendorId("1");
        return  dictionary;
    }

    }




