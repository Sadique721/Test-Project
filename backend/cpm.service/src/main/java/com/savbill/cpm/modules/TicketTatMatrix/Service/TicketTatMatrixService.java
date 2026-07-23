package com.savbill.cpm.modules.TicketTatMatrix.Service;

import com.savbill.cpm.constants.DeleteContant;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.TicketTatMatrix.Domain.QTicketTatMatrix;
import com.savbill.cpm.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.cpm.modules.TicketTatMatrix.Mapper.TicketTatMatrixMapper;
import com.savbill.cpm.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import com.savbill.cpm.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketTatMatrixService extends ExBaseAbstractService2<TicketTatMatrixDTO, TicketTatMatrix, Long> {
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
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (matrixname != null) {
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
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String tatmatrixname, Integer tatmatrixid) throws Exception {
        boolean flag = false;
        if (tatmatrixname != null) {
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
        }
        return flag;
    }


    public boolean deleteVerification(Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

        boolean flag = false;
        try {

            Integer count = repository.deleteVerify(id);
            if (count == 0) { // Count == 1 due to tatmatrix is not bind with any services
                flag = true;
            } else {
                throw new RuntimeException(DeleteContant.MATRIX_EXIST);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
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
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    public List<TicketTatMatrixDTO> getAllTicketTatMatrix() {
        List<TicketTatMatrix> ticketTatMatrixList = repository.getAllByStatus().stream().filter(x -> (x.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (x.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(x.getBuId()))).collect(Collectors.toList());
        List<TicketTatMatrixDTO> ticketTatMatrixDTOList = ticketTatMatrixList.stream().map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return ticketTatMatrixDTOList;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<TicketTatMatrix> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1) paginationList = repository.findAll(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                paginationList = repository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
            else
                paginationList = repository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public List<TicketTatMatrix> findAllByStatus() {
        QTicketTatMatrix ticketTatMatrix = QTicketTatMatrix.ticketTatMatrix;
        BooleanExpression booleanExpression = ticketTatMatrix.status.equalsIgnoreCase("Active").and(ticketTatMatrix.isDeleted.eq(false));
        if (getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(ticketTatMatrix.buId.in(getBUIdsFromCurrentStaff()));
        }
        if (getMvnoIdFromCurrentStaff() != 1) {
            booleanExpression = booleanExpression.and((ticketTatMatrix.mvnoId.in(getMvnoIdFromCurrentStaff(), 1)));
        }
        return IterableUtils.toList(repository.findAll(booleanExpression));
    }
}
