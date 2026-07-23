package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.entity.TemplatePojo;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.TemplateDto;

import javax.servlet.http.HttpServletRequest;

public interface TemplateService
{
	List<Template> findAll();
	Template saveTemplate(TemplateDto templateDto);
	List<TemplateDto> udpateTemplate(List<TemplateDto> templateDt,HttpServletRequest request);
	void deleteTemplate(Long templateId);

	List<TemplatePojo> findAllByMvnoIdAndBuId(Long usermvnoid, List<Long> buidlist);
	Template updateTemplateById(TemplateDto templateDto, HttpServletRequest request, Long id) throws CustomException;

	public String findTemplateByEventMvnoBU(Event eventName, Integer mvnoId, Integer buId, Boolean isForEmailRequest);

	List<TemplatePojo> findAllByMvnoIdAndBuIdAndTemplatename(Long usermvnoid, List<Long> buidlist, String templateName);

	Template getAllTemplateByMvnoAndBuAndEvent(Event event , Integer mvnoId , Integer buId);

}
