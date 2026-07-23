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
//import com.savbill.radius.controller.DictionaryValueControllerTest;
//import com.savbill.radius.entity.DictionaryValue;
//import com.savbill.radius.helper.DictionaryValueDto;
//import com.savbill.radius.helper.UpdateDictionaryValueDto;
//import com.savbill.radius.repository.DictionaryAttributeRepository;
//import com.savbill.radius.repository.DictionaryValueRepository;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class DictionaryValueServiceTest {
//	
//	@MockBean 
//	DictionaryValueRepository dictionaryValueRepository;
//	
//	@MockBean
//	DictionaryAttributeRepository dictionaryAttributeRepository;
//	
//	@Autowired
//	DictionaryValueService dictionaryValueService;
//	
//	/*@Test
//	public void testFindDictionaryValueById()
//	{
//		DictionaryValue dictionaryValue = getDictionaryValue();
//		Optional<DictionaryValue> optDictionaryValue = Optional.of(dictionaryValue);
//		Mockito.when(dictionaryValueRepository.findById(dictionaryValue.getDictionaryValueId())).thenReturn(optDictionaryValue);
//		assertThat(dictionaryValueService.findDictionaryValueById(dictionaryValue.getDictionaryValueId(), 1)).isEqualTo(dictionaryValue);
//	}*/
//	
//	/*@Test
//	public void testFindByName()
//	{
//		DictionaryValue dictionaryValue = getDictionaryValue();
//		List<DictionaryValue> dictionaryValueList = new ArrayList<>();
//		dictionaryValueList.add(dictionaryValue);
//		Mockito.when(dictionaryValueRepository.findByNameContaining(dictionaryValue.getName())).thenReturn(dictionaryValueList);
//		assertThat(dictionaryValueService.findByName(dictionaryValue.getName(),1)).isEqualTo(dictionaryValueList);
//	}*/
//	
//	@Test
//	public void testFindAllDictionaryValues()
//	{
//		DictionaryValue dictionaryValue = getDictionaryValue();
//		List<DictionaryValue> dictionaryValueList = new ArrayList<>();
//		dictionaryValueList.add(dictionaryValue);
//		
//		Mockito.when(dictionaryValueRepository.findAll()).thenReturn(dictionaryValueList);
//		assertThat(dictionaryValueService.findAllDictionaryValues(1)).isEqualTo(dictionaryValueList);
//	}
//	
//	public void testSaveDictionaryValue()
//	{
//		DictionaryValue dictionaryValueVo = getDictionaryValue();
//		DictionaryValueDto dictionaryValueDto = getDictionaryValueDto();
//		Mockito.when(dictionaryValueRepository.save(any(DictionaryValue.class))).thenReturn(dictionaryValueVo);
//		assertThat(dictionaryValueService.saveDictionaryValue(dictionaryValueDto, 1)).isEqualTo(dictionaryValueVo);
//	}
//	
//	
//	public void testUpdateDictionaryValue()
//	{
//		DictionaryValue dictionaryValueVo = getDictionaryValue();
//		UpdateDictionaryValueDto dictionaryValueDto = getUpdateDictionaryValueDto();
//		Optional<DictionaryValue> optionalDictionaryValue = Optional.of(dictionaryValueVo);
//		
//		Mockito.when(dictionaryValueRepository.findById(1L)).thenReturn(optionalDictionaryValue);
//		
//		Mockito.when(dictionaryValueRepository.save(any(DictionaryValue.class))).thenReturn(dictionaryValueVo);
//		
//		assertThat(dictionaryValueService.updateDictionaryValue(dictionaryValueDto, 1)).isEqualTo(dictionaryValueVo);
//	}
//	
//	@Test
//	public void testDeleteDictionaryValue()
//	{
//		DictionaryValue dictionaryValue = getDictionaryValue();
//		Optional<DictionaryValue> dictionaryValueOpt = Optional.of(dictionaryValue);
//		Mockito.when(dictionaryValueRepository.findById(1L)).thenReturn(dictionaryValueOpt);
//		Mockito.when(dictionaryValueRepository.existsById(dictionaryValue.getDictionaryValueId())).thenReturn(false);
//		assertFalse(dictionaryValueRepository.existsById(dictionaryValue.getDictionaryValueId()));
//	}
//	
//	private DictionaryValueDto getDictionaryValueDto() {
//		DictionaryValueDto dictionaryValueDto = new DictionaryValueDto();
//		dictionaryValueDto.setDictionaryAttributeName("Acct-Status-Type");
//		dictionaryValueDto.setName("Start");
//		dictionaryValueDto.setValue("1");
//		return dictionaryValueDto;
//	}
//	
//	public static UpdateDictionaryValueDto getUpdateDictionaryValueDto() {
//		UpdateDictionaryValueDto dictionaryValueDto = new UpdateDictionaryValueDto();
//		dictionaryValueDto.setDictionaryAttributeName("Acct-Status-Type");
//		dictionaryValueDto.setName("Start");
//		dictionaryValueDto.setValue("1");
//		dictionaryValueDto.setDictionaryValueId(1L);
//		return dictionaryValueDto;
//	}
//	
//	DictionaryValueControllerTest dictionaryValueControllerTest = new DictionaryValueControllerTest();
//	private DictionaryValue getDictionaryValue()
//	{
//		DictionaryValue dictionaryValueVo = new DictionaryValue();
//		dictionaryValueVo.setDictionaryAttribute(dictionaryValueControllerTest.getDictionaryAttribute());
//		dictionaryValueVo.setDictionaryValueId(1L);
//		dictionaryValueVo.setName("Start");
//		dictionaryValueVo.setType("VALUE");
//		dictionaryValueVo.setValue("1");
//		dictionaryValueVo.setMvnoId(1);
//		return dictionaryValueVo;
//	}
//}
