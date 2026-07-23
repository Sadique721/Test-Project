package com.savbill.radius.controller;

import com.savbill.radius.entity.CoaDMProfile;
import com.savbill.radius.helper.CoaDMProfileDto;
import com.savbill.radius.services.CoaDMProfileService;
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
@Ignore
public class CoaDmProfileControllerTest {
    @InjectMocks
    CoaDMProfileController coaDMProfileController;
    @Mock
    CoaDMProfileService coaDMProfileService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findAllCoaDMProfilesTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        List<CoaDMProfile> coaDMProfileList=new ArrayList<>();
        coaDMProfileList.add(coaDMProfile);
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfileList",coaDMProfileList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.findAllCoaDMProfiles(1)).thenReturn(coaDMProfileList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.findAllCoaDMProfiles(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findCoaDMProfilesByTypeTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        List<CoaDMProfile> coaDMProfileList=new ArrayList<>();
        coaDMProfileList.add(coaDMProfile);
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfileList",coaDMProfileList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.findByType("TTTT",1)).thenReturn(coaDMProfileList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.findCoaDMProfilesByType("TTTT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    @Test
    public void findCoaDMProfileByIdTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfile",coaDMProfile);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.findCoaDMProfileById(1L,1)).thenReturn(coaDMProfile);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.findCoaDMProfileById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
   // @Ignore
    public void findCoaDMProfileByNameTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        Optional<CoaDMProfile>coaDMProfileOptional=Optional.of(coaDMProfile);
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfile",coaDMProfile);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.findCoaDMProfileByName("ADPT",1)).thenReturn(coaDMProfileOptional);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.findCoaDMProfileByName("ADPT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void addCoaDMProfileTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        CoaDMProfileDto coaDMProfileDto=new CoaDMProfileDto();
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfile",coaDMProfile);
        response.put("message","COA/DM Profile has been added successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.saveCoaDMProfile(coaDMProfileDto,1)).thenReturn(coaDMProfile);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.addCoaDMProfile(coaDMProfileDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void updateCoaDMProfileTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfile",coaDMProfile);
        response.put("message","COA/DM Profile has been updated successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.updateCoaDMProfile(coaDMProfile,1,httpServletRequest)).thenReturn(coaDMProfile);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.updateCoaDMProfile(coaDMProfile,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteCoaDMProfileTest(){
        Map<String, Object> response = new HashMap<>();
        response.put("message","COA/DM Profile has been deleted successfully.");
        ResponseEntity<Map<String, Object>>res=new ResponseEntity<>(response,HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.deleteCoaDMProfile(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void searchCoaDMProfileTest(){
        CoaDMProfile coaDMProfile=getcoaDMProfile();
        List<CoaDMProfile>coaDMProfileList=new ArrayList<>();
        coaDMProfileList.add(coaDMProfile);
         Map<String, Object> response = new HashMap<>();
        response.put("coaDMProfileList",coaDMProfileList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(coaDMProfileService.searchCoaDMProfile("ADPT","TTTT",1)).thenReturn(coaDMProfileList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=coaDMProfileController.searchCoaDMProfile("ADPT","TTTT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
        @Test
        public void findCoaProfilesTest(){
            CoaDMProfile coaDMProfile=getcoaDMProfile();
            List<CoaDMProfile>coaDMProfileList=new ArrayList<>();
            coaDMProfileList.add(coaDMProfile);
            Map<String, Object> response = new HashMap<>();
            response.put("coaDMProfileList",coaDMProfileList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(coaDMProfileService.searchCoaDMProfile("ADPT","TTTT",1)).thenReturn(coaDMProfileList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=coaDMProfileController.searchCoaDMProfile("ADPT","TTTT",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }

    CoaDMProfile getcoaDMProfile(){
        CoaDMProfile coaDMProfile=new CoaDMProfile();
        coaDMProfile.setCoaDMProfileId(1L);
        coaDMProfile.setName("ADPT");
        coaDMProfile.setMvnoId(1);
        coaDMProfile.setGateway("RDRD");
        coaDMProfile.setType("TTTT");
        coaDMProfile.setCreatedOn(new Timestamp(1));
        coaDMProfile.setLastModifiedOn(new Timestamp(2));
        coaDMProfile.setPort(1122);
        coaDMProfile.setSharedkey("121");
        return coaDMProfile;

    }


}
