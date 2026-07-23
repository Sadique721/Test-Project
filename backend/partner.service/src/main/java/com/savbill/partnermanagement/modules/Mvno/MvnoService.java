package com.savbill.partnermanagement.modules.Mvno;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.rabbitmq.setting.SaveMvnoSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.setting.UpdateMvnoSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MvnoService extends ExBaseAbstractService<MvnoDTO, Mvno, Integer> {

    @Autowired
    MvnoRepository mvnoRepository;
    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoService]";
    }
    private static final Logger logger = LoggerFactory.getLogger(MvnoService.class);

    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        logger.info("Creating MVNO with name " + mvnoSharedDataMessage.getName());
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
            mvno.setCreatedById(mvnoSharedDataMessage.getCreatedById());
            mvno.setLastModifiedById(mvnoSharedDataMessage.getLastModifiedById());
            mvnoRepository.save(mvno);
            logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        logger.info("Updating MVNO with name " + updateMvnoSharedDataMessage.getName());
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
                mvno.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvnoRepository.save(mvno);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
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
                mvno1.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno1.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvnoRepository.save(mvno1);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void UpdateMvnoIdISP(Integer oldMvnoId, Integer newMvnoId) {
        try {
            logger.info("Updating MVNO ID "+ oldMvnoId +" to "+newMvnoId);
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldMvnoId.intValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newMvnoId.intValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.UpdateMvnoidISP(oldMvnoId,newMvnoId);
                logger.info("MVNO updated successfully " + oldMvnoId +" to "+newMvnoId);
            } else {
                logger.error("Unable to update MVNO ID "+ oldMvnoId);
            }
        } catch (Exception e) {
            logger.error("Unexpected error while updating MVNO ID "+ oldMvnoId+ e);
        }
    }
}
