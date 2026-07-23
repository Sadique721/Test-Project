package com.savbill.cpm.modules.TimeBasePolicy.service;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.TimeBasePolicy.domain.QTimeBasePolicy;
import com.savbill.cpm.modules.TimeBasePolicy.domain.TimeBasePolicy;
import com.savbill.cpm.modules.TimeBasePolicy.mapper.TimeBasePolicyMapper;
import com.savbill.cpm.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import com.savbill.cpm.modules.TimeBasePolicy.repository.TimeBasePolicyRepository;
import com.savbill.cpm.modules.qosPolicy.domain.QOSPolicy;
import com.savbill.cpm.modules.qosPolicy.repository.QOSPolicyRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TimeBasePolicyService extends ExBaseAbstractService2<TimeBasePolicyDTO, TimeBasePolicy, Long> {

    public TimeBasePolicyService(TimeBasePolicyRepository repository, TimeBasePolicyMapper mapper)
    {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TimeBasePolicyService]";
    }

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;
    @Autowired
    private TimeBasePolicyRepository timeBasePolicyRepository;

    //Get All Time Base Policy with Pagination
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<TimeBasePolicy> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = timeBasePolicyRepository.findAll(pageRequest);
        else {
            if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                paginationList = timeBasePolicyRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else
                paginationList = timeBasePolicyRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    // Duplicate Time Base Policy
    @Override
    public boolean duplicateVerifyAtSave(String policyname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (policyname != null) {
            policyname = policyname.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

//    @Override
//    public TimeBasePolicyDTO saveEntity(TimeBasePolicyDTO timeBasePolicyDTO) throws Exception {
//        //timeBasePolicyDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        timeBasePolicyDTO.setTimeBasePolicyDetailsList(timeBasePolicyDTO.getTimeBasePolicyDetailsList());
//        TimeBasePolicyDTO save = super.saveEntity(timeBasePolicyDTO);
//        QosPolicyMessage message = new QosPolicyMessage(save);
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_QOS_POLICY);
//        return save;
//    }

    //Search Time Base Policy
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            QTimeBasePolicy qTimeBasePolicy = QTimeBasePolicy.timeBasePolicy;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            BooleanExpression booleanExpression = qTimeBasePolicy.isNotNull().and(qTimeBasePolicy.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();

            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qTimeBasePolicy.name.containsIgnoreCase(genericSearchModel.getFilterValue()));

                }
            }
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                booleanExpression = booleanExpression.and(qTimeBasePolicy.mvnoId.eq(1).or(qTimeBasePolicy.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTimeBasePolicy.buId.in(getBUIdsFromCurrentStaff()))));
            }
            return makeGenericResponse(genericDataDTO, timeBasePolicyRepository.findAll(booleanExpression,pageRequest));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getTimeBasePolicyByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<TimeBasePolicy> timeBasePolicyList = null;
            if(getMvnoIdFromCurrentStaff() == 1)
                timeBasePolicyList = timeBasePolicyRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                timeBasePolicyList = timeBasePolicyRepository.findAllBynameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != timeBasePolicyList && 0 < timeBasePolicyList.getSize()) {
                makeGenericResponse(genericDataDTO, timeBasePolicyList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    // Duplicate Verify At Time Base Policy
    @Override
    public boolean duplicateVerifyAtEdit(String policyname, Integer policyid) throws Exception {
        boolean flag = false;
        if (policyname != null) {
            policyname = policyname.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = timeBasePolicyRepository.duplicateVerifyAtSave(policyname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff() == null)
                        countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = timeBasePolicyRepository.duplicateVerifyAtEdit(policyname, policyid, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
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

    // Get Time Base Policy By Id
    public TimeBasePolicy getById(Long policyid) {
        return timeBasePolicyRepository.findById(policyid).get();
    }

    //Delete Verification
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=timeBasePolicyRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public String getid(Long id){
        Optional<QOSPolicy> qosPolicy = qosPolicyRepository.findById(id);
        String name = qosPolicy.get().getName();
        return name;
    }


    public String getPolicyName(Integer id){
        return timeBasePolicyRepository.findPolicyName(id);
    }

}
