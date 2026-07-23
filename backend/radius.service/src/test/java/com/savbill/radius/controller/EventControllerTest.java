package com.savbill.radius.controller;

import com.savbill.radius.entity.Event;
import com.savbill.radius.helper.EventDto;
import com.savbill.radius.services.EventService;
import com.savbill.radius.utils.RadiusConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Ignore
public class EventControllerTest {
    @InjectMocks
    EventController eventController;
    @Mock
    EventService eventService;
    @Mock
    APIResponseController apiResponseController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Test
    public void findAllTest(){
        Event getevent=getevent();
        List<Event> eventList=new ArrayList<>();
         eventList.add(getevent);
        Map<String, Object> response = new HashMap<>();
        response.put("eventList",eventList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(eventService.findAllEvents(1)).thenReturn(eventList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=eventController.findAllEvents(1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void findEventByIdTest(){
        Event getevent=getevent();
        Map<String, Object> response = new HashMap<>();
        response.put("event",getevent);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(eventService.findEventById(1L,1)).thenReturn(getevent);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=eventController.findEventById(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void saveEventTest(){
        Event getevent=getevent();
        EventDto eventDto=new EventDto();
        eventDto.setEventType("Parellel");
        eventDto.setEventName("Active");
        eventDto.setStatus("Active");
        Map<String, Object> response = new HashMap<>();
        response.put("event",getevent);
        response.put(RadiusConstants.MESSAGE, "Event has been added successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(eventService.saveEvent(eventDto,1)).thenReturn(getevent);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=eventController.saveEvent(eventDto,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);
    }
    @Test
    public void deleteEventTest(){
        Map<String, Object> response = new HashMap<>();
        response.put(RadiusConstants.MESSAGE, "Event has been deleted successfully.");
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=eventController.deleteEvent(1L,1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    @Test
    public void findByNameTest(){
        Event getevent=getevent();
        List<Event> eventList=new ArrayList<>();
        eventList.add(getevent);
        Map<String, Object> response = new HashMap<>();
        response.put("eventList",eventList);
        ResponseEntity<Map<String, Object>> res=new ResponseEntity<>(response, HttpStatus.OK);
        Mockito.when(eventService.findByName("ADPT",1)).thenReturn(eventList);
        Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(),response)).thenReturn(res);
        ResponseEntity<Map<String, Object>> output=eventController.findByName("ADPT",1,httpServletRequest);
        assertNotNull(output);
        assertEquals(output.getStatusCode().value(),200);

    }
    Event getevent(){
        Event event=new Event();
        event.setEventId(1L);
        event.setEventName("ADPT");
        event.setEventType("Parellel");
        event.setStatus("Active");
        return event;
    }




}
