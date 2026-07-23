//package com.savbill.radius.services;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertFalse;
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
//import com.savbill.radius.entity.AuthResponse;
//import com.savbill.radius.repository.AuthResponseRepository;
//import com.savbill.radius.services.impl.AuthResponseServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class AuthResponseServiceTest {
//	@MockBean
//	private AuthResponseRepository authResponseRepository;
//	
//	@Autowired
//	AuthResponseServiceImpl authResponseService;
//	
//	/*@Test
//	public void testFindAuthResponseByUserName()
//	{
//		AuthResponse authResponse = new AuthResponse();
//		authResponse.setUserName("admin");
//		authResponse.setReplyMessage("admin123");
//		authResponse.setPacketType("type1");
//		authResponse.setClientIp("127.0.0.1");
//		authResponse.setClientGroup("Group");
//		authResponse.setMvnoId(1);
//
//		List<AuthResponse> authResponseList = new ArrayList<>();
//		authResponseList.add(authResponse);
//		
//		Mockito.when(authResponseRepository.findByUserNameContaining(authResponse.getUserName())).thenReturn(authResponseList);
//		assertThat(authResponseService.findAuthResponseByUserName(authResponse.getUserName(), 1)).isEqualTo(authResponseList);
//	}*/
//	
//	@Test
//	public void testFindAllAcctCdr()
//	{
//		AuthResponse authResponse = new AuthResponse();
//		authResponse.setUserName("admin");
//		authResponse.setReplyMessage("admin123");
//		authResponse.setPacketType("type1");
//		authResponse.setClientIp("127.0.0.1");
//		authResponse.setClientGroup("Group");
//		List<AuthResponse> authResponseList = new ArrayList<>();
//		authResponseList.add(authResponse);
//		
//		Mockito.when(authResponseRepository.findAll()).thenReturn(authResponseList);
//		assertThat(authResponseService.findAllAuthResponse(1, null).getContent()).isEqualTo(authResponseList);
//	}
//	
//	@Test
//	public void testDeleteClientById()
//	{
//		AuthResponse authResponse = new AuthResponse();
//		authResponse.setAuthresId(1L);
//		authResponse.setUserName("admin");
//		authResponse.setReplyMessage("admin123");
//		authResponse.setPacketType("type1");
//		authResponse.setClientIp("127.0.0.1");
//		authResponse.setClientGroup("Group");
//		Optional<AuthResponse> optionalAuthResp = Optional.of(authResponse);
//		
//		Mockito.when(authResponseRepository.findById(1L)).thenReturn(optionalAuthResp);
//		Mockito.when(authResponseRepository.existsById(authResponse.getAuthresId())).thenReturn(false);
//		assertFalse(authResponseRepository.existsById(authResponse.getAuthresId()));
//	}
//}
