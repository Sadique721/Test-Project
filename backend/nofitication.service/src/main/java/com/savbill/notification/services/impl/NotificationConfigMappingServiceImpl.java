package com.savbill.notification.services.impl;

import com.savbill.notification.entity.*;
import com.savbill.notification.entity.NotificationConfig;
import com.savbill.notification.entity.NotificationConfigMapping;
import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.helper.NotificationConfigMappingDto;
import com.savbill.notification.repository.NotificationConfigMappingRepository;
import com.savbill.notification.repository.NotificationConfigRepository;
import com.savbill.notification.repository.SmsConfigMappingRepository;
import com.savbill.notification.services.NotificationConfigMappingService;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.utils.UpdateDiffFinder;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationConfigMappingServiceImpl implements NotificationConfigMappingService {
    @Autowired
    private SmsConfigMappingRepository smsConfigMappingRepository;

	@Autowired
	private NotificationConfigMappingRepository notificationConfigMappingRepository;

	@Autowired
	private NotificationConfigRepository notificationConfigRepository;
	@Autowired
	UpdateDiffFinder updateDiffFinder;
	@Autowired
	TokenDataExtractor tokenDataExtractor;
	final Logger log = Logger.getLogger(NotificationConfigMappingServiceImpl.class);
    @Override
    public List<NotificationConfigMapping> findNotificationConfigMappingBySmsConfigId(Long notificationConfigId, Long mvnoId) {
	try {
	    return notificationConfigMappingRepository.findByNotificationconfigIdAndMvnoId(notificationConfigId, mvnoId);
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateSmsConfigMappingBySmsConfigId(Long smsConfigId, Long mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
		throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
	    } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsConfigId)) {
		throw new IllegalArgumentException("Please enter valid SMS Config id.");
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<NotificationConfigMapping> findAllNotificationConfigMapping(Long mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
		throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
	    } else {
		if (mvnoId == 1) {
		    return notificationConfigMappingRepository.findAll();
		} else {
		    QNotificationConfigMapping qNotificationConfigMapping =  QNotificationConfigMapping.notificationConfigMapping;
		    BooleanExpression boolExp = qNotificationConfigMapping.isNotNull();
		    boolExp = boolExp.and(qNotificationConfigMapping.mvnoId.eq(mvnoId).or(qNotificationConfigMapping.mvnoId.eq(1L)));
		    return (List<NotificationConfigMapping>) notificationConfigMappingRepository.findAll(boolExp);
		}
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public void deleteNotificationConfigMappingById(Long id, Long mvnoId) {
	try {
	    Optional<NotificationConfigMapping>  notificationConfigMapping= notificationConfigMappingRepository.findById(id);
	    notificationConfigMappingRepository.delete(notificationConfigMapping.get());
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private Optional<SmsConfigMapping> validateSmsConfigMappingById(Long id, Long mvnoId) {
	try {
	    if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
		throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
	    } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
		throw new IllegalArgumentException("Please enter valid SMS Config Mapping id.");
	    }
	    Optional<SmsConfigMapping> optionalSmsConfigMapping = smsConfigMappingRepository
		    .findBySmsConfigMappingIdAndMvnoId(id, mvnoId);

	    if (!optionalSmsConfigMapping.isPresent()) {
		throw new IllegalArgumentException("No record found for SMS Config Mapping with id : '" + id
			+ "'. Please enter valid SMS Config Mapping id.");
	    }
	    return optionalSmsConfigMapping;
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public List<NotificationConfigMapping> saveNotificationConfigMapping(List<NotificationConfigMappingDto> notificationConfigMappingDtoList, Long mvnoId) {
	try {
	    List<NotificationConfigMapping> notificationConfigMappingList = new ArrayList<>();
	    for (NotificationConfigMappingDto notificationConfigMappingDto : notificationConfigMappingDtoList) {
		NotificationConfigMapping notificationConfigMapping = new NotificationConfigMapping(notificationConfigMappingDto, mvnoId);
		notificationConfigMapping.setNotificationconfigId(notificationConfigMappingDto.getNotificationConfigId());
			notificationConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
			notificationConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
			notificationConfigMappingList.add(notificationConfigMapping);
	    }
	    notificationConfigMappingRepository.saveAll(notificationConfigMappingList);
	    return notificationConfigMappingRepository
		    .findByNotificationconfigIdAndMvnoId(notificationConfigMappingList.get(0).getNotificationconfigId(), mvnoId);
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    private void validateSmsConfigMappingData(SmsConfigMapping smsConfigMapping, Long mvnoId) {
	if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
	    throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
	} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfigMapping.getParameter())) {
	    throw new IllegalArgumentException(
		    "SMS Config Parameter is mandatory. Please enter valid SMS Config Parameter.");
	} else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsConfigMapping.getSmsConfigId())) {
	    throw new IllegalArgumentException("SMS Config Id is mandatory. Please enter valid SMS Config Id.");
	} else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfigMapping.getValue())) {
	    throw new IllegalArgumentException(
		    "SMS Config Parameter value is mandatory. Please enter valid SMS Config Parameter value.");
	}
    }

    @Override
    @Transactional
    public List<NotificationConfigMapping> updateNotificationConfigMapping(List<NotificationConfigMappingDto> notificationConfigMappingDtoList, Long mvnoId, Long notificationConfigId, HttpServletRequest request) {
	try {
	    List<NotificationConfigMapping> findAllMappingList = notificationConfigMappingRepository
		    .findByNotificationconfigIdAndMvnoId(notificationConfigId, mvnoId);
		List<NotificationConfigMapping> oldAllMappingList=findAllMappingList;
		deleteNotificationConfigMappings(findAllMappingList);
		String updated=null;
	    for(int i=0;i<notificationConfigMappingDtoList.size();i++)
	    {
			notificationConfigMappingDtoList.get(i).setNotificationConfigId(notificationConfigId);
			updated=updateDiffFinder.getUpdatedDiff(notificationConfigMappingDtoList.get(i),oldAllMappingList.get(i));
	    }
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification Config updated successfullt, with  :  " +updated +"," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return notificationConfigMappingRepository.saveAll(saveNotificationMappings(notificationConfigMappingDtoList,mvnoId));

	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	} catch (IOException e) {
        throw new RuntimeException(e);
    }
    }

    private List<NotificationConfigMapping> saveNotificationMappings(List<NotificationConfigMappingDto> notificationConfigMappingDtoList, Long mvnoId) {
   	List<NotificationConfigMapping> notificationConfigMappingList = new ArrayList<>();
   	for (NotificationConfigMappingDto notificationConfigMappingDto : notificationConfigMappingDtoList) {
   	    NotificationConfigMapping notificationConfigMapping = new NotificationConfigMapping(notificationConfigMappingDto, mvnoId);
		notificationConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
		notificationConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
		notificationConfigMappingList.add(notificationConfigMapping);
   	}
   	return notificationConfigMappingList;
       }

       private void deleteNotificationConfigMappings(List<NotificationConfigMapping> findAllMappingList) {
   	if (!findAllMappingList.isEmpty()) {
   	    notificationConfigMappingRepository.deleteAll(findAllMappingList);
   	}
       }
    
    private List<SmsConfigMapping> getChangedSmsConfigMapping(List<SmsConfigMapping> dbSmsConfigMappingList,
	    List<SmsConfigMapping> SmsConfigMappingList, Long mvnoId, Long smsConfigId) {
	List<SmsConfigMapping> smsConfigMappings = SmsConfigMappingList.stream()
		.filter(smsConfigMapping -> dbSmsConfigMappingList.stream().noneMatch(
			dbSmsConfigMapping -> dbSmsConfigMapping.getParameter().equals(smsConfigMapping.getParameter())
				&& smsConfigMapping.getValue().equals(dbSmsConfigMapping.getValue())))
		.collect(Collectors.toList());
	List<SmsConfigMapping> saveAttributes = smsConfigMappings.stream()
		.filter(smsConfigMapping -> SmsConfigMappingList.stream()
			.noneMatch(dbSmsConfigMapping -> smsConfigMapping.getSmsConfigId() == null ? false
				: smsConfigMapping.getSmsConfigId().equals(dbSmsConfigMapping.getSmsConfigId())
					&& smsConfigMapping.getSmsConfigMappingId() == null
						? false
						: smsConfigMapping.getSmsConfigMappingId()
							.equals(dbSmsConfigMapping.getSmsConfigMappingId())))
		.collect(Collectors.toList());
	for (SmsConfigMapping smsConfigMapping : saveAttributes) {
	    smsConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
	    smsConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
	    smsConfigMapping.setSmsConfigId(smsConfigId);
	    validateSmsConfigMappingData(smsConfigMapping, mvnoId);
	    smsConfigMapping.setMvnoId(mvnoId);
	    smsConfigMappingRepository.save(smsConfigMapping);
	}
	smsConfigMappings.removeAll(saveAttributes);
	return smsConfigMappings;
    }

	public String  getNotificationMappingList(Object buId , String parameter){
		String response ="";
		List<NotificationConfigMapping> notificationConfigMappingList =  new ArrayList<>();
		Optional<NotificationConfig> notificationConfig = null;
		if(buId != null) {
			notificationConfig = notificationConfigRepository.findByBuId(Long.parseLong(buId.toString()));
		}else{
			 notificationConfig = notificationConfigRepository.findByBuId(null);
		}
		if(notificationConfig.isPresent()) {
			notificationConfigMappingList = notificationConfigMappingRepository.findAllByNotificationconfigIdAndParameterContainingIgnoreCase(notificationConfig.get().getNotificationconfigId() ,  parameter);
			if(!notificationConfigMappingList.isEmpty()) {
				response = notificationConfigMappingList.get(0).getValue();
			}
		}
       return response ;
	}
}
