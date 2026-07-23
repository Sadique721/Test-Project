package com.savbill.notification.savbilliwfnotification.service;

import com.savbill.notification.savbilliwfnotification.dto.EventTemplateBindingDTO;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTempBindSearchDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTemplateDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface IwfEventTempBindService {
    /**
     * Save Event Template Binding
     *
     * @param eventTemplateBindingDTO
     * @param request
     * @return
     */
//    EventTemplateBindingDTO saveEventTempBind(EventTemplateBindingDTO eventTemplateBindingDTO, MultipartFile file) throws IOException;
    EventTemplateBindingDTO saveEventTempBind(EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException;

    /**
     * Update Event Template Binding By Event Id
     *
     * @param eventId
     * @param eventTemplateBindingDTO
     * @param request
     * @return
     */
    EventTemplateBindingDTO updateEventTempBind(Long eventId, EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException;

    /**
     * Delete Event Template Binding by Event Id
     *
     * @param id
     * @param request
     */
    void removeEventTemplateBind(Long id, HttpServletRequest request) throws IOException;

    /**
     * Get Event Template Binding With Pagination
     *
     * @param page
     * @param size
     * @param request
     * @param onlyActive
     * @return
     */
    Page<EventTemplateBindingDTO> getEventTempBindPagination(Integer page, Integer size, HttpServletRequest request, boolean onlyActive) throws AuthException, CustomException, IOException;

    /**
     * Get Event Template Binding List
     *
     * @return
     */
    List<EventTemplateBindingDTO> getEventTempBindList(HttpServletRequest request, boolean onlyActive) throws AuthException, CustomException, IOException;

    /**
     * Get Event Template Binding by Event Id
     *
     * @param id
     * @return
     */
    EventTemplateBindingDTO getEventTempBindById(Long id);

    /**
     * Filter Event Template Binding By Event Name
     *
     * @param page
     * @param size
     * @param mvnoId
     * @param eventName
     * @return
     */
    Page<EventTempBindSearchDTO> filterEventTempBindByName(PaginationRequestDTO requestDTO, Long mvnoId);

    /**
     * Validate Event Template Binding
     *
     * @param eventTemplateBindingDTO
     */
    void validateEventTempBind(EventTemplateBindingDTO eventTemplateBindingDTO);

    Page<EventTemplateDTO> filterEventTempBind(PaginationRequestDTO dto, Long mvnoId, String serviceType) throws Exception;

    boolean validation(PaginationRequestDTO paginationDTO);
}
