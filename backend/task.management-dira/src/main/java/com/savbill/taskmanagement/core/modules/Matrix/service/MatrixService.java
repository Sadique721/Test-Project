package com.savbill.taskmanagement.core.modules.Matrix.service;


import com.savbill.taskmanagement.core.constants.DeleteContant;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.modules.Matrix.domain.Matrix;
import com.savbill.taskmanagement.core.modules.Matrix.domain.QMatrix;
import com.savbill.taskmanagement.core.modules.Matrix.mapper.MatrixMapper;
import com.savbill.taskmanagement.core.modules.Matrix.model.MatrixDTO;
import com.savbill.taskmanagement.core.modules.Matrix.repository.MatrixRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MatrixService  extends ExBaseAbstractService<MatrixDTO, Matrix,Long> {


    public MatrixService(MatrixRepository repository, MatrixMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MatrixService]";
    }
    @Autowired
    MatrixRepository matrixRepository;

    @Override
    public boolean duplicateVerifyAtSave(String matrixname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (matrixname != null) {
            matrixname = matrixname.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = matrixRepository.duplicateVerifyAtSave(matrixname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String matrixname, Integer matrixid) throws Exception {
        boolean flag = false;
        if (matrixname != null) {
            matrixname = matrixname.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = matrixRepository.duplicateVerifyAtSave(matrixname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = matrixRepository.duplicateVerifyAtSave(matrixname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = matrixRepository.duplicateVerifyAtEdit(matrixname, matrixid, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
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

//    public GenericDataDTO search1(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
//        try {
//
//        } catch (Exception ex) {
//
//        }
//        return null;
//    }
    //Delete Verification
    public boolean deleteVerification(Integer id)throws Exception
        {
            String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

            boolean flag=false;
            try{

                Integer count=matrixRepository.deleteVerify(id);
                if(count==0){ // Count == 1 due to tatmatrix is not bind with any services
                    flag=true;
                }else {
                    throw new RuntimeException(DeleteContant.MATRIX_EXIST);
                }

            }catch (Exception ex){
                ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
                throw ex;
            }
            return flag;
        }

//    public String getid(Long id){
//        Optional<Matrix> matrix = matrixRepository.findById(id);
//        String name = matrix.get().getName();
//        return name;
//    }

    //Get All with Pagination
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Matrix> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getLoggedInUser().getLco())
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                paginationList = matrixRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    paginationList = matrixRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
                else
                    paginationList = matrixRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
        }
        else
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                paginationList = matrixRepository.findAll(pageRequest);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    paginationList = matrixRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    paginationList = matrixRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
        }


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }




    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QMatrix qMatrix = QMatrix.matrix;
        BooleanExpression booleanExpression = qMatrix.isNotNull().and(qMatrix.isDeleted.eq(false));
        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qMatrix.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qMatrix.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                        booleanExpression = booleanExpression.and(qMatrix.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }

        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qMatrix.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qMatrix.mvnoId.eq(1).or(qMatrix.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qMatrix.buId.in(getBUIdsFromCurrentStaff()))));
        }
        return makeGenericResponse(genericDataDTO, matrixRepository.findAll(booleanExpression, pageRequest));
    }

    public GenericDataDTO getTatmatrixByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Matrix> tatmatrixList = null;
            if(getMvnoIdFromCurrentStaff() == 1)
                tatmatrixList = matrixRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                tatmatrixList = matrixRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != tatmatrixList && 0 < tatmatrixList.getSize()) {
                makeGenericResponse(genericDataDTO, tatmatrixList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Matrix> matrixList()
    {
        List<Matrix> list =  new ArrayList<>();
        if(getLoggedInUser().getLco())
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                list   = matrixRepository.findbystatus(getLoggedInUser().getPartnerId());
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    list   = matrixRepository.findAllBystatus(Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
                else
                    list   = matrixRepository.findAllBystatus(getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
        }
        else
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                list   = matrixRepository.findbystatus();
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    list   = matrixRepository.findAllBystatus(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    list   = matrixRepository.findAllBystatus(getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
        }

        return list;
    }
}
