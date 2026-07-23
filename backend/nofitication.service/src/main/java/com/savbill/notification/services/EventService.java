package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.Event;
import com.savbill.notification.helper.EventDto;

public interface EventService
{
	List<Event> findAllEvents(Long mvnoId);
	Event saveEvent(EventDto eventDto);
	Event udpateEvent(EventDto eventDto);
	void deleteEvent(Long eventId);
	List<Event> findByName(String name);
	Event findEventById(Long id);
}
