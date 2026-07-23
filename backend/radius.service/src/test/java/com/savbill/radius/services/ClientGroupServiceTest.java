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
//import com.savbill.radius.entity.ClientGroup;
//import com.savbill.radius.helper.ClientGroupDto;
//import com.savbill.radius.repository.ClientGroupRepository;
//import com.savbill.radius.services.impl.ClientGroupServiceImpl;
//import com.savbill.radius.utils.RadiusConstants;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class ClientGroupServiceTest 
//{
//	@MockBean
//	private ClientGroupRepository clientGroupRepository;
//	
//	@Autowired
//	ClientGroupServiceImpl clientGroupService;
//	
//	@Test
//	public void testFindClientGroupByName()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		
//		List<ClientGroup> clientGroupList = new ArrayList<>();
//		clientGroupList.add(clientGroup);
//		
//		Mockito.when(clientGroupRepository.findByNameContaining(clientGroup.getName())).thenReturn(clientGroupList);
//		//assertThat(clientGroupService.findClientGroupByName(clientGroup.getName())).isEqualTo(clientGroupList);
//	}
//	
//	/*@Test
//	public void testFindClientGroupById()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		clientGroup.setMvnoId(1);
//		Optional<ClientGroup> optionalClientGroup = Optional.of(clientGroup);
//		
//		Mockito.when(clientGroupRepository.findById(clientGroup.getClientGroupId())).thenReturn(optionalClientGroup);
//		assertThat(clientGroupService.findClientGroupById(clientGroup.getClientGroupId(), 1)).isEqualTo(clientGroup);
//	}*/
//	
//	@Test
//	public void testFindAllClientGroups()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		
//		List<ClientGroup> clientGroupList = new ArrayList<>();
//		clientGroupList.add(clientGroup);
//		
//		Mockito.when(clientGroupRepository.findAll()).thenReturn(clientGroupList);
//		assertThat(clientGroupService.findAllClientGroups(1)).isEqualTo(clientGroupList);
//	}
//	
//	@Test
//	public void testSaveClientGroup()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		
//		ClientGroupDto dto = new ClientGroupDto();
//		dto.setCgStatus(RadiusConstants.ACTIVE);
//		dto.setName("Default");
//		
//		Mockito.when(clientGroupRepository.save(any(ClientGroup.class))).thenReturn(clientGroup);
//		assertThat(clientGroupService.saveClientGroup(dto, 2)).isEqualTo(clientGroup);
//	}
//	
//	/*@Test
//	public void testUpdateClientGroup()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		clientGroup.setMvnoId(1);
//		Optional<ClientGroup> optionalClientGroup = Optional.of(clientGroup);
//		
//		Mockito.when(clientGroupRepository.findById(1L)).thenReturn(optionalClientGroup);
//		clientGroup.setCgStatus(RadiusConstants.IN_ACTIVE);
//		
//		Mockito.when(clientGroupRepository.save(any(ClientGroup.class))).thenReturn(clientGroup);
//		
//		assertThat(clientGroupService.updateClientGroup(clientGroup, 1)).isEqualTo(clientGroup);
//	}*/
//	
//	@Test
//	public void testDeleteClientGroupById()
//	{
//		ClientGroup clientGroup = new ClientGroup();
//		clientGroup.setCgStatus(RadiusConstants.ACTIVE);
//		clientGroup.setClientGroupId(1L);
//		clientGroup.setName("Default");
//		
//		Optional<ClientGroup> optionalClientGroup = Optional.of(clientGroup);
//		Mockito.when(clientGroupRepository.findById(1L)).thenReturn(optionalClientGroup);
//		Mockito.when(clientGroupRepository.existsById(clientGroup.getClientGroupId())).thenReturn(false);
//		assertFalse(clientGroupRepository.existsById(clientGroup.getClientGroupId()));
//	}
//}
