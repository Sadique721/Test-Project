//package com.savbill.radius.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.savbill.radius.entity.RadiusProfile;
//import com.savbill.radius.helper.Status;
//import com.savbill.radius.repository.RadiusProfileRepository;
//import com.savbill.radius.services.AuthResponseService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class RadiusProfileControllerTest 
//{
//	private static final String ACCTPROFILE_LIST = "acctProfileList";
//	private static final String STATUS = "status";
//
//	@Autowired
//	AuthResponseService authResponseService; 
//	
//	@Autowired
//	APIResponseController apiResponseController;
//	
//	@Autowired 
//	RadiusProfileRepository acctProfileRepository;
//	
//	private MockMvc mockMvc;
//	
//	@Autowired
//	private WebApplicationContext context;
//
//	ObjectMapper om = new ObjectMapper();
//	
//	@Before
//	public void setup()
//	{
//		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//	}
//	
//	//String basicUrl = "/api/v1/";
//	String basicUrl = "/SavbillRadius/";
//	
//	@Test
//	@Ignore
//	public void testFindAllRadiusProfiles() throws Exception
//	{
//		MvcResult result = mockMvc.perform(get(basicUrl+"acctProfiles").content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> readValue = om.readValue(resultContent, HashMap.class);
//	
//		@SuppressWarnings("unchecked")
//		List<RadiusProfile> object = (List<RadiusProfile>) readValue.get(ACCTPROFILE_LIST);
//		assertNotNull(object);
//		assertThat(object.size() > 0);
//		assertEquals(readValue.get(STATUS), 200);
//	}
//	
//	@Test
//	@Ignore
//	public void testFindRadiusProfileByName() throws Exception
//	{
//		String name = "Default";
//		MvcResult result = mockMvc.perform(get(basicUrl+"findAcctProfileByName?name="+name).content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		@SuppressWarnings("unchecked")
//		List<RadiusProfile> object = (List<RadiusProfile>) output.get(ACCTPROFILE_LIST);
//		assertNotNull(object);
//		assertThat(object.size() > 0);
//		assertEquals(output.get(STATUS), 200);
//	}
//	
//	@Test
//	@Ignore
//	public void testDeleteRadiusProfile() throws Exception
//	{
//		RadiusProfile  acctProfile = new RadiusProfile();
//		acctProfile.setRadiusProfileId(1L);
//		acctProfile.setName("admin");
//		acctProfile.setStatus(Status.ACTIVE.getValue());
//		acctProfile.setCheckItem("item");
//		acctProfile.setAccountCdrStatus("Y");
//		acctProfile.setSessionStatus("Y");
//		acctProfile.setMappingMasterId(1L);
//		acctProfile.setPriority(999L);
//		
//		RadiusProfile acctProfileVo = acctProfileRepository.save(acctProfile);
//		
//		MvcResult result = mockMvc.perform(delete(basicUrl+"deleteAcctProfile?acctprofileid="+acctProfileVo.getRadiusProfileId()).content(MediaType.APPLICATION_JSON_VALUE))
//		.andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		assertEquals(output.get(STATUS), 200);
//	}
//}
