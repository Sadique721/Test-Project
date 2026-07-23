package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.utils.*;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.Event;
import com.savbill.radius.entity.QEvent;
import com.savbill.radius.entity.QTemplate;
import com.savbill.radius.entity.Template;
import com.savbill.radius.helper.TemplateDto;
import com.savbill.radius.repository.EventRepository;
import com.savbill.radius.repository.TemplateRepository;
import com.savbill.radius.services.TemplateService;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.servlet.http.HttpServletRequest;

@Service
public class TemplateServiceImpl implements TemplateService{
	private static final Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	EventRepository eventRepository;
	@Autowired
    UpdateDiffFinder updateDiffFinder;

	@Override
	public List<Template> findAll(Integer mvnoId)
	{
		try {
			QTemplate qTemplate = QTemplate.template;
			BooleanExpression exp = qTemplate.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return templateRepository.findAll();
			else {
				exp = exp.and(qTemplate.mvnoId.in(mvnoId, 1));
				return (List<Template>) templateRepository.findAll(exp);
			}

		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Template saveTemplate(TemplateDto templateDto, Integer mvnoId)
	{
		try
		{
			templateDto.setMvnoId(mvnoId);
			Template templateVo = validateEventId(templateDto);
			templateVo.setMvnoId(mvnoId);
			validateTemplateData(templateDto, false);
			Integer count = templateRepository.countByEventEventId(templateDto.getEventId()); 
			if(count == 1)
			{
				throw new RuntimeException("You can not create template with event id : '"+templateDto.getEventId()+"', Because it is already used by another tempalte.");
			}
			templateVo.setCreateDate(new Timestamp(new Date().getTime()));
			templateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
			if(templateVo.getSmsTemplateData().equals(RadiusConstants.BLANK_STRING))
			{
				templateVo.setSmsTemplateData(null);
			}
			if(templateVo.getEmailTemplateData().equals(RadiusConstants.BLANK_STRING))
			{
				templateVo.setEmailTemplateData(null);
			}
			return templateRepository.save(templateVo);
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public List<TemplateDto> udpateTemplate(List<TemplateDto> templateDtos, Integer mvnoId, HttpServletRequest request)
	{
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		try
		{
			List<Template> templateList = new ArrayList<>();
			for (TemplateDto templateDto : templateDtos)
			{
				Template emailTemplateVo = validateEventId(templateDto);
				validateTemplateData(templateDto, true);
				Template template = findByTemplateName(templateDto.getTemplateName(), templateDto.getMvnoId());
				String updated = updateDiffFinder.getUpdatedDiff(template, emailTemplateVo);
				emailTemplateVo.setStatus(RadiusConstants.ACTIVE);
				emailTemplateVo.setTemplateId(template.getTemplateId());
				emailTemplateVo.setCreateDate(template.getCreateDate());
				emailTemplateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
				if(mvnoId == 1)
				{
					emailTemplateVo.setMvnoId(template.getMvnoId());
				}
				else
				{
					emailTemplateVo.setMvnoId(mvnoId);
				}
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Template has been updated succesfully successfully with values" +updated+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
				templateList.add(emailTemplateVo);
			}
			templateRepository.saveAll(templateList);
			return templateDtos;
		}
		catch(Throwable e)
		{
			log.error("Error to update template: "+e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	private void validateTemplateData(TemplateDto templateDto, Boolean isUpdate)
	{
		try
		{
			if(templateDto.isEmailEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getEmailTemplateData()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Email template data is mandatory. Please enter valid email template data.");
			}
			else if(templateDto.isSmsEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getSmsTemplateData()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Sms template data is mandatory. Please enter valid sms template data.");
			} 
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getTemplateName()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Template name is mandatory. Please enter valid template name.");
			}
			if(!isUpdate) {
				QTemplate qTemplate = QTemplate.template;
				BooleanExpression boolExp = qTemplate.isNotNull();
				boolExp = boolExp.and(qTemplate.templateName.eq(templateDto.getTemplateName()));
				if (templateDto.getMvnoId() != 1)
					boolExp = boolExp.and(qTemplate.mvnoId.eq(templateDto.getMvnoId()));
				List<Template> templateList = (List<Template>) templateRepository.findAll(boolExp);
				if(templateList.size() > 0)
					throw new RuntimeException("Duplicate template name '"+templateDto.getTemplateName()+"'. Record already present with this template name.");
			}
//			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getStatus()))
//			{
//				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Template status is mandatory. Please enter valid template status.");
//			}
//			else if(!templateDto.getStatus().equals(RadiusConstants.ACTIVE) && !templateDto.getStatus().equals(RadiusConstants.IN_ACTIVE))
//			{
//				throw new RuntimeException("Please enter valid template status. It should be "+RadiusConstants.ACTIVE+" OR "+RadiusConstants.IN_ACTIVE+".");
//			}
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private Template validateEventId(TemplateDto templateDto) 
	{
		try
		{
			if(!ValidateCrudTransactionData.validateLongTypeFieldValue(templateDto.getEventId()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_NUMERIC_MSG+"Event id is mandatory. Please enter valid event id.");
			}
			else
			{
				QEvent qEvent = QEvent.event;
				BooleanExpression boolExp = qEvent.isNotNull();
				boolExp = boolExp.and(qEvent.eventId.eq(templateDto.getEventId()));
				if(templateDto.getMvnoId() == null || templateDto.getMvnoId() != 1)
					boolExp = boolExp.and(qEvent.mvnoId.eq(templateDto.getMvnoId()));
				Optional<Event> optionalEvent = eventRepository.findOne(boolExp);
				if(!optionalEvent.isPresent())
					throw new RuntimeException("No record found with event id : '"+templateDto.getEventId()+"'");
				else
					return new Template(templateDto,optionalEvent.get());
			}
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteTemplate(Long templateId, Integer mvnoId)
	{
		try
		{
			QTemplate qTemplate = QTemplate.template;
			BooleanExpression exp = qTemplate.isNotNull();
			if(mvnoId != 1)
				exp = exp.and(qTemplate.mvnoId.eq(mvnoId));
			exp = exp.and(qTemplate.templateId.eq(templateId));

			Optional<Template> optionalTemplate = templateRepository.findOne(exp);
			if(optionalTemplate.isPresent())
				templateRepository.delete(optionalTemplate.get());
			else
				throw new RuntimeException("Cannot delete record with id '"+templateId+"' because it does not exist");
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private Template findByTemplateName(String name, Integer mvnoId) {
		if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
			throw new IllegalArgumentException("Please enter valid template name.");
		QTemplate qTemplate = QTemplate.template;
		BooleanExpression boolExp = qTemplate.isNotNull();
		boolExp = boolExp.and(qTemplate.templateName.eq(name));
		if(mvnoId != 1)
			boolExp = boolExp.and(qTemplate.mvnoId.eq(mvnoId));
		Optional<Template> templateOptional = templateRepository.findOne(boolExp);
		if (!templateOptional.isPresent()) {
			throw new IllegalArgumentException(
					"No record found with template name " + name + " . Please enter valid template name.");
		}
		return templateOptional.get();
	}
}
