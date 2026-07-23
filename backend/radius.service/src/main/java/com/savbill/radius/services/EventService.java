package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.Event;
import com.savbill.radius.helper.EventDto;

public interface EventService {
	List<Event> findAllEvents(Integer mvnoId);
	Event saveEvent(EventDto eventDto, Integer mvnoId);
	Event udpateEvent(EventDto eventDto, Integer mvnoId);
	void deleteEvent(Long eventId, Integer mvnoId);
	List<Event> findByName(String name, Integer mvnoId);
	Event findEventById(Long id, Integer mvnoId);
}
