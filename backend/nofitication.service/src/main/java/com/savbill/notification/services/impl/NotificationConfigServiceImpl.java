package com.savbill.notification.services.impl;

import com.savbill.notification.entity.NotificationConfig;
import com.savbill.notification.entity.QNotificationConfig;
import com.savbill.notification.entity.QSmsConfig;
import com.savbill.notification.repository.NotificationConfigRepository;
import com.savbill.notification.repository.SmsConfigRepository;
import com.savbill.notification.services.NotificationConfigService;
import com.savbill.notification.services.SmsConfigService;
import com.savbill.notification.utils.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.utils.UpdateDiffFinder;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NotificationConfigServiceImpl implements NotificationConfigService {
	@Autowired
	SmsConfigRepository smsConfigRepository;

	@Autowired
	private NotificationConfigRepository notificationConfigRepository;
	@Autowired
    TokenDataExtractor tokenDataExtractor;
	@Autowired
    UpdateDiffFinder updateDiffFinder;

	private static Logger log = Logger.getLogger(SmsConfigService.class);
	
	@Override
	public NotificationConfig updateNotificationConfig(NotificationConfig notificationConfig, Long mvnoId, HttpServletRequest request)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(notificationConfig.getLastModifiedBy()))
				log.error("LastModifiedBy value is Missing");

			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
			{
				throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
			}
			else if(!ValidateCrudTransactionData.validateLongTypeFieldValue(notificationConfig.getNotificationconfigId()))
			{
				throw new IllegalArgumentException("Sms config id is mandatory. Please enter valid sms config id");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(notificationConfig.getBuName()))
			{
				throw new IllegalArgumentException("Sms url is mandatory. Please enter valid sms url");
			}

			NotificationConfig optionalNotificationConfig = notificationConfigRepository.findByNotificationconfigIdAndMvnoId(notificationConfig.getNotificationconfigId() ,mvnoId).orElse(null);
			if(Objects.isNull(optionalNotificationConfig))
			{
				throw new IllegalArgumentException("No record found to update the sms configuration.");
			}
			NotificationConfig addnotificationConfig = new NotificationConfig();
			addnotificationConfig = notificationConfig;
			if(mvnoId == 1)
			{
				addnotificationConfig.setMvnoId(optionalNotificationConfig.getMvnoId());
			}
			addnotificationConfig.setMvnoId(mvnoId);
			addnotificationConfig.setLastModifiedDate(LocalDateTime.now());
			addnotificationConfig.setLastModifiedBy(addnotificationConfig.getLastModifiedBy());
			addnotificationConfig.setCreateDate(optionalNotificationConfig.getCreateDate());
			addnotificationConfig.setCreatedBy(optionalNotificationConfig.getCreatedBy());
			String updatedValue= updateDiffFinder.getUpdatedDiff(optionalNotificationConfig,addnotificationConfig);
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification Configuration Updated Successfully with "+updatedValue+"  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
			return notificationConfigRepository.save(addnotificationConfig);
		}
		catch (RuntimeException e) 
		{
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	public List<NotificationConfig> findAllSmsConfig(Long mvnoId, Long buId)
	{
		try 
		{
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
			{
				throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
			}
			else
			{
				if(mvnoId == 1)
				{
					return notificationConfigRepository.findAll();
				}
				else
				{
					QNotificationConfig qNotificationConfig = QNotificationConfig.notificationConfig;
					BooleanExpression boolExp = qNotificationConfig.isNotNull();
					boolExp = boolExp.and(qNotificationConfig.mvnoId.eq(mvnoId).or(qNotificationConfig.mvnoId.eq(1L)));
					if(Objects.nonNull(buId)) {
						boolExp = boolExp.and(qNotificationConfig.buId.eq(buId));
					}
					if(Objects.isNull(buId)){
						boolExp = boolExp.and(qNotificationConfig.buId.isNull());
					}
					return  (List<NotificationConfig>) notificationConfigRepository.findAll(boolExp);
				}
			}
		}
		catch (RuntimeException e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public NotificationConfig addNotificationConfig(String buName, Long mvnoId, String createdBy , Long buId)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(createdBy))
				log.error("CreatedBy value is Missing");
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
			{
				throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(buName))
			{
				throw new IllegalArgumentException("Sms url is mandatory. Please enter valid sms url");
			}
			else
			{
				NotificationConfig notificationConfigVo = new NotificationConfig();
				notificationConfigVo.setMvnoId(mvnoId);
				notificationConfigVo.setBuName(buName);

				notificationConfigVo.setCreateDate(LocalDateTime.now());
				notificationConfigVo.setCreatedBy(createdBy);
				if(Objects.nonNull(buId)){
					notificationConfigVo.setBuId(buId);
				}

				return notificationConfigRepository.save(notificationConfigVo);
			}
		}
		catch (RuntimeException e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public NotificationConfig findNotificationConfigById(Long notificationConfigId, Long mvnoId) {
		try {
			QNotificationConfig qNotificationConfig = QNotificationConfig.notificationConfig;
			BooleanExpression boolExp = qNotificationConfig.isNotNull();
			boolExp = boolExp.and(qNotificationConfig.notificationconfigId.eq(notificationConfigId));
			if(mvnoId != 1)
				boolExp = boolExp.and(qNotificationConfig.mvnoId.in(mvnoId, 1));
			Optional<NotificationConfig> notificationConfig = notificationConfigRepository.findOne(boolExp);
			if (notificationConfig.isPresent()) {
				return notificationConfig.get();
			} else {
				throw new IllegalArgumentException(
						"No record found with sms config id " + notificationConfigId + " . Please enter valid sms config id.");
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
