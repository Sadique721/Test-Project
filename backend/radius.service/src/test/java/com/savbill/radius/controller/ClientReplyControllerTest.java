package com.savbill.radius.controller;
import com.savbill.radius.entity.ClientReply;
import com.savbill.radius.services.ClientReplyService;
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
public class ClientReplyControllerTest {
    @InjectMocks
    ClientReplyController clientReplyController;
    @Mock
    ClientReplyService clientReplyService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;


    @Test
    public void findAllClientRepliesTest(){
        ClientReply clientReply=getclientReply();
        List<ClientReply> clientReplyList=new ArrayList<>();
        clientReplyList.add(clientReply);
        Map<String, Object> response = new HashMap<>();
        response.put("clientReplyList",clientReplyList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(clientReplyService.findAllClientReply(1)).thenReturn(clientReplyList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=clientReplyController.findAllClientReplies(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findClientReplyByIdTest(){
        ClientReply clientReply=getclientReply();
        Map<String, Object> response = new HashMap<>();
        response.put("clientReply",clientReply);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(clientReplyService.findClientReplyById(1L,1)).thenReturn(clientReply);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=clientReplyController.findClientReplyById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
     @Test
    public void findClientReplyByClientGroupIdTest(){
         ClientReply clientReply=getclientReply();
         List<ClientReply> clientReplyList=new ArrayList<>();
         clientReplyList.add(clientReply);
         Map<String, Object> response = new HashMap<>();
         response.put("clientReplyList",clientReplyList);
         ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
         Mockito.when(clientReplyService.findClientReplyByClientGroupId(1L,1)).thenReturn(clientReplyList);
         Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
         ResponseEntity<Map<String, Object>>output=clientReplyController.findClientReplyByClientGroupId(1L,1,httpServletRequest);
         assertNotNull(output);
         assertEquals(output.getStatusCode().value(),200);
    }
        @Test
        public void addNewClientReplyTest(){
            ClientReply clientReply=getclientReply();
            Map<String, Object> response = new HashMap<>();
            response.put("clientReply",clientReply);
            response.put("message", "Client reply has been added successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientReplyService.addClientReply(clientReply,1)).thenReturn(clientReply);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientReplyController.addNewClientReply(clientReply,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
        @Test
        public void updateClientReply(){
            ClientReply clientReply=getclientReply();
            Map<String, Object> response = new HashMap<>();
            response.put("clientReply",clientReply);
            response.put("message","Client reply has been updated successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientReplyService.updateClientReply(clientReply,1)).thenReturn(clientReply);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientReplyController.updateClientReply(clientReply,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);
        }
         @Test
        public void deleteClientReplyTest() {
             Map<String, Object> response = new HashMap<>();
             response.put("message", "Client reply has been deleted successfully.");
             ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
             Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
             ResponseEntity<Map<String, Object>> output = clientReplyController.deleteClientReply(1, 1, httpServletRequest);
             assertNotNull(output);
             assertEquals(output.getStatusCode().value(), 200);
         }

        ClientReply getclientReply(){
            ClientReply clientReply=new ClientReply();
            clientReply.setAttributeId(1L);
            clientReply.setAttribute("ADDR");
            clientReply.setAttributeValue("2121");
            clientReply.setClientGroupId(1L);
            clientReply.setMvnoId(1);
            clientReply.setCreatedOn(new Timestamp(1));
            clientReply.setLastModifiedOn(new Timestamp(1));
            return clientReply;
        }
        




}
