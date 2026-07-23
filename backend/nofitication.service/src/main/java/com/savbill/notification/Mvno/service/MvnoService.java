package com.savbill.notification.Mvno.service;


import com.savbill.notification.Mvno.domain.Mvno;
import com.savbill.notification.Mvno.repository.MvnoRepository;
import com.savbill.notification.entity.Template;
import com.savbill.notification.rabbitmq.message.SaveMvnoSharedDataMessage;
import com.savbill.notification.rabbitmq.message.UpdateMvnoSharedDataMessage;
import com.savbill.notification.repository.TemplateRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MvnoService {
    //private static final log log = logFactory.getlog(MvnoService.class);

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    TemplateRepository templateRepository;

    // Shared MVNO Data from Common APIGW to CMS
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setId(mvnoSharedDataMessage.getId());
            mvno.setName(mvnoSharedDataMessage.getName());
            mvno.setUsername(mvnoSharedDataMessage.getUsername());
            mvno.setPassword(mvnoSharedDataMessage.getPassword());
            mvno.setSuffix(mvnoSharedDataMessage.getSuffix());
            mvno.setDescription(mvnoSharedDataMessage.getDescription());
            mvno.setEmail(mvnoSharedDataMessage.getEmail());
            mvno.setPhone(mvnoSharedDataMessage.getPhone());
            mvno.setStatus(mvnoSharedDataMessage.getStatus());
            mvno.setLogfile(mvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(mvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(mvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(mvnoSharedDataMessage.getIsDelete());
            mvnoRepository.save(mvno);
            autoCreateTemplateForNewMvno(mvno);
            log.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (Exception e) {
            log.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            if (mvno != null) {
                mvno.setId(updateMvnoSharedDataMessage.getId());
                mvno.setName(updateMvnoSharedDataMessage.getName());
                mvno.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno.setPhone(updateMvnoSharedDataMessage.getPhone());
                mvno.setStatus(updateMvnoSharedDataMessage.getStatus());
                mvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());

                mvnoRepository.save(mvno);



                log.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            } else {
                Mvno mvno1 = new Mvno();
                mvno1.setId(updateMvnoSharedDataMessage.getId());
                mvno1.setName(updateMvnoSharedDataMessage.getName());
                mvno1.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno1.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno1.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno1.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno1.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno1.setPhone(updateMvnoSharedDataMessage.getPhone());
                mvno1.setStatus(updateMvnoSharedDataMessage.getStatus());
                mvno1.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno1.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno1.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno1.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());

                mvnoRepository.save(mvno1);
                log.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            }
        } catch (Exception e) {
            log.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }


    @Transactional
    public void autoCreateTemplateForNewMvno(Mvno mvno){
        List<Template> defaultTemplateList = new ArrayList<>();

        List<Template> autCreatedTemplateList = new ArrayList<>();

        defaultTemplateList = templateRepository.findAllByMvnoIdAndBuId(2, (Long) null);

        for(Template template : defaultTemplateList){
           Template autoTemplate = new Template();

           autoTemplate.setEvent(template.getEvent());
           autoTemplate.setTemplateName(template.getTemplateName());
           autoTemplate.setSmsTemplateData(template.getSmsTemplateData());
           autoTemplate.setSmsEventConfigured(template.isSmsEventConfigured());
           autoTemplate.setEmailTemplateData(template.getEmailTemplateData());
           autoTemplate.setEmailEventConfigured(template.isEmailEventConfigured());
           autoTemplate.setStatus(template.getStatus());
           autoTemplate.setMvnoId(mvno.getId().intValue());
           autoTemplate.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
           autoTemplate.setLastModificationDate(Timestamp.valueOf(LocalDateTime.now()));
           autoTemplate.setAppendUrl(template.getAppendUrl());
           autoTemplate.setBuId(null);

           templateRepository.save(autoTemplate);
        }


    }
    public void updateMvnoIdIsptoIsp(Integer oldMvno, Integer newMvno) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldMvno.longValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newMvno.longValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.UpdateMvnoidISP(oldMvno, newMvno);
                log.info("MVNO updated successfully " + oldMvno +" to "+newMvno);
            } else {
                log.error("Unable to update MVNO ID "+ oldMvno);
            }
        } catch (Exception e) {
            log.error("Unexpected error while updating MVNO ID "+ oldMvno+ e);
        }
    }
}
