package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.helper.SmsConfigMappingDto;

import javax.servlet.http.HttpServletRequest;


public interface SmsConfigMappingService 
{
    List<SmsConfigMapping> findSmsConfigMappingBySmsConfigId(Long smsConfigId,Long mvnoId);
    List<SmsConfigMapping> findAllSmsConfigMapping(Long mvnoId);
    void deleteSmsConfigMappingById(Long id,Long mvnoId);
    List<SmsConfigMapping> saveSmsConfigMapping(List<SmsConfigMappingDto> smsConfigMappingDtoList,Long mvnoId);
    List<SmsConfigMapping> updateSmsConfigMapping(List<SmsConfigMappingDto> smsConfigMappingDtoList, Long mvnoId, Long smsConfigId, HttpServletRequest request);
}
