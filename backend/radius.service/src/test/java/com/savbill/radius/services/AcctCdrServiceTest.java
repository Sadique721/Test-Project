//package com.savbill.radius.services;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertFalse;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.savbill.radius.entity.AcctCdr;
//import com.savbill.radius.repository.AcctCdrRepository;
//import com.savbill.radius.services.impl.AcctCdrServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class AcctCdrServiceTest {
//
//	@MockBean
//	private AcctCdrRepository acctCdrRepository;
//	
//	@Autowired
//	AcctCdrServiceImpl acctCdrService;
//	
//	@Test
//	public void testFindAcctCdrByUserName()
//	{
//		AcctCdr acctCdr = new AcctCdr();
//		acctCdr.setUserName("admin");
//		acctCdr.setChapPassword("admin123");
//		acctCdr.setNasIpAddress("127.0.0.1");
//		acctCdr.setFramedIpAddress("127.0.0.1");
//		acctCdr.setCreatedDate(new Timestamp(new Date().getTime()));
//		List<AcctCdr> acctCdrList = new ArrayList<>();
//		acctCdrList.add(acctCdr);
//		
//		Mockito.when(acctCdrRepository.findByUserName(acctCdr.getUserName(),acctCdr.getFramedIpAddress())).thenReturn(acctCdrList);
//		//assertThat(acctCdrService.findAcctCrdByUserName(acctCdr.getUserName(),acctCdr.getFramedIpAddress(),acctCdr.getCreatedDate().toString(),acctCdr.getCreatedDate().toString())).isEqualTo(acctCdrList);
//	}
//	
//	@Test
//	@Ignore
//	public void testFindAllAcctCdr()
//	{
//		AcctCdr acctCdr = new AcctCdr();
//		acctCdr.setUserName("admin");
//		acctCdr.setChapPassword("admin123");
//		acctCdr.setNasIpAddress("127.0.0.1");
//		acctCdr.setMvnoId(1);
//		List<AcctCdr> acctCdrList = new ArrayList<>();
//		acctCdrList.add(acctCdr);
//		
//		Mockito.when(acctCdrRepository.findAll()).thenReturn(acctCdrList);
//		assertThat(acctCdrService.findAllAcctCdr(1, null)).isEqualTo(acctCdrList);
//	}
//	
//	@Test
//	public void testDeleteAcctCdrById()
//	{
//		AcctCdr acctCdr = new AcctCdr();
//		acctCdr.setCdrId(1L);
//		acctCdr.setUserName("admin");
//		acctCdr.setChapPassword("admin123");
//		acctCdr.setNasIpAddress("127.0.0.1");
//		Optional<AcctCdr> optionalAcctCdr = Optional.of(acctCdr);
//		
//		Mockito.when(acctCdrRepository.findById(1L)).thenReturn(optionalAcctCdr);
//		Mockito.when(acctCdrRepository.existsById(acctCdr.getCdrId())).thenReturn(false);
//		assertFalse(acctCdrRepository.existsById(acctCdr.getCdrId()));
//	}
//}
