package com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;

import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.QBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.mapper.BusinessUnitMapper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.model.BusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.IcNameBuMapping;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.IcNameBuMappingRepo;

import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.QIcNameBuMapping;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessUnitService extends ExBaseAbstractService<BusinessUnitDTO, BusinessUnit, Long> {
    public BusinessUnitService(BusinessUnitRepository repository, BusinessUnitMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    BusinessUnitMapper mapper;

    @Override
    public String getModuleNameForLog() {
        return "[BusinessUnitService]";
    }

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private IcNameBuMappingRepo icNameBuMappingRepo;

//    @Autowired
//    private BusinessUnitMapper mapper;

    //Get All Business Unit with Pagination
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<BusinessUnit> paginationList = null;
        //PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1) paginationList = businessUnitRepository.findAll(pageRequest);
        else
            paginationList = businessUnitRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    //Save Business Unit
    @Override
    public boolean duplicateVerifyAtSave(String buname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (buname != null) {
            buname = buname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSave(buname);
            else count = businessUnitRepository.duplicateVerifyAtSave(buname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    //Update Business Unit
    public boolean duplicateVerifyAtEdit(String buname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (buname != null) {
            buname = buname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSave(buname);
            else count = businessUnitRepository.duplicateVerifyAtSave(buname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = businessUnitRepository.duplicateVerifyAtEdit(buname, id);
                else countEdit = businessUnitRepository.duplicateVerifyAtEdit(buname, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    //Delete Business Unit
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = businessUnitRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public boolean deleteVerificationForSubBusinessunit(Integer id) throws Exception {
        boolean flag = false;
        Integer count = businessUnitRepository.deleteVerifyForSubBusinessunit(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    //Search Business Unit
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getBusinessUnitByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getBusinessUnitByName(String buname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QBusinessUnit qBusinessUnit = QBusinessUnit.businessUnit;
            Page<BusinessUnit> businessUnitList = null;

            BooleanExpression booleanExpression = qBusinessUnit.isNotNull()

                    .and(qBusinessUnit.buname.likeIgnoreCase("%" + buname + "%")).or(qBusinessUnit.bucode.likeIgnoreCase("%" + buname + "%")).and(qBusinessUnit.isDeleted.eq(false)).or(qBusinessUnit.status.equalsIgnoreCase(buname));
            if (getMvnoIdFromCurrentStaff() == 1) {
                //businessUnitList = businessUnitRepository.findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalse(buname, pageRequest);
                businessUnitList = businessUnitRepository.findAll(booleanExpression, pageRequest);
            } else {
                //businessUnitList = businessUnitRepository.findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(buname, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                booleanExpression = booleanExpression.and(qBusinessUnit.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                businessUnitList = businessUnitRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != businessUnitList && 0 < businessUnitList.getSize()) {
                makeGenericResponse(genericDataDTO, businessUnitList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public BusinessUnit getById(Long id) {
        return businessUnitRepository.findById(id).get();
    }


    public boolean duplicateVerifyAtSaveUcode(String bucode) {
        // TODO Auto-generated method stub
        boolean flagforUcode = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bucode != null) {
            bucode = bucode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode);
            else count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode, mvnoIds);
            if (count == 0) {
                flagforUcode = true;
            }
        }
        return flagforUcode;
    }

    public boolean duplicateVerifyUcodeAtEdit(String bucode, Long id) throws Exception {
        boolean flagforUcode = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (bucode != null) {
            bucode = bucode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode);
            else count = businessUnitRepository.duplicateVerifyAtSaveUcode(bucode, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = businessUnitRepository.duplicateVerifyUcodeAtEdit(bucode, id);
                else countEdit = businessUnitRepository.duplicateVerifyUcodeAtEdit(bucode, id, mvnoIds);
                if (countEdit == 1) {
                    flagforUcode = true;
                }
            } else {
                flagforUcode = true;
            }
        }
        return flagforUcode;
    }

    public GenericDataDTO getBUFromStaff() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<BusinessUnit> businessUnitList = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() == 1) {
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                businessUnitList = businessUnitRepository.findAllByIsDeletedIsFalseAndStatusAndIdIn(CommonConstants.ACTIVE_STATUS, getBUIdsFromCurrentStaff());
            } else {
                businessUnitList = businessUnitRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            }
        } else {
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                businessUnitList = businessUnitRepository.findAllByIsDeletedIsFalseAndStatusAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getBUIdsFromCurrentStaff(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            } else {
                businessUnitList = businessUnitRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
        }
        genericDataDTO.setDataList(businessUnitList);
        return genericDataDTO;
    }


    public void deleteIcNameBumapping(Long Id) {
        QIcNameBuMapping qIcNameBuMapping = QIcNameBuMapping.icNameBuMapping;
        BooleanExpression exp = qIcNameBuMapping.isDeleted.eq(false).and(qIcNameBuMapping.businessUnitid.id.eq(Id));
        List<IcNameBuMapping> icNameBuMappings = IterableUtils.toList(icNameBuMappingRepo.findAll(exp));
        for (int i = 0; i < icNameBuMappings.size(); i++) {
            icNameBuMappings.get(i).setIsDeleted(true);
            icNameBuMappingRepo.saveAll(icNameBuMappings);
        }
    }

    public BusinessUnitDTO convertBumodeltoPojo(Optional<BusinessUnit> businessUnit) {
        BusinessUnitDTO businessUnitDTO = new BusinessUnitDTO();
        try {
            if (businessUnitDTO.getId() != null) {
                businessUnit.get().setId(businessUnitDTO.getId());
            }

            businessUnitDTO.setBucode(businessUnit.get().getBucode());
            businessUnitDTO.setBuname(businessUnit.get().getBuname());
            businessUnitDTO.setStatus(businessUnit.get().getStatus());
            return businessUnitDTO;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public GenericDataDTO getBUFromCurrentStaff() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        QBusinessUnit businessUnit = QBusinessUnit.businessUnit;

        if (getBUIdsFromCurrentStaff().size() > 0) {
            BooleanExpression booleanExpression = businessUnit.isDeleted.eq(false).and(businessUnit.status.equalsIgnoreCase("Active"));
            booleanExpression = businessUnit.id.in(getBUIdsFromCurrentStaff());
            genericDataDTO.setDataList(IterableUtils.toList(businessUnitRepository.findAll(booleanExpression)));
        }
        return genericDataDTO;
    }

//    public void createPartnerbusinessUnit(Partner partner) {
//        BusinessUnitDTO businessUnit = new BusinessUnitDTO();
//        businessUnit.setBuname(partner.getName());
//        businessUnit.setBucode(partner.getPrcode());
//        businessUnit.setId(Long.valueOf(partner.getId()));
//        businessUnit.setStatus(CommonConstants.ACTIVE_STATUS);
//        businessUnit.setMvnoId(partner.getMvnoId());
//        businessUnit.setIsDeleted(partner.getIsDelete());
//    //    BusinessUnitDTO businessUnitDTO =  mapper.domainToDTO(businessUnit);
//    }


}
