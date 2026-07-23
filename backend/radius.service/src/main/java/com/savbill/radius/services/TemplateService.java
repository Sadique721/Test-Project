package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.Template;
import com.savbill.radius.helper.TemplateDto;

import javax.servlet.http.HttpServletRequest;

public interface TemplateService {
	List<Template> findAll(Integer mvnoId);
	Template saveTemplate(TemplateDto templateDto, Integer mvnoId);
	List<TemplateDto> udpateTemplate(List<TemplateDto> templateDto, Integer mvnoId, HttpServletRequest request);
	void deleteTemplate(Long templateId, Integer mvnoId);
}
