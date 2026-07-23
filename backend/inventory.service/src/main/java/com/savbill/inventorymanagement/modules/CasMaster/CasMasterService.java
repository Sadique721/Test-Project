package com.savbill.inventorymanagement.modules.CasMaster;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveCasMasterSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateCasMasterSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CasMasterService extends ExBaseAbstractService<CasMasterDTO, CasMaster, Long> {
    public CasMasterService(CasMasterRepository repository, CasMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CasMasterService]";
    }
    private static final Logger logger = Logger.getLogger(CasMasterService.class);
    @Autowired
    CasMasterRepository casMasterRepository;

    @Autowired
    CasParameterMappingRepocitory casParameterMappingRepocitory;

    public void saveCasMasterEntity(SaveCasMasterSharedDataMessage message) throws Exception {
        try {
            CasMaster casMaster = new CasMaster();
            casMaster.setId(message.getId());
            casMaster.setCasname(message.getCasname());
            casMaster.setBuId(message.getBuId());
            casMaster.setEndpoint(message.getEndpoint());
            casMaster.setStatus(message.getStatus());
            casMaster.setDeleteFlag(message.getIsDeleted());
            casMaster.setMvnoId(message.getMvnoId());
            casMaster.setCreatedById(message.getCreatedById());
            casMaster.setLastModifiedById(message.getLastModifiedById());
            casMaster.setCasParameterMappings(message.getCasParameterMappings());
            casMasterRepository.save(casMaster);
            logger.info("Cas Master created successfully with name " + message.getCasname());
        } catch (CustomValidationException e) {
            logger.error("Unable to create cas master with name " + message.getCasname() + " , Error: " + e.getMessage());
        }
    }

    public void updateCasMasterEntity(UpdateCasMasterSharedDataMessage message) throws Exception {
        try {
            CasMaster casMaster = casMasterRepository.findById(message.getId()).orElse(null);
            if (casMaster != null) {
                casMaster.setId(message.getId());
                casMaster.setCasname(message.getCasname());
                casMaster.setBuId(message.getBuId());
                casMaster.setEndpoint(message.getEndpoint());
                casMaster.setStatus(message.getStatus());
                casMaster.setDeleteFlag(message.getIsDeleted());
                casMaster.setMvnoId(message.getMvnoId());
                casMaster.setCreatedById(message.getCreatedById());
                casMaster.setLastModifiedById(message.getLastModifiedById());
                casMaster.setCasParameterMappings(message.getCasParameterMappings());
                casMasterRepository.save(casMaster);
                logger.info("Cas Master updated successfully with name " + message.getCasname());
            } else {
                CasMaster casMaster1 = new CasMaster();
                casMaster1.setId(message.getId());
                casMaster1.setCasname(message.getCasname());
                casMaster1.setBuId(message.getBuId());
                casMaster1.setEndpoint(message.getEndpoint());
                casMaster1.setStatus(message.getStatus());
                casMaster1.setDeleteFlag(message.getIsDeleted());
                casMaster1.setMvnoId(message.getMvnoId());
                casMaster1.setCreatedById(message.getCreatedById());
                casMaster1.setLastModifiedById(message.getLastModifiedById());
                casMaster1.setCasParameterMappings(message.getCasParameterMappings());
                casMasterRepository.save(casMaster1);
                logger.info("Cas Master updated successfully with name " + message.getCasname());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update cas master with name " + message.getCasname() + " , Error: " + e.getMessage());
        }
    }
}
