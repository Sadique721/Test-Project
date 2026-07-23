//package com.savbill.radius.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import org.junit.Before;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.savbill.radius.entity.Dictionary;
//import com.savbill.radius.entity.DictionaryAttribute;
//import com.savbill.radius.entity.DictionaryValue;
//import com.savbill.radius.helper.AttributeCategory;
//import com.savbill.radius.helper.DictionaryValueDto;
//import com.savbill.radius.helper.UpdateDictionaryValueDto;
//import com.savbill.radius.helper.VendorType;
//import com.savbill.radius.repository.DictionaryValueRepository;
//import com.savbill.radius.services.DictionaryValueService;
//import com.savbill.radius.services.DictionaryValueServiceTest;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
////@RunWith(SpringRunner.class)
////@SpringBootTest
//public class DictionaryValueControllerTest {
//
//	private static final String DICTIONARY_VALUE_LIST = "dictionaryValueList";
//	private static final String DICTIONARY_VALUE = "dictionaryValue";
//	private static final String STATUS = "status";
//
//	@Autowired
//	DictionaryValueService dictionaryValueService;
//	
//	@Autowired
//	APIResponseController apiResponseController;
//	
//	@Autowired 
//	DictionaryValueRepository dictionaryValueRepository;
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
//	String basicUrl = "/SavbillRadius/dictionary/value/";
//	
//	//@Test
//	public void testFindDictionaryValueById() throws Exception 
//	{
//		DictionaryValue dictionaryValue = getDictionaryValue();
//		MvcResult result = mockMvc.perform(get(basicUrl+"findById?mvnoId="+dictionaryValue.getMvnoId()+"&dictionaryValueId="+dictionaryValue.getDictionaryValueId()).content(MediaType.APPLICATION_JSON_VALUE))
//		.andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> readValue = om.readValue(resultContent, HashMap.class);
//		HashMap<?, ?> dictionaryValueVo = (LinkedHashMap<?, ?>) readValue.get(DICTIONARY_VALUE);
//		Long dictionaryValueId = Long.parseLong(dictionaryValueVo.get("dictionaryValueId").toString());
//		assertEquals(dictionaryValueId, dictionaryValue.getDictionaryValueId());
//	}
//	
//	//@Test
//	public void testFindByName() throws Exception
//	{
//		String name = "Start";
//		MvcResult result = mockMvc.perform(get(basicUrl+"findByName?name="+name).content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		@SuppressWarnings("unchecked")
//		List<DictionaryValue> object = (List<DictionaryValue>) output.get(DICTIONARY_VALUE_LIST);
//		assertNotNull(object);
//		assertThat(object.size() > 0);
//		assertEquals(output.get(STATUS), 200);
//	}
//	
//	//@Test
//	public void testFindAllDictionaryValues() throws Exception
//	{
//		MvcResult result = mockMvc.perform(get(basicUrl+"findAll").content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> readValue = om.readValue(resultContent, HashMap.class);
//		//String status = readValue.get("status").toString();
//		@SuppressWarnings("unchecked")
//		List<DictionaryValue> object = (List<DictionaryValue>) readValue.get(DICTIONARY_VALUE_LIST);
//		assertNotNull(object);
//		assertThat(object.size() > 0);
//		assertEquals(readValue.get(STATUS), 200);
//	}
//	
//	//@Test
//	public void testSaveDictionaryValue() throws Exception
//	{
//		DictionaryValueDto dictionaryValueDto = getDictionaryValueDto();
//		
//		String jsonRequest = om.writeValueAsString(dictionaryValueDto);
//		MvcResult result = mockMvc.perform(post(basicUrl+"save").content(jsonRequest)
//				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		HashMap<?, ?> dictionaryValueVo = (LinkedHashMap<?, ?>) output.get(DICTIONARY_VALUE);
//		String name = dictionaryValueVo.get("name").toString();
//		assertNotNull(dictionaryValueVo);
//		assertEquals(output.get(STATUS), 200);
//		assertEquals(name, dictionaryValueDto.getName());
//		
//		dictionaryValueRepository.deleteById(Long.parseLong(dictionaryValueVo.get("dictionaryValueId").toString()));
//	}
//	
//	//@Test
//	public void testUpdateDictionaryValue() throws Exception
//	{
//		UpdateDictionaryValueDto dictionaryValueDto = DictionaryValueServiceTest.getUpdateDictionaryValueDto();
//		dictionaryValueDto.setName("Start");
//		
//		String jsonRequest = om.writeValueAsString(dictionaryValueDto);
//		MvcResult result = mockMvc.perform(put(basicUrl+"update").content(jsonRequest)
//				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		HashMap<?, ?> dictionaryValueVo = (LinkedHashMap<?, ?>) output.get(DICTIONARY_VALUE);
//		String name = dictionaryValueVo.get("name").toString();
//		assertNotNull(dictionaryValueVo);
//		assertEquals(output.get(STATUS), 200);
//		assertEquals(name, dictionaryValueDto.getName());
//	}
//	
//	//@Test
//	public void testDeleteDictionaryValue() throws Exception
//	{
//		DictionaryValue dictionaryValue = new DictionaryValue();
//		dictionaryValue.setDictionaryAttribute(getDictionaryAttribute());
//		dictionaryValue.setName("StopNew");
//		dictionaryValue.setType("VALUE");
//		dictionaryValue.setValue("1");
//		dictionaryValue.setMvnoId(1);
//		DictionaryValue dictionaryValueVo = dictionaryValueRepository.save(dictionaryValue);
//		
//		MvcResult result = mockMvc.perform(delete(basicUrl+"delete?mvnoId="+ dictionaryValueVo.getMvnoId() +"&dictionaryValueId="+dictionaryValueVo.getDictionaryValueId()).content(MediaType.APPLICATION_JSON_VALUE))
//		.andExpect(status().isOk()).andReturn();
//		String resultContent = result.getResponse().getContentAsString();
//		HashMap<?, ?> output = om.readValue(resultContent, HashMap.class);
//		assertEquals(output.get(STATUS), 200);
//	}
//	
//	private DictionaryValueDto getDictionaryValueDto() {
//		DictionaryValueDto dictionaryValueDto = new DictionaryValueDto();
//		dictionaryValueDto.setDictionaryAttributeName("User-Name");
//		dictionaryValueDto.setName("StartNew");
//		dictionaryValueDto.setValue("1");
//		return dictionaryValueDto;
//	}
//	
//	private DictionaryValue getDictionaryValue()
//	{
//		DictionaryValue dictionaryValueVo = new DictionaryValue();
//		dictionaryValueVo.setDictionaryAttribute(getDictionaryAttribute());
//		dictionaryValueVo.setDictionaryValueId(1L);
//		dictionaryValueVo.setName("Start");
//		dictionaryValueVo.setType("VALUE");
//		dictionaryValueVo.setValue("1");
//		dictionaryValueVo.setMvnoId(1);
//		return dictionaryValueVo;
//	}
//	
//	public DictionaryAttribute getDictionaryAttribute()
//	{
//		DictionaryAttribute dictionaryAttributeVo = new DictionaryAttribute();
//		dictionaryAttributeVo.setAttributeId("1");
//		dictionaryAttributeVo.setCategory(AttributeCategory.ATTRIBUTE);
//		dictionaryAttributeVo.setDictionary(getDictionary());
//		dictionaryAttributeVo.setName("User-Name");
//		dictionaryAttributeVo.setType("string");
//		dictionaryAttributeVo.setDictionaryAttributeId(1L);
//		return dictionaryAttributeVo;
//	}
//	
//	public Dictionary getDictionary()
//	{
//		Dictionary dictionaryVo = new Dictionary();
//		dictionaryVo.setDictionaryId(1L);
//		dictionaryVo.setVendor("standard");
//		dictionaryVo.setVendorId("1");
//		dictionaryVo.setVendorType(VendorType.STANDARD);
//		return dictionaryVo;
//	}
//}
