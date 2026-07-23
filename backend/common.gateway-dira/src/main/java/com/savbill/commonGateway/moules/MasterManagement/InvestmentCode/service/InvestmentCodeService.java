package com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.DTO.InvestmentCodeDto;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.QInvestmentCode;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.repository.InvestmentCodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.IcNameBuMapping;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.IcNameBuMappingRepo;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping.QIcNameBuMapping;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.domain.PlanService;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.domain.QPlanService;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.repository.PlanServiceRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentCodeService extends ExBaseAbstractService<InvestmentCodeDto, InvestmentCode,Long> {

    @Autowired
    InvestmentCodeRepository investmentCodeRepository;


    @Autowired
    IcNameBuMappingRepo repo;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    PlanServiceRepository planServiceRepository;


    public InvestmentCodeService(JpaRepository<InvestmentCode, Long> repository, IBaseMapper<InvestmentCodeDto, InvestmentCode> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InvestmentCodeService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String icName) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (icName != null) {
            icName = icName.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icName);
            else count = investmentCodeRepository.duplicateVerifyAtSave(icName, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtSaveForCode(String icCode) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (icCode != null) {
            icCode = icCode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icCode);
            else count = investmentCodeRepository.duplicateVerifyAtSaveForCode(icCode, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public InvestmentCode getById(Long id) {
        return investmentCodeRepository.findById(id).get();
    }

    public boolean duplicateVerifyAtEdit(String icname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (icname != null) {
            icname = icname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = investmentCodeRepository.duplicateVerifyAtSave(icname);
            else count = investmentCodeRepository.duplicateVerifyAtSave(icname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = investmentCodeRepository.duplicateVerifyAtEdit(icname, id);
                else countEdit = investmentCodeRepository.duplicateVerifyAtEdit(icname, id, mvnoIds);
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
                        return getInvestmentCodeByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getInvestmentCodeByName(String icname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QInvestmentCode qInvestmentCode=QInvestmentCode.investmentCode;
            Page<InvestmentCode> investmentCodesList = null;
            BooleanExpression booleanExpression = qInvestmentCode.isNotNull()
                    .and(qInvestmentCode.isDeleted.eq(false))
                    .and(qInvestmentCode.icname.likeIgnoreCase("%" + icname + "%").or(qInvestmentCode.iccode.containsIgnoreCase(icname).or(qInvestmentCode.status.equalsIgnoreCase(icname))));
            if(getMvnoIdFromCurrentStaff() == 1) {
                investmentCodesList = investmentCodeRepository.findAll(booleanExpression, pageRequest);
            }else {
                booleanExpression = booleanExpression.and(qInvestmentCode.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                investmentCodesList = investmentCodeRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != investmentCodesList && 0 < investmentCodesList.getSize()) {
                makeGenericResponse(genericDataDTO, investmentCodesList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = new ArrayList<Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public List<InvestmentCode> getIcname(List<Long> buIds) {
        List<InvestmentCode> investmentCodeList = new ArrayList<>();
        try {
                List<BusinessUnit> businnesUnit = businessUnitRepository.findAllByIdIn(buIds);
                List<IcNameBuMapping> mapping = repo.findAllByBusinessUnitidIn(businnesUnit);
                List<Long> IcCodes = mapping.stream().map(i -> i.getInvestmentCodeid().getId()).distinct().collect(Collectors.toList());
                List<InvestmentCode> investmentCodes = investmentCodeRepository.findAllByIdIn(IcCodes);
                if (getMvnoIdFromCurrentStaff() == 1) {
                    investmentCodeList = investmentCodes.stream()
                            .filter(i -> i.getStatus().equalsIgnoreCase("Active")
                                    && !i.getIsDeleted())
                            .collect(Collectors.toList());
                } else {
                    investmentCodeList = investmentCodes.stream()
                            .filter(i -> i.getStatus().equalsIgnoreCase("Active")
                                    && !i.getIsDeleted())
                            .filter(investmentCode -> investmentCode.getMvnoId() == 1 ||
                                    getMvnoIdFromCurrentStaff() == 1 ||
                                    investmentCode.getMvnoId() == getMvnoIdFromCurrentStaff().intValue())
                            .collect(Collectors.toList());
                }
            return investmentCodeList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<InvestmentCode> getAllIcname(){
        List<InvestmentCode> investmentCodeList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                investmentCodeList = investmentCodeRepository.findAll().stream()
                        .filter(investmentCode -> investmentCode.getStatus().equalsIgnoreCase("Active")
                                && !investmentCode.getIsDeleted())
                        .collect(Collectors.toList());
            } else {
                investmentCodeList = investmentCodeRepository.findAll().stream()
                        .filter(investmentCode -> investmentCode.getStatus().equalsIgnoreCase("Active")
                                && !investmentCode.getIsDeleted())
                        .filter(investmentCode -> investmentCode.getMvnoId() == 1 ||
                                getMvnoIdFromCurrentStaff() == 1 ||
                                investmentCode.getMvnoId() == getMvnoIdFromCurrentStaff().intValue())
                        .collect(Collectors.toList());
            }
            return investmentCodeList;
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = investmentCodeRepository.deleteVerifyForInvestmentCode(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public List<String> getIcnameListByBuId(Long id) {
        try{
            QIcNameBuMapping qIcNameBuMapping=QIcNameBuMapping.icNameBuMapping;
            BooleanExpression exp=qIcNameBuMapping.isDeleted.eq(false).and(qIcNameBuMapping.businessUnitid.id.eq(id));
            List<IcNameBuMapping> icNameBuMappingList= (List<IcNameBuMapping>) repo.findAll(exp);
            List<String> icnames=new ArrayList<>();
            if (icNameBuMappingList.size()>0){
                for (IcNameBuMapping icname:icNameBuMappingList){
                    String names = icname.getInvestmentCodeid().getIcname();
                    icnames.add(names);
                }
            }
            return icnames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<InvestmentCode> removebindedInvestmet(List<InvestmentCode> investmentCodeList) {

        QPlanService qPlanService = QPlanService.planService;
        List<Long> ids = investmentCodeList.stream()
                .map(InvestmentCode::getId)
                .collect(Collectors.toList());
        BooleanExpression booleanExpression = qPlanService.isNotNull().and(qPlanService.investmentid.in(ids));
        List<PlanService> planServiceList = (List<PlanService>) planServiceRepository.findAll(booleanExpression);
        List<Long> iccodeids = planServiceList.stream().map(planService -> planService.getInvestmentid()).collect(Collectors.toList());
        planServiceList.stream()
                .map(PlanService::getInvestmentid)
                .forEach(ids::remove);
        investmentCodeList.removeIf(ic -> iccodeids.contains(ic.getId()));
        return investmentCodeList;

    }

    public InvestmentCode convertDtoToDomain(InvestmentCodeDto investmentCodeDto){
        InvestmentCode investmentCode = new InvestmentCode();
        investmentCode.setId(investmentCodeDto.getId());
        investmentCode.setStatus(investmentCodeDto.getStatus());
        investmentCode.setMvnoId(investmentCodeDto.getMvnoId());
        investmentCode.setIcname(investmentCodeDto.getIcname());
        investmentCode.setIccode(investmentCodeDto.getIccode());
        investmentCode.setIsDeleted(investmentCodeDto.getIsDeleted());
        investmentCode.setCreatedate(investmentCodeDto.getCreatedate());
        investmentCode.setCreatedById(investmentCodeDto.getCreatedById());
        investmentCode.setCreatedByName(investmentCodeDto.getCreatedByName());
        investmentCode.setLastModifiedById(investmentCodeDto.getLastModifiedById());
        investmentCode.setLastModifiedByName(investmentCodeDto.getLastModifiedByName());
//        investmentCode.setUpdatedate(investmentCodeDto.getUpdatedate());


        return investmentCode;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<InvestmentCode> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = investmentCodeRepository.findAllByIsDeletedIsFalse(pageRequest);
        else
            paginationList = investmentCodeRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
}
