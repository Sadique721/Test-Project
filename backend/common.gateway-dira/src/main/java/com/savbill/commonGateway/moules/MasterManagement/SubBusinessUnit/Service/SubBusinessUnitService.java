package com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;

import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain.QSubBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain.SubBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Model.SubBusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Repo.SubBusinessUnitRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SubBusinessUnitService extends ExBaseAbstractService<SubBusinessUnitDTO, SubBusinessUnit, Long> {

    @Autowired
    SubBusinessUnitRepository subBusinessUnitRepository;

    public SubBusinessUnitService(JpaRepository<SubBusinessUnit, Long> repository, IBaseMapper<SubBusinessUnitDTO, SubBusinessUnit> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubBusinessUnitService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String subbuname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (subbuname != null) {
            subbuname = subbuname.trim();
//            QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
//            BooleanExpression exp = qSubBusinessUnit.isNotNull();
//            if (getMvnoIdFromCurrentStaff() == 2) {
//                exp = exp.and(qSubBusinessUnit.subBuName.equalsIgnoreCase(subBuName));
//                if (exp.count().equals(null)) {
//                    flag = true;
//                }
//            }
//            pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId);
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1)
                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname);
            else count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public String getSubBUName(String subbuname) {
        QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
        BooleanExpression expression = qSubBusinessUnit.isNotNull();
        expression.and(qSubBusinessUnit.subbuname.equalsIgnoreCase(subbuname));

        return subBusinessUnitRepository.findAll(expression).toString();
    }

//    @Override
//    public boolean duplicateVerifyAtEdit(String subbuname) throws Exception {
//        duplicateVerifyAtEdit
//    }

    public boolean duplicateVerifyAtEdit(String subbuname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (subbuname != null) {
            subbuname = subbuname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname);
            } else {
                count = subBusinessUnitRepository.duplicateVerifyAtSaveWithName(subbuname, mvnoIds);
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = subBusinessUnitRepository.duplicateVerifyAtEdit(subbuname, id);
                else countEdit = subBusinessUnitRepository.duplicateVerifyAtEdit(subbuname, id, mvnoIds);
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
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getSubBusinessUnitByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getSubBusinessUnitByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getSubBusinessUnitByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<SubBusinessUnit> subBusinessUnitList = null;
//            QBranch qBranch = QBranch.branch;
            QSubBusinessUnit qSubBusinessUnit = QSubBusinessUnit.subBusinessUnit;
            BooleanExpression booleanExpression = qSubBusinessUnit.isNotNull()
                    .and(qSubBusinessUnit.isDeleted.eq(false))
                        .and(qSubBusinessUnit.subbuname.likeIgnoreCase("%" + name + "%").or(qSubBusinessUnit.subbucode.likeIgnoreCase("%" + name + "%").or(qSubBusinessUnit.status.equalsIgnoreCase(name))));
            if (getMvnoIdFromCurrentStaff() == 1) {
                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
                subBusinessUnitList = subBusinessUnitRepository.findAll(booleanExpression, pageRequest);
            } else {
                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                booleanExpression = booleanExpression.and(qSubBusinessUnit.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                subBusinessUnitList = subBusinessUnitRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != subBusinessUnitList && 0 < subBusinessUnitList.getSize()) {
                makeGenericResponse(genericDataDTO, subBusinessUnitList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        return null;
    }
    public boolean duplicateVerifyAtSaveSubBUcode(String bucode) {
        // TODO Auto-generated method stub
        boolean flagForsubbuCode = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bucode != null) {
            bucode = bucode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = subBusinessUnitRepository.duplicateVerifyAtSaveSubBUcode(bucode);
            else count = subBusinessUnitRepository.duplicateVerifyAtSaveSubBUcode(bucode, mvnoIds);
            if (count == 0) {
                flagForsubbuCode = true;
            }
        }
        return flagForsubbuCode;
    }
    public boolean duplicateVerifyAtEditSubBUcode(String bucode, Long id) throws Exception {
        boolean flagForsubbuCode = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bucode != null) {
            bucode = bucode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = subBusinessUnitRepository.duplicateVerifyAtSaveSubBUcode(bucode);
            else count = subBusinessUnitRepository.duplicateVerifyAtSaveSubBUcode(bucode, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = subBusinessUnitRepository.duplicateVerifyAtEditBuCode(bucode, id);
                else countEdit = subBusinessUnitRepository.duplicateVerifyAtEditBuCode(bucode, id, mvnoIds);
                if (countEdit == 1) {
                    flagForsubbuCode = true;
                }
            } else {
                flagForsubbuCode = true;
            }
        }
        return flagForsubbuCode;
    }
    public SubBusinessUnit getId(Long id){
        return subBusinessUnitRepository.findById(id).get();
    }
}
