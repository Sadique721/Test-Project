package com.savbill.taskmanagement.core.modules.MailConfigration.service;


import com.savbill.taskmanagement.core.constants.CommonConstants;
import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.EmailConfig.domain.EmailConfigBSS;
import com.savbill.taskmanagement.core.modules.EmailConfig.service.EmailConfigService;
import com.savbill.taskmanagement.core.modules.Mail.domain.Mail;
import com.savbill.taskmanagement.core.modules.Mail.repository.MailRepository;
import com.savbill.taskmanagement.core.modules.Mail.service.MailService;
import com.savbill.taskmanagement.core.modules.MailDocument.service.MailDocumentService;
import com.savbill.taskmanagement.core.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.taskmanagement.core.modules.PlanService.repository.PlanServiceRepository;
import com.savbill.taskmanagement.core.modules.PlanService.repository.ServiceRepository;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamUserMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamUserMappingsRepository;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.service.TicketFollowupDetailService;
import com.savbill.taskmanagement.core.modules.TicketRemark.service.TicketRemarkService;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategory;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.google.gson.Gson;
import com.savbill.taskmanagement.rabbitmq.messages.ImmediateAttentionForRegisterCustomerMessage;
import com.savbill.taskmanagement.rabbitmq.messages.ImmediateAttentionForUnRegisterCustomerMessage;
import com.savbill.taskmanagement.rabbitmq.messages.ImmediateAttentionForUnRegisterCustomerToStaffMessage;
import com.savbill.taskmanagement.rabbitmq.messages.TicketFollowupRemarkCustomerMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Message.RecipientType;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ReceiveMailServiceImpl implements ReceiveMailService {

    private static final Logger log = LoggerFactory.getLogger(ReceiveMailServiceImpl.class);

    private static final String DOWNLOAD_FOLDER = "data";

    private static final String COMPLETED_MAIL_FOLDER = "COMPLETED";

    private static final String EXCEPTION_MAIL_FOLDER = "EXCEPTION";

    private static final String JUNK_MAIL_FOLDER = "JUNK";

    @Autowired
    MailRepository mailRepository;

//    @Autowired
//    StaffUserRepository staffUserRepo;

//    @Autowired
//    ClientServiceRepository clientServiceRepository;


    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private StaffUserService staffUserService;


    @Autowired
    private CaseCategoryRepository caseCategoryRepository;


    @Autowired
    private CaseSubCategoryRepository caseSubCategoryRepository;

    @Autowired
    private CaseSubCategoryCategoryMappingRepository caseSubCategoryCategoryMappingRepository;

    @Autowired
    private CaseService caseService;



    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    CaseCategoryTatMappingRepo caseCategoryTatMappingRepo;

    @Autowired
    TicketFollowupDetailService ticketFollowupDetailService;

    @Autowired
    private EmailConfigService emailConfigService;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    ClientServiceSrv clientService;

//    @Autowired
//    StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;

//    @Autowired
//    BusinessUnitRepository businessUnitRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

//    @Autowired
//    private NotificationTemplateRepository templateRepository;

//    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private TicketRemarkService ticketRemarkService;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailDocumentService mailDocumentService;

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    TeamUserMappingsRepository teamUserMappingsRepository;


    private String receiverEmail;

    @Override
    public void handleReceivedMail(MimeMessage receivedMessage) {
        try {

            Folder folder = receivedMessage.getFolder();
            if(!folder.isOpen()){folder.open(Folder.READ_WRITE);}

            Message[] messages = folder.getMessages();
            fetchMessagesInFolder(folder, messages);

            Arrays.asList(messages).stream().filter(message -> {
                MimeMessage currentMessage = (MimeMessage) message;
                try {
                    return currentMessage.getMessageID().equalsIgnoreCase(receivedMessage.getMessageID());
                } catch (MessagingException e) {
                    log.error("Error occurred during process message", e);
                    return false;
                }
            }).forEach(this::extractMail);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void fetchMessagesInFolder(Folder folder, Message[] messages) throws MessagingException {
        FetchProfile contentsProfile = new FetchProfile();
        contentsProfile.add(FetchProfile.Item.ENVELOPE);
        contentsProfile.add(FetchProfile.Item.CONTENT_INFO);
        contentsProfile.add(FetchProfile.Item.FLAGS);
        contentsProfile.add(FetchProfile.Item.SIZE);
        folder.fetch(messages, contentsProfile);
    }

    private void copyMailToFolder(MimeMessage mimeMessage, String folderName) throws MessagingException {
        Store store = mimeMessage.getFolder().getStore();
        Folder mainFolder = mimeMessage.getFolder();
        if(!mainFolder.isOpen()){
            mainFolder.open(Folder.READ_WRITE);
        }
        Folder downloadedMailFolder = store.getFolder(folderName);
        if (downloadedMailFolder.exists()) {
            if(!downloadedMailFolder.isOpen()) {
                if(!mainFolder.isOpen()){
                    mainFolder.open(Folder.READ_WRITE);
                }
                downloadedMailFolder.open(Folder.READ_WRITE);
                downloadedMailFolder.appendMessages(new MimeMessage[]{mimeMessage});
            }
        } else {
            downloadedMailFolder.create(Folder.HOLDS_MESSAGES);
            downloadedMailFolder.setSubscribed(true);
            downloadedMailFolder.open(Folder.READ_WRITE);
            downloadedMailFolder.appendMessages(new MimeMessage[]{mimeMessage});
        }
    }

    private void extractMail(Message message) {
        try {
            final MimeMessage messageToExtract = (MimeMessage) message;
            final MimeMessageParser mimeMessageParser = new MimeMessageParser(messageToExtract).parse();
            if(!mimeMessageParser.getFrom().equalsIgnoreCase("postmaster@outlook.com")) {
                List<MultipartFile> file = new ArrayList<>();
                downloadAttachmentFiles(mimeMessageParser, file);
                showMailContent(mimeMessageParser, messageToExtract, file);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void showMailContent(MimeMessageParser mimeMessageParser, MimeMessage message, List<MultipartFile> files) throws Exception {
        System.out.println("From: {} to: {} | Subject: {}" + mimeMessageParser.getFrom() + mimeMessageParser.getTo() + " - " + mimeMessageParser.getSubject());
        System.out.println("Mail content: {}" + mimeMessageParser.getPlainContent());
        Folder folder = message.getFolder();
        if(!folder.isOpen()){
            folder.open(Folder.READ_WRITE);
        }
        String content = "";
        if(mimeMessageParser.getPlainContent() != ""){
            content = content + mimeMessageParser.getPlainContent().toString();
        }

        if(mimeMessageParser.getSubject() != null && mimeMessageParser.getSubject() != ""){
            content = content + mimeMessageParser.getSubject().toString();
        }
        boolean isTicketCreate = false;

        Mail mail = new Mail();
        String getTo = message.getHeader("Delivered-To")[0].toString();
        receiverEmail = getTo;
        receiverEmail = getTo;
        //  Address[] fromAddresses = mimeMessageParser.getMimeMessage().getRecipients(RecipientType.TO);/**Process mime string to email string**/
        mail.setReceiver(getTo);
        mail.setCc(mimeMessageParser.getCc().toString());

        if(isMailIsMailthread(mimeMessageParser)){
            log.info("enter in mail thread");
            mail.setSummary(getExtractSenderSubject(mimeMessageParser));
            mail.setSender(extractFromEmailsInMailThread(mimeMessageParser));
            mail.setDesc(mimeMessageParser.getPlainContent());
        }
        else{
            log.info("enter in normal mail");
            mail.setSender(mimeMessageParser.getFrom());
            mail.setSummary(mimeMessageParser.getSubject());
            String desc = emailProcesser(mimeMessageParser.getPlainContent().toString()
                    .replace(">", "")).trim();
            if(desc.length() > 0){
                mail.setDesc(desc);
            }
        }

        mail.setIsDelete(false);
        mail.setMessageId(message.getMessageID());
        mail.setBuId(mailService.getBuIdFromEmailConfig(getTo));
        mail.setMvnoId(mailService.getMvnoIdFromEmailConfig(getTo));
//        mail.setIssueid(1L);
//        mailRepository.save(mail);
        if(isNewEmail(message)) {   /**check if new mail or existing mail thread**/
            String ticketNumber = fetchTicketNoFromReply(message , mimeMessageParser.getSubject()); /**Fetch Ticket number from subject**/
//            if(ticketNumber.length() > 0) {
//                log.info("Ticket Number found in subject going to add remarks");
//                mail.setIsNew(false);
//                createTicketFollowUpDetails(mimeMessageParser, ticketNumber, files, false);
//            }
//            else{
                log.info("New Mail found enter for ticket creation");
                List<String> keywordList = new ArrayList<>();

                if(isMailIsMailthread(mimeMessageParser)) {
                    String emailBody = extractForwardedMail(mimeMessageParser.getPlainContent());
                    keywordList = mailScrapping(mail.getSummary()+emailBody);
                }
                else{
                    keywordList  = mailScrapping(content);
                }

                //List<String> plannames = new ArrayList<>();
                //List<String> serviceNames = new ArrayList<>();
                //List<String> finalPlanName;
                //List<String> finalserviceName;
                List<Customers> customersList = new ArrayList<>();
                List<StaffUser> staffUserList = new ArrayList<>();
                Long buId = null;
                Long mvnoId = null;
                buId =  mailService.getBuIdFromEmailConfig(getTo);
                mvnoId = mailService.getMvnoIdFromEmailConfig(getTo);
                log.info("BuId in mail service:"+buId);
                log.info("mvnoId in mail service:"+mvnoId);
//                if(buId != null){
//                    log.info("buid is"+buId);
//                    log.info("customer go to find by buId");
//                    customersList = customersService.findAllCustomerByEmailAndBuIdAndMvnoId(getFromEmail(mimeMessageParser), buId,mvnoId);
//                    staffUserList = staffUserService.findAllStaffUserByEmail(getFromEmail(mimeMessageParser)
//                    if (customersList.isEmpty()) {
//                        log.info("customer not found with email going into domain and buId and mvnoId");
//                        customersList = ticketRemarkService.getCustomerListFromEmailAndBuIdAndMvnoId(getFromEmail(mimeMessageParser), buId,mvnoId);
//                    }
//                }
//                else {
//                    log.info("buid is null  so common all customer with mvnoId called");
//                    customersList = customersService.findAllCustomerByEmailAndMvnoId(getFromEmail(mimeMessageParser),mvnoId);
//                    if (customersList.isEmpty()) {
//                        log.info("customer not found with email going into domain base with mvnoId");
//                        customersList = ticketRemarkService.getCustomerListFromEmail(getFromEmail(mimeMessageParser),mvnoId.intValue());
//                    }
//                }
                staffUserList = staffUserService.findAllStaffUserByEmail(getFromEmail(mimeMessageParser));
                staffUserList = staffUserList.stream().filter(staffUser -> staffUser.getIsDelete().equals(false)).collect(Collectors.toList());
                log.info("final customer list length"+staffUserList.size());
                //if (!staffUserList.isEmpty()) {
                    //StaffUser staffUser = staffUserList.get(0);

//                    List<Case> customerTicketList = caseRepository.findAllByCustomers_IdAndIsDeleteIsFalseOrderByCaseIdDesc(customer.getId());

                        /** Get Customer Plan Names **/
//                        if (!customer.getPlanMappingList().isEmpty()) {
//                            List<Integer> planIds = customer.getPlanMappingList().stream().map(custPlan -> custPlan.getPlanId()).collect(Collectors.toList());
//                            log.info("planIds" + planIds);
//                            plannames = postpaidPlanRepo.getAllPostpaidPlanNameByIdIn(planIds);
//                            log.info("plannames" + plannames);
//                        }
                        /** Get Customer Service Names **/
//                        if (!customer.getCustomerServiceMappingList().isEmpty()) {
//                            List<Long> seviceIds = customer.getCustomerServiceMappingList().stream().map(custService -> custService.getServiceId()).collect(Collectors.toList());
//                            log.info("serviceIds" + seviceIds);
//                            serviceNames = serviceRepository.getServiceNameByIdIn(seviceIds);
//                            log.info("serviceNames" + serviceNames);
//                        }
//                        finalPlanName = keywordList.stream()
//                                .filter(plannames::contains)
//                                .collect(Collectors.toList());
//                        log.info("finalPlanName" + finalPlanName);
//                        finalserviceName = keywordList.stream().filter(serviceNames::contains).collect(Collectors.toList());
//                        log.info("finalserviceName" + finalserviceName);

                        //if (staffUser!=null) {

                            if(isMailIsMailthread(mimeMessageParser)) {
                                isTicketCreate = createTicketByStaffUserEmail(mvnoId.intValue(),  getExtractSenderSubject(mimeMessageParser),  message, files, mail, getFromEmail(mimeMessageParser),mimeMessageParser); /**@ method for creating Ticket**/
                            }
                            else{
                                isTicketCreate = createTicketByStaffUserEmail(mvnoId.intValue(),  mimeMessageParser.getSubject(), message, files, mail, getFromEmail(mimeMessageParser),mimeMessageParser); /**@ method for creating Ticket**/
                            }

                            if (isTicketCreate) {
                                mail.setFolder(COMPLETED_MAIL_FOLDER);
                                mailRepository.save(mail);
                                try {
                                    copyMailToFolder(message, COMPLETED_MAIL_FOLDER);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                log.info("Ticket created sucessfully");
                            }
                        //}
//                        else {
//                            mail.setMailType("Registered");
//                            mail.setFolder(EXCEPTION_MAIL_FOLDER);
//                            mailRepository.save(mail);
//                            try {
//                                sendAlertEmail(mimeMessageParser.getFrom(), mimeMessageParser.getSubject(), true);
//                                copyMailToFolder(message, EXCEPTION_MAIL_FOLDER);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            log.info("not able to find staffuser for the task creation");
//                        }
                //}
//                else {
//                    mail.setIsNew(true);
//                    mail.setFolder(JUNK_MAIL_FOLDER);
//                    mail.setMailType("Unregistered");
//                    mailRepository.save(mail);
//                    try {
//                        sendAlertEmail(mimeMessageParser.getFrom(), mimeMessageParser.getSubject(), false);
//                        copyMailToFolder(message, JUNK_MAIL_FOLDER);
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    log.info("wNot able to find customer with email or multiple customer find with same email ", mimeMessageParser.getFrom());
//                }
            //}
        }
//        else{
//            String ticketNumber = fetchTicketNoFromReply(message , mimeMessageParser.getSubject()); /**Fetch Ticket number from subject**/
//            if(ticketNumber.length() > 0) {
//                createTicketFollowUpDetails(mimeMessageParser, ticketNumber, files, false);
//            }
//            else{
//                log.info("Ticket number not found in mail subject");
//            }
//            log.info("This is reply email");
//        }
        Mail mail1 =  mailRepository.save(mail);
        mailDocumentService.saveMultipartfileinMailDocument(files , mail1.getId().toString());

    }

    private void downloadAttachmentFiles(MimeMessageParser mimeMessageParser, List<MultipartFile> file) {
        log.debug("Email has {} attachment files", mimeMessageParser.getAttachmentList().size());
        mimeMessageParser.getAttachmentList().forEach(dataSource -> {
            if (StringUtils.isNotBlank(dataSource.getName())) {
                String rootDirectoryPath = new FileSystemResource("").getFile().getAbsolutePath();
                String dataFolderPath = rootDirectoryPath + File.separator + DOWNLOAD_FOLDER;
                createDirectoryIfNotExists(dataFolderPath);

                String downloadedAttachmentFilePath = rootDirectoryPath + File.separator + DOWNLOAD_FOLDER + File.separator + dataSource.getName();
                File downloadedAttachmentFile = new File(downloadedAttachmentFilePath);

                log.info("Save attachment file to: {}", downloadedAttachmentFilePath);

                try (
                        OutputStream out = new FileOutputStream(downloadedAttachmentFile)
                        // InputStream in = dataSource.getInputStream()
                ) {
                    InputStream in = dataSource.getInputStream();
                    IOUtils.copy(in, out);
                    FileInputStream input = new FileInputStream(downloadedAttachmentFile);
                    MultipartFile multipartFile = new MockMultipartFile("file",
                            downloadedAttachmentFile.getName(), "text/plain", IOUtils.toByteArray(input));
                    file.add(multipartFile);

                }
                catch (IOException e) {
                    log.error("Failed to save file.", e);
                }
            }
        });
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        if (!Files.exists(Paths.get(directoryPath))) {
            try {
                Files.createDirectories(Paths.get(directoryPath));
            } catch (IOException e) {
                log.error("An error occurred during create folder: {}", directoryPath, e);
            }
        }
    }

    public String stringProcesser(String getTo) {
        String emailRegex = "[\"<\\[]([^>\"]+)[\">\\]]";

        // Create a pattern object with the regex
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a matcher object
        Matcher matcher = pattern.matcher(getTo);

        String emailAddress = new String();

        // Find and print the email address
        if (matcher.find()) {
            String displayName = matcher.group(1);
            emailAddress = displayName;
        }
        return emailAddress;
    }

    @Override
    public List<String> mailScrapping(String email) throws IOException {
        List<String> finalKeyword = postpaidPlanRepo.getAllPostpaidPlanName();
        List<String> addServiceName =  planServiceRepository.getAllServiceName();
        finalKeyword.addAll(addServiceName);
        String[] keywordList = finalKeyword.toArray(new String[0]);
        List<String> extractedKeywords = new ArrayList<>();
        String[] emailArray = email.split(" ");

        for (String keyword : keywordList) {
            if (email.toLowerCase().contains(keyword.toLowerCase())) {
                extractedKeywords.add(keyword);
            }
        }
//        for (int i = 0; i < keywordList.length; i++) {
//            for(int j = 0; j < emailArray.length; j++){
//                if (keywordList[i].toString().contains(emailArray[j])) {
//                    extractedKeywords.add(keywordList[i]);
//                }
//            }
//        }
        return extractedKeywords;
    }

    public boolean createTicketByStaffUserEmail(Integer mvnoId, String caseTitle,  MimeMessage mimeMessage, List<MultipartFile> file, Mail mail,String from,MimeMessageParser mimeMessageParser) throws Exception {
        CaseDTO caseDTO = createTicketDTO(mvnoId, caseTitle, mimeMessage, mail, from);
        boolean isDuplicateCreate = true;
        if(caseDTO != null) {
            isDuplicateCreate = caseService.duplicateVerifyDomainAtSave(caseDTO); /**Ticket Creation check**/
        }
    if(mimeMessage!=null){
        caseDTO.setRemark(mimeMessageParser.getPlainContent().toString());
    }
//        if (!isDuplicateCreate && caseDTO != null) {
//            caseDTO = caseService.saveEntity(caseDTO, file);
//            mail.setIssueid(caseDTO.getCaseId());
        //createTicketFollowUpDetails(caseDTO.getCaseId() , mail.getDesc());
//            return true;
//        } else {
//            mail.setFolder(EXCEPTION_MAIL_FOLDER);
//            sendAlertEmail(from, caseTitle, true);
//            mailRepository.save(mail);
//            try {
//                copyMailToFolder(mimeMessage, EXCEPTION_MAIL_FOLDER);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            log.info("Maximum ticket create limit reached with same problem domain and sub problem domain");
//            return false;
//        }
        caseDTO.setIsFromCalender(false);
        caseDTO.setCreatedFrom("EMAIL");
        caseDTO.setTeamId(caseService.getDefaultTeam(mvnoId).getId().intValue());
        caseDTO.setLastModifiedById(1);
        caseDTO.setCreatedById(1);
        caseDTO = caseService.saveEntity(caseDTO, file);
        return  true;
    }



    public CaseDTO createTicketDTO(Integer mvnoId, String caseTitle, MimeMessage mimeMessage, Mail mail,String from) throws MessagingException {
        List<Long> serviceIds  = new ArrayList<>();
        CaseDTO caseDTO = new CaseDTO();
        caseDTO.setMvnoId(mvnoId);
        //caseDTO.setBuId(staffUser.getBuId());
//        if(!planName.isEmpty()) {
//            serviceIds  = postpaidPlanRepo.findServiceIdByPlanName(planName);
//        }

        if(serviceIds.isEmpty()){
            log.info("not able to find plan with email if service availble than proceed to create a ticket");
        }
        String getTo = receiverEmail;
        Long buID = null;
        buID = mailService.getBuIdFromEmailConfig(getTo);
        //Integer mvnoId = Math.toIntExact(mailService.getMvnoIdFromEmailConfig(getTo));
//        List<TicketReasonCategory> ticketReasonCategoryList = new ArrayList<>();
//        if(buID != null) {
//            if (serviceNameList.isEmpty()) {
//                ticketReasonCategoryList = ticketReasonCategoryRepo.findAllDefualtReasonCategoryUsingServiceIdIn(serviceIds.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()) , buID);
//            } else {
//                List<Integer> serviceIdsin = planServiceRepository.findServiceIdByPlanName(serviceNameList);
//                ticketReasonCategoryList = ticketReasonCategoryRepo.findAllDefualtReasonCategoryUsingServiceIdIn(serviceIdsin,buID);
//            }
//        }
//        else {
//            if (serviceNameList.isEmpty()) {
//                ticketReasonCategoryList = ticketReasonCategoryRepo.findAllDefualtReasonCategoryUsingServiceIdIn(serviceIds.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
//            } else {
//                List<Integer> serviceIdsin = planServiceRepository.findServiceIdByPlanName(serviceNameList);
//                ticketReasonCategoryList = ticketReasonCategoryRepo.findAllDefualtReasonCategoryUsingServiceIdIn(serviceIdsin);
//            }
//        }
//
//        if(ticketReasonCategoryList.isEmpty()){
//            mail.setMailType("Registered");
//            mail.setFolder(EXCEPTION_MAIL_FOLDER);
//            sendAlertEmail(from, caseTitle, true);
//            mailRepository.save(mail);
//            try {
//                copyMailToFolder(mimeMessage, EXCEPTION_MAIL_FOLDER);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            log.info("not able to find default problem domain");
//            return null;
//        }

//        List<CaseSubCategoryCategoryMapping> ticketSubCategoryReasonCategoryMappings = ticketSubCategoryReasonCategoryMappingRepository.findAllByTicketReasonCategoryIdInAndTicketReasonSubCategoryIdIsNotNull(ticketReasonCategoryList.stream().map(ticketReasonCategory -> ticketReasonCategory.getId()).collect(Collectors.toList()));

        CaseCategory caseCategory = caseCategoryRepository.findByIsDefaultCaseCategoryTrueAndMvnoId(mvnoId);
        CaseSubCategory caseSubCategory = caseSubCategoryRepository.findByIsDefaultCaseSubCategoryTrueAndMvnoId(mvnoId);

//        if(caseCategory!=null){
//            mail.setMailType("Registered");
//            mail.setFolder(EXCEPTION_MAIL_FOLDER);
//            sendAlertEmail(from, caseTitle, true);
//            mailRepository.save(mail);
//            try {
//                copyMailToFolder(mimeMessage, EXCEPTION_MAIL_FOLDER);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            log.info("not able to find default sub-problem domain");
//            return null;
//        }

        //ticketSubCategoryReasonCategoryMappings = ticketSubCategoryReasonCategoryMappings.stream().filter(ticketSubCategoryReasonCategoryMapping -> ticketSubCategoryReasonCategoryMapping.getTicketReasonSubCategoryId() != null).collect(Collectors.toList());
        caseDTO.setCaseTitle(caseTitle);
//        List<TicketServicemapping> ticketServicemappingList = new ArrayList<>();
//        TicketServicemapping ticketServicemapping = new TicketServicemapping();
//        ticketServicemapping.setServiceid(ticketReasonCategoryList.get(0).getService().getId().longValue());
//        ticketServicemappingList.add(ticketServicemapping);
        //caseDTO.setTicketServicemappingList(ticketServicemappingList);
        caseDTO.setUserName("NMS");
        //caseDTO.setServiceAreaName(sta.getServiceAreaName());
        //caseDTO.setServiceAreaId(customers.getServiceAreaId().longValue());
        caseDTO.setCaseType("Issue");
        caseDTO.setPriority("Low");
        caseDTO.setDepartment("Technical");
        caseDTO.setCaseStatus("Open");
        caseDTO.setCaseCategoryId(caseCategory.getCategoryId());
        caseDTO.setCaseSubCategoryId(caseSubCategory.getSubCategoryId());
//        caseDTO.setTicketReasonCategoryId(ticketReasonCategoryList.get(0).getId());
//        caseDTO.setReasonSubCategoryId(ticketReasonSubCategoryList.get(0).getId());


        //CaseCategory caseCategory = caseCategoryRepository.findById(caseCategoryList.get(0).getCategoryId()).orElse(null);
        //List<CaseCategoryTatMapping> ticketSubCategoryTatMapping = ticketSubCategoryTatMappingRepo.findByTicketReasonSubCategoryId(ticketReasonSubCategory.getId());
        Long time = 1L;
        String unit = "DAY";
        LocalDateTime now = LocalDateTime.now();
//        if (!ticketSubCategoryTatMapping.isEmpty() && ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getId() != null) {
//            time = ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getRtime();
//            unit = ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getRunit();
//        }
        if (unit.equalsIgnoreCase("DAY")) {
            now.plusDays(time);
        } else if (unit.equalsIgnoreCase("HOUR")) {
            now.plusHours(time);
        } else {
            now.plusMinutes(time);
        }
        caseDTO.setNextFollowupDate(now.toLocalDate());
        caseDTO.setNextFollowupTime(now.toLocalTime());
        //caseDTO.setStaffId(customers.getId());

        String mailBody = "";
        try{
            MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
            if(mimeMessageParser.getPlainContent() != ""){
                mailBody = mimeMessageParser.getPlainContent().toString();
            }
        }catch(Exception e){
            log.info("Not able to get mail body at the time of set data in ticket object");
        }
        caseDTO.setFirstRemark("Ticket Created by mail for: "+"NMS"+" for reason"+caseCategory.getCategoryName() + " and mail body is: "+ mailBody);

        caseDTO.setCreatedFrom("EMAIL");
        caseDTO.setCaseFor("Customer");
        caseDTO.setCaseForPartner("Customer");
        caseDTO.setCaseOrigin("Email");

        return caseDTO;
    }

    public static boolean isNewEmail(Message message) throws MessagingException {
        String[] references = message.getHeader("References");
        String[] inReplyTo = message.getHeader("In-Reply-To");
        // If References or In-Reply-To headers are present, it's likely a reply email

        boolean isReference = (references == null || references.length == 0) || (inReplyTo == null || inReplyTo.length == 0);
        boolean isNew = false;
        if(isReference){
            isNew = true;
        }else{
            if(StringUtils.containsIgnoreCase(message.getSubject(), "fw") || StringUtils.containsIgnoreCase(message.getSubject(), "fwd")){
                isNew = true;
            }
        }
        return isNew;
    }

    public void sendResponseMail(MimeMessage message , EmailConfigBSS emailConfigBSS ,String remark) {
        Transport transport = null;
        try {
            System.out.println("in response :::::");
            MimeMessage currentMessage = message;
            MimeMessage replyMessage = new MimeMessage(currentMessage.getSession());
            InternetAddress fromAddress = new InternetAddress(emailConfigBSS.getUserName());
//            replyMessage.setFrom(currentMessage.getRecipients(RecipientType.TO)[0]);
            replyMessage.setFrom(fromAddress);
            Address[] fromAddresses = currentMessage.getRecipients(RecipientType.TO);

            if (fromAddresses != null && fromAddresses.length > 0) {
                replyMessage.setRecipient(RecipientType.TO, fromAddresses[0]);
            }

            String originalSubject = currentMessage.getSubject();
            replyMessage.setSubject("Re: " + originalSubject);
            System.out.println("set header :::: ");
            replyMessage.setHeader("in-reply-to", currentMessage.getMessageID());
            replyMessage.setHeader("references", currentMessage.getMessageID());
            replyMessage.setText(remark);
            replyMessage.saveChanges();

            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", emailConfigBSS.getHostServer());
            props.put("mail.smtp.port", emailConfigBSS.getPort());
//            props.put("mail.debug", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfigBSS.getUserName(),
                            emailConfigBSS.getPassword());
                }
            });
            transport = session.getTransport("smtp");
            transport.connect(emailConfigBSS.getHostServer(), Integer.parseInt(emailConfigBSS.getPort()), emailConfigBSS.getUserName(), emailConfigBSS.getPassword());
            transport.sendMessage(replyMessage, replyMessage.getAllRecipients());
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchTicketNoFromReply(MimeMessage message , String subject) throws MessagingException {
        String ticketNo = "";
        String pattern = "TKT-[A-Za-z0-9]+";
        Pattern regexPattern = Pattern.compile(pattern);
        if(subject != null && subject != ""){
            Matcher matcher = regexPattern.matcher(subject);
            if (matcher.find()) {
                ticketNo = matcher.group();
                log.info("Ticket number found " + ticketNo);
            }
        }
        return ticketNo;
    }

    public String extractFirstLine(String body) {

        if(body.contains("\r\nwrote:\r\n\r\n")) {
            return getBodyFromMail(body, "\r\nwrote:\r\n\r\n");
        }else if (body.contains("wrote:\r\n\r\n")) {
            return getBodyFromMail(body, "wrote:\r\n\r\n");
        }else if (body.contains("Get Outlook")) {
            return getBodyFromMail(body, "Get Outlook");
        } else if (body.contains("Sent from ")) {
            return getBodyFromMail(body, "Sent from ");
        }else if (body.contains("Original Message")) {
            return getBodyFromMail(body, "Original Message");
        }else if (body.contains("___")) {
            return getBodyFromMail(body, "___");
        } else if (body.contains("From: ")) {
            return getBodyFromMail(body, "From: ");
        }else{
            return body;
        }
    }

    public String getBodyFromMail(String body, String pattern){
        String bodyContent = "";
        String bodyWoReply = body.split(pattern)[0];
        if(pattern.equalsIgnoreCase("Original Message") || pattern.equalsIgnoreCase("\r\nwrote:\r\n\r\n") || pattern.equalsIgnoreCase("wrote:\r\n\r\n")){
            bodyWoReply = bodyWoReply.substring(0,bodyWoReply.lastIndexOf("\n"));
        }
        String[] bodyLines = bodyWoReply.split("\\n");
        if(bodyLines.length <= 1) {
            bodyLines = bodyWoReply.split("\\r");
        }
        for (int i=0; i < (bodyLines.length); i++){
            bodyContent += bodyLines[i] + "\n";
        }
        return bodyContent;
    }

    @Override
    public void SendExternalRemarkMailInThread(Long mvnoId , Long buId , String caseNumber, String remark) {
        try {
            log.info("enter in external remark mail in thread");
            EmailConfigBSS emailConfigBSS = emailConfigService.getEmailConfigFromMvnoIdAndBuId(mvnoId,buId);
            System.out.println("in send :::::::");
            Properties properties = new Properties();
            properties.put("mail.imaps.host", "imaps");
            properties.put("mail.imaps.port", 993);
            properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.imap.socketFactory.fallback", false);
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.fetchsize", "1948576");

            Session session = Session.getDefaultInstance(properties);
            Store store = session.getStore("imaps");
            store.connect(emailConfigBSS.getHostServer(), emailConfigBSS.getUserName(), emailConfigBSS.getPassword());
            Folder inbox = store.getFolder("[Gmail]/Sent Mail");
            log.info("server :::::: "+ emailConfigBSS.getEmailConfigId());
            inbox.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new SubjectTerm("Ticket   :"+caseNumber);
            Message[] messages = inbox.search(searchTerm);
            MimeMessage existingMessage = new MimeMessage(session);
            if(messages.length > 0){
                existingMessage = (MimeMessage) messages[0];
                sendResponseMail(existingMessage , emailConfigBSS , remark);
            }
            // Close the folder and store
            inbox.close(false);
            store.close();
        } catch (Exception e) {
        }
    }

    public void saveCaseDocs(Case ticket, List<MultipartFile> files){
        try{
            if(files != null && files.size() > 0) {
                for (MultipartFile multipartFile : files) {
                    CaseDocDetails caseDoc = new CaseDocDetails();
                    caseDoc.setIsDelete(false);
                    caseDoc.setCreatedate(LocalDateTime.now());
                    caseDoc.setUpdatedate(LocalDateTime.now());
                    //caseDoc.setCreatedById(ticket.getStaffUser().getId());
                    //caseDoc.setLastModifiedById(ticket.getStaffUser().getId());
                    caseDoc.setTicketId(ticket.getCaseId());
                    //caseDoc.setCreatedByName(ticket.getStaffUser().getFullName());
                    //caseDoc.setLastModifiedByName(ticket.getStaffUser().getFullName());
                    log.info("Case Doc DTO created");
                    caseService.updateDocumentDetailsFromEmail(caseDoc, multipartFile);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Optional<Case> getCaseFromCaseNumberAndBUID(String caseNumber , Long buId, Long mvnoID){
        Optional<Case> caseOptional = null;
        List<Case> caseList = caseRepository.findAllByCaseNumber(caseNumber);
        if(!caseList.isEmpty() && caseList.size() >= 1){
            log.info("Multiple case found with same case number");
            if(buId != null) {
                caseOptional = Optional.of(caseRepository.findByCaseNumberAndBuIdAndMvnoId(caseNumber, buId, Math.toIntExact(mvnoID)));
                log.info("ticket found with buid");
            }
            else{
                caseOptional = Optional.of(caseRepository.findByCaseNumberAndBuIdAndMvnoId(caseNumber, null, Math.toIntExact(mvnoID)));
                log.info("ticket found without buid");
            }
        }
        return caseOptional;
    }

    public void createTicketFollowUpDetails(MimeMessageParser mimeMessageParser, String ticketNumber, List<MultipartFile> files, Boolean isFirstRemark) {
        try {
            String getTo = receiverEmail;
            Long buID = null;
            Long mvnoID = null;
            buID = mailService.getBuIdFromEmailConfig(getTo);
            mvnoID = mailService.getMvnoIdFromEmailConfig(getTo);
            log.info("buId"+buID);
            Optional<Case> ticket = null;
            if (buID != null) {
                ticket = getCaseFromCaseNumberAndBUID(ticketNumber, buID,mvnoID);
            } else {
                ticket = getCaseFromCaseNumberAndBUID(ticketNumber, null,mvnoID);
            }
            if (ticket != null) {
                if (ticket.isPresent()) {
                    log.info("Ticket is found in current system.");
                        TicketFollowupDetailDTO ticketFollowupDetailDTO = new TicketFollowupDetailDTO();
                        ticketFollowupDetailDTO.setIsFromCustomer(true);
                        ticketFollowupDetailDTO.setRemarkDate(LocalDateTime.now());
                        //ticketFollowupDetailDTO.setCustId(ticket.get().getStaffUser().getId());

                    String remarkBody = emailProcesser(extractFirstLine(mimeMessageParser.getPlainContent()));
                    String firstMailBody = emailProcesser(mimeMessageParser.getPlainContent());
                    if(isFirstRemark){
                        ticketFollowupDetailDTO.setRemark(firstMailBody);
                    }
                    else{
                        ticketFollowupDetailDTO.setRemark(remarkBody); /**extract mail body from reply email**/
                    }
                    if(ticket.get().getCurrentAssignee() != null){
                        ticketFollowupDetailDTO.setStaffId(ticket.get().getCurrentAssignee().getId());
                    }

                        ticketFollowupDetailDTO.setCaseId(ticket.get().getCaseId());
                        ticketFollowupDetailDTO.setRemarkType(CommonConstants.TICKET_REMARK_TYPE_EXTERNAL);
                        ticketFollowupDetailService.saveEntity(ticketFollowupDetailDTO);
                        log.info("Remark Created Successfully");
                        saveCaseDocs(ticket.get(), files);
                }
            } else {
                log.info("Ticket not found in current system.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createTicketFollowUpDetails(Long caseId , String remark) {
        try {
            Optional<Case> ticket = caseRepository.findById(caseId);
            if (ticket != null) {
                if (ticket.isPresent()) {
                    log.info("Ticket is found in current system.");
                    TicketFollowupDetailDTO ticketFollowupDetailDTO = new TicketFollowupDetailDTO();
                    ticketFollowupDetailDTO.setIsFromCustomer(true);
                    ticketFollowupDetailDTO.setRemarkDate(LocalDateTime.now());
                    //ticketFollowupDetailDTO.setCustId(ticket.get().getStaffUser().getId());
                    ticketFollowupDetailDTO.setRemark(remark);
                    if(ticket.get().getCurrentAssignee() != null){
                        ticketFollowupDetailDTO.setStaffId(ticket.get().getCurrentAssignee().getId());
                    }
                    ticketFollowupDetailDTO.setCaseId(ticket.get().getCaseId());
                    ticketFollowupDetailDTO.setRemarkType(CommonConstants.TICKET_REMARK_TYPE_EXTERNAL);
                    ticketFollowupDetailService.saveEntity(ticketFollowupDetailDTO);
                    log.info("Remark Created Successfully By Open Oppurnity");
                }
            } else {
                log.info("Ticket not found in current system.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** send notification ticket not created */

    public void sendAlertEmail(String emailFrom, String subject, Boolean isForRegisterCustomer){
        if(isForRegisterCustomer){
            sendMailToStaff(emailFrom, subject, true);
            sendTickekNotCreateMsgToCustomer(emailFrom, subject, mailService.getMvnoIdFromEmailConfig(receiverEmail), mailService.getBuIdFromEmailConfig(receiverEmail), true);
        }else{
            sendMailToStaff(emailFrom, subject, false);
            sendTickekNotCreateMsgToCustomer(emailFrom, subject, mailService.getMvnoIdFromEmailConfig(receiverEmail), mailService.getBuIdFromEmailConfig(receiverEmail), false);
        }
    }

    public void sendTickekNotCreateMsgToStaff(String senderEmail, String ticketNumber, String remark, String staffPersonName, String staffMobileNumber, String staffEmail, Integer mvnoId, String teamStaffName, Long buId, boolean isForRegisterCustomer) {
        try {
            if(isForRegisterCustomer){
                /* send mail to staff for register staff */
                TicketFollowupRemarkCustomerMessage ticketFollowupRemarkCustomerMessage = new TicketFollowupRemarkCustomerMessage(staffMobileNumber, staffEmail, RabbitMqConstants.TICKET_FOLLOWUP_REMARK_CUSTOMER_TEMPLATE_HEADER, null, senderEmail, staffPersonName, remark, mvnoId, ticketNumber, teamStaffName, buId);
                Gson gson = new Gson();
                gson.toJson(ticketFollowupRemarkCustomerMessage);
//                messageSender.send(ticketFollowupRemarkCustomerMessage, RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(ticketFollowupRemarkCustomerMessage, TicketFollowupRemarkCustomerMessage.class.getSimpleName()));

            } else {
                /* send mail to staff for unregister customer */
                ImmediateAttentionForUnRegisterCustomerToStaffMessage immediateAttentionForUnRegisterCustomerToStaffMessage = new ImmediateAttentionForUnRegisterCustomerToStaffMessage(staffMobileNumber,staffEmail,RabbitMqConstants.TICKET_FOLLOWUP_REMARK_CUSTOMER_TEMPLATE_HEADER,null,senderEmail,staffPersonName,remark,mvnoId,ticketNumber,teamStaffName,buId);
                Gson gson = new Gson();
                gson.toJson(immediateAttentionForUnRegisterCustomerToStaffMessage);
//                messageSender.send(immediateAttentionForUnRegisterCustomerToStaffMessage, RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF);
                kafkaMessageSender.send(new KafkaMessageData(immediateAttentionForUnRegisterCustomerToStaffMessage, ImmediateAttentionForUnRegisterCustomerToStaffMessage.class.getSimpleName()));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CaseDTO createTicketBasedOnExistingTicket(Customers customer, Case ticket, String caseTitle, MimeMessageParser mimeMessageParser){
        CaseDTO caseDTO = new CaseDTO();
        caseDTO.setCaseTitle(caseTitle);

//        List<TicketServicemapping> ticketServicemappingList =  new ArrayList<>();
//        TicketServicemapping ticketServicemapping = new TicketServicemapping();
//        ticketServicemapping.setServiceid(ticket.getTicketServicemappingList().get(0).getServiceid());
//        ticketServicemappingList.add(ticketServicemapping);
//        caseDTO.setTicketServicemappingList(ticketServicemappingList);

        caseDTO.setServiceAreaName(customer.getServiceAreaName());
        caseDTO.setServiceAreaId(customer.getServiceAreaId().longValue());
        caseDTO.setCaseType("Issue");
        caseDTO.setPriority("Low");
        caseDTO.setDepartment("Technical");
        caseDTO.setCaseStatus("Open");

        caseDTO.setCaseCategoryId(ticket.getCaseCategoryId());

        CaseCategory caseCategory = caseCategoryRepository.findById(ticket.getCaseCategoryId()).orElse(null);
        //List<CaseCategoryTatMapping> ticketSubCategoryTatMapping = ticketSubCategoryTatMappingRepo.findByTicketReasonSubCategoryId(ticketReasonSubCategory.getId());
        Long time = 1L;
        String unit = "DAY";
        LocalDateTime now = LocalDateTime.now();
//        if (!ticketSubCategoryTatMapping.isEmpty() && ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getId() != null) {
//            time = ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getRtime();
//            unit = ticketSubCategoryTatMapping.get(0).getTicketTatMatrix().getRunit();
//        }
        if (unit.equalsIgnoreCase("DAY")) {
            now.plusDays(time);
        } else if (unit.equalsIgnoreCase("HOUR")) {
            now.plusHours(time);
        } else {
            now.plusMinutes(time);
        }

        caseDTO.setNextFollowupDate(now.toLocalDate());
        caseDTO.setNextFollowupTime(now.toLocalTime());
        //caseDTO.setStaffId(ticket.getStaffUser().getId());
        String mailBody = "";
        try{
            if(mimeMessageParser.getPlainContent() != ""){
                mailBody = mimeMessageParser.getPlainContent().toString();
            }
        }catch(Exception e){
            log.info("Not able to get mail body at the time of set data in ticket object");
        }
        //caseDTO.setFirstRemark("Ticket Created by mail for: "+ticket.getStaffUser().getUsername()+" for reason"+caseCategory.getCategoryName() + " and mail body is: "+ mailBody);
        caseDTO.setCreatedFrom("EMAIL");
        caseDTO.setCaseFor("Customer");
        caseDTO.setCaseForPartner("Customer");
        caseDTO.setCaseOrigin("Email");
        return  caseDTO;
    }

    public String emailProcesser(String emailBody){
//        String body = extractFirstLine(emailBody);/**pattern commented becuase of signature removal**/
        String pattern = "\\[cid:[^\\]]+\\]";
        Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = regex.matcher(emailBody);
        emailBody = matcher.replaceAll("");
        return emailBody;
    }

    public String extractFromEmailsInMailThread(MimeMessageParser mimeMessageParser) throws Exception {
        log.info("enter in fetch sender email");
        String s = "";
        // Define the regex pattern to match "From" lines and extract email addresses
        String patternString = "^(From|On).*?(\\S+@\\S+)";
        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(mimeMessageParser.getPlainContent());
        List<String> fromAddresses = new ArrayList<>();
        // Find and add the extracted email addresses to the list
        while (matcher.find()) {
            String fromAddress = matcher.group(2);
            fromAddresses.add(fromAddress);
        }

        List<String> extractedEmails = new ArrayList<>();

        String patternString1 = "([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})";
        Pattern pattern1 = Pattern.compile(patternString1);

        for (String email : fromAddresses) {
            Matcher matcher1 = pattern1.matcher(email);
            if (matcher1.find()) {
                extractedEmails.add(matcher1.group());
            }
        }

        s = extractedEmails.get(extractedEmails.size() - 1);
        return s;
    }

    public String getExtractSenderSubject(MimeMessageParser mimeMessageParser) throws Exception {
        log.info("enter in sender subject");
        String subject = "";
        String regex = "^(Fw(d|r)?\\s*:\\s*)(.*)$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mimeMessageParser.getSubject());
        if(matcher.matches()){
            subject = matcher.group(3);
        }
        else{
            subject = mimeMessageParser.getSubject();
        }
        return subject;
    }
    public boolean isMailIsMailthread(MimeMessageParser mimeMessageParser) throws Exception {
        Boolean flag = false;
        String regex = "^(Fw(d|r)?\\s*:\\s*)(.*)$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        if(mimeMessageParser.getSubject() != null && mimeMessageParser.getSubject().length() > 0) {
            Matcher matcher = pattern.matcher(mimeMessageParser.getSubject());
            if (matcher.matches()) {
                flag = true;
            }
            log.info("isMailIsMailThread:   " + flag);
        }
        return  flag;
    }

    public String extractForwardedMail(String emailBody){
        String body = emailBody.replace(">","");
        int fromIndex = body.lastIndexOf("From: ");
        int wroteIndex = body.lastIndexOf("wrote:\n");
        if(fromIndex > wroteIndex){
            return getBodyFromForwardedMail(body.substring(fromIndex, body.length()), "Subject: ");
        }else{
            return getBodyFromForwardedMail(body.substring(wroteIndex, body.length()), "wrote:\n");
        }
    }

    public String getBodyFromForwardedMail(String body, String pattern){
        String bodyContent = "";
        String bodyWoReply = body.split(pattern)[1];
        if(pattern.equalsIgnoreCase("Subject: ")){
            bodyWoReply = bodyWoReply.substring(bodyWoReply.indexOf("\n"), bodyWoReply.length());
        }
        if(pattern.equalsIgnoreCase("wrote:\n")){
            bodyWoReply = bodyWoReply.substring(bodyWoReply.indexOf("\n"), bodyWoReply.length());
        }
        String[] bodyLines = bodyWoReply.split("\\n");
        if(bodyLines.length <= 1) {
            bodyLines = bodyWoReply.split("\\r");
        }
        for (int i=0; i < (bodyLines.length); i++){
            bodyContent += bodyLines[i] + "\n";
        }
        bodyContent = bodyContent.trim();
        return bodyContent;
    }

    public String getFromEmail(MimeMessageParser mimeMessageParser) throws Exception {
        String fromEmail = "";
        if(isMailIsMailthread(mimeMessageParser)){
            fromEmail = extractFromEmailsInMailThread(mimeMessageParser);
        }
        else{
            fromEmail = mimeMessageParser.getFrom();
        }
        return  fromEmail;
    }

    public void sendMailToStaff(String emailFrom, String subject, Boolean isForRegisterCustomer) {
        Long mvnoId = mailService.getMvnoIdFromEmailConfig(receiverEmail);
        ClientService teamClientService  = clientService.getByNameAndMvnoId(CommonConstants.EMAIL_TEAM,Integer.parseInt(String.valueOf(mvnoId)));
        if (teamClientService  != null) {
            String teamName = teamClientService.getValue();
            Teams teams = teamsRepository.findAllByMvnoIdAndName(mvnoId.intValue(), teamName);
            List<TeamUserMapping> teamUserMappingList = new ArrayList<>();
            List<Long> teamIdList = new ArrayList<>();
            if(teams != null){
                teamIdList.add(teams.getId());
            }
            if(teamIdList.size() > 0){
                teamUserMappingList = teamUserMappingsRepository.findAllByTeamIdIsIn(teamIdList);
            }

            List<Integer> staffIdList = new ArrayList<>();
            if(!teamUserMappingList.isEmpty()){
                staffIdList = teamUserMappingList.stream().map(teamUser -> Math.toIntExact(teamUser.getStaffId())).collect(Collectors.toList());
            }

        List<StaffUser> staffList = new ArrayList<>();
        if(!staffIdList.isEmpty()){
            staffList = staffUserRepository.findByIdIn(staffIdList);
            if(!staffList.isEmpty()){
                for(StaffUser staffUser: staffList){
                    System.out.println(staffUser.getBusinessUnitNameList());
                    Long emailBuId = null;
                    if(!staffUser.getBusinessUnitNameList().isEmpty()){
                        emailBuId = staffUser.getBusinessUnitNameList().get(0).getId();
                    }
                        sendTickekNotCreateMsgToStaff(emailFrom, "email", subject, staffUser.getUsername(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getUsername(), emailBuId, isForRegisterCustomer);
                }
            }
        }
    }

    }

    public void sendTickekNotCreateMsgToCustomer(String senderEmail, String subject, Long mvnoId, Long buId, boolean isForRegisterCustomer) {
        try {
            if (isForRegisterCustomer) {
                /* send mail to customer for register customer */
                List<Customers> customersList = new ArrayList<>();
                if(buId != null){
                    customersList = customersService.findAllCustomerByEmailAndBuIdAndMvnoId(senderEmail , buId , mvnoId);
                }
                else{
                    customersList = customersService.findAllCustomerByEmailAndMvnoId(senderEmail , mvnoId);
                }
                if(customersList.isEmpty()){
                    if(buId != null){
                        customersList = ticketRemarkService.getCustomerListFromEmailAndBuIdAndMvnoId(senderEmail , buId , mvnoId);
                    }
                    else{
                        customersList = ticketRemarkService.getCustomerListFromEmail(senderEmail , mvnoId.intValue());
                    }
                }

                Customers customers = customersList.get(0);
                ImmediateAttentionForRegisterCustomerMessage immediateAttentionForRegisterCustomerMessage = new ImmediateAttentionForRegisterCustomerMessage(customers.getUsername() , senderEmail , subject , mvnoId.intValue() , buId);
                Gson gson =  new Gson();
                gson.toJson(immediateAttentionForRegisterCustomerMessage);
//                messageSender.send(immediateAttentionForRegisterCustomerMessage , RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(immediateAttentionForRegisterCustomerMessage,ImmediateAttentionForRegisterCustomerMessage.class.getSimpleName()));
            } else {
                /* send mail to customer for unregister customer */

                ImmediateAttentionForUnRegisterCustomerMessage immediateAttentionForUnRegisterCustomerMessage = new ImmediateAttentionForUnRegisterCustomerMessage(senderEmail , subject , mvnoId.intValue() , buId);
                Gson gson =  new Gson();
                gson.toJson(immediateAttentionForUnRegisterCustomerMessage);
//                messageSender.send(immediateAttentionForUnRegisterCustomerMessage , RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(immediateAttentionForUnRegisterCustomerMessage,ImmediateAttentionForUnRegisterCustomerMessage.class.getSimpleName()));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
