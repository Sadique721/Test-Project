package com.savbill.notification.services.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.savbill.notification.BusinessUnit.domain.BusinessUnit;
import com.savbill.notification.BusinessUnit.domain.QBusinessUnit;
import com.savbill.notification.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.entity.TemplatePojo;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.utils.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.notification.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.notification.helper.TemplateDto;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.TemplateRepository;
import com.savbill.notification.services.TemplateService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final Logger log = Logger.getLogger(TemplateServiceImpl.class);
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    BusinessUnitRepository businessUnitRepository;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;

    @Override
    public List<Template> findAll() {
        try {
            return templateRepository.findAll();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Template saveTemplate(TemplateDto templateDto) {
        try {
            Template templateVo = validateEventId(templateDto);
            validateTemplateData(templateDto);
            Integer count = templateRepository.countByEventEventId(templateDto.getEventId());
            if (count == 1) {
                throw new RuntimeException("You can not create template with event id : '" + templateDto.getEventId() + "', Because it is already used by another tempalte.");
            }
            templateVo.setCreateDate(new Timestamp(new Date().getTime()));
            templateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
            if (templateVo.getSmsTemplateData().equals(NotificationConstants.BLANK_STRING)) {
                templateVo.setSmsTemplateData(null);
            }
            if (templateVo.getEmailTemplateData().equals(NotificationConstants.BLANK_STRING)) {
                templateVo.setEmailTemplateData(null);
            }
            return templateRepository.save(templateVo);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<TemplateDto> udpateTemplate(List<TemplateDto> templateDtos,HttpServletRequest request) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        try {
            for (int i = 0; i < templateDtos.size(); i++) {
                TemplateDto templateDto = templateDtos.get(i);
                Template emailTemplateVo = validateEventId(templateDto);
                emailTemplateVo.setStatus(NotificationConstants.ACTIVE);
                validateTemplateData(templateDto);
                Optional<Template> optionalTemplate = templateRepository.findByTemplateName(templateDto.getTemplateName());
//			if(!optionalTemplate.isPresent())
//			{
//				throw new RuntimeException("No record found with template name '"+templateDto.getTemplateName()+"', Please enter valid template name to update the template record.");
//			}
//			else
//			{
//				Integer count = templateRepository.countByEventEventId(templateDto.getEventId()); 
//				if(count == 1 && optionalTemplate.get().getEvent().getEventId() != templateDto.getEventId())
//				{
//					throw new RuntimeException("You can not update template with event id : '"+templateDto.getEventId()+"', Because it is already used by another tempalte.");
//				}

                emailTemplateVo.setTemplateId(optionalTemplate.get().getTemplateId());
                emailTemplateVo.setCreateDate(optionalTemplate.get().getCreateDate());
                emailTemplateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
                String updated=updateDiffFinder.getUpdatedDiff(optionalTemplate.get(),emailTemplateVo);
                log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Template has been updated successfully: updated value "+updated + templateDtos.get(0).getTemplateName() != null ? templateDtos.get(0).getTemplateName() : null + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + ","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                templateRepository.save(emailTemplateVo);
            }
            return templateDtos;
        } catch (Throwable e) {
//            log.error("Error to update template: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    private void validateTemplateData(TemplateDto templateDto) {
        try {
            if (templateDto.isEmailEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getEmailTemplateData())) {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG + "Email template data is mandatory. Please enter valid email template data.");
            } else if (templateDto.isSmsEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getSmsTemplateData())) {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG + "Sms template data is mandatory. Please enter valid sms template data.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getTemplateName())) {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG + "Template name is mandatory. Please enter valid template name.");
            }
//			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getStatus()))
//			{
//				throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Template status is mandatory. Please enter valid template status.");
//			}
//			else if(!templateDto.getStatus().equals(NotificationConstants.ACTIVE) && !templateDto.getStatus().equals(NotificationConstants.IN_ACTIVE))
//			{
//				throw new RuntimeException("Please enter valid template status. It should be "+NotificationConstants.ACTIVE+" OR "+NotificationConstants.IN_ACTIVE+".");
//			}
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Template validateEventId(TemplateDto templateDto) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(templateDto.getEventId())) {
                throw new RuntimeException(NotificationConstants.BASIC_NUMERIC_MSG + "Event id is mandatory. Please enter valid event id.");
            } else {
                Optional<Event> optionalEvent = eventRepository.findById(templateDto.getEventId());
                if (!optionalEvent.isPresent())
                    throw new RuntimeException("No record found with event id : '" + templateDto.getEventId() + "'");
                else
                    return new Template(templateDto, optionalEvent.get());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteTemplate(Long templateId) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        try {
            templateRepository.deleteById(templateId);
//            System.out.println("Template has been deleted successfully: " + templateId);
        } catch (Throwable e) {
//            log.error("Error to delete template: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @Override
    public Template updateTemplateById(TemplateDto templateDto, HttpServletRequest request, Long id) throws CustomException {

        try {
            Template templateForUpdate = templateRepository.findByTemplateId(id);
            Template old =templateForUpdate;
            templateDto.setEventId(templateForUpdate.getEvent().getEventId());
            Template emailTemplateVo = validateEventId(templateDto);
            emailTemplateVo.setStatus(NotificationConstants.ACTIVE);
            validateTemplateData(templateDto);
            boolean isValidTemplate = validateUpdateData(templateForUpdate,request);
            if(isValidTemplate){
            templateForUpdate.setEmailTemplateData(templateDto.getEmailTemplateData());
            templateForUpdate.setSmsTemplateData(templateDto.getSmsTemplateData());
            templateForUpdate.setAppendUrl(templateDto.getAppendUrl());
            templateForUpdate.setEmailEventConfigured(templateDto.isEmailEventConfigured());
            templateForUpdate.setSmsEventConfigured(templateDto.isSmsEventConfigured());
            templateForUpdate.setStatus(NotificationConstants.ACTIVE);
            templateForUpdate.setLastModificationDate(new Timestamp(new Date().getTime()));

                System.out.println(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  " + updateDiffFinder.getUpdatedDiff(old,templateForUpdate)+ " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
         //   System.out.println("Template has been updated successfully: " + templateDto.getTemplateName());
            templateRepository.save(templateForUpdate);
            }
            return templateForUpdate;
        }
        catch (CustomException exception){
            throw exception;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean validateUpdateData(Template templateDto, HttpServletRequest request) throws IOException, CustomException {
        Long loggedInUserMvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));
        if (templateDto.getMvnoId() == 1 && loggedInUserMvnoId != templateDto.getMvnoId().longValue()) {
            throw new CustomException("Permission denied to update this template", CommonConstants.EXPECTATION_FAILED);
        }
        List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
        if (buIdList.size() > 1) {
            throw new CustomException("You are not allowed to perform this action, Please contact your system administrator", CommonConstants.EXPECTATION_FAILED);
        }
        return true;
    }


    @Override
    public List<TemplatePojo> findAllByMvnoIdAndBuId(Long usermvnoid, List<Long> buidlist) {
        try {
            List<TemplatePojo> templatePojos = new ArrayList<>();
            TemplatePojo templatePojo = new TemplatePojo();
            QTemplate qTemplate = QTemplate.template;
            BooleanExpression booleanExpression = null;
            if(!buidlist.isEmpty()){
                booleanExpression = qTemplate.isNotNull().and(qTemplate.mvnoId.eq(Math.toIntExact(usermvnoid))).and(qTemplate.buId.in(buidlist));
            }else{
                booleanExpression = qTemplate.isNotNull().and(qTemplate.mvnoId.eq(Math.toIntExact(usermvnoid)));
            }

            JPAQuery<TemplatePojo> query = new JPAQuery<>(entityManager);
            QTemplate t = QTemplate.template;
            QBusinessUnit b = QBusinessUnit.businessUnit;
            QEvent e = QEvent.event;

            templatePojos = query.select(Projections.constructor(TemplatePojo.class,
                            t.templateId,
                            t.templateName,
                            t.emailTemplateData,
                            t.smsTemplateData,
                            e.eventName,
                            t.emailEventConfigured,
                            t.smsEventConfigured,
                            t.buId,
                            t.mvnoId,
                            t.appendUrl,
                            t.status,
                            b.buname))
                    .from(t)
                    .leftJoin(b).on(t.buId.eq(b.id))
                    .leftJoin(e).on(t.event.eq(e))
                    .where(booleanExpression)
                    .fetch();

            return templatePojos;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

	@Override
	public String findTemplateByEventMvnoBU(Event event, Integer mvnoId, Integer buId, Boolean isForEmailRequest) {
		try{
			//Template template = templateRepository.findTemplatesByMvnoBuAndEvent(mvnoId,buId,event).orElse(null);
            QTemplate qTemplate = QTemplate.template;
            BooleanExpression booleanExpression;
            if(buId!=null){
                 booleanExpression = qTemplate.isNotNull().and(qTemplate.event.eventId.eq(event.getEventId())).and(qTemplate.mvnoId.eq(mvnoId)).and(qTemplate.buId.eq(Long.valueOf(buId)));
            }else{
                booleanExpression = qTemplate.isNotNull().and(qTemplate.event.eventId.eq(event.getEventId())).and(qTemplate.mvnoId.eq(mvnoId).and(qTemplate.buId.isNull()));
            }

            Template template = templateRepository.findOne(booleanExpression).orElse(null);
			if(template!=null) {
				if (isForEmailRequest) {
					return template.getEmailTemplateData();
				} else {
					return template.getSmsTemplateData();
				}
			}else{
                return null;
            }
		}catch (Exception e){
			log.error("Template fetching error : "+e.getMessage());
		}
		return null;
	}

    @Override
    public List<TemplatePojo> findAllByMvnoIdAndBuIdAndTemplatename(Long usermvnoid, List<Long> buidlist, String templateName) {
        try {
            List<TemplatePojo> templatePojos = new ArrayList<>();
            TemplatePojo templatePojo = new TemplatePojo();
            QTemplate qTemplate = QTemplate.template;
            BooleanExpression booleanExpression = null;
            if(!buidlist.isEmpty()){
                booleanExpression = qTemplate.isNotNull().and(qTemplate.mvnoId.eq(usermvnoid.intValue()).and(qTemplate.buId.in(buidlist)).and(qTemplate.templateName.likeIgnoreCase("%"+ templateName +"%")));
            }else{
                booleanExpression = qTemplate.isNotNull().and(qTemplate.mvnoId.eq(usermvnoid.intValue()).and(qTemplate.templateName.likeIgnoreCase("%"+ templateName +"%")));
            }
            List<Template> templates = (List<Template>) templateRepository.findAll(booleanExpression);
            for (Template template : templates) {
                if (template.getBuId() != null) {
                    BusinessUnit businessUnit = businessUnitRepository.findById(template.getBuId()).orElse(null);
                    template.setBuName(businessUnit.getBuname());
                    templatePojos.add(convertIntoDTO(template));
                } else {
                    template.setBuName("");
                    templatePojos.add(convertIntoDTO(template));
                }
            }
            return templatePojos;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Template getAllTemplateByMvnoAndBuAndEvent(Event event , Integer mvnoId , Integer buId){
        QTemplate qTemplate = QTemplate.template;
        BooleanExpression booleanExpression;
        if(buId!=null){
            booleanExpression = qTemplate.isNotNull().and(qTemplate.event.eventId.eq(event.getEventId())).and(qTemplate.mvnoId.eq(mvnoId)).and(qTemplate.buId.eq(Long.valueOf(buId)));
        }else{
            booleanExpression = qTemplate.isNotNull().and(qTemplate.event.eventId.eq(event.getEventId())).and(qTemplate.mvnoId.eq(mvnoId).and(qTemplate.buId.isNull()));
        }

        Template template = templateRepository.findOne(booleanExpression).orElse(null);

        return template;
    }



    public TemplatePojo convertIntoDTO(Template template){
        TemplatePojo templatePojo = new TemplatePojo();

        templatePojo.setTemplateId(template.getTemplateId());
        templatePojo.setTemplateName(template.getTemplateName());
        templatePojo.setEmailTemplateData(template.getEmailTemplateData());
        templatePojo.setSmsTemplateData(template.getSmsTemplateData());
        templatePojo.setEventName(template.getEvent().getEventName());
        templatePojo.setEmailEventConfigured(template.isEmailEventConfigured());
        templatePojo.setSmsEventConfigured(template.isSmsEventConfigured());
        templatePojo.setBuId(template.getBuId());
        templatePojo.setMvnoId(templatePojo.getMvnoId());
        templatePojo.setAppendUrl(template.getAppendUrl());
        templatePojo.setCreateDate(template.getCreateDate());
        templatePojo.setLastModificationDate(template.getLastModificationDate());
        templatePojo.setStatus(template.getStatus());
        templatePojo.setBuName(template.getBuName());

        return  templatePojo;
    }


}
