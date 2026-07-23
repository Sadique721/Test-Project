package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.helper.DictionaryDto;
import com.savbill.radius.helper.UpdateDictionaryDto;

import javax.servlet.http.HttpServletRequest;


public interface DictionaryService 
{
	List<Dictionary> findAllDictionaries(Integer mvnoId);
	List<Dictionary> findByVendor(String vendor, Integer mvnoId);
	Dictionary saveDictionary(DictionaryDto dictionaryDto, Integer mvnoId);
	Dictionary updateDictionary(UpdateDictionaryDto dictionaryDto, Integer mvnoId, HttpServletRequest request);
	void deleteDictionary(Long dictionaryId, Integer mvnoId);
	Dictionary findDictionaryById(Long dictionaryId, Integer mvnoId);
	List<String> getVendorType();
	List<Dictionary> searchDictionary(String vendor, String vendorId, String vendorType, Integer mvnoId);
}
