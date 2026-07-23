package com.savbill.radius.controller;
import com.savbill.radius.entity.DBMapping;
import com.savbill.radius.services.DBMappingService;
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
public class DBMappingControllerTest {
    @InjectMocks
    DBMappingController dbMappingController;
    @Mock
    DBMappingService dbMappingService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
  @Test
   public void findAllDbMappingsTest(){
      DBMapping dbMapping=getdbMapping();
      List<DBMapping> dbMappingList=new ArrayList<>();
      dbMappingList.add(dbMapping);
      Map<String, Object> response = new HashMap<>();
      response.put("DbMappingList",dbMappingList);
      ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
      Mockito.when(dbMappingService.findAllDbMappings(1)).thenReturn(dbMappingList);
      Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
      ResponseEntity<Map<String, Object>> output=dbMappingController.findAllDbMappings(1,httpServletRequest);
      assertNotNull(output);
      assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findDBMappingByDBMappingMasterId(){
        DBMapping dbMapping=getdbMapping();
        List<DBMapping> dbMappingList=new ArrayList<>();
        dbMappingList.add(dbMapping);
        Map<String, Object> response = new HashMap<>();
        response.put("DbMappingList",dbMappingList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(dbMappingService.findDBMappingByDBMappingMasterId(1L,1)).thenReturn(dbMappingList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=dbMappingController.findDBMappingByDBMappingMasterId(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }

	/*
	 * @Test public void updateDBMappingTest(){ DBMapping dbMapping=getdbMapping();
	 * List<DBMapping> dbMappingList=new ArrayList<>();
	 * dbMappingList.add(dbMapping); Map<String, Object> response = new HashMap<>();
	 * response.put("DbMapping",dbMappingList);
	 * response.put(RadiusConstants.MESSAGE,
	 * "DB Mapping has been updated successfully."); ResponseEntity<Map<String,
	 * Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
	 * Mockito.when(dbMappingService.updateDBMapping(dbMappingList,1)).thenReturn(
	 * dbMappingList);
	 * Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response
	 * )).thenReturn(res); ResponseEntity<Map<String, Object>>
	 * output=dbMappingController.updateDBMapping(dbMappingList,1,httpServletRequest
	 * ); assertNotNull(output); assertEquals(output.getStatusCode().value(),200); }
	 */
        @Test
        public void deleteDBMappingTest(){
            Map<String, Object> response = new HashMap<>();
            response.put(RadiusConstants.MESSAGE, "DB Mapping has been deleted successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>> output=dbMappingController.deleteDBMapping(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }









    DBMapping getdbMapping(){
      DBMapping dbMapping=new DBMapping();
      dbMapping.setMappingId(1L);
      dbMapping.setMappingMasterId(1L);
      dbMapping.setRadiusName("RTRT");
      dbMapping.setMvnoId(1);
      return dbMapping;
    }


}
