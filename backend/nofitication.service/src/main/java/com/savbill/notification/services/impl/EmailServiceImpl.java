package com.savbill.notification.services.impl;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.Email;
import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.kafka.KafkaConstant;
import com.savbill.notification.kafka.KafkaMessageData;
import com.savbill.notification.kafka.KafkaMessageSender;
import com.savbill.notification.rabbitmq.RabbitMqConstants;
import com.savbill.notification.rabbitmq.message.TicketAuditMessage;
import com.savbill.notification.rabbitmq.message.TicketETRAuditMessage;
import com.savbill.notification.repository.EmailConfigRepository;
import com.savbill.notification.repository.EmailRepository;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.services.EmailService;
import com.savbill.notification.services.SmsService;
import com.savbill.notification.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.savbill.notification.utils.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {
    private static final String EVENT_ID = "eventId";
    private static final String EMAIL_CONTENT = "emailContent";
    private static final String TEMPLATE_NOT_FOUND = "template not found";
    private static final String SENT = "Sent";
    private static final String PENDING = "Pending";
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    //    @Autowired
//    MessageSender messageSender;
    @Autowired
    TemplateServiceImpl templateServiceImpl;
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    UpdateDiffFinder updateDiffFinder;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailConfigRepository emailConfigRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private NotificationConfigMappingServiceImpl notificationConfigMappingService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private Tracer tracer;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private InventoryEmailServiceImpl inventoryEmailService;

    public static String generateTicketDataContent(String ticketDTOList) throws JsonProcessingException {
        StringBuilder content = new StringBuilder();
        content.append("<table border=\"1\">");
        content.append("<tr><th>Ticket No</th><th>Status</th></tr>");
        List<TicketDTO> caseStatusDTOList = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\{caseNumber=(.*?), caseStatus=(.*?)\\}");
        Matcher matcher = pattern.matcher(ticketDTOList);

        while (matcher.find()) {
            String caseNumber = matcher.group(1);
            String caseStatus = matcher.group(2);
            caseStatusDTOList.add(new TicketDTO(caseNumber, caseStatus));
        }
        for (TicketDTO ticket : caseStatusDTOList) {
            content.append("<tr><td>").append(ticket.getCaseNumber()).append("</td><td>").append(ticket.getCaseStatus()).append("</td></tr>");
        }
        content.append("</table>");
        return content.toString();
    }

    @Override
    public List<EmailDataDTO> findEmailBySourceName(Long eventId, String status, String emailAddress, Long mvnoId) {
        try {
            List<EmailDataDTO> emailList = new ArrayList<>();
            QEmail qEmail = QEmail.email;
            BooleanExpression boolExp = qEmail.isNotNull();
            if (eventId == null && status == null && emailAddress == null) {
                if (mvnoId == 1) {
                    boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId)).or(qEmail.mvnoId.eq(1L));
                    //emailList = emailRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
                } else {
                    boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId)).or(qEmail.mvnoId.eq(1L));
                    //emailList = (List<Email>) emailRepository.findAll(boolExp,
                    //	Sort.by(Sort.Direction.DESC, "createDate"));
                }
            } else {
                if (mvnoId == 1) {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(status) && !status.equals("null")) {
                        boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
                    }
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
                            && !emailAddress.equals("null")) {
                        boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
                    }
                    if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                        boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
                    }
                    if(Objects.nonNull(mvnoId) && ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)){
                        boolExp=boolExp.and(qEmail.mvnoId.eq(mvnoId));
                    }
//					emailList = (List<Email>) emailRepository.findAll(boolExp,
//							Sort.by(Sort.Direction.DESC, "createDate"));
                } else {
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(status) && !status.equals("null")) {
                        boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
                    }
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
                            && !emailAddress.equals("null")) {
                        boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
                    }
                    if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                        boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
                    }
                    boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId).or(qEmail.mvnoId.eq(1L)));
//					emailList = (List<Email>) emailRepository.findAll(boolExp,
//							Sort.by(Sort.Direction.DESC, "createDate"));
                }
            }
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

            QueryResults<EmailDataDTO> queryResults = queryFactory
                    .select(Projections.constructor(
                            EmailDataDTO.class,
                            qEmail.emailId,
                            qEmail.sourceName,
                            qEmail.emailAddress,
                            qEmail.message,
                            qEmail.date,
                            qEmail.status,
                            qEmail.event.eventId,
                            qEmail.event.eventName))
                    .from(qEmail)
                    .where(boolExp)
                    .orderBy(qEmail.emailId.desc())
                    .fetchResults();
            emailList = queryResults.getResults();

            return emailList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//	@Override
//	public PageableResponse<EmailDataDTO> findAllEmails(Long eventId, String status, String emailAddress, Long mvnoId,
//			PaginationDTO paginationDTO,List<Long>buIdlist) {
//		try {
//			if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
//				throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
//			} else {
//				QEmail qEmail = QEmail.email;
//				BooleanExpression boolExp = qEmail.isNotNull();
//				if (paginationDTO.getPage() > 0) {
//					paginationDTO.setPage(paginationDTO.getPage() - 1);
//				}
//				PageableResponse<EmailDataDTO> pageableResponse = new PageableResponse<>();
//				Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(),
//						Sort.by(Sort.Direction.DESC, "createDate"));
//				if (mvnoId != 1) {
//					boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId).or(qEmail.mvnoId.eq(1L)));
//				}
//				if(buIdlist !=null && buIdlist.size()>0 ){
//					boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//				}
//				boolExp = boolExp.and(qEmail.serviceType.containsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_BSS));
//				if (eventId == null && status == null && emailAddress == null) {
//					if (mvnoId != 1) {
//						boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId)).or(qEmail.mvnoId.eq(1L));
//					}
//					if(buIdlist !=null && buIdlist.size()>0 ){
//						boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//					}
//				} else {
//					if (mvnoId == 1) {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
//								&& !emailAddress.equals("null")) {
//							boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
//						}
//					} else {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
//								&& !emailAddress.equals("null")) {
//							boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
//						}
//						if(buIdlist !=null && buIdlist.size()>0 ){
//							boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//						}
//						boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId).or(qEmail.mvnoId.eq(1L)));
//					}
//				}
//
//                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//                QueryResults<EmailDataDTO> queryResults = queryFactory
//                        .select(Projections.constructor(
//                                EmailDataDTO.class,
//                                qEmail.emailId,
//                                qEmail.sourceName,
//                                qEmail.emailAddress,
//                                qEmail.message,
//                                qEmail.date,
//                                qEmail.status,
//                                qEmail.event.eventId,
//                                qEmail.event.eventName))
//                        .from(qEmail)
//                        .where(boolExp)
//                        .orderBy(qEmail.emailId.desc())
//                        .offset((paginationDTO.getPage()) * paginationDTO.getSize())
//                        .limit(paginationDTO.getSize())
//                        .fetchResults();
//
//                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//                QueryResults<EmailDataDTO> queryResults = queryFactory
//                        .select(Projections.constructor(
//                                EmailDataDTO.class,
//                                qEmail.emailId,
//                                qEmail.sourceName,
//                                qEmail.emailAddress,
//                                qEmail.message,
//                                qEmail.date,
//                                qEmail.status,
//                                qEmail.event.eventId,
//                                qEmail.event.eventName))
//                        .from(qEmail)
//                        .where(boolExp)
//                        .orderBy(qEmail.emailId.desc())
//                        .offset((paginationDTO.getPage()) * paginationDTO.getSize())
//                        .limit(paginationDTO.getSize())
//                        .fetchResults();
//
//
//                long totalRecords = emailDataDTOList.size();
//                return pageableResponse.convert(new PageImpl<>(emailDataDTOList, PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize()), totalRecords));
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public Email findEmailById(Long emailId, Long mvnoId, Boolean isUpdateOrDelete) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            }
            QEmail qEmail = QEmail.email;
            BooleanExpression boolExp = qEmail.isNotNull();
            boolExp = boolExp.and(qEmail.emailId.eq(emailId));
            if (mvnoId != 1)
                if (isUpdateOrDelete)
                    boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId));
                else
                    boolExp = boolExp.and(qEmail.mvnoId.in(mvnoId, 1));
            Optional<Email> email = emailRepository.findOne(boolExp);
            if (email.isPresent()) {
                return email.get();
            } else {
                throw new IllegalArgumentException("No record found with source name " + emailId
                        + " . or you might not have access to update/delete this record.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PageableResponse<EmailDataDTO> findAllEmails(Long eventId, String status, String emailAddress, Long mvnoId,
                                                        PaginationDTO paginationDTO, List<Long> buIdlist) {
        try {
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            PageableResponse<EmailDataDTO> pageableResponse = new PageableResponse<>();
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(),
                    Sort.by(Sort.Direction.DESC, "createDate"));
            List<Email> emails = new ArrayList<>();
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                Page<EmailDataDTO> emailDataObjects;
                if (buIdlist != null && buIdlist.size() > 0) {
                    emailDataObjects = emailRepository.findAllByMvnoIdAndBuIdIn(mvnoId, buIdlist, pageable);
                } else {
                    emailDataObjects = emailRepository.findAllByMvnoId(mvnoId, pageable);
                }

                List<EmailDataDTO> emailDataDTOList = new ArrayList<>();
                emailDataDTOList.addAll(emailDataObjects.getContent());


//				QEmail qEmail = QEmail.email;
//				BooleanExpression boolExp = qEmail.isNotNull();
//				if (paginationDTO.getPage() > 0) {
//					paginationDTO.setPage(paginationDTO.getPage() - 1);
//				}
//				PageableResponse<EmailDataDTO> pageableResponse = new PageableResponse<>();
//				Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(),
//						Sort.by(Sort.Direction.DESC, "createDate"));
//				if (mvnoId != 1) {
//					boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId).or(qEmail.mvnoId.eq(1L)));
//				}
//				if(buIdlist !=null && buIdlist.size()>0 ){
//					boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//				}
//				boolExp = boolExp.and(qEmail.serviceType.containsIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_BSS));
//				if (eventId == null && status == null && emailAddress == null) {
//					if (mvnoId != 1) {
//						boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId)).or(qEmail.mvnoId.eq(1L));
//					}
//					if(buIdlist !=null && buIdlist.size()>0 ){
//						boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//					}
//				}
//                else {
//					if (mvnoId == 1) {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
//								&& !emailAddress.equals("null")) {
//							boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
//						}
//					} else {
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(status)
//								&& !status.equals("null")) {
//							boolExp = boolExp.and(qEmail.status.equalsIgnoreCase(status));
//						}
//						if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailAddress)
//								&& !emailAddress.equals("null")) {
//							boolExp = boolExp.and(qEmail.emailAddress.contains(emailAddress));
//						}
//						if (ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//							boolExp = boolExp.and(qEmail.event.eventId.eq(eventId));
//						}
//						if(buIdlist !=null && buIdlist.size()>0 ){
//							boolExp=boolExp.and(qEmail.buId.in(buIdlist));
//						}
//						boolExp = boolExp.and(qEmail.mvnoId.eq(mvnoId).or(qEmail.mvnoId.eq(1L)));
//					}
//				}

//                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//                QueryResults<EmailDataDTO> queryResults = queryFactory
//                        .select(Projections.constructor(
//                                EmailDataDTO.class,
//                                qEmail.emailId,
//                                qEmail.sourceName,
//                                qEmail.emailAddress,
//                                qEmail.message,
//                                qEmail.date,
//                                qEmail.status,
//                                qEmail.event.eventId,
//                                qEmail.event.eventName))
//                        .from(qEmail)
//                        .where(boolExp)
//                        .orderBy(qEmail.emailId.desc())
//                        .offset((paginationDTO.getPage()) * paginationDTO.getSize())
//                        .limit(paginationDTO.getSize())
//                        .fetchResults();


                long totalRecords = emailDataObjects.getTotalElements();
                return pageableResponse.convert(new PageImpl<>(emailDataDTOList, PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize()), totalRecords));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteEmailById(Long id, Long mvnoId) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            }
            Email email = findEmailById(id, mvnoId, true);
            emailRepository.deleteById(id);
            //	System.out.println("Email deleted successfully, deleted email : " + email.getEmailAddress());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @Override
    public Email saveEmail(EmailDto emailDto, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(emailDto.getCreatedBy()))
                throw new IllegalArgumentException("CreatedBy value is Missing");
            Email emailVo = new Email(emailDto, mvnoId);
            String eventName=null;
            Optional<Event> event=eventRepository.findById( emailDto.getEventId());
            if(event.isPresent()){
                eventName=event.get().getEventName();
            }
            validateEmailData(mvnoId, emailVo, emailDto.getEventId());
            emailVo.setStatus(PENDING);
            emailVo.setEmailConfigId(validateEmailConfigId(mvnoId, null, eventName));
            emailVo.setCreateDate(LocalDateTime.now());
            emailVo.setCreatedBy(emailDto.getCreatedBy());
            return emailRepository.save(emailVo);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Email updateEmail(UpdateEmailDto updateEmailDto, Long mvnoId, Long buId, HttpServletRequest request) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(updateEmailDto.getLastModifiedBy()))
                throw new IllegalArgumentException("LastModifiedBy value is Missing.");
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(updateEmailDto.getEmailId())) {
                throw new IllegalArgumentException(
                        "Email id is mandatory. Please enter valid email id to update the record.");
            } else {
                Email optionalEmail = null;
                if (mvnoId == 1)
                    optionalEmail = emailRepository.findById(updateEmailDto.getEmailId()).get();
                else
                    optionalEmail = emailRepository.findByEmailIdAndMvnoId(updateEmailDto.getEmailId(), mvnoId)
                            .orElse(null);
                if (Objects.isNull(optionalEmail)) {
                    throw new IllegalArgumentException("No record found with email id : '" + updateEmailDto.getEmailId()
                            + "' , Please enter valid email id.");
                }
                Email email = new Email(updateEmailDto, mvnoId, optionalEmail.getStatus(), buId);
                validateEmailData(mvnoId, email, updateEmailDto.getEventId());
                email.setEmailConfigId(validateEmailConfigId(mvnoId, null, null));
                email.setLastModifiedDate(LocalDateTime.now());
                email.setLastModifiedBy(tokenDataExtractor.getUserName(request.getHeader("Authorization")));
                email.setCreateDate(optionalEmail.getCreateDate());
                email.setDate(optionalEmail.getDate());
                String updatedValues = updateDiffFinder.getUpdatedDiff(optionalEmail, email);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email is Updated Successfully , with : " + updatedValues + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return emailRepository.save(email);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reSendEmail(Long id) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Optional<Email> email = emailRepository.findByEmailId(id);
            Email emailDetails = email.get();
            Properties props = new Properties();
            Optional<EmailConfig> emailConfig = emailConfigRepository
                    .findByEmailConfigId(emailDetails.getEmailConfigId());
            EmailConfig emailConfigDetails = emailConfig.get();

            // props.put(emailConfigDetails.getAuthParam(),
            // emailConfigDetails.getAuthValue());
            // props.put(emailConfigDetails.getAuthType(),
            // emailConfigDetails.getAuthTypeValue());
            // props.put(emailConfigDetails.getHostParam(),
            // emailConfigDetails.getHostValue());
            // props.put(emailConfigDetails.getPortParam(),
            // emailConfigDetails.getPortValue());

            props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
            if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                props.put(NotificationConstants.STARTTLS_PARAM, true);
            } else {
                props.put(NotificationConstants.SSL_PARAM, true);
            }
            props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
            props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfigDetails.getUserName(),
                            emailConfigDetails.getPassword());
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDetails.getemailaddress()));
            msg.setSubject(emailDetails.getEvent().getEventName());
            // msg.setContent(emailDetails.getMessage(), "text/html");
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailDetails.getMessage(), "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // MimeBodyPart attachPart = new MimeBodyPart();
            //
            // attachPart.attachFile("/var/tmp/image19.png");
            // multipart.addBodyPart(attachPart);
            msg.setContent(multipart);
            Transport.send(msg);
            emailDetails.setStatus(SENT);
            emailDetails.setDate(LocalDateTime.now());
//			System.out.println("Email send to " + emailDetails.getEmailAddress());
            emailRepository.save(emailDetails);
        } catch (RuntimeException e) {
//			System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (AddressException e) {
//			System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (MessagingException e) {
//			System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

//    private void validateEmailData(Long mvnoId, Email email, Long eventId) {
//        try {
//            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
//                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
//            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
//                throw new IllegalArgumentException("Event id is mandatory. Please enter valid event id");
//            } else if (email.getSourceName() == null || email.getSourceName().isEmpty()
//                    || email.getSourceName().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
//                throw new IllegalArgumentException("Source Name is mandatory. Please enter valid Source Name.");
//            } else if (email.getEmailAddress() == null || email.getEmailAddress().isEmpty()
//                    || email.getEmailAddress().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
//                throw new IllegalArgumentException("Email Address is mandatory. Please enter valid Email Address.");
//            } else if (email.getMessage() == null || email.getMessage().isEmpty()
//                    || email.getMessage().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
//                throw new IllegalArgumentException("Message is mandatory. Please enter valid Message.");
//            }
//
//            Optional<Event> eventVo = eventRepository.findById(eventId);
//            if (!eventVo.isPresent()) {
//                throw new IllegalArgumentException(
//                        "No event found with event id : '" + eventId + "' , Please enter valid event id.");
//            }
//            email.setEvent(eventVo.get());
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public void sendEmailNotification(String queueName, String message, Map<String, Object> data, String sourceName,
                                      String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured,
                                      boolean isSmsConfigured) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);

        Map<String, Object> buildEmailBody = new HashMap<>();
        Optional<EmailConfig> emailConfig = Optional.of(new EmailConfig());
        Integer mvnoId = null;
        Object mvnoIdObject = data.get("mvnoId");

        if (mvnoIdObject != null) {
            if (mvnoIdObject instanceof Double) {
                mvnoId = ((Double) mvnoIdObject).intValue();
            } else if (mvnoIdObject instanceof String) {
                mvnoId = Integer.parseInt((String) mvnoIdObject);
            } else {
                mvnoId = (Integer) data.get("mvnoId");
            }
        }
        data.put("mvnoId", mvnoId);
        String username = getUserName(data);
        MDC.put(NotificationConstants.USER_NAME, username);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        TemplateDto templateDto = getEmailTemplateConfigurationByevent(data, queueName);
        if (Objects.nonNull(templateDto)) {
        if (templateDto.getTemplateName().equalsIgnoreCase("Customer Dunning Advance Notification")
                || templateDto.getTemplateName().equalsIgnoreCase("Partner Dunning Document") ||
                templateDto.getTemplateName().equalsIgnoreCase("Partner Dunning Deactivation Document") ||
                templateDto.getTemplateName().equalsIgnoreCase("Customer Dunning Document") ||
                templateDto.getTemplateName().equalsIgnoreCase("Customer Payment") ||
                templateDto.getTemplateName().equalsIgnoreCase("Customer Dunning") ||
                templateDto.getTemplateName().equalsIgnoreCase("Expired Document") ||
                templateDto.getTemplateName().equalsIgnoreCase("Mvno Document Dunning Message") ||
                templateDto.getTemplateName().equalsIgnoreCase("Customer Dunning Deactivation Document") ||
                templateDto.getTemplateName().equalsIgnoreCase("Mvno Deactivation") ||
                templateDto.getTemplateName().equalsIgnoreCase("Mvno Payment") ||
                templateDto.getTemplateName().equalsIgnoreCase("Mvno Payment Expiry Reminder") ||
                templateDto.getTemplateName().equalsIgnoreCase("Ticket ETR Template")
//                || templateDto.getTemplateName().equalsIgnoreCase("Login OTP Event")

        ) {
            isEmailConfigured = isEmailConfigured;
            isSmsConfigured = isSmsConfigured;
        } else {
            isEmailConfigured = templateDto != null ? templateDto.isEmailEventConfigured() : isEmailConfigured;
            isSmsConfigured = templateDto != null ? templateDto.isSmsEventConfigured() : isSmsConfigured;
        }
        try {
            if (data.get("mvnoId") == null || (data.get("mvnoId").toString().isEmpty() || data.get("mvnoId").toString() == "")) {
                throw new RuntimeException("Mvno id is mandatory.");
            }
            if (data.get("buId") == null || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                emailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), null);
                if (emailConfig == null || !emailConfig.isPresent()) {
                    emailConfig = emailConfigRepository.findByMvnoId(1L);  //default config with superadmin
                }
                log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Default config called  ," + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            } else {
                Optional<EmailConfig> newemailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()));
                if (newemailConfig.isPresent()) {
                    emailConfig = newemailConfig;
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "New BuId :" + data.get("buId").toString() + "  ," + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                } else {
                    emailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), null);
                    if (emailConfig == null || !emailConfig.isPresent() && queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON)) {
                        emailConfig = emailConfigRepository.findByMvnoId(1L);
                    }
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Default config called Because buId emailConfig not found  ," + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            }
            if (isEmailConfigured && (data.get(RabbitMqConstants.EMAIL_ID) != null && ValidateCrudTransactionData.validateStringTypeFieldValue(data.get(RabbitMqConstants.EMAIL_ID).toString()))) {
                Properties props = new Properties();
                EmailConfig emailConfigDetails = emailConfig.get();
                props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
                if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                    props.put(NotificationConstants.STARTTLS_PARAM, true);
                } else {
                    props.put(NotificationConstants.SSL_PARAM, true);
                    props.put(NotificationConstants.SMTP_SOCKETFACTORY_PORT, emailConfigDetails.getPort()); //SSL Port
                    props.put(NotificationConstants.SMTP_SOCKETFACTORY_CLASS,NotificationConstants.SSL_SOCKETFACTORY); //SSL Factory Class
                    props.put(NotificationConstants.SMTP_TIMEOUT, "3000"); // 3 seconds
                    props.put(NotificationConstants.SMTP_SSL_TRUST, emailConfigDetails.getHostServer()); // Trust the self-signed certificate
                }
                props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
                props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfigDetails.getUserName(),
                                emailConfigDetails.getPassword());
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(data.get(RabbitMqConstants.EMAIL_ID).toString()));
                if (data.get(RabbitMqConstants.ALT_EMAIL) != null) {
                    msg.setRecipients(Message.RecipientType.CC,
                            InternetAddress.parse(data.get(RabbitMqConstants.ALT_EMAIL).toString()));
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Alternative Email is set Alt Email : " + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
                if (data.get(RabbitMqConstants.ALT_EMAIL_LIST) != null) {
                    Object altEmailListObj = data.get(RabbitMqConstants.ALT_EMAIL_LIST);
                    if (altEmailListObj instanceof List) {
                        List<?> altEmailList = (List<?>) altEmailListObj;
                        for (Object emailObj : altEmailList) {
                            if (emailObj instanceof String) {
                                String ccEmail = (String) emailObj;
                                msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail));
                                // Use ccEmail as needed
                            }
                        }
                    }
                }
                msg.setSubject(message);
                msg.setSentDate(new Date());
                if (data.containsKey("amount")) {
                    if (data.get("amount").equals(0)) {
                        throw new CustomException("No Payment Found !!", 417);
                    }
                }
                buildEmailBody = buildEmailBody(queueName, data, sourceName, emailTemplate);

                if (buildEmailBody.get(TEMPLATE_NOT_FOUND) != null) {
                    throw new CustomException("Template not found !!", 417);
                }
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                if (buildEmailBody.get(EMAIL_CONTENT) != null) {
                    messageBodyPart.setContent(buildEmailBody.get(EMAIL_CONTENT).toString(), "text/html");
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email is set to message body  Email : " + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
                if (buildEmailBody.get(EMAIL_CONTENT) != null && !buildEmailBody.get(EMAIL_CONTENT).toString().isEmpty()
                        && buildEmailBody.get(EMAIL_CONTENT).toString() != "") {

                    Transport.send(msg);
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Transport.send method called with the message : " + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.EMAIL_ID).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    if (buildEmailBody.get(EVENT_ID) != null && !buildEmailBody.get(EVENT_ID).toString().isEmpty()
                            && buildEmailBody.get(EVENT_ID).toString() != "") {
                        saveEmailNotificationOnSuccess(buildEmailBody.get(EMAIL_CONTENT).toString(), data,
                                buildEmailBody.get(EVENT_ID).toString(), sourceName);
                        Thread.sleep( 1000);
                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationStatus", "Success");
                            data.put("notificationMode", "email");
                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
                        }
                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationStatus", "Success");
                            data.put("notificationMode", "email");
                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
                        } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)|| queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)|| queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE)) {

                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationMode", "email");
                            data.put("notificationStatus", "Success");
                            TicketAuditMessage ticketAuditMessage = new TicketAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                            if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)){
                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_SUCCESS));
                            }else if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)|| queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE)){
                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_SUCCESS));

                            }

                        }
                    }
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email successfully sent to email : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + msg + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            } else {
                if (!isEmailConfigured) {
                    if ( Objects.nonNull(data.get(RabbitMqConstants.EMAIL_ID)) && data.get(RabbitMqConstants.EMAIL_ID).toString() != null) {
                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isEmailConfigured' column is false so email is not going to send : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    }
                } else {
                    if ( Objects.nonNull(data.get(RabbitMqConstants.EMAIL_ID)) && data.get(RabbitMqConstants.EMAIL_ID).toString() != null) {
                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email id is not valid so email is not going to send : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    }

                }
            }
        } catch (CustomException e) {
            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Template not found for bu" + data.get("buId") + " and mvno : " + data.get("mvnoId") + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
        } catch (Throwable e) {

            saveEmailNotificationOnFailure(queueName, data, sourceName, emailTemplate, null);

            //put check condition for etr here and then check add status failed
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
            }
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE)) {

                //put check condition for etr here and then check add status success
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketAuditMessage TicketAuditMessage = new TicketAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(TicketAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)){
                    kafkaMessageSender.send(new KafkaMessageData(TicketAuditMessage, TicketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_SUCCESS));
                }else if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE)){
                    kafkaMessageSender.send(new KafkaMessageData(TicketAuditMessage, TicketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_SUCCESS));
                }

            }

            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email failed. Message : " + e.getMessage() + "Email Address :" + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            throw new RuntimeException(e.getMessage());
        } finally {
            // MDC.remove(NotificationConstants.TYPE);
            try {
                if (isSmsConfigured) {
                    smsService.sendSmsNotification(queueName, message, data, sourceName, smsTemplate, templateDto.getAppendUrl());
                } else {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isSmsConfigured' column is false so sms is not going to send: " + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MDC.remove(NotificationConstants.USER_NAME);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }else{
            throw new RuntimeException("Template not found !!");
        }
    }

    @Override
    public void sendEmailwithAttachments(Long id, Long mvnoId) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        try {
            Optional<Email> email = emailRepository.findByEmailIdAndMvnoId(id, mvnoId);
            Email emailDetails = email.get();
            Properties props = new Properties();

            Optional<EmailConfig> emailConfig = emailConfigRepository
                    .findByEmailConfigIdAndMvnoId(emailDetails.getEmailConfigId(), mvnoId);
            EmailConfig emailConfigDetails = emailConfig.get();

            // props.put(emailConfigDetails.getAuthParam(),
            // emailConfigDetails.getAuthValue());
            // props.put(emailConfigDetails.getAuthType(),
            // emailConfigDetails.getAuthTypeValue());
            // props.put(emailConfigDetails.getHostParam(),
            // emailConfigDetails.getHostValue());
            // props.put(emailConfigDetails.getPortParam(),
            // emailConfigDetails.getPortValue());

            props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
            if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                props.put(NotificationConstants.STARTTLS_PARAM, true);
            } else {
                props.put(NotificationConstants.SSL_PARAM, true);
            }
            props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
            props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfigDetails.getUserName(),
                            emailConfigDetails.getPassword());
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDetails.getemailaddress()));
            msg.setSubject(emailDetails.getSourceName());
            // msg.setContent(emailDetails.getMessage(), "text/html");
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailDetails.getMessage(), "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            MimeBodyPart attachPart = new MimeBodyPart();

            attachPart.attachFile("D:\\savbill projects\\savbill.notification\\src\\main\\resources\\templates\\S3_INTL_OFR.pdf");
            multipart.addBodyPart(attachPart);
            msg.setContent(multipart);
            Transport.send(msg);
            emailDetails.setStatus(SENT);
            emailDetails.setDate(LocalDateTime.now());
//            System.out.println("Email send to " + emailDetails.getEmailAddress());
            emailRepository.save(emailDetails);
        } catch (RuntimeException e) {
//            System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (AddressException e) {
//            System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (MessagingException e) {
//            System.out.println("Error to send email: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // MDC.remove(NotificationConstants.TYPE);
        }
    }

    @Override
    public void sendEmailNotificationWithAttachment(String queueName, String message, Map<String, Object> data, String sourceName, String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured, boolean isSmsConfigured) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);

        Map<String, Object> buildEmailBody = new HashMap<>();
        String username = getUserName(data);
        MDC.put(NotificationConstants.USER_NAME, username);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {

//			if (data.get("mvnoId") == null
//					|| (data.get("mvnoId").toString().isEmpty() || data.get("mvnoId").toString() == "")) {
//				throw new RuntimeException("Mvno id is mandatory.");
//			}
            if (isEmailConfigured && data.get(RabbitMqConstants.CUST_MAIL_IDS_FOR_LEAD_QUOTATION_SEND) != null) {
                Properties props = new Properties();
                Object mvno = data.get("mvnoId");
                Long mvnoId = ((Number) mvno).longValue();

                Optional<EmailConfig> emailConfig = emailConfigRepository
                        .findByMvnoId(mvnoId);
                EmailConfig emailConfigDetails = emailConfig.get();
                props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
                if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                    props.put(NotificationConstants.STARTTLS_PARAM, true);
                } else {
                    props.put(NotificationConstants.SSL_PARAM, true);
                }
                props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
                props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfigDetails.getUserName(),
                                emailConfigDetails.getPassword());
                    }
                });
                List<String> custMailIds = Arrays.stream(String.valueOf(data.get("custMailIds")).split(",")).collect(Collectors.toList());
//                System.out.println(custMailIds);
                for (String custEmailId : custMailIds) {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));
                    //if (custEmailId != null && !custEmailId.equalsIgnoreCase(""))
                    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(custEmailId));
                    //if (data.get("staffEmail") != null && !String.valueOf(data.get("staffEmail")).equalsIgnoreCase(""))
                    //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailConfigDetails.getUserName()));
                    msg.setSubject(message);
                    msg.setSentDate(new Date());
                    //buildEmailBody = buildEmailBody(queueName, data, sourceName, emailTemplate);
                    MimeBodyPart attachmentTextPart = new MimeBodyPart();
                    attachmentTextPart.setText(emailTemplate);

                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    if (emailTemplate != null) {
                        messageBodyPart.setContent(emailTemplate, "text/html");
                    }

                    Multipart multipart = new MimeMultipart();
                    String fileName = data.get("fileName") != null ? String.valueOf(data.get("fileName")) : null;
                    String filePath = data.get("filePath") != null ? String.valueOf(data.get("filePath")) : null;
//					if (filePath != null) {
//						DataSource source = new FileDataSource("E:\\Users\\savbill\\leaddoc\\1\\");
//						messageBodyPart.setDataHandler(new DataHandler(source));
//					}
//					if (fileName != null)
//						messageBodyPart.setFileName(fileName);
                    File att = new File(new File(filePath), fileName);
                    messageBodyPart.attachFile(att);
                    multipart.addBodyPart(attachmentTextPart);
                    multipart.addBodyPart(messageBodyPart);
                    msg.setContent(multipart);
                    if (emailTemplate != null && !emailTemplate.toString().isEmpty()
                            && emailTemplate.toString() != "") {
                        Transport.send(msg);

//						if (buildEmailBody.get(EVENT_ID) != null && !buildEmailBody.get(EVENT_ID).toString().isEmpty()
//								&& buildEmailBody.get(EVENT_ID).toString() != "") {
//							saveEmailNotificationOnSuccess(emailTemplate.toString(), data,
//									buildEmailBody.get(EVENT_ID).toString(), sourceName);
//
//							if (queueName.equalsIgnoreCase(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)) {
//								//put check condition for etr here and then check add status success
//								HashMap<String, Object> etrAuditData = new HashMap<>();
//								etrAuditData.putAll((HashMap<String, Object>) data);
//								//data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
//								data.put("notificationStatus", "Success");
//								data.put("notificationMode", "email");
//								TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
//								//Gson gson = new Gson();
//								//gson.toJson(ticketETRAuditMessage);
//								messageSender.send(ticketETRAuditMessage, RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION);
//							}
//						}

                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email successfully . Message : " + emailTemplate.toString() + "and Email Address is sent: " + custEmailId + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    }
                }
            } else {
                if (!isEmailConfigured) {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isEmailConfigured' column is false so email is not going to send " + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email id is not valid so email is not going to send: " + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            saveEmailNotificationOnFailure(queueName, data, sourceName, emailTemplate, null);

            //put check condition for etr here and then check add status failed
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "email");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
                //messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
            }
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "email");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
                //messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
            }
            log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email failed. Message :Email Address :\"\n" +
                    "                    + " + data.get(RabbitMqConstants.EMAIL_ID).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            throw new RuntimeException(e.getMessage());
        } finally {
            // MDC.remove(NotificationConstants.TYPE);
            try {
                if (isSmsConfigured) {
                    smsService.sendSmsNotification(queueName, message, data, sourceName, smsTemplate, appendUrl);
                } else {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isSmsConfigured' column is false so sms is not going to send: " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MDC.remove(NotificationConstants.USER_NAME);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @Override
    public void sendEmailNotificationWithAttachmentForAllEvents(String queueName, String message, Map<String, Object> data, String sourceName,
                                                                String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured,
                                                                boolean isSmsConfigured) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        String username = getUserName(data);
        MDC.put(NotificationConstants.USER_NAME, username);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Object> buildEmailBody = new HashMap<>();
        Optional<EmailConfig> emailConfig = Optional.of(new EmailConfig());
        TemplateDto templateDto = getEmailTemplateConfigurationByevent(data, queueName);
        isEmailConfigured = templateDto != null ? templateDto.isEmailEventConfigured() : isEmailConfigured;
        isSmsConfigured = templateDto != null ? templateDto.isSmsEventConfigured() : isSmsConfigured;
        try {
            if (data.get("mvnoId") == null
                    || (data.get("mvnoId").toString().isEmpty() || data.get("mvnoId").toString() == "")) {
                throw new RuntimeException("Mvno id is mandatory.");
            }
            if (data.get("buId") == null
                    || (data.get("buId").toString().isEmpty() || data.get("buId").toString() == "")) {
                emailConfig = emailConfigRepository
                        .findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), null);
                System.out.println("*********************** email config" + emailConfig);
                if (!emailConfig.isPresent() || emailConfig == null) {
                    emailConfig = emailConfigRepository.findByMvnoId(1L);
                }
//				System.out.println("~~~~~~Defualt emailConfig called~~~~~~~");
            } else {
                Optional<EmailConfig> newemailConfig = emailConfigRepository
                        .findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()));
                if (newemailConfig.isPresent()) {
                    emailConfig = newemailConfig;
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "New BuId :" + data.get("buId").toString() + ":emailConfig called " + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());

                } else {
                    emailConfig = emailConfigRepository
                            .findByMvnoIdAndBuIdAndIsDeleteIsFalse(Long.parseLong(data.get("mvnoId").toString()), null);
                    if (emailConfig == null || !emailConfig.isPresent()) {
                        emailConfig = emailConfigRepository.findByMvnoId(1L);
                    }
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Default config called Because buId emailConfig not found: " + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            }
            if (isEmailConfigured && (data.get(RabbitMqConstants.EMAIL_ID) != null && ValidateCrudTransactionData
                    .validateStringTypeFieldValue(data.get(RabbitMqConstants.EMAIL_ID).toString()))) {
                Properties props = new Properties();
                EmailConfig emailConfigDetails = emailConfig.get();
                props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
                if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                    props.put(NotificationConstants.STARTTLS_PARAM, true);
                } else {
                    props.put(NotificationConstants.SSL_PARAM, true);
                }
                props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
                props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfigDetails.getUserName(),
                                emailConfigDetails.getPassword());
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(data.get(RabbitMqConstants.EMAIL_ID).toString()));
                if (data.get(RabbitMqConstants.ALT_EMAIL) != null) {
                    msg.setRecipients(Message.RecipientType.CC,
                            InternetAddress.parse(data.get(RabbitMqConstants.ALT_EMAIL).toString()));
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Alternative Email is set Alt Email : " + data.get(RabbitMqConstants.ALT_EMAIL).toString() + data.get(RabbitMqConstants.EMAIL_ID).toString() + msg + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
                msg.setSubject(message);
                msg.setSentDate(new Date());
                if (data.containsKey("amount")) {
                    if (data.get("amount").equals(0)) {
                        throw new CustomException("No Payment Found !!", 417);
                    }
                }
                buildEmailBody = buildEmailBody(queueName, data, sourceName, emailTemplate);

                if (buildEmailBody.get(TEMPLATE_NOT_FOUND) != null) {
                    throw new CustomException("Template not found !!", 417);
                }
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                if (buildEmailBody.get(EMAIL_CONTENT) != null) {
                    messageBodyPart.setContent(buildEmailBody.get(EMAIL_CONTENT).toString(), "text/html");
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email is set to message body  Email : " + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }

                Multipart multipart = new MimeMultipart();

                multipart.addBodyPart(messageBodyPart);
                MimeBodyPart attachmentPart = new MimeBodyPart();
                String fileName = data.get("fileName") != null ? String.valueOf(data.get("fileName")) : null;
                String filePath = data.get("filePath") != null ? String.valueOf(data.get("filePath")) : null;
                if (Objects.nonNull(filePath) && filePath != null && !fileName.contains("null")) {
                    File att = new File(new File(filePath), fileName);
                    attachmentPart.attachFile(att);
                    multipart.addBodyPart(attachmentPart);
                }
                msg.setContent(multipart);
                if (buildEmailBody.get(EMAIL_CONTENT) != null && !buildEmailBody.get(EMAIL_CONTENT).toString().isEmpty()
                        && buildEmailBody.get(EMAIL_CONTENT).toString() != "") {
                    Transport.send(msg);
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Transport.send method called with the message : " + msg + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    if (buildEmailBody.get(EVENT_ID) != null && !buildEmailBody.get(EVENT_ID).toString().isEmpty()
                            && buildEmailBody.get(EVENT_ID).toString() != "") {
                        saveEmailNotificationOnSuccess(buildEmailBody.get(EMAIL_CONTENT).toString(), data,
                                buildEmailBody.get(EVENT_ID).toString(), sourceName);
                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationStatus", "Success");
                            data.put("notificationMode", "email");
                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
                        }

                        if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationStatus", "Success");
                            data.put("notificationMode", "email");
                            TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                            kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
                        } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {

                            //put check condition for etr here and then check add status success
                            HashMap<String, Object> etrAuditData = new HashMap<>();
                            etrAuditData.putAll((HashMap<String, Object>) data);
                            //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                            data.put("notificationMode", "email");
                            data.put("notificationStatus", "Success");
                            TicketAuditMessage ticketAuditMessage = new TicketAuditMessage(data);
                            //Gson gson = new Gson();
                            //gson.toJson(ticketETRAuditMessage);
//                            messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                            if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)){
                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_SUCCESS));

                            }else if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)){
                                kafkaMessageSender.send(new KafkaMessageData(ticketAuditMessage, ticketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_SUCCESS));

                            }

                        }
                    }
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email successfully sent to email : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + msg + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            } else {
                if (!isEmailConfigured) {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isEmailConfigured' column is false so email is not going to send : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email id is not valid so email is not going to send : " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            }
        } catch (CustomException e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Template not found for bu" + data.get("buId") + " and mvno : " + data.get("mvnoId") + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("*********************** Error while sending email invoice" + e.getMessage());

            saveEmailNotificationOnFailure(queueName, data, sourceName, emailTemplate, null);

            //put check condition for etr here and then check add status failed
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_ETR_AUDIT_SUCCESS));
            }
            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage, ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
            } else if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)) {

                //put check condition for etr here and then check add status success
                HashMap<String, Object> etrAuditData = new HashMap<>();
                etrAuditData.putAll((HashMap<String, Object>) data);
                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
                data.put("notificationStatus", "Failed");
                data.put("notificationMode", "EMAIL");
                TicketAuditMessage TicketAuditMessage = new TicketAuditMessage(data);
                //Gson gson = new Gson();
                //gson.toJson(ticketETRAuditMessage);
//                messageSender.send(TicketAuditMessage, RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT);
                if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM)){
                    kafkaMessageSender.send(new KafkaMessageData(TicketAuditMessage, TicketAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_TAT_AUDIT_SUCCESS));
                }else if(queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE) || queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK)){
                    kafkaMessageSender.send(new KafkaMessageData(TicketAuditMessage, TicketAuditMessage.getClass().getSimpleName(), KafkaConstant.TASK_TAT_AUDIT_SUCCESS));
                }
            }
            log.error(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email failed. Message : " + e.getMessage() + "Email Address :" + data.get(RabbitMqConstants.EMAIL_ID).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            throw new RuntimeException(e.getMessage());
        } finally {
            // MDC.remove(NotificationConstants.TYPE);
            try {
                if (isSmsConfigured) {
                    smsService.sendSmsNotification(queueName, message, data, sourceName, smsTemplate, templateDto.getAppendUrl());
                } else {
                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isSmsConfigured' column is false so sms is not going to send: " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MDC.remove(NotificationConstants.USER_NAME);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    //    @Override
//    public Page<EmailSearchRespDto> searchEmailAudit(EmailSearchReqDto reqDto, Long mvnoId, Integer page, Integer pageSize) {
//        List<Email> dateEmailList =new ArrayList<>();
//        List<Email> withoutDateEmailList =new ArrayList<>();
//        List<Email> combinedList = new ArrayList<>();
//        page = page + 1;
//        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("emailConfigId")));
//    //  if ((reqDto.getFromDate().toString().isEmpty() && reqDto.getFromDate() == null) && (reqDto.getToDate().toString().isEmpty() && reqDto.getToDate() == null)) {
//          if(reqDto.getSourceName() != null || reqDto.getEmailSubject() != null || reqDto.getEmailAddress() != null || reqDto.getStatus() != null) {
////            Page<Email> emails = null;
////            Pageable pageable = PageRequest.of(page, pageSize);
////            Page<EmailSearchRespDto> searchDto = Page.empty(pageable);
//
//            /* Build Search object */
//            Email em = new Email();
//            try {
//                em.setSourceName(reqDto.getSourceName());
//                em.setEmailAddress(reqDto.getEmailAddress());
//                em.setEmailSubject(reqDto.getEmailSubject());
//                em.setStatus(reqDto.getStatus());
//                em.setMvnoId(mvnoId);
//            } catch (Exception e) {
//                e.getMessage();
//            }
//
//            /* Build Example and ExampleMatcher object */
//            ExampleMatcher customExampleMatcher = ExampleMatcher.matching()
//                        .withIgnoreNullValues()
//
//                    .withIgnoreCase();
//            Example<Email> employeeExample = Example.of(em, customExampleMatcher);
//
//            /* Get employees based on search criteria*/
//             withoutDateEmailList = emailRepository.findAll(employeeExample);
//             combinedList.addAll(withoutDateEmailList);
//        }else if (reqDto.getFromDate() != null && reqDto.getToDate() != null){
//              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//              LocalDateTime fromDate = LocalDateTime.parse(reqDto.getFromDate(),formatter);
//              LocalDateTime toDate = LocalDateTime.parse(reqDto.getToDate(),formatter);
//              dateEmailList =  emailRepository.findByMvnoIdAndDateBetween(mvnoId, fromDate,toDate);
//              if(reqDto.getEmailSubject()==null && reqDto.getSourceName() ==null && reqDto.getEmailAddress()==null && reqDto.getStatus()==null){
//                  combinedList.addAll(dateEmailList);
//              }
//              if(reqDto.getEmailSubject()!=null && reqDto.getSourceName() ==null && reqDto.getEmailAddress()==null && reqDto.getStatus()==null){
//                  List<Email> collect = dateEmailList.stream().filter(e -> e.getEmailSubject() == reqDto.getEmailSubject()).collect(Collectors.toList());
//                  combinedList.addAll(collect);
//              }
//              if(reqDto.getEmailSubject()!=null && reqDto.getSourceName() !=null && reqDto.getEmailAddress()==null && reqDto.getStatus()==null){
//                  List<Email> collect = dateEmailList.stream().filter(e -> e.getEmailSubject() == reqDto.getEmailSubject()).filter(e -> e.getSourceName() == reqDto.getSourceName()).collect(Collectors.toList());
//                  combinedList.addAll(collect);
//              }
//              if(reqDto.getEmailSubject()!=null && reqDto.getSourceName() !=null && reqDto.getEmailAddress()!=null && reqDto.getStatus()==null){
//                  List<Email> collect = dateEmailList.stream().filter(e -> e.getEmailSubject() == reqDto.getEmailSubject()).filter(e -> e.getSourceName() == reqDto.getSourceName()).filter(e -> e.getEmailAddress() == reqDto.getEmailAddress()).collect(Collectors.toList());
//                  combinedList.addAll(collect);
//              }
//              if(reqDto.getEmailSubject()!=null && reqDto.getSourceName() !=null && reqDto.getEmailAddress()!=null && reqDto.getStatus()!=null){
//                  List<Email> collect = dateEmailList.stream().filter(e -> e.getEmailSubject() == reqDto.getEmailSubject()).filter(e -> e.getSourceName() == reqDto.getSourceName()).filter(e -> e.getEmailAddress() == reqDto.getEmailAddress()).filter(e -> e.getStatus() == reqDto.getStatus()).collect(Collectors.toList());
//                  combinedList.addAll(collect);
//              }
//        }else {
//              List<Email> collect = emailRepository.findByMvnoId(mvnoId);
//              combinedList.addAll(collect);
//          }
//          Set<Email> uniqueEmails = new HashSet<>();
//          List<Email> finalFilterList = new ArrayList<>();
//        for (Email email : combinedList) {
//            if (uniqueEmails.add(email)){
//                finalFilterList.add(email);
//            }
//        }
//
//
////        Page<Email> emails = null;
////        Pageable pageable = PageRequest.of(page, pageSize);
////        Page<EmailSearchRespDto> searchDto = Page.empty(pageable);
////        ModelMapper mp = new ModelMapper();
////        mp.map(finalFilterList, searchDto);
//        List<EmailSearchRespDto> emailConfigDtos = finalFilterList.stream()
//                .map(this::createEmailConfigurationDTO)
//                .collect(Collectors.toList());
//        return new PageImpl<>(emailConfigDtos, pageable, emailConfigDtos.size());
////        return new PageImpl<>(finalFilterList, pageable, finalFilterList.size());
//    }
//
//    private EmailSearchRespDto createEmailConfigurationDTO(Email email) {
//        return new EmailSearchRespDto(
//                email.getMvnoId(),
//                email.getEmailId(),
//                email.getEmailAddress(),
//                email.getMessage(),
//                email.getDate(),
//                email.getStatus(),
//                email.getEmailSubject(),
//                email.getServiceType(),
//                email.getEventName(),
//                email.getSourceName()
//        );
//    }
//
    @Override
    public Page<EmailDto> searchEmailAudit(PaginationRequestDTO dto, Long mvnoId, String servicetype) {
        Integer page = dto.getPage();
        Integer size = dto.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("emailId")));
        Specification<Email> spec = Specification.where(null);
        Page<Email> email = null;
        spec.and((root, query, builder) -> builder.equal(root.get("isDelete"), false));
        if (mvnoId != 1) {
            spec = spec.and((root, query, builder) -> (root.get(NotificationConstants.SmsSearchEnum.MVNO_ID).in(Arrays.asList(mvnoId, 1))));
        }
        if (!servicetype.trim().isEmpty()) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.SmsSearchEnum.SERVICE_TYPE), servicetype));
        }
        if (null != dto.getFilters() && dto.getFilters().size() > 0) {
            for (GenericSearchModel searchModel : dto.getFilters()) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.ANY)) {
                    email = getAllemail(searchModel.getFilterValue().trim(), searchModel.getFilterCondition().trim(), spec, pageable, mvnoId, servicetype.trim());
                } else {
                    spec = getSmsConfigByFilter(searchModel.getFilterValue(), searchModel.getFilterCondition(), searchModel.getFilterColumn().trim(), spec, pageable, mvnoId, servicetype);
                }
            }
        }
        if (email == null || (email.isEmpty())) {
            email = emailRepository.findAll(spec, pageable);
        }

//    if(email==null || (email.isEmpty())){
//        throw new RuntimeException("No Record Found!");
//    }
        Page<EmailDto> map = email.map(this::setPropertiesToDto);
        return map;
    }

    public Page<Email> getAllemail(String value, String filterCondition, Specification<Email> spec, Pageable pageable, Long mvnoId, String serviceType) {
        Email email = new Email();
        try {
            email.setSourceName(value.trim());
            email.setEmailAddress(value.trim());
            email.setServiceType(value.trim());
            email.setEmailSubject(value.trim());
            email.setStatus(value.trim());
            email.setMvnoId(Long.valueOf(value.trim()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("emailId", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("sourceName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("emailAddress", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("message", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());


        Example<Email> example = Example.of(email, customExampleMatcher);
        Page<Email> emails = emailRepository.findAll(example, pageable);
        return emails;
    }

    public Specification<Email> getSmsConfigByFilter(String value, String filterCondition, String filterColumn, Specification<Email> spec, Pageable pageable, Long mvnoId, String serviceType) {
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

    public EmailDto setPropertiesToDto(Email email) {
        return new EmailDto(
                email.getSourceName(),
                email.getEmailAddress(),
                email.getMessage(),
                email.getStatus(),
                null,
                email.getCreatedBy(),
                email.getDate(),
                email.getEmailSubject()
        );
    }

    public Specification<Email> dateFilter(String value, String filterCondition, Specification<Email> spec, String filterColumn, Long mvnoId, String serviceType) {
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

    @Override
    public boolean validation(PaginationRequestDTO dto) {
        for (GenericSearchModel searchModel : dto.getFilters()) {
            if (searchModel.getFilterValue() == null || searchModel.getFilterValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void validateEmailData(Long mvnoId, Email email, Long eventId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(eventId)) {
                throw new IllegalArgumentException("Event id is mandatory. Please enter valid event id");
            } else if (email.getSourceName() == null || email.getSourceName().isEmpty()
                    || email.getSourceName().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
                throw new IllegalArgumentException("Source Name is mandatory. Please enter valid Source Name.");
            } else if (email.getEmailAddress() == null || email.getEmailAddress().isEmpty()
                    || email.getEmailAddress().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
                throw new IllegalArgumentException("Email Address is mandatory. Please enter valid Email Address.");
            } else if (email.getMessage() == null || email.getMessage().isEmpty()
                    || email.getMessage().equalsIgnoreCase(NotificationConstants.BLANK_STRING)) {
                throw new IllegalArgumentException("Message is mandatory. Please enter valid Message.");
            }

            Optional<Event> eventVo = eventRepository.findById(eventId);
            if (!eventVo.isPresent()) {
                throw new IllegalArgumentException(
                        "No event found with event id : '" + eventId + "' , Please enter valid event id.");
            }
            email.setEvent(eventVo.get());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Long validateEmailConfigId(Long mvnoId, Long buId, String eventName) {
//        Optional<EmailConfig> optionalEmailConfig = null;
//        optionalEmailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(mvnoId, buId);
        List<EmailConfig> emailConfigs = emailConfigRepository.findAllByMvnoIdAndBuIdAndIsDeleteIsFalse(mvnoId, buId);
        if (emailConfigs == null || emailConfigs.isEmpty()) {
            if (eventName.equalsIgnoreCase(NotificationConstants.LOGIN_OTP_EVENT)) {
//                optionalEmailConfig = emailConfigRepository.findByMvnoId(1L);
                List<EmailConfig> defaultConfigs = emailConfigRepository.findAllByMvnoId(1L);
                if (defaultConfigs == null || defaultConfigs.isEmpty()) {
                    throw new RuntimeException("No record found for email configuration");
                }
                return defaultConfigs.get(0).getEmailConfigId();
            }
            return null;
        }
        return emailConfigs.get(0).getEmailConfigId();
    }

    private Long validateEmailConfigId(Long mvnoId, Long buId) {
        Optional<EmailConfig> optionalEmailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(mvnoId, buId);
        if (!optionalEmailConfig.isPresent()) {
            throw new RuntimeException("No record found for email configuration");
        }
        return optionalEmailConfig.get().getEmailConfigId();
    }

    private Map<String, Object> buildEmailBody(String queueName, Map<String, Object> data, String sourceName,
                                               String emailTemplate) {
        Map<String, Object> returnData = new HashMap<>();
        try {
            String emailContent = "";
            String eventName = SmsServiceImpl.getEventNameBasedOnQueueName(queueName);
            if (eventName != null && eventName != "" && !eventName.isEmpty()) {
                if (eventName.equals(NotificationConstants.Inventory_Event_Name.DEVICE_INPUT_PORT_CONSUMED_PERCENTAGE_EVENT)) {
                    returnData = inventoryEmailService.buildInventoryDeviceInputPortConsumedPercentageMessage(data, emailContent, eventName, sourceName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.OTP_GENERATED_EVENT)) {
                    returnData = buildEmailBodyForOtpGeneration(data, emailContent, eventName, sourceName,
                            emailTemplate);
                } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_ASSIGNMENT_SUCCESS_EVENT)) {
                    returnData = inventoryEmailService.buildInventoryAssignmentForStaffMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.LEAD_CREATION_EVENT)) {
                    returnData = buildLeadCreationMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.LOGIN_SUCCESS_EVENT)
                        || eventName.equals(NotificationConstants.LOGIN_FAILURE_EVENT)) {
                    //This messege is not in used
                    returnData = buildEmailBodyForLoginEvent(data, emailContent, eventName, sourceName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.USED_QUOTA_EVENT)) {
                    //This message is not in used
                    returnData = buildEmailBodyForUsedQuotaEvent(data, emailContent, eventName, sourceName,
                            emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUST_APPROVAL)) {
                    returnData = buildEmailBodyForCustApprovalEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUST_REJECT)) {
                    returnData = buildEmailBodyForCustRejectEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUST_RENEW_SUCCESS)) {
                    returnData = buildEmailBodyForCustRenew(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUST_RECHARGE_SUCCESS)) {
                    returnData = buildEmailBodyForCustRecharge(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_REGISTRATION_SUCCESS_EVENT)) {
                    returnData = buildEmailBodyForCustRegistrationEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_REGISTRATION_FAILURE_EVENT)) {
                    returnData = buildEmailBodyForPaymentSuccess(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_LINK)) {
                    returnData = buildEmailBodyForPaymentLink(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_SUCCESS)) {
                    returnData = buildEmailBodyForPaymentSuccess(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_ASSIGN_SUCCESS)) {
                    returnData = buildticketassingtostaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER)) {
                    returnData = buildCustomerDunning(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER)) {
                    returnData = buildCustomerDeactivation(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER)) {
                    returnData = buildDocumentDunning(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildFollowUpReminderStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                    returnData = buildFollowUpReminderCustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildFollowUpOverDueStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                    returnData = buildFollowUpOverDueParentStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER)) {
                    returnData = buildStaffStatus(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildNoLeadFollowUpReminderStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                    returnData = buildNoLeadFollowUpReminderParentStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildNoFollowUpReminderStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                    returnData = buildNoFollowUpReminderParentStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.WORKFLOW_ACTION_ASSIGN)) {
                    returnData = buildWorkflowActionAssign(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_STATUS_CHANGE_EVENT)) {
                    returnData = buildticketstatuschangetocustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)) {
                    returnData = buildTatNotificationToTeam(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TAT_NOTIFICATION_TO_TEAM_FOR_TASK)) {
                    returnData = buildTatNotificationToTeam(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.FOLLOWUP_REMARK_MSG)) {
                    returnData = buildFollowupRemarkMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.PROBLEM_DOMAIN_EVENTNAME)) {
                    returnData = buildProblemDomainChangeMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CATEGORY_CHANGE_EVENTNAME_FOR_TASK)) {
                    returnData = buildProblemDomainChangeMsg(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.TICKET_ETR)) {
                    if (!(Boolean) data.get("isTemplateDynamic"))
                        returnData = buildTicketETRMsg(data, emailContent, eventName, emailTemplate);
                    else
                        returnData = buildTicketETRDynamicMsg(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.TASK_ETR)) {
                    if (!(Boolean) data.get("isTemplateDynamic"))
                        returnData = buildTicketETRMsg(data, emailContent, eventName, emailTemplate);
                    else
                        returnData = buildTicketETRDynamicMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                    returnData = buildCafFollowUpReminderCustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildCafFollowUpReminderStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildCafFollowUpOverDueStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                    returnData = buildCafFollowUpOverDueParentStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER)) {
                    returnData = buildTicketFollowUpReminderCustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildTicketFollowUpReminderStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF)) {
                    returnData = buildTicketFollowUpOverDueStaff(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF)) {
                    returnData = buildTicketFollowUpOverDueParentStaff(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.TICKET_CREATION)) {
                    //This message is not in used
                    returnData = buildTicketCreateNotification(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_CREATION_NOTIFICATION)) {
                    //This message is not in used
                    returnData = buildTaskCreateNotification(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_RESCHEDULE_EVENT)) {
                    returnData = buildTicketRescheduleMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_TAT_REMINDER_NOTIFICATION)) {
                    returnData = buildTicketReminderNotificationMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION)) {
                    returnData = buildTicketOverDueReminderMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER)) {
                    returnData = buildDunningAdvanceNotificationMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER)) {
                    returnData = buildDunningPartnerDocumentMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER)) {
                    returnData = buildDunningPartnerDocumentMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER)) {
                    returnData = buildDocumentDunning(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_STATUS_INACTIVATE_EVENT)) {
                    returnData = buildCustStatusInActiveNotificationMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DOCUMENT_VERIFICATION_EVENT)) {
                    returnData = buildCustDocumentVerificationMessage(data, emailContent, eventName, emailTemplate, "CustDocumentVerification");
                } else if (eventName.equalsIgnoreCase(NotificationConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)) {
                    returnData = buildCustDocumentVerificationMessage(data, emailContent, eventName, emailTemplate, "quotationReport");
                } else if (eventName.equals(NotificationConstants.CUSTOMER_SERVICE_ACTIVE_EVENT)) {
                    returnData = buildCustServiceActiveMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_SERVICE_INACTIVE_EVENT)) {
                    returnData = buildCustServiceInActiveMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_CHANGE_PASSWORD_EVENT)) {
                    returnData = buildCustChangePasswordMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER)) {
                    returnData = buildCustChangePasswordMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT)) {
                    returnData = buildCustAddressShiftingMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT)) {
                    returnData = buildCustAddressShiftingMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_PAYMENT_VERIFICATION_EVENT)) {
                    returnData = buildCustPaymentVerificationMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_TICKET_CLOSE_EVENT)) {
                    returnData = buildCustTicketCloseMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER)) {
                    returnData = buildDocumentDunningDeactivation(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TAT_NOTIFICATION)) {
                    returnData = buildTatNotificationToTeam(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_TAT_NOTIFICATION)) {
                    returnData = buildTatNotificationToTeam(data, emailContent, eventName, emailTemplate);
                }  else if (eventName.equals(NotificationConstants.TICKET_REMARK_CUSTOMER_EVENT)) {
                    returnData = buildFollowupRemarkMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_QUOTA_USAGE_NOTIFICATION_EVENT)) {
                    returnData = buildCustomerQuotaNotificationMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_QUOTA_EXHAUST_NOTIFICATION_EVENT)) {
                    returnData = buildCustomerQuotaNotificationMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TICKET_EXTERNAL_REMARK_CUSTOMER_EVENT)) {
                    returnData = buildExternalRemarkMsg(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.TASK_EXTERNAL_REMARK_EVENT)) {
                    returnData = buildExternalRemarkMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CUSTOMER_INVOICE_EVENT)) {
                    returnData = buildCustomerInvoiceMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.STAFF_TICKET_ALERT_EVENT)) {
                    returnData = buildStaffTicketAlertMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.STAFF_TASK_ALERT_EVENT)) {
                    returnData = buildStaffTicketAlertMsg(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER)) {
                    returnData = buildImmediteInqueryEmailToUnRegisterCustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_REGISTERED_CUSTOMER)) {
                    returnData = buildImmediteInqueryEmailToRegisterCustomer(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER_STAFF)) {
                    returnData = buildImmediteInqueryEmailToUnRegisterCustomerToStaffMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.STAFF_UNPICK_TICKET_ALERT_EVENT)) {
                    returnData = buildStaffUnpickTicketAlertMsg(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.MVNO_DOCUMENT_DUNNING_EVENT)) {
                    returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.MVNO_DEACTIVATION_EVENT)) {
                    returnData = buildEmailBodyForMvnoDocumentDunningEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.MVNO_PAYMENT_REMAINDER_EVENT)) {
                    returnData = buildEmailBodyForMvnoPaymentDunningEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.MVNO_PAYMENT_EXPIRY_EVENT)) {
                    returnData = buildEmailBodyForMvnoPaymentDunningEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.PLAN_EXPIRY_EVENT)) {
                    returnData = buildEmailBodyForPlanExpiryEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.LOGIN_OTP_EVENT)) {
                    returnData = buildEmailBodyForOtpGeneration(data, emailContent, eventName, sourceName,
                            emailTemplate);
                } else if (eventName.equals(NotificationConstants.CHANGE_PLAN_EVENT)) {
                    returnData = buildEmailBodyForChangePlanEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_REQUEST_EVENT)) {
                    returnData = inventoryEmailService.buildInventoryRequestMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_FULFILMENT_EVENT)) {
                    returnData = inventoryEmailService.buildInventoryFulfilmentMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_REMARK_EVENT)) {
                    returnData=buildEmailBodyForTaskFollowupEvent(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_RESCHEDUKE_EVENT)) {
                    returnData=buildTaskCreateNotification(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_CLOSED_EVENT)) {
                    returnData=buildTaskCreateNotification(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.TASK)) {
                    returnData=buildTaskCreateNotification(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.TASK_UPDATE)) {
                    returnData=buildTaskUpdateNotification(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.Inventory_Event_Name.INVENTORY_THRESHOLD_EVENT)) {
                    returnData = inventoryEmailService.buildInventoryThresholdMessage(data, emailContent, eventName, emailTemplate);
                } else if (eventName.equals(NotificationConstants.CHILD_CUSTOMER_REGISTRATION_SUCCESS_EVENT)) {
                    returnData = buildEmailBodyForChildCustRegistrationEvent(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.AUTO_RENEWAL_PREFERENCE_CHANGED_EVENT)) {
                    returnData=buildInsufficientWalletNotification(data, emailContent, eventName, emailTemplate);
                }else if (eventName.equals(NotificationConstants.INSUFFICIENT_WALLET_EVENT)) {
                    returnData=buildInsufficientWalletNotification(data, emailContent, eventName, emailTemplate);
                }

            }
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }




    private Map<String, Object> buildTaskCreateNotification(Map<String, Object> data, String emailContent, String eventName, String emailTemplate) {
            try {
                Map<String, Object> returnData = new HashMap<>();
                Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
                if (optionalEvent.isPresent()) {
                    String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_FOR) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{taskFor\\}",
                                data.get(RabbitMqConstants.TASK_FOR).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                                data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.USER_TYPE) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{type\\}",
                                data.get(RabbitMqConstants.USER_TYPE).toString());
                    }

                    if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{staffName\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_PRIORITY) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{casePriority\\}",
                                data.get(RabbitMqConstants.TASK_PRIORITY).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_STATUS) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseStatus\\}",
                                data.get(RabbitMqConstants.TASK_STATUS).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_REMARK) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseRemark\\}",
                                data.get(RabbitMqConstants.TASK_REMARK).toString());
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_LOGO) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{urlLogo\\}",
                                "https://savbillnettech.com/wp-content/uploads/2021/06/savbill-logo.png");
                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_URL) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{taskURL\\}",
                                data.get(RabbitMqConstants.TASK_URL).toString());
                    }
//				if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
//							data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//				}
//				if (emailTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{eventName\\}",
//							data.get(RabbitMqConstants.EVENT_NAME).toString());
//				}
                    if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }

                    if (emailTemplateData != null && data.get(RabbitMqConstants.START_DATE_TIME) != null) {
                        String input = data.get(RabbitMqConstants.START_DATE_TIME).toString();
                        String fixedInput = input.replace("T", ":");
                        emailTemplateData = emailTemplateData.replaceAll("\\{startDate\\}",
                                fixedInput);

                    }
                    if (emailTemplateData != null && data.get(RabbitMqConstants.END_DATE_TIME) != null) {
                        String input = data.get(RabbitMqConstants.END_DATE_TIME).toString();
                        String fixedInput = input.replace("T", ":");
                        emailTemplateData = emailTemplateData.replaceAll("\\{endDate\\}",
                                fixedInput);
                    }
                    if (Objects.isNull(data.get(RabbitMqConstants.START_DATE_TIME))) {
                        emailTemplateData = emailTemplateData.replaceAll("The task duration is from \\{startDate\\} to \\{endDate\\}\\.", "");
                    }
                    emailContent = emailTemplateData;

                    if (emailContent != null) {
                        returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                    } else {
                        saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                        returnData.put(TEMPLATE_NOT_FOUND, true);
                        //throw new CustomException("Template not found for this mvno and buid",417);
                    }
                }
                returnData.put(EMAIL_CONTENT, emailContent);
                return returnData;
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }


    }

    private Map<String, Object> buildEmailBodyForLoginEvent(Map<String, Object> data, String emailContent,
                                                            String eventName, String sourceName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildEmailBodyForCustomerEvent(Map<String, Object> data, String emailContent,
                                                               String eventName, String sourceName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.PASSWORD).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveEmailNotificationOnSuccess(String message, Map<String, Object> data, String eventId,
                                                String sourceName) {
        try {
            Long longEventId = Long.parseLong(eventId);
            Optional<Event> eventVo = eventRepository.findById(longEventId);

            if (!eventVo.isPresent()) {
                throw new RuntimeException("Event not found");
            }
            saveEmail(data.get(RabbitMqConstants.EMAIL_ID).toString(), message, data, eventVo.get(), sourceName);

            if (data.containsKey("altEmail") && !Objects.isNull(data.get("altEmail"))) {
                saveEmail(data.get("altEmail").toString(), message, data, eventVo.get(), sourceName);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveEmail(String emailAddress, String message, Map<String, Object> data, Event eventVo, String sourceName) {
        Email emailVo = new Email();
        Long cprId = data.get("cprId") != null ? Long.valueOf(data.get("cprId").toString()) : null;
        emailVo.setEmailAddress(emailAddress);
        emailVo.setEvent(eventVo);
        emailVo.setEventName(eventVo.getEventName());
        emailVo.setMessage(message);
        emailVo.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_BSS);
        emailVo.setDate(LocalDateTime.now());
        emailVo.setStatus(SmsServiceImpl.SENT);
        emailVo.setCreateDate(LocalDateTime.now());
        emailVo.setLastModifiedDate(LocalDateTime.now());
        emailVo.setCprId(cprId);

        if (sourceName == null) {
            emailVo.setSourceName("Savbill BSS API GATEWAY");
        } else {
            emailVo.setSourceName(sourceName);
        }

        if (data.get("mvnoId") != null && !data.get("mvnoId").toString().isEmpty()) {
            if (data.get("buId") == null || data.get("buId").toString().isEmpty()) {
                emailVo.setEmailConfigId(validateEmailConfigId(Long.parseLong(data.get("mvnoId").toString()), null, emailVo.getEventName()));
            } else {
                Optional<EmailConfig> optionalEmailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(
                        Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString())
                );

                if (optionalEmailConfig.isPresent()) {
                    emailVo.setEmailConfigId(validateEmailConfigId(
                            Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()), null
                    ));
                } else {
                    emailVo.setEmailConfigId(validateEmailConfigId(Long.parseLong(data.get("mvnoId").toString()), null, null));
                }
            }
            emailVo.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));

            if (data.get("buId") == null || data.get("buId").toString().isEmpty()) {
                emailVo.setBuId(null);
            } else {
                emailVo.setBuId(Long.parseLong(data.get("buId").toString()));
            }
        }

        emailRepository.save(emailVo);
    }

    public void saveEmailNotificationOnFailure(String queueName, Map<String, Object> data, String sourceName,
                                                String emailTemplate, String evntName) {
        try {
            String eventName;
            boolean isTemplateNotFound = false;

            if (evntName == null) {
                eventName = SmsServiceImpl.getEventNameBasedOnQueueName(queueName);
            } else {
                eventName = evntName;
                isTemplateNotFound = true;
            }

            if (eventName != null && !eventName.isEmpty() && Objects.nonNull(data.get(RabbitMqConstants.EMAIL_ID))) {
                Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
                if (optionalEvent.isPresent()) {
                    Event eventVo = optionalEvent.get();
                    if(data.containsKey(RabbitMqConstants.EMAIL_ID)){
                        saveFailureEmail(data.get(RabbitMqConstants.EMAIL_ID).toString(), queueName, data, sourceName, emailTemplate, eventVo, eventName, isTemplateNotFound);
                    }
                    if (data.containsKey("altEmail")  &&  Objects.nonNull(data.get("altEmail"))) {
                        saveFailureEmail(data.get("altEmail").toString(), queueName, data, sourceName, emailTemplate, eventVo, eventName, isTemplateNotFound);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveFailureEmail(String emailAddress, String queueName, Map<String, Object> data, String sourceName,
                                  String emailTemplate, Event eventVo, String eventName, boolean isTemplateNotFound) {
        Email emailVo = new Email();
        Long cprId = data.get("cprId") != null ? Long.valueOf(data.get("cprId").toString()) : null;
        emailVo.setEmailAddress(emailAddress);
        emailVo.setEvent(eventVo);
        emailVo.setEventName(eventName);
        emailVo.setDate(LocalDateTime.now());
        emailVo.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_BSS);
        emailVo.setStatus(SmsServiceImpl.FAIL);
        emailVo.setCreateDate(LocalDateTime.now());
        emailVo.setLastModifiedDate(LocalDateTime.now());
        emailVo.setCprId(cprId);

        if (sourceName == null) {
            emailVo.setSourceName("Savbill BSS API GATEWAY");
        } else {
            emailVo.setSourceName(sourceName);
        }

        if (!isTemplateNotFound) {
            Map<String, Object> buildEmailBody = buildEmailBody(queueName, data, sourceName, emailTemplate);
            if (buildEmailBody.get(EMAIL_CONTENT) != null && !buildEmailBody.get(EMAIL_CONTENT).toString().isEmpty()) {
                if (buildEmailBody.get(EVENT_ID) != null && !buildEmailBody.get(EVENT_ID).toString().isEmpty()) {
                    emailVo.setMessage(buildEmailBody.get(EMAIL_CONTENT).toString());
                }
            }
        } else {
            emailVo.setMessage("Template is not found for event: " + eventName);
            emailVo.setRemark("Template not found for mvnoId: " + data.get("mvnoId") + " and buId: " + data.get("buId") + " for event: " + eventName);
            emailVo.setSourceName("Notification");
        }

        if (data.get("mvnoId") != null && !data.get("mvnoId").toString().isEmpty()) {
            if (data.get("buId") == null || data.get("buId").toString().isEmpty()) {
                if(Objects.nonNull(eventName)){
                    emailVo.setEmailConfigId(validateEmailConfigId(Long.parseLong(data.get("mvnoId").toString()), null, eventName));
                }else{
                    emailVo.setEmailConfigId(validateEmailConfigId(Long.parseLong(data.get("mvnoId").toString()), null, null));
                }

            } else {
                Optional<EmailConfig> optionalEmailConfig = emailConfigRepository.findByMvnoIdAndBuIdAndIsDeleteIsFalse(
                        Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString())
                );

                if (optionalEmailConfig.isPresent()) {
                    emailVo.setEmailConfigId(validateEmailConfigId(
                            Long.parseLong(data.get("mvnoId").toString()), Long.parseLong(data.get("buId").toString()), null
                    ));
                } else {
                    emailVo.setEmailConfigId(validateEmailConfigId(Long.parseLong(data.get("mvnoId").toString()), null, null));
                }
            }

            emailVo.setMvnoId(Long.parseLong(data.get("mvnoId").toString()));

            if (isTemplateNotFound) {
                if (data.get("buId") != null) {
                    emailVo.setRemark("Template not found for buId: " + data.get("buId") + " and mvnoId: " + data.get("mvnoId"));
                } else {
                    emailVo.setRemark("Template not found for buId: null and mvnoId: " + data.get("mvnoId"));
                }
            }

            if (data.get("buId") == null || data.get("buId").toString().isEmpty()) {
                emailVo.setBuId(null);
            } else {
                emailVo.setBuId(Long.parseLong(data.get("buId").toString()));
            }
        }

        emailRepository.save(emailVo);
    }

    private Map<String, Object> buildEmailBodyForOtpGeneration(Map<String, Object> data, String emailContent,
                                                               String eventName, String sourceName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{mobileNumber\\}",
                            data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.OTP) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{otp\\}",
                            data.get(RabbitMqConstants.OTP).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{datetime\\}",
                            data.get(RabbitMqConstants.DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TIME_FRAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{timeframe\\}",
                            data.get(RabbitMqConstants.TIME_FRAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                    emailTemplateData = emailTemplateData.replaceAll("\\{web\\}", NotificationConstants.WEB);
                }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildEmailBodyForUsedQuotaEvent(Map<String, Object> data, String emailContent,
                                                                String eventName, String sourceName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SLICE_CHUNK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sliceChunk\\}",
                            data.get(RabbitMqConstants.SLICE_CHUNK).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildEmailBodyForCustApprovalEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{approverTeam\\}",
                            data.get(RabbitMqConstants.TEAM_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildEmailBodyForCustRejectEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.WALLET_BALANCE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentAmount\\}", data.get("amount").toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CURRENCY) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{currencySymbol\\}", data.get("Currency").toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{approverTeam\\}",
                            data.get(RabbitMqConstants.TEAM_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildEmailBodyForCustRegistrationEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SLICE_CHUNK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{name\\}",
                            data.get(RabbitMqConstants.TEAM_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.REGISTRATION_DATE) != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String date = data.get(RabbitMqConstants.REGISTRATION_DATE).toString();
                    date = date.replaceAll("\\.\\d+", "");
                    LocalDateTime regidate = LocalDateTime.parse(date, formatter);
                    emailTemplateData = emailTemplateData.replaceAll("\\{registrationDate\\}",
                            regidate.toLocalDate().toString());
                }
                if (emailTemplateData != null) {
                    String accountNumber = data.getOrDefault(RabbitMqConstants.ACCOUNT_NUMBER, "").toString();
                    emailTemplateData = emailTemplateData.replaceAll("\\{accountNumber\\}", accountNumber);
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planName\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForCustRegistrationFail(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SLICE_CHUNK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{name\\}",
                            data.get(RabbitMqConstants.TEAM_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForCustRecharge(Map<String, Object> data, String emailContent,
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
//                    if (emailTemplateData != null) {
//                        emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
//                    }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildticketassingtostaff(Map<String, Object> data, String emailContent, String eventName,
                                                        String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.ASSIGN_TEAM_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{team\\}",
                            data.get(RabbitMqConstants.ASSIGN_TEAM_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_FOLLOW_UPDATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{Followupdate\\}",
                            data.get(RabbitMqConstants.CASE_FOLLOW_UPDATE).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{eventName\\}",
                            data.get(RabbitMqConstants.EVENT_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{Assigndatetime\\}",
                            data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            data.put("notificationMessage", emailContent);
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForCustRenew(Map<String, Object> data, String emailContent,
                                                          String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                log.info(data.toString());
                if (data.get(RabbitMqConstants.MOBILE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{mobileNumber\\}",
                            data.get(RabbitMqConstants.MOBILE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
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
                    emailTemplateData = emailTemplateData.replaceAll("\\{customername\\}", fullName);
                }



                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_PLAN) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{plan\\}",
                            data.get(RabbitMqConstants.CUST_PLAN).toString());
                }
//                    if (emailTemplateData != null) {
//                        emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
//                    }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildEmailBodyForPaymentLink(Map<String, Object> data, String emailContent,
                                                            String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{currencySymbol\\}",
                            data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentAmount\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_URL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{url\\}",
                            data.get(RabbitMqConstants.CUSTMR_URL).toString());
                }
//                    if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_URL2) != null) {
//                        emailTemplateData = emailTemplateData.replaceAll("\\{url2\\}", RabbitMqConstants.CUSTMR_URL2);
//                    }
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildEmailBodyForPaymentSuccess(Map<String, Object> data, String emailContent,
                                                               String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{currencySymbol\\}",
                            data.get(RabbitMqConstants.CUSTMR_CURRENCY_SYMBOLE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentAmount\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_MODE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentMode\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_MODE).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_ID) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userId\\}",
                            data.get(RabbitMqConstants.CUSTMR_ID).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_USER_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{reciptNo\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentDate\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.PASSWORD).toString());
                }


                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildDocumentDunning(Map<String, Object> data, String emailContent, String eventName,
                                                    String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildFollowUpReminderStaff(Map<String, Object> data, String emailContent,
                                                          String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildFollowUpOverDueParentStaff(Map<String, Object> data, String emailContent,
                                                               String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }


                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildNoLeadFollowUpReminderStaff(Map<String, Object> data, String emailContent,
                                                                String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildNoFollowUpReminderParentStaff(Map<String, Object> data, String emailContent,
                                                                  String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupName\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildNoFollowUpReminderStaff(Map<String, Object> data, String emailContent,
                                                            String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupName\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildNoLeadFollowUpReminderParentStaff(Map<String, Object> data, String emailContent,
                                                                      String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildFollowUpOverDueStaff(Map<String, Object> data, String emailContent,
                                                         String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildFollowUpReminderCustomer(Map<String, Object> data, String emailContent,
                                                             String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);


                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildCustomerDunning(Map<String, Object> data, String emailContent, String eventName,
                                                    String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{amount\\}",
                            data.get(RabbitMqConstants.AMOUNT).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.START_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{startDate\\}",
                            data.get(RabbitMqConstants.START_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.END_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{endDate\\}",
                            data.get(RabbitMqConstants.END_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SUB_TOTAL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{subTotal\\}",
                            data.get(RabbitMqConstants.SUB_TOTAL).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TAX_AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taxAmount\\}",
                            data.get(RabbitMqConstants.TAX_AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TAX_PERCENTAGE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{totalDue\\}",
                            data.get(RabbitMqConstants.TAX_PERCENTAGE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TOTAL_DUE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taxPercentage\\}",
                            data.get(RabbitMqConstants.TOTAL_DUE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CREDIT_BALANCE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{walletBalance\\}",
                            data.get(RabbitMqConstants.CREDIT_BALANCE).toString());
                }





                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildCustomerDeactivation(Map<String, Object> data, String emailContent,
                                                         String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{amount\\}",
                            data.get(RabbitMqConstants.AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_REMARKS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remarks\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_PLANS_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.CUST_PLANS_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{date\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildStaffStatus(Map<String, Object> data, String emailContent, String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}", data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}", data.get(RabbitMqConstants.STATUS).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildWorkflowActionAssign(Map<String, Object> data, String emailContent,
                                                         String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.ACTION) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{action\\}",
                            data.get(RabbitMqConstants.ACTION).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if(data.get("isFromTask")!=null){
                    emailTemplateData = emailTemplateData.replaceAll("ticket", "task");
                    emailTemplateData = emailTemplateData.replaceAll("Ticket", "Task");
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildticketstatuschangetocustomer(Map<String, Object> data, String emailContent, String eventName,
                                                                 String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

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
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildTatNotificationToTeam(Map<String, Object> data, String emailContent, String eventName,
                                                          String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TAT_TEAM_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamName\\}",
                            data.get(RabbitMqConstants.TAT_TEAM_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{eventName\\}",
                            data.get(RabbitMqConstants.EVENT_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
//                if (emailTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//                    String date = data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString();
//                    date = date.replaceAll("\\.\\d+", "");
//                    LocalDateTime regidate;
//                    try {
//                        regidate = LocalDateTime.parse(date, formatter);
//                    } catch (Exception e) {
//                        regidate = LocalDateTime.parse(date, formatter1);
//                    }
//                    DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    String formattedDateTime = regidate.format(newFormatter);
//                    emailTemplateData = emailTemplateData.replaceAll("\\{Assigndatetime\\}",
//                            formattedDateTime);
//                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
                    String date = data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString();
                    emailTemplateData = emailTemplateData.replaceAll("\\{Assigndatetime\\}", date);
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_TITLE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseTitle\\}",
                            data.get(RabbitMqConstants.CASE_TITLE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_PRIORITY) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{casePriority\\}",
                            data.get(RabbitMqConstants.CASE_PRIORITY).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{team\\}",
                            data.get(RabbitMqConstants.TEAM).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }

                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }

                data.put("notificationMessage", emailContent);
//				if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION_TO_TEAM)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}else if(eventName.equalsIgnoreCase(NotificationConstants.TAT_NOTIFICATION)){
//					data.put("notificationMessage","Notification for pickup time breached");
//				}
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildFollowupRemarkMsg(Map<String, Object> data, String emailContent, String eventName,
                                                      String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
//                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
//                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildExternalRemarkMsg(Map<String, Object> data, String emailContent, String eventName,
                                                      String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = emailTemplate;
                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\n",
                            "<br/>");
                }

                emailContent = emailTemplateData;
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCustomerQuotaNotificationMsg(Map<String, Object> data, String emailContent, String eventName,
                                                                 String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();

            Double percentage = Double.parseDouble(data.get("percentage").toString());
            Integer roundUpPercentage = percentage.intValue();
            Optional<Event> optionalEvent = Optional.empty();
            optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PERCENTAGE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{percentage\\}",
                            data.get(RabbitMqConstants.PERCENTAGE).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildProblemDomainChangeMsg(Map<String, Object> data, String emailContent, String eventName,
                                                           String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.OLDVALUE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{oldValue\\}",
                            data.get(RabbitMqConstants.OLDVALUE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.NEWVALUE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{newValue\\}",
                            data.get(RabbitMqConstants.NEWVALUE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildTicketETRMsg(Map<String, Object> data, String emailContent, String eventName,
                                                 String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_SENDER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}",
                            data.get(RabbitMqConstants.TICKET_SENDER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.TICKET_STATUS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            data.put("notificationMessage", emailContent);
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildTicketETRDynamicMsg(Map<String, Object> data, String emailContent, String eventName,
                                                        String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            eventName = NotificationConstants.TICKET_ETR_DYNAMIC;
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

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
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\n",
                            "<br/>");
                }

                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            data.put("notificationMessage", emailContent);
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildCafFollowUpReminderCustomer(Map<String, Object> data, String emailContent,
                                                                String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCafFollowUpReminderStaff(Map<String, Object> data, String emailContent,
                                                             String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCafFollowUpOverDueStaff(Map<String, Object> data, String emailContent,
                                                            String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCafFollowUpOverDueParentStaff(Map<String, Object> data, String emailContent,
                                                                  String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }


                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketFollowUpReminderCustomer(Map<String, Object> data, String emailContent,
                                                                   String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketFollowUpReminderStaff(Map<String, Object> data, String emailContent,
                                                                String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketFollowUpOverDueStaff(Map<String, Object> data, String emailContent,
                                                               String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketFollowUpOverDueParentStaff(Map<String, Object> data, String emailContent,
                                                                     String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }


                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    if (data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CASE_NUMBER).toString());
                    }
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    if (data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                                data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                    }

                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketCreateNotification(Map<String, Object> data, String emailContent, String eventName,
                                                             String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffName\\}",
                            data.get(RabbitMqConstants.CUSTOMER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_PRIORITY) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{casePriority\\}",
                            data.get(RabbitMqConstants.TASK_PRIORITY).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseStatus\\}",
                            data.get(RabbitMqConstants.TASK_STATUS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseRemark\\}",
                            data.get(RabbitMqConstants.TASK_REMARK).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_LOGO) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{urlLogo\\}",
                            "https://savbillnettech.com/wp-content/uploads/2021/06/savbill-logo.png");
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_URL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taskURL\\}",
                            data.get(RabbitMqConstants.TASK_URL).toString());
                }
//				if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
//							data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//				}
//				if (emailTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{eventName\\}",
//							data.get(RabbitMqConstants.EVENT_NAME).toString());
//				}
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
//				if (emailTemplateData != null && data.get(RabbitMqConstants.ASSIGNED_DATE_TIME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{Assigndatetime\\}",
//							data.get(RabbitMqConstants.ASSIGNED_DATE_TIME).toString());
//				}
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildTicketRescheduleMessage(Map<String, Object> data, String emailContent,
                                                            String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketReminderNotificationMessage(Map<String, Object> data, String emailContent,
                                                                      String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildTicketOverDueReminderMessage(Map<String, Object> data, String emailContent,
                                                                 String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildDunningAdvanceNotificationMessage(Map<String, Object> data, String emailContent,
                                                                      String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);


                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRYDATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ExpiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRYDATE).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_USERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.TICKET_USERNAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PAYMENT_URL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentUrl\\}",
                            data.get(RabbitMqConstants.PAYMENT_URL).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.DUE_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ExpiryDate\\}",
                            data.get(RabbitMqConstants.DUE_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.START_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{startDate\\}",
                            data.get(RabbitMqConstants.START_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.END_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{endDate\\}",
                            data.get(RabbitMqConstants.END_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.DOCUMENT_ID) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{invoiceNumber\\}",
                            data.get(RabbitMqConstants.DOCUMENT_ID).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }if (emailTemplateData != null && data.get(RabbitMqConstants.AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planAmount\\}",
                            data.get(RabbitMqConstants.AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TAX_PERCENTAGE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taxPercentage\\}",
                            data.get(RabbitMqConstants.TAX_PERCENTAGE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TAX_AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taxAmount\\}",
                            data.get(RabbitMqConstants.TAX_AMOUNT).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planName\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TOTAL_DUE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{totalDue\\}",
                            data.get(RabbitMqConstants.TOTAL_DUE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CREDIT_BALANCE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{walletBalance\\}",
                            data.get(RabbitMqConstants.CREDIT_BALANCE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SUB_TOTAL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{subTotal\\}",
                            data.get(RabbitMqConstants.SUB_TOTAL).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildDunningPartnerDocumentMessage(Map<String, Object> data, String emailContent,
                                                                  String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFFNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffName\\}",
                            data.get(RabbitMqConstants.STAFFNAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARTNERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{partnerName\\}",
                            data.get(RabbitMqConstants.PARTNERNAME).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCustStatusInActiveNotificationMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer document verification*/
    public Map<String, Object> buildCustDocumentVerificationMessage(Map<String, Object> data, String emailContent,
                                                                    String eventName, String emailTemplate, String flag) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (flag.equalsIgnoreCase("quotationReport")) {
                    if (emailTemplateData != null && data.get("customerName") != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                                data.get("customerName").toString());
                    }

                    if (emailTemplateData != null && data.get("circuits") != null && !data.get("circuits").toString().equalsIgnoreCase("")) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{circuits\\}",
                                data.get("circuits").toString());
                    }
                } else {
                    if (emailTemplateData != null && data.get(RabbitMqConstants.CUST_NAME) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{username\\}",
                                data.get(RabbitMqConstants.CUST_NAME).toString());
                    }

                    if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                        emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                                data.get(RabbitMqConstants.STATUS).toString());
                    }
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
//    public void sendEmailNotificationWithAttachment(String queueName, String message, Map<String, Object> data, String sourceName, String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured, boolean isSmsConfigured) {
//        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
//
//        Map<String, Object> buildEmailBody = new HashMap<>();
//        String username = getUserName(data);
//        MDC.put(NotificationConstants.USER_NAME, username);
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put(NotificationConstants.TRACE_ID, traceContext.traceIdString());
//        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
//        try {
//
////			if (data.get("mvnoId") == null
////					|| (data.get("mvnoId").toString().isEmpty() || data.get("mvnoId").toString() == "")) {
////				throw new RuntimeException("Mvno id is mandatory.");
////			}
//            if (isEmailConfigured && data.get(RabbitMqConstants.CUST_MAIL_IDS_FOR_LEAD_QUOTATION_SEND) != null) {
//                Properties props = new Properties();
//                Object mvno = data.get("mvnoId");
//                Long mvnoId = ((Number) mvno).longValue();
//
//                Optional<EmailConfig> emailConfig = emailConfigRepository
//                        .findByMvnoId(mvnoId);
//                EmailConfig emailConfigDetails = emailConfig.get();
//                props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
//                if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
//                    props.put(NotificationConstants.STARTTLS_PARAM, true);
//                } else {
//                    props.put(NotificationConstants.SSL_PARAM, true);
//                }
//                props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
//                props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());
//
//                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(emailConfigDetails.getUserName(),
//                                emailConfigDetails.getPassword());
//                    }
//                });
//                List<String> custMailIds = Arrays.stream(String.valueOf(data.get("custMailIds")).split(",")).collect(Collectors.toList());
////                System.out.println(custMailIds);
//                for (String custEmailId : custMailIds) {
//                    Message msg = new MimeMessage(session);
//                    msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));
//                    //if (custEmailId != null && !custEmailId.equalsIgnoreCase(""))
//                    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(custEmailId));
//                    //if (data.get("staffEmail") != null && !String.valueOf(data.get("staffEmail")).equalsIgnoreCase(""))
//                    //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailConfigDetails.getUserName()));
//                    msg.setSubject(message);
//                    msg.setSentDate(new Date());
//                    //buildEmailBody = buildEmailBody(queueName, data, sourceName, emailTemplate);
//                    MimeBodyPart attachmentTextPart = new MimeBodyPart();
//                    attachmentTextPart.setText(emailTemplate);
//
//                    MimeBodyPart messageBodyPart = new MimeBodyPart();
//                    if (emailTemplate != null) {
//                        messageBodyPart.setContent(emailTemplate, "text/html");
//                    }
//
//                    Multipart multipart = new MimeMultipart();
//                    String fileName = data.get("fileName") != null ? String.valueOf(data.get("fileName")) : null;
//                    String filePath = data.get("filePath") != null ? String.valueOf(data.get("filePath")) : null;
////					if (filePath != null) {
////						DataSource source = new FileDataSource("E:\\Users\\savbill\\leaddoc\\1\\");
////						messageBodyPart.setDataHandler(new DataHandler(source));
////					}
////					if (fileName != null)
////						messageBodyPart.setFileName(fileName);
//                    File att = new File(new File(filePath), fileName);
//                    messageBodyPart.attachFile(att);
//                    multipart.addBodyPart(attachmentTextPart);
//                    multipart.addBodyPart(messageBodyPart);
//                    msg.setContent(multipart);
//                    if (emailTemplate != null && !emailTemplate.toString().isEmpty()
//                            && emailTemplate.toString() != "") {
//                        Transport.send(msg);
//
////						if (buildEmailBody.get(EVENT_ID) != null && !buildEmailBody.get(EVENT_ID).toString().isEmpty()
////								&& buildEmailBody.get(EVENT_ID).toString() != "") {
////							saveEmailNotificationOnSuccess(emailTemplate.toString(), data,
////									buildEmailBody.get(EVENT_ID).toString(), sourceName);
////
////							if (queueName.equalsIgnoreCase(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION)) {
////								//put check condition for etr here and then check add status success
////								HashMap<String, Object> etrAuditData = new HashMap<>();
////								etrAuditData.putAll((HashMap<String, Object>) data);
////								//data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
////								data.put("notificationStatus", "Success");
////								data.put("notificationMode", "email");
////								TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
////								//Gson gson = new Gson();
////								//gson.toJson(ticketETRAuditMessage);
////								messageSender.send(ticketETRAuditMessage, RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION);
////							}
////						}
//
//                        log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email successfully . Message : " + emailTemplate.toString() + "and Email Address is sent: " + custEmailId + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//                    }
//                }
//            } else {
//                if (!isEmailConfigured) {
//                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isEmailConfigured' column is false so email is not going to send " + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//                } else {
//                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email id is not valid so email is not going to send: " + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//                }
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            saveEmailNotificationOnFailure(queueName, data, sourceName, emailTemplate, null);
//
//            //put check condition for etr here and then check add status failed
//            if (queueName.equalsIgnoreCase(RabbitMqConstants.QUEUE_TICKET_ETR)) {
//                HashMap<String, Object> etrAuditData = new HashMap<>();
//                etrAuditData.putAll((HashMap<String, Object>) data);
//                //data.put("notificationMessage", buildEmailBody.get(EMAIL_CONTENT));
//                data.put("notificationStatus", "Failed");
//                data.put("notificationMode", "email");
//                TicketETRAuditMessage ticketETRAuditMessage = new TicketETRAuditMessage(data);
//                //Gson gson = new Gson();
//                //gson.toJson(ticketETRAuditMessage);
////                messageSender.send(ticketETRAuditMessage, RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT);
//                kafkaMessageSender.send(new KafkaMessageData(ticketETRAuditMessage,ticketETRAuditMessage.getClass().getSimpleName(), KafkaConstant.TICKET_ETR_AUDIT_SUCCESS));
//            }
//            log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Email failed. Message :Email Address :\"\n" +
//                    "                    + " + data.get(RabbitMqConstants.EMAIL_ID).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//            throw new RuntimeException(e.getMessage());
//        } finally {
//            // MDC.remove(NotificationConstants.TYPE);
//            try {
//                if (isSmsConfigured) {
//                    smsService.sendSmsNotification(queueName, message, data, sourceName, smsTemplate, appendUrl);
//                } else {
//                    log.info(LogConstants.REQUEST_FROM + NotificationConstants.UI + LogConstants.REQUEST_FOR + "Value of 'isSmsConfigured' column is false so sms is not going to send: " + data.get(RabbitMqConstants.EMAIL_ID).toString() + buildEmailBody.get(EMAIL_CONTENT) + data.get(RabbitMqConstants.ALT_EMAIL).toString() + LogConstants.REQUEST_BY + username + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
//                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            MDC.remove(NotificationConstants.USER_NAME);
//            MDC.remove(NotificationConstants.TYPE);
//            MDC.remove(NotificationConstants.TRACE_ID);
//            MDC.remove(NotificationConstants.SPAN_ID);
//        }
//    }

    /*method for customer service active message*/
    public Map<String, Object> buildCustServiceActiveMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer service inactive message*/
    public Map<String, Object> buildCustServiceInActiveMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer change password message*/
    public Map<String, Object> buildCustChangePasswordMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{status\\}",
                            data.get(RabbitMqConstants.STATUS).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARTNERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.PARTNERNAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFFNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffName\\}",
                            data.get(RabbitMqConstants.STAFFNAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.PASSWORD).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer address shifting message*/
    public Map<String, Object> buildCustAddressShiftingMessage(Map<String, Object> data, String emailContent,
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

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer payment verification message*/
    public Map<String, Object> buildCustPaymentVerificationMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{reciptNo\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_RECIPTNO).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentAmount\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_AMOUNT).toString());
                }


                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{paymentDate\\}",
                            data.get(RabbitMqConstants.CUSTMR_PAYMENT_DATE).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*method for customer ticket close message*/
    public Map<String, Object> buildCustTicketCloseMessage(Map<String, Object> data, String emailContent,
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

                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_TITLE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseTitle\\}",
                            data.get(RabbitMqConstants.CASE_TITLE).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildDocumentDunningDeactivation(Map<String, Object> data, String emailContent, String eventName,
                                                                String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
//            DecimalFormat df = new DecimalFormat();
//            df.setMaximumFractionDigits(2);
//            float amount = Float.parseFloat(data.get(RabbitMqConstants.AMOUNT).toString());
//            df.format(amount);
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.CUSTMR_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{customerName\\}",
                            data.get(RabbitMqConstants.CUST_NAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFFNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffName\\}",
                            data.get(RabbitMqConstants.CUSTMR_NAME).toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /*method for lead creation message*/
    public Map<String, Object> buildLeadCreationMessage(Map<String, Object> data, String emailContent,
                                                        String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.FIRSTNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{firstname\\}",
                            data.get(RabbitMqConstants.FIRSTNAME).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.LEAD_NO) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{leadNo\\}",
                            data.get(RabbitMqConstants.LEAD_NO).toString());
                }

                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
                //throw new CustomException("Template not found for this mvno and buid",417);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildCustomerInvoiceMsg(Map<String, Object> data, String emailContent, String eventName,
                                                       String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planname\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PERCENTAGE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{percentage\\}",
                            data.get(RabbitMqConstants.PERCENTAGE).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildStaffTicketAlertMsg(Map<String, Object> data, String emailContent, String eventName,
                                                        String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_DATA) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketData\\}", generateTicketDataContent(data.get(RabbitMqConstants.TICKET_DATA).toString()));

                }

                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildStaffUnpickTicketAlertMsg(Map<String, Object> data, String emailContent, String eventName,
                                                              String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_DATA) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketData\\}", generateTicketDataContent(data.get(RabbitMqConstants.TICKET_DATA).toString()));

                }

                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildImmediteInqueryEmailToUnRegisterCustomer(Map<String, Object> data, String emailContent, String eventName,
                                                                             String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SUBJECT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{subject\\}",
                            data.get(RabbitMqConstants.SUBJECT).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildImmediteInqueryEmailToRegisterCustomer(Map<String, Object> data, String emailContent, String eventName,
                                                                           String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SUBJECT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{subject\\}",
                            data.get(RabbitMqConstants.SUBJECT).toString());
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildImmediteInqueryEmailToUnRegisterCustomerToStaffMsg(Map<String, Object> data, String emailContent, String eventName,
                                                                                       String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }

                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public TemplateDto getEmailTemplateConfigurationByevent(Map<String, Object> data, String queuename) {
        TemplateDto templateDto = new TemplateDto();
        String eventName = SmsServiceImpl.getEventNameBasedOnQueueName(queuename);
        Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
        if (optionalEvent.isPresent()) {
            Template template = templateServiceImpl.getAllTemplateByMvnoAndBuAndEvent(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"));
            if (template != null) {
                templateDto.setTemplateName(template.getTemplateName());
                templateDto.setEmailEventConfigured(template.isEmailEventConfigured());
                templateDto.setSmsEventConfigured(template.isSmsEventConfigured());
                templateDto.setAppendUrl(template.getAppendUrl());
                return templateDto;
            } else {
                return null;
            }
        } else {
            return null;
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

    public EmailDataDTO emailDomainToDTO(Object[] emailDataObject) {
        EmailDataDTO emailDto = new EmailDataDTO();

        emailDto.setEmailId(((BigInteger) emailDataObject[0]).longValue());
        emailDto.setSourceName((String) emailDataObject[1]);
        emailDto.setEmailAddress((String) emailDataObject[2]);
        emailDto.setMessage((String) emailDataObject[3]);

        // Convert Timestamp to LocalDateTime
        Timestamp timestamp = (Timestamp) emailDataObject[4];
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        emailDto.setDate(localDateTime);

        emailDto.setStatus((String) emailDataObject[5]);
        emailDto.setEventId(((BigInteger) emailDataObject[6]).longValue());
        emailDto.setEventName((String) emailDataObject[7]);

        return emailDto;
    }

    public Map<String, Object> buildEmailBodyForMvnoDocumentDunningEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.DOCUMENT_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{documentName\\}",
                            data.get(RabbitMqConstants.DOCUMENT_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRY_DATE).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.DUE_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.DUE_DATE).toString());
                }


                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForMvnoPaymentDunningEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.DOCUMENT_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{documentName\\}",
                            data.get(RabbitMqConstants.DOCUMENT_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.DOCUMENT_ID) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{invoiceNumber\\}",
                            data.get(RabbitMqConstants.DOCUMENT_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EXPIRY_DATE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{expiryDate\\}",
                            data.get(RabbitMqConstants.EXPIRY_DATE).toString());
                }


                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForPlanExpiryEvent(Map<String, Object> data, String emailContent,
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
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, Object> buildEmailBodyForChangePlanEvent(Map<String, Object> data, String emailContent,
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
                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    private Map<String, Object> buildEmailBodyForTaskFollowupEvent(Map<String, Object> data, String emailContent, String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);

                if (emailTemplateData != null && data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentStaffPersonName\\}",
                            data.get(RabbitMqConstants.PARENT_STAFF_PERSON_NAME).toString());
                }
//                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
//                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//                }
//                if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//                    emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
//                            data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{remark\\}",
                            data.get(RabbitMqConstants.REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TICKET_NUMBERS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ticketNumber\\}",
                            data.get(RabbitMqConstants.TICKET_NUMBERS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{followupDateTime\\}",
                            data.get(RabbitMqConstants.FOLLOW_UP_DATE_TIME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TEAM_STAFF) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{teamStaff\\}",
                            data.get(RabbitMqConstants.TEAM_STAFF).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                String staffPersonName = data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null
                        ? data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString()
                        : null;
//                if (staffPersonName != null) {
//                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}", staffPersonName);
//                } else {
//                    emailTemplateData = emailTemplateData.replaceAll("Dear \\{userName\\},\\s*", "");
//                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                emailContent = emailTemplateData;

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private Map<String, Object> buildTaskUpdateNotification(Map<String, Object> data, String emailContent, String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_FOR) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taskFor\\}",
                            data.get(RabbitMqConstants.TASK_FOR).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.CUSTMR_USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.USER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USER_NAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_PRIORITY) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{casePriority\\}",
                            data.get(RabbitMqConstants.TASK_PRIORITY).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_STATUS) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseStatus\\}",
                            data.get(RabbitMqConstants.TASK_STATUS).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_REMARK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseRemark\\}",
                            data.get(RabbitMqConstants.TASK_REMARK).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_LOGO) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{urlLogo\\}",
                            "https://savbillnettech.com/wp-content/uploads/2021/06/savbill-logo.png");
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.TASK_URL) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{taskURL\\}",
                            data.get(RabbitMqConstants.TASK_URL).toString());
                }
//				if (emailTemplateData != null && data.get(RabbitMqConstants.STAFF_PERSON_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{staffPersonName\\}",
//							data.get(RabbitMqConstants.STAFF_PERSON_NAME).toString());
//				}
//				if (emailTemplateData != null && data.get(RabbitMqConstants.EVENT_NAME) != null) {
//					emailTemplateData = emailTemplateData.replaceAll("\\{eventName\\}",
//							data.get(RabbitMqConstants.EVENT_NAME).toString());
//				}
                if (emailTemplateData != null && data.get(RabbitMqConstants.CASE_NUMBER) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{caseNumber\\}",
                            data.get(RabbitMqConstants.CASE_NUMBER).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.START_DATE_TIME) != null) {
                    String input = data.get(RabbitMqConstants.START_DATE_TIME).toString();
                    String fixedInput = input.replace("T", ":");
                    emailTemplateData = emailTemplateData.replaceAll("\\{startDate\\}",
                            fixedInput);

                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.END_DATE_TIME) != null) {
                    String input = data.get(RabbitMqConstants.END_DATE_TIME).toString();
                    String fixedInput = input.replace("T", ":");
                    emailTemplateData = emailTemplateData.replaceAll("\\{endDate\\}",
                            fixedInput);
                }
                if (Objects.isNull(data.get(RabbitMqConstants.START_DATE_TIME))) {
                    emailTemplateData = emailTemplateData.replaceAll("The task duration is from \\{startDate\\} to \\{endDate\\}\\.", "");
                }
                emailContent = emailTemplateData;
                 emailContent = populateTemplate(emailContent, data);

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, Object> buildInsufficientWalletNotification(Map<String, Object> data, String emailContent, String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get(RabbitMqConstants.USERNAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{userName\\}",
                            data.get(RabbitMqConstants.USERNAME).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.EMAILID) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{emailId\\}",
                            data.get(RabbitMqConstants.EMAILID).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.WALLETAMOUNT) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{walletPrice\\}",
                            data.get(RabbitMqConstants.WALLETAMOUNT).toString());
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.AUTORENEWALPREFERANCE) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{Auto-RenewalPreference\\}",
                            data.get(RabbitMqConstants.AUTORENEWALPREFERANCE).toString());
                }


                emailContent = emailTemplateData;
                emailContent = populateTemplate(emailContent, data);

                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    //throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String populateTemplate(String emailContent, Map<String, Object> data) {
        String[] excludedKeys = {"mobileNumber", "caseId", "notificationFor", "mvnoId", "buId", "emailId","emailContent","userName","walletPrice","customerId","Auto-RenewalPreference"};

        // StringBuilder to collect key-value pairs
        StringBuilder appendedData = new StringBuilder();

        // Iterate through the map and filter excluded keys
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check if the key is not in the excluded list and the value is not null
            boolean isExcluded = false;
            for (String excludedKey : excludedKeys) {
                if (excludedKey.equals(key)) {
                    isExcluded = true;
                    break;
                }
            }

            if (!isExcluded && value != null) {
                appendedData.append(key).append(": ").append(value).append(", ");
            }
        }

        // Remove the trailing comma and space, if any
        if (appendedData.length() > 0) {
            appendedData.setLength(appendedData.length() - 2);
        }

        // Append the data before "Thank You."
        if (emailContent.contains("Thank You.")) {
            emailContent = emailContent.replace("Thank You.", appendedData + ". Thank You.");
        } else {
            emailContent += appendedData;
        }

        return emailContent;
    }

    public Map<String, Object> buildEmailBodyForChildCustRegistrationEvent(Map<String, Object> data, String emailContent,
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
                if (emailTemplateData != null && data.get(RabbitMqConstants.PASSWORD) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{password\\}",
                            data.get(RabbitMqConstants.CUST_PASSWORD).toString());
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.SLICE_CHUNK) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{name\\}",
                            data.get(RabbitMqConstants.TEAM_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{sender\\}", NotificationConstants.SENDER);
                }

                if (emailTemplateData != null && data.get(RabbitMqConstants.REGISTRATION_DATE) != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String date = data.get(RabbitMqConstants.REGISTRATION_DATE).toString();
                    date = date.replaceAll("\\.\\d+", "");
                    LocalDateTime regidate = LocalDateTime.parse(date, formatter);
                    emailTemplateData = emailTemplateData.replaceAll("\\{registrationDate\\}",
                            regidate.toLocalDate().toString());
                }
                if (emailTemplateData != null) {
                    String accountNumber = data.getOrDefault(RabbitMqConstants.ACCOUNT_NUMBER, "").toString();
                    emailTemplateData = emailTemplateData.replaceAll("\\{accountNumber\\}", accountNumber);
                }
                if (emailTemplateData != null && data.get(RabbitMqConstants.PLAN_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{planName\\}",
                            data.get(RabbitMqConstants.PLAN_NAME).toString());
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{cwscurl\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "cwscurl"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyname\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyname"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyaddress\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyaddress"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companycontact\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companycontact"));
                }
                if (emailTemplateData != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{companyemail\\}",
                            notificationConfigMappingService.getNotificationMappingList(data.get("buId"), "companyemail"));
                }
                if (data.get(RabbitMqConstants.PARENT_CUSTOMER_NAME) != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{parentCustomerName\\}",
                            data.get(RabbitMqConstants.PARENT_CUSTOMER_NAME).toString());
                }

                emailContent = emailTemplateData;
                if (emailContent != null) {
                    returnData.put(EVENT_ID, optionalEvent.get().getEventId());
                } else {
                    saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                    returnData.put(TEMPLATE_NOT_FOUND, true);
                    ////throw new CustomException("Template not found for this mvno and buid",417);
                }
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
