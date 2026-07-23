package com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Domain.QSubBusinessVertical;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Domain.SubBusinessVertical;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Mapper.SubBusinessVerticalMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Repository.SubBusinessVerticalRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class SubBusinessVerticalService  extends ExBaseAbstractService<SubBusinessVerticalDTO, SubBusinessVertical,Long> {


    @Autowired
    SubBusinessVerticalMapper subBusinessVerticalMapper;

    @Autowired
    private SubBusinessVerticalRepository subBusinessVerticalRepository;

    public SubBusinessVerticalService(SubBusinessVerticalRepository repository, SubBusinessVerticalMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public boolean duplicateVerifyAtSave(String sbvname) throws Exception {
        boolean flag = false;
        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (sbvname != null) {
            sbvname = sbvname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = subBusinessVerticalRepository.duplicateVerifyAtSave(sbvname);
            else count = subBusinessVerticalRepository.duplicateVerifyAtSave(sbvname, mvnoId);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String sbvname, Long id) throws Exception {
        boolean flag = false;
        List mvnoId = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (sbvname != null) {
            sbvname = sbvname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = subBusinessVerticalRepository.duplicateVerifyAtSaveWithName(sbvname);
            } else {
                count = subBusinessVerticalRepository.duplicateVerifyAtSaveWithName(sbvname, mvnoId);
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = subBusinessVerticalRepository.duplicateVerifyAtEdit(sbvname, id);
                else countEdit = subBusinessVerticalRepository.duplicateVerifyAtEdit(sbvname, id, mvnoId);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


//    public SubBusinessVerticalDTO getEntityForUpdateAndDelete(Long id) throws Exception {
//
////        SubBusinessVerticalDTO subBusinessVerticalDTO = subBusinessVerticalMapper.domainToDTO(subBusinessVerticalRepository.findById(id).get(), new CycleAvoidingMappingContext());
//        SubBusinessVertical subBusinessVertical = subBusinessVerticalRepository.findById(id).orElse(null);
//        if(getMvnoIdFromCurrentStaff() != null) {
//            subBusinessVertical.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        if(subBusinessVertical == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == subBusinessVertical.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return subBusinessVertical;
//    }

    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getSubBusinessVerticalByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getSubBusinessVerticalByName(String sbvname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getSubBusinessVerticalByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QSubBusinessVertical qSubBusinessVertical = QSubBusinessVertical.subBusinessVertical;
            BooleanExpression exp = qSubBusinessVertical.isNotNull();
            exp = exp.and(qSubBusinessVertical.sbvname.containsIgnoreCase(sbvname)).or(qSubBusinessVertical.status.containsIgnoreCase(sbvname)).and(qSubBusinessVertical.isDeleted.eq(false));
            Page<SubBusinessVertical> subBusinessVertical = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                subBusinessVertical = subBusinessVerticalRepository.findAll(exp, pageRequest);
            } else {
                exp = exp.and(qSubBusinessVertical.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                subBusinessVertical = subBusinessVerticalRepository.findAll(exp, pageRequest);
            }
            if (null != subBusinessVertical && 0 < subBusinessVertical.getSize()) {
                makeGenericResponse(genericDataDTO, subBusinessVertical);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public SubBusinessVertical getId(Long id){
        return subBusinessVerticalRepository.findById(id).get();
    }

//    public SubBusinessVerticalDTO convertDomainToDTO(SubBusinessVertical subBusinessVertical){
//        SubBusinessVerticalDTO dto = new SubBusinessVerticalDTO();
//        if(subBusinessVertical.getBusinessVerticals()!=null)
//            dto.setBuVerticalsId(subBusinessVertical.getBusinessVerticals().getId());
//        dto.setId(subBusinessVertical.getId());
//        dto.setMvnoId(subBusinessVertical.getMvnoId());
//        dto.setStatus(subBusinessVertical.getStatus());
//        dto.setIsDeleted(subBusinessVertical.getIsDeleted());
//        dto.setSbvname(subBusinessVertical.getSbvname());
//        return dto;
//    }
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<SubBusinessVertical> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = subBusinessVerticalRepository.findAll(pageRequest);
        else
            paginationList = subBusinessVerticalRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
        if (null != paginationList && !paginationList.getContent().isEmpty()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
}
