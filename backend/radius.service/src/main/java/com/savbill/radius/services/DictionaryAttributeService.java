package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.DictionaryAttribute;
import com.savbill.radius.helper.DictionaryAttributeDto;
import com.savbill.radius.helper.UpdateDictionaryAttributeDto;

public interface DictionaryAttributeService {

	List<DictionaryAttribute> findAllDictionaryAttributes(Integer mvnoId);
	List<DictionaryAttribute> findByName(String name, Integer mvnoId);
	DictionaryAttribute saveDictionaryAttribute(DictionaryAttributeDto dictionaryAttribute, Integer mvnoId);
	DictionaryAttribute updateDictionaryAttribute(UpdateDictionaryAttributeDto dictionaryAttribute, Integer mvnoId);
	void deleteDictionaryAttribute(Long id, Integer mvnoId);
	DictionaryAttribute findDictionaryAttributeById(Long id, Integer mvnoId);
	List<DictionaryAttribute> findByDictionaryId(Long id, Integer mvnoId);
	List<String> getAttributeCategories();
	List<DictionaryAttribute> searchDictionaryAttribute(String name,Long dictionaryId, Integer mvnoId);
}
