package com.savbill.radius.controller;
import com.savbill.radius.entity.ClientGroup;
import com.savbill.radius.helper.ClientGroupDto;
import com.savbill.radius.services.ClientGroupService;
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
public class ClientGroupControllerTest {
    @InjectMocks
    ClientGroupController clientGroupController;

    @Mock
    ClientGroupService clientGroupService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findAllClientGroupsTest(){
        ClientGroup clientGroup=getclientGroup();
        List<ClientGroup> clientList=new ArrayList<>();
        clientList.add(clientGroup);

        Map<String, Object> response = new HashMap<>();
        response.put("clientGroupList",clientList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(clientGroupService.findAllClientGroups(1)).thenReturn(clientList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=clientGroupController.findAllClientGroups(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);


    }
        @Test
        public void findClientReplyById(){
            ClientGroup clientGroup=getclientGroup();
            Map<String, Object> response = new HashMap<>();
            response.put("clientGroup",clientGroup);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientGroupService.findClientGroupById(1L,1)).thenReturn(clientGroup);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.findClientGroupById(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void findClientGroupByNameTest(){
            ClientGroup clientGroup=getclientGroup();
            List<ClientGroup> clientList=new ArrayList<>();
            clientList.add(clientGroup);
            Map<String, Object> response = new HashMap<>();
            response.put("clientGroupList",clientList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientGroupService.findClientGroupByName("ARDR",1)).thenReturn(clientList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.findClientGroupByName("ARDR",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

        }
        @Test
        public void addClientGroupTest(){
            ClientGroup clientGroup=getclientGroup();
            ClientGroupDto clientGroupDto=getclientGroupDto();
            Map<String, Object> response = new HashMap<>();
            response.put("clientGroup",clientGroup);
            response.put("message", "Client group has been added successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientGroupService.saveClientGroup(clientGroupDto,1)).thenReturn(clientGroup);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.addClientGroup(clientGroupDto,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }

         @Test
        public void updateClientGroupTest(){
             ClientGroup clientGroup=getclientGroup();
             ClientGroupDto clientGroupDto=new ClientGroupDto();
             Map<String, Object> response = new HashMap<>();
             response.put("clientGroup",clientGroup);
             response.put("message", "Client group has been updated successfully.");
             ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
             Mockito.when(clientGroupService.updateClientGroup(clientGroupDto,1)).thenReturn(clientGroup);
             Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
             ResponseEntity<Map<String, Object>>output=clientGroupController.updateClientGroup(clientGroupDto,1,httpServletRequest);
             assertNotNull(output);
             assertEquals(output.getStatusCode().value(),200);

        }
        @Test
        public void deleteClientGroupTest(){
            Map<String, Object> response = new HashMap<>();
            response.put("message","Client group has been deleted successfully.");
            ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.deleteClientGroup(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);


        }
        @Test
        public void updateClientGroupStatusTest(){
            String message="This is Demo";
            Map<String, Object> response = new HashMap<>();
            response.put("message",message);
            ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);

            Mockito.when(clientGroupService.updateClientGroupStatus(1L,"Active",1,httpServletRequest)).thenReturn(message);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.updateClientGroupStatus(1L,"Active",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }

        @Test
        public void getRadiusGroups(){
            ClientGroup clientGroup=getclientGroup();
            List<ClientGroup>clientGroupList=new ArrayList<>();
            clientGroupList.add(clientGroup);
            Map<String, Object> response = new HashMap<>();
            response.put("clientGroupList",clientGroupList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientGroupService.getRadiusGroups(1)).thenReturn(clientGroupList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientGroupController.getRadiusGroups(1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

    }
    ClientGroup getclientGroup(){
        ClientGroup clientGroup=new ClientGroup();
        clientGroup.setClientGroupId(1L);
        clientGroup.setCgStatus("Active");
        clientGroup.setName("ADPT");
        clientGroup.setMvnoId(1);
        clientGroup.setClientReplyList(new ArrayList<>());
        clientGroup.setCreatedOn(new Timestamp(1));
        clientGroup.setLastModifiedOn(new Timestamp(2));
        return clientGroup;
    }

    ClientGroupDto getclientGroupDto(){
        ClientGroupDto clientGroupDto=new ClientGroupDto();
        clientGroupDto.setClientReplyList(new ArrayList<>());
        clientGroupDto.setName("Client");
        clientGroupDto.setCgStatus("Active");
        return clientGroupDto;
    }
}
