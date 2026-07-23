package com.savbill.revenuemanagement.core.Mvno.service;


import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.Mvno.domain.UpdateMvnoData;
import com.savbill.revenuemanagement.core.Mvno.mapper.MvnoMapper;
import com.savbill.revenuemanagement.core.Mvno.model.MvnoDTO;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.core.service.common.NumberSequenceUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveMvnoSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateMvnoSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MvnoService extends ExBaseAbstractService<MvnoDTO, Mvno,Long> {


    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private SubscriberService subscriberService;

    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoService]";
    }

    @Transactional
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setFullName(mvnoSharedDataMessage.getFullName());
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
            mvno.setMvnoPaymentDueDays(mvnoSharedDataMessage.getMvnoPaymentDueDays());
            mvno.setClientId(mvnoSharedDataMessage.getClientId());
            mvno.setAddress(mvnoSharedDataMessage.getAddress());
            mvno.setIspCommissionPercentage(mvnoSharedDataMessage.getIspCommissionPercentage());
            mvno.setAddress(mvnoSharedDataMessage.getAddress());
            createMvnoDefaultData(mvno);
            mvno = mvnoRepository.save(mvno);
            subscriberService.updateCustomerAddressData(mvno,mvnoSharedDataMessage);
            ApplicationLogger.logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
           ApplicationLogger.logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }
    @Transactional
    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            mvno.setFullName(updateMvnoSharedDataMessage.getFullName());
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
            mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
            mvno.setClientId(updateMvnoSharedDataMessage.getClientId());
            mvno.setAddress(updateMvnoSharedDataMessage.getAddress());
            mvno.setIspCommissionPercentage(updateMvnoSharedDataMessage.getIspCommissionPercentage());
            mvno.setAddress(updateMvnoSharedDataMessage.getAddress());
            mvno = mvnoRepository.save(mvno);
            subscriberService.updateCustomerAddressData(mvno, updateMvnoSharedDataMessage);
//            createMvnoDefaultData(mvno);
            ApplicationLogger.logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void createMvnoDefaultData(Mvno mvno) {
        numberSequenceUtil.createInvoiceFunctionForMVNO(mvno);
    }

    public void updateMvnoRefForInvoice(Long mvnoId, Integer custInvoiceRefId) {
        Optional<Mvno> mvno = mvnoRepository.findById(mvnoId);
        if(mvno.isPresent()) {
            mvno.get().setCustInvoiceRefId(custInvoiceRefId);
            mvnoRepository.save(mvno.get());
        }
    }


    public void updateMvnoIsp(UpdateMvnoData message) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(message.getOldmvnoId());
            Mvno newMvnoEntity = mvnoRepository.getOne(message.getNewmvnoId());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.updatesMvnoidIsp(message.getOldmvnoId(), message.getNewmvnoId());
            } else {
                ApplicationLogger.logger.error("Unable to update MVNO ID ", message.getOldmvnoId() ," to ", newMvnoEntity);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Unexpected error while updating MVNO ID ", message.getOldmvnoId(), e);
        }
    }

}
