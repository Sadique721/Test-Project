package com.savbill.taskmanagement.core.modules.Mail.service;


import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.EmailConfig.repository.EmailConfigRepository;
import com.savbill.taskmanagement.core.modules.Mail.domain.Mail;
import com.savbill.taskmanagement.core.modules.Mail.domain.QMail;
import com.savbill.taskmanagement.core.modules.Mail.domain.ReceiveEmailConfiguration;
import com.savbill.taskmanagement.core.modules.Mail.mapper.MailMapper;
import com.savbill.taskmanagement.core.modules.Mail.model.MailDTO;
import com.savbill.taskmanagement.core.modules.Mail.repository.MailRepository;
import com.savbill.taskmanagement.core.modules.Mail.repository.ReceiveEmailConfigurationRepository;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MailService extends ExBaseAbstractService<MailDTO, Mail,Long> {
    public MailService(JpaRepository<Mail, Long> repository, IBaseMapper<MailDTO, Mail> mapper) {
        super(repository, mapper);
    }

    @Autowired
    MailRepository mailRepository;

    @Autowired
    MailMapper issueTypeMapper;

    @Autowired
    EmailConfigRepository emailConfigRepository;

    @Autowired
    ReceiveEmailConfigurationRepository receiveEmailConfigurationRepository;

    private static final Logger log = LoggerFactory.getLogger(MailService.class);



    public List<Mail> getallproject ()throws Exception{
        List<Mail> projects = mailRepository.findAll();
        return projects;
    }

    public List<Mail> getallproject (Long id)throws Exception{
        List<Mail> projects = mailRepository.findAllById(id);
        return projects;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = mailRepository.deleteVerify(id);
        if (count == 1) {
            flag = true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = mailRepository.duplicateVerifyAtSave(name);
            else count = mailRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = mailRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = mailRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Mail> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = mailRepository.findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderByAndFolder(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Mail> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "id", sortOrder);
        Long mvnoId = getMvnoIdFromCurrentStaff().longValue();
        if(getBUIdsFromCurrentStaff() != null && !getBUIdsFromCurrentStaff().isEmpty()) {
            Long buId = getBUIdsFromCurrentStaff().get(0);
            paginationList = mailRepository.findAllByFolderNotAndMvnoIdAndBuIdAndIsDelete("COMPLETED",mvnoId,buId, false, pageRequest);
        }
        else{
            paginationList = mailRepository.findAllByFolderNotAndMvnoIdAndIsDelete("COMPLETED",mvnoId, false, pageRequest);
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
    public GenericDataDTO searchEmail(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Mail> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "id", sortOrder);
        paginationList = getAllMailUsingEmail(filterList.get(0).getFilterValue(),pageRequest , filterList.get(0).getFilterColumn());
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;

    }

    public Page<Mail> getAllMailUsingEmail(String email, PageRequest pageRequest, String mailType){
        QMail qMail = QMail.mail;
        BooleanExpression booleanExpression = qMail.isNotNull();
        if(getBUIdsFromCurrentStaff() != null && !getBUIdsFromCurrentStaff().isEmpty()) {
            Long buId = getBUIdsFromCurrentStaff().get(0);
            booleanExpression = booleanExpression.and(qMail.buId.eq(buId));
        }
        Long mvnoId  = getMvnoIdFromCurrentStaff().longValue();
        booleanExpression = booleanExpression.and(qMail.mvnoId.eq(mvnoId));
        booleanExpression = booleanExpression.and(qMail.sender.containsIgnoreCase(email));
        booleanExpression = booleanExpression.and(qMail.isDelete.eq(false));
        booleanExpression = booleanExpression.and(qMail.folder.notEqualsIgnoreCase("COMPLETED"));
        if(mailType.length() > 0 && !mailType.equalsIgnoreCase("All")){
            booleanExpression = booleanExpression.and(qMail.mailType.equalsIgnoreCase(mailType));
        }

        Page<Mail> getAllMail =  mailRepository.findAll(booleanExpression, pageRequest);
        return getAllMail;
    }

    public Mail updateMailStatus(MailDTO mailDTO){
        Mail mail = new Mail();
        mail = mailRepository.findById(mailDTO.getId()).get();
        mail.setFolder(mailDTO.getFolder());
        mail = mailRepository.save(mail);
        return  mail;
    }




    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {

    }

    public Long getBuIdFromEmailConfig(String email) {
        log.info("come in for getting buid");
        Long buId = null;
        if (email.length() > 0) {
            ReceiveEmailConfiguration emailConfigBSS = receiveEmailConfigurationRepository.findByUserName(email);
            if (emailConfigBSS != null) {
                buId = emailConfigBSS.getBuId();
                log.info("found emailconfig given email");
                log.info("Buid is :" + buId);
            } else {
                log.info("Email Configuration not found for: " + email);
            }
        }
        return buId;
    }

    public Long getMvnoIdFromEmailConfig(String email) {
        log.info("come in for getting mvnoid");
        Long mvnoId = 2L;
        if (email.length() > 0) {
            ReceiveEmailConfiguration emailConfigBSS = receiveEmailConfigurationRepository.findByUserName(email);
            if (emailConfigBSS != null) {
                mvnoId = emailConfigBSS.getMvnoId();
                log.info("found emailconfig given email");
                log.info("mvnoId is :" + mvnoId);
            } else {
                log.info("Email Configuration not found for: " + email);
            }
        }
        return mvnoId;
    }

    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public void deleteMail(MailDTO mailDTO){
        Optional<Mail> mail = mailRepository.findById(mailDTO.getId());
        if(mail.isPresent()){
            mail.get().setIsDelete(true);
            mailRepository.save(mail.get());
        }
    }

    public String getRemarkFromMessageId(String messageId){
        String s ="";
        Optional<Mail> mail = mailRepository.findById(Long.parseLong(messageId));
        if(mail.isPresent()) {
            if (mail.get().getDesc() != null) {
                    s = mail.get().getDesc().toString();

            }
        }
        return s;
    }
}
