package com.savbill.integrationsystem.navmaster.service;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.navmaster.entity.NAVMaster;
import com.savbill.integrationsystem.navmaster.entity.QNAVMaster;
import com.savbill.integrationsystem.navmaster.mapper.NAVMasterMapper;
import com.savbill.integrationsystem.navmaster.model.NAVMasterDTO;
import com.savbill.integrationsystem.navmaster.repository.NAVMasterRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class NAVMasterService extends ExBaseAbstractService<NAVMasterDTO, NAVMaster, Long> {

    public NAVMasterService(NAVMasterRepo navMasterRepo, NAVMasterMapper mapper) {
        super(navMasterRepo, mapper);
    }

    @Autowired
    private NAVMasterRepo navMasterRepo;

    @Override
    public GenericDataDTO getListByPagination(PageRequest pageRequest, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QNAVMaster qnavMaster = QNAVMaster.nAVMaster;
            BooleanExpression booleanExpression = qnavMaster.isNotNull().and(qnavMaster.isdelete.eq(false));
            String authTokenHeader = request.getHeader("Authorization");
            if (getMvnoId(authTokenHeader) != 1) {
                booleanExpression = booleanExpression.and(qnavMaster.mvnoId.in(getMvnoId(authTokenHeader), 1, 2));
            }
            Page<NAVMaster> paginationList = navMasterRepo.findAll(booleanExpression, pageRequest);
            if (0 < paginationList.getSize()) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(null);
            genericDataDTO.setDataList(new ArrayList<>());
            genericDataDTO.setTotalPages(0);
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "{NAVMasterService[]}";
    }

    @Override
    public NAVMasterDTO updateEntity(NAVMasterDTO entity) {
        return getMapper().domainToDTO(getRepository().save(getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, HttpServletRequest request) {
        try {
            QNAVMaster qnavMaster = QNAVMaster.nAVMaster;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "id", 0);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            String authTokenHeader = request.getHeader("Authorization");
            BooleanExpression booleanExpression = qnavMaster.isNotNull().and(qnavMaster.isdelete.eq(false));
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().contains("any")) {
                    if (!searchModel.getFilterValue().isEmpty()) {
                        booleanExpression = booleanExpression.and(qnavMaster.batchName.likeIgnoreCase(searchModel.getFilterValue()).or(qnavMaster.serviceName.likeIgnoreCase(searchModel.getFilterValue())).or(qnavMaster.aggregationFrequency.likeIgnoreCase(searchModel.getFilterValue())));
                    }
                }
            }
            if (getMvnoId(authTokenHeader) != 1 || getMvnoId(authTokenHeader) != 2) {
                booleanExpression = booleanExpression.and(qnavMaster.mvnoId.in(getMvnoId(authTokenHeader), 1, 2));
            }
            Page<NAVMaster> navMasterPage = navMasterRepo.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(new ArrayList<>(navMasterPage.getContent()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(navMasterPage.getTotalElements());
            genericDataDTO.setPageRecords(navMasterPage.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(navMasterPage.getNumber() + 1);
            genericDataDTO.setTotalPages(navMasterPage.getTotalPages());
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }
}
