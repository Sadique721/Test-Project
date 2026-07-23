package com.savbill.revenuemanagement.core.schedulers;

import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SchedulerManagementService {
    void save (SchedulerManagementDTO schedulerManagementDTO);

    void update(SchedulerManagementDTO schedulerManagementDTO, Long id);

    void delete(Long id);

    SchedulerManagement getById(Long id);

    Page<SchedulerManagement> getAllScedulersWithPagination(PaginationRequestDTO paginationRequestDTO);

}
