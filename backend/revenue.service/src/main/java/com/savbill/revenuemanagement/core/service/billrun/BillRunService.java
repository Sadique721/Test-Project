package com.savbill.revenuemanagement.core.service.billrun;


import com.savbill.revenuemanagement.core.auditLog.model.AuditForResponseModel;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.dto.billrun.BillRunPojo;
import com.savbill.revenuemanagement.core.dto.common.GenericSearchModel;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.mapper.billrun.BillRunMapper;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.repository.BillRun.BillRunRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.security.constants.Constants;
import com.savbill.revenuemanagement.core.security.service.MessagesPropertyConfig;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillRunService extends AbstractService<BillRun, BillRunPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(BillRunService.class);

    @Autowired
    private BillRunRepository entityRepository;
    @Autowired
    private BillRunMapper billRunMapper;
//    @Autowired
//    MessageSender messageSender;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CustomersRepository customersRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected JpaRepository<BillRun, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<BillRun> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}
    public static final String MODULE = "[BillRunService]";

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> getAllActiveEntities() {
        if (getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findByStatus("Y");
        return entityRepository.findByStatusAndMvnoIdIn("Y", Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> getAllEntities() {
        if (getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAll();
        return entityRepository.findAll(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

//    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '1')")
//    public List<BillRun> findBillRunDetails(SearchBillRun search) {
//
//        /*
//         * QBillRun billRun = QBillRun.billRun;
//         *
//         * Predicate builder = new OptionalBooleanBuilder(billRun.isNotNull())
//         * .notEmptyAnd(billRun.status::equalsIgnoreCase, sBillRun.getBillrunstatus())
//         * .notEmptyAnd(billRun.id::eq, sBillRun.getBillrunid()) //
//         * .notEmptyAnd(DateFormatUtils. billRun.createdate::after,
//         * sBillRun.getBillfromdate()) // .notEmptyAnd(billRun.createdate::before,
//         * sBillRun.getBilltodate()) // . .build();
//         *
//         * return (List<BillRun>) entityRepository.findAll(builder); // return
//         * (List<BillRun>) entityRepository.findAll(billRunStatusMatches);
//         *
//         */
//
//        QBillRun entity = QBillRun.billRun;
//        BooleanExpression exp = entity.isNotNull();
//        if (search.getBillrunid() != null) {
//            exp = exp.and(entity.id.eq(search.getBillrunid()));
//        }
//        if (!StringUtils.isEmpty(search.getBillrunstatus()) && !"-1".equalsIgnoreCase(search.getBillrunstatus())) {
//            exp = exp.and(entity.status.eq(search.getBillrunstatus()));
//        }
//        if (search.getBillfromdate() != null && search.getBilltodate() != null) {
//            exp = exp.and(entity.createdate.between(search.getBillfromdate().atStartOfDay(), search.getBilltodate().plusDays(1).atStartOfDay().minusSeconds(1)));
//        } else if (search.getBilltodate() != null) {
//            exp = exp.and(entity.createdate.before(search.getBilltodate().plusDays(1).atStartOfDay().minusSeconds(1)));
//        } else if (search.getBillfromdate() != null) {
//            exp = exp.and(entity.createdate.after(search.getBillfromdate().atStartOfDay()));
//        }
//        Predicate builder1 = exp;
//        return (List<BillRun>) entityRepository.findAll(builder1);
//
//    }


    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '4')")
    public void deleteBillRun(Integer id) throws Exception {
        entityRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> findById(Integer billRunId) {
        List<Integer> myList = new ArrayList<Integer>();
        myList.add(billRunId);

        return entityRepository.findAllById(myList);
    }

    public BillRunPojo convertBillRunModelToBillRunPojo(BillRun billRun) {
        BillRunPojo pojo = null;
        if (billRun != null) {
            pojo = new BillRunPojo();
            pojo.setId(billRun.getId());
            pojo.setAmount(billRun.getAmount());
            pojo.setBillruncount(billRun.getBillruncount());
            pojo.setBillrunfinishdate(billRun.getBillrunfinishdate());
            pojo.setRundate(billRun.getRundate());
            pojo.setStatus(billRun.getStatus());
            pojo.setCreatedate(billRun.getCreatedate());
            pojo.setDelete(billRun.getIsDelete());
            pojo.setType(billRun.getType());
            pojo.setMvnoId(billRun.getMvnoId());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '1')")
    public List<BillRunPojo> convertResponseModelIntoPojo(List<BillRun> billRunServerList) {
        List<BillRunPojo> pojoListRes = new ArrayList<BillRunPojo>();
        if (billRunServerList != null && billRunServerList.size() > 0) {
            for (BillRun billRun : billRunServerList) {
                pojoListRes.add(convertBillRunModelToBillRunPojo(billRun));
            }
        }
        return pojoListRes;
    }

    public Page<BillRun> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String type) {
        //query to fetch custId as per next bill date
        pageRequest = generatePageRequest(pageNumber, customPageSize, "billrunid", sortOrder);
        if(getLoggedInUser().getLco())
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                return entityRepository.findAll(pageRequest,getLoggedInUser().getPartnerId());
            if (null == filterList || 0 == filterList.size()) {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()),getLoggedInUser().getPartnerId());
                else
                    return entityRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
            else
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
        }
        else
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                return entityRepository.findAll(pageRequest);
            if (null == filterList || 0 == filterList.size()) {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()),getLoggedInUser().getPartnerId());
                else
                    return entityRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff(),getLoggedInUser().getPartnerId());
            }
            else
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
        }
    }


    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '2')")
    public BillRunPojo save(BillRunPojo pojo) throws Exception {
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        BillRun obj = convertBillRunPojoToBillRunModel(pojo);
        obj = saveBillRun(obj);
        pojo = convertBillRunModelToBillRunPojo(obj);
        return pojo;
    }

    public BillRun convertBillRunPojoToBillRunModel(BillRunPojo billRunPojo) throws Exception {
        BillRun billRun = null;
        if (billRunPojo != null) {
            billRun = new BillRun();
            if (billRunPojo.getId() != null) {
                billRun.setId(billRunPojo.getId());
            }
            billRun.setAmount(billRunPojo.getAmount());
            billRun.setBillruncount(billRunPojo.getBillruncount());
            billRun.setRundate(billRunPojo.getRundate());
            billRun.setStatus(billRunPojo.getStatus());
            billRun.setBillrunfinishdate(billRunPojo.getBillrunfinishdate());
            billRun.setCreatedate(billRunPojo.getCreatedate());
            billRun.setIsDelete(billRunPojo.getDelete());
            billRun.setType(billRunPojo.getType());
            billRun.setMvnoId(billRunPojo.getMvnoId());
        }
        return billRun;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.BillRun', '2')")
    public BillRun saveBillRun(BillRun billRun) throws Exception {
        String operation = "edit";
        if (billRun != null && billRun.getId() == null) {
            operation = "add";
        }
        BillRun save = entityRepository.save(billRun);
        return save;
    }


//    public void validateRequest(BillRunPojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
//            if (pojo.getId() != null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//            }
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N"))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("BillRun");
        List<BillRunPojo> billRunPojoList = entityRepository.findAll().stream()
                .map(data -> billRunMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, BillRunPojo.class, billRunPojoList, null);
    }


    public void pdfGenerate(Document doc) throws Exception {
        List<BillRunPojo> billRunPojoList = entityRepository.findAll().stream()
                .map(data -> billRunMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, BillRunPojo.class, billRunPojoList, null);
    }

    @Override
    public BillRun get(Integer id) {
        BillRun billRun = super.get(id);
        if (billRun != null && (getMvnoIdFromCurrentStaff() == 1 || (billRun.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || billRun.getMvnoId() == 1)))
            return billRun;
        return null;
    }

    public BillRun getEntityForUpdateAndDelete(Integer id) {
        BillRun billRun = get(id);
        if (billRun == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == billRun.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return billRun;
    }

    public List<AuditForResponseModel> getBillListForAuditFor() {
        String SUBMODULE = MODULE + " [getCaseListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            List<BillRun> billRunList = getAllActiveEntities();
            if (null != billRunList && 0 < billRunList.size()) {
                for (BillRun billRun : billRunList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(billRun.getId());
                    responseModel.setName(billRun.getType());
                    responseList.add(responseModel);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Unable to get customer list for Audit response{};exception{}", APIConstants.FAIL, ex.getStackTrace());
            throw ex;
        }
        return responseList;
    }

}
