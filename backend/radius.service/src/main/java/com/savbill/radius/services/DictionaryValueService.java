package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.DictionaryValue;
import com.savbill.radius.helper.DictionaryValueDto;
import com.savbill.radius.helper.UpdateDictionaryValueDto;

public interface DictionaryValueService {

	List<DictionaryValue> findAllDictionaryValues(Integer mvnoId);
	List<DictionaryValue> findByName(String name, Integer mvnoId);
	DictionaryValue saveDictionaryValue(DictionaryValueDto dictionaryValuDto, Integer mvnoId);
	DictionaryValue updateDictionaryValue(UpdateDictionaryValueDto dictionaryValue, Integer mvnoId);
	void deleteDictionaryValue(Long id, Integer mvnoId);
	DictionaryValue findDictionaryValueById(Long id, Integer mvnoId);
	List<DictionaryValue> findByDictionaryAttributeId(Long dictionaryAttributeId, Integer mvnoId);
	List<DictionaryValue> searchDictionaryValue(String name, String value, Long dictionaryAttributeId, Integer mvnoId);
}
