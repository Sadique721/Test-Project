//package com.savbill.radius.services;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertFalse;
//import static org.mockito.ArgumentMatchers.any;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.savbill.radius.entity.Client;
//import com.savbill.radius.helper.ClientDto;
//import com.savbill.radius.repository.ClientRepository;
//import com.savbill.radius.services.impl.ClientServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class ClientServiceTest 
//{
//	@MockBean
//	private ClientRepository clientRepository;
//	
//	@Autowired
//	ClientServiceImpl clientService;
//	
//	@Test
//	public void testFindClientByIpAddress()
//	{
//		Client client = new Client();
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		//client.setCreatedOn(null);
//		//client.setLastModifiedOn(null);
//		List<Client> clientList = new ArrayList<>();
//		clientList.add(client);
//		
//		Mockito.when(clientRepository.findClientByIpAddress(client.getClientIpAddress())).thenReturn(client);
//		//assertThat(clientService.findClientByIpAddress(client.getClientIpAddress())).isEqualTo(clientList);
//	}
//
//	/*@Test
//	public void testFindClientById()
//	{
//		Client client = new Client();
//		client.setClientId(1L);
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		client.setMvnoId(1);
//		//client.setCreatedOn(null);
//		//client.setLastModifiedOn(null);
//		Optional<Client> optionalClient = Optional.of(client);
//		
//		Mockito.when(clientRepository.findById(client.getClientId())).thenReturn(optionalClient);
//		assertThat(clientService.findClientById(client.getClientId(), 1)).isEqualTo(client);
//	}*/
//	
//	@Test
//	public void testFindAllClients()
//	{
//		Client client = new Client();
//		client.setClientId(1L);
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		client.setMvnoId(1);
//		List<Client> clinetList = new ArrayList<>();
//		clinetList.add(client);
//		
//		Mockito.when(clientRepository.findAll()).thenReturn(clinetList);
//		assertThat(clientService.findAllClients(1)).isEqualTo(clinetList);
//	}
//	
//	@Test
//	public void testSaveClient()
//	{
//		Client client = new Client();
//		client.setClientId(1L);
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		
//		ClientDto clientDto = new ClientDto();
//		clientDto.setClientGroupId(1L);
//		clientDto.setClientIpAddress("/127.0.0.1");
//		clientDto.setIpType("IPv4");
//		clientDto.setSharedKey("secret");
//		clientDto.setTimeOut("3000");
//		
//		
//		Mockito.when(clientRepository.save(any(Client.class))).thenReturn(client);
//		assertThat(clientService.saveClient(clientDto,1)).isEqualTo(client);
//	}
//	
//	/*@Test
//	public void testUpdateClient()
//	{
//		Client client = new Client();
//		client.setClientId(1L);
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		client.setMvnoId(1);
//		Optional<Client> optionalClient = Optional.of(client);
//		
//		Mockito.when(clientRepository.findById(1L)).thenReturn(optionalClient);
//		client.setTimeOut("4000");
//		
//		Mockito.when(clientRepository.save(any(Client.class))).thenReturn(client);
//		
//		assertThat(clientService.updateClient(client, 1)).isEqualTo(client);
//	}*/
//	
//	@Test
//	public void testDeleteClientById()
//	{
//		Client client = new Client();
//		client.setClientId(1L);
//		client.setClientIpAddress("/127.0.0.1");
//		client.setClientGroupId(1L);
//		client.setIpType("IPv4");
//		client.setSharedKey("secret");
//		client.setTimeOut("3000");
//		
//		Optional<Client> optionalClient = Optional.of(client);
//		Mockito.when(clientRepository.findById(1L)).thenReturn(optionalClient);
//		Mockito.when(clientRepository.existsById(client.getClientId())).thenReturn(false);
//		assertFalse(clientRepository.existsById(client.getClientId()));
//	}
//}
