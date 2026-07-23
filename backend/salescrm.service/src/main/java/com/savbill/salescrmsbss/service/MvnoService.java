package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Mvno;
import com.savbill.salescrmsbss.rabbitMq.message.SaveMvnoSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateMvnoSharedDataMessage;
import com.savbill.salescrmsbss.repository.MvnoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MvnoService {

    private static Log log = LogFactory.getLog(CustomersService.class);

    @Autowired
    private MvnoRepository mvnoRepository;

    public void UpdateMvnoidISP(Integer oldmvnoId, Integer newmvnoId) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldmvnoId.longValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newmvnoId.longValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.UpdateMvnoidISP(oldmvnoId, newmvnoId);
            } else {
                log.info("Invalid mvno ");
            }
        } catch (Exception e) {
            log.error("Unexpected error while updating MVNO ID "+" "+ oldmvnoId+ e);
        }
    }

    public void saveMvno(SaveMvnoSharedDataMessage dataMessage) {
        Mvno mvno=new Mvno(dataMessage);
        mvnoRepository.save(mvno);
    }

    public void updateMvno(UpdateMvnoSharedDataMessage dataMessage) {
        Mvno existingMvno=mvnoRepository.findById(dataMessage.getId()).orElse(null);
        if(Objects.nonNull(existingMvno)){
           existingMvno.setUsername(dataMessage.getUsername());
           existingMvno.setMvnoHeader(dataMessage.getMvnoHeader());
           existingMvno.setEmail(dataMessage.getEmail());
           existingMvno.setStatus(dataMessage.getStatus());
            mvnoRepository.save(existingMvno);
        }else{
            Mvno mvno=new Mvno(dataMessage);
            mvnoRepository.save(mvno);
        }
    }
}
