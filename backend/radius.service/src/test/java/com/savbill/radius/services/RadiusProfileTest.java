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
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.savbill.radius.entity.RadiusProfile;
//import com.savbill.radius.helper.RadiusProfileDto;
//import com.savbill.radius.helper.RadiusProfileStatus;
//import com.savbill.radius.helper.Status;
//import com.savbill.radius.repository.RadiusProfileRepository;
//import com.savbill.radius.services.impl.RadiusProfileServiceImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class RadiusProfileTest {
//	@MockBean
//	private RadiusProfileRepository radiusProfileRepository;
//	
//	@Autowired
//	RadiusProfileServiceImpl radiusProfileService;
//	
//	@Test
//	@Ignore
//	public void testFindByName()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		List<RadiusProfile> radiusProfileList = new ArrayList<>();
//		radiusProfileList.add(radiusProfile);
//		
//		Mockito.when(radiusProfileRepository.findByNameContaining(radiusProfile.getName())).thenReturn(radiusProfileList);
//		//assertThat(acctProfileService.findAcctProfileByName(acctProfile.getName())).isEqualTo(acctProfileList);
//	}
//	
//	@Test
//	@Ignore
//	public void testFindById()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		Optional<RadiusProfile> radiusProfileOptional = Optional.of(radiusProfile);
//		
//		Mockito.when(radiusProfileRepository.findById(radiusProfile.getRadiusProfileId())).thenReturn(radiusProfileOptional);
//		assertThat(radiusProfileService.findById(radiusProfile.getRadiusProfileId())).isEqualTo(radiusProfile);
//	}
//	
//	@Test
//	@Ignore
//	public void testFindAll()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		List<RadiusProfile> radiusProfileList = new ArrayList<>();
//		radiusProfileList.add(radiusProfile);
//		
//		Mockito.when(radiusProfileRepository.findAll()).thenReturn(radiusProfileList);
//		assertThat(radiusProfileService.findAll()).isEqualTo(radiusProfileList);
//	}
//	
//	@Test
//	@Ignore
//	public void testSave()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setRadiusProfileId(1L);
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		
//		RadiusProfileDto  radiusProfileDto = new RadiusProfileDto();
//		radiusProfileDto.setName("admin");
//		radiusProfileDto.setStatus(Status.ACTIVE.getValue());
//		radiusProfileDto.setCheckItem("item");
//		radiusProfileDto.setAccountCdrStatus(RadiusProfileStatus.ENABLE.getValue());
//		radiusProfileDto.setSessionStatus(RadiusProfileStatus.ENABLE.getValue());
//		radiusProfileDto.setMappingMasterId(1L);
//		radiusProfileDto.setPriority(999L);
//		
//		Mockito.when(radiusProfileRepository.save(any(RadiusProfile.class))).thenReturn(radiusProfile);
//		assertThat(radiusProfileService.save(radiusProfileDto)).isEqualTo(radiusProfile);
//	}
//	
//	@Test
//	@Ignore
//	public void testUpdate()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setRadiusProfileId(1L);
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		
//		Optional<RadiusProfile> radiusProfileOptional = Optional.of(radiusProfile);
//		
//		RadiusProfileDto  radiusProfileDto = new RadiusProfileDto();
//		radiusProfileDto.setName("admin");
//		radiusProfileDto.setStatus(Status.ACTIVE.getValue());
//		radiusProfileDto.setCheckItem("item");
//		radiusProfileDto.setAccountCdrStatus(RadiusProfileStatus.ENABLE.getValue());
//		radiusProfileDto.setSessionStatus(RadiusProfileStatus.ENABLE.getValue());
//		radiusProfileDto.setMappingMasterId(1L);
//		radiusProfileDto.setPriority(999L);
//		
//		Mockito.when(radiusProfileRepository.findById(1L)).thenReturn(radiusProfileOptional);
//		Mockito.when(radiusProfileRepository.save(any(RadiusProfile.class))).thenReturn(radiusProfile);
//		assertThat(radiusProfileService.update(radiusProfileDto)).isEqualTo(radiusProfile);
//	}
//	
//	@Test
//	@Ignore
//	public void testDeleteById()
//	{
//		RadiusProfile  radiusProfile = new RadiusProfile();
//		radiusProfile.setRadiusProfileId(1L);
//		radiusProfile.setName("admin");
//		radiusProfile.setStatus(Status.ACTIVE.getValue());
//		radiusProfile.setCheckItem("item");
//		radiusProfile.setAccountCdrStatus("Y");
//		radiusProfile.setSessionStatus("Y");
//		radiusProfile.setMappingMasterId(1L);
//		radiusProfile.setPriority(999L);
//		
//		Optional<RadiusProfile> radiusProfileOptional = Optional.of(radiusProfile);
//		Mockito.when(radiusProfileRepository.findById(1L)).thenReturn(radiusProfileOptional);
//		Mockito.when(radiusProfileRepository.existsById(radiusProfile.getRadiusProfileId())).thenReturn(false);
//		assertFalse(radiusProfileRepository.existsById(radiusProfile.getRadiusProfileId()));
//	}
//}
