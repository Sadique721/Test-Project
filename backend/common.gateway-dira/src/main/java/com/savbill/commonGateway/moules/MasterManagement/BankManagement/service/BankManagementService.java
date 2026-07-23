package com.savbill.commonGateway.moules.MasterManagement.BankManagement.service;

import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain.BankManagement;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain.QBankManagement;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.mapper.BankManagementMapper;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.model.BankManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.repository.BankManagementRepository;
import com.savbill.commonGateway.moules.MasterManagement.CreditDocs.domain.QCreditDocument;
import com.savbill.commonGateway.moules.MasterManagement.CreditDocs.repository.CreditDocRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BankManagementService  extends ExBaseAbstractService<BankManagementDTO, BankManagement, Long> {

    public BankManagementService(BankManagementRepository repository, BankManagementMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;

    }


    @Autowired
    private BankManagementRepository bankManagementRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private BankManagementMapper bankManagementMapper;


    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<BankManagement> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = bankManagementRepository.findAll(pageRequest);
        else
            paginationList = bankManagementRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    //Save Bank
    @Override
    public boolean duplicateVerifyAtSave(String accountnum) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (accountnum != null) {
            accountnum = accountnum.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1)
                count = bankManagementRepository.duplicateVerifyAtSave(accountnum);
            else
                count = bankManagementRepository.duplicateVerifyAtSave(accountnum, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateBankNameVerifyAtSave(String bankName, String accountNo) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bankName != null) {
            bankName = bankName.trim();
            Integer count=0;
            if(getMvnoIdFromCurrentStaff() == 1) {
                if (Objects.nonNull(accountNo)) {
                    count = bankManagementRepository.countByBanknameAndAccountnumAndIsDeletedIsFalse(bankName, accountNo);
                } else {
                    count = bankManagementRepository.countByBanknameAndIsDeletedIsFalse(bankName);
                }
            }else{
                if (Objects.nonNull(accountNo)) {
                    count = bankManagementRepository.countByBanknameAccountnumAndIsDeletedIsFalseAndMvnoIdIn(bankName, accountNo,mvnoIds);
                } else {
                    count = bankManagementRepository.countByBanknameAndIsDeletedIsFalseAndMvnoIdIn(bankName, mvnoIds);
                }
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    public boolean deleteVerify(Long id)
    {
        boolean flag = false;
        QBankManagement qBankManagement = QBankManagement.bankManagement;
        QCreditDocument qCreditDocument =QCreditDocument.creditDocument;
//        BooleanExpression expression = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false));
        BooleanExpression expression = qCreditDocument.isNotNull().and(qCreditDocument.isDelete.eq(false));
        expression = expression.and((qCreditDocument.bankManagement.in(id)).or(qCreditDocument.destinationBank.in(id)));
        expression = expression.and(qCreditDocument.status.notEqualsIgnoreCase("rejected"));

        boolean count = creditDocRepository.exists(expression);
        if (count == false) {
            flag = true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String accountnum, Long id,String bankType) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (accountnum != null) {
            accountnum  = accountnum.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = bankManagementRepository.duplicateVerifyAtSave(accountnum);
            else count = bankManagementRepository.duplicateVerifyAtSave(accountnum, mvnoIds);
            if(count > 1 ){
                List<BankManagement> bankManagementList = bankManagementRepository.findByAccountnumAndMvnoIdIn(accountnum,mvnoIds);
                for(BankManagement bankManagement:bankManagementList){
                    if(bankManagement.getBanktype().equalsIgnoreCase(bankType)){
                        return false;
                    }
                }
            }

            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = bankManagementRepository.duplicateVerifyAtEdit(accountnum, id);
                else countEdit = bankManagementRepository.duplicateVerifyAtEdit(accountnum, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateBankNameVerifyAtEdit(String bankName, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bankName != null) {
            bankName  = bankName.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1)
                count = bankManagementRepository.countByBanknameAndIsDeletedIsFalse(bankName);
            else
                count = bankManagementRepository.countByBanknameAndIsDeletedIsFalseAndMvnoIdIn(bankName, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1)
                    countEdit = bankManagementRepository.countByBanknameAndIsDeletedIsFalseAndId(bankName, id);
                else
                    countEdit = bankManagementRepository.countByBanknameAndIsDeletedIsFalseAndMvnoIdInAndId(bankName, mvnoIds, id);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


//    public boolean deleteVerification(Long id)throws Exception {
//        boolean flag = false;
//        Integer count = bankManagementRepository.deleteVerify(id);
//        if(count==0){
//            flag=true;
//        }
//        return flag;
//    }
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression exp = qBankManagement.isNotNull();
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (!searchModel.getFilterValue().isEmpty()) {
                        String s = searchModel.getFilterValue();
                        exp = exp.and(qBankManagement.accountnum.containsIgnoreCase(s)
                                      .or(qBankManagement.bankname.containsIgnoreCase(s))
                        .or(qBankManagement.bankholdername.containsIgnoreCase(s))
                        .or(qBankManagement.ifsccode.containsIgnoreCase(s))
                                .or(qBankManagement.banktype.containsIgnoreCase(s))
                        .or(qBankManagement.status.equalsIgnoreCase(s)));

                        exp = exp.and(qBankManagement.isDeleted.eq(false));



                        if(getMvnoIdFromCurrentStaff() != 1)
                            exp = exp.and(qBankManagement.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
                        GenericDataDTO genericDataDTO = new GenericDataDTO();
                        Page<BankManagement> bankList = null;
                        bankList = bankManagementRepository.findAll(exp, pageRequest);
                        if (null != bankList && 0 < bankList.getSize()) {
                            makeGenericResponse(genericDataDTO, bankList);
                        }
                        return genericDataDTO;

                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public BankManagement validateBankByName(String name) {
        try {

            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression boolExp = qBankManagement.isNotNull();
            Long i = Long.parseLong(name);
            boolExp = boolExp.and(qBankManagement.id.eq(i));

            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(boolExp);
            if (!bankManagement.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with accoun num " + name + " Please enter valid account no");
            }
            return bankManagement.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<BankManagementDTO> findAllBankByStatus() {
        List<BankManagement> bankManagementList = new ArrayList<>();
        List<BankManagementDTO> bankManagementDTOS = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() == 1) {
            bankManagementList = bankManagementRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
        } else {
            bankManagementList = bankManagementRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
        if (!bankManagementList.isEmpty()) {
            bankManagementDTOS = bankManagementList.stream().map(bankManagement -> bankManagementMapper.domainToDTO(bankManagement, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }
        return bankManagementDTOS;
    }

    public List<BankManagementDTO> findAllBankByType(String banktype) {
        List<BankManagement> bankManagementList = new ArrayList<>();
        List<BankManagementDTO> bankManagementDTOS = new ArrayList<>();
        if(banktype!=null) {
            if (getMvnoIdFromCurrentStaff() == 1) {
                bankManagementList = bankManagementRepository.findAllByIsDeletedIsFalseAndStatusAndBanktype(CommonConstants.ACTIVE_STATUS, banktype);
            } else {
                bankManagementList = bankManagementRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndBanktype(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), banktype);
            }
            if (!bankManagementList.isEmpty()) {
                bankManagementDTOS = bankManagementList.stream().map(bankManagement -> bankManagementMapper.domainToDTO(bankManagement, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
        }
        return bankManagementDTOS;
    }
public BankManagement getId(long id){
        return bankManagementRepository.findById(id).get();
}
}


