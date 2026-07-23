package com.savbill.notification.savbilliwfnotification.service.impl;

import com.savbill.notification.savbilliwfnotification.dto.EventTemplateBindingDTO;
import com.savbill.notification.savbilliwfnotification.service.IwfEventTempBindService;
import com.savbill.notification.savbilliwfnotification.uploadfile.FileUtility;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsConfigEventTempBinding;
import com.savbill.notification.entity.SmsReceiverEventTempBinding;
import com.savbill.notification.entity.Template;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.GenericSearchModel;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.StaffCustomDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTempBindSearchDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTemplateDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.SMSConfTempBindSearchDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.SMSReceiveSearchDTO;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.SmsConfigEventTempBindingRepository;
import com.savbill.notification.repository.SmsReceiverEventTempBindingRepository;
import com.savbill.notification.repository.TemplateRepository;
import com.savbill.notification.services.SmsConfigEventTempBindingService;
import com.savbill.notification.services.SmsReceiverEventTempBindingService;
import com.savbill.notification.utils.CommonConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IwfEmailTempBindServiceImpl implements IwfEventTempBindService {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    FileUtility fileUtility;

    @Autowired
    SmsConfigEventTempBindingService smsConfigEventTempBindingService;

    @Autowired
    SmsReceiverEventTempBindingService smsReceiverEventTempBindingService;

    @Autowired
    SmsReceiverEventTempBindingRepository smsReceiverEventTempBindingRepository;

    @Autowired
    SmsConfigEventTempBindingRepository smsConfigEventTempBindingRepository;

    @Autowired
    TokenDataExtractor tokenDataExtractor;

    /**
     * Save Event Template Binding
     *
     * @param eventTemplateBindingDTO
     * @param request
     * @return
     */
    @Override
    @Transactional
    public EventTemplateBindingDTO saveEventTempBind(EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException {
        String encodeToken = request.getHeader("Authorization");
        Long usermvnoid = tokenDataExtractor.getMvnoId(encodeToken);
        /** To validate Event Name is Exist or Not*/
        List<Event> eventList = eventRepository.findAll();
        if (eventList.stream().anyMatch(event1 ->
                event1.getEventName().trim().equals(eventTemplateBindingDTO.getEventName().trim())
                        && event1.getIsDelete().equals(false))) {
            throw new RuntimeException("Event Name is already exist!");
        }
        /** To validate Template Name is Exist or Not*/
        List<Template> templateList = templateRepository.findAll();
        if (templateList.stream().anyMatch(template1 ->
                template1.getTemplateName().trim().equals(eventTemplateBindingDTO.getTemplateName().trim())
                        && template1.getIsDelete().equals(false))) {
            throw new RuntimeException("Template Name is already exist!");
        }
        /** Save the value in Event Table*/
        Event event = setEventData(null, eventTemplateBindingDTO, CommonConstants.OPERATION.OPERATION_ADD, usermvnoid);
        if (event != null) {
            try {
                if (eventTemplateBindingDTO.getIsSMSTemplate()) {
                    smsConfigEventTempBindingService.saveSmsConfigEventTempBinding(eventTemplateBindingDTO.getSmsConfigIdsList(), event.getEventId());
                    smsReceiverEventTempBindingService.saveSmsReceiverEventTempBinding(eventTemplateBindingDTO.getStaffDtoList(), event.getEventId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /** Save the value in Template Table*/
        Template template = setTemplateData(eventTemplateBindingDTO, event, CommonConstants.OPERATION.OPERATION_ADD, usermvnoid);
        return eventTemplateBindingDTO;
    }

    /**
     * Update Event Template Binding
     *
     * @param eventId
     * @param eventTemplateBindingDTO
     * @param request
     * @return
     */
    @Override
    @Transactional
    public EventTemplateBindingDTO updateEventTempBind(Long eventId, EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException {
        String encodeToken = request.getHeader("Authorization");
        Long usermvnoid = tokenDataExtractor.getMvnoId(encodeToken);
        /** To validate Event Id*/
        if (eventId == null) {
            throw new RuntimeException("Event Id is not present!!");
        }
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        /** To validate permission denied*/
        if (usermvnoid != null && eventOptional != null && usermvnoid != 1L && usermvnoid != eventOptional.get().getMvnoId()) {
            throw new RuntimeException("Permission Denied. Unable to update/delete this record");
        }
        /** To validate Event Id is Exist or Not*/
        if (!eventOptional.isPresent()) {
            throw new RuntimeException("Event does not exist!");
        }
        List<Event> eventList = eventRepository.findAll();
        /** To validate Event Name is Exist or Not*/
        if (eventList.stream().anyMatch(event ->
                event.getEventName().trim().equalsIgnoreCase(eventTemplateBindingDTO.getEventName().trim())
                        && event.getEventId() != eventId
                        && event.getIsDelete().equals(false))) {
            throw new RuntimeException(eventTemplateBindingDTO.getEventName() +
                    " event name is already associated with a different Event ID!");
        }
        /** To validate Template Name is Exist or Not*/
        List<Template> templateList = templateRepository.findAll();
        if (templateList.stream().anyMatch(template ->
                template.getTemplateName().trim().equalsIgnoreCase(eventTemplateBindingDTO.getTemplateName().trim())
                        && template.getEvent().getEventId() != eventId
                        && template.getIsDelete().equals(false))) {
            throw new RuntimeException(eventTemplateBindingDTO.getTemplateName() +
                    " template name is already associated with a different Event ID!");
        }
        /** Save the value in Event Table*/
        Event event = setEventData(eventId, eventTemplateBindingDTO, CommonConstants.OPERATION.OPERATION_UPDATE, usermvnoid);
        if (event != null) {
            try {
                if (eventTemplateBindingDTO.getIsSMSTemplate()) {
                    smsConfigEventTempBindingService.saveSmsConfigEventTempBinding(eventTemplateBindingDTO.getSmsConfigIdsList(), event.getEventId());
                    smsReceiverEventTempBindingService.saveSmsReceiverEventTempBinding(eventTemplateBindingDTO.getStaffDtoList(), event.getEventId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /** Save the value in Template Table*/
        Template template = setTemplateData(eventTemplateBindingDTO, event, CommonConstants.OPERATION.OPERATION_UPDATE, usermvnoid);
        return eventTemplateBindingDTO;
    }

    /**
     * Remove/ Delete Event Template Bind
     *
     * @param id
     * @param request
     */
    @Override
    public void removeEventTemplateBind(Long id, HttpServletRequest request) throws IOException {
        String encodeToken = request.getHeader("Authorization");
        Long usermvnoid = tokenDataExtractor.getMvnoId(encodeToken);
        Optional<Event> eventOptional = eventRepository.findById(id);
        /** To validate permission denied*/
        if (usermvnoid != null && eventOptional != null && usermvnoid != 1L && usermvnoid != eventOptional.get().getMvnoId()) {
            throw new RuntimeException("Permission Denied. Unable to update/delete this record");
        }
        Optional<Template> templateOptional = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(id, NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getIsDelete().equals(false)) {
                event.setIsDelete(true);
                eventRepository.save(event);
                if (templateOptional.isPresent()) {
                    Template template = templateOptional.get();
                    template.setIsDelete(true);
                    templateRepository.save(template);
                }
            } else {
                throw new RuntimeException("Event Id is already deleted!");
            }
        } else {
            throw new RuntimeException("Event Id is not exist!");
        }
    }

    /**
     * Get Event Template Binding with Pagination
     *
     * @param page
     * @param size
     * @param onlyActive
     * @return
     */
    @Override
    public Page<EventTemplateBindingDTO> getEventTempBindPagination(Integer page, Integer size, HttpServletRequest request, boolean onlyActive) throws AuthException, CustomException, IOException {
        Pageable pageable = PageRequest.of(page, size);
        List<EventTemplateBindingDTO> eventTemplateBindingDTOList = getEventTempBindList(request, onlyActive);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), eventTemplateBindingDTOList.size());
        List<EventTemplateBindingDTO> pageList = (start < end) ? eventTemplateBindingDTOList.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pageList, pageable, eventTemplateBindingDTOList.size());
    }

    /**
     * Get All Active Event Template Bind List
     *
     * @return
     */
    @Override
    public List<EventTemplateBindingDTO> getEventTempBindList(HttpServletRequest request, boolean onlyActive) throws AuthException, CustomException, IOException {
        String encodeToken = request.getHeader("Authorization");
        Long usermvnoid = tokenDataExtractor.getMvnoId(encodeToken);
        List<Event> eventList = getEventBYMVNOFilter(usermvnoid, onlyActive);
        Map<Long, Template> templateMap = templateRepository.findAll()
                .stream()
                .filter(template -> template != null
                        && template.getServiceType() != null
                        && template.getServiceType().equalsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF)
                        && template.getEvent() != null
                        && template.getEvent().getEventId() != null)
                .collect(Collectors.toMap(template -> template.getEvent().getEventId(), template -> template));

        return eventList.stream()
                .map(event -> createEventTemplateBindingDTO(event, templateMap.getOrDefault(event.getEventId(), new Template())))
                .collect(Collectors.toList());
    }

    /**
     * Get Event Template By Event Id
     *
     * @param eventId
     * @return
     */
    @Override
    public EventTemplateBindingDTO getEventTempBindById(Long eventId) {
        Event event = eventRepository.findByIsDeleteIsFalseAndStatusAndServiceTypeAndEventId(CommonConstants.ACTIVE, NotificationConstants.ServiceType.SERVICE_TYPE_IWF, eventId);
        if (event != null) {
            Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
            /** Call Create Template Binding Dto Method */
            return createEventTemplateBindingDTO(event, template);
        } else {
            throw new RuntimeException("Record not found with given event id!");
        }
    }

    /**
     * Get Event Template Binding by Event Name
     *
     * @param page
     * @param size
     * @param mvnoId
     * @param eventName
     * @return
     */
    @Override
    public Page<EventTempBindSearchDTO> filterEventTempBindByName(PaginationRequestDTO requestDTO, Long mvnoId) {
        Integer page = requestDTO.getPage();
        Integer size = requestDTO.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("eventId")));
        if (null != requestDTO.getFilters() && 0 < requestDTO.getFilters().size()) {
            for (GenericSearchModel searchModel : requestDTO.getFilters()) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase("any")) {
                    return getAnyEventTempBindSearchDTO(searchModel.getFilterValue(), pageable, mvnoId);
                }
                return getEventTempBindSearchDTO(searchModel.getFilterValue(), pageable, searchModel.getFilterColumn().trim(), mvnoId);
            }
        }
        return null;
    }

    private Page<EventTempBindSearchDTO> getAnyEventTempBindSearchDTO(String filterValue, Pageable pageable, Long mvnoId) {
        try {
            Page<Event> eventPage;
            if (mvnoId == 1) {
                eventPage = eventRepository.findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndMvnoIdIn(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, pageable, Collections.singletonList(mvnoId));
            } else {
                eventPage = eventRepository.findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndMvnoIdIn(
                        NotificationConstants.ServiceType.SERVICE_TYPE_IWF, Arrays.asList(mvnoId, 1L), pageable);
            }
            if (eventPage.isEmpty()) {
                throw new RuntimeException("No Record Found!");
            }
            List<Event> events = eventPage.getContent();
            Map<Event, Template> templateMap = fetchTemplates(events);
            Map<Long, List<SmsReceiverEventTempBinding>> smsReceiverMap = fetchSmsReceiverBindings(events);
            Map<Long, List<SmsConfigEventTempBinding>> smsConfigMap = fetchSmsConfigBindings(events);
            List<EventTempBindSearchDTO> eventTempBindSearchDTOList = events.stream()
                    .map(event -> createEventTempBindSearchDTO(event, templateMap.getOrDefault(event.getEventId(), new Template()),
                            smsReceiverMap.getOrDefault(event.getEventId(), new ArrayList<>()),
                            smsConfigMap.getOrDefault(event.getEventId(), new ArrayList<>())))
                    .collect(Collectors.toList());

            return new PageImpl<>(eventTempBindSearchDTOList, pageable, eventPage.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<EventTempBindSearchDTO> getEventTempBindSearchDTO(String filterValue, Pageable pageable, String filterColumn, Long mvnoId) {
        try {
            Page<Event> eventPage;
            List<Event> eventList = new ArrayList<>();
            if (mvnoId == 1) {
                eventList = eventRepository.findAllByIsDeleteIsFalseAndServiceTypeAndSystemGeneratedIsFalse(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            } else {
                eventList = eventRepository.findAllByIsDeleteIsFalseAndServiceTypeAndMvnoIdInAndSystemGeneratedIsFalse(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, Arrays.asList(mvnoId, 1L));
            }
            if (eventList.isEmpty()) {
                throw new RuntimeException("No Record Found!");
            } else if (!eventList.isEmpty()) {
                if (filterColumn.equalsIgnoreCase("eventname")) {
                    eventList = eventList.stream()
                            .filter(event -> event.getEventName().equalsIgnoreCase(filterValue))
                            .collect(Collectors.toList());
                }
                if (filterColumn.equalsIgnoreCase("templatename")) {
                    eventList = eventList.stream()
                            .filter(event -> event.getEventName().equalsIgnoreCase(filterValue))
                            .collect(Collectors.toList());
                }
            }
            Map<Event, Template> templateMap = fetchTemplates(eventList);
            Map<Long, List<SmsReceiverEventTempBinding>> smsReceiverMap = fetchSmsReceiverBindings(eventList);
            Map<Long, List<SmsConfigEventTempBinding>> smsConfigMap = fetchSmsConfigBindings(eventList);
            List<EventTempBindSearchDTO> eventTempBindSearchDTOList = eventList.stream()
                    .map(event -> createEventTempBindSearchDTO(event, templateMap.getOrDefault(event.getEventId(), new Template()),
                            smsReceiverMap.getOrDefault(event.getEventId(), new ArrayList<>()),
                            smsConfigMap.getOrDefault(event.getEventId(), new ArrayList<>())))
                    .collect(Collectors.toList());
            return new PageImpl<>(eventTempBindSearchDTOList, pageable, eventTempBindSearchDTOList.size());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Fetch Templates
     *
     * @param events
     * @return
     */
    private Map<Event, Template> fetchTemplates(List<Event> events) {
        Set<Long> eventIds = events
                .stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());
        List<Template> templates = templateRepository.findByEvent_EventIdInAndServiceTypeContainingIgnoreCase
                (eventIds, NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
        return templates
                .stream()
                .collect(Collectors.toMap(Template::getEvent, Function.identity()));
    }

    /**
     * Method: Fetch SMS Reciver Bindings
     *
     * @param events
     * @return
     */
    private Map<Long, List<SmsReceiverEventTempBinding>> fetchSmsReceiverBindings(List<Event> events) {
        return smsReceiverEventTempBindingRepository.findAllByEventIn(events)
                .stream()
                .collect(Collectors.groupingBy(smsReceiver -> smsReceiver.getEvent().getEventId()));
    }

    /**
     * Method: Fetch SMS Config Bindings
     *
     * @param events
     * @return
     */
    private Map<Long, List<SmsConfigEventTempBinding>> fetchSmsConfigBindings(List<Event> events) {
        return smsConfigEventTempBindingRepository.findAllByEventIn(events)
                .stream()
                .collect(Collectors.groupingBy(smsConfig -> smsConfig.getEvent().getEventId()));
    }

    /**
     * Method: Create Event Temp Bind Search DTO
     *
     * @param event
     * @param template
     * @param smsReceiverEventTempBindings
     * @param smsConfigEventTempBindingList
     * @return
     */
    private EventTempBindSearchDTO createEventTempBindSearchDTO(Event event, Template template, List<SmsReceiverEventTempBinding> smsReceiverEventTempBindings, List<SmsConfigEventTempBinding> smsConfigEventTempBindingList) {
        EventTempBindSearchDTO dto = new EventTempBindSearchDTO();

        dto.setEventId(event.getEventId());
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType());
        dto.setDescription(event.getDescription());
        dto.setStatus(event.getStatus());
        dto.setCreateDate(event.getCreateDate());
        dto.setLastModificationDate(event.getLastModificationDate());
        dto.setMvnoId(event.getMvnoId());
        dto.setEmailSubject(event.getEmailSubject());
        dto.setToEmailId(event.getToEmailId());
        dto.setCcEmailId(event.getCcEmailId());
        dto.setBccEmailId(event.getBccEmailId());
        dto.setEmailConfigId(event.getEmailConfigId());
        dto.setConstraintType(event.getConstraintType());
        dto.setServiceType(event.getServiceType());
        dto.setSystemGenerated(event.getSystemGenerated());

        if (template != null) {
            dto.setTemplateFilePath(template.getTemplateFilePath());
            dto.setContentType(template.getContentType());
            dto.setIsEmailTemplate(template.getIsEmailTemplate());
            dto.setIsSMSTemplate(template.getIsSMSTemplate());
            dto.setTemplateName(template.getTemplateName());
            dto.setEmailTemplateData(template.getEmailTemplateData());
            dto.setSmsTemplateData(template.getSmsTemplateData());
            dto.setFileName(template.getFileName());
            dto.setContent(template.getContent());
            dto.setIsAppendRequired(template.getIsAppendRequired());
            dto.setAppendURL(template.getAppendUrl());
        }
        if (!smsConfigEventTempBindingList.isEmpty()) {
            dto.setSmsConfTempBindSearchDTOS(smsConfigEventTempBindingList.stream()
                    .map(binding -> new SMSConfTempBindSearchDTO(binding.getSmsConfig().getSmsConfigId(), binding.getSmsConfig().getSmsUrl()))
                    .collect(Collectors.toList()));
        }
        if (!smsReceiverEventTempBindings.isEmpty()) {
            dto.setSmsReceiveSearchDTOS(smsReceiverEventTempBindings.stream()
                    .map(receiver -> new SMSReceiveSearchDTO(receiver.getStaffId(), receiver.getStaffFullName(), receiver.getMobileNumber()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /**
     * Validate Event Template Binding
     *
     * @param eventTemplateBindingDTO
     */
    @Override
    public void validateEventTempBind(EventTemplateBindingDTO eventTemplateBindingDTO) {
        /** Call With Validate Not Null Method */
        validateNotNull(eventTemplateBindingDTO.getEventName(), "Please enter value in event name");
        if (!isNotNullNotEmptyNotWhiteSpaceOnly(eventTemplateBindingDTO.getEventName())) {
            throw new RuntimeException("Only blank spaces are not allowed in event name");
        }
        validateNotNull(eventTemplateBindingDTO.getEventType(), "Please select event type");
        validateNotNull(eventTemplateBindingDTO.getDescription(), "Please enter value in description");
        validateNotNull(eventTemplateBindingDTO.getStatus(), "Please select status");
        if (eventTemplateBindingDTO.getIsFrequency()) {
            validateNotNull(eventTemplateBindingDTO.getTimeInterval(), "Please enter value in interval");
            validateNotNull(eventTemplateBindingDTO.getConstraintType(), "Please select constraint type");
            if (eventTemplateBindingDTO.getConstraintType() != null) {
                validateNotNull(eventTemplateBindingDTO.getColumnValue(), "Please select column value");
                if (eventTemplateBindingDTO.getConstraintType().equalsIgnoreCase(NotificationConstants.ConstraintType.CONSTRAINT_TYPE_REGEX_BASED)) {
                    validateNotNull(eventTemplateBindingDTO.getRegex(), "Please enter value in regex value");
                    validateNotNull(eventTemplateBindingDTO.getRegexGroupIndex(), "Please enter value in regex group index value");
                }
            }
        }
        validateNotNull(eventTemplateBindingDTO.getTemplateName(), "Please enter value in template name");
        if (!isNotNullNotEmptyNotWhiteSpaceOnly(eventTemplateBindingDTO.getTemplateName())) {
            throw new RuntimeException("Only blank spaces are not allowed in Template name");
        }
        if (eventTemplateBindingDTO.getIsEmailTemplate()) {
            validateNotNull(eventTemplateBindingDTO.getIsEmailTemplate(), "Please enter value in email template data");
        }
        if (eventTemplateBindingDTO.getIsSMSTemplate()) {
            validateNotNull(eventTemplateBindingDTO.getSmsTemplateData(), "Please enter value in sms template data");
        }
        if (eventTemplateBindingDTO.getIsEmailTemplate()) {
            validateNotNull(eventTemplateBindingDTO.getToEmailId(), "Please enter value in To Email Id");
            if (eventTemplateBindingDTO.getContentType() != null) {
                validateNotNull(eventTemplateBindingDTO.getContentType(), "Please select content type");
                if (eventTemplateBindingDTO.getContentType().equalsIgnoreCase(NotificationConstants.ContentType.CONTENT_TYPE_MANUAL)) {
                    validateNotNull(eventTemplateBindingDTO.getEmailTemplateData(), "Please enter value in email template data");
                }
            }
        }
    }

    /**
     * Validate Object is not Null
     *
     * @param value
     * @param errorMessage
     */
    private void validateNotNull(Object value, String errorMessage) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(errorMessage);
        }
        if (value instanceof String) {
            if (!isNotNullNotEmptyNotWhiteSpaceOnly(String.valueOf(value))) {
                throw new RuntimeException(errorMessage);
            }
        }
    }

    /**
     * Demonstrate checking for String that is not null, not empty, and not white
     * space only using standard Java classes.
     *
     * @param string String to be checked for not null, not empty, and not white
     *               space only.
     * @return {@code true} if provided String is not null, is not empty, and
     * has at least one character that is not considered white space.
     */
    public static boolean isNotNullNotEmptyNotWhiteSpaceOnly(String string) {
        return string != null && !string.isEmpty() && !string.trim().isEmpty();
    }

    @Override
    public Page<EventTemplateDTO> filterEventTempBind(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType) throws Exception {
        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getPageSize(), Sort.by(Sort.Order.desc("eventId")));
        Specification<Event> spec = Specification.where(null);
        spec = (root, query, builder) -> builder.isFalse(root.get("systemGenerated"));
        if (mvnoId != 1) {
            spec = spec.and((root, query, builder) -> root.get(NotificationConstants.EmailConfigSearch.MVNO_ID).in(mvnoId, 1));
        }
        if (!serviceType.trim().isEmpty()) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.EmailConfigSearch.SERVICE_TYPE), serviceType));
        }
        if (requestDTO.getFilters() != null && !requestDTO.getFilters().isEmpty()) {
            for (GenericSearchModel searchModel : requestDTO.getFilters()) {
                spec = "ANY".equalsIgnoreCase(searchModel.getFilterColumn())
                        ? getAllEvents(searchModel.getFilterValue(), searchModel.getFilterCondition(), spec)
                        : getEventByFilter(searchModel.getFilterValue(), searchModel.getFilterCondition(), searchModel.getFilterColumn(), spec);
            }
        }
        Page<Event> eventPage = eventRepository.findAll(spec, pageable);
        return eventPage.map(this::setPropertiesToDto);
    }


    public Specification<Event> getAllEvents(String value, String filterCondition, Specification<Event> spec) {
        String escapedValue = value.trim().replace("_", "\\_");
        if (filterCondition.equalsIgnoreCase("OR")) {
            spec = spec.or((root, query, builder) ->
                    builder.or(
                            builder.like(root.get("eventName"), "%" + escapedValue + "%"),
                            builder.like(root.get("eventType"), "%" + escapedValue + "%"),
                            builder.like(root.get("emailSubject"), "%" + escapedValue + "%"),
                            builder.like(root.join("template").get("templateName"), "%" + escapedValue + "%")
                    )
            );
        } else {
            spec = spec.and((root, query, builder) ->
                    builder.or(
                            builder.like(root.get("eventName"), "%" + escapedValue + "%"),
                            builder.like(root.get("eventType"), "%" + escapedValue + "%"),
                            builder.like(root.get("emailSubject"), "%" + escapedValue + "%"),
                            builder.like(root.join("template").get("templateName"), "%" + escapedValue + "%")
                    )
            );
        }
        return spec;
    }

    public Specification<Event> getEventByFilter(String value, String filterCondition, String filterColumn, Specification<Event> spec) {
        String escapedFirstName = value.trim().replace("_", "\\_");
        if ((filterColumn != null) && (!filterColumn.trim().isEmpty())) {
            if (filterCondition.equalsIgnoreCase("OR")) {
                if ("eventName".equalsIgnoreCase(filterColumn)) {
                    spec = spec.or((root, query, builder) -> builder.like(root.get("eventName"), "%" + escapedFirstName + "%"));
                } else if ("eventType".equalsIgnoreCase(filterColumn)) {
                    spec = spec.or((root, query, builder) -> builder.like(root.get("eventType"), "%" + escapedFirstName + "%"));
                } else if ("emailSubject".equalsIgnoreCase(filterColumn)) {
                    spec = spec.or((root, query, builder) -> builder.like(root.get("emailSubject"), "%" + escapedFirstName + "%"));
                } else if ("templateName".equalsIgnoreCase(filterColumn)) {
                    spec = spec.or((root, query, builder) -> builder.like(root.join("template").get("templateName"), "%" + escapedFirstName + "%"));
                }
            } else {
                if ("eventName".equalsIgnoreCase(filterColumn)) {
                    spec = spec.and((root, query, builder) -> builder.like(root.get("eventName"), "%" + escapedFirstName + "%"));
                } else if ("eventType".equalsIgnoreCase(filterColumn)) {
                    spec = spec.and((root, query, builder) -> builder.like(root.get("eventType"), "%" + escapedFirstName + "%"));
                } else if ("emailSubject".equalsIgnoreCase(filterColumn)) {
                    spec = spec.and((root, query, builder) -> builder.like(root.get("emailSubject"), "%" + escapedFirstName + "%"));
                } else if ("templateName".equalsIgnoreCase(filterColumn)) {
                    spec = spec.and((root, query, builder) -> builder.like(root.join("template").get("templateName"), "%" + escapedFirstName + "%"));
                }
            }
        }
        return spec;
    }

    public EventTemplateDTO setPropertiesToDto(Event event) {

        return new EventTemplateDTO(
                event.getEventId(),
                event.getEventName(),
                event.getEventType(),
                event.getDescription(),
                event.getStatus(),
                event.getTimeInterval(),
                event.getTimeIntervalType(),
                event.getEmailSubject(),
                event.getToEmailId(),
                event.getCcEmailId(),
                event.getBccEmailId(),
                event.getEmailConfigId(),
                event.getConstraintType(),
                event.getColumnValue(),
                event.getRegex(),
                event.getRegexGroupIndex(),
                event.getSystemGenerated(),
                event.getTemplate()
        );
    }

    @Override
    public boolean validation(PaginationRequestDTO paginationDTO) {
        for (GenericSearchModel searchModel : paginationDTO.getFilters()) {
            if (searchModel.getFilterValue() == null || searchModel.getFilterValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<Event> getEventBYMVNOFilter(Long mvnoId, boolean onlyActive) {
        return eventRepository.findAll()
                .stream()
                .filter(event -> (mvnoId == 1L || event.getMvnoId().equals(mvnoId) || event.getMvnoId().equals(1L)) &&
                        !event.getSystemGenerated() &&
                        !event.getIsDelete() &&
                        event.getServiceType().equalsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF) &&
                        (!onlyActive || event.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE)))
                .sorted(Comparator.comparingLong(Event::getEventId).reversed())
                .collect(Collectors.toList());
    }


    /**
     * Create EventTemplateBindingDTO
     *
     * @param event
     * @param template
     * @return
     */
    private EventTemplateBindingDTO createEventTemplateBindingDTO(Event event, Template template) {
        /** Call With Get Value If Non Empty Method */
        return new EventTemplateBindingDTO(
                event.getEventId(),
                (template != null) ? template.getTemplateId() : null,
                getValueIfNonEmpty(event.getEventName()),
                getValueIfNonEmpty(event.getEventType()),
                getValueIfNonEmpty(event.getDescription()),
                getValueIfNonEmpty(event.getStatus()),
                getValueIfNonEmpty(event.getTimeInterval()),
                getValueIfNonEmpty(event.getTimeIntervalType()),
                getValueIfNonEmpty(event.getEmailSubject()),
                getValueIfNonEmpty(event.getToEmailId()),
                getValueIfNonEmpty(event.getCcEmailId()),
                getValueIfNonEmpty(event.getBccEmailId()),
                event.getEmailConfigId(),
                getValueIfNonEmpty(event.getConstraintType()),
                getValueIfNonEmpty(event.getColumnValue()),
                getValueIfNonEmpty(event.getRegex()),
                getValueIfNonEmpty(event.getRegexGroupIndex()),
                event.getSystemGenerated(),
                (template != null) ? getValueIfNonEmpty(template.getTemplateFilePath()) : null,
                (template != null) ? getValueIfNonEmpty(template.getContentType()) : null,
                (template != null) ? template.getIsEmailTemplate() : null,
                (template != null) ? template.getIsSMSTemplate() : null,
                (template != null) ? getValueIfNonEmpty(template.getTemplateName()) : null,
                (template != null) ? getValueIfNonEmpty(template.getEmailTemplateData()) : null,
                (template != null) ? getValueIfNonEmpty(template.getSmsTemplateData()) : null,
                (template != null) ? template.getFileName() : null,
                (template != null) ? template.getContent() : null,
                event.getIsFrequency(),
                getSmsConfigIdsList(event),
                staffDtoList(event),
                template.getIsAppendRequired(),
                template.getAppendUrl()
        );
    }

    /**
     * Get a non-empty string or null
     *
     * @param s
     * @return
     */
    private String getValueIfNonEmpty(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : null;
    }

    private List<Long> getSmsConfigIdsList(Event event) {
        List<Long> smsConfigIdsList = new ArrayList<>();
        try {
            List<SmsConfigEventTempBinding> smsConfigEventTempBindingList = smsConfigEventTempBindingService.findAllSmsConfigEventTempBindingByEvent(event.getEventId());
            if (smsConfigEventTempBindingList != null && smsConfigEventTempBindingList.size() > 0) {
                for (SmsConfigEventTempBinding smsConfigEventTempBinding : smsConfigEventTempBindingList) {
                    smsConfigIdsList.add(smsConfigEventTempBinding.getSmsConfig().getSmsConfigId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsConfigIdsList;
    }

    private List<StaffCustomDTO> staffDtoList(Event event) {
        List<StaffCustomDTO> staffDtoList = new ArrayList<>();
        try {
            List<SmsReceiverEventTempBinding> smsReceiverEventTempBindingsList = smsReceiverEventTempBindingService.findAllSmsReceiverEventTempBindingByEvent(event.getEventId());
            if (smsReceiverEventTempBindingsList != null && smsReceiverEventTempBindingsList.size() > 0) {
                for (SmsReceiverEventTempBinding smsReceiverEventTempBinding : smsReceiverEventTempBindingsList) {
                    StaffCustomDTO staffCustomDTO = new StaffCustomDTO();
                    staffCustomDTO.setId(smsReceiverEventTempBinding.getStaffId());
                    staffCustomDTO.setMobileNumber(smsReceiverEventTempBinding.getMobileNumber());
                    staffCustomDTO.setUsername(smsReceiverEventTempBinding.getStaffUsername());
                    staffCustomDTO.setFullName(smsReceiverEventTempBinding.getStaffFullName());
                    staffDtoList.add(staffCustomDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staffDtoList;
    }

    /**
     * Set Event Data at Create/ Update Event Template Binding
     *
     * @param eventId
     * @param eventTemplateBindingDTO
     * @param operation
     * @param usermvnoid
     * @return
     */
    @Transactional
    public Event setEventData(Long eventId, EventTemplateBindingDTO eventTemplateBindingDTO, Integer operation, Long usermvnoid) throws IOException {
        Event event = null;
        if (operation.equals(CommonConstants.OPERATION.OPERATION_ADD)) {
            event = new Event();
            event.setMvnoId(usermvnoid);
        } else if (operation.equals(CommonConstants.OPERATION.OPERATION_UPDATE)) {
            event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Id is Null"));
            if (event == null) {
                throw new RuntimeException("Event Id is not exist!");
            }
        }
        if (event != null) {
            /** Set Basic Details of Event with Call Trim To Null Method */
            event.setEventName(trimToNull(eventTemplateBindingDTO.getEventName()));
            event.setEventType(trimToNull(eventTemplateBindingDTO.getEventType()));
            event.setDescription(trimToNull(eventTemplateBindingDTO.getDescription()));
            event.setStatus(trimToNull(eventTemplateBindingDTO.getStatus()));
            /** Set Mail Frequency Data of Event with Call Trim To Null Method*/
            if (eventTemplateBindingDTO.getIsFrequency()) {
                event.setIsFrequency(eventTemplateBindingDTO.getIsFrequency());
                event.setTimeInterval(trimToNull(eventTemplateBindingDTO.getTimeInterval()));
                event.setTimeIntervalType(trimToNull(eventTemplateBindingDTO.getTimeIntervalType()));
                event.setConstraintType(trimToNull(eventTemplateBindingDTO.getConstraintType()));
                event.setColumnValue(trimToNull(eventTemplateBindingDTO.getColumnValue()));
                if (eventTemplateBindingDTO.getTimeInterval() != null && eventTemplateBindingDTO.getTimeIntervalType() != null) {
                    try {
                        double intervalTime = Double.parseDouble(eventTemplateBindingDTO.getTimeInterval());
                        if (intervalTime % 1 != 0) {
                            throw new RuntimeException("Invalid interval time format. Please enter a whole number.");
                        }
                        switch (eventTemplateBindingDTO.getTimeIntervalType()) {
                            case NotificationConstants.IntervalTimeType.INTERVAL_TIME_TYPE_MINUTE:
                                event.setConvertedTime((long) (intervalTime * 60));
                                break;
                            case NotificationConstants.IntervalTimeType.INTERVAL_TIME_TYPE_HOUR:
                                event.setConvertedTime((long) intervalTime * 3600);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid interval time format");
                    }
                }
                event.setRegex(trimToNull(eventTemplateBindingDTO.getRegex()));
                event.setRegexGroupIndex(trimToNull(eventTemplateBindingDTO.getRegexGroupIndex()));
            }

            /** Set Email Template Config Data with Call Trim To Null Method */
            event.setSystemGenerated(false);
            event.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            event.setToEmailId(trimToNull(eventTemplateBindingDTO.getToEmailId()));
            event.setCcEmailId(trimToNull(eventTemplateBindingDTO.getCcEmailId()));
            event.setBccEmailId(trimToNull(eventTemplateBindingDTO.getBccEmailId()));
            event.setEmailSubject(trimToNull(eventTemplateBindingDTO.getEmailSubject()));
            event.setEmailConfigId(eventTemplateBindingDTO.getEmailConfigId());
            event = eventRepository.save(event);
        }
        return event;
    }

    /**
     * Set Template Data at Create/ Update Event Template Binding
     *
     * @param eventTemplateBindingDTO
     * @param event
     * @param operation
     * @return
     */
    @Transactional
    public Template setTemplateData(EventTemplateBindingDTO eventTemplateBindingDTO, Event event, Integer operation, Long usermvnoid) {
        Template template = null;
        if (operation.equals(CommonConstants.OPERATION.OPERATION_ADD)) {
            template = new Template();
        } else if (operation.equals(CommonConstants.OPERATION.OPERATION_UPDATE)) {
            template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
        }
        if (template != null) {
            /** Set Email Template Config Data with Call Trim To Null Method */
            template.setEvent(event);
            template.setMvnoId(Math.toIntExact(usermvnoid));
            template.setBuId(null);
            template.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            template.setTemplateName(trimToNull(eventTemplateBindingDTO.getTemplateName()));
            template.setIsAppendRequired(eventTemplateBindingDTO.getIsAppendRequired());
            template.setAppendUrl(eventTemplateBindingDTO.getAppendURL());
            if (eventTemplateBindingDTO.getIsSMSTemplate() != null && eventTemplateBindingDTO.getIsSMSTemplate()) {
                template.setIsSMSTemplate(true);
                template.setSmsTemplateData(trimToNull(eventTemplateBindingDTO.getSmsTemplateData()));
                template.setSmsEventConfigured(false);
            } else {
                template.setIsSMSTemplate(false);
                template.setSmsEventConfigured(false);
            }
            if (eventTemplateBindingDTO.getIsEmailTemplate() != null && eventTemplateBindingDTO.getIsEmailTemplate()) {
                template.setIsEmailTemplate(true);
                template.setEmailTemplateData(trimToNull(eventTemplateBindingDTO.getEmailTemplateData()));
                template.setEmailEventConfigured(true);
                if (eventTemplateBindingDTO.getContentType() != null) {
                    template.setContentType(trimToNull(eventTemplateBindingDTO.getContentType()));
                    if (eventTemplateBindingDTO.getContentType().equalsIgnoreCase(NotificationConstants.ContentType.CONTENT_TYPE_FTL_BASED)) {
                        if (eventTemplateBindingDTO.getContent() != null && !eventTemplateBindingDTO.getContent().isEmpty()) {
                            template.setFileName(eventTemplateBindingDTO.getFileName());
                            template.setContent(eventTemplateBindingDTO.getContent());
                        }
                    }
                }
            } else {
                template.setIsEmailTemplate(false);
                template.setEmailEventConfigured(false);
            }
            template.setStatus(trimToNull(eventTemplateBindingDTO.getStatus()));
            if (eventTemplateBindingDTO.getStatus() != null && eventTemplateBindingDTO.getStatus().equalsIgnoreCase(NotificationConstants.IN_ACTIVE)) {
                template.setIsActive(false);
            }
            template.setTemplateFilePath(trimToNull(eventTemplateBindingDTO.getTemplateFilePath()));
            template = templateRepository.save(template);
        }
        return template;
    }

    /**
     * Set null If any value is empty/ "string" / ""
     *
     * @param value
     * @return
     */
    private String trimToNull(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }
}
