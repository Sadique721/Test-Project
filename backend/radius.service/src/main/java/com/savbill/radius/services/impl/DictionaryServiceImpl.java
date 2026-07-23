package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.utils.*;
import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;

import com.savbill.radius.entity.*;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.DictionaryDto;
import com.savbill.radius.helper.UpdateDictionaryDto;
import com.savbill.radius.helper.VendorType;
import com.savbill.radius.repository.DictionaryAttributeRepository;
import com.savbill.radius.repository.DictionaryRepository;
import com.savbill.radius.services.DictionaryService;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.servlet.http.HttpServletRequest;

@Service
public class DictionaryServiceImpl implements DictionaryService 
{
	private static final String VENDOR_TYPE_VENDOR = "VENDOR";
	private static final String VENDOR_TYPE_STANDARD = "STANDARD";
//	private static Log log = LogFactory.getLog(DictionaryAttributeServiceImpl.class);

	@Autowired
	DictionaryRepository dictionaryRepository; 
	
	@Autowired
	DictionaryAttributeRepository dictionaryAttributeRepository;
	@Autowired
	private UpdateDiffFinder updateDiffFinder;

	private static final Logger log = LoggerFactory.getLogger(DictionaryServiceImpl.class);
	@Override
	public List<Dictionary> findAllDictionaries(Integer mvnoId)
	{
		try
		{
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression exp = qDictionary.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return dictionaryRepository.findAll();
			else {
//				exp = exp.and(qDictionary.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<Dictionary>) dictionaryRepository.findAll(exp);
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<Dictionary> findByVendor(String vendor, Integer mvnoId)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(vendor))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid vendor");
			}
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression exp = qDictionary.isNotNull();
			exp = exp.and(qDictionary.vendor.eq(vendor));
//			if(mvnoId == null || mvnoId != 1)
//				exp = exp.and(qDictionary.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<Dictionary>) dictionaryRepository.findAll(exp);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Dictionary saveDictionary(DictionaryDto dictionaryDto, Integer mvnoId)
	{
		try
		{
			Dictionary dictionaryVo =  new Dictionary(dictionaryDto);
//			dictionaryVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryData(dictionaryVo,false);
			dictionaryVo.setCreatedOn(new Timestamp(new Date().getTime()));
			dictionaryVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
			return dictionaryRepository.save(dictionaryVo);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Dictionary updateDictionary(UpdateDictionaryDto dictionaryDto, Integer mvnoId, HttpServletRequest request) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		try
		{
			Dictionary dictionaryVo =  new Dictionary(dictionaryDto);
			Optional<Dictionary> optional = dictionaryRepository.findById(dictionaryDto.getDictionaryId());
//			dictionaryVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validateDictionaryData(dictionaryVo,true);
			dictionaryVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
			String updated = updateDiffFinder.getUpdatedDiff(optional.get(), dictionaryVo);
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been updated successfully updated values,"+updated+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
			return dictionaryRepository.save(dictionaryVo);
		}
		catch (Throwable e)
		{
		//	log.error("Error while updating dictionary: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	private void validateDictionaryData(Dictionary dictionaryVo, boolean isUpdate) 
	{
		try
		{
			if(isUpdate && !ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryVo.getDictionaryId()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Please enter valid dictionary id.");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryVo.getVendor()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid vendor.");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(dictionaryVo.getVendorId()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid vendor id.");
			}
			else if(!dictionaryVo.getVendorType().name().equals(VENDOR_TYPE_VENDOR) && !dictionaryVo.getVendorType().name().equals(VENDOR_TYPE_STANDARD))
			{
				throw new RuntimeException("Please enter valid vendor type. It should be '"+VENDOR_TYPE_STANDARD+"' or '"+VENDOR_TYPE_VENDOR+"'");
			}
			else if(isUpdate)
			{
				Dictionary optionalObj = validateDictionaryForUpdateAndDelete(dictionaryVo.getDictionaryId());
				dictionaryVo.setCreatedOn(optionalObj.getCreatedOn());
				dictionaryVo.setDictionaryId(optionalObj.getDictionaryId());
			}
			checkForDuplicateDictionaryEntry(dictionaryVo.getDictionaryId(),dictionaryVo.getVendor(),isUpdate);
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private void checkForDuplicateDictionaryEntry(Long dictionaryId, String vendor, boolean isUpdate) {
		
		try {
			
			String msg = "Dictionary with vendor : '"+vendor+"' is already exist. Please enter unique vendor.";
			
			QDictionary  qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();

			if (isUpdate)
			{
				boolExp = boolExp.and(qDictionary.dictionaryId.ne(dictionaryId));
			}

//			if(mvnoId == 1)
//			{
				boolExp = boolExp.and(qDictionary.vendor.eq(vendor));
				List<Dictionary> dictionaryList = (List<Dictionary>) dictionaryRepository.findAll(boolExp);
				if(!dictionaryList.isEmpty())
				{
					throw new IllegalArgumentException(msg);
				}
//			}
//			else
//			{
//				boolExp = boolExp.and(qDictionary.vendor.eq(vendor)).and((qDictionary.mvnoId.eq(mvnoId)).or(qDictionary.mvnoId.eq(1)));
//				Optional<Dictionary> optionalDictionary = dictionaryRepository.findOne(boolExp);
//				if(optionalDictionary.isPresent())
//				{
//					throw new IllegalArgumentException(msg);
//				}
//			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteDictionary(Long dictionaryId, Integer mvnoId) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		
		try {
			Dictionary dictionary = validateDictionaryForUpdateAndDelete(dictionaryId);
			boolean isEmpty = dictionaryAttributeRepository.findByDictionaryId(dictionaryId).isEmpty();
			
			if(!isEmpty) {
				throw new RuntimeException("You can not delete this dictionary because one or more dictionary atrributes are associated with this dictionary.");
			}

			dictionaryRepository.deleteById(dictionaryId);
			//log.info("Dictionary deleted succefully for vendor : "+dictionary.getVendor());
		}
		catch (Throwable e) {
			//log.error("Error while deleting Dictionary: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public Dictionary findDictionaryById(Long dictionaryId, Integer mvnoId) {
		try {
			
			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryId))
				throw new IllegalArgumentException("Please enter valid dictionary id.");
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionary.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qDictionary.dictionaryId.eq(dictionaryId));

			Optional<Dictionary> dictionary = dictionaryRepository.findOne(boolExp);
			if (!dictionary.isPresent()) {
				throw new IllegalArgumentException(
						"No record found with dictionary id " + dictionaryId + " . Please enter valid dictionary id.");
			}
			return dictionary.get();
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private Dictionary validateDictionaryForUpdateAndDelete(Long dictionaryId) {
		try {

			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryId))
				throw new IllegalArgumentException("Please enter valid dictionary id.");
			QDictionary qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();
//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionary.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qDictionary.dictionaryId.eq(dictionaryId));

			Optional<Dictionary> dictionary = dictionaryRepository.findOne(boolExp);
			if (!dictionary.isPresent()) {
				throw new RuntimeException("You do not have access to update or delete this record.");
			}
			return dictionary.get();
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private Dictionary validateDictionaryId(Long dictionaryId) {
		try {
			
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(dictionaryId)) {
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid dictionary id.");
			}
			
			Optional<Dictionary> dicOptional = dictionaryRepository.findById(dictionaryId);
			
			if(!dicOptional.isPresent()) {
				throw new RuntimeException("No record found with id :'"+dictionaryId+"', Please enter valid dictionary id."); 
			}
			else {
				return dicOptional.get();
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<String> getVendorType() {
		
		try {
			
			List<String> vendorTypeList = new ArrayList<>();
			vendorTypeList.add(VendorType.STANDARD.toString());
			vendorTypeList.add(VendorType.VENDOR.toString());
			return vendorTypeList;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<Dictionary> searchDictionary(String vendor, String vendorId, String vendorType, Integer mvnoId) {
		
		try {
			
			//List<Dictionary> dictitonaries = new ArrayList<>();
			
			if(vendor == null || vendor.equals("null") || vendor.trim().length() == 0) {
				vendor = "";
			}
			
			if(vendorId == null || vendorId.equals("null") || vendorId.trim().length() == 0) {
				vendorId = "";
			}
			
			if(vendorType == null || vendorType.equals("null") || vendorType.trim().length() == 0) {
				vendorType = "";
			}
			
			QDictionary  qDictionary = QDictionary.dictionary;
			BooleanExpression boolExp = qDictionary.isNotNull();

//			if(mvnoId == null || mvnoId != 1)
//				boolExp = boolExp.and(qDictionary.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			
			if(!vendor.isEmpty() && !vendorId.isEmpty() && !vendorType.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendor.like("%"+ vendor +"%"))
						.and(qDictionary.vendorId.like("%"+ vendorId +"%"))
						.and(qDictionary.vendorType.eq(VendorType.valueOf(vendorType)));
				//return dictionaryRepository.findByVendorContainingAndVendorIdContainingAndVendorType(vendor, vendorId, vendorType);
			}
			else if(!vendor.isEmpty() && !vendorId.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendor.like("%"+ vendor +"%"))
						.and(qDictionary.vendorId.like("%"+ vendorId +"%"));
				//dictitonaries = dictionaryRepository.findByVendorContainingAndVendorIdContaining(vendor, vendorId);
			}
			else if(!vendor.isEmpty() && !vendorType.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendor.like("%"+ vendor +"%"))
				.and(qDictionary.vendorType.eq(VendorType.valueOf(vendorType)));
				
				//dictitonaries = dictionaryRepository.findByVendorContainingAndVendorType(vendor, vendorType);
			}
			else if(!vendorId.isEmpty() && !vendorType.isEmpty()){
				boolExp = boolExp.and(qDictionary.vendorId.like("%"+ vendorId +"%"))
				.and(qDictionary.vendorType.eq(VendorType.valueOf(vendorType)));
				
				//dictitonaries = dictionaryRepository.findByVendorIdContainingAndVendorType(vendorId, vendorType);
			}
			else if(!vendor.isEmpty() && vendorId.isEmpty() && vendorType.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendor.like("%"+ vendor +"%"));
				
				//dictitonaries = dictionaryRepository.findByVendorContaining(vendor);
			}
			else if(!vendorId.isEmpty() && vendor.isEmpty() && vendorType.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendorId.like("%"+ vendorId +"%"));
				
				//dictitonaries = dictionaryRepository.findByVendorIdContaining(vendorId);
			}
			else if(!vendorType.isEmpty() && vendor.isEmpty() && vendorId.isEmpty()) {
				boolExp = boolExp.and(qDictionary.vendorType.eq(VendorType.valueOf(vendorType)));
				
				//dictitonaries = dictionaryRepository.findByVendorType(vendorType);
			}
			
			List dictionaryList = (List<Dictionary>) dictionaryRepository.findAll(boolExp);
//			if(dictionaryList.isEmpty())
//			{
//			if (!StringUtils.isBlank(vendor) && !StringUtils.isBlank(vendorId) && !StringUtils.isBlank(vendorType)) {
//				throw new IllegalArgumentException(
//						"No record found by with vendor: "+vendor+" vendor Id: "+vendorId+" vendor type: "+vendorType);
//            } else if (!StringUtils.isBlank(vendor) && !StringUtils.isBlank(vendorId) && StringUtils.isBlank(vendorType)) {
//            	throw new IllegalArgumentException(
//						"No record found by with vendor: "+vendor+" vendor Id: "+vendorId);
//            } else if (!StringUtils.isBlank(vendor) && StringUtils.isBlank(vendorId) && !StringUtils.isBlank(vendorType)) {
//            	throw new IllegalArgumentException(
//						"No record found by with vendor: "+vendor+" vendor type: "+vendorType);
//            } else if (StringUtils.isBlank(vendor) && !StringUtils.isBlank(vendorId) && !StringUtils.isBlank(vendorType)) {
//            	throw new IllegalArgumentException(
//						"No record found by with vendor Id: "+vendorId+" vendor type: "+vendorType);
//            } else {
//            	throw new IllegalArgumentException("No record found!");
//             }
//			}
			return dictionaryList;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
