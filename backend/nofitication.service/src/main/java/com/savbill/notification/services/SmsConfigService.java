package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.SearchSmsRespDto;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface SmsConfigService 
{
	SmsConfig updateSmsConfig(SmsConfig smsConfig, Long mvnoId, HttpServletRequest request);
	List<SmsConfig> findAllSmsConfig(Long mvnoId, Long buId, String serviceType);
	SmsConfig addSmsConfig(String smsUrl, Long mvnoId, String createdBy , Long buId , Boolean configStatus, String serviceType) throws CustomException;
	SmsConfig findSmsConfigById(Long smsConfigId, Long mvnoId);
	Page<SmsConfig> getSmsConfigWithPagination(Integer page, Integer size, Long mvnoId, Long buIds, String serviceType);
	Page<SearchSmsRespDto> SerchSmsConfig(String smsUrl, Long mvnoId, Integer page, Integer size);
	Page<SearchSmsRespDto> SmsConfig(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType);

    boolean validation(PaginationRequestDTO requestDTO);
}
