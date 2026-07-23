package com.savbill.notification.controller;

import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.EventDto;
import com.savbill.notification.services.EventService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class EventControllerTest{
	@InjectMocks
	EventController eventController;
	@Mock
	EventService eventService;
	@Mock
	APIResponseController apiResponseController;
	@Mock
	HttpServletRequest httpServletRequest;
	@Mock
	TokenDataExtractor tokenDataExtractor;
	@Mock
	ApiDataValidator apiDataValidator;


	@Test
	public void findEventByIdTest() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		Map<String, Object> response = new HashMap<>();
		response.put("event",event);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findEventById(1L)).thenReturn(event);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.findEventById(request,1L);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 200);
	}
	@Test
	//@Ignore
	public void findEventByIdTestException() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		Map<String, Object> response = new HashMap<>();
		response.put("event",event);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findEventById(1L)).thenReturn(event);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
		Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
		NullPointerException exception=new NullPointerException();
		response.put(NotificationConstants.ERROR_MESSAGE,exception.getMessage());
		ResponseEntity<Map<String, Object>> output =eventController.findEventById(request,1L);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 400);
	}
	@Test
	public void findAllEventsTest() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		List<Event> eventList=new ArrayList<>();
		eventList.add(event);
		Map<String, Object> response = new HashMap<>();
		response.put("eventList",eventList);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findAllEvents(1L)).thenReturn(eventList);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.findAllEvents(1L,request);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 200);
	}
	@Test
	public void findAllEventsTestException() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		List<Event> eventList=new ArrayList<>();
		eventList.add(event);
		Map<String, Object> response = new HashMap<>();
		response.put("eventList",eventList);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findAllEvents(1L)).thenReturn(eventList);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
		Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.findAllEvents(1L,request);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 400);
	}
	@Test
	public void saveEventTest() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		EventDto eventDto=geteventDto();
		Map<String, Object> response = new HashMap<>();
		response.put("event",event);
		response.put(NotificationConstants.MESSAGE, "Event has been added successfully.");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
		Mockito.when(eventService.saveEvent(eventDto)).thenReturn(event);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.saveEvent(eventDto,httpServletRequest);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 200);
	}
	@Test
	public void saveEventTestException() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		EventDto eventDto=geteventDto();
		Map<String, Object> response = new HashMap<>();
		response.put("event",event);
		response.put(NotificationConstants.MESSAGE, "Event has been added successfully.");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
		Mockito.when(eventService.saveEvent(eventDto)).thenReturn(event);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
		Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.saveEvent(eventDto,httpServletRequest);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 400);
	}
	@Test
	public void deleteEventTest() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Map<String, Object> response = new HashMap<>();
		response.put(NotificationConstants.MESSAGE, "Event has been deleted successfully.");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.deleteEvent(httpServletRequest,1L,1L);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 200);
	}
	@Test
	public void deleteEventTestException() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Map<String, Object> response = new HashMap<>();
		response.put(NotificationConstants.MESSAGE, "Event has been deleted successfully.");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		Mockito.when(tokenDataExtractor.getUsermvnoId(httpServletRequest)).thenReturn(userMvnoId);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
		Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.deleteEvent(httpServletRequest,1L,1L);
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 400);
	}
	@Test
	public void findByNameTest() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		List<Event> eventList=new ArrayList<>();
		eventList.add(event);
		Map<String, Object> response = new HashMap<>();
		response.put("eventList",eventList);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findByName("admin")).thenReturn(eventList);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.findByName(request,"admin");
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 200);
	}
	@Test
	public void findByNameTestException() throws AuthException, CustomException, IOException {
		Long userMvnoId=11L;
		Event event=getevent();
		List<Event> eventList=new ArrayList<>();
		eventList.add(event);
		Map<String, Object> response = new HashMap<>();
		response.put("eventList",eventList);
		MockHttpServletRequest request=new MockHttpServletRequest();
		request.setRequestURI("/foo");
		ResponseEntity<Map<String, Object>> res = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		Mockito.when(tokenDataExtractor.getUsermvnoId(request)).thenReturn(userMvnoId);
		Mockito.when(eventService.findByName("admin")).thenReturn(eventList);
		Mockito.when(apiResponseController.apiResponse(HttpStatus.OK.value(), response)).thenThrow(NullPointerException.class);
		Mockito.when(apiResponseController.apiResponse(NotificationConstants.FAIL,response)).thenReturn(res);
		ResponseEntity<Map<String, Object>> output =eventController.findByName(request,"admin");
		assertNotNull(output);
		assertEquals(output.getStatusCode().value(), 400);
	}

	Event getevent(){
		Event event=new Event();
		event.setEventId(1L);
		event.setEventName("test");
		event.setEventType("test");
		event.setMvnoId(1L);
		event.setDescription("Success");
		List<Template> template=new ArrayList<>();
		event.setTemplate(template);
		event.setStatus("Active");
		Timestamp timestamp =Timestamp.valueOf("2007-09-23 10:10:10.0");
		event.setCreateDate(timestamp);
		event.setLastModificationDate(timestamp);
		return event;
	}

	EventDto geteventDto(){
		EventDto eventDto=new EventDto();
		eventDto.setEventName("test");
		eventDto.setEventType("test");
		eventDto.setDescription("Success");
		Template template=new Template();
		eventDto.setStatus("Active");
		return eventDto;
	}
}
