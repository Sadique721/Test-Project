package com.savbill.radius.controller;

import com.savbill.radius.entity.Client;
import com.savbill.radius.helper.ClientDto;
import com.savbill.radius.services.ClientService;
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
public class ClientControllerTest {
    @InjectMocks
    ClientController clientController;
    @Mock
    ClientService clientService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void findAllClientsTest(){
    Client client=getclient();
        List<Client>clientList=new ArrayList<>();
        clientList.add(client);

        Map<String, Object> response = new HashMap<>();
        response.put("clientList",clientList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        Mockito.when(clientService.findAllClients(1)).thenReturn(clientList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>>output=clientController.findAllClients(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
        @Test
        public void findClientById(){
        Client client=getclient();
            Map<String, Object> response = new HashMap<>();
            response.put("client",client);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientService.findClientById(1L,1,httpServletRequest)).thenReturn(client);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientController.findClientById(1L,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

        }
        @Test
        public void findClientByIpAddressTest(){
            Client client=getclient();
            List<Client>clientList=new ArrayList<>();
            clientList.add(client);
            Map<String, Object> response = new HashMap<>();
            response.put("clientList",clientList);
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientService.findClientByIpAddress("11111",1)).thenReturn(clientList);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientController.findClientByIpAddress("11111",1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

    }
      @Test
    public void addClientTest(){
          Client client=getclient();
          ClientDto clientDto=getclientDto();
          Map<String, Object> response = new HashMap<>();
          response.put("client",client);
          response.put("message", "Client has been added successfully.");
          ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
          Mockito.when(clientService.saveClient(clientDto,1)).thenReturn(client);
          Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
          ResponseEntity<Map<String, Object>>output=clientController.addClient(clientDto,1,httpServletRequest);
          assertNotNull(output);
          assertEquals(output.getStatusCode().value(),200);
    }
        @Test
        public void updateClientTest(){
            Client client=getclient();
            Map<String, Object> response = new HashMap<>();
            response.put("client",client);
            response.put("message", "Client has been updated successfully.");
            ResponseEntity<Map<String, Object>> res=new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            Mockito.when(clientService.updateClient(client,1,httpServletRequest)).thenReturn(client);
            Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
            ResponseEntity<Map<String, Object>>output=clientController.updateClient(client,1,httpServletRequest);
            assertNotNull(output);
            assertEquals(output.getStatusCode().value(),200);

        }
          @Test
        public void deleteClientTest(){
              Map<String, Object> response = new HashMap<>();
              response.put("message","Client has been deleted successfully.");
              ResponseEntity<Map<String, Object>>res=new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
              Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
              ResponseEntity<Map<String, Object>>output=clientController.deleteClient(1L,1,httpServletRequest);
              assertNotNull(output);
              assertEquals(output.getStatusCode().value(),200);
        }
    Client getclient(){
        Client client=new Client();
        client.setClientId(1L);
        client.setClientGroupId(1L);
        client.setClientIpAddress("11111");
        client.setMvnoId(1);
        client.setIpType("LL22MM");
        client.setCreatedOn(new Timestamp(1));
        client.setLastModifiedOn(new Timestamp(2));
        client.setSharedKey("11");
        client.setTimeOut("1");
        return client;
    }

    ClientDto getclientDto(){
        ClientDto clientDto=new ClientDto();
        clientDto.setClientGroupId(1L);
        clientDto.setClientGroupId(1L);
        clientDto.setClientIpAddress("PP11NN33");
        clientDto.setTimeOut("1");
        clientDto.setSharedKey("11");
    return  clientDto;
    }
}
