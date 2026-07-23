package com.savbill.revenuemanagement.mastermanagement.BankManagement.service;

import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.BankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.mapper.BankManagementMapper;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.model.BankManagementDTO;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.repository.BankManagementRepository;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.SaveBankSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class BankManagementService  extends ExBaseAbstractService<BankManagementDTO, BankManagement, Long> {

    @Autowired
    BankManagementRepository bankManagementRepository;
    private static Log log = LogFactory.getLog(BankManagementService.class);
    public BankManagementService(BankManagementRepository repository, BankManagementMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;

    }


    @Transactional
    public void saveBankdata(SaveBankSharedDataMessage message){
        try {
            BankManagement bankManagement= new BankManagement();
            bankManagement.setId(message.getId());
            bankManagement.setBankname(message.getBankname());
            bankManagement.setAccountnum(message.getAccountnum());
            bankManagement.setIfsccode(message.getIfsccode());
            bankManagement.setBankholdername(message.getBankholdername());
            bankManagement.setStatus(message.getStatus());
            bankManagement.setIsDeleted(message.getIsDeleted());
            bankManagement.setBankcode(message.getBankcode());
            bankManagement.setMvnoId(message.getMvnoId());
            bankManagement.setBanktype(message.getBanktype());
            bankManagementRepository.save(bankManagement);
        }catch (Exception e){
            log.error("Unable to create Business Unit with name"+message.getBankname()+""+e.getMessage());
        }
    }

    @Transactional
    public void updateBankdata(SaveBankSharedDataMessage message) {
        try {
            if(message.getId()!=null) {
                BankManagement bankManagement =bankManagementRepository.findById(message.getId()).orElse(null);


                bankManagement.setBankname(message.getBankname());
                bankManagement.setAccountnum(message.getAccountnum());
                bankManagement.setIfsccode(message.getIfsccode());
                bankManagement.setBankholdername(message.getBankholdername());
                bankManagement.setStatus(message.getStatus());
                bankManagement.setIsDeleted(message.getIsDeleted());
                bankManagement.setBankcode(message.getBankcode());
                bankManagement.setMvnoId(message.getMvnoId());
                bankManagement.setBanktype(message.getBanktype());
                bankManagementRepository.save(bankManagement);

            }

        } catch (Exception e) {
            log.error("Unable to create Business Unit with name"+message.getBankname()+""+e.getMessage());
        }
    }

    public BankManagement validateBankByName(String name) {
        try {
            Long i = Long.parseLong(name);
            Optional<BankManagement> bankManagement = bankManagementRepository.findById(i);
            if (!bankManagement.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with accoun num " + name + " Please enter valid account no");
            }
            return bankManagement.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}


