package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.radius.entity.Event;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.EventDto;
import com.savbill.radius.repository.EventRepository;
import com.savbill.radius.repository.TemplateRepository;
import com.savbill.radius.services.EventService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;

@Service
public class EventServiceImpl implements EventService {
	private static final String SCHEDULE = "Schedule";
	private static final String TRIGGER = "Trigger";
	@Autowired
	EventRepository eventRepository;
	@Autowired
	TemplateRepository templateRepository; 
	
	@Override
    public Event findEventById(Long id, Integer mvnoId) {
	try {

		QEvent qEvent = QEvent.event;
		BooleanExpression boolExp = qEvent.isNotNull();
		boolExp = boolExp.and(qEvent.eventId.eq(id));
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(qEvent.mvnoId.in(mvnoId, 1));
	    Optional<Event> event = eventRepository.findOne(boolExp);
		if (event.isPresent()) {
			return event.get();
	    } else {
			throw new IllegalArgumentException(
				"No record found with event id " + id + " . Please enter valid event id.");
	    }
	} catch (RuntimeException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }
	@Override
	public List<Event> findAllEvents(Integer mvnoId)
	{
		int count;
		try
		{
			List<Event> eventList = new ArrayList<>();
			QEvent qEvent = QEvent.event;
			BooleanExpression exp = qEvent.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				eventList = eventRepository.findAll();
			else {
				exp = exp.and(qEvent.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				eventList = (List<Event>) eventRepository.findAll(exp);
			}

			for(int i=0;i<eventList.size();i++) {
				//List<Template> templateVo = templateRepository.findAll();
				Event eventVo = eventList.get(i);
				count = templateRepository.countByEventEventId(eventVo.getEventId());
				if(count == 0)
				{
					eventVo.setTemplate(null);
					eventList.set(i, eventVo);
				}
			}
			return eventList;
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Event saveEvent(EventDto eventDto, Integer mvnoId)
	{
		try
		{
			Event event = validateEventData(eventDto, mvnoId);
			event.setCreateDate(new Timestamp(new Date().getTime()));
			event.setLastModificationDate(new Timestamp(new Date().getTime()));
			return eventRepository.save(event);
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	private Event validateEventData(EventDto eventDto, Integer mvnoId)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getEventName()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Event name is mandatory. Please enter valid event name.");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getEventType()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Event type is mandatory. Please enter valid event type.");
			}
			else if(!eventDto.getEventType().equals(SCHEDULE) && !eventDto.getEventType().equals(TRIGGER))
			{
				throw new RuntimeException("Please enter valid event type. It should be "+SCHEDULE+" OR "+TRIGGER+".");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(eventDto.getStatus()))
			{
				throw new RuntimeException(RadiusConstants.BASIC_STRING_MSG+"Event status is mandatory. Please enter valid event status.");
			}
			else if(!eventDto.getStatus().equals(RadiusConstants.ACTIVE) && !eventDto.getStatus().equals(RadiusConstants.IN_ACTIVE))
			{
				throw new RuntimeException("Please enter valid event status. It should be "+RadiusConstants.ACTIVE+" OR "+RadiusConstants.IN_ACTIVE+".");
			}
			Event event = new Event(eventDto);
			event.setMvnoId(mvnoId);
			return event;
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Event udpateEvent(EventDto eventDto, Integer mvnoId)
	{
		try
		{
			Event event = validateEventData(eventDto, mvnoId);
			Optional<Event> optionalEvent = eventRepository.findByEventName(eventDto.getEventName());
			if(!optionalEvent.isPresent())
			{
				throw new RuntimeException("No record found with event name '"+eventDto.getEventName()+"', Please enter valid event name to update the event record.");
			}
			else
			{
				event.setEventId(optionalEvent.get().getEventId());
				event.setCreateDate(optionalEvent.get().getCreateDate());
				event.setLastModificationDate(new Timestamp(new Date().getTime()));
				return eventRepository.save(event);
			}
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void deleteEvent(Long eventId, Integer mvnoId)
	{
		try {
			QEvent qEvent = QEvent.event;
			BooleanExpression boolExp = qEvent.isNotNull();
			boolExp = boolExp.and(qEvent.eventId.eq(eventId));
			if (mvnoId != 1)
				boolExp = boolExp.and(qEvent.mvnoId.in(mvnoId, 1));
			Optional<Event> event = eventRepository.findOne(boolExp);

			if (event.isPresent()) {
				eventRepository.deleteById(eventId);
			} else {
				throw new IllegalArgumentException(
						"No record found with event id " + eventId + " . Please enter valid event id.");
			}
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<Event> findByName(String name, Integer mvnoId)
	{
		try
		{
		    if(StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
		    {
			return eventRepository.findAll();
		    }

			QEvent qEvent = QEvent.event;
			BooleanExpression boolExp = qEvent.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qEvent.mvnoId.in(mvnoId, 1));
			boolExp = boolExp.and(qEvent.eventName.contains(name));
			return (List<Event>) eventRepository.findAll(boolExp);
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}
