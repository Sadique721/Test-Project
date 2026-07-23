package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.entity.DictionaryAttribute;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.AttributeCategory;
import com.savbill.radius.helper.DictionaryAttributeDto;
import com.savbill.radius.helper.UpdateDictionaryAttributeDto;
import com.savbill.radius.repository.DictionaryAttributeRepository;
import com.savbill.radius.repository.DictionaryRepository;
import com.savbill.radius.repository.DictionaryValueRepository;
import com.savbill.radius.services.DictionaryAttributeService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.RadiusUtils;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class DictionaryAttributeServiceImpl implements DictionaryAttributeService
{
	private static final Logger log = LoggerFactory.getLogger(DictionaryAttributeServiceImpl.class);
	
	@Autowired
	private DictionaryAttributeRepository dictionaryAttributeRepository;
	
	@Autowired
	private DictionaryRepository dictionaryRepository;
	
	@Autowired
	private DictionaryValueRepository dictionaryValueRepository;

	
	@Override
	public List<DictionaryAttribute> findAllDictionaryAttributes(Integer mvnoId)
	{
		try
		{
//			log.info("Getting all dictionary attribute");
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression exp = qDictionaryAttribute.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return dictionaryAttributeRepository.findAll();
			else {
//				exp = exp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<DictionaryAttribute>) dictionaryAttributeRepository.findAll(exp);
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<DictionaryAttribute> findByName(String name, Integer mvnoId)
	{
		try
		{
//			log.info("Getting dictionary attribute for name " + name);
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid name.");
			}
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression exp = qDictionaryAttribute.isNotNull();
			//exp = exp.and(qDictionaryAttribute.name.eq(name));
			exp = exp.and(qDictionaryAttribute.name.containsIgnoreCase(name));
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<DictionaryAttribute>) dictionaryAttributeRepository.findAll(exp);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public DictionaryAttribute findDictionaryAttributeById(Long id, Integer mvnoId) {
		try
		{
//			log.info("Getting dictionary attribute for id " + id);
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
				throw new IllegalArgumentException("Please enter valid dictionary attribute id.");
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp = qDictionaryAttribute.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qDictionaryAttribute.dictionaryAttributeId.eq(id));

			Optional<DictionaryAttribute> dictionaryAttribute = dictionaryAttributeRepository.findOne(boolExp);
			if (!dictionaryAttribute.isPresent()) {
				throw new IllegalArgumentException(
						"No record found with dictionary attribute id " + id + " . Please enter valid dictionary attribute id.");
			}
			return dictionaryAttribute.get();

		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private DictionaryAttribute validateDictionaryAttributeForUpdateAndDelete(Long id) {
		try
		{
//			log.info("Getting dictionary attribute for id " + id);
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
				throw new IllegalArgumentException("Please enter valid dictionary attribute id.");
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp = qDictionaryAttribute.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryAttribute.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qDictionaryAttribute.dictionaryAttributeId.eq(id));

			Optional<DictionaryAttribute> dictionaryAttribute = dictionaryAttributeRepository.findOne(boolExp);
			if (!dictionaryAttribute.isPresent()) {
				throw new IllegalArgumentException(
						"You do not have access to update or delete this record");
			}
			return dictionaryAttribute.get();

		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	/*private DictionaryAttribute validateDictionaryAttributeId(Long id) {
		try
		{
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
			{
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid id.");
			}
			Optional<DictionaryAttribute> optDic = dictionaryAttributeRepository.findById(id);
			if(!optDic.isPresent())
			{
				throw new RuntimeException("No record found with id : '"+id+"'. Please enter valid dictionary attribute id");
			}
			else
			{
				return optDic.get();
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
*/
	@Override
	public DictionaryAttribute saveDictionaryAttribute(DictionaryAttributeDto dictionaryAttributeDto, Integer mvnoId) {
		try
		{
//			log.info("Adding new dictionary attribute with name : "+dictionaryAttributeDto.getName());
			validateDictionary(dictionaryAttributeDto.getVendor(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
			Dictionary dictionaryVo = validateDictionaryForUpdateAndDelete(dictionaryAttributeDto.getVendor(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
			DictionaryAttribute dictionaryAttributeVo = new DictionaryAttribute(dictionaryAttributeDto, dictionaryVo);
//			dictionaryAttributeVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryAttributeData(dictionaryAttributeVo,false);	
			dictionaryAttributeVo.setCreatedOn(new Timestamp(new Date().getTime()));
			dictionaryAttributeVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
			return dictionaryAttributeRepository.save(dictionaryAttributeVo);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public DictionaryAttribute updateDictionaryAttribute(UpdateDictionaryAttributeDto dictionaryAttributeDto, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		try
		{
			Optional<DictionaryAttribute> optional = dictionaryAttributeRepository.findById(dictionaryAttributeDto.getDictionaryAttributeId());
			validateDictionary(dictionaryAttributeDto.getVendor(), mvnoId);
			Dictionary dictionaryVo = validateDictionaryForUpdateAndDelete(dictionaryAttributeDto.getVendor(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
			DictionaryAttribute dictionaryAttributeVo = new DictionaryAttribute(dictionaryAttributeDto, dictionaryVo);
			String updated = RadiusUtils.getUpdatedDiff(optional.get(), dictionaryAttributeVo);
//			dictionaryAttributeVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryAttributeData(dictionaryAttributeVo,true);
			dictionaryAttributeVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
	//		log.info("Dictionary attribute updated succefully, updated values "+updated );
			return dictionaryAttributeRepository.save(dictionaryAttributeVo);
		}
		catch (Throwable e)
		{
			log.error("Error while updating Dictionary attribute: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	private DictionaryAttribute validateDictionaryAttributeData(DictionaryAttribute  dictionaryAttributeVo, boolean isUpdate) {
		try
		{
			if(isUpdate && !ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryAttributeVo.getDictionaryAttributeId()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary attribute id");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryAttributeVo.getName()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid name.");
			}
			else if(dictionaryAttributeVo.getType() == null || dictionaryAttributeVo.getType().isEmpty())
			{
				throw new RuntimeException("Please enter valid type. It should not be null or blank.");
			}
			else if(!dictionaryAttributeVo.getCategory().equals(AttributeCategory.ATTRIBUTE) && !dictionaryAttributeVo.getCategory().equals(AttributeCategory.VENDORATTR))
			{
				throw new RuntimeException("Please enter valid category. It should be '"+AttributeCategory.ATTRIBUTE+"' or '"+AttributeCategory.VENDORATTR+"'. ");
			}
			else if(isUpdate)
			{
				if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryAttributeVo.getAttributeId()))
				{
					throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid attribute id");
				}
				DictionaryAttribute dicAttrVo = validateDictionaryAttributeForUpdateAndDelete(dictionaryAttributeVo.getDictionaryAttributeId());
				dictionaryAttributeVo.setCreatedOn(dicAttrVo.getCreatedOn());
			}
			checkForDuplicateDictionaryAttributeEntry(dictionaryAttributeVo.getDictionaryAttributeId(),dictionaryAttributeVo.getName(),isUpdate, dictionaryAttributeVo.getDictionary().getDictionaryId());
			return dictionaryAttributeVo;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private Dictionary validateDictionary(String vendor, Integer mvnoId)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(vendor))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid vendor.");
			}
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionary.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qDictionary.vendor.eq(vendor));
			Optional<Dictionary> optionalDictionary = dictionaryRepository.findOne(boolExp);
			if(!optionalDictionary.isPresent())
			{
				throw new RuntimeException("No record found of dictionary with vendor : '"+vendor+"'. Please enter valid vendor.");
			}
			return optionalDictionary.get();
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private Dictionary validateDictionaryForUpdateAndDelete(String vendor, Integer mvnoId)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(vendor))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid vendor.");
			}
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionary.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qDictionary.vendor.eq(vendor));
			Optional<Dictionary> optionalDictionary = dictionaryRepository.findOne(boolExp);
			if(!optionalDictionary.isPresent())
			{
				throw new RuntimeException("You do not have access to update or delete this record.");
			}
			return optionalDictionary.get();
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void checkForDuplicateDictionaryAttributeEntry(Long dictionaryAttributId,String name, boolean isUpdate, Long dictionaryId)  {
		
		try {
			String msg = "Dictionary attribute with name : '"+name+"' is already exist. Please enter unique attribute name.";
			
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp =   qDictionaryAttribute.isNotNull();
			boolExp = boolExp//.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1))
						.and(qDictionaryAttribute.dictionary.dictionaryId.eq(dictionaryId));
			if(!isUpdate) {
				boolExp = boolExp.and(qDictionaryAttribute.name.eq(name));
//				if(dictionaryAttributeRepository.findByName(name).isPresent())
//				{
//					throw new RuntimeException(msg);
//				}
			}
			else {
				boolExp = boolExp.and(qDictionaryAttribute.name.eq(name))
							.and(qDictionaryAttribute.dictionaryAttributeId.ne(dictionaryAttributId));
//				if(dictionaryAttributeRepository.findByNameOnUpdate(dictionaryAttributId, name).isPresent())
//				{
//					throw new RuntimeException(msg);
//				}
			}
			
			List<DictionaryAttribute> dictionaryAttributeList =  (List<DictionaryAttribute>) dictionaryAttributeRepository.findAll(boolExp);
			if(isUpdate){
				if(dictionaryAttributeList.size() > 1)
						throw new RuntimeException(msg);
			}
			else
				if(dictionaryAttributeList.size() > 0)
					throw new RuntimeException(msg);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteDictionaryAttribute(Long id, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		try
		{
			DictionaryAttribute dictionaryAttribute = validateDictionaryAttributeForUpdateAndDelete(id);
			if(!dictionaryValueRepository.findByDictionaryAttributeId(id).isEmpty())
			{
				throw new RuntimeException("You can not delete this dictionary attribute because one or more dictionary values are associated with this dictionary attribute.");
			}
			dictionaryAttributeRepository.deleteById(id);
		//	log.info("Dictionary Attribute deleted succefully: "+dictionaryAttribute.getName());
		}
		catch (Throwable e)
		{
		//	log.error("Error while deleting Dictionary Attribute: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}
	
	@Override
	public List<DictionaryAttribute> findByDictionaryId(Long id, Integer mvnoId) {
		
		try {
			
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary id.");
			}
			
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp =   qDictionaryAttribute.isNotNull();
			boolExp = boolExp.and(qDictionaryAttribute.dictionary.dictionaryId.eq(id));
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

			return (List<DictionaryAttribute>) dictionaryAttributeRepository.findAll(boolExp);
			
			//return dictionaryAttributeRepository.findByDictionaryId(id);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<String> getAttributeCategories() {
		
		try {
			
			List<String> attributeCategories = new ArrayList<>();
			attributeCategories.add(AttributeCategory.ATTRIBUTE.toString());
			attributeCategories.add(AttributeCategory.VENDORATTR.toString());
			return attributeCategories;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}
//



//	search is pending dId 2, mId null name N(2 records)

	@Override
	public List<DictionaryAttribute> searchDictionaryAttribute(String name, Long dictionaryId, Integer mvnoId) {
		
		try {
			
			/*if(!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid attribute name");
			}
			else if(!ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryId)) {
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary id.");
			}*/
			if(name == null || name.equals("null") || name.trim().length() == 0) {
				name = "";
			}

			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp =   qDictionaryAttribute.isNotNull();

//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

			if(dictionaryId != null)
				boolExp= boolExp.and(qDictionaryAttribute.dictionary.dictionaryId.eq(dictionaryId));
			boolExp = boolExp.and(qDictionaryAttribute.name.like("%" + name + "%"));
			List dictionaryAttributeList =  (List<DictionaryAttribute>) dictionaryAttributeRepository.findAll(boolExp);
//			if(dictionaryAttributeList.isEmpty())
//			{
//				throw new IllegalArgumentException(
//						"No record found by with name: "+name+" Please enter valid name");
//			}
			return dictionaryAttributeList;
			
			//return dictionaryAttributeRepository.searchDictionaryAttribute(name, dictionaryId);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
