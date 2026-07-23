package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.DictionaryAttribute;
import com.savbill.radius.entity.DictionaryValue;
import com.savbill.radius.entity.QDictionaryAttribute;
import com.savbill.radius.entity.QDictionaryValue;
import com.savbill.radius.helper.DictionaryValueDto;
import com.savbill.radius.helper.UpdateDictionaryValueDto;
import com.savbill.radius.repository.DictionaryAttributeRepository;
import com.savbill.radius.repository.DictionaryValueRepository;
import com.savbill.radius.services.DictionaryValueService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class DictionaryValueServiceImpl implements DictionaryValueService {


	
	@Autowired
	private DictionaryValueRepository dictionaryValueRepository;
	
	@Autowired
	private DictionaryAttributeRepository dictionaryAttributeRepository;
	@Autowired
	private UpdateDiffFinder updateDiffFinder;
	
	private static final Logger log = LoggerFactory.getLogger(DictionaryAttributeServiceImpl.class);
	@Override
	public List<DictionaryValue> findAllDictionaryValues(Integer mvnoId) {
		
		try {
//			log.info("Getting all dictionary value");
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression exp = qDictionaryValue.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return dictionaryValueRepository.findAll();
			else {
//				exp = exp.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<DictionaryValue>) dictionaryValueRepository.findAll(exp);
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<DictionaryValue> findByName(String name, Integer mvnoId) {
		
		try {
//			log.info("Getting dictionary value for name " + name);
			
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid name.");
			}
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression exp = qDictionaryValue.isNotNull();
			exp = exp.and(qDictionaryValue.name.eq(name));
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<DictionaryValue>) dictionaryValueRepository.findAll(exp);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public DictionaryValue findDictionaryValueById(Long id, Integer mvnoId) {
		
		try {
//			log.info("Getting dictionary value for id " + id);
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression exp = qDictionaryValue.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			exp = exp.and(qDictionaryValue.dictionaryValueId.eq(id));

			Optional<DictionaryValue> dictionaryValue = dictionaryValueRepository.findOne(exp);
			if (!dictionaryValue.isPresent()) {
				throw new IllegalArgumentException(
						"No record found with dictionary value id " + id + " . Please enter valid dictionary value id.");
			}
			return dictionaryValue.get();
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private DictionaryValue validateDictionaryValueForUpdateAndDelete(Long id, Integer mvnoId) {
		
		try {
//			log.info("Getting dictionary value for id " + id);
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression exp = qDictionaryValue.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionaryValue.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			exp = exp.and(qDictionaryValue.dictionaryValueId.eq(id));

			Optional<DictionaryValue> dictionaryValue = dictionaryValueRepository.findOne(exp);
			if (!dictionaryValue.isPresent()) {
				throw new RuntimeException("You do not have access to update or delete this record.");
			}
			return dictionaryValue.get();
		}
		catch (Throwable e){
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public DictionaryValue saveDictionaryValue(DictionaryValueDto dictionaryValuDto, Integer mvnoId) {
		
		try {
			
//			log.info("Adding new dictionary value with name : "+ dictionaryValuDto.getName());

			DictionaryAttribute dictionaryAttributeVo = validateDictionaryAttribute(dictionaryValuDto.getDictionaryAttributeName(), dictionaryValuDto.getDictionaryAttributeId(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
			DictionaryValue dictionaryValueVo = new DictionaryValue(dictionaryValuDto, dictionaryAttributeVo);
//			dictionaryValueVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryValueData(dictionaryValueVo,false);
			dictionaryValueVo.setCreatedOn(new Timestamp(new Date().getTime()));
			dictionaryValueVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
			return dictionaryValueRepository.save(dictionaryValueVo);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public DictionaryValue updateDictionaryValue(UpdateDictionaryValueDto dictionaryValuDto, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		
		try {
			
			Optional<DictionaryValue> dictionaryValue = dictionaryValueRepository.findById(dictionaryValuDto.getDictionaryValueId());
			DictionaryAttribute dictionaryAttributeVo = validateDictionaryAttribute(dictionaryValuDto.getDictionaryAttributeName(), dictionaryValuDto.getDictionaryAttributeId(), mvnoId);
			DictionaryValue dictionaryValueVo = new DictionaryValue(dictionaryValuDto, dictionaryAttributeVo);
//			dictionaryValueVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryValueData(dictionaryValueVo,true);
			String updated = updateDiffFinder.getUpdatedDiff(dictionaryValue.get(), dictionaryValueVo);
			dictionaryValueVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
			log.info("Dictionary Value updated succefully, updated values "+ updated);
			return dictionaryValueRepository.save(dictionaryValueVo);
		}
		catch (Throwable e) {
			log.error("Error while updating dictionary value: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public void deleteDictionaryValue(Long id, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		
		try {
			DictionaryValue dictionaryValue = validateDictionaryValueForUpdateAndDelete(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
			dictionaryValueRepository.deleteById(id);
			log.info("Dictionary Value deleted succefully: "+dictionaryValue.getName());
		}
		catch (Throwable e) {
			log.error("Error while deleting dictionary value: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}
	
	private DictionaryValue validateDictionaryValueData(DictionaryValue  dictionaryValueVo, boolean isUpdate) {
		
		try {
			
			if(isUpdate && !ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryValueVo.getDictionaryValueId())) {
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary value id");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryValueVo.getName())) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid name.");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryValueVo.getType())) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid type.");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryValueVo.getValue())) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid value.");
			}
			else if(isUpdate) {
				findDictionaryValueById(dictionaryValueVo.getDictionaryValueId(), null);
				DictionaryValue dicValueVo = validateDictionaryValueForUpdateAndDelete(dictionaryValueVo.getDictionaryValueId(), null);
				dictionaryValueVo.setCreatedOn(dicValueVo.getCreatedOn());
			}
			
			checkForDuplicateDictionaryValueEntry(dictionaryValueVo.getDictionaryValueId(), dictionaryValueVo.getName(),isUpdate, dictionaryValueVo.getDictionaryAttribute().getDictionaryAttributeId());
			return dictionaryValueVo;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private DictionaryAttribute validateDictionaryAttribute(String dictionaryAttributeName, Long dictionaryAttributeId, Integer mvnoId)  {
		
		try {
			
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryAttributeName)) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid dictionary attribute name.");
			}
			QDictionaryAttribute qDictionaryAttribute = QDictionaryAttribute.dictionaryAttribute;
			BooleanExpression boolExp = qDictionaryAttribute.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryAttribute.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId),1));
			boolExp = boolExp.and(qDictionaryAttribute.name.eq(dictionaryAttributeName))
					.and(qDictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId));

			Optional<DictionaryAttribute> optDictionaryAttribute = dictionaryAttributeRepository.findOne(boolExp);
			if(!optDictionaryAttribute.isPresent()) {
				throw new RuntimeException("No record found for dictionary attribute with name : '"+dictionaryAttributeName+"', Please enter valid dictionary atrribute name.");
			}
			
			return optDictionaryAttribute.get();
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void checkForDuplicateDictionaryValueEntry(Long dictionaryValueId, String name, boolean isUpdate, Long dictionaryAttrId) {
		
		try {
			
			//Boolean isPresent = null;
			String msg = "Dictionary value with name : '"+name+"' is already exist. Please enter unique dictionary value name.";
			
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression boolExp = qDictionaryValue.isNotNull();
			boolExp = boolExp//.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId),1))
						.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttrId));
			if(!isUpdate) {
				boolExp = boolExp.and(qDictionaryValue.name.eq(name));
				//isPresent = dictionaryValueRepository.findByName(name).isPresent();
				
//				if(boolExp != null) {
//					throw new RuntimeException(msg);
//				}
			}
			else {
				
				boolExp = boolExp.and(qDictionaryValue.name.eq(name))
							.and(qDictionaryValue.dictionaryValueId.ne(dictionaryValueId));
				
				//isPresent = dictionaryValueRepository.findByNameOnUpdate(dictionaryValueId,name).isPresent();
				
//				if(boolExp != null) {
//					throw new RuntimeException(msg);
//				}
			}
			
			List<DictionaryValue> dictionaryValueList =  (List<DictionaryValue>) dictionaryValueRepository.findAll(boolExp);
			if(isUpdate){
				if(dictionaryValueList.size() > 1)
					throw new RuntimeException(msg);
			}
			else
				if(dictionaryValueList.size() > 0)
					throw new RuntimeException(msg);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<DictionaryValue> findByDictionaryAttributeId(Long dictionaryAttributeId, Integer mvnoId) {
		
		try {
			
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryAttributeId)) {
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary attribute id.");
			}

			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression exp = qDictionaryValue.isNotNull();
			exp = exp.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId));
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<DictionaryValue>) dictionaryValueRepository.findAll(exp);
			//return dictionaryValueRepository.findByDictionaryAttributeId(dictionaryAttributeId);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<DictionaryValue> searchDictionaryValue(String name, String value, Long dictionaryAttributeId, Integer mvnoId) {
		
		try {
			
//			List<DictionaryValue> dictionaryValueList = new ArrayList<>();
			
//			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
//				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid name.");
//			}
//			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(value)) {
//				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid value.");
//			}
			
			if(name == null  || name.equals("null") || name.trim().length() == 0) {
				name = "";
			}
			
			if(value == null || value.equals("null") || value.trim().length() == 0) {
				value = "";
			}
			
			QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
			BooleanExpression boolExp = qDictionaryValue.isNotNull();

//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionaryValue.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

			if(!name.isEmpty() && !value.isEmpty()) {
				boolExp = boolExp.and(qDictionaryValue.name.like("%"+ name +"%")
							.and(qDictionaryValue.value.eq(value)))
								.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId));
				
				//dictionaryValueList = dictionaryValueRepository.searchByNameAndValue(name, value, dictionaryAttributeId);
			}
			else if(!name.isEmpty()) {
				boolExp = boolExp.and(qDictionaryValue.name.like("%"+ name +"%")
				.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId)));
				
				//dictionaryValueList = dictionaryValueRepository.searchByName(name, dictionaryAttributeId);
			}
			else {
				boolExp = boolExp.and(qDictionaryValue.value.eq(value)
				.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId)));
				
				//dictionaryValueList = dictionaryValueRepository.searchByValue(value, dictionaryAttributeId);
			}
			List dictionaryValueList = (List<DictionaryValue>) dictionaryValueRepository.findAll(boolExp);
//			if(dictionaryValueList.isEmpty())
//			{
//			if (!StringUtils.isBlank(name) && !StringUtils.isBlank(value)) {
//				throw new IllegalArgumentException(
//						"No record found by with name: "+name+" value: "+value);
//            } else if (!StringUtils.isBlank(name) && StringUtils.isBlank(value)) {
//            	throw new IllegalArgumentException(
//						"No record found by with name: "+name);
//            } else if (StringUtils.isBlank(name) && !StringUtils.isBlank(value)) {
//            	throw new IllegalArgumentException(
//						"No record found by with value: "+value);
//            } else {
//            	throw new IllegalArgumentException("No record found!");
//             }
//			}
			return dictionaryValueList;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private BooleanExpression findDictionaryValueByDictionaryAttributeId(Long dictionaryAttributeId) {
		
		QDictionaryValue qDictionaryValue = QDictionaryValue.dictionaryValue;
		BooleanExpression boolExp = qDictionaryValue.isNotNull();
		
		if (dictionaryAttributeId != null) {
			boolExp = boolExp.and(qDictionaryValue.dictionaryAttribute.dictionaryAttributeId.eq(dictionaryAttributeId));
		}
		return boolExp;
	}
}
