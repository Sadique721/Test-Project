package com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.exceptions.AlreadyExistException;

import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;

import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustAccountProfileService extends AbstractService<CustAccountProfile, CustAccountProfileDTO, Long> {
    @Override
    protected JpaRepository<CustAccountProfile, Long> getRepository() {
        return null;
    }

    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;

    private static final String MODULE = "[CustAccountProfileService]";

    public CustAccountProfileDTO save(CustAccountProfileDTO custAccountProfileDTO) throws Exception, AlreadyExistException {
        String SUBMODULE = MODULE + "save()";
        try {
            custAccountProfileDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            CustAccountProfile obj = convertPojoToModel(custAccountProfileDTO);
            obj = saveCustAccountProfile(obj);
            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
            custAccountProfileDTO = convertModelToPojo(obj);
        } catch (Exception | AlreadyExistException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
            throw e;
        }
        return custAccountProfileDTO;
    }

    public CustAccountProfileDTO update(CustAccountProfileDTO custAccountProfileDTO, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + "update()";
        CustAccountProfile old1 = getById(custAccountProfileDTO.getId());
//        Optional<CustAccountProfile> old1 = custAccountProfileRepository.findById(custAccountProfileDTO.getId());
        try {
            custAccountProfileDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            CustAccountProfile obj = convertPojoToModel(custAccountProfileDTO);
            getCustAccountProfileForUpdateAndDelete(obj.getId());
            ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update CustAccountProfile " + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated CustAccountProfile Details " + UpdateDiffFinder.getUpdatedDiff(old1, obj) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            obj = updateAccountProfile(obj);
            createDataSharedService.updateEntityDataForAllMicroService(obj);
            custAccountProfileDTO = convertModelToPojo(obj);

        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
        }
        return custAccountProfileDTO;
    }

    public void deleteCustAccountProfile(Long id) throws Exception {
        String SUBMODULE = MODULE + "delete()";
        try {
            CustAccountProfile custAccountProfile = getCustAccountProfileForUpdateAndDelete(id);
            CustAccountProfileDTO custAccountProfileDTO = convertModelToPojo(custAccountProfile);
            if (custAccountProfileDTO != null) {
                custAccountProfile = custAccountProfileRepository.findById(id)
                        .orElseThrow(() -> new Exception("CustAccountProfile not found"));

                // Check if the CustAccountProfile is assigned to any MVNO
                boolean isCustAccProfileAssignedToMvno = mvnoRepository.existsByCustAccountProfileId(id);

                if (isCustAccProfileAssignedToMvno) {
                    throw new CustomValidationException(HttpStatus.IM_USED.value(), "Cannot delete CustAccountProfile as it is assigned to an MVNO.", null);
                }

                custAccountProfile.setIsDelete(true);
                custAccountProfileRepository.save(custAccountProfile);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<CustAccountProfile> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        try {
            Specification<CustAccountProfile> spec = Specification.where(null);

            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn() != null) {
                    String filterColumn = searchModel.getFilterColumn().trim();
                    String filterValue = searchModel.getFilterValue();

                    if (filterColumn.equalsIgnoreCase(SearchConstants.ANY)) {
                        spec = spec.and((root, query, criteriaBuilder) ->
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterValue.toLowerCase() + "%")
                        );
                    }
                    // Add more conditions based on other columns if needed
                } else {
                    throw new RuntimeException("Please Provide Search Column!");
                }
            }
            // Adding mvnoId and is_delete = false conditions to the specification
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("mvnoId"), mvnoId)
            ).and((root, query, criteriaBuilder) ->
                    criteriaBuilder.isFalse(root.get("isDelete"))
            );

            return custAccountProfileRepository.findAll(spec, pageRequest);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public CustAccountProfile convertPojoToModel(CustAccountProfileDTO custAccountProfileDTO) throws Exception {
        String SUBMODULE = MODULE + " [convertCustAccountProfilePojoToCustAccountProfileModel()] ";
        CustAccountProfile custAccountProfile = null;
        try {
            if (custAccountProfileDTO != null) {
                custAccountProfile = new CustAccountProfile();
                if (custAccountProfileDTO.getId() != null) {
                    custAccountProfile.setId(custAccountProfileDTO.getId());
                }
                custAccountProfile.setName(custAccountProfileDTO.getName());
                custAccountProfile.setPrefix(custAccountProfileDTO.getPrefix());
                custAccountProfile.setType(custAccountProfileDTO.getType());
                custAccountProfile.setStartFrom(custAccountProfileDTO.getStartFrom());
                custAccountProfile.setYear(custAccountProfileDTO.isYear());
                custAccountProfile.setMonth(custAccountProfileDTO.isMonth());
                custAccountProfile.setDay(custAccountProfileDTO.isDay());
                custAccountProfile.setMvnoId(custAccountProfileDTO.getMvnoId());
                custAccountProfile.setStatus(custAccountProfileDTO.getStatus());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return custAccountProfile;
    }

    public CustAccountProfileDTO convertModelToPojo(CustAccountProfile custAccountProfile)throws Exception{
        String SUBMODULE = MODULE + " [convertCustAccountProfileModelToCustAccountProfilePojo()] ";
        CustAccountProfileDTO custAccountProfileDTO = null;
        try {
            if(custAccountProfile!=null){
                custAccountProfileDTO = new CustAccountProfileDTO();
                if(custAccountProfile.getId()!=null){
                    custAccountProfileDTO.setId(custAccountProfile.getId());
                }
                custAccountProfileDTO.setName(custAccountProfile.getName());
                custAccountProfileDTO.setPrefix(custAccountProfile.getPrefix());
                custAccountProfileDTO.setType(custAccountProfile.getType());
                custAccountProfileDTO.setStartFrom(custAccountProfile.getStartFrom());
                custAccountProfileDTO.setYear(custAccountProfile.isYear());
                custAccountProfileDTO.setMonth(custAccountProfile.isMonth());
                custAccountProfileDTO.setDay(custAccountProfile.isDay());
                custAccountProfileDTO.setMvnoId(custAccountProfile.getMvnoId());
                custAccountProfileDTO.setStatus(custAccountProfile.getStatus());
            }
        }catch (Exception e){
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
        }
        return custAccountProfileDTO;
    }


    public CustAccountProfile saveCustAccountProfile(CustAccountProfile custAccountProfile) throws Exception, AlreadyExistException {
        if (custAccountProfileRepository.existsByNameAndIsDeleteFalse(custAccountProfile.getName().trim())) {
            throw new AlreadyExistException("A CustAccountProfile with the name " + custAccountProfile.getName() + " already exists.");
        }
        if (getMvnoIdFromCurrentStaff() != null) {
            custAccountProfile.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        return custAccountProfileRepository.save(custAccountProfile);
    }

    public CustAccountProfile getCustAccountProfileForUpdateAndDelete(Long id) {
        CustAccountProfile custAccountProfile = getById(id);
        if (custAccountProfile == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == custAccountProfile.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return custAccountProfile;
    }

    public CustAccountProfile updateAccountProfile(CustAccountProfile custAccountProfile) throws Exception {
        if (getMvnoIdFromCurrentStaff() != null) {
            custAccountProfile.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        return custAccountProfileRepository.save(custAccountProfile);
    }

    public Page<CustAccountProfile> getAllCustAccountProfileList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Long mvnoId = Long.valueOf(getMvnoIdFromCurrentStaff());
        if (mvnoId == 1) {
            return custAccountProfileRepository.findAllByIsDeleteFalse(pageRequest);
        }
        if (filterList == null || filterList.isEmpty()) {
            return custAccountProfileRepository.findAllByMvnoIdAndIsDeleteFalse(pageRequest, Math.toIntExact(mvnoId));
        } else {
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
        }
    }

    public List<CustAccountProfileDTO> convertResponseModelIntoPojo(List<CustAccountProfile> custAccountProfiles) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<CustAccountProfileDTO> pojoListRes = new ArrayList<>();
        try {
            if (custAccountProfiles != null && !custAccountProfiles.isEmpty()) {
                for (CustAccountProfile custAccountProfile : custAccountProfiles) {
                    pojoListRes.add(convertModelToPojo(custAccountProfile));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;

    }

    public CustAccountProfile getById(Long id) {
        return custAccountProfileRepository.findById(id).get();
    }



    public List<CustAccountProfile> getAllActiveEntities() {
        return custAccountProfileRepository.findByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
                .stream().filter(custAccountProfile -> custAccountProfile.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || custAccountProfile.getMvnoId() == null || custAccountProfile.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }


    public CustAccountProfile getCustAccountProfilesById(Long id) {
        CustAccountProfile custAccountProfile = getById(id);
        if (getMvnoIdFromCurrentStaff() == 1 || (custAccountProfile.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || custAccountProfile.getMvnoId() == 1))
            return custAccountProfile;
        return null;
    }
}