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
//import com.savbill.radius.entity.Dictionary;
//import com.savbill.radius.entity.DictionaryAttribute;
//import com.savbill.radius.helper.AttributeCategory;
//import com.savbill.radius.helper.DictionaryAttributeDto;
//import com.savbill.radius.helper.UpdateDictionaryAttributeDto;
//import com.savbill.radius.helper.VendorType;
//import com.savbill.radius.repository.DictionaryAttributeRepository;
//import com.savbill.radius.repository.DictionaryRepository;
//import com.savbill.radius.repository.DictionaryValueRepository;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class DictionaryAttributeServiceTest {
//	
//	@MockBean
//	DictionaryRepository dictionaryRepository;
//	
//	@MockBean 
//	DictionaryAttributeRepository dictionaryAttributeRepository;
//	
//	@MockBean
//	DictionaryValueRepository dictionaryValueRepository;
//	
//	@Autowired
//	DictionaryAttributeService dictionaryAttributeService;
//	
//	/*@Test
//	public void testFindDictionaryAttributeById()
//	{
//		DictionaryAttribute dictionaryAttribute = getDictionaryAttribute();
//		Optional<DictionaryAttribute> optDictionaryAttribute = Optional.of(dictionaryAttribute);
//		Mockito.when(dictionaryAttributeRepository.findById(dictionaryAttribute.getDictionaryAttributeId())).thenReturn(optDictionaryAttribute);
//		assertThat(dictionaryAttributeService.findDictionaryAttributeById(dictionaryAttribute.getDictionaryAttributeId(),1)).isEqualTo(dictionaryAttribute);
//	}	*/
//	
//	/*@Test
//	public void testFindByName()
//	{
//		DictionaryAttribute dictionaryAttribute = getDictionaryAttribute();
//		List<DictionaryAttribute> dictionaryAttributeList = new ArrayList<>();
//		dictionaryAttributeList.add(dictionaryAttribute);
//		Mockito.when(dictionaryAttributeRepository.findByNameContaining(dictionaryAttribute.getName())).thenReturn(dictionaryAttributeList);
//		assertThat(dictionaryAttributeService.findByName(dictionaryAttribute.getName(), 1)).isEqualTo(dictionaryAttributeList);
//	}*/
//	
//	@Test
//	public void testFindAllDictionaryAttributes()
//	{
//		DictionaryAttribute dictionaryAttribute = getDictionaryAttribute();
//		List<DictionaryAttribute> dictionaryAttributeList = new ArrayList<>();
//		dictionaryAttributeList.add(dictionaryAttribute);
//		
//		Mockito.when(dictionaryAttributeRepository.findAll()).thenReturn(dictionaryAttributeList);
//		assertThat(dictionaryAttributeService.findAllDictionaryAttributes(1)).isEqualTo(dictionaryAttributeList);
//	}
//	
//	
//	public void testSaveDictionaryAttribute()
//	{
//		DictionaryAttribute dictionaryAttributeVo = getDictionaryAttribute();
//		DictionaryAttributeDto dictionaryAttributeDto = getDictionaryAttributeDto();
//		Mockito.when(dictionaryAttributeRepository.save(any(DictionaryAttribute.class))).thenReturn(dictionaryAttributeVo);
//		assertThat(dictionaryAttributeService.saveDictionaryAttribute(dictionaryAttributeDto, 1)).isEqualTo(dictionaryAttributeVo);
//	}
//	
//	
//	public void testUpdateDictionaryAttribute()
//	{
//		DictionaryAttribute dictionaryAttributeVo = getDictionaryAttribute();
//		UpdateDictionaryAttributeDto dictionaryAttributeDto = getUpdateDictionaryAttributeDto();
//		Optional<DictionaryAttribute> optionalDictionaryAttribute = Optional.of(dictionaryAttributeVo);
//		
//		Mockito.when(dictionaryAttributeRepository.findById(1L)).thenReturn(optionalDictionaryAttribute);
//		
//		Mockito.when(dictionaryAttributeRepository.save(any(DictionaryAttribute.class))).thenReturn(dictionaryAttributeVo);
//		
//		assertThat(dictionaryAttributeService.updateDictionaryAttribute(dictionaryAttributeDto, 1)).isEqualTo(dictionaryAttributeVo);
//	}
//	
//	@Test
//	public void testDeleteDictionaryAttribute()
//	{
//		DictionaryAttribute dictionaryAttribute = getDictionaryAttribute();
//		Optional<DictionaryAttribute> dictionaryAttributeOpt = Optional.of(dictionaryAttribute);
//		Mockito.when(dictionaryAttributeRepository.findById(1L)).thenReturn(dictionaryAttributeOpt);
//		Mockito.when(dictionaryAttributeRepository.existsById(dictionaryAttribute.getDictionaryAttributeId())).thenReturn(false);
//		assertFalse(dictionaryAttributeRepository.existsById(dictionaryAttribute.getDictionaryAttributeId()));
//	}
//	
//	private DictionaryAttributeDto getDictionaryAttributeDto() {
//		DictionaryAttributeDto dictionaryAttributeDto = new DictionaryAttributeDto();
//		dictionaryAttributeDto.setCategory(AttributeCategory.ATTRIBUTE);
//		dictionaryAttributeDto.setName("User-Name");
//		dictionaryAttributeDto.setType("string");
//		dictionaryAttributeDto.setVendor("cisco");
//		dictionaryAttributeDto.setAttributeId("1");
//		return dictionaryAttributeDto;
//	}
//	
//	public static UpdateDictionaryAttributeDto getUpdateDictionaryAttributeDto() {
//		UpdateDictionaryAttributeDto dictionaryAttributeDto = new UpdateDictionaryAttributeDto();
//		dictionaryAttributeDto.setCategory(AttributeCategory.ATTRIBUTE);
//		dictionaryAttributeDto.setName("User-Name");
//		dictionaryAttributeDto.setType("string");
//		dictionaryAttributeDto.setVendor("cisco");
//		dictionaryAttributeDto.setAttributeId("1");
//		dictionaryAttributeDto.setDictionaryAttributeId(1L);
//		return dictionaryAttributeDto;
//	}
//	
//	private DictionaryAttribute getDictionaryAttribute()
//	{
//		DictionaryAttribute dictionaryAttributeVo = new DictionaryAttribute();
//		dictionaryAttributeVo.setAttributeId("1");
//		dictionaryAttributeVo.setCategory(AttributeCategory.ATTRIBUTE);
//		dictionaryAttributeVo.setDictionary(getDictionary());
//		dictionaryAttributeVo.setName("User-Name");
//		dictionaryAttributeVo.setType("string");
//		dictionaryAttributeVo.setDictionaryAttributeId(1L);
//		dictionaryAttributeVo.setMvnoId(1);
//		return dictionaryAttributeVo;
//	}
//	
//	private Dictionary getDictionary()
//	{
//		Dictionary dictionaryVo = new Dictionary();
//		dictionaryVo.setDictionaryId(1L);
//		dictionaryVo.setVendor("standard");
//		dictionaryVo.setVendorId("1");
//		dictionaryVo.setVendorType(VendorType.STANDARD);
//		return dictionaryVo;
//	}
//}
