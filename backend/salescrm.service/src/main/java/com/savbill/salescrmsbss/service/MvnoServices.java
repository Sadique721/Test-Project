package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Mvno;
import com.savbill.salescrmsbss.repository.MvnoRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MvnoServices {
    @Autowired
    private MvnoRepository mvnoRepository;
    Logger logger = Logger.getLogger(MvnoServices.class);
    public void UpdateMvnoidISP(Integer oldmvnoId, Integer newmvnoId) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldmvnoId.longValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newmvnoId.longValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.UpdateMvnoidISP(oldmvnoId, newmvnoId);
            } else {
                logger.info("Invalid mvno ");
            }
        } catch (Exception e) {
            logger.error("Unexpected error while updating MVNO ID "+" "+ oldmvnoId+ e);
        }
    }
}
