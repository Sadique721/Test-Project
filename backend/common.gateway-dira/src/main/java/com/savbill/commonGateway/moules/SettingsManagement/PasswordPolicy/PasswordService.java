package com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy;

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
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PasswordService extends AbstractService<PasswordPolicy, PasswordDTO, Long> {

    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    public Integer MAX_PAGE_SIZE;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;

    public static final String MODULE = "[PasswordService]";

    private static final Logger LOGGER = LoggerFactory.getLogger(passwordController.class);

    @Override
    protected JpaRepository<PasswordPolicy, Long> getRepository() {
        return passwordRepository;
    }

    public Page<PasswordPolicy> getAllPasswordsList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Long mvnoId = Long.valueOf(getMvnoIdFromCurrentStaff());
        if (mvnoId == 1) {
            return passwordRepository.findAllByIsDeleteFalse(pageRequest);
        }
        if (filterList == null || filterList.isEmpty()) {
            return passwordRepository.findAllByMvnoIdAndIsDeleteFalse(pageRequest, Math.toIntExact(mvnoId));
        } else {
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
        }
    }

    public List<PasswordPolicy> getAllActiveEntities() {
        return passwordRepository.findByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS)
                .stream().filter(password -> password.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || password.getMvnoId() == null || password.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }

    public List<PasswordDTO> convertResponseModelIntoPojo(List<PasswordPolicy> passwordList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<PasswordDTO> pojoListRes = new ArrayList<>();
        try {
            if (passwordList != null && !passwordList.isEmpty()) {
                for (PasswordPolicy password : passwordList) {
                    pojoListRes.add(convertPasswordModelToPasswordPojo(password));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;

    }

    public PasswordDTO convertPasswordModelToPasswordPojo(PasswordPolicy password) throws Exception {
        String SUBMODULE = MODULE + " [convertCityModelToCityPojo()] ";
        PasswordDTO passwordDTO = null;
        try {
            if (password != null) {
                passwordDTO = new PasswordDTO();
                passwordDTO.setId(password.getId());
                passwordDTO.setName(password.getName());
                passwordDTO.setStatus(password.getStatus());
                passwordDTO.setMin_length(password.getMin_length());
                passwordDTO.setIsNotificationRequired(password.getIsNotificationRequired());
                passwordDTO.setMax_length(password.getMax_length());
                passwordDTO.setExpiration_days(password.getExpiration_days());
                passwordDTO.setDisable_recycling_prevention(password.getDisable_recycling_prevention());
                passwordDTO.setDisable_account_lockout(password.getDisable_account_lockout());
                passwordDTO.setPattern(password.getPattern());
                passwordDTO.setPattern_description(password.getPattern_description());
                passwordDTO.setCreatedate(password.getCreatedate());
                passwordDTO.setUpdatedate(password.getUpdatedate());
                passwordDTO.setCreatedById(password.getCreatedById());
                passwordDTO.setCreatedByName(password.getCreatedByName());
                passwordDTO.setLastModifiedById(password.getLastModifiedById());
                passwordDTO.setLastModifiedByName(password.getLastModifiedByName());
                passwordDTO.setMvnoId(password.getMvnoId());
                if (password.getMvnoId() != null) {
                    passwordDTO.setMvnoId(password.getMvnoId());
                }
                passwordDTO.setIsDelete(password.getIsDelete());

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return passwordDTO;
    }

    public PasswordPolicy convertPasswordPojoToPasswordModel(PasswordDTO passwordDTO) throws Exception {
        String SUBMODULE = MODULE + " [convertCityPojoToCityModel()] ";
        PasswordPolicy password = null;
        try {
            if (passwordDTO != null) {
                password = new PasswordPolicy();
                if (passwordDTO.getId() != null) {
                    password.setId(passwordDTO.getId());
                }
                password.setName(passwordDTO.getName());
                password.setStatus(passwordDTO.getStatus());
                if (passwordDTO.getMvnoId() != null) {
                    password.setMvnoId(passwordDTO.getMvnoId());
                }
                password.setMin_length(passwordDTO.getMin_length());
                password.setIsNotificationRequired(passwordDTO.getIsNotificationRequired());
                password.setMax_length(passwordDTO.getMax_length());
                password.setExpiration_days(passwordDTO.getExpiration_days());
                password.setDisable_recycling_prevention(passwordDTO.getDisable_recycling_prevention());
                password.setDisable_account_lockout(passwordDTO.getDisable_account_lockout());
                password.setPattern(passwordDTO.getPattern());
                password.setPattern_description(passwordDTO.getPattern_description());
                password.setCreatedate(passwordDTO.getCreatedate());
                password.setUpdatedate(passwordDTO.getUpdatedate());
                password.setCreatedById(passwordDTO.getCreatedById());
                password.setCreatedByName(passwordDTO.getCreatedByName());
                password.setLastModifiedById(passwordDTO.getLastModifiedById());
                password.setLastModifiedByName(passwordDTO.getLastModifiedByName());
                password.setMvnoId(passwordDTO.getMvnoId());
                password.setIsDelete(passwordDTO.getIsDelete());
                return password;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return password;
    }

    public PasswordPolicy savePassword(PasswordPolicy password) throws Exception, AlreadyExistException {
        if (passwordRepository.existsByNameAndIsDeleteFalse(password.getName().trim())) {
            throw new AlreadyExistException("A Password with the name " + password.getName() + " already exists.");
        }
        if (getMvnoIdFromCurrentStaff() != null) {
            password.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        return passwordRepository.save(password);
    }

    public PasswordDTO save(PasswordDTO passwordDTO) throws Exception, AlreadyExistException {
        String SUBMODULE = MODULE + "save()";
        try {
            passwordDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            PasswordPolicy obj = convertPasswordPojoToPasswordModel(passwordDTO);
            obj = savePassword(obj);
            passwordDTO = convertPasswordModelToPasswordPojo(obj);

        } catch (Exception | AlreadyExistException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage());
            throw e;
        }
        return passwordDTO;
    }

    public PasswordPolicy getPasswordById(Long id) {
        PasswordPolicy password = super.get(id);
        if (getMvnoIdFromCurrentStaff() == 1 || (password.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || password.getMvnoId() == 1))
            return password;
        return null;
    }

    public PasswordDTO updatePassword(PasswordDTO passwordDTO, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + "update()";
        Integer respCode = APIConstants.FAIL;
        PasswordPolicy old1 = get(passwordDTO.getId());
        try {
            passwordDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            PasswordPolicy obj = convertPasswordPojoToPasswordModel(passwordDTO);
            getPasswordForUpdateAndDelete(obj.getId());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update City " + LogConstants.LOG_BY_NAME + passwordDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated City Details " + UpdateDiffFinder.getUpdatedDiff(old1, obj) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            //String updatedValues = CommonUtils.getUpdatedDiff(convertCityModelToCityPojo(obj),convertCityModelToCityPojo(old1));
            obj = savePasswordwhileUpdate(obj);
            passwordDTO = convertPasswordModelToPasswordPojo(obj);


        } catch (Exception ex) {
            LOGGER.error("Request From : " + req.getHeader("requestFrom") + ", Request for : " + ", Request to update city :  " + passwordDTO.getName() + ", Requested by : " + getLoggedInUser().getFirstName() + ", Status : FAILED " + ", ERROR : " + ex.getMessage());
            throw ex;
        }
        return passwordDTO;
    }

    public PasswordPolicy getPasswordForUpdateAndDelete(Long id) {
        PasswordPolicy password = get(id);
        if (password == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == password.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return password;
    }

    public PasswordPolicy savePasswordwhileUpdate(PasswordPolicy password) throws Exception {
        if (getMvnoIdFromCurrentStaff() != null) {
            password.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        return passwordRepository.save(password);
    }

    public void deletePassword(Long id) throws Exception {
        String SUBMODULE = MODULE + "delete()";
        try {
            PasswordPolicy passwordPolicy = passwordRepository.findById(id)
                    .orElseThrow(() -> new Exception("Password policy not found"));

            // Check if the password policy is assigned to any MVNO
            boolean isPolicyAssignedToMvno = mvnoRepository.existsByPasswordPolicyId(id);

            if (isPolicyAssignedToMvno) {
                throw new CustomValidationException(HttpStatus.IM_USED.value(), "Cannot delete password policy as it is assigned to an MVNO.", null);
            }

            passwordPolicy.setIsDelete(true);
            passwordRepository.save(passwordPolicy);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<PasswordPolicy> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        Integer mvnoId = getMvnoIdFromCurrentStaff();

        try {
            Specification<PasswordPolicy> spec = Specification.where(null);

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

            return passwordRepository.findAll(spec, pageRequest);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Boolean checkNotificationRequired(Long mvnoId) {
        // Step 1: Fetch MVNO details by mvnoId
        Mvno mvno = mvnoRepository.findById(mvnoId).get();

        // Step 2: Get passwordPolicyId from MVNO
        Long passwordPolicyId = mvno.getPasswordPolicyId();

        // Step 3: Fetch Password Policy by passwordPolicyId
        PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId).get();

        return passwordPolicy.getIsNotificationRequired();
    }

}
