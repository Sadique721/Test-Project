package com.savbill.notification.services.impl;

import java.io.BufferedReader;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.*;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.kafka.KafkaConstant;
import com.savbill.notification.kafka.KafkaMessageData;
import com.savbill.notification.kafka.KafkaMessageSender;
//import com.savbill.notification.rabbitmq.MessageSender;
import com.savbill.notification.rabbitmq.message.TicketAuditMessage;
import com.savbill.notification.rabbitmq.message.TicketETRAuditMessage;

import com.savbill.notification.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.savbill.notification.utils.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.notification.rabbitmq.RabbitMqConstants;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.SmsConfigMappingRepository;
import com.savbill.notification.repository.SmsConfigRepository;
import com.savbill.notification.repository.SmsRepository;
import com.savbill.notification.services.SmsService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

@Service
public class SmsServiceImpl implements SmsService {
    private static final String EVENT_ID = "eventId";
    private static final String SMS_CONTENT = "smsContent";
    public static final String FAIL = "Failed";
    public static final String SENT = "Sent";
    public static final String PENDING = "Pending";

    private static final String TEMPLATE_NOT_FOUND = "template not found";

    @Value("${SMSHASUPPORT}")
    private String SMSHA;
    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    @Autowired
    SmsRepository smsRepository;
    @Autowired
    SmsConfigRepository smsConfigRepository;
    @Autowired
    SmsConfigMappingRepository smsConfigMappingRepository;
    @Autowired
    EventRepository eventRepository;

    @Autowired
    InventorySmsServiceImpl inventorySmsService;

    //	@Autowired
//	MessageSender messageSender;
    @Autowired
    TemplateServiceImpl templateServiceImpl;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    UpdateDiffFinder updateDiffFinder;
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    @Autowired
    private NotificationConfigMappingServiceImpl notificationConfigMappingService;
    @Autowired
    Tracer tracer;

    @Override
    public List<SmsDataDTO> findSmsBySourceName(Long eventId, String status, String mobileNo, Long mvnoId) {
        try {
            List<SmsDataDTO> smsList = new ArrayList<>();
            QSms qSms = QSms.sms;
            BooleanExpression boolExp = qSms.isNotNull();
            if (eventId == null && status == null && mobileNo == null) {
                if (mvnoId == 1) {
//					smsList = smsRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
                } else {
                    boolExp = boolExp.and(qSms.mvnoId.eq(mvnoId)).or(qSms.mvnoId.eq(1L));
//					smsList = (List<Sms>) smsRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "createDate"));
                }
            } else {
                if (mvnoId == 1) {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(status) && !status.equals("null")) {
                        boolExp = boolExp.and(qSms.status.equalsIgnoreCase(status));
                    }
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNo)
                            && !mobileNo.equals("null")) {
                        boolExp = boolExp.and(qSms.mobileNo.contains(mobileNo));
                    }
                    if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                        boolExp = boolExp.and(qSms.eventId.eq(eventId));
                    }
//					smsList = (List<Sms>) smsRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "createDate"));
                } else {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(status) && !status.equals("null")) {
                        boolExp = boolExp.and(qSms.status.equalsIgnoreCase(status));
                    }
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNo)
                            && !mobileNo.equals("null")) {
                        boolExp = boolExp.and(qSms.mobileNo.contains(mobileNo));
                    }
                    if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                        boolExp = boolExp.and(qSms.eventId.eq(eventId));
                    }
                    boolExp = boolExp.and(qSms.mvnoId.eq(mvnoId).or(qSms.mvnoId.eq(1L)));
//					smsList = (List<Sms>) smsRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "createDate"));

                }
                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

                QueryResults<SmsDataDTO> queryResults = queryFactory
                        .select(Projections.constructor(
                                SmsDataDTO.class,
                                qSms.smsId,
                                qSms.sourceName,
                                qSms.countryCode,
                                qSms.mobileNo,
                                qSms.message,
                                qSms.date,
                                qSms.status,
                                qSms.eventId,
                                qSms.eventName
                        )).from(qSms)
                        .where(boolExp)
                        .orderBy(qSms.smsId.desc())
                        .fetchResults();
                smsList = queryResults.getResults();
            }
            return smsList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Sms findSmsById(Long smsId, Long mvnoId, Boolean isDelete) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            }
            QSms qSms = QSms.sms;
            BooleanExpression booleanExpression = qSms.isNotNull();
            booleanExpression = booleanExpression.and(qSms.smsId.eq(smsId));
            if (mvnoId != 1)
                if (isDelete)
                    booleanExpression = booleanExpression.and(qSms.mvnoId.eq(mvnoId));
                else
                    booleanExpression = booleanExpression.and(qSms.mvnoId.in(mvnoId, 1));
            Optional<Sms> sms = smsRepository.findOne(booleanExpression);
            if (sms.isPresent()) {
                return sms.get();
            } else {
                throw new IllegalArgumentException(
                        "No record found with sms id " + smsId + " . Please enter valid sms id.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
//	@Override
//	public PageableResponse<SmsDataDTO> findAllSmss(Long eventId, String status, String mobileNo, Long mvnoId,
//											 PaginationDTO paginationDTO, List<Long> buIdList) {
//		try {
//			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
//				throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
//			} else {
//				QSms qSms = QSms.sms;
//				BooleanExpression boolExp = qSms.isNotNull();
//				if (paginationDTO.getPage() > 0) {
//					paginationDTO.setPage(paginationDTO.getPage() - 1);
//				}
//				PageableResponse<SmsDataDTO> pageableResponse = new PageableResponse<>();
//				Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(),
//						Sort.by(Sort.Direction.DESC, "createDate"));
////				if(mvnoId != 1)
////				{
////					boolExp = boolExp.and(qSms.mvnoId.eq(mvnoId).or(qSms.mvnoId.eq(1L)));
////				}
//
//				if (eventId == null && status == null && mobileNo == null) {
//					if (mvnoId != 1) {
//						boolExp = boolExp.and(qSms.mvnoId.eq(mvnoId)).or(qSms.mvnoId.eq(1L));
//					}
//					if(!buIdList.isEmpty()){
//						boolExp=boolExp.and(qSms.buId.in(buIdList));
//					}
//
//				} else {
//					if (mvnoId == 1) {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qSms.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNo)
//								&& !mobileNo.equals("null")) {
//							boolExp = boolExp.and(qSms.mobileNo.contains(mobileNo));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qSms.event.eventId.eq(eventId));
//						}
//					} else {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qSms.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNo)
//								&& !mobileNo.equals("null")) {
//							boolExp = boolExp.and(qSms.mobileNo.contains(mobileNo));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qSms.event.eventId.eq(eventId));
//						}
//						if(!buIdList.isEmpty()){
//							boolExp=boolExp.and(qSms.buId.in(buIdList));
//						}
//						boolExp = boolExp.and(qSms.mvnoId.eq(mvnoId).or(qSms.mvnoId.eq(1L)));
//					}
//				}
//
//				JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//				QueryResults<SmsDataDTO> queryResults = queryFactory
//						.select(Projections.constructor(
//								SmsDataDTO.class,
//								qSms.smsId,
//								qSms.sourceName,
//								qSms.countryCode,
//								qSms.mobileNo,
//								qSms.message,
//								qSms.date,
//								qSms.status,
//								qSms.event.eventId,
//								qSms.event.eventName))
//						.from(qSms)
//						.where(boolExp)
//						.orderBy(qSms.smsId.desc())
//						.offset((paginationDTO.getPage()) * paginationDTO.getSize())
//						.limit(paginationDTO.getSize())
//						.fetchResults();
//
//
//				List<SmsDataDTO> smsDataDTOList = queryResults.getResults();
//				long totalRecords = queryResults.getTotal();
//				//Page<Sms> page = smsRepository.findAll(boolExp, pageable);
//				return pageableResponse.convert(new PageImpl<>(smsDataDTOList, PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize()), totalRecords));
//			}
//		} catch (RuntimeException e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}

    @Override
    public PageableResponse<SmsDataDTO> findAllSmss(Long eventId, String status, String mobileNo, Long mvnoId,
                                                    PaginationDTO paginationDTO, List<Long> buIdList) {
        try {
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            PageableResponse<SmsDataDTO> pageableResponse = new PageableResponse<>();
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(),
                    Sort.by(Sort.Direction.DESC, "createDate"));

            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                Page<SmsDataDTO> smsDataObjects;
                if (buIdList != null && buIdList.size() > 0) {
                    smsDataObjects = smsRepository.findAllByMvnoIdAndBuIdIn(mvnoId, buIdList, pageable);
                } else {
                    smsDataObjects = smsRepository.findAllByMvnoId(mvnoId, pageable);
                }

                List<SmsDataDTO> smsDataDTOList = new ArrayList<>();
                smsDataDTOList.addAll(smsDataObjects.getContent());
                long totalRecords = smsDataObjects.getTotalElements();
                return pageableResponse.convert(new PageImpl<>(smsDataDTOList, PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize()), totalRecords));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void deleteSmsById(Long smsId, Long mvnoId) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            }
            Sms sms = findSmsById(smsId, mvnoId, true);
            smsRepository.deleteById(smsId);
            // log.info("Sms deleted successfully: "+sms.getMobileNo());
        } catch (RuntimeException e) {
            // log.error("Error to delete sms");
            throw new RuntimeException(e.getMessage());
        } finally {
            // MDC.remove(NotificationConstants.TYPE);
        }
    }

    @Override
    public Sms saveSms(SmsDto smsDto, Long mvnoId, Long buId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getCreatedBy()))
                throw new RuntimeException("CreatedBy value is Missing");
            Sms smsVo = new Sms(smsDto, mvnoId, buId);
            validateEmailData(mvnoId, smsVo, smsDto.getEventId());
            smsVo.setStatus(PENDING);
            smsVo.setCreateDate(LocalDateTime.now());
            smsVo.setCreatedBy(smsDto.getCreatedBy());
            smsVo.setSmsConfigId(validateSmsConfigId(mvnoId, null));
            Optional<Event> eventVo = eventRepository.findById(smsDto.getEventId());
            smsVo.setEventName(eventVo.get().getEventName());
            if (smsDto.getServiceType() == null || "".equals(smsDto.getServiceType())) {
                smsVo.setServiceType(CommonConstants.SERVICE_TYPE_BSS);
            } else {
                smsVo.setServiceType(CommonConstants.SERVICE_TYPE_IWF);
            }
            return smsRepository.save(smsVo);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public Sms updateSms(UpdateSmsDto smsDto, Long mvnoId, Long buId, HttpServletRequest request) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsDto.getLastModifiedBy())) ;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Last modifiedby is mot found ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsDto.getSmsId())) {
                throw new IllegalArgumentException(
                        "Sms id is mandatory. Please enter valid sms id to update the record.");
            } else {
                Sms optionalSms = null;
                if (mvnoId == 1)
                    optionalSms = smsRepository.findById(smsDto.getSmsId()).get();
                else
                    optionalSms = smsRepository.findBySmsIdAndMvnoIdAndBuId(smsDto.getSmsId(), mvnoId, buId).orElse(null);
                if (Objects.isNull(optionalSms)) {
                    throw new IllegalArgumentException(
                            "No record found with sms id : '" + smsDto.getSmsId() + "' , Please enter valid sms id.");
                }
                Sms sms = new Sms(smsDto, mvnoId, optionalSms.getStatus(), optionalSms.getCountryCode(), buId, optionalSms.getDate());
                validateEmailData(mvnoId, sms, smsDto.getEventId());
                sms.setSmsConfigId(validateSmsConfigId(mvnoId, null));
                sms.setLastModifiedDate(LocalDateTime.now());
                sms.setLastModifiedBy(smsDto.getLastModifiedBy());
                sms.setCreateDate(optionalSms.getCreateDate());
                String updatedValues = updateDiffFinder.getUpdatedDiff(optionalSms, sms);
                if (smsDto.getServiceType() == null || "".equals(smsDto.getServiceType())) {
                    sms.setServiceType(CommonConstants.SERVICE_TYPE_BSS);
                } else {
                    sms.setServiceType(CommonConstants.SERVICE_TYPE_IWF);
                }
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS updated successfully " + updatedValues + "  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return smsRepository.save(sms);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long validateSmsConfigId(Long mvnoId, Long buId) {
        List<SmsConfig> optionalSmsConfig = smsConfigRepository.findAllByMvnoIdAndBuIdAndConfigStatus(mvnoId, buId, true);
        if (optionalSmsConfig.size() == 0) {
            throw new RuntimeException("No record found for sms configuration.");
        }
        return optionalSmsConfig.get(0).getSmsConfigId();
    }

    private void validateEmailData(Long mvnoId, Sms sms, Long eventId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                throw new IllegalArgumentException("Event id is mandatory. Please enter valid event id");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(sms.getSourceName())) {
                throw new IllegalArgumentException("Source Name is mandatory. Please enter valid Source Name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(sms.getMobileNo())) {
                throw new IllegalArgumentException("Mobile number is mandatory. Please enter valid mobile number.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(sms.getMessage())) {
                throw new IllegalArgumentException("Message is mandatory. Please enter valid Message.");
            }

            Optional<Event> eventVo = eventRepository.findById(eventId);
            if (!eventVo.isPresent()) {
                throw new IllegalArgumentException(
                        "No event found with event id : '" + eventId + "' , Please enter valid event id.");
            }
            sms.setEventId(eventVo.get().getEventId());
            sms.setEventName(eventVo.get().getEventName());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void sendSms(Long id, Long mvnoId, Long buId, HttpServletRequest request) throws Exception {

        // String FROM_NUMBER = "+19798032515";
        String accountSid = "";
        String authToken = "";
        String fromNo = "";
        Optional<Sms> sms = smsRepository.findBySmsIdAndMvnoIdAndBuId(id, mvnoId, buId);
        Sms smsDetails = sms.get();

        Optional<SmsConfig> smsConfig = smsConfigRepository.findBySmsConfigIdAndMvnoIdAndBuId(smsDetails.getSmsConfigId(),
                mvnoId, buId);
        List<SmsConfigMapping> smsConfigMapping = smsConfigMappingRepository
                .findBySmsConfigIdAndMvnoId(smsDetails.getSmsConfigId(), mvnoId);
        SmsConfig smsConfigDetails = smsConfig.get();
        if ((smsConfigDetails.getSmsUrl()).contains("twilio")) {
            for (int i = 0; i < smsConfigMapping.size(); i++) {
                SmsConfigMapping smsConfigMappingVo = smsConfigMapping.get(i);
                if (smsConfigMappingVo.getParameter().equals(NotificationConstants.ACCOUNT_SID)) {
                    accountSid = smsConfigMappingVo.getValue();
                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.AUTH_TOKEN)) {
                    authToken = smsConfigMappingVo.getValue();
                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.FROM_NUMBER)) {
                    fromNo = smsConfigMappingVo.getValue();
                }
            }
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(new PhoneNumber(smsDetails.getCountryCode() + smsDetails.getMobileNo()),
                    new PhoneNumber(fromNo), smsDetails.getMessage()).create();
            // System.out.println("here is my id:"+message.getSid());
        } else {
            String username = "", password = "", sender = "", type = "", product = "", template = "";
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                for (int i = 0; i < smsConfigMapping.size(); i++) {
                    SmsConfigMapping smsConfigMappingVo = smsConfigMapping.get(i);
                    if (smsConfigMappingVo.getParameter().equals("username")) {
                        username = smsConfigMappingVo.getValue();
                    } else if (smsConfigMappingVo.getParameter().equals("password")) {
                        password = smsConfigMappingVo.getValue();
                    } else if (smsConfigMappingVo.getParameter().equals("sender")) {
                        sender = smsConfigMappingVo.getValue();
                    } else if (smsConfigMappingVo.getParameter().equals("type")) {
                        type = smsConfigMappingVo.getValue();
                    } else if (smsConfigMappingVo.getParameter().equals("product")) {
                        product = smsConfigMappingVo.getValue();
                    } else if (smsConfigMappingVo.getParameter().equals("template")) {
                        template = smsConfigMappingVo.getValue();
                    }
                }
                HttpUriRequest httppost = RequestBuilder.get().setUri(new URI(smsConfigDetails.getSmsUrl()))
                        .addParameter("username", username).addParameter("password", password)
                        .addParameter("sender", sender).addParameter("mobile", "91" + smsDetails.getMobileNo())
                        .addParameter("type", type).addParameter("product", product).addParameter("template", template)
                        .addParameter("message", smsDetails.getMessage().toString()).build();
                CloseableHttpResponse response = httpclient.execute(httppost);
//				System.out.println("Send sms URL is  " + httppost);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Template is deleted Successfully  with id" + httppost + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                try {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Template is deleted Successfully  with id" + httppost + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    // System.out.println(EntityUtils.toString(response.getEntity()));
                } finally {
                    response.close();
                }
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                httpclient.close();
            }
        }
        smsDetails.setStatus(SENT);
        smsDetails.setDate(LocalDateTime.now());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "sms successfully send to" + smsDetails.getMobileNo() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
        // log.info("sms successfully send to "+smsDetails.getMobileNo());
        smsRepository.save(smsDetails);
        // MDC.remove(NotificationConstants.TYPE);
    }

    @Override
    public void sendSmsNotification(String queueName, String message, Map<String, Object> data, String sourceName,
                                    String smsTemplate, String appendUrl) throws Exception {
        String name = getUserName(data);
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        MDC.put(NotificationConstants.USER_NAME, name);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Object> buildSmsBody = new HashMap<>();
        try {
            if (data.get("mvnoId") == null
                    || (data.get("mvnoId").toString().isEmpty() || data.get("mvnoId").toString() == "")) {
                throw new RuntimeException("Mvno id is mandatory.");
            }

            if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                if (ValidateCrudTransactionData
                        .validateStringTypeFieldValue(data.get(RabbitMqConstants.MOBILE_NUMBER).toString())
                        && data.get(RabbitMqConstants.MOBILE_NUMBER).toString().matches("[0-9]+")) {
                    Optional<SmsConfig> smsConfig = Optional.of(new SmsConfig());
                    List<SmsConfig> smsConfigList = getSmsConfigByData(data);
                    if (smsConfigList.isEmpty()) {
                        createFailedSmsAudit(data, queueName, null);
                        throw new CustomException("all sms config has problem please connect the administator", 417);
                    }
                    for (SmsConfig smsConfig1 : smsConfigList) {
                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "sms config" + smsConfig1.getSmsUrl() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                        smsConfig = Optional.ofNullable(smsConfig1);
                        List<SmsConfigMapping> smsConfigMapping = smsConfigMappingRepository.findBySmsConfigIdAndMvnoId(
                                smsConfig.get().getSmsConfigId(), Long.parseLong(data.get("mvnoId").toString()));

                        if (smsConfigMapping == null) {
                            smsConfigMapping = smsConfigMappingRepository.findByMvnoId(1L);
                        }
                        buildSmsBody = buildSmsBody(queueName, data, sourceName, smsTemplate);
                        if (buildSmsBody.get(TEMPLATE_NOT_FOUND) != null) {
                            throw new CustomException("Template not found !!", 417);
                        }
                        boolean isPayloadBased = checkRequestForSms(smsConfigMapping);
                        boolean isContentType = checkForContentType(smsConfigMapping);
                        if ((smsConfig.get().getSmsUrl()).contains("twilio")) {
                            // String FROM_NUMBER = "+19798032515";
                            String accountSid = "";
                            String authToken = "";
                            String fromNo = "";

                            for (int i = 0; i < smsConfigMapping.size(); i++) {
                                SmsConfigMapping smsConfigMappingVo = smsConfigMapping.get(i);
                                if (smsConfigMappingVo.getParameter().equals(NotificationConstants.ACCOUNT_SID)) {
                                    accountSid = smsConfigMappingVo.getValue();
                                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.AUTH_TOKEN)) {
                                    authToken = smsConfigMappingVo.getValue();
                                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.FROM_NUMBER)) {
                                    fromNo = smsConfigMappingVo.getValue();
                                }
                            }
                            MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
                            if (buildSmsBody.get(SMS_CONTENT) != null && !buildSmsBody.get(SMS_CONTENT).toString().isEmpty()
                                    && buildSmsBody.get(SMS_CONTENT).toString() != "") {
                                Twilio.init(accountSid, authToken);
                                if (data.get(RabbitMqConstants.COUNTRY_CODE) != null
                                        && (data.get(RabbitMqConstants.COUNTRY_CODE).toString() != null
                                        && !data.get(RabbitMqConstants.COUNTRY_CODE).toString().isEmpty()
                                        && data.get(RabbitMqConstants.COUNTRY_CODE).toString() != "")) {
                                    Message.creator(
                                            new PhoneNumber(data.get(RabbitMqConstants.COUNTRY_CODE).toString()
                                                    + data.get(RabbitMqConstants.MOBILE_NUMBER).toString()),
                                            new PhoneNumber(fromNo), buildSmsBody.get(SMS_CONTENT).toString()).create();
                                    if (buildSmsBody.get(EVENT_ID) != null
                                            && !buildSmsBody.get(EVENT_ID).toString().isEmpty()
                                            && buildSmsBody.get(EVENT_ID).toString() != "") {
                                        saveSmsNotificationOnSuccess(buildSmsBody.get(SMS_CONTENT).toString(), data,
                                                buildSmsBody.get(EVENT_ID).toString(), sourceName, smsConfig1);
                                    }
                                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Sms successfully sent. Message," + data.get(RabbitMqConstants.MOBILE_NUMBER).toString() + LogConstants.REQUEST_BY + name + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                                } else {
                                    Message.creator(new PhoneNumber(data.get(RabbitMqConstants.MOBILE_NUMBER).toString()),
                                            new PhoneNumber(fromNo), buildSmsBody.get(SMS_CONTENT).toString()).create();
                                    if (buildSmsBody.get(EVENT_ID) != null
                                            && !buildSmsBody.get(EVENT_ID).toString().isEmpty()
                                            && buildSmsBody.get(EVENT_ID).toString() != "") {
                                        saveSmsNotificationOnSuccess(buildSmsBody.get(SMS_CONTENT).toString(), data,
                                                buildSmsBody.get(EVENT_ID).toString(), sourceName, smsConfig1);
                                    }
                                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Sms successfully sent. Message," + data.get(RabbitMqConstants.MOBILE_NUMBER).toString() + LogConstants.REQUEST_BY + name + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//
                                }
                            }
                        } else {
                            String username = "", password = "", sender = "", type = "", product = "", template = "";
                            SSLContext sslContext = SSLContextBuilder.create()
                                    .loadTrustMaterial((x509Certificates, s) -> true)
                                    .build();
                            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

                            CloseableHttpClient httpclient = HttpClients.custom()
                                    .setSSLSocketFactory(sslSocketFactory)
                                    .build();
                            //CloseableHttpClient httpclient = HttpClients.createDefault();
                            String templateKeyName = null;
                            String mobileKeyName = null;
                            String messageKeyName = null;
                            String countryCode = "";
                            URIBuilder uriBuilder = null;
                            String sendurl = null;

                            try {

                                HashMap<String, String> smsDynamicPara = new HashMap<>();
                                smsConfigMapping.forEach(smsConfigMapping1 -> smsDynamicPara
                                        .put(smsConfigMapping1.getParameter(), smsConfigMapping1.getValue()));
                                // creating a list for keys
                                Set<String> keySet = smsDynamicPara.keySet();
                                ArrayList<String> listOfKeys = new ArrayList<String>(keySet);
                                // creating a list for values
                                Collection<String> values = smsDynamicPara.values();
                                ArrayList<String> listOfValues = new ArrayList<>(values);
                                String getwayUrl = smsConfig.get().getSmsUrl().replaceAll("\\s", "");

                                StringBuilder sendSmsUrl = new StringBuilder(getwayUrl);

                                if (isPayloadBased || isContentType) {
                                    CloseableHttpClient client = HttpClients.createDefault();
                                    CloseableHttpResponse response = null;
                                    HashMap<String, String> headers = new HashMap<>();
                                    HashMap<String, String> jsonBody = new HashMap<>();
                                    HashMap<String, Object> finalBody = new HashMap<>();

                                    ObjectMapper objectMapper = new ObjectMapper();

                                    /** Extracting required Headers and jsonBody */
                                    smsDynamicPara.forEach((key, value) -> {
                                        if (key.startsWith("H-")) {
                                            headers.put(key.substring(2), value);
                                        } else if (!CommonConstants.REQUEST_FOR.equalsIgnoreCase(key)) {
                                            jsonBody.put(key, value);
                                        }
                                    });

                                    /** Country code setup */
                                    if (data.get(RabbitMqConstants.COUNTRY_CODE) != null) {
                                        countryCode = data.get(RabbitMqConstants.COUNTRY_CODE).toString();
                                        countryCode = countryCode.replace("+", "");
                                    } else {
                                        countryCode = "";
                                    }

                                    /** Replace variables and build final body */
                                    for (Map.Entry<String, String> entry : jsonBody.entrySet()) {
                                        String value = entry.getValue();
                                        String key = entry.getKey();

                                        if (value.contains("{mobile}")) {
                                            String rawMobile = data.get(RabbitMqConstants.MOBILE_NUMBER).toString();
                                            String finalMobile = rawMobile;

                                            // Check if CountryCode is configured in tblmsmsconfigmapping
                                            String configCountryCode = smsDynamicPara.get("CountryCode"); // Assuming this param name
                                            if (configCountryCode == null) {
                                                configCountryCode = data.get(RabbitMqConstants.COUNTRY_CODE) != null
                                                        ? data.get(RabbitMqConstants.COUNTRY_CODE).toString().replace("+", "")
                                                        : "";
                                            } else {
                                                configCountryCode = configCountryCode.replace("+", "");
                                            }
                                            // Logic: Append country code only if configured and not already present in mobile prefix
                                            if (!configCountryCode.isEmpty()) {
                                                if (!rawMobile.startsWith(configCountryCode)) {
                                                    finalMobile = configCountryCode + rawMobile;
                                                }
                                            }
                                            value = value.replace("{mobile}", finalMobile);
                                            log.info("Final Mobile Number used: " + finalMobile);
                                        }
                                        if (value.contains("{message}")) {
                                            value = value.replace("{message}", buildSmsBody.get(SMS_CONTENT).toString());
                                        }
                                        // Parse JSON object or array if value starts with `{` or `[`
                                        try {
                                            // Special handling for MessageParameters as JSON array
                                            if ("MessageParameters".equalsIgnoreCase(key)) {
                                                // Parse and add as actual List/Array
                                                String unescapedValue = value.replace("\\\"", "\"");
                                                List<Map<String, Object>> messageParams = objectMapper.readValue(
                                                        unescapedValue, new TypeReference<List<Map<String, Object>>>() {}
                                                );
                                                finalBody.put(key, messageParams);
                                            }
                                            // Parse JSON object or array if value starts with `{` or `[`
                                            else if ((value.trim().startsWith("{") && value.trim().endsWith("}")) ||
                                                    (value.trim().startsWith("[") && value.trim().endsWith("]"))) {
                                                finalBody.put(key, objectMapper.readValue(value, Object.class));
                                                log.info("SMS Request Body:\n" + finalBody);
                                            } else {
                                                finalBody.put(key, value);
                                                log.info("SMS Request Body:\n" + finalBody);
                                            }
                                        } catch (Exception e) {
                                            throw new RuntimeException("Failed to parse body for key: " + key + ", value: " + value, e);
                                        }
                                    }

                                    // Logging payload
                                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Payload For Sms is " + finalBody + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                                    // Build request
                                    HttpPost request = new HttpPost(sendSmsUrl.toString());
                                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Send SMS Url is " + request + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                                    /** add headers dynamically */
                                    headers.forEach((headerKey, headerValue) -> {
                                        request.addHeader(headerKey, headerValue);
                                    });

                                    /** Determine content type and set body */
                                    if (isContentType) {
                                        /** if body contains form based url encoded */
                                        String encodedParams = finalBody.entrySet().stream()
                                                .map(entry -> {
                                                    try {
                                                        // Decode first to avoid double encoding
                                                        /*String decodedValue = URLDecoder.decode(entry.getValue().toString(), "UTF-8");
                                                        return URLEncoder.encode(entry.getKey(), "UTF-8") + "=" +
                                                                URLEncoder.encode(decodedValue, "UTF-8");*/
                                                        //Fix Uganda issue
                                                        return URLEncoder.encode(entry.getKey(), "UTF-8") + "=" +
                                                                URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                                                    } catch (Exception e) {
                                                        throw new RuntimeException("Encoding error", e);
                                                    }
                                                })
                                                .collect(Collectors.joining("&"));

                                        request.setEntity(new StringEntity(encodedParams, ContentType.APPLICATION_FORM_URLENCODED));
                                    } else {

                                        /** if body contains json based */
                                        String body = objectMapper.writeValueAsString(finalBody);
                                        StringEntity entity = new StringEntity(body);
                                        /** add body in request */
                                        request.setEntity(entity);
                                    }
                                     log.info("🔄 Sending SMS Request");
                                    log.info("URL: {}", sendSmsUrl);
                                    log.info("Headers: {}", headers);
                                    log.info("Request Body: {}", request);


                                    response = client.execute(request);
                                    HttpEntity responseEntity = response.getEntity();
                                    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");

                                    // Log response
                                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Response Body is " + responseBody + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                                    // Save success audit if event ID exists
                                    if (buildSmsBody.get(EVENT_ID) != null && !buildSmsBody.get(EVENT_ID).toString().isEmpty() && buildSmsBody.get(EVENT_ID).toString() != "") {
                                        saveSmsNotificationOnSuccess(buildSmsBody.get(SMS_CONTENT).toString(), data,
                                                buildSmsBody.get(EVENT_ID).toString(), sourceName, smsConfig1);
                                    }

                                    // Kafka tracking for task/ETR queues
                                    if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                                        HashMap<String, Object> etrAuditData = new HashMap<>();
                                        etrAuditData.putAll((HashMap<String, Object>) data);
                                        data.put("notificationStatus", "Success");
                                        data.put("notificationMode", "SMS");
                                        TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                                        kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
                                    }
                                    if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                                        HashMap<String, Object> etrAuditData = new HashMap<>();
                                        etrAuditData.putAll((HashMap<String, Object>) data);
                                        data.put("notificationStatus", "Success");
                                        data.put("notificationMode", "SMS");
                                        TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                                        kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
                                    }
                                } else {
                                    uriBuilder = new URIBuilder(getwayUrl);
                                    for (int i = 0; i < listOfKeys.size(); i++) {
                                        // uriBuilder.addParameter(listOfKeys.get(i),listOfValues.get(i));
                                        if (i == 0) {
                                            sendSmsUrl.append(listOfKeys.get(i)).append("=").append(listOfValues.get(i));
                                        } else {
                                            sendSmsUrl.append("&").append(listOfKeys.get(i)).append("=")
                                                    .append(listOfValues.get(i));
                                        }
                                        if (listOfValues.get(i).equals("{template}")) {
                                            templateKeyName = listOfKeys.get(i);
                                        }

                                    }
                                    if (smsDynamicPara.get("CountryCode") != null) {
                                        if (data.get(RabbitMqConstants.COUNTRY_CODE) != null
                                                && smsDynamicPara.get("CountryCode").equals("yes")) {
                                            countryCode = data.get(RabbitMqConstants.COUNTRY_CODE).toString();
                                            // countryCode = countryCode.replaceAll("[^a-zA-Z0-9]", "");
                                            countryCode = countryCode.replace("+", "");
                                        }
                                    } else {
                                        countryCode = "";
                                    }
                                    sendurl = sendSmsUrl.toString();

                                    HttpUriRequest httppost;
                                    HttpPost httpPost;
                                    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(appendUrl)) {

                                        sendurl = sendurl.replaceAll("\\s", "");
                                        sendurl = sendurl.replaceAll("&CountryCode=yes", "");
                                        sendurl = sendurl.replaceAll("\\?CountryCode=yes", "?");
                                        sendurl = sendurl.replace("{mobile}",
                                                countryCode + data.get(RabbitMqConstants.MOBILE_NUMBER));
                                        sendurl = sendurl.replace("{message}",
                                                URLEncoder.encode(buildSmsBody.get(SMS_CONTENT).toString()));


                                        httppost = RequestBuilder.get().setUri(sendurl).build();


                                    } else {

                                        sendurl = sendurl.replaceAll("\\s", "");
                                        sendurl = sendurl.replaceAll("&CountryCode=yes", "");
                                        sendurl = sendurl.replaceAll("\\?CountryCode=yes", "?");
                                        appendUrl = appendUrl.replace("&template=", "");
                                        sendurl = sendurl.replace("{template}", appendUrl);
                                        sendurl = sendurl.replace("{mobile}",
                                                countryCode + data.get(RabbitMqConstants.MOBILE_NUMBER));
                                        sendurl = sendurl.replace("{message}",
                                                URLEncoder.encode(buildSmsBody.get(SMS_CONTENT).toString()));


                                    }
                                    log.info("Created send url for trigger sms : " + sendurl);
                                    CloseableHttpResponse response = null;
                                    String responseBody = new String();
                                    if (smsDynamicPara.get("Type") != null && !smsDynamicPara.get("Type").equalsIgnoreCase("") && smsDynamicPara.get("Type").equalsIgnoreCase("GET")) {
                                        log.info("$$$ http get called $$$");
                                        HttpGet httpGet = new HttpGet(sendurl);
                                        log.info("Send sms URL is  " + httpGet);
                                        String requestBody = "{\"key\": \"value\"}";
                                        response = httpclient.execute(httpGet);

                                        // Get the response entity
                                        HttpEntity entity = response.getEntity();
                                        // Check if the entity is not null
                                        if (entity != null) {
                                            // Convert the entity to a string and print it
                                            responseBody = EntityUtils.toString(entity);
                                            System.out.println("Response body: " + responseBody);
                                            // Consume the entity to release the resources
                                            EntityUtils.consume(entity);
                                        }

                                    } else {
                                        httpPost = new HttpPost(sendurl);
                                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Send SMS Url is" + httpPost + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//								System.out.println("Send sms URL is  " + httpPost);

                                        String requestBody = "{\"key\": \"value\"}";
                                        StringEntity requestEntity = new StringEntity(requestBody);
                                        httpPost.setEntity(requestEntity);

                                        response = httpclient.execute(httpPost);
                                        // Process the response
                                        HttpEntity responseEntity = response.getEntity();
                                        responseBody = EntityUtils.toString(responseEntity);

//								// Print the response
                                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Response Body is " + requestBody + smsConfig1.getSmsUrl() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                                        // Consume the response entity to release resources
                                        EntityUtils.consume(responseEntity);
                                    }


                                    if (buildSmsBody.get(EVENT_ID) != null && !buildSmsBody.get(EVENT_ID).toString().isEmpty() && buildSmsBody.get(EVENT_ID).toString() != "") {
                                        saveSmsNotificationOnSuccess(buildSmsBody.get(SMS_CONTENT).toString(), data,
                                                buildSmsBody.get(EVENT_ID).toString(), sourceName, smsConfig1);
                                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                                            HashMap<String, Object> etrAuditData = new HashMap<>();
                                            etrAuditData.putAll((HashMap<String, Object>) data);
                                            //data.put("notificationMessage", buildSmsBody.get(SMS_CONTENT));
                                            data.put("notificationStatus", "Success");
                                            data.put("notificationMode", "SMS");
                                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                                            //Gson gson = new Gson();
                                            //gson.toJson(ticketETRAuditMessage);
//										messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
                                        }
                                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                                            HashMap<String, Object> etrAuditData = new HashMap<>();
                                            etrAuditData.putAll((HashMap<String, Object>) data);
                                            //data.put("notificationMessage", buildSmsBody.get(SMS_CONTENT));
                                            data.put("notificationStatus", "Success");
                                            data.put("notificationMode", "SMS");
                                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                                            //Gson gson = new Gson();
                                            //gson.toJson(ticketETRAuditMessage);
//										messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
                                        } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {

                                            //put check condition for etr here and then check add status success
                                            HashMap<String, Object> etrAuditData = new HashMap<>();
                                            etrAuditData.putAll((HashMap<String, Object>) data);
                                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                                            data.put("notificationStatus", "Success");
                                            data.put("notificationMode", "SMS");
                                            TicketAuditMessage ticketAuditMessage = new TicketAuditMessage(data);

//										messageSender.send(ticketAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                                            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)) {
                                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_SUCCESS));
                                            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {
                                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_SUCCESS));
                                            }

                                        }

                                    }
                                    try {
                                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Entity is" + response.getEntity().toString() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                                        // System.out.println(EntityUtils.toString(response.getEntity()));
                                    } finally {

                                        response.close();
                                    }
                                    break;
                                }
                            } catch (Throwable e) {
                                /**
                                 * Configurable HA Support
                                 * Sms config will not be disabled if SMSHA is false
                                 * **/
                                if (SMSHA.equalsIgnoreCase("true")) {
                                    smsConfig1.setConfigStatus(false);
                                } else {
                                    smsConfig1.setConfigStatus(true);
                                }
                                smsConfigRepository.save(smsConfig1);
                                createFailedSmsAudit(data, queueName, smsConfig1);

                            } finally {
                                httpclient.close();
                            }
                        }
                    }
                }
            } else {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Mobile number is not valid so sms is not going to send," + data.get(RabbitMqConstants.MOBILE_NUMBER).toString() + LogConstants.REQUEST_BY + name + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            }
        } catch (CustomException e) {
            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Template not found for bu " + data.get("buId") + " and mvno : " + data.get("mvnoId") + LogConstants.REQUEST_BY + name + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
        } catch (Throwable e) {
            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Sms failed. Mobile number " + data.get(RabbitMqConstants.MOBILE_NUMBER).toString() + LogConstants.REQUEST_BY + name + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());

            saveSmsNotificationOnFailure(queueName, data, sourceName, smsTemplate, null);
            if (queueName.equals(RabbitMqConstants.QUEUE_TASK_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildSmsBody.get(SMS_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "SMS");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//				messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_FAIL));
            }

            if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildSmsBody.get(SMS_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "SMS");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//				messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_FAIL));
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)) {

                //put check condition for etr here and then check add status success
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "sms");
                TicketAuditMessage ticketAuditMessage = new TicketAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//				messageSender.send(ticketAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)) {
                    kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_FAIL));
                } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {
                    kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_FAIL));
                }
            }

            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.USER_NAME);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }

    }

    public static String getEventNameBasedOnQueueName(String queueName) {
        try {
            String eventName = "";
            if (queueName.equals(RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION)) {
                eventName = NotificationConstants.Inventory_Event_Name.DEVICE_INPUT_PORT_CONSUMED_PERCENTAGE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION)) {
                eventName = NotificationConstants.Inventory_Event_Name.INVENTORY_ASSIGNMENT_SUCCESS_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_INVENTORY_REQUEST_TO_NOTIFICATION)) {
                eventName = NotificationConstants.Inventory_Event_Name.INVENTORY_REQUEST_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_INVENTORY_FULFILMENT_TO_NOTIFICATION)) {
                eventName = NotificationConstants.Inventory_Event_Name.INVENTORY_FULFILMENT_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_INVENTORY_THRESHOLD_NOTIFICATION)) {
                eventName = NotificationConstants.Inventory_Event_Name.INVENTORY_THRESHOLD_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_LOGIN_SUCCESS)) {
                eventName = NotificationConstants.LOGIN_SUCCESS_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_LOGIN_FAILURE)) {
                eventName = NotificationConstants.LOGIN_FAILURE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS)) {
                eventName = NotificationConstants.CUSTOMER_REGISTRATION_SUCCESS_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_REGISTRATION_FAILURE)) {
                eventName = NotificationConstants.CUSTOMER_REGISTRATION_FAILURE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_OTP_GENERATION)) {
                eventName = NotificationConstants.OTP_GENERATED_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_VOUCHERCODE)) {
                eventName = NotificationConstants.SEND_VOUCHERCODE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_USED_QUOTA)) {
                eventName = NotificationConstants.USED_QUOTA_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS)) {
                eventName = NotificationConstants.CUST_APPROVAL;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL)) {
                eventName = NotificationConstants.CUST_REJECT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS)) {
                eventName = NotificationConstants.CUST_RECHARGE_SUCCESS;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS)) {
                eventName = NotificationConstants.CUST_RENEW_SUCCESS;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS)) {
                eventName = NotificationConstants.CUST_REG_SUCCESS;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL)) {
                eventName = NotificationConstants.CUST_REG_FAIL;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK)) {
                eventName = NotificationConstants.CUSTOMER_PAYMENT_LINK;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS)) {
                eventName = NotificationConstants.CUSTOMER_PAYMENT_SUCCESS;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS)) {
                eventName = NotificationConstants.TICKET_ASSIGN_SUCCESS;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING)) {
                eventName = NotificationConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION)) {
                eventName = NotificationConstants.CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION)) {
                eventName = NotificationConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF)) {
                eventName = NotificationConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER)) {
                eventName = NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF)) {
                eventName = NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF)) {
                eventName = NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF)) {
                eventName = NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_STAFF_SEND_STATUS)) {
                eventName = NotificationConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF)) {
                eventName = NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF)) {
                eventName = NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF)) {
                eventName = NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF)) {
                eventName = NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE)) {
                eventName = NotificationConstants.CUSTOMER_STATUS_CHANGE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE)) {
                eventName = NotificationConstants.WORKFLOW_ACTION_ASSIGN;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)) {
                eventName = NotificationConstants.TAT_NOTIFICATION_TO_TEAM;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {
                eventName = NotificationConstants.TAT_NOTIFICATION_TO_TEAM_FOR_TASK;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG)) {
                eventName = NotificationConstants.FOLLOWUP_REMARK_MSG;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG)) {
                eventName = NotificationConstants.PROBLEM_DOMAIN_EVENTNAME;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_TASK_CATEGORY_CHANGE_MSG)) {
                eventName = NotificationConstants.CATEGORY_CHANGE_EVENTNAME_FOR_TASK;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                eventName = NotificationConstants.TICKET_ETR;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TASK_ETR)) {
                eventName = NotificationConstants.TASK_ETR;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER)) {
                eventName = NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF)) {
                eventName = NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF)) {
                eventName = NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF)) {
                eventName = NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER)) {
                eventName = NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF)) {
                eventName = NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF)) {
                eventName = NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF)) {
                eventName = NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS)) {
                eventName = NotificationConstants.TICKET_CREATION;
            } else if (queueName.equals(RabbitMqConstants.TASK_CREATION_NOTIFICATION)) {
                eventName = NotificationConstants.TASK_CREATION_NOTIFICATION;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG)) {
                eventName = NotificationConstants.TICKET_RESCHEDULE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER)) {
                eventName = NotificationConstants.TICKET_TAT_REMINDER_NOTIFICATION;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER)) {
                eventName = NotificationConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT)) {
                eventName = NotificationConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION)) {
                eventName = NotificationConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF)) {
                eventName = NotificationConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_STATUS_INACTIVATE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_DOCUMENT_VERIFICATION_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_SERVICE_ACTIVE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_SERVICE_INACTIVE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_CHANGE_PASSWORD_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT)) {
                eventName = NotificationConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_PAYMENT_VERIFICATION_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_TICKET_CLOSE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)) {
                eventName = NotificationConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION)) {
                eventName = NotificationConstants.LEAD_CREATION_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE)) {
                eventName = NotificationConstants.TAT_NOTIFICATION;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE)) {
                eventName = NotificationConstants.TASK_TAT_NOTIFICATION;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER)) {
                eventName = NotificationConstants.TICKET_REMARK_CUSTOMER_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER)) {
                eventName = NotificationConstants.CUSTOMER_QUOTA_USAGE_NOTIFICATION_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER)) {
                eventName = NotificationConstants.TICKET_EXTERNAL_REMARK_CUSTOMER_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_EXTERNAL_TASK_REMARK)) {
                eventName = NotificationConstants.TASK_EXTERNAL_REMARK_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION)) {
                eventName = NotificationConstants.CUSTOMER_INVOICE_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER)) {
                eventName = NotificationConstants.CUSTOMER_QUOTA_EXHAUST_NOTIFICATION_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF)) {
                eventName = NotificationConstants.STAFF_TICKET_ALERT_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_TASK_ALERT_TO_STAFF)) {
                eventName = NotificationConstants.STAFF_TASK_ALERT_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER)) {
                eventName = NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER)) {
                eventName = NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_REGISTERED_CUSTOMER;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF)) {
                eventName = NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER_STAFF;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF)) {
                eventName = NotificationConstants.STAFF_UNPICK_TICKET_ALERT_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE)) {
                eventName = NotificationConstants.TAT_NOTIFICATION;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE)) {
                eventName = NotificationConstants.TAT_NOTIFICATION;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE)) {
                eventName = NotificationConstants.TAT_NOTIFICATION;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION)) {
                eventName = NotificationConstants.MVNO_DOCUMENT_DUNNING_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION)) {
                eventName = NotificationConstants.MVNO_DEACTIVATION_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION)) {
                eventName = NotificationConstants.MVNO_PAYMENT_REMAINDER_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION)) {
                eventName = NotificationConstants.MVNO_PAYMENT_EXPIRY_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION)) {
                eventName = NotificationConstants.PLAN_EXPIRY_EVENT;
            } else if (queueName.equals(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON)) {
                eventName = NotificationConstants.LOGIN_OTP_EVENT;
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION)) {
                eventName = NotificationConstants.CHANGE_PLAN_EVENT;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.FOLLOWUP_TASK_MSG)) {
                eventName = NotificationConstants.TASK_REMARK_EVENT;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.TASK_RESCHEDUKE_NOTIFICATION)) {
                eventName = NotificationConstants.TASK_RESCHEDUKE_EVENT;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.TASK_CLOSED_EVENT)) {
                eventName = NotificationConstants.TASK_CLOSED_EVENT;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.TASK)) {
                eventName = NotificationConstants.TASK;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.TASK_UPDATE)) {
                eventName = NotificationConstants.TASK_UPDATE;
            } else if (queueName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)) {
                eventName = NotificationConstants.TAT_NOTIFICATION;
            }else if (queueName.equalsIgnoreCase(NotificationConstants.QUEUE_INSUFFICIENT_WALLET)) {
                eventName = NotificationConstants.INSUFFICIENT_WALLET_EVENT;
            }else if (queueName.equalsIgnoreCase(NotificationConstants.QUEUE_AUTO_RENEWAL_PREFERENCE_CHANGED)) {
                eventName = NotificationConstants.AUTO_RENEWAL_PREFERENCE_CHANGED_EVENT;
            }
            else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_BSS_CHILD_CUSTOMER_REGISTRATION_SUCCESS)) {
                eventName = NotificationConstants.CHILD_CUSTOMER_REGISTRATION_SUCCESS_EVENT;
            }

            return eventName;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildSmsBody(String queueName, Map<String, Object> data, String sourceName,
                                             String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            String smsContent = "";
            String eventName = getEventNameBasedOnQueueName(queueName);
            if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.OTP_GENERATED_EVENT)) {
                returnData = getSmsContentForOtpEvent(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.SEND_VOUCHERCODE_EVENT)) {
                returnData = getSmsContentForVoucherEvent(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.SEND_VOUCHERCODE_EVENT)) {
//                returnData = (data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_APPROVAL)) {
                returnData = getSmsContentForCustApprovalSuccessEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);

            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_REJECT)) {
                returnData = getSmsContentForCustApprovalFailEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);

            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_REG_SUCCESS)) {
                returnData = getSmsContentForCustRegApprovalEvent(data, smsContent, eventName, sourceName, smsTemplate);

            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_REG_FAIL)) {
                returnData = getSmsContentForCustRegFailEvent(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_RECHARGE_SUCCESS)) {
                returnData = getSmsContentForCustRechargeApprovalEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);

            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUST_RENEW_SUCCESS)) {
                returnData = getSmsContentForCustRenewApprovalEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_LINK)) {
                returnData = getSmsContentForCustPaymentLinkEvent(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_SUCCESS)) {
                returnData = getSmsContentForCustPaymentSuccessEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_ASSIGN_SUCCESS)) {
                returnData = getSmsContentForAssignTicketToStaffEvent(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER)) {
                returnData = getSmsCustomerDunning(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER)) {
                returnData = getSmsCustomerDeactivation(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER)) {
                returnData = getSmsCustomerOtpRegistration(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER)) {
                returnData = getSmsContentForDocumentDunning(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForFollowUpReminderForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                returnData = getSmsContentForFollowUpReminderForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForFollowUpOverDueForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                returnData = getSmsContentForFollowUpOverDueForParentStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER)) {
                returnData = getSmsContentForStaffStatus(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForNoLeadFollowUpReminderForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                returnData = getSmsContentForNoLeadFollowUpReminderForParentStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForNoFollowUpReminderForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                returnData = getSmsContentForNoFollowUpReminderForParentStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_STATUS_CHANGE_EVENT)) {
                returnData = buildticketstatuschangetocustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_ETR)) {

                if (!(Boolean) data.get("isTemplateDynamic"))
                    returnData = buildticketETR(data, smsContent, eventName, sourceName,
                            smsTemplate);
                else
                    returnData = buildticketETRDynamic(data, smsContent, eventName, sourceName,
                            smsTemplate);
            }
            //START
            else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForCafFollowUpReminderForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                returnData = getSmsContentForCafFollowUpReminderForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForCafFollowUpOverDueForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                returnData = getSmsContentForCafFollowUpOverDueForParentStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForTicketFollowUpReminderForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                returnData = getSmsContentForTicketFollowUpReminderForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF)) {
                returnData = getSmsContentForTicketFollowUpOverDueForStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                returnData = getSmsContentForTicketFollowUpOverDueForParentStaff(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_CREATION)) {
                //This message is not in used
                returnData = getSmsContentForTicketCreationForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_RESCHEDULE_EVENT)) {
                returnData = getSmsContentForTicketRescheduleMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_TAT_REMINDER_NOTIFICATION)) {
                returnData = buildTicketReminderNotificationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION)) {
                returnData = buildTicketOverDueReminderMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER)) {
                returnData = buildDunningAdvanceNotificationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_STATUS_INACTIVATE_EVENT)) {
                returnData = buildCustStatusInActiveNotificationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER)) {
                returnData = buildDunningPartenerDocumentMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER)) {
                returnData = buildDunningPartenerDocumentMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_DOCUMENT_VERIFICATION_EVENT)) {
                returnData = buildCustDocumentVerificationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_SERVICE_ACTIVE_EVENT)) {
                returnData = buildCustServiceActiveMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_SERVICE_INACTIVE_EVENT)) {
                returnData = buildCustServiceInActiveMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_CHANGE_PASSWORD_EVENT)) {
                returnData = buildCustChangePasswordMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT)) {
                returnData = buildCustAddressShiftingMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT)) {
                returnData = buildCustAddressShiftingMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_VERIFICATION_EVENT)) {
                returnData = buildCustPaymentVerificationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_TICKET_CLOSE_EVENT)) {
                returnData = buildCustTicketCloseMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.LEAD_CREATION_EVENT)) {
                returnData = buildLeadCreationMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TAT_NOTIFICATION)) {
                returnData = buildTatNotificationToTeam(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TASK_TAT_NOTIFICATION)) {
                returnData = buildTatNotificationToTeam(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)) {
                returnData = buildTatNotificationToTeam(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_REMARK_CUSTOMER_EVENT)) {
                returnData = buildTicketRemarkToCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_QUOTA_USAGE_NOTIFICATION_EVENT)) {
                returnData = buildCustomerQuotaToCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_QUOTA_EXHAUST_NOTIFICATION_EVENT)) {
                returnData = buildCustomerQuotaToCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TICKET_EXTERNAL_REMARK_CUSTOMER_EVENT)) {
                returnData = buildTicketRemarkToCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TASK_EXTERNAL_REMARK_EVENT)) {
                returnData = buildTicketRemarkToCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.CUSTOMER_INVOICE_EVENT)) {
                returnData = buildCustomerInvoiceMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.STAFF_TICKET_ALERT_EVENT)) {
                returnData = buildTicketAlertMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.STAFF_TASK_ALERT_EVENT)) {
                returnData = buildTicketAlertMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.STAFF_UNPICK_TICKET_ALERT_EVENT)) {
                returnData = buildUnpickTicketAlertMessage(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.WORKFLOW_ACTION_ASSIGN)) {
                returnData = buildWorkflowActionAssign(data, smsContent, eventName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.MVNO_DOCUMENT_DUNNING_EVENT)) {
                returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.MVNO_DEACTIVATION_EVENT)) {
                returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.MVNO_PAYMENT_REMAINDER_EVENT)) {
                returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.MVNO_PAYMENT_EXPIRY_EVENT)) {
                returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.MVNO_PAYMENT_EXPIRY_EVENT)) {
                returnData = buildSmsBodyForPlanExpiryEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.CHANGE_PLAN_EVENT)) {
                returnData = buildSmsBodyForChangePlan(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.LOGIN_OTP_EVENT)) {
                returnData = getSmsContentForOtpEvent(data, smsContent, eventName, sourceName, smsTemplate);
            } else if (eventName != null && eventName != "" && !eventName.isEmpty()
                    && eventName.equals(NotificationConstants.TASK_CREATION_NOTIFICATION)) {
                //This message is not in used
                returnData = getSmsContentForTaskCreationForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName.equalsIgnoreCase(NotificationConstants.TASK_RESCHEDUKE_NOTIFICATION)) {
                returnData = getSmsContentForTaskCreationForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName.equalsIgnoreCase(NotificationConstants.TASK)) {
                returnData = getSmsContentForTaskCreationForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            } else if (eventName.equals(NotificationConstants.CHILD_CUSTOMER_REGISTRATION_SUCCESS_EVENT)) {
                returnData = buildEmailBodyForChildCustRegistrationEvent(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_ASSIGNMENT_SUCCESS_EVENT)) {
                returnData = inventorySmsService.buildInventoryAssignmentForStaffMessage(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_REQUEST_EVENT)) {
                returnData = inventorySmsService.buildInventoryRequestMessage(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_FULFILMENT_EVENT)) {
                returnData = inventorySmsService.buildInventoryFulfilmentMessage(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_THRESHOLD_EVENT)) {
                returnData = inventorySmsService.buildInventoryThresholdMessage(data, smsContent, eventName, smsTemplate);
            } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.DEVICE_INPUT_PORT_CONSUMED_PERCENTAGE_EVENT)) {
                returnData = inventorySmsService.buildInventoryDeviceInputPortConsumedPercentageMessage(data, smsContent, eventName, smsTemplate);
            }else if (eventName.equalsIgnoreCase(NotificationConstants.QUEUE_INSUFFICIENT_WALLET)) {
                returnData = getSmsContentForInsufficientWalletForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            }else if (eventName.equalsIgnoreCase(NotificationConstants.QUEUE_AUTO_RENEWAL_PREFERENCE_CHANGED)) {
                returnData = getSmsContentForInsufficientWalletForCustomer(data, smsContent, eventName, sourceName,
                        smsTemplate);
            }
            else {
                returnData = getSmsContent(data, smsContent, eventName, sourceName, smsTemplate);
            }
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private Map<String, Object> buildSmsBodyForChangePlan(Map<String, Object> data, String smsContent, String eventName, String smsTemplate) {

        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_OLD_PLAN) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{oldPlanName\\}",
                            data.get(RabbitMqConstants.CUST_OLD_PLAN).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NEW_PLAN) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{newPlanName\\}",
                            data.get(RabbitMqConstants.CUST_NEW_PLAN).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRY_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.VALIDITY_DAYS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{validity\\}",
                            data.get(RabbitMqConstants.VALIDITY_DAYS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.VALIDITY_UNITS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{validityUnits\\}",
                            data.get(RabbitMqConstants.VALIDITY_UNITS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // Parse the input date string
                    Date date = inputFormat.parse(data.get(RabbitMqConstants.EXPIRY_DATE).toString());

                    // Create a SimpleDateFormat object for formatting output date
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

                    // Format the parsed date to desired format
                    String outputDateStr = outputFormat.format(date);
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            outputDateStr);
                }
                smsContent = emailTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, null, smsTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForVoucherEvent(Map<String, Object> data, String smsContent,
                                                             String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                if (smsTemplate != null) {
                    String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                    if (smsTemplateData != null) {
                        if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                            smsTemplateData = smsTemplateData.replaceAll("\\{mobilenumber\\}",
                                    data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                        }
                        if (data.get(RabbitMqConstants.VOUCHER) != null) {
                            smsTemplateData = smsTemplateData.replaceAll("\\{Voucher\\}",
                                    data.get(RabbitMqConstants.VOUCHER).toString());
                        }
                        smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                        smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                        smsContent = smsTemplateData;
                    }
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContent(Map<String, Object> data, String smsContent, String eventName,
                                              String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.USER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                                data.get(RabbitMqConstants.USER_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                                data.get(RabbitMqConstants.PASSWORD).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForOtpEvent(Map<String, Object> data, String smsContent, String eventName,
                                                         String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.USER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                                data.get(RabbitMqConstants.USER_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.OTP) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{otp\\}",
                                data.get(RabbitMqConstants.OTP).toString());
                    }
                    if (data.get(RabbitMqConstants.DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{datetime\\}",
                                data.get(RabbitMqConstants.DATE_TIME).toString());
                    }
                    if (data.get(RabbitMqConstants.TIME_FRAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{timeframe\\}",
                                data.get(RabbitMqConstants.TIME_FRAME).toString());
                    }

                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustApprovalSuccessEvent(Map<String, Object> data, String smsContent,
                                                                         String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.TEAM_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{approverTeam\\}",
                                data.get(RabbitMqConstants.TEAM_NAME).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustApprovalFailEvent(Map<String, Object> data, String smsContent,
                                                                      String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.TEAM_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{approverTeam\\}",
                                data.get(RabbitMqConstants.TEAM_NAME).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustRegApprovalEvent(Map<String, Object> data, String smsContent,
                                                                     String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                                data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                    }
                    if (data.get(RabbitMqConstants.REGISTRATION_DATE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{registrationDate\\}",
                                data.get(RabbitMqConstants.REGISTRATION_DATE).toString());
                    }
                    if (data.get(RabbitMqConstants.PLAN_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{planName\\}",
                                data.get(RabbitMqConstants.PLAN_NAME).toString());
                    }
                    String accountNumber = data.getOrDefault(RabbitMqConstants.ACCOUNT_NUMBER, "").toString();
                    smsTemplateData = smsTemplateData.replaceAll("\\{accountNumber\\}", accountNumber);

                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustRegFailEvent(Map<String, Object> data, String smsContent,
                                                                 String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                                data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustRechargeApprovalEvent(Map<String, Object> data, String smsContent,
                                                                          String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_PLAN) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{plan\\}",
                                data.get(RabbitMqConstants.CUST_PLAN).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustRenewApprovalEvent(Map<String, Object> data, String smsContent,
                                                                       String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_fullNAME) != null) {
                        String fullName = data.get(RabbitMqConstants.CUST_fullNAME).toString().trim();

                        // Define the prefixes to remove
                        String[] prefixes = {"Mr ", "Ms ", "Mrs ", "Miss ", "M/s ", "Dear "};

                        // Remove any prefix if it matches at the start (case insensitive)
                        for (String prefix : prefixes) {
                            if (fullName.toLowerCase().startsWith(prefix.toLowerCase())) {
                                fullName = fullName.substring(prefix.length()).trim();
                                break;  // Remove only the first matching prefix
                            }
                        }
                        smsTemplateData = smsTemplateData.replaceAll("\\{customername\\}", fullName);
                    }

                    if (data.get(RabbitMqConstants.CUST_PLAN) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{plan\\}",
                                data.get(RabbitMqConstants.CUST_PLAN).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustPaymentLinkEvent(Map<String, Object> data, String smsContent,
                                                                     String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{currencySymbol\\}",
                                data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{paymentAmount\\}",
                                data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_URL) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{url\\}",
                                data.get(RabbitMqConstants.CUSTMR_URL).toString());
                    }
                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCustPaymentSuccessEvent(Map<String, Object> data, String smsContent,
                                                                        String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_USER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTMR_USER_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_MODE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{paymentMode\\}",
                                data.get(RabbitMqConstants.CUSTMR_PAYMENT_MODE).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{paymentAmount\\}",
                                data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{currencySymbol\\}",
                                data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_ID) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerId\\}",
                                data.get(RabbitMqConstants.CUSTMR_ID).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_USER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                                data.get(RabbitMqConstants.CUSTMR_USER_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{paymentReciptNo\\}",
                                data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{paymentDate\\}",
                                data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE).toString());
                    }
                    if (data.get(RabbitMqConstants.PLAN_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{planname\\}",
                                data.get(RabbitMqConstants.PLAN_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                                data.get(RabbitMqConstants.PASSWORD).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForAssignTicketToStaffEvent(Map<String, Object> data, String smsContent,
                                                                         String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.ASSIGN_TEAM_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{team\\}",
                                data.get(RabbitMqConstants.ASSIGN_TEAM_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CASE_FOLLOW_UPDATE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{Followupdate\\}",
                                data.get(RabbitMqConstants.CASE_FOLLOW_UPDATE).toString());
                    }
                    if (data.get(RabbitMqConstants.EVENT_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{eventName\\}",
                                data.get(RabbitMqConstants.EVENT_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{Assigndatetime\\}",
                                data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
                    }
                    if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }
                    if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }
                    // smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}",
                    // NotificationConstants.SENDER);
                    // smsTemplateData = smsTemplateData.replaceAll("\\{web\\}",
                    // NotificationConstants.WEB);

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
                // returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            }

            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsCustomerDunning(Map<String, Object> data, String smsContent, String eventName,
                                                      String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.AMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{amount\\}",
                                data.get(RabbitMqConstants.AMOUNT).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsCustomerOtpRegistration(Map<String, Object> data, String smsContent,
                                                              String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{UserName\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{Password\\}",
                                data.get(RabbitMqConstants.PASSWORD).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsCustomerDeactivation(Map<String, Object> data, String smsContent,
                                                           String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.AMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{amount\\}",
                                data.get(RabbitMqConstants.AMOUNT).toString()); // Float.toString(amount)
                    }
                    if (data.get(RabbitMqConstants.CUST_REMARKS) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{remarks\\}",
                                data.get(RabbitMqConstants.CUST_REMARKS).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_PLANS_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{planname\\}",
                                data.get(RabbitMqConstants.CUST_PLANS_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_DATE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{date\\}",
                                data.get(RabbitMqConstants.CUST_DATE).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForDocumentDunning(Map<String, Object> data, String smsContent,
                                                                String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForFollowUpOverDueForStaff(Map<String, Object> data, String smsContent,
                                                                        String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForFollowUpOverDueForParentStaff(Map<String, Object> data,
                                                                              String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForNoLeadFollowUpReminderForStaff(Map<String, Object> data,
                                                                               String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForNoFollowUpReminderForStaff(Map<String, Object> data,
                                                                           String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupName\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForNoLeadFollowUpReminderForParentStaff(Map<String, Object> data,
                                                                                     String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForNoFollowUpReminderForParentStaff(Map<String, Object> data,
                                                                                 String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupName\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForFollowUpReminderForStaff(Map<String, Object> data, String smsContent,
                                                                         String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForFollowUpReminderForCustomer(Map<String, Object> data, String smsContent,
                                                                            String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveSmsNotificationOnSuccess(String message, Map<String, Object> data, String eventId,
                                              String sourceName, SmsConfig smsConfig) {
        try {
            Long eventIdLong = Long.parseLong(eventId);
            Sms smsVo = new Sms();
            smsVo.setMobileNo(data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
            Optional<Event> eventVo = eventRepository.findById(eventIdLong);
            smsVo.setEventId(eventVo.get().getEventId());
            smsVo.setEventName(eventVo.get().getEventName());
            // smsVo.setEventId(eventIdLong);
            smsVo.setMessage(message);
            smsVo.setDate(LocalDateTime.now());
            smsVo.setStatus(SENT);
            smsVo.setCreateDate(LocalDateTime.now());
            smsVo.setLastModifiedDate(LocalDateTime.now());
            if (sourceName != null) {
                smsVo.setSourceName(sourceName);
            } else {
                smsVo.setSourceName("Savbill BSS API GATEWAY");
            }
            if (data.get("cprId") != null) {
                Long cprId = Long.valueOf(data.get("cprId").toString());
                smsVo.setCprId(cprId);
            }

            if (data.get(RabbitMqConstants.COUNTRY_CODE) != null) {
                smsVo.setCountryCode(data.get(RabbitMqConstants.COUNTRY_CODE).toString());
            }
            if (data.get("mvnoId") != null
                    && (!data.get("mvnoId").toString().isEmpty() && data.get("mvnoId").toString() != "")) {
                if (data.get("buId") == null
                        || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                    smsVo.setSmsConfigId(smsConfig.getSmsConfigId());
                } else {
                    Optional<SmsConfig> optionalSmsConfig = Optional.ofNullable(smsConfig);
                    if (optionalSmsConfig.isPresent()) {
                        smsVo.setSmsConfigId(optionalSmsConfig.get().getSmsConfigId());
                    } else {
                        smsVo.setSmsConfigId(optionalSmsConfig.get().getSmsConfigId());
                    }
                }
                smsVo.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));
                //add buid support
                if (data.get("buId") == null || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                    smsVo.setBuId(null);
                } else {
                    smsVo.setBuId(Long.parseLong(data.get("buId").toString()));
                }

            }
            smsRepository.save(smsVo);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveSmsNotificationOnFailure(String queueName, Map<String, Object> data, String sourceName,
                                              String smsTemplate, String evntName) {
        try {
            String eventName = null;
            Boolean isTemplateNotFound = false;
            if (evntName == null) {
                eventName = getEventNameBasedOnQueueName(queueName);
            } else {
                eventName = evntName;
                isTemplateNotFound = true;
            }

            if (eventName != null && eventName != "" && !eventName.isEmpty()) {
                Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
                if (optionalEvent.isPresent()) {
                    Sms smsVo = new Sms();
                    smsVo.setMobileNo(data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    Optional<Event> eventVo = eventRepository.findById(optionalEvent.get().getEventId());
                    smsVo.setEventId(eventVo.get().getEventId());
                    // smsVo.setEventId(optionalEvent.get().getEventId());
                    if (!isTemplateNotFound) {
                        Map<String, Object> buildSmsBody = buildSmsBody(queueName, data, sourceName, smsTemplate);
                        if (buildSmsBody.get(SMS_CONTENT) != null && !buildSmsBody.get(SMS_CONTENT).toString().isEmpty()
                                && buildSmsBody.get(SMS_CONTENT).toString() != "") {
                            if (buildSmsBody.get(EVENT_ID) != null && !buildSmsBody.get(EVENT_ID).toString().isEmpty()
                                    && buildSmsBody.get(EVENT_ID).toString() != "") {
                                smsVo.setMessage(buildSmsBody.get(SMS_CONTENT).toString());
                            }
                        }
                    } else {
                        smsVo.setMessage("Template is not found event : " + eventName);
                        smsVo.setRemark("Tempalate not found for mvnoid : " + data.get("mvnoId") + " and buid : " + data.get("buId") + " for event : " + eventName);
                        smsVo.setSourceName("Notification");
                    }

                    // smsVo.setMessage(smsTemplate);
                    smsVo.setDate(LocalDateTime.now());
                    smsVo.setStatus(FAIL);
                    smsVo.setCreateDate(LocalDateTime.now());
                    smsVo.setLastModifiedDate(LocalDateTime.now());
                    smsVo.setSourceName(sourceName);
                    if (data.get(RabbitMqConstants.COUNTRY_CODE) != null) {
                        smsVo.setCountryCode(data.get(RabbitMqConstants.COUNTRY_CODE).toString());
                    }
                    if (data.get("mvnoId") != null
                            && (!data.get("mvnoId").toString().isEmpty() && data.get("mvnoId").toString() != "")) {
                        if (data.get("buId") == null
                                || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                            smsVo.setSmsConfigId(validateSmsConfigId(Long.parseLong(data.get("mvnoId").toString()), null));
                        } else {
                            Optional<SmsConfig> optionalSmsConfig = smsConfigRepository.findByMvnoIdAndBuId(Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()));
                            if (optionalSmsConfig.isPresent()) {
                                smsVo.setSmsConfigId(validateSmsConfigId(Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString())));
                            } else {
                                smsVo.setSmsConfigId(validateSmsConfigId(Long.parseLong(data.get("mvnoId").toString()), null));
                            }
                        }
                        smsVo.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));
                        //add buid support
                        if (data.get("buId") != null) {
                            smsVo.setBuId(Long.parseLong(data.get("buId").toString()));
                        } else {
                            smsVo.setBuId(null);
                        }
                        if (isTemplateNotFound == true) {
                            if (data.get("buId") != null) {
                                smsVo.setRemark("Template not found for buId : " + data.get("buId") + " and mvnoId : " + data.get("mvnoId"));
                            } else {
                                smsVo.setRemark("Template not found for buId : null and mvnoId : " + data.get("mvnoId"));
                            }

                        }
                    }
                    smsVo.setEventName(eventVo.get().getEventName());
                    smsRepository.save(smsVo);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForStaffStatus(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}", data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.STATUS) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{status\\}", data.get(RabbitMqConstants.STATUS).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildticketstatuschangetocustomer(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {

                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketnumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }
                smsTemplate = emailTemplateData;

                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public Map<String, Object> buildticketETR(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_ETR_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{additionalDate\\}",
                            data.get(RabbitMqConstants.TICKET_ETR_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_ETR_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{additionalTime\\}",
                            data.get(RabbitMqConstants.TICKET_ETR_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_SENDER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}",
                            data.get(RabbitMqConstants.TICKET_SENDER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.TICKET_STATUS).toString());
                }
                smsTemplate = emailTemplateData;

                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            data.put("notificationMessage", smsTemplate);
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildticketETRDynamic(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_SENDER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}",
                            data.get(RabbitMqConstants.TICKET_SENDER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                smsTemplate = emailTemplateData;

                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            data.put("notificationMessage", smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    // BSS Code

//    private void saveSmsNotificationOnCustomerApproval(String message, Map<String, Object> data, String eventId,String sourceName)
//    {
//        try
//        {
//            Long eventIdLong = Long.parseLong(eventId);
//            Sms smsVo = new Sms();
//            smsVo.setMobileNo(data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
//            Optional<Event> eventVo = eventRepository.findById(eventIdLong);
//            smsVo.setEvent(eventVo.get());
//            //smsVo.setEventId(eventIdLong);
//            smsVo.setMessage(message);
//            smsVo.setDate(new Timestamp(new Date().getTime()));
//            smsVo.setStatus(SENT);
//            smsVo.setCreateDate(LocalDateTime.now());
//            smsVo.setLastModifiedDate(LocalDateTime.now());
//            smsVo.setSourceName(sourceName);
//            if(data.get(RabbitMqConstants.COUNTRY_CODE) != null)
//            {
//                smsVo.setCountryCode(data.get(RabbitMqConstants.COUNTRY_CODE).toString());
//            }
//            if(data.get("mvnoId") != null && (!data.get("mvnoId").toString().isEmpty() && data.get("mvnoId").toString() != ""))
//            {
//                smsVo.setSmsConfigId(validateSmsConfigId(Long.parseLong(data.get("mvnoId").toString())));
//                smsVo.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));
//            }
//            smsRepository.save(smsVo);
//        }
//        catch(Throwable e)
//        {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    private Map<String, Object> getSmsContentForCafFollowUpReminderForStaff(Map<String, Object> data, String smsContent,
                                                                            String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {

                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCafFollowUpReminderForCustomer(Map<String, Object> data, String smsContent,
                                                                               String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCafFollowUpOverDueForStaff(Map<String, Object> data, String smsContent,
                                                                           String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForCafFollowUpOverDueForParentStaff(Map<String, Object> data,
                                                                                 String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private Map<String, Object> getSmsContentForTicketFollowUpReminderForStaff(Map<String, Object> data, String smsContent,
                                                                               String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForTicketFollowUpReminderForCustomer(Map<String, Object> data, String smsContent,
                                                                                  String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForTicketFollowUpOverDueForStaff(Map<String, Object> data, String smsContent,
                                                                              String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> getSmsContentForTicketFollowUpOverDueForParentStaff(Map<String, Object> data,
                                                                                    String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                                data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                                data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private Map<String, Object> getSmsContentForTicketCreationForCustomer(Map<String, Object> data,
                                                                          String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    if (data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                                data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Map<String, Object> getSmsContentForTicketRescheduleMessage(Map<String, Object> data,
                                                                       String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }


                if (smsTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketReminderNotificationMessage(Map<String, Object> data, String smsContent,
                                                                      String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                smsContent = smsTemplateData;
            }
            data.put("NotificationMsg", smsContent);
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketOverDueReminderMessage(Map<String, Object> data, String smsContent,
                                                                 String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Advnace Notification
     **/

    public Map<String, Object> buildDunningAdvanceNotificationMessage(Map<String, Object> data, String smsContent,
                                                                      String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{Username\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.EXPIRYDATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ExpiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRYDATE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.DUE_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ExpiryDate\\}",
                            data.get(RabbitMqConstants.DUE_DATE).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.PAYMENT_URL) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{paymentUrl\\}",
                            data.get(RabbitMqConstants.PAYMENT_URL).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.AMOUNT) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{amount\\}",
                            data.get(RabbitMqConstants.AMOUNT).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.START_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{startDate\\}",
                            data.get(RabbitMqConstants.START_DATE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.END_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{endDate\\}",
                            data.get(RabbitMqConstants.END_DATE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.SUB_TOTAL) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{subTotal\\}",
                            data.get(RabbitMqConstants.SUB_TOTAL).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAX_AMOUNT) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{taxAmount\\}",
                            data.get(RabbitMqConstants.TAX_AMOUNT).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAX_PERCENTAGE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{taxPercentage\\}",
                            data.get(RabbitMqConstants.TAX_PERCENTAGE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAX_PERCENTAGE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{taxPercentage\\}",
                            data.get(RabbitMqConstants.TAX_PERCENTAGE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TOTAL_DUE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{totalDue\\}",
                            data.get(RabbitMqConstants.TOTAL_DUE).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.CREDIT_BALANCE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{walletBalance\\}",
                            data.get(RabbitMqConstants.CREDIT_BALANCE).toString());
                }


                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Partner Document Expired
     **/

    public Map<String, Object> buildDunningPartenerDocumentMessage(Map<String, Object> data, String smsContent,
                                                                   String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFFNAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffName\\}",
                            data.get(RabbitMqConstants.USER_NAME_ADD).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.PARTNERNAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{partnerName\\}",
                            data.get(RabbitMqConstants.PARTNERNAME).toString());
                }


                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCustStatusInActiveNotificationMessage(Map<String, Object> data, String smsContent,
                                                                          String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /*sms msg for customer document verification*/
    public Map<String, Object> buildCustDocumentVerificationMessage(Map<String, Object> data, String smsContent,
                                                                    String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*sms msg for customer service active*/
    public Map<String, Object> buildCustServiceActiveMessage(Map<String, Object> data, String smsContent,
                                                             String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*sms msg for customer service Inactive*/
    public Map<String, Object> buildCustServiceInActiveMessage(Map<String, Object> data, String smsContent,
                                                               String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*sms msg for customer change password*/
    public Map<String, Object> buildCustChangePasswordMessage(Map<String, Object> data, String smsContent,
                                                              String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.PASSWORD).toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*sms for customer address shifting  */
    public Map<String, Object> buildCustAddressShiftingMessage(Map<String, Object> data, String smsContent,
                                                               String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*sms for customer payment verification */
    public Map<String, Object> buildCustPaymentVerificationMessage(Map<String, Object> data, String smsContent,
                                                                   String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{paymentReciptNo\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO).toString());
                }

                if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{paymentAmount\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                }

                if (data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{paymentDate\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /*sms for customer ticket close */
    public Map<String, Object> buildCustTicketCloseMessage(Map<String, Object> data, String smsContent,
                                                           String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /*sms for lead creation  */
    public Map<String, Object> buildLeadCreationMessage(Map<String, Object> data, String smsContent,
                                                        String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.FIRSTNAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{firstname\\}",
                            data.get(RabbitMqConstants.FIRSTNAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.LEAD_NO) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{leadNo\\}",
                            data.get(RabbitMqConstants.LEAD_NO).toString());
                }


                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Map<String, Object> buildTatNotificationToTeam(Map<String, Object> data, String smsContent,
                                                          String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAT_TEAM_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{teamName\\}",
                            data.get(RabbitMqConstants.TAT_TEAM_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{eventName\\}",
                            data.get(RabbitMqConstants.EVENT_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{Assigndatetime\\}",
                            data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PER_NAME).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public Map<String, Object> buildTicketRemarkToCustomer(Map<String, Object> data, String smsContent,
                                                           String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAT_TEAM_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{teamName\\}",
                            data.get(RabbitMqConstants.TAT_TEAM_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{eventName\\}",
                            data.get(RabbitMqConstants.EVENT_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{Assigndatetime\\}",
                            data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PER_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildCustomerQuotaToCustomer(Map<String, Object> data, String smsContent,
                                                            String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = Optional.empty();
            optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.PERCENTAGE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{percentage\\}",
                            data.get(RabbitMqConstants.PERCENTAGE).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildCustomerInvoiceMessage(Map<String, Object> data, String smsContent,
                                                           String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TAT_TEAM_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{teamName\\}",
                            data.get(RabbitMqConstants.TAT_TEAM_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{eventName\\}",
                            data.get(RabbitMqConstants.EVENT_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{Assigndatetime\\}",
                            data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PER_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildTicketAlertMessage(Map<String, Object> data, String smsContent,
                                                       String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_DATA) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ticketData\\}",
                            data.get(RabbitMqConstants.TICKET_DATA).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildUnpickTicketAlertMessage(Map<String, Object> data, String smsContent,
                                                             String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.TICKET_DATA) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ticketData\\}",
                            data.get(RabbitMqConstants.TICKET_DATA).toString());
                }
                smsTemplate = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", smsTemplate);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(SMS_CONTENT, smsTemplate);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public List<SmsConfig> getSmsConfigByData(Map<String, Object> data) {
        List<SmsConfig> smsConfigList = new ArrayList<>();

        try {
            if (data.get("buId") == null || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                smsConfigList = smsConfigRepository
                        .findAllByMvnoIdAndBuIdAndConfigStatus(Long.parseLong(data.get("mvnoId").toString()), null, true);
                if (smsConfigList.isEmpty()) {
                    smsConfigList.add(smsConfigRepository.findByMvnoId(1L).orElse(null));
                }
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Defualt smsConfig called" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            } else {
                smsConfigList.addAll(smsConfigRepository.findAllByMvnoIdAndBuIdAndConfigStatus(Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()), true));
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + data.get("buId").toString() + " smsConfig called," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                if (smsConfigList.isEmpty()) {
                    smsConfigList.addAll(smsConfigRepository.findAllByMvnoIdAndBuIdAndConfigStatus(Long.parseLong(data.get("mvnoId").toString()), null, true));
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + " Defualt smsConfig called becuase buid doesnt have config," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    if (smsConfigList.isEmpty()) {
                        smsConfigList.add(smsConfigRepository.findByMvnoId(1L).orElse(null));
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsConfigList;
    }

    public void createFailedSmsAudit(Map<String, Object> data, String queuename, SmsConfig smsConfig) {
        String eventName = getEventNameBasedOnQueueName(queuename);
        String name = getUserName(data);
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        MDC.put(NotificationConstants.USER_NAME, name);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "sms with " + eventName + " is failed create failed audit " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            Sms sms = new Sms();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Event is found " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                sms.setEventId(optionalEvent.get().getEventId());
            }
            if (data.get("buId") == null
                    || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "buId is not found " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                sms.setBuId(null);
            } else {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "buId is found " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                sms.setBuId((Long) data.get("buId"));
            }
            sms.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));
            if (Objects.nonNull(smsConfig)) {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "sms config found but config might be wrong  " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                sms.setSmsConfigId(smsConfig.getSmsConfigId());
                sms.setMessage("Sms with :" + smsConfig.getSmsUrl() + " is failed");
            } else {
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "no sms config found with active  " + LogConstants.REQUEST_BY + getUserName(data) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                sms.setMessage("Sms is failed to send due to no active profile found");
            }
            if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                sms.setMobileNo(data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
            }
            if (data.get(RabbitMqConstants.COUNTRY_CODE) != null) {
                sms.setCountryCode(data.get(RabbitMqConstants.COUNTRY_CODE).toString());
            }
            if (data.get("cprId") != null) {
                Long cprId = Long.valueOf(data.get("cprId").toString());
                sms.setCprId(cprId);
            }
            sms.setEventName(optionalEvent.get().getEventName());
            sms.setDate(LocalDateTime.now());
            sms.setStatus("Failed");
            sms.setSourceName("Savbill BSS API GATEWAY");
            smsRepository.save(sms);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MDC.remove(NotificationConstants.USER_NAME);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }

    }

    public Map<String, Object> buildWorkflowActionAssign(Map<String, Object> data, String smsContent,
                                                         String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);

                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.ACTION) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{action\\}",
                            data.get(RabbitMqConstants.ACTION).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildEmailBodyForMvnoDocumentDunningEvent(Map<String, Object> data, String smsContent,
                                                                         String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (smsTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.DOCUMENT_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{documentName\\}",
                            data.get(RabbitMqConstants.DOCUMENT_NAME).toString());
                }
                if (smsTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRY_DATE).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.DUE_DATE) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.DUE_DATE).toString());
                }

                smsContent = smsTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public Map<String, Object> buildMvnoDoc(Map<String, Object> data, String smsContent,
                                            String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);

                if (smsTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.ACTION) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{action\\}",
                            data.get(RabbitMqConstants.ACTION).toString());
                }

                if (smsTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (smsTemplateData != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    String getUserName(Map<String, Object> data) {
        String userName = null;
        if (data.containsKey(RabbitMqConstants.USER_NAME) && data.get(RabbitMqConstants.USER_NAME) != null) {
            userName = data.get(RabbitMqConstants.USER_NAME).toString();
        } else if (data.containsKey(RabbitMqConstants.MOBILE_NUMBER) && data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
            userName = data.get(RabbitMqConstants.MOBILE_NUMBER).toString();
        } else if (data.containsKey(RabbitMqConstants.EMPLOYEE_NAME) && data.get("employeeName") != null) {
            userName = data.get(RabbitMqConstants.EMPLOYEE_NAME).toString();
        } else if (data.containsKey(RabbitMqConstants.CUSTMR_NAME) && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
            userName = data.get(RabbitMqConstants.CUSTMR_NAME).toString();
        } else if (data.containsKey(RabbitMqConstants.FIRSTNAME) && data.get(RabbitMqConstants.FIRSTNAME) != null) {
            userName = data.get(RabbitMqConstants.FIRSTNAME).toString();
        } else if (data.containsKey(RabbitMqConstants.CUSTMR_NAME) && data.get("customerName") != null) {
            userName = data.get(RabbitMqConstants.CUSTMR_NAME).toString();
        }
        return userName;

    }

    @Override
    public void sendSmsIwf(SmsReceiverEventTempBinding smsReceiverEventTempBinding, SmsConfig smsConfigDetails, Template templateDetails) throws Exception {
        Sms smsDetails = new Sms();
        String username = "savbillnettech", password = "636fa365", sender = "SAVBILLN", type = "1", product = "1", template = templateDetails.getSmsTemplateData();
        try {
            String host = "http://makemysms.in"; // the hostname alogn with protocol
            String api = "/api/sendsms.php"; // api path
            //put all the query params in the below hashtable
            Hashtable<String, String> queryParams = new Hashtable<>();
            queryParams.put("username", username);
            queryParams.put("password", password);
            queryParams.put("sender", sender);
            queryParams.put("mobile", smsReceiverEventTempBinding.getMobileNumber());
            queryParams.put("type", type);
            queryParams.put("product", product);
            queryParams.put("template", "1707166789660151314");
            queryParams.put("message", URLEncoder.encode("This is your message", "UTF-8"));
            //below code forms the URI based on query params, host and api
            String uri = host + api;
            if (queryParams.size() > 0)
                uri = uri + "?";
            int count = 1;
            for (Map.Entry e : queryParams.entrySet()) {
                if (count > 1)
                    uri = uri + "&" + e.getKey() + "=" + e.getValue();
                else
                    uri = uri + e.getKey() + "=" + e.getValue();
                count++;
            }
            System.out.println("Requesting URL:" + uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        smsDetails.setStatus(SENT);
        smsDetails.setDate(LocalDateTime.now());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        smsRepository.save(smsDetails);
    }

    @Override
    public Page<SmsDataDTO> searchSmsAudit(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType) {
        Integer page = requestDTO.getPage();
        Integer size = requestDTO.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("smsId")));
        Specification<Sms> spec = Specification.where(null);
        Page<Sms> sms = null;
        if (mvnoId != 1) {
            spec = spec.and((root, query, builder) -> (root.get(NotificationConstants.SmsSearchEnum.MVNO_ID).in(Arrays.asList(mvnoId, 1))));
        }
        if (!serviceType.trim().isEmpty()) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.SmsSearchEnum.SERVICE_TYPE), serviceType));
        }
        if (null != requestDTO.getFilters() && requestDTO.getFilters().size() > 0) {
            for (GenericSearchModel searchModel : requestDTO.getFilters()) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.ANY)) {
                    sms = getAllSms(searchModel.getFilterValue().trim(), searchModel.getFilterCondition().trim(), spec, pageable, mvnoId, serviceType.trim());
                } else {
                    spec = getSmsByFilter(searchModel.getFilterValue(), searchModel.getFilterCondition(), searchModel.getFilterColumn().trim(), spec, pageable, mvnoId, serviceType);
                }
            }
        }
        if (sms == null || (sms.isEmpty())) {
            sms = smsRepository.findAll(spec, pageable);
        }

//		if(sms==null || (sms.isEmpty())){
//			throw new RuntimeException("No Record Found!");
//		}
        Page<SmsDataDTO> map = sms.map(this::setPropertiesToDto);
        return map;
    }

    public Page<Sms> getAllSms(String value, String filterCondition, Specification<Sms> spec, Pageable pageable, Long mvnoId, String serviceType) {
        Sms sms = new Sms();
        try {
            sms.setServiceType(value.trim());
//			sms.setMessage(value.trim());
            sms.setCountryCode(value.trim());
            sms.setLastModifiedBy(value.trim());
            sms.setMobileNo(value.trim());
            sms.setSourceName(value.trim());
            sms.setStatus(value.trim());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher(NotificationConstants.SmsSearchEnum.SOURCE_NAME, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher(NotificationConstants.SmsSearchEnum.STATUS, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher(NotificationConstants.SmsSearchEnum.MOBILE_NO, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<Sms> example = Example.of(sms, customExampleMatcher);
        Page<Sms> smsaudits = smsRepository.findAll(example, pageable);
        return smsaudits;
    }

    public Specification<Sms> getSmsByFilter(String value, String filterCondition, String filterColumn, Specification<Sms> spec, Pageable pageable, Long mvnoId, String serviceType) {
//		Specification specification = null;
        if ((filterColumn != null) && (!filterColumn.trim().isEmpty())) {
            if (filterCondition.equalsIgnoreCase("OR")) {
                if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.DATE)) {
                    spec = dateFilter(value, filterCondition, spec, filterColumn, mvnoId, serviceType);
                } else {
                    spec = spec.or((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                }
            } else {
                if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.DATE)) {
                    spec = dateFilter(value, filterCondition, spec, filterColumn, mvnoId, serviceType);

                } else {
                    spec = spec.and((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                }
            }
        }
        return spec;
    }

    public Specification<Sms> dateFilter(String value, String filterCondition, Specification<Sms> spec, String filterColumn, Long mvnoId, String serviceType) {
//		Specification specification = null;
        JSONObject filterValue = new JSONObject(value);
        String fromDate = filterValue.getString("from") + "T00:00:00";
        String toDate = filterValue.getString("to") + "T23:59:59";
        LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
        LocalDateTime toDateTime = LocalDateTime.parse(toDate);
        if (filterCondition.equalsIgnoreCase("OR")) {
            spec = spec.or((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        } else {
            spec = spec.and((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        }
        return spec;
    }

    public SmsDataDTO setPropertiesToDto(Sms sms) {
        return new SmsDataDTO(
                sms.getSmsId(),
                sms.getSourceName(),
                sms.getCountryCode(),
                sms.getMobileNo(),
                sms.getMessage(),
                sms.getDate(),
                sms.getStatus(),
                sms.getEventId(),
                sms.getEventName()
        );
    }


    public Map<String, Object> buildSmsBodyForPlanExpiryEvent(Map<String, Object> data, String smsContent,
                                                              String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_PLAN) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{plan\\}",
                            data.get(RabbitMqConstants.CUST_PLAN).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRY_DATE).toString());
                }

                smsContent = emailTemplateData;
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public boolean validation(PaginationRequestDTO requestDTO) {
        for (GenericSearchModel searchModel : requestDTO.getFilters()) {
            if (searchModel.getFilterValue() != null || !searchModel.getFilterValue().trim().isEmpty() || searchModel.getFilterValue().trim().equalsIgnoreCase("")) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> getSmsContentForTaskCreationForCustomer(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }
                    if (smsTemplateData != null && data.get(RabbitMqConstants.START_DATE_TIME) != null) {
                        String input = data.get(RabbitMqConstants.START_DATE_TIME).toString();
                        String fixedInput = input.replace("T", ":");
                        smsTemplateData = smsTemplateData.replaceAll("\\{startDate\\}",
                                fixedInput);

                    }
                    if (smsTemplateData != null && data.get(RabbitMqConstants.END_DATE_TIME) != null) {
                        String input = data.get(RabbitMqConstants.END_DATE_TIME).toString();
                        String fixedInput = input.replace("T", ":");
                        smsTemplateData = smsTemplateData.replaceAll("\\{endDate\\}",
                                fixedInput);
                    }

                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    if (data.get(RabbitMqConstants.EMAILID) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{emailId\\}",
                                data.get(RabbitMqConstants.EMAILID).toString());
                    }
                    if (data.get(RabbitMqConstants.WALLETAMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{walletPrice\\}",
                                data.get(RabbitMqConstants.WALLETAMOUNT).toString());
                    }smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data, sourceName, smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());}
	}
    private Map<String, Object> getSmsContentForInsufficientWalletForCustomer(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
                if (smsTemplateData != null) {

                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }


                    if (data.get(RabbitMqConstants.EMAILID) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{emailId\\}",
                                data.get(RabbitMqConstants.EMAILID).toString());
                    }
                    if (data.get(RabbitMqConstants.WALLETAMOUNT) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{walletPrice\\}",
                                data.get(RabbitMqConstants.WALLETAMOUNT).toString());
                    }
                    if (data.get(RabbitMqConstants.AUTORENEWALPREFERANCE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{Auto-RenewalPreference\\}",
                                data.get(RabbitMqConstants.AUTORENEWALPREFERANCE).toString());
                    }

                    smsContent = smsTemplateData;
                }
                if(smsContent!=null){
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                }else{
                    saveSmsNotificationOnFailure(null,data,sourceName,smsContent,eventName);
                    returnData.put(TEMPLATE_NOT_FOUND,true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    private Map<String, Object> getSmsContentForInsufficientWalletForCustomer(Map<String, Object> data, String smsContent, String eventName, String sourceName, String smsTemplate) {
//        try {
//            Map<String, Object> returnData = new HashMap<>();
//            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            if (optionalEvent.isPresent()) {
//                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buid"), false);
//                if (smsTemplateData != null) {
//
//                    if (data.get(RabbitMqConstants.USERNAME) != null) {
//                        smsTemplateData = smsTemplateData.replaceAll("\\{userName\\}",
//                                data.get(RabbitMqConstants.USERNAME).toString());
//                    }
//
//
//                    if (data.get(RabbitMqConstants.EMAILID) != null) {
//                        smsTemplateData = smsTemplateData.replaceAll("\\{emailId\\}",
//                                data.get(RabbitMqConstants.EMAILID).toString());
//                    }
//                    if (data.get(RabbitMqConstants.WALLETAMOUNT) != null) {
//                        smsTemplateData = smsTemplateData.replaceAll("\\{walletPrice\\}",
//                                data.get(RabbitMqConstants.WALLETAMOUNT).toString());
//                    }
//
//                    smsContent = smsTemplateData;
//                }
//                if(smsContent!=null){
//                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
//                }else{
//                    saveSmsNotificationOnFailure(null,data,sourceName,smsContent,eventName);
//                    returnData.put(TEMPLATE_NOT_FOUND,true);
//                    //throw new CustomException("Template not found for this mvno and buid",417);
//                }
//            }
//            returnData.put(SMS_CONTENT, smsContent);
//            return returnData;
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
        //}


    public boolean checkRequestForSms(List<SmsConfigMapping> smsConfigMappings) {
        return smsConfigMappings.stream()
                .anyMatch(smsConfigMapping -> CommonConstants.REQUEST_FOR.equalsIgnoreCase(smsConfigMapping.getParameter()) && CommonConstants.POST.equalsIgnoreCase(smsConfigMapping.getValue()));
    }

    public boolean checkForContentType(List<SmsConfigMapping> smsConfigMappings) {
        return smsConfigMappings.stream()
                .anyMatch(smsConfigMapping -> CommonConstants.CONTENT_TYPE.equalsIgnoreCase(smsConfigMapping.getParameter()) && CommonConstants.APPLICATION_FORM_URLENCODED.equalsIgnoreCase(smsConfigMapping.getValue()));
    }


    private Map<String, Object> buildEmailBodyForChildCustRegistrationEvent(Map<String, Object> data, String smsContent, String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null) {
                    if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{mobileNumber\\}",
                                data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                    }
                    if (data.get(RabbitMqConstants.CUST_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PASSWORD) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{password\\}",
                                data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                    }
                    if (data.get(RabbitMqConstants.REGISTRATION_DATE) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{registrationDate\\}",
                                data.get(RabbitMqConstants.REGISTRATION_DATE).toString());
                    }
                    if (data.get(RabbitMqConstants.PLAN_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{planName\\}",
                                data.get(RabbitMqConstants.PLAN_NAME).toString());
                    }
                    if (data.get(RabbitMqConstants.PARENT_CUSTOMER_NAME) != null) {
                        smsTemplateData = smsTemplateData.replaceAll("\\{parentCustomerName\\}",
                                data.get(RabbitMqConstants.PARENT_CUSTOMER_NAME).toString());
                    }
                    String accountNumber = data.getOrDefault(RabbitMqConstants.ACCOUNT_NUMBER, "").toString();
                    smsTemplateData = smsTemplateData.replaceAll("\\{accountNumber\\}", accountNumber);

                    smsTemplateData = smsTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    smsTemplateData = smsTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                    smsContent = smsTemplateData;
                }
                if (smsContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveSmsNotificationOnFailure(null, data,  "Savbill BSS API GATEWAY", smsContent, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
