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
//import com.savbill.radius.helper.DictionaryDto;
//import com.savbill.radius.helper.UpdateDictionaryDto;
//import com.savbill.radius.helper.VendorType;
//import com.savbill.radius.repository.DictionaryAttributeRepository;
//import com.savbill.radius.repository.DictionaryRepository;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class DictionaryServiceTest {
//
//	@MockBean
//	DictionaryRepository dictionaryRepository;
//	
//	@MockBean 
//	DictionaryAttributeRepository dictionaryAttributeRepository;
//	
//	@Autowired
//	DictionaryService dictionaryService;
//	
//	/*@Test
//	public void testFindDictionaryById()
//	{
//		Dictionary dictionary = getDictionary();
//		Optional<Dictionary> optDictionary = Optional.of(dictionary);
//		Mockito.when(dictionaryRepository.findById(dictionary.getDictionaryId())).thenReturn(optDictionary);
//		assertThat(dictionaryService.findDictionaryById(dictionary.getDictionaryId(), 1)).isEqualTo(dictionary);
//	}*/
//	
//	/*@Test
//	public void testFindByVendor()
//	{
//		Dictionary dictionary = getDictionary();
//		List<Dictionary> dictionaryList = new ArrayList<>();
//		dictionaryList.add(dictionary);
//		Mockito.when(dictionaryRepository.findByVendorContaining(dictionary.getVendor())).thenReturn(dictionaryList);
//		assertThat(dictionaryService.findByVendor(dictionary.getVendor(),1)).isEqualTo(dictionaryList);
//	}*/
//	
//	@Test
//	public void testFindAllDictionaries()
//	{
//		Dictionary dictionary = getDictionary();
//		List<Dictionary> dictionaryList = new ArrayList<>();
//		dictionaryList.add(dictionary);
//		
//		Mockito.when(dictionaryRepository.findAll()).thenReturn(dictionaryList);
//		assertThat(dictionaryService.findAllDictionaries(1)).isEqualTo(dictionaryList);
//	}
//	
//	@Test
//	public void testSaveDictionary()
//	{
//		Dictionary dictionaryVo = getDictionary();
//		DictionaryDto dictionaryDto = getDictionaryDto();
//		Mockito.when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(dictionaryVo);
//		assertThat(dictionaryService.saveDictionary(dictionaryDto, 1)).isEqualTo(dictionaryVo);
//	}
//	
//	public void testUpdateDictionary()
//	{
//		Dictionary dictionaryVo = getDictionary();
//		UpdateDictionaryDto dictionaryDto = getUpdateDictionaryDto();
//		Optional<Dictionary> optionalDictionary = Optional.of(dictionaryVo);
//		
//		Mockito.when(dictionaryRepository.findById(3L)).thenReturn(optionalDictionary);
//		//dictionaryVo.setVendor("cisco");
//		
//		Mockito.when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(dictionaryVo);
//		
//		assertThat(dictionaryService.updateDictionary(dictionaryDto, 1)).isEqualTo(dictionaryVo);
//	}
//	
//	@Test
//	public void testDeleteDictionary()
//	{
//		Dictionary dictionary = getDictionary();
//		Optional<Dictionary> dictionaryOpt = Optional.of(dictionary);
//		Mockito.when(dictionaryRepository.findById(3L)).thenReturn(dictionaryOpt);
//		Mockito.when(dictionaryRepository.existsById(dictionary.getDictionaryId())).thenReturn(false);
//		assertFalse(dictionaryRepository.existsById(dictionary.getDictionaryId()));
//	}
//	
//	public DictionaryDto getDictionaryDto() {
//		DictionaryDto dictionaryDto = new DictionaryDto();
//		dictionaryDto.setVendor("cisco");
//		dictionaryDto.setVendorId("9");
//		dictionaryDto.setVendorType(VendorType.VENDOR);
//		return dictionaryDto;
//	}
//	
//	public static UpdateDictionaryDto getUpdateDictionaryDto() {
//		UpdateDictionaryDto dictionaryDto = new UpdateDictionaryDto();
//		dictionaryDto.setVendor("cisco");
//		dictionaryDto.setVendorId("9");
//		dictionaryDto.setVendorType(VendorType.VENDOR);
//		dictionaryDto.setDictionaryId(3L);
//		return dictionaryDto;
//	}
//	
//	private Dictionary getDictionary()
//	{
//		Dictionary dictionaryVo = new Dictionary();
//		dictionaryVo.setDictionaryId(3L);
//		dictionaryVo.setVendor("cisco");
//		dictionaryVo.setVendorId("9");
//		dictionaryVo.setVendorType(VendorType.VENDOR);
//		dictionaryVo.setMvnoId(1);
//		return dictionaryVo;
//	}
//}
