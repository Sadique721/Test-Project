package com.savbill.cpm.modules.Cas.Service;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.constants.DeleteContant;
import com.savbill.cpm.constants.LogConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.modules.Cas.Domain.*;
import com.savbill.cpm.modules.Cas.Mapper.CasMapper;
import com.savbill.cpm.modules.Cas.Model.CasMasterDTO;
import com.savbill.cpm.modules.Cas.Repository.CasParameterMappingRepocitory;
import com.savbill.cpm.modules.Cas.Repository.CasePackageRepository;
import com.savbill.cpm.modules.InventoryManagement.product.ProductRepository;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CasMasterService extends ExBaseAbstractService2<CasMasterDTO, CasMaster, Long> {

    @Autowired
    CasePackageRepository repository;
    private final Logger logger= LoggerFactory.getLogger(CasMasterService.class);
    @Autowired
    private Tracer tracer;

    @Autowired
    private CasMasterRepository casMasterRepository;
    @Autowired
    private CasPackageMappingRepository casPackageMappingRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    CasParameterMappingRepocitory casParameterMappingRepocitory;

    @Autowired
    CreateDataSharedService createDataSharedService;

    public CasMasterService(CasePackageRepository repository, CasMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CasePackageService]";
    }

    @Override
    public boolean duplicateVerifyAtSave(String casname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (casname != null) {
            casname = casname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(casname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = repository.duplicateVerifyAtSave(casname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(casname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;

    }

    @Override
    public boolean duplicateVerifyAtEdit(String casname, Integer casid) throws Exception {
        boolean flag = false;
        if (casname != null) {
            casname = casname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(casname);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = repository.duplicateVerifyAtSave(casname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(casname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = repository.duplicateVerifyAtEdit(casname, casid);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        countEdit = repository.duplicateVerifyAtEdit(casname, casid, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = repository.duplicateVerifyAtEdit(casname, casid, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
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

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QCasMaster qCasMaster = QCasMaster.casMaster;
        BooleanExpression booleanExpression = qCasMaster.isNotNull().and(qCasMaster.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());

        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                booleanExpression = booleanExpression.and(qCasMaster.casname.containsIgnoreCase(genericSearchModel.getFilterValue()));

            }
        }

        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.eq(1).or(qCasMaster.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCasMaster.buId.in(getBUIdsFromCurrentStaff()))));
        }
        logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Search CasMaster using keyword : "+filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }


    //Delete Verification
    public boolean deleteVerification(Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteVerification()] ";

        boolean flag = false;
        try {

            Integer count = repository.deleteVerify(Long.valueOf(id));
            if (count == 1) { // Count == 1 due to cas is not bind with any services
                flag = true;
            } else {
                throw new RuntimeException(DeleteContant.CAS_EXIST);
            }

            if(productRepository.countAllByByCasId(id) > 0){
                throw new RuntimeException(DeleteContant.CAS_EXIST_IN_PRODUCT);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<CasMaster> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = repository.findAllByIsDeletedIsFalse(pageRequest);
        else if (null == filterList || filterList.isEmpty())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().isEmpty())
                paginationList = repository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
            else
                paginationList = repository.findAllByIsDeletedIsFalseAndMvnoIdInAndAndBuIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff(), pageRequest);
        if (null != paginationList && !paginationList.getContent().isEmpty()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    @Transactional
    public GenericDataDTO refreshCasPackage(Long casId) {

        return null;
    }


    public List<CasMaster> getAllActiveEntities() {
        QCasMaster qCasMaster=QCasMaster.casMaster;
        BooleanExpression booleanExpression=QCasMaster.casMaster.isNotNull().and(QCasMaster.casMaster.isDeleted.eq(false));
        booleanExpression=booleanExpression.and(qCasMaster.status.equalsIgnoreCase("Active"));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        if (getBUIdsFromCurrentStaff().size() != 0)
            booleanExpression = booleanExpression.and(qCasMaster.mvnoId.eq(1).or(qCasMaster.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCasMaster.buId.in(getBUIdsFromCurrentStaff()))));

      List<CasMaster> casMasters= IterableUtils.toList(casMasterRepository.findAll(booleanExpression));
       return casMasters;
    }


    @Override
    public CasMasterDTO updateEntity(CasMasterDTO entityDTO) throws Exception {
        List<CasParameterMapping> casParameterMappingList=new ArrayList<>();
                entityDTO.getCasParameterMappings().stream().forEach(r->{
                        if(r.getId()!=null){
                               r.setCasId(entityDTO.getId());
                                casParameterMappingList.add(r);
                          }

                });
         super.updateEntity(entityDTO);
        if(casParameterMappingList.size()>0) {
                         casParameterMappingRepocitory.saveAll(casParameterMappingList);
        }
        return entityDTO;
    }

    public void sendCreatedDataShared(CasMasterDTO casMasterDTO, Integer operation) throws Exception{
        try {
            CasMaster casMaster = casMasterRepository.findById(casMasterDTO.getId()).orElse(null);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(casMaster);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(casMaster);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(casMaster);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }
}
