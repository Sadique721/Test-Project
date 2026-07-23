package com.savbill.notification.services;

import java.util.List;
import java.util.Map;

import com.savbill.notification.entity.Sms;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.entity.SmsReceiverEventTempBinding;
import com.savbill.notification.entity.Template;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface SmsService 
{
	List<SmsDataDTO> findSmsBySourceName(Long eventId, String status, String mobileNo, Long mvnoId);
	Sms findSmsById(Long id,Long mvnoId, Boolean isDelete);
	PageableResponse<SmsDataDTO> findAllSmss(Long eventId, String status, String mobileNo, Long mvnoId, PaginationDTO paginationDTO, List<Long> buIdList);
	void deleteSmsById(Long id,Long mvnoId);
	Sms saveSms(SmsDto sms, Long mvnoId, Long aLong);
	Sms updateSms(UpdateSmsDto updateSmsDto, Long mvnoId, Long buId, HttpServletRequest request);
	void sendSms(Long id, Long mvnoId,Long buId,HttpServletRequest request) throws Exception;
	void sendSmsNotification(String queueName, String message, Map<String, Object> data,String sourceName,String smsTemplate,String appendUrl) throws Exception;

    void sendSmsIwf(SmsReceiverEventTempBinding smsReceiverEventTempBinding, SmsConfig smsConfigDetails, Template templateDetails) throws Exception;
	Page<SmsDataDTO>searchSmsAudit(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType);

    boolean validation(PaginationRequestDTO requestDTO);
}
