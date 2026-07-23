package com.savbill.taskmanagement.core.modules.TicketTatMatrix.Service;

import com.savbill.taskmanagement.core.constants.DeleteContant;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.QTicketTatMatrix;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Mapper.TicketTatMatrixMapper;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
@Service
public class TicketTatMatrixService extends ExBaseAbstractService<TicketTatMatrixDTO, TicketTatMatrix, Long> {
    public TicketTatMatrixService(TicketTatMatrixRepository repository, TicketTatMatrixMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    TicketTatMatrixRepository repository;

    @Autowired
    TicketTatMatrixMapper mapper;

    @Override
    public String getModuleNameForLog() {
        return "[TicketTatMatrixService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String matrixname) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[duplicateVerifyAtSave()]";
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (matrixname != null) {
            log.debug("Starting duplicate verification for save ; tatmatrix name: {}; module: {};", matrixname, SUBMODULE);
            matrixname = matrixname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(matrixname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = repository.duplicateVerifyAtSave(matrixname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(matrixname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
            log.debug("Exiting duplicate Verify At Save for save; tatmatrix name: {}; Flag : {}; module: {};", matrixname,flag,SUBMODULE);
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String tatmatrixname, Integer tatmatrixid) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[duplicateVerifyAtEdit()]";
        boolean flag = false;
        if (tatmatrixname != null) {
            log.debug("Starting duplicate verification for edit; tatmatrix name: {};, matrix ID: {}, module: {}", tatmatrixname, tatmatrixid, SUBMODULE);
            tatmatrixname = tatmatrixname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(tatmatrixname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = repository.duplicateVerifyAtSave(tatmatrixname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(tatmatrixname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = repository.duplicateVerifyAtEdit(tatmatrixname, tatmatrixid, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
            log.debug("completed duplicate Verify At Edit for edit; tatmatrix name: {}, matrix ID: {}; Flag: {}; module: {}", tatmatrixname, tatmatrixid,flag, SUBMODULE);
        }
        return flag;
    }


    public boolean deleteVerification(Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

        boolean flag = false;
        try {
            if(id != null) {
                log.debug("checking delete verification for TAT matrix with id : {}; Module : {};", id,SUBMODULE);
            }
            Integer count = repository.deleteVerify(id);
            if (count == 0) { // Count == 1 due to tatmatrix is not bind with any services
                flag = true;
            } else {
                throw new RuntimeException(DeleteContant.MATRIX_EXIST);
            }
            log.debug("completed delete verification for TAT matrix with id : {}; flag : {}; Module : {}", id, flag, SUBMODULE);
        } catch (Exception ex) {
            log.error("error while delete verification for TAT matrix with id : {}; Message : {}; Module : {}", (id != null)?id:"Not found",ex.getMessage(),SUBMODULE);
            throw ex;
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        log.debug("Starting search for TAT matrix; sortOrder: {}; module: {}",sortOrder, SUBMODULE);
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketTatMatrix qTicketTatMatrix = QTicketTatMatrix.ticketTatMatrix;
        BooleanExpression booleanExpression = qTicketTatMatrix.isNotNull().and(qTicketTatMatrix.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                booleanExpression = booleanExpression.and(qTicketTatMatrix.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

            }
        }

        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketTatMatrix.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketTatMatrix.mvnoId.eq(1).or(qTicketTatMatrix.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketTatMatrix.buId.in(getBUIdsFromCurrentStaff()))));
        }
        log.debug("completed search for TAT matrix; Module : {};",SUBMODULE);
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    public List<TicketTatMatrixDTO> getAllTicketTatMatrix() {
        String SUBMODULE = getModuleNameForLog() + " [getAllTicketTatMatrix()] ";
        log.debug("getting all TAT matrix; module: {}", SUBMODULE);
        List<TicketTatMatrix> ticketTatMatrixList = repository.getAllByStatus().stream().filter(x -> (x.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (x.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(x.getBuId()))).collect(Collectors.toList());
        List<TicketTatMatrixDTO> ticketTatMatrixDTOList = ticketTatMatrixList.stream().map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return ticketTatMatrixDTOList;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<TicketTatMatrix> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1) paginationList =  repository.findAllByAndIsDeletedFalse(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                paginationList = repository.findAllByAndIsDeletedFalseAndMvnoIdIn(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
            else
                paginationList = repository.findAllByAndIsDeletedFalseAndMvnoIdInAndBuIdIn(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()), getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        log.debug("fetched List By Page And Size And sortby: {}; module: {}",sortOrder, SUBMODULE);
        return genericDataDTO;
    }

    public List<TicketTatMatrix> findAllByStatus() {
        String SUBMODULE = getModuleNameForLog() + "[findAllByStatus]";
        log.debug("Fetching all active TicketTatMatrix entities, module: {}", SUBMODULE);
        QTicketTatMatrix ticketTatMatrix = QTicketTatMatrix.ticketTatMatrix;
        BooleanExpression booleanExpression = ticketTatMatrix.status.equalsIgnoreCase("Active").and(ticketTatMatrix.isDeleted.eq(false));
        if (getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(ticketTatMatrix.buId.in(getBUIdsFromCurrentStaff()));
        }
        if (getMvnoIdFromCurrentStaff() != 1) {
            booleanExpression = booleanExpression.and((ticketTatMatrix.mvnoId.in(getMvnoIdFromCurrentStaff(), 1)));
        }
        log.debug("Found TicketTatMatrix entities and exiting findAllByStatus; module: {}", SUBMODULE);
        return IterableUtils.toList(repository.findAll(booleanExpression));
    }

    @Override
    public TicketTatMatrixDTO getEntityById(Long id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getEntityById()] ";
        if(id != null) {
            log.debug("getting TAT matrix by id : {}; Module : {}",id,SUBMODULE);
            TicketTatMatrixDTO ticketTatMatrixDTO = mapper.domainToDTO(repository.findByIdAndIsDeletedFalse(id),new CycleAvoidingMappingContext());
            if(Objects.nonNull(ticketTatMatrixDTO)){
                log.debug("found TAT matrix by id : {}; Module : {}",ticketTatMatrixDTO.getId(),SUBMODULE);
                return ticketTatMatrixDTO;
            }else {
                log.warn("TAT matrix not found with id : {}",id,SUBMODULE);
            }
        }
        return  null;
    }
}
