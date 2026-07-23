package com.savbill.commonGateway.moules.userUiPreferences.service;


import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.moules.userUiPreferences.model.UserUiPreferencesDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserUiPreferencesService {

    /**
     * Save User UI Preferences
     *  MvnoId must be unique
     *
     * @param uiPreferencesDTO
     * @return UserUiPreferencesDTO
     */
    UserUiPreferencesDTO saveUserUi(UserUiPreferencesDTO uiPreferencesDTO) throws JsonProcessingException;

    /**
     * Update User UI Preferences
     * MvnoId must not be change
     *
     * @param uiPreferencesDTO
     * @return UserUiPreferencesDTO
     */
    UserUiPreferencesDTO updateUserUi(UserUiPreferencesDTO uiPreferencesDTO);

    /**
     * Copy all User Preferences from any other mvno
     *
     * @param pojo
     * @param newMvnoId
     * @return UserUiPreferencesDTO
     */
    UserUiPreferencesDTO copyUserUi(UserUiPreferencesDTO pojo, Integer newMvnoId);

    /**
     * Soft Delete user-preferences using mvnoId
     *
     * @param MvnoId
     * @return success or fail message
     */
    String deleteUseUi(Long MvnoId);

    /**
     * Update User Preference Status by id
     *
     * @param id
     * @param status
     * @return UserUiPreferencesDTO
     */
    UserUiPreferencesDTO updateStatus(Long id, String status);

    List<UserUiPreferencesDTO> fetchAllUserPrefrences();

    Page<UserUiPreferencesDTO> fetchAllUserPrefrencesByPagination(PaginationRequestDTO requestDTO);

    List<UserUiPreferencesDTO> getByMvnoId(Integer mvnoId);

    UserUiPreferencesDTO getByMvnoIdAndPageName(Integer mvnoId, String pageName);
}
