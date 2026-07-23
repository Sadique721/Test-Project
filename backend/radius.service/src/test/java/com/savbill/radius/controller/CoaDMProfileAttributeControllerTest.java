package com.savbill.radius.controller;

import com.savbill.radius.entity.CoaDMProfileAttribute;
import com.savbill.radius.services.CoaDMProfileAttributeService;
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
public class CoaDMProfileAttributeControllerTest {
    @InjectMocks
    CoaDMProfileAttributeController coaDMProfileAttributeController;
    @Mock
    CoaDMProfileAttributeService coaDMProfileAttributeService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findAllCoaDMProfileAttributesTest(){

        CoaDMProfileAttribute coaDMProfileAttribute=getcoaDMProfileAttribute();
        List<CoaDMProfileAttribute>coaDMProfileAttributesList=new ArrayList<>();
        coaDMProfileAttributesList.add(coaDMProfileAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("CoaDMProfileAttributeAttributeList",coaDMProfileAttributesList);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response,HttpStatus.OK);
        Mockito.when(coaDMProfileAttributeService.findAllCoaDMProfileAttributes(1)).thenReturn(coaDMProfileAttributesList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileAttributeController.findAllCoaDMProfileAttributes(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findCoaDMProfileAttributeByIdTest(){

        CoaDMProfileAttribute coaDMProfileAttribute=getcoaDMProfileAttribute();
        List<CoaDMProfileAttribute>coaDMProfileAttributesList=new ArrayList<>();
        coaDMProfileAttributesList.add(coaDMProfileAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("CoaDMProfileAttributeAttributeList",coaDMProfileAttributesList);
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response,HttpStatus.OK);
        Mockito.when(coaDMProfileAttributeService.findCoaDMProfileAttributeByCoaDMProfileId(1L,1)).thenReturn(coaDMProfileAttributesList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileAttributeController.findCoaDMProfileAttributeById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateCoaDMProfileAttributeTest(){
        CoaDMProfileAttribute coaDMProfileAttribute=getcoaDMProfileAttribute();
        List<CoaDMProfileAttribute>coaDMProfileAttributesList=new ArrayList<>();
        coaDMProfileAttributesList.add(coaDMProfileAttribute);
        Map<String, Object> response = new HashMap<>();
        response.put("CoaDMProfileAttributeAttribute",coaDMProfileAttributesList);
        response.put("message","COA/DM Profile attribute has been updated successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response,HttpStatus.OK);
        Mockito.when(coaDMProfileAttributeService.updateCoaDMProfileAttribute(coaDMProfileAttributesList,1, 1L)).thenReturn(coaDMProfileAttributesList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileAttributeController.updateCoaDMProfileAttribute(coaDMProfileAttributesList,1, 1L,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    @Test
    public void deleteCoaDMProfileAttributeTest(){
        Map<String, Object> response = new HashMap<>();
        response.put("message","COA/DM Profile attribute has been deleted successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response,HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileAttributeController.deleteCoaDMProfileAttribute(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
       CoaDMProfileAttribute getcoaDMProfileAttribute(){
        CoaDMProfileAttribute coaDMProfileAttribute=new CoaDMProfileAttribute();
        coaDMProfileAttribute.setCoaDMProfileId(1L);
        coaDMProfileAttribute.setProfileAtt("ADPT");
        coaDMProfileAttribute.setMvnoId(1);
        coaDMProfileAttribute.setRadiusAtt("RRRR");
        coaDMProfileAttribute.setCreatedOn(new Timestamp(1));
        coaDMProfileAttribute.setLastModifiedOn(new Timestamp(2));
        coaDMProfileAttribute.setProfileAtt("create");
        return coaDMProfileAttribute;



       }



}
