package com.savbill.radius.controller;

import com.savbill.radius.entity.DBMappingMaster;
import com.savbill.radius.helper.DBMappingMasterDto;
import com.savbill.radius.services.DBMappingMasterService;
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
public class DBMappingMasterControllerTest {
    @InjectMocks
    DBMappingMasterController dbMappingMasterController;
    @Mock
    DBMappingMasterService dbMappingService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
     public void  findAllDBMapingMastersTest(){
        DBMappingMaster dbMapping=new DBMappingMaster();
         dbMapping.setMappingMasterId(1L);
         dbMapping.setMvnoId(1);
        List<DBMappingMaster> dbMappingList=new ArrayList<>();
         dbMappingList.add(dbMapping);
        Map<String, Object> response = new HashMap<>();
         response.put("dbMapingMasterList",dbMappingList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.findAllDBMappingMasters(1)).thenReturn(dbMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.findAllDBMapingMasters(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
     }
    @Test
    public void  findDbMapingMastersByIdTest(){
        DBMappingMaster dbMapping=new DBMappingMaster();
        dbMapping.setMappingMasterId(1L);
        dbMapping.setMvnoId(1);
        Map<String, Object> response = new HashMap<>();
        response.put("dbMapingMaster",dbMapping);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.findDBMappingMasterById(1L,1)).thenReturn(dbMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.findDbMapingMastersById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void  findDbMappingMastersByName(){
        DBMappingMaster dbMapping=new DBMappingMaster();
        dbMapping.setMappingMasterId(1L);
        dbMapping.setMvnoId(1);
        dbMapping.setName("ADPT");
        List<DBMappingMaster> dbMappingList=new ArrayList<>();
        dbMappingList.add(dbMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("dbMapingMasterList",dbMappingList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.findDBMappingMastersByName("ADPT",1)).thenReturn(dbMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.findDbMappingMastersByName("ADPT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void  addDbMappingMasterTest(){
        DBMappingMaster dbMapping=new DBMappingMaster();
        dbMapping.setMappingMasterId(1L);
        dbMapping.setMvnoId(1);
        dbMapping.setName("ADPT");
        DBMappingMasterDto dbMappingMasterDto=new DBMappingMasterDto();
        dbMappingMasterDto.setName("Actvie");
        dbMappingMasterDto.setId(1L);
        Map<String, Object> response = new HashMap<>();
        response.put("dbMapingMaster",dbMapping);
        response.put(RadiusConstants.MESSAGE, "DM Mapping Master has been added successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.saveDbMappingMaster(dbMappingMasterDto,1)).thenReturn(dbMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.addDbMappingMaster(dbMappingMasterDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void   updatedDbMappingTest(){
        DBMappingMaster dbMapping=new DBMappingMaster();
        dbMapping.setMappingMasterId(1L);
        dbMapping.setMvnoId(1);
        dbMapping.setName("ADPT");
        dbMapping.setMvnoId(1);
        Map<String, Object> response = new HashMap<>();
        response.put("dbMapingMaster",dbMapping);
        response.put(RadiusConstants.MESSAGE, "DM Mapping Master has been updated successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.updateDBMappingMaster(dbMapping,1,httpServletRequest)).thenReturn(dbMapping);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.updateDbMappingMaster(dbMapping,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void    deleteDbMappingMasterByIdTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "DB Mapping Master has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
         Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.deleteDbMappingMasterById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void   changeDBMappingMasterStatusTest(){
        String msg="Active";
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE,msg);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.changeStatus(1L,"Inactive",1,httpServletRequest)).thenReturn(msg);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.changeDBMappingMasterStatus(1L,"Inactive",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

    @Test
    public void  getConcurrentPoliciesTest(){
        DBMappingMaster dbMapping=new DBMappingMaster();
        dbMapping.setMappingMasterId(1L);
        dbMapping.setMvnoId(1);
        dbMapping.setName("ADPT");
        List<DBMappingMaster> dbMappingList=new ArrayList<>();
        dbMappingList.add(dbMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("mappingList",dbMappingList);
        ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.getDBMappingMasters(1)).thenReturn(dbMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output = dbMappingMasterController.getConcurrentPolicies(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }


}



