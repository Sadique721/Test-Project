package com.savbill.commonGateway.moules.userUiPreferences.service.impl;

import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.userUiPreferences.domain.QUserUiPreferences;
import com.savbill.commonGateway.moules.userUiPreferences.domain.UserUiPreferences;
import com.savbill.commonGateway.moules.userUiPreferences.mapper.UserUiPreferencesMapper;
import com.savbill.commonGateway.moules.userUiPreferences.model.UserUiPreferencesDTO;
import com.savbill.commonGateway.moules.userUiPreferences.repository.UserUiPreferencesRepository;
import com.savbill.commonGateway.moules.userUiPreferences.service.UserUiPreferencesService;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserUiPreferencesServiceImpl implements UserUiPreferencesService {

    @Autowired
    private UserUiPreferencesRepository userUiPreferencesRepository;

    @Autowired
    private UserUiPreferencesMapper userUiPreferencesMapper;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    /**
     * Save User UI Preferences
     * MvnoId must be unique
     *
     * @param uiPreferencesDTO
     * @return UserUiPreferencesDTO
     */
    @Override
        public UserUiPreferencesDTO saveUserUi(UserUiPreferencesDTO uiPreferencesDTO) throws JsonProcessingException {
        try {
            Optional<UserUiPreferences> existsUserUiPreferences = userUiPreferencesRepository.findByMvnoIdAndPageNameAndIsDeleteFalse(uiPreferencesDTO.getMvnoId(), uiPreferencesDTO.getPageName());
            if (existsUserUiPreferences.isPresent()) {
                throw new CustomValidationException(APIConstants.NOT_FOUND, "User Preference already exists for " + existsUserUiPreferences.get().getMvnoName(), null);
            } else {
                String mvnoName = mvnoRepository.findMvnoNameById(Long.valueOf(uiPreferencesDTO.getMvnoId()));
                uiPreferencesDTO.setMvnoName(mvnoName);
                HashMap<String, Object> styleMap = uiPreferencesDTO.getStyleObj();
                ObjectMapper objectMapper = new ObjectMapper();
                String styleData = objectMapper.writeValueAsString(styleMap);
                uiPreferencesDTO.setStyle(styleData);
                UserUiPreferences userUiPreferences = userUiPreferencesMapper.dtoToDomain(uiPreferencesDTO, new CycleAvoidingMappingContext());
                userUiPreferences.setIsDelete(false);
                userUiPreferences = userUiPreferencesRepository.save(userUiPreferences);
                return userUiPreferencesMapper.domainToDTO(userUiPreferences, new CycleAvoidingMappingContext());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Update User UI Preferences
     * MvnoId must not be change
     *
     * @param uiPreferencesDTO
     * @return UserUiPreferencesDTO
     */
    @Override
    public UserUiPreferencesDTO updateUserUi(UserUiPreferencesDTO uiPreferencesDTO) {
        return null;
    }

    /**
     * Copy all User Preferences from any other mvno
     *
     * @param pojo
     * @param newMvnoId
     * @return UserUiPreferencesDTO
     */
    @Override
    public UserUiPreferencesDTO copyUserUi(UserUiPreferencesDTO pojo, Integer newMvnoId) {
        return null;
    }

    /**
     * Soft Delete user-preferences using mvnoId
     *
     * @param MvnoId
     * @return success or fail message
     */
    @Override
    public String deleteUseUi(Long MvnoId) {
        return null;
    }

    /**
     * Update User Preference Status by id
     *
     * @param id
     * @param status
     * @return UserUiPreferencesDTO
     */
    @Override
    public UserUiPreferencesDTO updateStatus(Long id, String status) {
        return null;
    }

    @Override
    public List<UserUiPreferencesDTO> fetchAllUserPrefrences() {
        try {
            int mvnoId = getLoggedInMvnoId();
            List<Long> mvnoIds = new ArrayList<>();
            if (mvnoId == 1) {
                List<UserUiPreferences> userUiPreferences = userUiPreferencesRepository.findAllByMvnoId(Long.valueOf(mvnoId));
                if (CollectionUtils.isEmpty(userUiPreferences)) {
                    throw new CustomValidationException(APIConstants.NOT_FOUND, "User Preference Not Available ", null);
                }
                List<UserUiPreferencesDTO> userUiPreferencesDTO = userUiPreferencesMapper.domainToDTO(userUiPreferences, new CycleAvoidingMappingContext());
                return userUiPreferencesDTO;
            }
            mvnoIds.add(Long.valueOf(mvnoId));
            mvnoIds.add(1L);
            List<UserUiPreferences> userUiPreferences = userUiPreferencesRepository.findAllByMvnoIdIn(mvnoIds);
            return userUiPreferencesMapper.domainToDTO(userUiPreferences, new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception to fetch User UI Preferences: " + ex.getMessage());
        }
    }

    @Override
    public Page<UserUiPreferencesDTO> fetchAllUserPrefrencesByPagination(PaginationRequestDTO requestDTO) {
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
        Page<UserUiPreferencesDTO> page = null;
        Pageable paging = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize());
        try {
            Page<UserUiPreferences> userUiPreferences = null;
            if (getLoggedInMvnoId() == 1) {
                userUiPreferences = userUiPreferencesRepository.findAll(pageRequest);
            } else {
                QUserUiPreferences qUserUiPreferences = QUserUiPreferences.userUiPreferences;
                BooleanExpression booleanExpression = qUserUiPreferences.isNotNull().and(qUserUiPreferences.isDelete.eq(false));

                if (getLoggedInMvnoId() != 1) {
                    booleanExpression = booleanExpression.and(qUserUiPreferences.mvnoId.in(getLoggedInMvnoId(), 1));
                }
                userUiPreferences = userUiPreferencesRepository.findAll(booleanExpression, pageRequest);
            }
            if (!userUiPreferences.isEmpty() && !userUiPreferences.getContent().isEmpty()) {
                List<UserUiPreferencesDTO> list = userUiPreferencesMapper.domainToDTO(userUiPreferences.getContent(), new CycleAvoidingMappingContext());
//                list = list.stream().filter(uiPreferences ->uiPreferences.getStyle() != null).peek(preferences -> preferences.setStyleObj(preferences.getStyle())).collect(Collectors.toList());
                page = new PageImpl<>(list, paging, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    @Override
    public List<UserUiPreferencesDTO> getByMvnoId(Integer mvnoId) {
        try {
            QUserUiPreferences qUserUiPreferences = QUserUiPreferences.userUiPreferences;
            BooleanExpression booleanExpression = qUserUiPreferences.isNotNull().and(qUserUiPreferences.isDelete.eq(false));
            booleanExpression = booleanExpression.and(qUserUiPreferences.mvnoId.eq(mvnoId));
            List<UserUiPreferences> userUiPreferences = (List<UserUiPreferences>) userUiPreferencesRepository.findAll(booleanExpression);
            if(!CollectionUtils.isEmpty(userUiPreferences)) {
                return userUiPreferencesMapper.domainToDTO(userUiPreferences, new CycleAvoidingMappingContext());
            } else {
                throw new CustomValidationException(APIConstants.NOT_FOUND, "User-Ui not found for Mvno: "+mvnoId, null);
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new CustomValidationException(ex.getErrCode(),ex.getMessage(),null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public UserUiPreferencesDTO getByMvnoIdAndPageName(Integer mvnoId, String pageName) {
        try {
            QUserUiPreferences qUserUiPreferences = QUserUiPreferences.userUiPreferences;
            BooleanExpression booleanExpression = qUserUiPreferences.isNotNull().and(qUserUiPreferences.isDelete.eq(false));
            booleanExpression = booleanExpression.and(qUserUiPreferences.mvnoId.eq(mvnoId)).and(qUserUiPreferences.pageName.eq(pageName));
            Optional<UserUiPreferences> userUiPreferences = userUiPreferencesRepository.findOne(booleanExpression);
            if(userUiPreferences.isPresent()) {
                return userUiPreferencesMapper.domainToDTO(userUiPreferences.get(), new CycleAvoidingMappingContext());
            } else {
                throw new CustomValidationException(APIConstants.NOT_FOUND, "User-Ui not found for Mvno: "+mvnoId+" and Page: "+pageName, null);
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new CustomValidationException(ex.getErrCode(),ex.getMessage(),null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public int getLoggedInMvnoId() {
        int loggedInMvnoId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            loggedInMvnoId = -1;
        }
        return loggedInMvnoId;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        Map<String, String> sortColMap = new HashMap<>();
        PageRequest pageRequest = null;
        Integer MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE)
            pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }
}
